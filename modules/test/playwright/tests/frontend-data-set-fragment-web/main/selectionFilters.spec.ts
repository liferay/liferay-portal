/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataSetManagerApiHelpersTest} from '../../../fixtures/dataSetManagerApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {picklistApiHelpersTest} from '../../../fixtures/picklistApiHelpersTest';
import getRandomString from '../../../utils/getRandomString';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';
import {API_ENDPOINT_PATH} from './utils/constants';

const picklistBooleanOptionLabel = 'Boolean';
const picklistDefaultOptionLabel = 'Default';

const picklistBooleanOptionKey = picklistBooleanOptionLabel.toLocaleLowerCase();
const picklistDefaultOptionKey = picklistDefaultOptionLabel.toLocaleLowerCase();

const picklistIntegerOptionLabelActive = 'Active';
const picklistIntegerOptionLabelInactive = 'Inactive';
const picklistIntegerOptionKeyActive = '1';
const picklistIntegerOptionKeyInactive = '0';

const picklistBooleanOptionLabelTrue = 'True';
const picklistBooleanOptionLabelFalse = 'False';
const picklistBooleanOptionKeyTrue =
	picklistBooleanOptionLabelTrue.toLocaleLowerCase();
const picklistBooleanOptionKeyFalse =
	picklistBooleanOptionLabelFalse.toLocaleLowerCase();

const picklistCollectionOptionLabelAnyKey = 'AnyKey';
const picklistCollectionOptionLabelEggs = 'Eggs';
const picklistCollectionOptionKeyAnyKey =
	picklistCollectionOptionLabelAnyKey.toLocaleLowerCase();
const picklistCollectionOptioKeyEggs =
	picklistCollectionOptionLabelEggs.toLocaleLowerCase();

const apiHeadlessName = 'FieldType';
const apiHeadlessURL = `c/${apiHeadlessName.toLocaleLowerCase()}s`;
const dataSetERCs: string[] = [];
let dataSetERC: string;
let dataSetLabel: string;
let objectDefinition: any;
let picklistBooleanOption: any;
let picklistDefaultOption: any;

let picklistIntegerOptionActive: any;
let picklistIntegerOptionInactive: any;

let picklistBooleanOptionTrue: any;
let picklistBooleanOptionFalse: any;

let integerPicklist;
let integerPicklistERC;
let integerPicklistName: string;

let booleanPicklist;
let booleanPicklistERC;
let booleanPicklistName: string;

let collectionPicklist;
let collectionPicklistERC;
let collectionPicklistName: string;

let picklistCollectionOptionAnyKey: any;
let picklistCollectionOptionEggs: any;

let picklistERC: any;
let picklistName: string;

export const test = mergeTests(
	apiHelpersTest,
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	dataSetFragmentPageTest,
	picklistApiHelpersTest
);

