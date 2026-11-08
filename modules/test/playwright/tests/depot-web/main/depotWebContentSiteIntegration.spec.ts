/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {assetPublisherPagesTest} from '../../../fixtures/assetPublisherPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {webContentDisplayPageTest} from '../../../fixtures/webContentDisplayPageTest';
import {WebContentDisplayPage} from '../../../pages/journal-content-web/WebContentDisplayPage';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import addConnectedDepot from './utils/addConnectedDepot';
import gotoDepotInItemSelector from './utils/gotoDepotInItemSelector';

const test = mergeTests(
	assetPublisherPagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
	}),
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	pageEditorPagesTest,
	pageViewModePagesTest,
	webContentDisplayPageTest
);

async function selectDepotWebContentInContentDisplay(
	page: Page,
	depotName: string,
	title: string
) {
	await page.getByLabel('Select Item').click();

	const selectItemMenuItem = page.getByRole('menuitem', {
		name: 'Select Item',
	});

	if (await selectItemMenuItem.isVisible()) {
		await selectItemMenuItem.click();
	}

	const itemSelector = page.frameLocator('iframe[title="Select"]');

	await itemSelector
		.getByRole('menuitem', {exact: true, name: 'Web Content'})
		.click();

	await gotoDepotInItemSelector(itemSelector, depotName);

	await clickAndExpectToBeHidden({
		target: page.locator('iframe[title="Select"]'),
		trigger: itemSelector.getByText(title, {exact: true}),
	});
}

async function selectDepotWebContentInWebContentDisplay({
	change = false,
	depotName,
	optionsButton,
	title,
	webContentDisplayPage,
}: {
	change?: boolean;
	depotName: string;
	optionsButton: Locator;
	title: string;
	webContentDisplayPage: WebContentDisplayPage;
}) {
	await clickAndExpectToBeVisible({
		target: webContentDisplayPage.configurationOption,
		trigger: optionsButton,
	});

	await webContentDisplayPage.configurationOption.click();

	await (
		change
			? webContentDisplayPage.configurationFrameChangeButton
			: webContentDisplayPage.configurationFrameSelectButton
	).click();

	const itemSelector =
		webContentDisplayPage.selectWebContentInConfigurationFrame;

	await gotoDepotInItemSelector(itemSelector, depotName);

	await clickAndExpectToBeHidden({
		target: webContentDisplayPage.configurationFrame.locator(
			'iframe[title="Select Web Content"]'
		),
		trigger: itemSelector.getByText(title, {exact: true}),
	});

	await webContentDisplayPage.saveConfigurationFrameOptions();
}

test(
	'Web Content Display shows web content from connected asset libraries',
	{tag: '@LPD-112512'},
	async ({apiHelpers, page, site, webContentDisplayPage, widgetPagePage}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const webContents = [];

		for (let i = 0; i < 2; i++) {
			const depot = await addConnectedDepot(apiHelpers, site);

			const content = getRandomString();

			const webContent =
				await apiHelpers.jsonWebServicesJournal.addWebContent({
					content,
					ddmStructureId,
					groupId: depot.siteId,
					titleMap: {en_US: getRandomString()},
				});

			webContents.push({content, depotName: depot.name, webContent});
		}

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'portlet'},
			title: getRandomString(),
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await widgetPagePage.addPortlet(
			'Web Content Display',
			'Content Management'
		);

		for (const [
			i,
			{content, depotName, webContent},
		] of webContents.entries()) {
			await test.step(`Select ${webContent.title}`, async () => {
				await widgetPagePage.goto(layout, site.friendlyUrlPath);

				await selectDepotWebContentInWebContentDisplay({
					change: i > 0,
					depotName,
					optionsButton: page
						.locator('[id*="JournalContentPortlet"]')
						.getByRole('button', {name: 'Options'}),
					title: webContent.title,
					webContentDisplayPage,
				});

				await widgetPagePage.goto(layout, site.friendlyUrlPath);

				await expect(
					page.getByText(content, {exact: true})
				).toBeVisible();
			});
		}
	}
);

