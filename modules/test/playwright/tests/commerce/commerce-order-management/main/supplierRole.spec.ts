/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectActionAPI,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js';
import {Page, expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {CommerceAdminCatalogDetailsPage} from '../../../../pages/commerce/commerce-catalog-web/commerceAdminCatalogDetailsPage';
import {CommerceAdminCatalogsPage} from '../../../../pages/commerce/commerce-catalog-web/commerceAdminCatalogsPage';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {
	createAccountWithBuyerUser,
	createAccountWithSupplierUser,
	miniumSetUp,
} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let miniumCatalog: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

async function linkSupplierToMiniumCatalog(
	commerceAdminCatalogDetailsPage: CommerceAdminCatalogDetailsPage,
	commerceAdminCatalogsPage: CommerceAdminCatalogsPage,
	page: Page,
	supplierName: string
) {
	await commerceAdminCatalogsPage.goto();

	await commerceAdminCatalogsPage.catalogLink(miniumCatalog.name).click();
	await commerceAdminCatalogDetailsPage.linkSupplierAutocomplete.click();
	await commerceAdminCatalogDetailsPage
		.linkSupplierDropdownItem(supplierName)
		.click();
	await commerceAdminCatalogDetailsPage.saveButton.click();

	await waitForAlert(page);
}

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	miniumCatalog = miniumResult.catalog;
	site = miniumResult.site;
	setupData = [...apiHelpers.data];

	await page.close();
});

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	apiHelpers.setData(setupData);

	await apiHelpers.clearData();

	await page.close();
});

