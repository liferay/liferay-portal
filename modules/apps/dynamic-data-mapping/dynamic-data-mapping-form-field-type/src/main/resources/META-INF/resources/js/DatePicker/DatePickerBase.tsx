/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {
	createAutoCorrectedDatePipe,
	datetimeUtils,
} from '@liferay/object-js-components-web';
import {dateUtils} from 'frontend-js-web';

// @ts-ignore

import moment from 'moment/min/moment-with-locales';
import React, {useEffect, useRef, useState} from 'react';
import {createTextMaskInputElement} from 'text-mask-core';

import {getTooltipTitle} from '../util/tooltip';
import {Date, DateMaskParams} from './DatePicker';

import type {Locale, LocalizedValue} from '../types';

const DIGIT_REGEX = /\d/;

export interface DatePickerBaseProps {
	date: Date;
	dateMaskParams: DateMaskParams;
	defaultLanguageId: Locale;
	dir: 'ltr' | 'rtl';
	displayErrors?: boolean;
	errorMessage?: string;
	htmlAutocompleteAttribute: string;
	id?: string;
	locale: Locale;
	localizable: boolean;
	localizedObjectField: boolean;
	localizedValue?: LocalizedValue<string>;
	name: string;
	onBlur: any;
	onChange: any;
	onFocus: any;
	predefinedValue?: string;
	readOnly: boolean;
	required: boolean;
	setValidField: React.Dispatch<
		React.SetStateAction<{
			displayErrors: boolean | undefined;
			errorMessage: string | undefined;
			valid: boolean;
		}>
	>;
	tip?: string;
	type: 'date' | 'date_time';
	valid: boolean;
	value: string | LocalizedValue<string>;
}

type MaskRef = {
	state: {
		previousConformedValue: string;
		previousPlaceholder: string;
	};
	update: (value: string) => void;
};