test.beforeEach(
	async ({apiHelpers, dataSetManagerApiHelpers, picklistApiHelpers}) => {
		dataSetERC = getRandomString();
		dataSetLabel = getRandomString();
		picklistName = getRandomString();
		integerPicklistName = getRandomString();
		booleanPicklistName = getRandomString();
		collectionPicklistName = getRandomString();

		dataSetERCs.push(dataSetERC);

		await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			keywords: ['foo', 'bar'],
			label: dataSetLabel,
		});

		await test.step('Create and populate a picklist', async () => {
			const picklist = await picklistApiHelpers.createPicklist({
				name: picklistName,
			});

			picklistERC = picklist.externalReferenceCode;

			picklistDefaultOption = await picklistApiHelpers.editPicklist({
				externalReferenceCode: picklistERC,
				key: picklistDefaultOptionKey,
				value: picklistDefaultOptionLabel,
			});

			picklistBooleanOption = await picklistApiHelpers.editPicklist({
				externalReferenceCode: picklistERC,
				key: picklistBooleanOptionKey,
				value: picklistBooleanOptionLabel,
			});
		});

		await test.step('Create and populate a picklist within integer options', async () => {
			integerPicklist = await picklistApiHelpers.createPicklist({
				name: integerPicklistName,
			});

			integerPicklistERC = integerPicklist.externalReferenceCode;

			picklistIntegerOptionInactive =
				await picklistApiHelpers.editPicklist({
					externalReferenceCode: integerPicklistERC,
					key: picklistIntegerOptionKeyInactive,
					value: picklistIntegerOptionLabelInactive,
				});

			picklistIntegerOptionActive = await picklistApiHelpers.editPicklist(
				{
					externalReferenceCode: integerPicklistERC,
					key: picklistIntegerOptionKeyActive,
					value: picklistIntegerOptionLabelActive,
				}
			);
		});

		await test.step('Create and populate a picklist within boolean options', async () => {
			booleanPicklist = await picklistApiHelpers.createPicklist({
				name: booleanPicklistName,
			});

			booleanPicklistERC = booleanPicklist.externalReferenceCode;

			picklistBooleanOptionTrue = await picklistApiHelpers.editPicklist({
				externalReferenceCode: booleanPicklistERC,
				key: picklistBooleanOptionKeyTrue,
				value: picklistBooleanOptionLabelTrue,
			});

			picklistBooleanOptionFalse = await picklistApiHelpers.editPicklist({
				externalReferenceCode: booleanPicklistERC,
				key: picklistBooleanOptionKeyFalse,
				value: picklistBooleanOptionLabelFalse,
			});
		});

		await test.step('Create and populate a picklist with collection options', async () => {
			collectionPicklist = await picklistApiHelpers.createPicklist({
				name: collectionPicklistName,
			});

			collectionPicklistERC = collectionPicklist.externalReferenceCode;

			picklistCollectionOptionAnyKey =
				await picklistApiHelpers.editPicklist({
					externalReferenceCode: collectionPicklistERC,
					key: picklistCollectionOptionKeyAnyKey,
					value: picklistCollectionOptionLabelAnyKey,
				});

			picklistCollectionOptionEggs =
				await picklistApiHelpers.editPicklist({
					externalReferenceCode: collectionPicklistERC,
					key: picklistCollectionOptioKeyEggs,
					value: picklistCollectionOptionLabelEggs,
				});
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await test.step('Create a Headless application and populate with filter values', async () => {
			objectDefinition = (
				await objectDefinitionAPIClient.postObjectDefinition({
					enableLocalization: true,
					label: {
						en_US: 'Field Type',
					},
					modifiable: true,
					name: apiHeadlessName,
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							indexed: true,
							indexedAsKeyword: true,
							label: {
								en_US: 'type',
							},
							localized: true,
							name: 'type',
							required: false,
							state: false,
						},
						{
							DBType: 'String',
							businessType: 'MultiselectPicklist',
							indexed: true,
							indexedAsKeyword: true,
							label: {
								en_US: 'picklist',
							},
							listTypeDefinitionExternalReferenceCode:
								picklistERC,
							localized: false,
							name: 'picklist',
							required: false,
							state: false,
						},
					],
					pluralLabel: {en_US: `${apiHeadlessName}s`},
					scope: 'company',
				})
			).body;

			await objectDefinitionAPIClient.postObjectDefinitionPublish(
				objectDefinition.id
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{type: 'array'},
				apiHeadlessURL
			);
			await apiHelpers.objectEntry.postObjectEntry(
				{type: 'boolean'},
				apiHeadlessURL
			);
			await apiHelpers.objectEntry.postObjectEntry(
				{type: 'integer'},
				apiHeadlessURL
			);
			await apiHelpers.objectEntry.postObjectEntry(
				{
					picklist: [
						{
							key: picklistBooleanOptionKey,
							name: picklistBooleanOptionLabel,
						},
					],
					type: 'object',
				},
				apiHeadlessURL
			);
			await apiHelpers.objectEntry.postObjectEntry(
				{
					picklist: [
						{
							key: picklistDefaultOptionKey,
							name: picklistDefaultOptionLabel,
						},
					],
					type: 'string',
				},
				apiHeadlessURL
			);
		});
	}
);

test.afterEach(
	async ({apiHelpers, dataSetManagerApiHelpers, picklistApiHelpers}) => {
		for (const DATA_SET_ERC of dataSetERCs) {
			await dataSetManagerApiHelpers.deleteDataSet({
				erc: DATA_SET_ERC,
			});
		}

		dataSetERCs.length = 0;

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await objectDefinitionAPIClient.deleteObjectDefinition(
			objectDefinition.id
		);

		const picklists = await picklistApiHelpers.getPicklists();
		const picklistsNames = [
			picklistName,
			integerPicklistName,
			booleanPicklistName,
			collectionPicklistName,
		];

		await picklistApiHelpers.deleteBatchPicklist(picklistsNames, picklists);
	}
);

