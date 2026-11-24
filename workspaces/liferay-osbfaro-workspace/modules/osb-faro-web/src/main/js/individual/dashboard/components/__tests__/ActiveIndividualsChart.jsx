import * as data from 'test/data';
import ActiveIndividualsChart from '../ActiveIndividualsChart';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {createDateKeysIMap} from 'shared/util/intervals';
import {RangeKeyTimeRanges} from 'shared/util/constants';

describe('ActiveIndividualsChart', () => {
	afterEach(cleanup);

	it('should render', () => {
		const chartData = [
			{
				anonymousVisitors: 1,
				intervalInitDate: data.getTimestamp(-1),
				knownVisitors: 3,
				visitors: 4
			},
			{
				anonymousVisitors: 6,
				intervalInitDate: data.getTimestamp(0),
				knownVisitors: 4,
				visitors: 2
			}
		];

		const {container} = render(
			<ActiveIndividualsChart
				data={chartData}
				dateKeysIMap={createDateKeysIMap(
					RangeKeyTimeRanges.Last30Days,
					chartData,
					'intervalInitDate'
				)}
				rangeSelectors={{rangeKey: '30'}}
			/>
		);
		expect(container).toMatchSnapshot();
	});
});
