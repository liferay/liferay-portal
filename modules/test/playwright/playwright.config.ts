/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReporterDescription, defineConfig, devices} from '@playwright/test';

import 'dotenv/config';

import {config as accessibilityMenuWeb} from './tests/accessibility-menu-web/main/config';
import {config as accountAdminWebConfig} from './tests/account-admin-web/main/config';
import {config as addressWebConfig} from './tests/address-web/main/config';
import {config as analyticsClientJs} from './tests/analytics-client-js/main/config';
import {config as analyticsReportsJsComponentsWeb} from './tests/analytics-reports-js-components-web/main/config';
import {config as analyticsSettingsWebConfig} from './tests/analytics-settings-web/main/config';
import {config as analyticsWebConfig} from './tests/analytics-web/main/config';
import {config as announcementsWebConfig} from './tests/announcements-web/main/config';
import {config as assetCategoriesAdminWebConfig} from './tests/asset-categories-admin-web/main/config';
import {config as assetPublisherWebConfig} from './tests/asset-publisher-web/main/config';
import {config as assetTagsAdminWebConfig} from './tests/asset-tags-admin-web/main/config';
import {config as batchPlannerConfig} from './tests/batch-planner/main/config';
import {config as blogsWebConfig} from './tests/blogs-web/main/config';
import {config as calendarWebConfig} from './tests/calendar-web/main/config';
import {config as captchaWebClientExtensionConfig} from './tests/captcha-web/client-extension/config';
import {config as captchaWebConfig} from './tests/captcha-web/main/config';
import {config as changeTrackingWebConfig} from './tests/change-tracking-web/main/config';
import {config as clientExtensionWebClusterConfig} from './tests/client-extension-web/cluster/config';
import {config as clientExtensionWebConfig} from './tests/client-extension-web/main/config';
import {config as commerceAccountWebConfig} from './tests/commerce/commerce-account-web/main/config';
import {config as commerceCartContentWebConfig} from './tests/commerce/commerce-cart-content-web/main/config';
import {config as commerceCatalogWebConfig} from './tests/commerce/commerce-catalog-web/main/config';
import {config as commerceChannelWebConfig} from './tests/commerce/commerce-channel-web/main/config';
import {config as commerceCheckoutWebConfig} from './tests/commerce/commerce-checkout-web/main/config';
import {config as commerceCurrencyWebConfig} from './tests/commerce/commerce-currency-web/main/config';
import {config as commerceDiscountContentWebConfig} from './tests/commerce/commerce-discount-content-web/main/config';
import {config as commerceInitializerUtilConfig} from './tests/commerce/commerce-initializer-util/main/config';
import {config as commerceOrderContentWebConfig} from './tests/commerce/commerce-order-content-web/main/config';
import {config as commerceOrderWebConfig} from './tests/commerce/commerce-order-web/main/config';
import {config as commercePaymentsWebConfig} from './tests/commerce/commerce-payment-web/main/config';
import {config as commerceProductContentSearchWebConfig} from './tests/commerce/commerce-product-content-search-web/main/config';
import {config as commerceProductContentWebConfig} from './tests/commerce/commerce-product-content-web/main/config';
import {config as commerceProductDefinitionsWebConfig} from './tests/commerce/commerce-product-definitions-web/main/config';
import {config as commerceProductOptionsWebConfig} from './tests/commerce/commerce-product-options-web/main/config';
import {config as commerceShippingEngineWebConfig} from './tests/commerce/commerce-shipping-engine-fixed-web/main/config';
import {config as commerceSiteInitializerWebConfig} from './tests/commerce/commerce-site-initializer/main/config';
import {config as commerceTaxEngineWebConfig} from './tests/commerce/commerce-tax-engine-web/main/config';
import {config as commerceThemeMiniumWebConfig} from './tests/commerce/commerce-theme-minium/main/config';
import {config as commerceWishListWebConfig} from './tests/commerce/commerce-wish-list-web/main/config';
import {config as configurationAdminWebConfig} from './tests/configuration-admin-web/main/config';
import {config as contentDashboardWebConfig} from './tests/content-dashboard-web/main/config';
import {config as cookiesBannerWebConfig} from './tests/cookies-banner-web/main/config';
import {config as depotWebConfig} from './tests/depot-web/main/config';
import {config as dispatchWebConfig} from './tests/dispatch-web/main/config';
import {config as documentLibraryWebConfig} from './tests/document-library-web/main/config';
import {config as dynamicDataMappingFormWebConfig} from './tests/dynamic-data-mapping-form-web/main/config';
import {config as exportImportServiceConfig} from './tests/export-import-service/main/config';
import {config as exportImportWebConfig} from './tests/export-import-web/main/config';
import {config as featureFlagWebConfig} from './tests/feature-flag-web/main/config';
import {config as fragmentWebConfig} from './tests/fragment-web/main/config';
import {config as friendlyURLConfig} from './tests/friendly-url-web/main/config';
import {config as frontendDataSetAdminWebConfig} from './tests/frontend-data-set-admin-web/main/config';
import {config as frontendDataSetWebConfig} from './tests/frontend-data-set-web/main/config';
import {config as frontendEditorCKEditorWebConfig} from './tests/frontend-editor-ckeditor-web/main/config';
import {config as frontendJsBootstrapSupportWebConfig} from './tests/frontend-js-bootstrap-support-web/main/config';
import {config as frontendJsComponentsWebConfig} from './tests/frontend-js-components-web/main/config';
import {config as frontendJsSpaWebConfig} from './tests/frontend-js-spa-web/main/config';
import {config as frontendJsWebConfig} from './tests/frontend-js-web/main/config';
import {config as frontendTaglibClayConfig} from './tests/frontend-taglib-clay/main/config';
import {config as frontendTaglibConfig} from './tests/frontend-taglib/main/config';
import {config as frontendTaglibSpaOffConfig} from './tests/frontend-taglib/spa-off/config';
import {config as frontendTheme} from './tests/frontend-theme/main/config';
import {config as headlessBuilderImplConfig} from './tests/headless-builder-impl/main/config';
import {config as headlessBuilderWebConfig} from './tests/headless-builder-web/main/config';
import {config as iframeWebConfig} from './tests/iframe-web/main/config';
import {config as itemSelectorTaglibConfig} from './tests/item-selector-taglib/main/config';
import {config as journalWebConfig} from './tests/journal-web/main/config';
import {config as knowledgeBaseWebConfig} from './tests/knowledge-base-web/main/config';
import {config as layoutAdminWebConfig} from './tests/layout-admin-web/main/config';
import {config as layoutContentPageEditorWebConfig} from './tests/layout-content-page-editor-web/main/config';
import {config as layoutLockedLayoutsWebConfig} from './tests/layout-locked-layouts-web/main/config';
import {config as layoutPageTemplateAdminWebConfig} from './tests/layout-page-template-admin-web/main/config';
import {config as layoutSetPrototypeWebConfig} from './tests/layout-set-prototype-web/main/config';
import {config as lockedItemsWebConfig} from './tests/locked-items-web/main/config';
import {config as loginWebConfig} from './tests/login-web/main/config';
import {config as loginWebSetupAdminConfig} from './tests/login-web/setup-admin/config';
import {config as messageBoardsWebConfig} from './tests/message-boards-web/main/config';
import {config as multifactorAuthenticationConfig} from './tests/multi-factor-authentication-timebased-otp-web/main/config';
import {config as multifactorAuthenticationWebConfig} from './tests/multi-factor-authentication-web/main/config';
import {config as nestedPortletsWebConfig} from './tests/nested-portlets-web/main/config';
import {config as notificationWebConfig} from './tests/notification-web/main/config';
import {config as notificationsWebConfig} from './tests/notifications-web/main/config';
import {config as objectWebConfig} from './tests/object-web/main/config';
import {config as openIdLinkConfig} from './tests/openid-link/main/config';
import {config as osbFaroWebConfig} from './tests/osb-faro-web/main/config';
import {config as passwordPoliciesAdminWebFirstLoginConfig} from './tests/password-policies-admin-web/first-login/config';
import {config as passwordPoliciesAdminWebConfig} from './tests/password-policies-admin-web/main/config';
import {config as passwordPoliciesAdminWebSetupAdminConfig} from './tests/password-policies-admin-web/setup-admin/config';
import {config as portalDefaultPermissionsWebConfig} from './tests/portal-default-permissions-web/main/config';
import {config as portalLanguageOverrideWebConfig} from './tests/portal-language-override-web/main/config';
import {config as portalSearchAdminWebConfig} from './tests/portal-search-admin-web/main/config';
import {config as portalSearchWebConfig} from './tests/portal-search-web/main/config';
import {config as portalSecurityAuditWebConfig} from './tests/portal-security-audit-web/main/config';
import {config as portalSecurityContentSecurityPolicyConfig} from './tests/portal-security-content-security-policy/main/config';
import {config as portalSecurityLdapConfig} from './tests/portal-security-ldap/main/config';
import {config as portalSecurityScriptManagementWebConfig} from './tests/portal-security-script-management-web/main/config';
import {config as portalSecurityServiceAccessPolicyService} from './tests/portal-security-service-access-policy-service/main/config';
import {config as portalToolsRestBuilderTestImpl} from './tests/portal-tools-rest-builder-test-impl/main/config';
import {config as portalUserLocaleOptionsConfig} from './tests/portal-user-locale-options-web/main/config';
import {config as portalWebConfig} from './tests/portal-web/main/config';
import {config as portalWorkflowKaleoDesignerWebConfig} from './tests/portal-workflow-kaleo-designer-web/main/config';
import {config as portalWorkflowKaleoFormsWebConfig} from './tests/portal-workflow-kaleo-forms-web/main/config';
import {config as portalWorkflowMetricsWebConfig} from './tests/portal-workflow-metrics-web/main/config';
import {config as portalWorkflowTaskWebConfig} from './tests/portal-workflow-task-web/main/config';
import {config as portletConfigurationCssWebConfig} from './tests/portlet-configuration-css-web/main/config';
import {config as productNavigationControlMenuWeb} from './tests/product-navigation-control-menu-web/main/config';
import {config as productNavigationProductMenuWeb} from './tests/product-navigation-product-menu-web/main/config';
import {config as productNavigationUserPersonalBarWebConfig} from './tests/product-navigation-user-personal-bar-web/main/config';
import {config as questionsWebConfig} from './tests/questions-web/main/config';
import {config as redirectWebConfig} from './tests/redirect-web/main/config';
import {config as rolesAdminWebConfig} from './tests/roles-admin-web/main/config';
import {config as rolesSelectorWebConfig} from './tests/roles-selector-web/main/config';
import {config as rssWebConfig} from './tests/rss-web/main/config';
import {config as samlWebConfig} from './tests/saml-web/main/config';
import {config as scimConfiguraitonWebConfig} from './tests/scim-configuration-web/main/config';
import {config as searchExperiencesWebConfig} from './tests/search-experiences-web/main/config';
import {config as segmentExperimentWebConfig} from './tests/segment-experiment-web/main/config';
import {config as segmentsWebConfig} from './tests/segments-web/main/config';
import {config as pageManagementSiteConfig} from './tests/setup/page-management-site/main/config';
import {config as pageManagementSiteTeardownConfig} from './tests/setup/page-management-site/teardown/config';
import {config as siteAdminWebConfig} from './tests/site-admin-web/main/config';
import {config as siteCmsSiteInitializerConfig} from './tests/site-cms-site-initializer/main/config';
import {config as siteNavigationAdminWebConfig} from './tests/site-navigation-admin-web/main/config';
import {config as siteNavigationBreadcrumbWebConfig} from './tests/site-navigation-breadcrumb-web/main/config';
import {config as siteNavigationDirectoryWebConfig} from './tests/site-navigation-directory-web/main/config';
import {config as siteNavigationLanguageWebConfig} from './tests/site-navigation-language-web/main/config';
import {config as siteNavigationMenuWebConfig} from './tests/site-navigation-menu-web/main/config';
import {config as siteSitemapWebConfig} from './tests/site-sitemap-web/main/config';
import {config as smokeConfig} from './tests/smoke/main/config';
import {config as stagingConfig} from './tests/staging-configuration-web/main/config';
import {config as stylebookWebConfig} from './tests/style-book-web/main/config';
import {config as templateWebConfig} from './tests/template-web/main/config';
import {config as usersAdminWebConfig} from './tests/users-admin-web/main/config';
import {config as usersAdminWebPermissionsConfig} from './tests/users-admin-web/permissions/config';
import {config as wikiWebConfig} from './tests/wiki-web/main/config';
import {config as customerConfig} from './tests/workspaces/liferay-customer-workspace/main/config';
import {config as commerceWorkspaceConfig} from './tests/workspaces/liferay-workspace-commerce/main/config';
import {config as jethr0Config} from './tests/workspaces/liferay-workspace-jethr0/main/config';
import {config as marketplaceConfig} from './tests/workspaces/liferay-workspace-marketplace/main/config';

