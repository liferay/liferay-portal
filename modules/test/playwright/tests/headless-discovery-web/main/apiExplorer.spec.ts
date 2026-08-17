/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
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
		await operationBlock.getByRole('button').first().click();

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

test(
	'Rejects an endpoint that is not a published OpenAPI document',
	{tag: '@LPD-102660'},
	async ({page}) => {
		const {baseUrl} = liferayConfig.environment;

		const forbiddenEndpoints = [
			[
				'A host that only starts with the portal origin',
				`${baseUrl}.attacker.test/openapi.json`,
			],
			[
				'A host smuggled in the user information',
				`${baseUrl}@attacker.test/openapi.json`,
			],
			[
				'A document hosted on the portal itself',
				`${baseUrl}/documents/0/0/attacker/openapi.json`,
			],
		];

		for (const [title, forbiddenEndpoint] of forbiddenEndpoints) {
			await test.step(title, async () => {
				await page.goto(`/o/api?endpoint=${forbiddenEndpoint}`);

				await expect(page.getByText('Forbidden access.')).toBeVisible({
					timeout: 10000,
				});
			});
		}
	}
);

test(
	'Renders an endpoint published by the portal',
	{tag: '@LPD-102660'},
	async ({apiExplorer, page}) => {
		await apiExplorer.goToApplication('headless-delivery/v1.0');

		await expect(page.getByText('Forbidden access.')).toBeHidden();
		await expect(
			apiExplorer.getOperationBlock('getSiteBlogPostingsPage')
		).toBeVisible();
	}
);

test(
	'Sends the CSRF token to an endpoint published by the portal',
	{tag: '@LPD-102660'},
	async ({apiExplorer, page}) => {
		const requestHeaders: Array<Record<string, string>> = [];

		page.on('request', (request) => {
			const {pathname} = new URL(request.url());

			if (pathname.endsWith('/openapi.json')) {
				requestHeaders.push(request.headers());
			}
		});

		await apiExplorer.goToApplication('headless-delivery/v1.0');

		await expect.poll(() => requestHeaders.length).toBeGreaterThan(0);

		expect(requestHeaders[0]['x-csrf-token']).toBeTruthy();
	}
);