test(
	'A user linked to a Supplier account can log in to the storefront',
	{tag: ['@COMMERCE-11449', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, page}) => {
		const {account, supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		expect(account.type).toBe('supplier');

		await performUserSwitch(page, supplierUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}`);

		await expect(page.getByText(account.name).first()).toBeVisible();
	}
);

test(
	'Catalog "Link to a Supplier" autocomplete shows only active supplier accounts',
	{
		tag: ['@COMMERCE-11824', '@LPD-88485', '@LPD-89343'],
	},
	async ({
		apiHelpers,
		commerceAdminCatalogDetailsPage,
		commerceAdminCatalogsPage,
		page,
	}) => {
		const randomSuffix = getRandomString().slice(0, 8);
		const businessAccountName = `Business Account ${randomSuffix}`;
		const activeSupplier1Name = `Active Supplier 1 ${randomSuffix}`;
		const activeSupplier2Name = `Active Supplier 2 ${randomSuffix}`;
		const inactiveSupplierName = `Inactive Supplier ${randomSuffix}`;

		await apiHelpers.headlessAdminUser.postAccount({
			name: businessAccountName,
			type: 'business',
		});
		const activeSupplier1 = await apiHelpers.headlessAdminUser.postAccount({
			name: activeSupplier1Name,
			type: 'supplier',
		});
		await apiHelpers.headlessAdminUser.postAccount({
			name: activeSupplier2Name,
			type: 'supplier',
		});
		await apiHelpers.headlessAdminUser.postAccount({
			name: inactiveSupplierName,
			status: 5,
			type: 'supplier',
		});

		const testCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: `Test Catalog ${randomSuffix}`,
			});

		await commerceAdminCatalogsPage.goto();

		await commerceAdminCatalogsPage.catalogLink(testCatalog.name).click();

		await commerceAdminCatalogDetailsPage.linkSupplierAutocomplete.click();

		await expect(
			commerceAdminCatalogDetailsPage.linkSupplierDropdownItem(
				activeSupplier1Name
			)
		).toBeVisible();
		await expect(
			commerceAdminCatalogDetailsPage.linkSupplierDropdownItem(
				activeSupplier2Name
			)
		).toBeVisible();
		await expect(
			commerceAdminCatalogDetailsPage.linkSupplierDropdownItem(
				businessAccountName
			)
		).toHaveCount(0);
		await expect(
			commerceAdminCatalogDetailsPage.linkSupplierDropdownItem(
				inactiveSupplierName
			)
		).toHaveCount(0);

		await commerceAdminCatalogDetailsPage
			.linkSupplierDropdownItem(activeSupplier1Name)
			.click();
		await commerceAdminCatalogDetailsPage.saveButton.click();

		await waitForAlert(page);

		const catalogsPage =
			await apiHelpers.headlessCommerceAdminCatalog.getCatalogsPage(
				testCatalog.name
			);

		const catalog = catalogsPage.items?.find(
			(c: {name: string}) => c.name === testCatalog.name
		);

		expect(catalog.accountId).toBe(activeSupplier1.id);
	}
);

test(
	'Renaming a catalog linked to a Supplier account preserves the link',
	{tag: ['@COMMERCE-11825', '@LPD-88485', '@LPD-89343']},
	async ({
		apiHelpers,
		commerceAdminCatalogDetailsPage,
		commerceAdminCatalogsPage,
		page,
	}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const supplierAccount = await apiHelpers.headlessAdminUser.postAccount({
			name: `Supplier Account ${randomSuffix}`,
			type: 'supplier',
		});

		const originalName = `Supplier Catalog ${randomSuffix}`;
		const renamedName = `${originalName} Edited`;

		await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
			accountId: supplierAccount.id,
			name: originalName,
		});

		await commerceAdminCatalogsPage.goto();

		await commerceAdminCatalogsPage.catalogLink(originalName).click();

		await commerceAdminCatalogDetailsPage.nameInput.fill(renamedName);
		await commerceAdminCatalogDetailsPage.saveButton.click();

		await waitForAlert(page);

		let catalogsPage =
			await apiHelpers.headlessCommerceAdminCatalog.getCatalogsPage(
				renamedName
			);

		let catalog = catalogsPage.items?.find(
			(c: {name: string}) => c.name === renamedName
		);

		expect(catalog).toBeDefined();
		expect(catalog.accountId).toBe(supplierAccount.id);

		await apiHelpers.headlessAdminUser.deleteAccount(supplierAccount.id);

		catalogsPage =
			await apiHelpers.headlessCommerceAdminCatalog.getCatalogsPage(
				renamedName
			);
		catalog = catalogsPage.items?.find(
			(c: {name: string}) => c.name === renamedName
		);

		expect(catalog.accountId).not.toBe(supplierAccount.id);
	}
);

test(
	'Supplier user can manage only catalogs linked to their account',
	{tag: ['@COMMERCE-11604', '@LPD-88485', '@LPD-89343']},
	async ({
		apiHelpers,
		commerceAdminCatalogDetailsPage,
		commerceAdminCatalogsPage,
		page,
	}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account, supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		const linkedCatalogName = `Linked Catalog ${randomSuffix}`;
		const editedCatalogName = `${linkedCatalogName} Edited`;
		const testPriceListName = `Test Price List ${randomSuffix}`;
		const testPromotionName = `Test Promotion ${randomSuffix}`;

		const linkedCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				accountId: account.id,
				name: linkedCatalogName,
			});

		await apiHelpers.headlessCommerceAdminPricing.postPriceList({
			catalogId: linkedCatalog.id,
			currencyCode: 'USD',
			name: testPriceListName,
			type: 'price-list',
		});
		await apiHelpers.headlessCommerceAdminPricing.postPriceList({
			catalogId: linkedCatalog.id,
			currencyCode: 'USD',
			name: testPromotionName,
			type: 'promotion',
		});

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminCatalogsPage.goto();

		await expect(
			commerceAdminCatalogsPage.catalogLink(linkedCatalogName)
		).toBeVisible();
		await expect(
			commerceAdminCatalogsPage.catalogLink('Master')
		).toHaveCount(0);
		await expect(commerceAdminCatalogsPage.addCatalogsButton).toBeVisible();
		await expect(
			commerceAdminCatalogsPage.catalogActionsButton(linkedCatalogName)
		).toHaveCount(0);

		await commerceAdminCatalogsPage.catalogLink(linkedCatalogName).click();

		await commerceAdminCatalogDetailsPage.nameInput.fill(editedCatalogName);
		await commerceAdminCatalogDetailsPage.currencySelect.selectOption(
			'EUR'
		);
		await commerceAdminCatalogDetailsPage.languageSelect.selectOption(
			'ca_ES'
		);

		await commerceAdminCatalogDetailsPage.basePriceListAutocomplete.fill(
			testPriceListName
		);
		await commerceAdminCatalogDetailsPage
			.basePriceListDropdownItem(testPriceListName)
			.click();
		await commerceAdminCatalogDetailsPage.basePromotionAutocomplete.fill(
			testPromotionName
		);
		await commerceAdminCatalogDetailsPage
			.basePromotionDropdownItem(testPromotionName)
			.click();

		await commerceAdminCatalogDetailsPage.saveButton.click();

		await waitForAlert(page);

		const catalogsPage =
			await apiHelpers.headlessCommerceAdminCatalog.getCatalogsPage(
				editedCatalogName
			);

		const updated = catalogsPage.items?.find(
			(c: {name: string}) => c.name === editedCatalogName
		);

		expect(updated).toBeDefined();
		expect(updated.currencyCode).toBe('EUR');
		expect(updated.defaultLanguageId).toBe('ca_ES');

		const basePriceList =
			await apiHelpers.headlessCommerceAdminPricing.getBasePriceList(
				linkedCatalog.id
			);
		const basePromotion =
			await apiHelpers.headlessCommerceAdminPricing.getBasePromoPriceList(
				linkedCatalog.id
			);

		expect(basePriceList.items[0].name).toBe(testPriceListName);
		expect(basePromotion.items[0].name).toBe(testPromotionName);
	}
);

test(
	'Supplier user can create own catalogs (auto-linked) and delete only those, not linked-but-not-owned catalogs',
	{tag: ['@COMMERCE-10893', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, commerceAdminCatalogsPage, page}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account, supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		const linkedNotOwnedCatalogName = `Linked Catalog ${randomSuffix}`;
		const ownedCatalogName = `Owned Catalog ${randomSuffix}`;

		await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
			accountId: account.id,
			name: linkedNotOwnedCatalogName,
		});

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminCatalogsPage.goto();

		await test.step('Supplier user creates a catalog via the Add modal - auto-linked to supplier', async () => {
			await commerceAdminCatalogsPage.addCatalogsButton.click();

			await expect(
				commerceAdminCatalogsPage.modalFieldName
			).toBeVisible();

			await commerceAdminCatalogsPage.modalFieldName.fill(
				ownedCatalogName
			);

			await expect(
				commerceAdminCatalogsPage.modalLinkSupplierAutocomplete
			).toHaveValue(account.name);

			await commerceAdminCatalogsPage.modalSubmitButton.click();

			await commerceAdminCatalogsPage.modalFieldName.waitFor({
				state: 'hidden',
			});
		});

		await test.step('Both catalogs visible in the supplier user’s catalogs admin', async () => {
			await commerceAdminCatalogsPage.goto();

			await expect(
				commerceAdminCatalogsPage.catalogLink(ownedCatalogName)
			).toBeVisible();
			await expect(
				commerceAdminCatalogsPage.catalogLink(linkedNotOwnedCatalogName)
			).toBeVisible();
		});

		await test.step('Supplier user can delete a catalog they created', async () => {
			await expect(async () => {
				await commerceAdminCatalogsPage
					.catalogActionsButton(ownedCatalogName)
					.click();

				await expect(
					commerceAdminCatalogsPage.deleteMenuItem
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});

			await commerceAdminCatalogsPage.deleteMenuItem.click();

			await expect(
				commerceAdminCatalogsPage.catalogLink(ownedCatalogName)
			).toHaveCount(0);
		});

		await test.step('Supplier user cannot delete a catalog linked to but not created by them', async () => {
			await expect(
				commerceAdminCatalogsPage.catalogActionsButton(
					linkedNotOwnedCatalogName
				)
			).toHaveCount(0);
		});
	}
);

test(
	'Supplier user in multiple supplier accounts can pick which account to link when creating a catalog',
	{tag: ['@COMMERCE-10893', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, commerceAdminCatalogsPage, page}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account: account1, supplierUser} =
			await createAccountWithSupplierUser(apiHelpers, site.id);

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			name: `Supplier Account 2 ${randomSuffix}`,
			type: 'supplier',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account2.id,
			[supplierUser.emailAddress]
		);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account2.id);

		const supplierRole = rolesResponse?.items?.find(
			(role: {name: string}) => role.name === 'Account Supplier'
		);

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account2.externalReferenceCode,
			supplierRole.id,
			supplierUser.emailAddress
		);

		const newCatalogName = `Catalog ${randomSuffix}`;

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminCatalogsPage.goto();

		await commerceAdminCatalogsPage.addCatalogsButton.click();

		await expect(commerceAdminCatalogsPage.modalFieldName).toBeVisible();

		await commerceAdminCatalogsPage.modalFieldName.fill(newCatalogName);
		await commerceAdminCatalogsPage.modalLinkSupplierAutocomplete.click();

		await expect(
			commerceAdminCatalogsPage.modalLinkSupplierDropdownItem(
				account1.name
			)
		).toBeVisible();
		await expect(
			commerceAdminCatalogsPage.modalLinkSupplierDropdownItem(
				account2.name
			)
		).toBeVisible();

		await commerceAdminCatalogsPage
			.modalLinkSupplierDropdownItem(account2.name)
			.click();

		await commerceAdminCatalogsPage.modalSubmitButton.click();

		await commerceAdminCatalogsPage.modalFieldName.waitFor({
			state: 'hidden',
		});

		const catalogsPage =
			await apiHelpers.headlessCommerceAdminCatalog.getCatalogsPage(
				newCatalogName
			);

		const catalog = catalogsPage.items?.find(
			(c: {name: string}) => c.name === newCatalogName
		);

		expect(catalog).toBeDefined();
		expect(catalog.accountId).toBe(account2.id);
	}
);

test(
	'Channel "Link to a Supplier" select shows only active supplier accounts',
	{tag: ['@COMMERCE-10923', '@LPD-88485', '@LPD-89343']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
	}) => {
		const randomSuffix = getRandomString().slice(0, 8);
		const businessAccountName = `Business Account ${randomSuffix}`;
		const activeSupplier1Name = `Active Supplier 1 ${randomSuffix}`;
		const activeSupplier2Name = `Active Supplier 2 ${randomSuffix}`;
		const inactiveSupplierName = `Inactive Supplier ${randomSuffix}`;
		const channelName = `Test Channel ${randomSuffix}`;

		await apiHelpers.headlessAdminUser.postAccount({
			name: businessAccountName,
			type: 'business',
		});
		const activeSupplier1 = await apiHelpers.headlessAdminUser.postAccount({
			name: activeSupplier1Name,
			type: 'supplier',
		});
		await apiHelpers.headlessAdminUser.postAccount({
			name: activeSupplier2Name,
			type: 'supplier',
		});
		await apiHelpers.headlessAdminUser.postAccount({
			name: inactiveSupplierName,
			status: 5,
			type: 'supplier',
		});

		let channel = await apiHelpers.headlessCommerceAdminChannel.postChannel(
			{
				name: channelName,
				siteGroupId: 0,
			}
		);

		await expect(async () => {
			await commerceAdminChannelsPage.goto();

			await commerceAdminChannelsPage.channelLink(channelName).click();

			const optionLabels =
				await commerceAdminChannelDetailsPage.linkSupplierSelect
					.locator('option')
					.evaluateAll((options) =>
						options.map((option) =>
							(option as HTMLOptionElement).label.trim()
						)
					);

			expect(optionLabels).toContain(activeSupplier1Name);
			expect(optionLabels).toContain(activeSupplier2Name);
			expect(optionLabels).not.toContain(businessAccountName);
			expect(optionLabels).not.toContain(inactiveSupplierName);
		}).toPass({timeout: 30000});

		await apiHelpers.headlessCommerceAdminChannel.patchChannelWithAccountId(
			activeSupplier1.id,
			channel
		);
		await apiHelpers.headlessAdminUser.deleteAccount(activeSupplier1.id);

		channel = await apiHelpers.headlessCommerceAdminChannel.getChannel(
			channel.id
		);

		expect(channel.accountId).not.toBe(activeSupplier1.id);
	}
);

test(
	'Supplier user can manage only the channel linked to their account',
	{tag: ['@COMMERCE-11607', '@COMMERCE-11866', '@LPD-88485', '@LPD-89343']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
	}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account, supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		const linkedChannelName = `Supplier Channel ${randomSuffix}`;
		const unlinkedChannelName = `Unlinked Channel ${randomSuffix}`;

		const linkedChannel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: linkedChannelName,
				siteGroupId: 0,
			});

		await apiHelpers.headlessCommerceAdminChannel.patchChannelWithAccountId(
			account.id,
			linkedChannel
		);

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			name: unlinkedChannelName,
			siteGroupId: 0,
		});

		await test.step('Admin cannot link a second channel to the same supplier', async () => {
			await commerceAdminChannelsPage.goto();

			await commerceAdminChannelsPage
				.channelLink(unlinkedChannelName)
				.click();

			await commerceAdminChannelDetailsPage.linkSupplierSelect.selectOption(
				{label: account.name}
			);
			await commerceAdminChannelDetailsPage.saveButton.click();

			await expect(
				commerceAdminChannelDetailsPage.errorMessage(
					'A supplier account can be linked only to one channel'
				)
			).toBeVisible();
		});

		await performUserSwitch(page, supplierUser.alternateName);

		await test.step('Supplier sees only the linked channel without Add or Delete actions', async () => {
			await commerceAdminChannelsPage.goto();

			await expect(
				commerceAdminChannelsPage.channelLink(linkedChannelName)
			).toBeVisible();
			await expect(
				commerceAdminChannelsPage.channelLink(unlinkedChannelName)
			).toHaveCount(0);
			await expect(commerceAdminChannelsPage.addButton).toHaveCount(0);
			await expect(
				commerceAdminChannelsPage.channelActionsButton(
					linkedChannelName
				)
			).toHaveCount(0);
		});

		await test.step('Supplier can edit channel settings (Guest Checkout)', async () => {
			await commerceAdminChannelsPage
				.channelLink(linkedChannelName)
				.click();

			await commerceAdminChannelDetailsPage.guestCheckoutToggle.setChecked(
				true
			);

			await expect(
				commerceAdminChannelDetailsPage.guestCheckoutToggle
			).toBeChecked();

			await commerceAdminChannelDetailsPage.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Admin unlinks the supplier and supplier sees no channels', async () => {
			await performLoginViaApi({page, screenName: 'test'});

			await commerceAdminChannelsPage.goto();
			await commerceAdminChannelsPage
				.channelLink(linkedChannelName)
				.click();

			await commerceAdminChannelDetailsPage.linkSupplierSelect.selectOption(
				''
			);
			await commerceAdminChannelDetailsPage.saveButton.click();

			await waitForAlert(page);

			await performUserSwitch(page, supplierUser.alternateName);

			await commerceAdminChannelsPage.goto();

			await expect(
				commerceAdminChannelsPage.channelLink(linkedChannelName)
			).toHaveCount(0);
		});
	}
);

test(
	'Supplier user sees only the price list entries belonging to a linked catalog',
	{tag: ['@COMMERCE-11757', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, commerceAdminPriceListsPage, page}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account, supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		const testCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				accountId: account.id,
				name: `Test Catalog ${randomSuffix}`,
			});

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminPriceListsPage.goto();

		await expect(
			commerceAdminPriceListsPage.priceListLink(
				`${testCatalog.name} Base Price List`
			)
		).toBeVisible();
		await expect(
			commerceAdminPriceListsPage.priceListLink('Master Base Price List')
		).toHaveCount(0);
	}
);

test(
	'Supplier user sees only the promotion entries belonging to a linked catalog',
	{tag: ['@COMMERCE-11757', '@COMMERCE-11843', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, commerceAdminPromotionsPage, page}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account, supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		const testCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				accountId: account.id,
				name: `Test Catalog ${randomSuffix}`,
			});

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminPromotionsPage.goto();

		await expect(
			commerceAdminPromotionsPage.promotionLink(
				`${testCatalog.name} Base Promotion`
			)
		).toBeVisible();
		await expect(
			commerceAdminPromotionsPage.promotionLink('Master Base Promotion')
		).toHaveCount(0);
	}
);

test(
	'Supplier user can add and delete a price list for their linked catalog',
	{tag: ['@COMMERCE-11758', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, commerceAdminPriceListsPage, page}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account, supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		const testCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				accountId: account.id,
				name: `Test Catalog ${randomSuffix}`,
			});

		const newPriceListName = `Test PL ${randomSuffix}`;

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminPriceListsPage.goto();

		await commerceAdminPriceListsPage.addPriceListButton.click();

		await expect(async () => {
			await commerceAdminPriceListsPage.addPriceListModalNameInput.fill(
				newPriceListName
			);

			await expect(
				commerceAdminPriceListsPage.addPriceListModalNameInput
			).toHaveValue(newPriceListName, {timeout: 1000});
		}).toPass({timeout: 10000});

		const catalogOptionLabels =
			await commerceAdminPriceListsPage.addPriceListModalCatalogSelect
				.locator('option')
				.evaluateAll((options) =>
					options.map((option) =>
						(option as HTMLOptionElement).label.trim()
					)
				);

		expect(catalogOptionLabels).toContain(testCatalog.name);
		expect(catalogOptionLabels).not.toContain('Master');

		await commerceAdminPriceListsPage.addPriceListModalCatalogSelect.selectOption(
			{label: testCatalog.name}
		);
		await commerceAdminPriceListsPage.addPriceListModalCurrencySelect.selectOption(
			{label: 'USD'}
		);

		await commerceAdminPriceListsPage.addPriceListModalSubmitButton.click();

		await expect(
			commerceAdminPriceListsPage.priceListLink(newPriceListName)
		).toBeVisible();

		await commerceAdminPriceListsPage
			.priceListRowActionsButton(newPriceListName)
			.click();

		await commerceAdminPriceListsPage.deleteMenuItem.click();

		await expect(
			commerceAdminPriceListsPage.priceListLink(newPriceListName)
		).toHaveCount(0);
	}
);

test(
	'Supplier user can add and delete a promotion for their linked catalog',
	{tag: ['@COMMERCE-11843', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, commerceAdminPromotionsPage, page}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account, supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		const testCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				accountId: account.id,
				name: `Test Catalog ${randomSuffix}`,
			});

		const newPromotionName = `Test Promo ${randomSuffix}`;

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminPromotionsPage.goto();

		await commerceAdminPromotionsPage.addPromotionButton.click();

		await expect(async () => {
			await commerceAdminPromotionsPage.addPromotionModalNameInput.fill(
				newPromotionName
			);

			await expect(
				commerceAdminPromotionsPage.addPromotionModalNameInput
			).toHaveValue(newPromotionName, {timeout: 1000});
		}).toPass({timeout: 10000});

		const catalogOptionLabels =
			await commerceAdminPromotionsPage.addPromotionModalCatalogSelect
				.locator('option')
				.evaluateAll((options) =>
					options.map((option) =>
						(option as HTMLOptionElement).label.trim()
					)
				);

		expect(catalogOptionLabels).toContain(testCatalog.name);
		expect(catalogOptionLabels).not.toContain('Master');

		await commerceAdminPromotionsPage.addPromotionModalCatalogSelect.selectOption(
			{label: testCatalog.name}
		);
		await commerceAdminPromotionsPage.addPromotionModalCurrencySelect.selectOption(
			{label: 'USD'}
		);

		await commerceAdminPromotionsPage.addPromotionModalSubmitButton.click();

		await expect(
			commerceAdminPromotionsPage.promotionLink(newPromotionName)
		).toBeVisible();

		await commerceAdminPromotionsPage
			.promotionRowActionsButton(newPromotionName)
			.click();

		await commerceAdminPromotionsPage.deleteMenuItem.click();

		await expect(
			commerceAdminPromotionsPage.promotionLink(newPromotionName)
		).toHaveCount(0);
	}
);

for (const variant of [
	{
		label: 'price list',
		tag: '@COMMERCE-11840',
		type: 'price-list' as const,
	},
	{
		label: 'promotion',
		tag: '@COMMERCE-11843',
		type: 'promotion' as const,
	},
]) {
	test(
		`Supplier user can add tier prices on a ${variant.label}`,
		{tag: [variant.tag, '@COMMERCE-11841', '@LPD-88485', '@LPD-89343']},
		async ({
			apiHelpers,
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			commerceAdminPromotionsPage,
			page,
		}) => {
			const randomSuffix = getRandomString().slice(0, 8);

			const {account, supplierUser} = await createAccountWithSupplierUser(
				apiHelpers,
				site.id
			);

			const linkedCatalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					accountId: account.id,
					name: `Linked Catalog ${randomSuffix}`,
				});

			const skuName = `TIER-SKU-${randomSuffix}`;

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					active: true,
					catalogId: linkedCatalog.id,
					name: {en_US: `Tier Product ${randomSuffix}`},
					skus: [
						{
							cost: 0,
							price: 10,
							published: true,
							purchasable: true,
							sku: skuName,
						},
					],
				});

			const entryName = `Test ${variant.label} ${randomSuffix}`;

			const entry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: linkedCatalog.id,
					currencyCode: 'USD',
					name: entryName,
					type: variant.type,
				});

			const priceEntry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
					price: 50,
					priceListId: entry.id,
					skuId: product.skus[0].id,
				});

			await performUserSwitch(page, supplierUser.alternateName);

			if (variant.type === 'price-list') {
				await commerceAdminPriceListsPage.goto();

				await commerceAdminPriceListsPage
					.priceListLink(entryName)
					.click();
			}
			else {
				await commerceAdminPromotionsPage.goto();

				await commerceAdminPromotionsPage
					.promotionLink(entryName)
					.click();
			}

			await commerceAdminPriceListDetailsPage.entriesTab.click();

			await commerceAdminPriceListDetailsPage
				.skusTableRowLink(skuName)
				.click();

			await commerceAdminPriceListDetailsPage.addTierPriceButton.click();

			await commerceAdminPriceListDetailsPage.addTierPriceEntryQuantity.fill(
				'2'
			);
			await commerceAdminPriceListDetailsPage.addTierPriceEntryPrice.fill(
				'1000'
			);

			await commerceAdminPriceListDetailsPage.addTierPriceEntrySaveButton.click();

			await commerceAdminPriceListDetailsPage.addTierPriceEntryQuantity.waitFor(
				{state: 'hidden'}
			);

			const tierPrices =
				await apiHelpers.headlessCommerceAdminPricing.getTierPrices(
					priceEntry.priceEntryId
				);

			const tierPrice = (tierPrices.items ?? []).find(
				(tier: {minimumQuantity: number; price: number}) =>
					tier.minimumQuantity === 2 && Number(tier.price) === 1000
			);

			expect(tierPrice).toBeDefined();
		}
	);
}

for (const variant of [
	{
		label: 'price list',
		tag: '@COMMERCE-11842',
		type: 'price-list' as const,
	},
	{
		label: 'promotion',
		tag: '@COMMERCE-11843',
		type: 'promotion' as const,
	},
]) {
	test(
		`Supplier user can edit a price entry on a ${variant.label}`,
		{tag: [variant.tag, '@LPD-88485', '@LPD-89343']},
		async ({
			apiHelpers,
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			commerceAdminPromotionsPage,
			page,
		}) => {
			const randomSuffix = getRandomString().slice(0, 8);

			const {account, supplierUser} = await createAccountWithSupplierUser(
				apiHelpers,
				site.id
			);

			const linkedCatalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					accountId: account.id,
					name: `Linked Catalog ${randomSuffix}`,
				});

			const skuName = `EDIT-SKU-${randomSuffix}`;

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					active: true,
					catalogId: linkedCatalog.id,
					name: {en_US: `Edit Product ${randomSuffix}`},
					skus: [
						{
							cost: 0,
							price: 10,
							published: true,
							purchasable: true,
							sku: skuName,
						},
					],
				});

			const entryName = `Test ${variant.label} ${randomSuffix}`;

			const entry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: linkedCatalog.id,
					currencyCode: 'USD',
					name: entryName,
					type: variant.type,
				});

			let priceEntry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
					price: 100,
					priceListId: entry.id,
					skuId: product.skus[0].id,
				});

			await performUserSwitch(page, supplierUser.alternateName);

			if (variant.type === 'price-list') {
				await commerceAdminPriceListsPage.goto();

				await commerceAdminPriceListsPage
					.priceListLink(entryName)
					.click();
			}
			else {
				await commerceAdminPromotionsPage.goto();

				await commerceAdminPromotionsPage
					.promotionLink(entryName)
					.click();
			}

			await commerceAdminPriceListDetailsPage.entriesTab.click();

			await commerceAdminPriceListDetailsPage
				.skusTableRowLink(skuName)
				.click();

			await commerceAdminPriceListDetailsPage.sidePanelPriceInput.fill(
				'80'
			);
			await commerceAdminPriceListDetailsPage.sidePanelSaveButton.click();

			await waitForAlert(
				commerceAdminPriceListDetailsPage.sidePanelFrame
			);

			priceEntry =
				await apiHelpers.headlessCommerceAdminPricing.getPriceEntry(
					priceEntry.priceEntryId
				);

			expect(Number(priceEntry.price)).toBe(80);
		}
	);
}

for (const variant of [
	{
		label: 'price list',
		tag: '@COMMERCE-11868',
		type: 'price-list' as const,
	},
	{
		label: 'promotion',
		tag: '@COMMERCE-11868',
		type: 'promotion' as const,
	},
]) {
	test(
		`Supplier user can set an override discount on a ${variant.label} entry`,
		{tag: [variant.tag, '@LPD-88485', '@LPD-89343']},
		async ({
			apiHelpers,
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			commerceAdminPromotionsPage,
			page,
		}) => {
			const randomSuffix = getRandomString().slice(0, 8);

			const {account, supplierUser} = await createAccountWithSupplierUser(
				apiHelpers,
				site.id
			);

			const linkedCatalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					accountId: account.id,
					name: `Linked Catalog ${randomSuffix}`,
				});

			const skuName = `DISC-SKU-${randomSuffix}`;

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					active: true,
					catalogId: linkedCatalog.id,
					name: {en_US: `Discount Product ${randomSuffix}`},
					skus: [
						{
							cost: 0,
							price: 100,
							published: true,
							purchasable: true,
							sku: skuName,
						},
					],
				});

			const entryName = `Test ${variant.label} ${randomSuffix}`;

			const entry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: linkedCatalog.id,
					currencyCode: 'USD',
					name: entryName,
					type: variant.type,
				});

			let priceEntry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
					price: 100,
					priceListId: entry.id,
					skuId: product.skus[0].id,
				});

			await performUserSwitch(page, supplierUser.alternateName);

			if (variant.type === 'price-list') {
				await commerceAdminPriceListsPage.goto();

				await commerceAdminPriceListsPage
					.priceListLink(entryName)
					.click();
			}
			else {
				await commerceAdminPromotionsPage.goto();

				await commerceAdminPromotionsPage
					.promotionLink(entryName)
					.click();
			}

			await commerceAdminPriceListDetailsPage.entriesTab.click();

			await commerceAdminPriceListDetailsPage
				.skusTableRowLink(skuName)
				.click();

			await commerceAdminPriceListDetailsPage.sidePanelOverrideDiscountToggle.click();

			await commerceAdminPriceListDetailsPage.sidePanelDiscountLevel1Input.fill(
				'20'
			);

			await commerceAdminPriceListDetailsPage.sidePanelSaveButton.click();

			await waitForAlert(
				commerceAdminPriceListDetailsPage.sidePanelFrame
			);

			priceEntry =
				await apiHelpers.headlessCommerceAdminPricing.getPriceEntry(
					priceEntry.priceEntryId
				);

			expect(priceEntry.discountDiscovery).toBe(false);
			expect(Number(priceEntry.discountLevel1)).toBe(20);
		}
	);
}

test(
	'Supplier user can manage products only on their linked catalog',
	{tag: ['@COMMERCE-11611', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, commerceAdminProductPage, page}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account, supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		const linkedCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				accountId: account.id,
				name: `Linked Catalog ${randomSuffix}`,
			});

		const otherCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: `Other Catalog ${randomSuffix}`,
			});

		const linkedProductName = `Linked Product ${randomSuffix}`;
		const otherProductName = `Other Product ${randomSuffix}`;

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			active: true,
			catalogId: linkedCatalog.id,
			name: {en_US: linkedProductName},
			skus: [
				{
					cost: 0,
					price: 10,
					published: true,
					purchasable: true,
					sku: `LSKU-${randomSuffix}`,
				},
			],
		});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			active: true,
			catalogId: otherCatalog.id,
			name: {en_US: otherProductName},
			skus: [
				{
					cost: 0,
					price: 25,
					published: true,
					purchasable: true,
					sku: `OSKU-${randomSuffix}`,
				},
			],
		});

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminProductPage.goto();

		await test.step('Supplier sees only their linked-catalog product', async () => {
			await expect(async () => {
				await commerceAdminProductPage.managementToolbarSearchInput.fill(
					linkedProductName
				);
				await commerceAdminProductPage.managementToolbarSearchInput.press(
					'Enter'
				);

				await expect(
					commerceAdminProductPage.managementToolbarItemLink(
						linkedProductName
					)
				).toBeVisible({timeout: 2000});
			}).toPass({timeout: 30000});

			await commerceAdminProductPage.managementToolbarSearchInput.fill(
				otherProductName
			);
			await commerceAdminProductPage.managementToolbarSearchInput.press(
				'Enter'
			);

			await expect(
				commerceAdminProductPage.managementToolbarItemLink(
					otherProductName
				)
			).toHaveCount(0);
		});

		const newProductName = `Probe ${randomSuffix}`;

		await test.step('Add Product modal Catalog autocomplete shows only linked catalogs', async () => {
			await commerceAdminProductPage.addButton.click();
			await commerceAdminProductPage
				.menuItemProductType('Simple')
				.click();

			await commerceAdminProductPage.modalFieldName.fill(newProductName);
			await commerceAdminProductPage.modalPlaceHolder.fill(
				`Catalog ${randomSuffix}`
			);

			await expect(
				commerceAdminProductPage.modalMenuItem(linkedCatalog.name)
			).toBeVisible();
			await expect(
				commerceAdminProductPage.modalMenuItem(otherCatalog.name)
			).toHaveCount(0);

			await commerceAdminProductPage.modalPlaceHolder.fill('Master');

			await expect(
				commerceAdminProductPage.modalMenuItem('Master')
			).toHaveCount(0);
		});

		await test.step('Supplier user submits the modal and creates the product on the linked catalog', async () => {
			await commerceAdminProductPage.modalPlaceHolder.fill(
				`Catalog ${randomSuffix}`
			);
			await commerceAdminProductPage
				.modalMenuItem(linkedCatalog.name)
				.click();

			await commerceAdminProductPage.modalSubmitButton.click();

			await commerceAdminProductPage.modalBody.waitFor({
				state: 'detached',
			});

			const products =
				await apiHelpers.headlessCommerceAdminCatalog.getProducts(
					new URLSearchParams({
						filter: `catalogId eq ${linkedCatalog.id}`,
					})
				);

			const product = (products.items ?? []).find(
				(item: {name: {en_US: string}}) =>
					item.name?.en_US === newProductName
			);

			expect(product).toBeDefined();
		});
	}
);

test(
	'Supplier user can delete a product on their linked catalog',
	{tag: ['@COMMERCE-11610', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, commerceAdminProductPage, page}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		page.on('dialog', (dialog) => dialog.accept());

		const {account, supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		const linkedCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				accountId: account.id,
				name: `Linked Catalog ${randomSuffix}`,
			});

		const productName = `Delete Product ${randomSuffix}`;

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				active: true,
				catalogId: linkedCatalog.id,
				name: {en_US: productName},
				skus: [
					{
						cost: 0,
						price: 10,
						published: true,
						purchasable: true,
						sku: `DEL-SKU-${randomSuffix}`,
					},
				],
			});

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminProductPage.goto();

		await expect(async () => {
			await commerceAdminProductPage.managementToolbarSearchInput.fill(
				productName
			);
			await commerceAdminProductPage.managementToolbarSearchInput.press(
				'Enter'
			);

			await expect(
				commerceAdminProductPage.managementToolbarItemLink(productName)
			).toBeVisible({timeout: 2000});
		}).toPass({timeout: 30000});

		await commerceAdminProductPage
			.productRowActionsButton(productName)
			.click();

		await commerceAdminProductPage.deleteMenuItem.click();

		await waitForAlert(page);

		await expect(
			commerceAdminProductPage.managementToolbarItemLink(productName)
		).toHaveCount(0);

		await performLoginViaApi({page, screenName: 'test'});

		const products =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `productId eq ${product.productId}`,
				})
			);

		expect(products.items ?? []).toHaveLength(0);
	}
);

test(
	'Supplier user sees only the split sub-order routed to their linked channel',
	{tag: ['@COMMERCE-11779', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, commerceAdminOrdersPage, page}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account: supplierAccount, supplierUser} =
			await createAccountWithSupplierUser(apiHelpers, site.id);

		const supplierCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				accountId: supplierAccount.id,
				name: `Supplier Catalog ${randomSuffix}`,
			});

		const supplierChannel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: `Supplier Channel ${randomSuffix}`,
				siteGroupId: 0,
			});

		await apiHelpers.headlessCommerceAdminChannel.patchChannelWithAccountId(
			supplierAccount.id,
			supplierChannel
		);

		const customerChannel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: `Customer Channel ${randomSuffix}`,
				siteGroupId: 0,
			});

		const supplierProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				active: true,
				catalogId: supplierCatalog.id,
				name: {en_US: `Supplier Product ${randomSuffix}`},
				skus: [
					{
						cost: 0,
						price: 5,
						published: true,
						purchasable: true,
						sku: `SUPSKU-${randomSuffix}`,
					},
				],
			});

		const otherCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: `Other Catalog ${randomSuffix}`,
			});

		const otherProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				active: true,
				catalogId: otherCatalog.id,
				name: {en_US: `Other Product ${randomSuffix}`},
				skus: [
					{
						cost: 0,
						price: 20,
						published: true,
						purchasable: true,
						sku: `OTHSKU-${randomSuffix}`,
					},
				],
			});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: commerceOrderDefinition} =
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				'L_COMMERCE_ORDER'
			);

		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectActionAPI);

		const {body: objectAction} =
			await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
				'L_COMMERCE_ORDER',
				{
					active: true,
					conditionExpression: 'orderStatus = 10',
					label: {en_US: 'Split order by catalog'},
					name: `SplitOrderByCatalog${randomSuffix}`,
					objectActionExecutorKey: 'split-commerce-order-by-catalog',
					objectActionTriggerKey: 'liferay/commerce_order_status',
					parameters: {
						objectDefinitionId: commerceOrderDefinition.id,
					},
				}
			);

		apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

		const buyerAccount = await apiHelpers.headlessAdminUser.postAccount({
			name: `Buyer Account ${randomSuffix}`,
			type: 'business',
		});

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				buyerAccount.id,
				{
					city: 'Test City',
					countryISOCode: 'US',
					defaultBilling: true,
					defaultShipping: true,
					name: 'Test Address',
					regionISOCode: 'CA',
					street1: 'Test Street',
					zip: '12345',
				}
			);

		const customerOrder =
			await apiHelpers.headlessCommerceAdminOrder.postOrder({
				accountId: buyerAccount.id,
				billingAddressId: address.id,
				channelId: customerChannel.id,
				orderItems: [
					{quantity: 1, skuId: String(supplierProduct.skus[0].id)},
					{quantity: 1, skuId: String(otherProduct.skus[0].id)},
				],
				orderStatus: '1',
				paymentMethod: 'money-order',
				paymentStatus: '2',
				shippingAddressId: address.id,
			});

		await apiHelpers.headlessCommerceAdminOrder.patchOrder(
			customerOrder.id,
			{orderStatus: '10'}
		);

		let supplierSubOrderId: number;

		await expect(async () => {
			const ordersAfterSplit =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			const supplierSubOrder = (ordersAfterSplit.items ?? []).find(
				(o: {channelId: number; id: number}) =>
					o.channelId === supplierChannel.id
			);

			expect(supplierSubOrder).toBeDefined();

			supplierSubOrderId = supplierSubOrder.id;
		}).toPass({timeout: 15000});

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminOrdersPage.goto();

		await expect(
			commerceAdminOrdersPage.tableRowOrderIdLink(supplierSubOrderId)
		).toBeVisible();
		await expect(
			commerceAdminOrdersPage.tableRowOrderIdLink(customerOrder.id)
		).toHaveCount(0);
	}
);

test(
	'Supplier user can manage the full workflow of an order from Accept to Delivered',
	{tag: ['@COMMERCE-11888', '@LPD-88485', '@LPD-89343']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const {account: supplierAccount, supplierUser} =
			await createAccountWithSupplierUser(apiHelpers, site.id);

		const supplierCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				accountId: supplierAccount.id,
				name: `Supplier Catalog ${randomSuffix}`,
			});

		const supplierChannel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: `Supplier Channel ${randomSuffix}`,
				siteGroupId: 0,
			});

		await apiHelpers.headlessCommerceAdminChannel.patchChannelWithAccountId(
			supplierAccount.id,
			supplierChannel
		);

		const customerChannel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: `Customer Channel ${randomSuffix}`,
				siteGroupId: 0,
			});

		let supplierProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: supplierCatalog.id,
				name: {en_US: `Supplier Product ${randomSuffix}`},
			});

		supplierProduct =
			await apiHelpers.headlessCommerceAdminCatalog.getProduct(
				supplierProduct.productId
			);

		const supplierSku = supplierProduct.skus[0];

		const otherCatalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: `Other Catalog ${randomSuffix}`,
			});

		let otherProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: otherCatalog.id,
				name: {en_US: `Other Product ${randomSuffix}`},
			});

		otherProduct = await apiHelpers.headlessCommerceAdminCatalog.getProduct(
			otherProduct.productId
		);

		const otherSku = otherProduct.skus[0];

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: 10,
					longitude: 0,
					warehouseItems: [
						{quantity: 100, sku: supplierSku.sku},
						{quantity: 100, sku: otherSku.sku},
					],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			customerChannel.id
		);
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			supplierChannel.id
		);

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: commerceOrderDefinition} =
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				'L_COMMERCE_ORDER'
			);

		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectActionAPI);

		const {body: objectAction} =
			await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
				'L_COMMERCE_ORDER',
				{
					active: true,
					conditionExpression: 'orderStatus = 10',
					label: {en_US: 'Split order by catalog'},
					name: `SplitOrderByCatalog${randomSuffix}`,
					objectActionExecutorKey: 'split-commerce-order-by-catalog',
					objectActionTriggerKey: 'liferay/commerce_order_status',
					parameters: {
						objectDefinitionId: commerceOrderDefinition.id,
					},
				}
			);

		apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

		const buyerAccount = await apiHelpers.headlessAdminUser.postAccount({
			name: `Buyer Account ${randomSuffix}`,
			type: 'business',
		});

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				buyerAccount.id,
				{
					city: 'Test City',
					countryISOCode: 'US',
					defaultBilling: true,
					defaultShipping: true,
					name: 'Test Address',
					regionISOCode: 'CA',
					street1: 'Test Street',
					zip: '12345',
				}
			);

		const customerOrder =
			await apiHelpers.headlessCommerceAdminOrder.postOrder({
				accountId: buyerAccount.id,
				billingAddressId: address.id,
				channelId: customerChannel.id,
				orderItems: [
					{quantity: 1, skuId: supplierSku.id},
					{quantity: 1, skuId: otherSku.id},
				],
				orderStatus: '1',
				paymentMethod: 'money-order',
				paymentStatus: '2',
				shippingAddressId: address.id,
				shippingMethod: 'by-weight',
				shippingOption: 'standard-option',
			} as any);

		await commerceAdminOrdersPage.goto();

		await commerceAdminOrdersPage
			.tableRowOrderIdLink(customerOrder.id)
			.click();

		await expect(
			commerceAdminOrderDetailsPage.headerDetailsTitle
		).toBeVisible();

		await commerceAdminOrderDetailsPage.acceptOrderButton.click();

		await waitForAlert(page);

		let supplierSubOrderId: number;

		await expect(async () => {
			const ordersAfterSplit =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			const supplierSubOrder = (ordersAfterSplit.items ?? []).find(
				(o: {channelId: number; id: number}) =>
					o.channelId === supplierChannel.id
			);

			expect(supplierSubOrder).toBeDefined();

			supplierSubOrderId = supplierSubOrder.id;
		}).toPass({timeout: 15000});

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminOrdersPage.goto();

		await commerceAdminOrdersPage
			.tableRowOrderIdLink(supplierSubOrderId)
			.click();

		await expect(
			commerceAdminOrderDetailsPage.headerDetailsTitle
		).toBeVisible();

		await commerceAdminOrderDetailsPage.createShipmentButton.click();

		await expect(
			commerceAdminOrderDetailsPage.orderStatusProcessing
		).toBeVisible();

		await commerceAdminShipmentsPage.addProductsToShipment.click();

		await (
			await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
				1,
				supplierSku.sku
			)
		).check();
		await commerceAdminShipmentsPage.shipmentsItemSubmitButton.click();
		await commerceAdminShipmentsPage
			.productsSkuLink(supplierSku.sku)
			.click();
		await commerceAdminShipmentsPage.addQuantityInShipment.fill('1');
		await commerceAdminShipmentsPage.editProductSaveButton.click();

		await waitForAlert(commerceAdminShipmentsPage.editProductFrame);

		await commerceAdminShipmentsPage.editProductCloseButton.click();

		await commerceAdminShipmentsPage
			.shipmentStatusLink('Finish Processing')
			.click();

		await waitForAlert(page);

		await commerceAdminShipmentsPage.shipmentStatusLink('Ship').click();

		await waitForAlert(page);

		await commerceAdminShipmentsPage.shipmentStatusLink('Deliver').click();

		await waitForAlert(page);
	}
);

for (const variant of [
	{
		baseSuffix: 'Base Price List',
		label: 'price list',
		tag: '@COMMERCE-11844',
		type: 'price-list' as const,
	},
	{
		baseSuffix: 'Base Promotion',
		label: 'promotion',
		tag: '@COMMERCE-11843',
		type: 'promotion' as const,
	},
]) {
	test(
		`Supplier user's Parent ${variant.label} autocomplete shows only entries from their linked catalog`,
		{tag: [variant.tag, '@COMMERCE-11844', '@LPD-88485', '@LPD-89343']},
		async ({
			apiHelpers,
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			commerceAdminPromotionsPage,
			page,
		}) => {
			const randomSuffix = getRandomString().slice(0, 8);

			const {account, supplierUser} = await createAccountWithSupplierUser(
				apiHelpers,
				site.id
			);

			const linkedCatalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					accountId: account.id,
					name: `Linked Catalog ${randomSuffix}`,
				});

			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: `Test Catalog ${randomSuffix}`,
			});

			const entryName = `Test ${variant.label} ${randomSuffix}`;

			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: linkedCatalog.id,
				currencyCode: 'USD',
				name: entryName,
				type: variant.type,
			});

			await performUserSwitch(page, supplierUser.alternateName);

			if (variant.type === 'price-list') {
				await commerceAdminPriceListsPage.goto();

				await commerceAdminPriceListsPage
					.priceListLink(entryName)
					.click();
			}
			else {
				await commerceAdminPromotionsPage.goto();

				await commerceAdminPromotionsPage
					.promotionLink(entryName)
					.click();
			}

			await commerceAdminPriceListDetailsPage.parentAutocomplete.click();

			await expect(
				commerceAdminPriceListDetailsPage.parentDropdownItem(
					`${linkedCatalog.name} ${variant.baseSuffix}`
				)
			).toBeVisible();
			await expect(
				commerceAdminPriceListDetailsPage.parentDropdownItem(
					`Master ${variant.baseSuffix}`
				)
			).toHaveCount(0);
		}
	);
}

