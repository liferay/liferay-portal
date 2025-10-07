/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import performLogin, {performLogout} from '../../../../utils/performLogin';
import {commerceReturnSetUp, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-10562': {enabled: true},
	}),
	loginTest()
);

test('LPD-32515 Returns admin page displays amount fields with correct currency pattern', async ({
	apiHelpers,
	commerceAdminReturnsPage,
	page,
}) => {
	const {commerceReturn, sku} = await commerceReturnSetUp(apiHelpers);

	await commerceAdminReturnsPage.goto();

	await expect(
		(await commerceAdminReturnsPage.tableRow(4, '$ 0.00', true)).row
	).toBeVisible();

	await (
		await commerceAdminReturnsPage.tableRowLink({
			colIndex: 1,
			rowValue: commerceReturn.id,
		})
	).click();

	await expect(
		(await commerceAdminReturnsPage.tableRow(0, sku.sku, true)).row
	).toBeVisible();

	for await (const currencyField of await page.getByText('0.00').all()) {
		await expect(currencyField.getByText('$')).toBeVisible();
	}
});

test('LPD-32524 Returns admin page shows comments for return items', async ({
	apiHelpers,
	commerceAdminReturnsPage,
}) => {
	const {commerceReturn} = await commerceReturnSetUp(apiHelpers);

	await commerceAdminReturnsPage.goto();

	await expect(
		(await commerceAdminReturnsPage.tableRow(4, '$ 0.00', true)).row
	).toBeVisible();

	await (
		await commerceAdminReturnsPage.tableRowLink({
			colIndex: 1,
			rowValue: commerceReturn.id,
		})
	).click();

	await commerceAdminReturnsPage.returnActionsButton.click();

	await expect(
		commerceAdminReturnsPage.returnActionsEditButton
	).toBeVisible();
	await commerceAdminReturnsPage.returnActionsEditButton.click();

	await expect(
		commerceAdminReturnsPage.returnItemsCommentTitle
	).toBeVisible();

	await commerceAdminReturnsPage.returnItemsCommentInput.fill(
		'This is a comment.'
	);

	await commerceAdminReturnsPage.returnItemsSubmitButton.click();

	await expect(
		commerceAdminReturnsPage.editReturnItemFrame.getByText(
			'This is a comment.'
		)
	).toBeVisible();

	await expect(
		commerceAdminReturnsPage.returnItemsCommentInput
	).toBeVisible();
});

test('LPD-34670 Returns Manager can view and edit returns in returns admin page', async ({
	apiHelpers,
	commerceAdminReturnsPage,
	page,
}) => {
	await miniumSetUp(apiHelpers);

	const {commerceReturn} = await commerceReturnSetUp(apiHelpers);

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);

	const role =
		await apiHelpers.headlessAdminUser.getRoleByName('Returns Manager');

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	await performLogout(page);

	await performLogin(page, 'demo.unprivileged');

	await commerceAdminReturnsPage.goto(false);

	await (
		await commerceAdminReturnsPage.tableRowLink({
			colIndex: 1,
			rowValue: commerceReturn.id,
		})
	).click();

	await expect(page.getByTestId('headerDetailsTitle')).toBeVisible();
});
