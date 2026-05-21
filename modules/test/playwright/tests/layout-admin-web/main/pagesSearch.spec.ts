/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {openProductMenu} from '../../../utils/productMenu';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-76864': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest,
	pageViewModePagesTest,
	workflowPagesTest
);

test(
	'With workflow disabled for Content Pages, checks pages in published status can be searched in the Page Tree and in a Widget Page, and pages in draft status can be searched in the Page Tree but not in a Widget Page',
	{
		tag: '@LPD-36963',
	},
	async ({apiHelpers, page, pagesAdminPage, site, widgetPagePage}) => {

		// Create a content page in draft status

		await pagesAdminPage.goto(site.friendlyUrlPath);

		const draftPageTitle = getRandomString();

		await pagesAdminPage.createNewPage({
			draft: true,
			name: draftPageTitle,
		});

		// Create a content page in published status

		const publishedPageTitle = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: publishedPageTitle,
		});

		// Navigate to a product menu section where pages are not listed, for example fragments

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.fragments}`
		);

		// Open the Product Menu

		await openProductMenu(page);

		const productMenu = page.getByLabel('Product Menu');

		// Open tree if it's not already open

		if (!(await productMenu.locator('.treeview').isVisible())) {
			await productMenu
				.getByRole('button', {exact: true, name: 'Page Tree'})
				.click();

			await productMenu.locator('.treeview').waitFor();
		}

		// Check all pages are listed

		await expect(productMenu.getByText(draftPageTitle)).toBeVisible();
		await expect(productMenu.getByText(publishedPageTitle)).toBeVisible();

		// Enter search term in page tree matching draft page title

		const pageTreeSearchInput = page.getByPlaceholder(
			'Start typing to find a page.'
		);

		await pageTreeSearchInput.fill(draftPageTitle);

		// Check only draft page is listed

		await expect(
			productMenu.getByText(publishedPageTitle)
		).not.toBeVisible();
		await expect(
			productMenu.getByRole('link', {name: draftPageTitle})
		).toBeVisible();
		await expect(productMenu.getByText('Page not found')).not.toBeVisible();

		// Enter search term in page tree matching published page title

		await pageTreeSearchInput.fill(publishedPageTitle);

		// Check only published page is listed

		await expect(
			productMenu.getByRole('link', {name: publishedPageTitle})
		).toBeVisible();
		await expect(productMenu.getByText('Page not found')).not.toBeVisible();
		await expect(page.getByText(draftPageTitle)).not.toBeVisible();

		// Create a widget page with search bar and search results portlets

		await pagesAdminPage.goto(site.friendlyUrlPath);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Search Bar', 'Search');
		await widgetPagePage.addPortlet('Search Results', 'Search');

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		const widgetPageSearchInput = page.getByPlaceholder('Search...');

		// Enter search term in search bar within widget page matching draft page title

		await widgetPageSearchInput.fill(draftPageTitle);
		await page.keyboard.press('Enter');

		// Check no pages are listed

		await expect(
			page.getByText(
				`No results were found that matched the keywords: ${draftPageTitle}`
			)
		).toBeVisible();

		// Enter search term in search bar within widget page matching published page title

		await widgetPageSearchInput.fill(publishedPageTitle);
		await page.keyboard.press('Enter');

		// Check only published page is listed

		await expect(
			page.getByText(`1 Result for ${publishedPageTitle}`)
		).toBeVisible();
	}
);

test(
	'With workflow enabled for Content Pages, checks pages in published status can be searched in the Page Tree and in a Widget Page, and pages in draft, pending and rejected status can be searched in the Page Tree but not in a Widget Page',
	{
		tag: '@LPD-36963',
	},
	async ({
		apiHelpers,
		page,
		pagesAdminPage,
		site,
		widgetPagePage,
		workflowPage,
		workflowTasksPage,
	}) => {

		// Enable Single Approver workflow for Content Pages

		await workflowPage.goto(site.friendlyUrlPath);

		await workflowPage.changeWorkflow('Content Page', 'Single Approver');

		// Create a content page in draft status

		await pagesAdminPage.goto(site.friendlyUrlPath);

		const draftPageTitle = getRandomString();

		await pagesAdminPage.createNewPage({
			draft: true,
			name: draftPageTitle,
		});

		// Create a content page in pending status

		await pagesAdminPage.goto(site.friendlyUrlPath);

		const pendingPageTitle = getRandomString();

		await pagesAdminPage.createNewPage({
			name: pendingPageTitle,
		});

		// Create a content page in published status

		const publishedPageTitle = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: publishedPageTitle,
		});

		// Create a content page in rejected status

		await pagesAdminPage.goto(site.friendlyUrlPath);

		const rejectedPageTitle = getRandomString();

		await pagesAdminPage.createNewPage({
			name: rejectedPageTitle,
		});

		await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

		await workflowTasksPage.assignToMe(rejectedPageTitle);

		await workflowTasksPage.reject(rejectedPageTitle);

		// Navigate to a product menu section where pages are not listed, for example fragments

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.fragments}`
		);

		// Open the Product Menu

		await openProductMenu(page);

		const productMenu = page.getByLabel('Product Menu');

		// Open tree if it's not already open

		if (!(await productMenu.locator('.treeview').isVisible())) {
			await productMenu
				.getByRole('button', {exact: true, name: 'Page Tree'})
				.click();

			await productMenu.locator('.treeview').waitFor();
		}

		// Check all pages are listed

		await expect(
			productMenu.getByText(`${draftPageTitle}*`, {exact: true})
		).toBeVisible();
		await expect(
			productMenu.getByText(`${pendingPageTitle}*`, {exact: true})
		).toBeVisible();
		await expect(productMenu.getByText(publishedPageTitle)).toBeVisible();
		await expect(
			productMenu.getByText(`${rejectedPageTitle}*`, {exact: true})
		).toBeVisible();

		const pageTreeSearchInput = productMenu.getByPlaceholder(
			'Start typing to find a page.'
		);

		// Enter search term matching draft page title

		await pageTreeSearchInput.fill(draftPageTitle);

		// Check only draft page is listed

		await expect(
			productMenu.getByRole('link', {name: draftPageTitle})
		).toBeVisible();
		await expect(page.getByText('Page not found')).not.toBeVisible();
		await expect(productMenu.getByText(pendingPageTitle)).not.toBeVisible();
		await expect(
			productMenu.getByText(publishedPageTitle)
		).not.toBeVisible();
		await expect(
			productMenu.getByText(rejectedPageTitle)
		).not.toBeVisible();

		// Enter search term matching pending page title

		await pageTreeSearchInput.fill(pendingPageTitle);

		// Check only pending page is listed

		await expect(
			productMenu.getByRole('link', {name: pendingPageTitle})
		).toBeVisible();
		await expect(productMenu.getByText('Page not found')).not.toBeVisible();
		await expect(productMenu.getByText(draftPageTitle)).not.toBeVisible();
		await expect(
			productMenu.getByText(publishedPageTitle)
		).not.toBeVisible();
		await expect(
			productMenu.getByText(rejectedPageTitle)
		).not.toBeVisible();

		// Enter search term matching published page title

		await pageTreeSearchInput.fill(publishedPageTitle);

		// Check only published page is listed

		await expect(
			productMenu.getByRole('link', {name: publishedPageTitle})
		).toBeVisible();
		await expect(productMenu.getByText('Page not found')).not.toBeVisible();
		await expect(productMenu.getByText(draftPageTitle)).not.toBeVisible();
		await expect(productMenu.getByText(pendingPageTitle)).not.toBeVisible();
		await expect(
			productMenu.getByText(rejectedPageTitle)
		).not.toBeVisible();

		// Enter search term matching rejected page title

		await pageTreeSearchInput.fill(rejectedPageTitle);

		// Check only rejected page is listed

		await expect(
			productMenu.getByRole('link', {name: rejectedPageTitle})
		).toBeVisible();
		await expect(productMenu.getByText('Page not found')).not.toBeVisible();
		await expect(productMenu.getByText(draftPageTitle)).not.toBeVisible();
		await expect(productMenu.getByText(pendingPageTitle)).not.toBeVisible();
		await expect(
			productMenu.getByText(publishedPageTitle)
		).not.toBeVisible();

		// Create a widget page with search bar and search results portlets

		await pagesAdminPage.goto(site.friendlyUrlPath);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Search Bar', 'Search');
		await widgetPagePage.addPortlet('Search Results', 'Search');

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		const widgetPageSearchInput = page.getByPlaceholder('Search...');

		// Enter search term in search bar within widget page matching draft page title

		await widgetPageSearchInput.fill(draftPageTitle);
		await page.keyboard.press('Enter');

		// Check no pages are listed

		await expect(
			page.getByText(
				`No results were found that matched the keywords: ${draftPageTitle}`
			)
		).toBeVisible();

		// Enter search term in search bar within widget page matching pending page title

		await widgetPageSearchInput.fill(pendingPageTitle);
		await page.keyboard.press('Enter');

		// Check no pages are listed

		await expect(
			page.getByText(
				`No results were found that matched the keywords: ${pendingPageTitle}`
			)
		).toBeVisible();

		// Enter search term in search bar within widget page matching published page title

		await widgetPageSearchInput.fill(publishedPageTitle);
		await page.keyboard.press('Enter');

		// Check only published page is listed

		await expect(
			page.getByText(`1 Result for ${publishedPageTitle}`)
		).toBeVisible();

		// Enter search term in search bar within widget page matching rejected page title

		await widgetPageSearchInput.fill(rejectedPageTitle);
		await page.keyboard.press('Enter');

		// Check no pages are listed

		await expect(
			page.getByText(
				`No results were found that matched the keywords: ${rejectedPageTitle}`
			)
		).toBeVisible();

		// Disable workflow for Content Pages

		await workflowPage.goto(site.friendlyUrlPath);

		await workflowPage.changeWorkflow('Content Page', 'No Workflow', {
			disable: true,
		});
	}
);
