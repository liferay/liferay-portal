/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {LocalizationInstanceSettingsPage} from '../pages/LocalizationInstanceSettingsPage';

const localizationPagesTest = test.extend<{
	localizationInstanceSettingsPage: LocalizationInstanceSettingsPage;
	restoreInstanceDefaultLanguage: void;
}>({
	localizationInstanceSettingsPage: async ({page}, use) => {
		await use(new LocalizationInstanceSettingsPage(page));
	},
	restoreInstanceDefaultLanguage: async (
		{localizationInstanceSettingsPage, page},
		use
	) => {
		try {
			await use();
		}
		finally {

			// Render the admin UI in English regardless of the current
			// instance default language, so the settings navigation and
			// controls resolve when the test left the instance in another
			// language.

			const response = await page.request.get(
				'/o/headless-admin-user/v1.0/my-user-account'
			);

			const myUserAccount = await response.json();

			await page.request.patch(
				`/o/headless-admin-user/v1.0/user-accounts/${myUserAccount.id}`,
				{data: {languageId: 'en_US'}}
			);

			await page.reload();

			await localizationInstanceSettingsPage.goto('Language', false);

			await localizationInstanceSettingsPage.setDefaultLanguage('en_US');
		}
	},
});

export {localizationPagesTest};
