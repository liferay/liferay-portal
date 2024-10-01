/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../../fixtures/accountSettingsPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {fdsFragmentPageTest} from './fixtures/fdsFragmentPageTest';

export const test = mergeTests(
	accountSettingsPagesTest,
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPD-19465': true,
		'LPS-178052': true,
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	fdsFragmentPageTest
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

test.describe('Sorting Dropdown in Data Set Fragment', () => {
	test('When sorting is configured with at least 1 sort, the dropdown is displayed in the fragment @LPD-19503', async ({
		dataSetManagerApiHelpers,
		fdsFragmentPage,
		layout,
		page,
	}) => {
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
				fieldName: 'name',
				label_i18n: {en_US: 'Name'},
			});
		});

		await test.step('Add fields, so data is displayed', async () => {
			await dataSetManagerApiHelpers.createDataSetField({
				dataSetERC,
				label_i18n: {
					en_US: 'ID',
				},
				name: 'id',
				sortable: true,
				type: 'string',
			});

			await dataSetManagerApiHelpers.createDataSetField({
				dataSetERC,
				label_i18n: {en_US: 'Name'},
				name: 'name',
				sortable: true,
				type: 'string',
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await fdsFragmentPage.configureDataSetFragment({
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
			const firstIDText = await fdsFragmentPage.fdsTableWrapper
				.locator('.dnd-tbody .dnd-tr:first-child .dnd-td:first-child')
				.textContent();

			const lastIDText = await fdsFragmentPage.fdsTableWrapper
				.locator('.dnd-tbody .dnd-tr:last-child .dnd-td:first-child')
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
			const firstIDText = await fdsFragmentPage.fdsTableWrapper
				.locator('.dnd-tbody .dnd-tr:first-child .dnd-td:first-child')
				.textContent();

			const lastIDText = await fdsFragmentPage.fdsTableWrapper
				.locator('.dnd-tbody .dnd-tr:last-child .dnd-td:first-child')
				.textContent();

			expect(firstIDText > lastIDText).toBeTruthy();
		});

		await test.step('Check that a different sort "Name" can be used', async () => {
			await page.getByRole('button', {name: 'Order'}).click();
			await page.getByRole('menuitem', {name: 'Name'}).click();

			const firstNameText = await fdsFragmentPage.fdsTableWrapper
				.locator('.dnd-tbody .dnd-tr:first-child .dnd-td:nth-child(2)')
				.textContent();

			const lastNameText = await fdsFragmentPage.fdsTableWrapper
				.locator('.dnd-tbody .dnd-tr:last-child .dnd-td:nth-child(2)')
				.textContent();

			expect(firstNameText > lastNameText).toBeTruthy();

			await page.getByRole('button', {name: 'Order'}).click();
			await page.getByRole('menuitem', {name: 'Ascending'}).click();

			const firstNameTextAscending = await fdsFragmentPage.fdsTableWrapper
				.locator('.dnd-tbody .dnd-tr:first-child .dnd-td:nth-child(2)')
				.textContent();

			const lastNameTextAscending = await fdsFragmentPage.fdsTableWrapper
				.locator('.dnd-tbody .dnd-tr:last-child .dnd-td:nth-child(2)')
				.textContent();

			expect(firstNameTextAscending < lastNameTextAscending).toBeTruthy();
		});
	});

	test('When the current page language is changed, the current translation is used and fallbacks to the site default language @LPD-25464', async ({
		accountSettingsPage,
		dataSetManagerApiHelpers,
		fdsFragmentPage,
		layout,
		page,
	}) => {
		let spanishLanguage = false;

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
					fieldName: 'name',
					label_i18n: {en_US: 'Name', es_ES: 'Nombre'},
				});
			});

			await test.step('Add fields, so data is displayed', async () => {
				await dataSetManagerApiHelpers.createDataSetField({
					dataSetERC,
					label_i18n: {
						en_US: 'ID',
					},
					name: 'id',
					sortable: true,
					type: 'string',
				});

				await dataSetManagerApiHelpers.createDataSetField({
					dataSetERC,
					label_i18n: {en_US: 'Name'},
					name: 'name',
					sortable: true,
					type: 'string',
				});
			});

			await test.step('Configure Data Set fragment', async () => {
				await fdsFragmentPage.configureDataSetFragment({
					dataSetLabel,
					layout,
				});
			});

			await test.step('Change user account default language to Spanish', async () => {

				// This method should be used, but since it uses it a different
				// configured `testIdAttribute`, a locator has to be used.
				//
				// await accountSettingsPage.goToAccountSettings();

				await page.locator('[data-qa-id="userPersonalMenu"]').click();

				await accountSettingsPage.accountSettingsMenuItem.click();

				await page.getByLabel('Language').selectOption('es_ES');

				await page.getByRole('button', {name: 'Save'}).click();

				await expect(
					page.getByText('Éxito:Su petición ha terminado con éxito.')
				).toBeVisible();

				spanishLanguage = true;
			});

			await test.step('Go to Data Set fragment page', async () => {
				await fdsFragmentPage.goToPage({layout});

				await page
					.locator('.data-set-content-wrapper')
					.waitFor({state: 'visible'});
			});

			await test.step('Check that the correct translations are displayed in the dropdown', async () => {
				await page.getByRole('button', {name: 'Pedido'}).click();

				await expect(
					page.getByRole('menuitem', {name: 'ID'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Nombre'})
				).toBeVisible();
			});
		}
		finally {
			if (spanishLanguage) {
				await test.step('Change user account default language back to English', async () => {
					await page
						.locator('[data-qa-id="userPersonalMenu"]')
						.click(); // This is using `locator` instead of `getByTestId` because of the difference in `testIdAttribute` names.

					await page
						.getByRole('menuitem', {
							name: 'Mi cuenta',
						})
						.click();

					await page.getByLabel('Lenguaje').selectOption('en_US');

					await page.getByRole('button', {name: 'Guardar'}).click();

					await waitForAlert(page);
				});
			}
		}
	});
});
