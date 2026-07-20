/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {databaseMigrationPagesTest} from './fixtures/databaseMigrationPagesTest';

export const test = mergeTests(databaseMigrationPagesTest, loginTest());

test(
	'LPD-92611 - Exporting the database schema to a writable path shows a success message.',
	{tag: '@LPD-92611'},
	async ({databaseMigrationPage}) => {
		await databaseMigrationPage.goto();

		await expect(databaseMigrationPage.tabLink).toBeVisible();

		const exportFilesPath = `/tmp/db-migration-schema-export-${getRandomInt()}`;

		await databaseMigrationPage.exportSchema(exportFilesPath);

		await expect(databaseMigrationPage.successToast).toContainText(
			`The database schema was exported successfully to ${exportFilesPath}`
		);
	}
);

test(
	'LPD-92611 - Exporting the database schema to an unwritable path shows an error message.',
	{tag: '@LPD-92611'},
	async ({databaseMigrationPage}) => {
		await databaseMigrationPage.goto();

		// A path segment longer than the file system limit cannot be created,
		// so the export fails regardless of the process privileges.

		await databaseMigrationPage.exportSchema(`/tmp/${'a'.repeat(300)}`);

		await expect(databaseMigrationPage.errorAlert).toContainText(
			'Unable to export the database schema'
		);
	}
);
