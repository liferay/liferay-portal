/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {searchPageTest} from '../../../../fixtures/searchPageTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {SearchPage} from '../../../../pages/portal-search-web/SearchPage';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import addApprovedStructuredContent from '../../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../../utils/structured-content/getBasicWebContentStructureId';
import addSiteUser from '../../../content-dashboard-web/main/utils/addSiteUser';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	searchPageTest
);

const BLOGS_ENTRY_CLASS_NAME = 'com.liferay.blogs.model.BlogsEntry';

const JOURNAL_ARTICLE_CLASS_NAME = 'com.liferay.journal.model.JournalArticle';

async function addSearchWidgets({
	apiHelpers,
	facets,
	searchPage,
	site,
}: {
	apiHelpers: ApiHelpers;
	facets: string[];
	searchPage: SearchPage;
	site: Site;
}) {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		options: {type: 'portlet'},
		title: getRandomString(),
		typeSettings: 'layout-template-id=1_column\n',
	});

	await searchPage.page.goto(
		`/web${site.friendlyUrlPath}${layout.friendlyURL}`
	);

	const portletNames = ['Search Bar', ...facets, 'Search Results'];

	for (const portletName of portletNames) {
		await searchPage.addPortlet(portletName, 'Search');
	}

	await searchPage.page.reload();

	await expect(
		searchPage.page.locator('#main-content .portlet-boundary')
	).toHaveCount(portletNames.length);
}

async function addWebContent(
	apiHelpers: ApiHelpers,
	siteId: string,
	title: string,
	{categoryIds, tags}: {categoryIds?: number[]; tags?: string[]} = {}
) {
	return addApprovedStructuredContent({
		apiHelpers,
		categoryIds,
		contentStructureId: await getBasicWebContentStructureId(apiHelpers),
		siteId,
		tags,
		title,
	});
}

function getRandomKeyword() {
	return `keyword${getRandomInt()}`;
}

