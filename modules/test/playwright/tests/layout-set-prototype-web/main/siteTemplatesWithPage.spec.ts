/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {sitesPageTest} from '../../../fixtures/sitesPageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import getRandomString from '../../../utils/getRandomString';
import {reloadUntilVisible} from '../../../utils/reloadUntilVisible';
import {sitesAdminPagesTest} from '../../site-admin-web/main/fixtures/sitesAdminPagesTest';
import {layoutSetPrototypePageTest} from './fixtures/layoutSetPrototypePageTest';
import createSiteTemplate from './utils/createSiteTemplate';

export const test = mergeTests(
	globalMenuPagesTest,
	dataApiHelpersTest,
	layoutSetPrototypePageTest,
	loginTest(),
	pageEditorPagesTest,
	pagesAdminPagesTest,
	productMenuPageTest,
	sitesAdminPagesTest,
	sitesPageTest,
	uiElementsPageTest
);

test(
	'Can not edit Pages created with Site Templates if the checkbox of allowing edition is disabled',
	{tag: ['@LPD-49053', '@LPS-131903', '@LPS-132256']},
	async ({
		apiHelpers,
		page,
		pageEditorPage,
		pagesAdminPage,
		productMenuPage,
		sitesAdminPage,
		sitesPage,
		uiElementsPage,
	}) => {

		// Create a site template with the checkbox of allowing edition (layoutsUpdateable) disabled

		const siteTemplateName: string = 'SiteTemplate-' + getRandomString();

		const layoutSetPrototype = await createSiteTemplate({
			apiHelpers,
			layoutsUpdateable: false,
			page,
			productMenuPage,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		// Create and publish a page in the site template

		const pageName: string = 'Page-' + getRandomString();

		await productMenuPage.goToPages();
		await pagesAdminPage.clickNewButtonAndWaitForBlankTemplate();
		await pagesAdminPage.addPage({
			name: pageName,
		});

		await uiElementsPage.publishButton.click();

		// Create a site using the site template

		await sitesAdminPage.goto();

		const siteName: string = 'Site-' + getRandomString();

		const {externalReferenceCode, siteId} = await sitesPage.createSite({
			isCustom: true,
			siteName,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({id: externalReferenceCode, type: 'site'});

		// Check the Edit button of the dropdown is not visible

		await productMenuPage.goToPages();

		const dropdown = page
			.locator('.miller-columns-item', {
				hasText: pageName,
			})
			.locator('button.dropdown-toggle');

		await reloadUntilVisible({
			myLocator: dropdown,
			page,
		});

		await dropdown.click();

		await expect(page.getByRole('menuitem', {name: 'Edit'})).toBeHidden();

		// Check the Page link opens the view mode

		const href = await page
			.getByLabel(pageName, {exact: true})
			.getAttribute('href');

		await page.goto(href);

		expect(await page.title()).toBe(`${pageName} - ${siteName} - Liferay`);

		await page
			.locator('.control-menu-nav-item')
			.getByRole('button', {name: 'Additional Information'})
			.click();

		await expect(
			page
				.locator('.popover-body')
				.getByText(
					'This page is linked to a site template which does not allow modifications to it.',
					{exact: true}
				)
		).toBeVisible();

		// Check the Edit button is not visible in the view mode

		await expect(
			page
				.locator('.control-menu-nav-item')
				.getByLabel('Edit', {exact: true})
		).toBeHidden();

		// Check going to the edit mode redirects to the view mode

		const layout = await apiHelpers.headlessDelivery.getSitePage(
			pageName,
			siteId
		);

		await pageEditorPage.goto(layout, `/${siteName.toLowerCase()}`);

		expect(await page.title()).toBe(`${pageName} - ${siteName} - Liferay`);
	}
);

test(
	'User cannot create child pages for pages derived from a site template',
	{tag: '@LPD-70284'},
	async ({
		apiHelpers,
		page,
		pagesAdminPage,
		productMenuPage,
		sitesAdminPage,
		sitesPage,
		uiElementsPage,
	}) => {

		// Create site template

		const siteTemplateName = 'SiteTemplate-' + getRandomString();

		const layoutSetPrototype = await createSiteTemplate({
			apiHelpers,
			layoutsUpdateable: false,
			page,
			productMenuPage,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		// Add new template page

		const templatePageName = 'Test Page-' + getRandomString();
		await productMenuPage.goToPages();
		await pagesAdminPage.newTemplatePageButton.click();
		await pagesAdminPage.addPage({name: templatePageName});
		await uiElementsPage.publishButton.click();
		await expect(
			page.getByLabel(templatePageName, {exact: true})
		).toBeVisible();

		// Create site based on that template

		await sitesAdminPage.goto();

		const siteName = 'Site-' + getRandomString();

		const {externalReferenceCode} = await sitesPage.createSite({
			isCustom: true,
			siteName,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({id: externalReferenceCode, type: 'site'});

		// Add new page on created site

		await productMenuPage.goToPages();

		const templatePage = page.getByLabel(templatePageName, {exact: true});

		await reloadUntilVisible({
			myLocator: templatePage,
			page,
		});

		await expect(templatePage).toBeVisible();

		const pageName = 'Test Page-' + getRandomString();
		await pagesAdminPage.clickNewButtonAndWaitForBlankTemplate();
		await pagesAdminPage.addPage({
			name: pageName,
		});

		await uiElementsPage.publishButton.click();

		// Check that for the page created on template child pages cannot be added

		await productMenuPage.goToPages();

		const pageCreatedOnTemplate =
			pagesAdminPage.getPageMenuItem(templatePageName);

		await expect(pageCreatedOnTemplate).toBeVisible();
		await expect(
			pageCreatedOnTemplate.getByLabel('Add Child Page')
		).toHaveCount(0);

		// Check that for the page created on site child pages can be added

		await expect(page.getByLabel(pageName, {exact: true})).toBeVisible();

		const childPageName = 'Child Page-' + getRandomString();

		await pagesAdminPage.createNewPage({
			name: childPageName,
			parent: pageName,
		});

		await productMenuPage.goToPages();

		await expect(async () => {
			await pagesAdminPage.showChildPages(pageName);

			await expect(
				pagesAdminPage.getPageMenuItem(childPageName)
			).toBeVisible({timeout: 1000});
		}).toPass();
	}
);

test(
	'Only one product menu toggle is visible on Site Template pages',
	{tag: '@LPD-86999'},
	async ({apiHelpers, globalMenuPage, layoutSetPrototypePage, page}) => {
		const siteTemplateName: string = 'SiteTemplate-' + getRandomString();

		await globalMenuPage.goToControlPanel('Site Templates');
		await layoutSetPrototypePage.addSiteTemplate(siteTemplateName);
		await globalMenuPage.goToControlPanel('Site Templates');

		let layoutSetPrototypeId;

		try {
			const siteTemplateUrl =
				await layoutSetPrototypePage.getSiteTemplateUrl(
					siteTemplateName
				);

			expect(siteTemplateUrl).toBeTruthy();

			layoutSetPrototypeId =
				siteTemplateUrl!.match(/template-(\d+)/)?.[1];

			expect(layoutSetPrototypeId).toBeTruthy();

			await page.goto(siteTemplateUrl!);

			await expect(
				page.locator('.lexicon-icon-product-menu-open')
			).toHaveCount(1);
		}
		finally {
			if (layoutSetPrototypeId) {
				await apiHelpers.jsonWebServicesLayoutSetPrototype.deleteLayoutSetPrototypes(
					layoutSetPrototypeId
				);
			}
		}
	}
);
