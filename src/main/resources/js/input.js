const viewTableButton = document.querySelector("#theoretical");
const nextTabButton = document.querySelector("#predict");

//사업장 조회
$(document).ready(function () {
  let selectedCompanyName = null; //사업장 명
  let selectedCompanyId = null; //사업장 id
  let selectedCompanyQuery = null; //배출구 검색 쿼리
  let outlet = null;

  //저장된 배출구 불러오기
  const savedOutlets = sessionStorage.getItem("autocompleteOutlets");
  if (savedOutlets) {
    const outlets = JSON.parse(savedOutlets);
    renderOutlet(outlets); // 저장된 자동완성 목록 렌더링
  }

  //페이지 로딩 시 세션 스토리지에 값 가져오기
  const savedCompanyName = sessionStorage.getItem("selectedCompanyName");
  if (savedCompanyName) {
    $("#company-search").val(savedCompanyName);
  }

  //검색어 입력시 자동완성 목록 표시
  $("#company-search").on("input", function () {
    var searchTerm = $(this).val(); //입력된 값을 가져옴
    if (searchTerm.length > 0) {
      // AJAX 요청을 보내서 서버에서 데이터를 가져옴
      $.ajax({
        url: "/soms/page5/input/autocomplete",
        method: "GET",
        data: { name: searchTerm },
        dataType: "json",
        success: function (res) {
          //성공적으로 데이터를 받아온 경우
          var suggestions = $("#suggestions");
          suggestions.empty();

          const companyNameSet = new Set(); //중복된 회사명 추적

          if (res.length > 0) {
            res.forEach(function (company) {
              outlet = company.outlet;
              const set1 = new Set(company.name);
              const set2 = new Set("-"+outlet);

              // 차집합 계산
              const diff = [...set1].filter(char => !set2.has(char));

              // 배열을 문자열로 변환하여 반환합니다.
              console.log(diff.join('') + "//" +outlet);

              //하이픈이 있는지 확인
              var companyNameParts = company.name.split("-");
              var displayName = companyNameParts[0]; //기본적으로 하이픈 앞부분 사용
              // 하이픈이 2개 포함된 경우
              if (companyNameParts.length >= 3) {
                var displayName = companyNameParts[0] + companyNameParts[1]; // 사업장 명: 첫 번째 + 두 번째

                // 중복된 이름이 아닌 경우 추가
                if (!companyNameSet.has(displayName)) {
                  companyNameSet.add(displayName);
                  suggestions.append(
                    '<li data-company-name="' + company.name + '"data-company-id="' + company.companyIndex +'" data-company-outlet="' + company.outlet + '">' +
                      displayName + // 표시할 이름
                      "</li>"
                  );
                }

                // 하이픈이 1개인 경우
              } else if (companyNameParts.length === 2) {
                var displayName = companyNameParts[0]; // 사업장 명: 첫 번째

                if (!companyNameSet.has(displayName)) {
                  companyNameSet.add(displayName);

                  suggestions.append(
                    '<li data-company-name="' + company.name + '" data-company-id="' + company.companyIndex + '" data-company-outlet="' + company.outlet + '">' +
                      displayName + //표시할 이름
                      "</li>"
                  );
                }
              } else {
                displayName = company.name;
                suggestions.append(
                  '<li data-company-name="' + company.name + '"data-company-id="' + company.companyIndex + '" data-company-outlet="' + company.outlet +'">' +
                    displayName + //표시할 이름
                    "</li>"
                );
              }
            });
          } else {
            suggestions.append("<li>정보를 찿을 수 없습니다</li>");
          }
        },
        error: function () {
          $("#suggestions").empty();
          $("#suggestions").append("<li>회사 목록을 불러오지 못했습니다</li>");
          alert("정보를 가져오는데 실패했습니다. 다시 시도해 주세요.");
        },
      });
    } else {
      // 검색어가 없을 때 자동완성 목록을 비움
      $("#suggestions").empty();
    }
  });

  //자동완성 항목 클릭 시 검색창에 값 설정 및 변수에 이름과 인덱스 저장
  $("#suggestions").on("click", "li", function () {
    selectedCompanyName = $(this).data("company-name"); //사업장 명을 가져옴
    selectedCompanyId = $(this).data("company-id"); //companyIndex를 가져옴
    outlet = $(this).data("company-outlet");
    console.log(outlet)
    // 검색창에 선택한 이름 넣음
    const companyNameP = selectedCompanyName.split("-");
    if (companyNameP.length >= 3) {
      // 길이가 3인 경우: 첫 번째 + 두 번째 부분을 검색창에 설정
      $("#company-search").val(companyNameP[0] + "-" + companyNameP[1]);
    } else {
      // 길이가 2인 경우 또는 1인 경우: 첫 번째 부분만 검색창에 설정
      $("#company-search").val(companyNameP[0]);
    }

    $("#suggestions").empty(); //선택 후 항목 삭제
    $("#company-search").blur(); // 검색창의 포커스를 해제하여 자동완성 목록이 사라지도록 함

    parseSearchTerm(selectedCompanyName, outlet); // 검색어를 하이픈으로 나누어 처리
    // 하이픈이 있는 경우 배출구 목록을 바로 요청
    if (selectedCompanyQuery) {
      fetchOuletsByQuery(); // 하이픈 앞쪽 부분으로 배출구 목록 요청
    }
  });

  //검색버튼 클릭시  하이픈 여부에 따라 다른 처리
  $("#company-btn").on("click", function (e) {
    e.preventDefault();

    if (selectedCompanyName != null) {
      //로딩 스피너 표시
      $("#loading-spinner").show();

      if (selectedCompanyQuery) {
        //하이픈이 있는 경우: 배출구 쿼리로 배출구 목록 요청
        fetchOuletsByQuery();
        $("#loading-spinner").hide();
      } else if (selectedCompanyId) {
        //하이픈이 없는 경우: 사업장 인덱스 기반 요청
        sessionStorage.removeItem("selectedCompanyQuery");
        sessionStorage.removeItem("selectedOutletName");
        sessionStorage.removeItem("autocompleteOutlets");

        // 데이터 요청 및 플래그 처리
        $.ajax({
          url: `/soms/page5/input?searchClicked=true&`,
          method: "GET",
          success: function () {
            // 페이지 새로 고침
            window.location.href = `/soms/page5/input?searchClicked=true&conpanyIndex=${selectedCompanyId}`;
          },
          error: function () {
            alert("데이터를 불러오는 데 실패했습니다.");
          },
        });
      } else {
        alert("조회하실 사업장을 선택해주세요.");
      }
    } else {
      alert("조회하실 사업장을 입력해주세요.");
      sessionStorage.removeItem("selectedCompanyName");
      sessionStorage.removeItem("selectedCompanyQuery");
      sessionStorage.removeItem("selectedOutletName");
      sessionStorage.removeItem("autocompleteOutlets");

      // 스피너 표시
      $("#loading-spinner").show();

      // 페이지를 새로 고침
      window.location.href = "/soms/page5/input";
    }
  });

  //검색어를 하이픈으로 나누어 변수에 저장
  function parseSearchTerm(searchTerm, outlet) {
    const parts = searchTerm.split("-").map((part) => part.trim()); // 하이픈으로 나누고 양쪽 공백 제거
    if (parts.length === 3) {
      // 길이가 3인 경우: 첫 번째 + 두 번째 부분을 사업장 명으로, 세 번째 부분을 배출구 명으로 사용
      selectedCompanyName = parts[0] + "-" + parts[1]; // 사업장 명: 첫 번째와 두 번째 부분 결합
      selectedCompanyQuery = outlet; // 배출구 명: 세 번째 부분
      sessionStorage.setItem("selectedCompanyName", selectedCompanyName); // 세션 스토리지에 저장
      sessionStorage.setItem("selectedCompanyQuery", outlet);
    } else if (parts.length === 2) {
      // 길이가 2인 경우: 첫 번째 부분을 사업장 명으로, 두 번째 부분을 배출구 명으로 사용
      selectedCompanyName = parts[0];
      selectedCompanyQuery = outlet;
      sessionStorage.setItem("selectedCompanyName", selectedCompanyName);
      sessionStorage.setItem("selectedCompanyQuery", outlet);
    } else {
      // 길이가 1인 경우: 하이픈이 없을 때
      selectedCompanyName = searchTerm;
      selectedCompanyQuery = null;
      sessionStorage.setItem("selectedCompanyName", selectedCompanyName);
      sessionStorage.removeItem("selectedCompanyQuery");
    }
  }

  //선택된 배출구 쿼리로 배출구 목록을 서버에서 가져오는 함수
  function fetchOuletsByQuery() {
    const companyNameParts = selectedCompanyName.split("-");

    if (companyNameParts.length === 3) {
      // 하이픈이 2개인 경우:
      searchPlace = companyNameParts[0] + companyNameParts[1];
    } else {
      // 하이픈이 1개인 경우:
      searchPlace = companyNameParts[0];
    }

    if (!searchPlace) return; // 쿼리가 없으면 요청하지 않음

    $.ajax({
      url: "/soms/page5/input/autocomplete",
      method: "GET",
      data: { name: searchPlace },
      dataType: "json",
      success: function (res) {
        if (res.length > 0) {
          // 데이터가 있을 경우, 세션스토리지에 자동완성 데이터 저장
          sessionStorage.setItem("autocompleteOutlets", JSON.stringify(res));

          $("#loading-spinner").hide(); // 로딩 스피너 숨김

          renderOutlet(res); //배출구 목록 렌터링 하는 함수 호출
        } else {
          $("#outlet-container").html("<tr><td>배출구가 없습니다</td></tr>");
        }
      },
      error: function () {
        $("#outlet-container").html(
          "<tr><td>배출구를 불러오지 못했습니다</td></tr>"
        );
      },
    });
  }

  //배출구 목록을 화면에 렌더링하는 함수
  function renderOutlet(outlets) {
    const outletsContainer = $("#outlet-container");
    outletsContainer.empty();

    outlets.forEach((outlet) => {
      const outletName = outlet.name.split("-");
      let outParts;
      if (outletName.length > 2) {
        outParts = outletName[outletName.length - 1];
      } else {
        outParts = outletName[1] || outlet.name;
      }

      const row = ` <tr>
            <td class="outlet-name" data-company-id="${outlet.companyIndex}" data-outlet-name="${outlet.outlet}">
              ${outParts}
            </td>
          </tr>
          `;
      outletsContainer.append(row);
    });

    //배출구 명을 클릭시 악취물질 정보 요청 하는 함수
    $(".outlet-name").on("click", function () {
      const companyOutlet = $(this).data("company-id");
      const selectedOutletName = $(this).data("outlet-name"); // 클릭한 배출구의 이름 가져오기

      if (companyOutlet) {
        // 선택한 배출구 정보를 세션 스토리지에 저장
        sessionStorage.setItem("selectedOutletName", selectedOutletName);

        // AJAX 요청으로 데이터 가져오기
        $.ajax({
          url: `/soms/page5/input?searchClicked=true&companyIndex=${companyOutlet}`,
          method: "GET",
          success: function (response) {
            // allZero 값을 확인
            const allZero = response.allZero; // 응답 데이터에서 allZero 값을 가져옴
            console.log("allZero 값:", allZero);
            // 페이지 새로 고침
            window.location.href = `/soms/page5/input?searchClicked=true&companyIndex=${companyOutlet}`;
          },
          error: function () {
            alert("데이터를 불러오는 데 실패했습니다.");
          },
        });
      } else {
        alert("배출구를 선택해 주세요.");
      }
    });
  }

  // 농도값이 있을때 행의 색 변환
  $(".factor-table tr").each(function () {
    var inputValue = $(this).find(".factor-input").val(); //input 필드의 값을 가져옴
    if (inputValue && parseFloat(inputValue) > 0) {
      this.style.setProperty("background-color", "#4B89A5", "important");
    } else {
      this.style.setProperty("background-color", "#1a305f");
    }
  });

  // 농도값 있을때 input 배경색 변환
  $(".factor-input").each(function () {
    var inputValue1 = $(this).val(); //input 필드의 값을 가져옴
    if (inputValue1 && parseFloat(inputValue1) > 0) {
      this.style.setProperty("background-color", "#4B89A5", "important");
    } else {
      this.style.setProperty("background-color", "#37538e");
    }
  });

  //페이지 로딩 완료 시 스피너 숨김
  $(window).on("load", function () {
    $("#loading-spinner").hide();
  });

  //초기화 버튼
  $("#reset-btn").on("click", function () {
    sessionStorage.removeItem("selectedCompanyName");
    sessionStorage.removeItem("selectedCompanyQuery");
    sessionStorage.removeItem("selectedOutletName");
    sessionStorage.removeItem("autocompleteOutlets");

    $.ajax({
      url: "/soms/page5/input/reset",
      type: "post",
      contentType: "application/json",
      success: function () {
        // 현재 경로에 따라 리다이렉트 경로를 동적으로 설정
        const currentPath = window.location.pathname;
        let redirectPath = "/soms/page5/input"; // 기본값

        // 현재 경로에 따라 리다이렉트 경로를 설정
        // if문 삭제 시, input 으로 리다이렉트 됨
        if (currentPath.includes("predict")) {
          redirectPath = "/soms/page5/predict";
        } else if (currentPath.includes("result")) {
          redirectPath = "/soms/page5/result";
        } else if (currentPath.includes("setting")) {
          redirectPath = "/soms/page5/setting";
        }

        // 설정한 경로로 리다이렉트
        window.location.href = redirectPath;
      },
      error: function (xhr, status, error) {
        console.error("Error resetting input data:", status, error);
      },
    });
  });
});

