/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
);

test(
	'Verifying reflected XSS vulnerability in IP Geocoder Sample widget mockIPGeocoderRemoteAddr request parameter',
	{
		tag: '@LPD-106202',
	},
	async ({apiHelpers, browser, page, site, widgetPagePage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('IP Geocoder Sample');

		const guestContext = await browser.newContext({
			storageState: {cookies: [], origins: []},
		});

		const guestPage = await guestContext.newPage();

		try {
			guestPage.on('dialog', async (dialog) => {
				await dialog.dismiss();

				expect(
					dialog.message(),
					'The injected script must not execute'
				).toBeNull();
			});

			await guestPage.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyURL}`
			);

			const portlet = guestPage.locator('.portlet-ip-geocoder-sample');

			const portletInnerText = await portlet.innerText();

			await guestPage.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyURL}?mockIPGeocoderRemoteAddr=${encodeURIComponent('<script>alert(1)</script>')}`
			);

			await expect(portlet).toHaveText(portletInnerText, {
				useInnerText: true,
			});
		}
		finally {
			await guestContext.close();
		}
	}
);
