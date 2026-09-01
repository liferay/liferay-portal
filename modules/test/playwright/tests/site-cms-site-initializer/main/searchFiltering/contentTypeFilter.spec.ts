/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {applyFDSSelectionFilter} from '../../../../utils/applyFDSSelectionFilter';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

async function createStructuredContentStructure(
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
		],
		objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
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
	'Filtering the All section by Basic Web Content excludes Structured Content and files',
	{tag: ['@LPD-95544', '@LPD-95544/TC-21.b']},
	async ({apiHelpers, assetsPage, page}) => {
		const webContentTitle = getRandomString();
		const structuredContentTitle = getRandomString();
		const fileTitle = getRandomString();

		await test.step('Create one Basic Web Content, one Structured Content, and one file', async () => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: `Space ${getRandomString()}`,
					type: 'Space',
				});

			const {applicationName} = await createStructuredContentStructure(
				apiHelpers,
				space.externalReferenceCode
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: webContentTitle,
				},
				'cms/basic-web-contents',
				space.name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: structuredContentTitle,
				},
				applicationName,
				space.name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: `${fileTitle}.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: fileTitle,
				},
				'cms/basic-documents',
				space.name
			);
		});

		await test.step('All three entries are visible before filtering', async () => {
			await assetsPage.gotoAll();

			await expect(assetsPage.getItem(webContentTitle)).toBeVisible();
			await expect(
				assetsPage.getItem(structuredContentTitle)
			).toBeVisible();
			await expect(assetsPage.getItem(fileTitle)).toBeVisible();
		});

		await test.step('Filtering by Type Basic Web Content shows only the web content', async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Type',
				value: 'Basic Web Content',
			});

			await expect(assetsPage.getItem(webContentTitle)).toBeVisible();
			await expect(
				assetsPage.getItem(structuredContentTitle)
			).toBeHidden();
			await expect(assetsPage.getItem(fileTitle)).toBeHidden();
		});
	}
);

test(
	'Filtering the Contents section by Folder excludes contents',
	{tag: '@LPD-102745'},
	async ({apiHelpers, assetsPage, page}) => {
		const contentTitle = `Content ${getRandomString()}`;
		const folderTitle = getRandomString();

		await test.step('Create a folder and a content in a fresh Space', async () => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: `Space ${getRandomString()}`,
					type: 'Space',
				});

			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: space.name,
				title: folderTitle,
			});

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				'cms/basic-web-contents',
				space.name
			);
		});

		await test.step('Both the folder and the content are visible before filtering', async () => {
			await assetsPage.gotoContents();

			await expect(assetsPage.getItem(folderTitle)).toBeVisible();
			await expect(assetsPage.getItem(contentTitle)).toBeVisible();
		});

		await test.step('Filtering by Type Folder shows only the folder', async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Type',
				value: 'Folder',
			});

			await expect(assetsPage.getItem(folderTitle)).toBeVisible();
			await expect(assetsPage.getItem(contentTitle)).toBeHidden();
		});
	}
);
