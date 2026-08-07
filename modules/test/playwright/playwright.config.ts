/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReporterDescription, defineConfig, devices} from '@playwright/test';

import 'dotenv/config';

import {liferayConfig} from './liferay.config';
import {config as accessibilityMenuWeb} from './tests/accessibility-menu-web/main/config';
import {config as accountAdminWebConfig} from './tests/account-admin-web/main/config';
import {config as addressWebConfig} from './tests/address-web/main/config';
import {config as aiCreatorOpenAIWebConfig} from './tests/ai-creator-openai-web/main/config';
import {config as aiHubSiteInitializerConfig} from './tests/ai-hub-site-initializer/main/config';
import {config as analyticsClientJs} from './tests/analytics-client-js/main/config';
import {config as analyticsReportsJsComponentsWeb} from './tests/analytics-reports-js-components-web/main/config';
import {config as analyticsSettingsWebConfig} from './tests/analytics-settings-web/main/config';
import {config as analyticsWebConfig} from './tests/analytics-web/main/config';
import {config as announcementsWebConfig} from './tests/announcements-web/main/config';
import {config as applicationListTaglibConfig} from './tests/application-list-taglib/main/config';
import {config as assetCategoriesAdminWebConfig} from './tests/asset-categories-admin-web/main/config';
import {config as assetListWebConfig} from './tests/asset-list-web/main/config';
import {config as assetPublisherWebConfig} from './tests/asset-publisher-web/main/config';
import {config as assetPublisherWebRelatedAssetsConfig} from './tests/asset-publisher-web/related-assets/config';
import {config as assetTagsAdminWebConfig} from './tests/asset-tags-admin-web/main/config';
import {config as audiencesWebConfig} from './tests/audiences-web/main/config';
import {config as batchPlannerConfig} from './tests/batch-planner/main/config';
import {config as blogsWebConfig} from './tests/blogs-web/main/config';
import {config as calendarWebConfig} from './tests/calendar-web/main/config';
import {config as captchaWebClientExtensionConfig} from './tests/captcha-web/client-extension/config';
import {config as captchaWebConfig} from './tests/captcha-web/main/config';
import {config as changeTrackingWebLocalePrependConfig} from './tests/change-tracking-web/main-locale-prepend/config';
import {config as changeTrackingWebConfig} from './tests/change-tracking-web/main/config';
import {config as clientExtensionWebClusterConfig} from './tests/client-extension-web/cluster/config';
import {config as clientExtensionWebConfig} from './tests/client-extension-web/main/config';
import {config as commerceAccountWebConfig} from './tests/commerce/commerce-account-web/main/config';
import {config as commerceCartContentWebConfig} from './tests/commerce/commerce-cart-content-web/main/config';
import {config as commerceCatalogWebConfig} from './tests/commerce/commerce-catalog-web/main/config';
import {config as commerceChannelWebConfig} from './tests/commerce/commerce-channel-web/main/config';
import {config as commerceCheckoutWebClientExtensionConfig} from './tests/commerce/commerce-checkout-web/client-extension/config';
import {config as commerceCheckoutWebConfig} from './tests/commerce/commerce-checkout-web/main/config';
import {config as commerceCurrencyWebConfig} from './tests/commerce/commerce-currency-web/main/config';
import {config as commerceDiscountContentWebConfig} from './tests/commerce/commerce-discount-content-web/main/config';
import {config as commerceFragmentImplConfig} from './tests/commerce/commerce-fragment-impl/main/config';
import {config as commerceInitializerUtilConfig} from './tests/commerce/commerce-initializer-util/main/config';
import {config as commerceInventoryWebConfig} from './tests/commerce/commerce-inventory-web/main/config';
import {config as commerceOrderContentWebConfig} from './tests/commerce/commerce-order-content-web/main/config';
import {config as commerceOrderManagementConfig} from './tests/commerce/commerce-order-management/main/config';
import {config as commerceOrderWebConfig} from './tests/commerce/commerce-order-web/main/config';
import {config as commercePaymentsWebConfig} from './tests/commerce/commerce-payment-web/main/config';
import {config as commercePricingWebConfig} from './tests/commerce/commerce-pricing-web/main/config';
import {config as commerceProductAssetCategoriesWebConfig} from './tests/commerce/commerce-product-asset-categories-web/main/config';
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
import {config as systemSettingsOverrideConfig} from './tests/configuration-admin-web/override-properties-only/config';
import {config as systemSettingsOverrideOsgiConfig} from './tests/configuration-admin-web/override-with-osgi/config';
import {config as systemSettingsExportConfig} from './tests/configuration-admin-web/site-settings-export/config';
import {config as systemSettingsWithUIConfig} from './tests/configuration-admin-web/system-settings-with-ui/config';
import {config as consentManagementPlatformIntegrationConfig} from './tests/consent-management-platform-integration/main/config';
import {config as contactsWebConfig} from './tests/contacts-web/main/config';
import {config as contentDashboardWebConfig} from './tests/content-dashboard-web/main/config';
import {config as cookiesBannerWebConfig} from './tests/cookies-banner-web/main/config';
import {config as dataCleanupConfig} from './tests/data-cleanup/main/config';
import {config as depotWebConfig} from './tests/depot-web/main/config';
import {config as designLibraryWebConfig} from './tests/design-library-web/main/config';
import {config as dispatchWebConfig} from './tests/dispatch-web/main/config';
import {config as documentLibraryWebConfig} from './tests/document-library-web/main/config';
import {config as dynamicDataMappingFormWebConfig} from './tests/dynamic-data-mapping-form-web/main/config';
import {config as e2eCmsDxpConfig} from './tests/e2e-cms-dxp/main/config';
import {config as expandoWebConfig} from './tests/expando-web/main/config';
import {config as exportImportServiceConfig} from './tests/export-import-service/main/config';
import {config as exportImportWebConfig} from './tests/export-import-web/main/config';
import {config as exportImportWebRevampConfig} from './tests/export-import-web/revamp/config';
import {config as facebookLinkConfig} from './tests/facebook-link/main/config';
import {config as featureFlagWebConfig} from './tests/feature-flag-web/main/config';
import {config as fragmentWebConfig} from './tests/fragment-web/main/config';
import {config as friendlyURLConfig} from './tests/friendly-url-web/main/config';
import {config as frontendCssCadminWebConfig} from './tests/frontend-css-cadmin-web/main/config';
import {config as frontendDataSetAdminWebConfig} from './tests/frontend-data-set-admin-web/main/config';
import {config as frontendDataSetFragmentWebConfig} from './tests/frontend-data-set-fragment-web/main/config';
import {config as frontendDataSetWebConfig} from './tests/frontend-data-set-web/main/config';
import {config as frontendEditorAlloyEditorWebConfig} from './tests/frontend-editor-alloyeditor-web/main/config';
import {config as frontendEditorCKEditorWebConfig} from './tests/frontend-editor-ckeditor-web/main/config';
import {config as frontendJsAuiWebConfig} from './tests/frontend-js-aui-web/main/config';
import {config as frontendJsAuiWebSearchContainerSelectConfig} from './tests/frontend-js-aui-web/search-container-select/config';
import {config as frontendJsBootstrapSupportWebConfig} from './tests/frontend-js-bootstrap-support-web/main/config';
import {config as frontendJsClayWebConfig} from './tests/frontend-js-clay-web/main/config';
import {config as frontendJsComponentsWebConfig} from './tests/frontend-js-components-web/main/config';
import {config as frontendJsItemSelectorWebConfig} from './tests/frontend-js-item-selector-web/main/config';
import {config as frontendJsSpaWebConfig} from './tests/frontend-js-spa-web/main/config';
import {config as frontendJsWebConfig} from './tests/frontend-js-web/main/config';
import {config as frontendTaglibClayConfig} from './tests/frontend-taglib-clay/main/config';
import {config as frontendTaglibConfig} from './tests/frontend-taglib/main/config';
import {config as frontendTaglibSpaOffConfig} from './tests/frontend-taglib/spa-off/config';
import {config as frontendTheme} from './tests/frontend-theme/main/config';
import {config as headlessBuilderImplConfig} from './tests/headless-builder-impl/main/config';
import {config as headlessBuilderWebConfig} from './tests/headless-builder-web/main/config';
import {config as headlessDiscoveryWebConfig} from './tests/headless-discovery-web/main/config';
import {config as iframeWebConfig} from './tests/iframe-web/main/config';
import {config as itemSelectorTaglibConfig} from './tests/item-selector-taglib/main/config';
import {config as journalWebConfig} from './tests/journal-web/main/config';
import {config as knowledgeBaseWebConfig} from './tests/knowledge-base-web/main/config';
import {config as layoutAdminWebConfig} from './tests/layout-admin-web/main/config';
import {config as layoutContentPageEditorWebFormContainerConfig} from './tests/layout-content-page-editor-web/form-container/config';
import {config as layoutContentPageEditorWebFragmentsConfig} from './tests/layout-content-page-editor-web/fragments/config';
import {config as layoutContentPageEditorWebConfig} from './tests/layout-content-page-editor-web/main/config';
import {config as layoutLockedLayoutsWebConfig} from './tests/layout-locked-layouts-web/main/config';
import {config as layoutPageTemplateAdminWebConfig} from './tests/layout-page-template-admin-web/main/config';
import {config as layoutSetPrototypeWebConfig} from './tests/layout-set-prototype-web/main/config';
import {config as lockedItemsWebConfig} from './tests/locked-items-web/main/config';
import {config as loginWebMainCaptchaEnableConfig} from './tests/login-web/main-captcha-enable/config';
import {config as loginWebConfig} from './tests/login-web/main/config';
import {config as loginWebSetupAdminConfig} from './tests/login-web/setup-admin/config';
import {config as mapsConfig} from './tests/maps/main/config';
import {config as marketplaceAppManagerWebConfig} from './tests/marketplace-app-manager-web/main/config';
import {config as mcpServerWebConfig} from './tests/mcp-server-web/main/config';
import {config as messageBoardsWebConfig} from './tests/message-boards-web/main/config';
import {config as messageBoardsWebPaginationConfig} from './tests/message-boards-web/pagination/config';
import {config as multifactorAuthenticationEmailOTPConfig} from './tests/multi-factor-authentication-email-otp-web/main/config';
import {config as multifactorAuthenticationConfig} from './tests/multi-factor-authentication-timebased-otp-web/main/config';
import {config as multifactorAuthenticationWebConfig} from './tests/multi-factor-authentication-web/main/config';
import {config as nestedPortletsWebConfig} from './tests/nested-portlets-web/main/config';
import {config as notificationWebConfig} from './tests/notification-web/main/config';
import {config as notificationsWebConfig} from './tests/notifications-web/main/config';
import {config as oauthClientAdministrationConfig} from './tests/oauth-client-administration/main/config';
import {config as oauth2ProviderWebConfig} from './tests/oauth2-provider-web/main/config';
import {config as objectActionWebConfig} from './tests/object-web/action/config';
import {config as objectClientExtensionWebConfig} from './tests/object-web/client-extension/config';
import {config as objectContentPageIntegrationWebConfig} from './tests/object-web/content-page-integration/config';
import {config as objectEntryWebConfig} from './tests/object-web/entry/config';
import {config as objectExportImportWebConfig} from './tests/object-web/export-import/config';
import {config as objectFieldWebConfig} from './tests/object-web/field/config';
import {config as objectFolderWebConfig} from './tests/object-web/folder/config';
import {config as objectFormsIntegrationWebConfig} from './tests/object-web/forms-integration/config';
import {config as objectHierarchyWebConfig} from './tests/object-web/hierarchy/config';
import {config as objectLayoutWebConfig} from './tests/object-web/layout/config';
import {config as listTypeDefinitionsWebConfig} from './tests/object-web/list-type-definition/config';
import {config as objectDefinitionWebConfig} from './tests/object-web/main/config';
import {config as objectNotificationWebConfig} from './tests/object-web/notification/config';
import {config as objectRelationshipWebConfig} from './tests/object-web/relationship/config';
import {config as objectSalesforceWebConfig} from './tests/object-web/salesforce/config';
import {config as objectUpgradeWebConfig} from './tests/object-web/upgrade/config';
import {config as objectValidationWebConfig} from './tests/object-web/validation/config';
import {config as objectViewWebConfig} from './tests/object-web/view/config';
import {config as objectWorkflowWebConfig} from './tests/object-web/workflow/config';
import {config as openIdLinkConfig} from './tests/openid-link/main/config';
import {config as osbFaroWebAssetsConfig} from './tests/osb-faro-web/assets/config';
import {config as osbFaroWebEventsConfig} from './tests/osb-faro-web/events/config';
import {config as osbFaroWebConfig} from './tests/osb-faro-web/main/config';
import {config as osbFaroWebSegmentsConfig} from './tests/osb-faro-web/segments/config';
import {config as osbFaroWebSettingsConfig} from './tests/osb-faro-web/settings/config';
import {config as passwordPoliciesAdminWebFirstLoginConfig} from './tests/password-policies-admin-web/first-login/config';
import {config as passwordPoliciesAdminWebConfig} from './tests/password-policies-admin-web/main/config';
import {config as passwordPoliciesAdminWebSetupAdminConfig} from './tests/password-policies-admin-web/setup-admin/config';
import {config as portalDefaultPermissionsWebConfig} from './tests/portal-default-permissions-web/main/config';
import {config as portalImplMainConfig} from './tests/portal-impl/main/config';
import {config as portalImplPortletConfig} from './tests/portal-impl/portlet/config';
import {config as portalLanguageOverrideWebClientExtensionConfig} from './tests/portal-language-override-web/client-extension/config';
import {config as portalLanguageOverrideWebConfig} from './tests/portal-language-override-web/main/config';
import {config as portalSearchAdminWebConfig} from './tests/portal-search-admin-web/main/config';
import {config as portalSearchWebConfig} from './tests/portal-search-web/main/config';
import {config as portalSecurityAuditWebConfig} from './tests/portal-security-audit-web/main/config';
import {config as portalSecurityContentSecurityPolicyConfig} from './tests/portal-security-content-security-policy/main/config';
import {config as portalSecurityLdapConfig} from './tests/portal-security-ldap/main/config';
import {config as portalSecurityScriptManagementWebConfig} from './tests/portal-security-script-management-web/main/config';
import {config as portalSecurityServiceAccessPolicyService} from './tests/portal-security-service-access-policy-service/main/config';
import {config as portalSettingsAuthenticationOpenSSOWebConfig} from './tests/portal-settings-authentication-opensso-web/main/config';
import {config as portalToolsRestBuilderTestImpl} from './tests/portal-tools-rest-builder-test-impl/main/config';
import {config as portalUserLocaleOptionsConfig} from './tests/portal-user-locale-options-web/main/config';
import {config as portalWebCDNConfig} from './tests/portal-web/cdn/config';
import {config as portalWebConfig} from './tests/portal-web/main/config';
import {config as portalWorkflowKaleoDesignerWebConfig} from './tests/portal-workflow-kaleo-designer-web/main/config';
import {config as portalWorkflowKaleoFormsWebConfig} from './tests/portal-workflow-kaleo-forms-web/main/config';
import {config as portalWorkflowMetricsWebConfig} from './tests/portal-workflow-metrics-web/main/config';
import {config as portalWorkflowTaskWebConfig} from './tests/portal-workflow-task-web/main/config';
import {config as portletConfigurationCssWebConfig} from './tests/portlet-configuration-css-web/main/config';
import {config as productAnalyticsWebConfig} from './tests/product-analytics-web/main/config';
import {config as productNavigationApplicationsMenuConfig} from './tests/product-navigation-applications-menu/main/config';
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
import {config as seoStudioWebConfig} from './tests/seo-studio-web/main/config';
import {config as serverAdminWebConfig} from './tests/server-admin-web/main/config';
import {config as pageManagementSiteConfig} from './tests/setup/page-management-site/main/config';
import {config as pageManagementSiteTeardownConfig} from './tests/setup/page-management-site/teardown/config';
import {config as seoStudioSiteConfig} from './tests/setup/seo-studio-site/main/config';
import {config as seoStudioSiteTeardownConfig} from './tests/setup/seo-studio-site/teardown/config';
import {config as siteCmsSiteConfig} from './tests/setup/site-cms-site/main/config';
import {config as siteCmsSiteTeardownConfig} from './tests/setup/site-cms-site/teardown/config';
import {config as siteAdminWebConfig} from './tests/site-admin-web/main/config';
import {config as siteCmpSiteInitializerConfig} from './tests/site-cmp-site-initializer/main/config';
import {config as siteCmsSiteInitializerConfig} from './tests/site-cms-site-initializer/main/config';
import {config as siteCmsSiteInitializerPermissionsConfig} from './tests/site-cms-site-initializer/permissions/config';
import {config as siteCmsSiteInitializerStructureBuilderConfig} from './tests/site-cms-site-initializer/structure-builder/config';
import {config as siteCmsStandaloneSiteInitializerConfig} from './tests/site-cms-standalone-site-initializer/main/config';
import {config as siteDsrSiteInitializerConfig} from './tests/site-dsr-site-initializer/main/config';
import {config as siteMySitesWebConfig} from './tests/site-my-sites-web/main/config';
import {config as siteNavigationAdminWebConfig} from './tests/site-navigation-admin-web/main/config';
import {config as siteNavigationBreadcrumbWebConfig} from './tests/site-navigation-breadcrumb-web/main/config';
import {config as siteNavigationDirectoryWebConfig} from './tests/site-navigation-directory-web/main/config';
import {config as siteNavigationLanguageWebConfig} from './tests/site-navigation-language-web/main/config';
import {config as siteNavigationMenuWebConfig} from './tests/site-navigation-menu-web/main/config';
import {config as siteNavigationSiteMapWebConfig} from './tests/site-navigation-site-map-web/main/config';
import {config as sitePimSiteInitializerConfig} from './tests/site-pim-site-initializer/main/config';
import {config as siteSitemapWebConfig} from './tests/site-sitemap-web/main/config';
import {config as siteTeamsWebConfig} from './tests/site-teams-web/main/config';
import {config as smokeConfig} from './tests/smoke/main/config';
import {config as stagingConfig} from './tests/staging-configuration-web/main/config';
import {config as stylebookWebConfig} from './tests/style-book-web/main/config';
import {config as templateWebConfig} from './tests/template-web/main/config';
import {config as translationWebConfig} from './tests/translation-web/main/config';
import {config as trashWebConfig} from './tests/trash-web/main/config';
import {config as usersAdminWebEmailConfig} from './tests/users-admin-web/email/config';
import {config as usersAdminWebConfig} from './tests/users-admin-web/main/config';
import {config as usersAdminWebPermissionsConfig} from './tests/users-admin-web/permissions/config';
import {config as usersAdminWebPropertiesConfig} from './tests/users-admin-web/properties/config';
import {config as utilTaglibConfig} from './tests/util-taglib/main/config';
import {config as wikiWebConfig} from './tests/wiki-web/main/config';
import {config as customerConfig} from './tests/workspaces/liferay-customer-workspace/main/config';
import {config as commerceWorkspaceConfig} from './tests/workspaces/liferay-workspace-commerce/main/config';
import {config as marketplaceConfig} from './tests/workspaces/liferay-workspace-marketplace/main/config';