for (const variant of [
	{
		baseSuffix: 'Base Price List',
		label: 'price list',
		tag: '@COMMERCE-11845',
		type: 'price-list' as const,
	},
	{
		baseSuffix: 'Base Promotion',
		label: 'promotion',
		tag: '@COMMERCE-11843',
		type: 'promotion' as const,
	},
]) {
	test(
		`Supplier user can edit a ${variant.label}'s details`,
		{tag: [variant.tag, '@COMMERCE-11845', '@LPD-88485', '@LPD-89343']},
		async ({
			apiHelpers,
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			commerceAdminPromotionsPage,
			page,
		}) => {
			const randomSuffix = getRandomString().slice(0, 8);

			const {account, supplierUser} = await createAccountWithSupplierUser(
				apiHelpers,
				site.id
			);

			const linkedCatalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					accountId: account.id,
					name: `Linked Catalog ${randomSuffix}`,
				});

			const parentName = `${linkedCatalog.name} ${variant.baseSuffix}`;

			const parentBase =
				variant.type === 'price-list'
					? await apiHelpers.headlessCommerceAdminPricing.getBasePriceList(
							linkedCatalog.id
						)
					: await apiHelpers.headlessCommerceAdminPricing.getBasePromoPriceList(
							linkedCatalog.id
						);

			const originalName = `Test ${variant.label} ${randomSuffix}`;
			const editedName = `${originalName} Edited`;

			const entry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: linkedCatalog.id,
					currencyCode: 'USD',
					name: originalName,
					priority: 0,
					type: variant.type,
				});

			await performUserSwitch(page, supplierUser.alternateName);

			if (variant.type === 'price-list') {
				await commerceAdminPriceListsPage.goto();

				await commerceAdminPriceListsPage
					.priceListLink(originalName)
					.click();
			}
			else {
				await commerceAdminPromotionsPage.goto();

				await commerceAdminPromotionsPage
					.promotionLink(originalName)
					.click();
			}

			await commerceAdminPriceListDetailsPage.nameInput.fill(editedName);
			await commerceAdminPriceListDetailsPage.priorityInput.fill('5');
			await commerceAdminPriceListDetailsPage.currencySelect.selectOption(
				{label: 'EUR'}
			);
			await commerceAdminPriceListDetailsPage.priceTypeSelect.selectOption(
				{label: 'Gross Price'}
			);
			await commerceAdminPriceListDetailsPage.parentAutocomplete.fill(
				parentName
			);
			await commerceAdminPriceListDetailsPage
				.parentDropdownItem(parentName)
				.click();

			await commerceAdminPriceListDetailsPage.publishButton.click();

			await waitForAlert(page);

			const priceListsPage =
				await apiHelpers.headlessCommerceAdminPricing.getPriceLists({
					search: editedName,
				});

			const priceList = priceListsPage.items?.find(
				(priceListResult: {id: number}) =>
					priceListResult.id === entry.id
			);

			expect(priceList.name).toBe(editedName);
			expect(priceList.priority).toBe(5);
			expect(priceList.currencyCode).toBe('EUR');
			expect(priceList.netPrice).toBe(false);
			expect(priceList.parentPriceListId).toBe(parentBase.items[0].id);
		}
	);
}

