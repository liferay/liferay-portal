/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {addSpaceUserWithSession, openFolder} from './utils/folders';

const test = mergeTests(dataApiHelpersTest, loginTest());

const CONTENT_APPLICATION_NAME = 'cms/basic-web-contents';

const FILE_APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'A Space Member can browse the folder hierarchy but cannot manage folders',
	{tag: ['@LPD-95536', '@LPD-95536/TC-13.g']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(360000);

		const spaceName = `Space ${getRandomString()}`;
		const parentFolderName = `Parent ${getRandomString()}`;
		const childFolderName = `Child ${getRandomString()}`;
		const fileFolderName = `Files ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;
		const fileTitle = `Image ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const spaceMember = await addSpaceUserWithSession(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Member'
		);

		const parentFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: parentFolderName,
			});

		const childFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode:
					parentFolder.externalReferenceCode,
				scopeKey: spaceName,
				title: childFolderName,
			});

		const fileFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: spaceName,
				title: fileFolderName,
			});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${bodyValue}</p>`,
				objectEntryFolderExternalReferenceCode:
					childFolder.externalReferenceCode,
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

		const memberContext = await browser.newContext();

		const memberPage = await memberContext.newPage();

		try {
			await performLoginViaApi({
				page: memberPage,
				screenName: spaceMember.alternateName,
			});

			const memberAssetsPage = new AssetsPage(memberPage);

			await test.step('The Space Member browses the content folder hierarchy and sees the content', async () => {
				await memberAssetsPage.gotoContents();

				await openFolder(memberPage, parentFolderName);

				await openFolder(memberPage, childFolderName);

				await expect(
					memberPage.getByText(contentTitle, {exact: true})
				).toBeVisible({timeout: 5000});
			});

			await test.step('The Space Member browses the file folder and sees the file', async () => {
				await memberAssetsPage.gotoFiles();

				await openFolder(memberPage, fileFolderName);

				await expect(
					memberPage.getByText(fileTitle, {exact: true})
				).toBeVisible({timeout: 5000});
			});

			await test.step('Folder creation is denied for the Space Member', async () => {
				for (const goToSection of [
					() => memberAssetsPage.gotoContents(),
					() => memberAssetsPage.gotoFiles(),
				]) {
					await goToSection();

					await expect(
						memberPage.getByTestId('fdsCreationActionButton')
					).toHaveCount(0, {timeout: 2000});
				}
			});

			await test.step('Folder rename, move and delete are denied for the Space Member', async () => {
				await memberAssetsPage.gotoContents();

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: memberPage.getByRole('menuitem', {
						name: 'View Folder',
					}),
					trigger: memberPage.getByRole('button', {
						name: `${parentFolderName} Actions`,
					}),
				});

				for (const deniedAction of ['Edit', 'Move', 'Delete']) {
					await expect(
						memberPage.getByRole('menuitem', {name: deniedAction})
					).toHaveCount(0, {timeout: 2000});
				}
			});
		}
		finally {
			await memberContext.close();
		}
	}
);
