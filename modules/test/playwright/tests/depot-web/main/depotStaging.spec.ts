/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {BlogsEditBlogEntryPage} from '../../blogs-web/main/pages/BlogsEditBlogEntryPage';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {stagedDepotSiteTest} from './fixtures/stagedDepotSiteTest';

const test = mergeTests(
	blogsPagesTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
	}),
	loginTest(),
	pagesAdminPagesTest,
	pagesPagesTest,
	stagedDepotSiteTest
);

const DL_FILE_ENTRY_CLASS_NAME =
	'com.liferay.document.library.kernel.model.DLFileEntry';

const DL_FOLDER_CLASS_NAME =
	'com.liferay.document.library.kernel.model.DLFolder';

function getImage() {
	return createReadStream(
		path.join(__dirname, 'dependencies', 'Document.jpg')
	);
}

async function gotoDocumentCustomFields(page: Page) {
	await page.goto(
		'/group/control_panel/manage?p_p_id=com_liferay_expando_web_portlet_ExpandoPortlet'
	);

	await clickAndExpectToBeVisible({
		target: page.getByRole('link', {name: 'Add Custom Field'}),
		trigger: page.getByRole('link', {exact: true, name: 'Document'}),
	});
}

async function openSelectFileEverywhere(
	blogsEditBlogEntryPage: BlogsEditBlogEntryPage,
	siteFriendlyURL: string
) {
	const {page} = blogsEditBlogEntryPage;

	await blogsEditBlogEntryPage.goto(siteFriendlyURL);

	await page.getByRole('button', {name: 'Select File'}).first().click();

	const itemSelector = page.frameLocator('iframe[title="Select File"]');

	await itemSelector.getByRole('link', {name: 'Documents and Media'}).click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: itemSelector.getByRole('menuitem', {
			exact: true,
			name: 'Everywhere',
		}),
		timeout: 3000,
		trigger: itemSelector.getByLabel('Filter', {exact: true}),
	});

	return itemSelector;
}

test(
	'Asset Publisher on a staged site shows the latest version of an asset library image',
	{tag: '@LPD-112054'},
	async ({apiHelpers, depotStagingPage, stagedDepotSite}) => {
		test.setTimeout(300000);

		const {
			assetLibrary,
			depotFriendlyURL,
			livePageURL,
			stage,
			stagedPageURL,
		} = stagedDepotSite;

		// Add an image to the asset library and stage both groups

		const documentTitle = `${getRandomString()}.jpg`;

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary.id,
			getImage(),
			{fileName: documentTitle, title: documentTitle}
		);

		await stage();

		// Edit the staged image and publish the asset library

		const description = getRandomString();

		await apiHelpers.headlessDelivery.patchDocument({
			document: {description},
			documentId: await depotStagingPage.getStagedFileEntryId(
				depotFriendlyURL,
				documentTitle
			),
		});

		await depotStagingPage.publishDepotToLive(depotFriendlyURL);

		// Select the image from the asset library on the staged page

		await depotStagingPage.page.goto(stagedPageURL);

		await depotStagingPage.openAssetPublisherConfiguration();
		await depotStagingPage.selectManualAsset(
			assetLibrary.name,
			documentTitle
		);
		await depotStagingPage.closeConfiguration();

		// The staged page shows the latest version from the staged asset library

		const {assetPublisher, page} = depotStagingPage;

		await page.reload();

		await expect(assetPublisher.getByText(description)).toBeVisible();

		await assetPublisher.getByText(documentTitle).hover();

		await expect(
			assetPublisher.getByRole('link', {name: 'Edit'})
		).toHaveAttribute('href', new RegExp(`${depotFriendlyURL}-staging/`));

		// Publish the site and view the image on the live page

		await depotStagingPage.publishSiteToLive(stagedPageURL);

		await page.goto(livePageURL);

		await expect(assetPublisher.getByText(description)).toBeVisible();
	}
);

test(
	'Asset Publisher on a staged site shows an asset library folder only after the site is published',
	{tag: '@LPD-112613'},
	async ({apiHelpers, depotStagingPage, stagedDepotSite}) => {
		test.setTimeout(300000);

		const {
			assetLibrary,
			depotFriendlyURL,
			livePageURL,
			stage,
			stagedPageURL,
		} = stagedDepotSite;

		// Add a folder with one document to the asset library and stage both groups

		const folderDescription = getRandomString();
		const folderName = getRandomString();

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			assetLibrary.siteId,
			{description: folderDescription, name: folderName}
		);

		const documentTitle = `${getRandomString()}.jpg`;

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			getImage(),
			{fileName: documentTitle, title: documentTitle}
		);

		await stage();

		await depotStagingPage.waitForStagedEntry(depotFriendlyURL, folderName);

		// Select the folder from the asset library on the staged page

		const {assetPublisher, page} = depotStagingPage;

		await page.goto(stagedPageURL);

		await depotStagingPage.openAssetPublisherConfiguration();
		await depotStagingPage.selectManualAsset(assetLibrary.name, folderName);
		await depotStagingPage.closeConfiguration();

		await page.reload();

		await expect(
			assetPublisher.getByText(folderName, {exact: true})
		).toBeVisible();
		await expect(assetPublisher.getByText(folderDescription)).toBeVisible();

		// The live page shows the folder only after the site is published

		await page.goto(livePageURL);

		await expect(assetPublisher.getByText(folderName)).toBeHidden();

		await depotStagingPage.publishSiteToLive(stagedPageURL);

		await page.goto(livePageURL);

		await expect(
			assetPublisher.getByText(folderName, {exact: true})
		).toBeVisible();
		await expect(assetPublisher.getByText(folderDescription)).toBeVisible();
	}
);

