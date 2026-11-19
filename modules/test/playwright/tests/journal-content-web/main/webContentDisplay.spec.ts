/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {commentsPagesTest} from '../../../fixtures/commentsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {webContentDisplayPageTest} from '../../../fixtures/webContentDisplayPageTest';
import {WebContentDisplayPage} from '../../../pages/journal-content-web/WebContentDisplayPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';

const test = mergeTests(
	commentsPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	webContentDisplayPageTest
);

const JOURNAL_CONTENT_PORTLET =
	'com_liferay_journal_content_web_portlet_JournalContentPortlet';

async function addWebContentFromMenu(
	page: Page,
	portletId: string,
	structureName: string
) {
	await page.locator(`#portlet_${portletId}`).hover();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: structureName}),
		timeout: 1000,
		trigger: page
			.locator(`[id^="portlet-title-menu_${portletId}_"]`)
			.getByRole('button', {name: 'Add'}),
	});
}

async function openWebContentSelector(
	page: Page,
	webContentDisplayPage: WebContentDisplayPage
) {
	await page
		.getByRole('button', {name: 'Select web content to make it visible.'})
		.click();

	await webContentDisplayPage.configurationFrameSelectButton.click();
}

async function selectWebContent(
	title: string,
	webContentDisplayPage: WebContentDisplayPage
) {
	await clickAndExpectToBeVisible({
		target: webContentDisplayPage.configurationFrameChangeButton,
		trigger:
			webContentDisplayPage.selectWebContentInConfigurationFrame.getByText(
				title,
				{exact: true}
			),
	});

	await webContentDisplayPage.saveConfigurationFrameOptions();
}

test(
	'Web Content Display shows a message for draft, trashed and deleted web content',
	{tag: '@LPD-115324'},
	async ({apiHelpers, browser, page, site, webContentDisplayPage}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const approvedContent = getRandomString();
		const approvedTitle = getRandomString();
		const draftTitle = getRandomString();

		const approvedWebContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: approvedContent,
				ddmStructureId,
				groupId: site.id,
				serviceContext: {addGuestPermissions: true},
				titleMap: {en_US: approvedTitle},
			});
		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			serviceContext: {workflowAction: 2},
			titleMap: {en_US: draftTitle},
		});

		const draftLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
			typeSettings: `layout-template-id=1_column\ncolumn-1=${JOURNAL_CONTENT_PORTLET}_INSTANCE_draft\n`,
		});

		await page.goto(
			`/web${site.friendlyUrlPath}${draftLayout.friendlyURL}`
		);

		await openWebContentSelector(page, webContentDisplayPage);
		await selectWebContent(draftTitle, webContentDisplayPage);

		await expect(
			page.getByText(`${draftTitle} is not approved.`, {exact: true})
		).toBeVisible();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
			typeSettings: `layout-template-id=1_column\ncolumn-1=${JOURNAL_CONTENT_PORTLET}_INSTANCE_approved\n`,
		});

		const layoutURL = `/web${site.friendlyUrlPath}${layout.friendlyURL}`;

		await page.goto(layoutURL);

		await openWebContentSelector(page, webContentDisplayPage);
		await selectWebContent(approvedTitle, webContentDisplayPage);

		await expect(
			page.getByText(approvedContent, {exact: true})
		).toBeVisible();

		await apiHelpers.jsonWebServicesJournal.moveArticleToTrash(
			String(site.id),
			approvedWebContent.articleId!
		);

		await page.goto(layoutURL);

		const recycleBinAlert = page.getByRole('alert').filter({
			hasText: `The web content article ${approvedTitle} was moved to the Recycle Bin.`,
		});

		await expect(recycleBinAlert).toContainText(
			'Do you want to Undo or Select Another web content?'
		);

		await recycleBinAlert.getByRole('link', {name: 'Undo'}).click();

		await expect(
			page.getByText(approvedContent, {exact: true})
		).toBeVisible();

		const guestContext = await browser.newContext();

		const guestPage = await guestContext.newPage();

		await guestPage.goto(layoutURL);

		await expect(
			guestPage.getByText(approvedContent, {exact: true})
		).toBeVisible();

		await apiHelpers.jsonWebServicesJournal.deleteArticle(
			String(site.id),
			approvedWebContent.articleId!
		);

		await page.goto(layoutURL);

		await expect(
			page.getByRole('alert').filter({
				hasText: 'The selected web content no longer exists.',
			})
		).toContainText('Do you want to Select Another web content?');

		await guestPage.goto(layoutURL);

		await expect(
			guestPage.getByText('The selected web content no longer exists.')
		).toBeHidden();
		await expect(
			guestPage.getByText(approvedContent, {exact: true})
		).toBeHidden();

		await guestContext.close();
	}
);

