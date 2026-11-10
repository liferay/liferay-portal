/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performAnalyticsCloudLoginViaApi} from '../../../utils/performLogin';
import {faroConfig} from '../main/faro.config';
import {switchChannel} from '../main/utils/channel';

const test = mergeTests(
	apiHelpersTest,
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

test(
	'Property menu reflects the last selected property across relogin and its deletion',
	{tag: ['@LRAC-8972', '@LRAC-8974']},
	async ({apiHelpers, page, project}) => {
		const propertyName = `Test Property ${getRandomString()}`;

		const property = await apiHelpers.jsonWebServicesOSBFaro.createChannel(
			propertyName,
			project.groupId
		);

		try {
			await page.goto(
				`${faroConfig.environment.baseUrl}/workspace/${project.groupId}`
			);

			// Switch to the new property via the sidebar menu

			await switchChannel({channelName: propertyName, page});

			// Sign out and back in, assert the last selected property persists

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Sign Out'}),
				trigger: page.locator('.sticker-circle'),
			});

			await performAnalyticsCloudLoginViaApi(page);

			await page.goto(
				`${faroConfig.environment.baseUrl}/workspace/${project.groupId}`
			);

			await expect(
				page.getByRole('combobox', {name: 'Property'})
			).toContainText(propertyName);

			// Delete the property and assert the sidebar menu no longer lists it

			await apiHelpers.jsonWebServicesOSBFaro.deleteChannel(
				`[${property.id}]`,
				project.groupId
			);

			await page.goto(
				`${faroConfig.environment.baseUrl}/workspace/${project.groupId}`
			);

			await clickAndExpectToBeVisible({
				target: page.getByPlaceholder('Search'),
				trigger: page.getByRole('combobox', {name: 'Property'}),
			});

			await expect(
				page.getByRole('option', {name: propertyName})
			).not.toBeVisible();
		}
		finally {
			await apiHelpers.jsonWebServicesOSBFaro.deleteChannel(
				`[${property.id}]`,
				project.groupId
			);
		}
	}
);

test(
	'Property menu search shows no rows for a non-existent name',
	{tag: '@LRAC-9230'},
	async ({page, project}) => {
		await page.goto(
			`${faroConfig.environment.baseUrl}/workspace/${project.groupId}`
		);

		await clickAndExpectToBeVisible({
			target: page.getByPlaceholder('Search'),
			trigger: page.getByRole('combobox', {name: 'Property'}),
		});

		await page.getByPlaceholder('Search').fill('Non Existent Property');

		await expect(page.getByRole('option')).toHaveCount(0);
	}
);
