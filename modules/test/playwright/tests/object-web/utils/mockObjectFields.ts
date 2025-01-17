/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectField} from '@liferay/object-admin-rest-client-js';

import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {
	getObjectEntryAPIDateFormat,
	getObjectEntryUIDateFormat,
} from './dateFormat';

interface MockObjectFieldsReturn {
	listTypeDefinition?: ListTypeDefinition;
	objectEntry?: ObjectEntry;
	objectFields: Partial<ObjectField>[];
	titleObjectFieldName?: string;
}

type ObjectFieldBusinessTypesLabelName = {
	[K in ObjectFieldBusinessTypes]: LabelNameObject;
};

type ObjectEntry = {
	[K in Partial<ObjectFieldBusinessTypes>]: string;
};

type ObjectFieldBusinessTypes =
	| 'attachment'
	| 'autoIncrement'
	| 'boolean'
	| 'date'
	| 'decimal'
	| 'encrypted'
	| 'integer'
	| 'longInteger'
	| 'longText'
	| 'multiselectPicklist'
	| 'picklist'
	| 'precisionDecimal'
	| 'richText'
	| 'text';

const objectFieldbusinessTypeInfo: {
	[K in ObjectFieldBusinessTypes]: {
		['DBType']: ObjectField['DBType'];
		['businessType']: ObjectField['businessType'];
		['type']: ObjectField['type'];
	};
} = {
	attachment: {
		DBType: ObjectField.DBTypeEnum.Long,
		businessType: ObjectField.BusinessTypeEnum.Attachment,
		type: ObjectField.TypeEnum.Long,
	},
	autoIncrement: {
		DBType: ObjectField.DBTypeEnum.String,
		businessType: ObjectField.BusinessTypeEnum.AutoIncrement,
		type: ObjectField.TypeEnum.String,
	},
	boolean: {
		DBType: ObjectField.DBTypeEnum.Boolean,
		businessType: ObjectField.BusinessTypeEnum.Boolean,
		type: ObjectField.TypeEnum.Boolean,
	},
	date: {
		DBType: ObjectField.DBTypeEnum.Date,
		businessType: ObjectField.BusinessTypeEnum.Date,
		type: ObjectField.TypeEnum.Date,
	},
	decimal: {
		DBType: ObjectField.DBTypeEnum.Double,
		businessType: ObjectField.BusinessTypeEnum.Decimal,
		type: ObjectField.TypeEnum.Double,
	},
	encrypted: {
		DBType: ObjectField.DBTypeEnum.Clob,
		businessType: ObjectField.BusinessTypeEnum.Encrypted,
		type: ObjectField.TypeEnum.Clob,
	},
	integer: {
		DBType: ObjectField.DBTypeEnum.Integer,
		businessType: ObjectField.BusinessTypeEnum.Integer,
		type: ObjectField.TypeEnum.Integer,
	},
	longInteger: {
		DBType: ObjectField.DBTypeEnum.Long,
		businessType: ObjectField.BusinessTypeEnum.LongInteger,
		type: ObjectField.TypeEnum.Long,
	},
	longText: {
		DBType: ObjectField.DBTypeEnum.Clob,
		businessType: ObjectField.BusinessTypeEnum.LongText,
		type: ObjectField.TypeEnum.Clob,
	},
	multiselectPicklist: {
		DBType: ObjectField.DBTypeEnum.String,
		businessType: ObjectField.BusinessTypeEnum.MultiselectPicklist,
		type: ObjectField.TypeEnum.String,
	},
	picklist: {
		DBType: ObjectField.DBTypeEnum.String,
		businessType: ObjectField.BusinessTypeEnum.Picklist,
		type: ObjectField.TypeEnum.String,
	},
	precisionDecimal: {
		DBType: ObjectField.DBTypeEnum.BigDecimal,
		businessType: ObjectField.BusinessTypeEnum.PrecisionDecimal,
		type: ObjectField.TypeEnum.BigDecimal,
	},
	richText: {
		DBType: ObjectField.DBTypeEnum.Clob,
		businessType: ObjectField.BusinessTypeEnum.RichText,
		type: ObjectField.TypeEnum.Clob,
	},
	text: {
		DBType: ObjectField.DBTypeEnum.String,
		businessType: ObjectField.BusinessTypeEnum.Text,
		type: ObjectField.TypeEnum.String,
	},
};

function isLocalizable(businessType: ObjectFieldBusinessTypes) {
	const localizableBusinessTypes: ObjectFieldBusinessTypes[] = [
		'boolean',
		'decimal',
		'integer',
		'longInteger',
		'precisionDecimal',
	];

	return localizableBusinessTypes.includes(businessType);
}

export function createObjectFields(
	businessType: keyof ObjectFieldBusinessTypesLabelName,
	objectFieldsBusinessTypeLabelName: LabelNameObject[],
	additionalSettings: Partial<ObjectField> = {},
	localizeAllLocalizable: boolean = false
): Partial<ObjectField>[] {
	const baseObjectField: ObjectField = {
		indexedAsKeyword: false,
		indexedLanguageId: '',
		localized: !!(isLocalizable(businessType) && localizeAllLocalizable),
		readOnly: ObjectField.ReadOnlyEnum.False,
		readOnlyConditionExpression: '',
		required: false,
		state: false,
		system: false,
		unique: false,
	};

	return objectFieldsBusinessTypeLabelName.map(({label, name}) => ({
		DBType: objectFieldbusinessTypeInfo[businessType].DBType,
		businessType: objectFieldbusinessTypeInfo[businessType].businessType,
		label: {
			en_US: label,
		},
		name,
		type: objectFieldbusinessTypeInfo[businessType].type,
		...additionalSettings,
		...baseObjectField,
	}));
}

