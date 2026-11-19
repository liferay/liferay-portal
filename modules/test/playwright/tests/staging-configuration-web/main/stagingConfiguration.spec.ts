/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
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
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {reloadUntilVisible} from '../../../utils/reloadUntilVisible';
import {enableLocalStaging} from '../../../utils/staging';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {exportImportPagesTest} from '../../export-import-web/main/fixtures/exportImportPagesTest';
import {stagingPageTest} from '../../export-import-web/main/fixtures/stagingPageTest';
import {StagingPage} from '../../export-import-web/main/pages/StagingPage';
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
	stagingPageTest,
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

test(
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
				'flatten=true&pageSize=100&privateLayout=false'
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

const STAGING_PROCESSES_NAMESPACE =
	'_com_liferay_staging_processes_web_portlet_StagingProcessesPortlet_';

const XSS_PAYLOAD = "<img src=x onerror=alert('XSS98204')>";

// Object labels are sanitized when they are stored, so only markup survives

const OBJECT_LABEL_PAYLOAD = '<b>XSS98204</b>';

// The import options are rendered by the legacy export/import UI

const testWithLegacyExportImportUI = mergeTests(
	test,
	featureFlagsTest({'LPD-57655': {enabled: false}})
);

function collectDialogs(page: Page) {
	const dialogs: string[] = [];

	page.on('dialog', async (dialog) => {
		dialogs.push(dialog.message());

		await dialog.dismiss();
	});

	return dialogs;
}

async function waitForInitialPublication(page: Page, site: Site) {
	await page.goto(
		`/group${site.friendlyUrlPath}-staging${PORTLET_URLS.staging}`
	);

	await reloadUntilVisible({
		maxAttempts: 10,
		myLocator: page.getByText('Successful', {exact: true}),
		page,
	});
}

async function publishAndOpenSummary(
	page: Page,
	site: Site,
	stagingPage: StagingPage
) {
	await stagingPage.goto(`${site.friendlyUrlPath.slice(1)}-staging`);

	await stagingPage.publish();

	await page
		.locator(
			'[id="_com_liferay_staging_processes_web_portlet_StagingProcessesPortlet_publishLayoutProcesses_1"]'
		)
		.getByRole('button')
		.last()
		.click();

	await page
		.getByText('Summary', {exact: true})
		.filter({visible: true})
		.click();

	const summary = page.frameLocator('iframe');

	await expect(summary.getByText('Pages Option')).toBeVisible();

	return summary;
}

test(
	'A cmd parameter in the publish URL is not executed as JavaScript',
	{tag: '@LPD-98204'},
	async ({apiHelpers, page}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await enableLocalStaging(apiHelpers, page, site);

		await waitForInitialPublication(page, site);

		const dialogs = collectDialogs(page);

		const searchParams = new URLSearchParams({
			[`${STAGING_PROCESSES_NAMESPACE}cmd`]:
				"</script><svg onload=alert('XSS98204')>",
			[`${STAGING_PROCESSES_NAMESPACE}mvcRenderCommandName`]:
				'/staging_processes/publish_layouts',
		});

		await page.goto(
			`/group${site.friendlyUrlPath}-staging${PORTLET_URLS.staging}&${searchParams}`
		);

		await expect(page.getByRole('radio', {name: 'Now'})).toBeVisible();

		expect(dialogs).toHaveLength(0);
	}
);

test(
	'A page name in the publish process summary is not executed as JavaScript',
	{tag: '@LPD-98204'},
	async ({apiHelpers, page, stagingPage}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await enableLocalStaging(apiHelpers, page, site);

		await waitForInitialPublication(page, site);

		const stagingSite =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				`${site.friendlyUrlPath}-staging`
			);

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: stagingSite.id,
			title: XSS_PAYLOAD,
		});

		const dialogs = collectDialogs(page);

		const summary = await publishAndOpenSummary(page, site, stagingPage);

		// The payload is rendered as text, not executed

		await expect(summary.getByText(XSS_PAYLOAD)).toBeVisible();

		expect(dialogs).toHaveLength(0);
	}
);

