/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	dataSetFragmentPageTest
);

let dataSetERC: string;
let dataSetLabel: string;
const dataSetERCs = [];

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	dataSetERCs.push(dataSetERC);

	await test.step('Create data set', async () => {
		await dataSetManagerApiHelpers.createDataSet({
			defaultVisualizationMode: 'table',
			erc: dataSetERC,
			label: dataSetLabel,
			restEndpoint: '/',
			restSchema: 'DataSet',
		});
	});

	await test.step('Add some table sections', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'id',
			label_i18n: {en_US: 'Id'},
			type: 'string',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'active',
			label_i18n: {en_US: 'Active'},
			sortable: false,
			type: 'boolean',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'description',
			label_i18n: {en_US: 'Description'},
			type: 'string',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'label',
			label_i18n: {en_US: 'Label'},
			type: 'string',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'restSchema',
			label_i18n: {en_US: 'Schema'},
			type: 'string',
		});
	});

	await test.step('Add some card sections', async () => {
		await dataSetManagerApiHelpers.createDataSetCardsSection({
			dataSetERC,
			fieldName: 'label',
			name: 'title',
		});

		await dataSetManagerApiHelpers.createDataSetCardsSection({
			dataSetERC,
			fieldName: 'description',
			name: 'description',
		});
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	for (const erc of dataSetERCs) {
		await dataSetManagerApiHelpers.deleteDataSet({
			erc,
		});
	}

	dataSetERCs.length = 0;
});

test(
	'Data Set does not show "Custom Views" if they are not enabled',
	{tag: '@LPD-10683'},
	async ({dataSetFragmentPage, layout}) => {
		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check that the Custom Views controls are not present', async () => {
			await expect(
				dataSetFragmentPage.customViewsSelectorButton
			).not.toBeInViewport();
			await expect(
				dataSetFragmentPage.customViewsActionsButton
			).not.toBeInViewport();
		});
	}
);