test(
	'Documents and Media widget on a staged site follows its asset library root folder',
	{tag: '@LPD-112613'},
	async ({apiHelpers, depotStagingPage, stagedDepotSite}) => {
		test.setTimeout(300000);

		const {assetLibrary, depotFriendlyURL, site, stage} = stagedDepotSite;

		// Add a folder and a subfolder with an image each to the asset library
		// and a Documents and Media widget page, then stage both groups

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			assetLibrary.siteId,
			{name: getRandomString()}
		);

		const folderDocumentTitle = `${getRandomString()}.jpg`;

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			getImage(),
			{fileName: folderDocumentTitle, title: folderDocumentTitle}
		);

		const subfolder =
			await apiHelpers.headlessDelivery.postDocumentFolderDocumentFolder(
				folder.id
			);

		const subfolderDocumentTitle = `${getRandomString()}.jpg`;

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			subfolder.id,
			getImage(),
			{fileName: subfolderDocumentTitle, title: subfolderDocumentTitle}
		);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
			typeSettings:
				'layout-template-id=1_column\ncolumn-1=com_liferay_document_library_web_portlet_DLPortlet\n',
		});

		await stage();

		const liveWidgetPageURL = `/web${site.friendlyUrlPath}${layout.friendlyURL}`;
		const stagedWidgetPageURL = `/web${site.friendlyUrlPath}-staging${layout.friendlyURL}`;

		const {documentsAndMedia, page} = depotStagingPage;

		const getDocumentCard = (title: string) =>
			documentsAndMedia.locator('.card').filter({hasText: title});

		await test.step('The staged widget shows the root folder and publishes it', async () => {
			await page.goto(stagedWidgetPageURL);

			await depotStagingPage.setDocumentsAndMediaRootFolder(
				assetLibrary.name,
				folder.name
			);

			await page.goto(stagedWidgetPageURL);

			await expect(getDocumentCard(folderDocumentTitle)).toBeVisible();

			await depotStagingPage.publishSiteToLive(stagedWidgetPageURL);

			await page.goto(liveWidgetPageURL);

			await expect(getDocumentCard(folderDocumentTitle)).toBeVisible();
		});

		await test.step('Changing the root to the subfolder reaches live after publishing', async () => {
			await page.goto(stagedWidgetPageURL);

			await depotStagingPage.setDocumentsAndMediaRootFolder(
				assetLibrary.name,
				subfolder.name,
				folder.name
			);

			await page.goto(stagedWidgetPageURL);

			await expect(getDocumentCard(subfolderDocumentTitle)).toBeVisible();
			await expect(getDocumentCard(folderDocumentTitle)).toHaveCount(0);

			await page.goto(liveWidgetPageURL);

			await expect(getDocumentCard(folderDocumentTitle)).toBeVisible();

			await depotStagingPage.publishSiteToLive(stagedWidgetPageURL);

			await page.goto(liveWidgetPageURL);

			await expect(getDocumentCard(subfolderDocumentTitle)).toBeVisible();
			await expect(getDocumentCard(folderDocumentTitle)).toHaveCount(0);
		});

		await test.step('A document deleted in the asset library leaves the widget after publishing', async () => {
			await apiHelpers.headlessDelivery.deleteDocument(
				String(
					await depotStagingPage.getStagedFileEntryId(
						depotFriendlyURL,
						subfolderDocumentTitle,
						[folder.name, subfolder.name]
					)
				)
			);

			await depotStagingPage.publishDepotToLive(depotFriendlyURL);

			await page.goto(stagedWidgetPageURL);

			await expect(getDocumentCard(subfolderDocumentTitle)).toHaveCount(
				0
			);

			await depotStagingPage.publishSiteToLive(stagedWidgetPageURL);

			await page.goto(liveWidgetPageURL);

			await expect(getDocumentCard(subfolderDocumentTitle)).toHaveCount(
				0
			);
		});
	}
);

