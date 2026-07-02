/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {liferayConfig} from '../../../../liferay.config';

export async function checkEmptyStateOnACSide(page: Page) {
	await expect(page.getByText('There are no tests found.')).toBeVisible();
}

export async function clickOnActionButton({
	name,
	page,
}: {
	name: string;
	page: Page;
}) {
	const linkLocator = page.locator(`xpath=//a[contains(text(),"${name}")]`);

	const href = await linkLocator.getAttribute('href');

	const {pathname, search} = new URL(href, liferayConfig.environment.baseUrl);

	await page.goto(`${liferayConfig.environment.baseUrl}${pathname}${search}`);
}
