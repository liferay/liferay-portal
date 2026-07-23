/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import {deleteVersion, openVersionHistory} from './utils/versioning';

const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	structureBuilderPagesTest
);

test(
	'A CMS Administrator permanently deletes a specific Structured Content version',
	{tag: ['@LPD-95538', '@LPD-95538/TC-15.d']},
	async ({apiHelpers, page, structureBuilderPage}) => {
		test.setTimeout(300000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const title = `Title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const objectDefinitionId =
			await test.step('Build a custom structure with a localizable Body field', async () => {
				const id = await structureBuilderPage.createStructureFromData({
					label: structureLabel,
					name: structureName,
					page: structureBuilderPage,
					publish: false,
					spaces: [spaceName],
				});

				await structureBuilderPage.addField('Text');
				await structureBuilderPage.selectFields([{label: 'Text'}]);
				await structureBuilderPage.changeFieldSettings({
					label: 'Body',
					localizable: true,
				});

				await structureBuilderPage.publishStructure();

				return id;
			});

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const applicationName = objectDefinition.restContextPath.replace(
			'/o/',
			''
		);

		const bodyField = objectDefinition.objectFields.find(
			(objectField) =>
				objectField.label && objectField.label.en_US === 'Body'
		);

		if (!bodyField) {
			throw new Error('Body field not found in object definition');
		}

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				[bodyField.name]: `Body ${getRandomString()}`,
				[objectDefinition.titleObjectFieldName]: title,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			},
			applicationName,
			spaceName
		);

		await test.step('Edit the entry twice to generate versions 2 and 3', async () => {
			await apiHelpers.objectEntry.patchObjectEntry(
				{
					[`${bodyField.name}_i18n`]: {
						en_US: `Body ${getRandomString()}`,
					},
				},
				applicationName,
				entry.id
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{
					[`${bodyField.name}_i18n`]: {
						en_US: `Body ${getRandomString()}`,
					},
				},
				applicationName,
				entry.id
			);
		});

		const assetsPage = new AssetsPage(page);

		await test.step('The version history lists all three versions', async () => {
			await assetsPage.gotoContents();

			await openVersionHistory(assetsPage, title);

			await expect(page.locator('tbody tr')).toHaveCount(3, {
				timeout: 10000,
			});

			for (const versionNumber of ['1', '2', '3']) {
				await expect(
					page.getByRole('cell', {exact: true, name: versionNumber})
				).toHaveCount(1, {timeout: 5000});
			}
		});

		await test.step('The CMS Administrator permanently deletes version 2', async () => {
			await deleteVersion(page, '2');
		});

		await test.step('The deleted version no longer appears and cannot be restored', async () => {
			await expect(page.locator('tbody tr')).toHaveCount(2, {
				timeout: 10000,
			});

			await expect(
				page.getByRole('cell', {exact: true, name: '2'})
			).toHaveCount(0, {timeout: 5000});

			await expect(
				page.getByRole('cell', {exact: true, name: '1'})
			).toHaveCount(1, {timeout: 5000});

			await expect(
				page.getByRole('cell', {exact: true, name: '3'})
			).toHaveCount(1, {timeout: 5000});
		});
	}
);
