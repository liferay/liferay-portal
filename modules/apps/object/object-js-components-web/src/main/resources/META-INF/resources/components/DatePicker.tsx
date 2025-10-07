/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import {FieldBase} from 'frontend-js-components-web';
import {dateUtils} from 'frontend-js-web';

// @ts-ignore

import moment from 'moment/min/moment-with-locales';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import {createTextMaskInputElement} from 'text-mask-core';

import {createAutoCorrectedDatePipe} from '../utils/createAutoCorrectedDatePipe';
import {
	Date,
	generateDate,
	generateDateConfigurations,
	generateInputMask,
} from '../utils/datetime';

interface DatePickerProps
	extends Omit<React.HTMLAttributes<HTMLInputElement>, 'onChange'> {
	className?: string;
	defaultLanguageId?: Liferay.Language.Locale;
	disabled?: boolean;
	error?: string;
	feedbackMessage?: string;
	hideFeedback?: boolean;
	id?: string;
	label?: string;
	locale?: Liferay.Language.Locale;
	name?: string;
	onBlur?: () => void;
	onChange: (value: string) => void;
	range?: boolean;
	required?: boolean;
	type: 'date' | 'date_time' | 'Date' | 'DateTime';
	value?: string;
}

enum FirstDayOfWeek {
	Sunday = 0,
	Monday = 1,
	Tuesday = 2,
	Wednesday = 3,
	Thursday = 4,
	Friday = 5,
	Saturday = 6,
}

type DateMaskParams = {
	clayFormat?: string;
	firstDayOfWeek?: FirstDayOfWeek;
	isDateTime?: boolean;
	momentFormat?: string;
	months?: string[];
	placeholder?: string;
	serverFormat?: string;
	use12Hours?: boolean;
	weekdaysShort?: string[];
};

type MaskRef = {
	state: {
		previousConformedValue: string;
		previousPlaceholder: string;
	};
	update: (value: string) => void;
};

export function DatePicker({
	className,
	defaultLanguageId = Liferay.ThemeDisplay.getDefaultLanguageId(),
	disabled,
	error,
	feedbackMessage,
	hideFeedback,
	id,
	label,
	locale = Liferay.ThemeDisplay.getLanguageId(),
	onBlur,
	onChange,
	name,
	range,
	required,
	type,
	value,
}: DatePickerProps) {
	const [expanded, setExpanded] = useState(false);

	const inputRef = useRef(null);
	const maskRef = useRef<null | MaskRef>(null);
	const {
		clayFormat,
		isDateTime = false,
		momentFormat = '',
		placeholder,
		serverFormat = '',
		use12Hours,
	} = useMemo(() => {
		let dateMaskParameters: DateMaskParams = {};

		dateMaskParameters = generateDateConfigurations({
			defaultLanguageId,
			locale,
			type,
		});

		return dateMaskParameters;
	}, [defaultLanguageId, locale, type]);

	const date: Date = useMemo(() => {
		let formattedDate = '';
		let year = moment().year();
		const rawDate = value ?? '';

		if (rawDate !== '') {
			const date = moment(rawDate, serverFormat, true);
			formattedDate = date
				.locale(locale ?? defaultLanguageId)
				.format(momentFormat);
			year = date.year();
		}

		return {
			formattedDate,
			locale,
			name,
			rawDate,
			value,
			years: {end: year + 25, start: year - 100},
		};
	}, [momentFormat, defaultLanguageId, locale, name, serverFormat, value]);

	const [{formattedDate, rawDate, years}, setDate] = useState(date);

	/**
	 * Updates the rawDate state whenever the prop value or localizedValue changes,
	 * but it keep user's input case theres no language change.
	 */
	useEffect(() => {
		setDate(({formattedDate, name, rawDate}) =>
			name === date.name && rawDate === ''
				? {...date, formattedDate}
				: date
		);
	}, [date]);

	/**
	 * Creates the input mask and update it whenever the format changes
	 */
	useEffect(() => {
		const {mask, pipeFormat} = generateInputMask(momentFormat);

		maskRef.current = createTextMaskInputElement({
			guide: true,
			inputElement: inputRef.current,
			keepCharPositions: true,
			mask,
			pipe: createAutoCorrectedDatePipe(pipeFormat),
			showMask: true,
		});
	}, [momentFormat]);

	const handleValueChange = (value: string) => {
		const nextState = generateDate({
			isDateTime,
			momentFormat,
			serverFormat,
			value,
		});

		setDate((previousState) => ({...previousState, ...nextState}));

		if (nextState.rawDate !== rawDate) {
			onChange(nextState.rawDate as string);
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

	return (
		<FieldBase
			className={className}
			disabled={disabled}
			errorMessage={error}
			helpMessage={feedbackMessage}
			hideFeedback={hideFeedback}
			id={id}
			label={label}
			required={required}
		>
			<ClayDatePicker
				ariaLabels={{
					buttonChooseDate: `${Liferay.Language.get('select-date')}`,
					buttonDot: `${Liferay.Language.get('select-current-date')}`,
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
				disabled={disabled}
				expanded={expanded}
				firstDayOfWeek={dateUtils.getFirstDayOfWeek()}
				id={id}
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
				onBlur={onBlur}
				onChange={handleValueChange}
				onExpandedChange={() => {
					setExpanded(!expanded);
				}}
				onInput={onInputMask}
				placeholder={placeholder}
				range={range}
				ref={inputRef}
				time={isDateTime}
				use12Hours={use12Hours}
				value={formattedDate}
				weekdaysShort={dateUtils.getWeekdaysShort()}
				years={years}
				yearsCheck={false}
			/>
		</FieldBase>
	);
}
