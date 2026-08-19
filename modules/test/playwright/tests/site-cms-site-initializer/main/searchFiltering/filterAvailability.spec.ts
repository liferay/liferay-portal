/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

// CMP Project is registered for both sections, but CMP is not part of the
// portal bundle, so it is left out.

const CONTENTS_FILTERS = [
	'Author',
	'Category',
	'Create Date',
	'Display Date',
	'Expiration Date',
	'Modified Date',
	'Review Date',
	'Space',
	'Status',
	'Tags',
	'Type',
];

const FILES_FILTERS = [...CONTENTS_FILTERS, 'Extension'];

// Both tests create an asset first because an empty section renders its empty
// state instead of the data set, which carries the Filter menu.

async function openFilterMenu(page: Page) {
	const filterButton = page.getByRole('button', {
		exact: true,
		name: 'Filter',
	});

	await expect(filterButton).toBeVisible();

	await filterButton.click();
}

test(
	'The Files section offers every expected filter',
	{tag: '@LPD-102741'},
	async ({apiHelpers, assetsPage, page}) => {
		const fileTitle = `${getRandomString()}.txt`;

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

		await openFilterMenu(page);

		for (const filter of FILES_FILTERS) {
			await expect(
				page.getByRole('menuitem', {exact: true, name: filter})
			).toBeVisible();
		}
	}
);

test(
	'The Contents section offers every expected filter but Extension',
	{tag: '@LPD-102745'},
	async ({apiHelpers, assetsPage, page}) => {
		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: `Content ${getRandomString()}`,
			},
			'cms/basic-web-contents',
			'Default'
		);

		await assetsPage.gotoContents();

		await openFilterMenu(page);

		for (const filter of CONTENTS_FILTERS) {
			await expect(
				page.getByRole('menuitem', {exact: true, name: filter})
			).toBeVisible();
		}

		// Extension is registered for the Files section only.

		await expect(
			page.getByRole('menuitem', {exact: true, name: 'Extension'})
		).toHaveCount(0);
	}
);
