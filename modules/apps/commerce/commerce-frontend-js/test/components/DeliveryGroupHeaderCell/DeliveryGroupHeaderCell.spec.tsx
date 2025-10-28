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
import {RenderResult, cleanup, render, waitFor} from '@testing-library/react';
import React from 'react';
import {act} from 'react-dom/test-utils';

import DeliveryGroupHeaderCell from '../../../src/main/resources/META-INF/resources/components/multishipping/DeliveryGroupHeaderCell';
import {setFieldValue} from '../../tests_utilities/utils.spec';

interface ILocators {
	actionsButton: HTMLButtonElement;
	deleteMenuItem: HTMLButtonElement;
	deliveryGroupNameInput: HTMLInputElement;
	editMenuItem: HTMLButtonElement;
	saveButton: HTMLButtonElement;
}

function getLocators(renderedComponent: RenderResult): ILocators {
	return {
		actionsButton: renderedComponent.queryByRole('button', {
			name: 'actions',
		}),
		deleteMenuItem: renderedComponent.queryByRole('menuitem', {
			name: 'delete',
		}),
		deliveryGroupNameInput:
			renderedComponent.queryByLabelText('group-name'),
		editMenuItem: renderedComponent.queryByRole('menuitem', {name: 'edit'}),
		saveButton: renderedComponent.queryByRole('button', {name: 'save'}),
	} as ILocators;
}

describe('DeliveryGroupHeaderCell', () => {
	const handleDelete = jest.fn();
	const handleSubmit = jest.fn();

	beforeEach(async () => {
		(window as any).Liferay = {
			...(window as any).Liferay,
			CustomDialogs: {},
		};

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

	it('Must display name, date and action menu', async () => {
		const renderedComponent = render(
			<DeliveryGroupHeaderCell
				accountId={10}
				deliveryGroup={{
					addressId: 101,
					deliveryDate: '2024-12-12',
					id: 100,
					name: 'deliveryGroupName',
				}}
				handleDeleteDeliveryGroup={handleDelete}
				handleSubmitDeliveryGroup={handleSubmit}
			/>
		);

		const {actionsButton, saveButton} = getLocators(renderedComponent);

		expect(renderedComponent.getByText('deliveryGroupName')).toBeVisible();
		expect(renderedComponent.getByText('12/12/24')).toBeVisible();

		await act(async () => {
			actionsButton.click();
		});

		const {deleteMenuItem, editMenuItem} = getLocators(renderedComponent);

		expect(saveButton).not.toBeInTheDocument();
		expect(editMenuItem).toBeVisible();
		expect(deleteMenuItem).toBeVisible();
	});

	it('Must delete the delivery group', async () => {
		jest.spyOn(window, 'confirm')
			.mockImplementationOnce(() => false)
			.mockImplementation(() => true);

		const deliveryGroup = {
			addressId: 101,
			deliveryDate: '2024-12-12',
			id: 100,
			name: 'deliveryGroupName',
		};

		const renderedComponent = render(
			<DeliveryGroupHeaderCell
				accountId={10}
				deliveryGroup={deliveryGroup}
				handleDeleteDeliveryGroup={handleDelete}
				handleSubmitDeliveryGroup={handleSubmit}
			/>
		);

		const {actionsButton} = getLocators(renderedComponent);

		await act(async () => {
			actionsButton.click();
		});

		const {deleteMenuItem} = getLocators(renderedComponent);

		await act(async () => {
			deleteMenuItem.click();
		});

		expect(window.confirm).toHaveBeenCalled();
		expect(handleDelete).not.toBeCalled();

		await act(async () => {
			deleteMenuItem.click();
		});

		expect(window.confirm).toBeCalled();
		expect(handleDelete).toBeCalledWith(deliveryGroup);
	});

	it('Must update the delivery group', async () => {
		const deliveryGroup = {
			addressId: 101,
			deliveryDate: '2024-12-12',
			id: 100,
			name: 'deliveryGroup',
		};

		const renderedComponent = render(
			<DeliveryGroupHeaderCell
				accountId={10}
				deliveryGroup={deliveryGroup}
				handleDeleteDeliveryGroup={handleDelete}
				handleSubmitDeliveryGroup={handleSubmit}
			/>
		);

		const {actionsButton} = getLocators(renderedComponent);

		await act(async () => {
			actionsButton.click();
		});

		const {editMenuItem} = getLocators(renderedComponent);

		await act(async () => {
			editMenuItem.click();
		});

		await waitFor(() => {
			const {saveButton} = getLocators(renderedComponent);

			expect(saveButton).toBeVisible();
		});

		const {deliveryGroupNameInput, saveButton} =
			getLocators(renderedComponent);

		await setFieldValue(deliveryGroupNameInput, 'deliveryGroupName');

		await act(async () => {
			saveButton.click();
		});

		expect(handleSubmit).toBeCalledWith({
			address: {
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
			addressId: 101,
			deliveryDate: '2024-12-12',
			id: 100,
			name: 'deliveryGroupName',
		});
	});

	it('Must disable the action menu and the header click', async () => {
		const renderedComponent = render(
			<DeliveryGroupHeaderCell
				accountId={10}
				deliveryGroup={{
					addressId: 101,
					deliveryDate: '2024-12-12',
					id: 100,
					name: 'deliveryGroupName',
				}}
				disabled={true}
				handleDeleteDeliveryGroup={handleDelete}
				handleSubmitDeliveryGroup={handleSubmit}
			/>
		);

		const {actionsButton} = getLocators(renderedComponent);
		const deliveryGroupName =
			renderedComponent.getByText('deliveryGroupName');

		expect(deliveryGroupName).toBeVisible();
		expect(renderedComponent.getByText('12/12/24')).toBeVisible();
		expect(actionsButton).toBeDisabled();

		await act(async () => {
			deliveryGroupName.click();
		});

		const {saveButton} = getLocators(renderedComponent);

		expect(saveButton).not.toBeInTheDocument();
	});

	it('Header should be clickable', async () => {
		const renderedComponent = render(
			<DeliveryGroupHeaderCell
				accountId={10}
				deliveryGroup={{
					addressId: 101,
					deliveryDate: '2024-12-12',
					id: 100,
					name: 'deliveryGroupName',
				}}
				handleDeleteDeliveryGroup={handleDelete}
				handleSubmitDeliveryGroup={handleSubmit}
			/>
		);

		const deliveryGroupName =
			renderedComponent.getByText('deliveryGroupName');

		expect(deliveryGroupName).toBeVisible();
		expect(renderedComponent.getByText('12/12/24')).toBeVisible();

		await act(async () => {
			deliveryGroupName.click();
		});

		await waitFor(() => {
			const {saveButton} = getLocators(renderedComponent);

			expect(saveButton).toBeVisible();
		});
	});
});
