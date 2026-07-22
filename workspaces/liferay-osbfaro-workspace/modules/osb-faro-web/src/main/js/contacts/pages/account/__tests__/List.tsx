import List from '../List';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render, screen} from '@testing-library/react';
import {mockChannelContext} from 'test/mock-channel-context';
import {MemoryRouter, useNavigate} from 'react-router-dom';
import {Provider} from 'react-redux';
import {useRequest} from 'shared/hooks/useRequest';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

type FakeFilter = {
	id: string;
	preloadedData?: {
		exclude: boolean;
		selectedItems: Array<{label?: string; value: string}>;
	};
};

let lastFilters: FakeFilter[] | undefined;

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: ({filters, id}: {filters: FakeFilter[]; id: string}) => {
		lastFilters = filters;

		return <div data-testid="fds-component" id={id} />;
	},
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('shared/util/breadcrumbs', () => ({
	getHome: jest.fn(({label}: {label?: string} = {}) => ({
		active: false,
		label: label || 'Home',
	})),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useNavigate: jest.fn(),
	useParams: () => ({
		channelId: '123',
		groupId: '23',
	}),
}));

const mockedUseNavigate = useNavigate as jest.Mock;
const mockedUseRequest = useRequest as jest.Mock;

const mockHistoryPush = jest.fn();

const buildInitialEntries = (path = '/workspace/23/123/accounts') => [path];

const store = mockStore();

// `useRequest` is consumed by `List` twice (fetchChannels, expects an object
// with `total`; fetchCatalogFields, expects `items`) and by `TotalAccounts`
// (account metrics, expects an array of `IAccountMetric`). Differentiate by
// `variables.channelIds` and `variables.tableName`, each unique to one call.

const accountMetricsMock = [
	{
		metricType: 'totalCount',
		trend: {percentage: 0, trendClassification: 'NEUTRAL'},
		value: 0,
	},
	{
		metricType: 'newCount',
		trend: {percentage: 0, trendClassification: 'NEUTRAL'},
		value: 0,
	},
	{
		metricType: 'activeCount',
		trend: {percentage: 0, trendClassification: 'NEUTRAL'},
		value: 0,
	},
];

const catalogFieldsMock = [
	{
		dataCategory: 'Text',
		dataType: 'STRING',
		description: null,
		displayName: 'Account Type',
		id: '1',
		name: 'accountType',
		parentField: null,
		tableName: 'account',
	},
];

const useRequestImpl =
	({
		catalogError = false,
		catalogLoading = false,
		total = 1,
	}: {
		catalogError?: boolean;
		catalogLoading?: boolean;
		total?: number;
	} = {}) =>
	({variables}: {variables: {[key: string]: any}}) => {
		if (variables?.channelIds !== undefined) {
			return {data: {total}};
		}

		if (variables?.tableName !== undefined) {
			if (catalogLoading) {
				return {loading: true};
			}

			return catalogError
				? {error: true}
				: {data: {items: catalogFieldsMock}};
		}

		return {data: accountMetricsMock};
	};

const renderList = ({queryString = ''}: {queryString?: string} = {}) =>
	render(
		<Provider store={store}>
			<ChannelContext.Provider value={mockChannelContext() as any}>
				<MemoryRouter
					initialEntries={buildInitialEntries(
						`/workspace/23/123/accounts${queryString}`
					)}
				>
					<List channelId="123" groupId="23" />
				</MemoryRouter>
			</ChannelContext.Provider>
		</Provider>
	);

describe('List', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		lastFilters = undefined;

		mockedUseNavigate.mockReturnValue(mockHistoryPush);
		mockedUseRequest.mockImplementation(useRequestImpl());
	});

	afterEach(cleanup);

	describe('rendering', () => {
		it('should render without crashing', () => {
			const {container} = renderList();

			expect(container).toBeInTheDocument();
		});

		it('should render the page title "Accounts"', () => {
			renderList();

			expect(screen.getByText('Accounts')).toBeInTheDocument();
		});

		it('should render the empty state when there are no data sources connected', () => {
			mockedUseRequest.mockImplementation(useRequestImpl({total: 0}));

			renderList();
		});

		it('should render the FrontendDataSet component', () => {
			renderList();

			expect(screen.getByTestId('fds-component')).toBeInTheDocument();
		});

		it('should withhold the data set until the field catalog resolves, without blanking the page', async () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({catalogLoading: true})
			);

			renderList();

			expect(
				await screen.findByRole('heading', {name: 'Accounts'})
			).toBeInTheDocument();
			expect(screen.queryByTestId('fds-component')).toBeNull();
		});

		it('should still render the data set when the field catalog request fails', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({catalogError: true})
			);

			renderList();

			expect(screen.getByTestId('fds-component')).toBeInTheDocument();
		});

		it('should render the FrontendDataSet with id "accounts-list-dataset"', () => {
			renderList();

			expect(screen.getByTestId('fds-component')).toHaveAttribute('id');
		});

		it('should preload the rangeKey filter with Last 30 Days by default', () => {
			renderList();

			const rangeKeyFilter = lastFilters?.find(
				(f) => f.id === 'rangeKey'
			);

			expect(rangeKeyFilter?.preloadedData).toEqual({
				exclude: false,
				selectedItems: [{label: 'Last 30 days', value: '30'}],
			});
		});

		it('should match the snapshot', async () => {
			const {container} = renderList();

			await waitForLoadingToBeRemoved();

			expect(container).toMatchSnapshot();
		});
	});
});
