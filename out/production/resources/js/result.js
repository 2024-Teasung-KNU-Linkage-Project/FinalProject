//DB에 저장할때 사용할 값 설정

function storeDB() {
  //세션스토리지에서 사업장 명과 공정 명을 가져옵니다
  var companyName = sessionStorage.getItem("selectedCompanyName");
  var outletNamed = sessionStorage.getItem("selectedOutletName");

  //현재 시간을 yyyymmdd-hhmmss형식으로 가져옵니다
  var now = new Date();
  var timestamp =
    now.getFullYear() +
    ("0" + (now.getMonth() + 1)).slice(-2) +
    ("0" + now.getDate()).slice(-2) +
    "-" +
    ("0" + now.getHours()).slice(-2) +
    ("0" + now.getMinutes()).slice(-2) +
    ("0" + now.getSeconds()).slice(-2);

  //저장 이름을 생성합니다
  var text;
  if (companyName) {
    if (outletNamed) {
      text = companyName + "-" + outletNamed + "_" + timestamp;
    } else {
      text = companyName + "_" + timestamp;
    }
  } else {
    text = "result_" + timestamp; //회사명과 배출구명이 없을 떄 기본 이름
  }
  document.getElementById("nameField").value = text;
}

//파일로 내보낼 떄 사용할 값 설정
function storeFile() {
  //현재 시간을 yyyymmdd-hhmmss형식으로 가져옵니다
  var now = new Date();
  var timestamp =
    now.getFullYear() +
    ("0" + (now.getMonth() + 1)).slice(-2) +
    ("0" + now.getDate()).slice(-2) +
    "-" +
    ("0" + now.getHours()).slice(-2) +
    ("0" + now.getMinutes()).slice(-2) +
    ("0" + now.getSeconds()).slice(-2);

  //세션스토리지에서 값 가져오기
  var companyName = sessionStorage.getItem("selectedCompanyName");
  var outletNamed = sessionStorage.getItem("selectedOutletName");

  // 파일 이름 생성
  var fileName;
  if (companyName && outletNamed) {
    fileName = companyName + "-" + outletNamed + "_" + timestamp;
  } else if (companyName) {
    fileName = companyName + "_" + timestamp;
  } else {
    fileName = "result_" + timestamp; // 회사명과 배출구명이 없을 때 기본 이름
  }

  // 폼 데이터 객체 생성
  var formData = new FormData(document.querySelector("form")); // 폼데이터 가져옴
  formData.append("fileName", fileName); // 파일 이름 추가
  formData.append("storeIn", "file"); // storeIn 값을 추가합니다.

  // AJAX 요청을 통해 서버에 파일 요청
  $.ajax({
    url: "/soms/page5/result",
    method: "POST",
    data: formData,
    processData: false, // jQuery가 데이터를 처리하지 않도록 설정
    contentType: false, // jQuery가 Content-Type을 설정하지 않도록 설정
    xhrFields: {
      responseType: "blob", // 서버 응답을 텍스트로 처리
    },
    success: function (res, status, xhr) {
      // Content-Disposition 헤더에서 파일 이름 추출
      var contentDisposition = xhr.getResponseHeader("Content-Disposition");
      var extractedFileName = fileName + ".csv"; // 기본 파일 이름 설정

      // 파일 이름 추출
      if (
        contentDisposition &&
        contentDisposition.indexOf("filename=") !== -1
      ) {
        extractedFileName = contentDisposition
          .split("filename=")[1]
          .trim()
          .replace(/["']/g, ""); // 파일 이름 정리
      }

      // 파일 데이터를 Blob으로 변환
      var file = new Blob([res], { type: "text/csv" });

      // 파일 다운로드를 위한 링크 생성
      var link = document.createElement("a");
      link.href = URL.createObjectURL(file);
      link.download = extractedFileName || "result_" + timestamp + ".csv";
      link.style.display = "none";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link); // 링크 제거
    },
    error: function () {
      alert("파일 다운로드에 실패했습니다");
    },
  });
}
