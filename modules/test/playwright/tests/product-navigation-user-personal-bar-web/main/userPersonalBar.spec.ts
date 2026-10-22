/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {userPersonalBarPagesTest} from '../../../fixtures/userPersonalBarPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	userPersonalBarPagesTest
);

const featureFlagTest = mergeTests(
	featureFlagsTest({'LPD-57922': {enabled: true}}),
	loginTest(),
	userPersonalBarPagesTest
);

test(
	'Notification badge configuration enables and disables notification badge in personal menu',
	{tag: ['@LPD-15423']},
	async ({apiHelpers, page, userPersonalBarPage}) => {
		test.setTimeout(90000);

		await userPersonalBarPage.goToProcessBuilderConfigurationTab();
		await userPersonalBarPage.enableSingleApproverWorkflowProduct();
		await userPersonalBarPage.disableNotificationBadgeInPersonalMenu();

		await page.goto('/');

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const patchedProduct =
			await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
				String(product.productId)
			);

		expect(patchedProduct.productId).toBe(product.productId);

		await expect(userPersonalBarPage.notificationBadge).not.toBeVisible();

		await userPersonalBarPage.enableNotificationBadgeInPersonalMenu();

		await page.goto('/');

		await expect(userPersonalBarPage.notificationBadge).toBeVisible();

		await userPersonalBarPage.disableSingleApproverWorkflowProduct();
	}
);

featureFlagTest(
	'Color scheme button is present in the personal menu when the feature flag is enabled and user is Site Admin',
	{tag: ['@LPD-57922']},
	async ({page, userPersonalBarPage}) => {
		await page.goto('/');

		await userPersonalBarPage.userMenuButton.click();

		await expect(
			page.getByRole('button', {name: 'Switch to Dark Mode'})
		).toBeVisible();
	}
);

test(
	'Personal menu loads its styles from a stylesheet',
	{tag: ['@LPD-103065']},
	async ({page}) => {
		await page.goto('/');

		await expect(page.locator('.user-avatar-link')).toBeVisible();
		await expect(page.locator('.control-menu-nav').first()).toBeAttached();

		await expect(page.locator('.user-avatar-link style')).toHaveCount(0);
		await expect(page.locator('.control-menu-nav style')).toHaveCount(0);

		await expect(
			page
				.locator('link[href*="product-navigation-taglib/css/main"]')
				.first()
		).toBeAttached();
	}
);
