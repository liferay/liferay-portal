/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.co25
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {ProductAnalyticsBannerPage} from '../pages/product-analytics-web/ProductAnalyticsBannerPage';
import {ProductAnalyticsConsentPanelPage} from '../pages/product-analytics-web/ProductAnalyticsConsentPanelPage';

const productAnalyticsPagesTest = test.extend<{
	productAnalyticsBannerPage: ProductAnalyticsBannerPage;
	productAnalyticsConsentPanelPage: ProductAnalyticsConsentPanelPage;
}>({
	productAnalyticsBannerPage: async ({page}, use) => {
		await use(new ProductAnalyticsBannerPage(page));
	},
	productAnalyticsConsentPanelPage: async ({page}, use) => {
		await use(new ProductAnalyticsConsentPanelPage(page));
	},
});

export {productAnalyticsPagesTest};
