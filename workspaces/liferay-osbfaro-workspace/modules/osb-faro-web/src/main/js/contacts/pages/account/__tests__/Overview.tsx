import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import Overview from '../Overview';
import React from 'react';
import {AccountIndividualMetricType} from '../utils/types';
import {cleanup, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: jest.fn(),
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

const mockedUseParams = useParams as jest.Mock;
const mockedUseRequest = useRequest as jest.Mock;

const mockAccountIndividualMetrics = [
	{
		metricType: AccountIndividualMetricType.TotalIndividuals,
		value: 1234,
	},
	{
		metricType: AccountIndividualMetricType.KnownIndividuals,
		value: 800,
	},
	{
		metricType: AccountIndividualMetricType.AnonymousIndividuals,
		value: 434,
	},
	{
		metricType: AccountIndividualMetricType.ReturningIndividuals,
		value: 512,
	},
	{
		metricType: AccountIndividualMetricType.FirstTimeIndividuals,
		value: 288,
	},
	{
		metricType: AccountIndividualMetricType.InactiveIndividuals,
		value: 96,
	},
];

// `useRequest` is consumed by `Overview` itself (the account individual
// metrics, which expect an array of `IAccountIndividualMetric`) and by its
// children, such as `TopCategoriesAndTags` (which expects an object with
// `items`). Differentiate by `dataSourceFn`.

const mockRefetch = jest.fn();

const mockUseRequest = ({
	data = mockAccountIndividualMetrics,
	error = false,
	loading = false,
}: {data?: unknown; error?: boolean; loading?: boolean} = {}) =>
	mockedUseRequest.mockImplementation(
		({dataSourceFn}: {dataSourceFn?: unknown}) =>
			dataSourceFn === API.accounts.fetchAccountIndividualMetrics
				? {data, error, loading, refetch: mockRefetch}
				: {data: {items: []}, error: false, loading: false}
	);

const getMetricsCard = (title: string) =>
	screen.getByText(title).closest('.card-root') as HTMLElement;

const mockAccount = {
	accountName: 'IQVIA',
	accountType: 'Prospect',
	annualRevenue: 11359000000,
	country: 'United States',
	id: 'acc-1',
	industry: 'Business Services',
	lifecycleStage: 'ENGAGED',
};

const renderOverview = (props = {}) =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<MockedProvider
					addTypename={false}
					mocks={[mockTimeRangeReq(), mockPreferenceReq()]}
				>
					<Overview {...props} />
				</MockedProvider>
			</MemoryRouter>
		</Provider>
	);

