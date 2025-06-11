if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('/mockServiceWorker.js')
            .then(registration => {
                console.log('ServiceWorker registration successful with scope: ', registration.scope);
            })
            .catch(error => {
                console.error('ServiceWorker registration failed: ', error);
            });
    });
} else {
    console.error('Service workers are not supported in this browser');
} 