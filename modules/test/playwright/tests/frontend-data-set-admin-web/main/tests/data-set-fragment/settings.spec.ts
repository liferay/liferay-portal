/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

let settingsDataSetERC: string;
let dataSetLabel: string;

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	dataSetFragmentPageTest
);

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	settingsDataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await dataSetManagerApiHelpers.createDataSet({
		erc: settingsDataSetERC,
		label: dataSetLabel,
		restEndpoint: `/by-external-reference-code/${settingsDataSetERC}/dataSetToDataSetCardsSections`,
		restSchema: 'DataSetCardsSection',
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: settingsDataSetERC});
});

const configureDataset = async ({dataSetFragmentPage, layout}) => {
	await test.step('Configure Data Set in the page', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});
};

const assertVisualizationMode = async ({locator}) => {
	await test.step('Check Data Set is present', async () => {
		await expect(locator).toBeVisible();

		await expect(locator).toBeInViewport();
	});
};

const assertCardsVisualizationMode = async ({dataSetFragmentPage}) => {
	await assertVisualizationMode({
		locator: dataSetFragmentPage.cardsWrapper,
	});
};

const assertListVisualizationMode = async ({dataSetFragmentPage}) => {
	await assertVisualizationMode({
		locator: dataSetFragmentPage.listWrapper,
	});
};

test.describe('Data Set Default Visualization Mode in fragment', () => {
	test('When there is only one visualization mode defined, that will be the default one. Cards', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
	}) => {
		await test.step('Assign a field to a Card title section', async () => {
			await dataSetManagerApiHelpers.createDataSetCardsSection({
				dataSetERC: settingsDataSetERC,
			});
		});

		await configureDataset({dataSetFragmentPage, layout});

		await assertCardsVisualizationMode({dataSetFragmentPage});
	});

	test('When there are more than one visualization mode defined (cards & list), the user could change the visualization option.', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
	}) => {
		await test.step('Assign a field to a Card and List title sections', async () => {
			await dataSetManagerApiHelpers.createDataSetCardsSection({
				dataSetERC: settingsDataSetERC,
			});
			await dataSetManagerApiHelpers.createDataSetListSection({
				dataSetERC: settingsDataSetERC,
			});
		});

		await configureDataset({dataSetFragmentPage, layout});

		await assertCardsVisualizationMode({dataSetFragmentPage});

		await test.step('Change Data Set Visualization option', async () => {
			await dataSetFragmentPage.changeVisualizationMode('List');
		});

		await assertListVisualizationMode({dataSetFragmentPage});
	});

	test('When there are more than one visualization modes defined, with a default selected (List), this will be the default one in the fragment.', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		await test.step('Assign a field to a Card and List title sections', async () => {
			await dataSetManagerApiHelpers.createDataSetCardsSection({
				dataSetERC: settingsDataSetERC,
			});
			await dataSetManagerApiHelpers.createDataSetListSection({
				dataSetERC: settingsDataSetERC,
			});
		});

		await test.step('Set List as default visualization mode', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				defaultVisualizationMode: 'list',
				erc: settingsDataSetERC,
			});
		});

		await configureDataset({dataSetFragmentPage, layout});

		await assertListVisualizationMode({dataSetFragmentPage});

		await test.step('Check Default Visualization Mode option', async () => {
			await dataSetFragmentPage.activeViewSelector.waitFor({
				state: 'visible',
			});
			await dataSetFragmentPage.activeViewSelector.click();

			await page
				.getByRole('listbox', {name: 'View Options'})
				.getByRole('option', {name: 'Cards', selected: false})
				.isVisible();

			await page
				.getByRole('listbox', {name: 'View Options'})
				.getByRole('option', {name: 'List', selected: true})
				.isVisible();
		});
	});

	test('When the default visualization mode is changed in the Data Set Manager, the change is reflected in the fragment', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		await test.step('Assign a field to a Card and List title sections', async () => {
			await dataSetManagerApiHelpers.createDataSetCardsSection({
				dataSetERC: settingsDataSetERC,
			});
			await dataSetManagerApiHelpers.createDataSetListSection({
				dataSetERC: settingsDataSetERC,
			});
		});

		await test.step('Set List as default visualization mode', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				defaultVisualizationMode: 'list',
				erc: settingsDataSetERC,
			});
		});

		await configureDataset({dataSetFragmentPage, layout});

		await assertListVisualizationMode({dataSetFragmentPage});

		await test.step('Check default visualization mode option', async () => {
			await dataSetFragmentPage.activeViewSelector.waitFor({
				state: 'visible',
			});
			await dataSetFragmentPage.activeViewSelector.click();

			await page
				.getByRole('listbox', {name: 'View Options'})
				.getByRole('option', {name: 'Cards', selected: false})
				.isVisible();

			await page
				.getByRole('listbox', {name: 'View Options'})
				.getByRole('option', {name: 'List', selected: true})
				.isVisible();
		});

		await test.step('Change default visualization mode to Cards', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				defaultVisualizationMode: 'cards',
				erc: settingsDataSetERC,
			});
		});

		await test.step('Reload page and check the default visualization mode', async () => {
			await page.reload();

			await assertCardsVisualizationMode({dataSetFragmentPage});

			await dataSetFragmentPage.activeViewSelector.waitFor({
				state: 'visible',
			});
			await dataSetFragmentPage.activeViewSelector.click();

			await page
				.getByRole('listbox', {name: 'View Options'})
				.getByRole('option', {name: 'Cards', selected: true})
				.isVisible();
		});
	});
});
