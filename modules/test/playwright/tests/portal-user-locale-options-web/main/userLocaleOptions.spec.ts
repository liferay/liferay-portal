/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import getRandomString from '../../../utils/getRandomString';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	siteSettingsPagesTest,
	usersAndOrganizationsPagesTest
);

test('LPD-46913 Language should change properly for admins even if the site does not have the admin language', async ({
	apiHelpers,
	page,
	siteSettingsLocalizationPage,
	userLocaleOptionsPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	await siteSettingsLocalizationPage.goto(site.friendlyUrlPath);

	try {
		await siteSettingsLocalizationPage.setCustomDefaultLanguage(
			'Spanish (Spain)',
			site.friendlyUrlPath
		);

		if (
			(await siteSettingsLocalizationPage.page
				.getByRole('listbox')
				.first()
				.count()) > 1
		) {
			await siteSettingsLocalizationPage.disableAllLanguagesExceptSp(
				site.friendlyUrlPath
			);
		}

		const siteURL = `/es/group${site.friendlyUrlPath}`;
		await page.goto(siteURL);

		await userLocaleOptionsPage.changeLanguageWithAlert();

		await siteSettingsLocalizationPage.goto(site.friendlyUrlPath);

		expect(
			siteSettingsLocalizationPage.customDefaultLanguageOption
		).toBeVisible();
	}
	finally {
		await page.goto('en');
	}
});
