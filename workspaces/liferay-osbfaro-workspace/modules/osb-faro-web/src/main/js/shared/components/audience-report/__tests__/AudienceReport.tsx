import AudienceReport from '../AudienceReport';
import mockStore from 'test/mock-store';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route} from 'react-router-dom';
import {MetricName} from 'shared/types/MetricName';
import {
	mockAudienceReportReq,
	mockPreferenceReq,
	mockSegmentReq,
	mockTimeRangeReq,
} from 'test/graphql-data';
import {MockedProvider} from '@apollo/client/testing';
import {Name} from '../types';
import {PageAudienceReportQuery, PageSegmentQuery} from '../queries';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {typePolicies} from 'shared/apollo/cache';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const tooltipEnabled = jest.fn();

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}: {children: React.ReactNode}) => (
			<OriginalModule.ResponsiveContainer height={350} width={800}>
				{children}
			</OriginalModule.ResponsiveContainer>
		),
		Tooltip: ({
			children,
			...props
		}: {
			children: React.ReactNode;
			active?: boolean;
		}) => {
			if (props.active) {
				tooltipEnabled();
			}

			return (
				<OriginalModule.Tooltip {...props} active>
					{children}
				</OriginalModule.Tooltip>
			);
		},
	};
});

const WrappedComponent = ({queryProps}: {queryProps: any}) => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={[
				'/workspace/123/456/Home%20Page/https%3A%2F%2Fwww.liferay.com',
			]}
		>
			<Route path="/workspace/:groupId/:channelId/:title/:touchpoint">
				<MockedProvider
					{...({freezeResults: false} as any)}
					cache={new InMemoryCache({typePolicies})}
					mocks={[
						mockTimeRangeReq(),
						mockPreferenceReq(),
						mockAudienceReportReq({queryProps}),
						mockSegmentReq({queryProps}),
					]}
				>
					<AudienceReport
						AudienceReportQuery={PageAudienceReportQuery(
							queryProps
						)}
						filters={{devices: [], location: []}}
						mapper={(result: any) =>
							result?.[queryProps.name]?.[queryProps.metricName]
						}
						name={Name.Page}
						rangeSelectors={{
							rangeEnd: '',
							rangeKey: RangeKeyTimeRanges.Last30Days,
							rangeStart: '',
						}}
						SegmentQuery={PageSegmentQuery(queryProps)}
					/>
				</MockedProvider>
			</Route>
		</MemoryRouter>
	</Provider>
);

describe('AudienceReport', () => {
	it('should render', async () => {
		const {container} = render(
			<WrappedComponent
				queryProps={{
					metricName: MetricName.Views,
					name: Name.Page,
				}}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render a tooltip when donuts mouse over', async () => {
		const {container} = render(
			<WrappedComponent
				queryProps={{
					metricName: MetricName.Views,
					name: Name.Page,
				}}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const donut = container.querySelector('.recharts-pie-sector');

		expect(donut).toBeInTheDocument();

		fireEvent.mouseEnter(donut!);

		expect(tooltipEnabled).toHaveBeenCalledTimes(1);
	});
});
