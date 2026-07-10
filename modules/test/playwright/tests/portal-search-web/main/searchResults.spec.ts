/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {searchPageTest} from '../../../fixtures/searchPageTest';
import getRandomString from '../../../utils/getRandomString';
import {hoverAndExpectToBeVisible} from '../../../utils/hoverAndExpectToBeVisible';
import {reloadUntilVisible} from '../../../utils/reloadUntilVisible';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

export const test = mergeTests(
	isolatedSiteTest,
	loginTest(),
	dataApiHelpersTest,
	pageEditorPagesTest,
	searchPageTest
);

test.describe('Result Detail Navigation', () => {
	test('Navigates to the asset edit page from the search result details @LPS-112599', async ({
		apiHelpers,
		page,
		searchPage,
		site,
	}) => {
		const content = getRandomString();
		const title = getRandomString();

		let siteLayout: Layout;

		await test.step('Create a basic web content article', async () => {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content,
				ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
				groupId: site.id,
				titleMap: {en_US: title},
			});
		});

		await test.step('Build a search page on the site', async () => {
			siteLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {type: 'portlet'},
				title: getRandomString(),
			});

			await page.goto(
				`/web${site.friendlyUrlPath}${siteLayout.friendlyURL}`
			);

			await searchPage.addPortlet('Search Bar', 'Search');

			await searchPage.addPortlet('Search Results', 'Search');
		});

		const resultLink = searchPage.searchResults.getByRole('link', {
			name: title,
		});

		await test.step('Search for the article', async () => {
			await expect(async () => {
				await page.goto(
					`/web${site.friendlyUrlPath}${siteLayout.friendlyURL}`
				);

				await searchPage.searchKeywordInMainContent(title);

				await expect(resultLink).toBeVisible({timeout: 2000});
			}).toPass({timeout: 30000});
		});

		await test.step('Open the result details in context', async () => {
			await resultLink.click();

			await expect(
				searchPage.searchResults.locator('.asset-title')
			).toHaveText(title);
		});

		await test.step('Click Edit and assert the article edit page loads with its content', async () => {
			await hoverAndExpectToBeVisible({
				autoClick: true,
				target: searchPage.searchResults.getByRole('link', {
					name: /edit/i,
				}),
				trigger: searchPage.searchResults
					.getByText(title, {exact: true})
					.first(),
			});

			await expect(page).toHaveURL(/mvcRenderCommandName.*edit_article/);

			await expect(
				page
					.getByRole('textbox', {name: 'Rich Text Editor. Editing'})
					.filter({hasText: content})
			).toBeVisible();
		});
	});
});

test.describe('Results Display', () => {
	test('Search results image thumbnail for PDFs render correctly @LPD-71503', async ({
		apiHelpers,
		page,
		searchPage,
		site,
	}) => {
		let siteLayout: Layout;

		await test.step('Upload a PDF to documents and media', async () => {
			await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					path.join(__dirname, '/dependencies/test.pdf')
				),
				{
					description: '',
					fileName: 'test.pdf',
					title: 'Sample Document',
				}
			);
		});

		await test.step('Create a portlet page associated to site', async () => {
			siteLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {type: 'portlet'},
				title: getRandomString(),
			});
		});

		await test.step('Navigate to the site page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${siteLayout.friendlyURL}`
			);
		});

		await test.step('Add search bar and results portlet to new page', async () => {
			await searchPage.addPortlet('Search Bar', 'Search');

			await searchPage.addPortlet('Search Results', 'Search');
		});

		await test.step('Search for "test" and view PDF in results', async () => {
			await searchPage.searchKeywordInMainContent('test');

			await expect(
				searchPage.searchResults.getByText('Sample Document')
			).toBeVisible();
		});

		await test.step('Verify correct alt text for thumbnail image', async () => {

			// Reload and wait for PDF image generation

			await reloadUntilVisible({
				beforeReload: () => page.waitForTimeout(1000),
				myLocator: searchPage.searchResults.locator('img.sticker-img'),
				page,
			});

			await expect(
				searchPage.searchResults.getByAltText(
					'Sample Document Thumbnail'
				)
			).toBeVisible();
		});
	});
});

