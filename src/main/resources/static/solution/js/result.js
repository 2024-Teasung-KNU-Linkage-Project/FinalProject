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
let isRequestInProgress = false; // 요청 진행 중 여부를 나타내는 플래그

function storeFile() {
  if (isRequestInProgress) return; // 요청이 진행 중이면 종료
  isRequestInProgress = true; // 요청 시작

  // 현재 시간을 yyyymmdd-hhmmss 형식으로 가져옵니다.
  var now = new Date();
  var timestamp =
      now.getFullYear() +
      ("0" + (now.getMonth() + 1)).slice(-2) +
      ("0" + now.getDate()).slice(-2) +
      "-" +
      ("0" + now.getHours()).slice(-2) +
      ("0" + now.getMinutes()).slice(-2) +
      ("0" + now.getSeconds()).slice(-2);

  // 세션스토리지에서 값 가져오기
  var companyName = sessionStorage.getItem("selectedCompanyName");
  var outletNamed = sessionStorage.getItem("selectedOutletName");

  // 파일 이름 생성
  var fileName;
  if (companyName && outletNamed) {
    fileName = companyName + "-" + outletNamed + "_" + timestamp;
  } else if (companyName) {
    fileName = companyName + "_" + timestamp;
  } else {
    fileName = "result_" + timestamp; // 기본 이름
  }

  // 파일 이름이 없으면 종료
  if (!fileName) {
    alert("파일 이름이 없습니다.");
    isRequestInProgress = false;
    return;
  }

  // 파일 이름을 URL 인코딩
  fileName = encodeURIComponent(fileName);

  // 폼 데이터 객체 생성
  var formData = new FormData(document.querySelector("form"));
  formData.append("fileName", fileName);
  formData.append("storeIn", "file");

  // AJAX 요청을 통해 서버에 파일 요청
  $.ajax({
    url: "/soms/page5/result",
    method: "POST",
    data: formData,
    processData: false,
    contentType: false,
    xhrFields: {
      responseType: "blob", // Blob 형태로 응답 처리
    },
    success: function (res, status, xhr) {
      // 응답이 빈 경우 처리
      if (res.size === 0) {
        alert("빈 파일을 생성할 수 없습니다.");
        isRequestInProgress = false;
        return;
      }

      // Content-Disposition 헤더에서 파일 이름 추출
      var contentDisposition = xhr.getResponseHeader("Content-Disposition");
      var extractedFileName = fileName + ".csv"; // 기본 파일 이름 설정

      // 파일 이름 추출 (URL 디코딩)
      if (contentDisposition && contentDisposition.indexOf("filename=") !== -1) {
        extractedFileName = decodeURIComponent(
            contentDisposition.split("filename=")[1].trim().replace(/["']/g, "")
        ); // URL 디코딩
      }

      // 파일 이름이 비어 있는 경우 처리
      if (!extractedFileName) {
        alert("파일 이름이 비어 있습니다.");
        isRequestInProgress = false;
        return;
      }

      // 파일 데이터를 Blob으로 변환
      var file = new Blob([res], { type: "text/csv" });

      // 다운로드 링크 생성
      var link = document.createElement("a");
      var url = URL.createObjectURL(file); // Blob URL 생성
      link.href = url;
      link.download = extractedFileName; // 올바른 파일 이름 설정
      link.style.display = "none";

      // 링크를 문서에 추가하고 클릭하여 다운로드 시작
      document.body.appendChild(link);
      link.click();

      // 링크 제거 및 Blob URL 해제
      document.body.removeChild(link);
      URL.revokeObjectURL(url); // 메모리 해제를 위해 Blob URL 해제

      isRequestInProgress = false; // 요청 완료 후 플래그 해제
    },
    error: function () {
      alert("파일 다운로드에 실패했습니다");
      isRequestInProgress = false; // 오류 발생 시에도 플래그 해제
    },
  });
}