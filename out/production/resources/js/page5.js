// tab1(악취기여물질)과 tab4(설정)에 사용// => //tab2 제외 모두 사용
// 위치 변경하지 말아주세요!

//공통 검색 함수
function addSearchFunctionality(searchElement, tableElements) {
  if (!searchElement || !tableElements || tableElements.length === 0) {
    return;
  }

  searchElement.addEventListener("keyup", function () {
    const filterValue = searchElement.value.toLowerCase();

    //테이블 요소들 각각에 대해 필터링 적용
    tableElements.forEach(function (tableElement) {
      const rows = tableElement.querySelectorAll("tr"); //각 테이블의 행 가져오기

      for (var i = 0; i < rows.length; i++) {
        var rowText = rows[i].textContent.toLowerCase(); // 행의 텍스트를 소문자로 변환
        if (rowText.includes(filterValue)) {
          rows[i].style.display = ""; // 검색어가 포함되면 표시
        } else {
          rows[i].style.display = "none"; // 검색어가 없으면 숨기기
        }
      }
    });
  });
}
// 악취기여물질 검색창 element를 id값으로 가져오기
const payrollSearch = document.querySelector("#search-bar");
// 악취기여물질 테이블의 tbody element를 클래스 값으로 가져오기
const payrollTable = document.querySelectorAll(".searchfactor tbody");

// 검색 기능 적용 (악취기여물질 테이블)
addSearchFunctionality(payrollSearch, payrollTable);

// 방해인자 검색창 element를 id값으로 가져오기
const payrollSearch_interruption = document.querySelector(
  "#interruption-search-bar"
);
// 방해인자 테이블의 tbody element를 클래스 값으로 가져오기
const payrollTable_interruption = document.querySelectorAll(
  ".interruptionfactor tbody"
);

// 검색 기능 적용 (방해인자 테이블)
addSearchFunctionality(payrollSearch_interruption, payrollTable_interruption);

//select 시 시설 변경
function change_facility(langSelect, facilites) {
  if (!langSelect || !(langSelect instanceof HTMLSelectElement)) {
    return;
  }

  if (!facilites || !(facilites instanceof HTMLElement)) {
    return;
  }

  // select element에서 선택된 option의 text가 저장된다.
  var selectText = langSelect.options[langSelect.selectedIndex]?.text;
  // 현재 tbody안에 있는 모든 div element를 가져와 rows에 저장
  var rows = facilites.querySelectorAll("div");

  for (var i = 0; i < rows.length; i++) {
    //div element중 id를 가지지 않는 element들은 무시
    if (rows[i].id) {
      //select한 시설과 id가 일치하면 보여주고 아니면 숨김
      if (selectText == rows[i].id) {
        rows[i].style.display = "block";
      } else {
        rows[i].style.display = "none";
      }
    }
  }
}

// 각 방지시설 종류마다 셀렉트박스와 시설을 할당
function chageLangSelect(num) {
  var langSelect;
  var facilites;
  //시설 셀렉트 할당
  langSelect = document.getElementById("selectbox" + num);
  //시설의 각 시설들 할당
  facilites = document.getElementById("select_facilities" + num);
  //select값과 시설 할당 된 것들 전달
  change_facility(langSelect, facilites);
}

document.addEventListener("DOMContentLoaded", function () {
  //페이지가 로딩될 때 select 된 시설만 표시
  chageLangSelect(1);
  chageLangSelect(2);
  chageLangSelect(3);
  chageLangSelect(4);
});
