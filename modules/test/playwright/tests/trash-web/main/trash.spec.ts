/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import {RecycleBinPage} from '../../../pages/trash-web/RecycleBinPage';
import {SizedFileType, createSizedFile} from '../../../utils/createSizedFile';
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

function createDocument(
	apiHelpers: DataApiHelpers,
	siteId: string,
	fileName: string
) {
	const type = fileName.split('.').pop() as SizedFileType;

	return apiHelpers.headlessDelivery.postDocument(
		siteId,
		createReadStream(
			createSizedFile(`${getRandomString()}.${type}`, type, 1024)
		),
		{fileName, title: fileName}
	);
}

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

test(
	'Can bulk restore multiple assets from the recycle bin',
	{tag: '@LPS-109555'},
	async ({apiHelpers, blogsPage, page, site}) => {
		const documentName = `${getRandomString()}.png`;
		const headline = `Blog ${getRandomString()}`;
		const title = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			datePublished: null,
			siteId: site.id,
			title,
		});

		await apiHelpers.headlessDelivery.postBlog(site.id, {headline});

		await createDocument(apiHelpers, site.id, documentName);

		const documentLibraryPage = new DocumentLibraryPage(page);
		const recycleBinPage = new RecycleBinPage(page);
		const webContentPage = new WebContentPage(page);

		// Move the web content, blog entry and document to the recycle bin

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.moveToRecycleBin(documentName);

		await blogsPage.goto(site.friendlyUrlPath);

		await blogsPage.moveEntryToRecycleBin(headline);

		await webContentPage.goto(site.friendlyUrlPath);

		await webContentPage.moveToRecycleBin(title);

		// Bulk restore all three from the recycle bin

		await recycleBinPage.goto(site.friendlyUrlPath);

		await recycleBinPage.assertEntry(documentName, 'Document');

		await recycleBinPage.assertEntry(headline, 'Blogs Entry');

		await recycleBinPage.assertEntry(title, 'Web Content Article');

		await recycleBinPage.bulkRestore([documentName, headline, title]);

		// Each asset is back in its own admin

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {name: documentName})
		).toBeVisible();

		await blogsPage.goto(site.friendlyUrlPath);

		await blogsPage.assertEntryPresent(headline);

		await webContentPage.goto(site.friendlyUrlPath);

		await webContentPage.assertEntryPresent(title);
	}
);

test(
	'Can automatically rename documents on bulk restore when the names already exist',
	{tag: '@LPS-109555'},
	async ({apiHelpers, page, site}) => {
		const base = getRandomString();
		const documentNames = [`${base}.png`, `${base}.pdf`];

		for (const documentName of documentNames) {
			await createDocument(apiHelpers, site.id, documentName);
		}

		const documentLibraryPage = new DocumentLibraryPage(page);
		const recycleBinPage = new RecycleBinPage(page);

		// Move both documents to the recycle bin

		await documentLibraryPage.goto(site.friendlyUrlPath);

		for (const documentName of documentNames) {
			await documentLibraryPage.moveToRecycleBin(documentName);
		}

		// Recreate documents with the same names so the restore collides

		for (const documentName of documentNames) {
			await createDocument(apiHelpers, site.id, documentName);
		}

		// Bulk restore both from the recycle bin

		await recycleBinPage.goto(site.friendlyUrlPath);

		await recycleBinPage.bulkRestore(documentNames);

		// Both the existing and the auto-renamed copies are present

		await documentLibraryPage.goto(site.friendlyUrlPath);

		for (const documentName of [
			`${base}.png`,
			`${base}.pdf`,
			`${base} (1).png`,
			`${base} (1).pdf`,
		]) {
			await expect(
				page.getByRole('link', {name: documentName})
			).toBeVisible();
		}
	}
);

test(
	'Can keep both entries by renaming a document restored from the recycle bin',
	{tag: ['@LPS-33785', '@LPS-109555']},
	async ({apiHelpers, page, site}) => {
		const documentTitle = `Document ${getRandomString()}`;
		const newName = `Previous Document ${getRandomString()}`;

		const postDocument = () =>
			apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					createSizedFile(`${getRandomString()}.png`, 'png', 1024)
				),
				{fileName: `${getRandomString()}.png`, title: documentTitle}
			);

		await postDocument();

		const documentLibraryPage = new DocumentLibraryPage(page);
		const recycleBinPage = new RecycleBinPage(page);

		// Move the document to the recycle bin

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.moveToRecycleBin(documentTitle);

		// Recreate a document with the same name so the restore collides

		await postDocument();

		// Restore the trashed document by keeping both and renaming it

		await recycleBinPage.goto(site.friendlyUrlPath);

		await recycleBinPage.restoreRename(documentTitle, newName);

		// Both the existing and the renamed restored document are present

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {name: documentTitle})
		).toBeVisible();

		await expect(page.getByRole('link', {name: newName})).toBeVisible();
	}
);

