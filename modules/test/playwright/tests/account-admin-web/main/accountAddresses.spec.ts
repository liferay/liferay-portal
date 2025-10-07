/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {serverAdministrationPageTest} from '../../../fixtures/serverAdministrationPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {nextPage, setItemsPerPage} from '../../../utils/pagination';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	accountsPagesTest,
	apiHelpersTest,
	applicationsMenuPageTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35914': {enabled: true},
	}),
	loginTest(),
	usersAndOrganizationsPagesTest,
	serverAdministrationPageTest
);

test(
	'Can add an address to an account',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
	}) => {
		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});
		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		let address = {
			city: getRandomString(),
			country: 'United States',
			countryId: '0',
			name: getRandomString(),
			postalCode: String(getRandomInt()),
			region: 'Alabama',
			regionId: '0',
			street1: getRandomString(),
		};

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account1.name).click();
		await editAccountPage.addressesTab.click();
		await accountAddressesPage.addressesTable.newButton.click();

		address = {
			...address,
			...(await editAccountAddressPage.addAddress(address)),
		};

		await expect(
			accountAddressesPage.addressesTable.valueLink(address.name)
		).toBeVisible();

		await accountAddressesPage.addressesTable
			.valueLink(address.name)
			.click();

		await expect(editAccountAddressPage.cityInput).toHaveValue(
			address.city
		);
		await expect(editAccountAddressPage.countryInput).toHaveValue(
			address.countryId
		);
		await expect(editAccountAddressPage.nameInput).toHaveValue(
			address.name
		);
		await expect(editAccountAddressPage.postalCodeInput).toHaveValue(
			address.postalCode
		);
		await expect(editAccountAddressPage.regionInput).toHaveValue(
			address.regionId
		);
		await expect(editAccountAddressPage.street1Input).toHaveValue(
			address.street1
		);

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account2.name).click();
		await editAccountPage.addressesTab.click();

		await expect(
			accountAddressesPage.addressesTable.valueLink(address.name)
		).toHaveCount(0);
	}
);

test(
	'Can paginate the addresses of an account',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
	}) => {
		test.setTimeout(120000);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		const addresses: {name: string}[] = [];

		for (let i = 0; i < 21; i++) {
			addresses.push({name: `Address ${String(i).padStart(2, '0')}`});
		}

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();

		for (const address of addresses) {
			await expect(async () => {
				await accountAddressesPage.addressesTable.newButton.click();

				await expect(editAccountAddressPage.nameInput).toBeVisible();
			}).toPass();

			await editAccountAddressPage.addAddress(address);
		}

		await setItemsPerPage(page, 20);

		for (const [index, address] of addresses.entries()) {
			if (index < 20) {
				await expect(
					accountAddressesPage.addressesTable.valueLink(address.name)
				).toBeVisible();
			}
			else {
				await expect(
					accountAddressesPage.addressesTable.valueLink(address.name)
				).toHaveCount(0);
			}
		}

		await nextPage(page);

		for (const [index, address] of addresses.entries()) {
			if (index < 20) {
				await expect(
					accountAddressesPage.addressesTable.valueLink(address.name)
				).toHaveCount(0);
			}
			else {
				await expect(
					accountAddressesPage.addressesTable.valueLink(address.name)
				).toBeVisible();
			}
		}
	}
);

