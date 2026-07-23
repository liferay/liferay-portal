/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';

const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	cmsPagesTest,
	loginTest()
);

test(
	'Warning popover is displayed in CMS site in a Publication',
	{tag: '@LPD-64066'},
	async ({assetsPage, changeTrackingPage, ctCollection, homePage, page}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		await homePage.goto();

		await expect(page.getByLabel('CMS Control Menu')).toBeVisible();

		const changeTrackingIndicator = page
			.locator('.change-tracking-indicator-title')
			.filter({
				hasText: ctCollection.body.name + ' (Editing in Production)',
			});

		await expect(changeTrackingIndicator).toBeVisible();

		const popoverTitle = page.locator('.popover-header').filter({
			hasText: 'Editing in Production',
		});

		await expect(popoverTitle).toBeVisible();

		await expect(
			page.getByText(
				'The CMS application is not fully supported by Publications yet. Changes are saved directly to production.',
				{exact: true}
			)
		).toBeVisible();

		await assetsPage.gotoAll();

		await expect(changeTrackingIndicator).toBeVisible();

		await expect(popoverTitle).not.toBeVisible();
	}
);

test(
	'Assert publication bar dropdown is disabled for CMS site',
	{tag: '@LPD-64065'},
	async ({changeTrackingPage, ctCollection, homePage, page}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		await homePage.goto();

		const changeTrackingIndicatorButtonProduction = page.locator(
			'.change-tracking-indicator-button'
		);

		await expect(changeTrackingIndicatorButtonProduction).toBeVisible();

		await changeTrackingIndicatorButtonProduction.click();

		const selectPublicationMenuItem = page.getByRole('menuitem', {
			name: 'Select a Publication',
		});

		await expect(selectPublicationMenuItem).not.toBeVisible();

		await page.goto('/');

		const changeTrackingIndicatorButtonPublication = page
			.locator('.change-tracking-indicator-button')
			.filter({hasText: ctCollection.body.name});

		await expect(changeTrackingIndicatorButtonPublication).toBeVisible();

		await changeTrackingIndicatorButtonPublication.click();

		await expect(selectPublicationMenuItem).toBeVisible();
	}
);
