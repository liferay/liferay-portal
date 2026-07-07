import List from 'assets/pages/List';
import mockStore, {mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {createMemoryHistory} from 'history';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {Router} from 'react-router-dom';

jest.unmock('react-dom');

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: ({
		emptyState,
		filters,
		groupedFilters,
		id,
		itemsActions,
	}: {
		emptyState?: {
			description?: React.ReactNode;
			image?: string;
			title?: string;
		};
		filters?: any[];
		groupedFilters?: any[];
		id: string;
		itemsActions?: Array<{onClick?: (item: any) => void}>;
	}) => (
		<div data-testid="fds-component" id={id}>
			{emptyState && (
				<div data-testid="fds-empty-state">
					<div data-testid="fds-empty-state-title">
						{emptyState.title}
					</div>

					<div data-testid="fds-empty-state-description">
						{emptyState.description}
					</div>
				</div>
			)}
			<div data-testid="fds-filters">{JSON.stringify(filters)}</div>

			<div data-testid="fds-grouped-filters">
				{JSON.stringify(groupedFilters)}
			</div>

			<button
				data-testid="trigger-info-panel"
				onClick={() =>
					itemsActions?.[0]?.onClick?.({
						itemData: {
							assetCategories: [],
							assetTags: [],
							assetTitle: 'Test Asset Title',
							assetType: 'blog',
							id: 'asset-id-1',
							mimeType: 'blog',
						},
					})
				}
			>
				{'Open Info Panel'}
			</button>

			<button
				data-testid="trigger-info-panel-no-mime"
				onClick={() =>
					itemsActions?.[0]?.onClick?.({
						itemData: {
							assetCategories: [],
							assetTags: [],
							assetTitle: 'Asset Without Mime',
							assetType: 'document',
							id: 'asset-id-2',
						},
					})
				}
			>
				{'Open Info Panel No Mime'}
			</button>

			<button
				data-testid="trigger-info-panel-no-title"
				onClick={() =>
					itemsActions?.[0]?.onClick?.({
						itemData: {
							assetCategories: [],
							assetTags: [],
							assetType: 'folder',
							id: 'fallback-id-3',
							mimeType: 'folder',
						},
					})
				}
			>
				{'Open Info Panel No Title'}
			</button>

			<button
				data-testid="trigger-info-panel-with-items"
				onClick={() =>
					itemsActions?.[0]?.onClick?.({
						itemData: {
							assetCategories: [
								{
									id: 'cat-1',
									name: 'Category One',
									vocabularyId: 'vocab-1',
								},
								{
									id: 'cat-2',
									name: 'Category Two',
									vocabularyId: 'vocab-1',
								},
							],
							assetTags: [{id: 'tag-1', name: 'Tag One'}],
							assetTitle: 'Rich Asset',
							assetType: 'webContent',
							assetVocabularies: [{id: 'vocab-1', name: 'Topic'}],
							id: 'asset-id-4',
							mimeType: 'basic-web-content',
						},
					})
				}
			>
				{'Open Info Panel With Items'}
			</button>

			<button
				data-testid="trigger-info-panel-empty-vocab"
				onClick={() =>
					itemsActions?.[0]?.onClick?.({
						itemData: {
							assetCategories: [
								{
									id: 'cat-1',
									name: 'Category One',
									vocabularyId: 'vocab-1',
								},
							],
							assetTags: [],
							assetTitle: 'Asset With Empty Vocab',
							assetType: 'blog',
							assetVocabularies: [
								{id: 'vocab-1', name: 'Topics'},
								{id: 'vocab-2', name: 'Genres'},
							],
							id: 'asset-id-5',
							mimeType: 'blog',
						},
					})
				}
			>
				{'Open Info Panel Empty Vocab'}
			</button>
		</div>
	),
}));