function getFormatDate(format: 'API' | 'UI'): string {
	const date = new Date();

	if (format === 'API') {
		return getObjectEntryAPIDateFormat(date);
	}
	else {
		return getObjectEntryUIDateFormat(date);
	}
}

function getRandomObjectFieldEntryValue(
	format: 'API' | 'UI',
	listTypeDefinitionItems: string[],
	objectFieldBusinessType: ObjectFieldBusinessTypes
) {
	switch (objectFieldBusinessType) {
		case 'boolean':
			return Math.random() < 0.5;
		case 'date':
			return getFormatDate(format);
		case 'decimal':
			return parseFloat(Math.random().toFixed(10)).toString();
		case 'encrypted':
			return getRandomString();
		case 'integer':
			return Math.floor(Math.random() * 100).toString();
		case 'longInteger':
			return getRandomInt().toString();
		case 'longText':
			return getRandomString();
		case 'multiselectPicklist':
			return [listTypeDefinitionItems[0], listTypeDefinitionItems[1]];
		case 'picklist':
			return {key: listTypeDefinitionItems[0]};
		case 'precisionDecimal':
			return parseFloat(Math.random().toFixed(15)).toString();
		case 'richText':
			return getRandomString();
		case 'text':
			return getRandomString();
		default:
			return '';
	}
}

export async function mockObjectFields({
	apiHelpers,
	localizeAllLocalizable,
	objectEntryReturn,
	objectFieldBusinessTypes,
	titleObjectFieldName,
}: {
	apiHelpers: ApiHelpers;
	localizeAllLocalizable?: boolean;
	objectEntryReturn?: {format: 'API' | 'UI'};
	objectFieldBusinessTypes: ObjectFieldBusinessTypes[];
	titleObjectFieldName?: ObjectFieldBusinessTypes;
}): Promise<MockObjectFieldsReturn> {
	let listTypeDefinition: ListTypeDefinition;
	let listTypeDefinitionItems: string[];

	if (
		objectFieldBusinessTypes.includes('picklist') ||
		objectFieldBusinessTypes.includes('multiselectPicklist')
	) {
		listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		listTypeDefinitionItems = new Array(3)
			.fill('')
			.map(() => getRandomInt().toString());

		for (const lisTypeEntry of listTypeDefinitionItems) {
			await apiHelpers.listTypeAdmin.postListTypeEntry(
				listTypeDefinition.externalReferenceCode,
				lisTypeEntry
			);
		}
	}

	let objectFieldBusinessTypesLabelName =
		{} as ObjectFieldBusinessTypesLabelName;

	function setLabelName(businessType: string, {label, name}) {
		objectFieldBusinessTypesLabelName = {
			...objectFieldBusinessTypesLabelName,
			[businessType]: [
				...(objectFieldBusinessTypesLabelName[businessType] || []),
				{label, name},
			],
		};
	}

	for (const objectFieldBusinessType of objectFieldBusinessTypes) {
		setLabelName(objectFieldBusinessType, {
			label: `${objectFieldBusinessType}${getRandomInt()}`,
			name: `${objectFieldBusinessType}${getRandomInt()}`,
		});
	}

	function setObjectFieldsAdditionalSettings(
		objectFieldBusinessType: ObjectFieldBusinessTypes
	): Partial<ObjectField> | undefined {
		switch (objectFieldBusinessType) {
			case 'attachment':
				return {
					objectFieldSettings: [
						{
							name: 'acceptedFileExtensions',
							value: 'jpeg, jpg, pdf, png',
						} as any,
						{
							name: 'fileSource',
							value: 'documentsAndMedia',
						} as any,
						{
							name: 'maximumFileSize',
							value: '100',
						} as any,
					],
				};
			case 'autoIncrement':
				return {
					objectFieldSettings: [
						{
							name: 'initialValue',
							value: '1',
						} as any,
					],
				};
			case 'longText':
				return {
					objectFieldSettings: [
						{
							name: 'showCounter',
							value: false,
						} as any,
					],
				};
			case 'multiselectPicklist':
				return {
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
					listTypeDefinitionId: listTypeDefinition.id,
				};
			case 'picklist':
				return {
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
				};
			default:
				return undefined;
		}
	}

	const objectEntry = {} as ObjectEntry;

	let objectFields: Partial<ObjectField>[] = [];

	for (const objectFieldBusinessType in objectFieldBusinessTypesLabelName) {
		objectFields = objectFields.concat(
			createObjectFields(
				objectFieldBusinessType as ObjectFieldBusinessTypes,
				objectFieldBusinessTypesLabelName[objectFieldBusinessType],
				setObjectFieldsAdditionalSettings(
					objectFieldBusinessType as ObjectFieldBusinessTypes
				),
				localizeAllLocalizable
			)
		);

		if (
			objectFieldBusinessType !== 'attachment' &&
			objectFieldBusinessType !== 'autoIncrement' &&
			objectEntryReturn
		) {
			for (const field of objectFieldBusinessTypesLabelName[
				objectFieldBusinessType
			]) {
				objectEntry[field.name] = getRandomObjectFieldEntryValue(
					objectEntryReturn.format,
					listTypeDefinitionItems,
					objectFieldBusinessType as ObjectFieldBusinessTypes
				);
			}
		}
	}

	return {
		listTypeDefinition,
		objectEntry: objectEntryReturn ? objectEntry : undefined,
		objectFields,
		titleObjectFieldName: titleObjectFieldName
			? objectFieldBusinessTypesLabelName[titleObjectFieldName].name
			: undefined,
	};
}
