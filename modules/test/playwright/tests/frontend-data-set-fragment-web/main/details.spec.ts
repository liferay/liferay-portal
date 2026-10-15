/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {dataSetManagerApiHelpersTest} from '../../../fixtures/dataSetManagerApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

export const test = mergeTests(
	accountSettingsPagesTest,
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': {enabled: true},
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

test('Check that the sort parameter is applied @LPD-25241', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
}) => {
	await test.step('Add fields', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'fieldName',
			label_i18n: {en_US: 'Field Name'},
			sortable: true,
			type: 'string',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'id',
			label_i18n: {en_US: 'ID'},
			sortable: true,
			type: 'string',
		});
	});

	await test.step('Add parameters', async () => {
		await dataSetManagerApiHelpers.updateDataSet({
			additionalAPIURLParameters: 'sort=fieldName:desc',
			erc: dataSetERC,
		});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('Check that the sorting is applied', async () => {
		const firstNameText = await dataSetFragmentPage.table.bodyRows
			.first()
			.locator('td:first-child')
			.textContent();

		const lastNameText = await dataSetFragmentPage.table.container
			.locator('tbody tr:last-child td:first-child')
			.textContent();

		expect(firstNameText > lastNameText).toBeTruthy();
	});
});

test('Check that the filter parameter is applied @LPD-25241', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
}) => {
	await test.step('Add fields', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'fieldName',
			label_i18n: {en_US: 'Field Name'},
			sortable: true,
			type: 'string',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'id',
			label_i18n: {en_US: 'ID'},
			sortable: true,
			type: 'string',
		});
	});

	await test.step('Add parameters', async () => {
		await dataSetManagerApiHelpers.updateDataSet({
			additionalAPIURLParameters: "filter=fieldName eq 'fieldName'",
			erc: dataSetERC,
		});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('Check that the filter is applied', async () => {
		const tableNameCellTexts = await dataSetFragmentPage.table.bodyRows
			.locator('td:nth-child(1)')
			.allInnerTexts();

		expect(
			tableNameCellTexts.every((value) => value === 'fieldName')
		).toBeTruthy();
	});
});
