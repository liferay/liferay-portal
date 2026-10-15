/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataSetManagerApiHelpersTest} from '../../../fixtures/dataSetManagerApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';
import {ECreationActionTarget} from './utils/types';

let dataSetERC: string;
let dataSetLabel: string;

export const test = mergeTests(
	apiHelpersTest,
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

	await test.step('Create table field', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'id',
			label_i18n: {en_US: 'Id'},
			type: 'string',
		});
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

test('Creation Action button does not appear if no creation action is defined', async ({
	dataSetFragmentPage,
	layout,
}) => {
	await test.step('Configure Data Set in the page', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('Check that the Creation Action button is not present', async () => {
		await expect(dataSetFragmentPage.creationMenuButton).not.toBeVisible();
	});
});

test(
	'Creation Action button does not appear if a creation action is defined but is "inactive"',
	{tag: '@LPD-39965'},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout}) => {
		const actionLabel = 'Custom Creation Action';

		await test.step('Create Creation Action', async () => {
			await dataSetManagerApiHelpers.createDataSetCreationAction({
				dataSetERC,
				label_i18n: {en_US: actionLabel},
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check that the Creation Action button is not present', async () => {
			await expect(
				dataSetFragmentPage.creationMenuButton
			).not.toBeVisible();
		});
	}
);

test('Show a simple button if only one Creation Action is defined', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
	page,
}) => {
	const actionLabel = 'Custom Creation Action';

	await test.step('Create Creation Action', async () => {
		await dataSetManagerApiHelpers.createDataSetCreationAction({
			dataSetERC,
			label_i18n: {en_US: actionLabel},
		});
	});

	await test.step('Configure Data Set in the page', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('Check that the Creation Action button is present', async () => {
		await expect(
			dataSetFragmentPage.page
				.getByRole('button', {
					name: actionLabel,
				})
				.first()
		).toBeVisible();
	});

	await test.step('Check that the Creation Action works', async () => {
		await dataSetFragmentPage.page
			.getByRole('button', {
				name: actionLabel,
			})
			.first()
			.click();

		await expect(page.getByText('Welcome to Liferay')).toBeVisible();
	});
});

test('Show the Creation Actions menu if more than one Creation Action is defined', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
}) => {
	const firstActionLabel = 'Custom Creation Action';
	const secondActionLabel = 'Another Creation Action';

	await test.step('Create Creation Actions', async () => {
		await dataSetManagerApiHelpers.createDataSetCreationAction({
			dataSetERC,
			label_i18n: {en_US: firstActionLabel},
			target: ECreationActionTarget.MODAL,
			title_i18n: {en_US: 'Modal title'},
		});

		await dataSetManagerApiHelpers.createDataSetCreationAction({
			dataSetERC,
			label_i18n: {en_US: secondActionLabel},
		});
	});

	await test.step('Configure Data Set in the page', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('Check that the Creation Action menu is present', async () => {
		await dataSetFragmentPage.creationMenuButton.first().isVisible();

		const button = dataSetFragmentPage.creationMenuButton.first();

		await button.click();

		await dataSetFragmentPage.dropdownMenu
			.filter({has: dataSetFragmentPage.page.getByRole('menu')})
			.waitFor();

		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('menuitem')
		).toHaveCount(2);

		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('menuitem', {
				exact: true,
				name: firstActionLabel,
			})
		).toBeVisible();

		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('menuitem', {
				exact: true,
				name: secondActionLabel,
			})
		).toBeVisible();

		await dataSetFragmentPage.page.keyboard.press('Escape');
	});

	await test.step('Creation Action of type "modal" opens a modal', async () => {
		await dataSetFragmentPage.creationMenuButton.first().click();

		await dataSetFragmentPage.dropdownMenu
			.getByRole('menuitem', {
				exact: true,
				name: firstActionLabel,
			})
			.click();

		await dataSetFragmentPage.page.getByRole('dialog').waitFor();

		const dialog = await dataSetFragmentPage.page.getByRole('dialog');

		await expect(dialog).toBeInViewport();

		await dialog.getByRole('button', {name: 'Close'}).click();

		await expect(dialog).not.toBeInViewport();
	});

	await test.step('Creation Action of type "link" is actionable', async () => {
		await dataSetFragmentPage.creationMenuButton.first().click();

		await dataSetFragmentPage.dropdownMenu
			.getByRole('menuitem', {
				exact: true,
				name: secondActionLabel,
			})
			.click();

		await expect(
			dataSetFragmentPage.page.getByText('Welcome to Liferay')
		).toBeVisible();
	});
});
