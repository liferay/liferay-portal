/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {
	clearConsentCookies,
	expectCookieValue,
	resetConsentManagerConfiguration,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

const cookieKeys = [
	'CONSENT_TYPE_FUNCTIONAL',
	'CONSENT_TYPE_PERFORMANCE',
	'CONSENT_TYPE_PERSONALIZATION',
];

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	siteSettingsPagesTest,
	systemSettingsPageTest
);

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Reset Consent Manager Configuration', async () => {
		await resetConsentManagerConfiguration(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage.page);
	});
});

test.beforeEach(async ({page}) => {
	await test.step('Enable the Consent Manager without activating it', async () => {
		await updateConsentManagerConfiguration(page, {
			active: false,
			enabled: true,
			forceReload: true,
		});
	});
});

test(
	'Verify the Cookies Banner Configuration widget saves the consent on a site page',
	{tag: '@LPS-142518'},
	async ({apiHelpers, page, site}) => {
		await goToCookiesBannerConfigurationWidgetPage(apiHelpers, page, site);

		await test.step('Verify the widget elements', async () => {
			const widget = getCookiesBannerConfigurationWidget(page);

			for (const cookieKey of cookieKeys) {
				await expect(
					widget.locator(`[data-cookie-key="${cookieKey}"]`)
				).toHaveCount(1);
			}

			for (const name of [
				'Accept All',
				'Accept Selected',
				'Use Necessary Cookies Only',
			]) {
				await expect(widget.getByRole('button', {name})).toBeVisible();
			}
		});

		await test.step('Accept every cookie type through the widget', async () => {
			await acceptAllCookieTypes(page);
		});
	}
);

test(
	'Verify the Cookies Banner Configuration widget uses the site-level settings',
	{tag: '@LPS-168413'},
	async ({apiHelpers, page, site, siteSettingsPage}) => {
		const cookiePolicyLink = `/web${site.friendlyUrlPath}`;
		const description = getRandomString();
		const linkDisplayText = getRandomString();

		await test.step('Customize the Cookie Panel of the site', async () => {
			await siteSettingsPage.goToSiteSetting(
				'Privacy',
				'Cookie Panel',
				site.friendlyUrlPath
			);

			await page.getByLabel('Cookie Policy Link').fill(cookiePolicyLink);
			await page
				.getByLabel('Description', {exact: true})
				.fill(description);
			await page
				.getByLabel('Link Display Text', {exact: true})
				.fill(linkDisplayText);

			await siteSettingsPage.saveConfiguration();
		});

		await goToCookiesBannerConfigurationWidgetPage(apiHelpers, page, site);

		await test.step('Verify the widget shows the site-level texts', async () => {
			const widget = getCookiesBannerConfigurationWidget(page);

			await expect(widget.getByText(description)).toBeVisible();
			await expect(
				widget.getByRole('link', {name: linkDisplayText})
			).toHaveAttribute('href', cookiePolicyLink);
		});

		await test.step('Accept every cookie type through the widget', async () => {
			await acceptAllCookieTypes(page);
		});
	}
);

async function acceptAllCookieTypes(page: Page) {
	const widget = getCookiesBannerConfigurationWidget(page);

	for (const cookieKey of cookieKeys) {
		const toggle = widget.locator(`[data-cookie-key="${cookieKey}"]`);

		await expect(toggle).toBeEnabled();

		await widget
			.locator(
				`label.toggle-switch:has([data-cookie-key="${cookieKey}"])`
			)
			.click();

		await expect(toggle).toBeChecked();
	}

	await widget.getByRole('button', {name: 'Accept Selected'}).click();

	for (const cookieKey of cookieKeys) {
		await expectCookieValue(cookieKey, page, 'true');
	}
}

function getCookiesBannerConfigurationWidget(page: Page) {
	return page.locator(
		'[id^="p_p_id_com_liferay_cookies_banner_web_portlet_CookiesBannerConfigurationPortlet"]'
	);
}

async function goToCookiesBannerConfigurationWidgetPage(
	apiHelpers: ApiHelpers,
	page: Page,
	site: Site
) {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getWidgetDefinition({
				id: getRandomString(),
				widgetName:
					'com_liferay_cookies_banner_web_portlet_CookiesBannerConfigurationPortlet',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);
}
