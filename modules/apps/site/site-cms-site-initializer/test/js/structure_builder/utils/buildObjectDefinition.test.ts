/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import buildObjectDefinition from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildObjectDefinition';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

const DATE_TIME_FIELD: Field = {
	erc: 'datetime-field',
	indexableConfig: {indexed: false},
	label: {en_US: 'Date and Time Field'},
	localized: true,
	name: 'datetimeField',
	required: false,
	settings: {
		timeStorage: 'convertToUTC',
	},
	type: 'datetime',
	uuid: getUuid(),
};

const TEXT_FIELD: Field = {
	erc: 'text-field',
	indexableConfig: {indexed: true, indexedAsKeyword: true},
	label: {en_US: 'Text Field'},
	localized: false,
	name: 'textField',
	required: true,
	settings: {},
	type: 'text',
	uuid: getUuid(),
};

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/config',
	() => ({
		config: {
			acceptedGroupExternalReferenceCodes:
				'acceptedGroupExternalReferenceCodesConfig',
		},
	})
);

describe('buildObjectDefinition', () => {
	it('Builds objectDefinition with a field without settings', () => {
		const result = buildObjectDefinition({
			erc: 'structureERC',
			fields: [TEXT_FIELD],
			id: 1,
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
		});

		expect(result).toEqual({
			enableFriendlyURLCustomization: true,
			enableIndexSearch: true,
			enableLocalization: true,
			enableObjectEntryDraft: true,
			enableObjectEntryVersioning: true,
			externalReferenceCode: 'structureERC',
			id: 1,
			label: {en_US: 'Structure'},
			name: 'myStructure',
			objectFields: [
				{
					DBType: 'String',
					businessType: 'Text',
					externalReferenceCode: 'text-field',
					indexed: true,
					indexedAsKeyword: true,
					indexedLanguageId: '',
					label: {en_US: 'Text Field'},
					localized: false,
					name: 'textField',
					objectFieldSettings: [],
					required: true,
				},
			],
			objectRelationships: [],
			pluralLabel: {en_US: 'Structure'},
			scope: 'depot',
		});
	});

	it('Builds objectDefinition with a field with settings', () => {
		const result = buildObjectDefinition({
			erc: 'structureERC',
			fields: [DATE_TIME_FIELD],
			id: 1,
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
		});

		expect(result).toEqual({
			enableFriendlyURLCustomization: true,
			enableIndexSearch: true,
			enableLocalization: true,
			enableObjectEntryDraft: true,
			enableObjectEntryVersioning: true,
			externalReferenceCode: 'structureERC',
			id: 1,
			label: {en_US: 'Structure'},
			name: 'myStructure',
			objectFields: [
				{
					DBType: 'DateTime',
					businessType: 'DateTime',
					externalReferenceCode: 'datetime-field',
					indexed: false,
					label: {en_US: 'Date and Time Field'},
					localized: true,
					name: 'datetimeField',
					objectFieldSettings: [
						{name: 'timeStorage', value: 'convertToUTC'},
					],
					required: false,
				},
			],
			objectRelationships: [],
			pluralLabel: {en_US: 'Structure'},
			scope: 'depot',
		});
	});

	it('Builds objectDefinition with spaces selected', () => {
		const result = buildObjectDefinition({
			erc: 'structureERC',
			fields: [TEXT_FIELD],
			id: 1,
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: ['space-1-erc', 'space-2-erc'],
		});

		expect(result).toEqual({
			enableFriendlyURLCustomization: true,
			enableIndexSearch: true,
			enableLocalization: true,
			enableObjectEntryDraft: true,
			enableObjectEntryVersioning: true,
			externalReferenceCode: 'structureERC',
			id: 1,
			label: {en_US: 'Structure'},
			name: 'myStructure',
			objectDefinitionSettings: [
				{
					name: 'acceptedGroupExternalReferenceCodes',
					value: 'space-1-erc,space-2-erc',
				},
			],
			objectFields: [
				{
					DBType: 'String',
					businessType: 'Text',
					externalReferenceCode: 'text-field',
					indexed: true,
					indexedAsKeyword: true,
					indexedLanguageId: '',
					label: {en_US: 'Text Field'},
					localized: false,
					name: 'textField',
					objectFieldSettings: [],
					required: true,
				},
			],
			objectRelationships: [],
			pluralLabel: {en_US: 'Structure'},
			scope: 'depot',
		});
	});
});
