/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {DocumentLibraryEditFilePage} from '../../../pages/document-library-web/DocumentLibraryEditFilePage';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

const DOCUMENT_PATH = path.join(__dirname, 'dependencies/Document.jpg');

async function addDMWidgetPage({
	apiHelpers,
	rootFolderExternalReferenceCode,
	selectedGroupExternalReferenceCode,
	site,
}: {
	apiHelpers: DataApiHelpers;
	rootFolderExternalReferenceCode?: string;
	selectedGroupExternalReferenceCode: string;
	site: Site;
}) {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getWidgetDefinition({
				id: getRandomString(),
				widgetConfig: {
					rootFolderExternalReferenceCode,
					selectedGroupExternalReferenceCode,
					showActions: 'true',
				},
				widgetName:
					'com_liferay_document_library_web_portlet_DLPortlet',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	return `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;
}

async function addConnectedDepot({
	apiHelpers,
	site,
}: {
	apiHelpers: DataApiHelpers;
	site: Site;
}) {
	const assetLibrary =
		await apiHelpers.headlessAssetLibrary.createAssetLibrary();

	await apiHelpers.headlessAssetLibrary.connectSite(
		assetLibrary.externalReferenceCode,
		site.externalReferenceCode
	);

	return assetLibrary;
}

test(
	'Depot folder content can be edited and added from a connected site DM widget until the site is disconnected',
	{tag: '@LPD-109097'},
	async ({apiHelpers, page, site}) => {
		const assetLibrary = await addConnectedDepot({apiHelpers, site});

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			assetLibrary.siteId,
			{name: getRandomString()}
		);

		const subfolder =
			await apiHelpers.headlessDelivery.postDocumentFolderDocumentFolder(
				folder.id
			);

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(DOCUMENT_PATH),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		const pageURL = await addDMWidgetPage({
			apiHelpers,
			rootFolderExternalReferenceCode: folder.externalReferenceCode,
			selectedGroupExternalReferenceCode:
				assetLibrary.externalReferenceCode,
			site,
		});

		const documentLibraryEditFilePage = new DocumentLibraryEditFilePage(
			page
		);
		const documentLibraryPage = new DocumentLibraryPage(page);

		const editedDocumentTitle = getRandomString();

		await test.step('Edit the depot document from the widget', async () => {
			await page.goto(pageURL);

			await documentLibraryPage.goToFileEntryAction(
				'Edit',
				documentTitle
			);

			await documentLibraryEditFilePage.titleSelector.fill(
				editedDocumentTitle
			);

			await documentLibraryEditFilePage.publishFileEntry();

			await page.goto(pageURL);

			await expect(
				page.locator('.card').filter({hasText: editedDocumentTitle})
			).toBeVisible();

			const {items} =
				await apiHelpers.headlessDelivery.getDocumentFolderDocuments(
					folder.id
				);

			expect(items.map(({title}) => title)).toEqual([
				editedDocumentTitle,
			]);
		});

		const newDocumentTitle = getRandomString();

		await test.step('Add a document to the depot subfolder from the widget', async () => {
			await page
				.getByRole('link', {exact: true, name: subfolder.name})
				.click();

			await documentLibraryPage.goToCreateNewFile();

			await page
				.locator('input[type="file"]')
				.setInputFiles(DOCUMENT_PATH);

			await documentLibraryEditFilePage.titleSelector.fill(
				newDocumentTitle
			);

			await documentLibraryEditFilePage.publishFileEntry();

			await expect(
				page.locator('.card').filter({hasText: newDocumentTitle})
			).toBeVisible();

			const {items} =
				await apiHelpers.headlessDelivery.getDocumentFolderDocuments(
					subfolder.id
				);

			expect(items.map(({title}) => title)).toEqual([newDocumentTitle]);
		});

		await test.step('The widget loses the depot folder once the site is disconnected', async () => {
			await apiHelpers.headlessAssetLibrary.disconnectSite(
				assetLibrary.externalReferenceCode,
				site.externalReferenceCode
			);

			await page.goto(pageURL);

			await expect(
				page.getByText('The folder could not be found')
			).toBeVisible();

			await expect(
				page.locator('.card').filter({hasText: editedDocumentTitle})
			).toHaveCount(0);
		});
	}
);

test(
	'Depot content can be deleted from a connected site DM widget',
	{tag: '@LPD-109097'},
	async ({apiHelpers, page, site}) => {
		const assetLibrary = await addConnectedDepot({apiHelpers, site});

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			assetLibrary.siteId
		);

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary.id,
			createReadStream(DOCUMENT_PATH),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		const pageURL = await addDMWidgetPage({
			apiHelpers,
			selectedGroupExternalReferenceCode:
				assetLibrary.externalReferenceCode,
			site,
		});

		const documentLibraryPage = new DocumentLibraryPage(page);

		await page.goto(pageURL);

		await documentLibraryPage.moveToRecycleBin(documentTitle);

		await documentLibraryPage.goToFolderAction('Delete', folder.name);

		await waitForAlert(page, 'was moved to the Recycle Bin');

		await page.goto(pageURL);

		await expect(
			page.getByText(
				'There are no documents or media files in this folder.'
			)
		).toBeVisible();
	}
);
