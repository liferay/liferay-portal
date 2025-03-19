/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore

import {PagesVisitor} from './visitors.es';

const DEFAULT_DDM_FIELD_PROPERTIES = new Set([
	'defaultValue',
	'fieldType',
	'indexable',
	'indexType',
	'label',
	'localizable',
	'name',
	'readOnly',
	'repeatable',
	'required',
	'showLabel',
	'tip',
]);

/**
 * Gets a data definition from a field
 *
 * @param {object} field - The field
 * @param {Object[]} field.nestedFields - The array containing all nested fields.
 * 										  It may be undefined
 * @param {object} field.settingsContext - The settings context of a field
 */
export function fieldToDataDefinition({
	nestedFields = [],
	settingsContext,
}: Field) {
	const dataDefinition: DataDefinition = {
		customProperties: {},
		nestedDataDefinitionFields: nestedFields.map((field) =>
			fieldToDataDefinition(field)
		),
	};
	const settingsContextVisitor = new PagesVisitor(settingsContext.pages);

	settingsContextVisitor.mapFields(
		({fieldName, localizable, localizedValue = {}, value}: Field) => {
			if (fieldName === 'predefinedValue') {
				fieldName = 'defaultValue';
			}
			else if (fieldName === 'type') {
				fieldName = 'fieldType';
			}

			const properties = DEFAULT_DDM_FIELD_PROPERTIES.has(fieldName)
				? dataDefinition
				: dataDefinition.customProperties;

			// @ts-ignore

			properties[fieldName] = localizable ? localizedValue : value;
		},
		false
	);

	return dataDefinition;
}

export function getDDMFormFieldSettingsContext({
	dataDefinitionField,
	defaultLanguageId = Liferay.ThemeDisplay.getDefaultLanguageId() as Locale,
	editingLanguageId = defaultLanguageId,
	fieldTypes,
}: {
	dataDefinitionField: DataDefinition;
	defaultLanguageId: Locale;
	editingLanguageId: Locale;
	fieldTypes: FieldType[];
}) {
	const {settingsContext} = fieldTypes.find(({name}) => {
		return name === dataDefinitionField.fieldType;
	}) as FieldType;

	const visitor = new PagesVisitor(settingsContext.pages);

	return {
		...settingsContext,
		pages: visitor.mapFields((field: Field) => {
			const {fieldName, localizable, type} = field;
			const {customProperties} = dataDefinitionField;
			const propertyName =
				_fromDDMFormToDataDefinitionPropertyName(fieldName);

			const propertyValue =
				customProperties &&
				!DEFAULT_DDM_FIELD_PROPERTIES.has(propertyName)
					? customProperties[propertyName]
					: // @ts-ignore

						dataDefinitionField[propertyName];

			const value = propertyValue ?? field.value;

			let localizedValue = {};

			if (localizable) {
				localizedValue = {...propertyValue};
			}

			if (!Object.keys(localizedValue).length) {
				localizedValue = {[defaultLanguageId]: ''};
			}

			const newField = {
				...field,
				defaultLanguageId,
				locale: defaultLanguageId,
				localizedValue,
				value,
			};

			if (type === 'select' && fieldName === 'predefinedValue') {
				newField.multiple =
					dataDefinitionField.customProperties.multiple;
				newField.options = (
					dataDefinitionField.customProperties
						.options as LocalizedValue<unknown>
				)[editingLanguageId];
			}

			return newField;
		}),
	};
}

/**
 * **Private function** exported for test purpose only
 */
export function _fromDDMFormToDataDefinitionPropertyName(propertyName: string) {
	switch (propertyName) {
		case 'fieldName':
			return 'name';
		case 'nestedFields':
			return 'nestedDataDefinitionFields';
		case 'predefinedValue':
			return 'defaultValue';
		case 'type':
			return 'fieldType';
		default:
			return propertyName;
	}
}

interface DataDefinition {
	customProperties: DataDefinitionCustomProperties;
	defaultValue?: unknown;
	fieldType?: unknown;
	indexType?: unknown;
	indexable?: unknown;
	label?: unknown;
	localizable?: unknown;
	name?: unknown;
	nestedDataDefinitionFields: DataDefinition[];
	readOnly?: unknown;
	repeatable?: unknown;
	required?: unknown;
	showLabel?: unknown;
	tip?: unknown;
}

interface DataDefinitionCustomProperties {
	options?: LocalizedValue<unknown>;
	[key: string]: unknown;
}

export interface Field<T = unknown> {
	editOnlyInDefaultLanguage?: boolean;
	fieldName: string;
	localizable?: boolean;
	localizedObjectField?: boolean;
	localizedValue?: LocalizedValue<T>;
	multiple?: unknown;
	nestedFields?: Field[];
	options?: unknown;
	settingsContext: {pages: unknown[]};
	type: FieldTypeName;
	value: T;
}

export interface FieldType {
	label: string;
	name: FieldTypeName;
	settingsContext: {pages: unknown[]};
}

export type FieldTypeName =
	| 'checkbox_multiple'
	| 'captcha'
	| 'checkbox'
	| 'color'
	| 'date'
	| 'document_library'
	| 'fieldset'
	| 'grid'
	| 'help_text'
	| 'image'
	| 'key_value'
	| 'localizable_text'
	| 'multi_language_option_select'
	| 'numeric'
	| 'numeric_input_mask'
	| 'object_field'
	| 'object-relationship'
	| 'options'
	| 'password'
	| 'paragraph'
	| 'redirect_button'
	| 'radio'
	| 'rich_text'
	| 'search_location'
	| 'separator'
	| 'select'
	| 'text'
	| 'validation';

type Locale = Liferay.Language.Locale;
type LocalizedValue<T> = Liferay.Language.LocalizedValue<T>;
