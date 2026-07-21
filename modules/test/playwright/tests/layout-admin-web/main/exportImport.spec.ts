/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {captureScreenshot} from '../../../utils/captureScreenshot';
import {compareScreenshots} from '../../../utils/compareScreenshots';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {exportImportPagesTest} from '../../export-import-web/main/fixtures/exportImportPagesTest';
import getContainerDefinition from '../../layout-content-page-editor-web/main/utils/getContainerDefinition';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getGridDefinition from '../../layout-content-page-editor-web/main/utils/getGridDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

const test = mergeTests(
	exportImportPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35443': {enabled: true},
		'LPD-57655': {enabled: false},
		'LPD-76864': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pagesAdminPagesTest
);

test(
	'Compare a page when exporting/importing a site',
	{tag: '@LPD-74680'},
	async ({
		apiHelpers,
		exportImportPage,
		pageEditorPage,
		pagesAdminPage,
		site: siteA,
	}) => {

		// Create a page in the Site A through the UI and take screenshots in view mode and edit mode

		const layoutName = getRandomString();

		await pagesAdminPage.goto(siteA.friendlyUrlPath);

		await pagesAdminPage.createNewPage({
			draft: true,
			name: layoutName,
			template: 'Blank',
		});

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.publishPage();

		const viewModeScreenshotA = await pageEditorPage.captureScreenshot({
			layoutName,
			name: 'viewModeScreenshotA.png',
			siteUrl: siteA.friendlyUrlPath,
		});

		const editModeScreenshotA = await pageEditorPage.captureScreenshot({
			layoutName,
			layoutOptions: {editMode: true},
			name: 'editModeScreenshotA.png',
			siteUrl: siteA.friendlyUrlPath,
		});

		// Export a site A

		await exportImportPage.goToExport(siteA.friendlyUrlPath);

		const exportFilePath = await exportImportPage.export();

		// Create a site B

		const siteB = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		// Import the site A into the site B

		await exportImportPage.goToImport(siteB.friendlyUrlPath);

		await exportImportPage.import({filePath: exportFilePath});

		// Take screenshots in the Site B

		const viewModeScreenshotB = await pageEditorPage.captureScreenshot({
			layoutName,
			name: 'viewModeScreenshotB.png',
			siteUrl: siteB.friendlyUrlPath,
		});

		const editModeScreenshotB = await pageEditorPage.captureScreenshot({
			layoutName,
			layoutOptions: {editMode: true},
			name: 'editModeScreenshotB.png',
			siteUrl: siteB.friendlyUrlPath,
		});

		// Compare screenshots

		compareScreenshots(viewModeScreenshotA, viewModeScreenshotB);
		compareScreenshots(editModeScreenshotA, editModeScreenshotB);
	}
);