export default function DatePickerBase({
	date,
	dateMaskParams,
	defaultLanguageId = Liferay.ThemeDisplay.getDefaultLanguageId(),
	dir,
	displayErrors,
	errorMessage,
	htmlAutocompleteAttribute,
	id,
	locale,
	name,
	onBlur,
	onChange,
	onFocus,
	predefinedValue,
	readOnly,
	required,
	setValidField,
	tip,
	valid,
}: DatePickerBaseProps) {
	const inputRef = useRef(null);
	const maskRef = useRef<null | MaskRef>(null);

	const {
		clayFormat,
		isDateTime = false,
		momentFormat = '',
		placeholder = '',
		serverFormat = '',
		use12Hours,
	} = dateMaskParams;

	const [{formattedDate, rawDate, years}, setDate] = useState(date);

	/**
	 * Updates the rawDate state whenever the prop value or localizedValue changes,
	 * but it keep user's input case theres no language change.
	 */
	useEffect(() => {
		setDate(({formattedDate, name, predefinedValue, rawDate}) =>
			name === date.name &&
			predefinedValue === date.predefinedValue &&
			rawDate === ''
				? {...date, formattedDate}
				: date
		);
	}, [date]);

	/**
	 * Always update formattedDate if there is a value in rawDate
	 */
	useEffect(() => {
		if (date.rawDate !== '') {
			const formattedDate = moment(rawDate, serverFormat, true)
				.locale(locale ?? defaultLanguageId)
				.format(momentFormat);

			setDate((previousState) => ({...previousState, formattedDate}));
		}
	}, [date, defaultLanguageId, locale, momentFormat, rawDate, serverFormat]);

	/**
	 * Creates the input mask and update it whenever the format changes
	 */
	useEffect(() => {
		const {mask, pipeFormat} =
			datetimeUtils.generateInputMask(momentFormat);

		maskRef.current = createTextMaskInputElement({
			guide: true,
			inputElement: inputRef.current,
			keepCharPositions: true,
			mask,
			pipe: createAutoCorrectedDatePipe(pipeFormat),
			showMask: true,
		});
	}, [momentFormat]);

	const handleValueChange = (dateValue: string) => {
		const nextState = datetimeUtils.generateDate({
			isDateTime,
			momentFormat,
			serverFormat,
			value: dateValue,
		});

		setDate((previousState) => ({...previousState, ...nextState}));

		if (nextState.rawDate !== rawDate) {
			onChange(nextState.rawDate as string);
		}
	};

	const [expanded, setExpanded] = useState(false);

	const handleBlur = () => {
		if (!required) {
			const isInputFilled = DIGIT_REGEX.test(formattedDate);

			const isValidMomentFormat = moment(
				formattedDate,
				momentFormat,
				true
			).isValid();

			if (!isInputFilled || isValidMomentFormat) {
				setValidField({
					displayErrors,
					errorMessage,
					valid,
				});

				return;
			}

			setValidField({
				displayErrors: true,
				errorMessage: Liferay.Language.get('please-enter-a-valid-date'),
				valid: false,
			});

			return;
		}

		setValidField({
			displayErrors: !!errorMessage && !valid,
			errorMessage,
			valid,
		});

		onBlur?.();
	};

	const handleExpandedChange = (value: boolean) => {
		if (value !== expanded) {
			setExpanded(value);

			if (value) {
				onFocus?.();
				setValidField({
					displayErrors,
					errorMessage,
					valid,
				});
			}
			else {
				handleBlur();
			}
		}
	};

	const onInputMask: React.FocusEventHandler<HTMLInputElement> = ({
		target: {value},
	}) => {
		try {
			maskRef.current?.update(value);
		}
		catch (error) {
			maskRef.current?.update('');
		}
	};

	useEffect(() => {
		if (required) {
			setValidField({
				displayErrors,
				errorMessage,
				valid,
			});
		}

		if (predefinedValue && !valid) {
			setValidField({
				displayErrors: !!errorMessage && !valid,
				errorMessage,
				valid,
			});
		}
	}, [
		displayErrors,
		errorMessage,
		required,
		predefinedValue,
		setValidField,
		valid,
	]);

	return (
		<ClayTooltipProvider autoAlign>
			<div
				data-tooltip-align="top"
				{...getTooltipTitle({placeholder, value: formattedDate})}
			>
				<ClayDatePicker
					{...(htmlAutocompleteAttribute && {
						autoComplete: htmlAutocompleteAttribute,
					})}
					{...((errorMessage || tip) && {
						'aria-describedby': `${id ?? name}_fieldFeedback`,
					})}
					aria-required={required}
					ariaLabels={{
						buttonChooseDate: `${Liferay.Language.get(
							'select-date'
						)}`,
						buttonDot: `${Liferay.Language.get(
							'select-current-date'
						)}`,
						buttonNextMonth: `${Liferay.Language.get(
							'select-next-month'
						)}`,
						buttonPreviousMonth: `${Liferay.Language.get(
							'select-previous-month'
						)}`,
						dialog: `${Liferay.Language.get('select-date')}`,
						selectMonth: `${Liferay.Language.get('select-a-month')}`,
						selectYear: `${Liferay.Language.get('select-a-year')}`,
					}}
					dateFormat={clayFormat}
					dir={dir}
					disabled={readOnly}
					expanded={expanded}
					firstDayOfWeek={dateUtils.getFirstDayOfWeek()}
					id={name}
					months={[
						`${Liferay.Language.get('january')}`,
						`${Liferay.Language.get('february')}`,
						`${Liferay.Language.get('march')}`,
						`${Liferay.Language.get('april')}`,
						`${Liferay.Language.get('may')}`,
						`${Liferay.Language.get('june')}`,
						`${Liferay.Language.get('july')}`,
						`${Liferay.Language.get('august')}`,
						`${Liferay.Language.get('september')}`,
						`${Liferay.Language.get('october')}`,
						`${Liferay.Language.get('november')}`,
						`${Liferay.Language.get('december')}`,
					]}
					onBlur={handleBlur}
					onChange={handleValueChange}
					onExpandedChange={handleExpandedChange}
					onFocus={onFocus}
					onInput={onInputMask}
					placeholder={placeholder}
					ref={inputRef}
					time={isDateTime}
					use12Hours={use12Hours}
					value={formattedDate}
					weekdaysShort={dateUtils.getWeekdaysShort()}
					years={years}
					yearsCheck={false}
				/>

				<input name={name} type="hidden" value={rawDate} />
			</div>
		</ClayTooltipProvider>
	);
}
