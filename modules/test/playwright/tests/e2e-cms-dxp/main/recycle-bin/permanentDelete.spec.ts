/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import {applyRecycleBinFilter} from './utils/recycleBin';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest(),
	structureBuilderPagesTest
);

const CONTENT_APPLICATION_NAME = 'cms/basic-web-contents';

const FILE_APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'A CMS Administrator permanently deletes selected items and they can no longer be restored',
	{tag: ['@LPD-95539', '@LPD-95539/TC-16.d']},
	async ({apiHelpers, page, recycleBinPage}) => {
		test.setTimeout(180000);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitle1 = `Content ${getRandomString()}`;
		const contentTitle2 = `Content ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		for (const title of [contentTitle1, contentTitle2]) {
			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{
					content: `<p>${getRandomString()}</p>`,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title,
				},
				CONTENT_APPLICATION_NAME,
				spaceName
			);

			await apiHelpers.objectEntry.deleteObjectEntry(
				CONTENT_APPLICATION_NAME,
				String(entry.id)
			);
		}

		await test.step('Select the trashed items and permanently delete them', async () => {
			await recycleBinPage.goto();

			await expect(
				page.locator('tbody tr', {hasText: contentTitle1})
			).toBeVisible({timeout: 10000});

			await recycleBinPage.selectItems([contentTitle1, contentTitle2]);

			await recycleBinPage.tableActions.click();

			await page
				.getByRole('menuitem', {exact: true, name: 'Delete'})
				.click();

			await expect(
				page.getByText('You are about to permanently delete 2 items.')
			).toBeVisible({timeout: 10000});

			await recycleBinPage.modalDeleteEntriesButton.click();

			await expect(
				page.locator('.alert', {hasText: 'successfully deleted'})
			).toBeVisible({timeout: 15000});
		});

		await test.step('The items are no longer listed and cannot be restored', async () => {
			await expect(async () => {
				await recycleBinPage.goto();

				await expect(
					page.locator('tbody tr', {hasText: contentTitle1})
				).toHaveCount(0, {timeout: 2000});

				await expect(
					page.locator('tbody tr', {hasText: contentTitle2})
				).toHaveCount(0, {timeout: 2000});
			}).toPass({timeout: 30000});
		});
	}
);

test(
	'The Recycle Bin can be filtered by content type',
	{tag: ['@LPD-95539', '@LPD-95539/TC-16.d', '@LPD-97359']},
	async ({apiHelpers, page, recycleBinPage}) => {
		test.setTimeout(180000);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const contentEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${getRandomString()}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			CONTENT_APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.deleteObjectEntry(
			CONTENT_APPLICATION_NAME,
			String(contentEntry.id)
		);

		const fileEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageBase64,
					name: `${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			FILE_APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.deleteObjectEntry(
			FILE_APPLICATION_NAME,
			String(fileEntry.id)
		);

		await recycleBinPage.goto();

		await applyRecycleBinFilter(page, 'Type', 'Basic Web Content');

		await expect(
			page.locator('tbody tr', {hasText: contentTitle})
		).toBeVisible({timeout: 10000});

		await expect(page.locator('tbody tr', {hasText: fileTitle})).toBeHidden(
			{timeout: 5000}
		);
	}
);

test(
	'Permanently deleting a file linked from a content entry warns that it is in use',
	{tag: ['@LPD-100265']},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		recycleBinPage,
		structureBuilderPage,
	}) => {
		test.setTimeout(180000);

		const contentTitle = `Content ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Structure${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageBase64,
					name: `${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			FILE_APPLICATION_NAME,
			spaceName
		);

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			publish: false,
			spaces: [spaceName],
		});

		await structureBuilderPage.addField('Select Related Content');

		await structureBuilderPage.selectFields([
			{label: 'Select Related Content'},
		]);

		await structureBuilderPage.changeFieldSettings({
			label: 'Attachment',
			relatedContent: 'Basic Document',
		});

		await structureBuilderPage.publishStructure();

		await test.step('Link the file from a content entry', async () => {
			await assetsPage.gotoContents(spaceName);

			await contentsPage.createContent(structureLabel, spaceName);

			await page
				.getByRole('textbox', {exact: true, name: 'Title'})
				.fill(contentTitle);

			const attachmentCombobox = page.getByRole('combobox', {
				name: 'Attachment',
			});

			await attachmentCombobox.click();

			await page.getByRole('option', {name: fileTitle}).click();

			await expect(attachmentCombobox).toHaveValue(fileTitle);

			await contentsPage.publishButton.click();

			await expect(page.getByText(contentTitle).first()).toBeVisible();
		});

		await test.step('Deleting the file from Files warns that it is in use', async () => {
			const recycleBinUsageWarning = page.getByText(
				'This item is being used in other assets or pages. Sending it to the Recycle Bin will break those references'
			);

			await assetsPage.gotoFiles();

			await assetsPage.changeVisualizationMode('Table');

			await assetsPage.execItemAction({
				action: 'Delete',
				filter: fileTitle,
			});

			await expect(recycleBinUsageWarning).toBeVisible();

			await recycleBinPage.modalDeleteEntriesButton.click();

			await expect(recycleBinUsageWarning).toBeHidden();
		});

		const permanentDeletionUsageWarning = page.getByText(
			'This item is being used in other assets or pages. Deleting it will break those references'
		);

		await test.step('Deleting the file from its item actions warns that it is in use', async () => {
			await recycleBinPage.goto();

			await expect(
				page.locator('tbody tr', {hasText: fileTitle})
			).toBeVisible({timeout: 10000});

			await recycleBinPage.execItemAction({
				action: 'Delete',
				filter: fileTitle,
			});

			await expect(permanentDeletionUsageWarning).toBeVisible();

			await recycleBinPage.modalCancelButton.click();

			await expect(permanentDeletionUsageWarning).toBeHidden();
		});

		await test.step('Deleting the file in bulk warns that it is in use', async () => {
			await recycleBinPage.selectItems([fileTitle]);

			await recycleBinPage.tableActions.click();

			await page
				.getByRole('menuitem', {exact: true, name: 'Delete'})
				.click();

			await expect(permanentDeletionUsageWarning).toBeVisible();
		});
	}
);
