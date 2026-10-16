/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {DocumentLibraryEditFilePage} from '../../../pages/document-library-web/DocumentLibraryEditFilePage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import createTempFile from '../../../utils/createTempFile';
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

const DL_ADMIN_NAMESPACE =
	'_com_liferay_document_library_web_portlet_DLAdminPortlet_';

const IMAGE_PATH = path.join(__dirname, 'dependencies/Document.jpg');

const VIDEO_PATH = path.join(__dirname, 'dependencies/Document.mp4');

async function addConnectedDepotFolder({
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

	const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
		assetLibrary.siteId
	);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getWidgetDefinition({
				id: getRandomString(),
				widgetConfig: {
					rootFolderExternalReferenceCode:
						folder.externalReferenceCode,
					selectedGroupExternalReferenceCode:
						assetLibrary.externalReferenceCode,
					showActions: 'true',
				},
				widgetName:
					'com_liferay_document_library_web_portlet_IGDisplayPortlet',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	return {
		assetLibrary,
		folder,
		pageURL: `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`,
	};
}

function getCard(page: Page, title: string) {
	return page.locator('.card').filter({hasText: title});
}

async function openCardAction(page: Page, title: string, action: string) {
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {exact: true, name: action}),
		trigger: getCard(page, title).getByRole('button', {
			name: 'Show Actions',
		}),
	});
}

test(
	'An image can be added to a depot subfolder from a connected site MG widget',
	{tag: '@LPD-109097'},
	async ({apiHelpers, page, site}) => {
		const {assetLibrary, folder, pageURL} = await addConnectedDepotFolder({
			apiHelpers,
			site,
		});

		const subfolder =
			await apiHelpers.headlessDelivery.postDocumentFolderDocumentFolder(
				folder.id
			);

		await page.goto(pageURL);

		await page
			.getByRole('link', {exact: true, name: subfolder.name})
			.click();

		await expect(
			page.getByText('There are no media files in this folder.')
		).toBeVisible();

		const portletId = (
			await page
				.locator(
					'[id^="p_p_id_com_liferay_document_library_web_portlet_IGDisplayPortlet_INSTANCE_"]'
				)
				.getAttribute('id')
		)
			.replace(/^p_p_id_/, '')
			.replace(/_$/, '');

		const imageTitle = getRandomString();

		await test.step('Add the image through the MG add form', async () => {
			await page.goto(
				`/group${site.friendlyUrlPath}/~/control_panel/manage?p_p_id=com_liferay_document_library_web_portlet_DLAdminPortlet&${DL_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fdocument_library%2Fedit_file_entry&${DL_ADMIN_NAMESPACE}cmd=add&${DL_ADMIN_NAMESPACE}redirect=${encodeURIComponent(pageURL)}&${DL_ADMIN_NAMESPACE}portletResource=${portletId}&${DL_ADMIN_NAMESPACE}folderId=${subfolder.id}&${DL_ADMIN_NAMESPACE}repositoryId=${assetLibrary.siteId}`
			);

			const documentLibraryEditFilePage = new DocumentLibraryEditFilePage(
				page
			);

			await page.locator('input[type="file"]').setInputFiles(IMAGE_PATH);

			await documentLibraryEditFilePage.titleSelector.fill(imageTitle);

			await documentLibraryEditFilePage.publishButton.click();

			await waitForAlert(page, undefined, {first: true});
		});

		await page.goto(pageURL);

		await page
			.getByRole('link', {exact: true, name: subfolder.name})
			.click();

		await expect(getCard(page, imageTitle)).toBeVisible();

		const {items} =
			await apiHelpers.headlessDelivery.getDocumentFolderDocuments(
				subfolder.id
			);

		expect(items.map(({title}) => title)).toEqual([imageTitle]);
	}
);

test(
	'A depot image can be edited from a connected site MG widget until the site is disconnected',
	{tag: '@LPD-109097'},
	async ({apiHelpers, page, site}) => {
		const {assetLibrary, folder, pageURL} = await addConnectedDepotFolder({
			apiHelpers,
			site,
		});

		const imageTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(IMAGE_PATH),
			{fileName: `${imageTitle}.jpg`, title: imageTitle}
		);

		const textTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(
				createTempFile(`${getRandomString()}.txt`, 'Text content')
			),
			{fileName: `${textTitle}.txt`, title: textTitle}
		);

		await page.goto(pageURL);

		await expect(getCard(page, imageTitle)).toBeVisible();
		await expect(getCard(page, textTitle)).toHaveCount(0);

		const editedDescription = getRandomString();
		const editedTitle = getRandomString();

		await test.step('Edit the image from the widget', async () => {
			await openCardAction(page, imageTitle, 'Edit');

			const documentLibraryEditFilePage = new DocumentLibraryEditFilePage(
				page
			);

			await documentLibraryEditFilePage.titleSelector.fill(editedTitle);
			await documentLibraryEditFilePage.descriptionInput.fill(
				editedDescription
			);

			await documentLibraryEditFilePage.publishButton.click();

			await waitForAlert(page, undefined, {first: true});

			await page.goto(pageURL);

			await expect(
				getCard(page, `${editedTitle} - ${editedDescription}`)
			).toBeVisible();

			const {items} =
				await apiHelpers.headlessDelivery.getDocumentFolderDocuments(
					folder.id
				);

			expect(items.map(({title}) => title).sort()).toEqual(
				[editedTitle, textTitle].sort()
			);
		});

		await test.step('The widget loses the depot folder once the site is disconnected', async () => {
			await apiHelpers.headlessAssetLibrary.disconnectSite(
				assetLibrary.externalReferenceCode,
				site.externalReferenceCode
			);

			await page.goto(pageURL);

			await expect(
				page.getByText('The folder could not be found.')
			).toBeVisible();

			await expect(getCard(page, editedTitle)).toHaveCount(0);
		});
	}
);

test(
	'A depot video can be moved to the recycle bin from a connected site MG widget',
	{tag: '@LPD-109097'},
	async ({apiHelpers, page, site}) => {
		const {folder, pageURL} = await addConnectedDepotFolder({
			apiHelpers,
			site,
		});

		const videoTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(VIDEO_PATH),
			{fileName: `${videoTitle}.mp4`, title: videoTitle}
		);

		await page.goto(pageURL);

		await openCardAction(page, videoTitle, 'Delete');

		await waitForAlert(page, 'was moved to the Recycle Bin');

		await expect(getCard(page, videoTitle)).toHaveCount(0);

		const {items} =
			await apiHelpers.headlessDelivery.getDocumentFolderDocuments(
				folder.id
			);

		expect(items).toHaveLength(0);
	}
);
