import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import TopPagesCard from '../TopPagesCard';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const MOCK_CONTEXT = {
	accountId: 'acc-1',
	filters: {},
	router: {
		params: {
			channelId: '123',
			groupId: '456',
			id: 'acc-1',
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

	it('should render the unique visitors of the visited pages tab', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('Excavator Maintenance')).toBeInTheDocument();
		expect(
			screen.getByText('/group/guest/excavator-maintenance')
		).toBeInTheDocument();
		expect(screen.getByText('18')).toBeInTheDocument();
	});

	it('should link the page title to the page dashboard', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		expect(
			screen
				.getByText('Excavator Maintenance')
				.closest('a')
				?.getAttribute('href')
		).toBe(
			'/workspace/456/123/sites/pages/overview/%2Fgroup%2Fguest%2Fexcavator-maintenance/Excavator%20Maintenance'
		);
	});

	it('should render the entrances of the entrance pages tab', async () => {
		const {container} = renderTopPagesCard();

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(screen.getByText('Entrance Pages'));

		expect(screen.getByText('Excavator Maintenance')).toBeInTheDocument();
		expect(screen.getByText('12')).toBeInTheDocument();
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
