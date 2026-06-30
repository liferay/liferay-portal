/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DocumentLibraryEditFilePage} from '../../../pages/document-library-web/DocumentLibraryEditFilePage';
import {createSizedFile} from '../../../utils/createSizedFile';
import {dmSettingsPagesTest} from './fixtures/dmSettingsPagesTest';

const test = mergeTests(dmSettingsPagesTest, isolatedSiteTest, loginTest());

const SIZE_LIMIT_ERROR = 'Please enter a file with a valid file size';

test.describe('upload enforcement', () => {
	test(
		'Enforces a mime type size limit when uploading to a site',
		{tag: '@LPD-93201'},
		async ({fileSizeLimitsSiteSettingsPage, page, site}) => {
			await test.step('Set a 10 KB limit for image/png', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'image/png',
					'10240'
				);

				await fileSizeLimitsSiteSettingsPage.save();
			});

			const documentLibraryEditFilePage = new DocumentLibraryEditFilePage(
				page
			);

			await test.step('Reject a file over the limit', async () => {
				await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

				await page
					.locator('input[type="file"]')
					.setInputFiles(
						createSizedFile('over-limit.png', 'png', 20480)
					);

				await expect(page.getByText(SIZE_LIMIT_ERROR)).toBeVisible();
			});

			await test.step('Accept a file under the limit', async () => {
				await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

				await page
					.locator('input[type="file"]')
					.setInputFiles(
						createSizedFile('under-limit.png', 'png', 5120)
					);

				await expect(
					page.getByTestId('uploadedFileName')
				).toBeVisible();
				await expect(page.getByText(SIZE_LIMIT_ERROR)).toBeHidden();
			});
		}
	);

	test(
		'Enforces independent limits for multiple mime types',
		{tag: '@LPD-93201'},
		async ({fileSizeLimitsSiteSettingsPage, page, site}) => {
			await test.step('Set limits for image/jpeg and application/pdf', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'image/jpeg',
					'40960'
				);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.addRow();

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					1,
					'application/pdf',
					'30720'
				);

				await fileSizeLimitsSiteSettingsPage.save();
			});

			const documentLibraryEditFilePage = new DocumentLibraryEditFilePage(
				page
			);

			await test.step('Reject a JPEG over its own limit', async () => {
				await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

				await page
					.locator('input[type="file"]')
					.setInputFiles(
						createSizedFile('over-limit.jpeg', 'jpeg', 51200)
					);

				await expect(page.getByText(SIZE_LIMIT_ERROR)).toBeVisible();
			});

			await test.step('Reject a PDF over its own limit', async () => {
				await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

				await page
					.locator('input[type="file"]')
					.setInputFiles(
						createSizedFile('over-limit.pdf', 'pdf', 40960)
					);

				await expect(page.getByText(SIZE_LIMIT_ERROR)).toBeVisible();
			});

			await test.step('Accept a PDF under its own limit', async () => {
				await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

				await page
					.locator('input[type="file"]')
					.setInputFiles(
						createSizedFile('under-limit.pdf', 'pdf', 10240)
					);

				await expect(
					page.getByTestId('uploadedFileName')
				).toBeVisible();
			});
		}
	);

	test(
		'Applies a site mime type size limit only to that site',
		{tag: '@LPD-93201'},
		async ({fileSizeLimitsSiteSettingsPage, page, site}) => {
			await test.step('Set a 10 KB limit for image/png on the site', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'image/png',
					'10240'
				);

				await fileSizeLimitsSiteSettingsPage.save();
			});

			const documentLibraryEditFilePage = new DocumentLibraryEditFilePage(
				page
			);

			await test.step('Reject the file on the limited site', async () => {
				await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

				await page
					.locator('input[type="file"]')
					.setInputFiles(
						createSizedFile('site-over-limit.png', 'png', 20480)
					);

				await expect(page.getByText(SIZE_LIMIT_ERROR)).toBeVisible();
			});

			await test.step('Accept the same file on another site', async () => {
				await documentLibraryEditFilePage.goto();

				await page
					.locator('input[type="file"]')
					.setInputFiles(
						createSizedFile('guest-over-limit.png', 'png', 20480)
					);

				await expect(
					page.getByTestId('uploadedFileName')
				).toBeVisible();
				await expect(page.getByText(SIZE_LIMIT_ERROR)).toBeHidden();
			});
		}
	);
});
