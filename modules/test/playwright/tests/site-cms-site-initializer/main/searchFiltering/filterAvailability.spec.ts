/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

// CMP Project is registered for the Files section too, but CMP is not part of
// the portal bundle, so it is left out.

const FILTERS = [
	'Author',
	'Category',
	'Create Date',
	'Display Date',
	'Expiration Date',
	'Extension',
	'Modified Date',
	'Review Date',
	'Space',
	'Status',
	'Tags',
	'Type',
];

test(
	'The Files section offers every expected filter',
	{tag: '@LPD-102741'},
	async ({apiHelpers, assetsPage, page}) => {
		const fileTitle = `${getRandomString()}.txt`;

		// An empty Files section renders its empty state instead of the data
		// set, which carries the Filter menu, so one file has to exist.

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						Buffer.from('plain text content').toString('base64'),
					name: fileTitle,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			'cms/basic-documents',
			'Default'
		);

		await assetsPage.gotoFiles();

		const filterButton = page.getByRole('button', {
			exact: true,
			name: 'Filter',
		});

		await expect(filterButton).toBeVisible();

		await filterButton.click();

		for (const filter of FILTERS) {
			await expect(
				page.getByRole('menuitem', {exact: true, name: filter})
			).toBeVisible();
		}
	}
);
