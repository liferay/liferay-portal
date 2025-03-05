/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {defineConfig, devices} from '@playwright/test';

import 'dotenv/config';

import {config as accessibilityMenuWeb} from './tests/accessibility-menu-web/config';
import {config as accountAdminWebConfig} from './tests/account-admin-web/config';
import {config as addressWebConfig} from './tests/address-web/config';
import {config as analyticsReportsJsComponentsWeb} from './tests/analytics-reports-js-components-web/config';
import {config as analyticsSettingsWebConfig} from './tests/analytics-settings-web/config';
import {config as analyticsWebConfig} from './tests/analytics-web/config';
import {config as announcementsWebConfig} from './tests/announcements-web/config';
import {config as assetCategoriesAdminWebConfig} from './tests/asset-categories-admin-web/config';
import {config as assetPublisherWebConfig} from './tests/asset-publisher-web/config';
import {config as assetTagsAdminWebConfig} from './tests/asset-tags-admin-web/config';
import {config as batchPlannerConfig} from './tests/batch-planner/config';
import {config as blogsWebConfig} from './tests/blogs-web/config';
import {config as calendarWebConfig} from './tests/calendar-web/config';
import {config as captchaWebConfig} from './tests/captcha-web/config';
import {config as changeTrackingWebConfig} from './tests/change-tracking-web/config';
import {config as clientExtensionWebConfig} from './tests/client-extension-web/config';
import {config as clientExtensionWebClusterConfig} from './tests/client-extension-web/tests/cluster/config';
import {config as commerceAccountWebConfig} from './tests/commerce/commerce-account-web/config';
import {config as commerceCartContentWebConfig} from './tests/commerce/commerce-cart-content-web/config';
import {config as commerceChannelWebConfig} from './tests/commerce/commerce-channel-web/config';
import {config as commerceCheckoutWebConfig} from './tests/commerce/commerce-checkout-web/config';
import {config as commerceCurrencyWebConfig} from './tests/commerce/commerce-currency-web/config';
import {config as commerceDiscountContentWebConfig} from './tests/commerce/commerce-discount-content-web/config';
import {config as commerceOrderContentWebConfig} from './tests/commerce/commerce-order-content-web/config';
import {config as commerceOrderWebConfig} from './tests/commerce/commerce-order-web/config';
import {config as commercePaymentsWebConfig} from './tests/commerce/commerce-payment-web/config';
import {config as commerceProductContentSearchWebConfig} from './tests/commerce/commerce-product-content-search-web/config';
import {config as commerceProductContentWebConfig} from './tests/commerce/commerce-product-content-web/config';
import {config as commerceProductDefinitionsWebConfig} from './tests/commerce/commerce-product-definitions-web/config';
import {config as commerceProductOptionsWebConfig} from './tests/commerce/commerce-product-options-web/config';
import {config as commerceShippingEngineWebConfig} from './tests/commerce/commerce-shipping-engine-fixed-web/config';
import {config as commerceSiteInitializerWebConfig} from './tests/commerce/commerce-site-initializer/config';
import {config as commerceTaxEngineWebConfig} from './tests/commerce/commerce-tax-engine-web/config';
import {config as commerceThemeMiniumWebConfig} from './tests/commerce/commerce-theme-minium/config';
import {config as commerceWishListWebConfig} from './tests/commerce/commerce-wish-list-web/config';
import {config as configurationAdminWebConfig} from './tests/configuration-admin-web/config';
import {config as contentDashboardWebConfig} from './tests/content-dashboard-web/config';
import {config as cookiesBannerWebConfig} from './tests/cookies-banner-web/config';
import {config as depotWebConfig} from './tests/depot-web/config';
import {config as dispatchWebConfig} from './tests/dispatch-web/config';
import {config as documentLibraryWebConfig} from './tests/document-library-web/config';
import {config as dynamicDataMappingFormWebConfig} from './tests/dynamic-data-mapping-form-web/config';
import {config as exportImportWebConfig} from './tests/export-import-web/config';
import {config as featureFlagWebConfig} from './tests/feature-flag-web/config';
import {config as fragmentWebConfig} from './tests/fragment-web/config';
import {config as friendlyURLConfig} from './tests/friendly-url-web/config';
import {config as frontendDataSetAdminWebConfig} from './tests/frontend-data-set-admin-web/config';
import {config as frontendDataSetWebConfig} from './tests/frontend-data-set-web/config';
import {config as frontendEditorCKEditorWebConfig} from './tests/frontend-editor-ckeditor-web/config';
import {config as frontendJsComponentsWebConfig} from './tests/frontend-js-components-web/config';
import {config as frontendJsSpaWebConfig} from './tests/frontend-js-spa-web/config';
import {config as frontendJsWebConfig} from './tests/frontend-js-web/config';
import {config as frontendTaglibClayConfig} from './tests/frontend-taglib-clay/config';
import {config as frontendTaglibConfig} from './tests/frontend-taglib/config';
import {config as frontendTheme} from './tests/frontend-theme/config';
import {config as headlessBuilderImplConfig} from './tests/headless-builder-impl/config';
import {config as headlessBuilderWebConfig} from './tests/headless-builder-web/config';
import {config as iframeWebConfig} from './tests/iframe-web/config';
import {config as itemSelectorTaglibConfig} from './tests/item-selector-taglib/config';
import {config as journalWebConfig} from './tests/journal-web/config';
import {config as knowledgeBaseWebConfig} from './tests/knowledge-base-web/config';
import {config as layoutAdminWebConfig} from './tests/layout-admin-web/config';
import {config as layoutContentPageEditorWebConfig} from './tests/layout-content-page-editor-web/config';
import {config as layoutLockedLayoutsWebConfig} from './tests/layout-locked-layouts-web/config';
import {config as layoutPageTemplateAdminWebConfig} from './tests/layout-page-template-admin-web/config';
import {config as layoutSetPrototypeWebConfig} from './tests/layout-set-prototype-web/config';
import {config as lockedItemsWebConfig} from './tests/locked-items-web/config';
import {config as loginWebConfig} from './tests/login-web/config';
import {config as messageBoardsWebConfig} from './tests/message-boards-web/config';
import {config as multifactorAuthenticationConfig} from './tests/multi-factor-authentication-timebased-otp/config';
import {config as nestedPortletsWebConfig} from './tests/nested-portlets-web/config';
import {config as notificationWebConfig} from './tests/notification-web/config';
import {config as notificationsWebConfig} from './tests/notifications-web/config';
import {config as objectWebConfig} from './tests/object-web/config';
import {config as openIdLinkConfig} from './tests/openid-link/config';
import {config as osbFaroWebConfig} from './tests/osb-faro-web/config';
import {config as passwordPoliciesAdminWebConfig} from './tests/password-policies-admin-web/config';
import {config as portalDefaultPermissionsWebConfig} from './tests/portal-default-permissions-web/config';
import {config as portalLanguageOverrideWebConfig} from './tests/portal-language-override-web/config';
import {config as portalSearchAdminWebConfig} from './tests/portal-search-admin-web/config';
import {config as portalSearchWebConfig} from './tests/portal-search-web/config';
import {config as portalSecurityAuditWebConfig} from './tests/portal-security-audit-web/config';
import {config as portalSecurityContentSecurityPolicyConfig} from './tests/portal-security-content-security-policy/config';
import {config as portalSecurityLdapConfig} from './tests/portal-security-ldap/config';
import {config as portalSecurityScriptManagementWebConfig} from './tests/portal-security-script-management-web/config';
import {config as portalSecurityServiceAccessPolicyService} from './tests/portal-security-service-access-policy-service/config';
import {config as portalToolsRestBuilderTestImpl} from './tests/portal-tools-rest-builder-test-impl/config';
import {config as portalUserLocaleOptionsConfig} from './tests/portal-user-locale-options-web/config';
import {config as portalWebConfig} from './tests/portal-web/config';
import {config as portalWorkflowKaleoDesignerWebConfig} from './tests/portal-workflow-kaleo-designer-web/config';
import {config as portalWorkflowTaskWebConfig} from './tests/portal-workflow-task-web/config';
import {config as portletConfigurationCssWebConfig} from './tests/portlet-configuration-css-web/config';
import {config as portletConfigurationWebConfig} from './tests/portlet-configuration-web/config';
import {config as productNavigationProductMenuWeb} from './tests/product-navigation-product-menu-web/config';
import {config as productNavigationUserPersonalBarWebConfig} from './tests/product-navigation-user-personal-bar-web/config';
import {config as questionsWebConfig} from './tests/questions-web/config';
import {config as rolesAdminWebConfig} from './tests/roles-admin-web/config';
import {config as rssWebConfig} from './tests/rss-web/config';
import {config as samlWebConfig} from './tests/saml-web/config';
import {config as scimConfiguraitonWebConfig} from './tests/scim-configuration-web/config';
import {config as searchExperiencesWebConfig} from './tests/search-experiences-web/config';
import {config as segmentExperimentWebConfig} from './tests/segment-experiment-web/config';
import {config as segmentsWebConfig} from './tests/segments-web/config';
import {
	pageManagementSiteSetup,
	pageManagementSiteTeardown,
} from './tests/setup/page-management-site/config';
import {config as siteAdminWebConfig} from './tests/site-admin-web/config';
import {config as siteCmsSiteInitializerConfig} from './tests/site-cms-site-initializer/config';
import {config as siteNavigationAdminWebConfig} from './tests/site-navigation-admin-web/config';
import {config as siteNavigationBreadcrumbWebConfig} from './tests/site-navigation-breadcrumb-web/config';
import {config as siteNavigationDirectoryWebConfig} from './tests/site-navigation-directory-web/config';
import {config as siteNavigationLanguageWebConfig} from './tests/site-navigation-language-web/config';
import {config as siteNavigationMenuWebConfig} from './tests/site-navigation-menu-web/config';
import {config as smokeConfig} from './tests/smoke/config';
import {config as stagingConfig} from './tests/staging-configuration-web/config';
import {config as stylebookWebConfig} from './tests/style-book-web/config';
import {config as templateWebConfig} from './tests/template-web/config';
import {config as usersAdminWebConfig} from './tests/users-admin-web/config';
import {config as wikiWebConfig} from './tests/wiki-web/config';
import {config as customerConfig} from './tests/workspaces/liferay-customer-workspace/config';
import {config as commerceWorkspaceConfig} from './tests/workspaces/liferay-workspace-commerce/config';
import {config as jethr0Config} from './tests/workspaces/liferay-workspace-jethr0/config';
import {config as marketplaceConfig} from './tests/workspaces/liferay-workspace-marketplace/config';
const setupProjects = [pageManagementSiteSetup, pageManagementSiteTeardown];

