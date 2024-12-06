
class Modal {
  constructor(modalId) {
    this.modalId = modalId;
    this.modal = document.getElementById(modalId);
    this.isDragging = false;
    this.dragStartX = 0;
    this.dragStartY = 0;
    this.dragStartLeft = 0;
    this.dragStartTop = 0;
    this.carValueSortedData;
    this.carRatioSortedData;
    this.placeValueCommonData;
    this.placeRatioCommonData;

    // 이벤트 리스너 등록
    this.modal.addEventListener("mousedown", this.handleMouseDown.bind(this));
    this.modal.addEventListener("mousemove", this.handleMouseMove.bind(this));
    this.modal.addEventListener("mouseup", this.handleMouseUp.bind(this));

  }

  // 드래그 시작 위치 저장
  handleMouseDown(e) {
    this.isDragging = true;
    this.dragStartX = e.clientX;
    this.dragStartY = e.clientY;
    this.dragStartLeft = parseInt(
      window.getComputedStyle(this.modal).getPropertyValue("left")
    );
    this.dragStartTop = parseInt(
      window.getComputedStyle(this.modal).getPropertyValue("top")
    );
  }

  // 드레그 중 모달 위치 업데이트
  handleMouseMove(e) {
    if (this.isDragging) {
      var offsetX = e.clientX - this.dragStartX;
      var offsetY = e.clientY - this.dragStartY;
      var newLeft = this.dragStartLeft + offsetX;
      var newTop = this.dragStartTop + offsetY;
      this.modal.style.left = newLeft + "px";
      this.modal.style.top = newTop + "px";
    }
  }

  // 드레그 종료
  handleMouseUp() {
    this.isDragging = false;
  }

  // 모달 닫기
  close_modal() {
    //모달 on/off 버튼 켜놓기
    const checkbox = document.getElementById("marker_hidden_slide");
    checkbox.checked = true;
    //좌측 메뉴 초기화
    clearTableText();
    // 필드 초기화
    this.complaintValueSortedData = null;
    this.complaintRatioSortedData = null;
    this.placeValueCommonData = null;
    this.placeRatioCommonData = null;

    //모달 숨기기
    if (this.modal.style.display === "block") {
      this.modal.style.display = "none";
    }


  }

  // 모달 열기
  open_modal() {

    // 초기 위치 변경
    if (this.modalId == "complaintModal") {
      this.modal.style.display = "block"
      this.modal.style.left = "60%";
      this.modal.style.top = "17%";
    } else if (this.modalId == "analysisModal") {
      this.modal.style.display = "block"
      this.modal.style.left = "20%";
      this.modal.style.top = "17%";
    }

    //모달 on/off 버튼이 on일때만 보이도록하기
    const checkbox = document.getElementById("marker_hidden_slide");
    if (checkbox.checked) {
      this.modal.style.display = "block";
    }
  }
}

//민원 모달(민원 상세 정보)
class ComplaintModal extends Modal {
  constructor(modalId) {
    super(modalId)
  }

  //동적으로 민원 정보 테이블 태그 만들기
  modal_init(complaintData) {
    const complaintTable = document.getElementById("complaintTable");
    this.makeComplaintTablehead(complaintData, complaintTable);
  }

  //민원 테이블 헤더 만들기 및 바디 만들기
  makeComplaintTablehead(complaintData, table) {
    const thead = table.getElementsByTagName("thead")[0];
    thead.innerHTML =
      "";
    const tbody = table.getElementsByTagName("tbody")[0];
    tbody.innerHTML = this.generateComplaintRow(complaintData);
    table.style.marginBottom = "20px";

  }

