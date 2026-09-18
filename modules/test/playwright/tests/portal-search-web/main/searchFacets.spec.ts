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
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';
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

test.describe('Category Facet', () => {
	test('Lists 20+ sites available to the user @LPD-33194', async ({
		apiHelpers,
		layout,
		page,
		searchPage,
	}) => {
		const siteName = getRandomString();

		await test.step('Create 21 sites to test listing', async () => {
			for (let count = 0; count < 21; count++) {
				await apiHelpers.headlessAdminSite.postSite({
					name: `${siteName}-${count}`,
				});
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

	test(
		'Filters by a vocabulary selected from an asset library',
		{tag: '@LPD-104381'},
		async ({apiHelpers, layout, page, searchPage}) => {
			const assetLibraryName = getRandomString();
			const categoryNames = [getRandomString(), getRandomString()];
			const keyword = getRandomString();
			const vocabularyNames = [getRandomString(), getRandomString()];

			await test.step('Create an asset library with two vocabularies connected to the site', async () => {
				const company =
					await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
						'liferay.com'
					);

				const guestGroup =
					await apiHelpers.jsonWebServicesGroup.getGroupByKey(
						company.companyId,
						'Guest'
					);

				const assetLibrary =
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name: assetLibraryName,
					});

				await apiHelpers.headlessAssetLibrary.connectSite(
					assetLibrary.externalReferenceCode,
					guestGroup.externalReferenceCode
				);

				const categoryIds: number[] = [];

				for (const [
					index,
					vocabularyName,
				] of vocabularyNames.entries()) {
					const vocabulary =
						await apiHelpers.headlessAdminTaxonomy.postAssetLibraryTaxonomyVocabulary(
							{
								assetLibraryId: assetLibrary.siteId,
								name: vocabularyName,
							}
						);

					const category =
						await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
							{
								name: categoryNames[index],
								vocabularyId: vocabulary.id,
							}
						);

					categoryIds.push(category.id);
				}

				await apiHelpers.headlessDelivery.postStructuredContent({
					categoryIds,
					contentFields: [
						{
							contentFieldValue: {data: keyword},
							name: 'content',
						},
					],
					contentStructureId:
						await getBasicWebContentStructureId(apiHelpers),
					datePublished: null,
					siteId: layout.groupId,
					title: keyword,
				});
			});

			await test.step('Add search portlets to the page', async () => {
				await page.goto('/web/guest' + layout.friendlyURL);

				await searchPage.addPortlet('Search Bar', 'Search');
				await searchPage.addPortlet('Category Facet', 'Search');
				await searchPage.addPortlet('Search Results', 'Search');
			});

			await test.step('Select the first asset library vocabulary in the configuration', async () => {
				await searchPage.openSearchPortletConfiguration(
					'Category Facet'
				);

				await searchPage.modalIFrame
					.getByLabel('Select Vocabularies')
					.click();

				const assetLibraryTreeItem = searchPage.modalIFrame.getByRole(
					'treeitem',
					{name: assetLibraryName}
				);

				await assetLibraryTreeItem
					.locator('.component-expander')
					.click();

				await searchPage.modalIFrame
					.getByRole('treeitem', {name: vocabularyNames[0]})
					.getByRole('checkbox')
					.check();

				await searchPage.savePortletConfiguration();
			});

			await test.step('Assert only the selected vocabulary category is listed', async () => {
				await searchPage.searchKeywordInMainContent(keyword);

				await expect(searchPage.searchResultsTotalLabel).toHaveText(
					`1 Result for ${keyword}`
				);

				await expect(
					await searchPage.getSearchFacetCheckbox(
						categoryNames[0],
						'Category'
					)
				).toBeVisible();

				await expect(
					await searchPage.getSearchFacetCheckbox(
						categoryNames[1],
						'Category'
					)
				).toBeHidden();
			});
		}
	);

	test(
		'Allows a Site Administrator without a library role to view the connected library vocabularies',
		{tag: '@LPD-104381'},
		async ({apiHelpers, layout, page, searchPage}) => {
			const assetLibraryName = getRandomString();
			const vocabularyName = getRandomString();

			const guestGroup =
				await test.step('Create an asset library connected to the site', async () => {
					const company =
						await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
							'liferay.com'
						);

					const guestGroup =
						await apiHelpers.jsonWebServicesGroup.getGroupByKey(
							company.companyId,
							'Guest'
						);

					const assetLibrary =
						await apiHelpers.headlessAssetLibrary.createAssetLibrary(
							{
								name: assetLibraryName,
							}
						);

					await apiHelpers.headlessAssetLibrary.connectSite(
						assetLibrary.externalReferenceCode,
						guestGroup.externalReferenceCode
					);

					await apiHelpers.headlessAdminTaxonomy.postAssetLibraryTaxonomyVocabulary(
						{
							assetLibraryId: assetLibrary.siteId,
							name: vocabularyName,
						}
					);

					return guestGroup;
				});

			await test.step('Assign a Site Administrator to the site without any library role', async () => {
				const user =
					await apiHelpers.headlessAdminUser.postUserAccount();

				userData[user.alternateName] = {
					name: user.givenName,
					password: 'test',
					surname: user.familyName,
				};

				const siteAdministratorRole =
					await apiHelpers.headlessAdminUser.getRoleByName(
						'Site Administrator'
					);

				await apiHelpers.headlessAdminUser.assignUserToSite(
					siteAdministratorRole.id,
					guestGroup.groupId,
					user.id
				);

				await performUserSwitchViaApi(page, user.alternateName);
			});

			await test.step('Confirm the connected library vocabulary is visible in the configuration', async () => {
				await page.goto('/web/guest' + layout.friendlyURL);

				await searchPage.addPortlet('Search Bar', 'Search');
				await searchPage.addPortlet('Category Facet', 'Search');
				await searchPage.addPortlet('Search Results', 'Search');

				await searchPage.openSearchPortletConfiguration(
					'Category Facet'
				);

				await searchPage.modalIFrame
					.getByLabel('Select Vocabularies')
					.click();

				const assetLibraryTreeItem = searchPage.modalIFrame.getByRole(
					'treeitem',
					{name: assetLibraryName}
				);

				await assetLibraryTreeItem
					.locator('.component-expander')
					.click();

				await expect(
					searchPage.modalIFrame.getByRole('treeitem', {
						name: vocabularyName,
					})
				).toBeVisible();
			});
		}
	);
});

