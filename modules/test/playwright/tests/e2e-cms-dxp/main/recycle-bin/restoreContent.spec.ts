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
import {ContentsPage} from '../../../site-cms-site-initializer/main/pages/ContentsPage';
import {RecycleBinPage} from '../../../site-cms-site-initializer/main/pages/RecycleBinPage';
import {deleteEntryToRecycleBin, restoreEntry} from './utils/recycleBin';

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
	'A CMS Administrator restores a deleted Basic Web Content from the Recycle Bin and it renders again for GUEST',
	{tag: ['@LPD-95539', '@LPD-95539/TC-16.a']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(300000);

		const spaceName = `Space ${getRandomString()}`;
		const title = `Title ${getRandomString()}`;
		const body = `Body ${getRandomString()}`;

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
				content: `<p>${body}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
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
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><div><lfr-editable id="content" type="rich-text">Content</lfr-editable></div></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the entry into a page fragment and publish', async () => {
			await expect(async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {entity: ENTITY, entry: title, field: 'Title'},
				});

				await pageEditorPage.selectEditable(fragmentId, 'content');

				await pageEditorPage.setMappingConfiguration({
					mapping: {entity: ENTITY, entry: title, field: 'Content'},
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
			await test.step('GUEST sees the published content', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(body, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			});

			const contentsPage = new ContentsPage(page);
			const recycleBinPage = new RecycleBinPage(page);

			await test.step('The CMS Administrator deletes the content into the Recycle Bin', async () => {
				await contentsPage.goto();

				await deleteEntryToRecycleBin(page, title);
			});

			await test.step('GUEST no longer sees the deleted content', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(body, {exact: true})
					).toBeHidden({timeout: 2000});
				}).toPass({timeout: 30000});
			});

			await test.step('The CMS Administrator restores the content from the Recycle Bin', async () => {
				await recycleBinPage.goto();

				await restoreEntry(page, title);
			});

			await test.step('The content reappears in the Space', async () => {
				await expect(async () => {
					await contentsPage.goto();

					await expect(
						page.getByRole('row', {name: title})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			});

			await test.step('GUEST sees the restored content again on the DXP page', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(body, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			});
		}
		finally {
			await guestContext.close();
		}
	}
);
