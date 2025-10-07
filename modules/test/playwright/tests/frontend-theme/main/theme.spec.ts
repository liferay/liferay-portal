/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import getRandomString from '../../../utils/getRandomString';
import {performLogout} from '../../../utils/performLogin';

export const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest
);

test(
	'Check accessibility of content page based on custom master page.',
	{tag: '@LPD-41441'},
	async ({apiHelpers, masterPagesPage, page, pageEditorPage, site}) => {
		const layoutPageTemplateEntryName = getRandomString();

		const masterPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
				{
					groupId: site.id,
					name: layoutPageTemplateEntryName,
					type: 'master-layout',
				}
			);

		// Add header and footer fragments to master page

		await masterPagesPage.goto(site.friendlyUrlPath);

		await masterPagesPage.editMaster(layoutPageTemplateEntryName);

		await pageEditorPage.addFragment('Navigation Bars', 'Header Dark');

		await pageEditorPage.addFragment('Footers', 'Footer Nav Dark');

		await pageEditorPage.publishPage();

		// Create a layout

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			masterLayoutPlid: masterPage.plid,
			options: {type: 'content'},
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.publishPage();

		// Assert accessibility in view mode

		await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

		await checkAccessibility({bestPractices: true, page});
	}
);

test(
	'Check accessibility of content page based on blank master page.',
	{tag: '@LPD-41441'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a layout

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.publishPage();

		// Assert accessibility in view mode

		await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

		await checkAccessibility({bestPractices: true, page});
	}
);

test(
	'Check accessibility of widget page.',
	{tag: '@LPD-41441'},
	async ({apiHelpers, page, site}) => {

		// Create a "Search" layout to avoid showing search bar is not visible message which includes an accessibility error.

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: 'Search',
		});

		// Create a layout

		const layoutTitle = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutTitle,
		});

		// Assert accessibility in view mode

		await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

		await checkAccessibility({bestPractices: true, page});
	}
);

test(
	'Check accessibility for a portal page.',
	{tag: '@LPD-55257'},
	async ({page}) => {
		await page.goto('/');

		await checkAccessibility({page});

		await performLogout(page);

		await checkAccessibility({page});
	}
);
