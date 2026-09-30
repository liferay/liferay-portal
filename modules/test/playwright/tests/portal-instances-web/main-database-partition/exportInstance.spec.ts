/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {notificationsPagesTest} from '../../../fixtures/notificationsPagesTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	loginTest(),
	notificationsPagesTest,
	virtualInstancesPagesTest
);

test(
	'LPD-92621 Exporting an instance reports the schema it was exported to',
	{tag: '@LPD-92621'},
	async ({virtualInstancesPage}) => {
		test.setTimeout(3 * 180 * 1000);

		const webId = getRandomString();

		let created = false;

		try {
			await virtualInstancesPage.addNewVirtualInstance(webId);

			created = true;

			const schemaName =
				await virtualInstancesPage.exportVirtualInstance(webId);

			expect(schemaName).toMatch(/^lexported_\d+$/);
		}
		finally {
			if (created) {
				await virtualInstancesPage.deleteVirtualInstance(webId);
			}
		}
	}
);

test(
	'LPD-93376 Exporting an instance twice notifies the user of the failure',
	{tag: '@LPD-93376'},
	async ({notificationsPage, virtualInstancesPage}) => {
		test.setTimeout(3 * 180 * 1000);

		const webId = getRandomString();

		let created = false;

		try {
			await virtualInstancesPage.addNewVirtualInstance(webId);

			created = true;

			const schemaName =
				await virtualInstancesPage.exportVirtualInstance(webId);

			await virtualInstancesPage.startVirtualInstanceExport(webId);

			await expect(async () => {
				await notificationsPage.goto();

				await expect(
					notificationsPage.getNotification(
						`The exported schema ${schemaName} already exists.`,
						`The instance ${webId} could not be exported.`
					)
				).toBeVisible({timeout: 10 * 1000});
			}).toPass({timeout: 300 * 1000});
		}
		finally {
			if (created) {
				await virtualInstancesPage.deleteVirtualInstance(webId);
			}
		}
	}
);
