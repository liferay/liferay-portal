/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DocumentLibraryEditFilePage} from '../../../pages/document-library-web/DocumentLibraryEditFilePage';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';
import {FileSizeMimetypesForm} from '../../document-library-web/main/pages/FileSizeMimetypesForm';
import addEditableDepot from './utils/addEditableDepot';

const test = mergeTests(dataApiHelpersTest, loginTest());

const DEPOT_ADMIN_NAMESPACE =
	'_com_liferay_depot_web_portlet_DepotAdminPortlet_';

const SIZE_LIMIT_ERROR =
	'Please enter a file with a valid file size no larger than 10 KB';

async function expectUploadAccepted(
	documentLibraryEditFilePage: DocumentLibraryEditFilePage,
	page: Page,
	depotURL: string,
	fileName: string
) {
	await uploadFile(documentLibraryEditFilePage, page, depotURL, fileName);

	await documentLibraryEditFilePage.publishFileEntry();
}

async function expectUploadRejected(
	documentLibraryEditFilePage: DocumentLibraryEditFilePage,
	page: Page,
	depotURL: string,
	fileName: string,
	{publish}: {publish: boolean}
) {
	await uploadFile(documentLibraryEditFilePage, page, depotURL, fileName);

	if (publish) {
		await documentLibraryEditFilePage.publishButton.click();
	}

	await expect(page.getByText(SIZE_LIMIT_ERROR)).toBeVisible();
}

async function uploadFile(
	documentLibraryEditFilePage: DocumentLibraryEditFilePage,
	page: Page,
	depotURL: string,
	fileName: string
) {
	await documentLibraryEditFilePage.goto(depotURL);

	await page
		.locator('input[type="file"]')
		.setInputFiles(path.join(__dirname, 'dependencies', fileName));
}

test(
	'A depot enforces its mime type size limits only on its own uploads',
	{tag: '@LPD-109572'},
	async ({apiHelpers, page}) => {
		const depot = await addEditableDepot(apiHelpers);

		const anotherAssetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: getRandomString(),
				type: 'AssetLibrary',
			});

		await test.step('Set limits for image/jpeg and application/*', async () => {
			await page.goto(
				`/group/guest${PORTLET_URLS.depotAdmin}&${DEPOT_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fdepot%2Fedit_depot_entry&${DEPOT_ADMIN_NAMESPACE}depotEntryId=${depot.depotEntryId}&${DEPOT_ADMIN_NAMESPACE}screenNavigationEntryKey=general`
			);

			const fileSizeMimetypesForm = new FileSizeMimetypesForm(page);

			await fileSizeMimetypesForm.fillRow(0, 'image/jpeg', '10240');

			await fileSizeMimetypesForm.addRow();

			await fileSizeMimetypesForm.fillRow(1, 'application/*', '10240');

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(page);
		});

		const depotURL = `/asset-library-${depot.depotEntryId}`;
		const documentLibraryEditFilePage = new DocumentLibraryEditFilePage(
			page
		);

		await test.step('The depot rejects the files over the limits', async () => {
			await expectUploadRejected(
				documentLibraryEditFilePage,
				page,
				depotURL,
				'Document.jpg',
				{publish: false}
			);

			// The upload form only checks exact mime types, so the server
			// enforces the application/* limit on publish

			await expectUploadRejected(
				documentLibraryEditFilePage,
				page,
				depotURL,
				'Document_Large.pdf',
				{publish: true}
			);
		});

		await test.step('The depot accepts the files under the limits', async () => {
			await expectUploadAccepted(
				documentLibraryEditFilePage,
				page,
				depotURL,
				'Document_Small.jpeg'
			);

			await expectUploadAccepted(
				documentLibraryEditFilePage,
				page,
				depotURL,
				'Document.pdf'
			);
		});

		await test.step('Another depot accepts the file over the limit', async () => {
			await expectUploadAccepted(
				documentLibraryEditFilePage,
				page,
				`/asset-library-${anotherAssetLibrary.id}`,
				'Document.jpg'
			);
		});
	}
);
