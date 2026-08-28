/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {waitForFDS} from '../../../../../utils/waitFor';
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

test(
	'Behavior of a selection filter that hides the "Exclude" toggle',
	{
		tag: ['@LPD-103049'],
	},
	async ({fdsSamplePage, page, site}) => {
		let blueCells: Locator;
		let colorFilterSummaryButton: Locator;
		let greenCells: Locator;
		let redCells: Locator;

		await test.step('Go to Hidden Exclude Toggle sample tab', async () => {
			await fdsSamplePage.setupFDSSampleWidget({site});

			await fdsSamplePage.selectTab('Hidden Exclude Toggle');

			await waitForFDS({page});

			blueCells = fdsSamplePage.table.container.getByRole('cell', {
				name: 'Blue',
			});

			colorFilterSummaryButton =
				fdsSamplePage.getFilterSummaryButton('Color');

			greenCells = fdsSamplePage.table.container.getByRole('cell', {
				name: 'Green',
			});

			redCells = fdsSamplePage.table.container.getByRole('cell', {
				name: 'Red',
			});
		});

		await test.step('Check the preloaded "Exclude" value excludes the "Blue" color', async () => {
			await expect(colorFilterSummaryButton).toBeVisible();

			await expect(blueCells).toHaveCount(0);
		});

		await test.step('Open the "Color" filter and check the "Exclude" toggle is not displayed', async () => {
			await colorFilterSummaryButton.click();

			await expect(
				fdsSamplePage.getFilterItemCheckbox('Blue')
			).toBeChecked();

			await expect(fdsSamplePage.filterExcludeToggle).toHaveCount(0);
		});

		await test.step('Select the "Green" color and check both colors are excluded', async () => {
			await fdsSamplePage.getFilterItemCheckbox('Green').check();

			await fdsSamplePage.filterShowResultsOrAddButton.click();

			await expect(blueCells).toHaveCount(0);
			await expect(greenCells).toHaveCount(0);
		});

		await test.step('Clear the selection and check every color is displayed', async () => {
			await colorFilterSummaryButton.click();

			await fdsSamplePage.getFilterItemCheckbox('Blue').uncheck();
			await fdsSamplePage.getFilterItemCheckbox('Green').uncheck();

			await fdsSamplePage.filterDeleteButton.click();

			await expect(colorFilterSummaryButton).toHaveCount(0);

			await expect(blueCells).not.toHaveCount(0);
		});

		await test.step('Select the "Red" color and check the configured "Exclude" value still applies', async () => {
			await fdsSamplePage.managementToolbar.filterButton.click();

			await fdsSamplePage.filterMenu
				.getByRole('menuitem', {name: 'Color'})
				.click();

			await expect(fdsSamplePage.filterExcludeToggle).toHaveCount(0);

			await fdsSamplePage.getFilterItemCheckbox('Red').check();

			await fdsSamplePage.filterShowResultsOrAddButton.click();

			await expect(redCells).toHaveCount(0);
		});

		await test.step('Remove the filter chip and check the preloaded "Exclude" value comes back', async () => {
			await fdsSamplePage.getFilterRemoveButton('Color').click();

			await expect(colorFilterSummaryButton).toHaveCount(0);

			await fdsSamplePage.managementToolbar.filterButton.click();

			await expect(
				fdsSamplePage.getFilterItemCheckbox('Green')
			).toBeVisible();

			await expect(fdsSamplePage.filterExcludeToggle).toHaveCount(0);

			await fdsSamplePage.getFilterItemCheckbox('Green').check();

			await fdsSamplePage.filterShowResultsOrAddButton.click();

			await expect(greenCells).toHaveCount(0);
			await expect(blueCells).not.toHaveCount(0);
		});
	}
);
