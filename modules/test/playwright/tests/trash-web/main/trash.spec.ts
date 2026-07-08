/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import {RecycleBinPage} from '../../../pages/trash-web/RecycleBinPage';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	blogsPagesTest,
	loginTest()
);

test(
	'Cannot view trash entry from another site in current site recycle bin',
	{tag: '@LPD-79076'},
	async ({apiHelpers, blogsPage, page}) => {
		const blogName = `Blog ${getRandomString()}`;
		const siteOneName = `Site One ${getRandomString()}`;
		const siteTwoName = `Site Two ${getRandomString()}`;
		let siteOne: Site;
		let siteTwo: Site;
		let blog;
		let trashEntryId;
		let user;

		await test.step('Create a new Site', async () => {
			siteOne = await apiHelpers.headlessAdminSite.postSite({
				name: siteOneName,
			});
		});

		await test.step('Create a new blog in first Site', async () => {
			blog = await apiHelpers.headlessDelivery.postBlog(siteOne.id, {
				headline: blogName,
			});
		});

		await test.step('Delete the created blog so it goes into the Recycle Bin', async () => {
			await blogsPage.goto(siteOne.friendlyUrlPath);

			await blogsPage.goToBlogEntryAction('Delete', blog.title);

			await waitForAlert(
				page,
				`Success: The element ${blogName} was moved to the Recycle Bin.`
			);
		});

		await test.step('Go to Recycle Bin to confirm deletion and get trash entry ID', async () => {
			await page.goto(
				`/group${siteOne.friendlyUrlPath}${PORTLET_URLS.recycleBin}`
			);

			trashEntryId = await page
				.locator(
					`[id="_com_liferay_trash_web_portlet_TrashPortlet_trash_1"]`
				)
				.locator('input[type=checkbox]')
				.inputValue();
		});

		await test.step('Create a new second Site', async () => {
			siteTwo = await apiHelpers.headlessAdminSite.postSite({
				name: siteTwoName,
			});
		});

		await test.step('Create new site administrator user for second Site and login as site administrator', async () => {
			user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			const siteAdminRole =
				await apiHelpers.headlessAdminUser.getRoleByName(
					'Site Administrator'
				);

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteAdminRole.id,
				siteTwo.id,
				user.id
			);

			await performUserSwitch(page, user.alternateName);
		});

		await test.step('Go to the deleted content in Recycle Bin of second site and try to view the deleted content', async () => {
			await page.goto(
				`/group${siteTwo.friendlyUrlPath}${PORTLET_URLS.recycleBin}entry/${trashEntryId}`
			);

			await expect(
				page.getByText('You do not have the required permissions')
			).toBeVisible();
		});
	}
);

test('Can permanently delete a web content in the recycle bin', async ({
	apiHelpers,
	page,
	site,
}) => {
	const title = getRandomString();

	await apiHelpers.headlessDelivery.postStructuredContent({
		contentStructureId: await getBasicWebContentStructureId(apiHelpers),
		datePublished: null,
		siteId: site.id,
		title,
	});

	const recycleBinPage = new RecycleBinPage(page);
	const webContentPage = new WebContentPage(page);

	await webContentPage.goto(site.friendlyUrlPath);

	await webContentPage.moveToRecycleBin(title);

	// Permanently delete the entry from the recycle bin

	await recycleBinPage.goto(site.friendlyUrlPath);

	await recycleBinPage.assertEntry(title, 'Web Content Article');

	await recycleBinPage.delete(title);

	await recycleBinPage.assertEntryAbsent(title);

	// The web content no longer exists in the web content admin either

	await webContentPage.goto(site.friendlyUrlPath);

	await webContentPage.assertEntryAbsent(title);
});

test('Can restore a web content from the recycle bin', async ({
	apiHelpers,
	page,
	site,
}) => {
	const title = getRandomString();

	await apiHelpers.headlessDelivery.postStructuredContent({
		contentStructureId: await getBasicWebContentStructureId(apiHelpers),
		datePublished: null,
		siteId: site.id,
		title,
	});

	const recycleBinPage = new RecycleBinPage(page);
	const webContentPage = new WebContentPage(page);

	await webContentPage.goto(site.friendlyUrlPath);

	await webContentPage.moveToRecycleBin(title);

	// Restore the entry from the recycle bin

	await recycleBinPage.goto(site.friendlyUrlPath);

	await recycleBinPage.restore(title);

	await recycleBinPage.assertEntryAbsent(title);

	// The web content is back in the web content admin

	await webContentPage.goto(site.friendlyUrlPath);

	await webContentPage.assertEntryPresent(title);
});

test(
	'Can undo moving a web content to the recycle bin',
	{tag: '@LPS-146835'},
	async ({apiHelpers, page, site}) => {
		const title = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			datePublished: null,
			siteId: site.id,
			title,
		});

		const recycleBinPage = new RecycleBinPage(page);
		const webContentPage = new WebContentPage(page);

		await webContentPage.goto(site.friendlyUrlPath);

		await webContentPage.moveToRecycleBin(title, {autoClose: false});

		await webContentPage.undoMoveToRecycleBin();

		// The web content is recovered and not in the recycle bin

		await webContentPage.assertEntryPresent(title);

		await recycleBinPage.goto(site.friendlyUrlPath);

		await recycleBinPage.assertEntryAbsent(title);
	}
);

