/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import {dateUtils} from 'frontend-js-web';

// @ts-ignore

import moment from 'moment/min/moment-with-locales';
import React, {useEffect, useMemo, useRef, useState} from 'react';

import {focusInput} from './focusInput';
import {getTranslationInput} from './getTranslationInput';
import {registerLocalizedInput} from './registerLocalizedInput';
import {registerUnlocalizedInput} from './registerUnlocalizedInput';

import type {FirstDayOfWeekLocale} from 'frontend-js-web';

// The server silently discards a date time not separated by a literal `T`.

const SERVER_DATE_FORMAT = 'YYYY-MM-DD';
const SERVER_DATE_TIME_FORMAT = 'YYYY-MM-DD[T]HH:mm';

const YEARS_AFTER = 25;
const YEARS_BEFORE = 100;

type Formats = {
	clayFormat: string;
	displayFormat: string;
	placeholder: string;
	serverFormat: string;
	use12Hours: boolean;
};

type OnChange = ReturnType<typeof registerLocalizedInput>['onChange'];

type Props = {
	availableLanguageIds?: string[];
	defaultLanguageId: Liferay.Language.Locale;
	disabled?: boolean;
	focus?: boolean;
	localizable?: boolean;
	name: string;
	namespace: string;
	readOnly?: boolean;
	readOnlyLabelId: string;
	required?: boolean;
	time?: boolean;
	unlocalizedFieldsState?: 'disabled' | 'read-only';
	value?: string;
	valueI18n?: Record<string, string>;
};

/**
 * Clay formats with date-fns tokens, so `clayFormat` holds only the date.
 */

function getFormats(time: boolean, locale: string): Formats {
	const localeData = moment().locale(locale).localeData();

	const dateFormat = localeData.longDateFormat('L');
	const use12Hours = localeData.longDateFormat('LT').endsWith('A');

	const displayFormat = time
		? `${dateFormat} ${use12Hours ? 'hh:mm A' : 'HH:mm'}`
		: dateFormat;

	return {
		clayFormat: dateFormat
			.replace('YYYY', 'yyyy')
			.replace('DD', 'dd')
			.replace('D', 'd'),
		displayFormat,
		placeholder: displayFormat.replace(/\w/g, '_'),
		serverFormat: time ? SERVER_DATE_TIME_FORMAT : SERVER_DATE_FORMAT,
		use12Hours,
	};
}

function toDisplayValue(value: string, formats: Formats, locale: string) {
	const date = moment(value, formats.serverFormat, true);

	return date.isValid()
		? date.locale(locale).format(formats.displayFormat)
		: '';
}

