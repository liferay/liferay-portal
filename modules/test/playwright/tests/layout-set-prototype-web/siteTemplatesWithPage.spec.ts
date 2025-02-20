/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../fixtures/productMenuPageTest';
import {sitesPageTest} from '../../fixtures/sitesPageTest';
import {uiElementsPageTest} from '../../fixtures/uiElementsTest';
import getRandomString from '../../utils/getRandomString';
import createSiteTemplate from './utils/createSiteTemplate';

export const test = mergeTests(
	applicationsMenuPageTest,
	dataApiHelpersTest,
	loginTest(),
	pageEditorPagesTest,
	pagesAdminPagesTest,
	productMenuPageTest,
	sitesPageTest,
	uiElementsPageTest
);

test(
	'Can not edit Pages created with Site Templates if the checkbox of allowing edition is disabled',
	{tag: ['@LPD-49053', '@LPS-131903', '@LPS-132256']},
	async ({
		apiHelpers,
		applicationsMenuPage,
		page,
		pageEditorPage,
		pagesAdminPage,
		productMenuPage,
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
		await pagesAdminPage.newButton.click();
		await pagesAdminPage.addPage({
			name: pageName,
		});

		await uiElementsPage.publishButton.click();

		// Create a site using the site template

		await applicationsMenuPage.goToSites();

		const siteName: string = 'Site-' + getRandomString();

		const siteId = await sitesPage.createSiteFromTemplate({
			siteName,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({id: siteId, type: 'site'});

		// Check the Edit button of the dropdown is not visible

		await productMenuPage.goToPages();

		await page.reload();

		const dropdown = page
			.locator('.miller-columns-item', {
				hasText: pageName,
			})
			.locator('button.dropdown-toggle');

		await dropdown.click();

		await expect(page.getByRole('menuitem', {name: 'Edit'})).toBeHidden();

		// Check the Page link opens the view mode

		const href = await page
			.getByLabel(pageName, {exact: true})
			.getAttribute('href');

		await page.goto(href);

		expect(await page.title()).toBe(
			`${pageName} - ${siteName} - Liferay DXP`
		);

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

		expect(await page.title()).toBe(
			`${pageName} - ${siteName} - Liferay DXP`
		);
	}
);
