/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {EFDSVisualizationMode, waitForFDS} from '../../../utils/waitFor';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {itemSelectorSamplePageTest} from './fixtures/itemSelectorSamplePageTest';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	itemSelectorSamplePageTest,
	isolatedSiteTest,
	loginTest()
);

let imageFile: any;
let jsonFile: any;
let user: null | TUserAccount = null;

test.beforeEach(async ({apiHelpers, itemSelectorSamplePage, site}) => {
	await test.step('Upload sample documents', async () => {
		imageFile = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/sample_image.png')
			),
			{
				description: getRandomString(),
				documentFolderId: 0,
				fileName: getRandomString(),
				title: 'Sample image',
			}
		);

		jsonFile = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/file.json')),
			{
				description: getRandomString(),
				documentFolderId: 0,
				fileName: getRandomString(),
				title: 'JSON File',
			}
		);
	});

	await test.step('Create extra user', async () => {
		user = await apiHelpers.headlessAdminUser.postUserAccount();
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getWidgetDefinition({
				id: getRandomString(),
				widgetName:
					'com_liferay_frontend_js_item_selector_sample_web_portlet_FrontendJSItemSelectorSampleWebPortlet',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await itemSelectorSamplePage.goToPage({layout, site});
});

test.afterEach(async ({apiHelpers}) => {
	imageFile &&
		(await apiHelpers.headlessDelivery.deleteDocument(imageFile.id));
	jsonFile && (await apiHelpers.headlessDelivery.deleteDocument(jsonFile.id));

	if (user) {
		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
	}
});

test('Item Selector Modal with single selection', async ({
	itemSelectorSamplePage,
	page,
}) => {
	await test.step('Check that an Item Selector Modal appears in the page', async () => {
		await expect(page.getByText('Item Selector Modal')).toBeVisible();
	});

	await test.step('Click in the Select User button opens the Item Selector Modal with a FDS component', async () => {
		await itemSelectorSamplePage.selectUserButton.click();

		await expect(
			itemSelectorSamplePage.selectUserModalHeader
		).toBeVisible();

		waitForFDS({page, visualizationMode: EFDSVisualizationMode.CARDS});
	});

	await test.step('Check that a single item can be selected in the Cards visualization mode', async () => {
		await expect(itemSelectorSamplePage.modal.selectButton).toBeDisabled();

		const items = itemSelectorSamplePage.page.locator(
			'.card:has(>.custom-radio)'
		);

		const numOfItems = await items.count();

		expect(numOfItems > 1).toEqual(true);

		const user1Label = items.getByText('Test', {exact: true});

		const user2Label = items.getByText(user?.givenName, {exact: true});

		expect(user1Label).not.toBe(user2Label);

		await user1Label.click();

		await expect(itemSelectorSamplePage.modal.selectButton).toBeEnabled();

		await expect(
			itemSelectorSamplePage.page.getByText(`Test Selected`)
		).toBeVisible();

		await user2Label.click();

		await expect(
			itemSelectorSamplePage.page.getByText(`${user?.givenName} Selected`)
		).toBeVisible();
	});

	await test.step('Check that it is possible to select an item in the Table visualization mode', async () => {
		await itemSelectorSamplePage.changeVisualizationMode({
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});

		itemSelectorSamplePage.selectByRowAndRole({row: 0});

		await expect(
			itemSelectorSamplePage.page.getByText(`Test Selected`)
		).toBeVisible();
	});
});

test('Item Selector Modal with multiple selection', async ({
	itemSelectorSamplePage,
	page,
}) => {
	await test.step('Check that an Item Selector Modal appears in the page', async () => {
		await expect(page.getByText('Item Selector Modal')).toBeVisible();
	});

	await test.step('Click in the Select Documents button opens the Item Selector Modal with a FDS component', async () => {
		await itemSelectorSamplePage.selectDocumentButton.click();

		await expect(
			itemSelectorSamplePage.selectDocumentModalHeader
		).toBeVisible();

		waitForFDS({page, visualizationMode: EFDSVisualizationMode.CARDS});
	});

	await test.step('Check that multiple items can be selected in the Cards visualization mode', async () => {
		await expect(itemSelectorSamplePage.modal.selectButton).toBeDisabled();

		await itemSelectorSamplePage.fdsContentContainer
			.locator('.custom-checkbox')
			.first()
			.click();

		await expect(itemSelectorSamplePage.modal.selectButton).toBeEnabled();

		await expect(
			itemSelectorSamplePage.page.getByText(
				`${imageFile.fileName} Selected`
			)
		).toBeVisible();

		await itemSelectorSamplePage.fdsContentContainer
			.locator('.custom-checkbox')
			.last()
			.click();

		await expect(
			itemSelectorSamplePage.page.getByText(`2 Items Selected`)
		).toBeVisible();
	});
});

test('Check user selection via modal in autocomplete input', async ({
	itemSelectorSamplePage,
	page,
}) => {
	const inputGroupLabel = 'Single Select (Users) - Open Modal Trigger';

	await test.step('Select use via modal', async () => {
		await itemSelectorSamplePage
			.inputGroup(inputGroupLabel)
			.getByLabel('Select Items')
			.click();

		await page.getByText('Test', {exact: true}).click();
		await itemSelectorSamplePage.modal.selectButton.click();
	});

	await test.step('Assert that the autocomplete input has the proper value', async () => {
		expect(
			itemSelectorSamplePage
				.inputGroup(inputGroupLabel)
				.getByRole('combobox')
		).toHaveValue('Test Test');
	});
});

test('Check user selection via modal in multiselect input', async ({
	itemSelectorSamplePage,
	page,
}) => {
	const inputGroupLabel = 'Multiple Select (Users) - Open Modal Trigger';

	await test.step('Select use via modal', async () => {
		await itemSelectorSamplePage
			.inputGroup(inputGroupLabel)
			.getByLabel('Select Items')
			.click();

		await page.getByText('Test', {exact: true}).click();
		await itemSelectorSamplePage.modal.selectButton.click();
	});

	await test.step('Assert that multiselect item is selected', async () => {
		expect(
			itemSelectorSamplePage.multiselectGridItem('Test Test')
		).toBeVisible();
	});
});

test('Check space selection via modal in autocomplete input', async ({
	apiHelpers,
	itemSelectorSamplePage,
	page,
}) => {
	const spaceName = `Space ${getRandomString()}`;

	await apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: spaceName,
		settings: {},
		type: 'Space',
	});

	const inputGroupLabel = 'Single Select (Spaces) - Open Modal Trigger';

	await test.step('Select space via modal', async () => {
		await itemSelectorSamplePage
			.inputGroup(inputGroupLabel)
			.getByLabel('Select Items')
			.click();

		await page.getByText(spaceName, {exact: true}).first().click();

		await itemSelectorSamplePage.modal.selectButton.click();
	});

	await test.step('Assert that the autocomplete input has the proper value', async () => {
		expect(
			itemSelectorSamplePage
				.inputGroup(inputGroupLabel)
				.getByRole('combobox')
		).toHaveValue(spaceName);
	});
});

