/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageTemplatesPagesTest} from '../../../fixtures/pageTemplatesPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-57283': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest,
	pageTemplatesPagesTest,
	pagesAdminPagesTest
);

test(
	'Can add a page from a content page template of a connected design library',
	{tag: ['@LPD-105565', '@LPD-105566', '@LPD-106766', '@LPD-106987']},
	async ({
		apiHelpers,
		page,
		pageEditorPage,
		pageTemplatesPage,
		pagesAdminPage,
	}) => {

		// Create a design library and a site, and connect them

		const designLibraryName = getRandomString();

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: designLibraryName,
				settings: {},
				type: 'DesignLibrary',
			});

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			designLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		// Add a fragment to the design library

		const fragmentCollectionName = getRandomString();

		const fragmentCollection =
			await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
				{
					groupId: String(designLibrary.siteId),
					name: fragmentCollectionName,
				}
			);

		const fragmentEntryName = getRandomString();
		const fragmentEntryText = getRandomString();

		await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
			fragmentCollectionId: String(
				fragmentCollection.fragmentCollectionId
			),
			groupId: String(designLibrary.siteId),
			html: `<p>${fragmentEntryText}</p>`,
			name: fragmentEntryName,
		});

		// Add a content page template to the design library

		const layoutPageTemplateCollectionName = getRandomString();

		const layoutPageTemplateCollection =
			await apiHelpers.jsonWebServicesLayoutPageTemplateCollection.addLayoutPageTemplateCollection(
				{
					groupId: String(designLibrary.siteId),
					name: layoutPageTemplateCollectionName,
				}
			);

		const layoutPageTemplateEntryName = getRandomString();

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
			{
				groupId: String(designLibrary.siteId),
				layoutPageTemplateCollectionId:
					layoutPageTemplateCollection.layoutPageTemplateCollectionId,
				name: layoutPageTemplateEntryName,
			}
		);

		// Add a heading and the fragment to the template, and publish it

		await pageTemplatesPage.goto(designLibrary.friendlyURL);

		await pageTemplatesPage.clickAction(
			'Edit',
			layoutPageTemplateEntryName
		);

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.addFragment(
			fragmentCollectionName,
			fragmentEntryName
		);

		await pageEditorPage.publishPage();

		// Check the template set is listed when adding a page to the site

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.gotoSelectTemplates(
			layoutPageTemplateCollectionName
		);

		const navItem = page.locator(
			'.page-template-sets-vertical-nav .nav-item',
			{hasText: layoutPageTemplateCollectionName}
		);

		await expect(navItem).toHaveAttribute(
			'data-title',
			`${designLibraryName} Design Library`
		);

		await expect(
			page.getByLabel(
				`${layoutPageTemplateCollectionName} from ${designLibraryName} Design Library`
			)
		).toBeVisible();

		// Add a page from the template and check its content in the editor

		const layoutName = getRandomString();

		const fragment = page.getByText(fragmentEntryText, {exact: true});
		const heading = page.getByText('Heading Example', {exact: true});

		await pagesAdminPage.addPage({
			name: layoutName,
			template: layoutPageTemplateEntryName,
		});

		await expect(heading).toBeVisible();
		await expect(fragment).toBeVisible();

		await pageEditorPage.goToSidebarTab('Browser');

		await expect(
			page.locator('.page-editor__page-structure__tree-node', {
				hasText: fragmentEntryName,
			})
		).toBeVisible();

		// Publish the page and check its content on the live page

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${site.friendlyUrlPath}/${layoutName.toLowerCase()}`
		);

		await expect(heading).toBeVisible();
		await expect(fragment).toBeVisible();

		// Disconnect the site and check the template set is not listed

		await apiHelpers.headlessAssetLibrary.disconnectSite(
			designLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickNewButtonAndWaitForBlankTemplate();

		await expect(navItem).toBeHidden();
	}
);
