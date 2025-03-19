/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	generateInstanceId,
	getField,
	getFieldProperties,
	localizeField,
	updateInputMaskProperties,
} from '../../utils/fieldSupport';
import {generateName, getRepeatedIndex} from '../../utils/repeatable.es';
import {PagesVisitor} from '../../utils/visitors.es';
import {EVENT_TYPES} from '../actions/eventTypes.es';

const deleteLanguageId = (pages, ...languageIds) => {
	const visitor = new PagesVisitor(pages);

	return visitor.mapFields((field) => {
		const {localizedValue} = field;
		const updatedField = {...field};

		if (localizedValue) {
			const newLocalizedValue = {...localizedValue};

			languageIds.forEach((languageId) => {
				delete newLocalizedValue[languageId];
			});

			updatedField.localizedValue = newLocalizedValue;
		}

		if (field.settingsContext) {
			updatedField.settingsContext = {
				...field.settingsContext,
				pages: deleteLanguageId(
					field.settingsContext.pages,
					...languageIds
				),
			};
		}

		return updatedField;
	});
};

const getLocalizedValue = ({
	defaultLanguageId,
	editingLanguageId,
	localizable,
	localizedValue,
	localizedValueEdited,
	type,
	value,
}) => {
	if (!localizable) {
		return value;
	}

	let _value;

	const defaultValue = localizedValue[defaultLanguageId];

	if (localizedValue) {
		if (
			localizedValue[editingLanguageId] !== null &&
			localizedValue[editingLanguageId] !== undefined
		) {
			if (
				Array.isArray(localizedValue[editingLanguageId]) &&
				!localizedValue[editingLanguageId]?.length &&
				!localizedValueEdited?.[editingLanguageId]
			) {
				_value = defaultValue;
			}
			else {
				_value = localizedValue[editingLanguageId];
			}
		}
		else if (defaultValue) {
			_value = defaultValue;
		}
	}

	switch (type) {
		case 'color':
		case 'numeric':
		case 'select':
		case 'text': {
			return _value;
		}
		case 'image': {
			try {
				return JSON.parse(value);
			}
			catch (error) {
				return _value;
			}
		}
		default:
			try {
				return JSON.parse(_value);
			}
			catch (error) {
				return _value;
			}
	}
};

const updateFieldLanguage = ({
	availableLanguageIds,
	dataSourceType,
	defaultLanguageId,
	editingLanguageId,
	instanceId,
	name,
	options,
	settingsContext,
	type,
}) => {
	const settingsVisitor = new PagesVisitor(settingsContext.pages);

	const fieldOptions = settingsVisitor.findField(
		({fieldName}) => fieldName === 'options'
	);

	const currentOptions =
		fieldOptions?.value[editingLanguageId] ??
		fieldOptions?.value[defaultLanguageId];

	const pages = settingsVisitor.mapFields((field) => {
		const updatedField = localizeField(
			field,
			defaultLanguageId,
			editingLanguageId
		);

		if (currentOptions && field.fieldName === 'predefinedValue') {
			updatedField.options = currentOptions;
		}

		updatedField.locale = editingLanguageId;

		return updatedField;
	});

	const newSettingsContext = {
		...settingsContext,
		availableLanguageIds,
		defaultLanguageId,
		pages,
	};

	const newField = {
		...getFieldProperties(
			newSettingsContext,
			defaultLanguageId,
			editingLanguageId
		),
		locale: editingLanguageId,
		name: generateName(name, {
			instanceId: instanceId || generateInstanceId(),
			repeatedIndex: getRepeatedIndex(name),
		}),
		settingsContext: newSettingsContext,
	};

	if (
		type === 'select' &&
		dataSourceType &&
		dataSourceType.includes('data-provider')
	) {
		return {
			...newField,
			options,
		};
	}

	return newField;
};

const removeLanguageFromForm = (focusedField, pages, ...deletedLanguageIds) => {
	let updatedPages = deleteLanguageId(pages, ...deletedLanguageIds);

	const visitor = new PagesVisitor(updatedPages);

	let updatedFocusedField = {};

	// TODO: Supposed to be a visitor.findField() but is not working

	visitor.mapFields((field) => {
		if (field.instanceId === focusedField.instanceId) {
			updatedFocusedField = field;
		}
	});

	updatedPages = visitor.mapPages((page) => {
		const {contentRenderer, localizedDescription, localizedTitle} = page;

		if (contentRenderer === 'success') {
			return page;
		}

		deletedLanguageIds.forEach((languageId) => {
			delete localizedDescription[languageId];
			delete localizedTitle[languageId];
		});

		return {
			...page,
			localizedDescription,
			localizedTitle,
		};
	});

	return {
		focusedField: updatedFocusedField,
		pages: updatedPages,
	};
};

