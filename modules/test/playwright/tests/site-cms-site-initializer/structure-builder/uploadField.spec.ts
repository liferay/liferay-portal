/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import chooseFileFromCMSLibrary from '../../layout-content-page-editor-web/main/utils/chooseFileFromCMSLibrary';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest(),
	structureBuilderPagesTest
);

test(
	'Every repeat of an upload field opens the same CMS item selector',
	{tag: '@LPD-96714'},
	async ({apiHelpers, contentsPage, page, structureBuilderPage}) => {
		const fileName = `file_${getRandomString()}.png`;
		const structureLabel = `StructureName${getRandomInt()}`;

		// Seed a file in the Default space so it can be found in the selector

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: fileName,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileName,
			},
			'cms/basic-documents',
			'Default'
		);

		apiHelpers.data.push({
			applicationName: 'cms/basic-documents',
			id: objectEntry.id,
			type: 'objectEntry',
		});

		// Create a structure whose upload field selects from the item selector
		// and is a repeatable group

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			publish: false,
		});

		await structureBuilderPage.addField('Upload');

		await structureBuilderPage.changeFieldSettings({
			label: 'Upload from DM',
			name: 'uploadFromDM',
			requestFile: 'document-library',
		});

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Upload from DM'}],
			label: 'Repeatable Group',
		});

		await structureBuilderPage.publishStructure();

		// Create a content for the structure in the Default space

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		const uploadFragments = page.locator('.file-upload');

		// The first repeat selects the seeded file through the CMS item selector

		await chooseFileFromCMSLibrary({
			fileName,
			page,
			trigger: uploadFragments.nth(0).getByText('Select File', {
				exact: true,
			}),
		});

		// The second repeat opens the same CMS item selector and finds the same
		// file instead of a different, empty selector

		await page.getByText('Add New', {exact: true}).first().click();

		await chooseFileFromCMSLibrary({
			fileName,
			page,
			trigger: uploadFragments.nth(1).getByText('Select File', {
				exact: true,
			}),
		});

		// Deleting the auto-tracked structure also removes this draft content

	}
);