export function DateInput({
	availableLanguageIds,
	defaultLanguageId,
	disabled,
	focus = false,
	localizable = false,
	name,
	namespace,
	readOnly,
	readOnlyLabelId,
	required,
	time = false,
	unlocalizedFieldsState = 'disabled',
	value = '',
	valueI18n,
}: Props) {
	const id = `${namespace}-date-input`;
	const locale = Liferay.ThemeDisplay.getLanguageId();

	const formats = useMemo(() => getFormats(time, locale), [locale, time]);

	const [displayValue, setDisplayValue] = useState(() =>
		toDisplayValue(value, formats, locale)
	);

	const [expanded, setExpanded] = useState(false);

	const [unlocalized, setUnlocalized] = useState(false);

	const onChangeRef = useRef<OnChange>();

	const registeredRef = useRef(false);

	const inputDisabled =
		disabled || (unlocalized && unlocalizedFieldsState === 'disabled');

	const inputReadOnly =
		readOnly || (unlocalized && unlocalizedFieldsState === 'read-only');

	useEffect(() => {

		// Registering twice would attach a second set of Liferay listeners.

		if (registeredRef.current) {
			return;
		}

		registeredRef.current = true;

		const valueInput = document.getElementById(
			`${id}-value`
		) as HTMLInputElement;

		if (!localizable) {
			registerUnlocalizedInput({
				changeTextDirection: false,
				customLocaleChangeHandler: true,
				defaultLanguageId,
				inputElement: document.getElementById(id) as HTMLInputElement,
				onLocaleChange: (languageId) =>
					setUnlocalized(languageId !== defaultLanguageId),
				readOnlyInputLabel: document.getElementById(
					readOnlyLabelId
				) as HTMLSpanElement,
				unlocalizedFieldsState,
				unlocalizedMessageContainer: document.getElementById(
					`${namespace}-unlocalized-info`
				)!,
			});

			return;
		}

		const localizationInputsContainer = document.getElementById(
			`${id}-translations`
		)!;

		let currentLanguageId: string = defaultLanguageId;

		// The caller derives these ids from `inputElement`, so reuse its id.

		const translationInput = (languageId: string) =>
			getTranslationInput({
				inputId: valueInput.id,
				inputName: name,
				languageId,
				localizationInputsContainer,
				namespace,
			});

		const setValue = (value = '') => {
			valueInput.value = value;

			setDisplayValue(toDisplayValue(value, formats, locale));
		};

		// Passing a handler replaces a default that writes straight to the DOM.

		const {onChange} = registerLocalizedInput({
			availableLanguageIds,
			changeTextDirection: false,
			defaultLanguageId,
			initialValues: valueI18n,
			inputElement: valueInput,
			inputName: name,
			localizationInputsContainer,
			namespace,
			onAutoTranslate: ({languageId, value}) => {
				translationInput(languageId).value = value ?? '';

				if (languageId === currentLanguageId) {
					setValue(value);
				}
			},

			onLocaleChange: ({languageId, value}) => {
				currentLanguageId = languageId;

				setValue(value);
			},
			onMarkAsTranslated: () => {
				const value = translationInput(defaultLanguageId).value;

				translationInput(currentLanguageId).value = value;

				setValue(value);
			},
			onResetTranslation: () => {
				translationInput(currentLanguageId).removeAttribute('value');

				setValue(translationInput(defaultLanguageId).value);
			},
		});

		onChangeRef.current = onChange;
	}, [
		availableLanguageIds,
		defaultLanguageId,
		formats,
		id,
		locale,
		localizable,
		name,
		namespace,
		readOnlyLabelId,
		unlocalizedFieldsState,
		valueI18n,
	]);

	// Clay types the picker after `HTMLAttributes`, which omits these two.

	useEffect(() => {
		const inputElement = document.getElementById(id) as HTMLInputElement;

		inputElement.readOnly = Boolean(inputReadOnly);
		inputElement.required = Boolean(required);

		if (focus) {
			focusInput(inputElement);
		}
	}, [focus, id, inputReadOnly, required]);

	const date = moment(value, formats.serverFormat, true);
	const year = date.isValid() ? date.year() : moment().year();

	return (
		<ClayDatePicker
			aria-describedby={`${id}-help-text`}
			aria-labelledby={`${id}-label`}
			ariaLabels={{
				buttonChooseDate: Liferay.Language.get('select-date'),
				buttonDot: Liferay.Language.get('select-current-date'),
				buttonNextMonth: Liferay.Language.get('select-next-month'),
				buttonPreviousMonth: Liferay.Language.get(
					'select-previous-month'
				),
				dialog: Liferay.Language.get('select-date'),
				selectMonth: Liferay.Language.get('select-a-month'),
				selectYear: Liferay.Language.get('select-a-year'),
			}}
			dateFormat={formats.clayFormat}
			disabled={inputDisabled}
			expanded={inputReadOnly ? false : expanded}
			firstDayOfWeek={dateUtils.getFirstDayOfWeek(
				Liferay.ThemeDisplay.getBCP47LanguageId() as FirstDayOfWeekLocale
			)}
			id={id}
			inputName={`${namespace}-date-picker`}
			months={dateUtils.getMonthsLong()}
			onChange={(displayValue) => {
				if (inputReadOnly) {
					return;
				}

				setDisplayValue(displayValue);

				const date = moment(displayValue, formats.displayFormat, true);

				const value = date.isValid()
					? date.format(formats.serverFormat)
					: '';

				const inputElement = document.getElementById(
					id
				) as HTMLInputElement;

				inputElement.setCustomValidity(
					displayValue && !value
						? Liferay.Language.get('please-enter-a-valid-date')
						: ''
				);

				(
					document.getElementById(`${id}-value`) as HTMLInputElement
				).value = value;

				onChangeRef.current?.(value);
			}}
			onExpandedChange={(value: boolean) => {
				if (!inputReadOnly) {
					setExpanded(value);
				}
			}}
			placeholder={formats.placeholder}
			time={time}
			use12Hours={formats.use12Hours}
			value={displayValue}
			weekdaysShort={dateUtils.getWeekdaysShort()}
			years={{end: year + YEARS_AFTER, start: year - YEARS_BEFORE}}
			yearsCheck={false}
		/>
	);
}
