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
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {openVersionHistory, restoreVersion} from './utils/versioning';

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
	'A CMS Administrator restores a previous file metadata version and it is reflected in the CMS and on the DXP page',
	{tag: ['@LPD-95538', '@LPD-95538/TC-15.c']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(300000);

		const spaceName = `Space ${getRandomString()}`;
		const titleV1 = `Image ${getRandomString()}`;
		const titleV3 = `Image ${getRandomString()}`;

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
				title: titleV1,
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
					mapping: {entity: ENTITY, entry: titleV1, field: 'Title'},
				});
			}).toPass({timeout: 30000});

			await pageEditorPage.selectEditable(fragmentId, 'image');

			await page.getByLabel('Source Selection').selectOption('Mapping');

			await pageEditorPage.setMappedItem({
				entity: ENTITY,
				entry: titleV1,
				field: 'Preview URL',
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('Update the file metadata twice to generate versions 2 and 3', async () => {
			await apiHelpers.objectEntry.patchObjectEntry(
				{title_i18n: {en_US: `Image ${getRandomString()}`}},
				APPLICATION_NAME,
				entry.id
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{title_i18n: {en_US: titleV3}},
				APPLICATION_NAME,
				entry.id
			);
		});

		const guestContext = await browser.newContext({
			storageState: {cookies: [], origins: []},
		});

		const guestPage = await guestContext.newPage();

		try {
			await test.step('GUEST sees the current metadata title and the image', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(titleV3, {exact: true})
					).toBeVisible({timeout: 2000});

					expect(
						await isImageLoaded(
							guestPage.locator('img[src*="/documents/"]').first()
						)
					).toBe(true);
				}).toPass({timeout: 30000});
			});

			const assetsPage = new AssetsPage(page);

			await test.step('The CMS Administrator restores version 1 of the metadata', async () => {
				await assetsPage.gotoFiles();

				await assetsPage.changeVisualizationMode('Table');

				await openVersionHistory(assetsPage, titleV3);

				await restoreVersion(page, titleV1);
			});

			await test.step('The CMS detail view reflects the restored title', async () => {
				await expect(async () => {
					await assetsPage.gotoFiles();

					await assetsPage.changeVisualizationMode('Table');

					await expect(
						page.getByText(titleV1, {exact: true}).first()
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			});

			await test.step('The DXP page shows the restored title and the image still renders', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(titleV1, {exact: true})
					).toBeVisible({timeout: 2000});

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
