/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';

const test = mergeTests(
	dataApiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-57283': {enabled: true},
		'LPD-81022': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest
);

test(
	'Can use a library master on a site page and fall back when the library is disconnected',
	{tag: '@LPD-108827'},
	async ({apiHelpers, designLibrariesPage, page, pageEditorPage}) => {

		// Add a design library with a master page, and connect a site

		const designLibraryName = getRandomString();

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary({
				name: designLibraryName,
			});

		const masterPageName = getRandomString();

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
			{
				groupId: String(designLibrary.siteId),
				name: masterPageName,
				type: 'master-layout',
			}
		);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			designLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		// Add a heading to the master page and publish it

		await designLibrariesPage.goToDesignLibrary(designLibraryName);

		await page
			.getByRole('link', {exact: true, name: masterPageName})
			.click();

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.publishPage();

		// Select the library master for a site page

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-paragraph',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToSidebarTab('Page Design Options');

		const masterPageCard = page.getByRole('button', {name: masterPageName});

		await masterPageCard.click();

		await expect(masterPageCard).toHaveClass(/tab-card--active/);

		const heading = page.getByText('Heading Example', {exact: true});

		await expect(heading).toBeVisible();

		// Publish the page and check the master on the live page

		await pageEditorPage.publishPage();

		const layoutURL = `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		await page.goto(layoutURL);

		await expect(heading).toBeVisible();

		// Disconnect the library and check the page falls back to no master

		await apiHelpers.headlessAssetLibrary.disconnectSite(
			designLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		await page.goto(layoutURL);

		await expect(heading).toBeHidden();
		await expect(page.locator('.component-paragraph')).toBeVisible();
	}
);
