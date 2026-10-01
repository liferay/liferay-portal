/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {notificationsPagesTest} from '../../../fixtures/notificationsPagesTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	loginTest(),
	notificationsPagesTest,
	virtualInstancesPagesTest
);

test(
	'LPD-92621 Importing an invalid schema name shows an error',
	{tag: '@LPD-92621'},
	async ({virtualInstancesPage}) => {
		await virtualInstancesPage.openImportVirtualInstanceModal();

		await virtualInstancesPage.submitImportVirtualInstance({
			schemaName: 'invalid-schema-name',
		});

		await expect(
			virtualInstancesPage.importInstanceErrorMessage
		).toBeVisible();
	}
);

test(
	'LPD-92621 Importing an exported schema shows the import success message',
	{tag: '@LPD-92621'},
	async ({virtualInstancesPage}) => {
		test.setTimeout(5 * 180 * 1000);

		const exportedWebId = getRandomString();
		const importedWebId = getRandomString();

		let exportedCreated = false;
		let imported = false;

		try {
			await virtualInstancesPage.addNewVirtualInstance(exportedWebId);

			exportedCreated = true;

			const schemaName =
				await virtualInstancesPage.exportVirtualInstance(exportedWebId);

			await virtualInstancesPage.deleteVirtualInstance(exportedWebId);

			exportedCreated = false;

			await virtualInstancesPage.openImportVirtualInstanceModal();

			await virtualInstancesPage.submitImportVirtualInstance({
				name: importedWebId,
				schemaName,
				virtualHost: importedWebId,
				webId: importedWebId,
			});

			imported = true;

			await expect(
				virtualInstancesPage.importStartedMessage(schemaName)
			).toBeVisible();

			await virtualInstancesPage.waitForImportNotification(importedWebId);
		}
		finally {
			if (exportedCreated || imported) {
				await virtualInstancesPage.deleteVirtualInstance(
					imported ? importedWebId : exportedWebId
				);
			}
		}
	}
);

test(
	'LPD-93377 Importing a nonexistent schema notifies the user of the failure',
	{tag: '@LPD-93377'},
	async ({notificationsPage, virtualInstancesPage}) => {
		test.setTimeout(2 * 180 * 1000);

		const schemaName = `lexported_${getRandomInt()}`;

		await virtualInstancesPage.openImportVirtualInstanceModal();

		await virtualInstancesPage.submitImportVirtualInstance({schemaName});

		await expect(
			virtualInstancesPage.importStartedMessage(schemaName)
		).toBeVisible();

		await expect(async () => {
			await notificationsPage.goto();

			await expect(
				notificationsPage.getNotification(
					'The exported schema does not exist.',
					`The instance could not be imported from the schema ${schemaName}.`
				)
			).toBeVisible({timeout: 10 * 1000});
		}).toPass({timeout: 300 * 1000});
	}
);
