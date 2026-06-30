/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, waitFor} from '@testing-library/react';
import React from 'react';

import MiniCompare from '../../../src/main/resources/META-INF/resources/components/mini_compare/MiniCompare';
import {TOGGLE_ITEM_IN_PRODUCT_COMPARISON} from '../../../src/main/resources/META-INF/resources/utilities/eventsDefinitions';

const PRODUCT_COMPARISON_COOKIES_TITLE_KEY = 'product-comparison-cookies-title';
const PRODUCT_COMPARISON_COOKIES_ALERT_KEY = 'product-comparison-cookies-alert';
const PRODUCT_COMPARISON_COOKIES_SUCCESS_KEY =
	'product-comparison-cookies-success';
const PRODUCT_COMPARISON_COOKIES_WARNING_KEY =
	'product-comparison-cookies-warning';

const mockCheckCookieConsentForTypes = jest.fn();
const mockCheckConsent = jest.fn();
const mockGetCookie = jest.fn();
const mockSetCookie = jest.fn();

jest.mock('@liferay/cookies-banner-web', () => ({
	checkCookieConsentForTypes: (...args) =>
		mockCheckCookieConsentForTypes(...args),
}));

jest.mock('frontend-js-web', () => ({
	COOKIE_TYPES: {
		FUNCTIONAL: 'CONSENT_TYPE_FUNCTIONAL',
		NECESSARY: 'CONSENT_TYPE_NECESSARY',
		PERFORMANCE: 'CONSENT_TYPE_PERFORMANCE',
		PERSONALIZATION: 'CONSENT_TYPE_PERSONALIZATION',
	},
	checkConsent: (...args) => mockCheckConsent(...args),
	getCookie: (...args) => mockGetCookie(...args),
	setCookie: (...args) => mockSetCookie(...args),
}));

const BASE_PROPS = {
	commerceChannelGroupId: 12345,
	compareProductsURL: 'http://test.url/compare',
	itemsLimit: 5,
	portletNamespace: '_portletNamespace_',
};