test(
	'Compare priorities of pages when exporting/importing a site',
	{tag: '@LPD-80650'},
	async ({apiHelpers, exportImportPage, site: siteA}) => {

		// Create pages in the Site A

		const page1 = await apiHelpers.headlessAdminSite.createPage(
			siteA.externalReferenceCode,
			{
				name_i18n: {en_US: getRandomString()},
				type: 'WidgetPage',
			}
		);
		const page2 = await apiHelpers.headlessAdminSite.createPage(
			siteA.externalReferenceCode,
			{
				name_i18n: {en_US: getRandomString()},
				type: 'WidgetPage',
			}
		);
		const page3 = await apiHelpers.headlessAdminSite.createPage(
			siteA.externalReferenceCode,
			{
				name_i18n: {en_US: getRandomString()},
				type: 'WidgetPage',
			}
		);
		const page4 = await apiHelpers.headlessAdminSite.createPage(
			siteA.externalReferenceCode,
			{
				name_i18n: {en_US: getRandomString()},
				type: 'WidgetPage',
			}
		);

		// Update pages priority

		page4.pageSettings.priority = 1;

		await apiHelpers.headlessAdminSite.putPage(
			siteA.externalReferenceCode,
			page4.externalReferenceCode,
			page4
		);

		page3.pageSettings.priority = 2;

		await apiHelpers.headlessAdminSite.putPage(
			siteA.externalReferenceCode,
			page3.externalReferenceCode,
			page3
		);

		// Get pages in the Site A

		const getPage1 = await apiHelpers.headlessAdminSite.getPage(
			siteA.externalReferenceCode,
			page1.externalReferenceCode
		);
		const getPage2 = await apiHelpers.headlessAdminSite.getPage(
			siteA.externalReferenceCode,
			page2.externalReferenceCode
		);
		const getPage3 = await apiHelpers.headlessAdminSite.getPage(
			siteA.externalReferenceCode,
			page3.externalReferenceCode
		);
		const getPage4 = await apiHelpers.headlessAdminSite.getPage(
			siteA.externalReferenceCode,
			page4.externalReferenceCode
		);

		// Assert page priorities in the site A

		expect(getPage1.pageSettings.priority).toEqual(0);
		expect(getPage4.pageSettings.priority).toEqual(1);
		expect(getPage3.pageSettings.priority).toEqual(2);
		expect(getPage2.pageSettings.priority).toEqual(3);

		// Assert pages can be sorted by priority in the site A

		const getPages = await apiHelpers.headlessAdminSite.getPages(
			siteA.externalReferenceCode,
			'sort=pageSettings/priority:asc'
		);

		expect(getPages.items[0].pageSettings.priority).toEqual(0);
		expect(getPages.items[1].pageSettings.priority).toEqual(1);
		expect(getPages.items[2].pageSettings.priority).toEqual(2);
		expect(getPages.items[3].pageSettings.priority).toEqual(3);

		expect(getPages.items[0].name_i18n['en-US']).toEqual(
			page1.name_i18n['en-US']
		);
		expect(getPages.items[1].name_i18n['en-US']).toEqual(
			page4.name_i18n['en-US']
		);
		expect(getPages.items[2].name_i18n['en-US']).toEqual(
			page3.name_i18n['en-US']
		);
		expect(getPages.items[3].name_i18n['en-US']).toEqual(
			page2.name_i18n['en-US']
		);

		// Export a site A

		await exportImportPage.goToExport(siteA.friendlyUrlPath);

		const exportFilePath = await exportImportPage.export();

		// Create a site B

		const siteB = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		// Import the site A into the site B

		await exportImportPage.goToImport(siteB.friendlyUrlPath);

		await exportImportPage.import({filePath: exportFilePath});

		// Get pages in the Site B

		const importedPage1 = await apiHelpers.headlessAdminSite.getPage(
			siteB.externalReferenceCode,
			page1.externalReferenceCode
		);
		const importedPage2 = await apiHelpers.headlessAdminSite.getPage(
			siteB.externalReferenceCode,
			page2.externalReferenceCode
		);
		const importedPage3 = await apiHelpers.headlessAdminSite.getPage(
			siteB.externalReferenceCode,
			page3.externalReferenceCode
		);
		const importedPage4 = await apiHelpers.headlessAdminSite.getPage(
			siteB.externalReferenceCode,
			page4.externalReferenceCode
		);

		// Assert page priorities in the site B

		expect(importedPage1.pageSettings.priority).toEqual(0);
		expect(importedPage4.pageSettings.priority).toEqual(1);
		expect(importedPage3.pageSettings.priority).toEqual(2);
		expect(importedPage2.pageSettings.priority).toEqual(3);

		// Assert pages can be sorted by priority in the site B

		const importedPages = await apiHelpers.headlessAdminSite.getPages(
			siteB.externalReferenceCode,
			'sort=pageSettings/priority:asc'
		);

		expect(importedPages.items[0].pageSettings.priority).toEqual(0);
		expect(importedPages.items[1].pageSettings.priority).toEqual(1);
		expect(importedPages.items[2].pageSettings.priority).toEqual(2);
		expect(importedPages.items[3].pageSettings.priority).toEqual(3);

		expect(importedPages.items[0].name_i18n['en-US']).toEqual(
			page1.name_i18n['en-US']
		);
		expect(importedPages.items[1].name_i18n['en-US']).toEqual(
			page4.name_i18n['en-US']
		);
		expect(importedPages.items[2].name_i18n['en-US']).toEqual(
			page3.name_i18n['en-US']
		);
		expect(importedPages.items[3].name_i18n['en-US']).toEqual(
			page2.name_i18n['en-US']
		);
	}
);