for (const variant of [
	{
		label: 'price list',
		type: 'price-list' as const,
	},
	{
		label: 'promotion',
		type: 'promotion' as const,
	},
]) {
	test(
		`Supplier user can set up a price modifier on a ${variant.label}`,
		{tag: ['@COMMERCE-11772', '@LPD-88485', '@LPD-89343']},
		async ({
			apiHelpers,
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			commerceAdminPromotionsPage,
			page,
		}) => {
			const randomSuffix = getRandomString().slice(0, 8);

			page.on('dialog', (dialog) => dialog.accept());

			const {account, supplierUser} = await createAccountWithSupplierUser(
				apiHelpers,
				site.id
			);

			const linkedCatalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					accountId: account.id,
					name: `Linked Catalog ${randomSuffix}`,
				});

			const entryName = `Test ${variant.label} ${randomSuffix}`;
			const modifierName = `Test Modifier ${randomSuffix}`;

			const entry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: linkedCatalog.id,
					currencyCode: 'USD',
					name: entryName,
					type: variant.type,
				});

			await performUserSwitch(page, supplierUser.alternateName);

			if (variant.type === 'price-list') {
				await commerceAdminPriceListsPage.goto();

				await commerceAdminPriceListsPage
					.priceListLink(entryName)
					.click();
			}
			else {
				await commerceAdminPromotionsPage.goto();

				await commerceAdminPromotionsPage
					.promotionLink(entryName)
					.click();
			}

			await commerceAdminPriceListDetailsPage.priceModifiersTab.click();
			await commerceAdminPriceListDetailsPage.addPriceModifierButton.click();
			await commerceAdminPriceListDetailsPage.addPriceModifierName.fill(
				modifierName
			);
			await commerceAdminPriceListDetailsPage.addPriceModifierTarget.selectOption(
				'products'
			);
			await commerceAdminPriceListDetailsPage.addPriceModifierType.selectOption(
				'fixed-amount'
			);
			await commerceAdminPriceListDetailsPage.addPriceModifierSaveButton.click();

			await commerceAdminPriceListDetailsPage.addPriceModifierName.waitFor(
				{state: 'hidden'}
			);

			const modifiers =
				await apiHelpers.headlessCommerceAdminPricing.getPriceListModifiers(
					entry.id
				);

			const createdModifier = (modifiers.items ?? []).find(
				(modifier: {title: string}) => modifier.title === modifierName
			);

			expect(createdModifier).toBeDefined();

			await performLoginViaApi({page, screenName: 'test'});

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					active: true,
					catalogId: linkedCatalog.id,
					name: {en_US: `Modifier Product ${randomSuffix}`},
					skus: [
						{
							cost: 0,
							price: 10,
							published: true,
							purchasable: true,
							sku: `MODSKU-${randomSuffix}`,
						},
					],
				});

			await apiHelpers.headlessCommerceAdminPricing.postPriceModifierProduct(
				createdModifier.id,
				product.productId
			);

			const linkedProducts =
				await apiHelpers.headlessCommerceAdminPricing.getPriceModifierProducts(
					createdModifier.id
				);

			expect(linkedProducts.items).toHaveLength(1);
			expect(linkedProducts.items[0].productId).toBe(product.productId);
		}
	);
}

