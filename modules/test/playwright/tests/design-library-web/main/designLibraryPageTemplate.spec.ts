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
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';

const test = mergeTests(
	dataApiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-57283': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest,
	pageTemplatesPagesTest,
	pagesAdminPagesTest
);

test(
	'Can open the page template sets and add a page from a template',
	{
		tag: [
			'@LPD-105565',
			'@LPD-105566',
			'@LPD-106766',
			'@LPD-106987',
			'@LPD-107947',
		],
	},
	async ({
		apiHelpers,
		designLibrariesPage,
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

		// Add an empty page template set

		const emptyLayoutPageTemplateCollectionName = getRandomString();

		await apiHelpers.jsonWebServicesLayoutPageTemplateCollection.addLayoutPageTemplateCollection(
			{
				groupId: String(designLibrary.siteId),
				name: emptyLayoutPageTemplateCollectionName,
			}
		);

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

		// Check each template set opens its own content

		await designLibrariesPage.goToPageTemplateCollection(
			designLibraryName,
			emptyLayoutPageTemplateCollectionName
		);

		await expect(
			page.getByRole('heading', {
				name: emptyLayoutPageTemplateCollectionName,
			})
		).toBeVisible();

		await expect(
			page.getByText('There are no page templates.')
		).toBeVisible();

		await designLibrariesPage.goToPageTemplateCollection(
			designLibraryName,
			layoutPageTemplateCollectionName
		);

		await expect(
			page.getByRole('heading', {name: layoutPageTemplateCollectionName})
		).toBeVisible();

		await expect(
			page
				.locator('.card-type-asset')
				.filter({hasText: layoutPageTemplateEntryName})
		).toBeVisible();

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

		// Add a heading and the fragment to the template, and publish it

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

test(
	'Can add a content page template to a design library and go back from its configuration',
	{tag: '@LPD-107609'},
	async ({apiHelpers, designLibrariesPage, page, pageEditorPage}) => {

		// Add a content page template in a new set from the design library

		const designLibraryName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: designLibraryName,
			settings: {},
			type: 'DesignLibrary',
		});

		await designLibrariesPage.goToDesignLibrary(designLibraryName);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'New Content Page Template',
			}),
			trigger: page.getByRole('button', {exact: true, name: 'New'}),
		});

		const addPageTemplateModal = page.getByRole('dialog', {
			name: 'Add Page Template',
		});

		const layoutPageTemplateEntryName = getRandomString();

		await addPageTemplateModal
			.getByLabel('Page Template Name')
			.fill(layoutPageTemplateEntryName);

		const layoutPageTemplateCollectionName = getRandomString();

		await addPageTemplateModal
			.getByLabel('Page Template Set Name')
			.fill(layoutPageTemplateCollectionName);

		await addPageTemplateModal
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		// Check publishing it goes to the set

		await pageEditorPage.publishPage();

		await expect(page).toHaveTitle(
			`Page Templates - ${designLibraryName} - Liferay`
		);

		const card = page
			.locator('.card-type-asset')
			.filter({hasText: layoutPageTemplateEntryName});

		await expect(card).toBeVisible();

		// Open the template from its card in the set

		await designLibrariesPage.goToDesignLibrary(designLibraryName);

		await page
			.getByRole('link', {
				exact: true,
				name: layoutPageTemplateCollectionName,
			})
			.click();

		await card.locator('.card-title').click();

		// Check the Page Design Options Cancel button goes back to the editor

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await page
			.getByTitle('More Page Design Options', {exact: true})
			.click();

		await page.waitForURL(/edit_layout/);

		await page.getByRole('button', {name: 'Cancel'}).click();

		await expect(page).toHaveTitle(
			`${layoutPageTemplateEntryName} - ${designLibraryName} - Liferay (Editing)`
		);

		// Check the page editor back button goes to the set

		await page.getByRole('link', {name: 'Go to Page Templates'}).click();

		await expect(page).toHaveTitle(
			`Page Templates - ${designLibraryName} - Liferay`
		);

		await expect(card).toBeVisible();
	}
);