test('Selection filter of type "Object Picklist" is displayed in fragment @LPD-10754', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
	picklistApiHelpers,
}) => {
	const filterLabel = getRandomString();

	await test.step('Add a field, so FDS has something to show', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'renderer',
			label_i18n: {en_US: 'Renderer'},
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'sortable',
			label_i18n: {en_US: 'Sortable'},
			renderer: 'boolean',
		});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('There are no filters in the Frontend Data Set', async () => {
		await expect(dataSetFragmentPage.filterButton).not.toBeVisible();
	});

	await test.step('Create a new selection filter', async () => {
		const picklist = await picklistApiHelpers.getPicklist(picklistERC);

		await dataSetManagerApiHelpers.createDataSetSelectionFilter({
			dataSetERC,
			fieldName: 'renderer',
			label_i18n: {en_US: filterLabel},
			source: picklist.externalReferenceCode,
			sourceType: 'OBJECT_PICKLIST',
		});
	});

	await test.step('Check current items in the Frontend Data Set', async () => {
		await dataSetFragmentPage.goToPage({layout});

		await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

		await expect(
			dataSetFragmentPage.paginationResults.getByText(
				'Showing 1 to 2 of 2 entries.'
			)
		).toBeVisible();
	});

	await test.step('Select filter', async () => {
		await dataSetFragmentPage.selectFilter(filterLabel);
	});

	await test.step('Configure and apply filter', async () => {
		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('radio', {
				name: picklistDefaultOptionLabel,
			})
		).toBeVisible();
		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('radio', {
				name: picklistBooleanOptionLabel,
			})
		).toBeVisible();

		await dataSetFragmentPage.dropdownMenu
			.getByRole('radio', {name: picklistBooleanOptionLabel})
			.check();

		await dataSetFragmentPage.addFilterButton.click();
	});

	await test.step('Assert that the filter is hidden', async () => {
		await expect(dataSetFragmentPage.filterConfirmButton).not.toBeVisible();
	});

	await test.step('Check that the filter works', async () => {
		await dataSetFragmentPage.filterResumeButton.waitFor({
			state: 'visible',
		});

		await expect(
			dataSetFragmentPage.page.getByRole('button', {
				name: `${filterLabel}: ${picklistBooleanOptionLabel}`,
			})
		).toBeVisible();

		await expect(
			dataSetFragmentPage.table.bodyRows.first().locator('td')
		).toHaveText(['boolean', 'No', '']);

		await expect(
			dataSetFragmentPage.page.getByText('Showing 1 to 1 of 1 entries.')
		).toBeVisible();
	});
});

test('Selection filter of type "Object Picklist" can be enabled and disabled', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
	picklistApiHelpers,
}) => {
	const filterLabel = getRandomString();
	let selectionFilter;

	await test.step('Add fields, so FDS has something to show', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'renderer',
			label_i18n: {en_US: 'Renderer'},
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'sortable',
			label_i18n: {en_US: 'Sortable'},
			renderer: 'boolean',
		});
	});

	await test.step('Create a new "inactive" single selection filter', async () => {
		const picklist = await picklistApiHelpers.getPicklist(picklistERC);

		selectionFilter =
			await dataSetManagerApiHelpers.createDataSetSelectionFilter({
				active: false,
				dataSetERC,
				fieldName: 'renderer',
				label_i18n: {en_US: filterLabel},
				multiple: false,
				source: picklist.externalReferenceCode,
				sourceType: 'OBJECT_PICKLIST',
			});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('There are no filters in the Frontend Data Set', async () => {
		await expect(dataSetFragmentPage.filterButton).not.toBeVisible();
	});

	await test.step('Update filter and make it "active"', async () => {
		await dataSetManagerApiHelpers.updateDataSetSelectionFilter({
			active: true,
			dataSetERC,
			selectionFilterERC: selectionFilter.externalReferenceCode,
		});
	});

	await test.step('Reload and check that the filter appears the Frontend Data Set', async () => {
		await dataSetFragmentPage.goToPage({layout});

		await expect(dataSetFragmentPage.filterButton).toBeInViewport();
	});
});

