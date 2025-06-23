/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {sitesPageTest} from '../../../fixtures/sitesPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {sitesAdminPagesTest} from '../../site-admin-web/main/fixtures/sitesAdminPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	loginTest(),
	pageEditorPagesTest,
	pageViewModePagesTest,
	pagesAdminPagesTest,
	productMenuPageTest,
	sitesAdminPagesTest,
	sitesPageTest,
	usersAndOrganizationsPagesTest,
	virtualInstancesPagesTest
);

test.afterEach(async ({sitesAdminPage, sitesPage}) => {
	await sitesAdminPage.goto();

	await sitesPage.deleteAllSites();
});

test('Smoke', async ({
	apiHelpers,
	applicationsMenuPage,
	page,
	pageEditorPage,
	pagesAdminPage,
	productMenuPage,
	siteConfigurationDetailsPage,
	sitesPage,
	widgetPagePage,
}) => {
	let siteName: string;
	let pageNames: string[];
	let contentPageName: string;

	await test.step('Given the admin user agrees to the terms of use and answers the reminder query', async () => {
		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'test@liferay.com'
			);

		await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
		await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);
	});

	await test.step('When the page is reloaded', async () => {
		await page.reload();
	});

	await test.step('Then the welcome page elements should be visible', async () => {
		await expect(page.getByText('Welcome to Liferay')).toBeVisible();

		const treeImage = page.locator('img[src*="tree.png"]');
		await expect(treeImage).toBeVisible();
	});

	await test.step('When the admin user creates a new blank site', async () => {
		await applicationsMenuPage.goToSites();

		siteName = getRandomString();

		await sitesPage.createSite({
			isCustom: false,
			siteName,
			templateName: 'Blank Site',
		});

		await siteConfigurationDetailsPage.selectMembership('Open');

		await siteConfigurationDetailsPage.saveButton.click();
	});

	await test.step('Then the created site should be visible in the sites page with the correct membership, status, and options', async () => {
		await applicationsMenuPage.goToSites();

		const row = page.getByRole('row').filter({hasText: siteName});

		const siteTableCheckbox = row.getByRole('checkbox');
		await expect(siteTableCheckbox).toBeVisible();

		const siteTableType = row.locator('[class*=membership-type]');
		await expect(siteTableType).toContainText('Open');

		const siteTableStatus = row.locator('[class*=active]');
		await expect(siteTableStatus).toContainText('Yes');

		const siteTableDropdown = row.getByLabel('Show Actions');
		await expect(siteTableDropdown).toBeVisible();
	});

	await test.step('When the admin user creates three widget pages for the site', async () => {
		await applicationsMenuPage.goToSite(siteName);

		await productMenuPage.goToPages();

		await page.waitForSelector('h1.taglib-empty-result-message-title', {
			state: 'visible',
		});

		await page.getByText('New', {exact: true}).click();

		pageNames = [getRandomString(), getRandomString(), getRandomString()];

		for (const pageName of pageNames) {
			await pagesAdminPage.addPage({
				name: pageName,
				template: 'Widget Page',
			});

			await page.goBack();
		}
	});

	await test.step('And the admin user is on the first widget page', async () => {
		await page.goto(`/web/${siteName}/${pageNames[0]}`);
	});

	await test.step('And the admin user adds a menu display portlet', async () => {
		await widgetPagePage.addPortlet('Menu Display');
	});

	await test.step('Then the portlet body should display links to the created widget pages', async () => {
		const portletTitle = page.getByRole('heading', {name: 'Menu Display'});
		await expect(portletTitle).toBeVisible();

		const portletBody = page
			.locator('[class*=portlet-content]')
			.filter({has: portletTitle})
			.locator('[class*=portlet-body]');

		for (const pageName of pageNames) {
			const portletBodyContent = portletBody.locator(
				`a[href*="${pageName}"]`
			);
			await expect(portletBodyContent).toBeVisible();
		}
	});

	await test.step(`Given the admin user is on the pages admin page for site ${siteName}`, async () => {
		await pagesAdminPage.goto(`/${siteName}`);
	});

	await test.step('When the admin creates a content page and adds a heading fragment to it', async () => {
		contentPageName = getRandomString();

		await pagesAdminPage.createNewPage({
			draft: true,
			name: contentPageName,
			template: 'Blank',
		});

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.publishPage();
	});

	await test.step('Then the heading fragment added to the page should be visible', async () => {
		await page.goto(`/web/${siteName}/${contentPageName}`);

		const headingFragment = page.getByRole('heading', {
			name: 'Heading Example',
		});
		await expect(headingFragment).toBeVisible();
	});

	await test.step('When the admin opens the product menu and accesses the web content portlet', async () => {
		await applicationsMenuPage.goToSite(siteName);

		await productMenuPage.openProductMenuIfClosed();

		await productMenuPage.goToWebContent();
	});

	await test.step('Then the portlet title should be visible', async () => {
		const siteAdministrationPortletTitle = page.getByRole('heading', {
			name: 'Web Content',
		});
		await expect(siteAdministrationPortletTitle).toBeVisible();
	});

	await test.step('Given the admin user is on the second widget page', async () => {
		await page.goto(`/web/${siteName}/${pageNames[1]}`);
	});

	await test.step('When the admin hides the toggle controls button', async () => {
		await widgetPagePage.toggleControls('hidden');
	});

	await test.step('Then the toggle controls button should display the hidden icon', async () => {
		expect(
			await widgetPagePage.toggleControlsButton
				.locator('svg')
				.evaluate((element) =>
					element.classList.contains('lexicon-icon-hidden')
				)
		).toBeTruthy();
	});
});
