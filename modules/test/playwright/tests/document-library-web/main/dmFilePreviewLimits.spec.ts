/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {filePreviewLimitsPagesTest} from './fixtures/filePreviewLimitsPagesTest';

const test = mergeTests(
	filePreviewLimitsPagesTest,
	isolatedSiteTest,
	loginTest()
);

const MAX_FILE_SIZE_LABEL = 'Maximum File Size';
const MAX_NUMBER_OF_PAGES_LABEL = 'Maximum Number of Pages';

// The default system maximum file size. Random sizes are kept below it so that
// every scope accepts them without hitting the cross-scope limit.

const PREVIEWABLE_PROCESSOR_MAX_SIZE_DEFAULT = 20971520;

test.describe('system scope', () => {
	test.beforeEach(async ({filePreviewLimitsSystemSettingsPage}) => {
		await filePreviewLimitsSystemSettingsPage.goto();
	});

	test.afterEach(async ({filePreviewLimitsSystemSettingsPage}) => {
		await filePreviewLimitsSystemSettingsPage.goto();

		await filePreviewLimitsSystemSettingsPage.resetToDefaultValues();
	});

	test(
		'Persists file preview limit configuration changes',
		{tag: '@LPD-93202'},
		async ({filePreviewLimitsSystemSettingsPage, page}) => {
			const newMaxFileSize = String(
				getRandomInt() % PREVIEWABLE_PROCESSOR_MAX_SIZE_DEFAULT
			);

			await page.getByLabel(MAX_FILE_SIZE_LABEL).fill(newMaxFileSize);
			await page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL).fill('2');

			await filePreviewLimitsSystemSettingsPage.save();

			await filePreviewLimitsSystemSettingsPage.goto();

			await expect(page.getByLabel(MAX_FILE_SIZE_LABEL)).toHaveValue(
				newMaxFileSize
			);
			await expect(
				page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
			).toHaveValue('2');
		}
	);
});

test.describe('instance scope', () => {
	test.beforeEach(async ({filePreviewLimitsInstanceSettingsPage}) => {
		await filePreviewLimitsInstanceSettingsPage.goto();
	});

	test.afterEach(async ({filePreviewLimitsInstanceSettingsPage}) => {
		await filePreviewLimitsInstanceSettingsPage.goto();

		await filePreviewLimitsInstanceSettingsPage.resetToDefaultValues();
	});

	test(
		'Persists file preview limit configuration changes',
		{tag: '@LPD-93202'},
		async ({filePreviewLimitsInstanceSettingsPage, page}) => {
			const newMaxFileSize = String(
				getRandomInt() % PREVIEWABLE_PROCESSOR_MAX_SIZE_DEFAULT
			);

			await page.getByLabel(MAX_FILE_SIZE_LABEL).fill(newMaxFileSize);
			await page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL).fill('2');

			await filePreviewLimitsInstanceSettingsPage.save();

			await filePreviewLimitsInstanceSettingsPage.goto();

			await expect(page.getByLabel(MAX_FILE_SIZE_LABEL)).toHaveValue(
				newMaxFileSize
			);
			await expect(
				page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
			).toHaveValue('2');
		}
	);
});

test.describe('site scope', () => {
	test(
		'Persists file preview limit configuration changes',
		{tag: '@LPD-93202'},
		async ({filePreviewLimitsSiteSettingsPage, page, site}) => {
			const newMaxFileSize = String(
				getRandomInt() % PREVIEWABLE_PROCESSOR_MAX_SIZE_DEFAULT
			);

			await filePreviewLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

			await page.getByLabel(MAX_FILE_SIZE_LABEL).fill(newMaxFileSize);
			await page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL).fill('2');

			await filePreviewLimitsSiteSettingsPage.save();

			await filePreviewLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

			await expect(page.getByLabel(MAX_FILE_SIZE_LABEL)).toHaveValue(
				newMaxFileSize
			);
			await expect(
				page.getByLabel(MAX_NUMBER_OF_PAGES_LABEL)
			).toHaveValue('2');
		}
	);
});

test.describe('cross-scope validation', () => {
	test.afterEach(async ({filePreviewLimitsSystemSettingsPage}) => {
		await filePreviewLimitsSystemSettingsPage.goto();

		await filePreviewLimitsSystemSettingsPage.resetToDefaultValues();
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
			const systemMaxFileSize =
				getRandomInt() % PREVIEWABLE_PROCESSOR_MAX_SIZE_DEFAULT;

			// Establish a finite system limit to cap the instance scope

			await filePreviewLimitsSystemSettingsPage.goto();

			await page
				.getByLabel(MAX_FILE_SIZE_LABEL)
				.fill(String(systemMaxFileSize));

			await filePreviewLimitsSystemSettingsPage.save();

			// Attempt to exceed the system limit at the instance scope

			await filePreviewLimitsInstanceSettingsPage.goto();

			await page
				.getByLabel(MAX_FILE_SIZE_LABEL)
				.fill(String(systemMaxFileSize + (getRandomInt() % 1000) + 1));

			await filePreviewLimitsInstanceSettingsPage.saveButton.click();

			await expect(
				page.getByText('Maximum file size limit is invalid.')
			).toBeVisible();
		}
	);
});
