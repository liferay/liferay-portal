/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser, expect} from '@playwright/test';

import {DataApiHelpers} from '../helpers/ApiHelpers';
import {PersonalMenuPage} from '../pages/users-admin-web/PersonalMenuPage';
import {performLoginViaApi, userData} from './performLogin';

export async function createRecipient(apiHelpers: DataApiHelpers) {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	return user;
}

export async function expectShareNotification(
	browser: Browser,
	screenName: string,
	title: string,
	{present}: {present: boolean}
) {
	const context = await browser.newContext();

	const page = await context.newPage();

	try {
		await performLoginViaApi({page, screenName});

		const personalMenuPage = new PersonalMenuPage(page);

		await page.goto('/');

		await personalMenuPage.userPersonalMenuButton.click();

		await personalMenuPage.menuItem('Notifications').click();

		const notification = page.getByText(`has shared ${title} with you`, {
			exact: false,
		});

		if (present) {
			await expect(notification).toBeVisible({timeout: 5000});
		}
		else {
			await expect(notification).toHaveCount(0);
		}
	}
	finally {
		await context.close();
	}
}
