/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {customFieldsPagesTest} from '../../../../fixtures/customFieldsPagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {createCategories} from '../../../../helpers/CreateCategories';
import {TCustomField} from '../../../../helpers/CustomFieldTypesHelper';
import getGlobalSiteId from '../../../../utils/getGlobalSiteId';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	applicationsMenuPageTest,
	dataApiHelpersTest,
	commercePagesTest,
	customFieldsPagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-41420 Verify configuration list eligibility management is available', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductConfigurationListPage,
	commerceAdminProductConfigurationListsPage,
	page,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const productConfigurationList =
		await apiHelpers.headlessCommerceAdminCatalog.postProductConfigurationList(
			{
				catalogId: catalog.id,
			}
		);

	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	await apiHelpers.headlessCommerceAdminCatalog.postProductConfigurationListAccountGroup(
		accountGroup.id,
		productConfigurationList.id
	);

	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	await apiHelpers.headlessCommerceAdminCatalog.postProductConfigurationListChannel(
		channel.id,
		productConfigurationList.id
	);

	const orderType = await apiHelpers.headlessCommerceAdminOrder.postOrderType(
		{
			active: true,
		}
	);

	await apiHelpers.headlessCommerceAdminCatalog.postProductConfigurationListOrderType(
		orderType.id,
		productConfigurationList.id
	);

	await applicationsMenuPage.goToCommerceProductConfigurationLists(false);

	await expect(
		commerceAdminProductConfigurationListsPage.table
	).toBeVisible();
	await expect(
		await page.getByText(productConfigurationList.name)
	).toBeVisible();

	await page.getByText(productConfigurationList.name).click();

	await expect(
		commerceAdminProductConfigurationListPage.eligibilitiesTab
	).toBeVisible();

	await commerceAdminProductConfigurationListPage.eligibilitiesTab.click();

	await expect(
		await commerceAdminProductConfigurationListPage.accountElgibilityTitle
	).toBeVisible();
	await expect(await page.getByText(accountGroup.name)).toBeVisible();
	await expect(
		await commerceAdminProductConfigurationListPage.channelElgibilityTitle
	).toBeVisible();
	await expect(await page.getByText(channel.name)).toBeVisible();
	await expect(
		await commerceAdminProductConfigurationListPage.orderTypeElgibilityTitle
	).toBeVisible();
	await expect(await page.getByText(orderType.name['en_US'])).toBeVisible();
});

test('LPD-41420 Verify configuration list eligibility management save button clears out fields when All option is selected', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductConfigurationListPage,
	commerceAdminProductConfigurationListsPage,
	page,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const productConfigurationList =
		await apiHelpers.headlessCommerceAdminCatalog.postProductConfigurationList(
			{
				catalogId: catalog.id,
			}
		);

	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	await apiHelpers.headlessCommerceAdminCatalog.postProductConfigurationListAccountGroup(
		accountGroup.id,
		productConfigurationList.id
	);

	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	await apiHelpers.headlessCommerceAdminCatalog.postProductConfigurationListChannel(
		channel.id,
		productConfigurationList.id
	);

	const orderType = await apiHelpers.headlessCommerceAdminOrder.postOrderType(
		{
			active: true,
		}
	);

	await apiHelpers.headlessCommerceAdminCatalog.postProductConfigurationListOrderType(
		orderType.id,
		productConfigurationList.id
	);

	await applicationsMenuPage.goToCommerceProductConfigurationLists(false);

	await expect(
		commerceAdminProductConfigurationListsPage.table
	).toBeVisible();
	await expect(
		await page.getByText(productConfigurationList.name)
	).toBeVisible();

	await page.getByText(productConfigurationList.name).click();

	await expect(
		commerceAdminProductConfigurationListPage.eligibilitiesTab
	).toBeVisible();

	await commerceAdminProductConfigurationListPage.eligibilitiesTab.click();

	await expect(
		await commerceAdminProductConfigurationListPage.accountElgibilityTitle
	).toBeVisible();
	await expect(await page.getByText(accountGroup.name)).toBeVisible();

	await commerceAdminProductConfigurationListPage.allAccountsLabel.check();

	await expect(
		await commerceAdminProductConfigurationListPage.allAccountsLabel
	).toBeChecked();

	await commerceAdminProductConfigurationListPage.saveButton.click;
	await commerceAdminProductConfigurationListPage.eligibilitiesTab.click();

	await expect(await page.getByText(accountGroup.name)).toBeHidden();
	await expect(
		await commerceAdminProductConfigurationListPage.channelElgibilityTitle
	).toBeVisible();
	await expect(await page.getByText(channel.name)).toBeVisible();

	await commerceAdminProductConfigurationListPage.allChannelsLabel.check();

	await expect(
		await commerceAdminProductConfigurationListPage.allChannelsLabel
	).toBeChecked();

	await commerceAdminProductConfigurationListPage.saveButton.click();
	await commerceAdminProductConfigurationListPage.eligibilitiesTab.click();

	await expect(await page.getByText(channel.name)).toBeHidden();
	await expect(
		await commerceAdminProductConfigurationListPage.orderTypeElgibilityTitle
	).toBeVisible();
	await expect(await page.getByText(orderType.name['en_US'])).toBeVisible();

	await commerceAdminProductConfigurationListPage.allOrderTypesLabel.check();

	await expect(
		await commerceAdminProductConfigurationListPage.allOrderTypesLabel
	).toBeChecked();

	await commerceAdminProductConfigurationListPage.saveButton.click();
	await commerceAdminProductConfigurationListPage.eligibilitiesTab.click();

	await expect(await page.getByText(orderType.name['en_US'])).toBeHidden();
});

