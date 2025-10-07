/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface Column {
	fields: WebContentField[];
	size: number;
}

type WebContentFieldType =
	| 'checkbox_multiple'
	| 'captcha'
	| 'checkbox'
	| 'color'
	| 'date'
	| 'document_library'
	| 'fieldset'
	| 'grid'
	| 'image'
	| 'localizable_text'
	| 'numeric'
	| 'options'
	| 'paragraph'
	| 'radio'
	| 'rich_text'
	| 'separator'
	| 'select'
	| 'text';

export interface WebContentField<T = unknown> {
	disabled?: boolean;
	fieldName: string;
	hidden?: boolean;
	localizable?: boolean;
	localizedValueEdited?: any;
	name: string;
	nestedFields?: WebContentField[];
	settingsContext: {pages: unknown[]};
	type: WebContentFieldType;
	value?: T;
	visible?: boolean;
}

/**
 * Returns an array with all the occurences of the string 'Fieldset' followed by digits.
 * Used to find all fieldsets that are ancestors of a nested field.
 */

export function getAllFieldsetsFromName(name: string) {
	return [...name.matchAll(/(?:\$|#)([^$#]+)\$/g)].map((match) => match[1]);
}

/**
 * Add a fieldset fieldname in the provided set if it appears on the nested field name.
 * Used to track if the fieldset should be shown in the chosen filter section.
 */

export function addVisibleFieldsets({
	name,
	visibleFieldsets,
}: {
	name: string;
	visibleFieldsets: Set<string>;
}) {
	const parsedName = getAllFieldsetsFromName(name);

	if (parsedName) {
		parsedName.forEach((fieldset: string) =>
			visibleFieldsets.add(fieldset)
		);
	}
}

/**
 * Returns a boolean to determine if a field should be shown or not.
 * If the field is not localizable it will never appear.
 * If the filter is translated, the field should have a translation for the editingLanguageId to appear.
 * If the filter is untranslated, the field shouldn't have a translation for the editingLanguageId to appear.
 */

export function showField({
	editingLanguageId,
	field,
	filter,
}: {
	editingLanguageId: string;
	field: WebContentField;
	filter: string;
}) {
	return !!(
		field.localizable &&
		((field.localizedValueEdited?.[editingLanguageId] &&
			filter === 'translated') ||
			(!field.localizedValueEdited?.[editingLanguageId] &&
				filter === 'untranslated'))
	);
}

/**
 * Returns an array of fields with updated props depending on the filter chosen.
 * Considers fieldsets and uses recursion to update the nested fields when necessary.
 */

export function showFilteredFields({
	editingLanguageId,
	fields,
	filter,
	visibleFieldsets,
}: {
	editingLanguageId: string;
	fields: WebContentField[];
	filter: string;
	visibleFieldsets: Set<string>;
}) {
	const newFields = [...fields];

	return newFields.map((field: WebContentField) => {
		if (field.nestedFields) {
			const newNestedFields: WebContentField[] = showFilteredFields({
				editingLanguageId,
				fields: field.nestedFields,
				filter,
				visibleFieldsets,
			});

			const visible = visibleFieldsets.has(field.fieldName);

			return {
				...field,
				disabled: !visible,
				hidden: !visible,
				nestedFields: newNestedFields,
				visible,
			};
		}

		if (showField({editingLanguageId, field, filter})) {
			addVisibleFieldsets({name: field.name, visibleFieldsets});

			return {
				...field,
				disabled: false,
				hidden: false,
				visible: true,
			};
		}
		else {
			return {
				...field,
				disabled: true,
				hidden: true,
				visible: false,
			};
		}
	});
}

/**
 * Returns the updated page depending on the filter chosen.
 */

export function getFilteredPage({
	editingLanguageId,
	filter,
	pagesVisitor,
}: {
	editingLanguageId: string;
	filter: string;
	pagesVisitor: any;
}) {
	return pagesVisitor.mapColumns((column: Column) => {
		const visibleFieldsets = new Set<string>();

		return {
			...column,
			fields: showFilteredFields({
				editingLanguageId,
				fields: column.fields,
				filter,
				visibleFieldsets,
			}),
		};
	});
}

/**
 * Returns message for all non localized field cases.
 */

export function getNonLocalizableFieldMessage(
	isLocalizationSupported: boolean | undefined
) {
	return isLocalizationSupported === undefined
		? Liferay.Language.get('this-field-cannot-be-localized')
		: isLocalizationSupported
			? Liferay.Language.get('translation-is-disabled-for-this-field')
			: Liferay.Language.get('this-field-does-not-support-translations');
}
