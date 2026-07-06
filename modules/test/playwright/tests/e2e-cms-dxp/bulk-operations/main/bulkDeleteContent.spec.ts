/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {addMappingFragment} from '../../../../utils/addMappingFragment';
import getRandomString from '../../../../utils/getRandomString';
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

const APPLICATION_NAME = 'cms/basic-web-contents';

const ENTITY = 'Basic Web Contents (CMS)';

test(
	'A Content Reviewer bulk deletes Basic Web Content entries and they stop rendering for GUEST',
	{tag: ['@LPD-95537', '@LPD-95537/TC-14.c', '@LPD-97251']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.fail();

		test.setTimeout(480000);

		const spaceName = `Space ${getRandomString()}`;
		const mappedTitle = `Title ${getRandomString()}`;
		const mappedBody = `Body ${getRandomString()}`;
		const secondTitle = `Title ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {trashEnabled: true},
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const contentReviewer = await addSpaceUserWithSession(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Content Reviewer'
		);

		const mappedEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${mappedBody}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: mappedTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${getRandomString()}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: secondTitle,
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
					roleName: 'Asset Library Content Reviewer',
				},
				{actionIds: ['VIEW'], roleName: 'Guest'},
				{actionIds: ['VIEW'], roleName: 'User'},
			]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><div><lfr-editable id="content" type="rich-text">Content</lfr-editable></div></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map one entry into a page fragment and publish', async () => {
			await expect(async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: ENTITY,
						entry: mappedTitle,
						field: 'Title',
					},
				});

				await pageEditorPage.selectEditable(fragmentId, 'content');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: ENTITY,
						entry: mappedTitle,
						field: 'Content',
					},
				});
			}).toPass({timeout: 30000});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		const guestContext = await browser.newContext({
			storageState: {cookies: [], origins: []},
		});

		const guestPage = await guestContext.newPage();

		try {
			await test.step('GUEST sees the mapped content before the bulk delete', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(mappedTitle, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						guestPage.getByText(mappedBody, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			});

			const scrContext = await browser.newContext();

			const scrPage = await scrContext.newPage();

			try {
				await performLoginViaApi({
					page: scrPage,
					screenName: contentReviewer.alternateName,
				});

				const scrAssetsPage = new AssetsPage(scrPage);

				await test.step('The Content Reviewer bulk deletes both entries', async () => {
					await scrAssetsPage.gotoContents();

					await scrAssetsPage.selectItems([mappedTitle, secondTitle]);

					await execBulkAction(scrPage, 'Delete');
				});

				await test.step('The entries are gone from the content list', async () => {
					await expect(async () => {
						await scrAssetsPage.gotoContents();

						for (const title of [mappedTitle, secondTitle]) {
							await expect(
								scrPage.getByRole('link', {
									exact: true,
									name: title,
								})
							).toBeHidden({timeout: 2000});
						}
					}).toPass({timeout: 30000});
				});
			}
			finally {
				await scrContext.close();
			}

			await test.step('GUEST no longer sees the mapped content', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(mappedTitle, {exact: true})
					).toBeHidden({timeout: 2000});

					await expect(
						guestPage.getByText(mappedBody, {exact: true})
					).toBeHidden({timeout: 2000});
				}).toPass({timeout: 15000});
			});
		}
		finally {
			await guestContext.close();
		}

		await test.step('The entries are in the Recycle Bin', async () => {
			const recycleBinPage = new RecycleBinPage(page);

			await expect(async () => {
				await recycleBinPage.goto();

				for (const title of [mappedTitle, secondTitle]) {
					await expect(
						page.getByRole('cell', {exact: true, name: title})
					).toBeVisible({timeout: 2000});
				}
			}).toPass({timeout: 30000});
		});
	}
);