test('Selection filter of type "Object Picklist" can be configured to include or exclude selected values', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
	picklistApiHelpers,
}) => {
	const filterLabel = getRandomString();
	let selectionFilter;

	await test.step('Add fields, so FDS has something to show', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'renderer',
			label_i18n: {en_US: 'Renderer'},
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'sortable',
			label_i18n: {en_US: 'Sortable'},
			renderer: 'boolean',
		});
	});

	await test.step('Create a new selection filter with preselected values and include mode', async () => {
		const picklist = await picklistApiHelpers.getPicklist(picklistERC);

		selectionFilter =
			await dataSetManagerApiHelpers.createDataSetSelectionFilter({
				dataSetERC,
				fieldName: 'renderer',
				include: true,
				label_i18n: {en_US: filterLabel},
				multiple: false,
				preselectedValues: JSON.stringify([
					{
						label: getRandomString(),
						value: picklistDefaultOption.externalReferenceCode,
					},
				]),
				source: picklist.externalReferenceCode,
				sourceType: 'OBJECT_PICKLIST',
			});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('Check current filter is applied in the Frontend Data Set', async () => {
		await expect(dataSetFragmentPage.filterResumeButton).toBeVisible();
		await expect(dataSetFragmentPage.filterResumeButton).toContainText(
			`${filterLabel}: ${picklistDefaultOptionLabel}`
		);
		await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

		await expect(
			dataSetFragmentPage.table.bodyRows.first().locator('td')
		).toHaveText(['default', 'No', '']);

		await expect(
			dataSetFragmentPage.paginationResults.getByText(
				'Showing 1 to 1 of 1 entries.'
			)
		).toBeVisible();
	});

	await test.step('Update filter to use preselected values exclude mode', async () => {
		await dataSetManagerApiHelpers.updateDataSetSelectionFilter({
			dataSetERC,
			preselectedValues: JSON.stringify([
				{
					label: getRandomString(),
					value: picklistBooleanOption.externalReferenceCode,
				},
			]),
			selectionFilterERC: selectionFilter.externalReferenceCode,
		});
	});

	await test.step('Check current items in the Frontend Data Set', async () => {
		await dataSetFragmentPage.goToPage({layout});

		await expect(dataSetFragmentPage.filterResumeButton).toContainText(
			`${filterLabel}: ${picklistBooleanOptionLabel}`
		);

		await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

		await expect(
			dataSetFragmentPage.table.bodyRows.first().locator('td')
		).toHaveText(['boolean', 'No', '']);

		await expect(
			dataSetFragmentPage.paginationResults.getByText(
				'Showing 1 to 1 of 1 entries.'
			)
		).toBeVisible();
	});

	await test.step('Can remove the current filter', async () => {
		await dataSetFragmentPage.page
			.getByRole('button', {exact: true, name: 'Remove Filter'})
			.click();
		await expect(dataSetFragmentPage.filterResumeButton).not.toBeVisible();

		await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

		await expect(
			dataSetFragmentPage.paginationResults.getByText(
				'Showing 1 to 2 of 2 entries.'
			)
		).toBeVisible();
	});
});

test(
	'Selection filter of type "Object Picklist" using a "Multiselect Picklist" type field',
	{tag: '@LPD-53174'},
	async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		picklistApiHelpers,
	}) => {
		const filterLabel = 'picklist';
		const picklistDataSetERC = getRandomString();
		const picklistDataSetLabel = getRandomString();

		await test.step('Create a new data set for FieldType', async () => {
			dataSetERCs.push(picklistDataSetERC);

			await dataSetManagerApiHelpers.createDataSet({
				erc: picklistDataSetERC,
				keywords: ['foo', 'fred'],
				label: picklistDataSetLabel,
				restApplication: `/${apiHeadlessURL}`,
				restSchema: apiHeadlessName,
			});
		});

		await test.step('Add fields, so FDS has something to show', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC: picklistDataSetERC,
				fieldName: 'picklist',
				label_i18n: {en_US: 'Picklist'},
			});
		});

		await test.step('Create a selection filter with the picklist', async () => {
			const picklist = await picklistApiHelpers.getPicklist(picklistERC);

			await dataSetManagerApiHelpers.createDataSetSelectionFilter({
				dataSetERC: picklistDataSetERC,
				fieldName: 'picklist[]name',
				label_i18n: {en_US: filterLabel},
				multiple: true,
				source: picklist.externalReferenceCode,
				sourceType: 'OBJECT_PICKLIST',
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel: picklistDataSetLabel,
				layout,
			});
		});

		await test.step(`Select ${filterLabel} filter`, async () => {
			await dataSetFragmentPage.selectFilter(filterLabel);
		});

		await test.step('Configure and apply filter', async () => {
			await expect(
				dataSetFragmentPage.dropdownMenu.getByRole('checkbox', {
					name: picklistDefaultOptionLabel,
				})
			).toBeVisible();
			await expect(
				dataSetFragmentPage.dropdownMenu.getByRole('checkbox', {
					name: picklistDefaultOptionLabel,
				})
			).toBeVisible();

			await dataSetFragmentPage.dropdownMenu
				.getByRole('checkbox', {name: picklistDefaultOptionLabel})
				.check();

			await dataSetFragmentPage.addFilterButton.click();
		});

		await test.step('Check that the filter works', async () => {
			await dataSetFragmentPage.filterResumeButton.waitFor({
				state: 'visible',
			});

			await expect(
				dataSetFragmentPage.page.getByRole('button', {
					name: `${filterLabel}: ${picklistDefaultOptionLabel}`,
				})
			).toBeVisible();

			const rows = await dataSetFragmentPage.table.bodyRows.all();

			for (const row of rows) {
				await expect(row.locator('td:first-child')).toHaveText(
					'[{"key":"default","name":"Default"}]'
				);
			}
		});
	}
);

