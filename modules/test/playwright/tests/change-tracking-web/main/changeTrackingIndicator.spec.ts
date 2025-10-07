/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import {featureFlagPagesTest} from '../../feature-flag-web/main/fixtures/featureFlagPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	featureFlagPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test('LPD-31710 Publication bar disappears when trying to select a publication', async ({
	apiHelpers,
	changeTrackingPage,
	page,
}) => {
	const user = await changeTrackingPage.addUserWithPublicationsUserRole();

	await performLogout(page);

	await performLoginViaApi({page, screenName: user.alternateName});

	const ctCollection =
		await apiHelpers.headlessChangeTracking.createCTCollection(
			getRandomString()
		);

	await performLogout(page);

	await performLoginViaApi({page, screenName: 'test'});

	await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));

	const changeTrackingIndicatorButton = page.locator(
		'.change-tracking-indicator-button'
	);

	await changeTrackingIndicatorButton.click();

	const selectPublicationMenuItem = page.getByRole('menuitem', {
		name: 'Select a Publication',
	});

	await expect(selectPublicationMenuItem).toBeVisible();

	await selectPublicationMenuItem.click();

	await expect(
		page.locator('li').filter({hasText: ctCollection.body.name})
	).toBeVisible();

	await apiHelpers.headlessChangeTracking.deleteCTCollection(
		ctCollection.body.id
	);
});

test('LPD-36221 Publications bar breaks when enabling the FF for LPD-20131', async ({
	apiHelpers,
	featureFlagsInstanceSettingsPage,
	page,
}) => {
	const ctCollection =
		await apiHelpers.headlessChangeTracking.createCTCollection(
			getRandomString()
		);

	await apiHelpers.headlessChangeTracking.checkoutCTCollection(
		ctCollection.body.id
	);

	await featureFlagsInstanceSettingsPage.goto('Release');

	await featureFlagsInstanceSettingsPage.search('LPD-20131');

	await featureFlagsInstanceSettingsPage.updateFeatureFlag('LPD-20131', true);

	const changeTrackingIndicatorButton = page.locator(
		'.change-tracking-indicator-button'
	);

	await changeTrackingIndicatorButton.click();

	const selectPublicationMenuItem = page.getByRole('menuitem', {
		name: 'Select a Publication',
	});

	await expect(selectPublicationMenuItem).toBeVisible();

	await selectPublicationMenuItem.click();

	await expect(
		page.locator('li').filter({hasText: ctCollection.body.name})
	).toBeVisible();

	await featureFlagsInstanceSettingsPage.goto('Release');

	await featureFlagsInstanceSettingsPage.search('LPD-20131');

	await featureFlagsInstanceSettingsPage.updateFeatureFlag(
		'LPD-20131',
		false
	);

	await apiHelpers.headlessChangeTracking.deleteCTCollection(
		ctCollection.body.id
	);
});

test('LPD-44274 Assert cursor type is pointer when hover over a not selected publication', async ({
	apiHelpers,
	ctCollection,
	page,
}) => {
	const ctCollection2 =
		await apiHelpers.headlessChangeTracking.createCTCollection(
			getRandomString()
		);

	await performLogout(page);
	await performLoginViaApi({page, screenName: 'test'});

	await apiHelpers.headlessChangeTracking.checkoutCTCollection(
		ctCollection2.body.id
	);

	await page.reload();

	await page.locator('.change-tracking-indicator-button').click();

	const selectPublicationMenuItem = page.getByRole('menuitem', {
		name: 'Select a Publication',
	});

	await expect(selectPublicationMenuItem).toBeVisible();

	await selectPublicationMenuItem.click();

	const cursorType = await page
		.getByText(ctCollection.body.name)
		.evaluate((element) =>
			window.getComputedStyle(element).getPropertyValue('cursor')
		);

	await expect(cursorType).toEqual('pointer');

	await apiHelpers.headlessChangeTracking.deleteCTCollection(
		ctCollection2.body.id
	);
});