test('LPD-42555 Verify configuration list table appears', async ({
	applicationsMenuPage,
	commerceAdminProductConfigurationListsPage,
}) => {
	await applicationsMenuPage.goToCommerceProductConfigurationLists(false);

	await expect(
		commerceAdminProductConfigurationListsPage.table
	).toBeVisible();
});

test('LPD-43390 Create child configuration list', async ({
	applicationsMenuPage,
	commerceAdminProductConfigurationListsPage,
}) => {
	await applicationsMenuPage.goToCommerceProductConfigurationLists(false);

	await expect(
		commerceAdminProductConfigurationListsPage.table
	).toBeVisible();

	await commerceAdminProductConfigurationListsPage.addConfigurationList.click();

	await expect(
		commerceAdminProductConfigurationListsPage.addConfigurationListParentList
	).toBeVisible();

	await commerceAdminProductConfigurationListsPage.addConfigurationListName.fill(
		'Test'
	);
	await commerceAdminProductConfigurationListsPage.addConfigurationListPriority.fill(
		'1'
	);
	await commerceAdminProductConfigurationListsPage.addConfigurationListCatalog.selectOption(
		{label: 'Master'}
	);
	await commerceAdminProductConfigurationListsPage.addConfigurationListParentList.click();
	await commerceAdminProductConfigurationListsPage
		.addConfigurationListParentListElement()
		.click();
	await commerceAdminProductConfigurationListsPage.addConfigurationListSaveButton.click();

	await expect(
		commerceAdminProductConfigurationListsPage.newConfigurationListName
	).toHaveText('Test');
});