test(
	'Can update the address type of an account',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		let address = {
			city: getRandomString(),
			country: 'United States',
			countryId: '0',
			name: getRandomString(),
			postalCode: String(getRandomInt()),
			region: 'Alabama',
			regionId: '0',
			street1: getRandomString(),
			type: 'Billing',
			typeId: '0',
		};

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();
		await accountAddressesPage.addressesTable.newButton.click();

		address = {
			...address,
			...(await editAccountAddressPage.addAddress(address)),
		};

		await expect(
			accountAddressesPage.addressesTable.valueLink(address.name)
		).toBeVisible();

		await accountAddressesPage.addressesTable
			.valueLink(address.name)
			.click();

		await expect(editAccountAddressPage.typeInput).toHaveValue(
			address.typeId
		);

		await editAccountAddressPage.typeInput.selectOption({
			label: 'Shipping',
		});

		address.typeId = await editAccountAddressPage.typeInput.inputValue();

		await editAccountAddressPage.saveButton.click();

		await waitForAlert(page);

		await accountAddressesPage.addressesTable
			.valueLink(address.name)
			.click();

		await expect(editAccountAddressPage.typeInput).toHaveValue(
			address.typeId
		);

		await editAccountAddressPage.typeInput.selectOption({
			label: 'Billing and Shipping',
		});

		address.typeId = await editAccountAddressPage.typeInput.inputValue();

		await editAccountAddressPage.saveButton.click();

		await waitForAlert(page);

		await accountAddressesPage.addressesTable
			.valueLink(address.name)
			.click();

		await expect(editAccountAddressPage.typeInput).toHaveValue(
			address.typeId
		);
	}
);

test(
	'Can delete an address of an account',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
	}) => {
		page.on('dialog', (dialog) => {
			dialog.accept().catch(() => {});
		});

		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		const address = {
			name: getRandomString(),
		};

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();
		await accountAddressesPage.addressesTable.newButton.click();

		await editAccountAddressPage.addAddress(address);

		await expect(
			accountAddressesPage.addressesTable.valueLink(address.name)
		).toBeVisible();

		await (
			await accountAddressesPage.addressesTable.rowActions(address.name)
		).click();
		await accountAddressesPage.deleteButton.click();

		await waitForAlert(page);
	}
);

test(
	'Can delete addresses of an account in bulk',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
	}) => {
		page.on('dialog', (dialog) => {
			dialog.accept().catch(() => {});
		});

		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		const addresses = [
			{
				name: getRandomString(),
			},
			{
				name: getRandomString(),
			},
			{
				name: getRandomString(),
			},
		];

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();

		for (const address of addresses) {
			await accountAddressesPage.addressesTable.newButton.click();

			await editAccountAddressPage.addAddress(address);
		}

		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[0].name)
		).toBeVisible();
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[1].name)
		).toBeVisible();
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[2].name)
		).toBeVisible();

		await (
			await accountAddressesPage.addressesTable.rowCheckbox(
				addresses[0].name
			)
		).check();
		await (
			await accountAddressesPage.addressesTable.rowCheckbox(
				addresses[1].name
			)
		).check();

		await accountAddressesPage.deleteButton.click();

		await waitForAlert(page);

		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[0].name)
		).toHaveCount(0);
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[1].name)
		).toHaveCount(0);
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[2].name)
		).toBeVisible();
	}
);

