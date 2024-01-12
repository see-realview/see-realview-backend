let loading = false;
let timer;


document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById('search-input');
    const searchContainer = document.querySelector('.search-container');

    searchInput.addEventListener('keyup', function (event) {
        if (event.key === 'Enter') {
            const keyword = searchInput.value;
            if (keyword !== "" && !loading) {
                searchContainer.innerHTML = '';
                loading = true;
                searchApiRequest(keyword, 1);
            }
            event.target.blur();
        }
    });
});

window.addEventListener('scroll', function () {
    const scrollHeight = document.documentElement.scrollHeight;
    const scrollTop = document.documentElement.scrollTop;
    const clientHeight = document.documentElement.clientHeight;

    if (scrollHeight - scrollTop - 1000 <= clientHeight && !loading) {
        const keyword = document.getElementById('search-input').value;
        const cursor = localStorage.getItem("cursor");

        if (!timer) {
            timer = setTimeout(() => {
                searchApiRequest(keyword, cursor);
                timer = null;
            }, 200);
        }
        loading = true;
    }
});


function searchApiRequest(keyword, cursor) {
    const apiUrl = `/api/search?keyword=${keyword}&cursor=${cursor}`;

    fetch(apiUrl, {
        method: 'GET',
    })
        .then(response => response.json())
        .then(data => {
            updateSearchResults(data);
            loading = false;
        })
        .catch(error => {
            console.error('Error during API request:', error);
            loading = false;
        });
}

function updateSearchResults(responseData) {
    const searchContainer = document.querySelector('.search-container');

    if (responseData.success && responseData.contents.data) {
        localStorage.setItem("cursor", responseData.contents.cursor);
        responseData.contents.data.forEach(function (item) {
            const listItem = document.createElement('div');
            listItem.classList.add('search-result');

            var link = document.createElement('a');
            link.href = item.link;
            link.target = '_blank';
            link.innerHTML = item.title;

            const description = document.createElement('p');
            description.innerHTML = item.description;

            const advertisementElement = document.createElement('span');
            advertisementElement.classList.add('advertisement');
            advertisementElement.textContent = item.advertisement ? '광고' : '내돈내산';

            const dateElement = document.createElement('span');
            dateElement.classList.add('date');
            dateElement.textContent = item.bloggerName + " | " + item.date;

            listItem.appendChild(advertisementElement);
            listItem.appendChild(link);
            listItem.appendChild(description);
            listItem.appendChild(dateElement);
            searchContainer.appendChild(listItem);
        });
    } else {
        console.error('Error in response:', responseData.error);
    }
}
