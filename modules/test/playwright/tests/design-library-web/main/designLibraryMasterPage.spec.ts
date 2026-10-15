/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
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
	}),
	loginTest(),
	pageEditorPagesTest
);

test(
	'Can select a library master when adding a display page template or a content page template',
	{tag: ['@LPD-109440', '@LPD-109441', '@LPD-109442', '@LPD-109443']},
	async ({apiHelpers, designLibrariesPage, page, pageEditorPage}) => {

		// Add a design library with a master page

		const designLibraryName = getRandomString();

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: designLibraryName,
				settings: {},
				type: 'DesignLibrary',
			});

		const masterPageName = getRandomString();

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
			{
				groupId: String(designLibrary.siteId),
				name: masterPageName,
				type: 'master-layout',
			}
		);

		// Add a display page template with the master page

		await designLibrariesPage.goToDesignLibrary(designLibraryName);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'New Display Page Template',
			}),
			trigger: page.getByRole('button', {name: 'Add Asset'}),
		});

		const blankCard = page.getByRole('button', {
			exact: true,
			name: 'Blank',
		});
		const masterPageCard = page.getByRole('button', {
			exact: true,
			name: masterPageName,
		});

		await expect(blankCard).toBeVisible();

		await expect(blankCard.locator('.card-type-asset-icon')).not.toHaveCSS(
			'color',
			'rgb(0, 110, 255)'
		);
		await expect(masterPageCard.locator('.card-type-asset-icon')).toHaveCSS(
			'color',
			'rgb(0, 110, 255)'
		);

		await masterPageCard.click();

		const displayPageTemplateName = getRandomString();

		await page
			.getByLabel('Name', {exact: true})
			.fill(displayPageTemplateName);

		await page
			.getByLabel('Content Type')
			.selectOption({label: 'Web Content Article'});

		await page
			.getByLabel('Subtype')
			.selectOption({label: 'Basic Web Content'});

		await page.getByRole('button', {exact: true, name: 'Save'}).click();

		// Check the editor uses the master page

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await expect(
			page.getByRole('button', {exact: true, name: masterPageName})
		).toHaveClass(/tab-card--active/);

		// Check publishing it goes back to the design library

		await pageEditorPage.publishPage();

		await expect(
			page.getByRole('row').filter({hasText: displayPageTemplateName})
		).toBeVisible();

		// Add a content page template in a new set with the master page

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'New Content Page Template',
			}),
			trigger: page.getByRole('button', {name: 'Add Asset'}),
		});

		await expect(blankCard).toBeVisible();

		await expect(masterPageCard.locator('.card-type-asset-icon')).toHaveCSS(
			'color',
			'rgb(0, 110, 255)'
		);

		await masterPageCard.click();

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

		// Check the editor uses the master page

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await expect(
			page.getByRole('button', {exact: true, name: masterPageName})
		).toHaveClass(/tab-card--active/);

		// Check publishing it goes to the new set

		await pageEditorPage.publishPage();

		await expect(page).toHaveTitle(
			`Page Templates - ${designLibraryName} - Liferay`
		);

		await expect(
			page
				.locator('.card-type-asset')
				.filter({hasText: layoutPageTemplateEntryName})
		).toBeVisible();

		await expect(
			page.getByRole('heading', {name: layoutPageTemplateCollectionName})
		).toBeVisible();

		// Add another content page template from the set with the master page

		await clickAndExpectToBeVisible({
			target: masterPageCard,
			trigger: page.getByRole('link', {name: 'Content Page Template'}),
		});

		await masterPageCard.click();

		await page
			.getByPlaceholder('Name', {exact: true})
			.fill(getRandomString());

		await page.getByRole('button', {exact: true, name: 'Save'}).click();

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await expect(
			page.getByRole('button', {exact: true, name: masterPageName})
		).toHaveClass(/tab-card--active/);
	}
);

test(
	'Can use a library master on a site page and fall back when the library is disconnected',
	{tag: '@LPD-108827'},
	async ({apiHelpers, designLibrariesPage, page, pageEditorPage}) => {

		// Add a design library with a master page, and connect a site

		const designLibraryName = getRandomString();

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: designLibraryName,
				settings: {},
				type: 'DesignLibrary',
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
