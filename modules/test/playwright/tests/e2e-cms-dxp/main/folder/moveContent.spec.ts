/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {ContentsPage} from '../../../site-cms-site-initializer/main/pages/ContentsPage';
import {addSpaceUserWithSession, openFolder} from './utils/folders';

const test = mergeTests(dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'A Space Administrator moves a Basic Web Content entry to a different folder',
	{tag: ['@LPD-95536', '@LPD-95536/TC-13.c']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(360000);

		const spaceName = `Space ${getRandomString()}`;
		const originFolderName = `Origin ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;

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
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: originFolderName,
			});

		const destinationFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: destinationFolderName,
			});

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${bodyValue}</p>`,
				objectEntryFolderExternalReferenceCode:
					originFolder.externalReferenceCode,
				title: contentTitle,
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
			const spaContentsPage = new ContentsPage(spaPage);

			await test.step('The Space Administrator moves the entry to the destination folder', async () => {
				await spaContentsPage.goto();

				await openFolder(spaPage, originFolderName);

				await spaAssetsPage.moveTo({
					destinationFolder: destinationFolderName,
					destinationSpace: spaceName,
					itemTitle: contentTitle,
				});
			});

			await test.step('The entry sits in the destination folder and is gone from the origin', async () => {
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

				await spaContentsPage.goto();

				await openFolder(spaPage, originFolderName);

				await expect(
					spaPage.getByRole('link', {exact: true, name: contentTitle})
				).toBeHidden({timeout: 5000});

				await spaContentsPage.goto();

				await openFolder(spaPage, destinationFolderName);

				await expect(
					spaPage.getByRole('link', {exact: true, name: contentTitle})
				).toBeVisible({timeout: 5000});
			});

			await test.step('The entry detail view is intact after the move', async () => {
				await spaPage
					.getByRole('link', {exact: true, name: contentTitle})
					.click();

				await expect(
					spaPage.getByText(bodyValue, {exact: true})
				).toBeVisible({timeout: 10000});
			});
		}
		finally {
			await spaContext.close();
		}
	}
);
