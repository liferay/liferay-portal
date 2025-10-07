/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../tests_utilities/polyfills';

import '@testing-library/jest-dom/extend-expect';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import CurrencySelector from '../../../src/main/resources/META-INF/resources/components/currency_selector/CurrencySelector';
import * as CurrencySelectorUtils from '../../../src/main/resources/META-INF/resources/components/currency_selector/util';
import * as CurrencyChangeModal from '../../../src/main/resources/META-INF/resources/utilities/modals/confirmCurrencyChange';

const mockCURRENCIES = [
	{
		active: true,
		code: 'USD',
		externalReferenceCode: '1772bef2-8512-b8a3-067c-0e5462a6e454',
		formatPattern: {
			en_US: '$ ###,##0.00',
		},
		id: 30128,
		maxFractionDigits: 2,
		minFractionDigits: 2,
		name: {
			en_US: 'US Dollar',
		},
		primary: true,
		priority: 1,
		rate: 1,
		roundingMode: 'HALF_EVEN',
		symbol: '$',
	},
	{
		active: true,
		code: 'GBP',
		externalReferenceCode: '1ca319a1-3fa5-b341-1ae7-15858064a23b',
		formatPattern: {
			en_US: '£ ###,##0.00',
		},
		id: 30130,
		maxFractionDigits: 2,
		minFractionDigits: 2,
		name: {
			en_US: 'British Pound',
		},
		primary: false,
		priority: 3,
		rate: 0.7914,
		roundingMode: 'HALF_EVEN',
		symbol: '£',
	},
	{
		active: true,
		code: 'CNY',
		externalReferenceCode: 'b007208b-2dfd-e79e-8b75-95837869c5af',
		formatPattern: {
			en_US: '¥ ###,##0.00',
		},
		id: 30132,
		maxFractionDigits: 2,
		minFractionDigits: 2,
		name: {
			en_US: 'Chinese Yuan Renminbi',
		},
		primary: false,
		priority: 5,
		rate: 7.25,
		roundingMode: 'HALF_EVEN',
		symbol: '¥',
	},
];

jest.mock(
	'../../../src/main/resources/META-INF/resources/ServiceProvider',
	() => ({
		...jest.requireActual(
			'../../../src/main/resources/META-INF/resources/ServiceProvider'
		),
		DeliveryCatalogAPI: () => ({
			getCurrenciesByChannelId: jest.fn(() =>
				Promise.resolve({
					items: mockCURRENCIES,
				})
			),
		}),
	})
);

jest.mock('frontend-js-web', () => {
	return {
		...jest.requireActual('frontend-js-web'),
		openToast: jest.fn(),
	};
});

