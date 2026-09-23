/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectField} from '../../common/types/ObjectDefinition';
import buildLocalizedValue from '../../common/utils/buildLocalizedValue';
import {
	DefaultLanguageLabels,
	getDefaultLanguageLabel,
} from '../../common/utils/defaultLanguageLabels';
import {Uuid} from '../types/Uuid';
import getRandomId from './getRandomId';
import getUuid from './getUuid';
import normalizeString from './normalizeString';

// Constants

export const FIELD_TYPES = [
	'text',
	'long-text',
	'rich-text',
	'integer',
	'decimal',
	'select-from-list',
	'phone-number',
	'date',
	'datetime',
	'email',
	'boolean',
	'upload',
] as const;

const FIELD_TYPE_LANGUAGE_KEY: Record<FieldType, string> = {
	'boolean': 'boolean',
	'date': 'date',
	'datetime': 'date-and-time',
	'decimal': 'decimal',
	'email': 'email',
	'integer': 'numeric',
	'long-text': 'long-text',
	'phone-number': 'phone-number',
	'rich-text': 'rich-text',
	'select-from-list': 'select-from-list',
	'text': 'text',
	'upload': 'upload',
} as const;

export const FIELD_TYPE_LABEL: Record<FieldType, string> = {
	'boolean': Liferay.Language.get('boolean'),
	'date': Liferay.Language.get('date'),
	'datetime': Liferay.Language.get('date-and-time'),
	'decimal': Liferay.Language.get('decimal'),
	'email': Liferay.Language.get('email'),
	'integer': Liferay.Language.get('numeric'),
	'long-text': Liferay.Language.get('long-text'),
	'phone-number': Liferay.Language.get('phone-number'),
	'rich-text': Liferay.Language.get('rich-text'),
	'select-from-list': Liferay.Language.get('select-from-list'),
	'text': Liferay.Language.get('text'),
	'upload': Liferay.Language.get('upload'),
} as const;

export const FIELD_TYPE_ICON: Record<FieldType, string> = {
	'boolean': 'check-square',
	'date': 'calendar',
	'datetime': 'date-time',
	'decimal': 'decimal',
	'email': 'envelope-closed',
	'integer': 'number',
	'long-text': 'field-area',
	'phone-number': 'phone',
	'rich-text': 'textbox',
	'select-from-list': 'select',
	'text': 'custom-field',
	'upload': 'upload',
} as const;

export function getFieldBusinessType(
	field: Field
): ObjectField['businessType'] {
	if (field.type === 'select-from-list') {
		if ((field as SelectFromListField).multiselection) {
			return 'MultiselectPicklist';
		}

		return 'Picklist';
	}

	switch (field.type) {
		case 'boolean':
			return 'Boolean';
		case 'date':
			return 'Date';
		case 'datetime':
			return 'DateTime';
		case 'decimal':
			return 'Decimal';
		case 'email':
			return 'EmailAddress';
		case 'integer':
			return 'Integer';
		case 'long-text':
			return 'LongText';
		case 'phone-number':
			return 'PhoneNumber';
		case 'rich-text':
			return 'RichText';
		case 'text':
			return 'Text';
		case 'upload':
			return 'Attachment';
		default:
			throw new Error(`Unsupported field type: ${field.type}`);
	}
}

export const FIELD_TYPE_TO_DB_TYPE: Record<FieldType, string> = {
	'boolean': 'Boolean',
	'date': 'Date',
	'datetime': 'DateTime',
	'decimal': 'Double',
	'email': 'String',
	'integer': 'Integer',
	'long-text': 'Clob',
	'phone-number': 'String',
	'rich-text': 'Clob',
	'select-from-list': 'String',
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
	locked: boolean;
	name: string;
	parent: Uuid;
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

export type EmailField = BaseField & {
	settings: {
		autocompleteDomains?: string;
		autocompleteEnabled?: boolean;
		blockedDomains?: string;
	};
	type: 'email';
} & UniqueValuesSettingsField;

export type LongTextField = BaseField & {
	type: 'long-text';
} & MaxLengthSettingsField;

export type NumericField = BaseField & {
	type: 'integer';
} & UniqueValuesSettingsField;

export type PhoneNumberField = BaseField & {
	settings: {
		country?: string;
		countrySource: 'definedByUser' | 'fixed';
	};
	type: 'phone-number';
} & UniqueValuesSettingsField;

export type SelectFromListField = BaseField & {
	multiselection: boolean;
	picklistId: number;
	type: 'select-from-list';
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
		fileSource: 'userComputerToCMSBasicDocument' | 'CMSBasicDocument';
		maximumFileSize: number;
		showFilesInLibrary?: boolean;
		storageDLFolderPath?: string;
		storageDepotGroup?: string;
	};
};

export type Field =
	| DateTimeField
	| EmailField
	| LongTextField
	| NumericField
	| PhoneNumberField
	| SelectFromListField
	| TextField
	| UploadField
	| (BaseField & {
			settings: {};
			type: Exclude<
				FieldType,
				[
					'datetime',
					'email',
					'long-text',
					'multiselect',
					'numeric',
					'phone-number',
					'select-from-list',
					'text',
					'upload',
				]
			>;
	  });

export type FieldType = (typeof FIELD_TYPES)[number];

// Functions

export function getDefaultField({
	defaultLanguageLabels,
	languageKey,
	locked = false,
	name,
	parent,
	required = false,
	type,
}: {
	defaultLanguageLabels: DefaultLanguageLabels;
	languageKey?: string;
	locked?: boolean;
	name?: string;
	parent: Uuid;
	required?: boolean;
	type: FieldType;
}): Field {
	const resolvedLanguageKey = languageKey ?? FIELD_TYPE_LANGUAGE_KEY[type];

	const defaultLocaleLabel = getDefaultLanguageLabel({
		defaultLanguageLabels,
		key: resolvedLanguageKey,
	});

	const base = {
		erc: getRandomId(),
		indexableConfig: {
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: Liferay.ThemeDisplay.getDefaultLanguageId(),
		},
		label: buildLocalizedValue({
			defaultLanguageLabels,
			key: resolvedLanguageKey,
		}),
		localized: true,
		locked,
		name: name ?? normalizeString(defaultLocaleLabel, {style: 'camel'}),
		parent,
		required,
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
	else if (type === 'select-from-list') {
		return {
			...base,
			multiselection: false,
			picklistId: 0,
			type: 'select-from-list',
		};
	}
	else if (type === 'phone-number') {
		return {
			...base,
			settings: {
				countrySource: 'definedByUser',
			},
			type: 'phone-number',
		};
	}
	else if (type === 'upload') {
		return {
			...base,
			settings: {
				acceptedFileExtensions: 'jpeg, jpg, pdf, png',
				fileSource: 'userComputerToCMSBasicDocument',
				maximumFileSize: 100,
			},
			type: 'upload',
		};
	}

	return {...base, type};
}
