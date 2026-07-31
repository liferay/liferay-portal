import * as data from 'test/data';
import mockStore from 'test/mock-store';
import OverviewRealTime from '../OverviewRealTime';
import React from 'react';
import {Provider} from 'react-redux';
import {render, screen} from '@testing-library/react';
import {Segment} from 'shared/util/records';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC',
	}),
}));

const renderOverviewRealTime = () =>
	render(
		<Provider store={mockStore()}>
			<StaticRouter>
				<OverviewRealTime
					channelId="123"
					groupId="23"
					segment={
						new Segment(
							data.mockSegment(0, {
								criteriaString:
									"(demographics/middleName/value eq 'additionalName')",
							})
						)
					}
				/>
			</StaticRouter>
		</Provider>
	);

describe('OverviewRealTime', () => {
	it('should display the segment criteria', () => {
		renderOverviewRealTime();

		expect(screen.getByText('Segment Criteria')).toBeTruthy();
	});

	it('should not display the membership of the segment', () => {
		renderOverviewRealTime();

		expect(screen.queryByText('Segment Membership Trend')).toBeNull();
		expect(screen.queryByText('Total Members')).toBeNull();
	});
});
