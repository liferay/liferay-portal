/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {addMappingFragment} from '../../../../utils/addMappingFragment';
import getRandomString from '../../../../utils/getRandomString';
import {isImageLoaded} from '../../../../utils/isImageLoaded';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {deleteEntryToRecycleBin, restoreEntry} from './utils/recycleBin';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const APPLICATION_NAME = 'cms/basic-documents';

const ENTITY = 'Basic Documents (CMS)';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'A CMS Administrator restores a deleted file from the Recycle Bin and it renders again for GUEST',
	{tag: ['@LPD-95539', '@LPD-95539/TC-16.c']},
	async ({
		apiHelpers,
		assetsPage,
		browser,
		page,
		pageEditorPage,
		recycleBinPage,
		site,
	}) => {
		test.setTimeout(300000);

		const spaceName = `Space ${getRandomString()}`;
		const title = `Image ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageBase64,
					name: `${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title,
			},
			APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[
				{actionIds: ['VIEW'], roleName: 'Guest'},
				{actionIds: ['VIEW'], roleName: 'User'},
			]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><lfr-editable id="image" type="image"><img alt="" src=""/></lfr-editable></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the file Title and Preview URL into a page fragment and publish', async () => {
			await expect(async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {entity: ENTITY, entry: title, field: 'Title'},
				});
			}).toPass({timeout: 30000});

			await pageEditorPage.selectEditable(fragmentId, 'image');

			await page.getByLabel('Source Selection').selectOption('Mapping');

			await pageEditorPage.setMappedItem({
				entity: ENTITY,
				entry: title,
				field: 'Preview URL',
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		const guestContext = await browser.newContext({
			storageState: {cookies: [], origins: []},
		});

		const guestPage = await guestContext.newPage();

		try {
			await test.step('GUEST sees the published file image', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					expect(
						await isImageLoaded(
							guestPage.locator('img[src*="/documents/"]').first()
						)
					).toBe(true);
				}).toPass({timeout: 30000});
			});

			await test.step('The CMS Administrator deletes the file into the Recycle Bin', async () => {
				await assetsPage.gotoFiles();

				await assetsPage.changeVisualizationMode('Table');

				await deleteEntryToRecycleBin(page, title);
			});

			await test.step('The CMS Administrator restores the file from the Recycle Bin', async () => {
				await recycleBinPage.goto();

				await restoreEntry(recycleBinPage, title);
			});

			await test.step('GUEST sees the restored file image again on the DXP page', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					expect(
						await isImageLoaded(
							guestPage.locator('img[src*="/documents/"]').first()
						)
					).toBe(true);
				}).toPass({timeout: 30000});
			});
		}
		finally {
			await guestContext.close();
		}
	}
);