export default defineConfig({
	expect: {
		timeout: 15 * 1000,
	},
	forbidOnly: !!process.env.CI,
	globalTimeout: 60 * 60 * 1000,
	projects: [
		accessibilityMenuWeb,
		accountAdminWebConfig,
		addressWebConfig,
		analyticsReportsJsComponentsWeb,
		analyticsSettingsWebConfig,
		analyticsWebConfig,
		announcementsWebConfig,
		assetCategoriesAdminWebConfig,
		assetPublisherWebConfig,
		assetTagsAdminWebConfig,
		batchPlannerConfig,
		blogsWebConfig,
		calendarWebConfig,
		captchaWebConfig,
		changeTrackingWebConfig,
		clientExtensionWebConfig,
		clientExtensionWebClusterConfig,
		commerceAccountWebConfig,
		commerceCartContentWebConfig,
		commerceChannelWebConfig,
		commerceCheckoutWebConfig,
		commerceCurrencyWebConfig,
		commerceDiscountContentWebConfig,
		commerceOrderWebConfig,
		commerceOrderContentWebConfig,
		commercePaymentsWebConfig,
		commerceProductContentSearchWebConfig,
		commerceProductContentWebConfig,
		commerceProductDefinitionsWebConfig,
		commerceProductOptionsWebConfig,
		commerceShippingEngineWebConfig,
		commerceSiteInitializerWebConfig,
		commerceTaxEngineWebConfig,
		commerceThemeMiniumWebConfig,
		commerceWishListWebConfig,
		commerceWorkspaceConfig,
		configurationAdminWebConfig,
		contentDashboardWebConfig,
		cookiesBannerWebConfig,
		customerConfig,
		depotWebConfig,
		dispatchWebConfig,
		documentLibraryWebConfig,
		dynamicDataMappingFormWebConfig,
		exportImportWebConfig,
		featureFlagWebConfig,
		fragmentWebConfig,
		friendlyURLConfig,
		frontendDataSetAdminWebConfig,
		frontendDataSetWebConfig,
		frontendEditorCKEditorWebConfig,
		frontendJsComponentsWebConfig,
		frontendJsSpaWebConfig,
		frontendJsWebConfig,
		frontendTaglibClayConfig,
		frontendTaglibConfig,
		frontendTheme,
		headlessBuilderImplConfig,
		headlessBuilderWebConfig,
		iframeWebConfig,
		itemSelectorTaglibConfig,
		jethr0Config,
		journalWebConfig,
		knowledgeBaseWebConfig,
		layoutAdminWebConfig,
		layoutContentPageEditorWebConfig,
		layoutLockedLayoutsWebConfig,
		layoutPageTemplateAdminWebConfig,
		layoutSetPrototypeWebConfig,
		lockedItemsWebConfig,
		loginWebConfig,
		marketplaceConfig,
		messageBoardsWebConfig,
		multifactorAuthenticationConfig,
		nestedPortletsWebConfig,
		notificationWebConfig,
		notificationsWebConfig,
		objectWebConfig,
		openIdLinkConfig,
		osbFaroWebConfig,
		passwordPoliciesAdminWebConfig,
		portalDefaultPermissionsWebConfig,
		portalLanguageOverrideWebConfig,
		portalSearchAdminWebConfig,
		portalSearchWebConfig,
		portalSecurityAuditWebConfig,
		portalSecurityContentSecurityPolicyConfig,
		portalSecurityLdapConfig,
		portalSecurityScriptManagementWebConfig,
		portalSecurityServiceAccessPolicyService,
		portalToolsRestBuilderTestImpl,
		portalUserLocaleOptionsConfig,
		portalWebConfig,
		portalWorkflowKaleoDesignerWebConfig,
		portalWorkflowTaskWebConfig,
		portletConfigurationCssWebConfig,
		portletConfigurationWebConfig,
		productNavigationProductMenuWeb,
		productNavigationUserPersonalBarWebConfig,
		questionsWebConfig,
		rolesAdminWebConfig,
		rssWebConfig,
		samlWebConfig,
		scimConfiguraitonWebConfig,
		searchExperiencesWebConfig,
		segmentExperimentWebConfig,
		segmentsWebConfig,
		siteAdminWebConfig,
		siteCmsSiteInitializerConfig,
		siteNavigationAdminWebConfig,
		siteNavigationBreadcrumbWebConfig,
		siteNavigationDirectoryWebConfig,
		siteNavigationLanguageWebConfig,
		siteNavigationMenuWebConfig,
		smokeConfig,
		stagingConfig,
		stylebookWebConfig,
		templateWebConfig,
		usersAdminWebConfig,
		wikiWebConfig,
		...setupProjects,
	],
	reporter: [
		[
			'html',
			{
				attachmentsBaseURL: process.env.CI
					? process.env.TESTRAY_CLOUD_STORAGE_BASE_URL
					: '',
				open: 'never',
			},
		],
		[
			'junit',
			{
				outputFile: 'test-results/TEST-playwright.xml',
			},
		],
	],
	retries: process.env.CI ? 1 : 0,
	testDir: './tests',
	timeout: 60 * 1000,
	use: {
		...devices['Desktop Chrome'],
		baseURL: process.env.PORTAL_URL
			? process.env.PORTAL_URL
			: 'http://localhost:8080',
		screenshot: 'only-on-failure',
		trace: 'retain-on-failure',
	},
	workers: 1,
});
