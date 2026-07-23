/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const SPACE_NAME = 'Default';

const cmsViewerURL = (objectEntryId: number) =>
	`/web/cms/view-asset?objectEntryId=${objectEntryId}`;

test(
	'Review-date notification link opens the CMS viewer for a content entry',
	{tag: '@LPD-79674'},
	async ({apiHelpers, page}) => {
		const title = `content ${getRandomString()}`;

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			'cms/basic-web-contents',
			SPACE_NAME
		);

		apiHelpers.data.push({id: objectEntry.id, type: 'document'});

		await page.goto(cmsViewerURL(objectEntry.id));

		await expect(page.getByText(title).first()).toBeAttached();
	}
);

test(
	'Review-date notification link opens the CMS viewer for a file entry',
	{tag: '@LPD-79674'},
	async ({apiHelpers, page}) => {
		const title = `file ${getRandomString()}`;

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `${title}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title,
			},
			'cms/basic-documents',
			SPACE_NAME
		);

		apiHelpers.data.push({id: objectEntry.id, type: 'document'});

		await page.goto(cmsViewerURL(objectEntry.id));

		await expect(page.getByText(title).first()).toBeAttached();
	}
);

test(
	'Review-date notification link opens the CMS viewer for a content entry placed in a sub-folder',
	{tag: '@LPD-79674'},
	async ({apiHelpers, page}) => {
		const folderTitle = `subfolder ${getRandomString()}`;
		const entryTitle = `content ${getRandomString()}`;

		const objectEntryFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: SPACE_NAME,
				title: folderTitle,
			});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode:
					objectEntryFolder.externalReferenceCode,
				title: entryTitle,
			},
			'cms/basic-web-contents',
			SPACE_NAME
		);

		apiHelpers.data.push({id: objectEntry.id, type: 'document'});

		await page.goto(cmsViewerURL(objectEntry.id));

		await expect(page.getByText(entryTitle).first()).toBeAttached();

		await apiHelpers.objectFolder.deleteObjectEntryFolder(
			objectEntryFolder.id
		);
	}
);
