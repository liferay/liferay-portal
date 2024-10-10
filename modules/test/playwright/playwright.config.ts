/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import 'dotenv/config';
import {defineConfig, devices} from '@playwright/test';

import {config as accessibilityMenuWeb} from './tests/accessibility-menu-web/config';
import {config as accountAdminWebConfig} from './tests/account-admin-web/config';
import {config as analyticsReportsJsComponentsWeb} from './tests/analytics-reports-js-components-web/config';
import {config as analyticsSettingsWebConfig} from './tests/analytics-settings-web/config';
import {config as analyticsWebConfig} from './tests/analytics-web/config';
import {config as announcementsWebConfig} from './tests/announcements-web/config';
import {config as assetPublisherWebConfig} from './tests/asset-publisher-web/config';
import {config as batchPlannerConfig} from './tests/batch-planner/config';
import {config as blogsWebConfig} from './tests/blogs-web/config';
import {config as calendarWebConfig} from './tests/calendar-web/config';
import {config as changeTrackingWebConfig} from './tests/change-tracking-web/config';
import {config as clientExtensionWebConfig} from './tests/client-extension-web/config';
import {config as commerceConfig} from './tests/commerce/config';
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
import {config as frontendDataSetAdminWebConfig} from './tests/frontend-data-set-admin-web/config';
import {config as frontendDataSetWebConfig} from './tests/frontend-data-set-web/config';
import {config as frontendEditorCKEditorWebConfig} from './tests/frontend-editor-ckeditor-web/config';
import {config as frontendJsSpaWebConfig} from './tests/frontend-js-spa-web/config';
import {config as frontendTaglibClayConfig} from './tests/frontend-taglib-clay/config';
import {config as frontendTaglibConfig} from './tests/frontend-taglib/config';
import {config as headlessBuilderImplConfig} from './tests/headless-builder-impl/config';
import {config as headlessBuilderWebConfig} from './tests/headless-builder-web/config';
import {config as itemSelectorTaglibConfig} from './tests/item-selector-taglib/config';
import {config as journalWebConfig} from './tests/journal-web/config';
import {config as knowledgeBaseWebConfig} from './tests/knowledge-base-web/config';
import {config as layoutAdminWebConfig} from './tests/layout-admin-web/config';
import {config as layoutContentPageEditorWebConfig} from './tests/layout-content-page-editor-web/config';
import {config as layoutPageTemplateAdminWebConfig} from './tests/layout-page-template-admin-web/config';
import {config as layoutSetPrototypeWebConfig} from './tests/layout-set-prototype-web/config';
import {config as lockedItemsWebConfig} from './tests/locked-items-web/config';
import {config as loginWebConfig} from './tests/login-web/config';
import {config as messageBoardsWebConfig} from './tests/message-boards-web/config';
import {config as nestedPortletsWebConfig} from './tests/nested-portlets-web/config';
import {config as notificationWebConfig} from './tests/notification-web/config';
import {config as objectWebConfig} from './tests/object-web/config';
import {config as openIdLinkConfig} from './tests/openid-link/config';
import {config as osbFaroWebConfig} from './tests/osb-faro-web/config';
import {config as portalDefaultPermissionsWebConfig} from './tests/portal-default-permissions-web/config';
import {config as portalLanguageOverrideWebConfig} from './tests/portal-language-override-web/config';
import {config as portalSearchAdminWebConfig} from './tests/portal-search-admin-web/config';
import {config as portalSearchWebConfig} from './tests/portal-search-web/config';
import {config as portalSecurityScriptManagementWebConfig} from './tests/portal-security-script-management-web/config';
import {config as portalSecurityServiceAccessPolicyService} from './tests/portal-security-service-access-policy-service/config';
import {config as portalToolsRestBuilderTestImpl} from './tests/portal-tools-rest-builder-test-impl/config';
import {config as portalWorkflowKaleoDesignerWebConfig} from './tests/portal-workflow-kaleo-designer-web/config';
import {config as portalWorkflowTaskWebConfig} from './tests/portal-workflow-task-web/config';
import {config as portletConfigurationCssWebConfig} from './tests/portlet-configuration-css-web/config';
import {config as portletConfigurationWebConfig} from './tests/portlet-configuration-web/config';
import {config as productNavigationProductMenuWeb} from './tests/product-navigation-product-menu-web/config';
import {config as productNavigationUserPersonalBarWebConfig} from './tests/product-navigation-user-personal-bar-web/config';
import {config as questionsWebConfig} from './tests/questions-web/config';
import {config as rolesAdminWebConfig} from './tests/roles-admin-web/config';
import {config as samlWebConfig} from './tests/saml-web/config';
import {config as scimConfiguraitonWebConfig} from './tests/scim-configuration-web/config';
import {config as searchExperiencesWebConfig} from './tests/search-experiences-web/config';
import {config as segmentExperimentWebConfig} from './tests/segment-experiment-web/config';
import {
	pageManagementSiteSetup,
	pageManagementSiteTeardown,
} from './tests/setup/page-management-site/config';
import {config as siteAdminWebConfig} from './tests/site-admin-web/config';
import {config as siteNavigationAdminWebConfig} from './tests/site-navigation-admin-web/config';
import {config as siteNavigationLanguageWebConfig} from './tests/site-navigation-language-web/config';
import {config as stableConfig} from './tests/stable/config';
import {config as stagingConfig} from './tests/staging-configuration-web/config';
import {config as stylebookWebConfig} from './tests/style-book-web/config';
import {config as templateWebConfig} from './tests/template-web/config';
import {config as usersAdminWebConfig} from './tests/users-admin-web/config';
import {config as wikiWebConfig} from './tests/wiki-web/config';
import {config as customerConfig} from './tests/workspaces/liferay-customer-workspace/config';
import {config as partnerConfig} from './tests/workspaces/liferay-partner-workspace/config';
import {config as commerceWorkspaceConfig} from './tests/workspaces/liferay-workspace-commerce/config';
import {config as jethr0Config} from './tests/workspaces/liferay-workspace-jethr0/config';
import {config as marketplaceConfig} from './tests/workspaces/liferay-workspace-marketplace/config';