test('Selection filter of type "API REST Application" is displayed in fragment @LPD-10754', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
}) => {
	const filterLabel = getRandomString();

	await test.step('Add fields, so FDS has something to show', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'id',
			label_i18n: {en_US: 'Id'},
			type: 'integer',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'type',
			label_i18n: {en_US: 'Type'},
			type: 'string',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'sortable',
			label_i18n: {en_US: 'Sortable'},
			type: 'boolean',
		});
	});

	await test.step('Create a new "API Rest Application" selection filter', async () => {
		await dataSetManagerApiHelpers.createDataSetSelectionFilter({
			dataSetERC,
			fieldName: 'type',
			itemKey: 'type',
			itemLabel: 'type',
			label_i18n: {en_US: filterLabel},
			multiple: true,
			source: `/o/${apiHeadlessURL}`,
			sourceType: 'API_REST_APPLICATION',
		});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('Check current items in the Frontend Data Set', async () => {
		await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

		await expect(
			dataSetFragmentPage.paginationResults.getByText(
				'Showing 1 to 3 of 3 entries.'
			)
		).toBeVisible();
	});

	await test.step('Select filter', async () => {
		await dataSetFragmentPage.selectFilter(filterLabel);
	});

	await test.step('Configure and apply filter', async () => {
		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('checkbox', {
				name: 'array',
			})
		).toBeVisible();
		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('checkbox', {
				name: 'boolean',
			})
		).toBeVisible();
		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('checkbox', {
				name: 'integer',
			})
		).toBeVisible();
		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('checkbox', {
				name: 'object',
			})
		).toBeVisible();
		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('checkbox', {
				name: 'string',
			})
		).toBeVisible();

		await dataSetFragmentPage.dropdownMenu
			.getByRole('checkbox', {name: 'integer'})
			.check();
		await dataSetFragmentPage.dropdownMenu
			.getByRole('button', {name: 'Add filter'})
			.click();
	});

	await test.step('Assert that the filter is hidden', async () => {
		await expect(dataSetFragmentPage.filterConfirmButton).not.toBeVisible();
	});

	await test.step('Check that the filter works', async () => {
		await dataSetFragmentPage.filterResumeButton.waitFor({
			state: 'visible',
		});

		await expect(
			dataSetFragmentPage.page.getByRole('button', {
				name: `${filterLabel}: integer`,
			})
		).toBeVisible();

		await expect(
			dataSetFragmentPage.table.bodyRows
				.filter({
					has: dataSetFragmentPage.page
						.getByText('integer', {exact: true})
						.first(),
				})
				.locator('td')
				.nth(1)
		).toHaveText(['integer']);

		await expect(
			dataSetFragmentPage.page.getByText('Showing 1 to 1 of 1 entries.')
		).toBeVisible();
	});

	await test.step('Open filters component', async () => {
		await dataSetFragmentPage.filterButton.click();
	});

	await test.step('Select filter', async () => {
		await expect(
			dataSetFragmentPage.dropdownMenu.getByRole('checkbox', {
				name: 'boolean',
			})
		).toBeVisible();

		await dataSetFragmentPage.dropdownMenu
			.getByRole('checkbox', {name: 'boolean'})
			.check();

		await dataSetFragmentPage.filterConfirmButton.click();
	});

	await test.step('Assert that the filter is hidden', async () => {
		await expect(dataSetFragmentPage.filterConfirmButton).not.toBeVisible();
	});

	await test.step('Check that the filter works', async () => {
		await dataSetFragmentPage.filterResumeButton.waitFor({
			state: 'visible',
		});

		await expect(
			dataSetFragmentPage.page.getByRole('button', {
				name: `${filterLabel}: integer, boolean`,
			})
		).toBeVisible();

		await expect(
			dataSetFragmentPage.table.bodyRows
				.filter({
					has: dataSetFragmentPage.page
						.getByText('boolean', {exact: true})
						.first(),
				})
				.locator('td')
				.nth(1)
		).toHaveText(['boolean']);

		await expect(
			dataSetFragmentPage.page.getByText('Showing 1 to 2 of 2 entries.')
		).toBeVisible();
	});

	await test.step('Can reset applied filters', async () => {
		await dataSetFragmentPage.removeFilterButton.click();
	});

	await test.step('Check initial items in the Frontend Data Set', async () => {
		await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

		await expect(
			dataSetFragmentPage.paginationResults.getByText(
				'Showing 1 to 3 of 3 entries.'
			)
		).toBeVisible();
	});
});