/**
 * NOTE: This is a literal copy of the old LayoutProvider logic. Small changes
 * were made only to adapt to the reducer.
 */
export default function languageReducer(state, action) {
	switch (action.type) {
		case EVENT_TYPES.LANGUAGE.CHANGE: {
			const {
				availableLanguageIds,
				defaultLanguageId: prevDefaultLanguageId,
				focusedField,
			} = state;
			const {
				defaultLanguageId = prevDefaultLanguageId,
				editingLanguageId,
				pages,
			} = action.payload;

			const visitor = new PagesVisitor(pages ?? state.pages);

			const newPages = visitor.mapFields(
				({
					localizable,
					localizedValue,
					localizedValueEdited,
					value: previousValue,
					...field
				}) => {

					// When languageReducer is used in the context of the
					// Form Builder, the fields contain settingsContext
					// which we also need to update but do not exist within
					// the fields in the settingsContext structure.

					if (field.settingsContext) {
						const newField = {
							...field,
							...updateFieldLanguage({
								...field,
								availableLanguageIds,
								defaultLanguageId,
								editingLanguageId,
							}),
							value: previousValue,
						};

						if (newField.inputMask) {
							updateInputMaskProperties(
								editingLanguageId,
								newField
							);
						}

						return newField;
					}

					return {
						value: getLocalizedValue({
							defaultLanguageId,
							editingLanguageId,
							localizable,
							localizedValue,
							localizedValueEdited,
							type: field.type,
							value: previousValue,
						}),
					};
				},
				true,
				true
			);

			return {
				defaultLanguageId,
				editingLanguageId,
				focusedField:
					getField(newPages, focusedField?.fieldName) ?? focusedField,
				pages: newPages,
			};
		}
		case EVENT_TYPES.LANGUAGE.ADD: {
			const {languageId} = action.payload;
			const {availableLanguageIds} = state;

			if (availableLanguageIds.includes(languageId)) {
				return state;
			}

			return {
				availableLanguageIds: [...availableLanguageIds, languageId],
			};
		}
		case EVENT_TYPES.LANGUAGE.LOCALES_DROPDOWN_CHANGE: {
			const {defaultLanguageId: prevDefaultLanguageId, focusedField} =
				state;

			const {
				defaultLanguageId = prevDefaultLanguageId,
				editingLanguageId,
				pages,
			} = action.payload;

			const visitor = new PagesVisitor(pages ?? state.pages);

			const newPages = visitor.mapFields(
				({localizable, value}) => {
					if (localizable) {
						return {
							value: {
								...value,
								[editingLanguageId]:
									value[editingLanguageId] ??
									value[defaultLanguageId],
							},
						};
					}
				},
				true,
				true
			);

			return {
				defaultLanguageId,
				editingLanguageId,
				focusedField:
					getField(newPages, focusedField?.fieldName) ?? focusedField,
				pages: newPages,
			};
		}
		case EVENT_TYPES.LANGUAGE.UPDATE: {
			const {availableLanguageIds, focusedField, pages} = state;

			const activeLanguageIds = action.payload.activeLanguageIds;

			const activeLanguageSet = new Set(activeLanguageIds);
			const deletedLanguageIds = availableLanguageIds.filter(
				(languageId) => !activeLanguageSet.has(languageId)
			);

			const availableLanguageSet = new Set(availableLanguageIds);
			const addedLanguageIds = activeLanguageIds.filter(
				(languageId) => !availableLanguageSet.has(languageId)
			);

			if (!deletedLanguageIds.length) {
				return addedLanguageIds.length
					? {availableLanguageIds: [...activeLanguageIds]}
					: state;
			}

			return {
				availableLanguageIds: activeLanguageIds,
				...removeLanguageFromForm(
					focusedField,
					pages,
					...deletedLanguageIds
				),
			};
		}
		case EVENT_TYPES.LANGUAGE.DELETE: {
			const {languageIds} = action.payload;
			const {availableLanguageIds, focusedField, pages} = state;

			return {
				availableLanguageIds: availableLanguageIds.filter(
					(id) => !languageIds.includes(id)
				),
				...removeLanguageFromForm(focusedField, pages, ...languageIds),
			};
		}
		default:
			return state;
	}
}
