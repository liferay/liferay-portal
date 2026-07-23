/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {ContentsPage} from '../../../site-cms-site-initializer/main/pages/ContentsPage';
import {addSpaceUserWithSession, openFolder} from './utils/folders';

const test = mergeTests(dataApiHelpersTest, loginTest());

const IMAGE_FILE_NAME = 'sample_small_wide_400x300.jpg';

test(
	'A Space Administrator builds a nested file folder hierarchy and a Content Reviewer uploads a file inside it',
	{tag: ['@LPD-95536', '@LPD-95536/TC-13.b']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(360000);

		const spaceName = `Space ${getRandomString()}`;
		const parentFolderName = `Parent ${getRandomString()}`;
		const childFolderNameA = `Child A ${getRandomString()}`;
		const childFolderNameB = `Child B ${getRandomString()}`;
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

		const contentReviewer = await addSpaceUserWithSession(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Content Reviewer'
		);

		await test.step('The Space Administrator creates a nested folder hierarchy in Files', async () => {
			const spaContext = await browser.newContext();

			const spaPage = await spaContext.newPage();

			try {
				await performLoginViaApi({
					page: spaPage,
					screenName: spaceAdministrator.alternateName,
				});

				const spaAssetsPage = new AssetsPage(spaPage);
				const spaContentsPage = new ContentsPage(spaPage);

				await spaAssetsPage.gotoFiles();

				await spaContentsPage.createFolder(parentFolderName, spaceName);

				await openFolder(spaPage, parentFolderName);

				await spaContentsPage.createFolder(childFolderNameA);

				await spaContentsPage.createFolder(childFolderNameB);

				await expect(
					spaPage.getByRole('link', {
						exact: true,
						name: childFolderNameA,
					})
				).toBeVisible({timeout: 5000});

				await expect(
					spaPage.getByRole('link', {
						exact: true,
						name: childFolderNameB,
					})
				).toBeVisible({timeout: 5000});
			}
			finally {
				await spaContext.close();
			}
		});

		const scrContext = await browser.newContext();

		const scrPage = await scrContext.newPage();

		try {
			await performLoginViaApi({
				page: scrPage,
				screenName: contentReviewer.alternateName,
			});

			const scrAssetsPage = new AssetsPage(scrPage);
			const scrContentsPage = new ContentsPage(scrPage);

			await test.step('The Content Reviewer uploads a file inside a child folder', async () => {
				await scrAssetsPage.gotoFiles();

				await openFolder(scrPage, parentFolderName);

				await openFolder(scrPage, childFolderNameA);

				await scrContentsPage.createContent('Single File');

				await scrContentsPage.fillData([
					{label: 'Title', value: fileTitle},
				]);

				const fileChooserPromise = scrPage.waitForEvent('filechooser');

				await scrPage
					.getByRole('button', {exact: true, name: 'Select File'})
					.click();

				const fileChooser = await fileChooserPromise;

				await fileChooser.setFiles(
					path.join(
						__dirname,
						`../../dependencies/${IMAGE_FILE_NAME}`
					)
				);

				await expect(scrPage.getByText(IMAGE_FILE_NAME)).toBeVisible({
					timeout: 5000,
				});

				await scrContentsPage.saveContent();
			});

			await test.step('The folder structure is preserved and the file is accessible in the nested folder', async () => {
				await scrAssetsPage.gotoFiles();

				await openFolder(scrPage, parentFolderName);

				await expect(
					scrPage.getByRole('link', {
						exact: true,
						name: childFolderNameB,
					})
				).toBeVisible({timeout: 5000});

				await openFolder(scrPage, childFolderNameA);

				await expect(
					scrPage.getByRole('link', {exact: true, name: fileTitle})
				).toBeVisible({timeout: 5000});

				await scrPage
					.getByRole('link', {exact: true, name: fileTitle})
					.click();

				await expect(
					scrPage.getByRole('heading', {name: fileTitle})
				).toBeVisible({timeout: 10000});
			});
		}
		finally {
			await scrContext.close();
		}
	}
);
