/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {AssetsPage} from '../pages/AssetsPage';
import {ContentsPage} from '../pages/ContentsPage';
import {DataSetPage} from '../pages/DataSetPage';
import {EditVocabularyPage} from '../pages/EditVocabularyPage';
import {FindAndReplacePage} from '../pages/FindAndReplacePage';
import {FolderPage} from '../pages/FolderPage';
import {HomePage} from '../pages/HomePage';
import {InfoPanelPage} from '../pages/InfoPanelPage';
import {PerformanceDashboardPage} from '../pages/PerformanceDashboardPage';
import {PicklistBuilderPage} from '../pages/PicklistBuilderPage';
import {RecycleBinPage} from '../pages/RecycleBinPage';
import {ShareModalPage} from '../pages/ShareModalPage';
import {SharedWithMePage} from '../pages/SharedWithMePage';
import {SpaceSummaryPage} from '../pages/SpaceSummaryPage';
import {StructuresPage} from '../pages/StructuresPage';
import {TagsPage} from '../pages/TagsPage';
import {VocabulariesPage} from '../pages/VocabulariesPage';

const cmsPagesTest = test.extend<{
	assetsPage: AssetsPage;
	contentsPage: ContentsPage;
	dataSetPage: DataSetPage;
	editVocabularyPage: EditVocabularyPage;
	findAndReplacePage: FindAndReplacePage;
	folderPage: FolderPage;
	homePage: HomePage;
	infoPanelPage: InfoPanelPage;
	performanceDashboardPage: PerformanceDashboardPage;
	picklistBuilderPage: PicklistBuilderPage;
	recycleBinPage: RecycleBinPage;
	shareModalPage: ShareModalPage;
	sharedWithMePage: SharedWithMePage;
	spaceSummaryPage: SpaceSummaryPage;
	structuresPage: StructuresPage;
	tagsPage: TagsPage;
	vocabulariesPage: VocabulariesPage;
}>({
	assetsPage: async ({page}, use) => {
		await use(new AssetsPage(page));
	},
	contentsPage: async ({page}, use) => {
		await use(new ContentsPage(page));
	},
	dataSetPage: async ({page}, use) => {
		await use(new DataSetPage(page));
	},
	editVocabularyPage: async ({page}, use) => {
		await use(new EditVocabularyPage(page));
	},
	findAndReplacePage: async ({page}, use) => {
		await use(new FindAndReplacePage(page));
	},
	folderPage: async ({page}, use) => {
		await use(new FolderPage(page));
	},
	homePage: async ({page}, use) => {
		await use(new HomePage(page));
	},
	infoPanelPage: async ({page}, use) => {
		await use(new InfoPanelPage(page));
	},
	performanceDashboardPage: async ({page}, use) => {
		await use(new PerformanceDashboardPage(page));
	},
	picklistBuilderPage: async ({page}, use) => {
		await use(new PicklistBuilderPage(page));
	},
	recycleBinPage: async ({page}, use) => {
		await use(new RecycleBinPage(page));
	},
	shareModalPage: async ({page}, use) => {
		await use(new ShareModalPage(page));
	},
	sharedWithMePage: async ({page}, use) => {
		await use(new SharedWithMePage(page));
	},
	spaceSummaryPage: async ({page}, use) => {
		await use(new SpaceSummaryPage(page));
	},
	structuresPage: async ({page}, use) => {
		await use(new StructuresPage(page));
	},
	tagsPage: async ({page}, use) => {
		await use(new TagsPage(page));
	},
	vocabulariesPage: async ({page}, use) => {
		await use(new VocabulariesPage(page));
	},
});

export {cmsPagesTest};
