/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';

import {
	WebContentField,
	addVisibleFieldsets,
	getAllFieldsetsFromName,
	showField,
	showFilteredFields,
} from '../../../src/main/resources/META-INF/resources/js/api/FieldBase/translation';

const fieldLocalizableNotTranslated: WebContentField = {
	fieldName: 'Text567',
	localizable: true,
	localizedValueEdited: {en_US: 'value'},
	name: 'Text567',
	settingsContext: {pages: []},
	type: 'text',
	value: 'value',
};

const fieldLocalizableTranslated: WebContentField = {
	fieldName: 'Text890',
	localizable: true,
	localizedValueEdited: {en_US: 'value', pt_BR: 'valor'},
	name: 'Text890#Fieldset123$0$$en_US',
	settingsContext: {pages: []},
	type: 'text',
	value: 'value',
};

const fieldNotLocalizable: WebContentField = {
	fieldName: 'Text123',
	localizable: false,
	name: 'Text123',
	settingsContext: {pages: []},
	type: 'text',
	value: 'value',
};

const fieldset: WebContentField = {
	fieldName: 'Fieldset123',
	localizable: false,
	name: 'Fieldset123',
	nestedFields: [fieldLocalizableTranslated],
	settingsContext: {pages: []},
	type: 'fieldset',
};

describe('addVisibleFieldsets(visibleFieldsets, name)', () => {
	it('adds all the fieldsets that should be visible inside the set', () => {
		const visibleFieldsets = new Set<string>();

		addVisibleFieldsets({
			name: 'ddm$$Fieldset123$0#Fieldset456$0#Fieldset789$0$$en_US',
			visibleFieldsets,
		});

		expect(visibleFieldsets.has('Fieldset123')).toBe(true);
		expect(visibleFieldsets.has('Fieldset456')).toBe(true);
		expect(visibleFieldsets.has('Fieldset789')).toBe(true);
	});
});

describe('getAllFieldsetsFromName(name)', () => {
	it('returns an array of all the fieldsets fieldnames related to the corresponding nested field', () => {
		let fieldsets = getAllFieldsetsFromName(
			'ddm$$FirstFieldset$0#SecondFieldset$0#ThirdFieldset$0$$en_US'
		);

		expect(fieldsets[0]).toBe('FirstFieldset');
		expect(fieldsets[1]).toBe('SecondFieldset');
		expect(fieldsets[2]).toBe('ThirdFieldset');
		expect(fieldsets.length).toBe(3);

		fieldsets = getAllFieldsetsFromName(
			'Field||set123Fieldset$#567Fieldset890$0$$en_US'
		);
		expect(fieldsets[0]).toBe('567Fieldset890');
		expect(fieldsets.length).toBe(1);

		fieldsets = getAllFieldsetsFromName('');
		expect(fieldsets.length).toBe(0);
	});
});

describe('showField(editingLanguageId, field, filter)', () => {
	it('returns false if field is not localizable regardless of filter choice', () => {
		expect(
			showField({
				editingLanguageId: 'en_US',
				field: fieldNotLocalizable,
				filter: 'translated',
			})
		).toBe(false);

		expect(
			showField({
				editingLanguageId: 'en_US',
				field: fieldNotLocalizable,
				filter: 'untranslated',
			})
		).toBe(false);
	});

	it('returns false if the field is localizable but is not translated and filter is set as translated', () => {
		expect(
			showField({
				editingLanguageId: 'pt_BR',
				field: fieldLocalizableNotTranslated,
				filter: 'translated',
			})
		).toBe(false);
	});

	it('returns false if the field is localizable but is translated and filter is set as untranslated', () => {
		expect(
			showField({
				editingLanguageId: 'pt_BR',
				field: fieldLocalizableTranslated,
				filter: 'untranslated',
			})
		).toBe(false);
	});

	it('returns true if the field is localizable, translated and filter is set as translated', () => {
		expect(
			showField({
				editingLanguageId: 'pt_BR',
				field: fieldLocalizableTranslated,
				filter: 'translated',
			})
		).toBe(true);
	});

	it('returns true if the field is localizable, untranslated and filter is set as untranslated', () => {
		expect(
			showField({
				editingLanguageId: 'pt_BR',
				field: fieldLocalizableNotTranslated,
				filter: 'untranslated',
			})
		).toBe(true);
	});
});

describe('showFilteredFields(editingLanguageId, fields, filter, visibleFieldsets)', () => {
	it('returns an array of fields having the visible properties correctly updated using the filter translated', () => {
		const editingLanguageId = 'pt_BR';
		const visibleFieldsets = new Set<string>();

		const fields = showFilteredFields({
			editingLanguageId,
			fields: [fieldset],
			filter: 'translated',
			visibleFieldsets,
		});

		fields.forEach((field: WebContentField) => {
			if (
				field.name === 'Fieldset123' ||
				field.name === 'Text890#Fieldset123'
			) {
				expect(field.disabled).toBe(false);
				expect(field.hidden).toBe(false);
				expect(field.visible).toBe(true);
			}
			else {
				expect(field.disabled).toBe(true);
				expect(field.hidden).toBe(true);
				expect(field.visible).toBe(false);
			}
		});
	});

	it('returns an array of fields having the visible properties correctly updated using the filter untranslated', () => {
		const editingLanguageId = 'pt_BR';
		const visibleFieldsets = new Set<string>();

		const fields = showFilteredFields({
			editingLanguageId,
			fields: [fieldset],
			filter: 'untranslated',
			visibleFieldsets,
		});

		fields.forEach((field: WebContentField) => {
			if (
				field.name === 'Fieldset123' ||
				field.name === 'Text890#Fieldset123'
			) {
				expect(field.disabled).toBe(true);
				expect(field.hidden).toBe(true);
				expect(field.visible).toBe(false);
			}
			else {
				expect(field.disabled).toBe(false);
				expect(field.hidden).toBe(false);
				expect(field.visible).toBe(true);
			}
		});
	});
});