test(
	'Web Content Display shows web content selected from folders and search results',
	{tag: ['@LPS-169728', '@LPD-115324']},
	async ({apiHelpers, page, site, webContentDisplayPage}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const brakeSystemFolder =
			await apiHelpers.jsonWebServicesJournal.addFolder({
				groupId: site.id,
				name: 'Brake System',
			});

		await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
			name: 'Exhaust System',
		});

		const absSensorFolder =
			await apiHelpers.jsonWebServicesJournal.addFolder({
				groupId: site.id,
				name: 'ABS Sensor',
				parentFolderId: brakeSystemFolder.folderId,
			});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			folderId: absSensorFolder.folderId,
			groupId: site.id,
			titleMap: {en_US: 'Tesla ABS Wheel Speed Sensor'},
		});
		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: 'Web Content Content 1',
				ddmStructureId,
				groupId: site.id,
				titleMap: {en_US: 'Web Content Title 1'},
			});
		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: 'Web Content Title 2'},
		});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
			typeSettings: `layout-template-id=1_column\ncolumn-1=${JOURNAL_CONTENT_PORTLET}_INSTANCE_selector\n`,
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		const notVisibleAlert = page.getByRole('alert').filter({
			hasText: 'This application is not visible to users yet.',
		});

		await expect(notVisibleAlert).toBeVisible();

		await openWebContentSelector(page, webContentDisplayPage);

		const selectWebContentFrame =
			webContentDisplayPage.selectWebContentInConfigurationFrame;

		await expect(
			selectWebContentFrame.getByText('Web Content Title 1', {
				exact: true,
			})
		).toBeVisible();

		await selectWebContentFrame
			.getByText('Brake System', {exact: true})
			.click();
		await selectWebContentFrame
			.getByText('ABS Sensor', {exact: true})
			.click();

		await expect(
			selectWebContentFrame.getByText('Tesla ABS Wheel Speed Sensor', {
				exact: true,
			})
		).toBeVisible();

		await selectWebContentFrame
			.getByRole('link', {exact: true, name: site.name})
			.click();
		await selectWebContentFrame
			.getByText('Exhaust System', {exact: true})
			.click();

		await expect(
			selectWebContentFrame.getByText('No web content was found.')
		).toBeVisible();

		await selectWebContentFrame
			.getByRole('link', {exact: true, name: site.name})
			.click();

		await expect(
			selectWebContentFrame.getByText('Web Content Title 1', {
				exact: true,
			})
		).toBeVisible();

		const searchInput =
			selectWebContentFrame.getByPlaceholder('Search for');
		const searchButton = selectWebContentFrame.getByRole('button', {
			exact: true,
			name: 'Search for',
		});

		await searchInput.fill('ABS Sensor');
		await searchButton.click();

		await expect(
			selectWebContentFrame.getByText('2 Results Found for "ABS Sensor"')
		).toBeVisible();
		await expect(
			selectWebContentFrame.getByText('ABS Sensor', {exact: true})
		).toBeVisible();
		await expect(
			selectWebContentFrame.getByText('Tesla ABS Wheel Speed Sensor', {
				exact: true,
			})
		).toBeVisible();

		await searchInput.fill('"Web1 Content1 Title 1"');
		await searchButton.click();

		await expect(
			selectWebContentFrame.getByText('No web content was found.')
		).toBeVisible();
		await expect(
			selectWebContentFrame.getByText('Web Content Title 1', {
				exact: true,
			})
		).toBeHidden();

		await searchInput.fill('"Web Content Title 1"');
		await searchButton.click();

		await expect(
			selectWebContentFrame.getByText(
				'1 Result Found for ""Web Content Title 1""'
			)
		).toBeVisible();
		await expect(
			selectWebContentFrame.getByText('Web Content Title 2', {
				exact: true,
			})
		).toBeHidden();

		await searchInput.fill('Web Content Title 1');
		await searchButton.click();

		await expect(
			selectWebContentFrame.getByText(
				'2 Results Found for "Web Content Title 1"'
			)
		).toBeVisible();
		await expect(
			selectWebContentFrame.getByText('Web Content Title 1', {
				exact: true,
			})
		).toBeVisible();
		await expect(
			selectWebContentFrame.getByText('Web Content Title 2', {
				exact: true,
			})
		).toBeVisible();

		await selectWebContent('Web Content Title 1', webContentDisplayPage);

		await expect(
			page.getByText('Web Content Content 1', {exact: true})
		).toBeVisible();

		await apiHelpers.jsonWebServicesJournal.expireArticle(
			String(site.id),
			webContent.articleId!
		);

		await page.reload();

		await expect(
			page.getByText('Web Content Title 1 is expired.', {exact: true})
		).toBeVisible();
		await expect(
			page.getByText('Web Content Content 1', {exact: true})
		).toBeHidden();

		await page
			.locator(`[id^="portlet_${JOURNAL_CONTENT_PORTLET}_INSTANCE_"]`)
			.hover();
		await page
			.locator(`[id^="portlet_${JOURNAL_CONTENT_PORTLET}_INSTANCE_"]`)
			.getByRole('button', {name: 'Options'})
			.click();
		await webContentDisplayPage.configurationOption.click();

		await webContentDisplayPage.configurationFrame
			.getByRole('button', {name: 'Remove'})
			.click();

		await webContentDisplayPage.saveConfigurationFrameOptions();

		await expect(notVisibleAlert).toBeVisible();
		await expect(
			page.getByText('Web Content Content 1', {exact: true})
		).toBeHidden();
	}
);