test.describe('Facet URL Parameters', () => {
	test(
		'Adds the type parameter for each selected type term',
		{tag: '@LPD-101537'},
		async ({apiHelpers, searchPage, site}) => {
			const keyword = getRandomKeyword();

			const blogTitle = `${keyword} Blog`;
			const webContentTitle = `${keyword} Web Content`;

			await test.step('Add a blog entry and a web content article', async () => {
				await apiHelpers.headlessDelivery.postBlog(site.id, {
					headline: blogTitle,
				});

				await addWebContent(apiHelpers, site.id, webContentTitle);
			});

			await addSearchWidgets({
				apiHelpers,
				facets: ['Type Facet'],
				searchPage,
				site,
			});

			await searchPage.searchKeywordInMainContent(keyword);

			await searchPage.assertSearchResults(keyword, [
				blogTitle,
				webContentTitle,
			]);

			const blogsEntryCheckbox = await searchPage.getSearchFacetCheckbox(
				/^Blogs Entry/,
				'Type'
			);
			const webContentArticleCheckbox =
				await searchPage.getSearchFacetCheckbox(
					/^Web Content Article/,
					'Type'
				);

			await test.step('Select the web content article term', async () => {
				await searchPage.selectSearchFacetCheckbox(
					webContentArticleCheckbox
				);

				await searchPage.assertSearchParameter('type', [
					JOURNAL_ARTICLE_CLASS_NAME,
				]);

				await searchPage.assertSearchResults(keyword, [
					webContentTitle,
				]);
			});

			await test.step('Select the blogs entry term', async () => {
				await searchPage.selectSearchFacetCheckbox(blogsEntryCheckbox);

				await searchPage.assertSearchParameter('type', [
					BLOGS_ENTRY_CLASS_NAME,
					JOURNAL_ARTICLE_CLASS_NAME,
				]);

				await searchPage.assertSearchResults(keyword, [
					blogTitle,
					webContentTitle,
				]);
			});

			await test.step('Deselect the web content article term', async () => {
				await searchPage.selectSearchFacetCheckbox(
					webContentArticleCheckbox,
					false
				);

				await searchPage.assertSearchParameter('type', [
					BLOGS_ENTRY_CLASS_NAME,
				]);

				await searchPage.assertSearchResults(keyword, [blogTitle]);
			});
		}
	);

	test(
		'Adds the category parameter with the category ID',
		{tag: '@LPD-101537'},
		async ({apiHelpers, searchPage, site}) => {
			const categoryName = getRandomString();
			const keyword = getRandomKeyword();

			const blogTitle = `${keyword} Blog`;
			const webContentTitle = `${keyword} Web Content`;

			let categoryId: number;

			await test.step('Add a categorized web content article and an uncategorized blog entry', async () => {
				const vocabulary =
					await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
						{
							name: getRandomString(),
							siteId: site.id,
						}
					);

				const category =
					await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
						{
							name: categoryName,
							vocabularyId: vocabulary.id,
						}
					);

				categoryId = category.id;

				await apiHelpers.headlessDelivery.postBlog(site.id, {
					headline: blogTitle,
				});

				await addWebContent(apiHelpers, site.id, webContentTitle, {
					categoryIds: [categoryId],
				});
			});

			await addSearchWidgets({
				apiHelpers,
				facets: ['Category Facet'],
				searchPage,
				site,
			});

			await searchPage.searchKeywordInMainContent(keyword);

			await searchPage.assertSearchResults(keyword, [
				blogTitle,
				webContentTitle,
			]);

			await test.step('Select the category term', async () => {
				await searchPage.selectSearchFacetCheckbox(
					await searchPage.getSearchFacetCheckbox(
						categoryName,
						'Category'
					)
				);

				await searchPage.assertSearchParameter('category', [
					categoryId,
				]);

				await searchPage.assertSearchResults(keyword, [
					webContentTitle,
				]);
			});
		}
	);

	test(
		'Adds the user parameter with the user ID',
		{tag: '@LPD-101537'},
		async ({apiHelpers, browser, searchPage, site}) => {
			const keyword = getRandomKeyword();

			const adminWebContentTitle = `${keyword} Admin`;
			const authorWebContentTitle = `${keyword} Author`;

			const author = await addSiteUser({
				apiHelpers,
				siteId: site.id,
				userAccount: {
					familyName: 'Author',
					givenName: `User${getRandomInt()}`,
				},
			});

			await test.step('Add a web content article as each user', async () => {
				await addWebContent(apiHelpers, site.id, adminWebContentTitle);

				const authorContext = await browser.newContext();

				try {
					const authorPage = await authorContext.newPage();

					await performLoginViaApi({
						page: authorPage,
						screenName: author.alternateName,
					});

					// The author cannot look up the structure, so pass the ID the
					// administrator resolved

					await addApprovedStructuredContent({
						apiHelpers: new ApiHelpers(authorPage),
						contentStructureId:
							await getBasicWebContentStructureId(apiHelpers),
						siteId: site.id,
						title: authorWebContentTitle,
					});
				}
				finally {
					await authorContext.close();
				}
			});

			await addSearchWidgets({
				apiHelpers,
				facets: ['User Facet'],
				searchPage,
				site,
			});

			await searchPage.searchKeywordInMainContent(keyword);

			await searchPage.assertSearchResults(keyword, [
				adminWebContentTitle,
				authorWebContentTitle,
			]);

			const authorCheckbox = await searchPage.getSearchFacetCheckbox(
				`${author.givenName} ${author.familyName}`,
				'User'
			);

			await test.step('Select the author term', async () => {
				await searchPage.selectSearchFacetCheckbox(authorCheckbox);

				await searchPage.assertSearchParameter('user', [author.id]);

				await searchPage.assertSearchResults(keyword, [
					authorWebContentTitle,
				]);
			});

			await test.step('Deselect the author term', async () => {
				await searchPage.selectSearchFacetCheckbox(
					authorCheckbox,
					false
				);

				await searchPage.assertSearchParameter('user', []);

				await searchPage.assertSearchResults(keyword, [
					adminWebContentTitle,
					authorWebContentTitle,
				]);
			});
		}
	);

	test(
		'Adds the tag parameter for each selected tag term',
		{tag: '@LPD-101537'},
		async ({apiHelpers, searchPage, site}) => {
			const keyword = getRandomKeyword();
			const tags = [`tag${getRandomInt()}`, `tag${getRandomInt()}`];

			const blogTitle = `${keyword} Blog`;
			const firstWebContentTitle = `${keyword} First`;
			const secondWebContentTitle = `${keyword} Second`;

			await test.step('Add a web content article per tag and an untagged blog entry', async () => {
				await addWebContent(apiHelpers, site.id, firstWebContentTitle, {
					tags: [tags[0]],
				});

				await addWebContent(
					apiHelpers,
					site.id,
					secondWebContentTitle,
					{tags: [tags[1]]}
				);

				await apiHelpers.headlessDelivery.postBlog(site.id, {
					headline: blogTitle,
				});
			});

			await addSearchWidgets({
				apiHelpers,
				facets: ['Tag Facet'],
				searchPage,
				site,
			});

			await searchPage.searchKeywordInMainContent(keyword);

			await searchPage.assertSearchResults(keyword, [
				blogTitle,
				firstWebContentTitle,
				secondWebContentTitle,
			]);

			await test.step('Select the first tag term', async () => {
				await searchPage.selectSearchFacetCheckbox(
					await searchPage.getSearchFacetCheckbox(tags[0], 'Tag')
				);

				await searchPage.assertSearchParameter('tag', [tags[0]]);

				await searchPage.assertSearchResults(keyword, [
					firstWebContentTitle,
				]);
			});

			await test.step('Select the second tag term', async () => {
				await searchPage.selectSearchFacetCheckbox(
					await searchPage.getSearchFacetCheckbox(tags[1], 'Tag')
				);

				await searchPage.assertSearchParameter('tag', tags);

				await searchPage.assertSearchResults(keyword, [
					firstWebContentTitle,
					secondWebContentTitle,
				]);
			});
		}
	);

	test(
		'Adds the site parameter with the site ID',
		{tag: '@LPD-101537'},
		async ({apiHelpers, searchPage, site}) => {
			const keyword = getRandomKeyword();

			const blogTitle = `${keyword} Blog`;
			const webContentTitle = `${keyword} Web Content`;

			let otherSite: Site;

			await test.step('Add a web content article to the site and a blog entry to another site', async () => {
				otherSite = await apiHelpers.headlessAdminSite.postSite({
					name: getRandomString(),
				});

				await apiHelpers.headlessDelivery.postBlog(otherSite.id, {
					headline: blogTitle,
				});

				await addWebContent(apiHelpers, site.id, webContentTitle);
			});

			await addSearchWidgets({
				apiHelpers,
				facets: ['Site Facet'],
				searchPage,
				site,
			});

			await test.step('Search everything', async () => {
				await searchPage.openSearchPortletConfiguration(
					'Search Bar',
					1
				);

				await searchPage.selectPortletConfigurationsSelect([
					{label: 'Scope', value: 'Everything'},
				]);

				await searchPage.savePortletConfiguration();

				await searchPage.page.reload();

				await searchPage.searchKeywordInMainContent(keyword);

				await searchPage.assertSearchResults(keyword, [
					blogTitle,
					webContentTitle,
				]);
			});

			await test.step('Select the other site term', async () => {
				await searchPage.selectSearchFacetCheckbox(
					await searchPage.getSearchFacetCheckbox(
						otherSite.name,
						'Site'
					)
				);

				await searchPage.assertSearchParameter('site', [otherSite.id]);

				await searchPage.assertSearchResults(keyword, [blogTitle]);
			});

			await test.step('Select the site term', async () => {
				await searchPage.selectSearchFacetCheckbox(
					await searchPage.getSearchFacetCheckbox(site.name, 'Site')
				);

				await searchPage.assertSearchParameter('site', [
					otherSite.id,
					site.id,
				]);

				await searchPage.assertSearchResults(keyword, [
					blogTitle,
					webContentTitle,
				]);
			});
		}
	);

	test(
		'Adds the folder parameter with the folder ID',
		{tag: '@LPD-101537'},
		async ({apiHelpers, searchPage, site}) => {
			const folderNames = [getRandomString(), getRandomString()];
			const keyword = getRandomKeyword();

			const firstWebContentTitle = `${keyword} First`;
			const secondWebContentTitle = `${keyword} Second`;

			let firstFolderId: number;

			await test.step('Add a web content article to each of two folders', async () => {
				const contentStructureId =
					await getBasicWebContentStructureId(apiHelpers);

				for (const [index, title] of [
					firstWebContentTitle,
					secondWebContentTitle,
				].entries()) {
					const folder =
						await apiHelpers.headlessDelivery.postStructuredContentFolder(
							site.id,
							{name: folderNames[index]}
						);

					if (index === 0) {
						firstFolderId = folder.id;
					}

					await apiHelpers.headlessDelivery.postStructuredContentFolderStructuredContent(
						{
							contentStructureId,
							datePublished: null,
							structuredContentFolderId: folder.id,
							title,
						}
					);
				}
			});

			await addSearchWidgets({
				apiHelpers,
				facets: ['Folder Facet'],
				searchPage,
				site,
			});

			await searchPage.searchKeywordInMainContent(keyword);

			await searchPage.assertSearchResults(keyword, [
				firstWebContentTitle,
				secondWebContentTitle,
			]);

			await test.step('Select the first folder term', async () => {
				await searchPage.selectSearchFacetCheckbox(
					await searchPage.getSearchFacetCheckbox(
						folderNames[0],
						'Folder'
					)
				);

				await searchPage.assertSearchParameter('folder', [
					firstFolderId,
				]);

				await searchPage.assertSearchResults(keyword, [
					firstWebContentTitle,
				]);
			});
		}
	);

	test(
		'Removes only the cleared facet parameter and keeps the keyword',
		{tag: '@LPD-101537'},
		async ({apiHelpers, searchPage, site}) => {
			const keyword = getRandomKeyword();
			const tag = `tag${getRandomInt()}`;

			const blogTitle = `${keyword} Blog`;
			const taggedWebContentTitle = `${keyword} Tagged`;
			const untaggedWebContentTitle = `${keyword} Untagged`;

			await test.step('Add tagged and untagged content', async () => {
				await addWebContent(
					apiHelpers,
					site.id,
					taggedWebContentTitle,
					{tags: [tag]}
				);

				await addWebContent(
					apiHelpers,
					site.id,
					untaggedWebContentTitle
				);

				await apiHelpers.headlessDelivery.postBlog(site.id, {
					headline: blogTitle,
					keywords: [tag],
				});
			});

			await addSearchWidgets({
				apiHelpers,
				facets: ['Tag Facet', 'Type Facet'],
				searchPage,
				site,
			});

			await searchPage.searchKeywordInMainContent(keyword);

			const tagCheckbox = await searchPage.getSearchFacetCheckbox(
				tag,
				'Tag'
			);
			const webContentArticleCheckbox =
				await searchPage.getSearchFacetCheckbox(
					/^Web Content Article/,
					'Type'
				);

			await test.step('Select a term in each facet', async () => {
				await searchPage.selectSearchFacetCheckbox(
					webContentArticleCheckbox
				);

				await searchPage.selectSearchFacetCheckbox(tagCheckbox);

				await searchPage.assertSearchParameter('tag', [tag]);
				await searchPage.assertSearchParameter('type', [
					JOURNAL_ARTICLE_CLASS_NAME,
				]);

				await searchPage.assertSearchResults(keyword, [
					taggedWebContentTitle,
				]);
			});

			await test.step('Clear the type facet', async () => {
				await searchPage
					.getSearchFacetClearButton(/^Web Content Article/)
					.click();

				await searchPage.assertSearchParameter('type', []);
				await searchPage.assertSearchParameter('tag', [tag]);
				await searchPage.assertSearchParameter('q', [keyword]);

				await searchPage.assertSearchResults(keyword, [
					blogTitle,
					taggedWebContentTitle,
				]);
			});

			await test.step('Clear the tag facet', async () => {
				await searchPage.getSearchFacetClearButton(tag).click();

				await searchPage.assertSearchParameter('tag', []);
				await searchPage.assertSearchParameter('q', [keyword]);

				await searchPage.assertSearchResults(keyword, [
					blogTitle,
					taggedWebContentTitle,
					untaggedWebContentTitle,
				]);
			});
		}
	);
});
