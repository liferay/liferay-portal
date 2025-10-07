/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

let dataSetERC: string;
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
		await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			label: dataSetLabel,
		});
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

test('Date-time filter is displayed in fragment, and applied to data @LPD-10754', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
}) => {
	const fieldLabel = getRandomString();

	const filterLabel = getRandomString();

	async function assertDataIsFetched() {
		await test.step('Assert that the data entry is fetched', async () => {
			await expect(
				dataSetFragmentPage.page.getByText(fieldLabel).first()
			).toBeVisible();
		});
	}

	await test.step('Create a new date-time filter', async () => {
		await dataSetManagerApiHelpers.createDataSetDateFilter({
			dataSetERC,
			fieldName: DATE_FIELD_NAME,
			from: '2020-01-01',
			label_i18n: {en_US: filterLabel},
			to: '3020-01-02',
			type: 'date-time',
		});
	});

	await test.step('Add a field, so FDS has something to show', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'rendererType',
			label_i18n: {en_US: fieldLabel},
			type: 'string',
		});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	const activeFilterButton = dataSetFragmentPage.page.getByRole('button', {
		name: `${filterLabel}:`,
	});

	await test.step('Assert that preloaded filter values are in UI @LPS-191295', async () => {
		await expect(activeFilterButton).toBeVisible();
	});

	await assertDataIsFetched();

	await test.step('Set an impossible date range', async () => {
		await activeFilterButton.click();

		const toInput = dataSetFragmentPage.page.getByLabel('To', {
			exact: true,
		});

		await expect(toInput).toBeVisible();

		await toInput.click();

		await toInput.fill('2020-01-02');

		const editButton = dataSetFragmentPage.page.getByRole('button', {
			name: 'Show Results',
		});

		await expect(editButton).toBeVisible();

		await editButton.click();
	});

	await test.step('Assert that the data entry is not fetched', async () => {
		await expect(dataSetFragmentPage.emptyStateTitle).toBeVisible();
	});

	await test.step('Remove the filter @LPS-191295', async () => {
		const removeFilterButton =
			dataSetFragmentPage.page.getByLabel('Remove Filter');

		await expect(removeFilterButton).toBeVisible();

		await removeFilterButton.click();
	});

	await assertDataIsFetched();
});

test('Can create Date-time filter without start and end dates', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
}) => {
	const fieldLabel = getRandomString();
	const filterLabel = getRandomString();

	async function assertDataIsFetched() {
		await test.step('Assert that the data entry is fetched', async () => {
			await expect(
				dataSetFragmentPage.page.getByText(fieldLabel).first()
			).toBeVisible();
		});
	}

	await test.step('Create a new date-time filter without start nor end dates', async () => {
		await dataSetManagerApiHelpers.createDataSetDateFilter({
			dataSetERC,
			fieldName: DATE_FIELD_NAME,
			from: '',
			label_i18n: {en_US: filterLabel},
			to: '',
			type: 'date-time',
		});
	});

	await test.step('Add a field, so FDS has something to show', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'renderer',
			label_i18n: {en_US: fieldLabel},
			type: 'string',
		});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	await test.step('Assert that the date filter is in the UI', async () => {
		await expect(dataSetFragmentPage.filterButton).toBeVisible();
		await dataSetFragmentPage.selectFilter(filterLabel);

		const fromInput = dataSetFragmentPage.page.getByLabel('From', {
			exact: true,
		});

		await expect(fromInput).toBeVisible();
		await expect(fromInput).toBeEmpty();

		const toInput = dataSetFragmentPage.page.getByLabel('To', {
			exact: true,
		});

		await expect(toInput).toBeVisible();
		await expect(toInput).toBeEmpty();
	});

	await assertDataIsFetched();
});

