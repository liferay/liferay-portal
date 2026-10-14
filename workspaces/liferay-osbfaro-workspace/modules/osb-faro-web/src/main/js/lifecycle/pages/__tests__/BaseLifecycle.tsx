import * as API from 'shared/api';
import BaseLifecycle from '../BaseLifecycle';
import mockStore from 'test/mock-store';
import ModalRenderer from 'shared/components/ModalRenderer';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter, useLocation} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
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
	...jest.requireActual('shared/util/breadcrumbs'),
	getHome: jest.fn(({label}: {label?: string} = {}) => ({
		active: false,
		label: label || 'Home',
	})),
}));

const mockUseParams = jest.fn();

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => mockUseParams(),
}));

jest.mock('lifecycle/components/FilterPicker', () => {
	const {useLifecycle} = jest.requireActual(
		'lifecycle/context/LifecycleContext'
	);

	const FilterPicker = ({
		entityLabel,
		filterKey,
	}: {
		entityLabel: string;
		filterKey: 'countryFilter' | 'industryFilter';
	}) => {
		const {filters, updateFilters} = useLifecycle();

		return (
			<button
				data-entity-label={entityLabel}
				data-testid={`filter-${filterKey}`}
				data-value={filters[filterKey]}
				onClick={() => updateFilters({[filterKey]: 'picked'})}
				type="button"
			/>
		);
	};

	return {__esModule: true, default: FilterPicker};
});

jest.mock('lifecycle/components/OverviewSection', () => ({
	__esModule: true,
	default: () => <div data-testid="overview-section" />,
}));

jest.mock('lifecycle/components/LifecycleChart', () => {
	const {useLifecycle} = jest.requireActual(
		'lifecycle/context/LifecycleContext'
	);

	const LifecycleChart = () => {
		const {filters, selectStage} = useLifecycle();

		return (
			<button
				data-stage={filters.lifecycleStageFilter ?? ''}
				data-testid="lifecycle-chart"
				onClick={() => selectStage('ENGAGED')}
				type="button"
			/>
		);
	};

	return {__esModule: true, default: LifecycleChart};
});

jest.mock('shared/components/accounts-data-set/AccountsDataSet', () => ({
	__esModule: true,
	default: () => <div data-testid="accounts-dataset" />,
}));

