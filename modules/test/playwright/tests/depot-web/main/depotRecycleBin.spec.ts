/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import {RecycleBinPage} from '../../../pages/trash-web/RecycleBinPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(dataApiHelpersTest, loginTest());

const DEPOT_ADMIN_NAMESPACE =
	'_com_liferay_depot_web_portlet_DepotAdminPortlet_';

function acceptDialog(page: Page) {
	return new Promise<string>((resolve, reject) => {
		const timeout = setTimeout(
			() => reject(new Error('No dialog was opened')),
			5000
		);

		page.once('dialog', async (dialog) => {
			clearTimeout(timeout);

			const message = dialog.message();

			await dialog.accept();

			resolve(message);
		});
	});
}

async function addDepotContent({
	apiHelpers,
	documentCount,
}: {
	apiHelpers: DataApiHelpers;
	documentCount: number;
}) {
	const assetLibrary =
		await apiHelpers.headlessAssetLibrary.createAssetLibrary();

	const documentTitles = [];

	for (let i = 0; i < documentCount; i++) {
		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary.id,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		documentTitles.push(documentTitle);
	}

	const webContentTitle = getRandomString();

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
		groupId: assetLibrary.siteId,
		titleMap: {en_US: webContentTitle},
	});

	return {
		assetLibrary,
		depotURL: `/asset-library-${assetLibrary.id}`,
		documentTitles,
		webContentTitle,
	};
}

test(
	'Documents and web content can be restored from the depot recycle bin',
	{tag: '@LPD-109097'},
	async ({apiHelpers, page}) => {
		const {depotURL, documentTitles, webContentTitle} =
			await addDepotContent({
				apiHelpers,
				documentCount: 2,
			});

		const documentLibraryPage = new DocumentLibraryPage(page);
		const recycleBinPage = new RecycleBinPage(page);
		const webContentPage = new WebContentPage(page);

		await test.step('Move the documents and the web content to the recycle bin', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.changeView('Cards');

			for (const documentTitle of documentTitles) {
				await documentLibraryPage.moveToRecycleBin(documentTitle);
			}

			await webContentPage.goto(depotURL);

			await webContentPage.moveToRecycleBin(webContentTitle);
		});

		await test.step('Restore them from the depot recycle bin', async () => {
			await recycleBinPage.goto(depotURL);

			for (const documentTitle of documentTitles) {
				await recycleBinPage.assertEntry(documentTitle, 'Document');
			}

			await recycleBinPage.assertEntry(
				webContentTitle,
				'Web Content Article'
			);

			await recycleBinPage.bulkRestore(documentTitles);

			await recycleBinPage.restore(webContentTitle);

			await recycleBinPage.assertEntryAbsent(webContentTitle);
		});

		await test.step('The restored entries are back in the depot', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.changeView('Cards');

			for (const documentTitle of documentTitles) {
				await expect(
					page.locator('.card').filter({hasText: documentTitle})
				).toBeVisible();
			}

			await webContentPage.goto(depotURL);

			await webContentPage.assertEntryPresent(webContentTitle);
		});
	}
);

test(
	'Documents and web content are deleted immediately when the depot recycle bin is disabled',
	{tag: '@LPD-109097'},
	async ({apiHelpers, page}) => {
		const {assetLibrary, depotURL, documentTitles, webContentTitle} =
			await addDepotContent({
				apiHelpers,
				documentCount: 1,
			});

		const documentLibraryPage = new DocumentLibraryPage(page);
		const recycleBinPage = new RecycleBinPage(page);
		const webContentPage = new WebContentPage(page);

		await test.step('Disable the depot recycle bin', async () => {
			await page.goto(
				`/group/guest${PORTLET_URLS.depotAdmin}&${DEPOT_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fdepot%2Fedit_depot_entry&${DEPOT_ADMIN_NAMESPACE}depotEntryId=${assetLibrary.id}`
			);

			const recycleBinPanel = page.getByRole('button', {
				exact: true,
				name: 'Recycle Bin',
			});

			if (
				(await recycleBinPanel.getAttribute('aria-expanded')) !== 'true'
			) {
				await recycleBinPanel.click();
			}

			const dialogMessage = acceptDialog(page);

			await page.getByLabel('Enable Recycle Bin').uncheck();

			expect(await dialogMessage).toBe(
				'Disabling the Recycle Bin prevents the restoring of content that has been moved to the Recycle Bin.'
			);

			await expect(
				page.getByLabel('Enable Recycle Bin')
			).not.toBeChecked();

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(page);
		});

		await test.step('Delete the document immediately', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.changeView('Cards');

			await documentLibraryPage.selectFileEntry(documentTitles[0]);

			const dialogMessage = acceptDialog(page);

			await page
				.getByRole('button', {exact: true, name: 'Delete'})
				.click();

			expect(await dialogMessage).toBe(
				'Are you sure you want to delete the selected entries? They will be deleted immediately.'
			);

			await waitForAlert(page);

			await expect(
				page.locator('.card').filter({hasText: documentTitles[0]})
			).toHaveCount(0);
		});

		await test.step('Delete the web content immediately', async () => {
			await webContentPage.goto(depotURL);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page
					.locator('.dropdown-menu:visible')
					.getByText('Delete', {exact: true}),
				trigger: page.getByRole('button', {
					name: `Actions for ${webContentTitle}`,
				}),
			});

			const dialog = page.getByRole('dialog');

			await expect(
				dialog.getByText(
					'Are you sure you want to delete this? It will be deleted immediately.'
				)
			).toBeVisible();

			await dialog
				.getByRole('button', {exact: true, name: 'Delete'})
				.click();

			await waitForAlert(page);

			await webContentPage.assertEntryAbsent(webContentTitle);
		});

		await test.step('Nothing reaches the depot recycle bin', async () => {
			await recycleBinPage.goto(depotURL);

			await recycleBinPage.assertEntryAbsent(documentTitles[0]);
			await recycleBinPage.assertEntryAbsent(webContentTitle);
		});
	}
);
