import React from 'react';
import {formatUTCDate, getCustomDateFormat, getDateNow} from 'shared/util/date';
import {Text} from '@clayui/core';

// The backend computes every lifecycle metric over the trailing 90 days, not
// counting today (i.e. yesterday back through 90 days prior). The window is a
// fixed rule rather than a configurable value, so it is derived here instead
// of being fetched.

const LIFECYCLE_DATE_RANGE_DAYS = 90;

const LifecycleDateRangeIndicator: React.FC = () => {
	const today = getDateNow();

	const endDate = today.clone().subtract(1, 'day');
	const startDate = today.clone().subtract(LIFECYCLE_DATE_RANGE_DAYS, 'days');

	const format = getCustomDateFormat();

	return (
		<span className="mr-2">
			<Text color="secondary" size={4}>
				{`${formatUTCDate(startDate, format)} – ${formatUTCDate(endDate, format)}`}
			</Text>
		</span>
	);
};

export default LifecycleDateRangeIndicator;
