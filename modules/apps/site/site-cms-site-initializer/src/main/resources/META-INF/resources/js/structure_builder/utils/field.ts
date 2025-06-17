/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Uuid} from '../types/Uuid';
import getRandomId from './getRandomId';
import getUuid from './getUuid';
import normalizeName from './normalizeName';

// Constants

export const FIELD_TYPES = [
	'text',
	'long-text',
	'rich-text',
	'integer',
	'decimal',
	'single-select',
	'multiselect',
	'date',
	'datetime',
	'boolean',
	'upload',
] as const;

export const FIELD_TYPE_LABEL: Record<FieldType, string> = {
	'boolean': Liferay.Language.get('boolean'),
	'date': Liferay.Language.get('date'),
	'datetime': Liferay.Language.get('date-and-time'),
	'decimal': Liferay.Language.get('decimal'),
	'integer': Liferay.Language.get('numeric'),
	'long-text': Liferay.Language.get('long-text'),
	'multiselect': Liferay.Language.get('multiselect'),
	'rich-text': Liferay.Language.get('rich-text'),
	'single-select': Liferay.Language.get('single-select'),
	'text': Liferay.Language.get('text'),
	'upload': Liferay.Language.get('upload'),
} as const;

export const FIELD_TYPE_ICON: Record<FieldType, string> = {
	'boolean': 'check-square',
	'date': 'calendar',
	'datetime': 'date-time',
	'decimal': 'decimal',
	'integer': 'number',
	'long-text': 'field-area',
	'multiselect': 'select-from-list',
	'rich-text': 'textbox',
	'single-select': 'select',
	'text': 'custom-field',
	'upload': 'upload',
} as const;

export const FIELD_TYPE_TO_BUSINESS_TYPE: Record<FieldType, string> = {
	'boolean': 'Boolean',
	'date': 'Date',
	'datetime': 'DateTime',
	'decimal': 'Decimal',
	'integer': 'Integer',
	'long-text': 'LongText',
	'multiselect': 'MultiselectPicklist',
	'rich-text': 'RichText',
	'single-select': 'Picklist',
	'text': 'Text',
	'upload': 'Attachment',
} as const;

export const FIELD_TYPE_TO_DB_TYPE: Record<FieldType, string> = {
	'boolean': 'Boolean',
	'date': 'Date',
	'datetime': 'DateTime',
	'decimal': 'Double',
	'integer': 'Integer',
	'long-text': 'Clob',
	'multiselect': 'String',
	'rich-text': 'Clob',
	'single-select': 'String',
	'text': 'String',
	'upload': 'Long',
} as const;

// Types

type BaseField = {
	erc: string;
	indexableConfig:
		| {
				indexed: false;
		  }
		| {
				indexed: true;
				indexedAsKeyword: boolean;
				indexedLanguageId?: Liferay.Language.Locale;
		  };
	label: Liferay.Language.LocalizedValue<string>;
	localized: boolean;
	name: string;
	required: boolean;
	settings: {};
	uuid: Uuid;
};

export type UniqueValuesSettingsField = {
	settings: {
		uniqueValues?: boolean;
	};
};

export type MaxLengthSettingsField = {
	settings: {
		maxLength?: number;
		showCounter?: boolean;
	};
};

export type DateTimeField = BaseField & {
	settings: {timeStorage: 'convertToUTC' | 'useInputAsEntered'};
	type: 'datetime';
};

export type LongTextField = BaseField & {
	type: 'long-text';
} & MaxLengthSettingsField;

export type MultiselectField = BaseField & {
	picklistId: number;
	type: 'multiselect';
};

export type NumericField = BaseField & {
	type: 'integer';
} & UniqueValuesSettingsField;

export type SingleSelectField = BaseField & {
	picklistId: number;
	type: 'single-select';
};

export type TextField = BaseField & {
	type: 'text';
} & MaxLengthSettingsField &
	UniqueValuesSettingsField;

export type UploadField = BaseField & {
	type: 'upload';
} & {
	settings: {
		acceptedFileExtensions: string;
		fileSource: 'userComputer' | 'documentsAndMedia';
		maximumFileSize: number;
		showFilesInDocumentsAndMedia?: boolean;
		storageDLFolderPath?: string;
	};
};

export type Field =
	| DateTimeField
	| LongTextField
	| MultiselectField
	| NumericField
	| SingleSelectField
	| TextField
	| UploadField
	| (BaseField & {
			settings: {};
			type: Exclude<
				FieldType,
				[
					'datetime',
					'long-text',
					'multiselect',
					'numeric',
					'single-select',
					'text',
					'upload',
				]
			>;
	  });

export type FieldType = (typeof FIELD_TYPES)[number];

export type FieldBusinessType =
	(typeof FIELD_TYPE_TO_BUSINESS_TYPE)[keyof typeof FIELD_TYPE_TO_BUSINESS_TYPE];

// Functions

export function getDefaultField({
	label,
	name,
	type,
}: {
	label?: string;
	name?: string;
	type: FieldType;
}): Field {
	const base = {
		erc: getRandomId(),
		indexableConfig: {
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: Liferay.ThemeDisplay.getDefaultLanguageId(),
		},
		label: {
			[Liferay.ThemeDisplay.getDefaultLanguageId()]:
				label ?? FIELD_TYPE_LABEL[type],
		},
		localized: Liferay.FeatureFlags['LPD-32050'],
		name: name ?? normalizeName(type),
		required: false,
		settings: {},
		uuid: getUuid(),
	};

	if (type === 'datetime') {
		return {
			...base,
			settings: {
				timeStorage: 'convertToUTC',
			},
			type: 'datetime',
		};
	}
	else if (type === 'upload') {
		return {
			...base,
			settings: {
				acceptedFileExtensions: 'jpeg, jpg, pdf, png',
				fileSource: 'userComputer',
				maximumFileSize: 100,
			},
			type: 'upload',
		};
	}
	else if (type === 'single-select') {
		return {
			...base,
			picklistId: 0,
			type: 'single-select',
		};
	}
	else if (type === 'multiselect') {
		return {
			...base,
			picklistId: 0,
			type: 'multiselect',
		};
	}

	return {...base, type};
}