function viewTableModal() {
  //이론적 희석배수 조회
  checkNull(); //입력값 유효성검사
  var data = $("#form-inp, #form-inf, #form-add, #form-factor").serialize();

  //ajax 통신
  $.ajax({
    type: "post",
    url: "/soms/page5/input",
    data: data,
    dataType: "text",
    success: function (resList) {
      $("#dilution-table-content").empty();
      $.each(JSON.parse(resList), function (index, item) {
        if (item["name"] === "total")
          $("#dilutionFactorSum").text(item["dilutionFactor"]);
        else {
          var tableTr = "<tr>";
          tableTr += '<td class="factor-name">' + item["name"] + "</td>";
          tableTr +=
            '<td class="value-width-6">' + item["dilutionFactor"] + "</td>";
          tableTr +=
            '<td class="value-width-6">' +
            Math.floor(item["contRate"] * 1000) / 1000 +
            "</td>";
          tableTr += "</tr>";
          $("#dilution-table-content").append(tableTr);
        }
      });
    },
    error: function () {
      alert("이론적 희석배수 조회에 실패하였습니다.");
    },
  });
}

function nextTabModal() {
  // 스피너 표시
  $("#loading-spinner").show();

  viewTableModal();
  if (validateForm()) {
    let url = "/soms/page5/predict";
    location.replace(url);
  }
}

