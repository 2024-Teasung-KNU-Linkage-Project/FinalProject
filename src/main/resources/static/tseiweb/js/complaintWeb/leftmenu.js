var currentComplaint;


//민원 추적 테이블 데이터 초기화
function clearTableText() {
  document.getElementById("placeName").innerHTML = "-"
  document.getElementById("leftLatLng").innerText = "-";
  document.getElementById("leftTime").innerText = "-";

  document.getElementById("odorkind").innerText = "-";
  document.getElementById("odorstrength").innerText = "-";
  document.getElementById("winddir_sp").innerText = "-";

  fillMatchingPlaceTable([])
  fillInRadiusTable([])


  var angleButtons = document.querySelectorAll('input[name="radius2"]');
  var radiusButtons = document.querySelectorAll('input[name="radius"]');

  angleButtons.forEach(function (button) {
    button.checked = false;
  });

  radiusButtons.forEach(function (button) {
    button.checked = false;
  });
}

//풍향 데이터 가시화를 위한 함수
function getWindDirection8(angle) {
  val = Math.floor((angle / 22.5) + .5);
  winds = ["북", "북북동", "북동", "동북동", "동", "동남동", "남동", "남남동", "남", "남남서", "남서", "서남서", "서", "서북서", "북서", "북북서"];
  return winds[(val) % 16];
}

//왼쪽 메뉴 민원 추적 테이블에 대한 정보를 채우는 함수
function fillCoordinateTable(placeName, latitude, longitude, date) {
  document.getElementById("placeName").innerHTML =
    placeName;


  let formattedLatLng = latitude + " / " + longitude;
  if (formattedLatLng.length > 20) { // 특정 길이를 기준으로 줄바꿈
    formattedLatLng = latitude + "<br/> / " + longitude;
  }
  document.getElementById("leftLatLng").innerHTML = formattedLatLng

  document.getElementById("leftTime").innerHTML = date;
}

//민원 추적 테이블 악취 종류 및 악취 세기 정보를 채우는 함수
function fillOdorTable(kind, strength) {
  document.getElementById("odorkind").innerHTML = kind;
  document.getElementById("odorstrength").innerHTML = strength;
}
//민원 추적 테이블 풍향 및 풍속 정보를 채우는 함수
function fillWindTable(direction, speed) {
  document.getElementById("winddir_sp").innerHTML = getWindDirection8(direction) + "/" + speed;
}


//클릭된 민원 객체의 radius와 anlge을 통해 기본 반경, 너비 조정 인터페이스를 채움
function setRadioButtons(complaint) {
  currentComplaint = complaint;

  var angle = convertToButtonValue("angle", currentComplaint.angle);
  var radius = convertToButtonValue("radius", currentComplaint.radius);
  var angleButtons = document.querySelectorAll('input[name="radius2"');
  var radiusButtons = document.querySelectorAll('input[name="radius"');

  angleButtons.forEach(function (button) {
    if (parseInt(button.value) === angle) {
      button.checked = true;
    } else {
      button.checked = false;
    }
  });

  radiusButtons.forEach(function (button) {
    if (parseInt(button.value) === radius) {
      button.checked = true;
    } else {
      button.checked = false;
    }
  });
}
//반경을 반경시 생기는 이벤트
function changeRadius(radius) {
  clearTableText()
  currentComplaint.radius = radius;

  currentComplaint.checkmarker_event_start();
}
//너비를 변경할 시 생기는 이벤트
function changeAngle(angle) {
  clearTableText()
  currentComplaint.angle = angle;

  currentComplaint.checkmarker_event_start();
}


//반경 및 부채꼴 너비 값을 범주형 데이터로 변환
function convertToButtonValue(type, value) {
  if (type === "angle") {
    // 반경을를 버튼의 값으로 변환합니다.
    if (value >= 0 && value <= 30) {
      return 30;
    } else if (value > 30 && value <= 60) {
      return 60;
    } else if (value > 60 && value <= 90) {
      return 90;
    } else if (value > 90 && value <= 120) {
      return 120;
    } else {
      return 120; // 해당하는 범위가 없을 경우 null 반환
    }
  } else if (type === "radius") {
    // 반경을 버튼의 값으로 변환합니다.
    // 예를 들어, 반경이 0 ~ 5000 사이에 있을 때, 1000, 2000, 3000, 4000, 5000 값으로 변환합니다.
    if (value >= 0 && value <= 500) {
      return 500;
    } else if (value > 500 && value <= 1000) {
      return 1000;
    } else if (value > 1000 && value <= 2000) {
      return 2000;
    } else if (value > 2000 && value <= 3000) {
      return 3000;
    } else if (value > 3000 && value <= 4000) {
      return 4000;
    } else if (value > 4000 && value <= 5000) {
      return 5000;
    } else {
      return null; // 해당하는 범위가 없을 경우 null 반환
    }
  } else {
    return null; // 유효하지 않은 type일 경우 null 반환
  }
}