test(
	'Selection filter of type "API REST Application" with a composed field name is displayed in the fragment',
	{tag: '@LPD-25905'},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout}) => {
		const filterLabel = getRandomString();
		const customDataSetLabel = getRandomString();
		const customDataSetERC = getRandomString();
		dataSetERCs.push(customDataSetERC);

		await test.step('Create custom data set of Data Sets', async () => {
			await dataSetManagerApiHelpers.createDataSet({
				erc: customDataSetERC,
				keywords: ['bar', 'eggs'],
				label: customDataSetLabel,
				restEndpoint: '/',
				restSchema: 'DataSet',
			});
		});

		await test.step('Add some card sections', async () => {
			await dataSetManagerApiHelpers.createDataSetCardsSection({
				dataSetERC: customDataSetERC,
				fieldName: 'label',
				name: 'title',
			});
		});

		await test.step('Create a new "API Rest Application" selection filter for card fields', async () => {
			await dataSetManagerApiHelpers.createDataSetSelectionFilter({
				dataSetERC: customDataSetERC,
				fieldName: 'dataSetToDataSetCardsSections[]fieldName',
				itemKey: 'fieldName',
				itemLabel: 'fieldName',
				label_i18n: {en_US: filterLabel},
				multiple: true,
				source: `/o${API_ENDPOINT_PATH}/by-external-reference-code/${customDataSetERC}/dataSetToDataSetCardsSections`,
				sourceType: 'API_REST_APPLICATION',
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel: customDataSetLabel,
				layout,
			});
		});

		await test.step('Check current items in the Frontend Data Set', async () => {
			await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

			await expect(
				dataSetFragmentPage.paginationResults.getByText(
					'Showing 1 to 2 of 2 entries.'
				)
			).toBeVisible();
		});

		await test.step('Select filter', async () => {
			await dataSetFragmentPage.selectFilter(filterLabel);
		});

		await test.step('Configure and apply filter', async () => {
			await expect(
				dataSetFragmentPage.dropdownMenu.getByRole('checkbox', {
					name: 'label',
				})
			).toBeVisible();

			await dataSetFragmentPage.dropdownMenu
				.getByRole('checkbox', {name: 'label'})
				.check();
			await dataSetFragmentPage.dropdownMenu
				.getByRole('button', {name: 'Add filter'})
				.click();
		});

		await test.step('Assert that the filter is hidden', async () => {
			await expect(
				dataSetFragmentPage.filterConfirmButton
			).not.toBeVisible();
		});

		await test.step('Check that the filter works', async () => {
			await dataSetFragmentPage.filterResumeButton.waitFor({
				state: 'visible',
			});

			await expect(
				dataSetFragmentPage.page.getByRole('button', {
					name: `${filterLabel}: label`,
				})
			).toBeVisible();

			await dataSetFragmentPage.page.locator('.card').first().waitFor();

			const firstCard = dataSetFragmentPage.page.locator('.card').first();

			await expect(firstCard.locator('.card-title')).toContainText(
				customDataSetLabel
			);

			await expect(
				dataSetFragmentPage.page.getByText(
					'Showing 1 to 1 of 1 entries.'
				)
			).toBeVisible();
		});
	}
);

test(
	'x-filterable: Selection filter of type "Object Picklist" can filter integer field type',
	{tag: '@LPD-56706'},
	async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
		picklistApiHelpers,
	}) => {
		const filterLabel = getRandomString();
		let selectionFilter;

		await test.step('Add fields, so FDS has something to show', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'renderer',
				label_i18n: {en_US: 'Renderer'},
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'sortable',
				label_i18n: {en_US: 'Sortable'},
				renderer: 'boolean',
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'status.label',
				label_i18n: {en_US: 'Status'},
			});
		});

		await test.step('Create a new selection filter with preselected values and include mode', async () => {
			const picklist =
				await picklistApiHelpers.getPicklist(integerPicklistERC);

			selectionFilter =
				await dataSetManagerApiHelpers.createDataSetSelectionFilter({
					dataSetERC,
					entityFieldType: 'integer',
					fieldName: 'dataSetToDataSetTableSections/status',
					include: true,
					label_i18n: {en_US: filterLabel},
					multiple: false,
					preselectedValues: JSON.stringify([
						{
							label: getRandomString(),
							value: picklistIntegerOptionActive.externalReferenceCode,
						},
					]),
					source: picklist.externalReferenceCode,
					sourceType: 'OBJECT_PICKLIST',
				});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check current filter is applied in the Frontend Data Set and return no results', async () => {
			await expect(dataSetFragmentPage.filterResumeButton).toBeVisible();
			await expect(dataSetFragmentPage.filterResumeButton).toContainText(
				`${filterLabel}: ${picklistIntegerOptionLabelActive}`
			);
			await expect(dataSetFragmentPage.emptyStateTitle).toBeVisible();
		});

		await test.step('Update filter to apply Inactive key', async () => {
			await dataSetManagerApiHelpers.updateDataSetSelectionFilter({
				dataSetERC,
				preselectedValues: JSON.stringify([
					{
						label: getRandomString(),
						value: picklistIntegerOptionInactive.externalReferenceCode,
					},
				]),
				selectionFilterERC: selectionFilter.externalReferenceCode,
			});
		});

		await test.step('Check current items in the Frontend Data Set', async () => {
			await page.reload();
			await expect(dataSetFragmentPage.filterResumeButton).toBeVisible();
			await expect(dataSetFragmentPage.filterResumeButton).toContainText(
				`${filterLabel}: ${picklistIntegerOptionLabelInactive}`
			);
			await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

			await expect(
				dataSetFragmentPage.table.bodyRows.first().locator('td')
			).toHaveText(['default', 'No', 'approved', '']);

			await expect(
				dataSetFragmentPage.paginationResults.getByText(
					'Showing 1 to 3 of 3 entries.'
				)
			).toBeVisible();
		});

		await test.step('Can remove the current filter', async () => {
			await dataSetFragmentPage.page
				.getByRole('button', {exact: true, name: 'Remove Filter'})
				.click();
			await expect(
				dataSetFragmentPage.filterResumeButton
			).not.toBeVisible();

			await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

			await expect(
				dataSetFragmentPage.paginationResults.getByText(
					'Showing 1 to 3 of 3 entries.'
				)
			).toBeVisible();
		});
	}
);