test.describe('Selection Persistence', () => {
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

test.describe('Custom Facet', () => {
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

test.describe('Folder Facet', () => {
	test('Includes CMS object entry folders @LPD-75959', async ({
		apiHelpers,
		layout,
		page,
		searchPage,
	}) => {
		let folderFacetTerm: Locator;
		let objectEntryContent: any;
		let objectEntryFolder: any;
		let space: any;

		const cmsId = `CMS_${getRandomString()}`;

		await test.step('Create a new Space', async () => {
			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: `Space ${cmsId}`,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a folder for that space', async () => {
			objectEntryFolder =
				await apiHelpers.objectFolder.createObjectEntryFolder({
					parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					scopeKey: space.siteId,
					title: `${cmsId} Folder`,
				});
		});

		await test.step('Create a web content within the new folder', async () => {
			objectEntryContent = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode:
						objectEntryFolder.externalReferenceCode,
					title: `${cmsId} Content`,
				},
				'cms/basic-web-contents',
				space.name
			);
		});

		await test.step('Add search widgets to new page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Search Bar', 'Search');
			await searchPage.addPortlet('Folder Facet', 'Search');
			await searchPage.addPortlet('Search Results', 'Search');
		});

		await test.step('Configure search bar scope', async () => {
			await searchPage.openSearchPortletConfiguration('Search Bar', 1);

			await searchPage.selectPortletConfigurationsSelect([
				{
					label: 'Scope',
					value: 'Everything',
				},
			]);

			await searchPage.savePortletConfiguration();
		});

		await test.step('Search for CMS and expect to see the folder facet term', async () => {
			await searchPage.searchKeywordInMainContent(cmsId);

			folderFacetTerm = await searchPage.getSearchFacetCheckbox(
				objectEntryFolder.title,
				'Folder'
			);

			await expect(folderFacetTerm).toBeVisible();
		});

		await test.step('Select the folder facet term and expect to see results', async () => {
			await searchPage.selectSearchFacetCheckbox(folderFacetTerm);

			await expect(
				searchPage.searchResults.getByRole('link', {
					name: objectEntryContent.title,
				})
			).toBeVisible();
		});
	});
});
