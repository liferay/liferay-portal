/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore

import fetchMock from 'fetch-mock';

import {
	IAddressSubtypeConfiguration,
	ICountryAPIResponse,
	IListTypeEntryAPIResponse,
	IPostalAddress,
	IPostalAddressAPIResponse,
} from '../../../src/main/resources/META-INF/resources/components/multishipping/Types';

import '@testing-library/jest-dom';
import {RenderResult, cleanup, render, waitFor} from '@testing-library/react';
import React from 'react';
import ResizeObserver from 'resize-observer-polyfill';

import AddressSelector from '../../../src/main/resources/META-INF/resources/components/multishipping/AddressSelector';
import * as ErrorMessage from '../../../src/main/resources/META-INF/resources/components/multishipping/ErrorMessage';
import {setFieldValue} from '../../tests_utilities/utils.spec';

interface ILocators {
	addressCountrySelect: HTMLSelectElement;
	addressIdSelect: HTMLSelectElement;
	addressLocalityInput: HTMLInputElement;
	addressRegionSelect: HTMLSelectElement;
	addressSubtypeInput: HTMLInputElement;
	nameInput: HTMLInputElement;
	phoneNumberInput: HTMLInputElement;
	postalCodeInput: HTMLInputElement;
	streetAddressLine1Input: HTMLInputElement;
	streetAddressLine2Input: HTMLInputElement;
	streetAddressLine3Input: HTMLInputElement;
}

function getLocators(renderedComponent: RenderResult): ILocators {
	return {
		addressCountrySelect: renderedComponent.getByLabelText('country'),
		addressIdSelect: renderedComponent.getByLabelText('choose-x'),
		addressLocalityInput: renderedComponent.getByLabelText('city'),
		addressRegionSelect: renderedComponent.getByLabelText('region'),
		addressSubtypeInput: renderedComponent.queryByLabelText('subtype'),
		nameInput: renderedComponent.getByLabelText('address-name'),
		phoneNumberInput: renderedComponent.getByLabelText('phone-number'),
		postalCodeInput: renderedComponent.getByLabelText('zip'),
		streetAddressLine1Input:
			renderedComponent.getByLabelText('address-line-1'),
		streetAddressLine2Input:
			renderedComponent.getByLabelText('address-line-2'),
		streetAddressLine3Input:
			renderedComponent.getByLabelText('address-line-3'),
	} as ILocators;
}

