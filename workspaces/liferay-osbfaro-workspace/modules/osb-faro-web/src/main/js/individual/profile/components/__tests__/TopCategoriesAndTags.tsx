import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import TopCategoriesAndTags from '../TopCategoriesAndTags';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {
	ITopCategory,
	ITopTag,
} from 'shared/components/TopCategoriesAndTagsBaseCard';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	categories: {
		fetchIndividualTopCategories: jest.fn(),
	},
	tags: {
		fetchIndividualTopTags: jest.fn(),
	},
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '5', groupId: '23', id: 'ind-1'}),
}));

const mockedUseRequest = useRequest as jest.Mock;

const INDIVIDUAL_ID = 'ind-1';

const MOCK_CONTEXT = {
	filters: {},
	individualId: INDIVIDUAL_ID,
	individualName: 'Jane Doe',
	router: {
		params: {channelId: '5', groupId: '23', id: INDIVIDUAL_ID},
		query: {rangeKey: RangeKeyTimeRanges.Last30Days},
	},
};

const renderTopCategoriesAndTags = () =>
	render(
		<Provider store={mockStore()}>
			<BasePage.Context.Provider value={MOCK_CONTEXT}>
				<MemoryRouter>
					<MockedProvider
						addTypename={false}
						mocks={[mockTimeRangeReq(), mockPreferenceReq()]}
					>
						<TopCategoriesAndTags individualId={INDIVIDUAL_ID} />
					</MockedProvider>
				</MemoryRouter>
			</BasePage.Context.Provider>
		</Provider>
	);

const buildCategory = (
	overrides: Partial<ITopCategory> = {}
): ITopCategory => ({
	downloadsMetric: {value: 0},
	id: 'c-1',
	impressionsMetric: {value: 0},
	name: 'Category',
	viewsMetric: {value: 0},
	vocabularyId: 'v-1',
	vocabularyName: 'Vocabulary',
	...overrides,
});

const buildTag = (overrides: Partial<ITopTag> = {}): ITopTag => ({
	downloadsMetric: {value: 0},
	id: 't-1',
	impressionsMetric: {value: 0},
	name: 'Tag',
	viewsMetric: {value: 0},
	...overrides,
});

const DEFAULT_CATEGORIES: ITopCategory[] = [
	buildCategory({
		downloadsMetric: {value: 8200},
		id: 'c-1',
		impressionsMetric: {value: 12000},
		name: 'Department Names',
		viewsMetric: {value: 9000},
		vocabularyName: 'Department',
	}),
	buildCategory({
		downloadsMetric: {value: 6700},
		id: 'c-2',
		impressionsMetric: {value: 9500},
		name: 'Specialties',
		viewsMetric: {value: 7000},
		vocabularyName: 'Specialty',
	}),
];

const mockUseRequestWith = ({
	data,
	loading = false,
}: {
	data?: {items: Array<ITopCategory | ITopTag>};
	loading?: boolean;
}) => {
	mockedUseRequest.mockImplementation(() => ({
		data,
		loading,
		refetch: jest.fn(),
	}));
};