  //민원 데이터 테이블 만들기
  generateComplaintRow(data) {
    const complaint = data[0] || []; // 데이터가 없을 경우 빈 객체로 처리
    const windDir = getWindDirection8(complaint.windDirection)
    return `
        <tr>
          <td style="font-weight: bold; width: 180px;background-color: #1a305f;">날짜</td>
          <td>${complaint.date || "-"}</td>
        </tr>
        <tr>
          <td style="font-weight: bold; width: 180px;background-color: #1a305f;">사용자</td>
          <td>${complaint.userName || "-"}</td>
        </tr>
        <tr>
        <td style="font-weight: bold; width: 180px;background-color: #1a305f;">시도명</td>
        <td>${complaint.provinceCityName || "-"}</td>
        </tr>
        <tr>
        <td style="font-weight: bold; width: 180px;background-color: #1a305f;">우편번호</td>
        <td>${complaint.postalCode || "-"}</td>
        </tr>
        <tr>
        <td style="font-weight: bold; width: 180px;background-color: #1a305f;">도로명</td>
        <td>${complaint.streetAddress || "-"}</td>
        </tr>
        <tr>
          <td style="font-weight: bold; width: 180px;background-color: #1a305f;">악취</td>
          <td>${complaint.odorName || "-"}  </td>
        </tr>
        <tr>
          <td style="font-weight: bold; width: 180px;background-color: #1a305f;">악취 세기</td>
          <td>${complaint.odorIntensity || "-"}</td>
        <tr>
          <td style="font-weight: bold; width: 180px;background-color: #1a305f;">바람 방향/속도</td>
          <td>${windDir || "-"} / ${complaint.windSpeed || "-"}m/s</td>
        </tr>
        <tr style="height:100px; max-height:100px;">
          <td style="font-weight: bold; background-color: #1a305f; ">내용</td>
          <td style="overflow-y:auto;">${complaint.context || "-"}</td>
        </tr>
      `;
  }


  //민원 titleindex 변경
  switchModalTitle(text) {
    const modalTextElement = document.getElementById("complaintModalText");
    if (modalTextElement) {
      modalTextElement.textContent = text;
    }
  }

}

// 분석 모달(객체별 화학 물질 정보 표시)
class AnalysisModal extends Modal {
  constructor(modalId) {
    super(modalId);
  }

  //모달 초기화, 분석 결과 테이블 생성 및 데이터 채우기
  modal_init(matchingResults) {
    const analysisTable = document.getElementById("analysisTable");
    this.makeAnalysisTablehead(analysisTable);
    this.makeAnalysisTableBody(matchingResults, analysisTable);
  }

  //분석 모달 테이블 헤더 생성 함수 
  makeAnalysisTablehead(analysisTable) {
    analysisTable.innerHTML = '';//기존 내용 삭제
    const thead = analysisTable.createTHead();//테이블 헤더 요소 생성
    const row = thead.insertRow();//새로운 행 추가
    const headers = ["사업장 이름", "주소", "악취"];//헤더 이름 설정
    //각 헤더에 애해 th요소를 생성하여 추가
    headers.forEach(header => {
      const th = document.createElement("th");
      th.textContent = header;
      th.style.padding = "5px";
      row.appendChild(th);
    })
  }
  //분석 모달 테이블 본문 생성 함수
  makeAnalysisTableBody(matchingResults, analysisTable) {
    const tbody = document.createElement("tbody");

    matchingResults.forEach((result, index) => {
      const row = tbody.insertRow();
      row.dataset.index = index;
      row.classList.add("table-row"); // 클래스 추가

      const nameCell = row.insertCell();
      nameCell.textContent = result.title || "-";
      nameCell.style.padding = "5px";

      const addressCell = row.insertCell();
      addressCell.textContent = result.latitude && result.longitude ? `${result.latitude}/${result.longitude}` : "-";
      addressCell.style.padding = "5px";

      const odortypeCell = row.insertCell();
      odortypeCell.textContent = result.odor || "-";
      odortypeCell.style.padding = "5px";

      //행 클릭 시 상세 정보 표시 토글 
      row.addEventListener("click", async () => await this.toggleDetailRow(row, result, analysisTable));
    });

    analysisTable.appendChild(tbody);
  }

