/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// Product Analytics API

export {
	default as ProductAnalyticsBanner,
	checkProductAnalyticsConsentForTypes,
	openProductAnalyticsConsentModal,
} from '../product_analytics_banner/js/ProductAnalyticsBanner';

export {default as ProductAnalyticsConsentPanel} from '../product_analytics_consent_panel/js/ProductAnalyticsConsentPanel';

export {default as ConfigurationFormEventHandler} from './ConfigurationFormEventHandler';
