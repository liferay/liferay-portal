/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from 'frontend-js-components-web';
import {delegate, unescapeHTML} from 'frontend-js-web';

const TPL_TAG_FORM =
	'<div class="c-mb-4 row {key}" >' +
	'<div class="col-md-4">' +
	'<input class="form-control c-mr-4" type="text" disabled="disabled" value="{parameterKey}" />' +
	'</div>' +
	'<div class="col-md-4">' +
	'<input class="form-control c-mr-4" type="text" disabled="disabled" value="{parameterValue}" />' +
	'</div>' +
	'<div class="col-md-4">' +
	'<button class="btn btn-monospaced btn-secondary remove-{key}-parameter"' +
	' data-parameterKey="{parameterKey}"' +
	' data-parameterValue="{parameterValue}"' +
	' data-parameterType="{parameterType}"' +
	' type="button">' +
	Liferay.Util.getLexiconIconTpl('times') +
	'</button>' +
	'</div>' +
	'</div>';

export function reportParameters({namespace, parameters}) {
	const portletMessageContainer = document.querySelector('.report-message');

	const parametersKeyElement = document.querySelector('.parameters-key');
	const parametersValueElement = document.querySelector('.parameters-value');
	const parametersTypeElement = document.querySelector(
		'.parameters-input-type'
	);

	const reportParametersElement =
		document.querySelector('.report-parameters');

	const addParameterElement = document.querySelector('.add-parameter');

	const removeReportElement = document.querySelector(
		'.remove-existing-report'
	);

	const templateReportFileNameElement = document.querySelector(
		'.lfr-reports__template-report-file-name'
	);

	const templateReportInputElement = document.querySelector(
		'.lfr-reports__template-report-input'
	);

	const cancelUpdateReportElement = document.querySelector(
		'.cancel-update-template-report'
	);

	portletMessageContainer.style.display = 'none';

	reportParametersElement.value = parameters;

	let delegateHandler;

	if (parameters) {
		const reportParameters = JSON.parse(parameters);

		for (const i in reportParameters) {
			const reportParameter = reportParameters[i];

			if (reportParameter.key && reportParameter.value) {
				addTag(
					reportParameter.key,
					reportParameter.value,
					reportParameter.type
				);
			}
		}
	}

	disableAddParameterButton();

	function addParameter() {
		portletMessageContainer.style.display = 'none';

		let parametersKey = parametersKeyElement.value;
		let parametersType = parametersTypeElement.value;
		let parametersValue = parametersValueElement.value;

		let message = '';

		if (
			parametersKey === ',' ||
			parametersKey.indexOf(',') > 0 ||
			parametersKey === '=' ||
			parametersKey.indexOf('=') > 0 ||
			parametersValue === '=' ||
			parametersValue.indexOf('=') > 0
		) {
			message = Liferay.Language.get(
				'one-of-your-fields-contains-invalid-characters'
			);

			message = Liferay.Util.escape(message);

			sendErrorMessage(message);

			return;
		}

		const reportParameters = reportParametersElement.value;

		if (reportParameters) {
			const reportParametersJSON = JSON.parse(reportParameters);

			for (const i in reportParametersJSON) {
				const reportParameter = reportParametersJSON[i];

				if (reportParameter.key === parametersKey) {
					message = Liferay.Language.get(
						'that-vocabulary-already-exists'
					);

					message = Liferay.Util.escape(message);

					sendErrorMessage(message);

					return;
				}
			}
		}

		if (parametersType === 'date') {
			parametersValue = getDateValue(namespace);
		}

		parametersKey = encodeURIComponent(parametersKey);
		parametersType = encodeURIComponent(parametersType);
		parametersValue = encodeURIComponent(parametersValue);

		addTag(parametersKey, parametersValue, parametersType);

		addReportParameter(parametersKey, parametersValue, parametersType);

		parametersKeyElement.value = '';
		parametersValueElement.value = '';

		disableAddParameterButton();
	}

	function addReportParameter(
		parametersKey,
		parametersValue,
		parametersType
	) {
		let reportParameters = [];

		if (reportParametersElement.value) {
			reportParameters = JSON.parse(reportParametersElement.value);
		}

		const reportParameter = {
			key: parametersKey,
			type: parametersType,
			value: parametersValue,
		};

		reportParameters.push(reportParameter);

		reportParametersElement.value = JSON.stringify(reportParameters);
	}

	function addTag(parameterKey, parameterValue, parameterType) {
		const tagsContainer = document.querySelector('.report-tags');

		const key = encodeURIComponent(
			('report-tag-' + parameterKey).replace(/ /g, 'BLANK')
		);

		const templateFormParameters = {
			key,
			parameterKey,
			parameterType,
			parameterValue,
		};

		let tagForm = TPL_TAG_FORM;

		Object.entries(templateFormParameters).forEach(([key, value]) => {
			tagForm = tagForm.replaceAll(`{${key}}`, value);
		});

		tagsContainer.innerHTML += tagForm;

		createRemoveParameterEvent(key, tagsContainer);
	}

	function createRemoveParameterEvent(key, tagsContainer) {
		delegateHandler = delegate(
			tagsContainer,
			'click',
			`.remove-${key}-parameter`,
			(event) => {
				const delegateTarget = event.delegateTarget;

				const parameterKey =
					delegateTarget.getAttribute('data-parameterKey');
				const parameterValue = delegateTarget.getAttribute(
					'data-parameterValue'
				);
				const parameterType =
					delegateTarget.getAttribute('data-parameterType');

				deleteParameter(parameterKey, parameterValue, parameterType);
			}
		);
	}

	function deleteParameter(parameterKey) {
		portletMessageContainer.style.display = 'none';

		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-this-entry'
			),
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					const reportParameters = JSON.parse(
						reportParametersElement.value
					);

					for (const i in reportParameters) {
						const reportParameter = reportParameters[i];

						if (reportParameter.key === parameterKey) {
							reportParameters.splice(i, 1);

							break;
						}
					}

					reportParametersElement.value =
						JSON.stringify(reportParameters);

					const key = ('.report-tag-' + parameterKey).replace(
						/ /g,
						'BLANK'
					);

					document.querySelector(key).remove();
				}
			},
		});
	}

	function disableAddParameterButton() {
		addParameterElement.classList.add('disabled');
	}

	function enableAddParameterButton() {
		addParameterElement.classList.remove('disabled');
	}

	function sendErrorMessage(message) {
		message = unescapeHTML(message);

		portletMessageContainer.classList.add('portlet-msg-error');
		portletMessageContainer.innerHTML = message;
		portletMessageContainer.style.display = 'block';
	}

	function toggleAddParameterButton() {
		const parametersKey = parametersKeyElement.value;
		const parametersType = parametersTypeElement.value;

		let parametersValue = parametersValueElement.value;

		if (parametersType === 'date') {
			parametersValue = getDateValue(namespace);
		}

		if (parametersKey && parametersValue) {
			enableAddParameterButton();
		}
		else {
			disableAddParameterButton();
		}
	}

	parametersKeyElement.addEventListener('keyup', toggleAddParameterButton);
	parametersValueElement.addEventListener('keyup', toggleAddParameterButton);
	addParameterElement.addEventListener('click', addParameter);

	removeReportElement.addEventListener('click', () => {
		templateReportFileNameElement.style.display = 'none';
		templateReportInputElement.style.display = 'block';
		cancelUpdateReportElement.style.display = 'block';
	});

	cancelUpdateReportElement.addEventListener('click', () => {
		templateReportFileNameElement.style.display = 'block';
		templateReportInputElement.style.display = 'none';
		cancelUpdateReportElement.style.display = 'none';
	});

	parametersTypeElement.addEventListener('change', (event) => {
		const currentTarget = event.currentTarget;

		const parametersInputDate = document.querySelector(
			'.parameters-input-date'
		);

		const keyInput = document.getElementById(namespace + 'key');

		if (currentTarget.value === 'text') {
			parametersValueElement.value = '';
			disableAddParameterButton();
			parametersValueElement.style.display = 'block';
			parametersInputDate.style.display = 'none';
		}

		if (currentTarget.value === 'date') {
			if (keyInput.value !== '') {
				enableAddParameterButton();
			}
			parametersValueElement.style.display = 'none';
			parametersInputDate.style.display = 'block';
		}
	});

	return {
		dispose() {
			delegateHandler?.dispose();
		},
	};
}

export function getDateValue(namespace) {
	const parameterDateDay = document.getElementById(
		namespace + 'parameterDateDay'
	);

	const parameterDateMonth = document.getElementById(
		namespace + 'parameterDateMonth'
	);

	const parameterDateYear = document.getElementById(
		namespace + 'parameterDateYear'
	);

	const parameterDate = new Date();

	parameterDate.setDate(parameterDateDay.value);
	parameterDate.setMonth(parameterDateMonth.value);
	parameterDate.setYear(parameterDateYear.value);

	return parameterDate.toISOString().split('T')[0];
}