test(
	'Blogs item selector on a staged site shows asset library images only while the site is connected',
	{tag: '@LPD-112613'},
	async ({
		apiHelpers,
		blogsEditBlogEntryPage,
		depotStagingPage,
		stagedDepotSite,
	}) => {
		test.setTimeout(300000);

		const {assetLibrary, depotFriendlyURL, site, stage} = stagedDepotSite;

		// Add an image to the asset library and to the site, then stage both groups

		const assetLibraryDocumentTitle = `${getRandomString()}.jpg`;

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary.id,
			getImage(),
			{
				fileName: assetLibraryDocumentTitle,
				title: assetLibraryDocumentTitle,
			}
		);

		const siteDocumentTitle = `${getRandomString()}.jpg`;

		await apiHelpers.headlessDelivery.postDocument(site.id, getImage(), {
			documentFolderId: 0,
			fileName: siteDocumentTitle,
			title: siteDocumentTitle,
		});

		await stage();

		await test.step('The Everywhere filter shows the asset library image', async () => {
			const itemSelector = await openSelectFileEverywhere(
				blogsEditBlogEntryPage,
				`${site.friendlyUrlPath}-staging`
			);

			await expect(
				itemSelector.getByText(siteDocumentTitle, {exact: true})
			).toBeVisible();
			await expect(
				itemSelector.getByText(assetLibraryDocumentTitle, {
					exact: true,
				})
			).toBeVisible();
		});

		await test.step('After disconnecting the site and publishing the asset library, the image is gone', async () => {
			await depotStagingPage.disconnectSite(depotFriendlyURL, site.name);

			await depotStagingPage.publishDepotToLive(depotFriendlyURL);

			const itemSelector = await openSelectFileEverywhere(
				blogsEditBlogEntryPage,
				`${site.friendlyUrlPath}-staging`
			);

			await expect(
				itemSelector.getByText(siteDocumentTitle, {exact: true})
			).toBeVisible();
			await expect(
				itemSelector.getByText(assetLibraryDocumentTitle, {
					exact: true,
				})
			).toBeHidden();
		});
	}
);

test(
	'Staged site uses an asset library document type only after its structures are made available',
	{tag: '@LPD-112613'},
	async ({
		depotStagingPage,
		documentLibraryEditDocumentTypesPage,
		documentLibraryEditFilePage,
		documentLibraryPage,
		documentLibraryViewFileEntryPage,
		page,
		stagedDepotSite,
	}) => {
		test.setTimeout(300000);

		const {depotFriendlyURL, site, stage, stagedPageURL} = stagedDepotSite;

		// Stage both groups, then add a document type to the staged asset library and publish it

		await stage();

		const documentTypeName = getRandomString();

		await documentLibraryEditDocumentTypesPage.createNewDLTypeWithTextField(
			documentTypeName,
			false,
			`${depotFriendlyURL}-staging`
		);

		await depotStagingPage.publishDepotToLive(depotFriendlyURL);

		const stagedSiteFriendlyURL = `${site.friendlyUrlPath}-staging`;

		await test.step('The staged site cannot use the document type while structures are unavailable', async () => {
			await documentLibraryPage.goto(stagedSiteFriendlyURL);

			await page.getByRole('button', {exact: true, name: 'New'}).click();

			await expect(
				page.getByRole('menuitem', {name: 'File Upload'})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {name: documentTypeName})
			).toBeHidden();
		});

		const documentTitle = getRandomString();
		const textValue = getRandomString();

		await test.step('After making structures available, a document of that type reaches live', async () => {
			await depotStagingPage.clickConnectedSiteAction(
				depotFriendlyURL,
				site.name,
				'Make Structures Available'
			);

			await documentLibraryEditFilePage.goToNewFileDifferentType(
				documentTypeName,
				stagedSiteFriendlyURL
			);

			await page.getByLabel('Title Required').fill(documentTitle);
			await page.getByLabel('Text', {exact: true}).fill(textValue);

			await documentLibraryEditFilePage.publishButton.click();

			await documentLibraryPage.waitForSuccessAlert();

			await depotStagingPage.publishSiteToLive(stagedPageURL);

			await documentLibraryPage.goto(site.friendlyUrlPath);

			await page
				.getByRole('link', {exact: true, name: documentTitle})
				.click();

			await documentLibraryViewFileEntryPage.openInfoTab();

			const {infoPanel} = documentLibraryViewFileEntryPage;

			await infoPanel
				.getByRole('button', {exact: true, name: documentTypeName})
				.click();

			await expect(
				infoPanel.getByRole('textbox', {exact: true, name: 'Text'})
			).toHaveValue(textValue);
		});
	}
);

