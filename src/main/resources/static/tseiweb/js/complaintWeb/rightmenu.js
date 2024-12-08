document.addEventListener("DOMContentLoaded", () => {
    const sendButton = document.getElementById("complaint-send-button");

    sendButton.addEventListener("click", () => {
        // 데이터 수집
        const datetime = document.getElementById("complaint-send-datetime").value;
        const username = document.getElementById("complaint-send-username").value;
        const context = document.getElementById("complaint-send-context").value;
        const latitude = document.getElementById("complaint-send-latitude").value;
        const longitude = document.getElementById("complaint-send-longitude").value;
        const odorType = document.getElementById("complaint-send-odorType").value;
        const odorIntensity = document.getElementById("complaint-send-odorIntensity").value;

        // 유효성 검사
        if (!datetime || !username || !latitude || !longitude || !odorType || !odorIntensity) {
            alert("모든 필드를 올바르게 입력해주세요.");
            return;
        }

        const data = {
            date: datetime,
            userName: username,
            context: context || "신고 내용 없음",
            latitude: parseFloat(latitude),
            longitude: parseFloat(longitude),
            odorName: odorType, // 드롭다운에서 선택한 값
            odorIntensity: odorIntensity,
        };

        // POST 요청
        fetch("/complaint/submit", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
        })
            .then((response) => {
                if (!response.ok) throw new Error("데이터 전송 실패");
                return response.json();
            })
            .then((result) => {
                console.log("민원이 성공적으로 접수되었습니다!");
                console.log(result);
            })
            .catch((error) => {
                console.error("오류:", error);
                console.log("민원 접수 중 문제가 발생했습니다.");
            });
    });
});
