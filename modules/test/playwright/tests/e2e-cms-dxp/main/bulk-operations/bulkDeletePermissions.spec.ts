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
import {addSpaceUserWithSession} from './utils/bulkOperations';

const test = mergeTests(dataApiHelpersTest, loginTest());

const CONTENT_APPLICATION_NAME = 'cms/basic-web-contents';

const FILE_APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'A Space Member cannot bulk delete content entries or files',
	{tag: ['@LPD-95537', '@LPD-95537/TC-14.g']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(360000);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitles = [
			`Title ${getRandomString()}`,
			`Title ${getRandomString()}`,
		];
		const fileTitles = [
			`Image ${getRandomString()}`,
			`Image ${getRandomString()}`,
		];

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const spaceMember = await addSpaceUserWithSession(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Member'
		);

		for (const title of contentTitles) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					content: `<p>${getRandomString()}</p>`,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title,
				},
				CONTENT_APPLICATION_NAME,
				spaceName
			);
		}

		for (const title of fileTitles) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: imageBase64,
						name: `${getRandomString()}.jpg`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title,
				},
				FILE_APPLICATION_NAME,
				spaceName
			);
		}

		const memberContext = await browser.newContext();

		const memberPage = await memberContext.newPage();

		try {
			await performLoginViaApi({
				page: memberPage,
				screenName: spaceMember.alternateName,
			});

			const memberAssetsPage = new AssetsPage(memberPage);

			const expectBulkDeleteUnavailable = async (firstTitle: string) => {
				await expect(
					memberPage.getByRole('checkbox', {
						name: `Select ${firstTitle}`,
					})
				).toBeChecked({timeout: 5000});

				const menu = memberPage.locator('.dropdown-menu.show');

				await expect(async () => {
					await memberPage
						.locator('.management-bar-wrapper')
						.getByLabel('Actions')
						.click({timeout: 2000});

					await expect(menu).toBeVisible({timeout: 2000});
				}).toPass({timeout: 15000});

				await expect(
					menu.getByRole('menuitem', {exact: true, name: 'Delete'})
				).toBeHidden({timeout: 2000});

				await memberPage.keyboard.press('Escape');
			};

			await test.step('Bulk delete is not available in the Contents section', async () => {
				await memberAssetsPage.gotoContents();

				await memberAssetsPage.selectItems(contentTitles);

				await expectBulkDeleteUnavailable(contentTitles[0]);
			});

			await test.step('Bulk delete is not available in the Files section', async () => {
				await memberAssetsPage.gotoFiles();

				await memberAssetsPage.selectItems(fileTitles);

				await expectBulkDeleteUnavailable(fileTitles[0]);
			});
		}
		finally {
			await memberContext.close();
		}
	}
);
