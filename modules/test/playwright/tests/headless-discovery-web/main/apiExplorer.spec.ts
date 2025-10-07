/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {headlessDiscoveryPagesTest} from './fixtures/headlessDiscoveryPagesTest';

export const test = mergeTests(headlessDiscoveryPagesTest, loginTest());

test(
	'Opens help popover and displays Filterable Fields copy button',
	{tag: '@LPD-54844'},
	async ({apiExplorer}) => {
		const operationBlock = apiExplorer.getOperationBlock(
			'getSiteBlogPostingsPage'
		);
		const filterRow = await operationBlock.getByRole('row', {
			name: 'filter',
		});

		await apiExplorer.goto();
		await operationBlock.getByRole('button').click();

		await test.step('Opens the help popover', async () => {
			await filterRow.getByRole('button').click();
			await expect(
				apiExplorer.helpPopover.getByRole('link', {
					name: 'Learn more about filterable fields.',
				})
			).toBeVisible();
		});

		await test.step('Displays the copy button for Filterable Fields', async () => {
			await expect(
				apiExplorer.helpPopover
					.locator('li')
					.filter({hasText: 'creatorId'})
					.getByLabel('Copy to Clipboard')
			).toBeVisible();
		});
	}
);

test(
	'Error mensaje is shown if the endpoint parameter is wrong',
	{tag: '@LPD-59421'},
	async ({page}) => {
		await page.goto('/o/api?endpoint=http://attacker.com/openapi.json');

		await expect(page.getByText(`Forbidden access.`)).toBeVisible({
			timeout: 3000,
		});
	}
);
