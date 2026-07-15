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
	'Images can be filtered by current site and reached everywhere from a connected asset library',
	{tag: '@LPS-118808'},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		const siteImage = `${getRandomString()}.png`;
		const libraryImage = `${getRandomString()}.png`;
		const libraryFolderImage = `${getRandomString()}.png`;
		const folderName = getRandomString();

		// Seed an image in the site, and an asset library holding an image in
		// its root and another inside a folder

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(SAMPLE_IMAGE),
			{documentFolderId: 0, fileName: siteImage, title: siteImage}
		);

		const depot =
			await apiHelpers.jsonWebServicesDepot.addDepotEntry(
				getRandomString()
			);

		depotEntryId = depot.depotEntryId;

		await apiHelpers.headlessDelivery.postDocument(
			depot.groupId,
			createReadStream(SAMPLE_IMAGE),
			{fileName: libraryImage, title: libraryImage}
		);

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			depot.groupId,
			{name: folderName}
		);

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(SAMPLE_IMAGE),
			{fileName: libraryFolderImage, title: libraryFolderImage}
		);

		await apiHelpers.jsonWebServicesDepotGroupRel.addDepotEntryGroupRel(
			depot.depotEntryId,
			String(site.id)
		);

		// Open the blog cover image selector on the Documents and Media tab

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		await page.getByRole('button', {name: 'Select File'}).first().click();

		const iframe = page.frameLocator('iframe[title="Select File"]');

		await iframe.getByRole('link', {name: 'Documents and Media'}).click();

		// The default Current Site scope shows only the site image, not the
		// connected asset library's image

		await expect(iframe.getByTitle(siteImage)).toBeVisible();

		await expect(iframe.getByTitle(libraryImage)).toBeHidden();

		// The Everywhere filter reveals the asset library's image and its
		// folder, which holds the third image

		await setScopeFilter(iframe, 'Everywhere');

		await expect(iframe.getByTitle(libraryImage)).toBeVisible();

		await iframe.getByText(folderName, {exact: true}).click();

		await expect(iframe.getByTitle(libraryFolderImage)).toBeVisible();
	}
);
