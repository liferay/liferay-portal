/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {
	EAsyncActionMethod,
	EItemActionType,
	EModalActionVariant,
} from '../../utils/types';
import {fdsFragmentPageTest} from './fixtures/fdsFragmentPageTest';

const LINK_ITEM_ACTION_NAME = 'Link item action';
const LINK_ITEM_ACTION_CONFIRMATION_MESSAGE =
	'Do you want to navigate to http://www.liferay.com?';

let dataSetERC: string;
let dataSetLabel: string;

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': true,
		'LPS-178052': true,
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	fdsFragmentPageTest
);

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await test.step('Create data set', async () => {
		await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			label: dataSetLabel,
		});
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

test.describe('Empty Item Actions in Data Set fragment', () => {
	test('Item Action button does not appear if there is no item action', async ({
		dataSetManagerApiHelpers,
		fdsFragmentPage,
		layout,
	}) => {
		await test.step('Create table field', async () => {
			await dataSetManagerApiHelpers.createDataSetField({
				dataSetERC,
				label_i18n: {en_US: 'Id'},
				name: 'id',
				type: 'string',
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await fdsFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check that the Item Action button is not present', async () => {
			await expect(
				fdsFragmentPage.page.getByLabel(LINK_ITEM_ACTION_NAME).first()
			).not.toBeVisible();
		});
	});
});

test.describe('Item Actions in Data Set fragment', () => {
	test.beforeEach(async ({dataSetManagerApiHelpers}) => {
		await test.step('Populate Data Set', async () => {
			await dataSetManagerApiHelpers.createDataSetField({
				dataSetERC,
				label_i18n: {en_US: 'Id'},
				name: 'id',
				type: 'string',
			});
			await dataSetManagerApiHelpers.createDataSetField({
				dataSetERC,
				label_i18n: {en_US: 'Name'},
				name: 'name',
				type: 'string',
			});
		});
	});

	test('Link Item Action (single action) is shown in the fragment', async ({
		dataSetManagerApiHelpers,
		fdsFragmentPage,
		layout,
		page,
	}) => {
		await test.step('Create Item Action', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				confirmationMessage_i18n: {
					en_US: LINK_ITEM_ACTION_CONFIRMATION_MESSAGE,
				},
				dataSetERC,
				label_i18n: {en_US: LINK_ITEM_ACTION_NAME},
				type: EItemActionType.LINK,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await fdsFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check that the Item Action button is present', async () => {
			await expect(
				fdsFragmentPage.page.getByLabel(LINK_ITEM_ACTION_NAME).first()
			).toBeVisible();
		});

		await test.step('Check that the Item Action works', async () => {
			const dialogPromise = page
				.waitForEvent('dialog')
				.then(async (dialog) => {
					await dialog.accept();

					return dialog.message();
				});

			await fdsFragmentPage.page
				.getByLabel(LINK_ITEM_ACTION_NAME)
				.first()
				.click();

			const confirmationMessage = await dialogPromise;

			expect(confirmationMessage).toBe(
				LINK_ITEM_ACTION_CONFIRMATION_MESSAGE
			);

			await expect(page.getByText('Welcome to Liferay')).toBeVisible();
		});
	});

	test('Link, Modal and Side Panel Item Actions (multiple actions) are shown in fragment', async ({
		dataSetManagerApiHelpers,
		fdsFragmentPage,
		layout,
		page,
	}) => {
		const modalItemActionName = 'Modal item action';
		const modalItemActionTitle = 'Modal title';
		const sidePanelItemActionName = 'SidePanel item action';
		const sidePanelItemActionUrl = liferayConfig.environment.baseUrl;

		await test.step('Create Item Actions', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: LINK_ITEM_ACTION_NAME},
				type: EItemActionType.LINK,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: modalItemActionName},
				modalSize: EModalActionVariant.SMALL,
				title_i18n: {en_US: modalItemActionTitle},
				type: EItemActionType.MODAL,
				url: liferayConfig.environment.baseUrl,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: sidePanelItemActionName},
				modalSize: EModalActionVariant.SMALL,
				title_i18n: {en_US: sidePanelItemActionName},
				type: EItemActionType.SIDE_PANEL,
				url: liferayConfig.environment.baseUrl,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await fdsFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		const datasetRow =
			await test.step('Check that the Item Actions dropdown is present in table row', async () => {
				const tableRow = await page
					.locator('.dnd-td.item-actions')
					.first();

				await expect(
					tableRow.getByRole('button', {
						exact: true,
						name: 'Actions',
					})
				).toBeVisible;

				const button = await tableRow.getByRole('button', {
					exact: true,
					name: 'Actions',
				});
				const dropdownId = await button.evaluate((node) =>
					node.getAttribute('aria-controls')
				);

				await button.click();

				await page
					.locator(`#${dropdownId}`)
					.filter({has: page.getByRole('menu')})
					.waitFor();

				await expect(
					page.locator(`#${dropdownId}`).getByRole('menuitem')
				).toHaveCount(3);

				await page.keyboard.press('Escape');

				return tableRow;
			});

		await test.step('Click the modal item action opens a modal window', async () => {
			const button = await datasetRow.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			const dropdownId = await button.evaluate((node) =>
				node.getAttribute('aria-controls')
			);

			await button.click();

			await page
				.locator(`#${dropdownId}`)
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await page
				.locator(`#${dropdownId}`)
				.getByRole('menuitem', {
					exact: true,
					name: modalItemActionName,
				})
				.click();

			await page.getByRole('dialog').waitFor();

			const dialog = page.getByRole('dialog');

			await expect(dialog.getByRole('heading')).toHaveText(
				modalItemActionTitle
			);

			await dialog.getByRole('button', {name: 'close'}).click();

			await expect(dialog).not.toBeInViewport();
		});

		await test.step('Click the side panel item action opens a side panel', async () => {
			const button = await datasetRow.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			const dropdownId = await button.evaluate((node) =>
				node.getAttribute('aria-controls')
			);

			await button.click();

			await page
				.locator(`#${dropdownId}`)
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await page
				.locator(`#${dropdownId}`)
				.getByRole('menuitem', {
					exact: true,
					name: sidePanelItemActionName,
				})
				.click();

			await page.getByRole('tabpanel').waitFor();

			const sidePanel = await page.getByRole('tabpanel');

			const iframeElement = await sidePanel
				.locator('iframe')
				.elementHandle();

			const frame = await iframeElement.contentFrame();

			await frame.waitForURL(
				new RegExp(`.*${sidePanelItemActionUrl}`, 'i')
			);

			await page.keyboard.press('Escape');

			await expect(sidePanel).not.toBeInViewport();
		});
	});

	test('Async and Headless Item Actions (multiple actions) are shown in fragment', async ({
		dataSetManagerApiHelpers,
		fdsFragmentPage,
		layout,
		page,
	}) => {
		const asyncItemActionName = 'Async item action';
		const asyncItemActionUrl = '/o/data-set-manager/table-sections/{id}';
		const headlessItemActionName = 'Headless item action';
		const headlessItemActionPermissionKey = 'delete';
		const nonAvailableHeadlessItemActionName =
			'Useless Headless Item Action';

		await test.step('Create Item Actions', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: headlessItemActionName},
				permissionKey: headlessItemActionPermissionKey,
				type: EItemActionType.HEADLESS,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: asyncItemActionName},
				method: EAsyncActionMethod.DELETE,
				type: EItemActionType.ASYNC,
				url: asyncItemActionUrl,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {
					en_US: nonAvailableHeadlessItemActionName,
				},
				permissionKey: 'remove',
				type: EItemActionType.HEADLESS,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await fdsFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		const datasetRow =
			await test.step('Check data set items have two item actions', async () => {
				const tableRow = await page
					.locator('.dnd-td.item-actions')
					.first();

				await expect(
					tableRow.getByRole('button', {
						exact: true,
						name: 'Actions',
					})
				).toBeVisible;

				const button = await tableRow.getByRole('button', {
					exact: true,
					name: 'Actions',
				});
				const dropdownId = await button.evaluate((node) =>
					node.getAttribute('aria-controls')
				);

				await button.click();

				await page
					.locator(`#${dropdownId}`)
					.filter({has: page.getByRole('menu')})
					.waitFor();

				await expect(
					page
						.locator(`#${dropdownId}`)
						.getByRole('menuitem', {name: asyncItemActionName})
				).toBeVisible();

				await expect(
					page
						.locator(`#${dropdownId}`)
						.getByRole('menuitem', {name: headlessItemActionName})
				).toBeVisible();

				await expect(
					page.locator(`#${dropdownId}`).getByRole('menuitem', {
						name: nonAvailableHeadlessItemActionName,
					})
				).not.toBeVisible();

				await page.keyboard.press('Escape');

				return tableRow;
			});

		await test.step('Click in the headless item action executes the action', async () => {
			const button = await datasetRow.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			const dropdownId = await button.evaluate((node) =>
				node.getAttribute('aria-controls')
			);

			await button.click();

			await page
				.locator(`#${dropdownId}`)
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await page
				.locator(`#${dropdownId}`)
				.getByRole('menuitem', {
					exact: true,
					name: headlessItemActionName,
				})
				.click();

			await waitForAlert(page);
		});

		await test.step('Click in the async item action executes the action', async () => {
			const nextTableRow = await page
				.locator('.dnd-td.item-actions')
				.first();

			const button = await nextTableRow.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			const dropdownId = await button.evaluate((node) =>
				node.getAttribute('aria-controls')
			);

			await button.click();

			await page
				.locator(`#${dropdownId}`)
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await page
				.locator(`#${dropdownId}`)
				.getByRole('menuitem', {
					exact: true,
					name: asyncItemActionName,
				})
				.click();

			await waitForAlert(page);
		});
	});

	test('Async and Headless Item Actions (multiple actions) performs UPDATE operations on items', async ({
		dataSetManagerApiHelpers,
		fdsFragmentPage,
		layout,
		page,
	}) => {
		const asyncItemActionName = 'Async item action';
		const asyncItemActionUrl = '/o/data-set-manager/table-sections/{id}';
		const asyncItemNewLabel = getRandomString();
		const headlessItemActionName = 'Headless item action';
		const headlessItemActionPermissionKey = 'update';
		const headlessItemNewLabel = getRandomString();

		await test.step('Create Item Actions', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: headlessItemActionName},
				permissionKey: headlessItemActionPermissionKey,
				requestBody: `{"name": "${headlessItemNewLabel}"}`,
				type: EItemActionType.HEADLESS,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: asyncItemActionName},
				method: EAsyncActionMethod.PATCH,
				requestBody: `{"name": "${asyncItemNewLabel}"}`,
				type: EItemActionType.ASYNC,
				url: asyncItemActionUrl,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await fdsFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check data set items have two item actions', async () => {
			const tableRow = await page.locator('.dnd-td.item-actions').first();

			await expect(
				tableRow.getByRole('button', {
					exact: true,
					name: 'Actions',
				})
			).toBeVisible;

			const button = await tableRow.getByRole('button', {
				exact: true,
				name: 'Actions',
			});
			const dropdownId = await button.evaluate((node) =>
				node.getAttribute('aria-controls')
			);

			await button.click();

			await page
				.locator(`#${dropdownId}`)
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await expect(
				page
					.locator(`#${dropdownId}`)
					.getByRole('menuitem', {name: asyncItemActionName})
			).toBeVisible();

			await expect(
				page
					.locator(`#${dropdownId}`)
					.getByRole('menuitem', {name: headlessItemActionName})
			).toBeVisible();

			await page.keyboard.press('Escape');
		});

		await test.step('Click in the headless item action executes the action', async () => {
			const tableRow = await page.locator('.dnd-td.item-actions').first();

			const button = await tableRow.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			const dropdownId = await button.evaluate((node) =>
				node.getAttribute('aria-controls')
			);

			await button.click();

			await page
				.locator(`#${dropdownId}`)
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await page
				.locator(`#${dropdownId}`)
				.getByRole('menuitem', {
					exact: true,
					name: headlessItemActionName,
				})
				.click();

			await waitForAlert(page);

			await expect(page.getByText(headlessItemNewLabel)).toBeVisible();
		});

		await test.step('Click in the async item action executes the action', async () => {
			const nextTableRow = await page
				.locator('.dnd-td.item-actions')
				.first();

			const button = await nextTableRow.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			const dropdownId = await button.evaluate((node) =>
				node.getAttribute('aria-controls')
			);

			await button.click();

			await page
				.locator(`#${dropdownId}`)
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await page
				.locator(`#${dropdownId}`)
				.getByRole('menuitem', {
					exact: true,
					name: asyncItemActionName,
				})
				.click();

			await waitForAlert(page);

			await expect(page.getByText(asyncItemNewLabel)).toBeVisible();
		});
	});

	test('Async Item Action shows an error toast in the fragment when a failure occurs', async ({
		dataSetManagerApiHelpers,
		fdsFragmentPage,
		layout,
		page,
	}) => {
		const asyncItemActionName = 'Async item action';
		const asyncItemActionWrongUrl =
			'/o/data-set-manager/table-sections/{foo}';

		await test.step('Create Item Actions', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: asyncItemActionName},
				method: EAsyncActionMethod.DELETE,
				type: EItemActionType.ASYNC,
				url: asyncItemActionWrongUrl,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await fdsFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		const datasetRow =
			await test.step('Checkt that the Item Actions is present in table row', async () => {
				const tableRow = await page
					.locator('.dnd-td.item-actions')
					.first();

				await expect(tableRow.getByRole('button')).toBeVisible();

				return tableRow;
			});

		await test.step('Click in the async Item Action shows an error toast.', async () => {
			await datasetRow
				.getByRole('button', {name: asyncItemActionName})
				.click();

			await page.getByRole('alert').waitFor();

			const alert = await page.getByRole('alert').first();

			await expect(alert).toHaveText(
				'Error:An unexpected error occurred.'
			);
		});
	});
});
