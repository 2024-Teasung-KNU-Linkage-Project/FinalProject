//예측설비//

// predict 탭의 방지시설 리스트 클릭 효과
const nonClick = document.querySelectorAll(".detail-table-unchecked");

window.onload = function () {
  // 세션 스토리지에서 값 가져오기
  var selectedCompanyName = sessionStorage.getItem("selectedCompanyName");
  var selectedOutletName = sessionStorage.getItem("selectedOutletName");

  // HTML에 값 삽입하기
  if (selectedCompanyName && selectedOutletName) {
    document.getElementById("company-outlet").textContent =
      selectedCompanyName + " - " + selectedOutletName;
  }
};

function handleClick(event) {
  // 로딩 스피너 표시
  $("#loading-spinner").show();

  // div에서 모든 "click" 클래스 제거
  nonClick.forEach((e) => {
    e.classList.remove("detail-table-checked");
  });
  // 클릭한 div만 "click"클래스 추가
  event.target.classList.add("detail-table-checked");

  //클릭된 div가 몇번째 조합인지 불러오기
  var clicked = event.target.firstElementChild;
  var texts = clicked.innerText.split(".");
  //console.log(texts);

  //서버로 전송해서 이미지파일 리스트 요청
  $.ajax({
    type: "post",
    url: "/soms/page5/predict?i=" + parseInt(texts[1], 10),
    dataType: "text",
    success: function (imagelist) {
      //성공 시 이미지 테이블 비우고
      $("#image-facility").empty();
      //console.log(imagelist);

      //이미지파일 리스트 각각 요소에 대해
      $.each(JSON.parse(imagelist), function (index, item) {
        //동적 테이블 생성

        var facImg = '<div class="comb-image">'; // 이미지와 표를 감싸는 div 생성

        //코드는 디자인상 필요하시면 바꾸시면 됩니다. 리스트 인덱스 필요하면 위에 function(index, item)에 index 쓰세요

        // 이미지 요소 추가
        facImg += '<img src="/static/solution/img/' + item + '"';
        facImg +=
          'alt="No image" class="image_left" style="width: 100%; height: 55%; padding: 10px 0px;" ';
        facImg += 'onclick="window.open(this.src)">';

        // 시설 상태 정보를 포함하는 표 추가
        facImg += '<div class="facility-state">';

        facImg += "<p>시설 상태 정보가 없습니다</p>"; // 시설 상태 정보 값 넣어줄 것. //

        facImg += "</div>"; // facility-state div 닫기
        facImg += "</div>"; // comb-image div 닫기

        $("#image-facility").append(facImg); //id="image-facility" 요소 안에 facImg 추가
      });
      // 로딩 스피너 숨기기
      $("#loading-spinner").hide();
    },
    error: function () {
      alert("시설 이미지 조회에 실패하였습니다.");
      // 로딩 스피너 숨기기
      $("#loading-spinner").hide();
    },
  });
}

nonClick.forEach((e) => {
  e.addEventListener("click", handleClick);
});

//결과 조회 버튼
const showResultButton = document.querySelector("#show-result-btn");

function showResult() {
  var clicked = document.querySelector(".detail-table-checked");
  if (clicked == null) {
    alert("조합을 선택해 주세요");
  } else {
    // 로딩 스피너 표시
    $("#loading-spinner").show();

    var texts = clicked.firstElementChild.innerText.split(".");

    location.href = "/soms/page5/predict?result=" + parseInt(texts[1], 10);
  }
}

showResultButton.addEventListener("click", showResult);

//체크박스
$(document).ready(function () {
  $("#form-pre input[type=checkbox]").change(function () {
    var data = $("#form-pre").serialize();
    // console.log(data);
    $.ajax({
      type: "post",
      url: "/soms/page5/predict?pre",
      data: data,
      dataType: "text",
      success: function () {
        document.location.reload();
      },
      error: function () {
        alert("체크박스 반영에 실패하였습니다.");
      },
    });
  });
  $("#form-proof input[type=checkbox]").change(function () {
    var data = $("#form-proof").serialize();
    $.ajax({
      type: "post",
      url: "/soms/page5/predict?proof",
      data: data,
      dataType: "text",
      success: function () {
        document.location.reload();
      },
      error: function () {
        alert("체크박스 반영에 실패하였습니다.");
      },
    });
  });
  $("#form-post input[type=checkbox]").change(function () {
    var data = $("#form-post").serialize();
    $.ajax({
      type: "post",
      url: "/soms/page5/predict?post",
      data: data,
      dataType: "text",
      success: function () {
        document.location.reload();
      },
      error: function () {
        alert("체크박스 반영에 실패하였습니다.");
      },
    });
  });
  $("#form-carb input[type=checkbox]").change(function () {
    var data = $("#form-carb").serialize();
    $.ajax({
      type: "post",
      url: "/soms/page5/predict?carb",
      data: data,
      dataType: "text",
      success: function () {
        document.location.reload();
      },
      error: function () {
        alert("체크박스 반영에 실패하였습니다.");
      },
    });
  });
});
