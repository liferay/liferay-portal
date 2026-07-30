import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import TopPagesCard from '../TopPagesCard';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockPreferenceReq,
	mockSitesTopPagesReq,
	mockTimeRangeReq,
} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const ACCOUNT_ID = 'acc-1';

const MOCK_CONTEXT = {
	accountId: ACCOUNT_ID,
	filters: {},
	router: {
		params: {
			channelId: '123',
			groupId: '456',
			id: ACCOUNT_ID,
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
							mockSitesTopPagesReq({accountId: ACCOUNT_ID}),
							mockSitesTopPagesReq({
								accountId: ACCOUNT_ID,
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

	it('should render the unique visitors of the pages of the account', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('My asset A')).toBeInTheDocument();
		expect(screen.getByText('20')).toBeInTheDocument();
	});

	it('should link the page title to the page dashboard', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		expect(
			screen.getByText('My asset A').closest('a')?.getAttribute('href')
		).toBe('/workspace/456/123/sites/pages/overview/123/My%20asset%20A');
	});

	it('should sort by entrances on the entrance pages tab', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(screen.getByText('Entrance Pages'));

		expect(await screen.findByText('My asset A')).toBeInTheDocument();
		expect(screen.getByText('10')).toBeInTheDocument();
	});

	it('should point the footer action to the site pages list', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		expect(
			screen.getByText('View All').closest('a')?.getAttribute('href')
		).toContain('/workspace/456/123/sites/pages');
	});

	it('should render the footer action like the account info one', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		const link = screen.getByText('View All').closest('a');

		expect(link).toHaveClass(
			'btn-outline-borderless',
			'btn-outline-primary',
			'btn-sm',
			'ml-auto',
			'rounded-lg'
		);
	});
});
