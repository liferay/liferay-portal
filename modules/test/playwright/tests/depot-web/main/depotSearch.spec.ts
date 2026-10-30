/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {searchPageTest} from '../../../fixtures/searchPageTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {SearchPage} from '../../../pages/portal-search-web/SearchPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	searchPageTest
);

const DEPOT_ADMIN_NAMESPACE =
	'_com_liferay_depot_web_portlet_DepotAdminPortlet_';

async function addDepotContent({
	apiHelpers,
	site,
}: {
	apiHelpers: DataApiHelpers;
	site: Site;
}) {
	const assetLibrary =
		await apiHelpers.headlessAssetLibrary.createAssetLibrary();

	const documentTitle = getRandomString();
	const tagName = getRandomString().toLowerCase();

	await apiHelpers.headlessDelivery.postAssetLibraryDocument(
		assetLibrary.id,
		createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
		{
			fileName: `${documentTitle}.jpg`,
			keywords: [tagName],
			title: documentTitle,
		}
	);

	const videoTitle = getRandomString();

	await apiHelpers.headlessDelivery.postExternalVideoShortcut(
		`asset-libraries/${assetLibrary.id}`,
		{keywords: [tagName], title: videoTitle}
	);

	const webContentTitle = getRandomString();

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
		groupId: assetLibrary.siteId,
		titleMap: {en_US: webContentTitle},
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getWidgetDefinition({
				id: getRandomString(),
				widgetName:
					'com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet',
			}),
			getWidgetDefinition({
				id: getRandomString(),
				widgetName:
					'com_liferay_portal_search_web_search_results_portlet_SearchResultsPortlet',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	return {
		assetLibrary,
		documentTitle,
		searchPageURL: `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`,
		tagName,
		videoTitle,
		webContentTitle,
	};
}

async function changeSearchability({
	assetLibraryId,
	menuItem,
	page,
	searchable,
	siteName,
}: {
	assetLibraryId: string;
	menuItem: 'Make Searchable' | 'Make Unsearchable';
	page: Page;
	searchable: 'No' | 'Yes';
	siteName: string;
}) {
	await page.goto(
		`/group/guest${PORTLET_URLS.depotAdmin}&${DEPOT_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fdepot%2Fedit_depot_entry&${DEPOT_ADMIN_NAMESPACE}depotEntryId=${assetLibraryId}&${DEPOT_ADMIN_NAMESPACE}screenNavigationEntryKey=sites`
	);

	const siteRow = page.getByRole('row').filter({hasText: siteName});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: menuItem}),
		timeout: 2000,
		trigger: siteRow.getByRole('button'),
	});

	await waitForAlert(page);

	const searchableContentCell = page
		.getByRole('row')
		.filter({hasText: siteName})
		.getByRole('cell')
		.nth(1);

	await expect(searchableContentCell).toHaveText(searchable);
}

function getSearchResult(searchPage: SearchPage, title: string, type: string) {
	return searchPage.searchResultsItems
		.filter({hasText: title})
		.filter({hasText: type});
}

async function search({
	keyword,
	page,
	searchPage,
	searchPageURL,
	verify,
}: {
	keyword: string;
	page: Page;
	searchPage: SearchPage;
	searchPageURL: string;
	verify: () => Promise<void>;
}) {
	await expect(async () => {
		await page.goto(searchPageURL);

		await searchPage.searchKeywordInMainContent(keyword);

		await verify();
	}).toPass({timeout: 60000});
}

async function viewResultDetails(searchPage: SearchPage, title: string) {
	await searchPage.searchResults
		.getByRole('link', {exact: true, name: title})
		.click();

	await expect(searchPage.searchResults.locator('.asset-title')).toHaveText(
		title
	);
}

