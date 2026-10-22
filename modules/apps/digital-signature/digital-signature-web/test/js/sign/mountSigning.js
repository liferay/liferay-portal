/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitFor} from '@testing-library/react';

import mountSigning from '../../../src/main/resources/META-INF/resources/js/sign/mountSigning';

const COMPLETE_DS_RECIPIENT_VIEW_URL =
	'http://localhost/web/guest/sign?p_p_lifecycle=1&p_auth=token';

describe('mountSigning', () => {
	let pageHideListeners;
	let sessionEndListener;
	let signing;

	afterEach(() => {
		pageHideListeners.forEach((listener) =>
			window.removeEventListener('pagehide', listener)
		);

		jest.restoreAllMocks();

		delete Liferay.Util.Window;
		delete navigator.sendBeacon;
		delete window.DocuSign;
		delete window.opener;

		document.head.innerHTML = '';
	});

	beforeEach(() => {
		jest.clearAllMocks();

		pageHideListeners = [];
		sessionEndListener = null;
		signing = {
			mount: jest.fn(),
			on: jest.fn((name, listener) => {
				if (name === 'sessionEnd') {
					sessionEndListener = listener;
				}
			}),
		};

		const addEventListener = window.addEventListener.bind(window);

		jest.spyOn(window, 'addEventListener').mockImplementation(
			(name, listener, options) => {
				if (name === 'pagehide') {
					pageHideListeners.push(listener);
				}

				addEventListener(name, listener, options);
			}
		);

		Liferay.Util.Window = {getById: jest.fn()};

		Object.defineProperty(navigator, 'sendBeacon', {
			configurable: true,
			value: jest.fn(),
		});

		window.DocuSign = {
			loadDocuSign: jest.fn(() =>
				Promise.resolve({signing: jest.fn(() => signing)})
			),
		};
		window.opener = {Liferay: {fire: jest.fn()}};
	});

	function getBeforeNavigateListener() {
		const [, listener] = Liferay.on.mock.calls.find(
			([name]) => name === 'beforeNavigate'
		);

		return listener;
	}

	async function mount() {
		mountSigning({
			backURL: 'http://localhost/web/guest/home',
			completeDSRecipientViewURL: COMPLETE_DS_RECIPIENT_VIEW_URL,
			containerId: 'signContainer',
			integrationKey: 'integrationKey',
			jsURL: 'http://localhost/bundle.js',
			recipientViewURL: 'http://localhost/signing',
		});

		document.head.querySelector('script').onload();

		await waitFor(() => expect(signing.mount).toHaveBeenCalled());
	}

	it('closes without completing the recipient view when the session ends without an outcome', async () => {
		await mount();

		sessionEndListener({sessionEndType: 'cancel'});

		expect(window.opener.Liferay.fire).toHaveBeenCalledWith('closeModal', {
			redirect: 'http://localhost/web/guest/home',
		});
		expect(fetch).not.toHaveBeenCalled();

		window.dispatchEvent(new Event('pagehide'));

		expect(navigator.sendBeacon).not.toHaveBeenCalled();
	});

	it('completes the recipient view and stops listening when the session ends', async () => {
		fetch.mockResponseOnce('');

		await mount();

		const beforeNavigateListener = getBeforeNavigateListener();

		sessionEndListener({sessionEndType: 'signing_complete'});

		await waitFor(() =>
			expect(window.opener.Liferay.fire).toHaveBeenCalledWith(
				'closeModal',
				{redirect: 'http://localhost/web/guest/home'}
			)
		);

		expect(Liferay.detach).toHaveBeenCalledWith(
			'beforeNavigate',
			beforeNavigateListener
		);
		expect(fetch.mock.calls[0][0]).toBe(COMPLETE_DS_RECIPIENT_VIEW_URL);

		window.dispatchEvent(new Event('pagehide'));

		expect(navigator.sendBeacon).not.toHaveBeenCalled();
	});

	it('completes the recipient view when the recipient declines', async () => {
		fetch.mockResponseOnce('');

		await mount();

		sessionEndListener({sessionEndType: 'decline'});

		await waitFor(() =>
			expect(window.opener.Liferay.fire).toHaveBeenCalledWith(
				'closeModal',
				{redirect: 'http://localhost/web/guest/home'}
			)
		);

		expect(fetch.mock.calls[0][0]).toBe(COMPLETE_DS_RECIPIENT_VIEW_URL);
	});

	it('completes the recipient view when the recipient finishes viewing a finished envelope', async () => {
		fetch.mockResponseOnce('');

		await mount();

		sessionEndListener({sessionEndType: 'viewing_complete'});

		await waitFor(() =>
			expect(window.opener.Liferay.fire).toHaveBeenCalledWith(
				'closeModal',
				{redirect: 'http://localhost/web/guest/home'}
			)
		);

		expect(fetch.mock.calls[0][0]).toBe(COMPLETE_DS_RECIPIENT_VIEW_URL);
	});

	it('goes to the complete URL or the back URL when there is no opener', async () => {
		const location = window.location;

		Object.defineProperty(window, 'location', {
			configurable: true,
			value: {href: ''},
		});

		delete window.opener;

		try {
			await mount();

			sessionEndListener({sessionEndType: 'signing_complete'});

			expect(window.location.href).toBe(COMPLETE_DS_RECIPIENT_VIEW_URL);

			sessionEndListener({sessionEndType: 'cancel'});

			expect(window.location.href).toBe(
				'http://localhost/web/guest/home'
			);
			expect(fetch).not.toHaveBeenCalled();
		}
		finally {
			Object.defineProperty(window, 'location', {
				configurable: true,
				value: location,
			});
		}
	});

	it('loads the bundle and mounts the default view in the container', async () => {
		await mount();

		expect(document.head.querySelector('script').src).toBe(
			'http://localhost/bundle.js'
		);
		expect(window.DocuSign.loadDocuSign).toHaveBeenCalledWith(
			'integrationKey'
		);

		const docuSign =
			await window.DocuSign.loadDocuSign.mock.results[0].value;

		expect(docuSign.signing).toHaveBeenCalledWith({
			displayFormat: 'default',
			url: 'http://localhost/signing',
		});
		expect(signing.mount).toHaveBeenCalledWith('#signContainer');
	});

	it('sends a beacon to complete the recipient view when the recipient leaves the page early', async () => {
		await mount();

		window.dispatchEvent(new Event('pagehide'));

		expect(navigator.sendBeacon).toHaveBeenCalledWith(
			COMPLETE_DS_RECIPIENT_VIEW_URL
		);
	});

	it('sends a beacon to complete the recipient view when the recipient navigates away early', async () => {
		await mount();

		const beforeNavigateListener = getBeforeNavigateListener();

		beforeNavigateListener();

		expect(Liferay.detach).toHaveBeenCalledWith(
			'beforeNavigate',
			beforeNavigateListener
		);
		expect(navigator.sendBeacon).toHaveBeenCalledWith(
			COMPLETE_DS_RECIPIENT_VIEW_URL
		);
	});
});
