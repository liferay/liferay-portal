/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	addFieldToColumn,
	removeEmptyRows as removeEmptyRowsUtil,
} from './FormSupport.es';
import {FIELD_TYPE_FIELDSET} from './constants';
import {normalizeFieldName} from './fields.es';
import {generateName, getRepeatedIndex, parseName} from './repeatable.es';
import {updateField} from './settingsContext';
import {PagesVisitor} from './visitors.es';

export function addFieldToPage({
	defaultLanguageId,
	editingLanguageId,
	fieldNameGenerator,
	generateFieldNameUsingFieldLabel,
	indexes,
	newField,
	pages,
	parentFieldName,
}) {
	const {columnIndex, pageIndex, rowIndex} = indexes;

	if (!parentFieldName) {
		return addFieldToColumn(
			pages,
			pageIndex,
			rowIndex,
			columnIndex,
			newField
		);
	}

	const visitor = new PagesVisitor(pages);

	return visitor.mapFields(
		(field) => {
			if (field.fieldName === parentFieldName) {
				const nestedFields = field.nestedFields
					? [...field.nestedFields, newField]
					: [newField];

				field = updateField(
					{
						defaultLanguageId,
						editingLanguageId,
						fieldNameGenerator,
						generateFieldNameUsingFieldLabel,
					},
					field,
					'nestedFields',
					nestedFields
				);

				const {rows} = field;
				const pages = addFieldToColumn(
					[
						{
							rows:
								typeof rows === 'string'
									? JSON.parse(rows)
									: rows,
						},
					], // TODO: Check if row can be a string
					0,
					rowIndex,
					columnIndex,
					newField.fieldName
				);

				return updateField(
					{
						defaultLanguageId,
						editingLanguageId,
						fieldNameGenerator,
						generateFieldNameUsingFieldLabel,
					},
					field,
					'rows',
					pages[0].rows
				);
			}

			return field;
		},
		true,
		true
	);
}

export function generateInstanceId(isNumbersOnly) {
	return Math.random()
		.toString(isNumbersOnly ? 10 : 36)
		.substr(2, 8);
}

export function getDefaultFieldName(isOptionField = false, fieldType = '') {
	const defaultFieldName = fieldType?.name
		? normalizeFieldName(fieldType.name)
				.split('_')
				.map((word) => word.charAt(0).toUpperCase() + word.slice(1))
				.join('')
		: isOptionField
			? Liferay.Language.get('option')
			: Liferay.Language.get('field');

	return defaultFieldName + generateInstanceId(true);
}

export function removeField(props, pages, fieldName, removeEmptyRows = true) {
	const visitor = new PagesVisitor(pages);

	const filter = (fields) =>
		fields
			.filter((field) => field.fieldName !== fieldName)
			.map((field) => {
				const nestedFields = field.nestedFields
					? filter(field.nestedFields)
					: [];

				field = updateField(props, field, 'nestedFields', nestedFields);

				if (field.type !== FIELD_TYPE_FIELDSET) {
					return {
						...field,
						nestedFields,
					};
				}

				let rows = [];

				if (field.rows) {
					const visitor = new PagesVisitor([
						{
							rows:
								typeof field.rows === 'string'
									? JSON.parse(field.rows)
									: field.rows || [],
						},
					]);

					const pages = visitor.mapColumns((column) => ({
						...column,
						fields: column.fields.filter(
							(nestedFieldName) => fieldName !== nestedFieldName
						),
					}));

					rows = removeEmptyRows
						? removeEmptyRowsUtil(pages, 0)
						: pages[0].rows;

					field = updateField(props, field, 'rows', rows);
				}

				return {
					...field,
					nestedFields,
					rows,
				};
			})
			.filter(({nestedFields = [], rows = [], type}) => {
				if (
					type === FIELD_TYPE_FIELDSET &&
					!nestedFields.length &&
					!rows.length
				) {
					return false;
				}

				return true;
			});

	return visitor.mapColumns((column) => ({
		...column,
		fields: filter(column.fields),
	}));
}

