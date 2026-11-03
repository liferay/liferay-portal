/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
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

test(
	'Columns hidden by default in a system data set',
	{
		tag: ['@LPD-107787'],
	},
	async ({fdsSamplePage, page, site}) => {
		let descriptionColumnHeader: Locator;
		let sizeColumnHeader: Locator;
		let statusColumnHeader: Locator;
		let titleColumnHeader: Locator;

		await test.step('Go to Single Selection sample tab', async () => {
			await fdsSamplePage.setupFDSSampleWidget({site});

			await fdsSamplePage.selectTab('Single Selection');

			await waitForFDS({
				page,
				visualizationMode: EFDSVisualizationMode.TABLE,
			});

			const headerCells = fdsSamplePage.table.headerCells;

			descriptionColumnHeader = headerCells.filter({
				hasText: 'Description',
			});
			sizeColumnHeader = headerCells.filter({hasText: 'Size'});
			statusColumnHeader = headerCells.filter({hasText: 'Status'});
			titleColumnHeader = headerCells.filter({hasText: 'Title'});
		});

		await test.step('Check the columns hidden by default are not displayed', async () => {
			await expect(statusColumnHeader).toBeVisible();
			await expect(titleColumnHeader).toBeVisible();

			await expect(descriptionColumnHeader).toHaveCount(0);
			await expect(sizeColumnHeader).toHaveCount(0);
		});

		await test.step('Show the "Description" column from the columns visibility menu', async () => {
			await fdsSamplePage.table.manageColumnsVisibilityButton.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: 'Description'})
				.click();

			await page.keyboard.press('Escape');

			await expect(descriptionColumnHeader).toBeVisible();

			await expect(sizeColumnHeader).toHaveCount(0);
		});
	}
);
