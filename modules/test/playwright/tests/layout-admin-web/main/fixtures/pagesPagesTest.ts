/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {ContentPageTranslationPage} from '../pages/ContentPageTranslationPage';
import {PageConfigurationPage} from '../pages/PageConfigurationPage';
import {PageTreePage} from '../pages/PageTreePage';
import {SimulationMenuPage} from '../pages/SimulationMenuPage';
import {UtilityPageConfigurationPage} from '../pages/UtilityPageConfigurationPage';
import {UtilityPagesPage} from '../pages/UtilityPagesPage';

const pagesPagesTest = test.extend<{
	contentPageTranslationPage: ContentPageTranslationPage;
	pageConfigurationPage: PageConfigurationPage;
	pageTreePage: PageTreePage;
	simulationMenuPage: SimulationMenuPage;
	utilityPageConfigurationPage: UtilityPageConfigurationPage;
	utilityPagesPage: UtilityPagesPage;
}>({
	contentPageTranslationPage: async ({page}, use) => {
		await use(new ContentPageTranslationPage(page));
	},
	pageConfigurationPage: async ({page}, use) => {
		await use(new PageConfigurationPage(page));
	},
	pageTreePage: async ({page}, use) => {
		await use(new PageTreePage(page));
	},
	simulationMenuPage: async ({page}, use) => {
		await use(new SimulationMenuPage(page));
	},
	utilityPageConfigurationPage: async ({page}, use) => {
		await use(new UtilityPageConfigurationPage(page));
	},
	utilityPagesPage: async ({page}, use) => {
		await use(new UtilityPagesPage(page));
	},
});

export {pagesPagesTest};
