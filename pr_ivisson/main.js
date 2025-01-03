
function ActionButtonGETAll() {
    const myHeaders = new Headers();
    myHeaders.append("Authorization", "Basic " + btoa("test@liferay.com:learn"));
    myHeaders.append("Cookie", "JSESSIONID=CA8922F96BC9892DBCD4C2D5ED307F51");

    const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
    };

    fetch("http://localhost:8080/o/headless-delivery/v1.0/sites/20117/blog-postings", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result);
        })
        .catch((error) => console.error('Error:', error));
}

function ActionButtonGETId() {
    const myHeaders = new Headers();
    myHeaders.append("Authorization", "Basic " + btoa("test@liferay.com:learn"));
    myHeaders.append("Cookie", "JSESSIONID=89A893BE9DA71AE5E5519A0206D23136");

    const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
    };

    var post = document.getElementById('postId').value;

    fetch(`http://localhost:8080/o/headless-delivery/v1.0/sites/20117/blog-postings/by-external-reference-code/${post}`, requestOptions)
        .then((response) => response.text())
        .then((result) => console.log(result))
        .catch((error) => console.error(error));
}

function ActionButtonPOST() {
    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", "Basic " + btoa("test@liferay.com:learn"));
    myHeaders.append("Cookie", "JSESSIONID=CA8922F96BC9892DBCD4C2D5ED307F51");

    var headline = document.getElementById('headline').value;
    var articleBody = document.getElementById('articleBody').value;

    const raw = JSON.stringify({
        "headline": `${headline}`,
        "articleBody": `${articleBody}`
    });

    const requestOptions = {
        method: "POST",
        headers: myHeaders,
        body: raw,
        redirect: "follow"
    };

    fetch("http://localhost:8080/o/headless-delivery/v1.0/sites/20117/blog-postings", requestOptions)
        .then((response) => response.text())
        .then((result) => console.log(result))
        .catch((error) => console.error(error));
}

function ActionButtonDELETE() {
    const myHeaders = new Headers();
    myHeaders.append("Authorization", "Basic " + btoa("test@liferay.com:learn"));
    myHeaders.append("Cookie", "JSESSIONID=CA8922F96BC9892DBCD4C2D5ED307F51");

    const requestOptions = {
        method: "DELETE",
        headers: myHeaders,
        redirect: "follow"
    };

    var externalReference = document.getElementById('externalReference').value;

    fetch(`http://localhost:8080/o/headless-delivery/v1.0/sites/20117/blog-postings/by-external-reference-code/${externalReference}`, requestOptions)
        .then((response) => response.text())
        .then((result) => console.log(result))
        .catch((error) => console.error(error));
}

function ActionButtonPUT() {
    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", "Basic " + btoa("test@liferay.com:learn"));
    myHeaders.append("Cookie", "JSESSIONID=CA8922F96BC9892DBCD4C2D5ED307F51");


    var headline = document.getElementById('headlineForPUT').value;
    var articleBody = document.getElementById('articleBodyForPUT').value;

    const raw = JSON.stringify({
        "headline": `${headline}`,
        "articleBody": `${articleBody}`
    });

    const requestOptions = {
        method: "PUT",
        headers: myHeaders,
        body: raw,
        redirect: "follow"
    };

    var externalReference = document.getElementById('externalReferenceForPUT');

    fetch(`http://localhost:8080/o/headless-delivery/v1.0/sites/20117/blog-postings/by-external-reference-code/${externalReference}`, requestOptions)
        .then((response) => response.text())
        .then((result) => console.log(result))
        .catch((error) => console.error(error));
}