test.describe('Multiple Widgets on a Page', () => {
	test('Change the pagination delta of a search results widget on a page with two search results widgets @LPD-36512', async ({
		apiHelpers,
		page,
		searchPage,
		site,
	}) => {
		let siteLayout: Layout;

		await test.step('Create web content for search results', async () => {
			const basicWebContentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			for (let count = 0; count < 21; count++) {
				await apiHelpers.jsonWebServicesJournal.addWebContent({
					ddmStructureId: basicWebContentStructureId,
					groupId: site.id,
					titleMap: {en_US: `Test Web Content ${count}`},
				});
			}
		});

		await test.step('Create a portlet page associated to site', async () => {
			siteLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {type: 'portlet'},
				title: getRandomString(),
			});
		});

		await test.step('Navigate to the site page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${siteLayout.friendlyURL}`
			);
		});

		await test.step('Add Search Results and Search Options portlets', async () => {
			await searchPage.addPortlet('Search Results', 'Search');

			await searchPage.addPortlet('Search Options', 'Search');

			await searchPage.addPortlet('Search Results', 'Search');

			await searchPage.addPortlet('Search Options', 'Search');
		});

		await test.step('Configure Search Results portlets', async () => {
			await searchPage.openSearchPortletConfiguration('Search Results');

			await searchPage.fillPortletConfigurationsInput([
				{
					label: 'Pagination Delta Parameter',
					value: 'delta1',
				},
				{
					label: 'Pagination Start Parameter',
					value: 'start1',
				},
				{
					label: 'Federated Search Key',
					value: 'federatedSearchKey1',
				},
			]);

			await searchPage.savePortletConfiguration();

			await searchPage.openSearchPortletConfiguration(
				'Search Results',
				1
			);

			await searchPage.fillPortletConfigurationsInput([
				{
					label: 'Federated Search Key',
					value: 'federatedSearchKey2',
				},
			]);

			await searchPage.savePortletConfiguration();
		});

		await test.step('Configure Search Options Configuration portlets', async () => {
			await searchPage.openSearchPortletConfiguration('Search Options');

			await searchPage.selectPortletConfigurationsCheckbox([
				{
					label: 'Allow Empty Searches',
					value: true,
				},
			]);
			await searchPage.fillPortletConfigurationsInput([
				{
					label: 'Federated Search Key',
					value: 'federatedSearchKey1',
				},
			]);

			await searchPage.savePortletConfiguration();

			await searchPage.openSearchPortletConfiguration(
				'Search Options',
				1
			);

			await searchPage.selectPortletConfigurationsCheckbox([
				{
					label: 'Allow Empty Searches',
					value: true,
				},
			]);
			await searchPage.fillPortletConfigurationsInput([
				{
					label: 'Federated Search Key',
					value: 'federatedSearchKey2',
				},
			]);

			await searchPage.savePortletConfiguration();
		});

		await test.step('Check if the pagination delta of a Search Results widget persists when changing that of another widget', async () => {
			await page.reload();

			await searchPage.selectPaginationItemsPerPage(40);

			await expect(
				searchPage.searchResultsPaginationItemsPerPageToggle.nth(1)
			).toHaveText('20 Entries');

			await searchPage.selectPaginationItemsPerPage(60, 1);

			await expect(
				searchPage.searchResultsPaginationItemsPerPageToggle.nth(0)
			).toHaveText('40 Entries');
		});

		await test.step('Remove the federatedSearchKey of one Search Results and Search Options widget', async () => {
			await searchPage.openSearchPortletConfiguration('Search Results');

			await searchPage.fillPortletConfigurationsInput([
				{
					label: 'Federated Search Key',
					value: '',
				},
			]);

			await searchPage.savePortletConfiguration();

			await searchPage.openSearchPortletConfiguration('Search Options');

			await searchPage.fillPortletConfigurationsInput([
				{
					label: 'Federated Search Key',
					value: '',
				},
			]);

			await searchPage.savePortletConfiguration();
		});

		await test.step('Navigate to the site page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${siteLayout.friendlyURL}`
			);
		});

		await test.step('Check if the pagination delta of a search results widget persists when changing that of another widget', async () => {
			await searchPage.selectPaginationItemsPerPage(60);

			await expect(
				searchPage.searchResultsPaginationItemsPerPageToggle.nth(1)
			).toHaveText('20 Entries');

			await searchPage.selectPaginationItemsPerPage(40, 1);

			await expect(
				searchPage.searchResultsPaginationItemsPerPageToggle.nth(0)
			).toHaveText('60 Entries');
		});
	});
});

