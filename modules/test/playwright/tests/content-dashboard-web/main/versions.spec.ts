/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForPageToBeLoaded} from '../../../utils/waitForPageToBeLoaded';
import {contentDashboardPagesTest} from './fixtures/contentDashboardPagesTest';
import getDocumentFile from './utils/getDocumentFile';

export const test = mergeTests(
	contentDashboardPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Versions tab compares and expires web content versions',
	{tag: ['@LPS-162826', '@LPS-162827']},
	async ({apiHelpers, contentDashboardPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const comparedWebContentTitle = getRandomString();

		const content = getRandomString();

		const comparedWebContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content,
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: comparedWebContentTitle},
			});

		const editedContent = `${content} Edit`;

		await apiHelpers.jsonWebServicesJournal.editWebContent(
			{content: editedContent, title: comparedWebContentTitle},
			site.id,
			comparedWebContent
		);

		const expiredWebContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: basicWebContentStructureId,
			siteId: site.id,
			title: expiredWebContentTitle,
		});

		await test.step('CompareToVersionWebContent', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(comparedWebContentTitle);

			await contentDashboardPage.openInfoPanelTab('Versions');

			await contentDashboardPage.clickVersionAction(
				'1.0',
				'Compare to...'
			);

			await expect(page.locator('.diff-container-column')).toContainText(
				editedContent
			);

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(comparedWebContentTitle);

			await contentDashboardPage.openInfoPanelTab('Versions');

			await contentDashboardPage.clickVersionAction(
				'1.1',
				'Compare to...'
			);

			await expect(page.locator('.management-bar-item-title')).toHaveText(
				['Version 1.1', 'Version 1.1']
			);
		});

		await test.step('ExpireVersionLabel', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(expiredWebContentTitle);

			await contentDashboardPage.openInfoPanelTab('Versions');

			await contentDashboardPage.clickVersionAction('1.0', 'Expire');

			await expect(
				contentDashboardPage.getCell(expiredWebContentTitle, 'status')
			).toHaveText('Expired');
		});
	}
);

test(
	'Versions tab downloads, views and reverts document versions and links to the version history',
	{tag: ['@LPS-161017', '@LPS-162823', '@LPS-162824', '@LPS-164334']},
	async ({apiHelpers, contentDashboardPage, page, site}) => {
		const documentTitle = getRandomString();

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${documentTitle}.txt`),
			{fileName: `${documentTitle}.txt`, title: documentTitle}
		);

		const editedDocumentTitle = getRandomString();

		await apiHelpers.headlessDelivery.patchDocument({
			document: {title: editedDocumentTitle},
			documentId: document.id,
		});

		const historyDocumentTitle = getRandomString();

		const historyDocument = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${historyDocumentTitle}.txt`),
			{
				fileName: `${historyDocumentTitle}.txt`,
				title: historyDocumentTitle,
			}
		);

		for (let i = 0; i < 10; i++) {
			await apiHelpers.headlessDelivery.patchDocument({
				document: {description: String(i)},
				documentId: historyDocument.id,
			});
		}

		await test.step('DownloadDocumentFromVersionsTab', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(editedDocumentTitle);

			await contentDashboardPage.openInfoPanelTab('Versions');

			const downloadPromise = page.waitForEvent('download');

			await contentDashboardPage.clickVersionAction('1.0', 'Download');

			const download = await downloadPromise;

			expect(download.suggestedFilename()).toBe(`${documentTitle}.txt`);
		});

		await test.step('ViewDocumentVersion', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(editedDocumentTitle);

			await contentDashboardPage.openInfoPanelTab('Versions');

			await contentDashboardPage.clickVersionAction('1.0', 'View');

			await expect(
				page.getByRole('heading', {
					name: `${documentTitle} (Version 1.0)`,
				})
			).toBeVisible();
		});

		await test.step('RevertDocumentVersion', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(editedDocumentTitle);

			await contentDashboardPage.openInfoPanelTab('Versions');

			await contentDashboardPage.clickVersionAction('1.0', 'Revert');

			await expect(contentDashboardPage.infoPanel).toHaveCount(0);

			await waitForPageToBeLoaded(page);

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(documentTitle);

			await contentDashboardPage.openInfoPanelTab('Versions');

			await expect(contentDashboardPage.versionTitles.first()).toHaveText(
				'Version 2.0'
			);

			await expect(
				contentDashboardPage
					.getVersion('2.0')
					.locator('.list-group-subtext')
			).toHaveText('Reverted to 1.0');
		});

		await test.step('ViewMoreButtonToDocumentsVersionHistory', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(historyDocumentTitle);

			await contentDashboardPage.openInfoPanelTab('Versions');

			await contentDashboardPage.viewMoreVersionsLink.click();

			await expect(page).toHaveURL(
				/document_library%2Fview_file_entry_history/
			);

			await expect(
				page
					.getByLabel('Control Menu')
					.getByRole('heading', {name: historyDocumentTitle})
			).toBeVisible();

			await expect(page.locator('td.lfr-version-column')).toHaveCount(11);
		});
	}
);