viewTableButton.addEventListener("click", viewTableModal);
nextTabButton.addEventListener("click", nextTabModal);

/* 유효성 검사 */
//빈 입력값 체크
function checkNull() {
  var values = $("#form-inp input[type=number]");
  $.each(values, function (index, target) {
    if (!target.value) target.value = 0.0;
  });

  values = $("#form-inf input[id$='.value']");
  var targetValues = $("#form-inf input[id$='.targetValue']");
  $.each(values, function (index, target) {
    if (!target.value) target.value = 0.0;
  });
  $.each(targetValues, function (index, target) {
    if (!target.value) target.value = 0.0;
  });

  values = $("#form-factor input[type=number]");
  $.each(values, function (index, target) {
    if (!target.value) target.value = 0;
  });
}

function validateForm() {
  var values = $("#form-inf input[id$='.value']");
  var targetValues = $("#form-inf input[id$='.targetValue']");
  var names = $("#form-inf td[class=factor-name]");
  for (var i = 0; i < values.length; i++) {
    if (values[i].value > 0 && targetValues[i].value == 0) {
      alert(names[i].innerText + " 목표값이 0입니다.");
      return false;
    }
  }

  values = $("#form-factor td[name=dilutionFactor]");
  targetValues = $("#form-factor input[name=targetDilutionFactor]");
  if (parseInt(values[0].innerText, 10) > 0 && targetValues[0].value == 0) {
    alert("목표희석배수가 0입니다.");
    return false;
  }
  return true;
}

