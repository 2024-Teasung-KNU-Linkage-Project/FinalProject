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

# 개선 사항
- 방지시설예측 시스템에 input, predict, result, setting 4개 페이지 모두 검색 기능 되도록 추가✅
- 검색창 사업장, 공정명 받아오는 방식 수정✅
- input 페이지 이론적 희석배수랑 목표 희석배수 차이 클 때 무한루프 발생 수정 - 무한루프가 아니고 단순 연산 시간 오래걸리는거로 보임
- predict 페이지 방지시설 조합 중복 결과 수정✅
- result 페이지 파일 내보내기 시 파일명 인코딩 ✅
- setting 페이지로 이동 시 로딩스피너 추가✅
- 민원 기능 통합✅
- 3개월 이내의 민원만 표시
- 악취 종류 비교 파트 odorName에서 odorID로 수정✅
- 민원신고기능✅
- DB에 기존 민원 데이터 추가✅
- 화면 깨지는 부분 수정
- 악취 세기 0~5로 수정
- 현재 검색 후 다른 검색 시도 시 악취 종류, 악취 세기 선택사항에 null에 해당하는 값이 없는 점 개선
