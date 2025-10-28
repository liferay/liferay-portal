/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';

const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	cmsPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-32050': {enabled: true},
	}),
	loginTest()
);

test(
	'Publications bar is not visible in CMS site in Production',
	{tag: '@LPD-64066'},
	async ({homePage, page}) => {
		const changeTrackingIndicator = page.locator(
			'.change-tracking-indicator'
		);

		await expect(changeTrackingIndicator).toBeVisible();

		await homePage.goto();

		await expect(changeTrackingIndicator).not.toBeVisible();

		await page.goto('/');

		await expect(changeTrackingIndicator).toBeVisible();
	}
);

test(
	'Warning popover is displayed in CMS site in a Publication',
	{tag: '@LPD-64066'},
	async ({changeTrackingPage, ctCollection, homePage, page}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		await homePage.goto();

		await expect(page.getByLabel('CMS Control Menu')).toBeVisible();

		await expect(
			page.locator('.change-tracking-indicator-title').filter({
				hasText:
					ctCollection.body.name +
					' (Do Not Make CMS Changes Inside a Publication)',
			})
		).toBeVisible();

		await expect(
			page.getByText('Do Not Make CMS Changes Inside a Publication', {
				exact: true,
			})
		).toBeVisible();

		await expect(
			page.getByText(
				'The CMS application is not fully supported by Publications yet. Click the button below to ensure all your changes are made in production.',
				{exact: true}
			)
		).toBeVisible();

		await expect(
			await page.getByRole('button', {name: 'Work on Production'})
		).toBeVisible();

		await expect(
			page.getByRole('button', {exact: true, name: 'close'})
		).not.toBeVisible();
	}
);

test(
	'Warning popover will not close without switching to Production',
	{tag: '@LPD-64066'},
	async ({changeTrackingPage, ctCollection, homePage, page}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		await homePage.goto();

		await page.locator('.change-tracking-indicator').click();

		await page.locator('.popover-header').click();

		await expect(
			page.getByText('Do Not Make CMS Changes Inside a Publication', {
				exact: true,
			})
		).toBeVisible();

		await page.getByRole('button', {name: 'Work on Production'}).click();

		await expect(
			page.locator('.change-tracking-indicator')
		).not.toBeVisible();
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
