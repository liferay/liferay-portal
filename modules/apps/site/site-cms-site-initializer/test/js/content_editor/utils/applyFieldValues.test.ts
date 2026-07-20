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

	it('writes a RichText field through the CKEditor instance', () => {
		const form = createForm(
			'<div class="lfr-ck"><div class="ck-editor__editable"></div>' +
				'<input name="ObjectField_body" type="hidden" /></div>'
		);

		const editable = form.querySelector('.ck-editor__editable');

		const setData = jest.fn();

		(editable as any).ckeditorInstance = {getData: jest.fn(), setData};

		applyFieldValues(form, {body: '<p>New</p>'});

		expect(setData).toHaveBeenCalledWith('<p>New</p>');
	});

	it('ignores a field that has no control', () => {
		const form = createForm('<input name="ObjectField_title" value="" />');

		expect(() => applyFieldValues(form, {missing: 'x'})).not.toThrow();
	});
});