test(
	'Staged site sees an asset library document under workflow only after it is approved and published',
	{tag: '@LPD-112613'},
	async ({
		apiHelpers,
		blogsEditBlogEntryPage,
		depotStagingPage,
		page,
		stagedDepotSite,
	}) => {
		test.setTimeout(300000);

		const {assetLibrary, depotFriendlyURL, site, stage, stagedPageURL} =
			stagedDepotSite;

		// Stage both groups and enable Single Approver in the staged asset library

		await stage();

		const stagingGroupId =
			await depotStagingPage.getStagingGroupId(depotFriendlyURL);

		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		const userAccount =
			await apiHelpers.headlessAdminUser.getMyUserAccount();

		const workflowDefinition =
			await apiHelpers.headlessAdminWorkflow.getWorkflowDefinitionByName(
				'Single Approver'
			);

		await apiHelpers.jsonWebServicesWorkflowDefinitionLink.addWorkflowDefinitionLink(
			{
				className: DL_FOLDER_CLASS_NAME,
				classPK: 0,
				companyId,
				groupId: stagingGroupId,
				typePK: -1,
				userId: userAccount.id,
				workflowDefinitionName: workflowDefinition.name,
				workflowDefinitionVersion: workflowDefinition.version,
			}
		);

		// Add a pending image to a folder of the staged asset library and publish it

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			stagingGroupId,
			{name: getRandomString()}
		);

		const documentTitle = `${getRandomString()}.jpg`;

		const document =
			await apiHelpers.headlessDelivery.postDocumentFolderDocument(
				folder.id,
				getImage(),
				{fileName: documentTitle, title: documentTitle}
			);

		await depotStagingPage.publishDepotToLive(depotFriendlyURL);

		const stagedSiteFriendlyURL = `${site.friendlyUrlPath}-staging`;

		await test.step('The pending image is not in the item selector', async () => {
			const itemSelector = await openSelectFileEverywhere(
				blogsEditBlogEntryPage,
				stagedSiteFriendlyURL
			);

			await itemSelector.getByText(folder.name, {exact: true}).click();

			await expect(
				itemSelector.getByText(documentTitle, {exact: true})
			).toBeHidden();
		});

		await test.step('After approving and publishing, the image is in the item selector and Asset Publisher', async () => {
			const fileVersion =
				await apiHelpers.jsonWebServicesDocumentLibrary.getLastestFileVersion(
					String(document.id)
				);

			let workflowTask;

			await expect(async () => {
				workflowTask =
					await apiHelpers.headlessAdminWorkflow.getWorkflowTaskByAsset(
						DL_FILE_ENTRY_CLASS_NAME,
						fileVersion.fileVersionId
					);

				expect(workflowTask).toBeTruthy();
			}).toPass();

			await apiHelpers.headlessAdminWorkflow.postAssignTaskToUser(
				workflowTask.id,
				userAccount.id
			);

			await apiHelpers.headlessAdminWorkflow.postWorkflowTaskChangeTransition(
				workflowTask.id,
				'approve'
			);

			await depotStagingPage.publishDepotToLive(depotFriendlyURL);

			const itemSelector = await openSelectFileEverywhere(
				blogsEditBlogEntryPage,
				stagedSiteFriendlyURL
			);

			await itemSelector.getByText(folder.name, {exact: true}).click();

			await expect(
				itemSelector.getByText(documentTitle, {exact: true})
			).toBeVisible();

			await page.goto(stagedPageURL);

			await depotStagingPage.openAssetPublisherConfiguration();
			await depotStagingPage.selectManualAsset(
				assetLibrary.name,
				documentTitle
			);
			await depotStagingPage.closeConfiguration();

			await page.reload();

			await expect(
				depotStagingPage.assetPublisher.getByText(documentTitle)
			).toBeVisible();
		});
	}
);

test(
	'Blog cover and web content images from an asset library still render on live after their friendly URL changes',
	{tag: '@LPD-112613'},
	async ({apiHelpers, depotStagingPage, page, stagedDepotSite}) => {
		test.setTimeout(300000);

		const {assetLibrary, depotFriendlyURL, site, stage} = stagedDepotSite;

		// Add an image to the asset library and a page with Blogs and Web Content Display, then stage both groups

		const documentTitle = `${getRandomString()}.jpg`;

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary.id,
			getImage(),
			{fileName: documentTitle, title: documentTitle}
		);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
			typeSettings: `layout-template-id=1_column\ncolumn-1=com_liferay_blogs_web_portlet_BlogsPortlet,com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_${getRandomString()}\n`,
		});

		await stage();

		const livePageURL = `/web${site.friendlyUrlPath}${layout.friendlyURL}`;
		const stagedPageURL = `/web${site.friendlyUrlPath}-staging${layout.friendlyURL}`;

		// Use the staged image as a blog cover and inside web content on the staged site

		const stagedFileEntryId = await depotStagingPage.getStagedFileEntryId(
			depotFriendlyURL,
			documentTitle
		);

		const stagedDocument = await apiHelpers.headlessDelivery.getDocument(
			String(stagedFileEntryId)
		);

		const stagingSiteGroupId = await depotStagingPage.getStagingGroupId(
			site.friendlyUrlPath
		);

		const blogHeadline = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(stagingSiteGroupId, {
			headline: blogHeadline,
			image: {imageId: stagedFileEntryId},
		});

		const webContentTitle = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
			groupId: stagingSiteGroupId,
			rawContent: `<p><img data-fileentryid="${stagedFileEntryId}" src="/documents/d${depotFriendlyURL}-staging/${stagedDocument.friendlyUrlPath}" /></p>`,
			titleMap: {en_US: webContentTitle},
		});

		await page.goto(stagedPageURL);

		await depotStagingPage.selectWebContent(webContentTitle);

		await depotStagingPage.publishSiteToLive(stagedPageURL);

		// Change the image friendly URL in the staged asset library and publish both groups

		const friendlyUrlPath = getRandomString();

		await apiHelpers.headlessDelivery.patchDocument({
			document: {friendlyUrlPath},
			documentId: stagedFileEntryId,
		});

		await depotStagingPage.publishDepotToLive(depotFriendlyURL);

		await depotStagingPage.publishSiteToLive(stagedPageURL);

		// The new friendly URL reaches live and both images render on the live page

		const newFriendlyURLResponse = await page.request.get(
			`/documents/d${depotFriendlyURL}/${friendlyUrlPath}`
		);

		expect(newFriendlyURLResponse.ok()).toBe(true);

		await page.goto(livePageURL);

		const webContentImage =
			depotStagingPage.webContentDisplay.locator('img');

		await expect(webContentImage).toBeVisible();

		await expect
			.poll(() =>
				webContentImage.evaluate(
					(image: HTMLImageElement) => image.naturalWidth
				)
			)
			.toBeGreaterThan(0);

		await expect(page.getByText(blogHeadline)).toBeVisible();

		const coverImageURL = await page
			.locator('.cover-image')
			.evaluate(
				(element) =>
					getComputedStyle(element).backgroundImage.match(
						/url\("?(.*?)"?\)/
					)[1]
			);

		const coverImageResponse = await page.request.get(coverImageURL);

		expect(coverImageResponse.ok()).toBe(true);
	}
);