test.describe('Search Paginator', () => {
	test('Retains items per page after new keyword search @LPD-19994', async ({
		apiHelpers,
		page,
		searchPage,
		site,
	}) => {
		let siteLayout: Layout;

		await test.step('Create web content for search results', async () => {
			const basicWebContentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			for (let count = 0; count < 21; count++) {
				await apiHelpers.jsonWebServicesJournal.addWebContent({
					ddmStructureId: basicWebContentStructureId,
					groupId: site.id,
					titleMap: {en_US: `Test Web Content ${count}`},
				});
			}
		});

		await test.step('Create a portlet page associated to site', async () => {
			siteLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {type: 'portlet'},
				title: getRandomString(),
			});
		});

		await test.step('Navigate to the site page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${siteLayout.friendlyURL}`
			);
		});

		await test.step('Add search bar and results portlet to new page', async () => {
			await searchPage.addPortlet('Search Bar', 'Search');

			await searchPage.addPortlet('Search Results', 'Search');
		});

		await test.step('Perform new search', async () => {
			await searchPage.searchKeywordInMainContent('test');

			await expect(searchPage.searchResultsTotalLabel).toHaveText(
				/\d+ Results for test/
			);
		});

		await test.step('Change pagination items per page and page number', async () => {
			await searchPage.selectPaginationItemsPerPage(20);

			await searchPage.selectPaginationPageNumber(2);
		});

		await test.step('Perform new search with different keyword', async () => {
			await searchPage.searchKeywordInMainContent('web');

			await expect(searchPage.searchResultsTotalLabel).toHaveText(
				/\d+ Results for web/
			);
		});

		await test.step('Verify that page number is reset but items per page is not', async () => {
			await expect(
				searchPage.searchResultsPaginationItemsPerPageToggle
			).toHaveText(/20 Entries/);

			await expect(
				searchPage.searchResultsPaginationBar.getByText('1').first()
			).toHaveAttribute('aria-current', 'page');

			await expect(
				searchPage.searchResultsPaginationDescription
			).toHaveText(/Showing 1 to 20 of \d+ entries./);
		});
	});

	test('Pagination is not available when currentURL is faulty @LPD-83772', async ({
		apiHelpers,
		page,
		searchPage,
		site,
	}) => {
		let siteLayout: Layout;

		await test.step('Create web content for search results', async () => {
			const basicWebContentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			for (let count = 0; count < 21; count++) {
				await apiHelpers.jsonWebServicesJournal.addWebContent({
					ddmStructureId: basicWebContentStructureId,
					groupId: site.id,
					titleMap: {en_US: `Test Web Content ${count}`},
				});
			}
		});

		await test.step('Create a portlet page associated to site', async () => {
			siteLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {type: 'portlet'},
				title: getRandomString(),
			});
		});

		await test.step('Navigate to the site page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${siteLayout.friendlyURL}`
			);
		});

		await test.step('Add search bar and results portlet to new page', async () => {
			await searchPage.addPortlet('Search Bar', 'Search');

			await searchPage.addPortlet('Search Results', 'Search');
		});

		await test.step('Perform new search and verify pagination', async () => {
			await searchPage.searchKeywordInMainContent('test');

			await expect(searchPage.searchResultsTotalLabel).toHaveText(
				/\d+ Results for test/
			);

			await expect(searchPage.searchResultsPaginationBar).toBeVisible();
		});

		await test.step('Navigate to search page with q, currentURL, and delta parameter', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${siteLayout.friendlyURL}?q=test&currentURL=https://www.google.com&delta=5`
			);
		});

		await test.step('Verify search results are available but pagination link has null href', async () => {
			await expect(searchPage.searchResults).toBeVisible();

			await searchPage.searchResultsPaginationItemsPerPageToggle.click();

			await expect(
				searchPage.searchResultsPaginationItemsPerPageDropdown
					.locator('a.dropdown-item')
					.first()
			).toHaveAttribute('href', /null/);
		});
	});
});

