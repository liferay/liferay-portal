/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {userGroupsPageTest} from '../../../fixtures/userGroupsPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi} from '../../../utils/performLogin';
import {localizationPagesTest} from '../../site-admin-web/main/fixtures/localizationPagesTest';
import {layoutSetPrototypePageTest} from './fixtures/layoutSetPrototypePageTest';
import createSiteTemplate from './utils/createSiteTemplate';

const DEFAULT_VIRTUAL_INSTANCE_NAME = 'www.able.com';
const VIRTUAL_INSTANCE_DOMAIN = 'able.com';
const VIRTUAL_INSTANCE_FULL_URL = `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}`;

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
	}),
	loginTest(),
	localizationPagesTest
);

const testWithSiteTemplateSync = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35443': {enabled: true},
		'LPD-82107': {enabled: true},
	}),
	globalMenuPagesTest,
	layoutSetPrototypePageTest,
	loginTest(),
	productMenuPageTest,
	siteSettingsPagesTest,
	userGroupsPageTest,
	usersAndOrganizationsPagesTest
);

test(
	'Change localization after Site Template is added in virtual instance',
	{tag: ['@LPS-180299']},
	async ({apiHelpers, localizationInstanceSettingsPage, page}) => {
		test.slow();

		const virtualInstance =
			await apiHelpers.headlessPortalInstance.addVirtualInstance({
				domain: VIRTUAL_INSTANCE_DOMAIN,
				portalInstanceId: DEFAULT_VIRTUAL_INSTANCE_NAME,
				virtualHost: DEFAULT_VIRTUAL_INSTANCE_NAME,
			});
		apiHelpers.data.push({
			id: virtualInstance.portalInstanceId,
			type: 'virtual-instance',
		});

		await performLoginViaApi({
			domain: `@${VIRTUAL_INSTANCE_DOMAIN}`,
			loginUrl: VIRTUAL_INSTANCE_FULL_URL,
			page,
			screenName: 'test',
		});

		await localizationInstanceSettingsPage.goto('Language', false);
		await localizationInstanceSettingsPage.setLanguage(['en_US']);

		await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
			{
				layoutsUpdateable: false,
				name: getRandomString(),
				url: VIRTUAL_INSTANCE_FULL_URL,
			}
		);

		await localizationInstanceSettingsPage.goto('Language', false);
		await localizationInstanceSettingsPage.setLanguage(['en_US', 'es_ES']);
	}
);

[
	{label: 'with the propagation toggle enabled', propagationEnabled: true},
	{label: 'with the propagation toggle disabled', propagationEnabled: false},
].forEach(({label, propagationEnabled}) => {
	testWithSiteTemplateSync(
		'Linking an existing Site to a Site Template through the Settings ' +
			`${label} does not execute the propagation`,
		{tag: '@LPD-87027'},
		async ({apiHelpers, page, productMenuPage, siteSettingsPage}) => {
			testWithSiteTemplateSync.slow();

			// Create a Site Template with a Web Content and a page

			const siteTemplateName = 'SiteTemplate-' + getRandomString();
			const webContentBody = 'Body-' + getRandomString();
			const webContentName = 'WebContent-' + getRandomString();

			const layoutSetPrototype = await createSiteTemplate({
				apiHelpers,
				page,
				productMenuPage,
				templateName: siteTemplateName,
				text: webContentBody,
				webContentName,
			});

			apiHelpers.data.push({
				id: layoutSetPrototype.layoutSetPrototypeId,
				type: 'layoutSetPrototype',
			});

			const layoutSetPrototypeGroup =
				await apiHelpers.jsonWebServicesGroup.getGroupByKey(
					layoutSetPrototype.companyId,
					layoutSetPrototype.layoutSetPrototypeId
				);

			const pageName = 'Page-' + getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: layoutSetPrototypeGroup.groupId,
				privateLayout: 'true',
				title: pageName,
			});

			// Create an empty Site that is not linked to any Site Template

			const site = await apiHelpers.headlessAdminSite.postSite({
				name: 'Site-' + getRandomString(),
			});

			const sitePages = await apiHelpers.headlessAdminSite.getPages(
				site.externalReferenceCode,
				'flatten=true&pageSize=100&privateLayout=false'
			);

			expect(
				sitePages.items.some(
					(item) => item.name_i18n['en-US'] === pageName
				)
			).toBeFalsy();

			await expect(
				apiHelpers.jsonWebServicesJournal.getArticleByUrlTitle(
					site.id,
					webContentName.toLowerCase()
				)
			).rejects.toThrow();

			// Link the Site to the Site Template from Site Settings > Site
			// Template Sync

			await siteSettingsPage.goToSiteSetting(
				'Pages',
				'Site Template Sync',
				site.friendlyUrlPath
			);

			await siteSettingsPage.linkSiteTemplate(siteTemplateName, {
				propagationEnabled,
			});

			// Connecting a Site to a Site Template must not trigger the sync
			// (LPD-82108 AC22): neither the page nor the Web Content is
			// propagated until a Site Template Sync is executed manually, even
			// when the propagation toggle is enabled

			const sitePagesAfterLink =
				await apiHelpers.headlessAdminSite.getPages(
					site.externalReferenceCode,
					'flatten=true&pageSize=100&privateLayout=false'
				);

			expect(
				sitePagesAfterLink.items.some(
					(item) => item.name_i18n['en-US'] === pageName
				)
			).toBeFalsy();

			await expect(
				apiHelpers.jsonWebServicesJournal.getArticleByUrlTitle(
					site.id,
					webContentName.toLowerCase()
				)
			).rejects.toThrow();
		}
	);
});

