document.addEventListener("DOMContentLoaded", function () {
    var searchInput = document.getElementById('search-input');

    searchInput.addEventListener('keyup', function (event) {
        if (event.key === 'Enter') {
            var keyword = searchInput.value;
            searchApiRequest(keyword, 1);
            event.target.blur();
        }
    });
});

function searchApiRequest(keyword, cursor) {
    const apiUrl = 'http://localhost:8080/api/search?keyword=' + keyword + "&cursor=" + cursor;

    fetch(apiUrl, {
        method: 'GET',
    })
        .then(response => response.json())
        .then(data => {
            updateSearchResults(data);
        })
        .catch(error => {
            console.error('Error during API request:', error);
        });
}

function updateSearchResults(responseData) {
    var searchContainer = document.querySelector('.search-container');

    searchContainer.innerHTML = '';

    if (responseData.success && responseData.contents.data) {
        responseData.contents.data.forEach(function (item) {
            var listItem = document.createElement('div');
            listItem.classList.add('search-result');

            var link = document.createElement('a');
            link.href = item.url;
            link.textContent = item.title;

            var description = document.createElement('p');
            description.textContent = item.description;

            var advertisementElement = document.createElement('span');
            advertisementElement.classList.add('advertisement');
            advertisementElement.textContent = item.advertisement ? '광고' : '내돈내산';

            var dateElement = document.createElement('span');
            dateElement.classList.add('date');
            dateElement.textContent = 'Date: ' + item.date;

            var bloggerNameElement = document.createElement('span');
            bloggerNameElement.classList.add('blogger-name');
            bloggerNameElement.textContent = 'Blogger: ' + item.bloggerName;

            listItem.appendChild(advertisementElement);
            listItem.appendChild(link);
            listItem.appendChild(description);
            listItem.appendChild(bloggerNameElement);
            listItem.appendChild(dateElement);
            searchContainer.appendChild(listItem);
        });
    } else {
        console.error('Error in response:', responseData.error);
    }
}
