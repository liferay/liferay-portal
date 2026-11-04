/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'Admin product page shows correct account groups for admin and account supplier role',
	{tag: ['@LPD-25206', '@LPD-110419']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsVisibilityPage,
		commerceAdminProductPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Supplier Account ' + getRandomString(),
			type: 'supplier',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['demo.unprivileged@liferay.com']
		);

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const accountSupplierRole = rolesResponse?.items?.filter((role) => {
			return role.name === 'Account Supplier';
		});

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			accountSupplierRole[0].id,
			user.emailAddress
		);

		const accountGroup1 =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup1.id, type: 'accountGroup'});

		const accountGroup2 =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup2.id, type: 'accountGroup'});

		const accountGroup3 =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup3.id, type: 'accountGroup'});

		const accountGroup4 =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup4.id, type: 'accountGroup'});

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account.externalReferenceCode,
			accountGroup1.externalReferenceCode
		);

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account.externalReferenceCode,
			accountGroup3.externalReferenceCode
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				accountId: account.id,
			});

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productAccountGroupFilter: true,
				productAccountGroups: [
					{accountGroupId: accountGroup1.id, id: 0},
					{accountGroupId: accountGroup2.id, id: 0},
				],
			});

		await test.step('Assert that the account group picker of the admin excludes the assigned account groups', async () => {
			await commerceAdminProductPage.gotoProduct(product1.name['en_US']);

			await expect(
				commerceAdminProductDetailsPage.productSkusLink
			).toBeVisible();

			await commerceAdminProductDetailsPage.goToProductVisibility();

			await expect(page.getByText(accountGroup1.name)).toBeVisible();
			await expect(page.getByText(accountGroup2.name)).toBeVisible();

			await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsButton.click();

			await expect(
				commerceAdminProductDetailsVisibilityPage.selectAccountGroupsTitle
			).toBeVisible();
			await expect(
				await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsRow(
					accountGroup3.name
				)
			).toBeVisible();
			await expect(
				await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsRow(
					accountGroup4.name
				)
			).toBeVisible();
			await expect(
				await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsRow(
					accountGroup1.name
				)
			).toBeHidden();
			await expect(
				await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsRow(
					accountGroup2.name
				)
			).toBeHidden();
		});

		await test.step('Assert that the account group picker of the supplier shows only its unassigned account groups', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			await commerceAdminProductPage.gotoProduct(product1.name['en_US']);

			await expect(
				commerceAdminProductDetailsPage.productSkusLink
			).toBeVisible();

			await commerceAdminProductDetailsPage.goToProductVisibility();

			await expect(page.getByText(accountGroup1.name)).toBeVisible();
			await expect(page.getByText(accountGroup2.name)).toBeHidden();

			await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsButton.click();

			await expect(
				commerceAdminProductDetailsVisibilityPage.selectAccountGroupsTitle
			).toBeVisible();
			await expect(
				await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsRow(
					accountGroup3.name
				)
			).toBeVisible();
			await expect(
				await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsRow(
					accountGroup4.name
				)
			).toBeHidden();
		});
	}
);

