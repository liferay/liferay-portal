/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function FormStepHandler({formId}) {
	const form = document.querySelector(`.lfr-layout-structure-item-${formId}`);

	const steps = form.querySelector(
		'.lfr-layout-structure-item-form-step-container'
	).children;

	const onStepChange = ({emitter, step}) => {

		// Return if the emitter is not in this form

		if (!form.contains(emitter)) {
			return;
		}

		// Hide current active step

		let currentStep = 0;

		for (const [index, formStep] of Array.from(steps).entries()) {
			if (!formStep.classList.contains('d-none')) {
				formStep.classList.add('d-none');

				currentStep = index;
				break;
			}
		}

		// Show new active step

		let index = currentStep;

		if (step === 'next') {
			if (currentStep <= steps.length - 2) {
				index += 1;
			}
		}
		else if (step === 'previous') {
			if (currentStep !== 0) {
				index -= 1;
			}
		}
		else {
			index = step;
		}

		steps[index].classList.remove('d-none');
	};

	Liferay.on('formFragment:changeStep', onStepChange);

	// Set active step when there's an error

	const group = form.querySelector('.form-group.has-error');

	if (group) {
		const step = group.closest('[data-step-index]');

		const index = Number(step.dataset.stepIndex);

		if (index) {
			Liferay.fire('formFragment:changeStep', {
				emitter: form,
				step: index,
			});
		}
	}

	// Set active step when there's an invalid field

	const onSubmit = () => {
		const fields = form.querySelectorAll('input, select, textarea');

		for (const field of Array.from(fields)) {
			if (!field.checkValidity()) {
				const step = field.closest('[data-step-index]');

				if (!step) {
					continue;
				}

				const index = Number(step.dataset.stepIndex);

				Liferay.fire('formFragment:changeStep', {
					emitter: form,
					step: index,
				});

				break;
			}
		}
	};

	Liferay.on('formFragment:submit', onSubmit);

	return {
		dispose: () => {
			Liferay.detach('formFragment:changeStep', onStepChange);
			Liferay.detach('formFragment:submit', onSubmit);
		},
	};
}
