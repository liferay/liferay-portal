/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {ContentsPage} from '../../main/pages/ContentsPage';
import {FolderPage} from '../../main/pages/FolderPage';
import {SpaceSummaryPage} from '../../main/pages/SpaceSummaryPage';
import {DefaultPermissionsPage} from '../pages/DefaultPermissionsPage';

const cmsPagesTest = test.extend<{
	contentsPage: ContentsPage;
	defaultPermissionsPage: DefaultPermissionsPage;
	folderPage: FolderPage;
	spaceSummaryPage: SpaceSummaryPage;
}>({
	contentsPage: async ({page}, use) => {
		await use(new ContentsPage(page));
	},
	defaultPermissionsPage: async ({page}, use) => {
		await use(new DefaultPermissionsPage(page));
	},
	folderPage: async ({page}, use) => {
		await use(new FolderPage(page));
	},
	spaceSummaryPage: async ({page}, use) => {
		await use(new SpaceSummaryPage(page));
	},
});

export {cmsPagesTest};
