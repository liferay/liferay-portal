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

const APPLICATION_NAME = 'cms/basic-web-contents';

const ENTITY = 'Basic Web Contents (CMS)';

test(
	'A CMS Administrator restores a previous Basic Web Content version and it renders for GUEST',
	{tag: ['@LPD-95538', '@LPD-95538/TC-15.a']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(300000);

		const spaceName = `Space ${getRandomString()}`;

		const titleV1 = `Title ${getRandomString()}`;
		const bodyV1 = `Body ${getRandomString()}`;
		const titleV3 = `Title ${getRandomString()}`;
		const bodyV3 = `Body ${getRandomString()}`;

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
				content: `<p>${bodyV1}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
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
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><div><lfr-editable id="content" type="rich-text">Content</lfr-editable></div></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the entry into a page fragment and publish', async () => {
			await expect(async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {entity: ENTITY, entry: titleV1, field: 'Title'},
				});

				await pageEditorPage.selectEditable(fragmentId, 'content');

				await pageEditorPage.setMappingConfiguration({
					mapping: {entity: ENTITY, entry: titleV1, field: 'Content'},
				});
			}).toPass({timeout: 30000});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('Edit the entry twice to generate versions 2 and 3', async () => {
			await apiHelpers.objectEntry.patchObjectEntry(
				{
					content_i18n: {en_US: `<p>Body ${getRandomString()}</p>`},
					title_i18n: {en_US: `Title ${getRandomString()}`},
				},
				APPLICATION_NAME,
				entry.id
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{
					content_i18n: {en_US: `<p>${bodyV3}</p>`},
					title_i18n: {en_US: titleV3},
				},
				APPLICATION_NAME,
				entry.id
			);
		});

		const guestContext = await browser.newContext({
			storageState: {cookies: [], origins: []},
		});

		const guestPage = await guestContext.newPage();

		try {
			await test.step('GUEST sees the current version content', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(titleV3, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						guestPage.getByText(bodyV3, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			});

			const assetsPage = new AssetsPage(page);

			await test.step('The version history lists all versions with timestamps', async () => {
				await assetsPage.gotoContents();

				await openVersionHistory(assetsPage, titleV3);

				const rows = page.locator('tbody tr');

				await expect(rows).toHaveCount(3, {timeout: 10000});

				for (const versionNumber of ['1', '2', '3']) {
					await expect(
						page.getByRole('cell', {
							exact: true,
							name: versionNumber,
						})
					).toHaveCount(1, {timeout: 5000});
				}

				await expect(page.getByText(/, \d{4},/)).toHaveCount(3, {
					timeout: 5000,
				});
			});

			await test.step('The CMS Administrator restores version 1', async () => {
				await restoreVersion(page, titleV1);
			});

			await test.step('GUEST sees the restored version 1 content', async () => {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(titleV1, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						guestPage.getByText(bodyV1, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});

				await expect(
					guestPage.getByText(titleV3, {exact: true})
				).toBeHidden({timeout: 2000});
			});
		}
		finally {
			await guestContext.close();
		}
	}
);