test(
	'Asset Publisher on a staged site still shows an asset library collection image after its friendly URL changes',
	{tag: '@LPD-112613'},
	async ({apiHelpers, depotStagingPage, page, stagedDepotSite}) => {
		test.setTimeout(300000);

		const {
			assetLibrary,
			depotFriendlyURL,
			livePageURL,
			stage,
			stagedPageURL,
		} = stagedDepotSite;

		// Add an image and a dynamic collection to the asset library, then stage both groups

		const documentTitle = `${getRandomString()}.jpg`;

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary.id,
			getImage(),
			{fileName: documentTitle, title: documentTitle}
		);

		const collectionName = getRandomString();

		await apiHelpers.jsonWebServicesAssetListEntry.addDynamicAssetListEntry(
			{
				groupId: String(assetLibrary.siteId),
				title: collectionName,
			}
		);

		await stage();

		// Select the staged collection on the staged page and publish the site

		await page.goto(stagedPageURL);

		await depotStagingPage.openAssetPublisherConfiguration();
		await depotStagingPage.selectCollection(
			assetLibrary.name,
			collectionName
		);
		await depotStagingPage.closeConfiguration();

		await depotStagingPage.publishSiteToLive(stagedPageURL);

		// Change the image friendly URL in the staged asset library and publish both groups

		await apiHelpers.headlessDelivery.patchDocument({
			document: {friendlyUrlPath: getRandomString()},
			documentId: await depotStagingPage.getStagedFileEntryId(
				depotFriendlyURL,
				documentTitle
			),
		});

		await depotStagingPage.publishDepotToLive(depotFriendlyURL);

		await depotStagingPage.publishSiteToLive(stagedPageURL);

		// The live page shows the image through the collection

		await page.goto(livePageURL);

		const {assetPublisher} = depotStagingPage;

		await assetPublisher
			.getByRole('link', {exact: true, name: documentTitle})
			.click();

		const image = assetPublisher.locator('img:visible').first();

		await expect(image).toBeVisible();

		await expect
			.poll(() =>
				image.evaluate(
					(element: HTMLImageElement) => element.naturalWidth
				)
			)
			.toBeGreaterThan(0);
	}
);

