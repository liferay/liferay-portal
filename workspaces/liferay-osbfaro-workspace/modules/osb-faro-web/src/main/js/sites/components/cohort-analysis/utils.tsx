import moment from 'moment';
import {CHART_COLOR_NAMES} from 'shared/util/charts';
import {getDayMonthFormat, getFullDayMonthFormat} from 'shared/util/date';
import {sub} from 'shared/util/lang';

const {
	martell,
	martellD2,
	martellL2,
	martellL4,
	mormont,
	mormontD2,
	mormontL2,
	mormontL4,
	stark,
	starkD2,
	starkL2,
	starkL4,
} = CHART_COLOR_NAMES;

export type IntervalType = 'D' | 'W' | 'M';

export type VisitorsType = 'visitors' | 'anonymousVisitors' | 'knownVisitors';

export const DAY: IntervalType = 'D';
export const MONTH: IntervalType = 'M';
export const WEEK: IntervalType = 'W';

export const ANONYMOUS_VISITORS: VisitorsType = 'anonymousVisitors';
export const KNOWN_VISITORS: VisitorsType = 'knownVisitors';
export const VISITORS: VisitorsType = 'visitors';

export const INTERVAL_OPTIONS = [
	{
		label: Liferay.Language.get('day'),
		value: DAY,
	},
	{
		label: Liferay.Language.get('week'),
		value: WEEK,
	},
	{
		label: Liferay.Language.get('month'),
		value: MONTH,
	},
];

export const VISITORS_TYPE_OPTIONS = [
	{
		label: Liferay.Language.get('all-visitors'),
		value: VISITORS,
	},
	{
		label: Liferay.Language.get('anonymous-visitors'),
		value: ANONYMOUS_VISITORS,
	},
	{
		label: Liferay.Language.get('known-visitors'),
		value: KNOWN_VISITORS,
	},
];

const COHORT_COLORS_MAP = {
	[ANONYMOUS_VISITORS]: [mormontL4, mormontL2, mormont, mormontD2],
	[KNOWN_VISITORS]: [starkL4, starkL2, stark, starkD2],
	[VISITORS]: [martellL4, martellL2, martell, martellD2],
};

export const formatDate = (
	date: string,
	interval: IntervalType,
	abbreviated: boolean = false
): string => {
	const momentDate = moment(date);

	const intervalFormat = abbreviated ? `${interval}-abbreviated` : interval;

	switch (intervalFormat) {
		case `${MONTH}-abbreviated`:
			return momentDate.format('MMM');

		case `${WEEK}-abbreviated`:
			return `${momentDate.format(getDayMonthFormat())} - ${moment(date)
				.add(7, 'day')
				.format(getDayMonthFormat())}`;

		case `${DAY}-abbreviated`:
			return momentDate.format(getDayMonthFormat());

		case MONTH:
			return momentDate.format('MMMM');

		case WEEK:
			return `${momentDate.format(getDayMonthFormat())} - ${moment(date)
				.add(7, 'day')
				.format(getDayMonthFormat())}`;

		case DAY:
		default:
			return momentDate.format(getFullDayMonthFormat());
	}
};

const PERIOD_LABEL_MAP = {
	[DAY]: Liferay.Language.get('day-x'),
	[MONTH]: Liferay.Language.get('month-x'),
	[WEEK]: Liferay.Language.get('week-x'),
};

export const getPeriodLabel = (
	period: number,
	interval: IntervalType
): string | string[] => sub(PERIOD_LABEL_MAP[interval], [period]);

export const getColorHex = (
	retention: number,
	visitorsType: VisitorsType
): string => {
	const cohortColors = COHORT_COLORS_MAP[visitorsType];

	if (retention >= 75) {
		return cohortColors[3];
	}
	else if (retention >= 50) {
		return cohortColors[2];
	}
	else if (retention >= 25) {
		return cohortColors[1];
	}
	else {
		return cohortColors[0];
	}
};
