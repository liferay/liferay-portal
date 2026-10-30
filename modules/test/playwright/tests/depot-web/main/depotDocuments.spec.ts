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
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';

const test = mergeTests(dataApiHelpersTest, loginTest());

function getCard(page: Page, title: string) {
	return page.locator('.card').filter({hasText: title});
}

test(
	'Asset library folders and documents can be copied to another folder',
	{tag: '@LPD-112510'},
	async ({apiHelpers, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const depotURL = `/asset-library-${assetLibrary.id}`;

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			assetLibrary.siteId
		);

		const folderDocumentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{
				fileName: `${folderDocumentTitle}.jpg`,
				title: folderDocumentTitle,
			}
		);

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			assetLibrary.siteId,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		const targetFolder =
			await apiHelpers.headlessDelivery.postDocumentFolder(
				assetLibrary.siteId
			);

		const documentLibraryPage = new DocumentLibraryPage(page);

		await test.step('Copy the folder with its document and a document to another folder', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.copyTo({
				entryTitle: folder.name,
				folderName: targetFolder.name,
				isFolder: true,
			});

			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.copyTo({
				entryTitle: documentTitle,
				folderName: targetFolder.name,
			});
		});

		await test.step('The copies are in the target folder and the originals stay', async () => {
			await expect(async () => {
				await documentLibraryPage.gotoFolder({
					folderId: targetFolder.id,
					siteUrl: depotURL,
				});

				await expect(getCard(page, documentTitle)).toBeVisible({
					timeout: 2000,
				});

				await page
					.getByRole('link', {exact: true, name: folder.name})
					.click({timeout: 2000});

				await expect(getCard(page, folderDocumentTitle)).toBeVisible({
					timeout: 2000,
				});
			}).toPass();

			await documentLibraryPage.goto(depotURL);

			await expect(getCard(page, folder.name)).toBeVisible();
			await expect(getCard(page, documentTitle)).toBeVisible();
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

test(
	'Asset library document type with a metadata set shows its field values',
	{tag: '@LPD-112510'},
	async ({apiHelpers, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const depotURL = `/asset-library-${assetLibrary.id}`;

		const metadataSetName = getRandomString();
		const metadataFieldName = `Text${getRandomInt()}`;

		await apiHelpers.dataEngine.createDocumentMetadataSet(
			assetLibrary.siteId,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{fieldType: 'text', name: metadataFieldName}],
				name: metadataSetName,
			})
		);

		const documentLibraryPage = new DocumentLibraryPage(page);

		const documentTypeName = getRandomString();

		await test.step('Add a document type that uses the metadata set', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.changeTab('Document Types');

			await documentLibraryPage.openNewDLTypeButton();

			await page.getByTitle('Numeric', {exact: true}).dblclick();

			await page.getByPlaceholder('Untitled').fill(documentTypeName);

			await page
				.getByRole('tab', {name: 'panelAdditional Metadata Fields'})
				.click();

			const additionalMetadataFieldsPanel = page.getByRole('tabpanel', {
				name: 'Additional Metadata Fields Panel',
			});

			await additionalMetadataFieldsPanel
				.getByRole('link', {exact: true, name: 'Select'})
				.click();

			await page
				.frameLocator('iframe[title="Select Metadata Set"]')
				.getByText(metadataSetName, {exact: true})
				.click();

			await expect(
				additionalMetadataFieldsPanel.getByRole('row', {
					name: metadataSetName,
				})
			).toBeVisible();

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(page);
		});

		const documentTitle = getRandomString();
		const metadataValue = getRandomString();

		await test.step('Add a document of that type with values', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.goToCreateNewFileWithDifferentType(
				documentTypeName
			);

			await page
				.locator('input[type="file"]')
				.setInputFiles(
					path.join(__dirname, 'dependencies/Document.jpg')
				);

			await page.getByLabel('Title').fill(documentTitle);

			await page.getByLabel('Numeric', {exact: true}).fill('1234');

			await page.getByLabel(metadataFieldName).fill(metadataValue);

			await page
				.getByRole('button', {exact: true, name: 'Publish'})
				.click();

			await waitForAlert(page);
		});

		await test.step('The document shows the values', async () => {
			await documentLibraryPage.goToViewFileEntry(documentTitle);

			const sidebar = page.locator(
				'#_com_liferay_document_library_web_portlet_DLAdminPortlet_ContextualSidebar'
			);

			await clickAndExpectToBeVisible({
				target: sidebar.getByRole('button', {name: metadataSetName}),
				trigger: page.locator('[data-qa-id="infoButton"]'),
			});

			const numericField = sidebar.getByRole('textbox', {
				exact: true,
				name: 'Numeric',
			});

			await clickAndExpectToBeVisible({
				target: numericField,
				trigger: sidebar.getByRole('button', {name: documentTypeName}),
			});

			await expect(numericField).toHaveValue('1234');

			const metadataField = sidebar.getByRole('textbox', {
				exact: true,
				name: metadataFieldName,
			});

			await clickAndExpectToBeVisible({
				target: metadataField,
				trigger: sidebar.getByRole('button', {name: metadataSetName}),
			});

			await expect(metadataField).toHaveValue(metadataValue);
		});
	}
);

test(
	'Asset library shortcut to a site document opens that document',
	{tag: '@LPD-112510'},
	async ({apiHelpers, page}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const depotURL = `/asset-library-${assetLibrary.id}`;

		const documentLibraryPage = new DocumentLibraryPage(page);

		await test.step('Add a shortcut to the site document', async () => {
			await documentLibraryPage.goto(depotURL);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					exact: true,
					name: 'Shortcut',
				}),
				trigger: page.getByRole('button', {exact: true, name: 'New'}),
			});

			await page
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			const itemSelector = page.frameLocator('iframe[title*="Select"]');

			await itemSelector
				.getByRole('link', {exact: true, name: 'Sites and Libraries'})
				.click();
			await itemSelector.getByText(site.name, {exact: true}).click();
			await itemSelector.getByText(documentTitle, {exact: true}).click();

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(page);
		});

		await test.step('The shortcut opens the site document', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.goToViewFileEntry(documentTitle);

			const latestVersionURL = page.getByRole('textbox', {
				name: 'Latest Version URL',
			});

			await clickAndExpectToBeVisible({
				target: latestVersionURL,
				trigger: page.locator('[data-qa-id="infoButton"]'),
			});

			await expect(latestVersionURL).toHaveValue(
				new RegExp(`/documents/d${site.friendlyUrlPath}/`)
			);
		});
	}
);
