/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../fixtures/loginTest';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-26243 Verify that discount rule field Cart Total Minimum Amount only accepts valid numbers', async ({
	apiHelpers,
	commerceAdminDiscountDetailsPage,
	commerceAdminDiscountsPage,
}) => {
	const discount = await apiHelpers.headlessCommerceAdminPricing.postDiscount(
		{
			usePercentage: true,
		}
	);

	apiHelpers.data.push({id: discount.id, type: 'discount'});

	const discountRule =
		await apiHelpers.headlessCommerceAdminPricing.postDiscountRule(
			discount.id
		);

	apiHelpers.data.push({id: discountRule.id, type: 'discountRule'});

	await commerceAdminDiscountsPage.goto();

	await expect(
		(await commerceAdminDiscountsPage.tableRow(0, discount.title, true)).row
	).toBeVisible();

	await (
		await commerceAdminDiscountsPage.tableRowLink({
			colIndex: 0,
			rowValue: discount.title,
		})
	).click();

	await expect(
		(
			await commerceAdminDiscountDetailsPage.tableRow(
				0,
				discountRule.name,
				true
			)
		).row
	).toBeVisible();

	await (
		await commerceAdminDiscountDetailsPage.tableRowLink({
			colIndex: 0,
			rowValue: discountRule.name,
		})
	).click();

	await (await commerceAdminDiscountDetailsPage.saveButton).click();

	await expect(
		commerceAdminDiscountDetailsPage.amountFieldReuiredErrorMessage
	).toBeVisible();

	await commerceAdminDiscountDetailsPage.cartTotalMiniumAmountInput.fill(
		getRandomString()
	);

	await commerceAdminDiscountDetailsPage.saveButton.click();

	await expect(
		commerceAdminDiscountDetailsPage.mustBeValidNumberErrorMessage
	).toBeVisible();

	await commerceAdminDiscountDetailsPage.cartTotalMiniumAmountInput.fill(
		'1000,00'
	);

	await commerceAdminDiscountDetailsPage.saveButton.click();

	await expect(
		commerceAdminDiscountDetailsPage.mustBeDecimalErrorMessage
	).toBeVisible();

	await commerceAdminDiscountDetailsPage.cartTotalMiniumAmountInput.fill(
		'1000.00'
	);

	await commerceAdminDiscountDetailsPage.saveButton.click();

	waitForAlert(
		commerceAdminDiscountDetailsPage.editDiscountRuleFrame,
		'Success:Your request completed successfully.',
		{autoClose: false}
	);
});
