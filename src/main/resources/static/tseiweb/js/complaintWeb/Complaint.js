class Complaint {
    constructor(
        map,
        customMap,
        titleIndex,
        complaintId,
        username,
        context,
        provinceCityName,
        postalCode,
        streetAddress,
        latitude,
        longitude,
        windDirection,
        windSpeed,
        odor,
        odorIntensity,
        date,
        odorName

    ) {
        this.map = map;
        this.customMap = customMap;
        this.titleIndex = titleIndex;
        this.complaintId = complaintId;
        this.username = username;
        this.context = context;
        this.provinceCityName = provinceCityName;
        this.postalCode = postalCode;
        this.streetAddress = streetAddress;
        this.latitude = parseFloat(latitude);
        this.longitude = parseFloat(longitude);
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.odor = odor;
        this.odorIntensity = odorIntensity;
        this.date = date;
        this.odorName = odorName;
        this.marker = null;
        this.radius = 500;
        this.clickListener = null;
        this.angle = 30;
    }

    // 민원 관측지점의 위치를 구글 위치 객체로 변환
    getLocation() {
        return new google.maps.LatLng(this.latitude, this.longitude);
    }

    //관측지점 마커 생성
    async createMarker() {

        const location = this.getLocation();
        if (!location) return; // 좌표가 유효하지 않으면 마커를 생성하지 않음
        this.marker = new google.maps.Marker({
            position: this.getLocation(),
            map: this.map,
            icon: {
                url: "/static/tseiweb/img/complaint_markerNoIncludeDegreeSmell.png",
                scaledSize: new google.maps.Size(30, 30), // 아이콘 크기 설정
                origin: new google.maps.Point(0, 0), // 이미지의 원점
                anchor: new google.maps.Point(15, 15), // 이미지의 앵커 포인트 (중앙에 위치)

            },
        });
        //민원 이벤트 추가
        this.addEventListener();
    }

    //마커 색 변경
    markerSetColor(url) {
        this.marker.setIcon({
            url: url,
            scaledSize: new google.maps.Size(30, 30), // 아이콘 크기 설정
            origin: new google.maps.Point(0, 0), // 이미지의 원점
            anchor: new google.maps.Point(15, 15), // 이미지의 앵커 포인트 (중앙에 위치)
        });
    }

    //지도 화면에서 민원 객체 범위를 확인하기 위해 원과 부채꼴 생성
    async drawCircularSector(complaintData) {

        //이미 원과 부채꼴이 존재하는 경우, 제거
        if (this.customMap.currentCircularSector) {
            this.customMap.currentCircularSector.setMap(null);
            this.customMap.currentCircularSector = null;
            this.customMap.currentCircular.setMap(null);
            this.customMap.currentCircular = null;
        }

        const { spherical } = await google.maps.importLibrary("geometry");
        const data = await this.calculateCircularSectorParameters(complaintData);
        setRadioButtons(this);

        const segments = 1000;
        const segmentList = [];// 부채꼴 좌표 배열 초기화

        //중심점 좌표

        const center = this.getLocation();


        //전체 원의 좌표 계산
        for (let i = 0; i <= segments; i++) {
            const angleStep = 360 / segments;
            const vertexAngle = i * angleStep;
            const vertex = spherical.computeOffset(center, data.radius, vertexAngle)
            segmentList.push(vertex);

        }

        //부채꼴의 좌표 계산
        const sectorList = [center];
        for (let j = 0; j <= segments; j++) {
            const angleStep = (data.endAngle - data.startAngle) / segments;
            const vertexAngle = data.startAngle + j * angleStep;
            const vertex = spherical.computeOffset(center, data.radius, vertexAngle)
            sectorList.push(vertex);
        }
        sectorList.push(center);//부채꼴 닫기



        //원을 그리기위한 폴리곤 속성
        this.customMap.currentCircular = new google.maps.Polygon({
            paths: segmentList,
            strokeColor: "#FF0000",
            strokeOpacity: 0.5,
            fillColor: "green",
            fillOpacity: 0.1,
            clickable: false,
            map: this.map,
        });

        //부채꼴을 그리기 위한 폴리곤 속성
        this.customMap.currentCircularSector = new google.maps.Polygon({
            paths: sectorList,
            strokeColor: "#FF0000",
            strokeOpacity: 0.8,
            strokeWeight: 1,
            fillColor: "green",
            fillOpacity: 0.35,
            clickable: false,
            map: this.map,
        });
    }

    //경도를 설정하는 메서드
    setLatitude(latitude) {
        const newlatitude = parseFloat(latitude);
        this.latitude = newlatitude;
        //마커 위치 변경
        if (this.marker) {
            this.marker.setPosition({ lat: this.latitude, lng: this.longitude });
        }
    }

    //위도를 설정하는 메서드
    setLongitude(longitude) {
        const newlongitude = parseFloat(longitude);
        this.longitude = newlongitude;

        if (this.marker) {
            this.marker.setPosition({ lat: this.latitude, lng: this.longitude });
        }
    }


    //마커를 지도에서 제거하는 메서드
    removeComplaint() {
        this.customMap = null;
        this.titleIndex = null;
        this.complaintId = null;
        this.username = null;
        this.latitude = null;
        this.longitude = null;
        this.odor = null;
        this.odorintensity = null;
        this.contents = null;
        this.direction = null;
        this.radius = null;
        this.date = null;
        this.odorName = null;

        if (this.marker) {
            this.marker.setMap(null);
            this.marker = null;

        }
    }





    //민원 이벤트 정의
    addEventListener() {
        this.clickListener = this.marker.addListener("click", () => {
            this.checkmarker_event_start();
        });
    }


    //민원 클릭이벤트
    async checkmarker_event_start() {

        //왼쪽 메뉴 테이블 텍스트 지우기
        clearTableText();
        //민원 전체 내용 들고 오기
        const complaintList = await web.getComplaintData(this.complaintId);
        const complaintData = complaintList[0];
        //이전 클릭 정보 지우기
        this.customMap.clickoffComplaint();
        this.customMap.clickoffPlace();


        //마커 색 변환 및 클릭한 민원 객체 등록
        const url = "/static/tseiweb/img/complaint_markerIncludeDegreeSmell.png"
        this.markerSetColor(url);
        this.customMap.onclickComplaint(this);
        this.customMap.onclickComplaintData(complaintData);


        //왼쪽 메뉴 table 채우기
        fillCoordinateTable(complaintData.streetAddress, complaintData.latitude, complaintData.longitude, complaintData.date)
        fillOdorTable(complaintData.odorName, complaintData.odorIntensity);
        fillWindTable(complaintData.windDirection, complaintData.windSpeed);


        // //반경을 이용해 원 그리기
        await this.drawCircularSector(complaintData);


        //원 내부에 있는 장소 객체 데이터 리스트 구하기
        const placesInDistance = await this.customMap.placeList.findPlaceInDistance(
            this,
            this.radius
        );

        //왼쪽 메뉴 반경 내 있는 장소 객체 데이터 추가
        fillInRadiusTable(placesInDistance)

        //부채꼴 내부에 있는 장소 객체 데이터 리스트 구하기
        const placesInCircularSector =
            this.customMap.placeList.findPlaceinCircularSector(placesInDistance);
        //부채꼴 내부에 있는 장소 객체들 중 장소의 악취유형과 관측지점의 악취유형이 일치하는 데이터를 담을 리스트
        let matchingResult = [];
        if (placesInCircularSector) {
            for (const place of placesInCircularSector) {
                if (place.odor === complaintData.odorName) {
                    matchingResult.push(place);
                }
            }
        }

        //왼쪽 메뉴 부채꼴 내에 있는 장소 객체 데이터 추가
        if (matchingResult.length) {
            fillMatchingPlaceTable(matchingResult);
        }

        //민원 모달 열기 및 모달 설정
        this.customMap.complaintModal.open_modal();
        this.customMap.complaintModal.modal_init([complaintData]);
        this.customMap.complaintModal.switchModalTitle(this.titleIndex + "번 지점의 민원 정보")
        //분석 모달 열기 및 모달 설정
        this.customMap.analysisModal.open_modal();
        this.customMap.analysisModal.modal_init(matchingResult)
    }
    //부채꼴을 그리기 위한 반지름 및 각도 등의 매개변수를 계산하는 함수
    async calculateCircularSectorParameters(complaintData) {
        const baseRadius = this.radius;
        const baseAngle = this.angle;
        let direction = complaintData.windDirection;

        let startAngle = complaintData.windDirection - baseAngle / 2;
        let endAngle = complaintData.windDirection + baseAngle / 2;

        return {
            radius: baseRadius,
            startAngle: startAngle,
            endAngle: endAngle,
            direction: direction,
        };
    }

}