test('Can view a recycle bin entry via the success message link', async ({
	apiHelpers,
	page,
	site,
}) => {
	const title = getRandomString();

	await apiHelpers.headlessDelivery.postStructuredContent({
		contentStructureId: await getBasicWebContentStructureId(apiHelpers),
		datePublished: null,
		siteId: site.id,
		title,
	});

	const webContentPage = new WebContentPage(page);

	await webContentPage.goto(site.friendlyUrlPath);

	await webContentPage.moveToRecycleBin(title, {autoClose: false});

	await webContentPage.gotoRecycleBinEntryViaSuccessMessage();

	await expect(page.getByText(title).first()).toBeVisible();
});

test('Can move a web content to the recycle bin via the delete icon', async ({
	apiHelpers,
	page,
	site,
}) => {
	const title = getRandomString();

	await apiHelpers.headlessDelivery.postStructuredContent({
		contentStructureId: await getBasicWebContentStructureId(apiHelpers),
		datePublished: null,
		siteId: site.id,
		title,
	});

	const recycleBinPage = new RecycleBinPage(page);
	const webContentPage = new WebContentPage(page);

	await webContentPage.goto(site.friendlyUrlPath);

	await webContentPage.moveToRecycleBinViaDeleteIcon(title);

	// The web content is gone from the admin and now in the recycle bin

	await webContentPage.assertEntryAbsent(title);

	await recycleBinPage.goto(site.friendlyUrlPath);

	await recycleBinPage.assertEntry(title, 'Web Content Article');
});

test('Can view duplicate web content and folder names in the recycle bin', async ({
	apiHelpers,
	page,
	site,
}) => {
	const folderName = getRandomString();
	const title = getRandomString();

	const contentStructureId = await getBasicWebContentStructureId(apiHelpers);

	const recycleBinPage = new RecycleBinPage(page);
	const webContentPage = new WebContentPage(page);

	// Move two web content with the same title to the recycle bin

	for (let i = 0; i < 2; i++) {
		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId,
			datePublished: null,
			siteId: site.id,
			title,
		});

		await webContentPage.goto(site.friendlyUrlPath);

		await webContentPage.moveToRecycleBinViaDeleteIcon(title);
	}

	await recycleBinPage.goto(site.friendlyUrlPath);

	await recycleBinPage.assertEntryCount(title, 2);

	// Move two folders with the same name to the recycle bin

	for (let i = 0; i < 2; i++) {
		await apiHelpers.headlessDelivery.postStructuredContentFolder(site.id, {
			name: folderName,
		});

		await webContentPage.goto(site.friendlyUrlPath);

		await webContentPage.moveFolderToRecycleBin(folderName);
	}

	await recycleBinPage.goto(site.friendlyUrlPath);

	await recycleBinPage.assertEntryCount(folderName, 2);
});

test('Can restore a web content from a folder in the recycle bin', async ({
	apiHelpers,
	page,
	site,
}) => {
	const folderName = getRandomString();
	const title = getRandomString();

	const structuredContentFolder =
		await apiHelpers.headlessDelivery.postStructuredContentFolder(site.id, {
			name: folderName,
		});

	await apiHelpers.headlessDelivery.postStructuredContentFolderStructuredContent(
		{
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			datePublished: null,
			structuredContentFolderId: structuredContentFolder.id,
			title,
		}
	);

	const recycleBinPage = new RecycleBinPage(page);
	const webContentPage = new WebContentPage(page);

	// Move the whole folder, along with its web content, to the recycle bin

	await webContentPage.goto(site.friendlyUrlPath);

	await webContentPage.moveFolderToRecycleBin(folderName);

	// Restore the web content out of the trashed folder to the site home

	await recycleBinPage.goto(site.friendlyUrlPath);

	await recycleBinPage.restoreContentFromFolder(folderName, title);

	// The web content is back in the web content admin at the site home

	await webContentPage.goto(site.friendlyUrlPath);

	await webContentPage.assertEntryPresent(title);
});

test('Can restore a blog entry from the recycle bin', async ({
	apiHelpers,
	blogsPage,
	page,
	site,
}) => {
	const headline = `Blog ${getRandomString()}`;

	await apiHelpers.headlessDelivery.postBlog(site.id, {headline});

	const recycleBinPage = new RecycleBinPage(page);

	// Move the blog entry to the recycle bin

	await blogsPage.goto(site.friendlyUrlPath);

	await blogsPage.moveEntryToRecycleBin(headline);

	// Restore it from the recycle bin

	await recycleBinPage.goto(site.friendlyUrlPath);

	await recycleBinPage.assertEntry(headline, 'Blogs Entry');

	await recycleBinPage.restore(headline);

	await recycleBinPage.assertEntryAbsent(headline);

	// The blog entry is back in blogs

	await blogsPage.goto(site.friendlyUrlPath);

	await blogsPage.assertEntryPresent(headline);
});

test('Can search for blog entries in the recycle bin', async ({
	apiHelpers,
	blogsPage,
	page,
	site,
}) => {
	const headlines = [1, 2, 3].map(() => `Blog ${getRandomString()}`);

	for (const headline of headlines) {
		await apiHelpers.headlessDelivery.postBlog(site.id, {headline});
	}

	const recycleBinPage = new RecycleBinPage(page);

	// Move the three blog entries to the recycle bin

	await blogsPage.goto(site.friendlyUrlPath);

	for (const headline of headlines) {
		await blogsPage.moveEntryToRecycleBin(headline);
	}

	// Each entry is found by searching the recycle bin for it

	await recycleBinPage.goto(site.friendlyUrlPath);

	for (const headline of headlines) {
		await recycleBinPage.search(headline);

		await recycleBinPage.assertEntry(headline, 'Blogs Entry');
	}
});