for (const variant of [
	{
		label: 'price list',
		tag: '@COMMERCE-11772',
		type: 'price-list' as const,
	},
	{
		label: 'promotion',
		tag: '@COMMERCE-11843',
		type: 'promotion' as const,
	},
]) {
	test(
		`Supplier user can edit a price modifier on a ${variant.label}`,
		{tag: [variant.tag, '@LPD-88485', '@LPD-89343']},
		async ({
			apiHelpers,
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			commerceAdminPromotionsPage,
			page,
		}) => {
			const randomSuffix = getRandomString().slice(0, 8);

			const {account, supplierUser} = await createAccountWithSupplierUser(
				apiHelpers,
				site.id
			);

			const linkedCatalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					accountId: account.id,
					name: `Linked Catalog ${randomSuffix}`,
				});

			const entryName = `Test ${variant.label} ${randomSuffix}`;
			const modifierName = `Test Modifier ${randomSuffix}`;

			const entry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: linkedCatalog.id,
					currencyCode: 'USD',
					name: entryName,
					type: variant.type,
				});

			const modifier =
				await apiHelpers.headlessCommerceAdminPricing.postPriceModifier(
					entry.id,
					{
						active: false,
						modifierAmount: 0,
						modifierType: 'fixed-amount',
						target: 'products',
						title: modifierName,
					}
				);

			await performUserSwitch(page, supplierUser.alternateName);

			if (variant.type === 'price-list') {
				await commerceAdminPriceListsPage.goto();

				await commerceAdminPriceListsPage
					.priceListLink(entryName)
					.click();
			}
			else {
				await commerceAdminPromotionsPage.goto();

				await commerceAdminPromotionsPage
					.promotionLink(entryName)
					.click();
			}

			await commerceAdminPriceListDetailsPage.priceModifiersTab.click();

			await commerceAdminPriceListDetailsPage
				.priceModifierLink(modifierName)
				.click();

			await commerceAdminPriceListDetailsPage.priceModifierAmountInput.fill(
				'-22'
			);
			await commerceAdminPriceListDetailsPage.priceModifierActiveToggle.click();

			await commerceAdminPriceListDetailsPage.priceModifierSaveButton.click();

			await waitForAlert(
				commerceAdminPriceListDetailsPage.sidePanelFrame
			);

			const priceModifier =
				await apiHelpers.headlessCommerceAdminPricing.getPriceModifier(
					modifier.id
				);

			expect(priceModifier.active).toBe(true);
			expect(Number(priceModifier.modifierAmount)).toBe(-22);
		}
	);
}

