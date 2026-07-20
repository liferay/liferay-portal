/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getFieldValues from '../../../../src/main/resources/META-INF/resources/js/content_editor/utils/getFieldValues';

function createForm(html: string): HTMLFormElement {
	const form = document.createElement('form');

	form.innerHTML = html;

	return form;
}

describe('getFieldValues', () => {
	it('reads a plain field value', () => {
		const form = createForm(
			'<input name="ObjectField_title" value="Hello" />'
		);

		expect(getFieldValues(form, [{name: 'title'}])).toEqual({
			title: 'Hello',
		});
	});

	it('reads a RichText field through the CKEditor instance', () => {
		const form = createForm(
			'<div class="lfr-ck"><div class="ck-editor__editable"></div>' +
				'<input name="ObjectField_body" type="hidden" value="stale" /></div>'
		);

		const editable = form.querySelector('.ck-editor__editable');

		(editable as any).ckeditorInstance = {
			getData: () => '<p>Fresh</p>',
			setData: jest.fn(),
		};

		expect(getFieldValues(form, [{name: 'body'}])).toEqual({
			body: '<p>Fresh</p>',
		});
	});

	it('skips a field that has no control', () => {
		const form = createForm(
			'<input name="ObjectField_title" value="Hi" />'
		);

		expect(
			getFieldValues(form, [{name: 'title'}, {name: 'missing'}])
		).toEqual({title: 'Hi'});
	});

	it('skips an empty plain field', () => {
		const form = createForm('<input name="ObjectField_title" value="" />');

		expect(getFieldValues(form, [{name: 'title'}])).toEqual({});
	});
});