test(
	'Can view a restored document in a subfolder after restoring the parent folder',
	{tag: '@LPS-44554'},
	async ({apiHelpers, page, site}) => {
		const documentTitle = getRandomString();
		const folderName = getRandomString();
		const subfolderName = getRandomString();

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id,
			{name: folderName}
		);

		const subfolder = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id,
			{name: subfolderName, parentDocumentFolderId: folder.id}
		);

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			subfolder.id,
			createReadStream(
				createSizedFile(`${getRandomString()}.png`, 'png', 1024)
			),
			{fileName: `${getRandomString()}.png`, title: documentTitle}
		);

		const documentLibraryPage = new DocumentLibraryPage(page);
		const recycleBinPage = new RecycleBinPage(page);

		// Move the parent folder, with its subfolder and document, to the
		// recycle bin

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.moveFolderToRecycleBin(folderName);

		// Restore the parent folder from the recycle bin

		await recycleBinPage.goto(site.friendlyUrlPath);

		await recycleBinPage.restore(folderName);

		await recycleBinPage.assertEntryAbsent(folderName);

		// The document is still inside the restored subfolder

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: folderName}).click();

		await page.getByRole('link', {name: subfolderName}).click();

		await expect(
			page.getByRole('link', {name: documentTitle})
		).toBeVisible();
	}
);

test(
	'Cannot search a trashed blog entry by its comments',
	{tag: '@LPS-44099'},
	async ({apiHelpers, blogsPage, page, site}) => {
		const comment = `Comment${getRandomString()}`;
		const headlines = [1, 2].map(() => `Blog ${getRandomString()}`);

		for (const headline of headlines) {
			const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
				headline,
			});

			await apiHelpers.post(
				`${apiHelpers.baseUrl}headless-delivery/v1.0/blog-postings/${blog.id}/comments`,
				{data: {text: comment}, failOnStatusCode: true}
			);
		}

		// Both blog entries are found when searching by their comment

		await blogsPage.goto(site.friendlyUrlPath);

		await blogsPage.searchEntry(comment);

		await expect(
			page.getByText(`2 Results Found for "${comment}"`)
		).toBeVisible();

		for (const headline of headlines) {
			await blogsPage.assertEntryPresent(headline);
		}

		// Move the first blog entry to the recycle bin

		await blogsPage.goto(site.friendlyUrlPath);

		await blogsPage.moveEntryToRecycleBin(headlines[0]);

		// The trashed entry is no longer found by its comment

		await blogsPage.searchEntry(comment);

		await expect(
			page.getByText(`1 Result Found for "${comment}"`)
		).toBeVisible();

		await blogsPage.assertEntryPresent(headlines[1]);

		await blogsPage.assertEntryPresent(headlines[0], false);
	}
);

test('Can search for documents from a deleted folder in the recycle bin', async ({
	apiHelpers,
	page,
	site,
}) => {
	const folderName = getRandomString();
	const documentTitles = [
		`Document ${getRandomString()}`,
		`Document ${getRandomString()}`,
	];

	const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
		site.id,
		{name: folderName}
	);

	for (const title of documentTitles) {
		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(
				createSizedFile(`${getRandomString()}.png`, 'png', 1024)
			),
			{fileName: `${getRandomString()}.png`, title}
		);
	}

	const documentLibraryPage = new DocumentLibraryPage(page);
	const recycleBinPage = new RecycleBinPage(page);

	// Move the folder, with its documents, to the recycle bin

	await documentLibraryPage.goto(site.friendlyUrlPath);

	await documentLibraryPage.moveFolderToRecycleBin(folderName);

	// Each document inside the deleted folder is found by searching for it

	await recycleBinPage.goto(site.friendlyUrlPath);

	for (const title of documentTitles) {
		await recycleBinPage.search(title);

		await recycleBinPage.assertEntry(title, 'Document');
	}
});

test(
	'Can restore documents from a trashed folder to existing folders',
	{tag: '@LPS-190757'},
	async ({apiHelpers, page, site}) => {
		const folderName = `Folder ${getRandomString()}`;
		const targetFolderName = `Folder ${getRandomString()}`;

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id,
			{name: folderName}
		);

		await apiHelpers.headlessDelivery.postDocumentFolder(site.id, {
			name: targetFolderName,
		});

		const homeDocumentTitle = `Document ${getRandomString()}.png`;
		const targetDocumentTitle = `Document ${getRandomString()}.png`;

		for (const title of [homeDocumentTitle, targetDocumentTitle]) {
			await apiHelpers.headlessDelivery.postDocumentFolderDocument(
				folder.id,
				createReadStream(
					createSizedFile(`${getRandomString()}.png`, 'png', 1024)
				),
				{fileName: `${getRandomString()}.png`, title}
			);
		}

		const documentLibraryPage = new DocumentLibraryPage(page);
		const recycleBinPage = new RecycleBinPage(page);

		// Move the folder, with its documents, to the recycle bin

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.moveFolderToRecycleBin(folderName);

		// Restore one document from the trashed folder to the site home

		await recycleBinPage.goto(site.friendlyUrlPath);

		await recycleBinPage.restoreContentFromFolder(
			folderName,
			homeDocumentTitle
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {name: homeDocumentTitle})
		).toBeVisible();

		// Restore another document from the trashed folder to the second folder

		await recycleBinPage.goto(site.friendlyUrlPath);

		await recycleBinPage.restoreContentFromFolder(
			folderName,
			targetDocumentTitle,
			targetFolderName
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: targetFolderName}).click();

		await expect(
			page.getByRole('link', {name: targetDocumentTitle})
		).toBeVisible();
	}
);