test(
	'Content Display shows asset library web content in a content page and a display page template',
	{tag: '@LPD-112512'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
		webContentDisplayPage,
	}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const depot = await addConnectedDepot(apiHelpers, site);

		const content = getRandomString();

		const depotWebContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content,
				ddmStructureId,
				groupId: depot.siteId,
				titleMap: {en_US: getRandomString()},
			});

		await test.step('Content page', async () => {
			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {type: 'content'},
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.addFragment(
				'Content Display',
				'Content Display'
			);

			await selectDepotWebContentInContentDisplay(
				page,
				depot.name,
				depotWebContent.title
			);

			await pageEditorPage.addWidget(
				'Content Management',
				'Web Content Display'
			);

			await selectDepotWebContentInWebContentDisplay({
				depotName: depot.name,
				optionsButton: page
					.locator('#wrapper')
					.getByText('Web Content Display')
					.last()
					.locator('..')
					.getByRole('button', {name: 'Options'}),
				title: depotWebContent.title,
				webContentDisplayPage,
			});

			await pageEditorPage.publishPage();

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await expect(page.getByText(content, {exact: true})).toHaveCount(2);
		});

		await test.step('Display page template', async () => {
			const name = getRandomString();

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.createTemplate({
				contentSubtype: 'Basic Web Content',
				contentType: 'Web Content Article',
				name,
			});

			const layoutPageTemplateEntry =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.fetchLayoutPageTemplateEntry(
					{groupId: site.id, name, type: 'display-page'}
				);

			apiHelpers.data.push({
				id: layoutPageTemplateEntry.layoutPageTemplateEntryId,
				type: 'layoutPageTemplateEntry',
			});

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
				{
					layoutPageTemplateEntryId:
						layoutPageTemplateEntry.layoutPageTemplateEntryId,
				}
			);

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.editTemplate(name);

			await pageEditorPage.addFragment(
				'Content Display',
				'Content Display'
			);

			await selectDepotWebContentInContentDisplay(
				page,
				depot.name,
				depotWebContent.title
			);

			await pageEditorPage.addWidget(
				'Content Management',
				'Web Content Display'
			);

			await selectDepotWebContentInWebContentDisplay({
				depotName: depot.name,
				optionsButton: page
					.locator('#wrapper')
					.getByText('Web Content Display')
					.last()
					.locator('..')
					.getByRole('button', {name: 'Options'}),
				title: depotWebContent.title,
				webContentDisplayPage,
			});

			await displayPageTemplatesPage.publishTemplate();

			const webContentTitle = getRandomString();

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId,
				groupId: site.id,
				titleMap: {en_US: webContentTitle},
			});

			await page.goto(`/web${site.friendlyUrlPath}/w/${webContentTitle}`);

			await expect(page.getByText(content, {exact: true})).toHaveCount(2);
		});
	}
);

test(
	'A connected site uses the web content structures of an asset library',
	{tag: '@LPD-112512'},
	async ({
		apiHelpers,
		assetPublisherPage,
		journalEditArticlePage,
		journalEditTemplatePage,
		journalPage,
		journalStructuresPage,
		page,
		site,
		webContentDisplayPage,
		widgetPagePage,
	}) => {
		const depot = await addConnectedDepot(apiHelpers, site, {
			ddmStructuresAvailable: true,
		});

		const structureName = getRandomString();

		await apiHelpers.dataEngine.createStructure(
			depot.siteId,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{name: 'Text'}],
				name: structureName,
			})
		);

		const siteStructureName = getRandomString();

		for (let i = 0; i < 8; i++) {
			await apiHelpers.dataEngine.createStructure(
				site.id,
				getDataStructureDefinition({
					defaultLanguageId: 'en_US',
					fields: [{name: 'Text'}],
					name: `${siteStructureName}${i}`,
				})
			);
		}

		await test.step('The site lists only its own structures', async () => {
			await journalStructuresPage.goto(site.friendlyUrlPath);

			await expect(
				page.getByRole('link', {name: `${siteStructureName}0`})
			).toBeVisible();
			await expect(
				page.getByRole('link', {exact: true, name: structureName})
			).toHaveCount(0);
		});

		await test.step('Add a site template for the structure', async () => {
			await journalEditTemplatePage.goto(site.friendlyUrlPath);

			const templateName = getRandomString();

			await journalEditTemplatePage.selectStructure(structureName);

			await expect(
				page.getByText(`New Template for Structure: ${structureName}`)
			).toBeVisible();

			await journalEditTemplatePage.editTemplate(templateName, '${Text}');
			await journalEditTemplatePage.saveTemplate();

			await expect(
				page.getByRole('link', {exact: true, name: templateName})
			).toBeVisible();
		});

		await test.step('The structure scope is the asset library', async () => {
			await journalPage.goto(site.friendlyUrlPath);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('button', {exact: true, name: 'More'}),
				trigger: journalPage.newButton,
			});

			const row = page
				.frameLocator('iframe[title="More"]')
				.getByRole('row')
				.filter({hasText: structureName});

			await expect(row).toContainText(depot.name);

			await row
				.getByRole('link', {exact: true, name: structureName})
				.click();
		});

		const text = getRandomString();
		const title = getRandomString();

		await test.step('Add a web content with the structure', async () => {
			await journalEditArticlePage.fillTitle(title);

			await journalEditArticlePage.expandFieldsPanel();

			await page.getByLabel('Text', {exact: true}).fill(text);

			await journalEditArticlePage.publishArticle();

			await expect(
				page
					.getByTestId('row')
					.getByRole('link', {exact: true, name: title})
			).toBeVisible();
		});

		await test.step('Display the web content', async () => {
			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {type: 'portlet'},
				title: getRandomString(),
			});

			await widgetPagePage.goto(layout, site.friendlyUrlPath);

			await widgetPagePage.addPortlet(
				'Web Content Display',
				'Content Management'
			);

			await webContentDisplayPage.addWebContentWithDisplay({
				pageType: 'widget',
				webContentName: title,
			});

			await widgetPagePage.addPortlet(
				'Asset Publisher',
				'Content Management'
			);

			await widgetPagePage.clickOnAction(
				'Asset Publisher',
				'Configuration'
			);

			await assetPublisherPage.changeAssetSelection('Dynamic');

			await assetPublisherPage.closeConfiguration();

			await widgetPagePage.goto(layout, site.friendlyUrlPath);

			await expect(page.getByText(text, {exact: true})).toBeVisible();
			await expect(
				page.locator('[id*="AssetPublisherPortlet"]').getByText(title)
			).toBeVisible();
		});
	}
);