test('LPD-43013 Configuration Entry form in side panel', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductConfigurationEntriesPage,
	commerceAdminProductConfigurationEntryPage,
	commerceAdminProductConfigurationListPage,
	commerceAdminProductConfigurationListsPage,
	page,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	const configurationLists =
		await apiHelpers.headlessCommerceAdminCatalog.getProductConfigurationListsPage();

	expect(configurationLists.items?.length).toBeGreaterThan(0);

	const configurationList = configurationLists.items.find((item) =>
		item.name.includes(catalog.name)
	);

	expect(configurationList).not.toBeNull();

	await applicationsMenuPage.goToCommerceProductConfigurationLists();

	await (
		await commerceAdminProductConfigurationListsPage.tableRowLink({
			colIndex: 1,
			rowValue: configurationList.name,
		})
	).click();

	await commerceAdminProductConfigurationListPage.entriesMenuItem.click();

	await expect(
		commerceAdminProductConfigurationEntriesPage.table
	).toBeVisible();
	await expect(
		commerceAdminProductConfigurationListPage.saveButton
	).not.toBeVisible();

	await (
		await commerceAdminProductConfigurationEntriesPage.tableRowLink({
			colIndex: 1,
			rowValue: product.name['en_US'],
		})
	).click();

	await expect(
		commerceAdminProductConfigurationEntryPage.sidePanelTitle
	).toBeVisible();

	await commerceAdminProductConfigurationEntryPage.allowedOrderQuantitiesInput.fill(
		'1 2'
	);
	await commerceAdminProductConfigurationEntryPage.backOrdersInput.click();
	await commerceAdminProductConfigurationEntryPage.CPDefinitionInventoryEngineInput.selectOption(
		'default'
	);
	await commerceAdminProductConfigurationEntryPage.depthInput.fill('2');
	await commerceAdminProductConfigurationEntryPage.displayAvailabilityInput.click();
	await commerceAdminProductConfigurationEntryPage.displayStockQuantityInput.click();
	await commerceAdminProductConfigurationEntryPage.freeShippingInput.click();
	await commerceAdminProductConfigurationEntryPage.heightInput.fill('3');
	await commerceAdminProductConfigurationEntryPage.lowStockActivityInput.selectOption(
		'default'
	);
	await commerceAdminProductConfigurationEntryPage.maxOrderQuantityInput.fill(
		'400'
	);
	await commerceAdminProductConfigurationEntryPage.minOrderQuantityInput.fill(
		'5'
	);
	await commerceAdminProductConfigurationEntryPage.minStockQuantityInput.fill(
		'6'
	);
	await commerceAdminProductConfigurationEntryPage.multipleOrderQuantityInput.fill(
		'7'
	);
	await commerceAdminProductConfigurationEntryPage.purchasableInput.click();
	await commerceAdminProductConfigurationEntryPage.shippableInput.click();

	await expect(
		commerceAdminProductConfigurationEntryPage.freeShippingInput
	).toBeHidden();
	await expect(
		commerceAdminProductConfigurationEntryPage.shipSeparatelyInput
	).toBeHidden();
	await expect(
		commerceAdminProductConfigurationEntryPage.depthInput
	).toBeHidden();
	await expect(
		commerceAdminProductConfigurationEntryPage.heightInput
	).toBeHidden();
	await expect(
		commerceAdminProductConfigurationEntryPage.weightInput
	).toBeHidden();
	await expect(
		commerceAdminProductConfigurationEntryPage.widthInput
	).toBeHidden();

	await commerceAdminProductConfigurationEntryPage.shippableInput.click();

	await commerceAdminProductConfigurationEntryPage.shipSeparatelyInput.click();
	await commerceAdminProductConfigurationEntryPage.taxExemptInput.click();
	await commerceAdminProductConfigurationEntryPage.weightInput.fill('8');
	await commerceAdminProductConfigurationEntryPage.widthInput.fill('9');

	await commerceAdminProductConfigurationEntryPage.saveButton.click();

	await waitForAlert(page);

	await (
		await commerceAdminProductConfigurationEntriesPage.tableRowLink({
			colIndex: 1,
			rowValue: product.name['en_US'],
		})
	).click();

	await expect(
		commerceAdminProductConfigurationEntryPage.sidePanelTitle
	).toBeVisible();
	await expect(
		commerceAdminProductConfigurationEntryPage.allowedOrderQuantitiesInput
	).toHaveValue('1 2');
	await expect(
		commerceAdminProductConfigurationEntryPage.backOrdersInput
	).not.toBeChecked();
	await expect(
		commerceAdminProductConfigurationEntryPage.CPDefinitionInventoryEngineInput
	).toHaveValue('default');
	await expect(
		commerceAdminProductConfigurationEntryPage.depthInput
	).toHaveValue('2.0');
	await expect(
		commerceAdminProductConfigurationEntryPage.displayAvailabilityInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationEntryPage.displayStockQuantityInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationEntryPage.freeShippingInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationEntryPage.heightInput
	).toHaveValue('3.0');
	await expect(
		commerceAdminProductConfigurationEntryPage.lowStockActivityInput
	).toHaveValue('default');
	await expect(
		commerceAdminProductConfigurationEntryPage.maxOrderQuantityInput
	).toHaveValue('400.0');
	await expect(
		commerceAdminProductConfigurationEntryPage.minOrderQuantityInput
	).toHaveValue('5.0');
	await expect(
		commerceAdminProductConfigurationEntryPage.minStockQuantityInput
	).toHaveValue('6.0');
	await expect(
		commerceAdminProductConfigurationEntryPage.multipleOrderQuantityInput
	).toHaveValue('7.0');
	await expect(
		commerceAdminProductConfigurationEntryPage.purchasableInput
	).not.toBeChecked();
	await expect(
		commerceAdminProductConfigurationEntryPage.shippableInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationEntryPage.shipSeparatelyInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationEntryPage.taxExemptInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationEntryPage.weightInput
	).toHaveValue('8.0');
	await expect(
		commerceAdminProductConfigurationEntryPage.widthInput
	).toHaveValue('9.0');
});

