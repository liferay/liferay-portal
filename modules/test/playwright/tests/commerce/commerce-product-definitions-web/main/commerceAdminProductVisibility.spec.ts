/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../../utils/performLogin';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-25206 Admin product page shows correct account groups for admin and account supplier role', async ({
	apiHelpers,
	commerceAdminProductDetailsPage,
	commerceAdminProductDetailsVisibilityPage,
	commerceAdminProductPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Supplier account',
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

	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountSupplierRole = rolesResponse?.items?.filter((role) => {
		return role.name === 'Account Supplier';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountSupplierRole[0].id,
		user.emailAddress
	);

	const accountGroup1 = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup1.id, type: 'accountGroup'});

	const accountGroup2 = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup2.id, type: 'accountGroup'});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account.externalReferenceCode,
		accountGroup1.externalReferenceCode
	);

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		accountId: account.id,
	});

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product1'},
		productAccountGroupFilter: true,
		productAccountGroups: [
			{accountGroupId: accountGroup1.id, id: 0},
			{accountGroupId: accountGroup2.id, id: 0},
		],
	});

	await commerceAdminProductPage.gotoProduct(product1.name['en_US'], false);

	await expect(
		await commerceAdminProductDetailsPage.productSkusLink
	).toBeVisible();

	await commerceAdminProductDetailsPage.goToProductVisibility();

	await expect(await page.getByText(accountGroup1.name)).toBeVisible();
	await expect(await page.getByText(accountGroup2.name)).toBeVisible();

	await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsButton.click();

	await expect(
		await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsTitle
	).toBeVisible();
	await expect(
		await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsRow(
			accountGroup1.name
		)
	).toBeVisible();
	await expect(
		await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsRow(
			accountGroup2.name
		)
	).toBeVisible();

	await performLogout(page);

	await performLogin(page, 'demo.unprivileged');

	await commerceAdminProductPage.gotoProduct(product1.name['en_US'], false);

	await expect(
		await commerceAdminProductDetailsPage.productSkusLink
	).toBeVisible();

	await commerceAdminProductDetailsPage.goToProductVisibility();

	await expect(await page.getByText(accountGroup1.name)).toBeVisible();
	await expect(await page.getByText(accountGroup2.name)).toBeHidden();

	await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsButton.click();

	await expect(
		await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsTitle
	).toBeVisible();
	await expect(
		await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsRow(
			accountGroup1.name
		)
	).toBeVisible();
	await expect(
		await commerceAdminProductDetailsVisibilityPage.selectAccountGroupsRow(
			accountGroup2.name
		)
	).toBeHidden();
});
