/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser, BrowserContext, Page} from '@playwright/test';

interface MFAChallengeSession {
	userContext: BrowserContext;
	userPage: Page;
}

/**
 * Signs in as the given user in a fresh browser context and stops on the
 * multi-factor authentication challenge, leaving any session on the caller's
 * page (typically the admin) untouched. Sign-in does not complete because the
 * challenge interrupts it, so the shared performLogin helper does not apply.
 */
export default async function signInAndReachMFAChallenge(
	browser: Browser,
	emailAddress: string,
	password = 'test'
): Promise<MFAChallengeSession> {
	const userContext = await browser.newContext();
	const userPage = await userContext.newPage();

	await userPage.goto('/');

	await userPage.getByRole('button', {name: 'Sign In'}).last().click();

	await userPage.getByLabel('Email Address').fill(emailAddress);
	await userPage.getByLabel('Password').fill(password);

	await userPage.getByRole('button', {name: 'Sign In'}).last().click();

	return {userContext, userPage};
}
