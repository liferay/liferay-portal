/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../../fixtures/usersAndOrganizationsPagesTest';
import getRandomString from '../../../../utils/getRandomString';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

test(
	'Create new order button in minium theme is disabled if max open account orders number is reached',
	{tag: ['@COMMERCE-6215', '@LPD-56172']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceThemeMiniumCatalogPage,
		page,
	}) => {
		const {channel, site} = await miniumSetUp(apiHelpers);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			channelId: channel.id,
			name: 'order1',
			orderStatus: '2',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.maxOpenOrderAccountInput.fill(
			'1'
		);
		await commerceAdminChannelDetailsPage.saveButton.click();

		await page.goto(`/web/${site.name}`);

		await commerceThemeMiniumCatalogPage.accountSelectorButton.click();

		await expect(
			await commerceThemeMiniumCatalogPage.createNewOrderButton
		).toBeDisabled();
	}
);

test(
	'Correct current order is fetched when creating an order with an impersonated user and then impersonating a second user',
	{tag: ['@LPP-59365', '@LPD-59082']},
	async ({apiHelpers, commerceThemeMiniumCatalogPage, page}) => {
		const {site} = await miniumSetUp(apiHelpers);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'Buyer ' + getRandomString(),
			rolePermissions: [
				{
					actionIds: ['MANAGE_ADDRESSES', 'VIEW_ADDRESSES'],
					primaryKey: '0',
					resourceName: 'com.liferay.account.model.AccountEntry',
					scope: 3,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.model.CommerceOrderType',
					scope: 1,
				},
				{
					actionIds: [
						'ADD_COMMERCE_ORDER',
						'CHECKOUT_OPEN_COMMERCE_ORDERS',
						'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
						'MANAGE_COMMERCE_ORDER_PAYMENT_METHODS',
						'MANAGE_COMMERCE_ORDER_PAYMENT_TERMS',
						'MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS',
						'VIEW_BILLING_ADDRESS',
						'VIEW_COMMERCE_ORDERS',
						'VIEW_OPEN_COMMERCE_ORDERS',
					],
					primaryKey: '0',
					resourceName: 'com.liferay.commerce.order',
					scope: 3,
				},
			],
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user1.id
		);

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user2.id
		);

		const siteMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			user1.id
		);

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			user2.id
		);

		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[user1.emailAddress]
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account2.id,
			[user2.emailAddress]
		);

		const doAsUserIdURL1 = `/web/${site.name}?&doAsUserId=${user1.id}`;
		await page.goto(doAsUserIdURL1);

		await commerceThemeMiniumCatalogPage.firstCardItemAddToCartButton.click();

		const accountNameField = page.getByText('There is no order selected.');

		await page.reload();

		await expect(accountNameField).not.toBeVisible();

		const doAsUserIdURL2 = `/web/${site.name}?&doAsUserId=${user2.id}`;
		await page.goto(doAsUserIdURL2);

		await expect(accountNameField).toBeVisible();

		await commerceThemeMiniumCatalogPage.firstCardItemAddToCartButton.click();

		await page.reload();

		await expect(accountNameField).not.toBeVisible();
	}
);
