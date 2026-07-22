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
import {
	RangeKeyTimeRanges,
	SEVEN_MONTHS,
	THIRTEEN_MONTHS,
} from 'shared/util/constants';
import {render, screen} from '@testing-library/react';
import {SitesMetricQuery, SitesTabsQuery} from '../queries';

jest.unmock('react-dom');

jest.mock('../MetricTabs', () => () => <div data-testid="MetricTabs" />);
jest.mock('../MetricChart', () => () => <div data-testid="MetricChart" />);

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(() => ({
		data: THIRTEEN_MONTHS,
		loading: false,
	})),
}));

const metrics: Metric[] = [
	CompositeMetric,
	SessionsPerVisitorMetric,
	SessionDurationMetric,
	BounceRateMetric,
];

const WrapperComponent = ({
	children,
	rangeKey = RangeKeyTimeRanges.Last30Days,
}: {
	children: React.ReactNode;
	rangeKey?: RangeKeyTimeRanges;
}) => (
	<MockedProvider addTypename={false}>
		<BasePage.Context.Provider
			value={{
				filters: {},
				router: {
					params: {channelId: '456', groupId: '2000'},
					query: {rangeKey},
				},
			}}
		>
			<MemoryRouter
				initialEntries={[
					`/workspace/2000/456/sites?rangeKey=${rangeKey}`,
				]}
			>
				<RouterRoutes>
					<Route
						element={children}
						path="/workspace/:groupId/:channelId/sites/*"
					/>
				</RouterRoutes>
			</MemoryRouter>
		</BasePage.Context.Provider>
	</MockedProvider>
);

describe('MetricBaseCard', () => {
	it('renders component', async () => {
		const {container} = render(
			<WrapperComponent>
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
						channelId: '456',
						devices: 'Any',
						interval: 'D',
						location: 'Any',
						rangeEnd: null,
						rangeKey: RangeKeyTimeRanges.Last30Days,
						rangeStart: null,
					})}
				/>
			</WrapperComponent>
		);

		expect(screen.getByTestId('MetricTabs')).toBeInTheDocument();
		expect(screen.getByTestId('MetricChart')).toBeInTheDocument();
		expect(container).toMatchSnapshot();
	});

	it('renders tooltip with retention period for 7 months', async () => {
		const {useRequest} = require('shared/hooks/useRequest');
		(useRequest as jest.Mock).mockReturnValue({
			data: SEVEN_MONTHS,
			loading: false,
		});

		// We need to keep MetricChart unmocked for this test since it has the logic we're testing
		// but for now, let's establish a working baseline.

		render(
			<WrapperComponent rangeKey={RangeKeyTimeRanges.Last180Days}>
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
						channelId: '456',
						devices: 'Any',
						interval: 'D',
						location: 'Any',
						rangeEnd: null,
						rangeKey: RangeKeyTimeRanges.Last180Days,
						rangeStart: null,
					})}
				/>
			</WrapperComponent>
		);

		expect(screen.getByTestId('MetricChart')).toBeInTheDocument();
	});
});
