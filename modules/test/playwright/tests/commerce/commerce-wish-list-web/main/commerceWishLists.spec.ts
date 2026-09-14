/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

test(
	'Verify wishlist visibility rules',
	{tag: '@LPD-34906'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceWishListPage,
		page,
	}) => {
		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Account1',
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			['test@liferay.com']
		);

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Account2',
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account2.id,
			['test@liferay.com']
		);

		const accountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account2.externalReferenceCode,
			accountGroup.externalReferenceCode
		);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: 'WishListsSite',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_wish_list_web_internal_portlet_CommerceWishListContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'WishListsSite',
			});

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product1'},
				productAccountGroupFilter: true,
				productAccountGroups: [
					{accountGroupId: accountGroup.id, id: 0},
				],
			});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'Product2'},
		});

		await apiHelpers.headlessCommerceDeliveryCatalog.postWishList(
			{
				name: getRandomString(),
				wishListItems: [
					{
						productId: product1.productId,
						skuId: product1.skus[0].id,
					},
				],
			},
			channel.id,
			account2.id
		);

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(await commerceWishListPage.addToCartButton).toBeDisabled();
	}
);

test(
	'Create, rename and delete a wish list from the storefront',
	{tag: '@LPD-105666'},
	async ({apiHelpers, commerceWishListPage, page}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_wish_list_web_internal_portlet_MyCommerceWishListsPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_wish_list_web_internal_portlet_CommerceWishListContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await commerceWishListPage.addWishListButton.click();

		await expect(
			commerceWishListPage.wishListLink('New Wish List')
		).toBeVisible();
		await expect(
			commerceWishListPage.wishListHeading('New Wish List')
		).toBeVisible();

		const wishListName = getRandomString();

		await commerceWishListPage.editWishListButton.click();

		await commerceWishListPage.nameInput.fill(wishListName);

		await commerceWishListPage.saveButton.click();

		await expect(
			commerceWishListPage.wishListLink(wishListName)
		).toBeVisible();
		await expect(
			commerceWishListPage.wishListHeading(wishListName)
		).toBeVisible();

		await commerceWishListPage.deleteWishListButton.click();

		await expect(
			commerceWishListPage.wishListLink(wishListName)
		).toHaveCount(0);
	}
);
