/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(dataApiHelpersTest, loginTest());

function getCard(page: Page, title: string) {
	return page.locator('.card').filter({hasText: title});
}

test(
	'Asset library folders can be edited, copied with their documents and deleted',
	{tag: '@LPD-112510'},
	async ({apiHelpers, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const depotURL = `/asset-library-${assetLibrary.id}`;

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			assetLibrary.siteId
		);

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		const targetFolder =
			await apiHelpers.headlessDelivery.postDocumentFolder(
				assetLibrary.siteId
			);

		const documentLibraryPage = new DocumentLibraryPage(page);

		const folderName = getRandomString();

		await test.step('Edit the folder', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.goToEditFolder(folder.name);

			await page.getByLabel('Name').fill(folderName);

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(page);

			await expect(getCard(page, folderName)).toBeVisible();
			await expect(getCard(page, folder.name)).toHaveCount(0);
		});

		await test.step('Copy the folder with its document to another folder', async () => {
			await documentLibraryPage.copyTo({
				entryTitle: folderName,
				folderName: targetFolder.name,
				isFolder: true,
			});

			await expect(async () => {
				await documentLibraryPage.gotoFolder({
					folderId: targetFolder.id,
					siteUrl: depotURL,
				});

				await page
					.getByRole('link', {exact: true, name: folderName})
					.click({timeout: 2000});

				await expect(getCard(page, documentTitle)).toBeVisible({
					timeout: 2000,
				});
			}).toPass();

			await documentLibraryPage.goto(depotURL);

			await expect(getCard(page, folderName)).toBeVisible();
		});

		await test.step('Delete the folder that holds the copy', async () => {
			await documentLibraryPage.moveFolderToRecycleBin(targetFolder.name);

			await expect(getCard(page, targetFolder.name)).toHaveCount(0);
			await expect(getCard(page, folderName)).toBeVisible();
		});
	}
);

