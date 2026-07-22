import BasePage from 'shared/components/base-page';
import MetricBaseCard from '../MetricBaseCard';
import React from 'react';
import {
	BounceRateMetric,
	CompositeMetric,
	Metric,
	SessionDurationMetric,
	SessionsPerVisitorMetric,
} from '../metrics';
import {getSiteMetricsChartData} from 'shared/components/metric-card/util';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {render, waitFor} from '@testing-library/react';
import {SitesMetricQuery, SitesTabsQuery} from '../queries';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(() => ({
		data: 13,
		loading: false,
	})),
}));

const metrics: Metric[] = [
	CompositeMetric,
	SessionsPerVisitorMetric,
	SessionDurationMetric,
	BounceRateMetric,
];

const sharedRequestVariables = {
	channelId: '456',
	devices: 'Any',
	interval: 'D',
	location: 'Any',
	rangeEnd: null,
	rangeKey: parseInt(RangeKeyTimeRanges.Last30Days),
	rangeStart: null,
};

const tabsResult = {
	site: {
		__typename: 'SiteMetric',
		bounceRateMetric: {
			__typename: 'Metric',
			previousValue: null,
			trend: {
				__typename: 'Trend',
				percentage: null,
				trendClassification: 'NEUTRAL',
			},
			value: 0,
		},
		sessionDurationMetric: {
			__typename: 'Metric',
			previousValue: null,
			trend: {
				__typename: 'Trend',
				percentage: null,
				trendClassification: 'NEUTRAL',
			},
			value: 0,
		},
		sessionsPerVisitorMetric: {
			__typename: 'Metric',
			previousValue: null,
			trend: {
				__typename: 'Trend',
				percentage: null,
				trendClassification: 'NEUTRAL',
			},
			value: 0,
		},
		visitorsMetric: {
			__typename: 'Metric',
			previousValue: null,
			trend: {
				__typename: 'Trend',
				percentage: null,
				trendClassification: 'NEUTRAL',
			},
			value: 0,
		},
	},
};

const compositeMetricResult = {
	site: {
		__typename: 'SiteMetric',
		anonymousVisitorsMetric: {
			__typename: 'HistogramMetric',
			histogram: {
				__typename: 'Histogram',
				asymmetricComparison: false,
				metrics: [],
			},
			previousValue: null,
			trend: {
				__typename: 'Trend',
				percentage: null,
				trendClassification: 'NEUTRAL',
			},
			value: 0,
		},
		knownVisitorsMetric: {
			__typename: 'HistogramMetric',
			histogram: {
				__typename: 'Histogram',
				asymmetricComparison: false,
				metrics: [],
			},
			previousValue: null,
			trend: {
				__typename: 'Trend',
				percentage: null,
				trendClassification: 'NEUTRAL',
			},
			value: 0,
		},
		visitorsMetric: {
			__typename: 'HistogramMetric',
			histogram: {
				__typename: 'Histogram',
				asymmetricComparison: false,
				metrics: [],
			},
			previousValue: null,
			trend: {
				__typename: 'Trend',
				percentage: null,
				trendClassification: 'NEUTRAL',
			},
			value: 0,
		},
	},
};

const buildMocks = (counter: {tabs: number; metric: number}) => [
	{
		newData: () => {
			counter.tabs += 1;
			return {data: tabsResult};
		},
		request: {
			query: SitesTabsQuery,
			variables: sharedRequestVariables,
		},
	},
	{
		newData: () => {
			counter.metric += 1;
			return {data: compositeMetricResult};
		},
		request: {
			query: SitesMetricQuery('visitorsMetric'),
			variables: sharedRequestVariables,
		},
	},
];

const renderWithProviders = (
	mocks: ReturnType<typeof buildMocks>,
	{visible = true}: {visible?: boolean} = {}
) =>
	render(
		<MockedProvider addTypename={false} mocks={mocks}>
			<BasePage.Context.Provider
				value={{
					filters: {},
					router: {
						params: {channelId: '456', groupId: '2000'},
						query: {rangeKey: RangeKeyTimeRanges.Last30Days},
					},
				}}
			>
				<MemoryRouter
					initialEntries={[
						`/workspace/2000/456/sites?rangeKey=${RangeKeyTimeRanges.Last30Days}`,
					]}
				>
					<RouterRoutes>
						<Route
							element={
								visible && (
									<MetricBaseCard
										chartDataMapFn={getSiteMetricsChartData}
										label="Visitors Behavior"
										metrics={metrics}
										queries={{
											MetricQuery: SitesMetricQuery,
											name: 'site',
											TabsQuery: SitesTabsQuery,
										}}
										variables={() => ({
											...sharedRequestVariables,
										})}
									/>
								)
							}
							path="/workspace/:groupId/:channelId/sites/*"
						/>
					</RouterRoutes>
				</MemoryRouter>
			</BasePage.Context.Provider>
		</MockedProvider>
	);

describe('MetricBaseCard network behavior', () => {
	it('fires only one TabsQuery on initial mount', async () => {
		const counter = {metric: 0, tabs: 0};

		renderWithProviders(buildMocks(counter));

		await waitFor(() => {
			expect(counter.tabs).toBeGreaterThan(0);
		});

		expect(counter.tabs).toBe(1);
		expect(counter.metric).toBe(1);
	});
});
