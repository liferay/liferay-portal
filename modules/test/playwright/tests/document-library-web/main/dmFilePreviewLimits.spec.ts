/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {filePreviewLimitsPagesTest} from './fixtures/filePreviewLimitsPagesTest';

const test = mergeTests(
	filePreviewLimitsPagesTest,
	isolatedSiteTest,
	loginTest()
);

const MAX_FILE_SIZE_LABEL = 'Maximum File Size';
const MAX_NUMBER_OF_PAGES_LABEL = 'Maximum Number of Pages';

const DEFAULT_MAX_FILE_SIZE = '20971520';
const DEFAULT_MAX_NUMBER_OF_PAGES = '3';

test.describe('system scope', () => {
	test.beforeEach(async ({filePreviewLimitsSystemSettingsPage}) => {
		await filePreviewLimitsSystemSettingsPage.goto();
	});

	test.afterEach(async ({filePreviewLimitsSystemSettingsPage, page}) => {
		await filePreviewLimitsSystemSettingsPage.goto();

		await page.getByLabel(MAX_FILE_SIZE_LABEL).fill(DEFAULT_MAX_FILE_SIZE);
		await page
			.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
			.fill(DEFAULT_MAX_NUMBER_OF_PAGES);

		await filePreviewLimitsSystemSettingsPage.save();
	});

	test(
		'Persists file preview limit configuration changes',
		{tag: '@LPD-93202'},
		async ({filePreviewLimitsSystemSettingsPage, page}) => {

			// The system scope has no parent, so any value is accepted

			const newMaxFileSize = '30000000';
			const newMaxNumberOfPages = '5';

			await page.getByLabel(MAX_FILE_SIZE_LABEL).fill(newMaxFileSize);
			await page
				.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
				.fill(newMaxNumberOfPages);

			await filePreviewLimitsSystemSettingsPage.save();

			await filePreviewLimitsSystemSettingsPage.goto();

			await expect(page.getByLabel(MAX_FILE_SIZE_LABEL)).toHaveValue(
				newMaxFileSize
			);
			await expect(
				page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
			).toHaveValue(newMaxNumberOfPages);
		}
	);
});

test.describe('instance scope', () => {
	test.beforeEach(async ({filePreviewLimitsInstanceSettingsPage}) => {
		await filePreviewLimitsInstanceSettingsPage.goto();
	});

	test.afterEach(async ({filePreviewLimitsInstanceSettingsPage, page}) => {
		await filePreviewLimitsInstanceSettingsPage.goto();

		await page.getByLabel(MAX_FILE_SIZE_LABEL).fill(DEFAULT_MAX_FILE_SIZE);
		await page
			.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
			.fill(DEFAULT_MAX_NUMBER_OF_PAGES);

		await filePreviewLimitsInstanceSettingsPage.save();
	});

	test(
		'Persists file preview limit configuration changes',
		{tag: '@LPD-93202'},
		async ({filePreviewLimitsInstanceSettingsPage, page}) => {

			// Stay within the system limit so the values are accepted

			const newMaxFileSize = '1000000';
			const newMaxNumberOfPages = '2';

			await page.getByLabel(MAX_FILE_SIZE_LABEL).fill(newMaxFileSize);
			await page
				.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
				.fill(newMaxNumberOfPages);

			await filePreviewLimitsInstanceSettingsPage.save();

			await filePreviewLimitsInstanceSettingsPage.goto();

			await expect(page.getByLabel(MAX_FILE_SIZE_LABEL)).toHaveValue(
				newMaxFileSize
			);
			await expect(
				page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
			).toHaveValue(newMaxNumberOfPages);
		}
	);
});

test.describe('site scope', () => {
	test(
		'Persists file preview limit configuration changes',
		{tag: '@LPD-93202'},
		async ({filePreviewLimitsSiteSettingsPage, page, site}) => {

			// Stay within the system limit so the values are accepted

			const newMaxFileSize = '1000000';
			const newMaxNumberOfPages = '2';

			await filePreviewLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

			await page.getByLabel(MAX_FILE_SIZE_LABEL).fill(newMaxFileSize);
			await page
				.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
				.fill(newMaxNumberOfPages);

			await filePreviewLimitsSiteSettingsPage.save();

			await filePreviewLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

			await expect(page.getByLabel(MAX_FILE_SIZE_LABEL)).toHaveValue(
				newMaxFileSize
			);
			await expect(
				page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
			).toHaveValue(newMaxNumberOfPages);
		}
	);
});

test.describe('cross-scope validation', () => {
	test.afterEach(async ({filePreviewLimitsSystemSettingsPage, page}) => {
		await filePreviewLimitsSystemSettingsPage.goto();

		await page.getByLabel(MAX_FILE_SIZE_LABEL).fill(DEFAULT_MAX_FILE_SIZE);
		await page
			.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
			.fill(DEFAULT_MAX_NUMBER_OF_PAGES);

		await filePreviewLimitsSystemSettingsPage.save();
	});

	test(
		'Rejects a maximum number of pages above the parent limit',
		{tag: '@LPD-93202'},
		async ({
			filePreviewLimitsInstanceSettingsPage,
			filePreviewLimitsSystemSettingsPage,
			page,
		}) => {

			// Establish a finite system limit to cap the instance scope

			await filePreviewLimitsSystemSettingsPage.goto();

			await page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL).fill('5');

			await filePreviewLimitsSystemSettingsPage.save();

			// Attempt to exceed the system limit at the instance scope

			await filePreviewLimitsInstanceSettingsPage.goto();

			await page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL).fill('10');

			await filePreviewLimitsInstanceSettingsPage.saveButton.click();

			await expect(
				page.getByText('Maximum number of pages limit is invalid.')
			).toBeVisible();
		}
	);

	test(
		'Rejects a maximum file size above the parent limit',
		{tag: '@LPD-93202'},
		async ({
			filePreviewLimitsInstanceSettingsPage,
			filePreviewLimitsSystemSettingsPage,
			page,
		}) => {

			// Establish a finite system limit to cap the instance scope

			await filePreviewLimitsSystemSettingsPage.goto();

			await page.getByLabel(MAX_FILE_SIZE_LABEL).fill('1000000');

			await filePreviewLimitsSystemSettingsPage.save();

			// Attempt to exceed the system limit at the instance scope

			await filePreviewLimitsInstanceSettingsPage.goto();

			await page.getByLabel(MAX_FILE_SIZE_LABEL).fill('9000000');

			await filePreviewLimitsInstanceSettingsPage.saveButton.click();

			await expect(
				page.getByText('Maximum file size limit is invalid.')
			).toBeVisible();
		}
	);
});