const POSTAL_ADDRESS_DATA: Array<IPostalAddress> = [
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

global.ResizeObserver = ResizeObserver;

describe('AddressSelector', () => {
	const handleSubmit = jest.fn();
	const isFormValid = jest.fn();

	beforeEach(async () => {
		fetchMock.get(
			/headless-admin-address\/.*\/countries/i,
			(): ICountryAPIResponse => {
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
			}
		);

		fetchMock.get(
			/headless-admin-user\/.*\/accounts\/\d+\/postal-addresses$/i,
			(): IPostalAddressAPIResponse => {
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

	it('Must load the countries and display only active', async () => {
		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		const {addressCountrySelect} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressCountrySelect?.options?.length).toBe(3);
		});
	});

	it('Must load the postal addresses and display only passed type', async () => {
		let renderedComponent = render(
			<AddressSelector
				accountId={10}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		let {addressIdSelect} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressIdSelect?.options?.length).toBe(6);
		});

		renderedComponent.unmount();

		renderedComponent = render(
			<AddressSelector
				accountId={10}
				addressType="billing"
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		({addressIdSelect} = getLocators(renderedComponent));

		await waitFor(() => {
			expect(addressIdSelect?.options?.length).toBe(4);
		});
	});

	it('Must load the regions when a country is selected', async () => {
		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		const {addressCountrySelect, addressRegionSelect} =
			getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressCountrySelect?.options?.length).toBe(3);
		});

		await setFieldValue(addressCountrySelect, 'Italy');

		expect(addressRegionSelect?.options?.length).toBe(1);
		expect(addressRegionSelect?.disabled).toBeTruthy();

		await setFieldValue(addressCountrySelect, 'United States');

		expect(addressRegionSelect?.options?.length).toBe(3);
		expect(addressRegionSelect?.disabled).toBeFalsy();

		await setFieldValue(addressRegionSelect, 'Alabama');

		await setFieldValue(addressCountrySelect, '');

		expect(addressRegionSelect?.options?.length).toBe(1);
		expect(addressRegionSelect?.disabled).toBeTruthy();
		expect(addressRegionSelect).toHaveValue('');
	});

	it('Must check mandatory fields', async () => {
		const checkIsFormValid = async (
			expectedResult: boolean,
			field: HTMLInputElement | HTMLSelectElement,
			value: string
		) => {
			await setFieldValue(field, value);

			expect(isFormValid).toBeCalledWith(expectedResult);
		};

		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		const {
			addressCountrySelect,
			addressLocalityInput,
			addressRegionSelect,
			nameInput,
			postalCodeInput,
			streetAddressLine1Input,
		} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressCountrySelect?.options?.length).toBe(3);
		});

		expect(isFormValid).toBeCalledWith(false);

		await checkIsFormValid(false, addressCountrySelect, 'Italy');
		await checkIsFormValid(false, addressLocalityInput, 'addressLocality');
		await checkIsFormValid(false, nameInput, 'name');
		await checkIsFormValid(false, postalCodeInput, 'postalCode');
		await checkIsFormValid(
			true,
			streetAddressLine1Input,
			'streetAddressLine1'
		);
		await checkIsFormValid(false, addressCountrySelect, 'United States');
		await checkIsFormValid(true, addressRegionSelect, 'Alabama');

		await checkIsFormValid(false, addressRegionSelect, '');
		await checkIsFormValid(false, addressCountrySelect, '');
		await checkIsFormValid(true, addressCountrySelect, 'Italy');
		await checkIsFormValid(false, addressLocalityInput, '');
		await checkIsFormValid(true, addressLocalityInput, 'addressLocality');
		await checkIsFormValid(false, nameInput, '');
		await checkIsFormValid(true, nameInput, 'name');
		await checkIsFormValid(false, postalCodeInput, '');
		await checkIsFormValid(true, postalCodeInput, 'postalCode');
		await checkIsFormValid(false, streetAddressLine1Input, '');
		await checkIsFormValid(
			true,
			streetAddressLine1Input,
			'streetAddressLine1'
		);
	});

	it('Must select an existing address', async () => {
		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		const {
			addressCountrySelect,
			addressIdSelect,
			addressLocalityInput,
			addressRegionSelect,
			nameInput,
			phoneNumberInput,
			postalCodeInput,
			streetAddressLine1Input,
			streetAddressLine2Input,
			streetAddressLine3Input,
		} = getLocators(renderedComponent);

		const changeAddress = (
			addressId: number,
			addressToCompare: IPostalAddress,
			fieldsDisabled: boolean = true
		) => {
			setFieldValue(addressIdSelect, String(addressId));

			fieldsDisabled
				? expect(addressCountrySelect).toBeDisabled()
				: expect(addressCountrySelect).toBeEnabled();
			expect(addressCountrySelect).toHaveValue(
				addressToCompare.addressCountry || ''
			);
			fieldsDisabled
				? expect(addressLocalityInput).toBeDisabled()
				: expect(addressLocalityInput).toBeEnabled();
			expect(addressLocalityInput).toHaveValue(
				addressToCompare.addressLocality || ''
			);
			expect(addressRegionSelect).toBeDisabled();
			expect(addressRegionSelect).toHaveValue(
				addressToCompare.addressRegion || ''
			);
			fieldsDisabled
				? expect(nameInput).toBeDisabled()
				: expect(nameInput).toBeEnabled();
			expect(nameInput).toHaveValue(addressToCompare.name || '');
			fieldsDisabled
				? expect(phoneNumberInput).toBeDisabled()
				: expect(phoneNumberInput).toBeEnabled();
			expect(phoneNumberInput).toHaveValue(
				addressToCompare.phoneNumber || ''
			);
			fieldsDisabled
				? expect(postalCodeInput).toBeDisabled()
				: expect(postalCodeInput).toBeEnabled();
			expect(postalCodeInput).toHaveValue(
				addressToCompare.postalCode || ''
			);
			fieldsDisabled
				? expect(streetAddressLine1Input).toBeDisabled()
				: expect(streetAddressLine1Input).toBeEnabled();
			expect(streetAddressLine1Input).toHaveValue(
				addressToCompare.streetAddressLine1 || ''
			);
			fieldsDisabled
				? expect(streetAddressLine2Input).toBeDisabled()
				: expect(streetAddressLine2Input).toBeEnabled();
			expect(streetAddressLine2Input).toHaveValue(
				addressToCompare.streetAddressLine2 || ''
			);
			fieldsDisabled
				? expect(streetAddressLine3Input).toBeDisabled()
				: expect(streetAddressLine3Input).toBeEnabled();
			expect(streetAddressLine3Input).toHaveValue(
				addressToCompare.streetAddressLine3 || ''
			);
		};

		await waitFor(() => {
			expect(addressCountrySelect?.options?.length).toBe(3);
			expect(addressIdSelect?.options?.length).toBe(6);
		});

		changeAddress(
			101,
			POSTAL_ADDRESS_DATA.find(
				(address) => address.id === 101
			) as IPostalAddress
		);

		expect(isFormValid).toBeCalledWith(true);

		changeAddress(
			102,
			POSTAL_ADDRESS_DATA.find(
				(address) => address.id === 102
			) as IPostalAddress
		);

		expect(isFormValid).toBeCalledWith(true);

		changeAddress(
			103,
			POSTAL_ADDRESS_DATA.find(
				(address) => address.id === 103
			) as IPostalAddress
		);

		expect(isFormValid).toBeCalledWith(true);

		changeAddress(0, {} as IPostalAddress, false);

		expect(isFormValid).toBeCalledWith(false);
	});

	it('Must preload with specified address', async () => {
		const selectedAddress = POSTAL_ADDRESS_DATA.find(
			(address) => address.id === 101
		) as IPostalAddress;

		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				addressId={selectedAddress.id}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		const {
			addressCountrySelect,
			addressIdSelect,
			addressLocalityInput,
			addressRegionSelect,
			nameInput,
			phoneNumberInput,
			postalCodeInput,
			streetAddressLine1Input,
			streetAddressLine2Input,
			streetAddressLine3Input,
		} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressCountrySelect?.options?.length).toBe(3);
			expect(addressIdSelect?.options?.length).toBe(6);
		});

		expect(addressCountrySelect).toBeDisabled();
		expect(addressCountrySelect).toHaveValue(
			selectedAddress.addressCountry
		);
		expect(addressLocalityInput).toBeDisabled();
		expect(addressLocalityInput).toHaveValue(
			selectedAddress.addressLocality
		);
		expect(addressRegionSelect).toBeDisabled();
		expect(addressRegionSelect).toHaveValue(selectedAddress.addressRegion);
		expect(nameInput).toBeDisabled();
		expect(nameInput).toHaveValue(selectedAddress.name);
		expect(phoneNumberInput).toBeDisabled();
		expect(phoneNumberInput).toHaveValue(selectedAddress.phoneNumber);
		expect(postalCodeInput).toBeDisabled();
		expect(postalCodeInput).toHaveValue(selectedAddress.postalCode);
		expect(streetAddressLine1Input).toBeDisabled();
		expect(streetAddressLine1Input).toHaveValue(
			selectedAddress.streetAddressLine1
		);
		expect(streetAddressLine2Input).toBeDisabled();
		expect(streetAddressLine2Input).toHaveValue(
			selectedAddress.streetAddressLine2
		);
		expect(streetAddressLine3Input).toBeDisabled();
		expect(streetAddressLine3Input).toHaveValue(
			selectedAddress.streetAddressLine3
		);

		expect(isFormValid).toBeCalledWith(true);
	});

	it('Must create a new address', async () => {
		fetchMock.post(
			/headless-admin-user\/.*\/accounts\/\d+\/postal-addresses$/i,
			(): IPostalAddress => {
				return {
					addressCountry: 'United States',
					addressLocality: 'addressLocality',
					addressRegion: 'Alabama',
					addressType: 'shipping',
					externalReferenceCode: '2156-321-321',
					id: 100,
					name: 'name',
					phoneNumber: 'phoneNumber',
					postalCode: 'postalCode',
					primary: false,
					streetAddressLine1: 'streetAddressLine1',
					streetAddressLine2: 'streetAddressLine2',
					streetAddressLine3: 'streetAddressLine3',
				};
			}
		);

		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		const {
			addressCountrySelect,
			addressLocalityInput,
			addressRegionSelect,
			nameInput,
			phoneNumberInput,
			postalCodeInput,
			streetAddressLine1Input,
			streetAddressLine2Input,
			streetAddressLine3Input,
		} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressCountrySelect?.options?.length).toBe(3);
		});

		await setFieldValue(addressCountrySelect, 'United States');
		await setFieldValue(addressLocalityInput, 'addressLocality');
		await setFieldValue(addressRegionSelect, 'Alabama');
		await setFieldValue(nameInput, 'name');
		await setFieldValue(phoneNumberInput, 'phoneNumberInput');
		await setFieldValue(postalCodeInput, 'postalCode');
		await setFieldValue(streetAddressLine1Input, 'streetAddressLine1');
		await setFieldValue(streetAddressLine2Input, 'streetAddressLine2');
		await setFieldValue(streetAddressLine3Input, 'streetAddressLine3');

		const handlerCallbackFunction: Function =
			handleSubmit.mock.calls.pop()[0];
		const handlerCallbackInstance: Function = handlerCallbackFunction();
		const handlerCallbackResult: IPostalAddress =
			await handlerCallbackInstance(new Event(''));
		expect(handlerCallbackResult.id).toBe(100);

		expect(fetchMock.calls().matched[2][1].body).toBe(
			'{"addressType":"shipping","id":0,"primary":false,"addressRegion":"Alabama","addressCountry":"United States","addressLocality":"addressLocality","name":"name","phoneNumber":"phoneNumberInput","postalCode":"postalCode","streetAddressLine1":"streetAddressLine1","streetAddressLine2":"streetAddressLine2","streetAddressLine3":"streetAddressLine3"}'
		);
		expect(fetchMock.calls().matched[2][1].method).toBe('POST');
	});

	it('Must submit selected address', async () => {
		const selectedAddress = POSTAL_ADDRESS_DATA.find(
			(address) => address.id === 102
		) as IPostalAddress;

		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				addressId={selectedAddress.id}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		const {addressCountrySelect} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressCountrySelect?.options?.length).toBe(3);
		});

		const handlerCallbackFunction: Function =
			handleSubmit.mock.calls.pop()[0];
		const handlerCallbackInstance: Function = handlerCallbackFunction();
		const handlerCallbackResult: IPostalAddress =
			await handlerCallbackInstance(new Event(''));
		expect(handlerCallbackResult.id).toBe(selectedAddress.id);
	});

	it('Must display error message if address creation API is not called correctly', async () => {
		fetchMock.post(
			/headless-admin-user\/.*\/accounts\/\d+\/postal-addresses$/i,
			() => {
				return {
					status: 500,
				};
			}
		);
		const spyOnShowError = jest.spyOn(ErrorMessage, 'showError');

		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		const {
			addressCountrySelect,
			addressLocalityInput,
			addressRegionSelect,
			nameInput,
			phoneNumberInput,
			postalCodeInput,
			streetAddressLine1Input,
			streetAddressLine2Input,
			streetAddressLine3Input,
		} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressCountrySelect?.options?.length).toBe(3);
		});

		await setFieldValue(addressCountrySelect, 'United States');
		await setFieldValue(addressLocalityInput, 'addressLocality');
		await setFieldValue(addressRegionSelect, 'Alabama');
		await setFieldValue(nameInput, 'name');
		await setFieldValue(phoneNumberInput, 'phoneNumberInput');
		await setFieldValue(postalCodeInput, 'postalCode');
		await setFieldValue(streetAddressLine1Input, 'streetAddressLine1');
		await setFieldValue(streetAddressLine2Input, 'streetAddressLine2');
		await setFieldValue(streetAddressLine3Input, 'streetAddressLine3');

		const handlerCallbackFunction: Function =
			handleSubmit.mock.calls.pop()[0];
		const handlerCallbackInstance: Function = handlerCallbackFunction();
		const handlerCallbackResult: IPostalAddress =
			await handlerCallbackInstance(new Event(''));

		await waitFor(() => {
			expect(spyOnShowError).toHaveBeenCalledTimes(1);
			expect(handlerCallbackResult.id).toBe(0);
		});
	});

	it('Must hide the subtype field if configuration is not provided', async () => {
		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(2);

		const {addressSubtypeInput} = getLocators(renderedComponent);

		expect(addressSubtypeInput).not.toBeInTheDocument();
	});

	it('Must show the subtype field if configuration is provided', async () => {
		fetchMock.get(
			/headless-admin-list-type\/.*\/list-type-definitions\/by-external-reference-code\/\w+\/list-type-entries/i,
			(): IListTypeEntryAPIResponse => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				};
			}
		);

		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				addressSubtypeConfiguration={{
					billingAndShipping: 'ERCBILLINGANDSHIPPING',
					shipping: 'ERCSHIPPING',
				}}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(3);

		const {addressSubtypeInput} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressSubtypeInput).toBeVisible();
		});
	});

	it('Must show the subtype field disabled if configuration is not provided for specific address type', async () => {
		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				addressSubtypeConfiguration={{
					billingAndShipping: 'ERCBILLINGANDSHIPPING',
				}}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(2);

		const {addressSubtypeInput} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressSubtypeInput).toBeVisible();
			expect(addressSubtypeInput).toBeDisabled();
		});
	});

	it('Must save an empty subtype field when adding a new address', async () => {
		fetchMock.get(
			/headless-admin-list-type\/.*\/list-type-definitions\/by-external-reference-code\/\w+\/list-type-entries/i,
			(): IListTypeEntryAPIResponse => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				};
			}
		);

		fetchMock.post(
			/headless-admin-user\/.*\/accounts\/\d+\/postal-addresses$/i,
			(): IPostalAddress => {
				return {
					addressCountry: 'United States',
					addressLocality: 'addressLocality',
					addressRegion: 'Alabama',
					addressSubtype: '',
					addressType: 'shipping',
					externalReferenceCode: '2156-321-321',
					id: 100,
					name: 'name',
					phoneNumber: 'phoneNumber',
					postalCode: 'postalCode',
					primary: false,
					streetAddressLine1: 'streetAddressLine1',
					streetAddressLine2: 'streetAddressLine2',
					streetAddressLine3: 'streetAddressLine3',
				};
			}
		);

		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				addressSubtypeConfiguration={{shipping: 'ERCSHIPPING'}}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(3);

		const {
			addressCountrySelect,
			addressLocalityInput,
			addressRegionSelect,
			addressSubtypeInput,
			nameInput,
			phoneNumberInput,
			postalCodeInput,
			streetAddressLine1Input,
			streetAddressLine2Input,
			streetAddressLine3Input,
		} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressCountrySelect?.options?.length).toBe(3);
		});

		await setFieldValue(addressCountrySelect, 'United States');
		await setFieldValue(addressLocalityInput, 'addressLocality');
		await setFieldValue(addressRegionSelect, 'Alabama');
		await setFieldValue(addressSubtypeInput, '');
		await setFieldValue(nameInput, 'name');
		await setFieldValue(phoneNumberInput, 'phoneNumberInput');
		await setFieldValue(postalCodeInput, 'postalCode');
		await setFieldValue(streetAddressLine1Input, 'streetAddressLine1');
		await setFieldValue(streetAddressLine2Input, 'streetAddressLine2');
		await setFieldValue(streetAddressLine3Input, 'streetAddressLine3');

		const handlerCallbackFunction: Function =
			handleSubmit.mock.calls.pop()[0];
		const handlerCallbackInstance: Function = handlerCallbackFunction();
		const handlerCallbackResult: IPostalAddress =
			await handlerCallbackInstance(new Event(''));
		expect(handlerCallbackResult.id).toBe(100);

		expect(fetchMock.calls().matched[3][1].body).toBe(
			'{"addressType":"shipping","id":0,"primary":false,"addressRegion":"Alabama","addressCountry":"United States","addressLocality":"addressLocality","name":"name","phoneNumber":"phoneNumberInput","postalCode":"postalCode","streetAddressLine1":"streetAddressLine1","streetAddressLine2":"streetAddressLine2","streetAddressLine3":"streetAddressLine3"}'
		);
		expect(fetchMock.calls().matched[3][1].method).toBe('POST');
	});

	it('Must save the subtype field when adding a new address', async () => {
		fetchMock.get(
			/headless-admin-list-type\/.*\/list-type-definitions\/by-external-reference-code\/\w+\/list-type-entries/i,
			(): IListTypeEntryAPIResponse => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				};
			}
		);

		fetchMock.post(
			/headless-admin-user\/.*\/accounts\/\d+\/postal-addresses$/i,
			(): IPostalAddress => {
				return {
					addressCountry: 'United States',
					addressLocality: 'addressLocality',
					addressRegion: 'Alabama',
					addressSubtype: 'SHIPPING2',
					addressType: 'shipping',
					externalReferenceCode: '2156-321-321',
					id: 100,
					name: 'name',
					phoneNumber: 'phoneNumber',
					postalCode: 'postalCode',
					primary: false,
					streetAddressLine1: 'streetAddressLine1',
					streetAddressLine2: 'streetAddressLine2',
					streetAddressLine3: 'streetAddressLine3',
				};
			}
		);

		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				addressSubtypeConfiguration={{shipping: 'ERSHIPPING'}}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(3);

		const {
			addressCountrySelect,
			addressLocalityInput,
			addressRegionSelect,
			addressSubtypeInput,
			nameInput,
			phoneNumberInput,
			postalCodeInput,
			streetAddressLine1Input,
			streetAddressLine2Input,
			streetAddressLine3Input,
		} = getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressCountrySelect?.options?.length).toBe(3);
		});

		await setFieldValue(addressCountrySelect, 'United States');
		await setFieldValue(addressLocalityInput, 'addressLocality');
		await setFieldValue(addressRegionSelect, 'Alabama');
		await setFieldValue(nameInput, 'name');
		await setFieldValue(phoneNumberInput, 'phoneNumberInput');
		await setFieldValue(postalCodeInput, 'postalCode');
		await setFieldValue(streetAddressLine1Input, 'streetAddressLine1');
		await setFieldValue(streetAddressLine2Input, 'streetAddressLine2');
		await setFieldValue(streetAddressLine3Input, 'streetAddressLine3');

		await setFieldValue(addressSubtypeInput, 'SHIPPING1');
		expect(addressSubtypeInput).toHaveValue('SHIPPING1');

		await setFieldValue(addressSubtypeInput, 'SHIPPING2');
		expect(addressSubtypeInput).toHaveValue('SHIPPING2');

		const handlerCallbackFunction: Function =
			handleSubmit.mock.calls.pop()[0];
		const handlerCallbackInstance: Function = handlerCallbackFunction();
		const handlerCallbackResult: IPostalAddress =
			await handlerCallbackInstance(new Event(''));
		expect(handlerCallbackResult.id).toBe(100);

		expect(fetchMock.calls().matched[3][1].body).toBe(
			'{"addressType":"shipping","id":0,"primary":false,"addressRegion":"Alabama","addressCountry":"United States","addressLocality":"addressLocality","name":"name","phoneNumber":"phoneNumberInput","postalCode":"postalCode","streetAddressLine1":"streetAddressLine1","streetAddressLine2":"streetAddressLine2","streetAddressLine3":"streetAddressLine3","addressSubtype":"SHIPPING2"}'
		);
		expect(fetchMock.calls().matched[3][1].method).toBe('POST');
	});

	it('Must preload subtype field if specified address as it', async () => {
		fetchMock.get(
			/headless-admin-list-type\/.*\/list-type-definitions\/by-external-reference-code\/\w+\/list-type-entries/i,
			(): IListTypeEntryAPIResponse => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				};
			}
		);

		const selectedAddress = POSTAL_ADDRESS_DATA.find(
			(address) => address.id === 105
		) as IPostalAddress;

		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				addressId={selectedAddress.id}
				addressSubtypeConfiguration={{shipping: 'ERCSHIPPING'}}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(3);

		const {addressIdSelect, addressSubtypeInput} =
			getLocators(renderedComponent);

		await waitFor(() => {
			expect(addressIdSelect?.options?.length).toBe(6);
		});

		expect(addressSubtypeInput).toBeDisabled();
		expect(addressSubtypeInput).toHaveValue('SHIPPING 2');

		expect(isFormValid).toBeCalledWith(true);
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
				} as IAddressSubtypeConfiguration;
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
				} as IAddressSubtypeConfiguration;
			})
			.mockReturnValue(() => {
				return {
					items: [
						{id: 100, key: 'SHIPPING1', name: 'SHIPPING 1'},
						{id: 101, key: 'SHIPPING2', name: 'SHIPPING 2'},
					],
				} as IAddressSubtypeConfiguration;
			});

		fetchMock.get(
			/headless-admin-list-type\/.*\/list-type-definitions\/by-external-reference-code\/\w+\/list-type-entries/i,
			mockFetch
		);

		const renderedComponent = render(
			<AddressSelector
				accountId={10}
				addressSubtypeConfiguration={{
					billingAndShipping: 'ERCBILLINGANDSHIPPING',
					shipping: 'ERCSHIPPING',
				}}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={handleSubmit}
				setIsFormValid={isFormValid}
			/>
		);

		expect(fetchMock.calls().matched.length).toBe(3);

		const {addressIdSelect, addressSubtypeInput} =
			getLocators(renderedComponent);

		const changeAddress = async (addressId: number, subtype: string) => {
			setFieldValue(addressIdSelect, String(addressId));

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
		expect(isFormValid).toBeCalledWith(true);

		await changeAddress(106, 'BILLING AND SHIPPING 2');

		expect(fetchMock.calls().matched.length).toBe(4);
		expect(isFormValid).toBeCalledWith(true);

		await changeAddress(101, '');

		expect(fetchMock.calls().matched.length).toBe(4);
		expect(isFormValid).toBeCalledWith(true);
	});
});

describe('AddressSelector API errors', () => {
	const spyOnShowError = jest.spyOn(ErrorMessage, 'showError');

	beforeEach(async () => {
		fetchMock.get(/headless-admin-address\/.*\/countries/i, () => {
			return {
				status: 500,
			};
		});

		fetchMock.get(
			/headless-admin-user\/.*\/accounts\/\d+\/postal-addresses$/i,
			() => {
				return {
					status: 500,
				};
			}
		);
	});

	afterEach(() => {
		fetchMock.restore();
		jest.clearAllMocks();

		cleanup();
	});

	it('Must display error message if api are not called correctly', async () => {
		render(
			<AddressSelector
				accountId={10}
				setHandleNameChange={jest.fn()}
				setHandleSubmit={jest.fn()}
				setIsFormValid={jest.fn()}
			/>
		);

		await waitFor(() => {
			expect(spyOnShowError).toHaveBeenCalledTimes(2);
		});
	});
});
