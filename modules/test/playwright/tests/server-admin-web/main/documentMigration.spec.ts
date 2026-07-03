/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {documentMigrationPagesTest} from './fixtures/documentMigrationPagesTest';

export const test = mergeTests(documentMigrationPagesTest, loginTest());

test(
	'LPD-92612 - The renamed Document Migration tab is reachable and active.',
	{tag: '@LPD-92612'},
	async ({documentMigrationPage}) => {
		await documentMigrationPage.goto();

		await expect(documentMigrationPage.tabLink).toBeVisible();

		await expect(documentMigrationPage.tabLink).toHaveClass(/active/);
	}
);
