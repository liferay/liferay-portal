import * as API from 'shared/api';
import BaseLifecycle from '../BaseLifecycle';
import mockStore from 'test/mock-store';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {createMemoryHistory} from 'history';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {Router} from 'react-router-dom';
import {Routes, toRoute} from 'shared/util/router';
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

jest.mock('lifecycle/components/FilterPicker', () => ({
	__esModule: true,
	default: ({
		entityLabel,
		filterKey,
	}: {
		entityLabel: string;
		filterKey: string;
	}) => (
		<div
			data-entity-label={entityLabel}
			data-testid={`filter-${filterKey}`}
		/>
	),
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
		processedDate = 1700000000000,
		totalCount = 1,
	}: {
		lifecycles?: {
			id: string;
			name?: string;
			processedDate?: number | null;
		}[];
		metricsLoading?: boolean;
		processedDate?: number | null;
		totalCount?: number;
	} = {}) =>
	({variables}: {variables?: {[key: string]: any}} = {}) =>
		variables?.channelId !== undefined
			? {
					data: buildAccountMetrics(totalCount),
					error: false,
					loading: metricsLoading,
				}
			: {
					data: lifecycles.map((lifecycle) => ({
						processedDate,
						...lifecycle,
					})),
					error: false,
					loading: false,
				};

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

	it('titles the page with the lifecycle name', () => {
		mockedUseRequest.mockImplementation(
			useRequestImpl({
				lifecycles: [{id: '1', name: "Tiago's Lifecycle"}],
				totalCount: 5,
			})
		);

		renderPage();

		expect(screen.getByText("Tiago's Lifecycle")).toBeInTheDocument();
		expect(screen.queryByText('Lifecycles')).toBeNull();
	});

	it('titles the page generically when no lifecycle exists', () => {
		mockedUseRequest.mockImplementation(
			useRequestImpl({lifecycles: [], totalCount: 5})
		);

		renderPage();

		expect(screen.getByText('Lifecycles')).toBeInTheDocument();
	});

	it('titles the page generically when the lifecycle is unnamed', () => {
		mockedUseRequest.mockImplementation(
			useRequestImpl({lifecycles: [{id: '1'}], totalCount: 5})
		);

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
			expect(screen.queryByTestId('filter-industryFilter')).toBeNull();
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
			expect(screen.queryByTestId('filter-industryFilter')).toBeNull();
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

	describe('when no lifecycles are configured', () => {
		beforeEach(() => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({lifecycles: []})
			);
		});

		it('renders the "Configure a New Lifecycle" empty state', () => {
			renderPage();

			expect(
				screen.getByText('Configure a New Lifecycle')
			).toBeInTheDocument();
			expect(
				screen.getByText(
					'Complete the configuration to start seeing insights.'
				)
			).toBeInTheDocument();
			expect(screen.queryByTestId('overview-section')).toBeNull();
			expect(screen.queryByTestId('filter-industryFilter')).toBeNull();
		});

		it('renders the New Lifecycle action linking to the create route', () => {
			renderPage();

			expect(
				screen.getByRole('link', {name: 'New Lifecycle'})
			).toHaveAttribute(
				'href',
				toRoute(Routes.LIFECYCLE_CREATE, {
					channelId: '123',
					groupId: '23',
				})
			);
		});
	});

	describe('when the lifecycle is still processing', () => {
		beforeEach(() => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({processedDate: null})
			);
		});

		it('renders the "almost ready" processing state', () => {
			renderPage();

			expect(
				screen.getByText('Your dashboard is almost ready!')
			).toBeInTheDocument();
			expect(screen.queryByTestId('overview-section')).toBeNull();
			expect(screen.queryByTestId('filter-industryFilter')).toBeNull();
		});
	});

	describe('the Lifecycle Configuration action', () => {
		it('navigates to the edit route when clicked by an admin', () => {
			const history = createMemoryHistory({
				initialEntries: ['/workspace/23/123/lifecycles'],
			});

			renderPage(history);

			fireEvent.click(
				screen.getByRole('button', {name: 'Lifecycle Configuration'})
			);

			expect(history.location.pathname).toBe(
				toRoute(Routes.LIFECYCLE_EDIT, {
					channelId: '123',
					groupId: '23',
					lifecycleId: '1',
				})
			);
		});

		it('is hidden for non-admins', () => {
			mockedUseCurrentUser.mockReturnValue({isAdmin: () => false});

			renderPage();

			expect(
				screen.queryByRole('button', {name: 'Lifecycle Configuration'})
			).toBeNull();
		});
	});

	it('renders the lifecycle content when account data is available', () => {
		renderPage();

		expect(screen.getByTestId('overview-section')).toBeInTheDocument();
		expect(screen.getByTestId('lifecycle-chart')).toBeInTheDocument();
		expect(screen.getByTestId('accounts-dataset')).toBeInTheDocument();
		expect(screen.getByTestId('filter-industryFilter')).toHaveAttribute(
			'data-entity-label',
			'Industries'
		);
		expect(screen.getByTestId('filter-countryFilter')).toHaveAttribute(
			'data-entity-label',
			'Countries'
		);
		expect(screen.queryByText('No Account Data Available')).toBeNull();
	});
});
