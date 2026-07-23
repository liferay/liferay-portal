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
import {
	addSpaceUserWithSession,
	clickItemAction,
	openFolder,
} from './utils/folders';

const test = mergeTests(dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'A Space Administrator moves a CMS file to a different folder',
	{tag: ['@LPD-95536', '@LPD-95536/TC-13.d']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(360000);

		const spaceName = `Space ${getRandomString()}`;
		const originFolderName = `Origin ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
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

		const originFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: spaceName,
				title: originFolderName,
			});

		const destinationFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: spaceName,
				title: destinationFolderName,
			});

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageBase64,
					name: `${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode:
					originFolder.externalReferenceCode,
				title: fileTitle,
			},
			APPLICATION_NAME,
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

			await test.step('The Space Administrator moves the file to the destination folder', async () => {
				await spaAssetsPage.gotoFiles();

				await openFolder(spaPage, originFolderName);

				await clickItemAction(spaPage, fileTitle, 'Move');

				await spaAssetsPage.selectCopyOrMoveDestination({
					destinationFolder: destinationFolderName,
					destinationSpace: spaceName,
				});
			});

			await test.step('The file sits in the destination folder and is gone from the origin', async () => {
				await expect(async () => {
					const movedEntry =
						await apiHelpers.objectEntry.getObjectEntryById(
							APPLICATION_NAME,
							String(entry.id)
						);

					expect(movedEntry.objectEntryFolderId).toBe(
						destinationFolder.id
					);
				}).toPass({timeout: 15000});

				await spaAssetsPage.gotoFiles();

				await openFolder(spaPage, originFolderName);

				await expect(
					spaPage.getByRole('link', {exact: true, name: fileTitle})
				).toBeHidden({timeout: 5000});

				await spaAssetsPage.gotoFiles();

				await openFolder(spaPage, destinationFolderName);

				await expect(
					spaPage.getByRole('link', {exact: true, name: fileTitle})
				).toBeVisible({timeout: 5000});
			});

			await test.step('The file is still accessible after the move', async () => {
				const movedEntry =
					await apiHelpers.objectEntry.getObjectEntryById(
						APPLICATION_NAME,
						String(entry.id)
					);

				const downloadStatus = await spaPage.evaluate(
					async (href) => (await fetch(href)).status,
					movedEntry.file.link.href
				);

				expect(downloadStatus).toBe(200);
			});
		}
		finally {
			await spaContext.close();
		}
	}
);
