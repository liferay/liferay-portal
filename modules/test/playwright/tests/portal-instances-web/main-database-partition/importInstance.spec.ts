/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
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

			await virtualInstancesPage.deleteVirtualInstance(
				exportedWebId,
				180 * 1000
			);

			exportedCreated = false;

			await virtualInstancesPage.openImportVirtualInstanceModal();

			await virtualInstancesPage.submitImportVirtualInstance({
				name: importedWebId,
				schemaName,
				timeout: 180 * 1000,
				virtualHost: importedWebId,
				webId: importedWebId,
			});

			imported = true;

			await expect(
				virtualInstancesPage.importInstanceSuccessMessage(importedWebId)
			).toBeVisible({timeout: 180 * 1000});
		}
		finally {
			if (exportedCreated || imported) {
				await virtualInstancesPage.deleteVirtualInstance(
					imported ? importedWebId : exportedWebId,
					180 * 1000
				);
			}
		}
	}
);
