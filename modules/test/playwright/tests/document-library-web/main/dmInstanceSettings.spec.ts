/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {dmSettingsPagesTest} from './fixtures/dmSettingsPagesTest';

const test = mergeTests(dmSettingsPagesTest, isolatedSiteTest, loginTest());

test.afterEach(async ({fileSizeLimitsInstanceSettingsPage}) => {
	await fileSizeLimitsInstanceSettingsPage.goto();

	await fileSizeLimitsInstanceSettingsPage.modifyInputAndSave(
		'Maximum File Upload Size',
		'0'
	);
});

test(
	'Updating Maximum File Upload Size at Instance level, not overrides site configuration',
	{tag: '@LPD-17827'},
	async ({
		fileSizeLimitsInstanceSettingsPage,
		fileSizeLimitsSiteSettingsPage,
		page,
		site,
	}) => {
		await fileSizeLimitsInstanceSettingsPage.goto();
		await fileSizeLimitsInstanceSettingsPage.modifyInputAndSave(
			'Maximum File Upload Size',
			'2000'
		);
		await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);
		await fileSizeLimitsSiteSettingsPage.modifyInputAndSave(
			'Maximum File Upload Size',
			'1000'
		);
		await fileSizeLimitsInstanceSettingsPage.goto();
		await fileSizeLimitsInstanceSettingsPage.modifyInputAndSave(
			'Maximum File Upload Size',
			'2000'
		);
		await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

		await expect(page.getByLabel('Maximum File Upload Size')).toHaveValue(
			'1000'
		);
	}
);
