// WebSocket 연결 설정 (주석 처리된 부분은 필요에 따라 활성화)
// const ws = new WebSocket('ws://localhost:8080/ws/complaints')

// ws.onopen = function() {
//     console.log('WebSocket connection opened');
// }

// ws.onmessage = function(event) {
//     const complaints = JSON.parse(event.data);
//     updateComplaintList(complaints); // 'updateComlaintList' -> 'updateComplaintList'
// }

// ws.onclose = function() 
//     console.log('WebSocket connection closed');
// }
class Web {
    constructor() {
        this.customMap = null;
        this.analysisModal = null;
        this.complaintModal = null;
        this.loading = null;
        this.complaintList = null;
        this.sourcePlaceList = null;

        this.sequentialCarIndex = 0;
        this.rangeValue = 0;
        this.search = null
    }


    //웹 초기화(데이터 초기화 및 데이터 세팅)
    async init() {
        return new Promise((resolve, reject) => {
            //로딩, 모달, 지도 객체 생성
            this.loading = new Loading("loading-anim", "process-bar");
            this.analysisModal = new AnalysisModal("analysisModal");
            this.complaintModal = new ComplaintModal("complaintModal");
            this.customMap = new CustomMap(this.analysisModal, this.complaintModal);

            //지도 중심위치 지정 및 초기화
            this.customMap
                .init(35.456966, 129.32799) //초기 중심 고정 좌표

                .then(async () => {
                    //장소 객체, 검색 객체 선언
                    this.sourcePlaceList = new SourcePlaceList(this.customMap.map, this.customMap);
                    this.search = new Search(this.customMap.map, this.customMap,);


                    //지도객체 장소 객체 참조 추가
                    this.customMap.setPlaceList(this.sourcePlaceList);


                    //민원 및 장소 데이터 세팅
                    await this.setData();

                    //검색 객체 초기화
                    this.search.init();

                    this.loading.loading_off();
                    resolve();
                }).catch((error) => { reject(error); });




        });


    }

    // 전체 리스트 데이터 세팅하기
    async setData() {

        try {

            //사업장의 데이터 저장
            const placeLocationList = await this.getPlaceData();

            const placePromises = placeLocationList.map((data) =>
                this.sourcePlaceList.addPlace(
                    data.companyIndex,
                    data.name,
                    {
                        lat: data.latitude,
                        lng: data.longitude,
                    },
                    data.csvFilename,
                    data.odor
                ));

            await Promise.all(placePromises);

            this.sourcePlaceList.makeCluster();
        } catch (error) {
            console.error('데이터 set 실패:', error)
        }
    }


    //장소 데이터 호출 
    async getPlaceData() {
        try {
            const response = await fetch("/complaint/places");
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            const data = await response.json();
            return this.makeNamesUnique(data.list);
        } catch (error) {
            console.error("Error fetching places data:", error);
            return [];
        }
    }


    //민원 상세 데이터 호출
    async getComplaintData(complaintId, isModal = true) {
        const url = `/complaint/complaintdata?complaintId=${complaintId}`;
        try {
            const response = await fetch(url)
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            const data = await response.json();

            return data.list;

        } catch (error) {
            console.error("Error feching complaint data:", error);
            return [];
        }

    }
    //사업장의 상세 데이터(화학 물질 정보) 호출
    async getPlaceChemicalData(companyIndex) {

        let chemicalData = [];
        try {
            const response = await fetch(`/complaint/complaintPlaceCsvContent?company_id=${companyIndex}`);
            const data = await response.json();
            chemicalData = data.list;
        } catch (error) {
            console.error("Error fetching chemical data:", error);
        }
        return chemicalData;
    }


    //사업장 중복 이름 unique 처리
    makeNamesUnique(dataList) {
        const nameCounts = {};
        return dataList.map((item) => {
            let originalName;
            let newName;

            if (!item.hasOwnProperty("name")) {
                originalName = item;
                newName = originalName;
            } else {
                originalName = item.name;
                newName = originalName;
            }

            if (nameCounts[originalName] === undefined) {
                nameCounts[originalName] = 0;
            } else {
                nameCounts[originalName]++;
                newName = `${originalName}-${nameCounts[originalName]}`;
            }

            if (!item.hasOwnProperty("name")) {
                return newName;
            } else {
                item.name = newName;
                return item;
            }
        });
    }


}




// 전역 변수 선언
const web = new Web();
window.web = web;
var mouseover = null;
let Markers = [];
// DOM 실행시 동작
const initListener = async () => {
    try {
        await web.init();
    } catch (error) {
        console.error("Initialization failed:", error);
    }
};
document.addEventListener("DOMContentLoaded", initListener);