test('LPD-43013 Configuration Entry form in side panel for virtual products', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductConfigurationEntriesPage,
	commerceAdminProductConfigurationEntryPage,
	commerceAdminProductConfigurationListPage,
	commerceAdminProductConfigurationListsPage,
	page,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		productType: 'virtual',
	});

	const configurationLists =
		await apiHelpers.headlessCommerceAdminCatalog.getProductConfigurationListsPage();

	expect(configurationLists.items?.length).toBeGreaterThan(0);

	const configurationList = configurationLists.items.find((item) =>
		item.name.includes(catalog.name)
	);

	expect(configurationList).not.toBeNull();

	await applicationsMenuPage.goToCommerceProductConfigurationLists();

	await (
		await commerceAdminProductConfigurationListsPage.tableRowLink({
			colIndex: 1,
			rowValue: configurationList.name,
		})
	).click();

	await commerceAdminProductConfigurationListPage.entriesMenuItem.click();

	await expect(
		commerceAdminProductConfigurationEntriesPage.table
	).toBeVisible();
	await expect(
		commerceAdminProductConfigurationListPage.saveButton
	).not.toBeVisible();

	await (
		await commerceAdminProductConfigurationEntriesPage.tableRowLink({
			colIndex: 1,
			rowValue: product.name['en_US'],
		})
	).click();

	await expect(
		commerceAdminProductConfigurationEntryPage.sidePanelTitle
	).toBeVisible();

	await commerceAdminProductConfigurationEntryPage.allowedOrderQuantitiesInput.fill(
		'1 2'
	);
	await commerceAdminProductConfigurationEntryPage.backOrdersInput.click();

	await expect(
		commerceAdminProductConfigurationEntryPage.shippableInput
	).toBeDisabled();

	await commerceAdminProductConfigurationEntryPage.saveButton.click();

	await waitForAlert(page);
});

