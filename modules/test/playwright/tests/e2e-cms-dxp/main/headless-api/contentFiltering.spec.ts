/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import {ADMIN_EMAIL_ADDRESS, getWithBasicAuth} from './getWithBasicAuth';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	structureBuilderPagesTest
);

const CONTENTS_APPLICATION_NAME = 'cms/basic-web-contents';

const DOCUMENTS_APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'A category filter returns only the entries tagged with that category',
	{tag: ['@LPD-95549', '@LPD-95549/TC-24.d']},
	async ({apiHelpers, browser, site}) => {
		const categorizedTitle = `Categorized ${getRandomString()}`;
		const otherCategorizedTitle = `Other Categorized ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const uncategorizedTitle = `Uncategorized ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode,
			{searchable: true}
		);

		const cmsSite =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('cms');

		const vocabulary =
			await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: `Vocabulary ${getRandomString()}`,
				siteId: cmsSite.id,
				visibilityType: 'PUBLIC',
			});

		const category =
			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{
					name: `Category ${getRandomString()}`,
					vocabularyId: vocabulary.id,
				}
			);

		const otherCategory =
			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{
					name: `Other Category ${getRandomString()}`,
					vocabularyId: vocabulary.id,
				}
			);

		for (const [title, taxonomyCategoryIds] of [
			[categorizedTitle, [category.id]],
			[otherCategorizedTitle, [otherCategory.id]],
			[uncategorizedTitle, undefined],
		] as [string, number[] | undefined][]) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					taxonomyCategoryIds,
					title,
				},
				CONTENTS_APPLICATION_NAME,
				spaceName
			);
		}

		await expect(async () => {
			const {body, status} = await getWithBasicAuth(
				browser,
				`/o/${CONTENTS_APPLICATION_NAME}/scopes/${encodeURIComponent(spaceName)}?filter=${encodeURIComponent(`taxonomyCategoryIds/any(t:t eq ${category.id})`)}`,
				ADMIN_EMAIL_ADDRESS
			);

			expect(status).toBe(200);

			const titles = ((body?.items as {title: string}[]) || []).map(
				(item) => item.title
			);

			expect(titles).toEqual([categorizedTitle]);
		}).toPass({timeout: 30000});
	}
);

test(
	'A tag filter returns only the content entries and files carrying that tag',
	{tag: ['@LPD-95549', '@LPD-95549/TC-24.e']},
	async ({apiHelpers, browser, site}) => {
		const spaceName = `Space ${getRandomString()}`;
		const tag = `tag${getRandomString()}`;
		const taggedFileTitle = `Tagged File ${getRandomString()}`;
		const taggedTitle = `Tagged ${getRandomString()}`;
		const untaggedFileTitle = `Untagged File ${getRandomString()}`;
		const untaggedTitle = `Untagged ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode,
			{searchable: true}
		);

		for (const [title, keywords] of [
			[taggedTitle, [tag]],
			[untaggedTitle, [`other${getRandomString()}`]],
		] as [string, string[]][]) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					keywords,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title,
				},
				CONTENTS_APPLICATION_NAME,
				spaceName
			);
		}

		for (const [title, keywords] of [
			[taggedFileTitle, [tag]],
			[untaggedFileTitle, undefined],
		] as [string, string[] | undefined][]) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: imageBase64,
						name: `${getRandomString()}.jpg`,
					},
					keywords,
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title,
				},
				DOCUMENTS_APPLICATION_NAME,
				spaceName
			);
		}

		const filter = encodeURIComponent(`keywords/any(k:k eq '${tag}')`);

		await expect(async () => {
			const contents = await getWithBasicAuth(
				browser,
				`/o/${CONTENTS_APPLICATION_NAME}/scopes/${encodeURIComponent(spaceName)}?filter=${filter}`,
				ADMIN_EMAIL_ADDRESS
			);

			expect(contents.status).toBe(200);
			expect(
				((contents.body?.items as {title: string}[]) || []).map(
					(item) => item.title
				)
			).toEqual([taggedTitle]);
		}).toPass({timeout: 30000});

		await expect(async () => {
			const files = await getWithBasicAuth(
				browser,
				`/o/${DOCUMENTS_APPLICATION_NAME}/scopes/${encodeURIComponent(spaceName)}?filter=${filter}`,
				ADMIN_EMAIL_ADDRESS
			);

			expect(files.status).toBe(200);
			expect(
				((files.body?.items as {title: string}[]) || []).map(
					(item) => item.title
				)
			).toEqual([taggedFileTitle]);
		}).toPass({timeout: 30000});
	}
);

test(
	'A field filter on a Structured Content type returns only the entries whose field matches',
	{tag: ['@LPD-95549', '@LPD-95549/TC-24.f']},
	async ({apiHelpers, browser, site, structureBuilderPage}) => {
		test.setTimeout(240000);

		const matchingTitle = `Matching ${getRandomString()}`;
		const nonMatchingTitle = `Non Matching ${getRandomString()}`;
		const regionValue = `Region ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Article ${getRandomString()}`;
		const structureName = `Article${getRandomInt()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode,
			{searchable: true}
		);

		const objectDefinitionId =
			await test.step('Build a custom structure with a Region text field', async () => {
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
					label: 'Region',
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

		const regionField = objectDefinition.objectFields.find(
			(objectField) => objectField.label?.en_US === 'Region'
		);

		if (!regionField) {
			throw new Error('Region field not found in object definition');
		}

		for (const [title, value] of [
			[matchingTitle, regionValue],
			[nonMatchingTitle, `Other ${getRandomString()}`],
		]) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					[regionField.name]: value,
					[objectDefinition.titleObjectFieldName]: title,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				},
				applicationName,
				spaceName
			);
		}

		await expect(async () => {
			const {body, status} = await getWithBasicAuth(
				browser,
				`/o/${applicationName}/scopes/${encodeURIComponent(spaceName)}?filter=${encodeURIComponent(`${regionField.name} eq '${regionValue}'`)}`,
				ADMIN_EMAIL_ADDRESS
			);

			expect(status).toBe(200);
			expect(
				((body?.items as {title: string}[]) || []).map(
					(item) => item.title
				)
			).toEqual([matchingTitle]);
		}).toPass({timeout: 30000});
	}
);
