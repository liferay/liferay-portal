/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let account;

test.beforeEach(async ({apiHelpers}) => {
	await test.step('Create account', async () => {
		account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});
	});
});

test.afterEach(async ({apiHelpers}) => {
	await apiHelpers.headlessAdminUser.deleteAccount(account.id);
});

test(
	'Width of columns can be set with CSS',
	{
		tag: ['@LPD-31378'],
	},
	async ({apiHelpers, applicationsMenuPage, page}) => {
		await test.step('Create commerce site', async () => {
			const site = await apiHelpers.headlessSite.createSite({
				name: 'Minium',
				templateKey: 'minium-initializer',
				templateType: 'site-initializer',
			});

			apiHelpers.data.push({id: site.id, type: 'site'});

			await applicationsMenuPage.goToSite('Minium');
		});

		await test.step('Add transmission to shopping cart', async () => {
			const accountNameField = page.getByText(
				'There is no order selected.'
			);
			await accountNameField.waitFor({state: 'visible'});

			const transmissionButton = page
				.locator('#wwxc_column_2d_2_1_add_to_cart')
				.getByRole('button', {name: 'Add to Cart'});

			await transmissionButton.waitFor({state: 'visible'});
			await transmissionButton.click();

			const cartButton = page.locator('[data-qa-id="miniCartButton"]');

			await cartButton.waitFor({state: 'visible'});
			await cartButton.click();
		});

		await test.step('Check Name and SKU headers width', async () => {
			const viewDetailsButton = page.getByRole('button', {
				name: 'View Details',
			});

			await viewDetailsButton.waitFor({state: 'visible'});
			await viewDetailsButton.click();

			const nameTableHeader = page.locator('.fds table th.cell-name');

			await nameTableHeader.waitFor({state: 'visible'});
			const nameTableHeaderBoundingBox =
				await nameTableHeader.boundingBox();
			const nameTableHeaderWidth = nameTableHeaderBoundingBox.width;

			expect(nameTableHeaderWidth).toBeGreaterThanOrEqual(150);

			const skuTableHeader = page.locator('.fds table th.cell-sku');

			const skuTableHeaderBoundingBox =
				await skuTableHeader.boundingBox();
			const skuTableHeaderWidth = skuTableHeaderBoundingBox.width;

			expect(skuTableHeaderWidth).toBeGreaterThanOrEqual(100);
		});
	}
);
