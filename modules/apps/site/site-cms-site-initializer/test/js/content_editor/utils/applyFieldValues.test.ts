/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import applyFieldValues from '../../../../src/main/resources/META-INF/resources/js/content_editor/utils/applyFieldValues';

function createForm(html: string): HTMLFormElement {
	const form = document.createElement('form');

	form.innerHTML = html;

	return form;
}

describe('applyFieldValues', () => {
	it('writes a plain field value and fires input and change events', () => {
		const form = createForm('<input name="ObjectField_title" value="" />');

		const input = form.querySelector('input') as HTMLInputElement;

		const inputListener = jest.fn();
		const changeListener = jest.fn();

		input.addEventListener('input', inputListener);
		input.addEventListener('change', changeListener);

		applyFieldValues(form, {title: 'Generated'});

		expect(input.value).toBe('Generated');
		expect(inputListener).toHaveBeenCalledTimes(1);
		expect(changeListener).toHaveBeenCalledTimes(1);
	});

	it('writes through the native setter when a control ignores direct assignment', () => {
		const form = createForm('<input name="ObjectField_title" value="" />');

		const input = form.querySelector('input') as HTMLInputElement;

		const nativeValueGetter = Object.getOwnPropertyDescriptor(
			HTMLInputElement.prototype,
			'value'
		)?.get;

		Object.defineProperty(input, 'value', {
			configurable: true,
			get() {
				return nativeValueGetter?.call(this);
			},
			set() {},
		});

		applyFieldValues(form, {title: 'Generated'});

		expect(nativeValueGetter?.call(input)).toBe('Generated');
	});

	it('replaces a RichText field through the editor model', () => {
		const form = createForm(
			'<div class="rich-text-input" data-field-name="ObjectField_body">' +
				'<div class="lfr-ck"><div class="ck ck-editor">' +
				'<div class="ck-editor__editable"></div></div></div></div>'
		);

		const editor = form.querySelector('.ck-editor');

		const insertContent = jest.fn();
		const remove = jest.fn();
		const createRangeIn = jest.fn(() => 'range');

		const root = {};
		const modelFragment = {};
		const viewFragment = {};

		(editor as any).ckeditorInstance = {
			data: {
				processor: {toView: jest.fn(() => viewFragment)},
				toModel: jest.fn(() => modelFragment),
			},
			getData: jest.fn(),
			model: {
				change: (callback: (writer: unknown) => void) =>
					callback({createRangeIn, remove}),
				document: {getRoot: () => root},
				insertContent,
			},
			setData: jest.fn(),
		};

		applyFieldValues(form, {body: '<p>New</p>'});

		expect(remove).toHaveBeenCalledWith('range');
		expect(insertContent).toHaveBeenCalledWith(modelFragment, root);
	});

	it('falls back to setData when the editor has no model API', () => {
		const form = createForm(
			'<div class="rich-text-input" data-field-name="ObjectField_body">' +
				'<div class="lfr-ck"><div class="ck ck-editor">' +
				'<div class="ck-editor__editable"></div></div></div></div>'
		);

		const editor = form.querySelector('.ck-editor');

		const setData = jest.fn();

		(editor as any).ckeditorInstance = {getData: jest.fn(), setData};

		applyFieldValues(form, {body: '<p>New</p>'});

		expect(setData).toHaveBeenCalledWith('<p>New</p>');
	});

	it('writes only the active locale control for a localized field', () => {
		const form = createForm(
			'<input name="ObjectField_title_en_US" value="" />' +
				'<input name="ObjectField_title_pt_BR" value="" />'
		);

		applyFieldValues(form, {title: 'Generated'}, 'pt_BR');

		expect(
			(
				form.querySelector(
					'[name="ObjectField_title_en_US"]'
				) as HTMLInputElement
			).value
		).toBe('');
		expect(
			(
				form.querySelector(
					'[name="ObjectField_title_pt_BR"]'
				) as HTMLInputElement
			).value
		).toBe('Generated');
	});

	it('falls back to the first control when the locale is absent', () => {
		const form = createForm(
			'<input name="ObjectField_title_en_US" value="" />' +
				'<input name="ObjectField_title_pt_BR" value="" />'
		);

		applyFieldValues(form, {title: 'Generated'}, 'fr_FR');

		expect(
			(
				form.querySelector(
					'[name="ObjectField_title_en_US"]'
				) as HTMLInputElement
			).value
		).toBe('Generated');
	});

	it('ignores a field that has no control', () => {
		const form = createForm('<input name="ObjectField_title" value="" />');

		expect(() => applyFieldValues(form, {missing: 'x'})).not.toThrow();
	});
});