describe('TopCategoriesAndTags', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		mockUseRequestWith({data: {items: DEFAULT_CATEGORIES}});
	});

	afterEach(cleanup);

	describe('rendering', () => {
		it('should render the card title', () => {
			renderTopCategoriesAndTags();

			expect(
				screen.getByText('TOP ASSET CATEGORIES AND TAGS')
			).toBeInTheDocument();
		});

		it('should render both tab labels', () => {
			renderTopCategoriesAndTags();

			expect(
				screen.getByRole('tab', {name: 'Category'})
			).toBeInTheDocument();
			expect(screen.getByRole('tab', {name: 'Tag'})).toBeInTheDocument();
		});

		it('should render the Group By picker with the default metric (Impressions)', () => {
			renderTopCategoriesAndTags();

			expect(screen.getAllByText('Group By').length).toBeGreaterThan(0);
			expect(screen.getAllByText('Impressions').length).toBeGreaterThan(
				0
			);
		});
	});

	describe('data rendering', () => {
		it('should render the name and the vocabulary of every item returned', () => {
			renderTopCategoriesAndTags();

			expect(
				screen.getAllByText('Department Names').length
			).toBeGreaterThan(0);
			expect(screen.getAllByText('Department').length).toBeGreaterThan(0);
			expect(screen.getAllByText('Specialties').length).toBeGreaterThan(
				0
			);
		});

		it('should render the metric values for the default Impressions metric', () => {
			renderTopCategoriesAndTags();

			expect(screen.getAllByText('12K').length).toBeGreaterThan(0);
			expect(screen.getAllByText('9.5K').length).toBeGreaterThan(0);
		});
	});

	describe('request shape', () => {
		it('should forward individualId, channelId, and groupId to the data source', () => {
			renderTopCategoriesAndTags();

			const firstCall = mockedUseRequest.mock.calls[0][0];

			expect(firstCall.variables.individualId).toBe('ind-1');
			expect(firstCall.variables.channelId).toBe('5');
			expect(firstCall.variables.groupId).toBe('23');
		});

		it('should request the items for the selected range', () => {
			renderTopCategoriesAndTags();

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

	describe('tab switching', () => {
		it('should query the categories data source on initial render', () => {
			const API = jest.requireMock('shared/api');

			renderTopCategoriesAndTags();

			const firstCall = mockedUseRequest.mock.calls[0][0];

			expect(firstCall.variables.isCategory).toBe(true);

			firstCall.dataSourceFn(firstCall.variables);

			expect(
				API.categories.fetchIndividualTopCategories
			).toHaveBeenCalled();
			expect(API.tags.fetchIndividualTopTags).not.toHaveBeenCalled();
		});

		it('should query the tags data source when the Tag tab is clicked', () => {
			const API = jest.requireMock('shared/api');

			renderTopCategoriesAndTags();

			fireEvent.click(screen.getByRole('tab', {name: 'Tag'}));

			const lastCall =
				mockedUseRequest.mock.calls[
					mockedUseRequest.mock.calls.length - 1
				][0];

			expect(lastCall.variables.isCategory).toBe(false);

			lastCall.dataSourceFn(lastCall.variables);

			expect(API.tags.fetchIndividualTopTags).toHaveBeenCalled();
			expect(
				API.categories.fetchIndividualTopCategories
			).not.toHaveBeenCalled();
		});

		it('should not send the isCategory flag to the endpoint', () => {
			const API = jest.requireMock('shared/api');

			renderTopCategoriesAndTags();

			const firstCall = mockedUseRequest.mock.calls[0][0];

			firstCall.dataSourceFn(firstCall.variables);

			expect(
				API.categories.fetchIndividualTopCategories
			).toHaveBeenCalledWith(
				expect.not.objectContaining({isCategory: expect.anything()})
			);
		});

		it('should render tag items after switching to the Tag tab', () => {
			renderTopCategoriesAndTags();

			mockUseRequestWith({
				data: {
					items: [
						buildTag({
							id: 't-promo',
							impressionsMetric: {value: 250},
							name: 'promo',
						}),
					],
				},
			});

			fireEvent.click(screen.getByRole('tab', {name: 'Tag'}));

			expect(screen.getAllByText('promo').length).toBeGreaterThan(0);
		});
	});

	describe('group by picker', () => {
		it('should refetch with downloadsMetric when the user picks Downloads', () => {
			renderTopCategoriesAndTags();

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
	});

	describe('loading state', () => {
		it('should render the loading indicator while the request is in flight', () => {
			mockUseRequestWith({loading: true});

			const {container} = renderTopCategoriesAndTags();

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();
		});
	});

	describe('empty state', () => {
		it('should render the categories empty state on the Category tab', () => {
			mockUseRequestWith({data: {items: []}});

			renderTopCategoriesAndTags();

			expect(
				screen.getAllByText('No Categories Available').length
			).toBeGreaterThan(0);
		});

		it('should render the tags empty state on the Tag tab', () => {
			mockUseRequestWith({data: {items: []}});

			renderTopCategoriesAndTags();

			fireEvent.click(screen.getByRole('tab', {name: 'Tag'}));

			expect(
				screen.getAllByText('No Tags Available').length
			).toBeGreaterThan(0);
		});
	});
});
