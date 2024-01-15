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

            const imagesContainer = document.createElement('div');
            imagesContainer.classList.add('images-container');

            // const leftArrow = document.createElement('button');
            // leftArrow.innerHTML = '&#9665;';
            // leftArrow.classList.add('arrow-button', 'left-arrow');
            //
            // const rightArrow = document.createElement('button');
            // rightArrow.innerHTML = '&#9655;';
            // rightArrow.classList.add('arrow-button', 'right-arrow');
            //
            // leftArrow.addEventListener('click', function () {
            //     imagesContainer.scrollBy({
            //         left: -500,
            //         behavior: 'smooth'
            //     });
            // });
            //
            // rightArrow.addEventListener('click', function () {
            //     imagesContainer.scrollBy({
            //         left: 500,
            //         behavior: 'smooth'
            //     });
            // });
            //
            // listItem.appendChild(leftArrow);
            // listItem.appendChild(rightArrow);

            if (item.images != null) {
                item.images.forEach(function (imageUrl) {
                    const imgElement = document.createElement('img');
                    imgElement.src = imageUrl;
                    imgElement.setAttribute('referrerpolicy', 'no-referrer');
                    imgElement.setAttribute("loading", "lazy");
                    imagesContainer.appendChild(imgElement);
                });

                listItem.appendChild(imagesContainer);
                searchContainer.appendChild(listItem);
            }

            const postContent = document.createElement('a');
            postContent.href = item.link;
            postContent.target = '_blank';
            postContent.classList.add('post-content');

            var link = document.createElement('div');
            link.innerHTML = item.title;
            link.classList.add('post-title');

            const description = document.createElement('p');
            description.innerHTML = item.description;

            const advertisementElement = document.createElement('span');
            advertisementElement.classList.add('advertisement');
            advertisementElement.textContent = item.advertisement ? '광고' : '내돈내산';

            const dateElement = document.createElement('span');
            dateElement.classList.add('date');
            dateElement.textContent = item.bloggerName + " | " + item.date;

            postContent.appendChild(advertisementElement);
            postContent.appendChild(link);
            postContent.appendChild(description);
            postContent.appendChild(dateElement);
            listItem.appendChild(postContent);
            searchContainer.appendChild(listItem);
        });
    } else {
        console.error('Error in response:', responseData.error);
    }
}
