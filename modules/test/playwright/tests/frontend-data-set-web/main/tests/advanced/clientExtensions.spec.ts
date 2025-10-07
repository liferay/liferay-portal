/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../../utils/clickAndExpectToBeVisible';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	await fdsSamplePage.setupFDSSampleWidget({site});

	await fdsSamplePage.selectTab('Advanced');

	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});
});

test(
	'Client extensions are applied',
	{
		tag: ['@LPD-58692'],
	},
	async ({fdsSamplePage, page}) => {
		await test.step('Cell client extension is applied', async () => {
			const firstColorCell = fdsSamplePage.table.container
				.locator('td.cell-color')
				.first();

			await expect(firstColorCell).toContainText('🍏');
		});

		await test.step('Cell client extension has access to data from other cells', async () => {
			await expect(
				fdsSamplePage.table.bodyRows.locator('td.cell-color').nth(1)
			).toContainText('Sample100 is Blue');
		});

		const clientExtensionMenuItem = page.getByRole('menuitem', {
			name: 'Client Extension',
		});

		await test.step('Filter client extension is applied', async () => {
			const filterButton = page
				.locator('.filters-dropdown')
				.getByText('Filter');

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: clientExtensionMenuItem,
				trigger: filterButton,
			});

			const filterInput = page.getByPlaceholder('Search with odata');

			await filterInput.fill("title eq 'Sample97'");

			await expect(filterInput).toHaveValue("title eq 'Sample97'");

			const submitButton = page.getByRole('button', {name: 'Submit'});

			await submitButton.click();

			await expect(
				page.getByText('Sample97', {exact: true})
			).toBeVisible();

			expect(await fdsSamplePage.table.bodyRows.count()).toEqual(1);
		});

		await test.step('Invalid filter client extension results only in error log', async () => {
			const invalidMenuItem = page.getByRole('menuitem', {
				name: 'Invalid',
			});

			await page.locator('.btn-filter-navigation').click();

			await expect(clientExtensionMenuItem).toBeVisible();
			await expect(invalidMenuItem).not.toBeVisible();
		});

		await test.step('Invalid cell client extension falls back to default renderer', async () => {
			await expect(
				fdsSamplePage.table.bodyRows.locator('td.cell-size').first()
			).toContainText('Small');
		});
	}
);