[
	{label: 'with the propagation toggle enabled', propagationEnabled: true},
	{label: 'with the propagation toggle disabled', propagationEnabled: false},
].forEach(({label, propagationEnabled}) => {
	testWithSiteTemplateSync(
		"Linking an existing Organization's Site to a Site Template " +
			`through the Settings ${label} does not execute the propagation`,
		{tag: '@LPD-87027'},
		async ({
			apiHelpers,
			editOrganizationPage,
			globalMenuPage,
			layoutSetPrototypePage,
			page,
			productMenuPage,
			usersAndOrganizationsPage,
		}) => {
			testWithSiteTemplateSync.slow();

			// Create a Site Template with a Web Content and a page

			const siteTemplateName = 'SiteTemplate-' + getRandomString();
			const webContentBody = 'Body-' + getRandomString();
			const webContentName = 'WebContent-' + getRandomString();

			const layoutSetPrototype = await createSiteTemplate({
				apiHelpers,
				page,
				productMenuPage,
				templateName: siteTemplateName,
				text: webContentBody,
				webContentName,
			});

			apiHelpers.data.push({
				id: layoutSetPrototype.layoutSetPrototypeId,
				type: 'layoutSetPrototype',
			});

			const layoutSetPrototypeGroup =
				await apiHelpers.jsonWebServicesGroup.getGroupByKey(
					layoutSetPrototype.companyId,
					layoutSetPrototype.layoutSetPrototypeId
				);

			const pageName = 'Page-' + getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: layoutSetPrototypeGroup.groupId,
				privateLayout: 'true',
				title: pageName,
			});

			// The Site Template has a page that a sync would propagate

			expect(
				await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
					Number(layoutSetPrototypeGroup.groupId),
					true
				)
			).toBeGreaterThan(0);

			// Create an Organization (its Site is not linked to any Site Template)

			const organization =
				await apiHelpers.headlessAdminUser.postOrganization();

			// Link the Organization's Site to the Site Template from its Site
			// settings

			await usersAndOrganizationsPage.goToOrganizations(true);

			await editOrganizationPage.linkSiteTemplate(
				organization.name,
				siteTemplateName,
				{propagationEnabled}
			);

			// Relating an Organization to a Site Template must not trigger the sync

			const organizationGroup =
				await apiHelpers.jsonWebServicesGroup.getGroupByKey(
					layoutSetPrototype.companyId,
					organization.name + ' LFR_ORGANIZATION'
				);

			expect(
				await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
					Number(organizationGroup.groupId),
					false
				)
			).toBe(0);

			// Execute the Site Template Sync manually

			await globalMenuPage.goToControlPanel('Site Templates');

			await layoutSetPrototypePage.executeSyncAndWaitForSuccess(
				siteTemplateName
			);

			// The sync only updates the Site when the propagation toggle is enabled
			// (LPD-82108 AC3)

			if (propagationEnabled) {
				await expect(async () => {
					expect(
						await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
							Number(organizationGroup.groupId),
							false
						)
					).toBeGreaterThan(0);
				}).toPass();
			}
			else {
				expect(
					await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
						Number(organizationGroup.groupId),
						false
					)
				).toBe(0);
			}
		}
	);
});