test.describe('URL Parameters Are Sorted', () => {
	test('Facet selection, modified range, pagination, and sort links on the page have alphabetically sorted URL parameters @LPD-86163', async ({
		apiHelpers,
		page,
		searchPage,
		site,
	}) => {
		let siteLayout: Layout;

		await test.step('Create web content for search results', async () => {
			const basicWebContentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			for (let count = 0; count < 21; count++) {
				await apiHelpers.jsonWebServicesJournal.addWebContent({
					ddmStructureId: basicWebContentStructureId,
					groupId: site.id,
					titleMap: {en_US: `Test Web Content ${count}`},
				});
			}
		});

		await test.step('Create a portlet page associated to site', async () => {
			siteLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {type: 'portlet'},
				title: getRandomString(),
			});
		});

		await test.step('Navigate to the site page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${siteLayout.friendlyURL}`
			);
		});

		await test.step('Add search portlets to new page', async () => {
			await searchPage.addPortlet('Modified Facet', 'Search');
			await searchPage.addPortlet('Search Bar', 'Search');
			await searchPage.addPortlet('Search Results', 'Search');
			await searchPage.addPortlet('Sort', 'Search');
			await searchPage.addPortlet('Type Facet', 'Search');
		});

		await test.step('Perform a search', async () => {
			await searchPage.searchKeywordInMainContent('test');

			await expect(searchPage.searchResultsTotalLabel).toHaveText(
				/\d+ Results for test/
			);
		});

		await test.step('Verify URL parameters are sorted after clicking facet checkbox', async () => {
			const typeFacetCheckbox = await searchPage.getSearchFacetCheckbox(
				/Web Content\s/,
				'Type'
			);

			await searchPage.selectSearchFacetCheckbox(typeFacetCheckbox);

			const facetKeys = Array.from(
				new URL(page.url()).searchParams.keys()
			);

			expect(facetKeys).toEqual([...facetKeys].sort());
		});

		await test.step('Verify URL parameters are sorted after clicking modified facet link', async () => {
			const pastYearFacetLink = await searchPage.getSearchFacetLink(
				'Past Year',
				'Last Modified'
			);

			await searchPage.selectSearchFacetLink(pastYearFacetLink);

			const modifiedKeys = Array.from(
				new URL(page.url()).searchParams.keys()
			);

			expect(modifiedKeys).toEqual([...modifiedKeys].sort());
		});

		await test.step('Verify URL parameters are sorted after changing items per page', async () => {
			await searchPage.selectPaginationItemsPerPage(40);

			const deltaKeys = Array.from(
				new URL(page.url()).searchParams.keys()
			);

			expect(deltaKeys).toEqual([...deltaKeys].sort());

			await searchPage.selectPaginationItemsPerPage(20);
		});

		await test.step('Verify URL parameters are sorted after clicking pagination page number', async () => {
			await searchPage.selectPaginationPageNumber(2);

			const paginationKeys = Array.from(
				new URL(page.url()).searchParams.keys()
			);

			expect(paginationKeys).toEqual([...paginationKeys].sort());
		});

		await test.step('Verify URL parameters are sorted after clicking sort option', async () => {
			const sortPortlet = page.locator('.portlet-sort');

			await sortPortlet.getByRole('button', {name: 'Sort By'}).click();

			await page.getByRole('menuitem', {name: 'Title'}).click();

			const sortKeys = Array.from(
				new URL(page.url()).searchParams.keys()
			);

			expect(sortKeys).toEqual([...sortKeys].sort());
		});
	});
});
