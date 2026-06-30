/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {dmSettingsPagesTest} from './fixtures/dmSettingsPagesTest';

const test = mergeTests(dmSettingsPagesTest, isolatedSiteTest, loginTest());

const INVALID_MIME_TYPE_ERROR = 'does not contain a valid mime type name';
const INVALID_NUMBER_ERROR = 'Please enter a valid number.';
const INVALID_SIZE_LIMIT_ERROR = 'does not contain a valid size limit value';

test.describe('configuration form validation', () => {
	test(
		'Does not save a mime type size limit with an empty field',
		{tag: '@LPD-93201'},
		async ({fileSizeLimitsSiteSettingsPage, page, site}) => {
			await test.step('Reject an empty mime type', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'',
					'10240'
				);

				await fileSizeLimitsSiteSettingsPage.saveButton.click();

				await expect(
					page.locator('.alert-danger', {
						hasText: INVALID_MIME_TYPE_ERROR,
					})
				).toBeVisible();
			});

			await test.step('Reject an empty maximum file size', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'image/png',
					''
				);

				await fileSizeLimitsSiteSettingsPage.saveButton.click();

				await expect(
					page.locator('.alert-danger', {
						hasText: INVALID_SIZE_LIMIT_ERROR,
					})
				).toBeVisible();
			});
		}
	);

	test(
		'Does not save a mime type size limit with an invalid value',
		{tag: '@LPD-93201'},
		async ({fileSizeLimitsSiteSettingsPage, page, site}) => {
			await test.step('Reject an invalid mime type', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'invalid',
					'10240'
				);

				await fileSizeLimitsSiteSettingsPage.saveButton.click();

				await expect(
					page.locator('.alert-danger', {
						hasText: INVALID_MIME_TYPE_ERROR,
					})
				).toBeVisible();
			});

			await test.step('Reject a negative maximum file size', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'image/png',
					'-10240'
				);

				await expect(
					page.getByText(INVALID_NUMBER_ERROR)
				).toBeVisible();
			});
		}
	);

	test(
		'Keeps the previous values after a failed update',
		{tag: '@LPD-93201'},
		async ({fileSizeLimitsSiteSettingsPage, page, site}) => {
			await test.step('Save a valid mime type size limit', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'image/png',
					'10240'
				);

				await fileSizeLimitsSiteSettingsPage.save();
			});

			await test.step('Fail to update with an invalid mime type', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'invalid',
					'10240'
				);

				await fileSizeLimitsSiteSettingsPage.saveButton.click();

				await expect(
					page.locator('.alert-danger', {
						hasText: INVALID_MIME_TYPE_ERROR,
					})
				).toBeVisible();
			});

			await test.step('Assert the saved values are intact', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await expect(
					fileSizeLimitsSiteSettingsPage.mimeTypes.mimeTypeInput(0)
				).toHaveValue('image/png');
				await expect(
					fileSizeLimitsSiteSettingsPage.mimeTypes.sizeInput(0)
				).toHaveValue('10240');
			});
		}
	);

	test(
		'Removes a configured mime type size limit',
		{tag: '@LPD-93201'},
		async ({fileSizeLimitsSiteSettingsPage, site}) => {
			await test.step('Save a mime type size limit', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'image/png',
					'10240'
				);

				await fileSizeLimitsSiteSettingsPage.save();
			});

			await test.step('Clear the mime type size limit', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await fileSizeLimitsSiteSettingsPage.mimeTypes.fillRow(
					0,
					'',
					''
				);

				await fileSizeLimitsSiteSettingsPage.save();
			});

			await test.step('Assert the mime type size limit was removed', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await expect(
					fileSizeLimitsSiteSettingsPage.mimeTypes.mimeTypeInput(0)
				).toHaveValue('');
				await expect(
					fileSizeLimitsSiteSettingsPage.mimeTypes.sizeInput(0)
				).toHaveValue('');
			});
		}
	);
});
