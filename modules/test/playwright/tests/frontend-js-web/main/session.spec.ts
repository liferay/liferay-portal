/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	performUserSwitch,
	userData,
} from '../../../utils/performLogin';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	productMenuPageTest,
	siteSettingsPagesTest
);

test(
	'Using session utils does not change locale',
	{tag: '@LPD-1935'},
	async ({apiHelpers, page, site, siteSettingsLocalizationPage}) => {
		await test.step('Create site with default Spanish locale', async () => {
			await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: getRandomString(),
			});

			await siteSettingsLocalizationPage.goto(site.friendlyUrlPath);

			await siteSettingsLocalizationPage.setCustomDefaultLanguage(
				'es_ES',
				site.friendlyUrlPath
			);
		});

		await test.step('Create new Spanish user and assign to site as administrator', async () => {
			const user = await apiHelpers.headlessAdminUser.postUserAccount({
				languageId: 'es_ES',
			});

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			const siteAdminRole =
				await apiHelpers.headlessAdminUser.getRoleByName(
					'Site Administrator'
				);

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteAdminRole.id,
				site.id,
				user.id
			);

			await performUserSwitch(page, user.alternateName);
		});

		await test.step('Go to any control menu site page, see it in Spanish', async () => {
			await page.goto(
				`/group${site.friendlyUrlPath}/~/control_panel/manage?p_p_id=com_liferay_layout_admin_web_portlet_GroupPagesPortlet`
			);

			const translateLink = page.getByRole('link', {
				name: 'Mostrar la página en español (España).',
			});

			const inEnglish = await translateLink.isVisible();

			if (inEnglish) {
				await translateLink.click();
			}

			const bcp47LanguageId = await page.evaluate(() =>
				Liferay.ThemeDisplay.getBCP47LanguageId()
			);

			expect(bcp47LanguageId).toEqual('es-ES');
		});

		await test.step('Make any session request', async () => {
			await page.evaluate(() => {
				Liferay.Util.Session.get('foo');
			});

			await page.waitForResponse(
				(response: any) =>
					response.status() === 200 &&
					response.url().includes('session_click')
			);
		});

		await test.step('Refresh page and check locale is preserved', async () => {
			await page.reload();

			const bcp47LanguageId = await page.evaluate(() =>
				Liferay.ThemeDisplay.getBCP47LanguageId()
			);

			expect(bcp47LanguageId).toEqual('es-ES');
		});
	}
);

test(
	'Check session timeout banner is disabled when set to 0',
	{tag: '@LPD-76518'},
	async ({page}) => {
		await expect(page.getByText('Due to inactivity')).toBeHidden();
	}
);

test(
	'Check session expired message is not shown when the tab wakes up after the session expired and the warning is disabled',
	{tag: '@LPD-107298'},
	async ({page}) => {
		await page.clock.install();

		await performLogout(page);
		await performLoginViaApi({page, rememberMe: false, screenName: 'test'});

		await page.waitForFunction(() => (window as any).Liferay.Session);

		const sessionLength = await page.evaluate(
			() => (window as any).Liferay.Session.sessionLength
		);

		await page.clock.fastForward(sessionLength + 1000);

		await expect
			.poll(() =>
				page.evaluate(
					() => (window as any).Liferay.Session.sessionState
				)
			)
			.toBe('expired');

		await expect(page.getByText('Due to inactivity')).toBeHidden();
	}
);
