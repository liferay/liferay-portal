/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {searchPageTest} from '../../../fixtures/searchPageTest';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

export const test = mergeTests(
	isolatedLayoutTest({type: 'portlet'}),
	isolatedSiteTest,
	loginTest(),
	searchPageTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	pageEditorPagesTest
);

test.describe('Category facet configuration for vocabularies', () => {
	test('Lists 20+ sites available to the user @LPD-33194', async ({
		apiHelpers,
		layout,
		page,
		searchPage,
	}) => {
		const siteName = getRandomString();

		await test.step('Create 21 sites to test listing', async () => {
			for (let count = 0; count < 21; count++) {
				const newSite = await apiHelpers.headlessSite.createSite({
					name: `${siteName}-${count}`,
				});

				apiHelpers.data.push({id: newSite.id, type: 'site'});
			}
		});

		await test.step('Add search bar and results portlet to new page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Search Bar', 'Search');
			await searchPage.addPortlet('Category Facet', 'Search');
			await searchPage.addPortlet('Search Results', 'Search');
		});

		await test.step('Search for keyword "Test"', async () => {
			await searchPage.searchKeywordInMainContent('test');

			await expect(searchPage.searchResultsTotalLabel).toHaveText(
				/\d+ Results for test/
			);
		});

		await test.step('Open category facet configurations', async () => {
			await searchPage.openSearchPortletConfiguration('Category Facet');
		});

		await test.step('Assert 21 sites are listed in the configuration', async () => {
			await searchPage.modalIFrame
				.getByLabel('Select Vocabularies')
				.click();

			await searchPage.modalIFrame
				.getByRole('treeitem', {name: 'Global'})
				.waitFor();

			for (let count = 0; count < 21; count++) {
				await expect(
					searchPage.modalIFrame.getByRole('treeitem', {
						exact: true,
						name: `${siteName}-${count}`,
					})
				).toBeVisible();
			}
		});
	});
});

test.describe('Retain items per page in search paginator', () => {
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
});

