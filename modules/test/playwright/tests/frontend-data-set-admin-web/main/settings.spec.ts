/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataSetManagerApiHelpersTest} from '../../../fixtures/dataSetManagerApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {dataSetManagerSetupTest} from './fixtures/dataSetManagerSetupTest';
import {settingsPageTest} from './fixtures/settingsPageTest';
import {visualizationModesPageTest} from './fixtures/visualizationModesPageTest';

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': {enabled: true},
	}),
	loginTest(),
	dataSetManagerSetupTest,
	settingsPageTest,
	visualizationModesPageTest
);

let settingsDataSetERC: string;
let dataSetLabel: string;

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	settingsDataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await dataSetManagerApiHelpers.createDataSet({
		erc: settingsDataSetERC,
		label: dataSetLabel,
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: settingsDataSetERC});
});

const navigateToSettings = async ({settingsPage}) => {
	await test.step('Navigate to Settings section', async () => {
		await settingsPage.goto({
			dataSetLabel,
		});

		await expect(
			settingsPage.defaultVisualizationModeLabel
		).toBeInViewport();
	});
};

test('If Default Visualization Mode is not configured allows user to navigate to Visualization Mode section', async ({
	settingsPage,
	visualizationModesPage,
}) => {
	await navigateToSettings({settingsPage});

	await test.step('Check Default Visualization Mode', async () => {
		await expect(settingsPage.defaultVisualizationModeLabel).toContainText(
			'Not Configured'
		);
	});

	await test.step('Navigate to Visualization Mode section if "Not Configured"', async () => {
		await expect(settingsPage.goToVisualizationModesLink).toBeInViewport();

		await settingsPage.goToVisualizationModesLink.click();

		await expect(
			visualizationModesPage.page.getByText('No fields added yet.')
		).toBeInViewport();
	});
});

test('When there is only one visualization mode defined, that will be the default one.', async ({
	dataSetManagerApiHelpers,
	page,
	settingsPage,
}) => {
	await test.step('Assign a field to a Card title section', async () => {
		await dataSetManagerApiHelpers.createDataSetCardsSection({
			dataSetERC: settingsDataSetERC,
		});

		await page.reload();
	});

	await navigateToSettings({settingsPage});

	await test.step('Check Default Visualization Mode', async () => {
		await expect(settingsPage.defaultVisualizationModeLabel).toContainText(
			'Cards'
		);
	});
});

test('When there are more than one visualization mode defined, the user could select the default one.', async ({
	dataSetManagerApiHelpers,
	page,
	settingsPage,
}) => {
	await test.step('Assign a field to title section for Cards and List', async () => {
		await dataSetManagerApiHelpers.createDataSetCardsSection({
			dataSetERC: settingsDataSetERC,
		});
		await dataSetManagerApiHelpers.createDataSetListSection({
			dataSetERC: settingsDataSetERC,
		});

		await page.reload();
	});

	await navigateToSettings({settingsPage});

	await test.step('Check Default Visualization Mode', async () => {
		await expect(settingsPage.defaultVisualizationModeLabel).toContainText(
			'Cards'
		);
	});

	await test.step('Change Default Visualization Mode', async () => {
		await settingsPage.defaultVisualizationModeLabel.click();

		await settingsPage.page.getByRole('option', {name: 'List'}).isVisible();

		await settingsPage.page.getByRole('option', {name: 'List'}).click();
	});

	await test.step('Save Default Visualization Mode', async () => {
		await settingsPage.saveButton.click();

		await settingsPage.toastContainer.isVisible();

		await settingsPage.page
			.getByText('Success:Your request completed successfully.')
			.waitFor();

		await settingsPage.toastContainer
			.getByRole('button', {
				name: 'Close',
			})
			.click();

		await settingsPage.toastContainer.isHidden();

		await expect(settingsPage.defaultVisualizationModeLabel).toContainText(
			'List'
		);
	});
});