test('LPD-43013 Edit configuration template', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductConfigurationListPage,
	commerceAdminProductConfigurationListsPage,
	page,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const configurationLists =
		await apiHelpers.headlessCommerceAdminCatalog.getProductConfigurationListsPage();

	expect(configurationLists.items?.length).toBeGreaterThan(0);

	const configurationList = configurationLists.items.find((item) =>
		item.name.includes(catalog.name)
	);

	expect(configurationList).not.toBeNull();

	await applicationsMenuPage.goToCommerceProductConfigurationLists();

	await (
		await commerceAdminProductConfigurationListsPage.tableRowLink({
			colIndex: 1,
			rowValue: configurationList.name,
		})
	).click();

	await expect(
		commerceAdminProductConfigurationListPage.detailsMenuItem
	).toBeVisible();
	await expect(
		commerceAdminProductConfigurationListPage.catalogNameInput
	).toBeDisabled();
	await expect(
		commerceAdminProductConfigurationListPage.catalogNameInput
	).toHaveValue(catalog.name);
	await expect(
		commerceAdminProductConfigurationListPage.parentCPConfigurationListNameInput
	).toHaveCount(0);
	await expect(
		commerceAdminProductConfigurationListPage.displayDateInput
	).toHaveCount(0);
	await expect(
		commerceAdminProductConfigurationListPage.expirationDateInput
	).toHaveCount(0);

	await commerceAdminProductConfigurationListPage.nameInput.fill('Name1');
	await commerceAdminProductConfigurationListPage.priorityInput.fill('2');
	await commerceAdminProductConfigurationListPage.allowedOrderQuantitiesInput.fill(
		'1 2'
	);
	await commerceAdminProductConfigurationListPage.backOrdersInput.click();
	await commerceAdminProductConfigurationListPage.CPDefinitionInventoryEngineInput.selectOption(
		'default'
	);
	await commerceAdminProductConfigurationListPage.depthInput.fill('2');
	await commerceAdminProductConfigurationListPage.displayAvailabilityInput.click();
	await commerceAdminProductConfigurationListPage.displayStockQuantityInput.click();
	await commerceAdminProductConfigurationListPage.freeShippingInput.click();
	await commerceAdminProductConfigurationListPage.heightInput.fill('3');
	await commerceAdminProductConfigurationListPage.lowStockActivityInput.selectOption(
		'default'
	);
	await commerceAdminProductConfigurationListPage.maxOrderQuantityInput.fill(
		'400'
	);
	await commerceAdminProductConfigurationListPage.minOrderQuantityInput.fill(
		'5'
	);
	await commerceAdminProductConfigurationListPage.minStockQuantityInput.fill(
		'6'
	);
	await commerceAdminProductConfigurationListPage.multipleOrderQuantityInput.fill(
		'7'
	);
	await commerceAdminProductConfigurationListPage.purchasableInput.click();
	await commerceAdminProductConfigurationListPage.shippableInput.click();

	await expect(
		commerceAdminProductConfigurationListPage.freeShippingInput
	).toBeHidden();
	await expect(
		commerceAdminProductConfigurationListPage.shipSeparatelyInput
	).toBeHidden();
	await expect(
		commerceAdminProductConfigurationListPage.depthInput
	).toBeHidden();
	await expect(
		commerceAdminProductConfigurationListPage.heightInput
	).toBeHidden();
	await expect(
		commerceAdminProductConfigurationListPage.weightInput
	).toBeHidden();
	await expect(
		commerceAdminProductConfigurationListPage.widthInput
	).toBeHidden();

	await commerceAdminProductConfigurationListPage.shippableInput.click();

	await commerceAdminProductConfigurationListPage.shipSeparatelyInput.click();
	await commerceAdminProductConfigurationListPage.taxExemptInput.click();
	await commerceAdminProductConfigurationListPage.weightInput.fill('8');
	await commerceAdminProductConfigurationListPage.widthInput.fill('9');

	await commerceAdminProductConfigurationListPage.saveButton.click();

	await waitForAlert(page);

	await page.reload();

	await expect(
		commerceAdminProductConfigurationListPage.nameInput
	).toHaveValue('Name1');
	await expect(
		commerceAdminProductConfigurationListPage.catalogNameInput
	).toBeDisabled();
	await expect(
		commerceAdminProductConfigurationListPage.catalogNameInput
	).toHaveValue(catalog.name);
	await expect(
		commerceAdminProductConfigurationListPage.priorityInput
	).toHaveValue('2.0');
	await expect(
		commerceAdminProductConfigurationListPage.allowedOrderQuantitiesInput
	).toHaveValue('1 2');
	await expect(
		commerceAdminProductConfigurationListPage.backOrdersInput
	).not.toBeChecked();
	await expect(
		commerceAdminProductConfigurationListPage.CPDefinitionInventoryEngineInput
	).toHaveValue('default');
	await expect(
		commerceAdminProductConfigurationListPage.depthInput
	).toHaveValue('2.0');
	await expect(
		commerceAdminProductConfigurationListPage.displayAvailabilityInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationListPage.displayStockQuantityInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationListPage.freeShippingInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationListPage.heightInput
	).toHaveValue('3.0');
	await expect(
		commerceAdminProductConfigurationListPage.lowStockActivityInput
	).toHaveValue('default');
	await expect(
		commerceAdminProductConfigurationListPage.maxOrderQuantityInput
	).toHaveValue('400.0');
	await expect(
		commerceAdminProductConfigurationListPage.minOrderQuantityInput
	).toHaveValue('5.0');
	await expect(
		commerceAdminProductConfigurationListPage.minStockQuantityInput
	).toHaveValue('6.0');
	await expect(
		commerceAdminProductConfigurationListPage.multipleOrderQuantityInput
	).toHaveValue('7.0');
	await expect(
		commerceAdminProductConfigurationListPage.purchasableInput
	).not.toBeChecked();
	await expect(
		commerceAdminProductConfigurationListPage.shippableInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationListPage.shipSeparatelyInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationListPage.taxExemptInput
	).toBeChecked();
	await expect(
		commerceAdminProductConfigurationListPage.weightInput
	).toHaveValue('8.0');
	await expect(
		commerceAdminProductConfigurationListPage.widthInput
	).toHaveValue('9.0');
});

