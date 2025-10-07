/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {AppManagerPage} from '../pages/marketplace-app-manager-web/AppManagerPage';
import {BundleBlacklistPage} from '../pages/marketplace-app-manager-web/BundleBlacklistPage';
import {LicenseManagerPage} from '../pages/marketplace-app-manager-web/LicenseManagerPage';

const appManagerPagesTest = test.extend<{
	appManagerPage: AppManagerPage;
	bundleBlacklistPage: BundleBlacklistPage;
	licenseManagerPage: LicenseManagerPage;
}>({
	appManagerPage: async ({page}, use) => {
		await use(new AppManagerPage(page));
	},
	bundleBlacklistPage: async ({page}, use) => {
		await use(new BundleBlacklistPage(page));
	},
	licenseManagerPage: async ({page}, use) => {
		await use(new LicenseManagerPage(page));
	},
});

export {appManagerPagesTest};