test(
	'Web Content Display adds and edits web content and templates from its menus',
	{tag: ['@LPD-116674', '@LPS-129944']},
	async ({
		apiHelpers,
		journalEditArticlePage,
		journalEditTemplatePage,
		page,
		site,
		webContentDisplayPage,
	}) => {
		test.setTimeout(180000);

		const structureName = getRandomString();

		const dataDefinition = await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [
					{name: 'Text'},
					{displayStyle: 'multiline', name: 'TextBox'},
				],
				name: structureName,
			})
		);

		const ddmStructureClassName =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.dynamic.data.mapping.model.DDMStructure'
			);
		const journalArticleClassName =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.journal.model.JournalArticle'
			);

		await apiHelpers.jsonWebServicesDDM.addTemplate({
			classNameId: ddmStructureClassName.classNameId,
			classPK: dataDefinition.id,
			groupId: site.id,
			nameMap: {en_US: getRandomString()},
			resourceClassNameId: journalArticleClassName.classNameId,
			script: `<h1 class="text">\${Text.getData()}</h1>
<h2 class="text-box">\${TextBox.getData()}</h2>
<@liferay_portlet["runtime"] instanceId="nested" portletName="${JOURNAL_CONTENT_PORTLET}" />`,
		});

		const portletId = `${JOURNAL_CONTENT_PORTLET}_INSTANCE_menus`;

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
			typeSettings: `layout-template-id=1_column\ncolumn-1=${portletId}\n`,
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		const webContentDisplay = page.locator(`#portlet_${portletId}`);

		await addWebContentFromMenu(page, portletId, 'Basic Web Content');

		await journalEditArticlePage.fillTitle('Web Content Title');
		await journalEditArticlePage.expandFieldsPanel();
		await journalEditArticlePage.fillContent('Web Content Content');
		await journalEditArticlePage.publishArticle();

		await expect(
			webContentDisplay.getByRole('heading', {
				exact: true,
				name: 'Web Content Title',
			})
		).toBeVisible();
		await expect(
			webContentDisplay.getByText('Web Content Content', {exact: true})
		).toBeVisible();

		await webContentDisplay
			.getByText('Web Content Content', {exact: true})
			.hover();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit Web Content'}),
			timeout: 1000,
			trigger: webContentDisplay.getByRole('button', {
				name: 'Show Actions',
			}),
		});

		await journalEditArticlePage.fillTitle('Web Content Title Edit');
		await journalEditArticlePage.expandFieldsPanel();
		await journalEditArticlePage.fillContent('Web Content Content Edit');
		await journalEditArticlePage.publishArticle(true);

		await expect(
			webContentDisplay.getByRole('heading', {
				exact: true,
				name: 'Web Content Title Edit',
			})
		).toBeVisible();
		await expect(
			webContentDisplay.getByText('Web Content Content Edit', {
				exact: true,
			})
		).toBeVisible();

		await addWebContentFromMenu(page, portletId, structureName);

		await journalEditArticlePage.fillTitle('WC WebContent Title');
		await journalEditArticlePage.expandFieldsPanel();

		await fillAndClickOutside(
			page,
			page.getByRole('textbox', {exact: true, name: 'Text'}),
			'WC WebContent Text'
		);
		await fillAndClickOutside(
			page,
			page.getByRole('textbox', {exact: true, name: 'TextBox'}),
			'Hello World\nWelcome to Liferay Community Edition Portal 7.4.0 CE GA1'
		);

		await journalEditArticlePage.publishArticle();

		const textHeading = webContentDisplay.getByRole('heading', {
			exact: true,
			level: 1,
			name: 'WC WebContent Text',
		});

		await expect(textHeading).toBeVisible();
		await expect(
			webContentDisplay.getByRole('heading', {
				exact: true,
				level: 2,
				name: 'Hello World Welcome to Liferay Community Edition Portal 7.4.0 CE GA1',
			})
		).toBeVisible();

		const nestedNotVisibleAlert = page
			.locator(`#p_p_id_${JOURNAL_CONTENT_PORTLET}_INSTANCE_nested_`)
			.getByRole('alert')
			.filter({hasText: 'This application is not visible to users yet.'});

		await expect(nestedNotVisibleAlert).toBeVisible();

		await webContentDisplay.hover();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menu').getByRole('menuitem', {
				exact: true,
				name: 'Configuration',
			}),
			timeout: 1000,
			trigger: page
				.locator(`#portlet-topper-toolbar_${portletId}`)
				.getByRole('button', {name: 'Options'}),
		});

		await webContentDisplayPage.configurationFrame
			.getByLabel('Related Assets', {exact: true})
			.check();

		await webContentDisplayPage.configurationFrame
			.getByRole('button', {name: 'Save'})
			.click();

		await expect(
			webContentDisplayPage.configurationFrame.getByText(
				'You have successfully updated the setup.'
			)
		).toBeVisible();

		await webContentDisplayPage.uiElementsPage.closeClickable.click();

		await expect(nestedNotVisibleAlert).toBeVisible();
		await expect(textHeading).toBeVisible();

		await textHeading.hover();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit Template'}),
			timeout: 1000,
			trigger: webContentDisplay.getByRole('button', {
				name: 'Show Actions',
			}),
		});

		await journalEditTemplatePage.editTemplate(
			'Template Edit',
			'Template Edited Extra Content'
		);

		await journalEditTemplatePage.saveButton.click();

		await expect(
			webContentDisplay.getByText('Template Edited Extra Content', {
				exact: true,
			})
		).toBeVisible();
	}
);