const setupProjects = [
	pageManagementSiteConfig,
	pageManagementSiteTeardownConfig,
];

const resultsPath = 'test-results/TEST-playwright.xml';

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
		analyticsClientJs,
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
		captchaWebClientExtensionConfig,
		captchaWebConfig,
		changeTrackingWebConfig,
		clientExtensionWebConfig,
		clientExtensionWebClusterConfig,
		commerceAccountWebConfig,
		commerceCartContentWebConfig,
		commerceCatalogWebConfig,
		commerceChannelWebConfig,
		commerceCheckoutWebConfig,
		commerceCurrencyWebConfig,
		commerceDiscountContentWebConfig,
		commerceInitializerUtilConfig,
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
		exportImportServiceConfig,
		exportImportWebConfig,
		featureFlagWebConfig,
		fragmentWebConfig,
		friendlyURLConfig,
		frontendDataSetAdminWebConfig,
		frontendDataSetWebConfig,
		frontendEditorCKEditorWebConfig,
		frontendJsBootstrapSupportWebConfig,
		frontendJsComponentsWebConfig,
		frontendJsSpaWebConfig,
		frontendJsWebConfig,
		frontendTaglibClayConfig,
		frontendTaglibConfig,
		frontendTaglibSpaOffConfig,
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
		loginWebSetupAdminConfig,
		marketplaceConfig,
		messageBoardsWebConfig,
		multifactorAuthenticationConfig,
		multifactorAuthenticationWebConfig,
		nestedPortletsWebConfig,
		notificationWebConfig,
		notificationsWebConfig,
		objectWebConfig,
		openIdLinkConfig,
		osbFaroWebConfig,
		passwordPoliciesAdminWebConfig,
		passwordPoliciesAdminWebFirstLoginConfig,
		passwordPoliciesAdminWebSetupAdminConfig,
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
		portalWorkflowKaleoFormsWebConfig,
		portalWorkflowMetricsWebConfig,
		portalWorkflowTaskWebConfig,
		portletConfigurationCssWebConfig,
		productNavigationControlMenuWeb,
		productNavigationProductMenuWeb,
		productNavigationUserPersonalBarWebConfig,
		questionsWebConfig,
		redirectWebConfig,
		rolesAdminWebConfig,
		rolesSelectorWebConfig,
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
		siteSitemapWebConfig,
		smokeConfig,
		stagingConfig,
		stylebookWebConfig,
		templateWebConfig,
		usersAdminWebConfig,
		usersAdminWebPermissionsConfig,
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
				outputFile: resultsPath,
			},
		],
		...(process.env.ci
			? ([
					[
						'./reporters/FlakyTestReporter',
						{
							resultsPath,
						},
					],
				] as ReporterDescription[])
			: []),
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
