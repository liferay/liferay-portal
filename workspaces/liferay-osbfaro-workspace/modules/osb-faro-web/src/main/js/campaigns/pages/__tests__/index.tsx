import * as API from 'shared/api';
import Campaigns from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {act, cleanup, render, waitFor} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '23',
	}),
}));

jest.mock('shared/context/dataSources', () => ({
	useDataSources: jest.fn(),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn(),
}));

let lastDropdownRangeKeyProps: any;

jest.mock('../../components/CampaignsDataSet', () => ({
	__esModule: true,
	default: () => <div data-testid="campaigns-data-set" />,
}));

jest.mock('../../components/OverviewSection', () => ({
	__esModule: true,
	default: () => <div data-testid="overview-section" />,
}));

// The real dropdown queries its presets through Apollo, which these tests do
// not provide. Its own suite covers it.

jest.mock('shared/components/dropdown-range-key/DropdownRangeKey', () => ({
	DropdownRangeKey: (props: any) => {
		lastDropdownRangeKeyProps = props;

		return <div data-testid="dropdown-range-key" />;
	},
}));

const fetchCampaignMetrics = API.campaigns.fetchCampaignMetrics as jest.Mock;
const mockedUseCurrentUser = useCurrentUser as jest.Mock;
const mockedUseDataSources = useDataSources as jest.Mock;

const renderCampaigns = (initialEntry = '/') =>
	render(
		<Provider store={mockStore()}>
			<ChannelContext.Provider value={mockChannelContext() as any}>
				<MemoryRouter initialEntries={[initialEntry]}>
					<Campaigns />
				</MemoryRouter>
			</ChannelContext.Provider>
		</Provider>
	);

describe('Campaigns', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		lastDropdownRangeKeyProps = undefined;

		mockedUseCurrentUser.mockReturnValue({isAdmin: () => true});
		mockedUseDataSources.mockReturnValue({empty: false, loading: false});
	});

	afterEach(cleanup);

	it('should render the page title', () => {
		const {getByText} = renderCampaigns();

		expect(getByText('Campaigns')).toBeTruthy();
	});

	it('should render the channel breadcrumb', () => {
		const {container} = renderCampaigns();

		expect(container.querySelector('.breadcrumb')).toBeTruthy();
	});

	it('should render the overview section above the table', () => {
		const {getByTestId} = renderCampaigns();

		const overview = getByTestId('overview-section');
		const table = getByTestId('campaigns-data-set');

		expect(overview).toBeTruthy();
		expect(
			overview.compareDocumentPosition(table) &
				Node.DOCUMENT_POSITION_FOLLOWING
		).toBeTruthy();
	});

	it('should render the campaigns table', () => {
		const {getByTestId} = renderCampaigns();

		expect(getByTestId('campaigns-data-set')).toBeTruthy();
	});

	it('should no longer pin the list to all time', () => {
		const {queryByText} = renderCampaigns();

		expect(queryByText('All Time')).toBeNull();
	});

	it('should render the range selector in the sub header', () => {
		const {container} = renderCampaigns();

		expect(
			container.querySelector(
				'.sub-header-root [data-testid="dropdown-range-key"]'
			)
		).toBeTruthy();
	});

	it('should default to the last 30 days', async () => {
		renderCampaigns();

		expect(lastDropdownRangeKeyProps.rangeSelectors.rangeKey).toBe('30');

		await waitFor(() =>
			expect(fetchCampaignMetrics).toHaveBeenCalledWith(
				expect.objectContaining({rangeQueryValues: {rangeKey: '30'}})
			)
		);
	});

	it('should read the range from the URL', async () => {
		renderCampaigns('/?rangeKey=7');

		expect(lastDropdownRangeKeyProps.rangeSelectors.rangeKey).toBe('7');

		await waitFor(() =>
			expect(fetchCampaignMetrics).toHaveBeenCalledWith(
				expect.objectContaining({rangeQueryValues: {rangeKey: '7'}})
			)
		);
	});

	it('should ask for the metrics of a custom range by its bounds alone', async () => {
		renderCampaigns(
			'/?rangeEnd=2026-10-20&rangeKey=CUSTOM&rangeStart=2026-10-01'
		);

		await waitFor(() =>
			expect(fetchCampaignMetrics).toHaveBeenCalledWith(
				expect.objectContaining({
					rangeQueryValues: {
						rangeEnd: '2026-10-20',
						rangeStart: '2026-10-01',
					},
				})
			)
		);
	});

	it('should write the selected range to the URL', async () => {
		renderCampaigns();

		act(() =>
			lastDropdownRangeKeyProps.onRangeSelectorChange({
				rangeEnd: '',
				rangeKey: '90',
				rangeStart: '',
			})
		);

		expect(lastDropdownRangeKeyProps.rangeSelectors.rangeKey).toBe('90');

		await waitFor(() =>
			expect(fetchCampaignMetrics).toHaveBeenCalledWith(
				expect.objectContaining({rangeQueryValues: {rangeKey: '90'}})
			)
		);
	});

	describe('when no data source is connected', () => {
		beforeEach(() => {
			mockedUseDataSources.mockReturnValue({empty: true, loading: false});
		});

		it('should explain that no campaign data is available', () => {
			const {getByText} = renderCampaigns();

			expect(getByText(/no campaign data available/i)).toBeTruthy();
		});

		it('should replace the overview, the table and the range selector', () => {
			const {queryByTestId} = renderCampaigns();

			expect(queryByTestId('overview-section')).toBeNull();
			expect(queryByTestId('campaigns-data-set')).toBeNull();
			expect(queryByTestId('dropdown-range-key')).toBeNull();
		});

		it('should ask an admin to connect a data source', () => {
			const {getByText} = renderCampaigns();

			expect(
				getByText(/connect a data source containing campaign data/i)
			).toBeTruthy();
			expect(getByText(/connect data source/i)).toBeTruthy();
		});

		it('should point a member at an administrator instead', () => {
			mockedUseCurrentUser.mockReturnValue({isAdmin: () => false});

			const {getByText, queryByText} = renderCampaigns();

			expect(
				getByText(/contact an administrator to connect a data source/i)
			).toBeTruthy();
			expect(queryByText(/^connect data source$/i)).toBeNull();
		});

		it('should link out to the data source documentation', () => {
			const {getByText} = renderCampaigns();

			expect(getByText(/learn more about data sources/i)).toBeTruthy();
		});
	});

	describe('while the data sources are loading', () => {
		it('should render neither the empty state nor the table', () => {
			mockedUseDataSources.mockReturnValue({empty: false, loading: true});

			const {queryByTestId, queryByText} = renderCampaigns();

			expect(queryByText(/no campaign data available/i)).toBeNull();
			expect(queryByTestId('campaigns-data-set')).toBeNull();
		});
	});
});