test(
	'A web content title in the publish process summary is not executed as JavaScript',
	{tag: '@LPD-98204'},
	async ({apiHelpers, page, stagingPage}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await enableLocalStaging(apiHelpers, page, site);

		await waitForInitialPublication(page, site);

		const stagingSite =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				`${site.friendlyUrlPath}-staging`
			);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			content: getRandomString(),
			ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
			groupId: stagingSite.id,
			titleMap: {en_US: XSS_PAYLOAD},
		});

		const dialogs = collectDialogs(page);

		const summary = await publishAndOpenSummary(page, site, stagingPage);

		// The payload is rendered as text, not executed

		await expect(summary.getByText(XSS_PAYLOAD)).toBeVisible();

		expect(dialogs).toHaveLength(0);
	}
);

testWithLegacyExportImportUI(
	'An object plural label in the import options is not rendered as markup',
	{tag: '@LPD-98204'},
	async ({apiHelpers, exportImportPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				scope: 'site',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await apiHelpers.patch(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinition.id}`,
			{pluralLabel: {en_US: OBJECT_LABEL_PAYLOAD}}
		);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: getRandomString()},
			`${normalizeRestPath(objectDefinition.restContextPath)}/scopes/${site.id}`
		);

		// Export the site with the object entries

		await exportImportPage.goToExport(site.friendlyUrlPath);

		const filePath = await exportImportPage.export({
			exportAllPortlets: true,
		});

		// Upload the export to another site

		const targetSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await exportImportPage.goToImport(targetSite.friendlyUrlPath);

		await exportImportPage.selectImportFile({filePath});

		// The payload is rendered as text

		await expect(
			page.getByText(OBJECT_LABEL_PAYLOAD).first()
		).toBeVisible();
	}
);

const testWithPageVersioning = mergeTests(
	test,
	featureFlagsTest({'LPD-105778': {enabled: true}})
);

testWithPageVersioning(
	'Save page versioning changes when the staging type is unchanged',
	{tag: '@LPD-115749'},
	async ({apiHelpers, page, stagingConfigurationPage}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await enableLocalStaging(apiHelpers, page, site);

		await waitForInitialPublication(page, site);

		// Enable page versioning without changing the staging type

		await page.goto(
			`/group${site.friendlyUrlPath}-staging${PORTLET_URLS.staging}&${STAGING_PROCESSES_NAMESPACE}showStagingConfiguration=true`
		);

		await stagingConfigurationPage.pageVersioningCheckbox.check();

		await stagingConfigurationPage.saveButton.click();

		// Check the change is saved

		await waitForAlert(
			page,
			'Success:Local staging configuration is successfully modified.'
		);

		await expect(
			stagingConfigurationPage.pageVersioningCheckbox
		).toBeChecked();
	}
);

test(
	'Identify the invalid remote staging field when saving fails',
	{tag: '@LPD-115749'},
	async ({apiHelpers, page, stagingConfigurationPage}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		// Save remote staging without a remote site ID

		await stagingConfigurationPage.gotoStagingConfiguration(
			site.friendlyUrlPath
		);

		await stagingConfigurationPage.remoteLiveRadio.check();

		await page.getByLabel('Remote Host/IP').fill('localhost');
		await page.getByLabel('Remote Port').fill('8080');

		page.on('dialog', async (dialog) => {
			await dialog.accept();
		});

		await stagingConfigurationPage.saveButton.click();

		// Check the error is tied to the invalid field

		const remoteSiteIdInput = page.getByLabel('Remote Site ID');

		await expect(remoteSiteIdInput).toBeFocused();
		await expect(remoteSiteIdInput).toHaveAttribute('aria-invalid', 'true');
		await expect(remoteSiteIdInput).toHaveAccessibleDescription(
			/The remote site ID 0 is not valid\./
		);

		await expect(
			page.getByText('Your request failed to complete.')
		).toBeHidden();
	}
);

test(
	'Submit nothing when an unstaged site is saved without staging',
	{tag: '@LPD-115749'},
	async ({apiHelpers, page, stagingConfigurationPage}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await stagingConfigurationPage.gotoStagingConfiguration(
			site.friendlyUrlPath
		);

		const editStagingConfigurationRequests: string[] = [];

		page.on('request', (request) => {
			if (
				request.method() === 'POST' &&
				request.url().includes('editStagingConfiguration')
			) {
				editStagingConfigurationRequests.push(request.url());
			}
		});

		// Save without selecting a staging type

		await stagingConfigurationPage.saveButton.click();

		// Enable local staging so that a later submission is observable

		await stagingConfigurationPage.enableLocalStaging({});

		expect(editStagingConfigurationRequests).toHaveLength(1);
	}
);