test(
	'x-filterable: Selection filter of type "Object Picklist" can filter boolean field type',
	{tag: '@LPD-56706'},
	async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
		picklistApiHelpers,
	}) => {
		const filterLabel = getRandomString();
		let selectionFilter;

		await test.step('Add fields, so FDS has something to show', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'renderer',
				label_i18n: {en_US: 'Renderer'},
			});

			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'sortable',
				label_i18n: {en_US: 'Sortable'},
				renderer: 'boolean',
			});
		});

		await test.step('Create a new selection filter with preselected values and include mode', async () => {
			const picklist =
				await picklistApiHelpers.getPicklist(booleanPicklistERC);

			selectionFilter =
				await dataSetManagerApiHelpers.createDataSetSelectionFilter({
					dataSetERC,
					entityFieldType: 'boolean',
					fieldName: 'sortable',
					include: true,
					label_i18n: {en_US: filterLabel},
					multiple: false,
					preselectedValues: JSON.stringify([
						{
							label: getRandomString(),
							value: picklistBooleanOptionTrue.externalReferenceCode,
						},
					]),
					source: picklist.externalReferenceCode,
					sourceType: 'OBJECT_PICKLIST',
				});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check current filter is applied in the Frontend Data Set and return no results', async () => {
			await expect(dataSetFragmentPage.filterResumeButton).toBeVisible();
			await expect(dataSetFragmentPage.filterResumeButton).toContainText(
				`${filterLabel}: ${picklistBooleanOptionLabelTrue}`
			);
		});

		await test.step('Update filter to apply false key', async () => {
			await dataSetManagerApiHelpers.updateDataSetSelectionFilter({
				dataSetERC,
				preselectedValues: JSON.stringify([
					{
						label: getRandomString(),
						value: picklistBooleanOptionFalse.externalReferenceCode,
					},
				]),
				selectionFilterERC: selectionFilter.externalReferenceCode,
			});
		});

		await test.step('Check current items in the Frontend Data Set', async () => {
			await page.reload();
			await expect(dataSetFragmentPage.filterResumeButton).toBeVisible();
			await expect(dataSetFragmentPage.filterResumeButton).toContainText(
				`${filterLabel}: ${picklistBooleanOptionLabelFalse}`
			);
			await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

			await expect(
				dataSetFragmentPage.table.bodyRows.first().locator('td')
			).toHaveText(['default', 'No', '']);

			await expect(
				dataSetFragmentPage.paginationResults.getByText(
					'Showing 1 to 2 of 2 entries.'
				)
			).toBeVisible();
		});

		await test.step('Can remove the current filter', async () => {
			await dataSetFragmentPage.page
				.getByRole('button', {exact: true, name: 'Remove Filter'})
				.click();
			await expect(
				dataSetFragmentPage.filterResumeButton
			).not.toBeVisible();

			await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

			await expect(
				dataSetFragmentPage.paginationResults.getByText(
					'Showing 1 to 2 of 2 entries.'
				)
			).toBeVisible();
		});
	}
);