  //테이블 행 클릭 시 디테일 행을 토글하는 함수
  async toggleDetailRow(row, result, analysisTable) {
    const existingDetailRow = analysisTable.querySelector(".detailRow");

    //이미 디테일 행이 열려 있을 경우
    if (existingDetailRow) {
      if (existingDetailRow === row.nextElementSibling) {
        //동일한 행을 클릭하면 디테일 행 제거
        existingDetailRow.remove();
        return;
      } else {
        //다른 행을 클릭하면 기존 디테일 행을 제거
        existingDetailRow.remove();
      }
    }

    // 클릭한 행의 다음에 디테일 행을 추가합니다.
    const detailRow = analysisTable.insertRow(row.rowIndex + 1);
    detailRow.classList.add("detailRow");
    const detailCell = detailRow.insertCell(0);
    detailCell.colSpan = 3;

    // 회사 ID를 사용하여 화학 데이터를 가져옵니다.
    const chemicalData = await web.getPlaceChemicalData(result.companyIndex);


    if (Array.isArray(chemicalData) && chemicalData.length > 0) {
      let valueSum = 0;

      //각 화학 물질의 희석 배수와 총합 계산
      for (let chemical of chemicalData) {

        chemical.minimumValue = chemical.msv != null ? chemical.msv : 1; // msr가 없으면 기본값 1 사용
        chemical.dilutionRate = chemical.chemicalValue / chemical.minimumValue;
        valueSum += chemical.dilutionRate;
      }

      //각 화학 물질의 비율 계산
      for (let chemical of chemicalData) {
        chemical.relativeRatio = (chemical.dilutionRate / valueSum) * 100;
      }

      //화학 데이터 농도 기준으로 정렬
      const sortedChemicalData = chemicalData.sort((a, b) => b.chemicalValue - a.chemicalValue);


      // 디테일 데이터를 렌더링하여 디테일 셀에 추가합니다.
      detailCell.innerHTML = this.renderDetailData(sortedChemicalData);
    } else {
      detailCell.innerHTML = this.renderDetailData([]);
    }


  }

  //상세 데이터를 테이블로 렌더링 하는 함수
  renderDetailData(detailData) {
    let detailHTML = `
              <table class ="detailTable" style="background-color: #30497d; margin-top:5px;">
              <thead>
              <tr>
              <th width='105px'>물질명</th>
              <th width='105px'>농도</th>
              <th width='105px'>최소 감지 값</th>
              <th width='105px'>희석 배수</th>
              <th width='105px'>비율</th>
              </tr>
              </thead>
              <tbody>`;

    if (detailData.length === 0) {
      // 데이터가 없을 때 기본 "-" 값 표시
      detailHTML += `
                  <tr>
                    <td>-</td>
                    <td>-</td>
                    <td>-</td>
                    <td>-</td>
                    <td>-</td>
                  </tr>`;
    } else {
      detailData.forEach(data => {
        detailHTML += `
                    <tr>
                      <td>${data.chemicalName ? data.chemicalName : "-"}</td>
                      <td>${data.chemicalValue ? data.chemicalValue.toFixed(1) : "-"}</td>
                      <td>${data.minimumValue ? data.minimumValue.toFixed(1) : "-"}</td>
                      <td>${data.dilutionRate ? data.dilutionRate.toFixed(1) : "-"}</td>
                      <td>${data.relativeRatio ? data.relativeRatio.toFixed(1) : "-"}</td>
                    </tr>`;
      });
    }

    detailHTML += `
              </tbody>
              </table>
              `;
    return detailHTML;
  }


  // 분석테이블 제목 바꾸기
  switchModalTitle(text) {
    const modalTextElement = document.getElementById("modalText");
    if (modalTextElement) {
      modalTextElement.textContent = text;
    }
  }


}
