/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {webContentDisplayPageTest} from '../../../fixtures/webContentDisplayPageTest';
import {WebContentDisplayPage} from '../../../pages/journal-content-web/WebContentDisplayPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	webContentDisplayPageTest
);

const JOURNAL_CONTENT_PORTLET =
	'com_liferay_journal_content_web_portlet_JournalContentPortlet';

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