jest.mock('shared/components/SegmentDropdown', () => ({
	__esModule: true,
	default: ({
		initialSegmentId,
		initialSegmentName,
	}: {
		initialSegmentId?: string;
		initialSegmentName?: string;
	}) => (
		<div
			data-initial-segment-id={initialSegmentId}
			data-initial-segment-name={initialSegmentName}
			data-testid="segment-dropdown"
		/>
	),
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
		lifecycle = {id: '1'},
		lifecycleError = false,
		metricsLoading = false,
		processedDate = 1700000000000,
		totalCount = 1,
	}: {
		lifecycle?: {
			id: string;
			name?: string;
			processedDate?: number | null;
		} | null;
		lifecycleError?: boolean;
		metricsLoading?: boolean;
		processedDate?: number | null;
		totalCount?: number;
	} = {}) =>
	({
		dataSourceFn,
		skipRequest,
	}: {
		dataSourceFn?: unknown;
		skipRequest?: boolean;
	} = {}) => {
		if (skipRequest) {
			return {data: null, error: false, loading: true};
		}

		if (dataSourceFn === API.accounts.fetchMetrics) {
			return {
				data: buildAccountMetrics(totalCount),
				error: false,
				loading: metricsLoading,
			};
		}

		return {
			data: lifecycle && {processedDate, ...lifecycle},
			error: lifecycleError,
			loading: false,
		};
	};

const store = mockStore();

const LocationProbe = () => (
	<div data-testid="location">{useLocation().pathname}</div>
);

const renderPage = (initialEntries = ['/workspace/23/123/lifecycle/1']) =>
	render(
		<Provider store={store}>
			<ModalRenderer />

			<ChannelContext.Provider value={mockChannelContext() as any}>
				<MemoryRouter initialEntries={initialEntries}>
					<BaseLifecycle />

					<LocationProbe />
				</MemoryRouter>
			</ChannelContext.Provider>
		</Provider>
	);

describe('BaseLifecycle', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(API as any).accounts = {fetchMetrics: jest.fn()};
		(API as any).lifecycle = {
			fetchLifecycle: jest.fn(),
		};

		mockUseParams.mockReturnValue({
			channelId: '123',
			groupId: '23',
			lifecycleId: '1',
		});

		mockedUseCurrentUser.mockReturnValue({isAdmin: () => true});
		mockedUseDataSources.mockReturnValue({empty: false, loading: false});
		mockedUseRequest.mockImplementation(useRequestImpl({totalCount: 5}));
	});

	afterEach(cleanup);

	it('resets the filters and reloads the data when switching lifecycles', () => {
		const {rerender} = renderPage();

		fireEvent.click(screen.getByTestId('filter-industryFilter'));
		fireEvent.click(screen.getByTestId('filter-countryFilter'));
		fireEvent.click(screen.getByTestId('lifecycle-chart'));

		expect(screen.getByTestId('filter-industryFilter')).toHaveAttribute(
			'data-value',
			'picked'
		);
		expect(screen.getByTestId('filter-countryFilter')).toHaveAttribute(
			'data-value',
			'picked'
		);
		expect(screen.getByTestId('lifecycle-chart')).toHaveAttribute(
			'data-stage',
			'ENGAGED'
		);

		mockUseParams.mockReturnValue({
			channelId: '123',
			groupId: '23',
			lifecycleId: '2',
		});

		rerender(
			<Provider store={mockStore()}>
				<ChannelContext.Provider value={mockChannelContext() as any}>
					<MemoryRouter
						initialEntries={['/workspace/23/123/lifecycle/2']}
					>
						<BaseLifecycle />
					</MemoryRouter>
				</ChannelContext.Provider>
			</Provider>
		);

		expect(screen.getByTestId('filter-industryFilter')).toHaveAttribute(
			'data-value',
			''
		);
		expect(screen.getByTestId('filter-countryFilter')).toHaveAttribute(
			'data-value',
			''
		);
		expect(screen.getByTestId('lifecycle-chart')).toHaveAttribute(
			'data-stage',
			''
		);

		const requestVariables = mockedUseRequest.mock.calls.map(
			([{variables}]) => variables
		);

		expect(requestVariables).toContainEqual({
			groupId: '23',
			lifecycleId: '2',
		});
		expect(requestVariables).toContainEqual(
			expect.objectContaining({
				country: '',
				industry: '',
				lifecycleId: '2',
			})
		);
	});

	it('titles the page with the lifecycle name', () => {
		mockedUseRequest.mockImplementation(
			useRequestImpl({
				lifecycle: {id: '1', name: "Tiago's Lifecycle"},
				totalCount: 5,
			})
		);

		renderPage();

		expect(
			screen.getByRole('heading', {name: "Tiago's Lifecycle"})
		).toBeInTheDocument();
	});

	it('titles the page generically when the lifecycle is unnamed', () => {
		renderPage();

		expect(
			screen.getByRole('heading', {name: /^lifecycles$/i})
		).toBeInTheDocument();
	});

	it('shows the lifecycle in the breadcrumb under a link to the lifecycles', () => {
		mockedUseRequest.mockImplementation(
			useRequestImpl({
				lifecycle: {id: '1', name: 'Growth Hub'},
				totalCount: 5,
			})
		);

		renderPage();

		expect(
			screen.getByRole('link', {name: /^lifecycles$/i})
		).toHaveAttribute(
			'href',
			toRoute(Routes.LIFECYCLE, {channelId: '123', groupId: '23'})
		);
		expect(screen.getAllByText('Growth Hub').length).toBeGreaterThan(1);
	});

	describe('when the lifecycle does not exist', () => {
		beforeEach(() => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({lifecycle: null, lifecycleError: true})
			);
		});

		it('renders the not found page', () => {
			renderPage();

			expect(screen.getByText(/lifecycle not found/i)).toBeInTheDocument();
			expect(screen.queryByTestId('overview-section')).toBeNull();
		});

		it('links back to the lifecycles', () => {
			renderPage();

			expect(
				screen.getByRole('link', {name: /go to lifecycles/i})
			).toHaveAttribute(
				'href',
				toRoute(Routes.LIFECYCLE, {channelId: '123', groupId: '23'})
			);
		});
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

	const openActionsMenu = () =>
		fireEvent.click(screen.getByRole('button', {name: 'Menu'}));

	describe('the Lifecycle Configuration action', () => {
		it('navigates to the edit route when clicked by an admin', () => {
			renderPage();

			openActionsMenu();

			fireEvent.click(
				screen.getByRole('menuitem', {name: 'Lifecycle Configuration'})
			);

			expect(screen.getByTestId('location')).toHaveTextContent(
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

			openActionsMenu();

			expect(screen.queryByText('Lifecycle Configuration')).toBeNull();
		});
	});

	describe('the Manage Notifications action', () => {
		it('opens the manage notifications modal when clicked', () => {
			renderPage();

			openActionsMenu();

			fireEvent.click(
				screen.getByRole('menuitem', {name: 'Manage Notifications'})
			);

			expect(
				screen.getByText('Manage Lifecycle Notifications')
			).toBeInTheDocument();
		});

		it('is available for non-admins', () => {
			mockedUseCurrentUser.mockReturnValue({isAdmin: () => false});

			renderPage();

			openActionsMenu();

			expect(
				screen.getByRole('menuitem', {name: 'Manage Notifications'})
			).toBeInTheDocument();
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
		expect(screen.getByTestId('segment-dropdown')).toBeInTheDocument();
		expect(
			screen.getByText(/^\S+ \d+, \d{4} – \S+ \d+, \d{4}$/)
		).toBeInTheDocument();
		expect(screen.queryByText('No Account Data Available')).toBeNull();
	});
});
