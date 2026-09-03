import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import TopAssets from '../TopAssets';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {ITopAsset} from 'shared/api/assets';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	assets: {
		fetchIndividualTopAssets: jest.fn(),
	},
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

const mockPush = jest.fn();

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '5', groupId: '23', id: 'ind-1'}),
}));

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: mockPush}),
}));

const mockedUseRequest = useRequest as jest.Mock;

const INDIVIDUAL = {individualId: 'ind-1', individualName: 'Jane Doe'};

const MOCK_CONTEXT = {
	filters: {},
	individualId: INDIVIDUAL.individualId,
	individualName: INDIVIDUAL.individualName,
	router: {
		params: {channelId: '5', groupId: '23', id: INDIVIDUAL.individualId},
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
						<TopAssets {...INDIVIDUAL} />
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
		assetTitle: 'Brochure PDF',
		assetType: 'document',
		downloadsMetric: {value: 25},
		id: 'a-2',
		impressionsMetric: {value: 30},
		viewsMetric: {value: 12},
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
				screen.getAllByRole('link', {name: 'Brochure PDF'})[0]
			).toBeInTheDocument();
		});

		it('should render the metric values for the default Impressions metric', () => {
			renderTopAssets();

			expect(screen.getAllByText('100').length).toBeGreaterThan(0);
			expect(screen.getAllByText('30').length).toBeGreaterThan(0);
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

		it('should include individualId and individualName as query params on each asset link', () => {
			renderTopAssets();

			const link = screen.getAllByRole('link', {
				name: 'Web Content One',
			})[0] as HTMLAnchorElement;

			expect(link.getAttribute('href')).toContain('individualId=ind-1');
			expect(link.getAttribute('href')).toContain(
				'individualName=Jane+Doe'
			);
		});
	});

	describe('request shape', () => {
		it('should forward individualId, channelId, and groupId to the data source', () => {
			renderTopAssets();

			const firstCall = mockedUseRequest.mock.calls[0][0];

			expect(firstCall.variables.individualId).toBe('ind-1');
			expect(firstCall.variables.channelId).toBe('5');
			expect(firstCall.variables.groupId).toBe('23');
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

		it('should not offer the Downloads metric on the Content tab', () => {
			renderTopAssets();

			fireEvent.click(
				screen.getAllByRole('combobox', {name: 'Group By'})[0]
			);

			expect(
				screen.queryByRole('option', {name: 'Downloads'})
			).toBeNull();
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

	describe('view all', () => {
		it('should not render the View All button when assets are returned', () => {
			renderTopAssets();

			expect(screen.queryByRole('button', {name: 'View All'})).toBeNull();
		});

		it('should not render the View All button when there are no assets', () => {
			mockUseRequestWith({data: {items: []}});

			renderTopAssets();

			expect(screen.queryByRole('button', {name: 'View All'})).toBeNull();
		});
	});

	describe('bottom spacing', () => {
		it('should pad the tab pane bottom when no View All footer follows the table', () => {
			const {container} = renderTopAssets();

			expect(container.querySelector('.tab-pane.active')).toHaveClass(
				'pb-4'
			);
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
	});
});
