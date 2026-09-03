import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import TopAssets from '../TopAssets';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	within,
} from '@testing-library/react';
import {ITopAsset} from 'shared/api/assets';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {useRequest} from 'shared/hooks/useRequest';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	assets: {
		fetchAccountTopAssets: jest.fn(),
	},
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

const mockPush = jest.fn();

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '5', groupId: '23', id: 'acc-1'}),
}));

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: mockPush}),
}));

const mockedUseRequest = useRequest as jest.Mock;

const ACCOUNT = {accountName: 'Acme', id: 'acc-1'};

const MOCK_CONTEXT = {
	accountId: ACCOUNT.id,
	accountName: ACCOUNT.accountName,
	filters: {},
	router: {
		params: {channelId: '5', groupId: '23', id: ACCOUNT.id},
		query: {rangeKey: RangeKeyTimeRanges.Last30Days},
	},
};

const renderTopAssets = () =>
	render(
		<Provider store={mockStore()}>
			<BasePage.Context.Provider value={MOCK_CONTEXT}>
				<MemoryRouter>
					<MockedProvider
						addTypename={false}
						mocks={[mockTimeRangeReq(), mockPreferenceReq()]}
					>
						<TopAssets account={ACCOUNT} />
					</MockedProvider>
				</MemoryRouter>
			</BasePage.Context.Provider>
		</Provider>
	);

const renderTopAssetsWithoutAccount = () =>
	render(
		<Provider store={mockStore()}>
			<BasePage.Context.Provider value={MOCK_CONTEXT}>
				<MemoryRouter>
					<MockedProvider
						addTypename={false}
						mocks={[mockTimeRangeReq(), mockPreferenceReq()]}
					>
						<TopAssets />
					</MockedProvider>
				</MemoryRouter>
			</BasePage.Context.Provider>
		</Provider>
	);

const buildAsset = (overrides: Partial<ITopAsset> = {}): ITopAsset => ({
	assetTitle: 'Asset 1',
	assetType: 'webContent',
	downloadsMetric: {value: 5},
	id: 'a-1',
	impressionsMetric: {value: 30},
	mimeType: undefined,
	viewsMetric: {value: 10},
	...overrides,
});

const DEFAULT_ASSETS: ITopAsset[] = [
	buildAsset({
		assetTitle: 'Web Content One',
		assetType: 'webContent',
		downloadsMetric: {value: 1},
		id: 'a-1',
		impressionsMetric: {value: 100},
		viewsMetric: {value: 50},
	}),
	buildAsset({
		assetTitle: 'Blog Post',
		assetType: 'blog',
		downloadsMetric: {value: 2},
		id: 'a-2',
		impressionsMetric: {value: 80},
		viewsMetric: {value: 40},
	}),
	buildAsset({
		assetTitle: 'Brochure PDF',
		assetType: 'document',
		downloadsMetric: {value: 25},
		id: 'a-3',
		impressionsMetric: {value: 30},
		viewsMetric: {value: 12},
	}),
	buildAsset({
		assetTitle: 'Lead Form',
		assetType: 'form',
		downloadsMetric: {value: 0},
		id: 'a-4',
		impressionsMetric: {value: 20},
		viewsMetric: {value: 8},
	}),
	buildAsset({
		assetTitle: 'Custom Entry',
		assetType: 'customObjectEntry',
		downloadsMetric: {value: 0},
		id: 'a-5',
		impressionsMetric: {value: 10},
		viewsMetric: {value: 4},
	}),
];

const mockUseRequestWith = ({
	data,
	loading = false,
}: {
	data?: {items: ITopAsset[]};
	loading?: boolean;
}) => {
	mockedUseRequest.mockImplementation(() => ({
		data,
		loading,
		refetch: jest.fn(),
	}));
};