test(
	'Depot content can be searched from a connected site by title, tag and user',
	{tag: ['@LPD-109097', '@LPD-112510']},
	async ({apiHelpers, page, searchPage, site}) => {
		const {
			assetLibrary,
			documentTitle,
			searchPageURL,
			tagName,
			videoTitle,
			webContentTitle,
		} = await addDepotContent({apiHelpers, site});

		await test.step('Depot content is not found from a site that is not connected', async () => {
			await search({
				keyword: documentTitle,
				page,
				searchPage,
				searchPageURL,
				verify: async () => {
					await expect(
						searchPage.searchResults.getByText(
							`No results were found that matched the keywords: ${documentTitle}.`
						)
					).toBeVisible({timeout: 2000});
				},
			});
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			assetLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		await test.step('Search by user', async () => {
			await search({
				keyword: 'Test Test',
				page,
				searchPage,
				searchPageURL,
				verify: async () => {
					await expect(
						getSearchResult(searchPage, documentTitle, 'Document')
					).toBeVisible({timeout: 2000});

					await expect(
						getSearchResult(
							searchPage,
							webContentTitle,
							'Web Content Article'
						)
					).toBeVisible({timeout: 2000});
				},
			});
		});

		await test.step('Search by document title', async () => {
			await search({
				keyword: documentTitle,
				page,
				searchPage,
				searchPageURL,
				verify: async () => {
					await expect(
						getSearchResult(searchPage, documentTitle, 'Document')
					).toBeVisible({timeout: 2000});
				},
			});

			await viewResultDetails(searchPage, documentTitle);
		});

		await test.step('Search by tag', async () => {
			await search({
				keyword: tagName,
				page,
				searchPage,
				searchPageURL,
				verify: async () => {
					await expect(
						getSearchResult(searchPage, documentTitle, 'Document')
					).toBeVisible({timeout: 2000});
					await expect(
						getSearchResult(searchPage, videoTitle, 'Document')
					).toBeVisible({timeout: 2000});
				},
			});

			await viewResultDetails(searchPage, documentTitle);
		});

		await test.step('Search by web content title', async () => {
			await search({
				keyword: webContentTitle,
				page,
				searchPage,
				searchPageURL,
				verify: async () => {
					await expect(
						getSearchResult(
							searchPage,
							webContentTitle,
							'Web Content Article'
						)
					).toBeVisible({timeout: 2000});
				},
			});

			await viewResultDetails(searchPage, webContentTitle);
		});
	}
);

test(
	'Depot content is not found from a connected site while the depot is unsearchable',
	{tag: '@LPD-109097'},
	async ({apiHelpers, page, searchPage, site}) => {
		const {
			assetLibrary,
			documentTitle,
			searchPageURL,
			tagName,
			webContentTitle,
		} = await addDepotContent({apiHelpers, site});

		await apiHelpers.headlessAssetLibrary.connectSite(
			assetLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		await test.step('Make the depot unsearchable for the site', async () => {
			await changeSearchability({
				assetLibraryId: assetLibrary.id,
				menuItem: 'Make Unsearchable',
				page,
				searchable: 'No',
				siteName: site.name!,
			});
		});

		await test.step('Search by user, title and tag finds no depot content', async () => {
			await search({
				keyword: 'Test Test',
				page,
				searchPage,
				searchPageURL,
				verify: async () => {
					await expect(searchPage.searchResults).toBeVisible({
						timeout: 2000,
					});

					await expect(
						getSearchResult(searchPage, documentTitle, 'Document')
					).toHaveCount(0, {timeout: 2000});

					await expect(
						getSearchResult(
							searchPage,
							webContentTitle,
							'Web Content Article'
						)
					).toHaveCount(0, {timeout: 2000});
				},
			});

			for (const keyword of [documentTitle, tagName, webContentTitle]) {
				await search({
					keyword,
					page,
					searchPage,
					searchPageURL,
					verify: async () => {
						await expect(
							searchPage.searchResults.getByText(
								`No results were found that matched the keywords: ${keyword}.`
							)
						).toBeVisible({timeout: 2000});
					},
				});
			}
		});

		await test.step('Make the depot searchable again', async () => {
			await changeSearchability({
				assetLibraryId: assetLibrary.id,
				menuItem: 'Make Searchable',
				page,
				searchable: 'Yes',
				siteName: site.name!,
			});

			await search({
				keyword: webContentTitle,
				page,
				searchPage,
				searchPageURL,
				verify: async () => {
					await expect(
						getSearchResult(
							searchPage,
							webContentTitle,
							'Web Content Article'
						)
					).toBeVisible({timeout: 2000});
				},
			});

			await viewResultDetails(searchPage, webContentTitle);
		});
	}
);
