/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest()
);

test(
	'Can view a content version',
	{tag: '@LPD-64984'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: `title ${getRandomString()}`,
			},
			applicationName,
			spaceName
		);

		assetsPage.gotoContents();

		await testCanViewVersion(assetsPage, page, objectEntry.title);

		await apiHelpers.objectEntry.deleteObjectEntry(
			applicationName,
			String(objectEntry.id)
		);
	}
);

test(
	'Can view a file version',
	{tag: '@LPD-64984'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const spaceName = 'Default';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `title ${getRandomString()}`,
			},
			applicationName,
			spaceName
		);

		assetsPage.gotoFiles();

		await testCanViewVersion(assetsPage, page, objectEntry.title);

		await apiHelpers.objectEntry.deleteObjectEntry(
			applicationName,
			String(objectEntry.id)
		);
	}
);

async function testCanViewVersion(assetsPage, page, title: string) {
	expect(page.getByRole('heading', {name: title})).toBeVisible();

	assetsPage.execItemAction({action: 'View History', filter: title});

	await expect(
		page.getByRole('heading', {name: `"${title}" History`})
	).toBeVisible();

	expect(page.getByRole('button', {name: title})).toBeVisible();

	page.getByRole('cell', {name: title}).first().locator('a').click();

	expect(
		page.getByRole('heading', {name: `${title} (Version 1)`})
	).toBeVisible();

	await page.getByRole('button', {name: 'Close'}).click();
}
