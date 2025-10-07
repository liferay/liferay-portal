/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {
	OptionalProductAnalyticsCookieTypes,
	ProductAnalyticsCookie,
	RequiredProductAnalyticsCookieTypes,
} from '../productAnalyticsConsentPanel.spec';

async function checkCookies(
	expectedCookies: ProductAnalyticsCookie[],
	page: Page
) {
	const actualCookies = await page.context().cookies();

	for (const expectedCookie of expectedCookies.values()) {
		const actualCookie = await actualCookies.find(
			(actualCookie) => actualCookie.name === expectedCookie.name
		);

		await expect(actualCookie).toBeDefined();
		await expect(actualCookie.value).toEqual(
			expectedCookie.value.toString()
		);
	}
}

export async function clearProductAnalyticsCookies(page: Page) {
	await page.context().clearCookies({name: /^PRODUCT_ANALYTICS/});

	await page.reload({waitUntil: 'domcontentloaded'});
}

export async function expectAllCookiesAccepted(page: Page) {
	await page.reload({waitUntil: 'domcontentloaded'});

	const productAnalyticsCookies: ProductAnalyticsCookie[] = [];

	await setOptionalCookiesValues(productAnalyticsCookies, true);

	await setRequiredCookiesValues(productAnalyticsCookies);

	await checkCookies(productAnalyticsCookies, page);
}

export async function expectAllCookiesDeclined(page: Page) {
	await page.reload({waitUntil: 'domcontentloaded'});

	const productAnalyticsCookies: ProductAnalyticsCookie[] = [];

	await setOptionalCookiesValues(productAnalyticsCookies, false);

	await setRequiredCookiesValues(productAnalyticsCookies);

	await checkCookies(productAnalyticsCookies, page);
}

async function setOptionalCookiesValues(
	cookies: ProductAnalyticsCookie[],
	enabled: boolean
) {
	Object.values(OptionalProductAnalyticsCookieTypes).forEach((name) => {
		cookies.push({name, value: enabled});
	});
}

async function setRequiredCookiesValues(cookies: ProductAnalyticsCookie[]) {
	Object.values(RequiredProductAnalyticsCookieTypes).forEach((name) => {
		cookies.push({name, value: true});
	});
}
