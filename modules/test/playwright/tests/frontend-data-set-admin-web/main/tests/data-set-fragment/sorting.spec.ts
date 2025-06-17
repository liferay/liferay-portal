/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../../../fixtures/accountSettingsPagesTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

export const test = mergeTests(
	accountSettingsPagesTest,
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	dataSetFragmentPageTest
);

let dataSetERC: string;
let dataSetLabel: string;

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await dataSetManagerApiHelpers.createDataSet({
		erc: dataSetERC,
		label: dataSetLabel,
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

test(
	'When sorting is configured with at least 1 sort and it is active, the dropdown is displayed in the fragment',
	{tag: ['@LPD-19503', '@LPD-39965']},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout, page}) => {
		await test.step('Create sorting', async () => {
			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: true,
				fieldName: 'id',
				label_i18n: {en_US: 'ID'},
				orderType: 'asc',
			});

			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: false,
				fieldName: 'fieldName',
				label_i18n: {en_US: 'Field Name'},
			});
		});

		await test.step('Add fields, so data is displayed', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'id',
				label_i18n: {
					en_US: 'ID',
				},
				sortable: true,
				type: 'string',
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'fieldName',
				label_i18n: {en_US: 'Field Name'},
				sortable: true,
				type: 'string',
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check that the order dropdown is displayed', async () => {
			await expect(
				page.getByRole('button', {name: 'Order'})
			).toBeVisible();
		});

		await test.step('Check that default sorting is applied', async () => {
			const firstIDText = await dataSetFragmentPage.table.bodyRows
				.first()
				.locator('td:first-child')
				.textContent();

			const lastIDText = await dataSetFragmentPage.table.container
				.locator('tr:last-child td:first-child')
				.textContent();

			expect(firstIDText < lastIDText).toBeTruthy();
		});

		await test.step('Check that sorting is displayed in the dropdown', async () => {
			await page.getByRole('button', {name: 'Order'}).click();

			await expect(
				page.getByRole('menuitem', {name: 'ID'})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {name: 'Name'})
			).toBeVisible();
		});

		await test.step('Select "Descending" in the dropdown', async () => {
			await page.getByRole('menuitem', {name: 'Descending'}).click();
		});

		await test.step('Check that the first ID is greater than the last ID in the table', async () => {
			const firstIDText = await dataSetFragmentPage.table.bodyRows
				.first()
				.locator('td:first-child')
				.textContent();

			const lastIDText = await dataSetFragmentPage.table.container
				.locator('tr:last-child td:first-child')
				.textContent();

			expect(firstIDText > lastIDText).toBeTruthy();
		});

		await test.step('Check that a different sort "Name" can be used', async () => {
			await page.getByRole('button', {name: 'Order'}).click();
			await page.getByRole('menuitem', {name: 'Name'}).click();

			const firstNameText = await dataSetFragmentPage.table.bodyRows
				.first()
				.locator('td:nth-child(2)')
				.textContent();

			const lastNameText = await dataSetFragmentPage.table.container
				.locator('tr:last-child td:nth-child(2)')
				.textContent();

			expect(firstNameText > lastNameText).toBeTruthy();

			await page.getByRole('button', {name: 'Order'}).click();
			await page.getByRole('menuitem', {name: 'Ascending'}).click();

			const firstNameTextAscending =
				await dataSetFragmentPage.table.bodyRows
					.first()
					.locator('td:nth-child(2)')
					.textContent();

			const lastNameTextAscending =
				await dataSetFragmentPage.table.container
					.locator('tr:last-child td:nth-child(2)')
					.textContent();

			expect(firstNameTextAscending < lastNameTextAscending).toBeTruthy();
		});
	}
);

test(
	'When sorting is configured with 1 sort that it is inactive, the dropdown does not display it in the fragment',
	{tag: '@LPD-39965'},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout, page}) => {
		await test.step('Create sorting', async () => {
			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: true,
				fieldName: 'id',
				label_i18n: {en_US: 'ID'},
				orderType: 'asc',
			});

			await dataSetManagerApiHelpers.createDataSetSort({
				active: false,
				dataSetERC,
				defaultValue: false,
				fieldName: 'fieldName',
				label_i18n: {en_US: 'Field Name'},
			});
		});

		await test.step('Add fields, so data is displayed', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'id',
				label_i18n: {
					en_US: 'ID',
				},
				sortable: true,
				type: 'string',
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'fieldName',
				label_i18n: {en_US: 'Field Name'},
				sortable: true,
				type: 'string',
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check that the order dropdown is displayed', async () => {
			await expect(
				page.getByRole('button', {name: 'Order'})
			).toBeVisible();
		});

		await test.step('Check that default sorting is applied', async () => {
			const firstIDText = await dataSetFragmentPage.table.bodyRows
				.first()
				.locator('td:first-child')
				.textContent();

			const lastIDText = await dataSetFragmentPage.table.container
				.locator('tr:last-child td:first-child')
				.textContent();

			expect(firstIDText < lastIDText).toBeTruthy();
		});

		await test.step('Check that the "inactive" sorting is not displayed in the dropdown', async () => {
			await page.getByRole('button', {name: 'Order'}).click();

			await expect(
				page.getByRole('menuitem', {name: 'ID'})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {name: 'Name'})
			).not.toBeVisible();
		});
	}
);

test('When the current page language is changed, the current translation is used and fallbacks to the site default language @LPD-25464', async ({
	accountSettingsPage,
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
	page,
}) => {
	let germanLanguage = false;

	try {
		await test.step('Create sorting', async () => {
			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: true,
				fieldName: 'id',
				label_i18n: {en_US: 'ID'},
				orderType: 'asc',
			});

			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: false,
				fieldName: 'fieldName',
				label_i18n: {de_DE: 'Feldname', en_US: 'Field name'},
			});
		});

		await test.step('Add fields, so data is displayed', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'id',
				label_i18n: {
					en_US: 'ID',
				},
				sortable: true,
				type: 'string',
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'fieldName',
				label_i18n: {en_US: 'Field Name'},
				sortable: true,
				type: 'string',
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Change user account default language to German', async () => {
			await accountSettingsPage.selectAccountLanguage({
				languageId: 'de_DE',
				navigate: true,
			});

			await expect(page.locator('.alert-success')).toBeVisible();

			germanLanguage = true;
		});

		await test.step('Go to Data Set fragment page', async () => {
			await dataSetFragmentPage.goToPage({layout});

			await page
				.locator('.data-set-content-wrapper')
				.waitFor({state: 'visible'});
		});

		await test.step('Check that the correct translations are displayed in the dropdown', async () => {
			await page.getByRole('button', {name: 'Auftrag'}).click();

			await expect(
				page.getByRole('menuitem', {name: 'ID'})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {name: 'Feldname'})
			).toBeVisible();
		});
	}
	finally {
		if (germanLanguage) {
			await test.step('Change user account default language back to English', async () => {
				await accountSettingsPage.selectAccountLanguage({
					languageId: 'en_US',
					navigate: true,
				});

				await expect(page.locator('.alert-success')).toBeVisible();
			});
		}
	}
});
