/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {ContentsPage} from '../../../site-cms-site-initializer/main/pages/ContentsPage';
import {
	addSpaceUserWithSession,
	openFolder,
	searchContentList,
} from './utils/folders';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'A Space Administrator builds a nested content folder hierarchy and a Content Reviewer creates content inside it',
	{tag: ['@LPD-95536', '@LPD-95536/TC-13.a']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(360000);

		const spaceName = `Space ${getRandomString()}`;
		const parentFolderName = `Parent ${getRandomString()}`;
		const childFolderNameA = `Child A ${getRandomString()}`;
		const childFolderNameB = `Child B ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;

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

		await test.step('The Space Administrator creates a nested folder hierarchy in Contents', async () => {
			const spaContext = await browser.newContext();

			const spaPage = await spaContext.newPage();

			try {
				await performLoginViaApi({
					page: spaPage,
					screenName: spaceAdministrator.alternateName,
				});

				const spaContentsPage = new ContentsPage(spaPage);

				await spaContentsPage.goto();

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

			const scrContentsPage = new ContentsPage(scrPage);

			await test.step('The Content Reviewer creates a Basic Web Content inside a child folder', async () => {
				await scrContentsPage.goto();

				await openFolder(scrPage, parentFolderName);

				await openFolder(scrPage, childFolderNameA);

				await scrContentsPage.createContent('Basic Web Content');

				await scrContentsPage.fillData([
					{label: 'Title', value: contentTitle},
				]);

				await scrContentsPage.saveContent();
			});

			await test.step('The folder structure is preserved and the content sits in the correct folder', async () => {
				await scrContentsPage.goto();

				await openFolder(scrPage, parentFolderName);

				await expect(
					scrPage.getByRole('link', {
						exact: true,
						name: childFolderNameB,
					})
				).toBeVisible({timeout: 5000});

				await openFolder(scrPage, childFolderNameA);

				await expect(
					scrPage.getByRole('link', {exact: true, name: contentTitle})
				).toBeVisible({timeout: 5000});
			});

			await test.step('The content is accessible from the content list', async () => {
				await scrContentsPage.goto();

				await searchContentList(scrPage, contentTitle);
			});
		}
		finally {
			await scrContext.close();
		}
	}
);
