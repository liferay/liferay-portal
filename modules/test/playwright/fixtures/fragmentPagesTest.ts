/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {FragmentEditorPage} from '../pages/fragment-web/FragmentEditorPage';
import {FragmentsPage} from '../pages/fragment-web/FragmentsPage';
import {LocalizationSelectPage} from '../pages/fragment-web/LocalizationSelectPage';

const fragmentsPagesTest = test.extend<{
	fragmentEditorPage: FragmentEditorPage;
	fragmentsPage: FragmentsPage;
	localizationSelectPage: LocalizationSelectPage;
}>({
	fragmentEditorPage: async ({page}, use) => {
		await use(new FragmentEditorPage(page));
	},
	fragmentsPage: async ({page}, use) => {
		await use(new FragmentsPage(page));
	},
	localizationSelectPage: async ({page}, use) => {
		await use(new LocalizationSelectPage(page));
	},
});

export {fragmentsPagesTest};
