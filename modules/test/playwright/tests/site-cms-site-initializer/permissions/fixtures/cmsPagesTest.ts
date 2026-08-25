/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {AssetsPage} from '../../main/pages/AssetsPage';
import {ContentsPage} from '../../main/pages/ContentsPage';
import {FolderPage} from '../../main/pages/FolderPage';
import {HomePage} from '../../main/pages/HomePage';
import {ShareModalPage} from '../../main/pages/ShareModalPage';
import {SpaceSummaryPage} from '../../main/pages/SpaceSummaryPage';
import {CopyFolderModalPage} from '../pages/CopyFolderModalPage';
import {DefaultPermissionsPage} from '../pages/DefaultPermissionsPage';
import {FilesPage} from '../pages/FilesPage';
import {PermissionsPage} from '../pages/PermissionsPage';

const cmsPagesTest = test.extend<{
	assetsPage: AssetsPage;
	contentsPage: ContentsPage;
	copyFolderModalPage: CopyFolderModalPage;
	defaultPermissionsPage: DefaultPermissionsPage;
	filesPage: FilesPage;
	folderPage: FolderPage;
	homePage: HomePage;
	permissionsPage: PermissionsPage;
	shareModalPage: ShareModalPage;
	spaceSummaryPage: SpaceSummaryPage;
}>({
	assetsPage: async ({page}, use) => {
		await use(new AssetsPage(page));
	},
	contentsPage: async ({page}, use) => {
		await use(new ContentsPage(page));
	},
	copyFolderModalPage: async ({page}, use) => {
		await use(new CopyFolderModalPage(page));
	},
	defaultPermissionsPage: async ({page}, use) => {
		await use(new DefaultPermissionsPage(page));
	},
	filesPage: async ({page}, use) => {
		await use(new FilesPage(page));
	},
	folderPage: async ({page}, use) => {
		await use(new FolderPage(page));
	},
	homePage: async ({page}, use) => {
		await use(new HomePage(page));
	},
	permissionsPage: async ({page}, use) => {
		await use(new PermissionsPage(page));
	},
	shareModalPage: async ({page}, use) => {
		await use(new ShareModalPage(page));
	},
	spaceSummaryPage: async ({page}, use) => {
		await use(new SpaceSummaryPage(page));
	},
});

export {cmsPagesTest};
