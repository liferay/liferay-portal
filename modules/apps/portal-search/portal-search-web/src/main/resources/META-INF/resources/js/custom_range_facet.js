/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-search-custom-range-facet',
	(A) => {
		const AGGREGATION_TYPES = {
			DATE_RANGE: 'dateRange',
			RANGE: 'range',
		};

		const DEFAULTS_FORM_VALIDATOR = A.config.FormValidator;

		const Util = Liferay.Util;

		const CustomRangeFacet = function (config) {
			const instance = this;

			instance.form = config.form;
			instance.aggregationType =
				config.aggregationType || AGGREGATION_TYPES.DATE_RANGE;
			instance.fromInputName = config.fromInputName;
			instance.namespace = config.namespace;
			instance.parameterName = config.parameterName;
			instance.searchCustomRangeButton = config.searchCustomRangeButton;
			instance.searchCustomRangeToggleName =
				config.searchCustomRangeToggleName;
			instance.toInputName = config.toInputName;

			if (instance.aggregationType === AGGREGATION_TYPES.RANGE) {
				instance.fromInputPicker = A.one(`#${instance.fromInputName}`);
				instance.toInputPicker = A.one(`#${instance.toInputName}`);

				if (instance.fromInputPicker && instance.toInputPicker) {
					instance._initializeFormValidator();
				}
			}
			else {
				instance.fromInputDatePicker = Liferay.component(
					instance.fromInputName + 'DatePicker'
				);
				instance.toInputDatePicker = Liferay.component(
					instance.toInputName + 'DatePicker'
				);

				if (
					instance.fromInputDatePicker &&
					instance.toInputDatePicker
				) {
					instance._initializeFormDateValidator();
				}
			}

			if (instance.searchCustomRangeButton) {
				instance.searchCustomRangeButton.on(
					'click',
					A.bind(instance.filter, instance)
				);
			}
		};

		const CustomRangeFacetUtil = {
			addURLParameter(key, value, parameterArray) {
				key = encodeURIComponent(key);
				value = encodeURIComponent(value);

				parameterArray[parameterArray.length] = [key, value].join('=');

				return parameterArray;
			},

			removeURLParameters(key, parameterArray) {
				key = encodeURIComponent(key);

				return parameterArray.filter((item) => {
					const itemSplit = item.split('=');

					return !(itemSplit && itemSplit[0] === key);
				});
			},

			submitSearch(parameterString) {
				const url = new URL(window.location.href);

				url.search = parameterString;

				Liferay.Util.navigate(url.toString());
			},

			/**
			 * Formats a date to 'YYYY-MM-DD' format.
			 * @param {Date} date The date to format.
			 * @returns {String} The date string.
			 */
			toLocaleDateStringFormatted(date) {
				const localDate = new Date(date);

				localDate.setMinutes(
					date.getMinutes() - date.getTimezoneOffset()
				);

				return localDate.toISOString().split('T')[0];
			},
		};

		A.mix(CustomRangeFacet.prototype, {
			_initializeFormDateValidator() {
				const instance = this;

				const dateRangeRuleName = instance.namespace + 'dateRange';

				A.mix(
					DEFAULTS_FORM_VALIDATOR.STRINGS,
					{
						[dateRangeRuleName]: Liferay.Language.get(
							'search-custom-range-invalid-date-range'
						),
					},
					true
				);

				A.mix(
					DEFAULTS_FORM_VALIDATOR.RULES,
					{
						[dateRangeRuleName]() {
							return A.Date.isGreaterOrEqual(
								instance.toInputDatePicker.getDate(),
								instance.fromInputDatePicker.getDate()
							);
						},
					},
					true
				);

				const customDateRangeValidator = new A.FormValidator({
					boundingBox: instance.form,
					fieldContainer: 'div',
					on: {
						errorField() {
							Util.toggleDisabled(
								instance.searchCustomRangeButton,
								true
							);
						},
						validField() {
							Util.toggleDisabled(
								instance.searchCustomRangeButton,
								false
							);
						},
					},
					rules: {
						[instance.fromInputName]: {
							[dateRangeRuleName]: true,
						},
						[instance.toInputName]: {
							[dateRangeRuleName]: true,
						},
					},
				});

				const onDateRangeSelectionChange = function () {
					customDateRangeValidator.validate();
				};

				instance.fromInputDatePicker.on(
					'selectionChange',
					onDateRangeSelectionChange
				);

				instance.toInputDatePicker.on(
					'selectionChange',
					onDateRangeSelectionChange
				);
			},

			_initializeFormValidator() {
				const instance = this;

				const rangeRuleName = instance.namespace + 'range';

				A.mix(
					DEFAULTS_FORM_VALIDATOR.STRINGS,
					{
						[rangeRuleName]: Liferay.Language.get(
							'search-custom-range-invalid-range'
						),
					},
					true
				);

				A.mix(
					DEFAULTS_FORM_VALIDATOR.RULES,
					{
						[rangeRuleName]() {
							return (
								instance.fromInputPicker.val() !== '' &&
								instance.toInputPicker.val() !== '' &&
								Number(instance.fromInputPicker.val()) <=
									Number(instance.toInputPicker.val())
							);
						},
					},
					true
				);

				const customRangeValidator = new A.FormValidator({
					boundingBox: instance.form,
					fieldContainer: 'div',
					on: {
						errorField() {
							Util.toggleDisabled(
								instance.searchCustomRangeButton,
								true
							);
						},
						validField() {
							Util.toggleDisabled(
								instance.searchCustomRangeButton,
								false
							);
						},
					},
					rules: {
						[instance.fromInputName]: {
							[rangeRuleName]: true,
						},
						[instance.toInputName]: {
							[rangeRuleName]: true,
						},
					},
					validateOnInput: true,
				});

				const onRangeSelectionChange = function () {
					customRangeValidator.validate();
				};

				instance.fromInputPicker.on('change', onRangeSelectionChange);

				instance.toInputPicker.on('change', onRangeSelectionChange);
			},

			filter() {
				const instance = this;

				let fromParameter;
				let toParameter;

				if (instance.fromInputPicker) {
					fromParameter = instance.fromInputPicker.val();
				}

				if (instance.toInputPicker) {
					toParameter = instance.toInputPicker.val();
				}

				if (instance.fromInputDatePicker) {
					fromParameter =
						CustomRangeFacetUtil.toLocaleDateStringFormatted(
							instance.fromInputDatePicker.getDate()
						);
				}

				if (instance.toInputDatePicker) {
					toParameter =
						CustomRangeFacetUtil.toLocaleDateStringFormatted(
							instance.toInputDatePicker.getDate()
						);
				}

				const param = instance.parameterName;
				const paramFrom = param + 'From';
				const paramTo = param + 'To';

				let parameterArray = window.location.search
					.substr(1)
					.split('&');

				const searchCustomRangeToggle = document.getElementById(
					instance.searchCustomRangeToggleName
				);

				if (
					!searchCustomRangeToggle?.hasAttribute('data-term-id') ||
					searchCustomRangeToggle?.type === 'radio'
				) {
					parameterArray = CustomRangeFacetUtil.removeURLParameters(
						param,
						parameterArray
					);
				}

				parameterArray = CustomRangeFacetUtil.removeURLParameters(
					paramFrom,
					parameterArray
				);

				parameterArray = CustomRangeFacetUtil.removeURLParameters(
					paramTo,
					parameterArray
				);

				const startParameterNameElement = document.getElementById(
					instance.namespace + 'start-parameter-name'
				);

				if (startParameterNameElement) {
					parameterArray = CustomRangeFacetUtil.removeURLParameters(
						startParameterNameElement.value,
						parameterArray
					);
				}

				parameterArray = CustomRangeFacetUtil.addURLParameter(
					paramFrom,
					fromParameter,
					parameterArray
				);

				parameterArray = CustomRangeFacetUtil.addURLParameter(
					paramTo,
					toParameter,
					parameterArray
				);

				CustomRangeFacetUtil.submitSearch(parameterArray.join('&'));
			},
		});

		Liferay.namespace('Search').CustomRangeFacet = CustomRangeFacet;

		Liferay.namespace('Search').CustomRangeFacetUtil = CustomRangeFacetUtil;
	},
	'',
	{
		requires: ['aui-form-validator'],
	}
);
