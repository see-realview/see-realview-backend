function sendEmail() {
    var subject = document.getElementById("subject").value;
    var content = document.getElementById("content").value;
    var report = document.getElementById("report-btn").value;
    report.innerText = "전송중";

    var bugReport = {
        title: subject,
        content: content
    };

    fetch('/api/report/bug', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        body: JSON.stringify(bugReport),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to submit bug report. Please try again.');
            }
            return response.json();
        })
        .then(data => {
            // 성공적으로 요청이 완료되었을 때의 처리
            alert('Bug report submitted successfully!');
            report.innerText = "전송";
        })
        .catch(error => {
            // 요청이 실패했을 때의 처리
            alert(error.message);
        });
}
