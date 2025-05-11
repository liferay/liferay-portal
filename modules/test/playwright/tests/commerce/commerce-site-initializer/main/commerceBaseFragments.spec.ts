/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../../fixtures/systemSettingsPageTest';
import {liferayConfig} from '../../../../liferay.config';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {classicCommerceSetUp} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-20379': {enabled: true},
	}),
	loginTest(),
	systemSettingsPageTest
);

test(
	'Commerce Classic Header main fragment is correctly displayed',
	{tag: ['@LPD-23780']},
	async ({apiHelpers, page}) => {
		test.setTimeout(180000);

		const {site} = await classicCommerceSetUp(
			apiHelpers,
			`classic-commerce`
		);

		await page.goto(`/web${site.friendlyUrlPath}`);

		await page.getByRole('link', {exact: true, name: 'Edit'}).click();
		await page.getByLabel('Page Design Options').click();
		await page.getByLabel('Commerce Classic Master').click();
		await page.getByLabel('Publish', {exact: true}).click();

		const commerceHeaderTagFragments = page.locator(
			'#commerce-components-group'
		);

		await expect(commerceHeaderTagFragments).toBeVisible();
		await expect(
			commerceHeaderTagFragments.locator('.account-selector-root')
		).toHaveClass(/mr-2/);
		await expect(
			commerceHeaderTagFragments.locator('.cart-root')
		).toBeVisible();
		await expect(page.locator('header .portlet-search-bar')).toBeVisible();
	}
);

test(
	'Multishipping tab displays correctly when enabled',
	{tag: ['@LPD-35323']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
	}) => {
		test.setTimeout(180000);

		const {catalog, channel, site} = await classicCommerceSetUp(
			apiHelpers,
			getRandomString()
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				skus: [
					{
						cost: 0,
						price: 20,
						published: true,
						purchasable: true,
						sku: 'Sku' + getRandomInt(),
					},
				],
			});

		const sku = product.skus[0];

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						quantity: 1,
						skuId: sku.id,
					},
				],
			},
			channel.id
		);

		const orderDetailsPageURL =
			liferayConfig.environment.baseUrl +
			`/web/${site.name}/order/${cart.id}`;

		await page.goto(orderDetailsPageURL);

		const multishippingTab = page.getByRole('tab', {name: 'Multishipping'});

		await expect(multishippingTab).toHaveCount(0);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.allowMultishippingToggle.setChecked(
			true
		);

		await expect(
			commerceAdminChannelDetailsPage.allowMultishippingToggle
		).toBeChecked();

		await commerceAdminChannelDetailsPage.saveButton.click();

		await expect(
			commerceAdminChannelDetailsPage.allowMultishippingToggle
		).toBeChecked();

		await waitForAlert(page);

		await page.goto(orderDetailsPageURL);

		await expect(multishippingTab).toBeVisible();
	}
);
