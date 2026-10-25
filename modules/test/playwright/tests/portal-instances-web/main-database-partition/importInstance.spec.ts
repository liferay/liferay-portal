/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(loginTest(), virtualInstancesPagesTest);

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
	async ({page, virtualInstancesPage}) => {
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

			const apiHelpers = new ApiHelpers(page);

			const headlessBatchEngine = apiHelpers.headlessBatchEngine;

			const headlessPortalInstance = apiHelpers.headlessPortalInstance;

			const importTask =
				await headlessPortalInstance.deleteVirtualInstancesBatch([
					exportedWebId,
				]);

			const finishedImportTask =
				await headlessBatchEngine.waitForImportTask(importTask.id);

			expect(finishedImportTask.executeStatus).toBe('COMPLETED');

			exportedCreated = false;

			await virtualInstancesPage.openImportVirtualInstanceModal();

			await expect(
				virtualInstancesPage.importInstanceDocumentLibraryFilesWarning
			).toBeVisible();

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
	'LPD-93377 Importing a nonexistent schema shows an error',
	{tag: '@LPD-93377'},
	async ({virtualInstancesPage}) => {
		await virtualInstancesPage.openImportVirtualInstanceModal();

		await virtualInstancesPage.submitImportVirtualInstance({
			schemaName: `lexported_${getRandomInt()}`,
		});

		await expect(
			virtualInstancesPage.importInstanceNonexistentSchemaErrorMessage
		).toBeVisible();
	}
);
