import * as API from 'shared/api';
import BaseLifecycle from '../BaseLifecycle';
import mockStore from 'test/mock-store';
import ModalRenderer from 'shared/components/ModalRenderer';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {ChannelContext} from 'shared/context/channel';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	within,
} from '@testing-library/react';
import {MemoryRouter, useLocation} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useRequest} from 'shared/hooks/useRequest';
import {setBodyWidth} from 'test/body-width';

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
			defaultAccountLifecycle?: boolean;
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
			data: lifecycle && {
				externalReferenceCode: `erc-${lifecycle.id}`,
				processedDate,
				...lifecycle,
			},
			error: lifecycleError,
			loading: false,
		};
	};

const LocationProbe = () => (
	<div data-testid="location">{useLocation().pathname}</div>
);

const renderPage = (initialEntries = ['/workspace/23/123/lifecycle/erc-1']) =>
	render(
		<Provider store={mockStore()}>
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
			fetchLifecyclesPage: jest.fn(),
			setDefaultLifecycle: jest.fn(() => Promise.resolve()),
		};

		mockUseParams.mockReturnValue({
			channelId: '123',
			groupId: '23',
			lifecycleId: 'erc-1',
		});

		setBodyWidth(1440);

		mockedUseCurrentUser.mockReturnValue({isAdmin: () => true});
		mockedUseDataSources.mockReturnValue({empty: false, loading: false});
		mockedUseRequest.mockImplementation(useRequestImpl({totalCount: 5}));
	});

	afterEach(cleanup);

	it('resets the filters and loads the current version when switching lifecycles', () => {
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
			lifecycleId: 'erc-2',
		});

		mockedUseRequest.mockImplementation(
			useRequestImpl({lifecycle: {id: '2'}, totalCount: 5})
		);

		rerender(
			<Provider store={mockStore()}>
				<ChannelContext.Provider value={mockChannelContext() as any}>
					<MemoryRouter
						initialEntries={['/workspace/23/123/lifecycle/erc-2']}
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
			lifecycleId: 'erc-2',
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

	it('shows no generic title before the lifecycle name is known', () => {
		renderPage();

		expect(
			screen.queryByRole('heading', {name: /^lifecycles$/i})
		).toBeNull();
	});

	it('shows the lifecycle in the breadcrumb after the lifecycles', () => {
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

			expect(
				screen.getByText(/lifecycle not found/i)
			).toBeInTheDocument();
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

	it('renders only a loading state, without the header, until the lifecycle arrives', () => {
		const dashboardRequest = useRequestImpl();

		mockedUseRequest.mockImplementation(
			(options: {dataSourceFn?: unknown}) =>
				options.dataSourceFn === API.lifecycle.fetchLifecycle
					? {data: undefined, error: false, loading: true}
					: dashboardRequest(options)
		);

		const {container} = renderPage();

		expect(container.querySelector('.breadcrumb')).toBeNull();
		expect(
			screen.queryByRole('button', {name: /switch.dashboard/i})
		).toBeNull();
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

	describe('the Edit action', () => {
		it('navigates to the edit route when clicked by an admin', () => {
			renderPage();

			openActionsMenu();

			fireEvent.click(screen.getByRole('menuitem', {name: /^edit$/i}));

			expect(screen.getByTestId('location')).toHaveTextContent(
				toRoute(Routes.LIFECYCLE_EDIT, {
					channelId: '123',
					groupId: '23',
					lifecycleId: 'erc-1',
				})
			);
		});

		it('is hidden for non-admins', () => {
			mockedUseCurrentUser.mockReturnValue({isAdmin: () => false});

			renderPage();

			openActionsMenu();

			expect(
				screen.queryByRole('menuitem', {name: /^edit$/i})
			).toBeNull();
		});
	});

	describe('the default lifecycle', () => {
		it('pins the title of the default lifecycle', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecycle: {defaultAccountLifecycle: true, id: '1'},
				})
			);

			renderPage();

			expect(
				screen.getByRole('img', {name: /default/i})
			).toBeInTheDocument();
		});

		it('shows no sticker beside another lifecycle', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecycle: {defaultAccountLifecycle: false, id: '1'},
				})
			);

			const {container} = renderPage();

			expect(screen.queryByRole('img', {name: /default/i})).toBeNull();
			expect(
				container.querySelector('.title-section .sticker')
			).toBeNull();
		});

		it('hides Set Default on the default lifecycle', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecycle: {defaultAccountLifecycle: true, id: '1'},
				})
			);

			renderPage();

			openActionsMenu();

			expect(
				screen.queryByRole('menuitem', {name: /set.default/i})
			).toBeNull();
		});

		it('pins the title once the lifecycle is set as default', async () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecycle: {defaultAccountLifecycle: false, id: '1'},
				})
			);

			renderPage();

			openActionsMenu();

			fireEvent.click(
				screen.getByRole('menuitem', {name: /set.default/i})
			);

			expect(API.lifecycle.setDefaultLifecycle).toHaveBeenCalledWith({
				groupId: '23',
				lifecycleId: '1',
			});

			expect(
				await screen.findByRole('img', {name: /default/i})
			).toBeInTheDocument();
		});
	});

	describe('the Show Details action', () => {
		it('opens the details of the open lifecycle in the side panel', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecycle: {id: '1', name: 'Growth Hub'},
					totalCount: 5,
				})
			);

			renderPage();

			openActionsMenu();

			fireEvent.click(
				screen.getByRole('menuitem', {name: /show.details/i})
			);

			expect(
				screen.getByRole('tab', {name: /^details$/i})
			).toBeInTheDocument();
			expect(screen.queryByRole('button', {name: /^back$/i})).toBeNull();
			expect(
				screen.getByRole('button', {name: /switch dashboard/i})
			).toHaveAttribute('aria-expanded', 'true');
		});
	});

	it('does not offer the View action on the open dashboard', () => {
		renderPage();

		openActionsMenu();

		expect(screen.queryByRole('menuitem', {name: /^view$/i})).toBeNull();
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

	describe('the Switch Dashboard button', () => {
		const getSwitchButton = () =>
			screen.getByRole('button', {name: /switch dashboard/i});

		it('starts with the panel closed', () => {
			renderPage();

			expect(getSwitchButton()).toHaveAttribute('aria-expanded', 'false');
		});

		it('opens and closes the switch panel', () => {
			renderPage();

			fireEvent.click(getSwitchButton());

			expect(getSwitchButton()).toHaveAttribute('aria-expanded', 'true');
			expect(getSwitchButton()).toHaveClass(
				'active',
				'btn-outline-borderless'
			);
			expect(
				screen
					.getByText(/switch lifecycle dashboard/i)
					.closest('.sidebar')
			).not.toHaveAttribute('inert');

			fireEvent.click(getSwitchButton());

			expect(getSwitchButton()).toHaveAttribute('aria-expanded', 'false');
		});

		it('closes the panel from its close button', () => {
			renderPage();

			fireEvent.click(getSwitchButton());

			const panel = screen
				.getByText(/switch lifecycle dashboard/i)
				.closest('.sidebar') as HTMLElement;

			fireEvent.click(
				within(panel).getByRole('button', {name: /^close$/i})
			);

			expect(getSwitchButton()).toHaveAttribute('aria-expanded', 'false');
		});
	});

	it('lays the dashboard out at full width', () => {
		const {container} = renderPage();

		expect(screen.getByTestId('overview-section')).toBeInTheDocument();
		expect(container.querySelector('.page-container')).toBeNull();
		expect(container.querySelector('.header-container')).toBeNull();
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

	describe('when the panel is open beside the dashboard', () => {
		const LIST_LIFECYCLES = [
			{id: '1', name: 'Growth Hub'},
			{id: '2', name: 'Partner Lifecycle'},
		];

		beforeEach(() => {
			const dashboardRequest = useRequestImpl({
				lifecycle: {
					defaultAccountLifecycle: false,
					id: '1',
					name: 'Growth Hub',
				},
			});

			mockedUseRequest.mockImplementation(
				(options: {
					dataSourceFn?: unknown;
					variables?: {defaultChangeCount?: number};
				}) => {
					if (
						options.dataSourceFn !==
						API.lifecycle.fetchLifecyclesPage
					) {
						return dashboardRequest(options);
					}

					const defaultLifecycleId = options.variables
						?.defaultChangeCount
						? '1'
						: '2';

					return {
						data: {
							items: LIST_LIFECYCLES.map((lifecycle) => ({
								...lifecycle,
								defaultAccountLifecycle:
									lifecycle.id === defaultLifecycleId,
								externalReferenceCode: `erc-${lifecycle.id}`,
							})),
							lastPage: 1,
							page: 1,
							pageSize: 20,
							totalCount: LIST_LIFECYCLES.length,
						},
						error: false,
						loading: false,
						refetch: jest.fn(),
					};
				}
			);
		});

		const getRow = (name: string) =>
			within(
				screen
					.getByText(/switch lifecycle dashboard/i)
					.closest('.sidebar') as HTMLElement
			)
				.getByRole('link', {name})
				.closest('li') as HTMLElement;

		it('moves the pin in the list when the header sets the default', async () => {
			renderPage();

			fireEvent.click(
				screen.getByRole('button', {name: /switch dashboard/i})
			);

			expect(
				within(getRow('Partner Lifecycle')).getByRole('img', {
					name: /default/i,
				})
			).toBeInTheDocument();

			openActionsMenu();

			fireEvent.click(
				screen.getByRole('menuitem', {name: /set.default/i})
			);

			expect(
				await within(getRow('Growth Hub')).findByRole('img', {
					name: /default/i,
				})
			).toBeInTheDocument();
			expect(
				within(getRow('Partner Lifecycle')).queryByRole('img', {
					name: /default/i,
				})
			).toBeNull();

			fireEvent.click(
				within(getRow('Growth Hub')).getByRole('button', {
					name: /actions for/i,
				})
			);

			expect(
				screen.queryByRole('menuitem', {name: /set.default/i})
			).toBeNull();
		});
	});
});