const setupProjects = [
	pageManagementSiteConfig,
	pageManagementSiteTeardownConfig,
	seoStudioSiteConfig,
	seoStudioSiteTeardownConfig,
	siteCmsSiteConfig,
	siteCmsSiteTeardownConfig,
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
		aiCreatorOpenAIWebConfig,
		aiHubSiteInitializerConfig,
		analyticsClientJs,
		analyticsReportsJsComponentsWeb,
		analyticsSettingsWebConfig,
		analyticsWebConfig,
		announcementsWebConfig,
		applicationListTaglibConfig,
		assetCategoriesAdminWebConfig,
		assetListWebConfig,
		assetPublisherWebConfig,
		assetPublisherWebRelatedAssetsConfig,
		assetTagsAdminWebConfig,
		audiencesWebConfig,
		batchPlannerConfig,
		blogsWebConfig,
		calendarWebConfig,
		captchaWebClientExtensionConfig,
		captchaWebConfig,
		changeTrackingWebConfig,
		changeTrackingWebLocalePrependConfig,
		clientExtensionWebConfig,
		clientExtensionWebClusterConfig,
		commerceAccountWebConfig,
		commerceCartContentWebConfig,
		commerceCatalogWebConfig,
		commerceChannelWebConfig,
		commerceCheckoutWebClientExtensionConfig,
		commerceCheckoutWebConfig,
		commerceCurrencyWebConfig,
		commerceDiscountContentWebConfig,
		commerceFragmentImplConfig,
		commerceInitializerUtilConfig,
		commerceInventoryWebConfig,
		commerceOrderManagementConfig,
		commerceOrderWebConfig,
		commerceOrderContentWebConfig,
		commercePaymentsWebConfig,
		commercePricingWebConfig,
		commerceProductAssetCategoriesWebConfig,
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
		consentManagementPlatformIntegrationConfig,
		contactsWebConfig,
		systemSettingsExportConfig,
		systemSettingsOverrideOsgiConfig,
		systemSettingsOverrideConfig,
		systemSettingsWithUIConfig,
		contentDashboardWebConfig,
		cookiesBannerWebConfig,
		customerConfig,
		depotWebConfig,
		designLibraryWebConfig,
		dispatchWebConfig,
		documentLibraryWebConfig,
		dynamicDataMappingFormWebConfig,
		e2eCmsDxpConfig,
		expandoWebConfig,
		exportImportServiceConfig,
		exportImportWebConfig,
		exportImportWebRevampConfig,
		facebookLinkConfig,
		featureFlagWebConfig,
		fragmentWebConfig,
		friendlyURLConfig,
		frontendCssCadminWebConfig,
		frontendDataSetAdminWebConfig,
		frontendDataSetFragmentWebConfig,
		frontendDataSetWebConfig,
		frontendEditorAlloyEditorWebConfig,
		frontendEditorCKEditorWebConfig,
		frontendJsAuiWebConfig,
		frontendJsAuiWebSearchContainerSelectConfig,
		frontendJsBootstrapSupportWebConfig,
		frontendJsClayWebConfig,
		frontendJsComponentsWebConfig,
		frontendJsItemSelectorWebConfig,
		frontendJsSpaWebConfig,
		frontendJsWebConfig,
		frontendTaglibClayConfig,
		frontendTaglibConfig,
		frontendTaglibSpaOffConfig,
		frontendTheme,
		headlessBuilderImplConfig,
		headlessBuilderWebConfig,
		headlessDiscoveryWebConfig,
		iframeWebConfig,
		itemSelectorTaglibConfig,
		journalWebConfig,
		knowledgeBaseWebConfig,
		layoutAdminWebConfig,
		layoutContentPageEditorWebConfig,
		layoutContentPageEditorWebFormContainerConfig,
		layoutContentPageEditorWebFragmentsConfig,
		layoutLockedLayoutsWebConfig,
		layoutPageTemplateAdminWebConfig,
		layoutSetPrototypeWebConfig,
		lockedItemsWebConfig,
		loginWebConfig,
		loginWebMainCaptchaEnableConfig,
		loginWebSetupAdminConfig,
		mapsConfig,
		marketplaceAppManagerWebConfig,
		marketplaceConfig,
		mcpServerWebConfig,
		messageBoardsWebConfig,
		messageBoardsWebPaginationConfig,
		multifactorAuthenticationConfig,
		multifactorAuthenticationEmailOTPConfig,
		multifactorAuthenticationWebConfig,
		nestedPortletsWebConfig,
		notificationWebConfig,
		notificationsWebConfig,
		oauthClientAdministrationConfig,
		oauth2ProviderWebConfig,
		listTypeDefinitionsWebConfig,
		objectActionWebConfig,
		objectClientExtensionWebConfig,
		objectContentPageIntegrationWebConfig,
		objectDefinitionWebConfig,
		objectEntryWebConfig,
		objectExportImportWebConfig,
		objectFieldWebConfig,
		objectFolderWebConfig,
		objectFormsIntegrationWebConfig,
		objectHierarchyWebConfig,
		objectLayoutWebConfig,
		objectNotificationWebConfig,
		objectRelationshipWebConfig,
		objectSalesforceWebConfig,
		objectUpgradeWebConfig,
		objectValidationWebConfig,
		objectViewWebConfig,
		objectWorkflowWebConfig,
		openIdLinkConfig,
		osbFaroWebAssetsConfig,
		osbFaroWebConfig,
		osbFaroWebEventsConfig,
		osbFaroWebSegmentsConfig,
		osbFaroWebSettingsConfig,
		passwordPoliciesAdminWebConfig,
		passwordPoliciesAdminWebFirstLoginConfig,
		passwordPoliciesAdminWebSetupAdminConfig,
		portalDefaultPermissionsWebConfig,
		portalImplMainConfig,
		portalImplPortletConfig,
		portalLanguageOverrideWebClientExtensionConfig,
		portalLanguageOverrideWebConfig,
		portalSearchAdminWebConfig,
		portalSearchWebConfig,
		portalSecurityAuditWebConfig,
		portalSecurityContentSecurityPolicyConfig,
		portalSecurityLdapConfig,
		portalSecurityScriptManagementWebConfig,
		portalSecurityServiceAccessPolicyService,
		portalSettingsAuthenticationOpenSSOWebConfig,
		portalToolsRestBuilderTestImpl,
		portalUserLocaleOptionsConfig,
		portalWebCDNConfig,
		portalWebConfig,
		portalWorkflowKaleoDesignerWebConfig,
		portalWorkflowKaleoFormsWebConfig,
		portalWorkflowMetricsWebConfig,
		portalWorkflowTaskWebConfig,
		portletConfigurationCssWebConfig,
		productAnalyticsWebConfig,
		productNavigationApplicationsMenuConfig,
		productNavigationControlMenuWeb,
		productNavigationProductMenuWeb,
		productNavigationUserPersonalBarWebConfig,
		questionsWebConfig,
		redirectWebConfig,
		rolesAdminWebConfig,
		rolesSelectorWebConfig,
		rssWebConfig,
		dataCleanupConfig,
		samlWebConfig,
		scimConfiguraitonWebConfig,
		searchExperiencesWebConfig,
		segmentExperimentWebConfig,
		segmentsWebConfig,
		seoStudioWebConfig,
		serverAdminWebConfig,
		siteAdminWebConfig,
		siteCmpSiteInitializerConfig,
		siteCmsSiteInitializerConfig,
		siteCmsSiteInitializerPermissionsConfig,
		siteCmsSiteInitializerStructureBuilderConfig,
		siteCmsStandaloneSiteInitializerConfig,
		siteDsrSiteInitializerConfig,
		siteMySitesWebConfig,
		siteNavigationAdminWebConfig,
		siteNavigationBreadcrumbWebConfig,
		siteNavigationDirectoryWebConfig,
		siteNavigationLanguageWebConfig,
		siteNavigationMenuWebConfig,
		siteNavigationSiteMapWebConfig,
		sitePimSiteInitializerConfig,
		siteSitemapWebConfig,
		siteTeamsWebConfig,
		smokeConfig,
		stagingConfig,
		stylebookWebConfig,
		templateWebConfig,
		translationWebConfig,
		trashWebConfig,
		usersAdminWebConfig,
		usersAdminWebEmailConfig,
		usersAdminWebPermissionsConfig,
		usersAdminWebPropertiesConfig,
		utilTaglibConfig,
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
	timeout: 90 * 1000,
	use: {
		...devices['Desktop Chrome'],
		baseURL: liferayConfig.environment.baseUrl,
		screenshot: 'only-on-failure',
		trace: 'retain-on-failure',
	},
	workers: 1,
});
