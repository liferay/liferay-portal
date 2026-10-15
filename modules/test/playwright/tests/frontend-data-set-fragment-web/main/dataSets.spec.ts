/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataSetManagerApiHelpersTest} from '../../../fixtures/dataSetManagerApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../utils/performLogin';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForFDS} from '../../../utils/waitFor';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

export const test = mergeTests(
	apiHelpersTest,
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	dataSetFragmentPageTest,
	pageEditorPagesTest
);

const dataSetERCs: string[] = [];
let article: any;
let siteId: string;
let structuredContentId: number;
let structuredContentTitle: string;

const autoResolvedTokenDataSetConfig = {
	erc: getRandomString(),
	label: getRandomString(),
	restApplication: '/headless-delivery/v1.0',
	restEndpoint: '/v1.0/sites/{siteId}/structured-contents',
	restSchema: 'StructuredContent',
};

const manuallyResolvedTokenDataSetConfig = {
	erc: getRandomString(),
	label: getRandomString(),
	restApplication: '/data-set-admin/data-sets',
	restEndpoint:
		'/by-external-reference-code/{currentExternalReferenceCode}/dataSetToDataSetTableSections',
	restSchema: 'DataSetTableSection',
};

test.afterEach(async ({apiHelpers, dataSetManagerApiHelpers}) => {
	for (const erc of dataSetERCs) {
		await dataSetManagerApiHelpers.deleteDataSet({
			erc,
		});
	}

	dataSetERCs.length = 0;

	if (article) {
		await test.step('Move article to trash', async () => {
			await apiHelpers.jsonWebServicesJournal.moveArticleToTrash(
				siteId,
				article.articleId
			);
		});

		article = null;
	}
});

test('Data set selection modal shows a "No results found" message when there are no data sets created', async ({
	dataSetFragmentPage,
	layout,
}) => {
	await test.step('Go to page configuration, add "Data Set" fragment', async () => {
		await dataSetFragmentPage.addDataSetFragment(layout);
	});

	await test.step('Open data set selection modal', async () => {
		await dataSetFragmentPage.selectDataSetButton.click();
	});

	await test.step('Assert that there are no Data Sets available to select', async () => {
		await expect(
			dataSetFragmentPage.selectDataSetModal.container.locator(
				'.c-empty-state-title'
			)
		).toContainText('No Results Found');
	});
});