test(
	'Asset Publisher on a staged site follows edits and deletion of an asset library document with a custom field',
	{tag: '@LPD-112613'},
	async ({apiHelpers, depotStagingPage, page, stagedDepotSite}) => {
		test.setTimeout(300000);

		const {
			assetLibrary,
			depotFriendlyURL,
			livePageURL,
			stage,
			stagedPageURL,
		} = stagedDepotSite;

		// Add a document custom field

		const customFieldName = getRandomString().replace(/-/g, '');

		await gotoDocumentCustomFields(page);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('link', {name: 'Input Field'}),
			trigger: page.getByRole('link', {name: 'Add Custom Field'}),
		});

		await page.getByText('Field Name Required').fill(customFieldName);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		try {

			// Add an image with a custom field value to the asset library and stage both groups

			const documentTitle = `${getRandomString()}.jpg`;
			const description = getRandomString();

			await apiHelpers.headlessDelivery.postAssetLibraryDocument(
				assetLibrary.id,
				getImage(),
				{
					customFields: [
						{
							customValue: {data: getRandomString()},
							name: customFieldName,
						},
					],
					description,
					fileName: documentTitle,
					title: documentTitle,
				}
			);

			await stage();

			const {assetPublisher} = depotStagingPage;

			await test.step('The selected image reaches live', async () => {
				await page.goto(stagedPageURL);

				await depotStagingPage.openAssetPublisherConfiguration();
				await depotStagingPage.selectManualAsset(
					assetLibrary.name,
					documentTitle
				);
				await depotStagingPage.closeConfiguration();

				await depotStagingPage.publishSiteToLive(stagedPageURL);

				await page.goto(livePageURL);

				await expect(
					assetPublisher.getByText(documentTitle)
				).toBeVisible();
				await expect(
					assetPublisher.getByText(description)
				).toBeVisible();
			});

			const stagedFileEntryId =
				await depotStagingPage.getStagedFileEntryId(
					depotFriendlyURL,
					documentTitle
				);

			const newDocumentTitle = `${getRandomString()}.jpeg`;

			await test.step('A new title and file reach the staged page and then live', async () => {
				await apiHelpers.headlessDelivery.patchDocument({
					document: {
						fileName: newDocumentTitle,
						title: newDocumentTitle,
					},
					documentId: stagedFileEntryId,
					file: createReadStream(
						path.join(
							__dirname,
							'dependencies',
							'Document_Small.jpeg'
						)
					),
				});

				await depotStagingPage.publishDepotToLive(depotFriendlyURL);

				await page.goto(stagedPageURL);

				await expect(
					assetPublisher.getByText(newDocumentTitle)
				).toBeVisible();

				await depotStagingPage.publishSiteToLive(stagedPageURL);

				await page.goto(livePageURL);

				await expect(
					assetPublisher.getByText(newDocumentTitle)
				).toBeVisible();
			});

			await test.step('The deleted document leaves the staged page and then live', async () => {
				await apiHelpers.headlessDelivery.deleteDocument(
					String(stagedFileEntryId)
				);

				await depotStagingPage.publishDepotToLive(depotFriendlyURL);

				await page.goto(stagedPageURL);

				await expect(
					assetPublisher.getByText(newDocumentTitle)
				).toBeHidden();

				await depotStagingPage.publishSiteToLive(stagedPageURL);

				await page.goto(livePageURL);

				await expect(
					assetPublisher.getByText(newDocumentTitle)
				).toBeHidden();
			});
		}
		finally {
			await gotoDocumentCustomFields(page);

			page.once('dialog', (dialog) => dialog.accept());

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('link', {name: 'Delete'}),
				trigger: page
					.getByRole('row')
					.filter({hasText: customFieldName})
					.locator('.dropdown-toggle'),
			});

			await waitForAlert(page);
		}
	}
);

test(
	'Blog cover and web content image field on a staged site follow a replaced and deleted asset library image',
	{tag: '@LPD-112613'},
	async ({apiHelpers, depotStagingPage, page, stagedDepotSite}) => {
		test.setTimeout(300000);

		const {assetLibrary, depotFriendlyURL, site, stage} = stagedDepotSite;

		// Add two images to the asset library and a page with Blogs and Web Content Display, then stage both groups

		const documentTitles = [
			`${getRandomString()}.jpg`,
			`${getRandomString()}.jpg`,
		];

		for (const documentTitle of documentTitles) {
			await apiHelpers.headlessDelivery.postAssetLibraryDocument(
				assetLibrary.id,
				getImage(),
				{fileName: documentTitle, title: documentTitle}
			);
		}

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
			typeSettings: `layout-template-id=1_column\ncolumn-1=com_liferay_blogs_web_portlet_BlogsPortlet,com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_${getRandomString()}\n`,
		});

		await stage();

		const livePageURL = `/web${site.friendlyUrlPath}${layout.friendlyURL}`;
		const stagedPageURL = `/web${site.friendlyUrlPath}-staging${layout.friendlyURL}`;

		const stagedFileEntryIds = [];

		for (const documentTitle of documentTitles) {
			stagedFileEntryIds.push(
				await depotStagingPage.getStagedFileEntryId(
					depotFriendlyURL,
					documentTitle
				)
			);
		}

		const stagingSiteGroupId = await depotStagingPage.getStagingGroupId(
			site.friendlyUrlPath
		);

		const getCoverImageURL = () =>
			page
				.locator('.cover-image')
				.evaluate(
					(element) =>
						getComputedStyle(element).backgroundImage.match(
							/url\("?(.*?)"?\)/
						)[1]
				);

		const expectWebContentImage = async (documentTitle: string) => {
			await expect(depotStagingPage.webContentDisplay).toContainText(
				documentTitle
			);

			const webContentImageURL = (
				await depotStagingPage.webContentDisplay.textContent()
			).match(/\/documents\/\S+/)[0];

			const response = await page.request.get(webContentImageURL);

			expect(response.ok()).toBe(true);
		};

		// Use the first image as a blog cover and in a web content image field

		const blog = await apiHelpers.headlessDelivery.postBlog(
			stagingSiteGroupId,
			{image: {imageId: stagedFileEntryIds[0]}}
		);

		const structure = await apiHelpers.dataEngine.createStructure(
			String(stagingSiteGroupId),
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{fieldType: 'image', name: 'Image'}],
				name: getRandomString(),
			})
		);

		const webContentTitle = getRandomString();

		const webContent =
			await apiHelpers.headlessDelivery.postStructuredContent({
				contentFields: [
					{
						contentFieldValue: {image: {id: stagedFileEntryIds[0]}},
						name: 'Image',
					},
				],
				contentStructureId: Number(structure.id),
				datePublished: new Date().toISOString(),
				siteId: String(stagingSiteGroupId),
				title: webContentTitle,
			});

		await page.goto(stagedPageURL);

		await depotStagingPage.selectWebContent(webContentTitle);

		await test.step('Live shows the first image in both', async () => {
			await depotStagingPage.publishSiteToLive(stagedPageURL);

			await page.goto(livePageURL);

			expect(await getCoverImageURL()).toContain(documentTitles[0]);

			await expectWebContentImage(documentTitles[0]);
		});

		await test.step('Live shows the second image in both after replacing it', async () => {
			await apiHelpers.headlessDelivery.putBlog(blog.id, {
				headline: blog.headline,
				image: {imageId: stagedFileEntryIds[1]},
			});

			await apiHelpers.put(
				`${apiHelpers.baseUrl}headless-delivery/v1.0/structured-contents/${webContent.id}`,
				{
					data: {
						contentFields: [
							{
								contentFieldValue: {
									image: {id: stagedFileEntryIds[1]},
								},
								name: 'Image',
							},
						],
						contentStructureId: structure.id,
						title: webContentTitle,
					},
					failOnStatusCode: true,
				}
			);

			await depotStagingPage.publishSiteToLive(stagedPageURL);

			await page.goto(livePageURL);

			expect(await getCoverImageURL()).toContain(documentTitles[1]);

			await expectWebContentImage(documentTitles[1]);
		});

		await test.step('Live web content drops the second image after it is deleted', async () => {
			await apiHelpers.headlessDelivery.deleteDocument(
				String(stagedFileEntryIds[1])
			);

			await depotStagingPage.publishDepotToLive(depotFriendlyURL);

			await depotStagingPage.publishSiteToLive(stagedPageURL);

			await page.goto(livePageURL);

			await expect(depotStagingPage.webContentDisplay).toBeVisible();

			await expect(depotStagingPage.webContentDisplay).not.toContainText(
				documentTitles[1]
			);
		});
	}
);