jest.mock(
	'../../../src/main/resources/META-INF/resources/utilities/modals/confirmCurrencyChange',
	() => ({
		confirmCurrencyChange: jest.fn(),
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/components/currency_selector/util'
);

describe('CurrencySelector', () => {
	const BASE_PROPS = {
		commerceChannelId: 24324,
		commerceOrderDetailBaseURL: 'http://order-detail.url',
		commerceOrderId: 0,
	};

	const {Liferay: originalLiferay, location: originalLocation} = window;

	beforeEach(() => {
		window['Liferay'] = {
			...originalLiferay,
			CommerceContext: {
				account: {
					accountId: 12345,
				},
				currency: {
					currencyCode: 'USD',
				},
			},
			detach: jest.fn(),
			on: jest.fn(),
		};

		Object.defineProperty(window, 'location', {
			configurable: true,
			value: {reload: jest.fn()},
		});
	});

	afterEach(() => {
		cleanup();

		Object.defineProperty(window, 'location', {
			configurable: true,
			value: {reload: originalLocation},
		});

		window.Liferay = originalLiferay;

		jest.resetAllMocks();
	});

	describe('Default', () => {
		it('shows the currently active currency', async () => {
			jest.spyOn(
				CurrencySelectorUtils,
				'retrieveCommerceCurrency'
			).mockImplementation(() => undefined);

			const {getByText} = render(<CurrencySelector {...BASE_PROPS} />);

			await waitFor(() => {
				const currencySelectorElement = getByText('$ USD');

				expect(currencySelectorElement).toBeInTheDocument();
				expect(currencySelectorElement.type).toEqual('button');
				expect(
					CurrencySelectorUtils.retrieveCommerceCurrency
				).toHaveBeenCalled();
			});
		});

		it('shows the list of available currencies', async () => {
			mockCURRENCIES.push({
				active: false,
				code: 'NOPE',
				externalReferenceCode: 'no-pe',
				formatPattern: {
					en_US: '$ ###,##0.00',
				},
				id: 30128,
				maxFractionDigits: 2,
				minFractionDigits: 2,
				name: {
					en_US: 'NO pe',
				},
				primary: true,
				priority: 1,
				rate: 1,
				roundingMode: 'HALF_EVEN',
				symbol: '$',
			});

			jest.spyOn(
				CurrencySelectorUtils,
				'retrieveCommerceCurrency'
			).mockImplementation(() => undefined);

			const {getByText} = render(<CurrencySelector {...BASE_PROPS} />);

			await waitFor(() => {
				const currencySelectorElement = getByText('$ USD');

				fireEvent.click(currencySelectorElement);
			});

			await waitFor(() => {
				const dropdownItems = Array.from(
					document.querySelectorAll('.dropdown-item')
				);

				expect(dropdownItems.length).toEqual(3);

				dropdownItems.forEach((dropdownItem, index) => {
					expect(dropdownItem.innerHTML).toEqual(
						`${mockCURRENCIES[index].symbol} ${mockCURRENCIES[index].code}`
					);

					if (mockCURRENCIES[index].code === 'USD') {
						expect(dropdownItem.classList.contains('active')).toBe(
							true
						);
					}
				});
			});
		});

		it('stores as a cookie the default selected currency code if not present', async () => {
			jest.spyOn(
				CurrencySelectorUtils,
				'retrieveCommerceCurrency'
			).mockImplementation(() => undefined);

			jest.spyOn(CurrencySelectorUtils, 'storeCommerceCurrency');

			render(<CurrencySelector {...BASE_PROPS} />);

			await waitFor(() => {
				expect(
					CurrencySelectorUtils.storeCommerceCurrency
				).toHaveBeenCalled();
			});
		});
	});

	describe('Interaction', () => {
		it('allows to select a different currency', async () => {
			jest.spyOn(
				CurrencySelectorUtils,
				'retrieveCommerceCurrency'
			).mockImplementation(() => 'USD');

			jest.spyOn(CurrencySelectorUtils, 'storeCommerceCurrency');

			const {container, getByText} = render(
				<CurrencySelector {...BASE_PROPS} />
			);

			await waitFor(() => {
				const currencySelectorButton = getByText('$ USD');

				fireEvent.click(currencySelectorButton);
			});

			await waitFor(() => {
				const currencySelectionItem = document.querySelector(
					'[data-testid="GBP"]'
				);

				fireEvent.click(currencySelectionItem);
			});

			await waitFor(() => {
				expect(container.querySelector('button').innerHTML).toEqual(
					'£ GBP'
				);
				expect(
					CurrencySelectorUtils.retrieveCommerceCurrency
				).toHaveBeenCalled();
				expect(
					CurrencySelectorUtils.storeCommerceCurrency
				).toHaveBeenCalledWith('GBP');
				expect(window.location.reload).toHaveBeenCalled();
			});
		});

		it('allows to change to a different currency with an active order', async () => {
			jest.spyOn(
				CurrencySelectorUtils,
				'retrieveCommerceCurrency'
			).mockImplementation(() => 'USD');

			jest.spyOn(CurrencySelectorUtils, 'storeCommerceCurrency');

			const {getByText} = render(
				<CurrencySelector {...BASE_PROPS} commerceOrderId={123} />
			);

			await waitFor(() => {
				const currencySelectorButton = getByText('$ USD');

				fireEvent.click(currencySelectorButton);
			});

			await waitFor(() => {
				const currencySelectionItem = document.querySelector(
					'[data-testid="CNY"]'
				);

				fireEvent.click(currencySelectionItem);
			});

			await waitFor(() => {
				expect(
					CurrencyChangeModal.confirmCurrencyChange
				).toHaveBeenCalledTimes(1);
				expect(
					CurrencyChangeModal.confirmCurrencyChange
				).toHaveBeenCalledWith({
					accountId: window.Liferay.CommerceContext.account.accountId,
					commerceChannelId: BASE_PROPS.commerceChannelId,
					currencyCode: 'CNY',
					onCancel: expect.any(Function),
					orderDetailURL: BASE_PROPS.commerceOrderDetailBaseURL,
				});
			});
		});
	});
});
