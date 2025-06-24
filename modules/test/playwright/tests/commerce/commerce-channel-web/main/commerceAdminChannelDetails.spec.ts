/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../../utils/performLogin';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

test('LPD-13490 Manage channel country visibility from channel page', async ({
	apiHelpers,
	commerceAdminChannelDetailsCountriesPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	page,
}) => {
	await page.goto('/');

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	await commerceAdminChannelsPage.goto();

	await (
		await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
	).click();

	await commerceAdminChannelDetailsPage.goToCountries();

	await commerceAdminChannelDetailsCountriesPage.addCountryButton.click();

	const countryName1 = 'Afghanistan';
	const countryName2 = 'Aland Islands';

	await (
		await commerceAdminChannelDetailsCountriesPage.countryFrameCountry(
			countryName1
		)
	).check();
	await (
		await commerceAdminChannelDetailsCountriesPage.countryFrameCountry(
			countryName2
		)
	).check();

	await commerceAdminChannelDetailsCountriesPage.addCountryAddButton.click();

	await expect(
		(
			await commerceAdminChannelDetailsCountriesPage.countriesTableRow(
				0,
				countryName1,
				true
			)
		).row
	).toBeVisible();
	await expect(
		(
			await commerceAdminChannelDetailsCountriesPage.countriesTableRow(
				0,
				countryName2,
				true
			)
		).row
	).toBeVisible();

	await (
		await commerceAdminChannelDetailsCountriesPage.countriesTableRowAction(
			countryName1,
			'Remove'
		)
	).click();

	await page.reload();

	expect(
		await commerceAdminChannelDetailsCountriesPage.countriesTableRows()
	).toHaveLength(1);
	await expect(
		(
			await commerceAdminChannelDetailsCountriesPage.countriesTableRow(
				0,
				countryName2,
				true
			)
		).row
	).toBeVisible();
});

test('LPD-30466 Verify users without edit permission cannot click on channel name link', async ({
	apiHelpers,
	commerceAdminChannelDetailsPage,
	page,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'User' + getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['VIEW_COMMERCE_CHANNELS'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.channel',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName:
					'com.liferay.commerce.product.model.CommerceChannel',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_channel_web_internal_portlet_CommerceChannelsPortlet',
				scope: 1,
			},
		],
	});

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performLogout(page);

	await performLogin(page, userAccount.alternateName);

	await commerceAdminChannelDetailsPage.goto(false);

	try {
		await expect(page.getByText(channel.name)).toBeVisible();
		await expect(
			commerceAdminChannelDetailsPage.channelNameLink(channel.name)
		).toHaveCount(0);
	}
	finally {
		await performLogout(page);

		await performLogin(page, 'test');
	}
});