test(
	'Can update the address of an account',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		let address = {
			city: getRandomString(),
			country: 'United States',
			countryId: '0',
			name: getRandomString(),
			postalCode: String(getRandomInt()),
			region: 'Alabama',
			regionId: '0',
			street1: getRandomString(),
			type: 'Billing',
			typeId: '0',
		};

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();
		await accountAddressesPage.addressesTable.newButton.click();

		address = {
			...address,
			...(await editAccountAddressPage.addAddress(address)),
		};

		await expect(
			accountAddressesPage.addressesTable.valueLink(address.name)
		).toBeVisible();

		await accountAddressesPage.addressesTable
			.valueLink(address.name)
			.click();

		const updatedAddress = {
			city: getRandomString(),
			country: 'Italy',
			countryId: '0',
			description: getRandomString(),
			name: getRandomString(),
			phoneNumber: getRandomString(),
			postalCode: String(getRandomInt()),
			region: 'Milano',
			regionId: '0',
			street1: getRandomString(),
			type: 'Shipping',
			typeId: '0',
		};

		await editAccountAddressPage.nameInput.fill(updatedAddress.name);
		await editAccountAddressPage.descriptionInput.fill(
			updatedAddress.description
		);
		await editAccountAddressPage.countryInput.selectOption({
			label: updatedAddress.country,
		});
		await editAccountAddressPage.regionInput.selectOption({
			label: updatedAddress.region,
		});
		await editAccountAddressPage.cityInput.fill(updatedAddress.city);
		await editAccountAddressPage.street1Input.fill(updatedAddress.street1);
		await editAccountAddressPage.postalCodeInput.fill(
			String(updatedAddress.postalCode)
		);
		await editAccountAddressPage.phoneNumberInput.fill(
			String(updatedAddress.phoneNumber)
		);
		await editAccountAddressPage.typeInput.selectOption({
			label: updatedAddress.type,
		});

		updatedAddress.countryId =
			await editAccountAddressPage.countryInput.inputValue();
		updatedAddress.regionId =
			await editAccountAddressPage.regionInput.inputValue();
		updatedAddress.typeId =
			await editAccountAddressPage.typeInput.inputValue();

		await editAccountAddressPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			accountAddressesPage.addressesTable.valueLink(address.name)
		).toHaveCount(0);
		await expect(
			accountAddressesPage.addressesTable.valueLink(updatedAddress.name)
		).toBeVisible();

		await accountAddressesPage.addressesTable
			.valueLink(updatedAddress.name)
			.click();

		await expect(editAccountAddressPage.nameInput).toHaveValue(
			updatedAddress.name
		);
		await expect(editAccountAddressPage.descriptionInput).toHaveValue(
			updatedAddress.description
		);
		await expect(editAccountAddressPage.countryInput).toHaveValue(
			updatedAddress.countryId
		);
		await expect(editAccountAddressPage.regionInput).toHaveValue(
			updatedAddress.regionId
		);
		await expect(editAccountAddressPage.cityInput).toHaveValue(
			updatedAddress.city
		);
		await expect(editAccountAddressPage.street1Input).toHaveValue(
			updatedAddress.street1
		);
		await expect(editAccountAddressPage.postalCodeInput).toHaveValue(
			updatedAddress.postalCode
		);
		await expect(editAccountAddressPage.phoneNumberInput).toHaveValue(
			updatedAddress.phoneNumber
		);
		await expect(editAccountAddressPage.typeInput).toHaveValue(
			updatedAddress.typeId
		);
	}
);

test(
	'Can search / filter an account address',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		const addresses = [
			{
				city: 'Dalian',
				country: 'China',
				name: 'Liferay Dalian Software Co., Ltd.',
				phoneNumber: '+86 (0)411 88120855',
				postalCode: String(116023),
				region: 'Liaoning Sheng',
				street1: '537 Huangpu Road Taide Building,',
				type: 'Billing',
			},
			{
				name: getRandomString(),
				type: 'Shipping',
			},
			{
				name: getRandomString(),
				type: 'Billing and Shipping',
			},
		];

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();

		for (const address of addresses) {
			await accountAddressesPage.addressesTable.newButton.click();

			await editAccountAddressPage.addAddress(address);
		}

		await accountAddressesPage.addressesTable.search(getRandomString());

		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[0].name)
		).toHaveCount(0);
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[1].name)
		).toHaveCount(0);
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[2].name)
		).toHaveCount(0);

		await accountAddressesPage.addressesTable.search(addresses[0].name);

		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[0].name)
		).toBeVisible();
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[1].name)
		).toHaveCount(0);
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[2].name)
		).toHaveCount(0);

		await accountAddressesPage.addressesTable.search(addresses[1].name);

		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[0].name)
		).toHaveCount(0);
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[1].name)
		).toBeVisible();
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[2].name)
		).toHaveCount(0);

		await accountAddressesPage.addressesTable.search('');

		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[0].name)
		).toBeVisible();
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[1].name)
		).toBeVisible();
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[2].name)
		).toBeVisible();

		for (const searchTerm of [
			'Dalian',
			'China',
			'Liaoning',
			'Liferay',
			'116023',
		]) {
			await accountAddressesPage.addressesTable.search(searchTerm);

			await expect(
				accountAddressesPage.addressesTable.valueLink(addresses[0].name)
			).toBeVisible();
		}

		await accountAddressesPage.addressesTable.search('');

		await accountAddressesPage.addressesTable.filterButton.click();
		await accountAddressesPage.addressesTable
			.filterMenuItem('Billing')
			.click();

		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[0].name)
		).toBeVisible();
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[1].name)
		).toHaveCount(0);
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[2].name)
		).toBeVisible();

		await accountAddressesPage.addressesTable.filterButton.click();
		await accountAddressesPage.addressesTable
			.filterMenuItem('Shipping')
			.click();

		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[0].name)
		).toHaveCount(0);
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[1].name)
		).toBeVisible();
		await expect(
			accountAddressesPage.addressesTable.valueLink(addresses[2].name)
		).toBeVisible();
	}
);