[
	{label: 'with the propagation toggle enabled', propagationEnabled: true},
	{label: 'with the propagation toggle disabled', propagationEnabled: false},
].forEach(({label, propagationEnabled}) => {
	testWithSiteTemplateSync(
		"Linking an existing User Group's Site to a Site Template " +
			`through the Settings ${label} does not execute the propagation`,
		{tag: '@LPD-87027'},
		async ({
			apiHelpers,
			globalMenuPage,
			layoutSetPrototypePage,
			page,
			productMenuPage,
			userGroupsPage,
		}) => {
			testWithSiteTemplateSync.slow();

			// Create a Site Template with a Web Content and a page

			const siteTemplateName = 'SiteTemplate-' + getRandomString();
			const webContentBody = 'Body-' + getRandomString();
			const webContentName = 'WebContent-' + getRandomString();

			const layoutSetPrototype = await createSiteTemplate({
				apiHelpers,
				page,
				productMenuPage,
				templateName: siteTemplateName,
				text: webContentBody,
				webContentName,
			});

			apiHelpers.data.push({
				id: layoutSetPrototype.layoutSetPrototypeId,
				type: 'layoutSetPrototype',
			});

			const layoutSetPrototypeGroup =
				await apiHelpers.jsonWebServicesGroup.getGroupByKey(
					layoutSetPrototype.companyId,
					layoutSetPrototype.layoutSetPrototypeId
				);

			const pageName = 'Page-' + getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: layoutSetPrototypeGroup.groupId,
				privateLayout: 'true',
				title: pageName,
			});

			// The Site Template has a page that a sync would propagate

			expect(
				await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
					Number(layoutSetPrototypeGroup.groupId),
					true
				)
			).toBeGreaterThan(0);

			// Create a User Group (its Site is not linked to any Site Template)

			const userGroup =
				await apiHelpers.headlessAdminUser.postUserGroup();

			// Link the User Group's Site to the Site Template from its Site
			// settings

			await userGroupsPage.goto(true);

			await userGroupsPage.linkSiteTemplate(
				userGroup.name,
				siteTemplateName,
				{propagationEnabled}
			);

			// Relating a User Group to a Site Template must not trigger the sync

			const userGroupGroup =
				await apiHelpers.jsonWebServicesGroup.getGroupByKey(
					layoutSetPrototype.companyId,
					String(userGroup.id)
				);

			expect(
				await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
					Number(userGroupGroup.groupId),
					false
				)
			).toBe(0);

			// Execute the Site Template Sync manually

			await globalMenuPage.goToControlPanel('Site Templates');

			await layoutSetPrototypePage.executeSyncAndWaitForSuccess(
				siteTemplateName
			);

			// The sync only updates the Site when the propagation toggle is enabled
			// (LPD-82108 AC3)

			if (propagationEnabled) {
				await expect(async () => {
					expect(
						await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
							Number(userGroupGroup.groupId),
							false
						)
					).toBeGreaterThan(0);
				}).toPass();
			}
			else {
				expect(
					await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
						Number(userGroupGroup.groupId),
						false
					)
				).toBe(0);
			}
		}
	);
});

[
	{label: 'with the propagation toggle enabled', propagationEnabled: true},
	{label: 'with the propagation toggle disabled', propagationEnabled: false},
].forEach(({label, propagationEnabled}) => {
	testWithSiteTemplateSync(
		"Linking an existing User's Site to a Site Template through the " +
			`Settings ${label} does not execute the propagation`,
		{tag: '@LPD-87027'},
		async ({
			apiHelpers,
			editUserPage,
			globalMenuPage,
			layoutSetPrototypePage,
			page,
			productMenuPage,
			usersAndOrganizationsPage,
		}) => {
			testWithSiteTemplateSync.slow();

			// Create a Site Template with a Web Content and a page

			const siteTemplateName = 'SiteTemplate-' + getRandomString();
			const webContentBody = 'Body-' + getRandomString();
			const webContentName = 'WebContent-' + getRandomString();

			const layoutSetPrototype = await createSiteTemplate({
				apiHelpers,
				page,
				productMenuPage,
				templateName: siteTemplateName,
				text: webContentBody,
				webContentName,
			});

			apiHelpers.data.push({
				id: layoutSetPrototype.layoutSetPrototypeId,
				type: 'layoutSetPrototype',
			});

			const layoutSetPrototypeGroup =
				await apiHelpers.jsonWebServicesGroup.getGroupByKey(
					layoutSetPrototype.companyId,
					layoutSetPrototype.layoutSetPrototypeId
				);

			const pageName = 'Page-' + getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: layoutSetPrototypeGroup.groupId,
				privateLayout: 'true',
				title: pageName,
			});

			// The Site Template has a page that a sync would propagate

			expect(
				await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
					Number(layoutSetPrototypeGroup.groupId),
					true
				)
			).toBeGreaterThan(0);

			// Create a User (the personal Site is not linked to any Site Template)

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			// Link the User's profile pages to the Site Template from its Profile
			// and Dashboard settings

			await usersAndOrganizationsPage.goToUsers(true);

			await usersAndOrganizationsPage.goToUser(
				`${user.givenName} ${user.familyName}`
			);

			await editUserPage.linkSiteTemplate(siteTemplateName, {
				propagationEnabled,
			});

			// Relating a User to a Site Template must not trigger the sync

			const userGroup =
				await apiHelpers.jsonWebServicesGroup.getUserGroup(
					layoutSetPrototype.companyId,
					String(user.id)
				);

			expect(
				await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
					Number(userGroup.groupId),
					false
				)
			).toBe(0);

			// Execute the Site Template Sync manually

			await globalMenuPage.goToControlPanel('Site Templates');

			await layoutSetPrototypePage.executeSyncAndWaitForSuccess(
				siteTemplateName
			);

			// The sync only updates the Site when the propagation toggle is enabled
			// (LPD-82108 AC3)

			if (propagationEnabled) {
				await expect(async () => {
					expect(
						await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
							Number(userGroup.groupId),
							false
						)
					).toBeGreaterThan(0);
				}).toPass();
			}
			else {
				expect(
					await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
						Number(userGroup.groupId),
						false
					)
				).toBe(0);
			}
		}
	);
});
