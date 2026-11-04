/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedDXPSyncedChannelTest} from '../../../fixtures/isolatedDXPSyncedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {waitForAlert} from '../../../utils/waitForAlert';
import {faroConfig} from '../main/faro.config';
import {signInToAnalyticsCloud} from '../main/utils/signInToAnalyticsCloud';

const test = mergeTests(
	apiHelpersTest,
	isolatedDXPSyncedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

async function enableEmailReport(page, frequency: string) {
	await page.getByRole('button', {name: 'Configure Email Reports'}).click();

	await page.getByRole('dialog').getByRole('checkbox').click();

	await page.locator('select[name="frequency"]').selectOption(frequency);

	await page.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(page, 'Changes to email reports saved', {
		autoClose: false,
		first: true,
	});
}

test(
	'Cancel after configuring email reports leaves them disabled',
	{tag: '@LRAC-11844'},
	async ({analyticsChannel, page, project}) => {
		await page.goto(
			`${faroConfig.environment.baseUrl}/workspace/${project.groupId}/settings/properties/${analyticsChannel.id}`
		);

		await expect(page.getByText('Email Reports: Disabled')).toBeVisible();

		// Open the Configure Email Reports modal

		await page
			.getByRole('button', {name: 'Configure Email Reports'})
			.click();

		// Enable the toggle and pick Daily

		await page.getByRole('dialog').getByRole('checkbox').click();

		await page.locator('select[name="frequency"]').selectOption('daily');

		// Cancel without saving

		await page.getByRole('button', {name: 'Cancel'}).click();

		// The property status stays Disabled

		await expect(page.getByText('Email Reports: Disabled')).toBeVisible();
	}
);

test(
	'Email report frequency can be set to daily, weekly, or monthly',
	{tag: '@LRAC-11841'},
	async ({analyticsChannel, page, project}) => {
		await page.goto(
			`${faroConfig.environment.baseUrl}/workspace/${project.groupId}/settings/properties/${analyticsChannel.id}`
		);

		// Enable the toggle once; the property keeps its enabled state across
		// frequency changes within the same modal session

		await page
			.getByRole('button', {name: 'Configure Email Reports'})
			.click();

		await page.getByRole('dialog').getByRole('checkbox').click();

		for (const frequency of ['daily', 'weekly', 'monthly']) {
			await page
				.locator('select[name="frequency"]')
				.selectOption(frequency);

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page, 'Changes to email reports saved', {
				autoClose: false,
				first: true,
			});

			await expect(
				page.getByText('Email Reports: Enabled')
			).toBeVisible();

			await page
				.getByRole('button', {name: 'Configure Email Reports'})
				.click();
		}
	}
);

test(
	'Email reports widget appears disabled by default for every user',
	{tag: '@LRAC-11835'},
	async ({analyticsChannel, page, project}) => {
		const propertyUrl = `${faroConfig.environment.baseUrl}/workspace/${project.groupId}/settings/properties/${analyticsChannel.id}`;

		for (const emailAddress of [
			'bryan.cheung@faro.io',
			'michelle.hoshi@faro.io',
			'corbin.murakami@faro.io',
		]) {
			await signInToAnalyticsCloud(page, emailAddress);

			await page.goto(propertyUrl);

			await expect(
				page.getByText('Email Reports: Disabled')
			).toBeVisible();

			await enableEmailReport(page, 'daily');

			await expect(
				page.getByText('Email Reports: Enabled')
			).toBeVisible();
		}

		// Restore the default session for the next test

		await signInToAnalyticsCloud(page, faroConfig.user.login);
	}
);

test(
	'Email report settings of one user do not affect other users',
	{tag: '@LRAC-11847'},
	async ({analyticsChannel, page, project}) => {
		const propertyUrl = `${faroConfig.environment.baseUrl}/workspace/${project.groupId}/settings/properties/${analyticsChannel.id}`;

		// The default user enables daily reports

		await page.goto(propertyUrl);

		await enableEmailReport(page, 'daily');

		await expect(page.getByText('Email Reports: Enabled')).toBeVisible();

		// A different user still sees the property disabled and enables monthly

		await signInToAnalyticsCloud(page, 'corbin.murakami@faro.io');

		await page.goto(propertyUrl);

		await expect(page.getByText('Email Reports: Disabled')).toBeVisible();

		await enableEmailReport(page, 'monthly');

		await expect(page.getByText('Email Reports: Enabled')).toBeVisible();

		// Back to the default user: daily persists, then disable it

		await signInToAnalyticsCloud(page, faroConfig.user.login);

		await page.goto(propertyUrl);

		await page
			.getByRole('button', {name: 'Configure Email Reports'})
			.click();

		await expect(page.locator('select[name="frequency"]')).toHaveValue(
			'daily'
		);

		await page.getByRole('dialog').getByRole('checkbox').click();

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Changes to email reports saved', {
			autoClose: false,
			first: true,
		});

		await expect(page.getByText('Email Reports: Disabled')).toBeVisible();

		// The other user keeps its own monthly setting

		await signInToAnalyticsCloud(page, 'corbin.murakami@faro.io');

		await page.goto(propertyUrl);

		await expect(page.getByText('Email Reports: Enabled')).toBeVisible();

		await page
			.getByRole('button', {name: 'Configure Email Reports'})
			.click();

		await expect(page.locator('select[name="frequency"]')).toHaveValue(
			'monthly'
		);

		// Restore the default session for the next test

		await signInToAnalyticsCloud(page, faroConfig.user.login);
	}
);