test(
	'Add and remove channel and account group filters on a product',
	{tag: ['@LPD-87061']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsVisibilityPage,
		commerceAdminProductPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel ' + getRandomString(),
			});

		const accountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: 'Guest ' + getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Simple T-Shirt'},
			});

		await commerceAdminProductPage.gotoProduct(product.name.en_US);

		await commerceAdminProductDetailsPage.goToProductVisibility();

		await test.step('Enable the Channels visibility and add the channel', async () => {
			await commerceAdminProductDetailsVisibilityPage
				.sectionToggle('Channels')
				.click();

			await commerceAdminProductDetailsVisibilityPage.addChannelButton.click();

			await expect(
				commerceAdminProductDetailsVisibilityPage.selectChannelTitle
			).toBeVisible();

			await commerceAdminProductDetailsVisibilityPage
				.selectChannelCheckbox(channel.name)
				.check();

			await commerceAdminProductDetailsVisibilityPage.modalSelectButton.click();

			await expect(
				commerceAdminProductDetailsVisibilityPage.visibilityEntityRow(
					channel.name
				)
			).toBeVisible();
		});

		await test.step('Remove the channel row and disable the Channels visibility', async () => {
			await commerceAdminProductDetailsVisibilityPage
				.deleteEntityLink(channel.name)
				.click();

			await expect(
				commerceAdminProductDetailsVisibilityPage.visibilityEntityRow(
					channel.name
				)
			).toBeHidden();

			await commerceAdminProductDetailsVisibilityPage
				.sectionToggle('Channels')
				.click();

			await expect(
				commerceAdminProductDetailsVisibilityPage.sectionToggleInput(
					'Channels'
				)
			).not.toBeChecked();
		});

		await test.step('Enable the Account Groups visibility and add the account group', async () => {
			await commerceAdminProductDetailsVisibilityPage
				.sectionToggle('Account Groups')
				.click();

			await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsButton.click();

			await expect(
				commerceAdminProductDetailsVisibilityPage.selectAccountGroupsTitle
			).toBeVisible();

			await commerceAdminProductDetailsVisibilityPage
				.selectAccountGroupsCheckbox(accountGroup.name)
				.check();

			await commerceAdminProductDetailsVisibilityPage.modalSelectButton.click();

			await expect(
				commerceAdminProductDetailsVisibilityPage.visibilityEntityRow(
					accountGroup.name
				)
			).toBeVisible();
		});

		await test.step('Remove the account group row and disable the Account Groups visibility', async () => {
			await commerceAdminProductDetailsVisibilityPage
				.deleteEntityLink(accountGroup.name)
				.click();

			await expect(
				commerceAdminProductDetailsVisibilityPage.visibilityEntityRow(
					accountGroup.name
				)
			).toBeHidden();

			await commerceAdminProductDetailsVisibilityPage
				.sectionToggle('Account Groups')
				.click();

			await expect(
				commerceAdminProductDetailsVisibilityPage.sectionToggleInput(
					'Account Groups'
				)
			).not.toBeChecked();
		});
	}
);

test(
	'Empty channels visibility is shown to a user with view permissions on products and catalogs',
	{tag: ['@COMMERCE-10610', '@LPD-87061']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsVisibilityPage,
		commerceAdminProductPage,
		page,
	}) => {
		await page.goto('/');

		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Simple Product ' + getRandomString()},
				productChannelFilter: true,
			});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await test.step('Create a custom role with view permissions on products and catalogs and assign it to the user', async () => {
			const role = await apiHelpers.headlessAdminUser.postRole({
				name: 'Test Role ' + getRandomString(),
				rolePermissions: [
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_commerce_catalog_web_internal_portlet_CommerceCatalogsPortlet',
						scope: 1,
					},
					{
						actionIds: ['VIEW_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName: '90',
						scope: 1,
					},
					{
						actionIds: ['VIEW'],
						primaryKey: companyId,
						resourceName:
							'com.liferay.commerce.product.model.CommerceCatalog',
						scope: 1,
					},
					{
						actionIds: ['VIEW_COMMERCE_CATALOGS'],
						primaryKey: companyId,
						resourceName: 'com.liferay.commerce.catalog',
						scope: 1,
					},
					{
						actionIds: [
							'MANAGE_COMMERCE_PRODUCT_CHANNEL_VISIBILITY',
						],
						primaryKey: companyId,
						resourceName: 'com.liferay.commerce.product',
						scope: 1,
					},
				],
			});

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role.externalReferenceCode,
				user.id
			);
		});

		await test.step('Log in as the user and open the visibility tab of the product', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			await commerceAdminProductPage.gotoProduct(product.name.en_US);

			await commerceAdminProductDetailsPage.goToProductVisibility();
		});

		await test.step('Assert that no channels are displayed', async () => {
			await expect(
				commerceAdminProductDetailsVisibilityPage.channelsEmptyStateMessage
			).toBeVisible();
		});
	}
);
