/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {siteSettingsPagesTest} from '../../../../fixtures/siteSettingsPagesTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {deployProductFragmentsOnDefaultDPT} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-109588': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest,
	siteSettingsPagesTest
);

test(
	'Guests are sent to sign in and see no products or mini cart while guest browsing is off',
	{tag: ['@LPD-109588', '@LPD-110007']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		siteSettingsPage,
	}) => {

		// Add a Classic site with a channel, a catalog and a product

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: getRandomString(),
				siteGroupId: site.id,
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		await deployProductFragmentsOnDefaultDPT(apiHelpers, {
			displayPageTemplatesPage,
			fragmentNames: ['Product Card'],
			pageEditorPage,
			site,
		});

		// Add a page with the mini cart

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const layoutURL = `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		// Check that guests see the mini cart by default

		await performLogout(page);

		await page.goto(layoutURL);

		await expect(commerceMiniCartPage.miniCartButton).toBeVisible();

		await performLoginViaApi({page, screenName: 'test'});

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

		// Check that guests do not see the mini cart

		await performLogout(page);

		await page.goto(layoutURL);

		await expect(commerceMiniCartPage.miniCartButton).toBeHidden();

		// Open the product as a guest

		const productURL = `/web${site.friendlyUrlPath}/p/${product.urls.en_US}`;

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

		await expect(page).toHaveURL(new RegExp(`/p/${product.urls.en_US}`));
		await expect(
			page.getByText(product.name['en_US']).first()
		).toBeVisible();

		// Check that signed in users still see the mini cart

		await page.goto(layoutURL);

		await expect(commerceMiniCartPage.miniCartButton).toBeVisible();
	}
);
