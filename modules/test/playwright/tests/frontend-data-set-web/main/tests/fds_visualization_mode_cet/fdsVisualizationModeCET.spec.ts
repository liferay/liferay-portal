/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	await fdsSamplePage.setupFDSSampleWidget({site});

	await fdsSamplePage.selectTab('Visualization Mode Client Extension');

	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});
});

test(
	'Table renders its client extension cells next to a visualization mode client extension',
	{tag: '@LPD-107931'},
	async ({fdsSamplePage}) => {
		await expect(
			fdsSamplePage.table.container.locator('td.cell-color').first()
		).toContainText('🍏');
	}
);

test(
	'Visualization mode client extension shows the items of the data set',
	{tag: '@LPD-107931'},
	async ({fdsSamplePage, page}) => {
		await fdsSamplePage.visualizationModeSelector.click();

		await page
			.getByRole('option', {
				name: 'Liferay Sample Frontend Data Set Timeline',
			})
			.click();

		const timeline = page.getByRole('list', {name: 'Timeline'});

		await test.step('Draw the items oldest first', async () => {
			await expect(timeline.getByRole('listitem').first()).toContainText(
				'This is a description for sample 1.'
			);
			await expect(timeline.getByRole('checkbox')).toHaveCount(0);
		});

		await test.step('Draw the items newest first', async () => {
			await page
				.getByRole('button', {exact: true, name: 'Order'})
				.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: 'Descending'})
				.click();

			await expect(timeline.getByRole('listitem').first()).toContainText(
				'This is a description for sample 100.'
			);
		});

		await test.step('Draw the items of a search', async () => {
			await fdsSamplePage.search('Sample97');

			await expect(
				timeline.getByText('Sample97', {exact: true})
			).toBeVisible();
			await expect(timeline.getByRole('listitem')).toHaveCount(1);
		});
	}
);