describe('TopAssets', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		mockUseRequestWith({data: {items: DEFAULT_ASSETS}});
	});

	afterEach(cleanup);

	describe('rendering', () => {
		it('should render the card title', () => {
			renderTopAssets();

			expect(screen.getByText('TOP ASSETS')).toBeInTheDocument();
		});

		it('should render both tab labels', () => {
			renderTopAssets();

			expect(
				screen.getByRole('tab', {name: 'Content'})
			).toBeInTheDocument();
			expect(
				screen.getByRole('tab', {name: 'Files'})
			).toBeInTheDocument();
		});

		it('should render the View All button', () => {
			renderTopAssets();

			expect(
				screen.getByRole('button', {name: 'View All'})
			).toBeInTheDocument();
		});

		it('should render the Group By picker with the default metric (Impressions)', () => {
			renderTopAssets();

			expect(screen.getAllByText('Group By').length).toBeGreaterThan(0);
			expect(screen.getAllByText('Impressions').length).toBeGreaterThan(
				0
			);
		});
	});

	describe('data rendering', () => {
		it('should render an asset link for every item returned', () => {
			renderTopAssets();

			expect(
				screen.getAllByRole('link', {name: 'Web Content One'})[0]
			).toBeInTheDocument();
			expect(
				screen.getAllByRole('link', {name: 'Blog Post'})[0]
			).toBeInTheDocument();
			expect(
				screen.getAllByRole('link', {name: 'Brochure PDF'})[0]
			).toBeInTheDocument();
			expect(
				screen.getAllByRole('link', {name: 'Lead Form'})[0]
			).toBeInTheDocument();
			expect(
				screen.getAllByRole('link', {name: 'Custom Entry'})[0]
			).toBeInTheDocument();
		});

		it('should render the metric values for the default Impressions metric', () => {
			renderTopAssets();

			expect(screen.getAllByText('100').length).toBeGreaterThan(0);
			expect(screen.getAllByText('80').length).toBeGreaterThan(0);
			expect(screen.getAllByText('30').length).toBeGreaterThan(0);
			expect(screen.getAllByText('20').length).toBeGreaterThan(0);
			expect(screen.getAllByText('10').length).toBeGreaterThan(0);
		});

		it('should route blog assets through the Blogs overview path', () => {
			renderTopAssets();

			const link = screen.getAllByRole('link', {
				name: 'Blog Post',
			})[0] as HTMLAnchorElement;

			expect(link.getAttribute('href')).toContain('/assets/blogs/');
		});

		it('should route document assets through the Documents and Media path', () => {
			renderTopAssets();

			const link = screen.getAllByRole('link', {
				name: 'Brochure PDF',
			})[0] as HTMLAnchorElement;

			expect(link.getAttribute('href')).toContain(
				'/assets/documents-and-media/'
			);
		});

		it('should route form assets through the Forms path', () => {
			renderTopAssets();

			const link = screen.getAllByRole('link', {
				name: 'Lead Form',
			})[0] as HTMLAnchorElement;

			expect(link.getAttribute('href')).toContain('/assets/forms/');
		});

		it('should route web content assets through the Web Content path', () => {
			renderTopAssets();

			const link = screen.getAllByRole('link', {
				name: 'Web Content One',
			})[0] as HTMLAnchorElement;

			expect(link.getAttribute('href')).toContain('/assets/web-content/');
		});

		it('should route unknown asset types through the Object Entry path', () => {
			renderTopAssets();

			const link = screen.getAllByRole('link', {
				name: 'Custom Entry',
			})[0] as HTMLAnchorElement;

			expect(link.getAttribute('href')).toContain(
				'/assets/object-entry/'
			);
		});

		it('should include accountId and accountName as query params on each asset link', () => {
			renderTopAssets();

			const link = screen.getAllByRole('link', {
				name: 'Web Content One',
			})[0] as HTMLAnchorElement;

			expect(link.getAttribute('href')).toContain('accountId=acc-1');
			expect(link.getAttribute('href')).toContain('accountName=Acme');
		});
	});

	describe('time range', () => {
		it('should render the range key dropdown', async () => {
			const {container} = renderTopAssets();

			await waitForLoadingToBeRemoved(container);

			expect(
				container.querySelector('.card-header .dropdown-range-key-root')
			).toBeInTheDocument();
		});

		it('should request the assets for the selected range', () => {
			renderTopAssets();

			expect(mockedUseRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					variables: expect.objectContaining({
						rangeEnd: null,
						rangeKey: 30,
						rangeStart: null,
					}),
				})
			);
		});
	});

	describe('group by picker', () => {
		it('should refetch with viewsMetric when the user picks Views', () => {
			renderTopAssets();

			fireEvent.click(
				screen.getAllByRole('combobox', {name: 'Group By'})[0]
			);

			fireEvent.click(screen.getAllByRole('option', {name: 'Views'})[0]);

			const lastCall =
				mockedUseRequest.mock.calls[
					mockedUseRequest.mock.calls.length - 1
				][0];

			expect(lastCall.variables.selectedMetric).toBe('viewsMetric');
		});

		it('should refetch with downloadsMetric when the user picks Downloads on the Files tab', () => {
			renderTopAssets();

			fireEvent.click(screen.getByRole('tab', {name: 'Files'}));

			fireEvent.click(
				screen.getAllByRole('combobox', {name: 'Group By'})[0]
			);

			fireEvent.click(
				screen.getAllByRole('option', {name: 'Downloads'})[0]
			);

			const lastCall =
				mockedUseRequest.mock.calls[
					mockedUseRequest.mock.calls.length - 1
				][0];

			expect(lastCall.variables.selectedMetric).toBe('downloadsMetric');
		});

		it('should not offer the Downloads metric on the Content tab', () => {
			renderTopAssets();

			fireEvent.click(
				screen.getAllByRole('combobox', {name: 'Group By'})[0]
			);

			expect(
				screen.queryByRole('option', {name: 'Downloads'})
			).toBeNull();
			expect(
				screen.getAllByRole('option', {name: 'Impressions'}).length
			).toBeGreaterThan(0);
			expect(
				screen.getAllByRole('option', {name: 'Views'}).length
			).toBeGreaterThan(0);
		});

		it('should offer the Downloads metric on the Files tab', () => {
			renderTopAssets();

			fireEvent.click(screen.getByRole('tab', {name: 'Files'}));

			fireEvent.click(
				screen.getAllByRole('combobox', {name: 'Group By'})[0]
			);

			expect(
				screen.getAllByRole('option', {name: 'Downloads'}).length
			).toBeGreaterThan(0);
		});

		it('should reset to impressionsMetric when switching to Content while Downloads is selected', () => {
			renderTopAssets();

			fireEvent.click(screen.getByRole('tab', {name: 'Files'}));

			fireEvent.click(
				screen.getAllByRole('combobox', {name: 'Group By'})[0]
			);

			fireEvent.click(
				screen.getAllByRole('option', {name: 'Downloads'})[0]
			);

			fireEvent.click(screen.getByRole('tab', {name: 'Content'}));

			const lastCall =
				mockedUseRequest.mock.calls[
					mockedUseRequest.mock.calls.length - 1
				][0];

			expect(lastCall.variables.objectType).toBe('content');
			expect(lastCall.variables.selectedMetric).toBe('impressionsMetric');
		});
	});

	describe('tab switching', () => {
		it('should request the Content objectType on initial render', () => {
			renderTopAssets();

			const firstCall = mockedUseRequest.mock.calls[0][0];

			expect(firstCall.variables.objectType).toBe('content');
		});

		it('should request the File objectType after clicking the Files tab', () => {
			renderTopAssets();

			fireEvent.click(screen.getByRole('tab', {name: 'Files'}));

			const lastCall =
				mockedUseRequest.mock.calls[
					mockedUseRequest.mock.calls.length - 1
				][0];

			expect(lastCall.variables.objectType).toBe('file');
		});
	});

	describe('request shape', () => {
		it('should forward accountId, channelId, and groupId to the data source', () => {
			renderTopAssets();

			const firstCall = mockedUseRequest.mock.calls[0][0];

			expect(firstCall.variables.accountId).toBe('acc-1');
			expect(firstCall.variables.channelId).toBe('5');
			expect(firstCall.variables.groupId).toBe('23');
		});
	});

	describe('view all', () => {
		it('should navigate to the asset list when the View All button is clicked', () => {
			renderTopAssets();

			fireEvent.click(screen.getByRole('button', {name: 'View All'}));

			expect(mockPush).toHaveBeenCalledTimes(1);

			const pushedURL = mockPush.mock.calls[0][0];

			expect(pushedURL).toContain('/assets');
			expect(pushedURL).toContain('accountId=acc-1');
			expect(pushedURL).toContain('accountName=Acme');
		});

		it('should pass the default metric (Impressions) as the orderBy param', () => {
			renderTopAssets();

			fireEvent.click(screen.getByRole('button', {name: 'View All'}));

			const pushedURL = mockPush.mock.calls[0][0];

			expect(pushedURL).toContain('orderBy=impressionsMetric');
		});

		it('should pass the metric selected in the Group By picker as the orderBy param', () => {
			renderTopAssets();

			fireEvent.click(
				screen.getAllByRole('combobox', {name: 'Group By'})[0]
			);

			fireEvent.click(screen.getAllByRole('option', {name: 'Views'})[0]);

			fireEvent.click(screen.getByRole('button', {name: 'View All'}));

			const pushedURL = mockPush.mock.calls[0][0];

			expect(pushedURL).toContain('orderBy=viewsMetric');
		});

		it('should pass the Content objectType from the Content tab', () => {
			renderTopAssets();

			fireEvent.click(screen.getByRole('button', {name: 'View All'}));

			const pushedURL = mockPush.mock.calls[0][0];

			expect(pushedURL).toContain('objectType=content');
		});

		it('should pass the File objectType from the Files tab', () => {
			renderTopAssets();

			fireEvent.click(screen.getByRole('tab', {name: 'Files'}));

			fireEvent.click(screen.getByRole('button', {name: 'View All'}));

			const pushedURL = mockPush.mock.calls[0][0];

			expect(pushedURL).toContain('objectType=file');
		});
	});

	describe('loading state', () => {
		it('should render the loading indicator while the request is in flight', () => {
			mockUseRequestWith({loading: true});

			const {container} = renderTopAssets();

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();
		});

		it('should stop loading when there is no account to scope by', () => {
			mockUseRequestWith({loading: true});

			const {container} = renderTopAssetsWithoutAccount();

			const tabPanel = container.querySelector(
				'.tab-pane'
			) as HTMLElement;

			expect(tabPanel.querySelector('.loading-root')).toBeNull();
			expect(
				within(tabPanel).getAllByText('No Assets Available').length
			).toBeGreaterThan(0);
		});

		it('should not render asset rows while loading', () => {
			mockUseRequestWith({loading: true});

			renderTopAssets();

			expect(
				screen.queryByRole('link', {name: 'Web Content One'})
			).toBeNull();
		});

		it('should not render the View All button while loading', () => {
			mockUseRequestWith({loading: true});

			renderTopAssets();

			expect(screen.queryByRole('button', {name: 'View All'})).toBeNull();
		});
	});

	describe('view all visibility', () => {
		it('should not render the View All button when there are no assets', () => {
			mockUseRequestWith({data: {items: []}});

			renderTopAssets();

			expect(screen.queryByRole('button', {name: 'View All'})).toBeNull();
		});

		it('should render the View All button when assets are returned', () => {
			renderTopAssets();

			expect(
				screen.getByRole('button', {name: 'View All'})
			).toBeInTheDocument();
		});
	});

	describe('bottom spacing', () => {
		it('should leave the tab pane bottom unpadded when the View All footer follows the table', () => {
			const {container} = renderTopAssets();

			expect(container.querySelector('.tab-pane.active')).toHaveClass(
				'pb-0'
			);
		});

		it('should pad the tab pane bottom when no assets leave the card without a footer', () => {
			mockUseRequestWith({data: {items: []}});

			const {container} = renderTopAssets();

			expect(container.querySelector('.tab-pane.active')).toHaveClass(
				'pb-4'
			);
		});
	});

	describe('empty state', () => {
		it('should render the Content empty state when no assets are returned on the Content tab', () => {
			mockUseRequestWith({data: {items: []}});

			renderTopAssets();

			expect(
				screen.getAllByText('No Assets Available').length
			).toBeGreaterThan(0);
		});

		it('should render the Files empty state when no assets are returned on the Files tab', () => {
			mockUseRequestWith({data: {items: []}});

			renderTopAssets();

			fireEvent.click(screen.getByRole('tab', {name: 'Files'}));

			expect(
				screen.getAllByText('No Files Available').length
			).toBeGreaterThan(0);
		});

		it('should not render asset rows when no assets are returned', () => {
			mockUseRequestWith({data: {items: []}});

			renderTopAssets();

			expect(
				screen.queryByRole('link', {name: 'Web Content One'})
			).toBeNull();
		});
	});

	describe('metric column', () => {
		it('should show the selected metric value in the metric column', () => {
			mockUseRequestWith({
				data: {
					items: [
						buildAsset({
							assetTitle: 'Solo',
							downloadsMetric: {value: 7},
							id: 'a-solo',
							impressionsMetric: {value: 999},
							viewsMetric: {value: 333},
						}),
					],
				},
			});

			const {container} = renderTopAssets();

			const tabPanel = container.querySelector(
				'.tab-pane'
			) as HTMLElement;

			expect(within(tabPanel).getAllByText('999').length).toBe(1);
		});

		it('should show a zero when the asset carries no selected metric', () => {
			mockUseRequestWith({
				data: {
					items: [
						buildAsset({
							assetTitle: 'Metricless',
							id: 'a-metricless',
							impressionsMetric: undefined,
						}),
					],
				},
			});

			const {container} = renderTopAssets();

			const tabPanel = container.querySelector(
				'.tab-pane'
			) as HTMLElement;

			expect(within(tabPanel).getAllByText('0').length).toBe(1);
		});
	});
});
