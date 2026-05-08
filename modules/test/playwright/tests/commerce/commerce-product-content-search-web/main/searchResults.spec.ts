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
import {searchPageTest} from '../../../../fixtures/searchPageTest';
import getRandomString from '../../../../utils/getRandomString';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	searchPageTest
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
