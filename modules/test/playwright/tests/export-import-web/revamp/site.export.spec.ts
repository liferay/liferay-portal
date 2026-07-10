/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	exportImportPagesTest,
	isolatedSiteTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	loginTest()
);

test(
	'can export at site level with a custom task name',
	{tag: '@LPD-57655'},
	async ({exportImportPage, site}) => {
		await exportImportPage.goToExport(site.friendlyUrlPath);

		const name = `MyExport-${getRandomString()}`;

		await exportImportPage.export(name);

		await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();
	}
);

test(
	'cannot export at site level without a file name',
	{tag: '@LPD-57655'},
	async ({exportImportPage, site}) => {
		await exportImportPage.goToExport(site.friendlyUrlPath);

		await exportImportPage.clickNew();

		await expect(exportImportPage.exportButton).toBeDisabled();
	}
);

test(
	'data sections are checked by default and can be toggled',
	{tag: '@LPD-57655'},
	async ({exportImportDataSelectionPage, exportImportPage, site}) => {
		await exportImportPage.goToExport(site.friendlyUrlPath);

		await exportImportPage.clickNew();

		expect(
			await exportImportDataSelectionPage.isSectionChecked('Site Builder')
		).toBe(true);

		await exportImportDataSelectionPage.unselectSection('Site Builder');

		expect(
			await exportImportDataSelectionPage.isSectionChecked('Site Builder')
		).toBe(false);

		await exportImportDataSelectionPage.selectSection('Site Builder');

		expect(
			await exportImportDataSelectionPage.isSectionChecked('Site Builder')
		).toBe(true);
	}
);
