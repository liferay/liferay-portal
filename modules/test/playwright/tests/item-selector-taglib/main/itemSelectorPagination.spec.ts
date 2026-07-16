/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {nextPage, setItemsPerPage} from '../../../utils/pagination';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';

const SAMPLE_IMAGE = path.join(
	__dirname,
	'../../frontend-js-item-selector-web/main/dependencies/sample_image.png'
);

const test = mergeTests(
	apiHelpersTest,
	blogsPagesTest,
	isolatedSiteTest,
	loginTest()
);

async function setScopeFilter(iframe: FrameLocator, scope: string) {
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: iframe.getByRole('menuitem', {exact: true, name: scope}),
		timeout: 3000,
		trigger: iframe.getByLabel('Filter', {exact: true}),
	});
}

let depotEntryId: string;

test.afterEach(async ({apiHelpers}) => {
	if (depotEntryId) {
		await apiHelpers.jsonWebServicesDepot.deleteDepotEntry(depotEntryId);

		depotEntryId = '';
	}
});

test(
	'Everywhere document results paginate correctly across a site and its asset library',
	{tag: '@LPS-122139'},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {

		// Seed 7 documents in the site and 23 in a connected asset library, so
		// the everywhere scope holds exactly 30 documents

		for (let i = 0; i < 7; i++) {
			const fileName = `${getRandomString()}.jpg`;

			await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(SAMPLE_IMAGE),
				{documentFolderId: 0, fileName, title: fileName}
			);
		}

		const depot =
			await apiHelpers.jsonWebServicesDepot.addDepotEntry(
				getRandomString()
			);

		depotEntryId = depot.depotEntryId;

		for (let i = 0; i < 23; i++) {
			const fileName = `${getRandomString()}.jpg`;

			await apiHelpers.headlessDelivery.postDocument(
				depot.groupId,
				createReadStream(SAMPLE_IMAGE),
				{fileName, title: fileName}
			);
		}

		await apiHelpers.jsonWebServicesDepotGroupRel.addDepotEntryGroupRel(
			depot.depotEntryId,
			String(site.id)
		);

		// Open the blog cover image selector on the Documents and Media tab and
		// browse everywhere

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		await page.getByRole('button', {name: 'Select File'}).first().click();

		const iframe = page.frameLocator('iframe[title="Select File"]');

		await iframe.getByRole('link', {name: 'Documents and Media'}).click();

		await setScopeFilter(iframe, 'Everywhere');

		// The default 20 per page shows the first of the 30 documents

		await expect(
			iframe.getByText('Showing 1 to 20 of 30 entries')
		).toBeVisible();

		// 40 per page fits all 30 documents on a single page

		await setItemsPerPage(iframe, 40);

		await expect(
			iframe.getByText('Showing 1 to 30 of 30 entries')
		).toBeVisible();

		// Back to 20 per page, the next page holds the remaining documents

		await setItemsPerPage(iframe, 20);

		await nextPage(iframe);

		await expect(
			iframe.getByText('Showing 21 to 30 of 30 entries')
		).toBeVisible();
	}
);