const setupProjects = [pageManagementSiteSetup, pageManagementSiteTeardown];

export default defineConfig({
	expect: {
		timeout: 15 * 1000,
	},
	forbidOnly: !!process.env.CI,
	projects: [
		accessibilityMenuWeb,
		accountAdminWebConfig,
		analyticsReportsJsComponentsWeb,
		analyticsSettingsWebConfig,
		analyticsWebConfig,
		depotWebConfig,
		announcementsWebConfig,
		assetPublisherWebConfig,
		batchPlannerConfig,
		blogsWebConfig,
		calendarWebConfig,
		cookiesBannerWebConfig,
		changeTrackingWebConfig,
		clientExtensionWebConfig,
		commerceConfig,
		commerceWorkspaceConfig,
		configurationAdminWebConfig,
		contentDashboardWebConfig,
		customerConfig,
		dispatchWebConfig,
		documentLibraryWebConfig,
		dynamicDataMappingFormWebConfig,
		exportImportWebConfig,
		featureFlagWebConfig,
		fragmentWebConfig,
		frontendDataSetAdminWebConfig,
		frontendDataSetWebConfig,
		frontendEditorCKEditorWebConfig,
		frontendJsSpaWebConfig,
		frontendTaglibClayConfig,
		frontendTaglibConfig,
		headlessBuilderImplConfig,
		headlessBuilderWebConfig,
		itemSelectorTaglibConfig,
		jethr0Config,
		journalWebConfig,
		knowledgeBaseWebConfig,
		layoutAdminWebConfig,
		layoutContentPageEditorWebConfig,
		layoutSetPrototypeWebConfig,
		layoutPageTemplateAdminWebConfig,
		lockedItemsWebConfig,
		loginWebConfig,
		marketplaceConfig,
		messageBoardsWebConfig,
		nestedPortletsWebConfig,
		notificationWebConfig,
		objectWebConfig,
		openIdLinkConfig,
		osbFaroWebConfig,
		partnerConfig,
		portalDefaultPermissionsWebConfig,
		portalLanguageOverrideWebConfig,
		portalSearchAdminWebConfig,
		portalSearchWebConfig,
		portalSecurityScriptManagementWebConfig,
		portalSecurityServiceAccessPolicyService,
		portalToolsRestBuilderTestImpl,
		portalWorkflowKaleoDesignerWebConfig,
		portalWorkflowTaskWebConfig,
		portletConfigurationCssWebConfig,
		portletConfigurationWebConfig,
		productNavigationProductMenuWeb,
		productNavigationUserPersonalBarWebConfig,
		questionsWebConfig,
		rolesAdminWebConfig,
		samlWebConfig,
		scimConfiguraitonWebConfig,
		searchExperiencesWebConfig,
		segmentExperimentWebConfig,
		siteAdminWebConfig,
		siteNavigationAdminWebConfig,
		siteNavigationLanguageWebConfig,
		stableConfig,
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
	retries: process.env.CI ? 2 : 0,
	testDir: './tests',
	timeout: 30 * 1000,
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
