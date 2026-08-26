import IndividualProfileCard from '../ProfileCard';
import mockStore from 'test/mock-store';
import React from 'react';
import {act, fireEvent, render} from '@testing-library/react';
import {Individual} from 'shared/util/records';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockEventMetrics,
	mockPreferenceReq,
	mockSessions,
	mockTimeRangeReq,
} from 'test/graphql-data';
import {mockIndividual} from 'test/data';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const DefaultComponent = ({children}: {children: React.ReactNode}) => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={[
				'/workspace/23/123123/contacts/individuals/known-individuals/4423123123',
			]}
		>
			<RouterRoutes>
				<Route
					element={children}
					path={`${Routes.CONTACTS_INDIVIDUAL}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

const inputValue = 'add to cart';
const searchKeyword = {keywords: inputValue};

describe('IndividualProfileCard', () => {
	it('should render', async () => {
		const {getByPlaceholderText} = render(
			<DefaultComponent>
				<MockedProvider
					mocks={[
						mockEventMetrics({
							rangeKey: 30,
						}),
						mockTimeRangeReq(),
						mockPreferenceReq(),
						mockSessions({
							rangeKey: 30,
						}),
					]}
				>
					<IndividualProfileCard
						channelId="123123"
						delta={50}
						entity={new Individual(mockIndividual())}
						groupId="123"
						interval="D"
						onChangeInterval={jest.fn()}
						onDeltaChange={jest.fn()}
						onPageChange={jest.fn()}
						onQueryChange={jest.fn()}
						onRangeSelectorsChange={jest.fn()}
						page={1}
						query=""
						rangeSelectors={{
							rangeEnd: '',
							rangeKey: RangeKeyTimeRanges.Last30Days,
							rangeStart: '',
						}}
						resetPage={jest.fn()}
						tabId=""
					/>
				</MockedProvider>
			</DefaultComponent>
		);

		await waitForLoadingToBeRemoved(document.body);

		expect(getByPlaceholderText('Search')).toBeInTheDocument();
	});

	it('shows the no-results message when an activity search returns nothing', async () => {
		const noMatch = 'no-matching-activity';

		const emptySessions = mockSessions({keywords: noMatch, rangeKey: 30});

		emptySessions.result = {
			data: {
				eventsByUserSessions: {
					__typename: 'EventsByUserSession',
					totalEvents: 0,
					totalPageGroupsMetric: {
						__typename: 'Metric',
						value: 0,
					},
					userSessions: [],
				},
			},
		};

		const {getByText} = render(
			<DefaultComponent>
				<MockedProvider
					mocks={[
						mockEventMetrics({keywords: noMatch, rangeKey: 30}),
						mockTimeRangeReq(),
						mockPreferenceReq(),
						emptySessions,
					]}
				>
					<IndividualProfileCard
						channelId="123123"
						delta={50}
						entity={new Individual(mockIndividual())}
						groupId="123"
						interval="D"
						onChangeInterval={jest.fn()}
						onDeltaChange={jest.fn()}
						onPageChange={jest.fn()}
						onQueryChange={jest.fn()}
						onRangeSelectorsChange={jest.fn()}
						page={1}
						query={noMatch}
						rangeSelectors={{
							rangeEnd: '',
							rangeKey: RangeKeyTimeRanges.Last30Days,
							rangeStart: '',
						}}
						resetPage={jest.fn()}
						tabId=""
					/>
				</MockedProvider>
			</DefaultComponent>
		);

		await waitForLoadingToBeRemoved(document.body);

		jest.runAllTimers();

		expect(getByText('No results were found.')).toBeInTheDocument();
	});

	it('should clear search input when clear button is clicked', async () => {
		const {container, getByPlaceholderText, getByText} = render(
			<DefaultComponent>
				<MockedProvider
					mocks={[
						mockEventMetrics(),
						mockTimeRangeReq(),
						mockPreferenceReq(),
						mockSessions(),
						mockEventMetrics(),
						mockSessions(),
						mockEventMetrics(),
						mockSessions(),
						mockEventMetrics(),
						mockSessions(),
						mockEventMetrics(searchKeyword),
						mockSessions(searchKeyword),
						mockEventMetrics(searchKeyword),
						mockSessions(searchKeyword),
						mockEventMetrics(searchKeyword),
						mockSessions(searchKeyword),
					]}
				>
					<IndividualProfileCard
						channelId="123123"
						delta={20}
						entity={new Individual(mockIndividual())}
						groupId="123"
						interval="D"
						onChangeInterval={jest.fn()}
						onDeltaChange={jest.fn()}
						onPageChange={jest.fn()}
						onQueryChange={jest.fn()}
						onRangeSelectorsChange={jest.fn()}
						page={0}
						query="add to cart"
						rangeSelectors={{
							rangeEnd: '',
							rangeKey: RangeKeyTimeRanges.Last30Days,
							rangeStart: '',
						}}
						resetPage={jest.fn()}
						tabId=""
					/>
				</MockedProvider>
			</DefaultComponent>
		);

		await waitForLoadingToBeRemoved(container);

		jest.runAllTimers();

		const searchInput = getByPlaceholderText('Search');

		fireEvent.change(searchInput, {target: {value: inputValue}});

		fireEvent.keyDown(searchInput, {
			charCode: 13,
			code: 'Enter',
			key: 'Enter',
		});

		jest.runAllTimers();

		expect(getByPlaceholderText('Search')).toHaveValue(inputValue);

		fireEvent.click(getByText('Clear'));

		jest.runAllTimers();

		expect(getByPlaceholderText('Search')).toHaveValue('');
	});

	it('should clear search input when X clear button is clicked', async () => {
		const {container, getByPlaceholderText} = render(
			<DefaultComponent>
				<MockedProvider
					mocks={[
						mockEventMetrics(),
						mockTimeRangeReq(),
						mockPreferenceReq(),
						mockSessions(),
						mockEventMetrics(),
						mockSessions(),
						mockEventMetrics(),
						mockSessions(),
						mockTimeRangeReq(),
						mockEventMetrics(),
						mockSessions(),
						mockTimeRangeReq(),
						mockEventMetrics(searchKeyword),
						mockTimeRangeReq(),
						mockSessions(searchKeyword),
						mockEventMetrics(searchKeyword),
						mockSessions(searchKeyword),
						mockEventMetrics(searchKeyword),
						mockSessions(searchKeyword),
					]}
				>
					<IndividualProfileCard
						channelId="123123"
						delta={20}
						entity={new Individual(mockIndividual())}
						groupId="123"
						interval="D"
						onChangeInterval={jest.fn()}
						onDeltaChange={jest.fn()}
						onPageChange={jest.fn()}
						onQueryChange={jest.fn()}
						onRangeSelectorsChange={jest.fn()}
						page={0}
						query=""
						rangeSelectors={{
							rangeEnd: '',
							rangeKey: RangeKeyTimeRanges.Last30Days,
							rangeStart: '',
						}}
						resetPage={jest.fn()}
						tabId=""
					/>
				</MockedProvider>
			</DefaultComponent>
		);

		await act(async () => {
			await jest.advanceTimersByTimeAsync(500);
		});

		const searchInput = getByPlaceholderText('Search');

		fireEvent.change(searchInput, {target: {value: inputValue}});

		fireEvent.keyDown(searchInput, {
			charCode: 13,
			code: 'Enter',
			key: 'Enter',
		});

		await act(async () => {
			await jest.advanceTimersByTimeAsync(500);
		});

		expect(getByPlaceholderText('Search')).toHaveValue(inputValue);

		fireEvent.click(container.querySelector('.lexicon-icon-times')!);

		await act(async () => {
			await jest.advanceTimersByTimeAsync(500);
		});

		expect(getByPlaceholderText('Search')).toHaveValue('');
	});

	it('should render w/ an error display', async () => {
		const {container, getByText} = render(
			<DefaultComponent>
				<MockedProvider mocks={[]}>
					<IndividualProfileCard
						channelId="123123"
						delta={20}
						entity={new Individual(mockIndividual())}
						groupId="123"
						interval="D"
						onChangeInterval={jest.fn()}
						onDeltaChange={jest.fn()}
						onPageChange={jest.fn()}
						onQueryChange={jest.fn()}
						onRangeSelectorsChange={jest.fn()}
						page={0}
						query=""
						rangeSelectors={{
							rangeEnd: '',
							rangeKey: RangeKeyTimeRanges.Last30Days,
							rangeStart: '',
						}}
						resetPage={jest.fn()}
						tabId=""
					/>
				</MockedProvider>
			</DefaultComponent>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(getByText('An unexpected error occurred.')).toBeTruthy();
	});

	it('should render w/ loading', () => {
		const {container} = render(
			<DefaultComponent>
				<MockedProvider mocks={[]}>
					<IndividualProfileCard
						channelId="123123"
						delta={20}
						entity={new Individual(mockIndividual())}
						groupId="123"
						interval="D"
						onChangeInterval={jest.fn()}
						onDeltaChange={jest.fn()}
						onPageChange={jest.fn()}
						onQueryChange={jest.fn()}
						onRangeSelectorsChange={jest.fn()}
						page={0}
						query=""
						rangeSelectors={{
							rangeEnd: '',
							rangeKey: RangeKeyTimeRanges.Last30Days,
							rangeStart: '',
						}}
						resetPage={jest.fn()}
						tabId=""
					/>
				</MockedProvider>
			</DefaultComponent>
		);

		jest.runAllTimers();

		expect(container.querySelector('.loading-root')).toBeTruthy();
	});
});
