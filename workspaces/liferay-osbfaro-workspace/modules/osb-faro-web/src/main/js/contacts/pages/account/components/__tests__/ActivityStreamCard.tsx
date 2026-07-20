import ActivityStreamCard from '../ActivityStreamCard';
import mockStore from 'test/mock-store';
import React from 'react';
import {act, fireEvent, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {
	mockAccountEventMetricsReq,
	mockAccountEventsTrendReq,
	mockAccountUserSessionsReq,
} from 'test/graphql-data';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC',
	}),
}));

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}: {children: React.ReactNode}) => (
			<OriginalModule.ResponsiveContainer height={350} width={800}>
				{children}
			</OriginalModule.ResponsiveContainer>
		),
	};
});

const SEARCH_KEYWORDS = 'add to cart';

interface WrapperProps {
	mocks: any[];
}

const Wrapper: React.FC<WrapperProps> = ({mocks}) => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<MockedProvider mocks={mocks}>
				<ActivityStreamCard
					accountId="abc"
					channelId="123123"
					interval="D"
					rangeSelectors={{
						rangeEnd: null,
						rangeKey: RangeKeyTimeRanges.Last30Days,
						rangeStart: null,
					}}
				/>
			</MockedProvider>
		</MemoryRouter>
	</Provider>
);

describe('ActivityStreamCard', () => {
	it('renders the search input and the timeline after queries resolve', async () => {
		const {container, getByPlaceholderText, getByText} = render(
			<Wrapper
				mocks={[
					mockAccountEventMetricsReq(),
					mockAccountEventsTrendReq(),
					mockAccountUserSessionsReq(),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByPlaceholderText('Search')).toBeInTheDocument();
		expect(getByText('Jane Doe')).toBeInTheDocument();
	});

	it('drives pagination from the activity stream event total, not the session count', async () => {
		const {container} = render(
			<Wrapper
				mocks={[
					mockAccountEventMetricsReq(),
					mockAccountEventsTrendReq(),
					mockAccountUserSessionsReq({totalEvents: 186}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelector('.pagination-results')
		).toHaveTextContent('186');
	});

	it('renders the empty state when the histogram has no events', async () => {
		const {container, getByText} = render(
			<Wrapper
				mocks={[
					mockAccountEventMetricsReq({empty: true}),
					mockAccountEventsTrendReq({
						percentage: 0,
						trendClassification: 'NEUTRAL',
						value: 0,
					}),
					mockAccountUserSessionsReq({sessions: [], totalEvents: 0}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			getByText('There is no data for account activities.')
		).toBeInTheDocument();
		expect(
			getByText('Learn more about account activities.')
		).toBeInTheDocument();
		expect(container.querySelector('.recharts-cartesian-grid')).toBeNull();
	});

	it('clears the search and reverts to the un-filtered list when the no-results clear button is clicked', async () => {
		const {container, getByPlaceholderText, getByText} = render(
			<Wrapper
				mocks={[
					mockAccountEventMetricsReq(),
					mockAccountEventsTrendReq(),
					mockAccountUserSessionsReq(),
					mockAccountEventMetricsReq({keywords: SEARCH_KEYWORDS}),
					mockAccountEventsTrendReq({keywords: SEARCH_KEYWORDS}),
					mockAccountUserSessionsReq({
						keywords: SEARCH_KEYWORDS,
						sessions: [],
						totalEvents: 0,
					}),
					mockAccountEventMetricsReq(),
					mockAccountEventsTrendReq(),
					mockAccountUserSessionsReq(),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const searchInput = getByPlaceholderText('Search');

		fireEvent.change(searchInput, {target: {value: SEARCH_KEYWORDS}});
		fireEvent.keyDown(searchInput, {
			charCode: 13,
			code: 'Enter',
			key: 'Enter',
		});

		await act(async () => {
			await jest.advanceTimersByTimeAsync(500);
		});

		expect(getByText('There are no results found.')).toBeInTheDocument();

		fireEvent.click(getByText('Clear Search'));

		await act(async () => {
			await jest.advanceTimersByTimeAsync(500);
		});

		expect(getByPlaceholderText('Search')).toHaveValue('');
		expect(getByText('Jane Doe')).toBeInTheDocument();
	});

	it('renders an up-arrow icon for a positive trend', async () => {
		const {container} = render(
			<Wrapper
				mocks={[
					mockAccountEventMetricsReq(),
					mockAccountEventsTrendReq({
						percentage: 22.5,
						trendClassification: 'POSITIVE',
						value: 56,
					}),
					mockAccountUserSessionsReq(),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelector('.lexicon-icon-caret-top-l')
		).toBeInTheDocument();
	});

	it('renders a down-arrow icon for a negative trend', async () => {
		const {container} = render(
			<Wrapper
				mocks={[
					mockAccountEventMetricsReq(),
					mockAccountEventsTrendReq({
						percentage: -10,
						trendClassification: 'NEGATIVE',
						value: 4,
					}),
					mockAccountUserSessionsReq(),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelector('.lexicon-icon-caret-bottom-l')
		).toBeInTheDocument();
	});

	it('renders no trend icon for a neutral trend', async () => {
		const {container} = render(
			<Wrapper
				mocks={[
					mockAccountEventMetricsReq(),
					mockAccountEventsTrendReq({
						percentage: 0,
						trendClassification: 'NEUTRAL',
						value: 56,
					}),
					mockAccountUserSessionsReq(),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.lexicon-icon-caret-top-l')).toBeNull();
		expect(
			container.querySelector('.lexicon-icon-caret-bottom-l')
		).toBeNull();
	});
});
