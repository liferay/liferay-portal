/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {compareScreenshots} from '../../../utils/compareScreenshots';
import {getSiteHomePageScreenshot} from '../../../utils/getSiteHomePageScreenshot';
import {stagingPageTest} from './fixtures/stagingPageTest';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35443': {enabled: false},
		'LPD-45276': {enabled: true},
	}),
	loginTest(),
	stagingPageTest
);

[
	{name: 'com.liferay.site.initializer.masterclass'},
	{name: 'com.liferay.site.initializer.welcome'},
].forEach(({name}) => {
	test(`Local Staging can be enabled with site initializer ${name}`, async ({
		apiHelpers,
		page,
		stagingPage,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name,
			templateKey: name,
			templateType: 'site-initializer',
		});

		expect(site.name).toBeDefined();

		await stagingPage.goto(site.name);

		await stagingPage.enableLocalStaging();

		compareScreenshots(
			await getSiteHomePageScreenshot(page, site.name, {
				mask: page.getByTestId('notificationsCount'),
				staging: false,
			}),
			await getSiteHomePageScreenshot(page, site.name, {
				mask: page.getByTestId('notificationsCount'),
				staging: true,
			}),
			{
				errorMessage: 'The live and staging pages differ.',
				writeDiff: true,
			}
		);
	});
});
