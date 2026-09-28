/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {notificationsPagesTest} from '../../../fixtures/notificationsPagesTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	loginTest(),
	notificationsPagesTest,
	virtualInstancesPagesTest
);

test(
	'LPD-93373 Adding an instance notifies the user once the instance is ready',
	{tag: '@LPD-93373'},
	async ({notificationsPage, virtualInstancesPage}) => {
		test.setTimeout(360000);

		const name = getRandomString();

		await virtualInstancesPage.addNewVirtualInstance(name);

		await notificationsPage.goto();

		await expect(
			notificationsPage.getNotification(
				`The instance ${name} is ready to use.`,
				`The instance ${name} was created.`
			)
		).toBeVisible();

		await virtualInstancesPage.deleteVirtualInstance(name);
	}
);

test(
	'LPD-93373 Adding an instance with a duplicate virtual host notifies the user of the failure',
	{tag: '@LPD-93373'},
	async ({notificationsPage, page}) => {
		test.setTimeout(360000);

		const apiHelpers = new ApiHelpers(page);

		const headlessBatchEngine = apiHelpers.headlessBatchEngine;

		const headlessPortalInstance = apiHelpers.headlessPortalInstance;

		const name = getRandomString();

		const portalInstances =
			await headlessPortalInstance.getVirtualInstances();

		const importTask =
			await headlessPortalInstance.addVirtualInstancesBatch([
				{
					domain: `${name}.com`,
					portalInstanceId: name,
					virtualHost: portalInstances[0].virtualHost,
				},
			]);

		await expect
			.poll(
				async () => {
					const currentImportTask =
						await headlessBatchEngine.getImportTask(importTask.id);

					return currentImportTask.executeStatus;
				},
				{intervals: [1000], timeout: 180 * 1000}
			)
			.toBe('FAILED');

		await notificationsPage.goto();

		await expect(
			notificationsPage.getNotification(
				'Please enter a valid virtual host.',
				`The instance ${name} could not be created.`
			)
		).toBeVisible();
	}
);
