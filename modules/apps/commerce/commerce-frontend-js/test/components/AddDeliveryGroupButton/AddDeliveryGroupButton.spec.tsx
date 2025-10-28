/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore

import fetchMock from 'fetch-mock';

import {
	ICountryAPIResponse,
	IPostalAddressAPIResponse,
} from '../../../src/main/resources/META-INF/resources/components/multishipping/Types';

import '@testing-library/jest-dom';
import {
	RenderResult,
	cleanup,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import React from 'react';
import {act} from 'react-dom/test-utils';

import AddDeliveryGroupButton from '../../../src/main/resources/META-INF/resources/components/multishipping/AddDeliveryGroupButton';
import {setFieldValue} from '../../tests_utilities/utils.spec';

interface ILocators {
	addDeliveryGroupButton: HTMLButtonElement;
	addressIdSelect: HTMLSelectElement;
	cancelButton: HTMLButtonElement;
	deliveryGroupNameInput: HTMLInputElement;
	saveButton: HTMLButtonElement;
}

function getLocators(renderedComponent: RenderResult): ILocators {
	return {
		addDeliveryGroupButton: renderedComponent.queryByRole('button', {
			name: 'add-delivery-group',
		}),
		addressIdSelect: renderedComponent.queryByLabelText('choose-x'),
		cancelButton: renderedComponent.queryByRole('button', {name: 'cancel'}),
		deliveryGroupNameInput:
			renderedComponent.queryByLabelText('group-name'),
		saveButton: renderedComponent.queryByRole('button', {name: 'save'}),
	} as ILocators;
}

describe('AddDeliveryGroupButton', () => {
	const handleSubmit = jest.fn();

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
							],
							title_i18n: {
								en_US: 'United States',
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
					items: [
						{
							addressCountry: 'United States',
							addressLocality: 'addressLocality1',
							addressRegion: 'Alabama',
							addressType: 'billing-and-shipping',
							externalReferenceCode:
								'71061669-ba97-943c-70a3-96cdc0c8305a',
							id: 101,
							name: 'name1',
							phoneNumber: 'phoneNumber1',
							postalCode: 'postalCode1',
							primary: false,
							streetAddressLine1: 'streetAddressLine11',
							streetAddressLine2: 'streetAddressLine21',
							streetAddressLine3: 'streetAddressLine31',
						},
					],
				};
			}
		);
	});

	afterEach(() => {
		fetchMock.restore();
		jest.clearAllMocks();

		cleanup();
	});

	it('Must check modal opening', async () => {
		const renderedComponent = render(
			<AddDeliveryGroupButton
				accountId={10}
				handleSubmit={handleSubmit}
			/>
		);

		const {addDeliveryGroupButton} = getLocators(renderedComponent);

		await act(async () => {
			addDeliveryGroupButton.click();
		});

		await waitFor(() => {
			const {saveButton} = getLocators(renderedComponent);

			expect(saveButton).toBeVisible();
		});

		const {saveButton} = getLocators(renderedComponent);

		expect(saveButton).toBeVisible();
	});

	it('Must close modal on cancel', async () => {
		const renderedComponent = render(
			<AddDeliveryGroupButton
				accountId={10}
				handleSubmit={handleSubmit}
			/>
		);

		const {addDeliveryGroupButton} = getLocators(renderedComponent);

		await act(async () => {
			addDeliveryGroupButton.click();
		});

		await waitFor(() => {
			const {cancelButton} = getLocators(renderedComponent);

			expect(cancelButton).toBeVisible();
		});

		const {cancelButton} = getLocators(renderedComponent);

		await act(async () => {
			cancelButton.click();
		});

		await waitFor(() => {
			expect(screen.queryByText('cancel')).not.toBeInTheDocument();
		});
	});

	it('Must close modal on submit', async () => {
		const renderedComponent = render(
			<AddDeliveryGroupButton
				accountId={10}
				handleSubmit={handleSubmit}
			/>
		);

		const {addDeliveryGroupButton} = getLocators(renderedComponent);

		await act(async () => {
			addDeliveryGroupButton.click();
		});

		await waitFor(() => {
			const {saveButton} = getLocators(renderedComponent);

			expect(saveButton).toBeVisible();
		});

		const {addressIdSelect, deliveryGroupNameInput, saveButton} =
			getLocators(renderedComponent);

		await setFieldValue(addressIdSelect, String(101));
		await setFieldValue(deliveryGroupNameInput, 'deliveryGroupName');

		await act(async () => {
			saveButton.click();
		});

		expect(handleSubmit).toBeCalledTimes(1);
		await waitFor(() => {
			expect(screen.queryByText('cancel')).not.toBeInTheDocument();
		});
	});
});
