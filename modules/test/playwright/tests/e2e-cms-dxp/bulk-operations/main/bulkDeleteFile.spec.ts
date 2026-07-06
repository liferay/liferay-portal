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
import {performLoginViaApi} from '../../../../utils/performLogin';
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {RecycleBinPage} from '../../../site-cms-site-initializer/main/pages/RecycleBinPage';
import {addSpaceUserWithSession, execBulkAction} from './utils/bulkOperations';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
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
	'A Space Administrator bulk deletes files and they stop being accessible on the site',
	{tag: ['@LPD-95537', '@LPD-95537/TC-14.d', '@LPD-97251']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.fail();

		test.setTimeout(480000);

		const spaceName = `Space ${getRandomString()}`;
		const mappedFileTitle = `Image ${getRandomString()}`;
		const secondFileTitle = `Image ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {trashEnabled: true},
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const spaceAdministrator = await addSpaceUserWithSession(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Administrator'
		);

		const mappedEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageBase64,
					name: `${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: mappedFileTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		const secondEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageBase64,
					name: `${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: secondFileTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			mappedEntry.id,
			[
				{
					actionIds: ['DELETE', 'UPDATE', 'VIEW'],
					roleName: 'Asset Library Administrator',
				},
				{actionIds: ['VIEW'], roleName: 'Guest'},
				{actionIds: ['VIEW'], roleName: 'User'},
			]
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			secondEntry.id,
			[
				{
					actionIds: ['DELETE', 'UPDATE', 'VIEW'],
					roleName: 'Asset Library Administrator',
				},
			]
		);

		const downloadHref = mappedEntry.file.link.href;

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><lfr-editable id="image" type="image"><img alt="" src=""/></lfr-editable></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map one file into a page fragment and publish', async () => {
			await expect(async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: ENTITY,
						entry: mappedFileTitle,
						field: 'Title',
					},
				});
			}).toPass({timeout: 30000});

			await pageEditorPage.selectEditable(fragmentId, 'image');

			await page.getByLabel('Source Selection').selectOption('Mapping');

			await pageEditorPage.setMappedItem({
				entity: ENTITY,
				entry: mappedFileTitle,
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
			await test.step('GUEST sees the mapped file before the bulk delete', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(mappedFileTitle, {exact: true})
					).toBeVisible({timeout: 2000});

					expect(
						await isImageLoaded(
							guestPage.locator('img[src*="/documents/"]').first()
						)
					).toBe(true);
				}).toPass({timeout: 30000});
			});

			const spaContext = await browser.newContext();

			const spaPage = await spaContext.newPage();

			try {
				await performLoginViaApi({
					page: spaPage,
					screenName: spaceAdministrator.alternateName,
				});

				const spaAssetsPage = new AssetsPage(spaPage);

				await test.step('The Space Administrator bulk deletes both files', async () => {
					await spaAssetsPage.gotoFiles();

					await spaAssetsPage.selectItems([
						mappedFileTitle,
						secondFileTitle,
					]);

					await execBulkAction(spaPage, 'Delete');
				});

				await test.step('The files are gone from the file list', async () => {
					await expect(async () => {
						await spaAssetsPage.gotoFiles();

						for (const title of [
							mappedFileTitle,
							secondFileTitle,
						]) {
							await expect(
								spaPage.getByRole('button', {
									name: `${title} Actions`,
								})
							).toBeHidden({timeout: 2000});
						}
					}).toPass({timeout: 30000});
				});
			}
			finally {
				await spaContext.close();
			}

			await test.step('The file is no longer accessible on the site for GUEST', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(mappedFileTitle, {exact: true})
					).toBeHidden({timeout: 2000});

					await expect(
						guestPage.locator('img[src*="/documents/"]')
					).toHaveCount(0, {timeout: 2000});
				}).toPass({timeout: 15000});

				const downloadStatus = await guestPage.evaluate(
					async (href) =>
						(await fetch(href, {redirect: 'manual'})).status,
					downloadHref
				);

				expect(downloadStatus).not.toBe(200);
			});
		}
		finally {
			await guestContext.close();
		}

		await test.step('Both files are in the Recycle Bin', async () => {
			const recycleBinPage = new RecycleBinPage(page);

			await expect(async () => {
				await recycleBinPage.goto();

				for (const title of [mappedFileTitle, secondFileTitle]) {
					await expect(
						page.getByRole('cell', {exact: true, name: title})
					).toBeVisible({timeout: 2000});
				}
			}).toPass({timeout: 30000});
		});
	}
);