test('LPD-37882 Show purchasable field', async ({
	apiHelpers,
	commerceAdminProductDetailsConfigurationPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
	page,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	let product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	expect(product.skus[0].purchasable).toBeTruthy();

	await commerceAdminProductPage.gotoProduct(product.name['en_US'], false);

	await expect(
		await commerceAdminProductDetailsPage.productSkusLink
	).toBeVisible();

	await commerceAdminProductDetailsPage.goToProductConfiguration();

	await expect(
		commerceAdminProductDetailsConfigurationPage.purchasableInput
	).toBeVisible();
	await expect(
		commerceAdminProductDetailsConfigurationPage.purchasableInput
	).toBeChecked();

	await commerceAdminProductDetailsConfigurationPage.purchasableInput.click();
	await commerceAdminProductDetailsConfigurationPage.publishLink.click();

	await waitForAlert(page);

	await page.reload();

	await expect(
		commerceAdminProductDetailsConfigurationPage.purchasableInput
	).not.toBeChecked();

	product = await apiHelpers.headlessCommerceAdminCatalog.getProduct(
		product.productId
	);

	expect(product.skus[0].purchasable).toBeFalsy();
});

test(
	'Can filter configuration entries dataset',
	{tag: '@LPD-37886'},
	async ({
		apiHelpers,
		applicationsMenuPage,
		commerceAdminProductConfigurationEntriesPage,
		commerceAdminProductConfigurationListsPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'Catalog',
			});

		const categoryName = getRandomString();

		const siteId = await getGlobalSiteId(apiHelpers);

		const categories: Array<any> = await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName}],
			siteId,
			vocabularyName: getRandomString(),
		});

		apiHelpers.data.push({
			id: categories[0].vocabularyId,
			type: 'taxonomyVocabulary',
		});

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				categories,
				name: {
					en_US: 'Product 1',
				},
			});
		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {
					en_US: 'Product 2',
				},
			});

		const productConfigurationList =
			await apiHelpers.headlessCommerceAdminCatalog.postProductConfigurationList(
				{
					catalogId: catalog.id,
					name: getRandomString(),
					productConfigurations: [
						{
							allowBackOrder: true,
							entityId: product1.id,
							maxOrderQuantity: 10000,
							minOrderQuantity: 1,
							multipleOrderQuantity: 1,
							productShippingConfiguration: {
								shippable: true,
							},
							purchasable: true,
						},
						{
							allowBackOrder: true,
							entityId: product2.id,
							maxOrderQuantity: 10000,
							minOrderQuantity: 1,
							multipleOrderQuantity: 1,
							productShippingConfiguration: {
								shippable: false,
							},
							purchasable: false,
						},
					],
				}
			);

		await applicationsMenuPage.goToCommerceProductConfigurationLists(false);

		await (
			await commerceAdminProductConfigurationListsPage.tableRowLink({
				colIndex: 1,
				rowValue: productConfigurationList.name,
			})
		).click();
		await commerceAdminProductConfigurationListsPage.entriesLink.click();

		await expect(
			(
				await commerceAdminProductConfigurationEntriesPage.tableRow(
					1,
					product1.name['en_US'],
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await commerceAdminProductConfigurationEntriesPage.tableRow(
					1,
					product2.name['en_US'],
					true
				)
			).row
		).toBeVisible();

		await commerceAdminProductConfigurationEntriesPage.addDataSetFilter(
			'Category',
			categories[0].name
		);

		await expect(
			(
				await commerceAdminProductConfigurationEntriesPage.tableRow(
					1,
					product1.name['en_US'],
					true
				)
			).row
		).toBeVisible();

		try {
			await expect(
				(
					await commerceAdminProductConfigurationEntriesPage.tableRow(
						1,
						product2.name['en_US'],
						true
					)
				).row
			).toHaveCount(0);
		}
		catch (error) {
			expect(error).toBeDefined();
		}

		await commerceAdminProductConfigurationEntriesPage.resetFiltersButton.click();
		await commerceAdminProductConfigurationEntriesPage.addDataSetFilter(
			'Product Type',
			'Simple',
			true,
			true
		);

		await expect(
			commerceAdminProductConfigurationEntriesPage.noResultsText
		).toBeVisible();

		await commerceAdminProductConfigurationEntriesPage.resetFiltersButton.click();
		await commerceAdminProductConfigurationEntriesPage.addDataSetFilter(
			'Purchasable',
			'Yes',
			false,
			true
		);

		await expect(
			(
				await commerceAdminProductConfigurationEntriesPage.tableRow(
					1,
					product1.name['en_US'],
					true
				)
			).row
		).toBeVisible();

		try {
			await expect(
				(
					await commerceAdminProductConfigurationEntriesPage.tableRow(
						1,
						product2.name['en_US'],
						true
					)
				).row
			).toHaveCount(0);
		}
		catch (error) {
			expect(error).toBeDefined();
		}

		await commerceAdminProductConfigurationEntriesPage.resetFiltersButton.click();
		await commerceAdminProductConfigurationEntriesPage.addDataSetFilter(
			'Shippable',
			'Yes',
			false,
			true
		);

		await expect(
			(
				await commerceAdminProductConfigurationEntriesPage.tableRow(
					1,
					product1.name['en_US'],
					true
				)
			).row
		).toBeVisible();

		try {
			await expect(
				(
					await commerceAdminProductConfigurationEntriesPage.tableRow(
						1,
						product2.name['en_US'],
						true
					)
				).row
			).toHaveCount(0);
		}
		catch (error) {
			expect(error).toBeDefined();
		}
	}
);