test('Filters are displayed in the order stored in the filtersOrder field', async ({
	dataSetFragmentPage,
	dataSetManagerApiHelpers,
	layout,
}) => {
	const fieldLabel = getRandomString();
	const filter1Label = getRandomString();
	const filter2Label = getRandomString();
	const modifiedDateField = 'dateModified';
	const filterIds = [];

	await test.step('Create a couple of new date-time filters without start nor end dates', async () => {
		const filter1 = await dataSetManagerApiHelpers.createDataSetDateFilter({
			dataSetERC,
			fieldName: DATE_FIELD_NAME,
			from: '',
			label_i18n: {en_US: filter1Label},
			to: '',
			type: 'date-time',
		});

		const filter2 = await dataSetManagerApiHelpers.createDataSetDateFilter({
			dataSetERC,
			fieldName: modifiedDateField,
			from: '',
			label_i18n: {en_US: filter2Label},
			to: '',
			type: 'date-time',
		});

		filterIds.push(filter1.id, filter2.id);
	});

	await test.step('Add a field, so FDS has something to show', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'renderer',
			label_i18n: {en_US: fieldLabel},
			type: 'string',
		});
	});

	await test.step('Configure Data Set fragment', async () => {
		await dataSetFragmentPage.configureDataSetFragment({
			dataSetLabel,
			layout,
		});
	});

	const filterDropdownId =
		await dataSetFragmentPage.filterButton.getAttribute('aria-controls');

	await test.step('Assert that the date filters are in the UI', async () => {
		await expect(dataSetFragmentPage.filterButton).toBeVisible();

		await dataSetFragmentPage.filterButton.click();
		await dataSetFragmentPage.page
			.locator(`#${filterDropdownId}`)
			.waitFor({state: 'visible'});
		const filtersDropdown = dataSetFragmentPage.page.locator(
			`#${filterDropdownId}`
		);
		const filterDropdownItems = filtersDropdown.getByRole('menuitem');
		await expect(filterDropdownItems).toHaveCount(2);

		await expect(filterDropdownItems.nth(0)).toHaveText(filter1Label);
		await expect(filterDropdownItems.nth(1)).toHaveText(filter2Label);
	});

	await test.step('Update filters order', async () => {
		await dataSetManagerApiHelpers.updateDataSet({
			erc: dataSetERC,
			filtersOrder: filterIds.reverse().join(),
		});

		await dataSetFragmentPage.page.reload();
	});

	await test.step('Assert that the date filters are shown in the UI in the new order', async () => {
		await expect(dataSetFragmentPage.filterButton).toBeVisible();

		await dataSetFragmentPage.filterButton.click();
		await dataSetFragmentPage.page
			.locator(`#${filterDropdownId}`)
			.waitFor({state: 'visible'});
		const filtersDropdown = dataSetFragmentPage.page.locator(
			`#${filterDropdownId}`
		);
		const filterDropdownItems = filtersDropdown.getByRole('menuitem');
		await expect(filterDropdownItems).toHaveCount(2);

		await expect(filterDropdownItems.nth(0)).toHaveText(filter2Label);
		await expect(filterDropdownItems.nth(1)).toHaveText(filter1Label);
	});
});

test(
	'Only "active" Date filters are displayed the fragment',
	{tag: '@LPD-39965'},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout}) => {
		const fieldLabel = getRandomString();
		const filter1Label = getRandomString();
		const filter2Label = getRandomString();
		const modifiedDateField = 'dateModified';

		await test.step('Create a couple of new date-time filters', async () => {
			await dataSetManagerApiHelpers.createDataSetDateFilter({
				dataSetERC,
				fieldName: DATE_FIELD_NAME,
				from: '',
				label_i18n: {en_US: filter1Label},
				to: '',
				type: 'date-time',
			});

			await dataSetManagerApiHelpers.createDataSetDateFilter({
				active: false,
				dataSetERC,
				fieldName: modifiedDateField,
				from: '',
				label_i18n: {en_US: filter2Label},
				to: '',
				type: 'date-time',
			});
		});

		await test.step('Add a field, so FDS has something to show', async () => {
			await dataSetManagerApiHelpers.createDataSetTableSection({
				dataSetERC,
				fieldName: 'renderer',
				label_i18n: {en_US: fieldLabel},
				type: 'string',
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		const filterDropdownId =
			await dataSetFragmentPage.filterButton.getAttribute(
				'aria-controls'
			);

		await test.step('Assert that only "active" date filters are in the UI', async () => {
			await expect(dataSetFragmentPage.filterButton).toBeVisible();

			await dataSetFragmentPage.filterButton.click();
			await dataSetFragmentPage.page
				.locator(`#${filterDropdownId}`)
				.waitFor({state: 'visible'});
			const filtersDropdown = dataSetFragmentPage.page.locator(
				`#${filterDropdownId}`
			);
			const filterDropdownItems = filtersDropdown.getByRole('menuitem');
			await expect(filterDropdownItems).toHaveCount(1);

			await expect(filterDropdownItems.nth(0)).toHaveText(filter1Label);
		});
	}
);
