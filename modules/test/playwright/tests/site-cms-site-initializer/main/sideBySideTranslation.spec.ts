/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {LocalizationSelectPage} from '../../../pages/fragment-web/LocalizationSelectPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

async function getSampleStructureDefinition(apiHelpers: ApiHelpers) {
	const assetLibraries =
		await apiHelpers.headlessAssetLibrary.getAssetLibrariesPage(
			"type eq 'Space'"
		);

	const defaultSpaceERC = assetLibraries.find(
		({assetLibraryKey}) => assetLibraryKey === 'Default'
	).externalReferenceCode;

	const SAMPLE_STRUCTURE_DEFINITION: ObjectDefinition = {
		enableComments: true,
		enableFriendlyURLCustomization: true,
		enableIndexSearch: true,
		enableLocalization: true,
		enableObjectEntryDraft: true,
		enableObjectEntryHistory: true,
		enableObjectEntrySchedule: true,
		enableObjectEntryVersioning: true,
		externalReferenceCode: getRandomString(),
		label: {
			en_US: 'Sample Structure',
		},
		name: `Sample${getRandomInt()}`,
		objectDefinitionSettings: [
			{
				name: 'acceptedGroupExternalReferenceCodes',
				value: defaultSpaceERC,
			} as any,
		],
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: '06098c29-52f6-40df-8043-3abb82a955be',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {
					en_US: 'Title',
				},
				localized: true,
				name: 'title',
				objectFieldSettings: [],
				required: true,
			},
			{
				DBType: 'Clob',
				businessType: 'LongText',
				externalReferenceCode: '2a75b7cd-331b-44d4-a7ad-1f5f2354e592',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {
					en_US: 'Long Text',
				},
				localized: true,
				name: 'longtext',
				objectFieldSettings: [],
				required: false,
			},
			{
				DBType: 'Date',
				businessType: 'Date',
				externalReferenceCode: '8e8f69e6-ed93-49b9-bf77-34db151055c1',
				indexed: true,
				indexedAsKeyword: false,
				label: {
					en_US: 'Date',
				},
				localized: true,
				name: 'date',
				objectFieldSettings: [],
				required: false,
			},
			{
				DBType: 'Boolean',
				businessType: 'Boolean',
				externalReferenceCode: '8f678eeb-ac9b-46fd-85e5-f3541dbc8384',
				indexed: true,
				indexedAsKeyword: false,
				label: {
					en_US: 'Boolean',
				},
				localized: true,
				name: 'boolean',
				objectFieldSettings: [],
				required: false,
			},
		],
		objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
		objectRelationships: [],
		pluralLabel: {
			en_US: 'Fruit',
		},
		scope: 'depot',
		status: {
			code: 0,
		},
		titleObjectFieldName: 'title',
	};

	return SAMPLE_STRUCTURE_DEFINITION;
}

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest()
);

test(
	'Can translate a content with side by side view',
	{tag: '@LPD-52073'},
	async ({apiHelpers, contentsPage, page}) => {

		// Post object definition for structure

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const sampleStructureDefinition =
			await getSampleStructureDefinition(apiHelpers);

		const {
			body: {id: structureId},
		} = await objectDefinitionAPIClient.postObjectDefinition(
			sampleStructureDefinition
		);

		apiHelpers.data.push({id: structureId, type: 'objectDefinition'});

		// Create a content

		const contentTitle = getRandomString();

		await contentsPage.goto();

		await contentsPage.createContent(sampleStructureDefinition.label.en_US);

		await contentsPage.fillData([
			{label: 'Title', value: contentTitle},
			{label: 'Long Text', value: 'This is a fruit'},
			{label: 'Date', value: '2025-08-08'},
			{label: 'Boolean', type: 'Checkbox', value: true},
		]);

		await contentsPage.saveContent();

		// Translate content with side by side

		await contentsPage.goto();

		await contentsPage.translateContent(contentTitle);

		// Check left side is read only and right side is editable

		await expect(page.getByLabel('Title').first()).toHaveAttribute(
			'readonly'
		);
		await expect(page.getByLabel('Long Text').first()).toHaveAttribute(
			'readonly'
		);
		await expect(page.getByLabel('Date').first()).toHaveAttribute(
			'readonly'
		);
		await expect(page.getByLabel('Boolean').first()).toHaveAttribute(
			'readonly'
		);

		await expect(page.getByLabel('Title').nth(1)).not.toHaveAttribute(
			'readonly'
		);
		await expect(page.getByLabel('Long Text').nth(1)).not.toHaveAttribute(
			'readonly'
		);
		await expect(page.getByLabel('Date').nth(1)).not.toHaveAttribute(
			'readonly'
		);
		await expect(page.getByLabel('Boolean').nth(1)).not.toHaveAttribute(
			'readonly'
		);

		// Change right side language and translate

		const rightLocalizationSelectPage = new LocalizationSelectPage(page, 1);

		await rightLocalizationSelectPage.switchLanguage('es-ES');

		const spanishTitle = `Spanish ${contentTitle}`;

		await contentsPage.fillData([
			{label: 'Title', nth: 1, value: spanishTitle},
			{label: 'Long Text', nth: 1, value: 'This is a vegetable'},
			{label: 'Date', nth: 1, value: '2025-08-15'},
			{label: 'Boolean', nth: 1, type: 'Checkbox', value: false},
		]);

		// Change left side language and check it shows persisted values

		const localizationSelectPage = new LocalizationSelectPage(page);

		await localizationSelectPage.switchLanguage('es-ES');

		await expect(page.getByLabel('Title').first()).toHaveValue(
			contentTitle
		);
		await expect(page.getByLabel('Long Text').first()).toHaveValue(
			'This is a fruit'
		);

		await expect(page.getByLabel('Date').first()).toHaveValue('2025-08-08');

		// Save and check translation is persisted

		await contentsPage.saveContent();

		await contentsPage.goto();

		await contentsPage.editContent(contentTitle);

		await localizationSelectPage.switchLanguage('es-ES');

		await expect(page.getByLabel('Title')).toHaveValue(spanishTitle);
	}
);
