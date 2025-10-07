/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	cleanup,
	fireEvent,
	render,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import fetch from 'jest-fetch-mock';
import React from 'react';
import {act} from 'react-dom/test-utils';

import {MarketplaceSettings} from '../../src/main/resources/META-INF/resources/js';

import '@testing-library/jest-dom/extend-expect';

const marketplaceSettingsProps = {
	baseResourceURL: 'http://localhost:8080?p_p_id=marketplace_settings',
	clientId: 'marketplace-client-id',
	portletNamespace: '_marketplacePortletNamespace_',
	redirect: '/web/marketplace/authorize',
	url: 'https://marketplace.liferay.com',
};

const messageEventInvalid = new MessageEvent('message', {
	data: {
		code: 'marketplace-uuid',
	},
	origin: 'http://localhost:8080',
	source: window,
});

const messageEventInvalidWithoutData = new MessageEvent('message', {
	origin: 'https://marketplace.liferay.com',
	source: window,
});

const messageEventValid = new MessageEvent('message', {
	data: {
		code: 'marketplace-uuid',
		serviceURL: 'https://springboot.marketplace.liferay.com',
		settings: {},
	},
	origin: 'https://marketplace.liferay.com',
	source: window,
});

const MarketplaceSettingsWrapper: React.FC<{
	children?: React.ReactNode | undefined;
}> = () => {
	return (
		<MarketplaceSettings
			{...marketplaceSettingsProps}
			learnResources={[]}
		/>
	);
};

describe('MarketplaceConnect', () => {
	let closePopUpMock: jest.Mock;
	let windowOpenSpy: jest.SpyInstance;

	beforeEach(() => {
		closePopUpMock = jest.fn();

		windowOpenSpy = jest.spyOn(window, 'open').mockImplementation(
			() =>
				({
					close: closePopUpMock,
				}) as unknown as Window
		);
		jest.useFakeTimers();
	});

	afterEach(() => {
		cleanup();

		jest.clearAllTimers();
		jest.restoreAllMocks();
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	it('renders unauthorized', async () => {
		fetch.mockResponseOnce(JSON.stringify({authorized: false, data: null}));

		const {container, queryAllByRole, queryByText} = render(
			<MarketplaceSettingsWrapper />
		);

		expect(fetch.mock.calls.length).toEqual(1);

		await waitForElementToBeRemoved(() =>
			container.querySelector('.loading-animation-primary')
		);

		const [connectListItem, statusListItem] = queryAllByRole('listitem');

		expect(queryByText('connect-to-the-marketplace')).toBeTruthy();
		expect(connectListItem).toHaveClass('active');
		expect(statusListItem).not.toHaveClass('active');
	});

	it('renders unauthorized and do the connection flow', async () => {
		fetch
			.mockResponseOnce(JSON.stringify({authorized: false, data: null}))
			.mockResponseOnce(JSON.stringify({success: true}));

		const {container, queryAllByRole, queryByText} = render(
			<MarketplaceSettingsWrapper />
		);

		expect(fetch.mock.calls.length).toEqual(1);

		await waitForElementToBeRemoved(() =>
			container.querySelector('.loading-animation-primary')
		);

		const [connectListItem, statusListItem] = queryAllByRole('listitem');

		expect(connectListItem).toHaveClass('active');
		expect(queryByText('connect-to-the-marketplace')).toBeInTheDocument();

		const connectButton = container.querySelector(
			'.btn.btn-primary'
		) as HTMLButtonElement;

		expect(connectButton).toBeInTheDocument();
		expect(connectButton).toHaveTextContent('connect');

		fireEvent.click(connectButton);

		expect(windowOpenSpy).toBeCalledTimes(1);

		const {searchParams} = new URL(windowOpenSpy.mock.calls[0][0]);

		expect(searchParams.get('client_id')).toBe(
			marketplaceSettingsProps.clientId
		);
		expect(searchParams.get('redirect_uri')).toBe(
			`${marketplaceSettingsProps.url}${marketplaceSettingsProps.redirect}`
		);
		expect(JSON.parse(searchParams.get('state') || '')).toMatchObject({
			origin: 'http://localhost',
		});
		expect(closePopUpMock).toBeCalledTimes(0);

		await act(async () => {
			window.dispatchEvent(messageEventValid);
		});

		expect(fetch.mock.calls.length).toEqual(2);

		const connectPostRequest = fetch.mock.calls[1][1];
		const connectPostBody = connectPostRequest!.body as FormData;

		expect(
			connectPostBody.get(
				`${marketplaceSettingsProps.portletNamespace}code`
			)
		).toBe(messageEventValid.data.code);
		expect(
			connectPostBody.get(
				`${marketplaceSettingsProps.portletNamespace}codeVerifier`
			)
		).toBeTruthy();
		expect(
			connectPostBody.get(
				`${marketplaceSettingsProps.portletNamespace}serviceURL`
			)
		).toBe(messageEventValid.data.serviceURL);

		expect(closePopUpMock).toBeCalledTimes(1);

		expect(statusListItem).toHaveClass('active');

		expect(queryByText('connected')).toBeInTheDocument();

		expect(queryByText('disconnect')).toBeTruthy();
	});

	it('renders authorized and to the disconnection flow', async () => {
		fetch
			.mockResponseOnce(JSON.stringify({authorized: true, data: {}}))
			.mockResponseOnce(JSON.stringify({success: true}));

		const {container, queryByText} = render(<MarketplaceSettingsWrapper />);

		expect(fetch.mock.calls.length).toEqual(1);

		await waitForElementToBeRemoved(() =>
			container.querySelector('.loading-animation-primary')
		);

		expect(queryByText('marketplace-connection')).toBeInTheDocument();

		const disconnectButton = container.querySelector('button');

		expect(disconnectButton).toBeInTheDocument();
		expect(disconnectButton).toHaveTextContent('disconnect');

		await act(async () => {
			await fireEvent.click(disconnectButton as HTMLButtonElement);
		});

		expect(queryByText('connect-to-the-marketplace')).toBeTruthy();
	});

	it('renders unauthorized and connect with invalid message event data', async () => {
		fetch.mockResponseOnce(JSON.stringify({authorized: false, data: null}));

		const {container} = render(<MarketplaceSettingsWrapper />);

		await waitForElementToBeRemoved(() =>
			container.querySelector('.loading-animation-primary')
		);

		const connectButton = container.querySelector('.btn.btn-primary');

		fireEvent.click(connectButton as HTMLButtonElement);

		await act(async () => {
			window.dispatchEvent(messageEventInvalid);

			expect(closePopUpMock).toBeCalledTimes(0);
		});

		await act(async () => {
			window.dispatchEvent(messageEventInvalidWithoutData);

			expect(closePopUpMock).toBeCalledTimes(0);
		});
	});
});