//반경 내 사업장 데이터 채우기
function fillInRadiusTable(objects) {
  //테이블 호출
  var table = document.querySelector("#odor_correct_table");
  //기존 thead 제거
  var exisitingThead = table.querySelector('thead');
  if (exisitingThead) {
    table.removeChild(exisitingThead);
  }

  //새로운 thead 생성
  var thead = document.createElement('thead');
  //첫 번째 tr요소와 td요소 생성
  var tr1 = document.createElement('tr');
  var td1 = document.createElement('td');
  td1.setAttribute('style', 'width: 10%; padding-top: 4px; padding-bottom: 4px; border: 1px solid white; background-color: #1f2f63;');
  td1.setAttribute('rowspan', Math.max(objects.length, 3) + 1);
  td1.innerText = '사업장 명';
  tr1.appendChild(td1);
  thead.appendChild(tr1);

  // 나머지 tr과 td 요소를 만듭니다.
  for (var i = 0; i < Math.max(objects.length, 3); i++) {
    var tr = document.createElement('tr');
    var td = document.createElement('td');
    td.className = 'inRadius';
    td.setAttribute('style', 'width: 25%; border: 1px solid white; padding: 3px 0px;');
    td.innerText = objects[i] ? objects[i].title : '.'; // 객체가 존재하면 title, 아니면 '.'을 설정합니다.
    tr.appendChild(td);
    thead.appendChild(tr);
  }

  // 테이블에 thead를 추가합니다.
  table.appendChild(thead);

}

//부채꼴 내 악취 이름 일치 사업장 데이터 채우기
function fillMatchingPlaceTable(objects) {
  var table = document.querySelector("#wind_odor_correct_table");

  // 기존 thead를 제거합니다.
  var existingThead = table.querySelector('thead');
  if (existingThead) {
    table.removeChild(existingThead);
  }

  // 새로운 thead 요소를 만듭니다.
  var thead = document.createElement('thead');

  // 첫 번째 tr 요소와 td 요소를 만듭니다.
  var tr1 = document.createElement('tr');
  var td1 = document.createElement('td');
  td1.setAttribute('style', 'width: 10%; padding-top: 4px; padding-bottom: 4px; border: 1px solid white; background-color: #1f2f63;');
  td1.setAttribute('rowspan', Math.max(objects.length, 3) + 1);
  td1.innerText = '사업장 명';
  tr1.appendChild(td1);
  thead.appendChild(tr1);

  // 나머지 tr과 td 요소를 만듭니다.
  for (var i = 0; i < Math.max(objects.length, 3); i++) {
    var tr = document.createElement('tr');
    var td = document.createElement('td');
    td.className = 'matching';
    td.setAttribute('style', 'width: 25%; border: 1px solid white; padding: 3px 0px;');
    td.innerText = objects[i] ? objects[i].title : '.'; // 객체가 존재하면 title, 아니면 '.'을 설정합니다.
    tr.appendChild(td);
    thead.appendChild(tr);
  }

  // 테이블에 thead를 추가합니다.
  table.appendChild(thead);

}


document.addEventListener("DOMContentLoaded", function () {

  //기본 반경 클릭시 이벤트
  document.querySelectorAll('input[name="radius"]').forEach((radio) => {
    radio.addEventListener("change", (event) => {
      console.log("반경 변경");
      if (currentComplaint) {
        changeRadius(event.target.value);
      }
    });
  });

  // 너비 변경시 이벤트
  document.querySelectorAll('input[name="radius2"]').forEach((radio) => {
    radio.addEventListener("change", (event) => {
      console.log("너비 변경");
      if (currentComplaint) {
        changeAngle(event.target.value);
      }
    });
  });
});
