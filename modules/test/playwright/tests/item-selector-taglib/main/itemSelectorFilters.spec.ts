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

// The item selector has no semantic role for a result item, so each display
// style is matched by the container class the repository entry browser renders
// (card, list row, table row); the location badge and title both live inside it

const ITEM_CONTAINER_BY_DISPLAY_STYLE = {
	Cards: '.card',
	List: '.list-group-item',
	Table: 'tr',
};

async function setDisplayStyle(iframe: FrameLocator, displayStyle: string) {
	const menuItem = iframe.getByRole('menuitem', {
		exact: true,
		name: displayStyle,
	});

	await clickAndExpectToBeVisible({
		target: menuItem,
		timeout: 3000,
		trigger: iframe.getByLabel('Select View, Currently'),
	});

	// The menu item is a floating link, so force past the actionability check;
	// the toggle label then reflects the view that took effect

	await menuItem.click({force: true});

	await expect(
		iframe.getByLabel(`Select View, Currently Selected: ${displayStyle}`)
	).toBeVisible();
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

test('Documents show their originating location in every display style when browsing everywhere', async ({
	apiHelpers,
	blogsEditBlogEntryPage,
	page,
	site,
}) => {
	const documentTitle1 = `${getRandomString()}.jpg`;
	const documentTitle2 = `${getRandomString()}.jpg`;
	const folderName = getRandomString();

	// Seed the same document names in a folder and at the root of both the
	// site and a connected asset library, so only the location badge tells
	// the otherwise identical entries apart

	const depotName = getRandomString();

	const depot =
		await apiHelpers.jsonWebServicesDepot.addDepotEntry(depotName);

	depotEntryId = depot.depotEntryId;

	for (const groupId of [site.id, depot.groupId]) {
		await apiHelpers.headlessDelivery.postDocument(
			groupId,
			createReadStream(SAMPLE_IMAGE),
			{
				documentFolderId: 0,
				fileName: documentTitle1,
				title: documentTitle1,
			}
		);

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			groupId,
			{name: folderName}
		);

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(SAMPLE_IMAGE),
			{fileName: documentTitle2, title: documentTitle2}
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

	// Each entry, including the folder, reports its site or asset library
	// as its location in the cards, list, and table views

	for (const displayStyle of ['Cards', 'List', 'Table']) {
		await setDisplayStyle(iframe, displayStyle);

		for (const location of [site.name, depotName]) {
			for (const title of [documentTitle1, documentTitle2, folderName]) {
				await expect(
					iframe
						.locator(
							ITEM_CONTAINER_BY_DISPLAY_STYLE[displayStyle],
							{hasText: title}
						)
						.filter({hasText: location})
				).toBeVisible();
			}
		}
	}
});
