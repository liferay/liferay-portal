/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {gogoShellPageTest} from '../../../../fixtures/gogoShellPageTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	gogoShellPageTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Create orders using GoGo Shell command',
	{tag: ['@COMMERCE-11716', '@LPD-56676']},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceAdminOrdersPage,
		gogoShellPage,
		page,
	}) => {
		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const shippingOptions = [getRandomString(), getRandomString()];

		await commerceAdminChannelsPage.setupCommerceChannelShippingMethod(
			channel.name,
			'Flat Rate',
			shippingOptions
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'Product'},
		});

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await apiHelpers.headlessCommerceAdminAccount.postAddress(account.id, {
			defaultBilling: false,
			defaultShipping: false,
		});

		await gogoShellPage.addCommand(
			'generateOrders ' + channel.siteGroupId + ' 50'
		);

		await commerceAdminOrdersPage.goto();

		await expect(
			await page.getByText('Showing 1 to 20 of 50 entries.')
		).toBeVisible();
	}
);
