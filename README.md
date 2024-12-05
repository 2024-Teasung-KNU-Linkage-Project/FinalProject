# 코드 수정 사항

## 1. 좌측 메뉴창 변경 - trackingByCar/leftmenu
### leftmenu.html
- menu-info-detail1
    - style에서 width 340px 제거

- div class="solution-button" 추가
    - img - shield.png 추가
    - Modal.js에서 버튼 생성 대신, solution-button에 리다이렉션 지정
      - 기존 데이터 전송 버튼 이해한거 기록
        - SourcePlace.js 에서 장소 버튼 클릭
        <br>이후 modal_init 여기서 실행
        <br>이때 파라미터로 csvFileName 넘겨줌

### leftmenu.css (static/tseiweb 하위)
- .menu-info-detail2-1 에
    - padding을 5px -> 5px 0px로 변경

- .contents-leftmenu에서
    - height: calc(100vh - 106px);
  <br>min-width 300px 추가
  <br>overflow-y: scroll 추가

- .solution-button 관련 추가

## 2. solution/header
### header.html (solution 하위)
- header-center
    - style padding 제거

### resources/css/header.css (static 하위 아님 주의)
- header-container
    - justify-content 추가

- header-center
    - margin-left 제거

## 3. 검색 돋보기 날아간 거 추가
- MonitoringSystem.html
  - font/bootstrap-icons.css 링크 추가

## 4. 창 2개 뜨는거 세로 1개로 합치기