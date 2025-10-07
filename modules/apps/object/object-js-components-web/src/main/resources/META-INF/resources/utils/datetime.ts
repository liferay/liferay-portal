/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FirstDayOfWeek} from '@clayui/date-picker';

// @ts-ignore

import moment from 'moment/min/moment-with-locales';

const DIGIT_REGEX = /\d/;
const LETTER_REGEX = /[a-z]/i;
const MINUS_REGEX = /-/g;
const A_OR_P_CHARACTER_REGEX = /[AP]/i;
const NOT_LETTER_REGEX = /[^a-z]/gi;
const M_CHARACTER_REGEX = /M/i;
const PIPE_FORBIDDEN_ENDING_CHAR_REGEX = /[^\w]/i;
const WORD_CHARACTER_REGEX = /\w/g;
const SERVER_DATE_FORMAT = 'YYYY-MM-DD';
const SERVER_DATE_TIME_FORMAT = 'YYYY-MM-DD HH:mm';

export interface Date {
	formattedDate?: string;
	locale?: string;
	name?: string;
	predefinedValue?: string;
	rawDate?: string;
	value?: string;
	years?: {
		end: number;
		start: number;
	};
}

export type DateConfig = {
	clayFormat: string;
	firstDayOfWeek: FirstDayOfWeek;
	isDateTime: boolean;
	momentFormat: string;
	placeholder: string;
	serverFormat: string;
	use12Hours: boolean;
};

interface GenerateDateConfigurationsProps {
	defaultLanguageId: Liferay.Language.Locale;
	locale?: Liferay.Language.Locale;
	type: 'date' | 'date_time' | 'Date' | 'DateTime';
}

interface GenerateDateProps {
	isDateTime: boolean;
	momentFormat: string;
	serverFormat: string;
	value: string;
}

type maskItem = string | RegExp;

export function generateDateConfigurations({
	defaultLanguageId,
	locale,
	type,
}: GenerateDateConfigurationsProps): DateConfig {
	let use12Hours = false;

	const isDateTime = type === 'date_time' || type === 'DateTime';
	const momentLocale = moment().locale(locale ?? defaultLanguageId);
	const dateFormat = momentLocale.localeData().longDateFormat('L');
	const firstDayOfWeek = momentLocale.localeData().firstDayOfWeek();
	const time = momentLocale.localeData().longDateFormat('LT');

	let momentFormat = dateFormat;

	if (isDateTime) {
		const [hourFormat] = time.split(NOT_LETTER_REGEX, 1);

		const formattedTime =
			hourFormat.length === 1
				? hourFormat[0] === 'H'
					? `H${time}`
					: `h${time}`
				: time;

		momentFormat = `${dateFormat} ${formattedTime}`;
		use12Hours = time.endsWith('A');
	}

	const clayFormat = dateFormat
		.replace('YYYY', 'yyyy')
		.replace('DD', 'dd')
		.replace('D', 'd');

	const placeholder = momentFormat.replace(WORD_CHARACTER_REGEX, '_');

	const serverFormat = isDateTime
		? SERVER_DATE_TIME_FORMAT
		: SERVER_DATE_FORMAT;

	return {
		clayFormat,
		firstDayOfWeek,
		isDateTime,
		momentFormat,
		placeholder,
		serverFormat,
		use12Hours,
	};
}

export function generateDate({
	isDateTime,
	momentFormat,
	serverFormat,
	value,
}: GenerateDateProps) {
	let formattedDate = value;
	if (isDateTime) {
		const firstSpace = value.indexOf(' ');
		formattedDate = value.substring(0, firstSpace);
		const formattedTime = value
			.substring(firstSpace)
			.replace(MINUS_REGEX, '_');
		formattedDate = `${formattedDate}${formattedTime}`;
	}
	const nextState: Date = {
		formattedDate,
		rawDate: '',
	};

	const date = moment(formattedDate, momentFormat, true);
	if (date.isValid()) {
		nextState.rawDate = date.locale('en').format(serverFormat);
		nextState.years = {end: date.year() + 5, start: date.year() - 5};
	}

	return nextState;
}

export function generateInputMask(momentFormat: string) {
	const mask: maskItem[] = [];
	[...momentFormat].forEach((char) => {
		if (char === 'A' || char === 'a') {
			mask.push(A_OR_P_CHARACTER_REGEX);
			mask.push(M_CHARACTER_REGEX);

			return;
		}
		mask.push(LETTER_REGEX.test(char) ? DIGIT_REGEX : char);
	});

	const pipeFormat = momentFormat
		.split(PIPE_FORBIDDEN_ENDING_CHAR_REGEX)
		.reduce((format, item) => {
			switch (item) {
				case 'YYYY':
					return `${format} yyyy`;
				case 'MM':
					return `${format} mm`;
				case 'DD':
					return `${format} dd`;
				case 'mm':
					return `${format} MM`;
				case 'A':
				case 'a':
					return format;
				case '':
					return `${format} `;
				default:
					return `${format} ${item}`;
			}
		}, '')
		.trim();

	return {mask, pipeFormat};
}
