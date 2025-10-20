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
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../../fixtures/usersAndOrganizationsPagesTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

test(
	'Account Selector in Minium theme working as expected',
	{tag: ['@COMMERCE-6215', '@LPD-56172', '@LPD-48266']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		commerceThemeMiniumCatalogPage,
		page,
		pendingOrdersPage,
		site,
	}) => {
		let account;
		let channel;
		let layout;
		let order1;
		let order2Id;

		await test.step('Create a Site and a channel', async () => {
			await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: getRandomString(),
						key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_commerce_order_content_web_internal_portlet_CommerceOpenOrderContentPortlet',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			channel = await apiHelpers.headlessCommerceAdminChannel.postChannel(
				{
					siteGroupId: site.id,
				}
			);

			await commerceAdminChannelsPage.changeCommerceChannelSiteType(
				channel.name,
				'B2B'
			);
		});

		await test.step('Create an Account and a buyer', async () => {
			account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			const user =
				await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
					'demo.unprivileged@liferay.com'
				);

			const rolesResponse =
				await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

			const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
				return role.name === 'Buyer';
			});

			await apiHelpers.headlessAdminUser.assignAccountRoles(
				account.externalReferenceCode,
				accountRoleBuyer[0].id,
				user.emailAddress
			);

			const siteRole =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				user.id
			);
			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				[user.emailAddress]
			);
		});

		await test.step('Create an order via API and login as a Buyer', async () => {
			order1 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
				accountId: account.id,
				channelId: channel.id,
				name: 'order1',
				orderStatus: '2',
			});

			await page.waitForTimeout(2000);

			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: 'demo.unprivileged',
			});
		});

		await test.step('Go to Site and check that the account selector has the order selected', async () => {
			await page.goto(`/web/${site.name}`);

			await page.waitForLoadState();

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorButton.locator(
					'.order-id'
				)
			).toHaveText(order1.id.toString());
		});
		try {
			await test.step('Go to pending orders and add an empty order, then assert that the new order is selected in the account selector', async () => {
				await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

				await commerceLayoutsPage.addOrderButton.click();

				await page.waitForLoadState('networkidle');

				await page.goto(`/web/${site.name}`);

				await expect(
					commerceThemeMiniumCatalogPage.accountSelectorButton.locator(
						'.order-id'
					)
				).not.toHaveText(order1.id.toString());

				order2Id =
					await commerceThemeMiniumCatalogPage.accountSelectorButton
						.locator('.order-id')
						.textContent();
			});

			await test.step('Create an order from the create new order button and verify that the new order is selected', async () => {
				await commerceThemeMiniumCatalogPage.accountSelectorButton.click();
				await commerceThemeMiniumCatalogPage.createNewOrderButton.click();

				await page.waitForLoadState('networkidle');

				await page.goto(`/web/${site.name}`, {
					waitUntil: 'networkidle',
				});

				await expect(
					commerceThemeMiniumCatalogPage.accountSelectorButton.locator(
						'.order-id'
					)
				).not.toHaveText(order2Id);
			});

			await test.step('Change order from orders list in account selector and assert that the new order is selected', async () => {
				await commerceThemeMiniumCatalogPage.accountSelectorButton.click();

				await commerceThemeMiniumCatalogPage.accountSelectorOrdersList
					.getByText(order2Id, {exact: true})
					.click();

				await expect(pendingOrdersPage.checkoutButton).toBeVisible();

				await page.goto(`/web/${site.name}`, {
					waitUntil: 'networkidle',
				});

				await expect(
					commerceThemeMiniumCatalogPage.accountSelectorButton.locator(
						'.order-id'
					)
				).toHaveText(order2Id);
			});

			await test.step('Login as Admin and go to channel and change the max open order account', async () => {
				await performLogout(page);
				await performLoginViaApi({page, screenName: 'test'});

				await commerceAdminChannelsPage.goto();
				await (
					await commerceAdminChannelsPage.channelsTableRowLink(
						channel.name
					)
				).click();

				await commerceAdminChannelDetailsPage.maxOpenOrderAccountInput.fill(
					'3'
				);
				await commerceAdminChannelDetailsPage.saveButton.click();

				await waitForAlert(page);
			});

			await test.step('Go to Minium site and assert that the create new order button is disabled', async () => {
				await page.goto(`/web/${site.name}`);

				await page.waitForLoadState();

				await commerceThemeMiniumCatalogPage.accountSelectorButton.click();

				await expect(
					commerceThemeMiniumCatalogPage.createNewOrderButton
				).toBeDisabled();
			});
		}
		finally {
			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			if (orders && orders.items) {
				for (const order of orders.items) {
					apiHelpers.data.push({id: order.id, type: 'order'});
				}
			}
		}
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
