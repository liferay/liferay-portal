/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {RecycleBinPage} from '../../../site-cms-site-initializer/main/pages/RecycleBinPage';
import {
	addSpaceUserWithSession,
	expectRestoreUnavailable,
} from './utils/recycleBin';

const test = mergeTests(dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-web-contents';

async function createTrashedContent(
	apiHelpers: DataApiHelpers,
	spaceName: string
) {
	const title = `Content ${getRandomString()}`;

	const entry = await apiHelpers.objectEntry.postObjectEntry(
		{
			content: `<p>${getRandomString()}</p>`,
			objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			title,
		},
		APPLICATION_NAME,
		spaceName
	);

	await apiHelpers.objectEntry.deleteObjectEntry(
		APPLICATION_NAME,
		String(entry.id)
	);

	return title;
}

test(
	'A CMS Administrator sees Recycle Bin items from all Spaces',
	{tag: ['@LPD-95539', '@LPD-95539/TC-16.e']},
	async ({apiHelpers, page}) => {
		test.setTimeout(120000);

		const spaceName1 = `Space ${getRandomString()}`;
		const spaceName2 = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName1,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName2,
			type: 'Space',
		});

		const title1 = await createTrashedContent(apiHelpers, spaceName1);
		const title2 = await createTrashedContent(apiHelpers, spaceName2);

		const recycleBinPage = new RecycleBinPage(page);

		await recycleBinPage.goto();

		await expect(async () => {
			await recycleBinPage.goto();

			await expect(
				page.locator('tbody tr', {hasText: title1})
			).toBeVisible({timeout: 2000});

			await expect(
				page.locator('tbody tr', {hasText: title2})
			).toBeVisible({timeout: 2000});
		}).toPass({timeout: 30000});
	}
);

test(
	'A Space Administrator sees only their own Space in the Recycle Bin',
	{tag: ['@LPD-95539', '@LPD-95539/TC-16.e']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(120000);

		const spaceName1 = `Space ${getRandomString()}`;
		const spaceName2 = `Space ${getRandomString()}`;

		const space1 = await apiHelpers.headlessAssetLibrary.createAssetLibrary(
			{
				name: spaceName1,
				type: 'Space',
			}
		);

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName2,
			type: 'Space',
		});

		const title1 = await createTrashedContent(apiHelpers, spaceName1);
		const title2 = await createTrashedContent(apiHelpers, spaceName2);

		const spaceAdministrator = await addSpaceUserWithSession(
			apiHelpers,
			space1.externalReferenceCode,
			'Asset Library Administrator'
		);

		const spaContext = await browser.newContext();

		const spaPage = await spaContext.newPage();

		try {
			await performLoginViaApi({
				page: spaPage,
				screenName: spaceAdministrator.alternateName,
			});

			const recycleBinPage = new RecycleBinPage(spaPage);

			await recycleBinPage.goto();

			await expect(
				spaPage.locator('tbody tr', {hasText: title1})
			).toBeVisible({timeout: 10000});

			await expect(
				spaPage.locator('tbody tr', {hasText: title2})
			).toBeHidden({timeout: 5000});
		}
		finally {
			await spaContext.close();
		}
	}
);

test(
	'A Space Member cannot restore items from the Recycle Bin',
	{tag: ['@LPD-95539', '@LPD-95539/TC-16.e', '@LPD-95539/TC-16.a']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(120000);

		const spaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const title = await createTrashedContent(apiHelpers, spaceName);

		const spaceMember = await addSpaceUserWithSession(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Member'
		);

		const memberContext = await browser.newContext();

		const memberPage = await memberContext.newPage();

		try {
			await performLoginViaApi({
				page: memberPage,
				screenName: spaceMember.alternateName,
			});

			const recycleBinPage = new RecycleBinPage(memberPage);

			await recycleBinPage.goto();

			await expect(
				memberPage.locator('tbody tr', {hasText: title})
			).toBeVisible({timeout: 10000});

			await expectRestoreUnavailable(memberPage, title);
		}
		finally {
			await memberContext.close();
		}
	}
);

test(
	'A Space Content Reviewer cannot restore items from the Recycle Bin',
	{
		tag: [
			'@LPD-95539',
			'@LPD-95539/TC-16.f',
			'@LPD-95539/TC-16.a',
			'@LPD-96455',
		],
	},
	async ({apiHelpers, browser}) => {
		test.setTimeout(120000);

		test.fail(
			true,
			"LPD-96455 (Won't Fix): the product allows a Space Content Reviewer to restore from the Recycle Bin, so the restore action is available."
		);

		const spaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const title = await createTrashedContent(apiHelpers, spaceName);

		const contentReviewer = await addSpaceUserWithSession(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Content Reviewer'
		);

		const reviewerContext = await browser.newContext();

		const reviewerPage = await reviewerContext.newPage();

		try {
			await performLoginViaApi({
				page: reviewerPage,
				screenName: contentReviewer.alternateName,
			});

			const recycleBinPage = new RecycleBinPage(reviewerPage);

			await recycleBinPage.goto();

			await expect(
				reviewerPage.locator('tbody tr', {hasText: title})
			).toBeVisible({timeout: 10000});

			await expectRestoreUnavailable(reviewerPage, title);
		}
		finally {
			await reviewerContext.close();
		}
	}
);
