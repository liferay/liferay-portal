/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {FilePreviewLimitsInstanceSettingsPage} from '../pages/FilePreviewLimitsInstanceSettingsPage';
import {FilePreviewLimitsSiteSettingsPage} from '../pages/FilePreviewLimitsSiteSettingsPage';
import {FilePreviewLimitsSystemSettingsPage} from '../pages/FilePreviewLimitsSystemSettingsPage';

const filePreviewLimitsPagesTest = test.extend<{
	filePreviewLimitsInstanceSettingsPage: FilePreviewLimitsInstanceSettingsPage;
	filePreviewLimitsSiteSettingsPage: FilePreviewLimitsSiteSettingsPage;
	filePreviewLimitsSystemSettingsPage: FilePreviewLimitsSystemSettingsPage;
}>({
	filePreviewLimitsInstanceSettingsPage: async ({page}, use) => {
		await use(new FilePreviewLimitsInstanceSettingsPage(page));
	},
	filePreviewLimitsSiteSettingsPage: async ({page}, use) => {
		await use(new FilePreviewLimitsSiteSettingsPage(page));
	},
	filePreviewLimitsSystemSettingsPage: async ({page}, use) => {
		await use(new FilePreviewLimitsSystemSettingsPage(page));
	},
});

export {filePreviewLimitsPagesTest};