test('Check space selection via modal in multiselect input', async ({
	apiHelpers,
	itemSelectorSamplePage,
	page,
}) => {
	const spaceName = `Space ${getRandomString()}`;

	await apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: spaceName,
		settings: {},
		type: 'Space',
	});

	const inputGroupLabel = 'Multiple Select (Spaces) - Open Modal Trigger';

	await test.step('Select space via modal', async () => {
		await itemSelectorSamplePage
			.inputGroup(inputGroupLabel)
			.getByLabel('Select Items')
			.click();

		await page.getByText(spaceName, {exact: true}).first().click();

		await itemSelectorSamplePage.modal.selectButton.click();
	});

	await test.step('Assert that multiselect item is selected', async () => {
		expect(
			itemSelectorSamplePage.multiselectGridItem(spaceName)
		).toBeVisible();
	});
});

test(
	'Open Item Selector Modal With JS Utility',
	{
		tag: ['@LPD-49253'],
	},
	async ({itemSelectorSamplePage, page}) => {
		await test.step('Click on the "Open Modal With JS Utility" button opens an item selector modal with an FDS component', async () => {
			await itemSelectorSamplePage.jsUtilityButton.click();

			await expect(
				itemSelectorSamplePage.selectUserModalHeader
			).toBeVisible();

			waitForFDS({page, visualizationMode: EFDSVisualizationMode.CARDS});
		});

		await test.step('Select an item', async () => {
			const items = itemSelectorSamplePage.page.locator(
				'.card:has(>.custom-radio)'
			);

			const userLabel = items.getByText('Test', {exact: true});

			await userLabel.click();

			await expect(
				itemSelectorSamplePage.modal.selectButton
			).toBeEnabled();

			await expect(
				itemSelectorSamplePage.page.getByText(`Test Selected`)
			).toBeVisible();

			await itemSelectorSamplePage.modal.selectButton.click();
		});

		await test.step('Assert that item is selected', async () => {
			await expect(
				itemSelectorSamplePage.page.getByText('Test Test')
			).toBeVisible();
		});

		await test.step('Opening item selector modal again autoselects previous selection', async () => {
			await itemSelectorSamplePage.jsUtilityButton.click();

			await expect(
				itemSelectorSamplePage.selectUserModalHeader
			).toBeVisible();

			waitForFDS({page, visualizationMode: EFDSVisualizationMode.CARDS});

			await expect(
				itemSelectorSamplePage.page.getByText(`Test Selected`)
			).toBeVisible();
		});
	}
);
