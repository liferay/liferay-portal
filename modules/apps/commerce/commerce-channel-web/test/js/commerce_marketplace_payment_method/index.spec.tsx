/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import mockFetch from 'jest-fetch-mock';
import React from 'react';
import {act} from 'react-dom/test-utils';

import '@testing-library/jest-dom/extend-expect';

import CommerceChannelAddPaymentMethod from '../../../src/main/resources/META-INF/resources/js/commerce_marketplace_payment_method';
import marketplacePermissionsMock from '../__mock__/marketplacePermissions';
import {marketplaceSettingsMock} from '../__mock__/marketplaceSettings';
import placedOrders from '../__mock__/placedOrderresponse';
import {productResponseMock} from '../__mock__/product';
import projectsMockResponse from '../__mock__/projetctMockResponse';

const cartResponseMock = {
	cartItems: [
		{
			id: 123,
		},
	],
	id: 123,
};

const marketplaceToken = {
	accessToken: 'abcdef',
	accessTokenExpirationTime: (new Date().getTime() + 60000).toString(),
};

jest.mock('frontend-js-web', () => {
	const actual = jest.requireActual('frontend-js-web');

	return {
		...actual,
		createResourceURL: jest.fn((baseURL, params) => ({
			href: `${baseURL}?${new URLSearchParams(params).toString()}`,
			toString: () =>
				`${baseURL}?${new URLSearchParams(params).toString()}`,
		})),
		fetch: mockFetch,
	};
});

