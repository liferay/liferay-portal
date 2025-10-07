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

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	dataSetFragmentPageTest,
	isolatedLayoutTest({publish: false})
);

let dataSetERC: string;

const dataSetLabel: string = getRandomString();

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();

	await dataSetManagerApiHelpers.createDataSet({
		erc: dataSetERC,
		label: dataSetLabel,
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({
		erc: dataSetERC,
	});
});

test.describe('Visualization Modes in Data Set fragment', () => {
	test('Show mapped fields in the fragment', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const SAMPLE_SCALAR_FIELD = 'id';
		const SAMPLE_OBJECT_FIELD = 'dataSetToDataSetTableSections';
		const SAMPLE_OBJECT_CHILD_FIELD = 'label';

		await test.step('Update Data Set to include additionalAPIURLParameters', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				additionalAPIURLParameters:
					'fields=dataSetToDataSetTableSections.label,id,label',
				erc: dataSetERC,
			});
		});

		await test.step('Create table fields', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: `${SAMPLE_OBJECT_FIELD}.${SAMPLE_OBJECT_CHILD_FIELD}`,
				label_i18n: {en_US: 'Data Set Label'},
				type: 'string',
			});
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: `${SAMPLE_OBJECT_CHILD_FIELD}`,
				label_i18n: {en_US: 'Table Section Label'},
				type: 'string',
			});
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: `${SAMPLE_SCALAR_FIELD}`,
				label_i18n: {en_US: 'Id'},
				sortable: true,
				type: 'string',
			});
		});

		await test.step('Create cards section fields', async () => {
			await dataSetManagerApiHelpers.createDataSetCardsSection({
				dataSetERC,
				fieldName: `${SAMPLE_OBJECT_FIELD}.${SAMPLE_OBJECT_CHILD_FIELD}`,
				name: 'title',
			});
			await dataSetManagerApiHelpers.createDataSetCardsSection({
				dataSetERC,
				fieldName: `${SAMPLE_SCALAR_FIELD}`,
				name: 'description',
			});
		});

		await test.step('Create list section fields', async () => {
			await dataSetManagerApiHelpers.createDataSetListSection({
				dataSetERC,
				fieldName: `${SAMPLE_OBJECT_FIELD}.${SAMPLE_OBJECT_CHILD_FIELD}`,
				name: 'title',
			});
			await dataSetManagerApiHelpers.createDataSetListSection({
				dataSetERC,
				fieldName: `${SAMPLE_SCALAR_FIELD}`,
				name: 'description',
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check Data Set Cards are present', async () => {
			await dataSetFragmentPage.cardsWrapper.waitFor({
				state: 'visible',
			});

			await expect(dataSetFragmentPage.cardsWrapper).toBeInViewport();

			await dataSetFragmentPage.page.locator('.card').first().waitFor();

			const firstCard = dataSetFragmentPage.page.locator('.card').first();

			await expect(firstCard.locator('.card-title')).toContainText(
				dataSetLabel
			);

			await expect(firstCard.locator('.card-subtitle')).not.toBeEmpty();
		});

		await test.step('Change visualization mode to List', async () => {
			await dataSetFragmentPage.changeVisualizationMode('List');
		});

		await test.step('Check Data Set List is present', async () => {
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

			await expect(
				firstListItem.locator('.list-group-title')
			).toContainText(dataSetLabel);

			await expect(
				firstListItem.locator('.list-group-text')
			).not.toBeEmpty();
		});

		await test.step('Change visualization mode to Table', async () => {
			await dataSetFragmentPage.changeVisualizationMode('Table');
		});

		await test.step('Data Set Table is in the page', async () => {
			expect(
				await dataSetFragmentPage.table.headRow
					.locator('th')
					.allInnerTexts()
			).toEqual([
				'Data Set Label',
				'Table Section Label',
				'Id',
				'Manage Columns Visibility',
			]);
		});

		await test.step('Data Set request URL contains additionalAPIURLParameters and nestedFields', async () => {
			const additionalAPIURLParameters =
				'fields=dataSetToDataSetTableSections.label%2Cid%2Clabel';

			const nestedFieldsValue =
				'nestedFields=dataSetToDataSetTableSections';

			const datasetRequestPromise = page.waitForRequest((request) => {
				return (
					request.url().includes(`${nestedFieldsValue}`) &&
					request
						.url()
						.includes(
							`&page=1&pageSize=20&sort=id%3Aasc&${additionalAPIURLParameters}`
						)
				);
			});

			// Force data request

			await dataSetFragmentPage.sortBy(SAMPLE_SCALAR_FIELD);

			const datasetRequest = await datasetRequestPromise;

			let datasetRequestUrl: string;

			if (datasetRequest) {
				try {
					datasetRequestUrl = datasetRequest.url();
				}
				catch (error) {
					console.error(
						'Error reading request datasetRequest.url:',
						error
					);
				}
			}
			else {
				console.error('Request not received within the timeout period');
			}

			expect(datasetRequestUrl).toContain(nestedFieldsValue);

			expect(datasetRequestUrl).toContain(additionalAPIURLParameters);
		});
	});

	test('Show mapped scalar array field in the fragment @LPD-11769', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
	}) => {
		const SAMPLE_SCALAR_ARRAY_FIELD = 'keywords';
		const SAMPLE_SCALAR_ARRAY_CONTENT = ['one', 'two', 'three'];

		await test.step('Create table fields', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				extraBodyParams: {
					keywords: SAMPLE_SCALAR_ARRAY_CONTENT,
				},
				fieldName: SAMPLE_SCALAR_ARRAY_FIELD,
				label_i18n: {en_US: SAMPLE_SCALAR_ARRAY_FIELD},
				type: 'array',
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Data Set Table is in the page', async () => {
			expect(
				await dataSetFragmentPage.table.headRow
					.locator('th')
					.allInnerTexts()
			).toEqual([SAMPLE_SCALAR_ARRAY_FIELD, 'Manage Columns Visibility']);

			expect(
				await dataSetFragmentPage.table.bodyRows
					.first()
					.locator('td')
					.allInnerTexts()
			).toEqual(['one, two, three', '']);
		});
	});
});