test(
	'Assign a data set to the "Data Set" fragment, change and delete assignment',
	{
		tag: ['@LPD-93809', '@LPS-172403', '@LPS-190724'],
	},
	async ({
		apiHelpers,
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const structuredContentDescription = getRandomString();
		structuredContentTitle = 'Sample Structured Content title';
		structuredContentId = await getBasicWebContentStructureId(apiHelpers);

		siteId = await page.evaluate(() => {
			return String(Liferay.ThemeDisplay.getSiteGroupId());
		});

		await test.step('Create a structured content', async () => {
			article = await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: structuredContentId,
				descriptionMap: {en_US: structuredContentDescription},
				groupId: siteId,
				titleMap: {en_US: structuredContentTitle},
			});
		});

		await test.step('Create a data set with automatically resolved token in endpoint URL', async () => {
			dataSetERCs.push(autoResolvedTokenDataSetConfig.erc);

			await dataSetManagerApiHelpers.createDataSet({
				erc: autoResolvedTokenDataSetConfig.erc,
				label: autoResolvedTokenDataSetConfig.label,
				restApplication: autoResolvedTokenDataSetConfig.restApplication,
				restEndpoint: autoResolvedTokenDataSetConfig.restEndpoint,
				restSchema: autoResolvedTokenDataSetConfig.restSchema,
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC: autoResolvedTokenDataSetConfig.erc,
				fieldName: 'title',
				label_i18n: {
					en_US: 'Title',
				},
				sortable: false,
				type: 'string',
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC: autoResolvedTokenDataSetConfig.erc,
				fieldName: 'description',
				label_i18n: {en_US: 'Description'},
				sortable: false,
				type: 'string',
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC: autoResolvedTokenDataSetConfig.erc,
				fieldName: 'id',
				label_i18n: {en_US: 'ID'},
				sortable: false,
				type: 'string',
			});
		});

		await test.step('Create a data set with manually resolved token in endpoint URL', async () => {
			dataSetERCs.push(manuallyResolvedTokenDataSetConfig.erc);

			await dataSetManagerApiHelpers.createDataSet({
				erc: manuallyResolvedTokenDataSetConfig.erc,
				label: manuallyResolvedTokenDataSetConfig.label,
				restApplication:
					manuallyResolvedTokenDataSetConfig.restApplication,
				restEndpoint: manuallyResolvedTokenDataSetConfig.restEndpoint,
				restSchema: manuallyResolvedTokenDataSetConfig.restSchema,
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC: manuallyResolvedTokenDataSetConfig.erc,
				fieldName: 'id',
				label_i18n: {
					en_US: 'ID',
				},
				sortable: false,
				type: 'number',
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC: manuallyResolvedTokenDataSetConfig.erc,
				fieldName: 'fieldName',
				label_i18n: {en_US: 'Field Name'},
				sortable: false,
				type: 'string',
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC: manuallyResolvedTokenDataSetConfig.erc,
				fieldName: 'label',
				label_i18n: {en_US: 'Label'},
				sortable: false,
				type: 'string',
			});
		});

		await test.step('Configure Data Set fragment with automatically resolved token', async () => {
			await dataSetFragmentPage.addDataSetFragment(layout);

			await dataSetFragmentPage.selectDataSetButton.click();

			await dataSetFragmentPage.selectDataSetModal.container
				.locator('li')
				.filter({hasText: autoResolvedTokenDataSetConfig.label})
				.locator('input')
				.click();

			await dataSetFragmentPage.selectDataSetModal.selectButton.click();

			await expect(dataSetFragmentPage.selectedDataSetInput).toHaveValue(
				autoResolvedTokenDataSetConfig.label
			);

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Completed');
		});

		await test.step('Assert that the data set is displayed', async () => {
			await waitForFDS({page});

			expect(
				await dataSetFragmentPage.table.headRow
					.locator('th')
					.allInnerTexts()
			).toEqual([
				'Title',
				'Description',
				'ID',
				'Manage Columns Visibility',
			]);

			expect(
				await dataSetFragmentPage.table.bodyRows
					.locator('td')
					.allInnerTexts()
			).toEqual(
				expect.arrayContaining([
					structuredContentTitle,
					structuredContentDescription,
				])
			);
		});

		await test.step('Change data set to sample with manually resolved token', async () => {
			await dataSetFragmentPage.changeDataSetButton.click();

			await dataSetFragmentPage.selectDataSetModal.container
				.locator('li')
				.filter({hasText: manuallyResolvedTokenDataSetConfig.label})
				.locator('input')
				.click();

			await dataSetFragmentPage.selectDataSetModal.selectButton.click();

			await expect(dataSetFragmentPage.selectedDataSetInput).toHaveValue(
				manuallyResolvedTokenDataSetConfig.label
			);

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Incomplete');

			await dataSetFragmentPage.selectToken(
				'currentExternalReferenceCode'
			);

			await dataSetFragmentPage.fillTokenValue(
				manuallyResolvedTokenDataSetConfig.erc
			);

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Completed');
		});

		await test.step('Assert that the data set is displayed', async () => {
			await waitForFDS({page});

			expect(
				await dataSetFragmentPage.table.headRow
					.locator('th')
					.allInnerTexts()
			).toEqual([
				'ID',
				'Field Name',
				'Label',
				'Manage Columns Visibility',
			]);
		});

		await test.step('Unassign data set', async () => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					name: 'Remove Data Set',
				}),
				trigger: page.getByRole('button', {
					name: 'View Data Set Options',
				}),
			});

			await expect(dataSetFragmentPage.selectedDataSetInput).toHaveValue(
				''
			);

			await expect(
				dataSetFragmentPage.fragmentSelectionArea
			).toBeVisible();
		});
	}
);

test('An unauthorized user accessing a page with a data set fragment', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
	page,
}) => {
	const dataSetERC = getRandomString();
	const dataSetLabel = getRandomString();

	dataSetERCs.push(dataSetERC);

	await test.step('Create data set', async () => {
		await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			label: dataSetLabel,
		});
	});

	await test.step('Create sample data for data sets', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'fieldName',
			label_i18n: {en_US: 'Field Name'},
		});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('Log out', async () => {
		await performLogout(page);

		await expect(page.getByRole('button', {name: 'Sign In'})).toBeVisible();
	});

	try {
		await test.step('Go to Data Set fragment page', async () => {
			await dataSetFragmentPage.goToPage({layout});

			await page
				.locator('.data-set-content-wrapper')
				.waitFor({state: 'visible'});
		});

		await test.step('Assert that no results are displayed', async () => {
			await expect(
				page
					.locator('.data-set-content-wrapper')
					.getByText('No Results Found')
			).toBeVisible();
		});
	}
	finally {
		await test.step('Log back in as admin', async () => {
			await performLogin(page, 'test');
		});
	}
});