test.describe('Clear and retain facet selections', () => {
	let typeDocumentFacetCheckbox: Locator;
	let folderLiferayFacetCheckbox: Locator;
	let userTestTestFacetCheckbox: Locator;
	let lastModifiedPastYearFacetLink: Locator;

	test.beforeEach(async ({layout, page, searchPage}) => {
		await test.step('Add search portlet to a new page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Search Bar', 'Search');
			await searchPage.addPortlet('Folder Facet', 'Search');
			await searchPage.addPortlet('Type Facet', 'Search');
			await searchPage.addPortlet('User Facet', 'Search');
			await searchPage.addPortlet('Modified Facet', 'Search');
			await searchPage.addPortlet('Search Results', 'Search');
			await searchPage.addPortlet('Search Options', 'Search');
		});

		await test.step('Perform new search', async () => {
			await searchPage.searchKeywordInMainContent('test');

			await expect(searchPage.searchResultsTotalLabel).toHaveText(
				/\d+ Results for test/
			);
		});

		await test.step('Select facet terms and assert checked', async () => {
			folderLiferayFacetCheckbox =
				await searchPage.getSearchFacetCheckbox(
					'Provided by Liferay',
					'Folder'
				);
			typeDocumentFacetCheckbox = await searchPage.getSearchFacetCheckbox(
				/Document\s/,
				'Type'
			);
			userTestTestFacetCheckbox = await searchPage.getSearchFacetCheckbox(
				'Test Test',
				'User'
			);
			lastModifiedPastYearFacetLink = await searchPage.getSearchFacetLink(
				'Past Year',
				'Last Modified'
			);

			await searchPage.selectSearchFacetCheckbox(
				folderLiferayFacetCheckbox
			);
			await searchPage.selectSearchFacetCheckbox(
				typeDocumentFacetCheckbox
			);
			await searchPage.selectSearchFacetCheckbox(
				userTestTestFacetCheckbox
			);
			await searchPage.selectSearchFacetLink(
				lastModifiedPastYearFacetLink
			);
		});
	});

	test('Clears facet terms after new keyword search @LPD-19994', async ({
		searchPage,
	}) => {
		await test.step('Perform new search with different keyword', async () => {
			await searchPage.searchKeywordInMainContent('png');

			await expect(searchPage.searchResultsTotalLabel).toHaveText(
				/\d+ Results for png/
			);
		});

		await test.step('Verify that facet selections are cleared', async () => {
			await expect(folderLiferayFacetCheckbox).not.toBeChecked();
			await expect(typeDocumentFacetCheckbox).not.toBeChecked();
			await expect(userTestTestFacetCheckbox).not.toBeChecked();
			await expect(lastModifiedPastYearFacetLink).not.toHaveClass(
				/facet-term-selected/
			);
		});
	});

	test('Retains facet terms if search keyword has not changed @LPD-19994', async ({
		searchPage,
	}) => {
		await test.step('Perform new search with same keyword', async () => {
			await searchPage.searchKeywordInMainContent('test');

			await expect(searchPage.searchResultsTotalLabel).toHaveText(
				/\d+ Results for test/
			);
		});

		await test.step('Verify that facet selections are retained', async () => {
			await expect(folderLiferayFacetCheckbox).toBeChecked();
			await expect(typeDocumentFacetCheckbox).toBeChecked();
			await expect(userTestTestFacetCheckbox).toBeChecked();
			await expect(lastModifiedPastYearFacetLink).toHaveClass(
				/facet-term-selected/
			);
		});
	});

	test('Clears facet terms if performing an empty search @LPD-19994', async ({
		page,
		searchPage,
	}) => {
		await test.step('Configure search options to retain facet selections', async () => {
			await searchPage.searchOptionsConfigurationLink.click();

			await searchPage.selectPortletConfigurationsCheckbox([
				{
					label: 'Allow Empty Searches',
					value: true,
				},
			]);

			await searchPage.savePortletConfiguration();

			await page.reload();
		});

		await test.step('Perform new search with empty keyword', async () => {
			await searchPage.searchKeywordInMainContent('');

			await expect(searchPage.searchResultsTotalLabel).toHaveText(
				/\d+ Results for\s+/
			);
		});

		await test.step('Verify that facet selections are cleared', async () => {
			await expect(folderLiferayFacetCheckbox).not.toBeChecked();
			await expect(typeDocumentFacetCheckbox).not.toBeChecked();
			await expect(userTestTestFacetCheckbox).not.toBeChecked();
			await expect(lastModifiedPastYearFacetLink).not.toHaveClass(
				/facet-term-selected/
			);
		});
	});

	test('Retains facet terms if configured under search options @LPD-19994', async ({
		page,
		searchPage,
	}) => {
		await test.step('Configure search options to retain facet selections', async () => {
			await searchPage.searchOptionsConfigurationLink.click();

			await searchPage.selectPortletConfigurationsCheckbox([
				{
					label: 'Retain Facet Selections Across Searches',
					value: true,
				},
			]);
			await searchPage.savePortletConfiguration();

			await page.reload();
		});

		await test.step('Perform new search with different keyword', async () => {
			await searchPage.searchKeywordInMainContent('png');

			await expect(searchPage.searchResultsTotalLabel).toHaveText(
				/\d+ Results for png/
			);
		});

		await test.step('Verify that facet selections are retained', async () => {
			await expect(folderLiferayFacetCheckbox).toBeChecked();
			await expect(typeDocumentFacetCheckbox).toBeChecked();
			await expect(userTestTestFacetCheckbox).toBeChecked();
			await expect(lastModifiedPastYearFacetLink).toHaveClass(
				/facet-term-selected/
			);
		});
	});
});

test.describe('Custom facet is not registered more than once', () => {
	test('Shows no registration warning when adding a custom facet with date picker', async ({
		apiHelpers,
		page,
		pageEditorPage,
		searchPage,
		site,
	}) => {
		let layout: Layout;

		await test.step('Create site page and go to the page', async () => {
			layout = await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);
		});

		await test.step('Add custom facet to new page', async () => {
			await pageEditorPage.addWidget('Search', 'Search Bar');

			await pageEditorPage.addWidget('Search', 'Custom Facet');
		});

		await test.step('Configure custom facet to aggregate by date', async () => {
			const customFacetFragmentId =
				await pageEditorPage.getFragmentId('Custom Facet');

			await pageEditorPage.clickFragmentOption(
				customFacetFragmentId,
				'Configuration'
			);

			await searchPage.modalIFrame
				.getByLabel('Aggregation Type', {exact: true})
				.selectOption('Date Range');

			await searchPage.modalIFrame
				.getByLabel('Aggregation Field Required')
				.fill('modified');

			await searchPage.savePortletConfiguration();
		});

		await test.step('Publish page and exit edit mode', async () => {
			await pageEditorPage.publishPage();

			await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);
		});

		await test.step('Conduct a sample search', async () => {
			await searchPage.searchKeywordInMainContent('test');

			await page.getByLabel('Custom Range', {exact: false}).click();
		});

		await test.step('Expect no warnings about the date picker registration', async () => {
			let warningOccurred = false;

			page.on('console', (message) => {
				if (
					message.type() === 'warning' &&
					message
						.text()
						.includes('DatePicker" is being registered twice')
				) {
					warningOccurred = true;
				}
			});

			await page.waitForTimeout(500);

			expect(warningOccurred).toBe(false);
		});
	});
});
