/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {
	addSpaceUserWithSession,
	bulkMoveToFolder,
	openFolder,
} from './utils/bulkOperations';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

const APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'A Space Administrator bulk moves multiple files to another folder',
	{tag: ['@LPD-95537', '@LPD-95537/TC-14.b']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(360000);

		const spaceName = `Space ${getRandomString()}`;
		const originFolderName = `Origin ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;

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

		const entries = [];

		for (let i = 0; i < 3; i++) {
			entries.push(
				await apiHelpers.objectEntry.postObjectEntry(
					{
						file: {
							fileBase64: imageBase64,
							name: `${getRandomString()}.jpg`,
						},
						objectEntryFolderExternalReferenceCode:
							originFolder.externalReferenceCode,
						title: `Image ${getRandomString()}`,
					},
					APPLICATION_NAME,
					spaceName
				)
			);
		}

		const fileTitles = entries.map((entry) => entry.title);

		const spaContext = await browser.newContext();

		const spaPage = await spaContext.newPage();

		try {
			await performLoginViaApi({
				page: spaPage,
				screenName: spaceAdministrator.alternateName,
			});

			const spaAssetsPage = new AssetsPage(spaPage);

			await test.step('The Space Administrator bulk moves the three files', async () => {
				await spaAssetsPage.gotoFiles();

				await openFolder(spaPage, originFolderName);

				await spaAssetsPage.selectItems(fileTitles);

				await bulkMoveToFolder(spaPage, {
					destinationFolder: destinationFolderName,
					destinationSpace: spaceName,
				});
			});

			await test.step('All files sit in the destination folder', async () => {
				for (const entry of entries) {
					await expect(async () => {
						const movedEntry =
							await apiHelpers.objectEntry.getObjectEntryById(
								APPLICATION_NAME,
								String(entry.id)
							);

						expect(movedEntry.objectEntryFolderId).toBe(
							destinationFolder.id
						);
					}).toPass({timeout: 30000});
				}
			});

			await test.step('The files are gone from the origin folder and listed in the destination', async () => {
				await expect(async () => {
					await spaAssetsPage.gotoFiles();

					await openFolder(spaPage, originFolderName);

					for (const title of fileTitles) {
						await expect(
							spaPage.getByRole('button', {
								name: `${title} Actions`,
							})
						).toBeHidden({timeout: 2000});
					}

					await spaAssetsPage.gotoFiles();

					await openFolder(spaPage, destinationFolderName);

					for (const title of fileTitles) {
						await expect(
							spaPage.getByRole('button', {
								name: `${title} Actions`,
							})
						).toBeVisible({timeout: 2000});
					}
				}).toPass({timeout: 60000});
			});
		}
		finally {
			await spaContext.close();
		}
	}
);
