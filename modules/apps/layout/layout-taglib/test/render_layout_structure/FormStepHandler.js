/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import FormStepHandler from '../../src/main/resources/META-INF/resources/js/render_layout_structure/FormStepHandler';

const FORM_ID = 'formId';

const renderForm = ({hiddenStepContent = '', outsideStepContent = ''} = {}) => {
	document.body.innerHTML = `
		<form class="lfr-layout-structure-item-${FORM_ID}">
			${outsideStepContent}

			<div class="lfr-layout-structure-item-form-step-container">
				<div data-step-index="0"></div>

				<div class="d-none" data-step-index="1">
					${hiddenStepContent}
				</div>
			</div>
		</form>
	`;

	return document.querySelector(`.lfr-layout-structure-item-${FORM_ID}`);
};

const submitForm = () => {
	const [, onSubmit] = Liferay.on.mock.calls.find(
		([eventName]) => eventName === 'formFragment:submit'
	);

	onSubmit();
};

describe('FormStepHandler', () => {
	let handler;

	beforeEach(() => {
		jest.clearAllMocks();
	});

	afterEach(() => {
		handler.dispose();
	});

	it('does not set the active step when every field is valid', () => {
		renderForm({
			hiddenStepContent: '<textarea required>Value</textarea>',
		});

		handler = FormStepHandler({formId: FORM_ID});

		submitForm();

		expect(Liferay.fire).not.toHaveBeenCalled();
	});

	it('ignores invalid fields that are outside of a step', () => {
		renderForm({
			outsideStepContent: '<input required type="text" />',
		});

		handler = FormStepHandler({formId: FORM_ID});

		expect(submitForm).not.toThrow();

		expect(Liferay.fire).not.toHaveBeenCalled();
	});

	it('sets the active step when an invalid field follows one outside of a step', () => {
		const form = renderForm({
			hiddenStepContent: '<textarea required></textarea>',
			outsideStepContent: '<input required type="text" />',
		});

		handler = FormStepHandler({formId: FORM_ID});

		submitForm();

		expect(Liferay.fire).toHaveBeenCalledWith('formFragment:changeStep', {
			emitter: form,
			step: 1,
		});
	});

	it('sets the active step when an invalid input is on a hidden step', () => {
		const form = renderForm({
			hiddenStepContent: '<input required type="text" />',
		});

		handler = FormStepHandler({formId: FORM_ID});

		submitForm();

		expect(Liferay.fire).toHaveBeenCalledWith('formFragment:changeStep', {
			emitter: form,
			step: 1,
		});
	});

	it('sets the active step when an invalid select is on a hidden step', () => {
		const form = renderForm({
			hiddenStepContent:
				'<select required><option value="">Choose</option></select>',
		});

		handler = FormStepHandler({formId: FORM_ID});

		submitForm();

		expect(Liferay.fire).toHaveBeenCalledWith('formFragment:changeStep', {
			emitter: form,
			step: 1,
		});
	});

	it('sets the active step when an invalid textarea is on a hidden step', () => {
		const form = renderForm({
			hiddenStepContent: '<textarea required></textarea>',
		});

		handler = FormStepHandler({formId: FORM_ID});

		submitForm();

		expect(Liferay.fire).toHaveBeenCalledWith('formFragment:changeStep', {
			emitter: form,
			step: 1,
		});
	});
});
