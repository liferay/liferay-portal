import {formatTime} from 'shared/util/time';
import {DateFormat, formatUTCDate} from 'shared/util/date';
import {isBlank} from 'shared/util/util';
import {NO_DATA_VALUE} from 'shared/util/constants';
import {
	formatPercent,
	formatPercentFromRatio,
	toCurrency,
	toLocale,
	toRounded,
	toThousands,
} from 'shared/util/numbers';

export type DataValueType =
	| 'count'
	| 'currency'
	| 'date'
	| 'duration'
	| 'number'
	| 'percent'
	| 'ratio'
	| 'text';

export interface IDataValueOptions {
	currencyCode?: string;
	dateFormat?: DateFormat | string;
	locale?: string;
	precision?: number;
}

const NUMERIC_TYPES: DataValueType[] = [
	'count',
	'currency',
	'duration',
	'number',
	'percent',
	'ratio',
];

export const isMissingDataValue = (
	value: unknown,
	type: DataValueType = 'text'
): boolean =>
	isBlank(value) ||
	value === NO_DATA_VALUE ||
	(NUMERIC_TYPES.includes(type) && !Number.isFinite(Number(value)));

export const formatDataValue = (
	value: unknown,
	type: DataValueType = 'text',
	{currencyCode, dateFormat, locale, precision}: IDataValueOptions = {}
): string => {
	if (isMissingDataValue(value, type)) {
		return NO_DATA_VALUE;
	}

	switch (type) {
		case 'count':
			return toThousands(Number(value), locale);
		case 'currency':
			return toCurrency(Number(value), currencyCode, locale);
		case 'date':
			return formatUTCDate(value as string | number, dateFormat);
		case 'duration':
			return formatTime(Number(value));
		case 'number':
			return isBlank(precision)
				? toLocale(Number(value), locale)
				: toRounded(Number(value), precision, locale);
		case 'percent':
			return formatPercent(Number(value), precision, locale);
		case 'ratio':
			return formatPercentFromRatio(Number(value), precision, locale);
		default:
			return String(value);
	}
};
