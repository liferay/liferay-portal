/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

async function createFileStructureWithDescription(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string
) {
	const definition = await apiHelpers.objectAdmin.postRandomObjectDefinition({
		objectDefinitionSettings: [
			{
				name: 'acceptedGroupExternalReferenceCodes',
				value: spaceExternalReferenceCode as unknown as object,
			},
		],
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {en_US: 'Title'},
				localized: true,
				name: 'title',
				required: true,
			},
			{
				DBType: 'Long',
				businessType: 'Attachment',
				externalReferenceCode: getRandomString(),
				label: {en_US: 'File'},
				name: 'file',
				objectFieldSettings: [
					{
						name: 'acceptedFileExtensions',
						value: '*' as unknown as object,
					},
					{name: 'maximumFileSize', value: 100 as unknown as object},
					{
						name: 'fileSource',
						value: 'userComputerToCMSBasicDocument' as unknown as object,
					},
					{
						name: 'showFilesInLibrary',
						value: false as unknown as object,
					},
				],
				required: true,
			},
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {en_US: 'Description'},
				localized: true,
				name: 'description',
			},
		],
		objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
		scope: 'depot',
		status: {code: 0},
		titleObjectFieldName: 'title',
	});

	apiHelpers.data.push({id: definition.id, type: 'objectDefinition'});

	return {
		applicationName: definition.restContextPath.replace(/^\/o\//, ''),
	};
}

test(
	'Searching a word from a file’s Description metadata field returns that file',
	{tag: ['@LPD-95544', '@LPD-95544/TC-21.f']},
	async ({apiHelpers, assetsPage}) => {
		const matchingTitle = getRandomString();
		const otherTitle = getRandomString();
		const descriptionKeyword = getRandomString();

		await test.step('Create a File structure with a Description field and two files, one with a matching description', async () => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: `Space ${getRandomString()}`,
					type: 'Space',
				});

			const {applicationName} = await createFileStructureWithDescription(
				apiHelpers,
				space.externalReferenceCode
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					description: `A file about ${descriptionKeyword} topics`,
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: `${matchingTitle}.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: matchingTitle,
				},
				applicationName,
				space.name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					description: 'An unrelated description',
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: `${otherTitle}.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: otherTitle,
				},
				applicationName,
				space.name
			);
		});

		await test.step('Both files are visible before searching', async () => {
			await assetsPage.gotoAll();

			await expect(assetsPage.getItem(matchingTitle)).toBeVisible();
			await expect(assetsPage.getItem(otherTitle)).toBeVisible();
		});

		await test.step('Searching a word from the description returns only the matching file', async () => {
			await assetsPage.dataSetFragmentPage.search(descriptionKeyword);

			await expect(assetsPage.getItem(matchingTitle)).toBeVisible();
			await expect(assetsPage.getItem(otherTitle)).toBeHidden();
		});
	}
);
