/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {SiteSettingsLocalizationPage} from '../pages/site-admin-web/SiteSettingsLocalizationPage';
import { SiteSettingsPage } from '../pages/site-admin-web/SiteSettingsPage';

const siteSettingsPagesTest = test.extend<{
	siteSettingsLocalizationPage: SiteSettingsLocalizationPage;
	siteSettingsPage: SiteSettingsPage
}>({
	siteSettingsLocalizationPage: async ({page}, use) => {
		await use(new SiteSettingsLocalizationPage(page));
	},
	siteSettingsPage: async ({page}, use) => {
		await use(new SiteSettingsPage(page));
	}
});

export {siteSettingsPagesTest};
