class ComplaintList {
    constructor(map, customMap) {
        this.map = map;
        this.customMap = customMap;
        this.complaints = [];
        this.titleIndex = 1;
        this.complaintMarkers = [];
    }


    //민원 리스트 초기화
    init() {
        this.complaints.forEach((complaint) => complaint.removeComplaint());
        this.complaints = [];
        this.complaintLocations = [];
        this.complaintTitleIndex = 1;
        this.complaintMarkers = [];
    }

    //DB 민원 위도 경도 데이터를 구글의 위도 경도 좌표 데이터로 변환
    replaceLocationObject(location) {
        return new google.maps.LatLng(location.lat, location.lng);
    }


    //민원 객체를 민원 리스트에 추가
    async addComplaintLocationDate(complaintId, complaintLocation, date) {
        const complaint = new Complaint(
            this.map,
            this.customMap,
            this.titleIndex,
            complaintId,
            "", // username
            "", // context
            "", // provinceCityName
            "", // postalCode
            "", // streetAddress
            complaintLocation.lat,
            complaintLocation.lng,
            "", // windDirection
            "", // windSpeed
            "", // odor
            "", // odorIntensity
            date,
            ""
        );
        this.titleIndex += 1;
        await complaint.createMarker();
        this.complaints.push(complaint);
        this.complaintMarkers.push(complaint.marker);
    }

    //모든 민원 객체 지우기
    async clearAllComplaints() {

        this.complaints.forEach((complaint) => {
            complaint.removeComplaint();

        });
        this.complaints = [];
        this.titleIndex = 1;
        this.complaintMarkers = [];

    }

}
