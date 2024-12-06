initClock();
var apiKey = '8f82b2caa0e0e7e57bdb478d65795b8b';  // OpenWeatherMap API 키 입력


//날씨 정보 요청
function fetchWeather() {
  var weatherContainer = $('#weather-container');

  $.ajax({
    url: 'https://api.openweathermap.org/data/2.5/weather?q=Ulsan&appid=' + apiKey,
    method: 'GET',
    dataType: 'json',
    success: function (data) {
      var temperature = Math.round(data.main.temp - 273.15);
      var city = data.name;
      var windspeed = data.wind.speed;
      var windegree = convertWindDirection(data.wind.deg);
      var humidity = data.main.humidity;
      weatherContainer.html('<span style="font-size:1.4rem;"> ' + city + '</span>' + '<p><i class="fas fa-thermometer-half"></i>' + temperature + '°C  / <i class="fas fa-tint"></i> ' + humidity + '%' + '   /  <i class="fas fa-wind"></i>' + windegree + '      ' + windspeed + ' m/s  </p>');
    },
    error: function (error) {
      console.log('날씨 데이터를 가져오는 중 오류가 발생했습니다.');
    }
  });
}

// 초기 실행 및 10초마다 날씨 데이터 업데이트
fetchWeather();
setInterval(fetchWeather, 10000);

//풍향 정보 변환
function convertWindDirection(degrees) {
  val = Math.floor((degrees / 22.5) + .5);
  winds = ["북", "북북동", "북동", "동북동", "동", "동남동", "남동", "남남동", "남", "남남서", "남서", "서남서", "서", "서북서", "북서", "북북서"];
  return winds[(val) % 16];
}
//시간 정보 
function getTime() {
  let today = new Date();
  let year = today.getFullYear();
  let month = today.getMonth() + 1;
  if (month < 10) {
    month = "0" + month;
  }
  let date = today.getDate();
  if (date < 10) {
    date = "0" + date;
  }

  $("#nowDate").replaceWith(
    '<span id="nowDate">' + year + "년" + month + "월" + date + "일" + "</span>"
  );
  const time = new Date();
  const hour = time.getHours();
  const minutes = time.getMinutes();
  const seconds = time.getSeconds();

  $(".h1-clock").replaceWith(
    "<h1 class = h1-clock>" +
    `${hour < 10 ? `0${hour}` : hour}:${minutes < 10 ? `0${minutes}` : minutes
    }:${seconds < 10 ? `0${seconds}` : seconds}` +
    "</h1>"
  );
}
//초기 실행 및 10초마다 시간 업데이트
function initClock() {
  setInterval(getTime, 1000);
}