test(
	'Open Graph image of a staged page drops an asset library image after it is deleted',
	{tag: '@LPD-112613'},
	async ({
		apiHelpers,
		depotStagingPage,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		stagedDepotSite,
	}) => {
		test.setTimeout(300000);

		const {assetLibrary, depotFriendlyURL, site, stage} = stagedDepotSite;

		// Add an image to the asset library and a page, then stage both groups

		const documentTitle = `${getRandomString()}.jpg`;

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary.id,
			getImage(),
			{fileName: documentTitle, title: documentTitle}
		);

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: layoutTitle,
		});

		await stage();

		const stagedPageURL = `/web${site.friendlyUrlPath}-staging${layout.friendlyURL}`;

		const openGraphImage = page.getByRole('textbox', {
			exact: true,
			name: 'Image',
		});

		const gotoStagedOpenGraph = async () => {
			await pagesAdminPage.goto(`${site.friendlyUrlPath}-staging`);

			await pageConfigurationPage.goToSection(layoutTitle, 'Open Graph');
		};

		const gotoLiveOpenGraph = async () => {
			const namespace =
				'_com_liferay_layout_admin_web_portlet_GroupPagesPortlet_';

			await page.goto(
				`/group${site.friendlyUrlPath}/~/control_panel/manage?p_p_id=com_liferay_layout_admin_web_portlet_GroupPagesPortlet&${namespace}mvcRenderCommandName=%2Flayout_admin%2Fedit_layout&p_r_p_selPlid=${layout.plid}&${namespace}screenNavigationEntryKey=open-graph`
			);
		};

		await test.step('The asset library image is the Open Graph image on live', async () => {
			await gotoStagedOpenGraph();

			const selectImageFrame = page.frameLocator(
				'iframe[title="Select Image"]'
			);

			const sitesAndLibrariesLink = selectImageFrame.getByRole('link', {
				exact: true,
				name: 'Sites and Libraries',
			});

			await clickAndExpectToBeVisible({
				target: sitesAndLibrariesLink,
				trigger: page.getByRole('button', {name: 'Select Image'}),
			});

			await sitesAndLibrariesLink.click();
			await selectImageFrame
				.getByText('Asset Library', {exact: true})
				.click();
			await selectImageFrame
				.getByText(assetLibrary.name, {exact: true})
				.click();
			await selectImageFrame
				.getByText(documentTitle, {exact: true})
				.click();

			await pageConfigurationPage.save();

			await depotStagingPage.publishSiteToLive(stagedPageURL);

			await gotoLiveOpenGraph();

			await expect(openGraphImage).toHaveValue(documentTitle);
		});

		await test.step('The deleted image leaves the Open Graph image on staging and live', async () => {
			await apiHelpers.headlessDelivery.deleteDocument(
				String(
					await depotStagingPage.getStagedFileEntryId(
						depotFriendlyURL,
						documentTitle
					)
				)
			);

			await depotStagingPage.publishDepotToLive(depotFriendlyURL);

			await gotoStagedOpenGraph();

			await expect(openGraphImage).toHaveValue('');

			await depotStagingPage.publishSiteToLive(stagedPageURL);

			await gotoLiveOpenGraph();

			await expect(openGraphImage).toHaveValue('');
		});
	}
);

