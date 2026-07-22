/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export class CMSLoginPage {
	readonly page: Page;

	readonly carousel: Locator;
	readonly emailAddressInput: Locator;
	readonly passwordInput: Locator;
	readonly welcomeHeading: Locator;

	constructor(page: Page) {
		this.page = page;

		this.carousel = page.locator('.login-carousel');

		this.emailAddressInput = page.getByLabel('Email Address');

		this.passwordInput = page.getByLabel('Password');

		this.welcomeHeading = page.getByRole('heading', {
			name: 'Headless CMS',
		});
	}

	async goto() {
		await this.page.goto('/sign-in');

		await expect(this.carousel).toBeVisible();
	}
}
