/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {portletConfigurationPermissionsPageTest} from '../../../fixtures/portletConfigurationPermissionsPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {sitesPageTest} from '../../../fixtures/sitesPageTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import {webContentDisplayPageTest} from '../../../fixtures/webContentDisplayPageTest';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {reloadUntilVisible} from '../../../utils/reloadUntilVisible';
import {enableLocalStaging} from '../../../utils/staging';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {exportImportPagesTest} from '../../export-import-web/main/fixtures/exportImportPagesTest';
import {stagingPageTest} from '../../export-import-web/main/fixtures/stagingPageTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {portletPublishToLivePageTest} from './fixtures/portletPublishToLivePageTest';
import {stagingConfigurationPageTest} from './fixtures/stagingConfigurationPageTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	globalMenuPagesTest,
	loginTest(),
	instanceSettingsPagesTest,
	pageViewModePagesTest,
	pagesAdminPagesTest,
	pageEditorPagesTest,
	productMenuPageTest,
	portletPublishToLivePageTest,
	sitesPageTest,
	stagingConfigurationPageTest,
	webContentDisplayPageTest,
	uiElementsPageTest,
	journalPagesTest,
	systemSettingsPageTest
);

export const testFlagsEnabled = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-11131': {enabled: true},
		'LPD-35013': {enabled: true},
		'LPD-39304': {enabled: true},
	}),
	dataApiHelpersTest,
	loginTest(),
	portletConfigurationPermissionsPageTest,
	portletPublishToLivePageTest,
	stagingPageTest,
	test,
	webContentDisplayPageTest
);

export const testWithSitePagesAPI = mergeTests(
	test,
	featureFlagsTest({'LPD-35443': {enabled: true}})
);

test(
	'Verify there is advanced staging configuration checkbox with description in Instance Setting,the configuration checkbox can be enabled',
	{tag: ['@LPS-189238', '@LPD-88913']},
	async ({
		apiHelpers,
		exportImportStagingInstanceSettingsPage,
		page,
		pageEditorPage,
		portletPublishToLivePage,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: getRandomString(),
		});

		// Publish the page so it can be reached through its live friendly URL

		await pageEditorPage.goto(layout, site.friendlyUrlPath);
		await pageEditorPage.publishPage();

		await exportImportStagingInstanceSettingsPage.goto();
		await exportImportStagingInstanceSettingsPage.checkConfigurationOption({
			checked: true,
			label: 'Show Advanced Staging Configuration by Default',
		});

		try {
			await exportImportStagingInstanceSettingsPage.instanceSettingsPage.saveAndWaitForAlert();

			await enableLocalStaging(apiHelpers, page, site);

			const stagingSite =
				await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
					`${site.friendlyUrlPath}-staging`
				);

			await page.goto(
				`/web${stagingSite.friendlyUrlPath}${layout.friendlyURL}`
			);
			await reloadUntilVisible({
				myLocator: portletPublishToLivePage.publishToLiveButton,
				page,
			});
			await portletPublishToLivePage.publishToLiveButton.click();

			await test.step('LPD-88913 - Publish to Live modal must keep the cadmin wrapper', async () => {
				await expect(
					page.locator(
						'div.cadmin > [id$="publishLatestChangesDialog"]'
					)
				).toBeVisible();
			});

			await expect(
				portletPublishToLivePage.publishToLiveIframe.getByRole('link', {
					name: 'Switch to Simple Publish Process',
				})
			).toBeVisible();
		}
		finally {
			await exportImportStagingInstanceSettingsPage.goto();
			await exportImportStagingInstanceSettingsPage.resetDefaultValues();
		}
	}
);

test(
	'Verify there is advanced staging configuration checkbox with description in System Setting,the configuration checkbox can be enabled',
	{tag: ['@LPS-189238']},
	async ({
		apiHelpers,
		exportImportStagingSystemSettingsPage,
		page,
		pageEditorPage,
		portletPublishToLivePage,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: getRandomString(),
		});

		// Publish the page so it can be reached through its live friendly URL

		await pageEditorPage.goto(layout, site.friendlyUrlPath);
		await pageEditorPage.publishPage();

		await exportImportStagingSystemSettingsPage.goto();
		await exportImportStagingSystemSettingsPage.checkConfigurationOption({
			checked: true,
			label: 'Show Advanced Staging Configuration by Default',
		});

		try {
			await enableLocalStaging(apiHelpers, page, site);

			const stagingSite =
				await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
					`${site.friendlyUrlPath}-staging`
				);

			await page.goto(
				`/web${stagingSite.friendlyUrlPath}${layout.friendlyURL}`
			);
			await reloadUntilVisible({
				myLocator: portletPublishToLivePage.publishToLiveButton,
				page,
			});
			await portletPublishToLivePage.publishToLiveButton.click();

			await expect(
				portletPublishToLivePage.publishToLiveIframe.getByRole('link', {
					name: 'Switch to Simple Publish Process',
				})
			).toBeVisible();
		}
		finally {
			await exportImportStagingSystemSettingsPage.goto();
			await exportImportStagingSystemSettingsPage.resetDefaultValues();
		}
	}
);

