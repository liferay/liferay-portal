import React from 'react';
import {formatUTCDate, getCustomDateFormat, getDateNow} from 'shared/util/date';
import {Text} from '@clayui/core';

// Yesterday back through 90 days prior. The backend computes its metrics over
// that window and returns no boundaries with them, so the range is derived
// here rather than fetched. Today is excluded because the day is still
// accumulating and would read as a slump against the ones before it.

const TRAILING_DAYS = 90;

/**
 * The window a section's figures cover, rendered beside its heading. The window
 * is a fixed rule rather than something the reader picks, so it takes no props:
 * every section showing this is showing the same 90 days.
 *
 * Derived on each render rather than once at module scope, so a session left
 * open overnight does not keep showing the day it was opened.
 */
const TrailingNinetyDayRange: React.FC = () => {
	const format = getCustomDateFormat();
	const today = getDateNow();

	const endDate = today.clone().subtract(1, 'day');
	const startDate = today.clone().subtract(TRAILING_DAYS, 'days');

	return (
		<span className="mr-2">
			<Text color="secondary" size={4}>
				{`${formatUTCDate(startDate, format)} – ${formatUTCDate(
					endDate,
					format
				)}`}
			</Text>
		</span>
	);
};

export default TrailingNinetyDayRange;
