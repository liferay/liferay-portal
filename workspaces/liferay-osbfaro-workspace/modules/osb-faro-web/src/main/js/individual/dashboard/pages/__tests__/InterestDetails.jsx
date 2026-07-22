import InterestDetails from '../InterestDetails';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockTimeRangeReq, mockTouchpointsReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {waitForLoading} from 'test/helpers';

jest.unmock('react-dom');

const mockItems = [
	{
		__typename: 'PageMetric',
		assetId: 'https://www.liferay.com',
		assetTitle: 'Dashboard - Retail',
		avgTimeOnPageMetric: {
			__typename: 'Metric',
			value: 23
		},
		bounceRateMetric: {
			__typename: 'Metric',
			value: 0.23
		},
		dataSourceId: '123123',
		entrancesMetric: {
			__typename: 'Metric',
			value: 56
		},
		exitRateMetric: {
			__typename: 'Metric',
			value: 0.53
		},
		viewsMetric: {__typename: 'Metric', value: 243.0},
		visitorsMetric: {
			__typename: 'Metric',
			value: 45.0
		}
	}
];

describe('Individuals Dashboard Individuals Interest Details', () => {
	afterEach(cleanup);

	it('renders', async () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<MockedProvider
					mocks={[
						mockTouchpointsReq(mockItems, {size: 2}),
						mockTimeRangeReq()
					]}
				>
					<MemoryRouter
						initialEntries={[
							'/workspace/23/321321/contacts/individuals/interests/test'
						]}
					>
						<RouterRoutes>
							<Route
								element={
									<InterestDetails
										router={{
											params: {
												channelId: '321321',
												groupId: '23',
												interestId: 'test'
											},
											query: {
												delta: '2',
												page: '1',
												rangeEnd: null,
												rangeKey: '30',
												rangeStart: null
											}
										}}
									/>
								}
								path={`${Routes.CONTACTS_INDIVIDUALS_INTEREST_DETAILS}/*`}
							/>
						</RouterRoutes>
					</MemoryRouter>
				</MockedProvider>
			</Provider>
		);

		await waitForLoading(document.body);

		expect(getByText('Back to Interests')).toBeInTheDocument();
	});
});
