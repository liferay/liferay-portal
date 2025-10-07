/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {liferayConfig} from '../../../../../liferay.config';
import getRandomString from '../../../../../utils/getRandomString';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {API_ENDPOINT_PATH} from '../../utils/constants';
import {
	EAsyncActionMethod,
	EItemActionTarget,
	EModalActionVariant,
} from '../../utils/types';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

const LINK_ITEM_ACTION_NAME = 'Link item action';
const LINK_ITEM_ACTION_CONFIRMATION_MESSAGE =
	'Do you want to navigate to http://www.liferay.com?';

let dataSetERC: string;
let dataSetLabel: string;

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
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
	}) => {
		await test.step('Create table field', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'id',
				label_i18n: {en_US: 'Id'},
				type: 'string',
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check that the Item Action button is not present', async () => {
			await expect(
				dataSetFragmentPage.page
					.getByLabel(LINK_ITEM_ACTION_NAME)
					.first()
			).not.toBeVisible();
		});
	});
});

test.describe('Item Actions in Data Set fragment', () => {
	test.beforeEach(async ({dataSetManagerApiHelpers}) => {
		await test.step('Populate Data Set', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'id',
				label_i18n: {en_US: 'Id'},
				type: 'string',
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'fieldName',
				label_i18n: {en_US: 'Field Name'},
				type: 'string',
			});
		});
	});

	test('Link Item Action (single action) is shown in the fragment', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
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
				target: EItemActionTarget.LINK,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check that the Item Action button is present', async () => {
			await expect(
				dataSetFragmentPage.page
					.getByLabel(LINK_ITEM_ACTION_NAME)
					.first()
			).toBeVisible();
		});

		await test.step('Check that the Item Action works', async () => {
			const dialogPromise = page
				.waitForEvent('dialog')
				.then(async (dialog) => {
					await dialog.accept();

					return dialog.message();
				});

			await dataSetFragmentPage.page
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
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const modalItemActionName = 'Modal item action';
		const modalItemActionTitle = 'Modal title';
		const sidePanelItemActionName = 'SidePanel item action';

		await test.step('Create Item Actions', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: LINK_ITEM_ACTION_NAME},
				target: EItemActionTarget.LINK,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: modalItemActionName},
				modalSize: EModalActionVariant.SMALL,
				target: EItemActionTarget.MODAL,
				title_i18n: {en_US: modalItemActionTitle},
				url: liferayConfig.environment.baseUrl,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: sidePanelItemActionName},
				modalSize: EModalActionVariant.SMALL,
				target: EItemActionTarget.SIDE_PANEL,
				title_i18n: {en_US: sidePanelItemActionName},
				url: liferayConfig.environment.baseUrl,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		const datasetRow =
			await test.step('Check that the Item Actions dropdown is present in table row', async () => {
				const tableRow = dataSetFragmentPage.table.bodyRows
					.first()
					.locator('td.cell-item-actions');

				await expect(
					tableRow.getByRole('button', {
						exact: true,
						name: 'Actions',
					})
				).toBeVisible();

				const button = await tableRow.getByRole('button', {
					exact: true,
					name: 'Actions',
				});

				const dropdownId = await button.getAttribute('aria-controls');

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

			const dropdownId = await button.getAttribute('aria-controls');

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

			await dialog.getByRole('button', {name: 'Close'}).click();

			await expect(dialog).not.toBeInViewport();
		});

		await test.step('Click the side panel item action opens a side panel', async () => {
			const button = await datasetRow.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			const dropdownId = await button.getAttribute('aria-controls');

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

			await expect(
				page
					.locator(`#${dropdownId}`)
					.filter({has: page.getByRole('menu')})
			).not.toBeVisible();

			const frame = dataSetFragmentPage.sidePanelFrame;

			await expect(
				frame.locator('.side-panel-iframe-header')
			).not.toBeInViewport();

			await expect(frame.getByText('Welcome to Liferay')).toBeVisible();

			await page.keyboard.press('Escape');

			await expect(dataSetFragmentPage.sidePanel).toHaveClass(
				/is-hidden/
			);
		});
	});

	test('Link item action works in Cards, List and Visualization modes', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		await test.step('Create sample data for the data set cards and list', async () => {
			await dataSetManagerApiHelpers.createDataSetCardsSection({
				dataSetERC,
				fieldName: 'id',
				name: 'title',
			});

			await dataSetManagerApiHelpers.createDataSetListSection({
				dataSetERC,
				fieldName: 'id',
				name: 'title',
			});
		});

		await test.step('Create a link Item Action with an interpolated argument', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: LINK_ITEM_ACTION_NAME},
				target: EItemActionTarget.LINK,
				url: '/detail/{id}',
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Action is visible in the Cards visualization mode', async () => {
			await dataSetFragmentPage.cardsWrapper.waitFor({
				state: 'visible',
			});

			await expect(dataSetFragmentPage.cardsWrapper).toBeInViewport();

			await dataSetFragmentPage.page.locator('.card').first().waitFor();

			const firstCard = dataSetFragmentPage.page.locator('.card').first();

			const itemId = await firstCard
				.locator('.card-title')
				.allInnerTexts();

			const cardActionsDropdownId = await firstCard
				.getByLabel('More Actions')
				.getAttribute('aria-controls');

			await firstCard.getByLabel('More Actions').click();

			await page
				.locator(`#${cardActionsDropdownId}`)
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const itemAction = await page
				.locator(`#${cardActionsDropdownId}`)
				.getByRole('menuitem', {name: LINK_ITEM_ACTION_NAME});

			await expect(
				(await itemAction.getAttribute('href')).valueOf()
			).toContain(`/detail/${itemId}`);

			await itemAction.click();

			await page.getByText('Not Found').isVisible();

			await dataSetFragmentPage.goToPage({layout});
		});

		await test.step('Change visualization mode to List', async () => {
			await dataSetFragmentPage.changeVisualizationMode('List');
		});

		await test.step('Action is visible in the List visualization mode', async () => {
			await dataSetFragmentPage.listWrapper.waitFor({
				state: 'visible',
			});

			await expect(dataSetFragmentPage.listWrapper).toBeInViewport();

			await dataSetFragmentPage.page
				.locator('.list-group-item')
				.first()
				.waitFor();

			const firstListItem = dataSetFragmentPage.page
				.locator('.list-group-item')
				.first();

			const itemId = await firstListItem
				.locator('.list-group-title')
				.allInnerTexts();

			const listActionLink = await firstListItem.getByLabel(
				LINK_ITEM_ACTION_NAME
			);

			await expect(
				(await listActionLink.getAttribute('href')).valueOf()
			).toContain(`/detail/${itemId}`);
		});

		await test.step('Change visualization mode to Table', async () => {
			await dataSetFragmentPage.changeVisualizationMode('Table');
		});

		await test.step('Action is visible in the Table visualization mode', async () => {
			await dataSetFragmentPage.table.container.waitFor({
				state: 'visible',
			});

			await expect(dataSetFragmentPage.table.container).toBeInViewport();

			const itemActionsCell =
				dataSetFragmentPage.table.itemActionsCells.first();

			const itemId = await dataSetFragmentPage.table.bodyRows
				.locator('td')
				.first()
				.allInnerTexts();

			const tableActionLink = await itemActionsCell.getByLabel(
				LINK_ITEM_ACTION_NAME
			);

			expect(
				(await tableActionLink.getAttribute('href')).valueOf()
			).toContain(`/detail/${itemId}`);
		});
	});

	test('Async and Headless Item Actions (multiple actions) are shown in fragment', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const asyncItemActionName = 'Async item action';
		const asyncItemActionUrl = `/o${API_ENDPOINT_PATH}/table-sections/{id}`;
		const headlessItemActionName = 'Headless item action';
		const headlessItemActionPermissionKey = 'delete';
		const nonAvailableHeadlessItemActionName =
			'Useless Headless Item Action';

		await test.step('Create Item Actions', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: headlessItemActionName},
				permissionKey: headlessItemActionPermissionKey,
				target: EItemActionTarget.HEADLESS,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: asyncItemActionName},
				method: EAsyncActionMethod.DELETE,
				target: EItemActionTarget.ASYNC,
				url: asyncItemActionUrl,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {
					en_US: nonAvailableHeadlessItemActionName,
				},
				permissionKey: 'remove',
				target: EItemActionTarget.HEADLESS,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check data set items have two item actions', async () => {
			const itemActionsCell =
				dataSetFragmentPage.table.itemActionsCells.first();

			const button = itemActionsCell.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			await expect(button).toBeVisible();

			const dropdownId = await button.getAttribute('aria-controls');

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
		});

		await test.step('Click in the headless item action executes the action', async () => {
			const button = dataSetFragmentPage.table.bodyRows
				.first()
				.getByRole('button', {
					exact: true,
					name: 'Actions',
				});

			const dropdownId = await button.getAttribute('aria-controls');

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
			const itemActionsCell =
				dataSetFragmentPage.table.itemActionsCells.first();

			const button = itemActionsCell.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			const dropdownId = await button.getAttribute('aria-controls');

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
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const asyncItemActionName = 'Async item action';
		const asyncItemActionUrl = `/o${API_ENDPOINT_PATH}/table-sections/{id}`;
		const asyncItemNewLabel = getRandomString();
		const headlessItemActionName = 'Headless item action';
		const headlessItemActionPermissionKey = 'update';
		const headlessItemNewLabel = getRandomString();

		await test.step('Update Data Set with default sort', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				additionalAPIURLParameters: 'sort=id:asc',
				erc: dataSetERC,
			});
		});

		await test.step('Create Item Actions', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: headlessItemActionName},
				permissionKey: headlessItemActionPermissionKey,
				requestBody: `{"label_i18n": {"en_US": "${headlessItemNewLabel}"}}`,
				target: EItemActionTarget.HEADLESS,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: asyncItemActionName},
				method: EAsyncActionMethod.PATCH,
				requestBody: `{"label_i18n": {"en_US": "${asyncItemNewLabel}"}}`,
				target: EItemActionTarget.ASYNC,
				url: asyncItemActionUrl,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check data set items have two item actions', async () => {
			const itemActionsCell =
				dataSetFragmentPage.table.itemActionsCells.first();

			const button = itemActionsCell.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			await expect(button).toBeVisible();

			const dropdownId = await button.getAttribute('aria-controls');

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
			const itemActionsCell =
				dataSetFragmentPage.table.itemActionsCells.first();

			const button = itemActionsCell.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			const dropdownId = await button.getAttribute('aria-controls');

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

			await page.reload();

			await expect(
				page.locator('.cell-id').getByText(headlessItemNewLabel)
			).toBeVisible();
		});

		await test.step('Click in the async item action executes the action', async () => {
			const itemActionsCell =
				dataSetFragmentPage.table.itemActionsCells.first();

			const button = itemActionsCell.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

			const dropdownId = await button.getAttribute('aria-controls');

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

			await page.reload();

			await expect(
				page.locator('.cell-id').getByText(asyncItemNewLabel)
			).toBeVisible();
		});
	});

	test('Async Item Action shows an error toast in the fragment when a failure occurs', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const asyncItemActionName = 'Async item action';
		const asyncItemActionWrongUrl = `/o${API_ENDPOINT_PATH}/table-sections/{foo}`;

		await test.step('Create Item Actions', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				label_i18n: {en_US: asyncItemActionName},
				method: EAsyncActionMethod.DELETE,
				target: EItemActionTarget.ASYNC,
				url: asyncItemActionWrongUrl,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check that the Item Actions is present in table row', async () => {
			const itemActionsCell =
				dataSetFragmentPage.table.itemActionsCells.first();

			await expect(itemActionsCell.getByRole('button')).toBeVisible();
		});

		await test.step('Click in the async Item Action shows an error toast.', async () => {
			await dataSetFragmentPage.table.bodyRows
				.first()
				.getByRole('button', {name: asyncItemActionName})
				.click();

			await page.getByRole('alert').waitFor();

			const alert = page.getByRole('alert').first();

			await expect(alert).toHaveText(
				'Error:An unexpected error occurred.'
			);
		});
	});

	test(
		'Only "active" Item Actions are shown in fragment',
		{tag: '@LPD-39965'},
		async ({
			dataSetFragmentPage,
			dataSetManagerApiHelpers,
			layout,
			page,
		}) => {
			const modalItemActionName = 'Modal item action';
			const modalItemActionTitle = 'Modal title';
			const sidePanelItemActionName = 'SidePanel item action';

			await test.step('Create Item Actions', async () => {
				await dataSetManagerApiHelpers.createDataSetItemAction({
					dataSetERC,
					label_i18n: {en_US: LINK_ITEM_ACTION_NAME},
					target: EItemActionTarget.LINK,
				});

				await dataSetManagerApiHelpers.createDataSetItemAction({
					dataSetERC,
					label_i18n: {en_US: modalItemActionName},
					modalSize: EModalActionVariant.SMALL,
					target: EItemActionTarget.MODAL,
					title_i18n: {en_US: modalItemActionTitle},
					url: liferayConfig.environment.baseUrl,
				});

				await dataSetManagerApiHelpers.createDataSetItemAction({
					active: false,
					dataSetERC,
					label_i18n: {en_US: sidePanelItemActionName},
					modalSize: EModalActionVariant.SMALL,
					target: EItemActionTarget.SIDE_PANEL,
					title_i18n: {en_US: sidePanelItemActionName},
					url: liferayConfig.environment.baseUrl,
				});
			});

			await test.step('Configure Data Set in the page', async () => {
				await dataSetFragmentPage.configureDataSetFragment({
					dataSetLabel,
					layout,
				});
			});

			await test.step('Check that the Item Actions dropdown is present in table row', async () => {
				const tableRow = dataSetFragmentPage.table.bodyRows
					.first()
					.locator('td.cell-item-actions');

				await expect(
					tableRow.getByRole('button', {
						exact: true,
						name: 'Actions',
					})
				).toBeVisible();

				const button = tableRow.getByRole('button', {
					exact: true,
					name: 'Actions',
				});

				const dropdownId = await button.getAttribute('aria-controls');

				await button.click();

				await page
					.locator(`#${dropdownId}`)
					.filter({has: page.getByRole('menu')})
					.waitFor();

				await expect(
					page.locator(`#${dropdownId}`).getByRole('menuitem')
				).toHaveCount(2);

				await expect(
					page.locator(`#${dropdownId}`).getByRole('menuitem', {
						exact: true,
						name: sidePanelItemActionName,
					})
				).not.toBeVisible();

				await page.keyboard.press('Escape');

				return tableRow;
			});
		}
	);
});
