/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {getHeader} from '../../../../helpers/ApiHelpers';
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
			try {

				// Render the admin UI in English regardless of the current
				// instance default language, so the settings navigation and
				// controls resolve when the test left the instance in another
				// language. Both calls carry the authenticity token: without
				// it the portal answers Forbidden, and the body it returns for
				// that is not always JSON.

				const response = await page.request.get(
					'/o/headless-admin-user/v1.0/my-user-account',
					{headers: await getHeader(page)}
				);

				const myUserAccount = await response.json();

				await page.request.patch(
					`/o/headless-admin-user/v1.0/user-accounts/${myUserAccount.id}`,
					{
						data: {languageId: 'en_US'},
						headers: await getHeader(page),
					}
				);

				await page.reload();
			}
			finally {

				// The instance default language is instance wide state that
				// every later test inherits, so restore it even when the steps
				// above fail. Leaving it set fails sign-in verification,
				// object definition labels, and site page creation across
				// every file and worker that follows.

				await localizationInstanceSettingsPage.goto('Language', false);

				await localizationInstanceSettingsPage.setDefaultLanguage(
					'en_US'
				);
			}
		}
	},
});

export {localizationPagesTest};
