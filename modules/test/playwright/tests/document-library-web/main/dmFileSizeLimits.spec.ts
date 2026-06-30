/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {dmSettingsPagesTest} from './fixtures/dmSettingsPagesTest';

const test = mergeTests(dmSettingsPagesTest, isolatedSiteTest, loginTest());

const FILE_MAX_SIZE_LABEL = 'Maximum File Upload Size';
const MAX_SIZE_TO_COPY_LABEL = 'Size Limit for Copying Files';

test.describe('system scope', () => {
	test.beforeEach(async ({fileSizeLimitsSystemSettingsPage}) => {
		await fileSizeLimitsSystemSettingsPage.goto();
	});

	test.afterEach(async ({fileSizeLimitsSystemSettingsPage}) => {
		await fileSizeLimitsSystemSettingsPage.goto();

		await fileSizeLimitsSystemSettingsPage.resetToDefaultValues();
	});

	test(
		'Persists document library size limit configuration changes',
		{tag: '@LPD-93201'},
		async ({fileSizeLimitsSystemSettingsPage, page}) => {
			const newFileMaxSize = String(getRandomInt());
			const newMaxSizeToCopy = String(getRandomInt());

			await test.step('Change the configuration values', async () => {
				await page.getByLabel(FILE_MAX_SIZE_LABEL).fill(newFileMaxSize);
				await page
					.getByLabel(MAX_SIZE_TO_COPY_LABEL)
					.fill(newMaxSizeToCopy);

				await fileSizeLimitsSystemSettingsPage.save();
			});

			await test.step('Assert the values persisted', async () => {
				await fileSizeLimitsSystemSettingsPage.goto();

				await expect(page.getByLabel(FILE_MAX_SIZE_LABEL)).toHaveValue(
					newFileMaxSize
				);
				await expect(
					page.getByLabel(MAX_SIZE_TO_COPY_LABEL)
				).toHaveValue(newMaxSizeToCopy);
			});
		}
	);
});

test.describe('instance scope', () => {
	test.beforeEach(async ({fileSizeLimitsInstanceSettingsPage}) => {
		await fileSizeLimitsInstanceSettingsPage.goto();
	});

	test.afterEach(async ({fileSizeLimitsInstanceSettingsPage}) => {
		await fileSizeLimitsInstanceSettingsPage.goto();

		await fileSizeLimitsInstanceSettingsPage.resetToDefaultValues();
	});

	test(
		'Persists document library size limit configuration changes',
		{tag: '@LPD-93201'},
		async ({fileSizeLimitsInstanceSettingsPage, page}) => {
			const newFileMaxSize = String(getRandomInt());
			const newMaxSizeToCopy = String(getRandomInt());

			await test.step('Change the configuration values', async () => {
				await page.getByLabel(FILE_MAX_SIZE_LABEL).fill(newFileMaxSize);
				await page
					.getByLabel(MAX_SIZE_TO_COPY_LABEL)
					.fill(newMaxSizeToCopy);

				await fileSizeLimitsInstanceSettingsPage.save();
			});

			await test.step('Assert the values persisted', async () => {
				await fileSizeLimitsInstanceSettingsPage.goto();

				await expect(page.getByLabel(FILE_MAX_SIZE_LABEL)).toHaveValue(
					newFileMaxSize
				);
				await expect(
					page.getByLabel(MAX_SIZE_TO_COPY_LABEL)
				).toHaveValue(newMaxSizeToCopy);
			});
		}
	);
});

test.describe('site scope', () => {
	test(
		'Persists document library size limit configuration changes',
		{tag: '@LPD-93201'},
		async ({fileSizeLimitsSiteSettingsPage, page, site}) => {
			const newFileMaxSize = String(getRandomInt());
			const newMaxSizeToCopy = String(getRandomInt());

			await test.step('Change the configuration values', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await page.getByLabel(FILE_MAX_SIZE_LABEL).fill(newFileMaxSize);
				await page
					.getByLabel(MAX_SIZE_TO_COPY_LABEL)
					.fill(newMaxSizeToCopy);

				await fileSizeLimitsSiteSettingsPage.save();
			});

			await test.step('Assert the values persisted', async () => {
				await fileSizeLimitsSiteSettingsPage.goto(site.friendlyUrlPath);

				await expect(page.getByLabel(FILE_MAX_SIZE_LABEL)).toHaveValue(
					newFileMaxSize
				);
				await expect(
					page.getByLabel(MAX_SIZE_TO_COPY_LABEL)
				).toHaveValue(newMaxSizeToCopy);
			});
		}
	);
});