test('Check if local staging can be enabled', async ({
	apiHelpers,
	globalMenuPage,
	stagingConfigurationPage,
}) => {
	const siteName: string = getRandomString();

	await globalMenuPage.goToControlPanel('Sites');

	const site = await apiHelpers.headlessAdminSite.postSite({
		name: siteName,
	});

	await stagingConfigurationPage.gotoStagingConfiguration(
		site.friendlyUrlPath
	);

	await stagingConfigurationPage.enableLocalStaging({});
});

test(
	'Validate friendlyURL with special characters',
	{tag: ['@LPS-89116']},
	async ({
		apiHelpers,
		globalMenuPage,
		journalPage,
		page,
		pagesAdminPage,
		productMenuPage,
		stagingConfigurationPage,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await globalMenuPage.goToSite(site.name);
		await productMenuPage.goToPages();

		await pagesAdminPage.createNewPage({
			name: getRandomString(),
		});

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContentName = 'Special Char aŐb';
		await apiHelpers.jsonWebServicesJournal.addWebContent({
			content: 'Web Content Content',
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: webContentName},
		});

		await journalPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByText(webContentName, {exact: true})
		).toBeVisible();

		await page.getByText(webContentName).click();

		await expect(
			page.getByLabel('Friendly URL', {exact: true})
		).toHaveValue('special-char-aőb');

		await apiHelpers.jsonWebServicesJournal.addWebContentDetailed({
			content: 'Web Content Content2',
			ddmStructureId: basicWebContentStructureId,
			friendlyURLMap: {en_US: 'special-char-a-c5-90b'},
			groupId: site.id,
			titleMap: {en_US: 'Web Content Title'},
		});

		await stagingConfigurationPage.gotoStagingConfiguration(
			site.friendlyUrlPath
		);

		await stagingConfigurationPage.enableLocalStaging({});
	}
);

test(
	'Verify that the admin could configure staging to ignore previews and thumbnails during the local staging publish process',
	{tag: ['@LPS-189191', '@LPS-190360']},
	async ({
		apiHelpers,
		exportImportStagingInstanceSettingsPage,
		page,
		pageEditorPage,
		portletPublishToLivePage,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: getRandomString(),
		});

		// Publish the page so it can be reached through its live friendly URL

		await pageEditorPage.goto(layout, site.friendlyUrlPath);
		await pageEditorPage.publishPage();

		await exportImportStagingInstanceSettingsPage.goto();
		await exportImportStagingInstanceSettingsPage.checkConfigurationOption({
			checked: true,
			label: 'Include Thumbnails And Previews During Staging',
		});

		await enableLocalStaging(apiHelpers, page, site);

		const stagingSite =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				`${site.friendlyUrlPath}-staging`
			);

		await apiHelpers.headlessDelivery.postDocument(
			stagingSite.id,
			createReadStream(
				path.join(__dirname, '/dependencies/Document.jpg')
			),
			{
				fileName: 'Document.jpg',
				title: 'Document.jpg',
			}
		);

		await page.goto(
			`/web${stagingSite.friendlyUrlPath}${layout.friendlyURL}`
		);

		await reloadUntilVisible({
			myLocator: portletPublishToLivePage.publishToLiveButton,
			page,
		});

		await portletPublishToLivePage.goToPortletAdvancedStagings();

		const documentsAndMedia = portletPublishToLivePage.publishToLiveIframe
			.locator(
				'[id="_com_liferay_exportimport_web_portlet_ExportImportPortlet_selectContents"] ul'
			)
			.filter({hasText: 'Documents and Media '});
		await documentsAndMedia.getByRole('button', {name: 'Change'}).click();
		await documentsAndMedia.getByLabel('Previews and Thumbnails').check();

		await portletPublishToLivePage.publishToLiveIframe
			.getByRole('button', {name: 'Publish to Live'})
			.click();

		await expect(
			portletPublishToLivePage.publishToLiveSuccessStatus
		).toBeVisible();

		await page.goto(
			`/group${stagingSite.friendlyUrlPath}${PORTLET_URLS.documentLibrary}`
		);

		expect(
			await page
				.locator('.card')
				.filter({has: page.getByRole('link', {name: 'Document.jpg'})})
				.locator('img')
		).toBeVisible();
	}
);

