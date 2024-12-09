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

  //서버로 전송해서 이미지파일 및 상세정보 리스트 요청
  $.ajax({
    type: "post",
    url: "/soms/page5/predict?i=" + parseInt(texts[1], 10),
    dataType: "json",
    success: function (response) {
      //성공 시 이미지 테이블 비우고
      $("#image-facility").empty();
      //console.log(imagelist);

      //이미지파일 리스트 각각 요소에 대해
      response.forEach(function (facility)  {

        let facilityHtml = '<div class="comb-image">';

        // 이미지 추가
        facilityHtml += '<img src="/static/solution/img/' + facility.imagesrc + '"';
        facilityHtml +=
            'alt="No image" class="image_left" style="width: 100%; height: 55%; padding: 10px 0px;" ';
        facilityHtml += 'onclick="window.open(this.src)">';

        // 시설 상세 정보 표시
        // 시설 정보 추가 표시 필요하면 json 수정 후 추가 가능
        facilityHtml += '<div class="facility-state">';
        facilityHtml += "<p>시설 이름: " + facility.name + "</p>";
        facilityHtml += "<p>시설 종류: " + facility.category + "</p>";
        facilityHtml += "<p>시설 가격: " + facility.cost + "</p>";
        facilityHtml += "</div>";

        facilityHtml += "</div>";

        $("#image-facility").append(facilityHtml); //id="image-facility" 요소 안에 facilityHtml 추가
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