describe('CommerceMarketplacePaymentMethod', () => {
	let getItemFn: jest.Mock<WindowSessionStorage['sessionStorage']['getItem']>;
	let setItemFn: jest.Mock<WindowSessionStorage['sessionStorage']['setItem']>;

	beforeEach(() => {
		getItemFn = jest
			.fn()
			.mockImplementation(() => JSON.stringify(marketplaceToken));
		setItemFn = jest.fn();

		globalThis.Liferay.Util.SessionStorage = {
			TYPES: {
				NECESSARY: 'Necessary',
			},
			getItem: getItemFn,
			setItem: setItemFn,
		} as any;

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

	it('renders unauthorized and click add button', async () => {
		const {fetch} = require('frontend-js-web');

		fetch.mockResponseOnce(JSON.stringify({authorized: false, data: null}));

		const {queryByRole, queryByText} = render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);
		const marketplaceIcon = queryByRole('presentation');

		expect(marketplaceIcon).toBeInTheDocument();
		expect(marketplaceIcon).toBeVisible();
		expect(marketplaceIcon).toHaveClass('lexicon-icon-marketplace');

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		act(() => jest.runAllTimers());

		expect(fetch.mock.calls.length).toBe(1);
		expect(queryByText('connection-with-marketplace-needed')).toBeTruthy();
		expect(queryByText('go-to-instance-settings')).toBeTruthy();
		expect(
			queryByText(
				'you-are-trying-to-add-a-new-payment-method-through-the-marketplace,-but-the-connection-has-not-been-established-yetplease-go-to-instance-settings-to-enable-the-connection'
			)
		).toBeTruthy();
	});

	it('renders modal connection with marketplace needed', async () => {
		const {fetch} = require('frontend-js-web');

		fetch.mockResponseOnce(JSON.stringify({authorized: false, data: null}));

		const {queryByText} = render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		act(() => jest.runAllTimers());

		expect(fetch.mock.calls.length).toBe(1);

		expect(queryByText('connection-with-marketplace-needed')).toBeTruthy();
		expect(queryByText('go-to-instance-settings')).toBeTruthy();
	});

	it('renders unauthorized and click on go to instance settings button', async () => {
		const {fetch} = require('frontend-js-web');

		fetch.mockResponseOnce(JSON.stringify({authorized: false, data: null}));

		const {queryByText} = render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		act(() => jest.runAllTimers());

		const instanceSettingsButton = queryByText('go-to-instance-settings');

		expect(fetch.mock.calls.length).toBe(1);
		expect(instanceSettingsButton).toBeInTheDocument();
	});

	it('renders unauthorized without apps', async () => {
		const {fetch} = require('frontend-js-web');

		fetch
			.mockResponseOnce(JSON.stringify(marketplaceSettingsMock))
			.mockResponseOnce(
				JSON.stringify({...productResponseMock, items: []})
			)
			.mockResponseOnce(JSON.stringify(placedOrders))
			.mockResponseOnce(JSON.stringify(projectsMockResponse));

		const {queryByText} = await render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		await act(async () => jest.runAllTimers());

		expect(fetch.mock.calls.length).toBe(4);
		expect(queryByText('no-products-were-found')).toBeTruthy();
		expect(queryByText('no-results-were-found')).toBeTruthy();
	});

	it('renders authorized with apps and click install without cloud projects', async () => {
		const {fetch} = require('frontend-js-web');

		fetch
			.mockResponseOnce(
				JSON.stringify({
					...marketplaceSettingsMock,
					data: {
						...marketplaceSettingsMock.data,
						settings: {
							...marketplaceSettingsMock.data.settings,
							cloudProject: null,
						},
					},
				})
			)
			.mockResponseOnce(JSON.stringify(productResponseMock))
			.mockResponseOnce(JSON.stringify(placedOrders));

		const {queryByText} = render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		await act(async () => jest.runAllTimers());

		act(() => expect(queryByText('install')).toBeTruthy());

		await act(async () =>
			fireEvent.click(queryByText('install') as HTMLButtonElement)
		);

		expect(fetch.mock.calls.length).toBe(3);
		expect(queryByText('no-cloud-project-available')).toBeTruthy();
	});

	xit('renders authorized with apps and click on app card', async () => {
		const {fetch} = require('frontend-js-web');

		fetch
			.mockResponseOnce(JSON.stringify(marketplaceSettingsMock))
			.mockResponseOnce(JSON.stringify(productResponseMock))
			.mockResponseOnce(JSON.stringify(placedOrders))
			.mockResponseOnce(JSON.stringify(projectsMockResponse));

		const {queryByText} = await render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		await act(async () => jest.runAllTimers());

		expect(queryByText(productResponseMock.items[0].name)).toBeTruthy();

		const element = queryByText(
			productResponseMock.items[0].name
		) as HTMLButtonElement;

		fireEvent.click(element);

		expect(fetch.mock.calls.length).toBe(4);
		expect(queryByText('back-to-list')).toBeTruthy();
	});

	it('renders authorized with apps and click on cancel button', async () => {
		const {fetch} = require('frontend-js-web');

		fetch
			.mockResponseOnce(JSON.stringify(marketplaceSettingsMock))
			.mockResponseOnce(JSON.stringify(productResponseMock))
			.mockResponseOnce(JSON.stringify(placedOrders))
			.mockResponse(JSON.stringify(projectsMockResponse));

		const {queryByText} = await render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		await act(async () => jest.runAllTimers());

		expect(queryByText(productResponseMock.items[0].name)).toBeTruthy();

		await act(async () =>
			fireEvent.click(queryByText('install') as HTMLButtonElement)
		);
		await act(async () =>
			fireEvent.click(queryByText('cancel') as HTMLButtonElement)
		);

		expect(fetch.mock.calls.length).toBe(4);
		expect(queryByText(productResponseMock.items[0].name)).toBeTruthy();
	});

	it('renders authorized with apps and click install failed', async () => {
		const {fetch} = require('frontend-js-web');

		fetch
			.mockResponseOnce(JSON.stringify(marketplaceSettingsMock))
			.mockResponseOnce(JSON.stringify(productResponseMock))
			.mockResponseOnce(JSON.stringify(placedOrders))
			.mockResponseOnce(JSON.stringify(projectsMockResponse))
			.mockResponseOnce(JSON.stringify(projectsMockResponse))
			.mockResponseOnce(JSON.stringify(cartResponseMock))
			.mockRejectOnce();

		const {queryByText} = render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		await act(async () => jest.runAllTimers());

		fireEvent.click(queryByText('My App') as HTMLButtonElement);

		await act(async () =>
			fireEvent.click(queryByText('install') as HTMLButtonElement)
		);

		expect(queryByText('confirmation-required')).toBeTruthy();

		await act(async () =>
			fireEvent.click(queryByText('install') as HTMLButtonElement)
		);

		expect(fetch.mock.calls.length).toBe(7);
		expect(queryByText('there-was-an-unknown-error')).toBeTruthy();
	});

	it('render no resources', async () => {
		const {fetch} = require('frontend-js-web');

		const noResources = {
			...projectsMockResponse,
			rootProjectPlanUsage: {
				cpu: {
					free: 0,
					limit: 0,
					used: 0,
				},
				instance: {
					free: 0,
					limit: 0,
					used: 0,
				},
				memory: {
					free: 0,
					limit: 0,
					used: 0,
				},
			},
		};

		fetch
			.mockResponseOnce(JSON.stringify(marketplaceSettingsMock))
			.mockResponseOnce(JSON.stringify(productResponseMock))
			.mockResponseOnce(JSON.stringify(placedOrders))
			.mockResponse(JSON.stringify(noResources));

		const {queryByText} = render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		await act(async () => jest.runAllTimers());
		await act(async () => expect(queryByText('install')).toBeTruthy());
		await act(async () =>
			fireEvent.click(queryByText('install') as HTMLButtonElement)
		);

		expect(fetch.mock.calls.length).toBe(4);
		expect(queryByText('insufficient-resources')).toBeTruthy();
	});

	xit('renders success on instalation', async () => {
		const {fetch} = require('frontend-js-web');

		const placedOrderResponse = {
			...placedOrders,
			items: placedOrders.items.map((item) => ({
				...item,
				customFields: {
					...item.customFields,
					'cloud-provisioning': '',
				},
				id: 999999,
				placedOrderItems: item.placedOrderItems.map((orderItem) => ({
					...orderItem,
					customFields: {
						...orderItem.customFields,
						'cloud-provisioning': '',
					},
					id: 999999,
					productId: 999999,
				})),
			})),
		};

		fetch
			.mockResponseOnce(JSON.stringify(marketplaceSettingsMock))
			.mockResponseOnce(JSON.stringify(productResponseMock))
			.mockResponseOnce(JSON.stringify(placedOrderResponse))
			.mockResponseOnce(JSON.stringify(projectsMockResponse))
			.mockResponse(JSON.stringify(cartResponseMock));

		const {queryByText} = render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		await act(async () => jest.runAllTimers());

		fireEvent.click(queryByText('My App') as HTMLButtonElement);

		await act(() =>
			fireEvent.click(queryByText('install') as HTMLButtonElement)
		);
		await act(() =>
			fireEvent.click(queryByText('install') as HTMLButtonElement)
		);

		expect(fetch.mock.calls.length).toBe(7);
		expect(queryByText('success')).toBeTruthy();
		expect(
			queryByText(
				'your-application-has-been-installed,-wait-a-few-moments-for-it-to-become-available'
			)
		).toBeTruthy();
	});

	it('render disabled button on apps and storefront', async () => {
		const {fetch} = require('frontend-js-web');

		const placedOrderResponse = {
			...placedOrders,
			items: placedOrders.items.map((item) => ({
				...item,

				id: 999999,
				placedOrderItems: item.placedOrderItems.map((orderItem) => ({
					...orderItem,
					id: 999999,
					productId: 999999,
				})),
			})),
		};

		fetch
			.mockResponseOnce(JSON.stringify(marketplaceSettingsMock))
			.mockResponseOnce(JSON.stringify(productResponseMock))
			.mockResponseOnce(JSON.stringify(placedOrderResponse))
			.mockResponseOnce(JSON.stringify(projectsMockResponse));

		const {queryByText} = render(
			<CommerceChannelAddPaymentMethod
				baseResourceURL=""
				permissions={marketplacePermissionsMock}
			/>
		);

		await act(() => {
			fireEvent.click(queryByText('add') as HTMLButtonElement);
		});

		await act(async () => jest.runAllTimers());

		fireEvent.click(queryByText('My App') as HTMLButtonElement);

		await act(async () => jest.runAllTimers());

		expect(fetch.mock.calls.length).toBe(4);
		expect(queryByText('install')).toBeDisabled();
	});
});
