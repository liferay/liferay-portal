import * as API from 'shared/api';
import BaseLifecycle from '../BaseLifecycle';
import mockStore from 'test/mock-store';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render, screen} from '@testing-library/react';
import {createMemoryHistory} from 'history';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {Router} from 'react-router-dom';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('shared/context/dataSources', () => ({
	useDataSources: jest.fn(),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn(),
}));

jest.mock('shared/util/breadcrumbs', () => ({
	getHome: jest.fn(({label}: {label?: string} = {}) => ({
		active: false,
		label: label || 'Home',
	})),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '23',
	}),
}));

jest.mock('lifecycle/components/GlobalFilters', () => ({
	__esModule: true,
	default: () => <div data-testid="global-filters" />,
}));

jest.mock('lifecycle/components/OverviewSection', () => ({
	__esModule: true,
	default: () => <div data-testid="overview-section" />,
}));

jest.mock('lifecycle/components/LifecycleChart', () => ({
	__esModule: true,
	default: () => <div data-testid="lifecycle-chart" />,
}));

jest.mock('shared/components/AccountsDataSet', () => ({
	__esModule: true,
	default: () => <div data-testid="accounts-dataset" />,
}));

const mockedUseCurrentUser = useCurrentUser as jest.Mock;
const mockedUseDataSources = useDataSources as jest.Mock;
const mockedUseRequest = useRequest as jest.Mock;

const buildAccountMetrics = (totalCount: number) =>
	['totalCount', 'newCount', 'activeCount'].map((metricType) => ({
		metricType,
		trend: {percentage: 0, trendClassification: 'NEUTRAL'},
		value: metricType === 'totalCount' ? totalCount : 0,
	}));

const useRequestImpl =
	({
		lifecycles = [{id: '1'}],
		metricsLoading = false,
		totalCount = 1,
	}: {
		lifecycles?: {id: string}[];
		metricsLoading?: boolean;
		totalCount?: number;
	} = {}) =>
	({variables}: {variables?: {[key: string]: any}} = {}) =>
		variables?.channelId !== undefined
			? {
					data: buildAccountMetrics(totalCount),
					error: false,
					loading: metricsLoading,
				}
			: {data: lifecycles, error: false, loading: false};

const store = mockStore();

const renderPage = (
	history = createMemoryHistory({
		initialEntries: ['/workspace/23/123/lifecycles'],
	})
) =>
	render(
		<Provider store={store}>
			<ChannelContext.Provider value={mockChannelContext() as any}>
				<Router history={history}>
					<BaseLifecycle />
				</Router>
			</ChannelContext.Provider>
		</Provider>
	);

describe('BaseLifecycle', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(API as any).accounts = {fetchMetrics: jest.fn()};
		(API as any).lifecycle = {fetchLifecycles: jest.fn()};

		mockedUseCurrentUser.mockReturnValue({isAdmin: () => true});
		mockedUseDataSources.mockReturnValue({empty: false, loading: false});
		mockedUseRequest.mockImplementation(useRequestImpl({totalCount: 5}));
	});

	afterEach(cleanup);

	it('renders the page title', () => {
		renderPage();

		expect(screen.getByText('Lifecycles')).toBeInTheDocument();
	});

	it('renders neither an empty state nor content while loading', () => {
		mockedUseDataSources.mockReturnValue({empty: false, loading: true});

		renderPage();

		expect(screen.queryByText('No Data Sources Connected')).toBeNull();
		expect(screen.queryByText('No Account Data Available')).toBeNull();
		expect(screen.queryByTestId('overview-section')).toBeNull();
	});

	it('renders loading while account metrics are still loading', () => {
		mockedUseRequest.mockImplementation(
			useRequestImpl({metricsLoading: true})
		);

		renderPage();

		expect(screen.queryByText('No Account Data Available')).toBeNull();
		expect(screen.queryByTestId('overview-section')).toBeNull();
	});

	describe('when no data sources are connected', () => {
		beforeEach(() => {
			mockedUseDataSources.mockReturnValue({empty: true, loading: false});
		});

		it('renders the "No Data Sources Connected" empty state', () => {
			renderPage();

			expect(
				screen.getByText('No Data Sources Connected')
			).toBeInTheDocument();
			expect(
				screen.getByText(
					'Connect a data source to sync lifecycle stages.'
				)
			).toBeInTheDocument();
			expect(screen.queryByTestId('overview-section')).toBeNull();
			expect(screen.queryByTestId('global-filters')).toBeNull();
		});

		it('renders the connect action for admins', () => {
			renderPage();

			expect(screen.getByText('Connect Data Source')).toBeInTheDocument();
		});

		it('renders the learn-more documentation link', () => {
			renderPage();

			expect(
				screen.getByRole('link', {
					name: /learn more about data sources/i,
				})
			).toHaveAttribute('href', URLConstants.DataSourceConnection);
		});

		it('hides the connect action for non-admins', () => {
			mockedUseCurrentUser.mockReturnValue({isAdmin: () => false});

			renderPage();

			expect(screen.queryByText('Connect Data Source')).toBeNull();
			expect(
				screen.getByText(
					'Please contact your workspace administrator to add data sources.'
				)
			).toBeInTheDocument();
		});
	});

	describe('when data sources have no account data', () => {
		beforeEach(() => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({totalCount: 0})
			);
		});

		it('renders the "No Account Data Available" empty state', () => {
			renderPage();

			expect(
				screen.getByText('No Account Data Available')
			).toBeInTheDocument();
			expect(
				screen.getByText(
					'Connect a data source containing account data.'
				)
			).toBeInTheDocument();
			expect(screen.queryByTestId('overview-section')).toBeNull();
			expect(screen.queryByTestId('global-filters')).toBeNull();
		});

		it('renders the contact-administrator message for non-admins', () => {
			mockedUseCurrentUser.mockReturnValue({isAdmin: () => false});

			renderPage();

			expect(
				screen.getByText(
					'Contact an administrator to connect a data source containing account data.'
				)
			).toBeInTheDocument();
			expect(screen.queryByText('Connect Data Source')).toBeNull();
		});
	});

	it('renders the lifecycle content when account data is available', () => {
		renderPage();

		expect(screen.getByTestId('overview-section')).toBeInTheDocument();
		expect(screen.getByTestId('lifecycle-chart')).toBeInTheDocument();
		expect(screen.getByTestId('accounts-dataset')).toBeInTheDocument();
		expect(screen.getByTestId('global-filters')).toBeInTheDocument();
		expect(screen.queryByText('No Account Data Available')).toBeNull();
	});

	it('renders the empty state and hides the filters when no lifecycle exists', () => {
		mockedUseRequest.mockImplementation(
			useRequestImpl({lifecycles: [], totalCount: 5})
		);

		renderPage();

		expect(
			screen.getByText('No Account Data Available')
		).toBeInTheDocument();
		expect(screen.queryByTestId('overview-section')).toBeNull();
		expect(screen.queryByTestId('global-filters')).toBeNull();
	});
});