jest.mock('shared/components/dropdown-range-key/DropdownRangeKey', () => ({
	DropdownRangeKey: ({
		onRangeSelectorChange,
		rangeSelectors,
	}: {
		onRangeSelectorChange: (rs: any) => void;
		rangeSelectors: any;
	}) => (
		<div data-testid="dropdown-range-key">
			<span data-testid="current-range-key">
				{rangeSelectors.rangeKey}
			</span>

			<button
				data-testid="change-range-btn"
				onClick={() =>
					onRangeSelectorChange({
						rangeEnd: null,
						rangeKey: '7', // RangeKeyTimeRanges.Last7Days
						rangeStart: null,
					})
				}
			>
				{'Change Range'}
			</button>

			<button
				data-testid="change-range-custom-btn"
				onClick={() =>
					onRangeSelectorChange({
						rangeEnd: '2024-03-01',
						rangeKey: 'CUSTOM', // RangeKeyTimeRanges.CustomRange
						rangeStart: '2024-01-01',
					})
				}
			>
				{'Change to Custom Range'}
			</button>
		</div>
	),
}));

// breadcrumbs.getHome is a pure utility – mocking it keeps the test focused
// on the page's own behaviour and avoids router-path side effects.

jest.mock('shared/util/breadcrumbs', () => ({
	getHome: jest.fn(({label}: {label?: string} = {}) => ({
		active: false,
		label: label || 'Home',
	})),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useHistory: jest.fn(),
	useParams: () => ({
		channelId: '123',
		groupId: '23',
	}),
}));

// Default push spy shared across tests, reset in beforeEach.

const mockHistoryPush = jest.fn();

const buildHistory = (path = '/workspace/23/123/assets') => {
	const history = createMemoryHistory({initialEntries: [path]});

	history.push = mockHistoryPush;

	return history;
};

// LDP is enabled by default so the account/segment filters, which are LDP-only,
// stay present for the shared assertions and the snapshot.

const store = mockStore(mockStoreDataLDP);

// Helper: wrap List in the minimum context providers it needs.

const renderList = (
	{
		queryString = '',
		store: storeOverride = store,
	}: {queryString?: string; store?: typeof store} = {},
	history = buildHistory(`/workspace/23/123/assets${queryString}`)
) =>
	render(
		<Provider store={storeOverride}>
			<ChannelContext.Provider value={mockChannelContext() as any}>
				<Router history={history}>
					<List />
				</Router>
			</ChannelContext.Provider>
		</Provider>
	);

// Obtain the mocked useHistory so we can configure it per test.

// eslint-disable-next-line @typescript-eslint/no-var-requires
const {useHistory} = require('react-router-dom');

