/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetchMock from 'fetch-mock';

import '@testing-library/jest-dom';
import {cleanup, render, waitFor} from '@testing-library/react';
import React from 'react';
import ResizeObserver from 'resize-observer-polyfill';

import DefaultView from '../../../../src/main/resources/META-INF/resources/js/info_box/DefaultView';
import InfoBoxModalAddressInput from '../../../../src/main/resources/META-INF/resources/js/info_box/modal/InfoBoxModalAddressInput';
import {setFieldValue} from '../../tests_utilities/utils.spec';

function getLocators(renderedComponent) {
	return {
		addressIdSelect: renderedComponent.getByLabelText('choose-x'),
		addressSubtypeInput: renderedComponent.queryByLabelText('subtype'),
	};
}

const POSTAL_ADDRESS_DATA = [
	{
		addressCountry: 'United States',
		addressLocality: 'addressLocality1',
		addressRegion: 'Alabama',
		addressType: 'billing-and-shipping',
		externalReferenceCode: '71061669-ba97-943c-70a3-96cdc0c8305a',
		id: 101,
		name: 'name1',
		phoneNumber: 'phoneNumber1',
		postalCode: 'postalCode1',
		primary: false,
		streetAddressLine1: 'streetAddressLine11',
		streetAddressLine2: 'streetAddressLine21',
		streetAddressLine3: 'streetAddressLine31',
	},
	{
		addressCountry: 'United States',
		addressLocality: 'addressLocality2',
		addressRegion: 'Alabama',
		addressType: 'shipping',
		externalReferenceCode: '71061669-ba97-943c-70a3-96cdc0c8305b',
		id: 102,
		name: 'name2',
		postalCode: 'postalCode2',
		primary: false,
		streetAddressLine1: 'streetAddressLine12',
	},
	{
		addressCountry: 'Italy',
		addressLocality: 'addressLocality3',
		addressType: 'shipping',
		externalReferenceCode: '71061669-ba97-943c-70a3-96cdc0c8305c',
		id: 103,
		name: 'name3',
		phoneNumber: 'phoneNumber3',
		postalCode: 'postalCode3',
		primary: false,
		streetAddressLine1: 'streetAddressLine13',
		streetAddressLine2: 'streetAddressLine23',
		streetAddressLine3: 'streetAddressLine33',
	},
	{
		addressType: 'billing',
		id: 104,
		name: 'name4',
	},
	{
		addressCountry: 'United States',
		addressLocality: 'addressLocality1',
		addressRegion: 'Alabama',
		addressSubtype: 'SHIPPING2',
		addressType: 'shipping',
		externalReferenceCode: '71061669-ba97-943c-70a3-96cdc0c8305a',
		id: 105,
		name: 'name1',
		phoneNumber: 'phoneNumber1',
		postalCode: 'postalCode1',
		primary: false,
		streetAddressLine1: 'streetAddressLine11',
		streetAddressLine2: 'streetAddressLine21',
		streetAddressLine3: 'streetAddressLine31',
	},
	{
		addressCountry: 'United States',
		addressLocality: 'addressLocality1',
		addressRegion: 'Alabama',
		addressSubtype: 'BILLINGANDSHIPPING2',
		addressType: 'billing-and-shipping',
		externalReferenceCode: '71061669-ba97-943c-70a3-96cdc0c8305a',
		id: 106,
		name: 'name1',
		phoneNumber: 'phoneNumber1',
		postalCode: 'postalCode1',
		primary: false,
		streetAddressLine1: 'streetAddressLine11',
		streetAddressLine2: 'streetAddressLine21',
		streetAddressLine3: 'streetAddressLine31',
	},
];

global.Liferay.CommerceContext = {
	account: {
		accountId: 10,
	},
};
global.ResizeObserver = ResizeObserver;

