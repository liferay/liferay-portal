/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

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

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	await fdsSamplePage.setupFDSSampleWidget({site});

	await fdsSamplePage.selectTab('Advanced');

	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});

	await fdsSamplePage.changeVisualizationMode({
		page,
		visualizationMode: EFDSVisualizationMode.LIST,
	});
});

test(
	'Clicking item title in list view opens the info panel',
	{tag: ['@LPD-82099']},
	async ({fdsSamplePage}) => {
		await test.step('Click on the first item title link', async () => {
			const firstListItemTitle = fdsSamplePage.list.items
				.first()
				.locator('.list-group-title a');

			await expect(firstListItemTitle).toBeVisible();

			await firstListItemTitle.click();
		});

		await test.step('Assert the info panel is visible with item details', async () => {
			await expect(fdsSamplePage.infoPanel).toBeVisible();

			await expect(
				fdsSamplePage.infoPanel.getByText(
					'Content from propsTransformer'
				)
			).toBeVisible();

			await expect(
				fdsSamplePage.infoPanel.getByText(
					'This is a description for sample 1.'
				)
			).toBeVisible();
		});

		await test.step('Click on another item title and assert info panel updates', async () => {
			const secondListItemTitle = fdsSamplePage.list.items
				.nth(1)
				.locator('.list-group-title a');

			await secondListItemTitle.click();

			await expect(fdsSamplePage.infoPanel).toBeVisible();

			await expect(
				fdsSamplePage.infoPanel.getByText(
					'Content from propsTransformer'
				)
			).toBeVisible();

			await expect(
				fdsSamplePage.infoPanel.getByText(
					'This is a description for sample 100.'
				)
			).toBeVisible();
		});
	}
);
