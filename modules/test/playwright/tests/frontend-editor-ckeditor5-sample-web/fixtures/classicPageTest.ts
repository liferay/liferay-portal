/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TabName} from '../pages/CKEditor5SamplePage';
import {ClassicPage} from '../pages/ClassicPage';
import {ckeditor5SamplePageTest} from './ckeditor5SamplePageTest';

const classicPageTestFor = (tabName: TabName) =>
	ckeditor5SamplePageTest.extend<{
		classicPage: ClassicPage;
	}>({
		classicPage: async ({ckeditor5SamplePage}, use) => {
			const classicPage: ClassicPage =
				await ckeditor5SamplePage.gotoTab(tabName);

			await use(classicPage);
		},
	});

const advancedClassicPageTest = classicPageTestFor(TabName.ADVANCED_CLASSIC);
const basicClassicPageTest = classicPageTestFor(TabName.BASIC_CLASSIC);
const reactClassicPageTest = classicPageTestFor(TabName.REACT);
const reactPlusCETClassicPageTest = classicPageTestFor(TabName.REACT_PLUS_CET);
const reactPlusCETPremiumClassicPageTest = classicPageTestFor(
	TabName.REACT_PLUS_CET_PREMIUM
);

export {
	advancedClassicPageTest,
	basicClassicPageTest,
	reactClassicPageTest,
	reactPlusCETClassicPageTest,
	reactPlusCETPremiumClassicPageTest,
};