test(
	'Can set account default addresses',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountDefaultAddressSelectorPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		const addresses = [
			{
				name: getRandomString(),
				type: 'Billing',
			},
			{
				name: getRandomString(),
				type: 'Shipping',
			},
			{
				name: getRandomString(),
				type: 'Billing and Shipping',
			},
		];

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();

		for (const address of addresses) {
			await accountAddressesPage.addressesTable.newButton.click();

			await editAccountAddressPage.addAddress(address);
		}

		await editAccountPage.detailsTab.click();

		await expect(async () => {
			await editAccountPage.setBillingDefaultAddressButton.click();
			await accountDefaultAddressSelectorPage.setDefaultAddress(
				addresses[0].name,
				'Billing'
			);
		}).toPass();

		await expect(
			editAccountPage.defaultBillingAddress(addresses[0].name)
		).toBeVisible();

		await editAccountPage.setShippingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage.setDefaultAddress(
			addresses[1].name,
			'Shipping'
		);

		await expect(
			editAccountPage.defaultShippingAddress(addresses[1].name)
		).toBeVisible();

		await editAccountPage.setBillingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage.setDefaultAddress(
			addresses[2].name,
			'Billing'
		);

		await expect(
			editAccountPage.defaultBillingAddress(addresses[2].name)
		).toBeVisible();

		await editAccountPage.setShippingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage.setDefaultAddress(
			addresses[2].name,
			'Shipping'
		);

		await expect(
			editAccountPage.defaultShippingAddress(addresses[2].name)
		).toBeVisible();
	}
);

test(
	'Only the chosen address type displays when setting default shipping and billing addresses',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountDefaultAddressSelectorPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		const addresses = [
			{
				name: getRandomString(),
				type: 'Billing',
			},
			{
				name: getRandomString(),
				type: 'Shipping',
			},
			{
				name: getRandomString(),
				type: 'Billing and Shipping',
			},
		];

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();

		for (const address of addresses) {
			await accountAddressesPage.addressesTable.newButton.click();

			await editAccountAddressPage.addAddress(address);
		}

		await editAccountPage.detailsTab.click();

		await expect(async () => {
			await editAccountPage.setBillingDefaultAddressButton.click();

			await expect(
				accountDefaultAddressSelectorPage.selectAddressInput(
					addresses[0].name,
					'Billing'
				)
			).toBeVisible();
			await expect(
				accountDefaultAddressSelectorPage.selectAddressInput(
					addresses[1].name,
					'Billing'
				)
			).toHaveCount(0);
			await expect(
				accountDefaultAddressSelectorPage.selectAddressInput(
					addresses[2].name,
					'Billing'
				)
			).toBeVisible();
			await expect(
				accountDefaultAddressSelectorPage.filterButton('Billing')
			).toHaveCount(0);
		}).toPass();

		await page.reload();

		await expect(async () => {
			await editAccountPage.setShippingDefaultAddressButton.click();

			await expect(
				accountDefaultAddressSelectorPage.selectAddressInput(
					addresses[0].name,
					'Shipping'
				)
			).toHaveCount(0);
			await expect(
				accountDefaultAddressSelectorPage.selectAddressInput(
					addresses[1].name,
					'Shipping'
				)
			).toBeVisible();
			await expect(
				accountDefaultAddressSelectorPage.selectAddressInput(
					addresses[2].name,
					'Shipping'
				)
			).toBeVisible();
			await expect(
				accountDefaultAddressSelectorPage.filterButton('Shipping')
			).toHaveCount(0);
		}).toPass();
	}
);

