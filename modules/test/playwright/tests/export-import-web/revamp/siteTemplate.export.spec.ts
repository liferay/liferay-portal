/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-38869': {enabled: false},
		'LPD-57655': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

test(
	'Can pick the site template pages when private pages are disabled',
	{tag: '@LPD-116351'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		page,
	}) => {
		const layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{name: `SiteTemplate-${getRandomString()}`}
			);

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		const layoutSetPrototypeGroup =
			await apiHelpers.jsonWebServicesGroup.getGroupByKey(
				layoutSetPrototype.companyId,
				layoutSetPrototype.layoutSetPrototypeId
			);

		const pageTitle = `Page-${getRandomString()}`;

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: layoutSetPrototypeGroup.groupId,
			privateLayout: 'true',
			title: pageTitle,
		});

		await exportImportPage.goToExport(layoutSetPrototypeGroup.friendlyURL);

		await exportImportPage.clickNew();

		await exportImportDataSelectionPage.expandSection('Site Builder');

		await page.getByRole('button', {name: 'Select Pages'}).click();

		const pageTreePicker = page.getByRole('dialog');

		await expect(
			pageTreePicker.getByRole('checkbox', {
				exact: true,
				name: 'Pages',
			})
		).toBeVisible();
		await expect(
			pageTreePicker.getByRole('checkbox', {
				exact: true,
				name: pageTitle,
			})
		).toBeVisible();
	}
);