test(
	'Can create, edit and delete Custom Views',
	{tag: '@LPD-10683'},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout, page}) => {
		let customViewsActionsDropdown: Locator;
		let customViewsDropdown: Locator;
		let columnsVisibilityDropdown: Locator;

		const customView1Name = getRandomString();
		const customView2Name = getRandomString();

		await test.step('Create collection of Data Sets', async () => {
			const testDataSetERCs = Array.from(Array(5).keys()).map(() =>
				getRandomString()
			);

			for (const DATA_SET_ERC of testDataSetERCs) {
				dataSetERCs.push(DATA_SET_ERC);

				await dataSetManagerApiHelpers.createDataSet({
					erc: DATA_SET_ERC,
					label: getRandomString(),
					restEndpoint: '/',
					restSchema: 'DataSet',
				});
			}
		});

		await test.step('Enable Custom Views', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				customViewsEnabled: true,
				erc: dataSetERC,
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Custom Views controls are present', async () => {
			await expect(
				dataSetFragmentPage.customViewsSelectorButton
			).toBeInViewport();
			await expect(
				dataSetFragmentPage.customViewsSelectorButton
			).toHaveText('Default View');

			await expect(
				dataSetFragmentPage.customViewsActionsButton
			).toBeInViewport();
		});

		await test.step('Get dropdown references', async () => {

			// Click on dropdown toggle button adds the aria-controls attribute

			await dataSetFragmentPage.customViewsActionsButton.click();

			const customViewsActionsDropdownId =
				await dataSetFragmentPage.customViewsActionsButton.getAttribute(
					'aria-controls'
				);

			customViewsActionsDropdown = page.locator(
				`#${customViewsActionsDropdownId}`
			);

			page.keyboard.press('Escape');

			await dataSetFragmentPage.customViewsSelectorButton.click();

			const customViewsDropdownId =
				await dataSetFragmentPage.customViewsSelectorButton.getAttribute(
					'aria-controls'
				);

			customViewsDropdown = page.locator(`#${customViewsDropdownId}`);

			page.keyboard.press('Escape');

			await dataSetFragmentPage.table.manageColumnsVisibilityButton.click();

			page.keyboard.press('Escape');
		});

		await test.step('Changing FDS configuration marks the custom view as updated (* added)', async () => {
			const itemsPerPageButton =
				dataSetFragmentPage.paginationWrapper.getByLabel(
					'Items Per Page'
				);

			await expect(itemsPerPageButton).toHaveText('20 Items');

			await itemsPerPageButton.click();

			const paginationOptionsDropdownId =
				await itemsPerPageButton.evaluate((node) =>
					node.getAttribute('aria-controls')
				);

			await dataSetFragmentPage.page
				.locator(`#${paginationOptionsDropdownId}`)
				.waitFor();

			const paginationOptions = dataSetFragmentPage.page
				.locator(`#${paginationOptionsDropdownId}`)
				.getByRole('option');

			await paginationOptions.filter({hasText: '4 Items'}).click();

			await dataSetFragmentPage.table.manageColumnsVisibilityButton.click();

			const columnsVisibilityDropdownId =
				await dataSetFragmentPage.table.manageColumnsVisibilityButton.getAttribute(
					'aria-controls'
				);

			columnsVisibilityDropdown = page.locator(
				`#${columnsVisibilityDropdownId}`
			);

			await columnsVisibilityDropdown
				.getByRole('menuitem', {name: 'Description'})
				.click();

			page.keyboard.press('Escape');

			await expect(
				dataSetFragmentPage.customViewsSelectorButton
			).toHaveText('Default ViewDefault View Updated');
		});

		await test.step('Can save changes and create a new view', async () => {
			await dataSetFragmentPage.customViewsActionsButton.click();

			await customViewsActionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = customViewsActionsDropdown.getByRole('menuitem', {
				name: 'Save View As...',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(
				dataSetFragmentPage.customViewsSaveModal
			).toBeInViewport();

			await dataSetFragmentPage.customViewsSaveModal
				.getByLabel('NameRequired')
				.fill(customView1Name);

			await dataSetFragmentPage.customViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await waitForAlert(page, 'Success:View was saved successfully.');

			await expect(
				dataSetFragmentPage.customViewsSelectorButton
			).toHaveText(customView1Name);

			await dataSetFragmentPage.customViewsSelectorButton.click();

			await expect(customViewsDropdown.getByRole('option')).toHaveCount(
				2
			);

			page.keyboard.press('Escape');
		});

		await test.step('Confirm that changes in a custom view does not affect Default View', async () => {
			await expect(
				dataSetFragmentPage.customViewsSelectorButton
			).toHaveText(customView1Name);

			await expect(dataSetFragmentPage.table.headerCells).toHaveCount(5);

			await dataSetFragmentPage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await customViewsDropdown
				.getByRole('option', {name: 'Default View'})
				.click();

			await expect(dataSetFragmentPage.table.headerCells).toHaveCount(6);
		});

		await test.step('Can update the new view', async () => {
			await dataSetFragmentPage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await customViewsDropdown
				.getByRole('option', {name: customView1Name})
				.click();

			await dataSetFragmentPage.changeVisualizationMode('Cards');

			await dataSetFragmentPage.cardsWrapper.waitFor({
				state: 'visible',
			});

			await expect(dataSetFragmentPage.cardsWrapper).toBeInViewport();

			await expect(
				dataSetFragmentPage.customViewsSelectorButton
			).toHaveText(`${customView1Name}${customView1Name} Updated`);

			await dataSetFragmentPage.customViewsActionsButton.click();

			await customViewsActionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = customViewsActionsDropdown.getByRole('menuitem', {
				exact: true,
				name: 'Save View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await waitForAlert(page, 'Success:View was saved successfully.');
		});

		await test.step('Can restore the default view settings', async () => {
			await dataSetFragmentPage.customViewsSelectorButton.click();

			await customViewsDropdown
				.getByRole('option', {name: 'Default View'})
				.click();

			await dataSetFragmentPage.table.container.waitFor({
				state: 'visible',
			});

			await expect(dataSetFragmentPage.table.container).toBeInViewport();
		});

		await test.step('Can rename a custom view', async () => {
			await dataSetFragmentPage.customViewsSelectorButton.click();

			await customViewsDropdown
				.getByRole('option', {name: customView1Name})
				.click();

			await dataSetFragmentPage.cardsWrapper.waitFor({
				state: 'visible',
			});

			await expect(dataSetFragmentPage.cardsWrapper).toBeInViewport();

			await dataSetFragmentPage.customViewsActionsButton.click();

			await customViewsActionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = customViewsActionsDropdown.getByRole('menuitem', {
				exact: true,
				name: 'Rename View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(
				dataSetFragmentPage.customViewsSaveModal
			).toBeInViewport();

			await dataSetFragmentPage.customViewsSaveModal
				.getByLabel('NameRequired')
				.fill(customView2Name);

			await dataSetFragmentPage.customViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await waitForAlert(page, 'Success:View was renamed successfully.');

			await expect(
				dataSetFragmentPage.customViewsSelectorButton
			).toHaveText(customView2Name);
		});

		await test.step('Can delete a custom view', async () => {
			await dataSetFragmentPage.customViewsActionsButton.click();

			await customViewsActionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = customViewsActionsDropdown.getByRole('menuitem', {
				exact: true,
				name: 'Delete View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(
				dataSetFragmentPage.customViewsDeleteAlert
			).toBeVisible();

			await dataSetFragmentPage.customViewsDeleteAlert
				.getByRole('button', {name: 'Delete'})
				.click();

			await dataSetFragmentPage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await expect(
				customViewsDropdown.getByRole('option', {name: customView2Name})
			).not.toBeVisible();
		});
	}
);