test(
	'A default account address can be removed',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountDefaultAddressSelectorPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		const addresses = [
			{
				name: getRandomString(),
				type: 'Billing',
			},
			{
				name: getRandomString(),
				type: 'Shipping',
			},
			{
				name: getRandomString(),
				type: 'Billing and Shipping',
			},
		];

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();

		for (const address of addresses) {
			await accountAddressesPage.addressesTable.newButton.click();

			await editAccountAddressPage.addAddress(address);
		}

		await editAccountPage.detailsTab.click();
		await editAccountPage.setBillingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage.setDefaultAddress(
			addresses[0].name,
			'Billing'
		);

		await expect(
			editAccountPage.defaultBillingAddress(addresses[0].name)
		).toBeVisible();

		await editAccountPage.setShippingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage.setDefaultAddress(
			addresses[1].name,
			'Shipping'
		);

		await expect(
			editAccountPage.defaultShippingAddress(addresses[1].name)
		).toBeVisible();

		await page.reload();

		await expect(
			editAccountPage.defaultBillingAddress(addresses[0].name)
		).toBeVisible();
		await expect(
			editAccountPage.defaultShippingAddress(addresses[1].name)
		).toBeVisible();

		await editAccountPage.removeBillingDefaultAddressButton.click();

		await waitForAlert(page);

		await expect(
			editAccountPage.defaultBillingAddress(addresses[0].name)
		).toHaveCount(0);
		await expect(
			editAccountPage.defaultShippingAddress(addresses[1].name)
		).toBeVisible();

		await editAccountPage.removeShippingDefaultAddressButton.click();

		await waitForAlert(page);

		await expect(
			editAccountPage.defaultBillingAddress(addresses[0].name)
		).toHaveCount(0);
		await expect(
			editAccountPage.defaultShippingAddress(addresses[1].name)
		).toHaveCount(0);
	}
);

