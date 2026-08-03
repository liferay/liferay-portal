/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import * as path from 'path';

import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import getRandomString from '../../../utils/getRandomString';
import {stagingPageTest} from '../main/fixtures/stagingPageTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	documentLibraryPagesTest,
	productMenuPageTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	loginTest(),
	stagingPageTest
);

test('Can import using the new navigation buttons', async ({
	documentLibraryPage,
	exportImportPage,
	productMenuPage,
}) => {
	await documentLibraryPage.goto();
	await documentLibraryPage.openOptionsMenu();
	await exportImportPage.importMenuItem.click();

	await exportImportPage.newButton.click();

	await exportImportPage.import({
		folderPath: path.join(__dirname, 'dependencies', 'folder.portlet.lar'),
		name: `Test import-${getRandomString()}`,
	});

	await productMenuPage.backButton.click();

	await expect(
		documentLibraryPage.page.getByRole('link', {name: 'LPS-205933'})
	).toBeVisible();
});
