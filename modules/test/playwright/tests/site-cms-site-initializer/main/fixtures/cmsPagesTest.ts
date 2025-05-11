/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {EditVocabularyPage} from '../pages/EditVocabularyPage';
import {FilesPage} from '../pages/FilesPage';
import {PicklistBuilderPage} from '../pages/PicklistBuilderPage';
import {StructureBuilderPage} from '../pages/StructureBuilderPage';
import {StructuresPage} from '../pages/StructuresPage';
import {TagsPage} from '../pages/TagsPage';
import {VocabulariesPage} from '../pages/VocabulariesPage';

const cmsPagesTest = test.extend<{
	editVocabularyPage: EditVocabularyPage;
	filesPage: FilesPage;
	picklistBuilderPage: PicklistBuilderPage;
	structureBuilderPage: StructureBuilderPage;
	structuresPage: StructuresPage;
	tagsPage: TagsPage;
	vocabulariesPage: VocabulariesPage;
}>({
	editVocabularyPage: async ({page}, use) => {
		await use(new EditVocabularyPage(page));
	},
	filesPage: async ({page}, use) => {
		await use(new FilesPage(page));
	},
	picklistBuilderPage: async ({page}, use) => {
		await use(new PicklistBuilderPage(page));
	},
	structureBuilderPage: async ({page}, use) => {
		await use(new StructureBuilderPage(page));
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