test(
	'Can restore web content from a trashed folder to existing folders',
	{tag: '@LPS-190757'},
	async ({apiHelpers, page, site}) => {
		const folderName = `Folder ${getRandomString()}`;
		const targetFolderName = `Folder ${getRandomString()}`;

		const structuredContentFolder =
			await apiHelpers.headlessDelivery.postStructuredContentFolder(
				site.id,
				{
					name: folderName,
				}
			);

		await apiHelpers.headlessDelivery.postStructuredContentFolder(site.id, {
			name: targetFolderName,
		});

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const homeWebContentTitle = `Web Content ${getRandomString()}`;
		const targetWebContentTitle = `Web Content ${getRandomString()}`;

		for (const title of [homeWebContentTitle, targetWebContentTitle]) {
			await apiHelpers.headlessDelivery.postStructuredContentFolderStructuredContent(
				{
					contentStructureId,
					datePublished: null,
					structuredContentFolderId: structuredContentFolder.id,
					title,
				}
			);
		}

		const recycleBinPage = new RecycleBinPage(page);
		const webContentPage = new WebContentPage(page);

		// Move the folder, with its web content, to the recycle bin

		await webContentPage.goto(site.friendlyUrlPath);

		await webContentPage.moveFolderToRecycleBin(folderName);

		// Restore one web content from the trashed folder to the site home

		await recycleBinPage.goto(site.friendlyUrlPath);

		await recycleBinPage.restoreContentFromFolder(
			folderName,
			homeWebContentTitle
		);

		await webContentPage.goto(site.friendlyUrlPath);

		await webContentPage.assertEntryPresent(homeWebContentTitle);

		// Restore another web content from the trashed folder to the second folder

		await recycleBinPage.goto(site.friendlyUrlPath);

		await recycleBinPage.restoreContentFromFolder(
			folderName,
			targetWebContentTitle,
			targetFolderName
		);

		await webContentPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: targetFolderName}).click();

		await webContentPage.assertEntryPresent(targetWebContentTitle);
	}
);

test(
	'Can access the recycle bin back button tooltip via the keyboard',
	{tag: '@LPS-177777'},
	async ({apiHelpers, blogsPage, page, site}) => {
		const blogHeadline = `Blog ${getRandomString()}`;
		const documentTitle = `Document ${getRandomString()}.png`;
		const webContentTitle = `Web Content ${getRandomString()}`;

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blogHeadline,
		});

		await createDocument(apiHelpers, site.id, documentTitle);

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			datePublished: null,
			siteId: site.id,
			title: webContentTitle,
		});

		const documentLibraryPage = new DocumentLibraryPage(page);
		const recycleBinPage = new RecycleBinPage(page);
		const webContentPage = new WebContentPage(page);

		// Move each asset to the recycle bin

		await webContentPage.goto(site.friendlyUrlPath);

		await webContentPage.moveToRecycleBin(webContentTitle);

		await blogsPage.goto(site.friendlyUrlPath);

		await blogsPage.moveEntryToRecycleBin(blogHeadline);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.moveToRecycleBin(documentTitle);

		// Opening any trashed asset exposes a keyboard focusable back button
		// whose tooltip reads "Go to Recycle Bin"

		for (const assetName of [
			webContentTitle,
			blogHeadline,
			documentTitle,
		]) {
			await recycleBinPage.goto(site.friendlyUrlPath);

			await recycleBinPage.viewEntry(assetName);

			const backButton = page.getByRole('link', {
				name: 'Go to Recycle Bin',
			});

			const tooltip = page
				.locator('.tooltip')
				.getByText('Go to Recycle Bin');

			await backButton.focus();

			// The tooltip only appears on a keyboard driven focus, so tab away
			// and back to land on the button through the keyboard

			await expect(async () => {
				await page.keyboard.press('Shift+Tab');

				await page.keyboard.press('Tab');

				await expect(tooltip).toBeVisible({timeout: 2000});
			}).toPass();
		}
	}
);
