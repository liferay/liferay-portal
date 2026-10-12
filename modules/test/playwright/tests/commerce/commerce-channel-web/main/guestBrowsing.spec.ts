/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {siteSettingsPagesTest} from '../../../../fixtures/siteSettingsPagesTest';
import getRandomString from '../../../../utils/getRandomString';
import {performLogout, userData} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-109588': {enabled: true},
	}),
	loginTest(),
	siteSettingsPagesTest
);

test(
	'Guests are sent to sign in and see no products while guest browsing is off',
	{tag: '@LPD-109588'},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
		siteSettingsPage,
	}) => {
		const {catalog, channel, site} = await miniumSetUp(apiHelpers);

		// Add a product to the Minium catalog

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		// Turn guest browsing off in the channel

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await expect(
			commerceAdminChannelDetailsPage.guestBrowsingToggle
		).toBeChecked();

		await commerceAdminChannelDetailsPage.guestBrowsingToggle.setChecked(
			false
		);

		await expect(
			commerceAdminChannelDetailsPage.guestCheckoutToggle
		).toBeDisabled();
		await expect(
			commerceAdminChannelDetailsPage.guestCheckoutToggle
		).not.toBeChecked();

		await commerceAdminChannelDetailsPage.saveButton.click();

		await waitForAlert(page, 'success');

		await page.reload();

		await expect(
			commerceAdminChannelDetailsPage.guestBrowsingToggle
		).not.toBeChecked();

		// Prompt guests to sign in

		await siteSettingsPage.goToSiteSetting(
			'Login',
			'Login',
			site.friendlyUrlPath
		);

		await siteSettingsPage.page
			.getByLabel('Prompt Enabled')
			.setChecked(true);

		await siteSettingsPage.saveButton.click();

		await waitForAlert(page);

		// Open the product as a guest

		await performLogout(page);

		const productURL = `/web/${site.name}/p/${product.name['en_US']}`;

		await page.goto(productURL);

		await expect(page.getByLabel('Email Address')).toBeVisible();

		// Check that the delivery API returns no products to guests

		const productsResponse = await page.request.get(
			`/o/headless-commerce-delivery-catalog/v1.0/channels/${channel.id}/products`
		);

		expect(productsResponse.ok()).toBeTruthy();

		const productsPage = await productsResponse.json();

		expect(productsPage.totalCount).toBe(0);

		// Sign in and return to the product

		await page.getByLabel('Email Address').fill('test@liferay.com');
		await page.getByLabel('Password').fill(userData['test'].password);

		await page.getByRole('button', {name: 'Sign In'}).click();

		await expect(page).toHaveURL(new RegExp(`/p/${product.name['en_US']}`));
		await expect(
			page.getByText(product.name['en_US']).first()
		).toBeVisible();
	}
);
