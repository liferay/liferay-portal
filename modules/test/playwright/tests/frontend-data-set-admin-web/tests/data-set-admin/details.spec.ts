/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {dataSetManagerSetupTest} from './fixtures/dataSetManagerSetupTest';
import {detailsPageTest} from './fixtures/detailsPageTest';
import {visualizationModesPageTest} from './fixtures/visualizationModesPageTest';

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': true,
	}),
	loginTest(),
	dataSetManagerSetupTest,
	detailsPageTest,
	visualizationModesPageTest
);

let detailsDataSetERC: string;
let dataSetLabel: string;

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	detailsDataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await dataSetManagerApiHelpers.createDataSet({
		erc: detailsDataSetERC,
		label: dataSetLabel,
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: detailsDataSetERC});
});

test.describe('Data Set Details', () => {
	test.describe('Advanced Optional Parameters', () => {
		test('Parameters persist after saving @LPD-25241', async ({
			detailsPage,
			page,
		}) => {
			await test.step('Navigate to Details section', async () => {
				await detailsPage.goto({
					dataSetLabel,
				});
			});

			await test.step('Fill parameters input', async () => {
				await detailsPage.parametersInput.fill('sort=name:asc');
			});

			await test.step('Save the data set', async () => {
				await detailsPage.saveButton.click();

				await waitForAlert(page);
			});

			await test.step('Reload the page', async () => {
				await page.reload();
			});

			await test.step('Check that the parameters input persisted', async () => {
				await expect(detailsPage.parametersInput).toHaveValue(
					'sort=name:asc'
				);
			});
		});

		test('URL Preview encodes the parameters input @LPD-25241', async ({
			detailsPage,
		}) => {
			await test.step('Navigate to Details section', async () => {
				await detailsPage.goto({
					dataSetLabel,
				});
			});

			await test.step('Fill parameters input', async () => {
				await detailsPage.parametersInput.fill(
					'filter=name eq "Test Test"'
				);
			});

			await test.step('Check the URL Preview input properly displays the REST endpoint with the encoded parameters', async () => {
				await expect(detailsPage.urlPreviewInput).toHaveValue(
					'/data-set-manager/table-sections?filter=name%20eq%20%22Test%20Test%22'
				);
			});
		});
	});
});
