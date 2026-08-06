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

const RELATION_FIELD_LABEL = 'Attachment';

test(
	'A content entry whose linked CMS file was deleted reports the broken relation',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.g', '@LPD-100265']},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const documentsApplicationName = 'cms/basic-documents';
		const entryTitle = getRandomString();
		const fileTitle = `File ${getRandomString()}`;

		const relationField = page.getByRole('combobox', {
			name: RELATION_FIELD_LABEL,
		});

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const fileEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: readFileSync(
						path.join(
							__dirname,
							'../../dependencies/sample_small_wide_400x300.jpg'
						)
					).toString('base64'),
					name: `${fileTitle}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			documentsApplicationName,
			space.name
		);

		const structureLabel = `Structure${getRandomString()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			publish: false,
			spaces: [space.name],
		});

		await structureBuilderPage.addField('Select Related Content');

		await structureBuilderPage.selectFields([
			{label: 'Select Related Content'},
		]);

		await structureBuilderPage.changeFieldSettings({
			label: RELATION_FIELD_LABEL,
			relatedContent: 'Basic Document',
		});

		await structureBuilderPage.publishStructure();

		await test.step('Create a content entry linked to the CMS file', async () => {
			await assetsPage.gotoContents(space.name);

			await contentsPage.createContent(structureLabel, space.name);

			await page
				.getByRole('textbox', {exact: true, name: 'Title'})
				.fill(entryTitle);

			await relationField.click();

			await page.getByRole('option', {name: fileTitle}).click();

			await expect(relationField).toHaveValue(fileTitle);

			await contentsPage.publishButton.click();

			await expect(page.getByText(entryTitle).first()).toBeVisible();
		});

		await test.step('Delete the linked file from the CMS and empty it from the Recycle Bin', async () => {
			await apiHelpers.objectEntry.deleteObjectEntry(
				documentsApplicationName,
				String(fileEntry.id)
			);

			await apiHelpers.objectEntry.deleteObjectEntry(
				documentsApplicationName,
				String(fileEntry.id)
			);
		});

		await test.step('Reopening the entry warns that the linked file no longer exists', async () => {
			await assetsPage.gotoContents(space.name);

			await contentsPage.editContent(entryTitle);

			await expect(relationField).toBeVisible();

			test.fail(
				true,
				'LPD-100265: the relation field is silently cleared, with no warning that the linked file is gone.'
			);

			await expect(page.getByText('no longer exists')).toBeVisible();
		});
	}
);
