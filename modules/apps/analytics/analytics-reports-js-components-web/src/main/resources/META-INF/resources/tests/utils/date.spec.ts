/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RangeSelectors} from '../../js/components/RangeSelectorsDropdown';
import {formatDate, getDateRange, toUnix} from '../../js/utils/date';

describe('date utils', () => {
	it('format date', () => {
		const formattedDate = formatDate(
			new Date(0),
			RangeSelectors.Last30Days
		);

		expect(formattedDate).toEqual('1970 Jan 1');
	});

	it('format date for last 24 hours', () => {
		const formattedDate = formatDate(
			new Date(0),
			RangeSelectors.Last24Hours
		);

		expect(formattedDate).toEqual('Jan 1, 12 AM');
	});

	it('get date range', () => {
		const dateRange = getDateRange(RangeSelectors.Last30Days, new Date(0));

		expect(dateRange).toEqual({
			endDate: new Date('1969-12-01T00:00:00.000Z'),
			startDate: new Date('1969-12-31T00:00:00.000Z'),
		});
	});

	it('transform to unix', () => {
		const unix = toUnix('2025-01-28T10:00');

		expect(unix).toEqual(1738058400000);
	});
});
