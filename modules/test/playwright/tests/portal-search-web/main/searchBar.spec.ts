/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {searchPageTest} from '../../../fixtures/searchPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {SearchPage} from '../../../pages/portal-search-web/SearchPage';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	isolatedLayoutTest({type: 'portlet'}),
	loginTest(),
	productMenuPageTest,
	usersAndOrganizationsPagesTest,
	pagesPagesTest,
	searchPageTest
);

test.describe('Search Bar with user input', () => {
	test('Alert does not display on search page @LPD-39110', async ({page}) => {
		await test.step('Check that an alert with message does not appear', async () => {
			page.on('dialog', async (dialog) => {
				dialog.accept();

				expect(dialog.message(), 'test').toBeNull();
			});

			await page.goto(
				liferayConfig.environment.baseUrl +
					'/search;%22%3E%3Cscript%3Ealert("test");%3C%2Fscript%3E%3C'
			);
		});

		await test.step('Check that text does not appear in searchbar', async () => {
			await page.goto(
				liferayConfig.environment.baseUrl +
					'/search;%3B%3Cdiv%20id%3D%221%22%3EQuestionable%20Text%3C%2Fdiv%3E'
			);

			await expect(page.getByText(/Questionable Text/)).toBeNull;
		});
	});
});

test.describe('Search Bar directs to correct page', () => {
	test('Disregards manually input currentURL @LPD-68675', async ({
		layout,
		page,
		searchPage,
	}) => {
		await test.step('Add search bar and results portlet to new page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Search Bar', 'Search');

			await searchPage.addPortlet('Search Results', 'Search');
		});

		await test.step('Navigate to page with faulty currentURL', async () => {
			await page.goto(
				'/web/guest' +
					layout.friendlyURL +
					'?currentURL=https%253a//example.com&q=test'
			);
		});

		await test.step('Click on enter in search bar', async () => {
			await searchPage.searchBarInputInMainContent.press('Enter');
		});

		await test.step('Assert the search page does not redirect to faulty currentURL', async () => {
			await expect(page).not.toHaveURL(/^https.\/\/example.com\/\?/);

			await expect(searchPage.searchResults).toBeVisible();
		});
	});

	test('Retains impersonation parameter when suggestions is disabled @LPD-17509', async ({
		apiHelpers,
		layout,
		page,
		searchPage,
		usersAndOrganizationsPage,
	}) => {
		let impersonatePage: Page;
		let impersonateSearchPage: SearchPage;
		let testUser: any;

		await test.step('Create test user', async () => {
			testUser = await apiHelpers.headlessAdminUser.postUserAccount();
		});

		await test.step('Add search bar and results portlet to new page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Search Bar', 'Search');

			await searchPage.addPortlet('Search Results', 'Search');
		});

		await test.step('Disable search suggestions for added search bar', async () => {
			await searchPage.searchBarPortletInMainContent
				.getByLabel('Options')
				.click();

			await searchPage.configurationMenuItem.click();

			await searchPage.selectPortletConfigurationsCheckbox([
				{
					label: 'Enable Suggestions',
					value: false,
				},
			]);

			await searchPage.savePortletConfiguration();
		});

		await test.step('Impersonate as the test user', async () => {
			await usersAndOrganizationsPage.goto();

			await usersAndOrganizationsPage.goToUsers();

			await (
				await usersAndOrganizationsPage.usersTableRowActions(
					testUser.alternateName
				)
			).click();

			const popupPromise = page.waitForEvent('popup');

			await page
				.getByRole('menuitem', {
					name: 'Impersonate User (Opens a new window)',
				})
				.click();

			impersonatePage = await popupPromise;

			impersonateSearchPage = new SearchPage(impersonatePage);
		});

		await test.step('Check that impersonation parameter persists', async () => {
			await impersonatePage
				.getByRole('menuitem', {name: layout.nameCurrentValue})
				.click();

			await impersonateSearchPage.searchKeywordInMainContent('test');

			await expect(
				impersonateSearchPage.searchResultsTotalLabel
			).toHaveText(/Results for test/);

			await expect(impersonatePage).toHaveURL(/doAsUserId=.+/);
			await expect(impersonatePage).toHaveURL(/q=test/);
		});
	});
});
