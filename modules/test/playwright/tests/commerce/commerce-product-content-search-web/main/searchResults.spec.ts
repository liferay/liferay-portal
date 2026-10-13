/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {searchPageTest} from '../../../../fixtures/searchPageTest';
import getRandomString from '../../../../utils/getRandomString';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {templatesPageTest} from '../../../template-web/main/fixtures/templatesPageTest';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	searchPageTest,
	templatesPageTest
);

test(
	'Search should always properly strip keyword of leading asterisk when searching for a product',
	{tag: ['@LPD-88196']},
	async ({apiHelpers, page, searchPage, site}) => {
		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						allowEmptySearches: 'true',
						keywordsParameterName: 'q',
						searchScope: 'everything',
					},
					widgetName:
						'com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						allowEmptySearches: 'true',
					},
					widgetName:
						'com_liferay_portal_search_web_search_options_portlet_SearchOptionsPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_search_web_internal_portlet_CPSearchResultsPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(page.getByText(product1.name.en_US)).toBeVisible();

		await expect(page.getByText(product2.name.en_US)).toBeVisible();

		await page.goto(
			`/web/${site.name}/${layout.friendlyUrlPath}?q=*(cpProductId:${product1.productId})`
		);

		await expect(page.getByText(product1.name.en_US)).toBeVisible();

		await expect(page.getByText(product2.name.en_US)).not.toBeVisible();

		await searchPage.searchBarInputInMainContent.fill(
			`*(cpProductId:${product2.productId})`
		);
		await searchPage.searchBarInputInMainContent.press('Enter');

		await expect(page.getByText(product1.name.en_US)).not.toBeVisible();

		await expect(page.getByText(product2.name.en_US)).toBeVisible();
	}
);

test(
	'Commerce search results should still display when a search bar on the same page targets a different destination',
	{tag: ['@LPD-97403']},
	async ({apiHelpers, page, searchPage, site}) => {
		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const searchLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_search_web_internal_portlet_CPSearchResultsPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						allowEmptySearches: 'true',
						destination: searchLayout.friendlyUrlPath,
						keywordsParameterName: 'q',
						searchScope: 'everything',
					},
					widgetName:
						'com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						allowEmptySearches: 'true',
					},
					widgetName:
						'com_liferay_portal_search_web_search_options_portlet_SearchOptionsPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_search_web_internal_portlet_CPSearchResultsPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(searchPage.searchBarInputInMainContent).toBeVisible();

		await expect(page.getByText(product1.name.en_US)).toBeVisible();

		await expect(page.getByText(product2.name.en_US)).toBeVisible();

		await searchPage.searchBarInputInMainContent.fill(product2.name.en_US);
		await searchPage.searchBarInputInMainContent.press('Enter');

		await page.waitForURL(`**${searchLayout.friendlyUrlPath}**`);

		await expect(page.getByText(product2.name.en_US)).toBeVisible();

		await expect(page.getByText(product1.name.en_US)).not.toBeVisible();
	}
);

test(
	'A display template selected for the Commerce Search Results widget renders the paginated results',
	{tag: ['@COMMERCE-6663', '@LPD-107430']},
	async ({
		apiHelpers,
		page,
		pageEditorPage,
		searchResultsPage,
		site,
		templatesPage,
	}) => {
		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const searchResultsWidgetId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						allowEmptySearches: 'true',
					},
					widgetName:
						'com_liferay_portal_search_web_search_options_portlet_SearchOptionsPortlet',
				}),
				getWidgetDefinition({
					id: searchResultsWidgetId,
					widgetName:
						'com_liferay_commerce_product_content_search_web_internal_portlet_CPSearchResultsPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const searchResultsWidgetTemplateName = getRandomString();

		await test.step('Create a widget template for the Commerce Search Results widget', async () => {
			await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);
			await templatesPage.createWidgetTemplate(
				searchResultsWidgetTemplateName,
				'Commerce Search Results Template'
			);
			await templatesPage.editTemplate(searchResultsWidgetTemplateName);
			await templatesPage.importInformationTemplate(
				__dirname,
				'search_results_template.ftl'
			);
			await templatesPage.saveTemplate(searchResultsWidgetTemplateName);
		});

		await test.step('Verify the widget renders both products with the default renderer', async () => {
			await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

			await expect(page.getByText(product1.name.en_US)).toBeVisible();
			await expect(page.getByText(product2.name.en_US)).toBeVisible();
		});

		await test.step('Select the widget template and one item per page in the widget configuration', async () => {
			await pageEditorPage.goto(layout, site.friendlyUrlPath);
			await pageEditorPage.goToWidgetConfiguration(searchResultsWidgetId);

			await searchResultsPage.openRenderSelection();

			await expect(
				searchResultsPage.productListRendererSelectedOption
			).toHaveText('Default');

			await searchResultsPage.selectDisplayTemplate(
				searchResultsWidgetTemplateName
			);
			await searchResultsPage.setNumberOfItemsToDisplay(1);
			await searchResultsPage.saveConfiguration();
			await searchResultsPage.configurationCloseButton.click();

			await pageEditorPage.publishPage();
		});

		await test.step('Verify the widget renders one result through the widget template', async () => {
			await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

			await expect(
				page.getByText(
					new RegExp(
						`^Search Result (${product1.id}|${product2.id})$`
					)
				)
			).toHaveCount(1);

			await expect(page.getByText(product1.name.en_US)).not.toBeVisible();
			await expect(page.getByText(product2.name.en_US)).not.toBeVisible();
		});
	}
);
