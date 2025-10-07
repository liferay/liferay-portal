/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {liferayConfig} from '../../liferay.config';

export class SMTPMockServerPage {
	readonly createAccountLink: Locator;
	readonly emailBody: (accountName: string) => Locator;
	readonly emailHeading: (accountName: string) => Locator;
	readonly emailLink: (accountName: string) => Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.createAccountLink = page.getByRole('link', {
			name: 'Create Account',
		});
		this.emailBody = (accountName: string) =>
			page.getByText(
				`Test Test has invited you to join ${accountName}.`,
				{exact: true}
			);
		this.emailHeading = (accountName: string) =>
			page.getByRole('heading', {
				name: `Test Test has invited you to join ${accountName}`,
			});
		this.emailLink = (accountName: string) =>
			page.getByRole('link', {name: accountName});
		this.page = page;
	}

	async goto(page: Page) {
		await page.goto(
			liferayConfig.environment.baseUrl.replace(/:([0-9]*)$/, ':8282')
		);
	}
}
