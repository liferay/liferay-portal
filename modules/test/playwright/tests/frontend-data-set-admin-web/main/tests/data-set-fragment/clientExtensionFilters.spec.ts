/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../../utils/getRandomString';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

let dataSetERC: string;
let dataSet: any;
let dataSetLabel: string;
const DATE_FIELD_NAME = 'dateCreated';

export const test = mergeTests(
	apiHelpersTest,
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	dataSetFragmentPageTest
);

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await test.step('Create a data set', async () => {
		dataSet = await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			label: dataSetLabel,
		});
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

test('Deployed client extension filter is available in fragment @LPS-190457', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
	page,
}) => {
	const filterLabel = getRandomString();

	await test.step('Create a new client extension filter', async () => {
		await dataSetManagerApiHelpers.createDataSetClientExtensionFilter({
			clientExtensionEntryERC: 'LXC:liferay-sample-fds-filter',
			dataSetERC: dataSet.externalReferenceCode,
			fieldName: DATE_FIELD_NAME,
			label_i18n: {en_US: filterLabel},
		});
	});

	await test.step('Add a field, so FDS has something to show', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'rendererType',
			label_i18n: {en_US: getRandomString()},
			type: 'string',
		});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	const clientExtensionMenuItem = page.getByRole('menuitem', {
		name: `${filterLabel}`,
	});

	const filterButton = page.locator('.filters-dropdown').getByText('Filter');

	await expect(filterButton).toBeInViewport();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: clientExtensionMenuItem,
		timeout: 500,
		trigger: filterButton,
	});

	const filterInput = page.getByPlaceholder('Search with Odata');

	await expect(filterInput).toBeInViewport();
});

test(
	'Deployed client extension filter is not available in fragment if it is "inactive"',
	{tag: ['@LPS-190457', '@LPD-39965']},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout, page}) => {
		const filterLabel = getRandomString();

		await test.step('Create an "inactive" new client extension filter', async () => {
			await dataSetManagerApiHelpers.createDataSetClientExtensionFilter({
				active: false,
				clientExtensionEntryERC: 'LXC:liferay-sample-fds-filter',
				dataSetERC: dataSet.externalReferenceCode,
				fieldName: DATE_FIELD_NAME,
				label_i18n: {en_US: filterLabel},
			});
		});

		await test.step('Add a field, so FDS has something to show', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'rendererType',
				label_i18n: {en_US: getRandomString()},
				type: 'string',
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		const filterButton = page
			.locator('.filters-dropdown')
			.getByText('Filter');

		await expect(filterButton).not.toBeInViewport();
	}
);
