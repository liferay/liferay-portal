/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {frontendThemePagesTest} from './fixtures/frontendThemePagesTest';
import {PageHelper} from './helpers/PageHelper';

const CLASSIC_THEME_CONTEXT_PATH = 'classic-theme';
const CMS_THEME_CONTEXT_PATH = 'cms-theme';

async function createPage(pageHelper: PageHelper) {
	const sitePageName = getRandomString();
	const sitePage = await pageHelper.createPage(sitePageName);

	return {sitePage, sitePageName};
}

const test = mergeTests(
	frontendThemePagesTest,
	loginTest(),
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	})
);

test(
	'Verifies CMS theme can be applied to a site page',
	{tag: '@LPD-70288'},
	async ({pageHelper, themeHelper}) => {
		const {sitePage, sitePageName} =
			await test.step('Create site page', async () =>
				await createPage(pageHelper));

		await test.step('Verify classic theme is applied by default', async () => {
			await pageHelper.goToPage(sitePage);

			await pageHelper.expectPageToUseThemeCss(
				CLASSIC_THEME_CONTEXT_PATH
			);
		});

		await test.step('Verify CMS theme can be applied', async () => {
			await themeHelper.changePageThemeToCMS(sitePageName);

			await themeHelper.publishPage(sitePageName);

			await pageHelper.expectPageToUseThemeCss(CMS_THEME_CONTEXT_PATH);
		});
	}
);

test(
	'A theme can be deactivated and reactivated',
	{tag: '@LPD-70288'},
	async ({pageHelper, themeHelper}) => {
		const {sitePage, sitePageName} =
			await test.step('Create site page', async () =>
				await createPage(pageHelper));

		await test.step('Set page theme to CMS theme', async () => {
			await themeHelper.changePageThemeToCMS(sitePageName);

			await themeHelper.publishPage(sitePageName);

			await pageHelper.goToPage(sitePage);

			await pageHelper.expectPageToUseThemeCss(CMS_THEME_CONTEXT_PATH);
		});

		await test.step('Deactivates CMS theme', async () => {
			await themeHelper.deactivateCMSTheme(sitePageName);

			await themeHelper.expectCurrentThemeToBeClassic(sitePageName);

			await pageHelper.goToPage(sitePage);

			await pageHelper.expectPageToUseThemeCss(
				CLASSIC_THEME_CONTEXT_PATH
			);
		});

		await test.step('Reactivates CMS theme', async () => {
			await themeHelper.activateCMSTheme(sitePageName);

			await themeHelper.expectCurrentThemeToBeCMS(sitePageName);

			await pageHelper.goToPage(sitePage);

			await pageHelper.expectPageToUseThemeCss(CMS_THEME_CONTEXT_PATH);
		});
	}
);

test(
	'A theme can be uninstalled and reinstalled',
	{tag: '@LPD-70288'},
	async ({pageHelper, themeHelper}) => {
		const {sitePage, sitePageName} =
			await test.step('Create site page', async () =>
				await createPage(pageHelper));

		await test.step('Set page theme to CMS theme', async () => {
			await themeHelper.changePageThemeToCMS(sitePageName);

			await themeHelper.publishPage(sitePageName);

			await pageHelper.goToPage(sitePage);

			await pageHelper.expectPageToUseThemeCss(CMS_THEME_CONTEXT_PATH);
		});

		await test.step('Uninstall CMS theme', async () => {
			await themeHelper.uninstallCMSTheme(sitePageName);

			await themeHelper.expectCurrentThemeToBeClassic(sitePageName);

			await pageHelper.goToPage(sitePage);

			await pageHelper.expectPageToUseThemeCss(
				CLASSIC_THEME_CONTEXT_PATH
			);
		});

		await test.step('Redeploy CMS theme', async () => {
			await themeHelper.reinstallCMSTheme(sitePageName);

			await themeHelper.expectCurrentThemeToBeCMS(sitePageName);

			await pageHelper.goToPage(sitePage);

			await pageHelper.expectPageToUseThemeCss(CMS_THEME_CONTEXT_PATH);
		});
	}
);
