/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'Can view discontinued replacement SKUs in product details',
	{tag: '@LPD-49015'},
	async ({apiHelpers, page, productDetailsPage}) => {
		test.setTimeout(120000);

		const {site} = await miniumSetUp(apiHelpers);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

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
					primaryKey: await page.evaluate(() => {
						return Liferay.ThemeDisplay.getCompanyId();
					}),
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

		await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
			role.id,
			Number(user.id)
		);

		apiHelpers.data.push({
			id: `${role.id}_${user.id}`,
			type: 'roleUserAccountAssociation',
		});

		await apiHelpers.jsonWebServicesUser.addGroupUsers(site.id, [user.id]);

		const replacementSku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN93015'
			);

		const skuList = [
			'MIN93016A',
			'MIN93016B',
			'MIN93016C',
			'MIN93027',
			'MIN93021',
		];

		for (const skuName of skuList) {
			const sku =
				await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
					skuName
				);

			await apiHelpers.headlessCommerceAdminCatalog.patchSku(sku.id, {
				cost: sku.cost,
				discontinued: true,
				price: sku.price,
				published: true,
				purchasable: sku.purchasable,
				replacementSkuId: replacementSku.id,
				sku: sku.sku,
			});
		}

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/p/abs-sensor`);

		await productDetailsPage.replacementsTab.click();

		for (const skuName of skuList) {
			await expect(
				productDetailsPage.replacementsTableCell(skuName)
			).toBeVisible();
		}
		await expect(
			productDetailsPage.paginationText('Showing 1 to 5 of 5 entries.')
		).toBeVisible();

		await productDetailsPage.replacementsSearchBar.fill('Wear Sensors');
		await productDetailsPage.replacementsSearchButton.click();

		for (const skuName of skuList) {
			if (skuName === 'MIN93027') {
				await expect(
					productDetailsPage.replacementsTableCell(skuName)
				).toBeVisible();
			}
			else {
				await expect(
					productDetailsPage.replacementsTableCell(skuName)
				).not.toBeVisible();
			}
		}
	}
);