export function getFieldProperties(
	{pages},
	defaultLanguageId,
	editingLanguageId
) {
	const properties = {};
	const visitor = new PagesVisitor(pages);

	visitor.mapFields(
		({fieldName, localizable, localizedValue = {}, type, value}) => {
			if (
				localizable &&
				localizedValue[editingLanguageId] !== undefined
			) {
				properties[fieldName] = localizedValue[editingLanguageId];
			}
			else if (localizable && localizedValue[defaultLanguageId]) {
				properties[fieldName] = localizedValue[defaultLanguageId];
			}
			else if (type === 'options') {
				if (!value[editingLanguageId] && value[defaultLanguageId]) {
					properties[fieldName] = value[defaultLanguageId];
				}
				else {
					properties[fieldName] = value[editingLanguageId];
				}
			}
			else if (type === 'validation') {
				if (!value.errorMessage[editingLanguageId]) {
					value.errorMessage[editingLanguageId] =
						value.errorMessage[defaultLanguageId];
				}

				/* TODO: define a proper parameter type and apply it here */
				if (!value.parameter[editingLanguageId]) {
					value.parameter[editingLanguageId] =
						value.parameter[defaultLanguageId];
				}

				properties[fieldName] = value;
			}
			else {
				properties[fieldName] = value;
			}
		}
	);

	return properties;
}

export function normalizeSettingsContextPages(
	pages,
	defaultLanguageId,
	editingLanguageId,
	fieldType,
	generatedFieldName
) {
	const visitor = new PagesVisitor(pages);

	return visitor.mapFields(
		(field) => {
			const {fieldName} = field;

			if (fieldName === 'fieldReference' || fieldName === 'name') {
				field = {
					...field,
					value: generatedFieldName,
				};
			}
			else if (fieldName === 'label') {
				const localizedValue = {
					...field.localizedValue,
					[editingLanguageId]: fieldType.label,
				};

				if (
					editingLanguageId !== defaultLanguageId &&
					!localizedValue[defaultLanguageId]
				) {
					localizedValue[defaultLanguageId] = fieldType.label;
				}

				field = {
					...field,
					localizedValue,
					type: 'text',
					value: fieldType.label,
				};
			}
			else if (fieldName === 'type') {
				field = {
					...field,
					value: fieldType.name,
				};
			}
			else if (fieldName === 'validation') {
				field = {
					...field,
					validation: {
						...field.validation,
						fieldName: generatedFieldName,
					},
				};
			}

			if (field.dataType === 'ddm-options') {
				field = {
					...field,
					value: {
						...field.value,
						[editingLanguageId]:
							field.value[editingLanguageId] ??
							field.value[field.locale],
					},
				};

				field.value[defaultLanguageId] =
					field.value[defaultLanguageId] ??
					field.value[editingLanguageId];
			}

			if (field.localizable) {
				const {localizedValue} = field;

				localizedValue[defaultLanguageId] =
					localizedValue[defaultLanguageId] ??
					localizedValue[field.locale];

				const availableLocales = Object.keys(localizedValue);

				availableLocales.forEach((availableLocale) => {
					if (
						availableLocale !== defaultLanguageId &&
						availableLocale !== editingLanguageId &&
						!localizedValue[availableLocale]
					) {
						delete localizedValue[availableLocale];
					}
				});
			}

			const instanceId = generateInstanceId();

			if (field.type === 'rich_text' && field.editorConfig) {
				field = {
					...field,
					editorConfig: updateEditorConfigInstanceId(
						field.editorConfig,
						instanceId
					),
				};
			}

			return {
				...field,
				defaultLanguageId,
				instanceId,
				locale: defaultLanguageId,
				name: generateName(field.name, {
					instanceId,
					repeatedIndex: getRepeatedIndex(field.name),
				}),
			};
		},
		false,
		true
	);
}

export function createField({
	defaultLanguageId,
	editingLanguageId,
	fieldNameGenerator,
	fieldType,
	portletNamespace,
	skipFieldNameGeneration = false,
	useFieldName = '',
}) {
	let newFieldName = useFieldName;

	if (!useFieldName) {
		if (skipFieldNameGeneration) {
			const {settingsContext} = fieldType;
			const visitor = new PagesVisitor(settingsContext.pages);

			visitor.mapFields(({fieldName, value}) => {
				if (fieldName === 'name') {
					newFieldName = value;
				}
			});
		}
		else {
			newFieldName = fieldNameGenerator(
				getDefaultFieldName(false, fieldType)
			);
		}
	}

	const instanceId = generateInstanceId();

	const newField = {
		...fieldType,
		fieldName: newFieldName,
		fieldReference: newFieldName,
		name: generateName(null, {
			editingLanguageId,
			fieldName: newFieldName,
			instanceId,
			portletNamespace,
			repeatedIndex: 0,
		}),
		settingsContext: {
			...fieldType.settingsContext,
			defaultLanguageId,
			editingLanguageId,
			pages: normalizeSettingsContextPages(
				[...fieldType.settingsContext.pages],
				defaultLanguageId,
				editingLanguageId,
				fieldType,
				newFieldName
			),
			type: fieldType.name,
		},
	};

	const {editorConfig, fieldName, fieldReference, name, settingsContext} =
		newField;

	return {
		...getFieldProperties(
			settingsContext,
			defaultLanguageId,
			editingLanguageId
		),
		editorConfig,
		fieldName,
		fieldReference,
		instanceId,
		name,
		settingsContext,
		type: fieldType.name,
	};
}

