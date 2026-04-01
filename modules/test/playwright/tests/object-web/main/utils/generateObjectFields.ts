/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectField} from '@liferay/object-admin-rest-client-js';

import {getRandomInt} from '../../../../utils/getRandomInt';

type objectFieldBaseProperties = {
	externalReferenceCode?: string;
	indexedAsKeyword: boolean;
	indexedLanguageId: string;
	label: LocalizedValue<string>;
	localized: boolean;
	name?: string;
	objectFieldSettings?: any;
	readOnly: ObjectField['readOnly'];
	readOnlyConditionExpression: string;
	required: boolean;
	state: boolean;
	system: boolean;
	unique: boolean;
};

function getObjectFieldBaseProperties(): objectFieldBaseProperties {
	return {
		indexedAsKeyword: false,
		indexedLanguageId: '',
		label: {en_US: ''},
		localized: false,
		readOnly: 'false' as ObjectField['readOnly'],
		readOnlyConditionExpression: '',
		required: false,
		state: false,
		system: false,
		unique: false,
	};
}

function getObjectFieldSpecificProperties(
	objectFieldBusinessType: ObjectField['businessType'],
	listTypeDefinitionExternalReferenceCode: string
): {
	['DBType']: ObjectField['DBType'];
	['businessType']: ObjectField['businessType'];
	['listTypeDefinitionExternalReferenceCode']?: ObjectField['listTypeDefinitionExternalReferenceCode'];
	['objectFieldSettings']?: any;
	['type']: ObjectField['type'];
} {
	switch (objectFieldBusinessType) {
		case 'Assignee':
			return {
				DBType: 'Long',
				businessType: 'Assignee',
				type: 'Long',
			};
		case 'Attachment':
			return {
				DBType: 'Long',
				businessType: 'Attachment',
				objectFieldSettings: [
					{
						name: 'acceptedFileExtensions',
						value: 'jpeg, jpg, pdf, png',
					},
					{
						name: 'fileSource',
						value: 'documentsAndMedia',
					},
					{
						name: 'maximumFileSize',
						value: '0',
					},
				],
				type: 'Long',
			};
		case 'AutoIncrement':
			return {
				DBType: 'String',
				businessType: 'AutoIncrement',
				objectFieldSettings: [
					{
						name: 'initialValue',
						value: '1',
					},
				],
				type: 'String',
			};
		case 'Boolean':
			return {
				DBType: 'Boolean',
				businessType: 'Boolean',
				type: 'Boolean',
			};
		case 'Date':
			return {
				DBType: 'Date',
				businessType: 'Date',
				type: 'Date',
			};
		case 'DateTime':
			return {
				DBType: 'DateTime',
				businessType: 'DateTime',
				objectFieldSettings: [
					{
						name: 'timeStorage',
						value: 'convertToUTC',
					},
				],
				type: 'DateTime',
			};
		case 'Decimal':
			return {
				DBType: 'Double',
				businessType: 'Decimal',
				type: 'Double',
			};
		case 'Encrypted':
			return {
				DBType: 'Clob',
				businessType: 'Encrypted',
				type: 'Clob',
			};
		case 'Integer':
			return {
				DBType: 'Integer',
				businessType: 'Integer',
				type: 'Integer',
			};
		case 'LongInteger':
			return {
				DBType: 'Long',
				businessType: 'LongInteger',
				type: 'Long',
			};
		case 'LongText':
			return {
				DBType: 'Clob',
				businessType: 'LongText',
				objectFieldSettings: [
					{
						name: 'showCounter',
						value: false,
					} as any,
				],
				type: 'Clob',
			};
		case 'MultiselectPicklist':
			return {
				DBType: 'String',
				businessType: 'MultiselectPicklist',
				listTypeDefinitionExternalReferenceCode,
				type: 'String',
			};
		case 'Picklist':
			return {
				DBType: 'String',
				businessType: 'Picklist',
				listTypeDefinitionExternalReferenceCode,
				type: 'String',
			};
		case 'PrecisionDecimal':
			return {
				DBType: 'BigDecimal',
				businessType: 'PrecisionDecimal',
				type: 'BigDecimal',
			};
		case 'RichText':
			return {
				DBType: 'Clob',
				businessType: 'RichText',
				type: 'Clob',
			};
		case 'Text':
			return {
				DBType: 'String',
				businessType: 'Text',
				type: 'String',
			};
		default:
			throw new Error(
				`Unsupported object field business type: ${objectFieldBusinessType}`
			);
	}
}

function generateObjectFieldProperties({
	additionalSettings = {},
	listTypeDefinitionExternalReferenceCode,
	objectFieldBusinessType,
}: {
	additionalSettings?: Partial<ObjectField>;
	listTypeDefinitionExternalReferenceCode?: string;
	objectFieldBusinessType: ObjectField['businessType'];
}): Partial<ObjectField> {
	const objectFieldBaseProperties = getObjectFieldBaseProperties();
	const objectFieldLabel = `${objectFieldBusinessType}${getRandomInt()}`;
	const objectFieldSpecificProperties = getObjectFieldSpecificProperties(
		objectFieldBusinessType,
		listTypeDefinitionExternalReferenceCode
	);

	const mergedSettings = [
		...(objectFieldSpecificProperties.objectFieldSettings ?? []),
		...(additionalSettings.objectFieldSettings ?? []),
	];

	return {
		...objectFieldBaseProperties,
		...objectFieldSpecificProperties,
		label: {en_US: objectFieldLabel},
		name: objectFieldLabel.toLocaleLowerCase(),
		...additionalSettings,
		objectFieldSettings: mergedSettings,
	};
}

export function generateObjectFields({
	listTypeDefinitionExternalReferenceCode,
	objectFieldBusinessTypes,
}: {
	listTypeDefinitionExternalReferenceCode?: string;
	objectFieldBusinessTypes: (
		| ObjectField['businessType']
		| (Partial<objectFieldBaseProperties> & {
				businessType: ObjectField['businessType'];
		  })
	)[];
}) {
	const objectFields: Partial<ObjectField>[] = [];

	for (const objectField of objectFieldBusinessTypes) {
		const objectFieldProperties =
			typeof objectField === 'string'
				? generateObjectFieldProperties({
						listTypeDefinitionExternalReferenceCode,
						objectFieldBusinessType: objectField,
					})
				: generateObjectFieldProperties({
						additionalSettings: objectField,
						listTypeDefinitionExternalReferenceCode,
						objectFieldBusinessType: objectField.businessType,
					});
		objectFields.push(objectFieldProperties);
	}

	return objectFields;
}
