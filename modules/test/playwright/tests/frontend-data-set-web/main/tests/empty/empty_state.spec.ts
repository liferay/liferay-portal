/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
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

	await fdsSamplePage.selectTab('Empty');

	await page
		.locator('.fds .data-set-content-wrapper')
		.waitFor({state: 'visible'});
});

test(
	'Check the empty state when there are no results',
	{
		tag: ['@LPD-56880'],
	},
	async ({page}) => {
		await test.step('Check that the text "No Data Sets Created" is displayed', async () => {
			await expect(page.getByText('No Data Sets Created')).toBeVisible();
		});

		await test.step('Check that the text "Start creating one to show your data." is displayed', async () => {
			await expect(
				page.getByText('Start creating one to show your data.')
			).toBeVisible();
		});

		await test.step('Check that the "Create New Item" button has btn-primary class', async () => {
			await expect(
				page.getByRole('button', {name: 'Create New Item'})
			).toHaveClass(/btn-primary/);
		});
	}
);

test(
	'Check that management toolbar is hidden in empty state',
	{
		tag: ['@LPD-56880'],
	},
	async ({fdsSamplePage}) => {
		await test.step('Verify management toolbar is hidden when no items and no active filters/search', async () => {
			await expect(
				fdsSamplePage.managementToolbar.container
			).not.toBeVisible();

			await expect(
				fdsSamplePage.managementToolbar.searchInput
			).not.toBeVisible();

			await expect(
				fdsSamplePage.managementToolbar.container.getByRole('button', {
					name: 'Filter',
				})
			).not.toBeVisible();
		});
	}
);