describe('Overview', () => {
	afterEach(cleanup);

	beforeEach(() => {
		mockRefetch.mockClear();

		mockedUseParams.mockReturnValue({
			channelId: '123',
			groupId: '456',
			id: 'acc-1',
		});

		mockUseRequest();
	});

	it('should render the account firmographics from the account', () => {
		renderOverview({account: mockAccount});

		expect(screen.getByText('IQVIA')).toBeInTheDocument();
		expect(screen.getByText('United States')).toBeInTheDocument();
		expect(screen.getByText('11.36B Revenue')).toBeInTheDocument();
		expect(screen.getByText('Business Services')).toBeInTheDocument();
		expect(screen.getByText('Lifecycle: Engaged')).toBeInTheDocument();
		expect(screen.getByText('Type: Prospect')).toBeInTheDocument();
	});

	it('should render no lifecycle label when the account has none', () => {
		renderOverview({account: {...mockAccount, lifecycleStage: null}});

		expect(screen.getByText('IQVIA')).toBeInTheDocument();
		expect(screen.queryByText(/Lifecycle/)).not.toBeInTheDocument();
	});

	it('should render no account type label when the account has none', () => {
		renderOverview({account: {...mockAccount, accountType: ''}});

		expect(screen.getByText('IQVIA')).toBeInTheDocument();
		expect(screen.queryByText(/Type:/)).not.toBeInTheDocument();
	});

	it('should render the card without an account', () => {
		const {container} = renderOverview();

		expect(screen.getByText('ACCOUNT INFO')).toBeInTheDocument();
		expect(container.querySelectorAll('.label')).toHaveLength(0);
	});

	it('should render the Top Pages card under the engagement section', () => {
		renderOverview({account: mockAccount});

		expect(screen.getByText('ENGAGEMENT SUMMARY')).toBeInTheDocument();
		expect(screen.getByText('TOP PAGES')).toBeInTheDocument();
	});

	it('should render the Top Assets card in a half-width column', () => {
		const {container} = renderOverview({account: mockAccount});

		expect(screen.getByText('TOP ASSETS')).toBeInTheDocument();
		expect(
			container.querySelector('.col-xl-6 .top-assets')
		).toBeInTheDocument();
	});

	it('should render the Top Asset Categories and Tags card beside the Top Assets card', () => {
		const {container} = renderOverview({account: mockAccount});

		expect(
			screen.getByText('TOP ASSET CATEGORIES AND TAGS')
		).toBeInTheDocument();
		expect(
			container.querySelectorAll(
				'.col-xl-6 .top-assets, .col-xl-6 .top-categories-and-tags'
			)
		).toHaveLength(2);
	});

	it('should stretch both engagement cards so they stay aligned', () => {
		const {container} = renderOverview({account: mockAccount});

		expect(
			container.querySelectorAll(
				'.col-xl-6.d-flex.flex-column > .flex-grow-1.top-assets, .col-xl-6.d-flex.flex-column > .flex-grow-1.top-categories-and-tags'
			)
		).toHaveLength(2);
	});

	describe('metrics cards', () => {
		it('should request the individual metrics for the account being viewed', () => {
			renderOverview({account: mockAccount});

			expect(mockedUseRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					dataSourceFn: API.accounts.fetchAccountIndividualMetrics,
					variables: {
						accountId: 'acc-1',
						channelId: '123',
						groupId: '456',
					},
				})
			);
		});

		it('should request the metrics once the account id is known', () => {
			renderOverview({account: mockAccount});

			expect(mockedUseRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					dataSourceFn: API.accounts.fetchAccountIndividualMetrics,
					skipRequest: false,
				})
			);
		});

		it('should skip the request while the account id is missing', () => {
			mockedUseParams.mockReturnValue({
				channelId: '123',
				groupId: '456',
			});

			renderOverview({account: mockAccount});

			expect(mockedUseRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					dataSourceFn: API.accounts.fetchAccountIndividualMetrics,
					skipRequest: true,
				})
			);
		});

		it('should render one card per metric group', () => {
			renderOverview({account: mockAccount});

			expect(screen.getByText('TOTAL INDIVIDUALS')).toBeInTheDocument();
			expect(screen.getByText('IDENTITY BREAKDOWN')).toBeInTheDocument();
			expect(screen.getByText('ENGAGEMENT STATUS')).toBeInTheDocument();
			expect(screen.getByText('INACTIVE USERS')).toBeInTheDocument();
		});

		it('should render the total individuals count', () => {
			renderOverview({account: mockAccount});

			expect(getMetricsCard('TOTAL INDIVIDUALS')).toHaveTextContent(
				'1.23K Individuals'
			);
		});

		it('should render the individuals label in the singular for one', () => {
			mockUseRequest({
				data: [
					{
						metricType:
							AccountIndividualMetricType.TotalIndividuals,
						value: 1,
					},
				],
			});

			renderOverview({account: mockAccount});

			const card = getMetricsCard('TOTAL INDIVIDUALS');

			expect(card).toHaveTextContent('1 Individual');
			expect(card).not.toHaveTextContent('1 Individuals');
		});

		it('should break the identity down into known and anonymous', () => {
			renderOverview({account: mockAccount});

			const card = getMetricsCard('IDENTITY BREAKDOWN');

			expect(card).toHaveTextContent('800 Known');
			expect(card).toHaveTextContent('434 Anonymous');
		});

		it('should break the engagement down into returning and first-time', () => {
			renderOverview({account: mockAccount});

			const card = getMetricsCard('ENGAGEMENT STATUS');

			expect(card).toHaveTextContent('512 Returning');
			expect(card).toHaveTextContent('288 First Time');
		});

		it('should render the inactive individuals count', () => {
			renderOverview({account: mockAccount});

			expect(getMetricsCard('INACTIVE USERS')).toHaveTextContent(
				'96 No Activity'
			);
		});

		it('should render zero for a metric missing from the response', () => {
			mockUseRequest({
				data: [
					{
						metricType:
							AccountIndividualMetricType.TotalIndividuals,
						value: 1234,
					},
				],
			});

			renderOverview({account: mockAccount});

			expect(getMetricsCard('TOTAL INDIVIDUALS')).toHaveTextContent(
				'1.23K Individuals'
			);
			expect(getMetricsCard('IDENTITY BREAKDOWN')).toHaveTextContent(
				'0 Known'
			);
		});

		it('should render zero when the metrics have not been fetched yet', () => {
			mockUseRequest({data: null, loading: false});

			renderOverview({account: mockAccount});

			expect(getMetricsCard('TOTAL INDIVIDUALS')).toHaveTextContent(
				'0 Individuals'
			);
		});

		it('should render every card in error when the metrics fail to load', () => {
			mockUseRequest({error: true});

			renderOverview({account: mockAccount});

			expect(
				screen.getAllByText('An unexpected error occurred.')
			).toHaveLength(4);
			expect(screen.queryByText('1.23K')).not.toBeInTheDocument();
		});

		it('should render every card as loading while the metrics load', () => {
			mockUseRequest({loading: true});

			renderOverview({account: mockAccount});

			[
				'TOTAL INDIVIDUALS',
				'IDENTITY BREAKDOWN',
				'ENGAGEMENT STATUS',
				'INACTIVE USERS',
			].forEach((title) => {
				expect(
					getMetricsCard(title).querySelector('.loading-root')
				).toBeInTheDocument();
			});

			expect(screen.queryByText('1.23K')).not.toBeInTheDocument();
		});

		it('should render the cards in a quarter-width column each', () => {
			const {container} = renderOverview({account: mockAccount});

			expect(
				container.querySelectorAll('.col-lg-3.col-md-6 .card-root')
			).toHaveLength(4);
		});

		// The columns must be flex containers for the cards' `flex-fill` to
		// apply, otherwise the cards stay content-height and a taller one
		// leaves its neighbours short.

		it('should stretch every card so they stay aligned', () => {
			const {container} = renderOverview({account: mockAccount});

			expect(
				container.querySelectorAll(
					'.col-lg-3.col-md-6.d-flex > .flex-fill.card-root'
				)
			).toHaveLength(4);
		});

		it('should render the cards between the account info and the engagement summary', () => {
			const {container} = renderOverview({account: mockAccount});

			const cards = container.querySelector('.col-lg-3.col-md-6')!;
			const engagementSummary = screen
				.getByText('ENGAGEMENT SUMMARY')
				.closest('div')!;

			expect(
				cards.compareDocumentPosition(engagementSummary) &
					Node.DOCUMENT_POSITION_FOLLOWING
			).toBeTruthy();
		});
	});
});
