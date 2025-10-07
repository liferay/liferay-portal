/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {AssetsPage} from '../pages/AssetsPage';
import {ContentsPage} from '../pages/ContentsPage';
import {EditVocabularyPage} from '../pages/EditVocabularyPage';
import {FolderPage} from '../pages/FolderPage';
import {HomePage} from '../pages/HomePage';
import {InfoPanelPage} from '../pages/InfoPanelPage';
import {PicklistBuilderPage} from '../pages/PicklistBuilderPage';
import {RecycleBinPage} from '../pages/RecycleBinPage';
import {SpaceSummaryPage} from '../pages/SpaceSummaryPage';
import {StructuresPage} from '../pages/StructuresPage';
import {TagsPage} from '../pages/TagsPage';
import {VocabulariesPage} from '../pages/VocabulariesPage';

const cmsPagesTest = test.extend<{
	assetsPage: AssetsPage;
	contentsPage: ContentsPage;
	editVocabularyPage: EditVocabularyPage;
	folderPage: FolderPage;
	homePage: HomePage;
	infoPanelPage: InfoPanelPage;
	picklistBuilderPage: PicklistBuilderPage;
	recycleBinPage: RecycleBinPage;
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
	editVocabularyPage: async ({page}, use) => {
		await use(new EditVocabularyPage(page));
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
	picklistBuilderPage: async ({page}, use) => {
		await use(new PicklistBuilderPage(page));
	},
	recycleBinPage: async ({page}, use) => {
		await use(new RecycleBinPage(page));
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
