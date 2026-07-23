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
import {performLoginViaApi} from '../../../../utils/performLogin';
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {ContentsPage} from '../../../site-cms-site-initializer/main/pages/ContentsPage';
import {RecycleBinPage} from '../../../site-cms-site-initializer/main/pages/RecycleBinPage';
import {addSpaceUserWithSession, deletePopulatedFolder} from './utils/folders';

const test = mergeTests(dataApiHelpersTest, loginTest());

const CONTENT_APPLICATION_NAME = 'cms/basic-web-contents';

const FILE_APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'Deleting populated folders sends the contained items to the Recycle Bin',
	{tag: ['@LPD-95536', '@LPD-95536/TC-13.f']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(360000);

		const spaceName = `Space ${getRandomString()}`;
		const contentFolderName = `Contents ${getRandomString()}`;
		const fileFolderName = `Files ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;
		const fileTitle = `Image ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const spaceAdministrator = await addSpaceUserWithSession(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Administrator'
		);

		const contentFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: contentFolderName,
			});

		const fileFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: spaceName,
				title: fileFolderName,
			});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${getRandomString()}</p>`,
				objectEntryFolderExternalReferenceCode:
					contentFolder.externalReferenceCode,
				title: contentTitle,
			},
			CONTENT_APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageBase64,
					name: `${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode:
					fileFolder.externalReferenceCode,
				title: fileTitle,
			},
			FILE_APPLICATION_NAME,
			spaceName
		);

		const spaContext = await browser.newContext();

		const spaPage = await spaContext.newPage();

		try {
			await performLoginViaApi({
				page: spaPage,
				screenName: spaceAdministrator.alternateName,
			});

			const spaAssetsPage = new AssetsPage(spaPage);
			const spaContentsPage = new ContentsPage(spaPage);
			const spaRecycleBinPage = new RecycleBinPage(spaPage);

			await test.step('The Space Administrator deletes the populated folders', async () => {
				await spaContentsPage.goto();

				await deletePopulatedFolder(spaPage, contentFolderName);

				await spaAssetsPage.gotoFiles();

				await deletePopulatedFolder(spaPage, fileFolderName);
			});

			await test.step('The folders are gone from their sections', async () => {
				await spaContentsPage.goto();

				await expect(
					spaPage.getByRole('link', {
						exact: true,
						name: contentFolderName,
					})
				).toBeHidden({timeout: 5000});

				await spaAssetsPage.gotoFiles();

				await expect(
					spaPage.getByRole('link', {
						exact: true,
						name: fileFolderName,
					})
				).toBeHidden({timeout: 5000});
			});

			await test.step('The trashed folders are in the Recycle Bin with their items preserved inside', async () => {
				for (const [folderName, itemTitle] of [
					[contentFolderName, contentTitle],
					[fileFolderName, fileTitle],
				]) {
					await spaRecycleBinPage.goto();

					await spaPage
						.getByRole('link', {exact: true, name: folderName})
						.click();

					await spaPage.waitForURL('**/recycle-bin/**');

					await expect(
						spaPage.getByText(itemTitle, {exact: true})
					).toBeVisible({timeout: 10000});
				}
			});
		}
		finally {
			await spaContext.close();
		}
	}
);
