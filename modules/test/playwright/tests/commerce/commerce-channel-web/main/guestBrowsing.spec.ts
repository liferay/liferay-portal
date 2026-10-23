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
import {searchPageTest} from '../../../../fixtures/searchPageTest';
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
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
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
	searchPageTest,
	siteSettingsPagesTest
);

test(
	'Guests are sent to sign in and see no products or mini cart while guest browsing is off',
	{tag: ['@LPD-109588', '@LPD-110007', '@LPD-111537']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		searchPage,
		siteSettingsPage,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const channelName = getRandomString();

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: channelName,
				siteGroupId: site.id,
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productChannelFilter: true,
				productChannels: [
					{
						channelId: channel.id,
						currencyCode: 'USD',
						name: channelName,
						type: 'site',
					},
				],
			});

		await deployProductFragmentsOnDefaultDPT(apiHelpers, {
			displayPageTemplatesPage,
			fragmentNames: ['Product Card'],
			pageEditorPage,
			site,
		});

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

		const searchLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {searchScope: 'everything'},
					widgetName:
						'com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_portal_search_web_search_results_portlet_SearchResultsPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const searchLayoutURL = `/web${site.friendlyUrlPath}${searchLayout.friendlyUrlPath}`;

		const productURL = `/web${site.friendlyUrlPath}/p/${product.urls.en_US}`;

		await test.step('Guests see the mini cart by default', async () => {
			await performLogout(page);

			await page.goto(layoutURL);

			await expect(commerceMiniCartPage.miniCartButton).toBeVisible();
		});

		await test.step('Guests find the product by default', async () => {
			await page.goto(searchLayoutURL);

			await searchPage.searchBarInputInMainContent.fill(
				product.name.en_US
			);

			await expect(
				searchPage.searchBarSuggestions.getByText(product.name.en_US)
			).toBeVisible();

			await searchPage.searchBarInputInMainContent.press('Enter');

			await expect(
				searchPage.searchResultsItems.getByText(product.name.en_US)
			).toBeVisible();

			await performLoginViaApi({page, screenName: 'test'});
		});

		await test.step('Turn guest browsing off in the channel', async () => {
			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
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
		});

		await test.step('Prompt guests to sign in', async () => {
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
		});

		await test.step('Guests do not see the mini cart', async () => {
			await performLogout(page);

			await page.goto(layoutURL);

			await expect(commerceMiniCartPage.miniCartButton).toBeHidden();
		});

		await test.step('Guests do not find the product', async () => {
			await page.goto(searchLayoutURL);

			const suggestionsResponsePromise = page.waitForResponse(
				'**/v1.0/suggestions**'
			);

			await searchPage.searchBarInputInMainContent.fill(
				product.name.en_US
			);

			const suggestionsResponse = await suggestionsResponsePromise;

			expect(await suggestionsResponse.text()).not.toContain(
				product.name.en_US
			);

			await expect(
				searchPage.searchBarSuggestions.getByText(product.name.en_US)
			).toBeHidden();

			await searchPage.searchBarInputInMainContent.press('Enter');

			await expect(
				searchPage.searchResults.getByText('No results were found.')
			).toBeVisible();
		});

		await test.step('Guests are sent to sign in from the product', async () => {
			await page.goto(productURL);

			await expect(page.getByLabel('Email Address')).toBeVisible();
		});

		await test.step('The delivery API returns no products to guests', async () => {
			const productsResponse = await page.request.get(
				`/o/headless-commerce-delivery-catalog/v1.0/channels/${channel.id}/products`
			);

			expect(productsResponse.ok()).toBeTruthy();

			const productsPage = await productsResponse.json();

			expect(productsPage.totalCount).toBe(0);
		});

		await test.step('Signed in users return to the product', async () => {
			await page.getByLabel('Email Address').fill('test@liferay.com');
			await page.getByLabel('Password').fill(userData['test'].password);

			await page.getByRole('button', {name: 'Sign In'}).click();

			await expect(page).toHaveURL(
				new RegExp(`/p/${product.urls.en_US}`)
			);
			await expect(
				page.getByText(product.name['en_US']).first()
			).toBeVisible();
		});

		await test.step('Signed in users still see the mini cart', async () => {
			await page.goto(layoutURL);

			await expect(commerceMiniCartPage.miniCartButton).toBeVisible();
		});

		await test.step('Signed in users still find the product', async () => {
			await page.goto(searchLayoutURL);

			await searchPage.searchKeywordInMainContent(product.name.en_US);

			await expect(
				searchPage.searchResultsItems.getByText(product.name.en_US)
			).toBeVisible();
		});

		await test.step('Turn guest browsing back on in the channel', async () => {
			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			await commerceAdminChannelDetailsPage.guestBrowsingToggle.setChecked(
				true
			);

			await commerceAdminChannelDetailsPage.saveButton.click();

			await waitForAlert(page, 'success');
		});

		await test.step('Guests find the product again', async () => {
			await performLogout(page);

			await page.goto(searchLayoutURL);

			await searchPage.searchKeywordInMainContent(product.name.en_US);

			await expect(
				searchPage.searchResultsItems.getByText(product.name.en_US)
			).toBeVisible();

			await performLoginViaApi({page, screenName: 'test'});
		});
	}
);