describe('MiniCompare', () => {
	beforeEach(() => {
		window.themeDisplay = {
			getPathContext: () => '/',
		};

		window.Liferay = {
			Language: {
				get: jest.fn((key) => key),
			},
			Util: {
				openToast: jest.fn(),
			},
			detach: jest.fn(),
			fire: jest.fn(),
			on: jest.fn(),
		};

		mockCheckConsent.mockReturnValue(true);
		mockGetCookie.mockReturnValue(undefined);
		mockSetCookie.mockReturnValue(true);
	});

	afterEach(() => {
		jest.resetAllMocks();
	});

	it('renders nothing when there are no items in the compare cookie', () => {
		const {container} = render(<MiniCompare {...BASE_PROPS} />);

		expect(container).toBeEmptyDOMElement();
		expect(mockCheckCookieConsentForTypes).not.toHaveBeenCalled();
	});

	it('renders the compare bar when items are present and functional cookies are accepted', () => {
		mockGetCookie.mockReturnValue('1:2');
		mockCheckConsent.mockReturnValue(true);

		const {container} = render(
			<MiniCompare
				{...BASE_PROPS}
				items={[
					{id: '1', thumbnail: 'thumb1.png'},
					{id: '2', thumbnail: 'thumb2.png'},
				]}
			/>
		);

		expect(
			container.querySelector('.mini-compare.active')
		).toBeInTheDocument();
		expect(mockCheckCookieConsentForTypes).not.toHaveBeenCalled();
	});

	it('triggers the consent modal with the product-comparison-cookies-title custom title when items are present and functional cookies are declined', async () => {
		mockGetCookie.mockReturnValue('1');
		mockCheckConsent.mockReturnValue(false);
		mockCheckCookieConsentForTypes.mockReturnValue(new Promise(() => {}));

		render(
			<MiniCompare
				{...BASE_PROPS}
				items={[{id: '1', thumbnail: 'thumb1.png'}]}
			/>
		);

		await waitFor(() => {
			expect(mockCheckCookieConsentForTypes).toHaveBeenCalledWith(
				'CONSENT_TYPE_FUNCTIONAL',
				{
					alertMessage: PRODUCT_COMPARISON_COOKIES_ALERT_KEY,
					customTitle: PRODUCT_COMPARISON_COOKIES_TITLE_KEY,
				}
			);
		});
	});

	it('does not render the compare bar while the consent modal is pending', () => {
		mockGetCookie.mockReturnValue('1');
		mockCheckConsent.mockReturnValue(false);
		mockCheckCookieConsentForTypes.mockReturnValue(new Promise(() => {}));

		const {container} = render(
			<MiniCompare
				{...BASE_PROPS}
				items={[{id: '1', thumbnail: 'thumb1.png'}]}
			/>
		);

		expect(
			container.querySelector('.mini-compare')
		).not.toBeInTheDocument();
	});

	it('persists the compare cookie and shows a success toast when the buyer accepts functional cookies', async () => {
		mockGetCookie.mockReturnValue('1');
		mockCheckConsent.mockReturnValue(false);
		mockCheckCookieConsentForTypes.mockResolvedValue();

		render(
			<MiniCompare
				{...BASE_PROPS}
				items={[{id: '1', thumbnail: 'thumb1.png'}]}
			/>
		);

		await waitFor(() => {
			expect(mockSetCookie).toHaveBeenCalledWith(
				`COMMERCE_COMPARE_cpDefinitionIds_${BASE_PROPS.commerceChannelGroupId}`,
				'1',
				'CONSENT_TYPE_FUNCTIONAL',
				{path: '/'}
			);

			expect(window.Liferay.Util.openToast).toHaveBeenCalledWith(
				expect.objectContaining({
					message: PRODUCT_COMPARISON_COOKIES_SUCCESS_KEY,
					type: 'success',
				})
			);
		});
	});

	it('shows a warning toast when the buyer declines functional cookies', async () => {
		mockGetCookie.mockReturnValue('1');
		mockCheckConsent.mockReturnValue(false);
		mockCheckCookieConsentForTypes.mockRejectedValue();

		render(
			<MiniCompare
				{...BASE_PROPS}
				items={[{id: '1', thumbnail: 'thumb1.png'}]}
			/>
		);

		await waitFor(() => {
			expect(window.Liferay.Util.openToast).toHaveBeenCalledWith(
				expect.objectContaining({
					message: PRODUCT_COMPARISON_COOKIES_WARNING_KEY,
					type: 'warning',
				})
			);
		});
	});

	it('triggers the consent modal when the buyer lands on a site with previously declined functional cookies and items in the compare cookie', async () => {
		mockGetCookie.mockReturnValue('1:2');
		mockCheckConsent.mockReturnValue(false);
		mockCheckCookieConsentForTypes.mockReturnValue(new Promise(() => {}));

		render(
			<MiniCompare
				{...BASE_PROPS}
				items={[
					{id: '1', thumbnail: 'thumb1.png'},
					{id: '2', thumbnail: 'thumb2.png'},
				]}
			/>
		);

		await waitFor(() => {
			expect(mockCheckCookieConsentForTypes).toHaveBeenCalledTimes(1);
			expect(mockCheckCookieConsentForTypes).toHaveBeenCalledWith(
				'CONSENT_TYPE_FUNCTIONAL',
				expect.objectContaining({
					customTitle: PRODUCT_COMPARISON_COOKIES_TITLE_KEY,
				})
			);
		});
	});

	it('adds the product to the compare bar when a product that is not being compared is toggled from its card', async () => {
		mockGetCookie.mockReturnValue('1');
		mockCheckConsent.mockReturnValue(true);

		let toggleItemTrigger;

		window.Liferay.on = jest.fn((eventName, callback) => {
			if (eventName === TOGGLE_ITEM_IN_PRODUCT_COMPARISON) {
				toggleItemTrigger = callback;
			}
		});

		const {container} = render(
			<MiniCompare
				{...BASE_PROPS}
				items={[{id: '1', thumbnail: 'thumb1.png'}]}
			/>
		);

		expect(
			container.querySelectorAll('.mini-compare-item.active')
		).toHaveLength(1);

		await act(async () => {
			toggleItemTrigger({id: '2', thumbnail: 'thumb2.png'});
		});

		await waitFor(() => {
			expect(
				container.querySelectorAll('.mini-compare-item.active')
			).toHaveLength(2);
		});
	});

	it('removes the product from the compare bar when a product that is already being compared is toggled from its card', async () => {
		mockGetCookie.mockReturnValue('1:2');
		mockCheckConsent.mockReturnValue(true);

		let toggleItemTrigger;

		window.Liferay.on = jest.fn((eventName, callback) => {
			if (eventName === TOGGLE_ITEM_IN_PRODUCT_COMPARISON) {
				toggleItemTrigger = callback;
			}
		});

		const {container} = render(
			<MiniCompare
				{...BASE_PROPS}
				items={[
					{id: '1', thumbnail: 'thumb1.png'},
					{id: '2', thumbnail: 'thumb2.png'},
				]}
			/>
		);

		expect(
			container.querySelectorAll('.mini-compare-item.active')
		).toHaveLength(2);

		await act(async () => {
			toggleItemTrigger({id: '1', thumbnail: 'thumb1.png'});
		});

		await waitFor(() => {
			expect(
				container.querySelectorAll('.mini-compare-item.active')
			).toHaveLength(1);
		});
	});
});
