import { worker } from './mocks/browser';

let mswServiceWorkerRegistration = null;

export function setupMocks() {

    worker.start({
        serviceWorker: {
            url: '/o/exportimport-web/mockServiceWorker.js',
            options: {
                scope: '/group/'
            },
        },
        onUnhandledRequest: 'bypass'
    }).then((registration) => {
        mswServiceWorkerRegistration = registration;

        const unregisterMswServiceWorker = async () => {
            if (mswServiceWorkerRegistration && typeof mswServiceWorkerRegistration.unregister === 'function') {
                await mswServiceWorkerRegistration.unregister();
            }

            mswServiceWorkerRegistration = null;
        };

        Liferay.on('destroyPortlet', unregisterMswServiceWorker);
    }).catch((error) => {
        console.error('Error starting the service worker:', error);
    });
    
}

setupMocks();