test('LPD-43013 Edit child configuration list', async ({
	addCustomFieldPage,
	applicationsMenuPage,
	commerceAdminProductConfigurationListPage,
	commerceAdminProductConfigurationListsPage,
	page,
}) => {
	const customField: TCustomField = {
		fieldName: getRandomString(),
		fieldType: 'inputField',
		resource: 'Product Configuration List',
	};

	await addCustomFieldPage.addCustomField(customField);

	await applicationsMenuPage.goToCommerceProductConfigurationLists(false);

	await expect(
		commerceAdminProductConfigurationListsPage.table
	).toBeVisible();

	await commerceAdminProductConfigurationListsPage.addConfigurationList.click();

	await expect(
		commerceAdminProductConfigurationListsPage.addConfigurationListParentList
	).toBeVisible();

	await commerceAdminProductConfigurationListsPage.addConfigurationListName.fill(
		'Test'
	);
	await commerceAdminProductConfigurationListsPage.addConfigurationListPriority.fill(
		'1'
	);
	await commerceAdminProductConfigurationListsPage.addConfigurationListCatalog.selectOption(
		{label: 'Master'}
	);
	await commerceAdminProductConfigurationListsPage.addConfigurationListParentList.click();
	await commerceAdminProductConfigurationListsPage
		.addConfigurationListParentListElement()
		.click();
	await commerceAdminProductConfigurationListsPage.addConfigurationListSaveButton.click();

	await expect(
		commerceAdminProductConfigurationListsPage.newConfigurationListName
	).toHaveText('Test');
	await expect(
		commerceAdminProductConfigurationListPage.detailsMenuItem
	).toBeVisible();
	await expect(
		commerceAdminProductConfigurationListPage.catalogNameInput
	).toBeDisabled();
	await expect(
		commerceAdminProductConfigurationListPage.catalogNameInput
	).toHaveValue('Master');
	await expect(
		commerceAdminProductConfigurationListPage.parentCPConfigurationListNameInput
	).toBeDisabled();
	await expect(
		commerceAdminProductConfigurationListPage.parentCPConfigurationListNameInput
	).toHaveValue('Master Configuration Master');
	await expect(
		commerceAdminProductConfigurationListPage.displayDateInput
	).toBeEnabled();
	await expect(
		commerceAdminProductConfigurationListPage.expirationDateInput
	).toBeDisabled();
	await expect(
		commerceAdminProductConfigurationListPage.allowedOrderQuantitiesInput
	).toHaveCount(0);

	await commerceAdminProductConfigurationListPage.nameInput.fill('Test1');
	await commerceAdminProductConfigurationListPage.priorityInput.fill('2');
	await commerceAdminProductConfigurationListPage.neverExpireInput.click();

	const randomString = getRandomString();

	await commerceAdminProductConfigurationListPage
		.customFieldInput(customField.fieldName)
		.fill(randomString);

	await commerceAdminProductConfigurationListPage.saveButton.click();

	await waitForAlert(page);

	await page.reload();

	await expect(
		commerceAdminProductConfigurationListPage.nameInput
	).toHaveValue('Test1');
	await expect(
		commerceAdminProductConfigurationListPage.catalogNameInput
	).toBeDisabled();
	await expect(
		commerceAdminProductConfigurationListPage.catalogNameInput
	).toHaveValue('Master');
	await expect(
		commerceAdminProductConfigurationListPage.parentCPConfigurationListNameInput
	).toBeDisabled();
	await expect(
		commerceAdminProductConfigurationListPage.parentCPConfigurationListNameInput
	).toHaveValue('Master Configuration Master');
	await expect(
		commerceAdminProductConfigurationListPage.priorityInput
	).toHaveValue('2.0');
	await expect(
		commerceAdminProductConfigurationListPage.expirationDateInput
	).toBeEnabled();
	await expect(
		commerceAdminProductConfigurationListPage.customFieldInput(
			customField.fieldName
		)
	).toHaveValue(randomString);
});

