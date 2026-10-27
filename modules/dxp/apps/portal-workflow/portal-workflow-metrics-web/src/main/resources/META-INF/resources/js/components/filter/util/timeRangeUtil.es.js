/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	defaultDateFormat,
	formatDate,
	getLocaleDateFormat,
	isValidDate,
} from '../../../shared/util/date.es';
import moment from '../../../shared/util/moment.es';

const convertQueryDate = (date = '', format = 'L') => {
	return moment(decodeURIComponent(date), null, 'en').format(format);
};

const parseDateMoment = (date, format = 'L') => {
	return moment(date, format, 'en');
};

const formatDateTime = (date, format, isEndDate) => {
	let dateTime = parseDateMoment(date, format || 'L');

	dateTime = isEndDate ? dateTime.endOf('day') : dateTime.startOf('day');

	return dateTime.format(defaultDateFormat);
};

const formatDescriptionDate = (date) => {
	return formatDate(
		decodeURIComponent(date),
		getLocaleDateFormat('ll'),
		defaultDateFormat
	);
};

const getFormatPattern = (dateEndMoment, dateStartMoment, isAmPm) => {
	let dateStartPattern = Liferay.Language.get('mmm-dd-yyyy');

	if (dateEndMoment.diff(dateStartMoment, 'days') <= 1) {
		if (isAmPm) {
			dateStartPattern = Liferay.Language.get('mmm-dd-hh-mm-a');
		}
		else {
			dateStartPattern = Liferay.Language.get('mmm-dd-hh-mm');
		}
	}
	else if (dateEndMoment.diff(dateStartMoment, 'years') < 1) {
		dateStartPattern = Liferay.Language.get('mmm-dd');
	}

	let dateEndPattern = dateStartPattern;

	if (dateEndMoment.diff(dateStartMoment, 'days') > 90) {
		dateEndPattern = Liferay.Language.get('mmm-dd-yyyy');
	}

	return {
		dateEndPattern,
		dateStartPattern,
	};
};

const formatTimeRange = (timeRange, isAmPm) => {
	const {dateEnd, dateStart} = timeRange;

	if (!dateEnd && !dateStart) {
		return null;
	}

	const dateEndMoment = moment(dateEnd);
	const dateStartMoment = moment(dateStart);

	const {dateEndPattern, dateStartPattern} = getFormatPattern(
		dateEndMoment,
		dateStartMoment,
		isAmPm
	);

	return `${dateStartMoment.format(
		dateStartPattern
	)} - ${dateEndMoment.format(dateEndPattern)}`;
};

const getCustomTimeRange = (dateEnd, dateStart) => {
	const customTimeRange = {
		active: false,
		dateEnd: dateEnd ? decodeURIComponent(dateEnd) : undefined,
		dateStart: dateStart ? decodeURIComponent(dateStart) : undefined,
		dividerAfter: true,
		id: 'custom',
		name: Liferay.Language.get('custom-range'),
	};

	customTimeRange.resultName = `${formatDescriptionDate(
		dateStart
	)} - ${formatDescriptionDate(dateEnd)}`;

	return customTimeRange;
};

const getDefaultEndDate = () => {
	const date = new Date();

	let newDate = date.toISOString();

	newDate = newDate.split('.')[0] + 'Z';

	return newDate;
};

const getDefaultStartDate = () => {
	const date = new Date();

	date.setDate(date.getDate() - 29);
	date.setHours(0, 0, 0, 0);

	let newDate = date.toISOString();

	newDate = newDate.split('.')[0] + 'Z';

	return newDate;
};

const getTimeRangeParams = (dateStartEncoded = '', dateEndEncoded = '') => {
	let params = {};

	const dateEnd = decodeURIComponent(dateEndEncoded);
	const dateStart = decodeURIComponent(dateStartEncoded);

	if (
		isValidDate(dateEnd, defaultDateFormat) &&
		isValidDate(dateStart, defaultDateFormat)
	) {
		params = {
			dateEnd,
			dateStart,
		};
	}

	return params;
};

const parseDateItems = (isAmPm) => (items) => {
	return items.map((item) => {
		const parsedItem = {
			...item,
			dateEnd: item.dateEnd,
			dateStart: item.dateStart,
			key: item.key,
		};

		if (parsedItem.key !== 'custom') {
			parsedItem.description = formatTimeRange(item, isAmPm);
		}

		return parsedItem;
	});
};

export {
	convertQueryDate,
	formatDateTime,
	formatDescriptionDate,
	formatTimeRange,
	getCustomTimeRange,
	getDefaultEndDate,
	getDefaultStartDate,
	getTimeRangeParams,
	isValidDate,
	parseDateMoment,
	parseDateItems,
};
