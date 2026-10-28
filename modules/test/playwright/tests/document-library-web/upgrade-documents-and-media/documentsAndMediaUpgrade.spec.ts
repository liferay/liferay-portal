/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';
import {DocumentLibraryEditFilePage} from '../../../pages/document-library-web/DocumentLibraryEditFilePage';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {RecycleBinPage} from '../../../pages/trash-web/RecycleBinPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';
import {reindexAllSearchIndexes} from '../../portal-db-infrastructure/utils/reindexAllSearchIndexes';

const test = mergeTests(loginTest(), searchAdminPageTest);

const LONG_TITLE = Array(4).fill('DM Document DOC With A Long Title').join(' ');

const DOCUMENTS = [
	{fileName: 'DM Document Type Title.doc', title: 'DM Document Type Title'},
	{fileName: 'DM Document Video.mp4', title: 'DM Document Video'},
	{fileName: 'DM Document JPG.jpg', title: 'DM Document JPG'},
	{fileName: `${LONG_TITLE}.doc`, title: LONG_TITLE},
];

test.describe.serial('Documents and Media upgrade', () => {
	test(
		'Can view the upgraded documents',
		{tag: '@LPD-112520'},
		async ({page, searchAdminPage}) => {
			await reindexAllSearchIndexes({searchAdminPage});

			const documentLibraryPage = new DocumentLibraryPage(page);

			for (const {fileName, title} of DOCUMENTS) {
				await test.step(`View ${title}`, async () => {
					await page.goto('/web/guest/documents');

					await page
						.getByRole('link', {exact: true, name: title})
						.click();

					await expect(
						page.getByRole('heading', {exact: true, name: title})
					).toBeVisible();

					await documentLibraryPage.clickFileEntryAction('Edit');

					await expect(
						page.getByRole('textbox', {name: 'Title Required'})
					).toHaveValue(title);

					await expect(
						page.getByRole('textbox', {name: 'File Name Required'})
					).toHaveValue(fileName);
				});
			}

			await test.step('View the document type values', async () => {
				await page.goto('/web/guest/documents');

				await page
					.getByRole('link', {
						exact: true,
						name: 'DM Document Type Title',
					})
					.click();

				await documentLibraryPage.clickFileEntryAction('Edit');

				await expect(
					page
						.getByRole('combobox', {name: /^Document Type/})
						.locator('option:checked')
				).toHaveText('DM Document Type Name');

				await expect(
					page.getByRole('textbox', {exact: true, name: 'Text'})
				).toHaveValue('test');

				await expect(
					page.getByRole('textbox', {exact: true, name: 'File'})
				).toHaveValue('DM Document JPG');

				await expect(
					page.getByRole('checkbox', {name: 'Boolean'})
				).toBeChecked();

				await expect(
					page.getByRole('textbox', {exact: true, name: 'Number'})
				).toHaveValue('1234');
			});

			const siteId = await page.evaluate(() =>
				Liferay.ThemeDisplay.getScopeGroupId()
			);

			await test.step('Download the video', async () => {
				const response = await page.request.get(
					`/documents/${siteId}/0/DM Document Video.mp4`
				);

				expect(response.status()).toBe(200);

				expect((await response.body()).length).toBeGreaterThan(0);
			});

			await test.step('Preview the image', async () => {
				await page.goto(`/documents/${siteId}/0/DM Document JPG`);

				await expect(
					page.locator(`img[src*="/documents/${siteId}/0/DM"]`)
				).toBeVisible();
			});
		}
	);

	test(
		'Can view the upgraded document type and metadata set',
		{tag: '@LPD-112520'},
		async ({page}) => {
			const documentLibraryPage = new DocumentLibraryPage(page);

			await documentLibraryPage.goto();

			await documentLibraryPage.changeTab('Document Types');

			await page
				.getByRole('link', {exact: true, name: 'DM Document Type Name'})
				.click();

			await expect(
				page.getByRole('application', {name: 'Edit Text'})
			).toBeVisible();

			await expect(
				page.getByRole('application', {name: 'Edit Upload'})
			).toBeVisible();

			await page
				.getByRole('tab', {name: 'panelAdditional Metadata Fields'})
				.click();

			await expect(
				page.getByText('Document Metadata Set Name', {exact: true})
			).toBeVisible();

			await documentLibraryPage.goto();

			await documentLibraryPage.changeTab('Metadata Sets');

			await page
				.getByRole('link', {
					exact: true,
					name: 'Document Metadata Set Name',
				})
				.click();

			await expect(
				page.getByRole('application', {name: 'Edit Boolean'})
			).toBeVisible();

			await expect(
				page.getByRole('application', {name: 'Edit Numeric'})
			).toBeVisible();
		}
	);

	test(
		'Can add a document with the upgraded document type',
		{tag: '@LPD-112520'},
		async ({page}) => {
			const documentLibraryEditFilePage = new DocumentLibraryEditFilePage(
				page
			);

			await documentLibraryEditFilePage.goToNewFileDifferentType(
				'DM Document Type Name'
			);

			await page
				.locator('input[type=file]')
				.first()
				.setInputFiles(
					path.join(
						__dirname,
						'../../message-boards-web/main/dependencies/Document_1.doc'
					)
				);

			await page
				.getByRole('textbox', {name: 'Title Required'})
				.fill('DM Document Type Title After Upgrade');

			await page
				.getByRole('textbox', {exact: true, name: 'Number'})
				.fill('1234');

			await page
				.getByRole('button', {exact: true, name: 'Documents and Media'})
				.click();

			const itemSelectorFrame = page.frameLocator(
				'iframe[title*="Select"]'
			);

			await itemSelectorFrame
				.getByText('DM Document JPG', {exact: true})
				.click();

			await expect(
				page.getByRole('textbox', {exact: true, name: 'File'})
			).toHaveValue('DM Document JPG');

			await documentLibraryEditFilePage.publishFileEntry();

			const documentLibraryPage = new DocumentLibraryPage(page);

			await documentLibraryPage.goto();

			await documentLibraryPage.goToViewFileEntry(
				'DM Document Type Title After Upgrade'
			);
		}
	);

	test(
		'Can edit the upgraded document type and metadata set',
		{tag: '@LPD-112520'},
		async ({page}) => {
			const documentLibraryPage = new DocumentLibraryPage(page);

			const editStructure = async ({
				currentFieldLabel,
				fieldLabel,
				name,
				nameEdit,
				tab,
			}: {
				currentFieldLabel: string;
				fieldLabel: string;
				name: string;
				nameEdit: string;
				tab: string;
			}) => {
				await documentLibraryPage.goto();

				await documentLibraryPage.changeTab(tab);

				await page.getByRole('link', {exact: true, name}).click();

				await page
					.locator('.ddm-drag')
					.filter({hasText: currentFieldLabel})
					.click();

				await page
					.getByRole('textbox', {exact: true, name: 'Label'})
					.fill(fieldLabel);

				await page
					.getByRole('textbox', {name: 'Name Required'})
					.fill(nameEdit);

				await page
					.getByRole('button', {exact: true, name: 'Save'})
					.click();

				await documentLibraryPage.waitForSuccessAlert();
			};

			await editStructure({
				currentFieldLabel: 'Number',
				fieldLabel: 'Phone Number',
				name: 'Document Metadata Set Name',
				nameEdit: 'Document Metadata Set Name Edit',
				tab: 'Metadata Sets',
			});

			await editStructure({
				currentFieldLabel: 'Text',
				fieldLabel: 'Address',
				name: 'DM Document Type Name',
				nameEdit: 'DM Document Type Name Edit',
				tab: 'Document Types',
			});

			await documentLibraryPage.goto();

			await documentLibraryPage.goToViewFileEntry(
				'DM Document Type Title'
			);

			await documentLibraryPage.clickFileEntryAction('Edit');

			await expect(
				page.getByRole('textbox', {exact: true, name: 'Address'})
			).toHaveValue('test');

			await expect(
				page.getByRole('textbox', {exact: true, name: 'Phone Number'})
			).toHaveValue('1234');
		}
	);

	test(
		'Can edit and delete the upgraded documents',
		{tag: '@LPD-112520'},
		async ({page}) => {
			const documentLibraryPage = new DocumentLibraryPage(page);

			await documentLibraryPage.goto();

			await documentLibraryPage.goToViewFileEntry('DM Document Video');

			await documentLibraryPage.clickFileEntryAction('Edit');

			await page
				.getByRole('textbox', {name: 'Title Required'})
				.fill('DM Document Video Edit');

			await page
				.getByRole('textbox', {name: 'File Name Required'})
				.fill('DM Document Video Edit.mp4');

			await new DocumentLibraryEditFilePage(page).publishFileEntry();

			await documentLibraryPage.goto();

			await documentLibraryPage.goToViewFileEntry(
				'DM Document Video Edit'
			);

			await expect(
				page.locator('.sidebar-header .label-item').first()
			).toHaveText('Version 1.1');

			await documentLibraryPage.goto();

			await documentLibraryPage.moveToRecycleBin('DM Document JPG');

			await documentLibraryPage.goto();

			await page.getByLabel('Select All Items on the Page').check();

			await page
				.locator('.management-bar')
				.getByRole('button', {name: 'Delete'})
				.click();

			await waitForAlert(page, 'moved to the Recycle Bin');

			await documentLibraryPage.goto();

			await expect(
				page.getByText(
					'There are no documents or media files in this folder.'
				)
			).toBeVisible();
		}
	);

	test(
		'Can delete the upgraded document type and metadata set',
		{tag: '@LPD-112520'},
		async ({page}) => {
			await new RecycleBinPage(page).goto();

			page.once('dialog', (dialog) => dialog.accept());

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					name: 'Empty Trash',
				}),
				trigger: page.getByRole('button', {name: 'Options'}),
			});

			await waitForAlert(page);

			const documentLibraryPage = new DocumentLibraryPage(page);

			await documentLibraryPage.deleteDocumentType(
				'DM Document Type Name Edit'
			);

			await waitForAlert(page);

			await documentLibraryPage.goto();

			await documentLibraryPage.changeTab('Document Types');

			await expect(
				page.getByRole('link', {name: 'DM Document Type Name Edit'})
			).toBeHidden();

			await documentLibraryPage.changeTab('Metadata Sets');

			const metadataSetRow = page
				.getByRole('row')
				.filter({hasText: 'Document Metadata Set Name Edit'});

			page.once('dialog', (dialog) => dialog.accept());

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('link', {name: 'Delete'}),
				trigger: metadataSetRow.getByRole('button'),
			});

			await waitForAlert(page);

			await expect(metadataSetRow).toBeHidden();
		}
	);
});
