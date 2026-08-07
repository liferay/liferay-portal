/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {getTempDir} from '../../../utils/temp';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test(
	'Can export at site level with a custom task name',
	{tag: '@LPD-57655'},
	async ({exportImportPage, site}) => {
		await exportImportPage.goToExport(site.friendlyUrlPath);

		const name = `MyExport-${getRandomString()}`;

		await exportImportPage.export(name);

		await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();

		expect(await exportImportPage.download(name)).toBe(
			`${getTempDir()}${name}.lar`
		);
	}
);

test(
	'Can manage an export process from the processes list',
	{tag: '@LPD-100541'},
	async ({exportImportPage, page, site}) => {
		await exportImportPage.goToExport(site.friendlyUrlPath);

		const name = `MyExport-${getRandomString()}`;

		await exportImportPage.export(name);

		await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();

		await exportImportPage.actionsButton(name).click();

		await expect(exportImportPage.relaunchMenuItem).toBeVisible();
		await expect(exportImportPage.downloadMenuItem).toBeVisible();
		await expect(exportImportPage.clearMenuItem).toBeVisible();

		let confirmationMessage = '';

		page.once('dialog', async (dialog) => {
			confirmationMessage = dialog.message();

			await dialog.accept();
		});

		await exportImportPage.clearMenuItem.click();

		await expect(page.getByRole('row', {name})).toBeHidden();

		expect(confirmationMessage).toContain('Are you sure');
	}
);

test(
	'Can sort the export processes list by title',
	{tag: ['@LPD-100541', '@LRQA-28935']},
	async ({exportImportPage, site}) => {
		await exportImportPage.goToExport(site.friendlyUrlPath);

		const name1 = `MyExport-${getRandomString()}`;

		await exportImportPage.export(name1);

		await expect(exportImportPage.taskStatusLabel(name1)).toBeVisible();

		const name2 = `MyExport-${getRandomString()}`;

		await exportImportPage.export(name2);

		await expect(exportImportPage.taskStatusLabel(name2)).toBeVisible();

		await exportImportPage.sortBy('Title');
		let values = await exportImportPage.getColumnValues('Title');
		expect(values).toEqual([...values].sort((a, b) => a.localeCompare(b)));

		await exportImportPage.sortBy('Title');
		values = await exportImportPage.getColumnValues('Title');
		expect(values).toEqual([...values].sort((a, b) => b.localeCompare(a)));
	}
);

test(
	'Can select comments and ratings at site level',
	{tag: '@LPD-57655'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		page,
		site,
	}) => {
		await apiHelpers.headlessDelivery.postBlog(site.id);

		await exportImportPage.goToExport(site.friendlyUrlPath);

		await exportImportPage.clickNew();

		await exportImportDataSelectionPage.expandSection('Content & Data');

		await expect(page.getByText('Comments and Ratings')).toBeVisible();
	}
);
