import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import TopPagesCard from '../TopPagesCard';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockSitesTopPagesReq,
	mockPreferenceReq,
	mockTimeRangeReq,
} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const INDIVIDUAL_ID = 'ind-1';

const MOCK_CONTEXT = {
	filters: {},
	individualId: INDIVIDUAL_ID,
	individualName: 'Jane Doe',
	router: {
		params: {
			channelId: '123',
			groupId: '456',
			id: INDIVIDUAL_ID,
		},
		query: {
			rangeKey: RangeKeyTimeRanges.Last30Days,
		},
	},
};

const renderTopPagesCard = () =>
	render(
		<Provider store={mockStore()}>
			<BasePage.Context.Provider value={MOCK_CONTEXT}>
				<MemoryRouter>
					<MockedProvider
						addTypename={false}
						mocks={[
							mockTimeRangeReq(),
							mockPreferenceReq(),
							mockSitesTopPagesReq({
								individualId: INDIVIDUAL_ID,
							}),
							mockSitesTopPagesReq({
								individualId: INDIVIDUAL_ID,
								sort: {
									column: 'entrancesMetric',
									type: 'DESC',
								},
							}),
						]}
					>
						<TopPagesCard />
					</MockedProvider>
				</MemoryRouter>
			</BasePage.Context.Provider>
		</Provider>
	);

const mockEmptyIndividualTopPagesReq = () => ({
	...mockSitesTopPagesReq({individualId: INDIVIDUAL_ID}),
	result: {
		data: {
			pages: {
				__typename: 'AssetMetricBag',
				assetMetrics: [],
				total: 0,
			},
		},
	},
});

const renderTopPagesCardWithoutPages = () =>
	render(
		<Provider store={mockStore()}>
			<BasePage.Context.Provider value={MOCK_CONTEXT}>
				<MemoryRouter>
					<MockedProvider
						addTypename={false}
						mocks={[
							mockTimeRangeReq(),
							mockPreferenceReq(),
							mockEmptyIndividualTopPagesReq(),
						]}
					>
						<TopPagesCard />
					</MockedProvider>
				</MemoryRouter>
			</BasePage.Context.Provider>
		</Provider>
	);

/**
 * Renders with no mock for the pages query on purpose: the card must not run
 * it, and Apollo surfaces an unmatched query as an error state.
 */

const renderTopPagesCardWithoutIndividual = () =>
	render(
		<Provider store={mockStore()}>
			<BasePage.Context.Provider
				value={{
					...MOCK_CONTEXT,
					individualId: undefined,
					individualName: undefined,
				}}
			>
				<MemoryRouter>
					<MockedProvider
						addTypename={false}
						mocks={[mockTimeRangeReq(), mockPreferenceReq()]}
					>
						<TopPagesCard />
					</MockedProvider>
				</MemoryRouter>
			</BasePage.Context.Provider>
		</Provider>
	);

describe('TopPagesCard', () => {
	afterEach(cleanup);

	it('should render the card with a tab per metric', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('TOP PAGES')).toBeInTheDocument();
		expect(screen.getByText('Visited Pages')).toBeInTheDocument();
		expect(screen.getByText('Entrance Pages')).toBeInTheDocument();
		expect(screen.getByText('Exit Pages')).toBeInTheDocument();
	});

	it('should render the unique visitors of the pages of the individual', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('My asset A')).toBeInTheDocument();
		expect(screen.getByText('20')).toBeInTheDocument();
	});

	it('should link the page title to the individual filtered page dashboard', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		const href = screen
			.getByText('My asset A')
			.closest('a')
			?.getAttribute('href');

		expect(href).toContain(
			'/workspace/456/123/sites/pages/overview/123/My%20asset%20A'
		);
		expect(href).toContain('individualId=ind-1');
		expect(href).toContain('individualName=Jane+Doe');
	});

	it('should sort by entrances on the entrance pages tab', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(screen.getByText('Entrance Pages'));

		expect(await screen.findByText('My asset A')).toBeInTheDocument();
		expect(screen.getByText('10')).toBeInTheDocument();
	});

	it('should not render the footer action', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		expect(screen.queryByText('View All')).toBeNull();
	});

	it('should request nothing while the individual is unknown', async () => {
		renderTopPagesCardWithoutIndividual();

		expect(
			await screen.findByText('No Pages Available')
		).toBeInTheDocument();
		expect(screen.queryByText('An unexpected error occurred.')).toBeNull();
	});

	it('should render the empty state when the individual has no pages', async () => {
		const {container} = renderTopPagesCardWithoutPages();

		await waitForLoadingToBeRemoved(container);

		expect(
			await screen.findByText('No Pages Available')
		).toBeInTheDocument();
		expect(
			screen.getByText('Pages will appear here when available.')
		).toBeInTheDocument();
	});
});