test(
	'x-filterable: Selection filter of type "Object Picklist" can filter a collection type',
	{tag: '@LPD-56706'},
	async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
		picklistApiHelpers,
	}) => {
		const filterLabel = getRandomString();
		let selectionFilter;

		const customDataSetLabel = getRandomString();
		const customDataSetERC = getRandomString();
		dataSetERCs.push(customDataSetERC);

		await test.step('Create custom data set of Data Sets', async () => {
			await dataSetManagerApiHelpers.createDataSet({
				erc: customDataSetERC,
				keywords: ['bar', 'eggs', 'more eggs'],
				label: customDataSetLabel,
				restEndpoint: '/',
				restSchema: 'DataSet',
			});
		});

		await test.step('Add fields, so FDS has something to show', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC: customDataSetERC,
				fieldName: 'keywords',
				label_i18n: {en_US: 'Keywords'},
				renderer: 'default',
				type: 'array',
			});
		});

		await test.step('Create a new selection filter with preselected values and include mode', async () => {
			const picklist = await picklistApiHelpers.getPicklist(
				collectionPicklistERC
			);

			selectionFilter =
				await dataSetManagerApiHelpers.createDataSetSelectionFilter({
					dataSetERC: customDataSetERC,
					entityFieldType: 'collection-string',
					fieldName: 'keywords',
					include: true,
					label_i18n: {en_US: filterLabel},
					multiple: true,
					preselectedValues: JSON.stringify([
						{
							label: getRandomString(),
							value: picklistCollectionOptionAnyKey.externalReferenceCode,
						},
					]),
					source: picklist.externalReferenceCode,
					sourceType: 'OBJECT_PICKLIST',
				});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel: customDataSetLabel,
				layout,
			});
		});

		await test.step('Check current filter is applied in the Frontend Data Set and return no results', async () => {
			await expect(dataSetFragmentPage.filterResumeButton).toBeVisible();
			await expect(dataSetFragmentPage.filterResumeButton).toContainText(
				`${filterLabel}: ${picklistCollectionOptionLabelAnyKey}`
			);
		});

		await test.step('Update filter to apply to eggs option', async () => {
			await dataSetManagerApiHelpers.updateDataSetSelectionFilter({
				dataSetERC: customDataSetERC,
				preselectedValues: JSON.stringify([
					{
						label: getRandomString(),
						value: picklistCollectionOptionEggs.externalReferenceCode,
					},
				]),
				selectionFilterERC: selectionFilter.externalReferenceCode,
			});
		});

		await test.step('Check current items in the Frontend Data Set', async () => {
			await page.reload();
			await expect(dataSetFragmentPage.filterResumeButton).toBeVisible();
			await expect(dataSetFragmentPage.filterResumeButton).toContainText(
				`${filterLabel}: ${picklistCollectionOptionLabelEggs}`
			);
			await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

			await expect(
				dataSetFragmentPage.table.bodyRows.first().locator('td')
			).toHaveText(['bar, eggs, more eggs', '']);

			await expect(
				dataSetFragmentPage.paginationResults.getByText(
					'Showing 1 to 1 of 1 entries.'
				)
			).toBeVisible();
		});

		await test.step('Can remove the current filter', async () => {
			await dataSetFragmentPage.page
				.getByRole('button', {exact: true, name: 'Remove Filter'})
				.click();
			await expect(
				dataSetFragmentPage.filterResumeButton
			).not.toBeVisible();

			await dataSetFragmentPage.paginationResults.scrollIntoViewIfNeeded();

			await expect(
				dataSetFragmentPage.paginationResults.getByText(
					'Showing 1 to 2 of 2 entries.'
				)
			).toBeVisible();
		});
	}
);

test(
	'Selection filter of type "Object Picklist" shows a "+N" badge when more items are preselected than the pill preview can display',
	{tag: '@LPD-98305'},
	async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		picklistApiHelpers,
	}) => {
		const filterLabel = getRandomString();
		const badgePicklistName = getRandomString();
		const badgePicklistOptionKeys = ['a', 'b', 'c', 'd'];

		let badgePicklistERC: string;
		let badgePicklistOptions: any[];

		await test.step('Add a field, so FDS has something to show', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'renderer',
				label_i18n: {en_US: 'Renderer'},
			});
		});

		await test.step('Create a picklist with more options than the pill preview can display', async () => {
			const badgePicklist = await picklistApiHelpers.createPicklist({
				name: badgePicklistName,
			});

			badgePicklistERC = badgePicklist.externalReferenceCode;

			badgePicklistOptions = await Promise.all(
				badgePicklistOptionKeys.map((key) =>
					picklistApiHelpers.editPicklist({
						externalReferenceCode: badgePicklistERC,
						key,
						value: key,
					})
				)
			);
		});

		await test.step('Create a selection filter preselecting all four values via the API', async () => {
			await dataSetManagerApiHelpers.createDataSetSelectionFilter({
				dataSetERC,
				fieldName: 'renderer',
				label_i18n: {en_US: filterLabel},
				multiple: true,
				preselectedValues: JSON.stringify(
					badgePicklistOptions.map((option, index) => ({
						label: badgePicklistOptionKeys[index],
						value: option.externalReferenceCode,
					}))
				),
				source: badgePicklistERC,
				sourceType: 'OBJECT_PICKLIST',
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Shows a "+1" badge for the one preselected value hidden beyond the three-item preview', async () => {
			await expect(dataSetFragmentPage.filterResumeButton).toBeVisible();
			await expect(dataSetFragmentPage.filterResumeBadge).toHaveText(
				'+1'
			);
		});

		await picklistApiHelpers.deletePicklist(badgePicklistERC);
	}
);
