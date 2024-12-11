class Search {
  constructor(map, customMap) {
    this.map = map;
    this.customMap = customMap;
    this.startDate = null;
    this.endDate = null;
    this.odorType = null;
    this.odorIntensity = null;
    this.complaintList = [];
  }

  //민원 객체 초기화
  async init() {
    // 검색창 오늘 날짜 세팅
    const today = this.setToday();
    document.getElementById("selectStartDate").value = today;
    document.getElementById("selectEndDate").value = today;


    //DB에서 악취 종류 이름 불러와서 검색 옵션 추가
    const odorNames = await this.getOdorNameALL();
    const odorTypeSelect = document.getElementById("selectOdorType");
    odorTypeSelect.innerHTML = '<option value="" disabled selected>악취 종류</option>'; //
    // 악취 종류 이름 검색 옵션 추가
    odorNames.forEach(odorName => {
      const option = document.createElement("option");
      option.textContent = odorName.odorName;
      option.value = odorName.odorId;

      odorTypeSelect.appendChild(option);
    });


    //악취 세기 검색을 위한 검색 옵션 추가
    const odorIntensitySelect = document.getElementById("selectOdorIntensity");
    odorIntensitySelect.innerHTML = '<option value="" disabled selected>세기</option>';
    for (let i = 1; i <= 5; i++) {
      const option = document.createElement("option");
      option.value = i;
      option.textContent = i;
      odorIntensitySelect.appendChild(option);
    }

    //민원 객체 생성
    this.complaintList = new ComplaintList(this.customMap.map, this.customMap),

      //모든 민원 정보 호출
      await this.fetchAllData();


    //검색 버튼 클릭 시 이벤트
    document.getElementById("searchComplaint").addEventListener("click", async () => {
      //생성된 모달 클로즈
      this.customMap.analysisModal.close_modal();
      this.customMap.complaintModal.close_modal();

      //선택된 민원 및 사업장 해제
      this.customMap.clickoffComplaint();
      this.customMap.clickoffPlace();

      //원 및 부채꼴 선택 해제
      this.customMap.deletecurrentCircularSector();

      //이전에 생성된 민원 객체 삭제
      await this.complaintList.clearAllComplaints();

      //조건에 의해 필터링된 민원 객체 생성
      await this.search();

    });

    //리셋 버튼 클릭시 이벤트
    document.getElementById("resetComplaint").addEventListener("click", async () => {

      //생성된 모달 클로즈
      this.customMap.analysisModal.close_modal();
      this.customMap.complaintModal.close_modal();

      //선택된 민원 및 사업장 해제
      this.customMap.clickoffComplaint();
      this.customMap.clickoffPlace();
      //원 및 부채꼴 선택 해제
      this.customMap.deletecurrentCircularSector();

      //이전에 생성된 민원 객체 삭제
      await this.complaintList.clearAllComplaints();

      //리셋 초기화
      await this.reset();
    });
  }

  //오늘 날짜 세팅
  setToday() {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  }

  //DB에 저장된 odorName 데이터 호출
  async getOdorNameALL() {
    try {
      const response = await fetch('/complaint/odorNameAll');
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();

      return data.list || [];
    } catch (error) {
      console.log("Error fetching odor names:", error);
      return [];
    }
  }

  //모든 민원 데이터 생성
  //초기 화면 민원 데이터 호출
  async fetchAllData() {
    try {
      const data = await this.getComplaintLocationDateData();
      if (data.length > 0) {
        // 모든 민원 데이터 객체 생성
        const promises = data.map((complaint) =>
          this.complaintList.addComplaintLocationDate(
            complaint.complaintId,
            { lat: complaint.latitude, lng: complaint.longitude },
            complaint.date
          )
        );
        await Promise.all(promises); // 모든 민원 데이터를 지도에 추가 완료
      } else {
        console.log("전체 데이터가 없습니다.");
      }
    } catch (error) {
      console.error('전체 데이터 로딩 중 오류 발생:', error);
    }
  }


  async search() {
    //검색 요소에 들어있는 데이터 가져오기
    const selectedStartDate = document.getElementById("selectStartDate").value
    const selectedEndDate = document.getElementById("selectEndDate").value
    const selectedOdorId = document.getElementById("selectOdorType").value
    const selectedOdorIntensity = document.getElementById("selectOdorIntensity").value


    //검색 조건에 의해 생성되는 파라미터 
    const searchParams = {
      startDate: selectedStartDate || null,
      endDate: selectedEndDate || null,
      odorIntensity: selectedOdorIntensity,
      odorId: selectedOdorId,
    };

    try {

      // 서버에 요청 보냄
      const data = await this.getComplaintLocationDateData(searchParams);

      if (data.length > 0) {
        // 새로운 민원 데이터를 지도에 추가
        const promises = data.map((complaint) =>
          this.complaintList.addComplaintLocationDate(
            complaint.complaintId,
            { lat: complaint.latitude, lng: complaint.longitude },
            complaint.date
          )

        );
        await Promise.all(promises); // 모든 민원 데이터를 지도에 추가 완료
      } else {
        console.log("검색 결과가 없습니다.");
      }
    } catch (error) {
      console.error('검색 중 오류 발생:', error);
    }

  }


  //민원 데이터 호출
  async getComplaintLocationDateData(params ) {
    try {
      let url;

      // 파라미터가 있는 경우 쿼리스트링 추가 후 민원 데이터 호출(검색 기능)
      if (params) {
        url = "/complaint/searchComplaintStartEnd";
        const queryParams = new URLSearchParams(params).toString();
        url += `?${queryParams}`;
      } else {
        //파라미터가 없는 경우 전체 데이터 호출(초기 호출 기능)
        url = "/complaint/complaintLocationDate";
      }

      const response = await fetch(url);

      if (!response.ok) {
        throw new Error("Network response was not ok");
      }

      const data = await response.json();


      return data.list || []; // 데이터가 없을 경우 빈 배열 반환
    } catch (error) {
      console.error("Error fetching complaintLocationDate data:", error);
      return []; // 오류 발생 시 빈 배열 반환
    }
  }

  //검색 초기화 
  async reset() {
    document.getElementById("selectStartDate").value = this.setToday();
    document.getElementById("selectEndDate").value = this.setToday();
    document.getElementById("selectOdorType").value = "";
    document.getElementById("selectOdorIntensity").value = "";

    //현재 DB에 저장된 모든 민원 데이터 호출 후 생성
    await this.fetchAllData();
  }
}

//마커 스위치 변경시 모달 표시에 관한 이벤트
document
  .getElementById("marker_hidden_slide")
  .addEventListener("change", function () {
    if (this.checked) {
      web.analysisModal.modal.style.display = "block";
      web.complaintModal.modal.style.display = "block";
    } else {
      web.analysisModal.modal.style.display = "none";
      web.complaintModal.modal.style.display = "none";
    }
  });