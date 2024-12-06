function navigateTo(url, button) {
    // 모든 버튼에서 'active' 클래스 제거
    const buttons = document.querySelectorAll('.button');
    buttons.forEach(btn => btn.classList.remove('active'));

    // 클릭된 버튼에 'active' 클래스 추가
    button.classList.add('active');

    // 페이지 이동
    window.location.href = url;
}