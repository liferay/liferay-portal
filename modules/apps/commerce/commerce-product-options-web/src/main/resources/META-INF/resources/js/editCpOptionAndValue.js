/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {slugify} from 'commerce-frontend-js';
import {debounce} from 'frontend-js-web';

export default function ({
	allowedSkuContributorTypeNames,
	availableTypeNames,
	bcp47LanguageId,
	isCPOptionSelectDate,
	namespace,
}) {
	const allowedSkuContributorFieldTypeSelectOptions =
		allowedSkuContributorTypeNames.split(',');
	const availableFieldTypeSelectOptions = availableTypeNames.split(',');
	const form = document.getElementById(namespace + 'fm');

	const dateInput = form.querySelector('#' + namespace + 'date');
	const durationInput = form.querySelector('#' + namespace + 'duration');
	const durationTypeInput = form.querySelector(
		'#' + namespace + 'durationType'
	);
	const formFieldTypeSelect = form.querySelector(
		'#' + namespace + 'commerceOptionTypeKey'
	);
	const labelInput = form.querySelector(
		'#' + namespace + 'optionValueSelectDateLabel'
	);
	const skuContributorInput = form.querySelector(
		'#' + namespace + 'skuContributor'
	);
	const timeInput = form.querySelector('#' + namespace + 'time');
	const timeZoneInput = form.querySelector('#' + namespace + 'timezone');

	const optionValueSelectDateObj = new optionValueSelectDate();

	function checkDDMFormFieldType() {
		const skuContributorInputChecked = skuContributorInput.checked;

		enableFormFieldTypeSelectOptionValues(availableFieldTypeSelectOptions);

		if (skuContributorInputChecked) {
			enableFormFieldTypeSelectOptionValues(
				allowedSkuContributorFieldTypeSelectOptions
			);
		}
	}

	function enableFormFieldTypeSelectOptionValues(
		availableFieldTypeSelectOptions
	) {
		for (let i = 0; i < formFieldTypeSelect.options.length; i++) {
			const formFieldTypeSelectOption = formFieldTypeSelect.options[i];

			if (formFieldTypeSelectOption.value === '') {
				continue;
			}

			if (
				endsWith(
					formFieldTypeSelectOption.value,
					availableFieldTypeSelectOptions
				)
			) {
				if (formFieldTypeSelectOption.getAttribute('disabled')) {
					formFieldTypeSelectOption.removeAttribute('disabled');
				}

				continue;
			}

			formFieldTypeSelectOption.setAttribute('disabled', true);
		}
	}

	function endsWith(value, array) {
		value = value.toLowerCase();

		for (let i = 0; i < array.length; i++) {
			if (value.endsWith(array[i].toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	function optionValueSelectDate() {
		this.date = null;
		this.duration = null;
		this.durationType = null;
		this.time = null;
		this.timezone = null;

		this.setDate = function (date) {
			this.date = date;
		};

		this.setDuration = function (duration) {
			this.duration = duration;
		};

		this.setDurationType = function (durationType) {
			this.durationType = durationType;
		};

		this.setTime = function (time) {
			this.time = time;
		};

		this.setTimezone = function (timezone) {
			this.timezone = timezone;
		};

		this.getKey = function () {
			return (
				this.date +
				'-' +
				this.time +
				'-' +
				this.duration +
				'-' +
				this.durationType +
				'-' +
				this.timezone
			);
		};

		this.getLabel = function (locale) {
			const dateSplit = this.date.split('-');
			const [hour, minute] = this.time.split('-');
			const date = new Date(
				dateSplit[2],
				dateSplit[0] - 1,
				dateSplit[1],
				hour,
				minute
			);
			const options = {
				day: 'numeric',
				hour: 'numeric',
				minute: 'numeric',
				month: 'numeric',
				year: 'numeric',
			};
			const formattedDate = date.toLocaleDateString(locale, options);

			if (this.duration && this.durationType) {
				return (
					formattedDate +
					' (' +
					this.timezone +
					'), ' +
					this.duration +
					' ' +
					this.durationType
				);
			}

			return formattedDate + ' (' + this.timezone + ')';
		};
	}

	if (isCPOptionSelectDate) {
		const handleOnLabelInput = function () {
			optionValueSelectDateObj.setDate(slugify(dateInput.value));
			optionValueSelectDateObj.setDuration(durationInput.value);
			optionValueSelectDateObj.setDurationType(durationTypeInput.value);
			optionValueSelectDateObj.setTime(slugify(timeInput.value));
			optionValueSelectDateObj.setTimezone(timeZoneInput.value);

			labelInput.value =
				optionValueSelectDateObj.getLabel(bcp47LanguageId);
		};

		dateInput.addEventListener('focus', debounce(handleOnLabelInput, 200));
		durationInput.addEventListener(
			'input',
			debounce(handleOnLabelInput, 200)
		);
		durationTypeInput.addEventListener(
			'input',
			debounce(handleOnLabelInput, 200)
		);
		timeInput.addEventListener('change', debounce(handleOnLabelInput, 200));
		timeZoneInput.addEventListener(
			'change',
			debounce(handleOnLabelInput, 200)
		);
	}
	else {
		const keyInput = form.querySelector('#' + namespace + 'key');

		const handleOnKeyInput = function () {
			keyInput.value = slugify(keyInput.value);
		};

		keyInput.addEventListener('input', debounce(handleOnKeyInput, 200));
	}

	if (allowedSkuContributorTypeNames) {
		const handleFormFieldTypeSelectChanges = function () {
			if (
				endsWith(
					formFieldTypeSelect.value,
					allowedSkuContributorFieldTypeSelectOptions
				)
			) {
				if (skuContributorInput.getAttribute('disabled')) {
					skuContributorInput.removeAttribute('disabled');
				}
			}
			else {
				if (!skuContributorInput.checked) {
					if (!skuContributorInput.getAttribute('disabled')) {
						skuContributorInput.setAttribute('disabled', 'true');
					}
				}
			}
		};

		formFieldTypeSelect.addEventListener(
			'change',
			handleFormFieldTypeSelectChanges
		);
		skuContributorInput.addEventListener('change', checkDDMFormFieldType);
		checkDDMFormFieldType();
		handleFormFieldTypeSelectChanges();
	}
}
