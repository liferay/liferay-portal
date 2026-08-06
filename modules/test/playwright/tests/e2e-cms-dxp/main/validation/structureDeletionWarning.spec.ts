/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest(),
	structureBuilderPagesTest
);

test(
	'Deleting a content structure in use warns how many entries it affects and requires typed confirmation',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.i']},
	async ({apiHelpers, page, structureBuilderPage, structuresPage}) => {
		const entryCount = 3;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const structureLabel = `Structure${getRandomString()}`;

		const objectDefinitionId =
			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				page: structureBuilderPage,
				spaces: [space.name],
			});

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const applicationName = objectDefinition.restContextPath.replace(
			'/o/',
			''
		);

		for (let i = 0; i < entryCount; i++) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: `Content ${getRandomString()}`,
				},
				applicationName,
				space.name
			);
		}

		await test.step('Trigger the structure deletion', async () => {
			await structuresPage.goto();

			await structuresPage.execItemAction({
				action: 'Delete',
				filter: structureLabel,
			});
		});

		await test.step('The warning names the affected entry count and the risk', async () => {
			await expect(
				page.getByText(
					`"${structureLabel}" is currently used by ${entryCount} entries.`
				)
			).toBeVisible();

			await expect(
				page.getByText(
					'This action is permanent and cannot be undone.',
					{exact: false}
				)
			).toBeVisible();
		});

		await test.step('Deletion is only possible after typing the structure name', async () => {
			const deleteButton = page
				.getByRole('dialog')
				.getByRole('button', {name: 'Delete'});

			await expect(deleteButton).toBeDisabled();

			await page
				.getByPlaceholder('Confirm Content Structure Name')
				.fill(structureLabel);

			await expect(deleteButton).toBeEnabled();
		});
	}
);

test(
	'Deleting a file structure in use warns how many entries it affects',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.j']},
	async ({apiHelpers, page, structureBuilderPage, structuresPage}) => {
		const entryCount = 2;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const structureLabel = `Structure${getRandomString()}`;

		const objectDefinitionId =
			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				page: structureBuilderPage,
				spaces: [space.name],
				type: 'file',
			});

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const applicationName = objectDefinition.restContextPath.replace(
			'/o/',
			''
		);

		const fileBase64 = readFileSync(
			path.join(
				__dirname,
				'../../dependencies/sample_small_wide_400x300.jpg'
			)
		).toString('base64');

		for (let i = 0; i < entryCount; i++) {
			const title = `File ${getRandomString()}`;

			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64,
						name: `${title}.jpg`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title,
				},
				applicationName,
				space.name
			);
		}

		await test.step('Trigger the file structure deletion', async () => {
			await structuresPage.goto();

			await structuresPage.execItemAction({
				action: 'Delete',
				filter: structureLabel,
			});
		});

		await test.step('The warning names the affected entry count', async () => {
			await expect(
				page.getByText(
					`"${structureLabel}" is currently used by ${entryCount} entries.`
				)
			).toBeVisible();

			await expect(
				page.getByRole('dialog').getByRole('button', {name: 'Delete'})
			).toBeDisabled();
		});
	}
);
