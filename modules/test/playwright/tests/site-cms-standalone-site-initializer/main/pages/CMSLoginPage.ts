/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export const CAROUSEL_SLIDE_KEYS = [
	'structured-content-for-every-channel',
	'all-your-assets-one-smart-library',
	'collaboration-without-friction',
	'control-every-piece-of-content',
];

export class CMSLoginPage {
	readonly page: Page;

	readonly carousel: Locator;
	readonly emailAddressInput: Locator;
	readonly passwordInput: Locator;
	readonly pauseButton: Locator;
	readonly resumeButton: Locator;
	readonly welcomeHeading: Locator;

	constructor(page: Page) {
		this.page = page;

		this.carousel = page.locator('.login-carousel');

		this.emailAddressInput = page.getByLabel('Email Address');

		this.passwordInput = page.getByLabel('Password');

		this.pauseButton = page.getByRole('button', {name: 'Pause'});

		this.resumeButton = page.getByRole('button', {name: 'Resume'});

		this.welcomeHeading = page.getByRole('heading', {
			name: 'Headless CMS',
		});
	}

	activeSlideHeading(title: string): Locator {
		return this.page.getByRole('heading', {level: 2, name: title});
	}

	dot(label: string): Locator {
		return this.page.getByRole('button', {name: label});
	}

	async getSlideDotLabels(): Promise<string[]> {
		return this.page.evaluate(
			(count) =>
				Array.from({length: count}, (_, index) =>
					Liferay.Util.sub(Liferay.Language.get('go-to-slide-x'), [
						String(index + 1),
					])
				),
			CAROUSEL_SLIDE_KEYS.length
		);
	}

	async getSlideTitles(): Promise<string[]> {
		return this.page.evaluate(
			(keys) => keys.map((key) => Liferay.Language.get(key)),
			CAROUSEL_SLIDE_KEYS
		);
	}

	async goto() {
		await this.page.goto('/sign-in');

		await expect(this.carousel).toBeVisible();
	}
}