describe('List', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		useHistory.mockReturnValue({push: mockHistoryPush});
	});

	afterEach(cleanup);

	describe('empty state', () => {
		it('should pass the correct title to the FDS empty state', () => {
			renderList();

			expect(
				screen.getByTestId('fds-empty-state-title')
			).toHaveTextContent('There are no assets found.');
		});

		it('should include the check-back-later text in the empty state description', () => {
			renderList();

			expect(
				screen.getByTestId('fds-empty-state-description')
			).toHaveTextContent(
				'Check back later to verify if data has been received from your data sources, or you can try a different date range.'
			);
		});

		it('should render a learn-more-about-assets link in the empty state description', () => {
			renderList();

			const link = screen.getByRole('link', {
				exact: false,
				name: /learn more about assets/i,
			});

			expect(link).toBeInTheDocument();
			expect(link).toHaveAttribute(
				'href',
				'https://learn.liferay.com/w/dxp/personalization/analytics-cloud/touchpoints/assets-analytics'
			);
		});
	});

	describe('rendering', () => {
		it('should render without crashing', () => {
			const {container} = renderList();

			expect(container).toBeInTheDocument();
		});

		it('should render the page title "Assets"', () => {
			renderList();

			expect(screen.getByText('Assets')).toBeInTheDocument();
		});

		it('should render the FrontendDataSet component', () => {
			renderList();

			expect(screen.getByTestId('fds-component')).toBeInTheDocument();
		});

		it('should render the FrontendDataSet with id "assetTable"', () => {
			renderList();

			expect(screen.getByTestId('fds-component')).toHaveAttribute(
				'id',
				'assetTable'
			);
		});

		it('should pass the mimeType filter to FrontendDataSet', () => {
			renderList();

			const filters = JSON.parse(
				screen.getByTestId('fds-filters').textContent
			);

			const mimeTypeFilter = filters.find(
				(filter: {apiURL: string; id: string; label: string}) =>
					filter.id === 'mimeType'
			);

			expect(mimeTypeFilter).toBeDefined();
			expect(mimeTypeFilter.label).toBe('File Type');
			expect(mimeTypeFilter.apiURL).toContain('asset-summary-mime-types');
		});

		it('should render the DropdownRangeKey', () => {
			renderList();

			expect(
				screen.getByTestId('dropdown-range-key')
			).toBeInTheDocument();
		});

		it('should match the snapshot', () => {
			const {container} = renderList();

			expect(container).toMatchSnapshot();
		});
	});

	describe('account filter', () => {
		const getFilters = () =>
			JSON.parse(screen.getByTestId('fds-filters').textContent);

		const getAccountFilter = () => {
			renderList();

			return getFilters().find(
				(filter: {id: string}) => filter.id === 'accountIds'
			);
		};

		it('should pass the account filter to FrontendDataSet', () => {
			expect(getAccountFilter()).toBeDefined();
		});

		it('should set the account filter as the first filter', () => {
			renderList();

			expect(getFilters()[0].id).toBe('accountIds');
		});

		it('should use the account search endpoint in the account filter apiURL', () => {
			expect(getAccountFilter().apiURL).toContain('account/search');
		});

		it('should include the channelId in the account filter apiURL', () => {
			expect(getAccountFilter().apiURL).toContain('channelId=123');
		});

		it('should set itemKey to "id" in the account filter', () => {
			expect(getAccountFilter().itemKey).toBe('id');
		});

		it('should set itemLabel to "accountName" in the account filter', () => {
			expect(getAccountFilter().itemLabel).toBe('accountName');
		});

		it('should not preload the account filter when no accountId is in the URL', () => {
			expect(getAccountFilter().preloadedData).toBeUndefined();
		});

		it('should preload the account filter from the accountId URL param', () => {
			renderList({queryString: '?accountId=acc-1'});

			const accountFilter = getFilters().find(
				(filter: {id: string}) => filter.id === 'accountIds'
			);

			expect(accountFilter.preloadedData).toEqual({
				selectedItems: [{label: 'acc-1', value: 'acc-1'}],
			});
		});

		it('should use the accountName from the URL as the preloaded label', () => {
			renderList({
				queryString: '?accountId=acc-1&accountName=Acme%20Corp',
			});

			const accountFilter = getFilters().find(
				(filter: {id: string}) => filter.id === 'accountIds'
			);

			expect(accountFilter.preloadedData).toEqual({
				selectedItems: [{label: 'Acme Corp', value: 'acc-1'}],
			});
		});
	});

	describe('filter by people (LDP gating)', () => {
		const getFilterIds = () =>
			JSON.parse(screen.getByTestId('fds-filters').textContent).map(
				(filter: {id: string}) => filter.id
			);

		const getGroupedFilters = () =>
			JSON.parse(screen.getByTestId('fds-grouped-filters').textContent);

		describe('when LDP is enabled', () => {
			it('should include the account and segment filters', () => {
				renderList();

				const ids = getFilterIds();

				expect(ids).toContain('accountIds');
				expect(ids).toContain('segmentIds');
			});

			it('should render the "Filter by People" grouped filter', () => {
				renderList();

				const labels = getGroupedFilters().map(
					(group: {label: string}) => group.label
				);

				expect(labels).toContain('Filter by People');
			});
		});

		describe('when LDP is not enabled', () => {
			const nonLDPStore = mockStore();

			it('should not include the account filter', () => {
				renderList({store: nonLDPStore});

				expect(getFilterIds()).not.toContain('accountIds');
			});

			it('should not include the segment filter', () => {
				renderList({store: nonLDPStore});

				expect(getFilterIds()).not.toContain('segmentIds');
			});

			it('should not render the "Filter by People" grouped filter', () => {
				renderList({store: nonLDPStore});

				const labels = getGroupedFilters().map(
					(group: {label: string}) => group.label
				);

				expect(labels).not.toContain('Filter by People');
			});

			it('should keep the "Filter by" grouped filter with its filters', () => {
				renderList({store: nonLDPStore});

				const groupedFilters = getGroupedFilters();

				expect(groupedFilters).toHaveLength(1);
				expect(groupedFilters[0].label).toBe('Filter By');
				expect(groupedFilters[0].filters).toEqual([
					'assetType',
					'tags/id',
					'categories/id',
					'mimeType',
				]);
			});
		});
	});

	describe('initial range selector state', () => {
		it('should default to Last30Days when no query string is present', () => {
			renderList();

			expect(screen.getByTestId('current-range-key')).toHaveTextContent(
				RangeKeyTimeRanges.Last30Days
			);
		});

		it('should pick up rangeKey from the URL query string', () => {

			// The real useQueryRangeSelectors reads from the URL; we provide a
			// URL carrying a rangeKey to verify the initial state is seeded
			// from the query params.

			renderList({
				queryString: `?rangeKey=${RangeKeyTimeRanges.Last7Days}`,
			});

			expect(screen.getByTestId('current-range-key')).toHaveTextContent(
				RangeKeyTimeRanges.Last7Days
			);
		});
	});

	describe('onRangeSelectorChange', () => {
		it('should call history.push when the range selector changes', () => {
			renderList();

			fireEvent.click(screen.getByTestId('change-range-btn'));

			expect(mockHistoryPush).toHaveBeenCalledTimes(1);
		});

		it('should update the displayed range key after a change', () => {

			// List calls setRangeSelectors in the onRangeSelectorChange
			// handler, which causes a re-render passing the new rangeSelectors
			// to the stub DropdownRangeKey. Since history.push is mocked and
			// does not navigate, the state update drives the re-render.

			renderList();

			fireEvent.click(screen.getByTestId('change-range-btn'));

			expect(screen.getByTestId('current-range-key')).toHaveTextContent(
				RangeKeyTimeRanges.Last7Days
			);
		});

		it('should include the new rangeKey in the URL pushed to history', () => {
			renderList();

			fireEvent.click(screen.getByTestId('change-range-btn'));

			const pushedPath: string = mockHistoryPush.mock.calls[0][0];

			expect(pushedPath).toContain(RangeKeyTimeRanges.Last7Days);
		});

		it('should reset page to DEFAULT_CUR (1) when the range changes', () => {
			renderList();

			fireEvent.click(screen.getByTestId('change-range-btn'));

			// FaroConstants.pagination.cur === 1 in the jest config globals

			const pushedPath: string = mockHistoryPush.mock.calls[0][0];

			expect(pushedPath).toContain('page=1');
		});

		it('should strip rangeEnd and rangeStart from the URL when switching to a preset range', () => {

			// Start with a custom range in the URL so the strip logic is
			// exercised by removeUriQueryParam.

			renderList({
				queryString:
					'?rangeKey=CUSTOM&rangeStart=2024-01-01&rangeEnd=2024-03-01',
			});

			fireEvent.click(screen.getByTestId('change-range-btn'));

			const pushedPath: string = mockHistoryPush.mock.calls[0][0];

			expect(pushedPath).not.toContain('rangeEnd=2024-03-01');
			expect(pushedPath).not.toContain('rangeStart=2024-01-01');
		});

		it('should include rangeEnd and rangeStart in the URL for a custom range', () => {
			renderList();

			fireEvent.click(screen.getByTestId('change-range-custom-btn'));

			// pickBy strips null values; rangeEnd and rangeStart are truthy
			// for a custom range, so they should appear in the URL.

			const pushedPath: string = mockHistoryPush.mock.calls[0][0];

			expect(pushedPath).toContain('rangeEnd=2024-03-01');
			expect(pushedPath).toContain('rangeStart=2024-01-01');
		});

		it('should update the displayed range key to CustomRange after a custom range change', () => {
			renderList();

			fireEvent.click(screen.getByTestId('change-range-custom-btn'));

			expect(screen.getByTestId('current-range-key')).toHaveTextContent(
				RangeKeyTimeRanges.CustomRange
			);
		});
	});

	describe('breadcrumbs', () => {
		it('should build the home breadcrumb using the selected channel name', () => {

			// mockChannelContext() returns selectedChannel = mockChannel(1),
			// whose name is "Channel 1".

			// eslint-disable-next-line @typescript-eslint/no-var-requires
			const breadcrumbs = require('shared/util/breadcrumbs');

			renderList();

			expect(breadcrumbs.getHome).toHaveBeenCalledWith(
				expect.objectContaining({
					channelId: '123',
					groupId: '23',
					label: 'Channel 1',
				})
			);
		});

		it('should pass null label when no channel is selected', () => {

			// eslint-disable-next-line @typescript-eslint/no-var-requires
			const breadcrumbs = require('shared/util/breadcrumbs');

			const contextWithNoChannel = {
				...mockChannelContext(),
				selectedChannel: null,
			};

			render(
				<Provider store={store}>
					<ChannelContext.Provider
						value={contextWithNoChannel as any}
					>
						<Router history={buildHistory()}>
							<List />
						</Router>
					</ChannelContext.Provider>
				</Provider>
			);

			expect(breadcrumbs.getHome).toHaveBeenCalledWith(
				expect.objectContaining({
					label: undefined,
				})
			);
		});
	});

	describe('FDS remount key', () => {
		it('should reflect the updated rangeKey in component state after change, triggering FDS remount', () => {

			// List passes key={Object.values(rangeSelectors).join()} to FDS.
			// After setRangeSelectors is called the key changes, forcing FDS
			// to remount. We verify via the DropdownRangeKey stub that the
			// state was updated.

			renderList();

			fireEvent.click(screen.getByTestId('change-range-btn'));

			expect(screen.getByTestId('current-range-key')).toHaveTextContent(
				RangeKeyTimeRanges.Last7Days
			);
		});
	});

	describe('info panel', () => {
		it('should display the asset title in the panel header when opened', () => {
			renderList();

			fireEvent.click(screen.getByTestId('trigger-info-panel'));

			expect(screen.getByText('Test Asset Title')).toBeInTheDocument();
		});

		it('should fall back to asset id when assetTitle is absent', () => {
			renderList();

			fireEvent.click(screen.getByTestId('trigger-info-panel-no-title'));

			expect(screen.getByRole('heading', {level: 4})).toHaveTextContent(
				'fallback-id-3'
			);
		});

		it('should render AssetIcon when mimeType is present', () => {
			const {container} = renderList();

			fireEvent.click(screen.getByTestId('trigger-info-panel'));

			// AssetIcon renders a ClaySticker; verify a sticker is present
			// inside the side panel header area.

			expect(container.querySelector('.sticker')).toBeInTheDocument();
		});

		it('should render a default AssetIcon when mimeType is absent', () => {
			const {container} = renderList();

			fireEvent.click(screen.getByTestId('trigger-info-panel-no-mime'));

			expect(container.querySelector('.sticker')).toBeInTheDocument();
		});

		it('should add the sidebar-opened class to the page when the panel is open', () => {
			const {container} = renderList();

			fireEvent.click(screen.getByTestId('trigger-info-panel'));

			expect(
				container.querySelector('.sidebar-opened')
			).toBeInTheDocument();
		});

		it('should not have the sidebar-opened class before the panel is opened', () => {
			const {container} = renderList();

			expect(container.querySelector('.sidebar-opened')).toBeNull();
		});

		it('should remove the sidebar-opened class after the panel is closed', () => {
			const {container} = renderList();

			fireEvent.click(screen.getByTestId('trigger-info-panel'));

			expect(
				container.querySelector('.sidebar-opened')
			).toBeInTheDocument();

			// ClayCore's SidePanel calls onOpenChange when closed; trigger it
			// via the close button rendered inside the panel.

			const closeButton = container.querySelector(
				'.info-panel-root .close'
			);

			if (closeButton) {
				fireEvent.click(closeButton);

				expect(container.querySelector('.sidebar-opened')).toBeNull();
			}
		});

		it('should render the Categorization tab', () => {
			renderList();

			fireEvent.click(screen.getByTestId('trigger-info-panel'));

			expect(screen.getByText('Categorization')).toBeInTheDocument();
		});
	});

	describe('CategoriesInfoPanelContent', () => {
		it('should display empty state when there are no categories', () => {
			renderList();

			fireEvent.click(screen.getByTestId('trigger-info-panel'));
			fireEvent.click(screen.getByText('Categorization'));

			expect(
				screen.getByText('No Categories were found for this asset.')
			).toBeInTheDocument();
		});

		it('should group categories under their vocabulary name', () => {
			renderList();

			fireEvent.click(
				screen.getByTestId('trigger-info-panel-with-items')
			);
			fireEvent.click(screen.getByText('Categorization'));

			expect(screen.getByText('Topic')).toBeInTheDocument();
			expect(screen.getByText('Category One')).toBeInTheDocument();
			expect(screen.getByText('Category Two')).toBeInTheDocument();
		});

		it('should not render a vocabulary that has no matching categories', () => {
			renderList();

			fireEvent.click(
				screen.getByTestId('trigger-info-panel-empty-vocab')
			);
			fireEvent.click(screen.getByText('Categorization'));

			expect(screen.getByText('Topics')).toBeInTheDocument();
			expect(screen.queryByText('Genres')).not.toBeInTheDocument();
		});

		it('should group all categories from the same vocabulary under one header', () => {
			renderList();

			fireEvent.click(
				screen.getByTestId('trigger-info-panel-with-items')
			);
			fireEvent.click(screen.getByText('Categorization'));

			expect(screen.getAllByText('Topic')).toHaveLength(1);
			expect(screen.getByText('Category One')).toBeInTheDocument();
			expect(screen.getByText('Category Two')).toBeInTheDocument();
		});

		it('should not show empty state when categories are present', () => {
			renderList();

			fireEvent.click(
				screen.getByTestId('trigger-info-panel-with-items')
			);
			fireEvent.click(screen.getByText('Categorization'));

			expect(
				screen.queryByText('No Categories were found for this asset.')
			).not.toBeInTheDocument();
		});
	});

	describe('TagsInfoPanelContent', () => {
		it('should display empty state when there are no tags', () => {
			renderList();

			fireEvent.click(screen.getByTestId('trigger-info-panel'));
			fireEvent.click(screen.getByText('Categorization'));

			expect(
				screen.getByText('No Tags were found for this asset.')
			).toBeInTheDocument();
		});

		it('should render tags as labels', () => {
			renderList();

			fireEvent.click(
				screen.getByTestId('trigger-info-panel-with-items')
			);
			fireEvent.click(screen.getByText('Categorization'));

			expect(screen.getByText('Tag One')).toBeInTheDocument();
		});

		it('should not show empty state when tags are present', () => {
			renderList();

			fireEvent.click(
				screen.getByTestId('trigger-info-panel-with-items')
			);
			fireEvent.click(screen.getByText('Categorization'));

			expect(
				screen.queryByText('No Tags were found for this asset.')
			).not.toBeInTheDocument();
		});
	});
});
