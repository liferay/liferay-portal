/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getFieldControls from '../../../../src/main/resources/META-INF/resources/js/content_editor/utils/getFieldControls';

function createForm(html: string): HTMLFormElement {
	const form = document.createElement('form');

	form.innerHTML = html;

	return form;
}

describe('getFieldControls', () => {
	it('does not match a field that only shares the prefix', () => {
		const form = createForm(
			'<input name="ObjectField_title" />' +
				'<input name="ObjectField_titleImage" />'
		);

		const controls = getFieldControls(form, 'title');

		expect(controls).toHaveLength(1);
		expect(controls[0].getAttribute('name')).toBe('ObjectField_title');
	});

	it('falls back to the rich text container when there is no named control', () => {
		const form = createForm(
			'<div class="rich-text-input" data-field-name="ObjectField_content">' +
				'<div class="lfr-ck"></div></div>'
		);

		const controls = getFieldControls(form, 'content');

		expect(controls).toHaveLength(1);
		expect(controls[0].getAttribute('data-field-name')).toBe(
			'ObjectField_content'
		);
	});

	it('returns an empty array when the field is absent', () => {
		const form = createForm('<input name="ObjectField_body" />');

		expect(getFieldControls(form, 'title')).toHaveLength(0);
	});

	it('returns every locale-suffixed control for a localized field', () => {
		const form = createForm(
			'<input name="ObjectField_title_en_US" />' +
				'<input name="ObjectField_title_pt_BR" />'
		);

		expect(
			getFieldControls(form, 'title').map((control) =>
				control.getAttribute('name')
			)
		).toEqual(['ObjectField_title_en_US', 'ObjectField_title_pt_BR']);
	});

	it('returns the control for a non-localized field', () => {
		const form = createForm('<input name="ObjectField_title" />');

		const controls = getFieldControls(form, 'title');

		expect(controls).toHaveLength(1);
		expect(controls[0].getAttribute('name')).toBe('ObjectField_title');
	});
});
