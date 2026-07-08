/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EVENT_TYPES} from '../../../../src/main/resources/META-INF/resources/js/core/actions/eventTypes.es';
import pageValidationReducer from '../../../../src/main/resources/META-INF/resources/js/core/reducers/pageValidationReducer.es';

const action = {
	payload: {pageIndex: 0},
	type: EVENT_TYPES.PAGE.VALIDATION_FAILED,
};

const createField = (name, type) => ({
	errorMessage: 'This field is required.',
	label: 'Field Label',
	name,
	type,
	valid: false,
});

const createState = (fields) => ({
	pages: [{rows: [{columns: [{fields}]}]}],
});

describe('page validation failed', () => {
	afterEach(() => {
		Liferay.FeatureFlags['LPD-11235'] = false;

		document.body.innerHTML = '';

		delete window.CKEDITOR;
	});

	it('focuses the input when a regular field is invalid', () => {
		document.body.innerHTML = `
			<input name="textField" type="text">
		`;

		const input = document.querySelector("[name='textField']");

		input.focus = jest.fn();

		const state = createState([createField('textField', 'text')]);

		pageValidationReducer(state, action);

		expect(input.focus).toHaveBeenCalled();
	});

	it('scrolls the field into view and focuses the CKEditor 4 instance when a rich text field is invalid', () => {
		Liferay.FeatureFlags['LPD-11235'] = true;

		document.body.innerHTML = `
			<div data-field-name="richTextField"></div>
		`;

		const editor = {focus: jest.fn()};

		window.CKEDITOR = {instances: {richTextField: editor}};

		const wrapper = document.querySelector(
			"[data-field-name='richTextField']"
		);

		wrapper.scrollIntoView = jest.fn();

		const state = createState([createField('richTextField', 'rich_text')]);

		pageValidationReducer(state, action);

		expect(wrapper.scrollIntoView).toHaveBeenCalledWith({
			behavior: 'smooth',
			block: 'center',
		});

		expect(editor.focus).toHaveBeenCalled();
	});

	it('scrolls the field into view and focuses the CKEditor 5 editable when a rich text field is invalid', () => {
		document.body.innerHTML = `
			<div data-field-name="richTextField">
				<div class="ck-editor__editable" contenteditable="true"></div>
			</div>
		`;

		const editable = document.querySelector('.ck-editor__editable');
		const wrapper = document.querySelector(
			"[data-field-name='richTextField']"
		);

		editable.focus = jest.fn();
		wrapper.scrollIntoView = jest.fn();

		const state = createState([createField('richTextField', 'rich_text')]);

		pageValidationReducer(state, action);

		expect(wrapper.scrollIntoView).toHaveBeenCalledWith({
			behavior: 'smooth',
			block: 'center',
		});

		expect(editable.focus).toHaveBeenCalled();
	});

	it('scrolls the parent element into view when the invalid field input is hidden', () => {
		document.body.innerHTML = `
			<div><input name="hiddenField" type="hidden"></div>
		`;

		const parentElement = document.querySelector('div');

		parentElement.scrollIntoView = jest.fn();

		const state = createState([createField('hiddenField', 'text')]);

		pageValidationReducer(state, action);

		expect(parentElement.scrollIntoView).toHaveBeenCalled();
	});
});
