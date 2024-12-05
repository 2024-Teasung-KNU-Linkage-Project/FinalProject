class CustomMap {
  constructor(analysisModal, compareModal) {
    this.analysisModal = analysisModal;
    this.compareModal = compareModal;
    this.placeList = null;
    this.carList = null;
    this.currentCircularSector = null;
    this.onCar = null;
    this.onPlace = null;
    this.map = null;
    this.now_lat = null;
    this.now_lon = null;
    this.address = null;
  }

  // 장소 리스트 참조 만들기
  setPlaceList(placeList) {
    this.placeList = placeList;
  }

  // 차량리스트 참조만들기
  setCarList(carList) {
    this.carList = carList;
  }

  // 현재 클릭 된 차량 마커 저장
  onclickCar(car) {
    this.onCar = car;
  }

  // 현재 클릭 된 장소 마커 저장
  onclickPlace(place) {
    this.onPlace = place;
  }

  // 클릭 상태 제거
  clickoffCar() {
    if (this.onCar) this.onCar.markerSetColor("blue");
    this.onCar = null;
  }

  // 클릭 상태 제거
  clickoffPlace() {
    if (this.onPlace) this.onPlace.markerSetColor("blue");
    this.onPlace = null;
  }

  // 지도 객체 초기화
  async init(lat, lng) {
    if (this.placeList) this.placeList.init();

    if (this.carList) this.carList.init();

    const { Map, Geocoder, event } = await google.maps.importLibrary("maps");

    const position = new google.maps.LatLng({ lat: lat, lng: lng });
    const zoom = 15;
    const options = {
      center: position,
      zoom: zoom,
      mapId: "50a49411f97c72d2",
      disableDefaultUI: true,
      disableDoubleClickZoom: true,
      draggable: true,
      keyboardShortcuts: false,
      gestureHandling: "greedy",
      zoomControl: false,
      mapTypeControl: false,
      mapTypeControlOptions: {
        style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
        position: google.maps.ControlPosition.TOP_RIGHT,
      },
      scaleControl: false,
      streetViewControl: false,
      rotateControl: true,
      mapTypeId: google.maps.MapTypeId.SATELLITE,
    };

    this.map = new Map(document.getElementById("map"), options);

    // 지도 클릭시 이벤트
    google.maps.event.addListener(this.map, "click", () => {
      // 모달 닫기
      this.analysisModal.close_modal();
      this.compareModal.close_modal();
      // 연관 검색어 숨기기
      $("#relatedKeywords").hide();

      //클릭상태 제거
      this.clickoffCar();
      this.clickoffPlace();
      //부채꼴 및 원 제거
      if (this.currentCircularSector) {
        this.currentCircularSector.setMap(null);
        this.currentCircularSector = null;
        this.currentCircular.setMap(null);
        this.currentCircular = null;
      }
    });

    // 지도 멈췄을때 이벤트
    this.map.addListener("idle", () => {
      this.handleMapIdle();
    });

  }


   // 지도 멈췄을때 이벤트 
  async handleMapIdle() {
    const center = this.map.getCenter();
    this.now_lat = center.lat();
    this.now_lon = center.lng();
    const geocoder = new google.maps.Geocoder();

    try {

      // 좌표 정보 가져오기 및 좌측 메뉴 주소 바꾸기
      geocoder.geocode({ location: center }, (results, status) => {
        if (status === "OK") {
          if (results[0]) {
            this.address = results[0].formatted_address;
            this.address = this.address.replace("대한민국 ", "");

            $("#nowPosition").replaceWith(
              '<h1 id="nowPosition">' + this.address + "</h1>"
            );
          }
        } 
      });
    } catch (error) {
      alert("위치 정보를 불러올 수 없습니다. 좌표를 확인하세요.");
    }
  }
}
