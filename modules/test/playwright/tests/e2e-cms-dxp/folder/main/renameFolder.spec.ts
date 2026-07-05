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
import {ContentsPage} from '../../../site-cms-site-initializer/main/pages/ContentsPage';
import {
	addSpaceUserWithSession,
	clickItemAction,
	openFolder,
} from './utils/folders';

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

const CONTENT_APPLICATION_NAME = 'cms/basic-web-contents';

const FILE_APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'Renaming a populated folder keeps its items accessible and mapped page references intact',
	{tag: ['@LPD-95536', '@LPD-95536/TC-13.e']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(480000);

		const spaceName = `Space ${getRandomString()}`;
		const contentFolderName = `Contents ${getRandomString()}`;
		const fileFolderName = `Files ${getRandomString()}`;
		const renamedContentFolderName = `Renamed ${getRandomString()}`;
		const renamedFileFolderName = `Renamed ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;
		const fileTitle = `Image ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
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

		const contentFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: contentFolderName,
			});

		const fileFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: spaceName,
				title: fileFolderName,
			});

		const contentEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${bodyValue}</p>`,
				objectEntryFolderExternalReferenceCode:
					contentFolder.externalReferenceCode,
				title: contentTitle,
			},
			CONTENT_APPLICATION_NAME,
			spaceName
		);

		const fileEntry = await apiHelpers.objectEntry.postObjectEntry(
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

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			CONTENT_APPLICATION_NAME,
			contentEntry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			FILE_APPLICATION_NAME,
			fileEntry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><lfr-editable id="image" type="image"><img alt="" src=""/></lfr-editable></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the folder items into a page fragment and publish', async () => {
			await expect(async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: 'Basic Web Contents (CMS)',
						entry: contentTitle,
						field: 'Title',
					},
				});
			}).toPass({timeout: 30000});

			await pageEditorPage.selectEditable(fragmentId, 'image');

			await page.getByLabel('Source Selection').selectOption('Mapping');

			await pageEditorPage.setMappedItem({
				entity: 'Basic Documents (CMS)',
				entry: fileTitle,
				field: 'Preview URL',
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		const spaContext = await browser.newContext();

		const spaPage = await spaContext.newPage();

		try {
			await performLoginViaApi({
				page: spaPage,
				screenName: spaceAdministrator.alternateName,
			});

			const spaAssetsPage = new AssetsPage(spaPage);
			const spaContentsPage = new ContentsPage(spaPage);

			await test.step('The Space Administrator renames the populated folders', async () => {
				await spaContentsPage.goto();

				await clickItemAction(spaPage, contentFolderName, 'Edit');

				await spaPage
					.getByLabel('NameRequired')
					.fill(renamedContentFolderName);

				await spaPage.getByRole('button', {name: 'Save'}).click();

				await spaPage.waitForURL('**/web/cms/contents**');

				await spaAssetsPage.gotoFiles();

				await clickItemAction(spaPage, fileFolderName, 'Edit');

				await spaPage
					.getByLabel('NameRequired')
					.fill(renamedFileFolderName);

				await spaPage.getByRole('button', {name: 'Save'}).click();

				await spaPage.waitForURL('**/web/cms/files**');
			});

			await test.step('The items remain accessible inside the renamed folders', async () => {
				await spaContentsPage.goto();

				await openFolder(spaPage, renamedContentFolderName);

				await expect(
					spaPage.getByRole('link', {exact: true, name: contentTitle})
				).toBeVisible({timeout: 5000});

				await spaAssetsPage.gotoFiles();

				await openFolder(spaPage, renamedFileFolderName);

				await expect(
					spaPage.getByRole('button', {
						name: `${fileTitle} Actions`,
					})
				).toBeVisible({timeout: 10000});
			});
		}
		finally {
			await spaContext.close();
		}

		await test.step('The published page still renders the mapped items', async () => {
			const guestContext = await browser.newContext({
				storageState: {cookies: [], origins: []},
			});

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(contentTitle, {exact: true})
					).toBeVisible({timeout: 2000});

					expect(
						await isImageLoaded(
							guestPage.locator('img[src*="/documents/"]').first()
						)
					).toBe(true);
				}).toPass({timeout: 30000});
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