test(
	'Web Content Display shows the enabled metadata and user tools',
	{tag: '@LPD-116674'},
	async ({apiHelpers, commentsPage, page, site, webContentDisplayPage}) => {
		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const relatedStructuredContent =
			await apiHelpers.headlessDelivery.postStructuredContent({
				contentFields: [
					{
						contentFieldValue: {
							data: '<p>Web Content Content 2</p>',
						},
						name: 'content',
					},
				],
				contentStructureId,
				datePublished: null,
				siteId: site.id,
				title: 'Web Content Title 2',
			});

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentFields: [
				{
					contentFieldValue: {data: '<p>Web Content Content 1</p>'},
					contentFieldValue_i18n: {
						'en-US': {data: '<p>Web Content Content 1</p>'},
						'es-ES': {data: '<p>Web Contenido Contenido 1</p>'},
					},
					name: 'content',
				},
			],
			contentStructureId,
			datePublished: null,
			relatedContents: [
				{
					contentType: 'StructuredContent',
					id: relatedStructuredContent.id,
					title: 'Web Content Title 2',
				},
			],
			siteId: site.id,
			title: 'Web Content Title 1',
			title_i18n: {
				'en-US': 'Web Content Title 1',
				'es-ES': 'Web Contenido Título 1',
			},
		});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
			typeSettings: `layout-template-id=1_column\ncolumn-1=${JOURNAL_CONTENT_PORTLET}_INSTANCE_settings\n`,
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await openWebContentSelector(page, webContentDisplayPage);

		await clickAndExpectToBeVisible({
			target: webContentDisplayPage.configurationFrameChangeButton,
			trigger:
				webContentDisplayPage.selectWebContentInConfigurationFrame.getByText(
					'Web Content Title 1',
					{exact: true}
				),
		});

		const configurationFrame = webContentDisplayPage.configurationFrame;

		await configurationFrame
			.getByLabel('Comment Ratings', {exact: true})
			.check();
		await configurationFrame.getByLabel('Comments', {exact: true}).check();
		await configurationFrame.getByLabel('Print', {exact: true}).check();
		await configurationFrame.getByLabel('Ratings', {exact: true}).check();
		await configurationFrame
			.getByLabel('Related Assets', {exact: true})
			.check();
		await configurationFrame
			.getByLabel('Translations', {exact: true})
			.check();
		await configurationFrame
			.getByLabel('View Count Increment', {exact: true})
			.check();

		await webContentDisplayPage.saveConfigurationFrameOptions();

		await expect(
			page.getByRole('button', {name: 'Print Web Content Title 1'})
		).toBeVisible();
		await expect(
			page.getByRole('button', {name: 'Subscribe to Comments'})
		).toBeVisible();
		await expect(
			page.getByRole('link', {name: 'Web Content Title 2'})
		).toBeVisible();

		await commentsPage.addComment('Hello!');

		const comment = page
			.locator('.comment-container')
			.filter({hasText: 'Hello!'});

		await expect(comment.getByTitle('Rate this as bad')).toBeVisible();
		await expect(comment.getByTitle('Rate this as good')).toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'español-España'}),
			timeout: 1000,
			trigger: page.getByTitle('Select a Language'),
		});

		await expect(
			page.getByRole('heading', {
				exact: true,
				name: 'Web Contenido Título 1',
			})
		).toBeVisible();
		await expect(
			page.getByText('Web Contenido Contenido 1', {exact: true})
		).toBeVisible();
	}
);
