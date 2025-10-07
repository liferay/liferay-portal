/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';

import {MetadataFieldsManager} from '../../../src/main/resources/META-INF/resources/js/translation_manager/MetadataFieldsManager';
import {
	Field,
	Fields,
} from '../../../src/main/resources/META-INF/resources/js/translation_manager/Types';

export const DEFAULT_FIELDS: Fields = {
	descriptionMapAsXML: {
		en_US: 'English Description',
		es_ES: 'Spanish Description',
	} as Field,

	friendlyURL: {
		en_US: 'english-url',
		es_ES: 'spanish-url',
	} as Field,

	titleMapAsXML: {
		en_US: 'English Title',
		es_ES: 'Spanish Title',
	} as Field,
};

const createHiddenInput = (
	fieldName: string,
	languageId: string,
	value = ''
) => {
	const input = document.createElement('input');
	input.type = 'hidden';
	input.setAttribute('data-field-name', fieldName);
	input.setAttribute('data-languageid', languageId);
	input.value = value;
	document.body.appendChild(input);

	return input;
};

describe('MetadataFieldsManager', () => {
	beforeEach(() => {
		document.body.innerHTML = '';
	});

	describe('buildSelector', () => {
		it('builds correct CSS selector for field name and language id', () => {
			const selector = MetadataFieldsManager.buildSelector(
				'titleMapAsXML',
				'en_US'
			);

			expect(selector).toBe(
				'[type="hidden"][data-field-name="titleMapAsXML"][data-languageid="en_US"]'
			);
		});
	});

	describe('getMetadataValue', () => {
		it('returns value when hidden input exists', () => {
			createHiddenInput('titleMapAsXML', 'en_US', 'test value');

			const value = MetadataFieldsManager.getMetadataValue(
				'titleMapAsXML',
				'en_US'
			);

			expect(value).toBe('test value');
		});

		it('returns null when hidden input does not exist', () => {
			const value = MetadataFieldsManager.getMetadataValue(
				'nonexistent',
				'en_US'
			);

			expect(value).toBeNull();
		});

		it('returns empty string when hidden input has empty value', () => {
			createHiddenInput('titleMapAsXML', 'en_US', '');

			const value = MetadataFieldsManager.getMetadataValue(
				'titleMapAsXML',
				'en_US'
			);

			expect(value).toBe('');
		});
	});

	describe('setMetadataValue', () => {
		it('sets value when hidden input exists', () => {
			const input = createHiddenInput(
				'titleMapAsXML',
				'en_US',
				'old value'
			);

			MetadataFieldsManager.setMetadataValue(
				'titleMapAsXML',
				'en_US',
				'new value',
				'en_US'
			);

			expect(input.value).toBe('new value');
		});

		it('falls back to default language value when current value is empty', () => {
			createHiddenInput('titleMapAsXML', 'en_US', 'Default Title');

			const targetInput = createHiddenInput('titleMapAsXML', 'es_ES', '');

			MetadataFieldsManager.setMetadataValue(
				'titleMapAsXML',
				'es_ES',
				'',
				'en_US'
			);

			expect(targetInput.value).toBe('Default Title');
		});

		it('falls back to default language value when current value is null', () => {
			createHiddenInput(
				'descriptionMapAsXML',
				'en_US',
				'Default Description'
			);

			const targetInput = createHiddenInput(
				'descriptionMapAsXML',
				'ca_ES',
				''
			);

			MetadataFieldsManager.setMetadataValue(
				'descriptionMapAsXML',
				'ca_ES',
				null as any,
				'en_US'
			);

			expect(targetInput.value).toBe('Default Description');
		});

		it('falls back to default language value when current value is undefined', () => {
			createHiddenInput('friendlyURL', 'en_US', 'default-url');

			const targetInput = createHiddenInput('friendlyURL', 'fr_FR', '');

			MetadataFieldsManager.setMetadataValue(
				'friendlyURL',
				'fr_FR',
				undefined as any,
				'en_US'
			);

			expect(targetInput.value).toBe('default-url');
		});
	});

	describe('resetMetadataValue', () => {
		it('removes single hidden input when it exists', () => {
			createHiddenInput('titleMapAsXML', 'en_US', 'test value');

			expect(
				document.querySelectorAll('input[type="hidden"]')
			).toHaveLength(1);

			MetadataFieldsManager.resetMetadataValue('titleMapAsXML', 'en_US');

			expect(
				document.querySelectorAll('input[type="hidden"]')
			).toHaveLength(0);
		});
	});

	describe('getAllMetadataValues', () => {
		it('returns all values for existing fields', () => {
			createHiddenInput('titleMapAsXML', 'en_US', 'title value');
			createHiddenInput(
				'descriptionMapAsXML',
				'en_US',
				'description value'
			);
			createHiddenInput('friendlyURL', 'en_US', 'url value');

			const values = MetadataFieldsManager.getAllMetadataValues(
				DEFAULT_FIELDS,
				'en_US'
			);

			expect(values).toEqual({
				descriptionMapAsXML: 'description value',
				friendlyURL: 'url value',
				titleMapAsXML: 'title value',
			});
		});

		it('returns only values for fields that exist in DOM', () => {
			createHiddenInput('titleMapAsXML', 'en_US', 'title value');

			const values = MetadataFieldsManager.getAllMetadataValues(
				DEFAULT_FIELDS,
				'en_US'
			);

			expect(values).toEqual({
				titleMapAsXML: 'title value',
			});
		});

		it('returns empty object when no fields exist', () => {
			const values = MetadataFieldsManager.getAllMetadataValues(
				DEFAULT_FIELDS,
				'en_US'
			);

			expect(values).toEqual({});
		});

		it('includes fields with empty string values', () => {
			createHiddenInput('titleMapAsXML', 'en_US', '');

			const values = MetadataFieldsManager.getAllMetadataValues(
				DEFAULT_FIELDS,
				'en_US'
			);

			expect(values).toEqual({
				titleMapAsXML: '',
			});
		});
	});

	describe('resetAllMetadataValues', () => {
		it('resets all specified fields for given language', () => {
			createHiddenInput('titleMapAsXML', 'en_US', 'title value');
			createHiddenInput(
				'descriptionMapAsXML',
				'en_US',
				'description value'
			);
			createHiddenInput('friendlyURL', 'en_US', 'url value');
			createHiddenInput('titleMapAsXML', 'es_ES', 'spanish title');

			expect(
				document.querySelectorAll('input[type="hidden"]')
			).toHaveLength(4);

			MetadataFieldsManager.resetAllMetadataValues(
				DEFAULT_FIELDS,
				'en_US'
			);

			expect(
				document.querySelectorAll('input[type="hidden"]')
			).toHaveLength(1);
			expect(
				document.querySelector('[data-languageid="es_ES"]')
			).toBeInTheDocument();
		});
	});

	describe('restoreAllMetadataValues', () => {
		it('restores all values for existing inputs', () => {
			const input1 = createHiddenInput('titleMapAsXML', 'en_US');
			const input2 = createHiddenInput('descriptionMapAsXML', 'en_US');

			const values = {
				descriptionMapAsXML: 'restored description',
				titleMapAsXML: 'restored title',
			};

			MetadataFieldsManager.restoreAllMetadataValues(
				'en_US',
				values,
				'en_US'
			);

			expect(input1.value).toBe('restored title');
			expect(input2.value).toBe('restored description');
		});

		it('handles empty values object', () => {
			const input = createHiddenInput(
				'titleMapAsXML',
				'en_US',
				'original'
			);

			MetadataFieldsManager.restoreAllMetadataValues(
				'en_US',
				{},
				'en_US'
			);

			expect(input.value).toBe('original');
		});

		it('restores partial matches', () => {
			const input1 = createHiddenInput(
				'titleMapAsXML',
				'en_US',
				'original1'
			);
			const input2 = createHiddenInput(
				'descriptionMapAsXML',
				'en_US',
				'original2'
			);

			const values = {
				nonexistent: 'ignored value',
				titleMapAsXML: 'restored title',
			};

			MetadataFieldsManager.restoreAllMetadataValues(
				'en_US',
				values,
				'en_US'
			);

			expect(input1.value).toBe('restored title');
			expect(input2.value).toBe('original2');
		});
	});

	describe('integration tests', () => {
		it('gets, resets, and restores values in sequence', () => {
			createHiddenInput('titleMapAsXML', 'en_US', 'original title');
			createHiddenInput(
				'descriptionMapAsXML',
				'en_US',
				'original description'
			);

			const values = MetadataFieldsManager.getAllMetadataValues(
				DEFAULT_FIELDS,
				'en_US'
			);
			expect(values).toEqual({
				descriptionMapAsXML: 'original description',
				titleMapAsXML: 'original title',
			});

			MetadataFieldsManager.resetAllMetadataValues(
				DEFAULT_FIELDS,
				'en_US'
			);
			expect(
				document.querySelectorAll('input[type="hidden"]')
			).toHaveLength(0);

			createHiddenInput('titleMapAsXML', 'en_US');
			createHiddenInput('descriptionMapAsXML', 'en_US');

			MetadataFieldsManager.restoreAllMetadataValues(
				'en_US',
				values,
				'en_US'
			);

			const restoredTitle = MetadataFieldsManager.getMetadataValue(
				'titleMapAsXML',
				'en_US'
			);
			const restoredDescription = MetadataFieldsManager.getMetadataValue(
				'descriptionMapAsXML',
				'en_US'
			);

			expect(restoredTitle).toBe('original title');
			expect(restoredDescription).toBe('original description');
		});

		it('handles multiple languages independently', () => {
			createHiddenInput('titleMapAsXML', 'en_US', 'english title');
			createHiddenInput('titleMapAsXML', 'es_ES', 'spanish title');
			createHiddenInput(
				'descriptionMapAsXML',
				'en_US',
				'english description'
			);

			const englishValues = MetadataFieldsManager.getAllMetadataValues(
				DEFAULT_FIELDS,
				'en_US'
			);
			const spanishValues = MetadataFieldsManager.getAllMetadataValues(
				DEFAULT_FIELDS,
				'es_ES'
			);

			expect(englishValues).toEqual({
				descriptionMapAsXML: 'english description',
				titleMapAsXML: 'english title',
			});

			expect(spanishValues).toEqual({
				titleMapAsXML: 'spanish title',
			});

			MetadataFieldsManager.resetAllMetadataValues(
				DEFAULT_FIELDS,
				'en_US'
			);

			expect(
				document.querySelectorAll('input[type="hidden"]')
			).toHaveLength(1);
			expect(
				document.querySelector('[data-languageid="es_ES"]')
			).toBeInTheDocument();
		});
	});
});