describe('InfoBoxModalAddressInput', () => {
	beforeEach(async () => {
		fetchMock.get(/headless-admin-address\/.*\/countries/i, () => {
			return {
				items: [
					{
						a2: 'US',
						a3: 'USA',
						active: true,
						id: 1,
						name: 'united-states',
						regions: [
							{
								active: true,
								countryId: 1,
								id: 100,
								name: 'Alabama',
								regionCode: 'AL',
								title_i18n: {
									en_US: 'Alabama',
								},
							},
							{
								active: true,
								countryId: 1,
								id: 101,
								name: 'Alaska',
								regionCode: 'AK',
								title_i18n: {
									en_US: 'Alaska',
								},
							},
							{
								active: false,
								countryId: 1,
								id: 102,
								name: 'Test',
								regionCode: 'TS',
								title_i18n: {
									en_US: 'Test',
								},
							},
						],
						title_i18n: {
							en_US: 'United States',
						},
					},
					{
						a2: 'IT',
						a3: 'ITA',
						active: true,
						id: 2,
						name: 'italy',
						regions: [],
						title_i18n: {
							en_US: 'Italy',
						},
					},
					{
						a2: 'DE',
						a3: 'DEN',
						active: false,
						id: 3,
						name: 'germany',
						regions: [],
						title_i18n: {
							en_US: 'Germany',
						},
					},
				],
			};
		});

		fetchMock.get(
			/headless-admin-user\/.*\/accounts\/\d+\/postal-addresses$/i,
			() => {
				return {
					items: POSTAL_ADDRESS_DATA,
				};
			}
		);
	});

	afterEach(() => {
		fetchMock.restore();
		jest.clearAllMocks();

		cleanup();
	});

	it('Must hide the subtype field if configuration is not provided', async () => {
		const renderedComponent = render(
			<InfoBoxModalAddressInput
				additionalProps={{
					addressSubtypeConfiguration: {
						billing: '',
						billingAndShipping: '',
						shipping: '',
					},
				}}
				field="shippingAddress"
				label="Shipping"
				orderId="10"
				setHandleSubmit={jest.fn()}
				setInputValue={jest.fn()}
				setParseRequest={jest.fn()}
				setParseResponse={jest.fn()}
				submitOrder={jest.fn()}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(2);

		const {addressSubtypeInput} = getLocators(renderedComponent);

		expect(addressSubtypeInput).not.toBeInTheDocument();
	});

	it('Must show the subtype field if configuration is provided', async () => {
		fetchMock.get(
			/headless-admin-list-type\/.*\/list-type-definitions\/by-external-reference-code\/\w+\/list-type-entries/i,
			() => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				};
			}
		);

		const renderedComponent = render(
			<InfoBoxModalAddressInput
				additionalProps={{
					addressSubtypeConfiguration: {
						billing: '',
						billingAndShipping: 'ERCBILLINGANDSHIPPING',
						shipping: 'ERCSHIPPING',
					},
				}}
				field="shippingAddress"
				label="Shipping"
				orderId="10"
				setHandleSubmit={jest.fn()}
				setInputValue={jest.fn()}
				setParseRequest={jest.fn()}
				setParseResponse={jest.fn()}
				submitOrder={jest.fn()}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(3);

		const {addressSubtypeInput} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressSubtypeInput).toBeVisible();
		});
	});

	it('Must show the subtype field disabled if configuration is not provided for specific address type', async () => {
		fetchMock.get(
			/headless-admin-list-type\/.*\/list-type-definitions\/by-external-reference-code\/\w+\/list-type-entries/i,
			() => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				};
			}
		);

		const renderedComponent = render(
			<InfoBoxModalAddressInput
				additionalProps={{
					addressSubtypeConfiguration: {
						billing: '',
						billingAndShipping: 'ERCBILLINGANDSHIPPING',
						shipping: '',
					},
				}}
				field="shippingAddress"
				label="Shipping"
				orderId="10"
				setHandleSubmit={jest.fn()}
				setInputValue={jest.fn()}
				setParseRequest={jest.fn()}
				setParseResponse={jest.fn()}
				submitOrder={jest.fn()}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(2);

		const {addressSubtypeInput} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressSubtypeInput).toBeVisible();
			expect(addressSubtypeInput).toBeDisabled();
		});
	});

	it('Must set the subtype field when adding a new address', async () => {
		fetchMock.get(
			/headless-admin-list-type\/.*\/list-type-definitions\/by-external-reference-code\/\w+\/list-type-entries/i,
			() => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				};
			}
		);

		const renderedComponent = render(
			<InfoBoxModalAddressInput
				additionalProps={{
					addressSubtypeConfiguration: {
						billing: '',
						billingAndShipping: '',
						shipping: 'ERCSHIPPING',
					},
				}}
				field="shippingAddress"
				label="Shipping"
				orderId="10"
				setHandleSubmit={jest.fn()}
				setInputValue={jest.fn()}
				setParseRequest={jest.fn()}
				setParseResponse={jest.fn()}
				submitOrder={jest.fn()}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(3);

		const {addressIdSelect, addressSubtypeInput} =
			getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressIdSelect?.options?.length).toBe(5);
		});

		await setFieldValue(addressSubtypeInput, 'SHIPPING1');
		expect(addressSubtypeInput).toHaveValue('SHIPPING1');

		await setFieldValue(addressSubtypeInput, 'SHIPPING2');
		expect(addressSubtypeInput).toHaveValue('SHIPPING2');
	});

	it('Must preload subtype field if specified address as it', async () => {
		fetchMock.get(
			/headless-admin-list-type\/.*\/list-type-definitions\/by-external-reference-code\/\w+\/list-type-entries/i,
			() => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				};
			}
		);

		const renderedComponent = render(
			<InfoBoxModalAddressInput
				additionalProps={{
					addressSubtypeConfiguration: {
						billing: '',
						billingAndShipping: 'ERCBILLINGANDSHIPPING',
						shipping: 'ERCSHIPPING',
					},
				}}
				field="shippingAddress"
				inputValue="105"
				label="Shipping"
				orderId="10"
				setHandleSubmit={jest.fn()}
				setInputValue={jest.fn()}
				setParseRequest={jest.fn()}
				setParseResponse={jest.fn()}
				submitOrder={jest.fn()}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(3);

		const {addressIdSelect, addressSubtypeInput} =
			getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressIdSelect?.options?.length).toBe(5);
		});

		expect(addressSubtypeInput).toBeDisabled();
		expect(addressSubtypeInput).toHaveValue('SHIPPING 2');
	});

	it('Must preload the subtype field when selecting an existing address', async () => {
		const mockFetch = jest
			.fn()
			.mockReturnValueOnce(() => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				};
			})
			.mockReturnValueOnce(() => {
				return {
					items: [
						{
							id: 100,
							key: 'BILLINGANDSHIPPING1',
							name: 'BILLING AND SHIPPING 1',
						},
						{
							id: 101,
							key: 'BILLINGANDSHIPPING2',
							name: 'BILLING AND SHIPPING 2',
						},
					],
				};
			})
			.mockReturnValue(() => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				};
			});

		fetchMock.get(
			/headless-admin-list-type\/.*\/list-type-definitions\/by-external-reference-code\/\w+\/list-type-entries/i,
			mockFetch
		);

		const renderedComponent = render(
			<DefaultView
				additionalProps={{
					addressSubtypeConfiguration: {
						billing: '',
						billingAndShipping: 'ERCBILLINGANDSHIPPING',
						shipping: 'ERCSHIPPING',
					},
					hasManageAddressesPermission: true,
				}}
				field="shippingAddress"
				hasPermission={true}
				isOpen={true}
				label="Shipping"
				orderId="10"
			/>
		);

		await renderedComponent.getByRole('button', {name: 'add-x'}).click();

		await waitFor(() => {
			expect(
				renderedComponent.queryByLabelText('subtype')
			).not.toBeNull();
		});

		const {addressIdSelect, addressSubtypeInput} =
			getLocators(renderedComponent);

		const changeAddress = async (addressId, subtype) => {
			setFieldValue(addressIdSelect, String(addressId), false);

			await waitFor(() => {
				expect(addressSubtypeInput).toBeDisabled();
				expect(addressSubtypeInput).toHaveValue(subtype);
			});
		};

		await waitFor(() => {
			expect(addressIdSelect?.options?.length).toBe(6);
		});

		await changeAddress(105, 'SHIPPING 2');

		expect(fetchMock.calls().matched.length).toBe(3);

		await changeAddress(106, 'BILLING AND SHIPPING 2');

		expect(fetchMock.calls().matched.length).toBe(4);

		await changeAddress(101, '');

		expect(fetchMock.calls().matched.length).toBe(4);
	});
});