test(
	'Compare fragment configurations when exporting/importing a site',
	{tag: '@LPD-74680'},
	async ({
		apiHelpers,
		exportImportPage,
		page,
		pageEditorPage,
		site: siteA,
	}) => {
		test.slow();

		const getFragmentConfigurationScreenshots = async ({screenshots}) => {
			const configurationPanel = page.getByLabel('Configuration Panel', {
				exact: true,
			});
			const treeNodes = page.locator(
				'.page-editor__page-structure__tree-node__name'
			);

			const getConfigurationScreenshot = async (
				configurationTabName: ConfigurationTab
			) => {
				await pageEditorPage.goToConfigurationTab(configurationTabName);

				await expect(
					page.getByRole('tabpanel', {name: configurationTabName})
				).toHaveClass(/active/);

				return await captureScreenshot({
					locator: configurationPanel,
					name: `${configurationTabName}_${getRandomString()}`,
				});
			};

			let clicked = 0;

			await expect(treeNodes.first()).toBeVisible();

			while (true) {
				const count = await treeNodes.count();

				if (clicked === count) {
					break;
				}

				for (let i = clicked; i < count; i++) {
					await treeNodes.nth(i).scrollIntoViewIfNeeded();
					await treeNodes.nth(i).click({force: true});

					const text = await treeNodes.nth(i).innerText();

					if (text.includes('element-text')) {
						screenshots.push(
							await getConfigurationScreenshot('Mapping')
						);

						screenshots.push(
							await getConfigurationScreenshot('Link')
						);
					}
					else if (!text.includes('Module')) {
						screenshots.push(
							await getConfigurationScreenshot('General')
						);

						screenshots.push(
							await getConfigurationScreenshot('Styles')
						);

						screenshots.push(
							await getConfigurationScreenshot('Advanced')
						);
					}
				}

				clicked = count;
			}
		};

		// Change the viewport size to capture all the configuration content in the screenshot

		await page.setViewportSize({height: 1525, width: 1437});

		// Create a page in the Site A with a Container, a Grid and a Heading

		const containerDefinition = getContainerDefinition({
			id: getRandomString(),
		});

		const headingDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-heading',
		});

		const gridDefinition = getGridDefinition({
			columns: [
				{pageElements: [headingDefinition], size: 4},
				{size: 4},
				{size: 4},
			],
			id: getRandomString(),
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				containerDefinition,
				gridDefinition,
			]),
			siteId: siteA.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, siteA.friendlyUrlPath);

		// Go to the Browser and take screenshoots of each configuration for each fragment in the Site A

		await pageEditorPage.goToSidebarTab('Browser');

		const screenshotsA = [];

		await getFragmentConfigurationScreenshots({screenshots: screenshotsA});

		// Export a site A

		await exportImportPage.goToExport(siteA.friendlyUrlPath);

		const exportFilePath = await exportImportPage.export();

		// Create a site B

		const siteB = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		// Import the site A into the site B

		await exportImportPage.goToImport(siteB.friendlyUrlPath);

		await exportImportPage.import({filePath: exportFilePath});

		// Go to the Browser and take screenshoots of each configuration for each fragment in the Site B

		await pageEditorPage.goto(layout, siteB.friendlyUrlPath);

		await pageEditorPage.goToSidebarTab('Browser');

		const screenshotsB = [];

		await getFragmentConfigurationScreenshots({screenshots: screenshotsB});

		// Compare screenshots

		for (let i = 0; i < screenshotsA.length; i++) {
			compareScreenshots(screenshotsA[i], screenshotsB[i]);
		}
	}
);

test(
	'Can export and import a site with an embedded Web Content Display in a fragment',
	{tag: '@LPS-96391'},
	async ({apiHelpers, exportImportPage, page, site: siteA}) => {
		test.slow();

		const webContentTitle1 = `Original-${getRandomString()}`;
		const webContentTitle2 = `WC-${getRandomString()}`;

		// Create a web content on site A

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: webContentTitle1,
				ddmStructureId: basicWebContentStructureId,
				groupId: siteA.id,
				titleMap: {en_US: webContentTitle2},
			});

		// Create a fragment whose HTML embeds a Web Content Display widget

		const {fragmentCollectionId} =
			await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
				{
					groupId: siteA.id,
					name: getRandomString(),
				}
			);

		const fragmentEntryName = getRandomString();

		await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
			fragmentCollectionId,
			groupId: siteA.id,
			html: '<lfr-widget-web-content></lfr-widget-web-content>',
			name: fragmentEntryName,
		});

		// Create a content page with the fragment, pre-configuring its
		// embedded Web Content Display to point to the web content above

		const fragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: fragmentEntryName,
			widgetInstances: [
				{
					widgetConfig: {
						articleExternalReferenceCode:
							webContent.externalReferenceCode,
						articleId: webContent.articleId,
						groupId: String(siteA.id),
					},
					widgetName:
						'com_liferay_journal_content_web_portlet_JournalContentPortlet',
				},
			],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([fragmentDefinition]),
			siteId: siteA.id,
			title: `Page-${getRandomString()}`,
		});

		await page.goto(
			`/web${siteA.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(page.getByText(webContentTitle1)).toBeVisible();

		// Export site A as a LAR

		await exportImportPage.goToExport(siteA.friendlyUrlPath);

		const exportFilePath = await exportImportPage.export();

		// Create site B and import the LAR into it

		const siteB = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await exportImportPage.goToImport(siteB.friendlyUrlPath);

		await exportImportPage.import({filePath: exportFilePath});

		// The imported page on site B shows the same web content

		await page.goto(
			`/web${siteB.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(page.getByText(webContentTitle1)).toBeVisible();
	}
);