test(
	'A new address can be added via Set Default Address',
	{tag: ['@LPD-46415']},
	async ({
		accountAddressesPage,
		accountDefaultAddressSelectorPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		let address = {
			city: getRandomString(),
			country: 'United States',
			countryId: '0',
			name: getRandomString(),
			postalCode: String(getRandomInt()),
			region: 'Alabama',
			regionId: '0',
			street1: getRandomString(),
		};

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();

		await page.waitForTimeout(100);

		await editAccountPage.setBillingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage
			.addAddressButton('Billing')
			.click();

		await expect(
			editAccountAddressPage.typeInput.locator('option', {
				hasText: /^\s*Billing\s*$/,
			})
		).toHaveCount(1);
		await expect(
			editAccountAddressPage.typeInput.locator('option', {
				hasText: /^\s*Billing and Shipping\s*$/,
			})
		).toHaveCount(1);
		await expect(
			editAccountAddressPage.typeInput.locator('option', {
				hasText: /^\s*Shipping\s*$/,
			})
		).toHaveCount(0);

		address = {
			...address,
			...(await editAccountAddressPage.addAddress({
				...address,
				type: 'Billing',
			})),
		};

		await expect(
			editAccountPage.defaultBillingAddress(address.name)
		).toBeVisible();

		await editAccountPage.addressesTab.click();

		await expect(
			accountAddressesPage.addressesTable.valueLink(address.name)
		).toBeVisible();

		await editAccountPage.detailsTab.click();

		await editAccountPage.setShippingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage
			.addAddressButton('Shipping')
			.click();

		await expect(
			editAccountAddressPage.typeInput.locator('option', {
				hasText: /^\s*Billing\s*$/,
			})
		).toHaveCount(0);
		await expect(
			editAccountAddressPage.typeInput.locator('option', {
				hasText: /^\s*Billing and Shipping\s*$/,
			})
		).toHaveCount(1);
		await expect(
			editAccountAddressPage.typeInput.locator('option', {
				hasText: /^\s*Shipping\s*$/,
			})
		).toHaveCount(1);

		address.name = getRandomString();

		address = {
			...address,
			...(await editAccountAddressPage.addAddress({
				...address,
				type: 'Shipping',
			})),
		};

		await expect(
			editAccountPage.defaultShippingAddress(address.name)
		).toBeVisible();

		await editAccountPage.addressesTab.click();

		await expect(
			accountAddressesPage.addressesTable.valueLink(address.name)
		).toBeVisible();
	}
);

test(
	'Can add an address to an account with subtype',
	{tag: '@LPD-51453'},
	async ({
		accountAddressesPage,
		accountInstanceSettingsAccountAddressSubtypePage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
	}) => {
		test.setTimeout(120000);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		const {
			billingAndShippingListTypeDefinition,
			billingAndShippingListTypeEntry,
			billingListTypeDefinition,
			billingListTypeEntry,
			shippingListTypeDefinition,
			shippingListTypeEntry,
		} =
			await accountInstanceSettingsAccountAddressSubtypePage.initAddressSubtypePicklists(
				apiHelpers
			);

		await accountInstanceSettingsAccountAddressSubtypePage.setAddressSubtypeExternalReferenceCodes(
			billingListTypeDefinition.name,
			billingAndShippingListTypeDefinition.name,
			shippingListTypeDefinition.name
		);

		const billingAddress = {
			city: getRandomString(),
			country: 'United States',
			countryId: '0',
			name: getRandomString(),
			postalCode: String(getRandomInt()),
			region: 'Alabama',
			regionId: '0',
			street1: getRandomString(),
			subtype: billingListTypeEntry.key,
			type: 'Billing',
		};
		const billingAndShippingAddress = {
			city: getRandomString(),
			country: 'United States',
			countryId: '0',
			name: getRandomString(),
			postalCode: String(getRandomInt()),
			region: 'Alabama',
			regionId: '0',
			street1: getRandomString(),
			subtype: billingAndShippingListTypeEntry.key,
			type: 'Billing and Shipping',
		};
		const shippingAddress = {
			city: getRandomString(),
			country: 'United States',
			countryId: '0',
			name: getRandomString(),
			postalCode: String(getRandomInt()),
			region: 'Alabama',
			regionId: '0',
			street1: getRandomString(),
			subtype: shippingListTypeEntry.key,
			type: 'Shipping',
		};

		try {
			await accountsPage.goto();
			await accountsPage.accountsTable.valueLink(account.name).click();
			await editAccountPage.addressesTab.click();
			await accountAddressesPage.addressesTable.newButton.click();
			await editAccountAddressPage.addAddress(billingAddress);

			await expect(
				accountAddressesPage.addressesTable.valueLink(
					billingAddress.name
				)
			).toBeVisible();
			await expect(
				await accountAddressesPage.rowSubtypeCell(
					billingAddress.name,
					billingListTypeEntry.key
				)
			).toHaveCount(1);

			await accountAddressesPage.addressesTable
				.valueLink(billingAddress.name)
				.click();

			await expect(editAccountAddressPage.subtypeInput).toHaveValue(
				new RegExp(billingAddress.subtype, 'i')
			);

			await editAccountAddressPage.backButton.click();
			await accountAddressesPage.addressesTable.newButton.click();
			await editAccountAddressPage.addAddress(billingAndShippingAddress);

			await expect(
				accountAddressesPage.addressesTable.valueLink(
					billingAndShippingAddress.name
				)
			).toBeVisible();
			await expect(
				await accountAddressesPage.rowSubtypeCell(
					billingAndShippingAddress.name,
					billingAndShippingListTypeEntry.key
				)
			).toHaveCount(1);

			await accountAddressesPage.addressesTable
				.valueLink(billingAndShippingAddress.name)
				.click();

			await expect(editAccountAddressPage.subtypeInput).toHaveValue(
				new RegExp(billingAndShippingAddress.subtype, 'i')
			);

			await editAccountAddressPage.backButton.click();
			await accountAddressesPage.addressesTable.newButton.click();
			await editAccountAddressPage.addAddress(shippingAddress);

			await expect(
				accountAddressesPage.addressesTable.valueLink(
					shippingAddress.name
				)
			).toBeVisible();
			await expect(
				await accountAddressesPage.rowSubtypeCell(
					shippingAddress.name,
					shippingListTypeEntry.key
				)
			).toHaveCount(1);

			await accountAddressesPage.addressesTable
				.valueLink(shippingAddress.name)
				.click();

			await expect(editAccountAddressPage.subtypeInput).toHaveValue(
				new RegExp(shippingAddress.subtype, 'i')
			);

			await editAccountAddressPage.backButton.click();
			await accountAddressesPage.addressesTable
				.valueLink(shippingAddress.name)
				.click();

			await expect(editAccountAddressPage.subtypeInput).toBeEnabled();

			await editAccountAddressPage.typeInput.selectOption('Billing');
			await editAccountAddressPage.subtypeInput.fill(
				billingListTypeEntry.key
			);
			await editAccountAddressPage
				.subtypeMenuItem(billingListTypeEntry.key)
				.click();
			await editAccountAddressPage.saveButton.click();

			await waitForAlert(page);

			await expect(
				await accountAddressesPage.rowSubtypeCell(
					shippingAddress.name,
					billingListTypeEntry.key
				)
			).toHaveCount(1);
		}
		finally {
			await accountInstanceSettingsAccountAddressSubtypePage.setAddressSubtypeExternalReferenceCodes();

			await accountsPage.goto();
			await accountsPage.accountsTable.valueLink(account.name).click();
			await editAccountPage.addressesTab.click();
			await accountAddressesPage.addressesTable
				.valueLink(shippingAddress.name)
				.click();

			await expect(editAccountAddressPage.subtypeInput).toHaveCount(0);
		}
	}
);

test(
	'Region is a required field when adding an address to an account',
	{
		tag: '@LPD-53469',
	},
	async ({
		accountAddressesPage,
		accountsPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			description: getRandomString(),
			type: 'business',
		});

		await accountsPage.goto();

		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();
		await accountAddressesPage.addressesTable.newButton.click();

		await editAccountAddressPage.countryInput.selectOption({
			label: 'Afghanistan',
		});

		await expect(
			await editAccountAddressPage.regionRequiredWrapper
		).toBeVisible();

		await editAccountAddressPage.saveButton.click();

		await expect(editAccountAddressPage.regionSelector).toHaveClass(
			/error-field/
		);

		await editAccountAddressPage.countryInput.selectOption({
			label: 'Anguilla',
		});

		await expect(
			await editAccountAddressPage.regionRequiredWrapper
		).not.toBeVisible();

		await editAccountAddressPage.saveButton.click();

		await expect(editAccountAddressPage.regionSelector).not.toHaveClass(
			/error-field/
		);
	}
);

test(
	'Can search an account address and view its status',
	{tag: '@LPD-56111'},
	async ({
		accountAddressesPage,
		accountsPage,
		apiHelpers,
		editAccountPage,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount();

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id
			);

		await accountsPage.goto();
		await accountsPage.accountsTable.valueLink(account.name).click();
		await editAccountPage.addressesTab.click();
		await accountAddressesPage.addressesTable.search(address.name);

		await expect(
			accountAddressesPage.addressesTable.cell(address.name)
		).toHaveCount(1);
		await expect(
			accountAddressesPage.addressesTable.cell('Approved')
		).toBeVisible();
	}
);