test(
	'Documents and Media widget on a staged site follows a root folder moved to another asset library',
	{tag: '@LPD-112613'},
	async ({apiHelpers, depotStagingPage, page, stagedDepotSite}) => {
		test.setTimeout(300000);

		const {assetLibrary, site, stage} = stagedDepotSite;

		// Add a second connected asset library, a folder with an image in each library and a Documents and Media widget page, then stage every group

		const assetLibrary2 =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		await apiHelpers.headlessAssetLibrary.connectSite(
			assetLibrary2.externalReferenceCode,
			site.externalReferenceCode
		);

		const folders = [];
		const documentTitles = [];

		for (const {siteId} of [assetLibrary, assetLibrary2]) {
			const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
				siteId,
				{name: getRandomString()}
			);

			const documentTitle = `${getRandomString()}.jpg`;

			await apiHelpers.headlessDelivery.postDocumentFolderDocument(
				folder.id,
				getImage(),
				{fileName: documentTitle, title: documentTitle}
			);

			folders.push(folder);
			documentTitles.push(documentTitle);
		}

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
			typeSettings:
				'layout-template-id=1_column\ncolumn-1=com_liferay_document_library_web_portlet_DLPortlet\n',
		});

		await stage({assetLibraries: [assetLibrary2]});

		const liveWidgetPageURL = `/web${site.friendlyUrlPath}${layout.friendlyURL}`;
		const stagedWidgetPageURL = `/web${site.friendlyUrlPath}-staging${layout.friendlyURL}`;

		const {documentsAndMedia} = depotStagingPage;

		const getDocumentCard = (title: string) =>
			documentsAndMedia.locator('.card').filter({hasText: title});

		// Publish the widget with a root folder in each library in turn

		for (const [index, library] of [
			assetLibrary,
			assetLibrary2,
		].entries()) {
			await page.goto(stagedWidgetPageURL);

			await depotStagingPage.setDocumentsAndMediaRootFolder(
				library.name,
				folders[index].name
			);

			await depotStagingPage.publishSiteToLive(stagedWidgetPageURL);

			await page.goto(liveWidgetPageURL);

			await expect(getDocumentCard(documentTitles[index])).toBeVisible();
			await expect(
				getDocumentCard(documentTitles[1 - index])
			).toHaveCount(0);
		}
	}
);

test(
	'Asset Publisher on a staged site shows an asset library external video thumbnail, also after its friendly URL changes',
	{tag: '@LPD-112613'},
	async ({
		apiHelpers,
		depotStagingPage,
		documentLibraryEditFilePage,
		page,
		stagedDepotSite,
	}) => {
		test.setTimeout(300000);

		const {
			assetLibrary,
			depotFriendlyURL,
			livePageURL,
			stage,
			stagedPageURL,
		} = stagedDepotSite;

		await stage();

		// Add an external video shortcut to the staged asset library and publish it

		await depotStagingPage.gotoDocuments(`${depotFriendlyURL}-staging`);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'External Video Shortcut',
			}),
			trigger: page.getByRole('button', {exact: true, name: 'New'}),
		});

		await page
			.getByLabel('Video URL')
			.fill('https://www.youtube.com/watch?v=HOdbzGCI5ME');

		await expect(page.getByLabel('Title')).not.toHaveValue('');

		const videoTitle = getRandomString();

		await page.getByLabel('Title').fill(videoTitle);

		await documentLibraryEditFilePage.publishFileEntry();

		await depotStagingPage.publishDepotToLive(depotFriendlyURL);

		const {assetPublisher} = depotStagingPage;

		const videoThumbnail = assetPublisher.locator(
			'.aspect-ratio[style*="https://i.ytimg.com"]'
		);

		await test.step('The video thumbnail shows on the staged page and on live', async () => {
			await page.goto(stagedPageURL);

			await depotStagingPage.openAssetPublisherConfiguration();
			await depotStagingPage.selectManualAsset(
				assetLibrary.name,
				videoTitle
			);
			await depotStagingPage.closeConfiguration();

			await page.reload();

			await expect(videoThumbnail).toBeVisible();

			await depotStagingPage.publishSiteToLive(stagedPageURL);

			await page.goto(livePageURL);

			await expect(videoThumbnail).toBeVisible();
		});

		await test.step('The video thumbnail still shows on live after its friendly URL changes', async () => {
			await apiHelpers.headlessDelivery.patchDocument({
				document: {friendlyUrlPath: getRandomString()},
				documentId: await depotStagingPage.getStagedFileEntryId(
					depotFriendlyURL,
					videoTitle
				),
			});

			await depotStagingPage.publishDepotToLive(depotFriendlyURL);

			await depotStagingPage.publishSiteToLive(stagedPageURL);

			await page.goto(livePageURL);

			await expect(videoThumbnail).toBeVisible();
		});
	}
);