/* 파일 불러오기 기능 */
function fileLoad(event) {
  /* 파일명 출력 */
  var fileInfo = document.querySelector("input[type=file]").files;
  $("input[name=fileName]").attr("value", fileInfo[0].name);
  /* 엑셀파일 불러오기 */
  excelLoad(event, getExcelData);
}

function excelLoad(event, callback) {
  /* 엑셀 파일 양식 읽기 */
  let input = event.target;
  let reader = new FileReader();
  reader.onload = function () {
    let fileData = reader.result;
    let wb = XLSX.read(fileData, { type: "binary" });
    let sheetNameList = wb.SheetNames; /* 시트 이름 목록 가져오기 */
    for (let i = 0; i < sheetNameList.length; i++) {
      let SheetName = sheetNameList[i]; /* 시트명 */
      let SheetContents = wb.Sheets[SheetName]; /* 시트 내용 */
      callback(i, SheetContents); /* i: 시트 번호에 따라 입력인자 종류 구분 */
    }
  };

  reader.readAsBinaryString(input.files[0]);
}

function getExcelData(i, sheet) {
  /* 엑셀 데이터 읽어와 표에 입력 */
  let jsonType = XLSX.utils.sheet_to_json(sheet, { header: 1 });
  let last_value = null;

  /* 시트별 동작 */
  switch (i) {
    case 0 /* 악취기여물질 */:
      let cntCategory = 0;
      let cntRow = 0;

      /* 엑셀 파일 표 한 줄씩 읽기 */
      jsonType.forEach((r) => {
        last_value = r[0] = r[0] != null ? r[0] : last_value;

        if (typeof r[1] === typeof 0) {
          /* 입력값 숫자 확인 */
          /* 표에 값 입력 */
          let rowID = "inplist" + cntCategory + cntRow + ".value";
          document.getElementById(rowID).value = r[1];

          /* 카테고리에 따른 다음 id값 찾기 */
          /* cntRow의 값이 카테고리의 항목 최대 개수일 때  값 재설정함 */
          switch (++cntRow) {
            case 2:
              if (cntCategory == 0) {
                /* 질소화합물 */
                cntCategory++;
                cntRow = 0;
              }
              break;
            case 4:
              if (cntCategory == 1) {
                /* 황화합물 */
                cntCategory++;
                cntRow = 0;
              }
              break;
            case 11:
              if (cntCategory == 2) {
                /* 휘발성 유기화합물 */
                cntCategory++;
                cntRow = 0;
              }
              break;
            case 5:
              if (cntCategory == 3) {
                /* 알데하이드 */
                cntCategory++;
                cntRow = 0;
              }
              break;
            default:
              break;
          }
        }
      });
      break;
    case 1 /* 방해인자 */:
      let cntInf = 0;

      /* 엑셀 파일 표 한 줄씩 읽기 */
      jsonType.forEach((r) => {
        last_value = r[0] = r[0] != null ? r[0] : last_value;

        if (typeof r[1] === typeof 0) {
          /* 실측값 입력 숫자 확인 */
          let infID = "inflist" + cntInf + ".value";
          document.getElementById(infID).value = r[1];
        }
        if (typeof r[2] === typeof 0) {
          /* 목표값 입력 숫자 확인 */
          let targetID = "inflist" + cntInf + ".targetValue";
          document.getElementById(targetID).value = r[2];
          cntInf++;
        }
      });
      break;
    case 2 /* 추가설비 */:
      let cntCheck = 0;

      /* 엑셀 파일 표 한 줄씩 읽기 */
      jsonType.forEach((r) => {
        last_value = r[0] = r[0] != null ? r[0] : last_value;

        switch (r[1]) {
          case "O":
          case "o":
            let checkID1 = "addlist" + cntCheck++ + ".available1";
            document.getElementById(checkID1).checked = true;
            break;
          case "X":
          case "x":
            let checkID2 = "addlist" + cntCheck++ + ".available1";
            document.getElementById(checkID2).checked = false;
            break;
          default:
            break;
        }
      });
      break;
    case 3 /* 기타: 목표희석배수 및 풍량 */:
      /* 엑셀 파일 표 한 줄씩 읽기 */
      jsonType.forEach((r) => {
        last_value = r[0] = r[0] != null ? r[0] : last_value;

        if (typeof r[1] === typeof 0) {
          switch (r[0]) {
            case "목표희석배수":
            case "목표 희석배수":
              document.getElementById("targetDilutionFactor").value = r[1];
              break;
            case "풍량":
              document.getElementById("wind").value = r[1];
              break;
            case NULL:
            default:
              break;
          }
        }
      });
      break;
    default:
      alert("오류: 파일 형식이 잘못되었습니다.");
      break;
  }
}
