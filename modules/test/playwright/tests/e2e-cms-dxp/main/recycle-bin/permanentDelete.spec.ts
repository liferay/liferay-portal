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
import {applyRecycleBinFilter} from './utils/recycleBin';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

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

		test.fail(
			true,
			'LPD-97359: the Recycle Bin type filter returns no results for trashed content, so filtering by "Basic Web Content" hides items that are of that type.'
		);

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
