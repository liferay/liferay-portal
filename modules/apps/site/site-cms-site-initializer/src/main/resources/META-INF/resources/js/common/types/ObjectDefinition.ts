/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type ObjectField = {
	DBType: string;
	businessType:
		| 'Attachment'
		| 'Boolean'
		| 'Decimal'
		| 'Date'
		| 'DateTime'
		| 'EmailAddress'
		| 'Integer'
		| 'MultiselectPicklist'
		| 'Long'
		| 'LongText'
		| 'PhoneNumber'
		| 'Picklist'
		| 'RichText'
		| 'Relationship'
		| 'String'
		| 'Text';
	externalReferenceCode: string;
	indexed: boolean;
	indexedAsKeyword?: boolean;
	indexedLanguageId?: Liferay.Language.Locale | '';
	label: Liferay.Language.LocalizedValue<string>;
	listTypeDefinitionId?: number;
	localized: boolean;
	name: string;
	objectFieldSettings?: {
		name: string;
		value: boolean | string | number | StateFlowValue;
	}[];
	required: boolean;
	system: boolean;
};

export type ObjectRelationship = {
	deletionType: string;
	edge?: boolean;
	externalReferenceCode: string;
	label: Liferay.Language.LocalizedValue<string>;
	name: string;
	objectDefinitionExternalReferenceCode1: string;
	objectDefinitionExternalReferenceCode2: string;
	objectDefinitionName2?: string;
	reverse?: boolean;
	type: 'manyToMany' | 'oneToMany' | 'oneToOne';
};

export type ObjectDefinition = {
	enableComments: boolean;
	enableFriendlyURLCustomization: boolean;
	enableIndexSearch: boolean;
	enableLocalization: boolean;
	enableObjectEntryDraft: boolean;
	enableObjectEntryHistory: boolean;
	enableObjectEntrySchedule: boolean;
	enableObjectEntryVersioning: boolean;
	externalReferenceCode: string;
	friendlyURLSeparator?: string;
	id?: number;
	label: Liferay.Language.LocalizedValue<string>;
	name?: string;
	objectDefinitionSettings?: {
		name:
			| 'acceptAllGroups'
			| 'acceptedGroupExternalReferenceCodes'
			| 'allowStandaloneObjectEntry';
		value: string;
	}[];
	objectFields?: ObjectField[];
	objectFolderExternalReferenceCode?:
		| 'L_CMS_CONTENT_STRUCTURES'
		| 'L_CMS_FILE_TYPES'
		| 'L_CMS_STRUCTURE_REPEATABLE_GROUPS';
	objectRelationships?: ObjectRelationship[];
	pluralLabel: Liferay.Language.LocalizedValue<string>;
	restContextPath?: string;
	scope: 'company' | 'depot' | 'site';
	status?: {
		code: number;
	};
	system?: boolean;
	titleObjectFieldName?: string;
	workflowDefinitionLinks?: {
		groupExternalReferenceCode: string;
		workflowDefinitionName: string;
	}[];
};

export type ObjectDefinitions = Record<
	ObjectDefinition['externalReferenceCode'],
	ObjectDefinition
>;

export type StateFlowValue = {
	id: number;
	objectStates: {
		id: number;
		key: string;
		objectStateTransitions: {
			key: string;
		}[];
	}[];
};
