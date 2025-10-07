/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {listTypeDefinitionsPagesTest} from '../../../fixtures/listTypeDefinitionsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	listTypeDefinitionsPagesTest,
	loginTest(),
	siteSettingsPagesTest
);

test(
	'Input localized default language when navigating between sites with a different default language',
	{tag: '@LPD-48286'},
	async ({
		apiHelpers,
		listTypeDefinitionPage,
		page,
		site,
		siteSettingsLocalizationPage,
	}) => {
		await test.step('Create site with default Spanish (Spain) locale', async () => {
			await siteSettingsLocalizationPage.goto(site.friendlyUrlPath);

			await siteSettingsLocalizationPage.setCustomDefaultLanguage(
				'es_ES',
				site.friendlyUrlPath
			);
		});

		let listTypeDefinition: ListTypeDefinition;

		await test.step('Create a picklist', async () => {
			listTypeDefinition =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			apiHelpers.data.push({
				id: listTypeDefinition.id,
				type: 'listTypeDefinition',
			});
		});

		await test.step('Go to picklist page', async () => {
			await listTypeDefinitionPage.goto();
		});

		await test.step('Open picklist edit panel', async () => {
			await page
				.getByRole('link', {name: listTypeDefinition.name})
				.click();
		});

		await test.step('Open add picklist item modal', async () => {
			await listTypeDefinitionPage.addPicklistItemButton.click();
		});

		await test.step('Check the default language is "en_US"', async () => {
			expect(
				page.frameLocator('iframe').getByTitle('en_US')
			).toBeVisible();
		});
	}
);