test(
	'Verify if information about staging system settings are present',
	{tag: ['@LPS-123156']},
	async ({instanceSettingsPage}) => {
		await instanceSettingsPage.goToInstanceSetting(
			'Web Content',
			'Web Content',
			true,
			'Virtual Instance Scope'
		);
		await instanceSettingsPage.assertOptionVisible({
			description:
				'Specify characters that are not allowed in web content folder names.',
			label: 'Single Asset Publish Process Includes Version History',
		});

		await instanceSettingsPage.goToInstanceSetting(
			'Infrastructure',
			'Staging',
			true,
			'Virtual Instance Scope'
		);
		await instanceSettingsPage.assertOptionVisible({
			description:
				'Uncheck to avoid deleting the temporary LAR during a failed staging publish process. In remote staging contexts, this only applies for the staging environment.',
			label: 'Delete temporary LAR during a failed staging publish process.',
		});
		await instanceSettingsPage.assertOptionVisible({
			description:
				'Uncheck to avoid deleting the temporary LAR during a successful staging publish process. In remote staging contexts, this only applies for the staging environment.',
			label: 'Delete temporary LAR during a successful staging publish process.',
		});
	}
);

testFlagsEnabled(
	'Check if local staging with page-scoped Web Content can be enabled',
	{tag: ['@LPS-83147']},
	async ({apiHelpers, page, webContentDisplayPage, widgetPagePage}) => {
		const siteName = getRandomString();
		const layoutName = getRandomString();
		const webContentName = getRandomString();

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: siteName,
		});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'portlet'},
			title: layoutName,
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await page.getByLabel('Add', {exact: true}).click();

		await widgetPagePage.addPortlet(
			'Web Content Display',
			'Content Management'
		);

		await webContentDisplayPage.goToConfiguration();
		await webContentDisplayPage.setScope(layout.uuid);
		await webContentDisplayPage.saveConfigurationFrameOptions();

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const group = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			company.companyId,
			layout.plid
		);

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: getRandomString(),
				ddmStructureId: basicWebContentStructureId,
				groupId: group.groupId,
				titleMap: {en_US: webContentName},
			});

		apiHelpers.data.push({
			id: `${group.groupId}_${webContent.articleId}`,
			type: 'webContent',
		});

		await webContentDisplayPage.addWebContentWithDisplay({
			pageType: 'widget',
			webContentName,
		});

		await enableLocalStaging(apiHelpers, page, site);

		await webContentDisplayPage.gotoWebContentAdmin(layout.plid);
		await page.getByText(webContentName).waitFor({state: 'visible'});
	}
);

testWithSitePagesAPI(
	'Staging is blocked for a Site linked to a Site Template with propagation enabled',
	{tag: '@LPD-87027'},
	async ({
		apiHelpers,
		globalMenuPage,
		sitesPage,
		stagingConfigurationPage,
	}) => {
		const siteTemplateName = 'SiteTemplate-' + getRandomString();

		const layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{
					name: siteTemplateName,
				}
			);

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await globalMenuPage.goToControlPanel('Sites');

		const siteName = 'Site-' + getRandomString();

		const {externalReferenceCode} = await sitesPage.createSite({
			isCustom: true,
			siteName,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({id: externalReferenceCode, type: 'site'});

		// Wait until the Site Template pages propagate to the linked Site so the
		// propagation background task completes before the cleanup deletes it

		await expect(async () => {
			const sitePages = await apiHelpers.headlessAdminSite.getPages(
				externalReferenceCode,
				'pageSize=100&privateLayout=false'
			);

			expect(sitePages.items.length).toBeGreaterThan(0);
		}).toPass();

		await stagingConfigurationPage.gotoStagingConfiguration(
			'/' + siteName.toLowerCase()
		);

		await expect(
			stagingConfigurationPage.page.getByText(
				'Staging cannot be used for this site because the propagation ' +
					'of changes from the site template is enabled.'
			)
		).toBeVisible();

		await expect(stagingConfigurationPage.localLiveRadio).toBeHidden();
	}
);