test('LPD-44818 Show difference icons', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductConfigurationEntriesPage,
	commerceAdminProductConfigurationEntryPage,
	commerceAdminProductConfigurationListPage,
	commerceAdminProductConfigurationListsPage,
	page,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	const configurationLists =
		await apiHelpers.headlessCommerceAdminCatalog.getProductConfigurationListsPage();

	expect(configurationLists.items?.length).toBeGreaterThan(0);

	const configurationList = configurationLists.items.find((item) =>
		item.name.includes(catalog.name)
	);

	expect(configurationList).not.toBeNull();

	await applicationsMenuPage.goToCommerceProductConfigurationLists();

	await commerceAdminProductConfigurationListsPage.addConfigurationList.click();

	await expect(
		commerceAdminProductConfigurationListsPage.addConfigurationListParentList
	).toBeVisible();

	await commerceAdminProductConfigurationListsPage.addConfigurationListName.fill(
		`Child Configuration ${catalog.name}`
	);
	await commerceAdminProductConfigurationListsPage.addConfigurationListPriority.fill(
		'1'
	);
	await commerceAdminProductConfigurationListsPage.addConfigurationListCatalog.selectOption(
		{label: catalog.name}
	);
	await commerceAdminProductConfigurationListsPage.addConfigurationListParentList.click();
	await commerceAdminProductConfigurationListsPage
		.addConfigurationListParentListElement(
			`Master Configuration ${catalog.name}`
		)
		.click();
	await commerceAdminProductConfigurationListsPage.addConfigurationListSaveButton.click();

	await expect(
		commerceAdminProductConfigurationListPage.parentCPConfigurationListNameInput
	).toBeDisabled();
	await expect(
		commerceAdminProductConfigurationListPage.parentCPConfigurationListNameInput
	).toHaveValue(`Master Configuration ${catalog.name}`);

	await commerceAdminProductConfigurationListPage.entriesMenuItem.click();

	await expect(
		commerceAdminProductConfigurationEntriesPage.table
	).toBeVisible();
	await expect(
		commerceAdminProductConfigurationListPage.saveButton
	).not.toBeVisible();

	await expect(
		commerceAdminProductConfigurationEntriesPage.differenceIcon()
	).toHaveCount(0);

	await (
		await commerceAdminProductConfigurationEntriesPage.tableRowLink({
			colIndex: 1,
			rowValue: product.name['en_US'],
		})
	).click();

	await expect(
		commerceAdminProductConfigurationEntryPage.sidePanelTitle
	).toBeVisible();

	await commerceAdminProductConfigurationEntryPage.maxOrderQuantityInput.fill(
		'400'
	);

	await commerceAdminProductConfigurationEntryPage.saveButton.click();

	await waitForAlert(page);

	await page.reload();

	await expect(
		commerceAdminProductConfigurationEntriesPage.differenceIcon()
	).toHaveCount(0);

	await (
		await commerceAdminProductConfigurationEntriesPage.tableRowLink({
			colIndex: 1,
			rowValue: product.name['en_US'],
		})
	).click();

	await expect(
		commerceAdminProductConfigurationEntryPage.sidePanelTitle
	).toBeVisible();

	await commerceAdminProductConfigurationEntryPage.maxOrderQuantityInput.fill(
		'500'
	);

	await commerceAdminProductConfigurationEntryPage.saveButton.click();

	await waitForAlert(page);

	await expect(
		commerceAdminProductConfigurationEntriesPage.differenceIcon()
	).toHaveCount(2);
	await expect(
		commerceAdminProductConfigurationEntriesPage.differenceIcon(
			(
				await commerceAdminProductConfigurationEntriesPage.tableRow(
					1,
					product.name['en_US']
				)
			).column
		)
	).toHaveCount(1);
	await expect(
		commerceAdminProductConfigurationEntriesPage.differenceIcon(
			(
				await commerceAdminProductConfigurationEntriesPage.tableRow(
					3,
					'yes'
				)
			).column
		)
	).toHaveCount(0);

	await (
		await commerceAdminProductConfigurationEntriesPage.tableRowLink({
			colIndex: 1,
			rowValue: product.name['en_US'],
		})
	).click();

	await commerceAdminProductConfigurationEntryPage.maxOrderQuantityInput.fill(
		'400'
	);

	await commerceAdminProductConfigurationEntryPage.saveButton.click();

	await waitForAlert(page);

	await page.reload();

	await expect(
		commerceAdminProductConfigurationEntriesPage.differenceIcon()
	).toHaveCount(0);
});

test(
	'Product configuration tab saves changes on new simple product',
	{tag: ['@LPD-52711']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsConfigurationPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				productStatus: 2,
			});

		await commerceAdminProductPage.gotoProduct(
			product.name['en_US'],
			false
		);

		await expect(
			await commerceAdminProductDetailsPage.productConfigurationLink
		).toBeVisible();

		await commerceAdminProductDetailsPage.goToProductConfiguration();

		const minStockQuantity =
			await commerceAdminProductDetailsConfigurationPage.minStockQuantityInput.inputValue();

		await expect(minStockQuantity).toEqual('0.0');

		await expect(page.locator('.workflow-status-draft')).toBeVisible();

		await commerceAdminProductDetailsConfigurationPage.publishLink.click();

		await waitForAlert(page);

		await expect(page.locator('.workflow-status-approved')).toBeVisible();
	}
);