export function updateEditorConfigInstanceId(editorConfig, instanceId) {
	const updatedEditorConfig = {...editorConfig};
	for (const [key, value] of Object.entries(updatedEditorConfig)) {
		if (typeof value === 'string') {
			const parsedName = parseName(decodeURIComponent(value));

			if (parsedName.instanceId) {
				updatedEditorConfig[key] = value.replace(
					parsedName.instanceId,
					instanceId
				);
			}
		}
	}

	return updatedEditorConfig;
}

export function updateInputMaskProperties(editingLanguageId, field) {
	let inputMaskFormat = '';
	let numericInputMask = {};
	let predefinedValueField;
	let validationField;

	const visitor = new PagesVisitor(field.settingsContext.pages);

	visitor.visitFields((setting) => {
		if (setting.fieldName === 'inputMaskFormat') {
			inputMaskFormat = setting.localizedValue?.[editingLanguageId];
		}
		else if (setting.fieldName === 'numericInputMask') {
			numericInputMask = setting.localizedValue?.[editingLanguageId];

			if (typeof numericInputMask === 'string') {
				numericInputMask = JSON.parse(numericInputMask);
			}
		}
		else if (setting.fieldName === 'predefinedValue') {
			predefinedValueField = setting;
		}
		else if (setting.fieldName === 'validation') {
			validationField = setting;
		}
	});

	field.inputMaskFormat = inputMaskFormat;
	predefinedValueField.inputMaskFormat = inputMaskFormat;
	validationField.inputMaskFormat = inputMaskFormat;

	Object.keys(numericInputMask).forEach((key) => {
		field[key] = numericInputMask[key];
		predefinedValueField[key] = numericInputMask[key];
		validationField[key] = numericInputMask[key];
	});
}

export function formatFieldName(instanceId, languageId, value) {
	return `ddm$$${value}$${instanceId}$0$$${languageId}`;
}

export function getField(pages, fieldName) {
	const visitor = new PagesVisitor(pages);

	return visitor.findField((field) => field.fieldName === fieldName);
}

export function getParentField(pages, fieldName) {
	const visitor = new PagesVisitor(pages);

	return visitor.findField((field) => {
		const nestedFieldsVisitor = new PagesVisitor(field.nestedFields || []);

		return nestedFieldsVisitor.containsField(fieldName);
	});
}

export function localizeField(field, defaultLanguageId, editingLanguageId) {
	let value = field.value;

	if (
		field.dataType === 'json' &&
		field.fieldName !== 'rows' &&
		typeof value === 'object'
	) {
		value = JSON.stringify(value);
	}

	if (
		field.dataType === 'json' &&
		field.fieldName === 'rows' &&
		typeof value === 'string'
	) {
		value = JSON.parse(value);
	}

	if (field.localizable && field.localizedValue) {
		let localizedValue = field.localizedValue[editingLanguageId];

		if (localizedValue === undefined || localizedValue === '') {
			localizedValue = field.localizedValue[defaultLanguageId];
		}

		if (localizedValue !== undefined) {
			value = localizedValue;
		}
	}
	else if (field.dataType === 'ddm-options') {
		if (value[editingLanguageId] === undefined) {
			value = {
				...value,
				[editingLanguageId]:
					value[defaultLanguageId]?.map((option) => {
						return {...option, edited: false};
					}) ?? [],
			};
		}
		else {
			value = {
				...value,
				[editingLanguageId]: [
					...value[editingLanguageId].map((option) => {
						if (
							typeof option.edited === 'undefined' ||
							option.edited
						) {
							return option;
						}

						const {label} = value[defaultLanguageId].find(
							(defaultOption) =>
								defaultOption.value === option.value
						);

						return {...option, edited: false, label};
					}),
				],
			};
		}
	}

	return {
		...field,
		defaultLanguageId,
		editingLanguageId,
		localizedValue: {
			...(field.localizedValue || {}),
			[editingLanguageId]: value,
		},
		value,
	};
}