test(
	'Asset library documents can be edited, copied and moved to a folder',
	{tag: '@LPD-112510'},
	async ({apiHelpers, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const depotURL = `/asset-library-${assetLibrary.id}`;

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			assetLibrary.siteId,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		const movedDocumentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			assetLibrary.siteId,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${movedDocumentTitle}.jpg`, title: movedDocumentTitle}
		);

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			assetLibrary.siteId
		);

		const documentLibraryPage = new DocumentLibraryPage(page);

		const editedDocumentTitle = getRandomString();

		await test.step('Edit the title and the file of a document', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.goToFileEntryAction(
				'Edit',
				documentTitle
			);

			await page
				.locator('input[type="file"]')
				.setInputFiles(
					path.join(__dirname, 'dependencies/Document_Small.jpeg')
				);

			await page.getByLabel('Title').fill(editedDocumentTitle);

			await page
				.getByRole('button', {exact: true, name: 'Publish'})
				.click();

			await waitForAlert(page);

			await documentLibraryPage.goToViewFileEntry(editedDocumentTitle);

			const sidebar = page.locator(
				'#_com_liferay_document_library_web_portlet_DLAdminPortlet_ContextualSidebar'
			);

			await clickAndExpectToBeVisible({
				target: sidebar.getByText('Document_Small.jpeg'),
				trigger: page.locator('[data-qa-id="infoButton"]'),
			});

			await expect(
				sidebar.locator('.label-item', {hasText: 'Version 2.0'})
			).toBeVisible();
		});

		await test.step('Copy the document to a folder', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.copyTo({
				entryTitle: editedDocumentTitle,
				folderName: folder.name,
			});

			await expect(async () => {
				await documentLibraryPage.gotoFolder({
					folderId: folder.id,
					siteUrl: depotURL,
				});

				await expect(getCard(page, editedDocumentTitle)).toBeVisible({
					timeout: 2000,
				});
			}).toPass();
		});

		await test.step('Move another document to the folder', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.moveTo(movedDocumentTitle, folder.name);

			await documentLibraryPage.goto(depotURL);

			await expect(getCard(page, editedDocumentTitle)).toBeVisible();
			await expect(getCard(page, movedDocumentTitle)).toHaveCount(0);

			await documentLibraryPage.gotoFolder({
				folderId: folder.id,
				siteUrl: depotURL,
			});

			await expect(getCard(page, movedDocumentTitle)).toBeVisible();
		});
	}
);

test(
	'Multiple documents can be uploaded and deleted together in an asset library',
	{tag: '@LPD-112510'},
	async ({apiHelpers, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const depotURL = `/asset-library-${assetLibrary.id}`;

		const documentLibraryPage = new DocumentLibraryPage(page);

		const fileNames = [
			'Document.jpg',
			'Document_Large.pdf',
			'Document_Small.jpeg',
		];

		await test.step('Upload several files at once', async () => {
			await documentLibraryPage.goto(depotURL);

			const tempFileNamesResponse = page.waitForResponse(
				(response) =>
					response.url().includes('/api/jsonws/invoke') &&
					!!response
						.request()
						.postData()
						?.includes('/dlapp/get-temp-file-names')
			);

			await documentLibraryPage.goToCreateNewFileWithDifferentType(
				'Multiple Files Upload'
			);

			await tempFileNamesResponse;

			await page
				.locator('input[type="file"]')
				.setInputFiles(
					fileNames.map((fileName) =>
						path.join(__dirname, 'dependencies', fileName)
					)
				);

			await expect(
				page.getByText('All files ready to be saved.')
			).toBeVisible();

			for (const fileName of fileNames) {
				await expect(
					page.getByRole('checkbox', {exact: true, name: fileName})
				).toBeChecked();
			}

			await page
				.getByRole('button', {exact: true, name: 'Publish'})
				.click();

			for (const fileName of fileNames) {
				await expect(
					page.getByRole('link', {
						exact: true,
						name: fileName.replace(/\.[^.]+$/, ''),
					})
				).toBeVisible();
			}
		});

		await test.step('Delete all of them together', async () => {
			await documentLibraryPage.goto(depotURL);

			await page.getByLabel('Select All Items on the Page').check();

			await page
				.getByRole('button', {exact: true, name: 'Delete'})
				.click();

			await waitForAlert(page, 'moved to the Recycle Bin');

			await expect(
				page.getByText(
					'There are no documents or media files in this folder.'
				)
			).toBeVisible();
		});
	}
);

test(
	'Asset library documents can be filtered by category, extension and tag',
	{tag: '@LPD-112510'},
	async ({apiHelpers, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const depotURL = `/asset-library-${assetLibrary.id}`;

		const vocabularyName = getRandomString();

		const [category1, category2] = await createCategories({
			apiHelpers,
			categoryNames: [
				{name: getRandomString()},
				{name: getRandomString()},
			],
			siteId: String(assetLibrary.siteId),
			vocabularyName,
		});

		const [tag1, tag2] = [getRandomString(), getRandomString()];

		const imageTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			assetLibrary.siteId,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{
				fileName: `${imageTitle}.jpg`,
				keywords: [tag1, tag2],
				taxonomyCategoryIds: [category1.id, category2.id],
				title: imageTitle,
			}
		);

		const pdfTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			assetLibrary.siteId,
			createReadStream(path.join(__dirname, 'dependencies/Document.pdf')),
			{
				fileName: `${pdfTitle}.pdf`,
				keywords: [tag2],
				taxonomyCategoryIds: [category2.id],
				title: pdfTitle,
			}
		);

		const documentLibraryPage = new DocumentLibraryPage(page);

		const imageLink = page.getByRole('link', {
			exact: true,
			name: imageTitle,
		});
		const pdfLink = page.getByRole('link', {exact: true, name: pdfTitle});

		await test.step('Filter by a category of the asset library', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.openFilter('Categories');

			const categoriesModal =
				documentLibraryPage.filterByCategoriesFrameLocator;

			await clickAndExpectToBeVisible({
				target: categoriesModal.getByText(category1.name, {
					exact: true,
				}),
				trigger: categoriesModal.getByText(vocabularyName, {
					exact: false,
				}),
			});

			await categoriesModal
				.getByText(category1.name, {exact: true})
				.click();

			await page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await expect(
				page.getByText('1 Result Found With Filters')
			).toBeVisible();

			await expect(imageLink).toBeVisible();
			await expect(pdfLink).toBeHidden();
		});

		await test.step('Filter by extension', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.openFilter('Extension');

			await page
				.frameLocator('iframe[title="Filter by Extension"]')
				.getByRole('checkbox', {name: 'Vectorial (1 Item)'})
				.check();

			await page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Apply'})
				.click();

			await expect(pdfLink).toBeVisible();
			await expect(imageLink).toBeHidden();
		});

		await test.step('Filter by tag', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.openFilter('Tags');

			await page
				.frameLocator('iframe[title="Filter by Tags"]')
				.getByRole('row', {name: tag1})
				.getByRole('checkbox')
				.check();

			await page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await expect(imageLink).toBeVisible();
			await expect(pdfLink).toBeHidden();
		});
	}
);