for (const variant of [
	{
		label: 'price list',
		tag: '@COMMERCE-11772',
		type: 'price-list' as const,
	},
	{
		label: 'promotion',
		tag: '@COMMERCE-11843',
		type: 'promotion' as const,
	},
]) {
	test(
		`Supplier user's price modifier edit on a ${variant.label} reaches the buyer storefront`,
		{tag: [variant.tag, '@LPD-88485', '@LPD-89343']},
		async ({
			apiHelpers,
			commerceAdminCatalogDetailsPage,
			commerceAdminCatalogsPage,
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			commerceAdminPromotionsPage,
			page,
			productDetailsPage,
		}) => {
			const randomSuffix = getRandomString().slice(0, 8);

			const {buyerUser} = await createAccountWithBuyerUser(
				apiHelpers,
				site.id
			);
			const {account: supplierAccount, supplierUser} =
				await createAccountWithSupplierUser(apiHelpers, site.id);

			await linkSupplierToMiniumCatalog(
				commerceAdminCatalogDetailsPage,
				commerceAdminCatalogsPage,
				page,
				supplierAccount.name
			);

			const skuName = `MOD-SF-SKU-${randomSuffix}`;
			const productName = `Modifier SF Product ${randomSuffix}`;
			const basePrice = 24;

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					active: true,
					catalogId: miniumCatalog.id,
					name: {en_US: productName},
					skus: [
						{
							cost: 0,
							price: basePrice,
							published: true,
							purchasable: true,
							sku: skuName,
						},
					],
				});

			const entryName = `Test ${variant.label} ${randomSuffix}`;

			const entry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: miniumCatalog.id,
					currencyCode: 'USD',
					name: entryName,
					priority: 10,
					type: variant.type,
				});

			const modifierName = `Test Modifier ${randomSuffix}`;

			const modifier =
				await apiHelpers.headlessCommerceAdminPricing.postPriceModifier(
					entry.id,
					{
						active: false,
						modifierAmount: 0,
						modifierType: 'fixed-amount',
						target: 'products',
						title: modifierName,
					}
				);

			await apiHelpers.headlessCommerceAdminPricing.postPriceModifierProduct(
				modifier.id,
				product.productId
			);

			await performUserSwitch(page, supplierUser.alternateName);

			if (variant.type === 'price-list') {
				await commerceAdminPriceListsPage.goto();

				await commerceAdminPriceListsPage
					.priceListLink(entryName)
					.click();
			}
			else {
				await commerceAdminPromotionsPage.goto();

				await commerceAdminPromotionsPage
					.promotionLink(entryName)
					.click();
			}

			await commerceAdminPriceListDetailsPage.priceModifiersTab.click();

			await commerceAdminPriceListDetailsPage
				.priceModifierLink(modifierName)
				.click();

			await commerceAdminPriceListDetailsPage.priceModifierAmountInput.fill(
				'-22'
			);
			await commerceAdminPriceListDetailsPage.priceModifierActiveToggle.click();
			await commerceAdminPriceListDetailsPage.priceModifierSaveButton.click();

			await waitForAlert(
				commerceAdminPriceListDetailsPage.sidePanelFrame
			);

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${encodeURIComponent(productName)}`
			);

			await expect(
				await productDetailsPage.priceField(
					'$ 2.00',
					productDetailsPage.priceContainer
				)
			).toBeVisible();
		}
	);
}

for (const variant of [
	{
		entryPrice: 100,
		expectedStorefrontPrice: '$ 80.00',
		label: 'price list',
		skuBasePrice: 100,
		tag: '@COMMERCE-11868',
		type: 'price-list' as const,
	},
	{
		entryPrice: 50,
		expectedStorefrontPrice: '$ 40.00',
		label: 'promotion',
		skuBasePrice: 100,
		tag: '@COMMERCE-11868',
		type: 'promotion' as const,
	},
]) {
	test(
		`Supplier user's override discount on a ${variant.label} entry reaches the buyer storefront`,
		{tag: [variant.tag, '@LPD-88485', '@LPD-89343']},
		async ({
			apiHelpers,
			commerceAdminCatalogDetailsPage,
			commerceAdminCatalogsPage,
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			commerceAdminPromotionsPage,
			page,
			productDetailsPage,
		}) => {
			const randomSuffix = getRandomString().slice(0, 8);

			const {buyerUser} = await createAccountWithBuyerUser(
				apiHelpers,
				site.id
			);
			const {account: supplierAccount, supplierUser} =
				await createAccountWithSupplierUser(apiHelpers, site.id);

			await linkSupplierToMiniumCatalog(
				commerceAdminCatalogDetailsPage,
				commerceAdminCatalogsPage,
				page,
				supplierAccount.name
			);

			const skuName = `DISC-SF-SKU-${randomSuffix}`;
			const productName = `Discount SF Product ${randomSuffix}`;

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					active: true,
					catalogId: miniumCatalog.id,
					name: {en_US: productName},
					skus: [
						{
							cost: 0,
							price: variant.skuBasePrice,
							published: true,
							purchasable: true,
							sku: skuName,
						},
					],
				});

			const entryName = `Test ${variant.label} ${randomSuffix}`;

			const entry =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: miniumCatalog.id,
					currencyCode: 'USD',
					name: entryName,
					priority: 10,
					type: variant.type,
				});

			await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
				price: variant.entryPrice,
				priceListId: entry.id,
				skuId: product.skus[0].id,
			});

			await performUserSwitch(page, supplierUser.alternateName);

			if (variant.type === 'price-list') {
				await commerceAdminPriceListsPage.goto();

				await commerceAdminPriceListsPage
					.priceListLink(entryName)
					.click();
			}
			else {
				await commerceAdminPromotionsPage.goto();

				await commerceAdminPromotionsPage
					.promotionLink(entryName)
					.click();
			}

			await commerceAdminPriceListDetailsPage.entriesTab.click();

			await commerceAdminPriceListDetailsPage
				.skusTableRowLink(skuName)
				.click();

			await commerceAdminPriceListDetailsPage.sidePanelOverrideDiscountToggle.click();

			await commerceAdminPriceListDetailsPage.sidePanelDiscountLevel1Input.fill(
				'20'
			);
			await commerceAdminPriceListDetailsPage.sidePanelSaveButton.click();

			await waitForAlert(
				commerceAdminPriceListDetailsPage.sidePanelFrame
			);

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${encodeURIComponent(productName)}`
			);

			await expect(
				await productDetailsPage.priceField(
					variant.expectedStorefrontPrice,
					productDetailsPage.priceContainer
				)
			).toBeVisible();
		}
	);
}

test(
	'Supplier user does not see Inventory, Warehouses or Discounts in the Commerce sidebar',
	{tag: ['@COMMERCE-11820', '@COMMERCE-11842', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, commerceAdminPriceListsPage, page}) => {
		const {supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		await performUserSwitch(page, supplierUser.alternateName);

		await commerceAdminPriceListsPage.goto();

		await expect(
			commerceAdminPriceListsPage.sidebarMenuItem('Price Lists')
		).toBeVisible();

		for (const restrictedPortlet of [
			'Discounts',
			'Inventory',
			'Warehouses',
		]) {
			await expect(
				commerceAdminPriceListsPage.sidebarMenuItem(restrictedPortlet)
			).toHaveCount(0);
		}
	}
);

test(
	'A buyer user in a supplier account cannot access the Channels admin',
	{tag: ['@COMMERCE-11820', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, page}) => {
		const randomSuffix = getRandomString().slice(0, 8);

		const supplierAccount = await apiHelpers.headlessAdminUser.postAccount({
			name: `Supplier Account ${randomSuffix}`,
			type: 'supplier',
		});

		const buyerUser = await apiHelpers.headlessAdminUser.postUserAccount({
			alternateName: `buyer${randomSuffix}`,
			emailAddress: `buyer${randomSuffix}@liferay.com`,
			familyName: 'User',
			givenName: `Buyer${randomSuffix}`,
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			supplierAccount.id,
			[buyerUser.emailAddress]
		);

		const accountRoles = await apiHelpers.headlessAdminUser.getAccountRoles(
			supplierAccount.id
		);
		const buyerRole = accountRoles?.items?.find(
			(role: {name: string}) => role.name === 'Buyer'
		);

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			supplierAccount.externalReferenceCode,
			buyerRole.id,
			buyerUser.emailAddress
		);

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			site.id,
			buyerUser.id
		);

		userData[buyerUser.alternateName] = {
			name: buyerUser.givenName,
			password: 'test',
			surname: buyerUser.familyName,
		};

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(
			'/group/guest/~/control_panel/manage?p_p_id=com_liferay_commerce_channel_web_internal_portlet_CommerceChannelsPortlet&p_p_lifecycle=0&p_p_state=maximized'
		);

		await expect(
			page.getByText(
				'You do not have the roles required to access this portlet.'
			)
		).toBeVisible();
	}
);

test(
	'Supplier user cannot access the Channel panel after the Access permission is removed',
	{tag: ['@COMMERCE-11865', '@LPD-88485', '@LPD-89343']},
	async ({apiHelpers, page}) => {
		const companyId = await page.evaluate(() =>
			(window as any).Liferay.ThemeDisplay.getCompanyId()
		);

		const {supplierUser} = await createAccountWithSupplierUser(
			apiHelpers,
			site.id
		);

		const supplierRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Supplier');

		const portletName =
			'com_liferay_commerce_channel_web_internal_portlet_CommerceChannelsPortlet';

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
			'ACCESS_IN_CONTROL_PANEL',
			companyId,
			'0',
			portletName,
			String(companyId),
			String(supplierRole.id),
			'1'
		);

		try {
			await performUserSwitch(page, supplierUser.alternateName);

			await page.goto(
				`/group/guest/~/control_panel/manage?p_p_id=${portletName}&p_p_lifecycle=0&p_p_state=maximized`
			);

			await expect(
				page.getByText(
					'You do not have the roles required to access this portlet.'
				)
			).toBeVisible();
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.addResourcePermission(
				'ACCESS_IN_CONTROL_PANEL',
				companyId,
				'0',
				portletName,
				String(companyId),
				String(supplierRole.id),
				'1'
			);
		}
	}
);
