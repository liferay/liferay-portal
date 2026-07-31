import React from 'react';
import TopCategoriesAndTags, {
	ITopCategory,
	ITopTag,
} from '../TopCategoriesAndTags';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	within,
} from '@testing-library/react';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	categories: {
		fetchAccountTopCategories: jest.fn(),
	},
	tags: {
		fetchAccountTopTags: jest.fn(),
	},
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '5', groupId: '23', id: 'acc-1'}),
}));

const mockedUseRequest = useRequest as jest.Mock;

const ACCOUNT = {accountName: 'Acme', id: 'acc-1'};

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
	buildCategory({
		downloadsMetric: {value: 3400},
		id: 'c-3',
		impressionsMetric: {value: 6000},
		name: 'Document Types',
		viewsMetric: {value: 4500},
		vocabularyName: 'Document Type',
	}),
	buildCategory({
		downloadsMetric: {value: 2900},
		id: 'c-4',
		impressionsMetric: {value: 4800},
		name: 'Employment Status',
		viewsMetric: {value: 3200},
		vocabularyName: 'Employment Type',
	}),
	buildCategory({
		downloadsMetric: {value: 1500},
		id: 'c-5',
		impressionsMetric: {value: 3100},
		name: 'Facility Status',
		viewsMetric: {value: 2000},
		vocabularyName: 'Category',
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
			render(<TopCategoriesAndTags account={ACCOUNT} />);

			expect(
				screen.getByText('TOP ASSET CATEGORIES AND TAGS')
			).toBeInTheDocument();
		});

		it('should render both tab labels', () => {
			render(<TopCategoriesAndTags account={ACCOUNT} />);

			expect(
				screen.getByRole('tab', {name: 'Category'})
			).toBeInTheDocument();
			expect(screen.getByRole('tab', {name: 'Tag'})).toBeInTheDocument();
		});

		it('should render the Group By picker with the default metric (Impressions)', () => {
			render(<TopCategoriesAndTags account={ACCOUNT} />);

			expect(screen.getAllByText('Group By').length).toBeGreaterThan(0);
			expect(screen.getAllByText('Impressions').length).toBeGreaterThan(
				0
			);
		});
	});

	describe('data rendering', () => {
		it('should render the name of every item returned', () => {
			render(<TopCategoriesAndTags account={ACCOUNT} />);

			expect(
				screen.getAllByText('Department Names').length
			).toBeGreaterThan(0);
			expect(screen.getAllByText('Specialties').length).toBeGreaterThan(
				0
			);
			expect(
				screen.getAllByText('Document Types').length
			).toBeGreaterThan(0);
			expect(
				screen.getAllByText('Employment Status').length
			).toBeGreaterThan(0);
			expect(
				screen.getAllByText('Facility Status').length
			).toBeGreaterThan(0);
		});

		it('should render the metric values for the default Impressions metric', () => {
			render(<TopCategoriesAndTags account={ACCOUNT} />);

			expect(screen.getAllByText('12K').length).toBeGreaterThan(0);
			expect(screen.getAllByText('9.5K').length).toBeGreaterThan(0);
			expect(screen.getAllByText('6K').length).toBeGreaterThan(0);
			expect(screen.getAllByText('4.8K').length).toBeGreaterThan(0);
			expect(screen.getAllByText('3.1K').length).toBeGreaterThan(0);
		});

		it('should render the Category Name and Vocabulary column headers on the Category tab', () => {
			const {container} = render(
				<TopCategoriesAndTags account={ACCOUNT} />
			);

			const tabPanel = container.querySelector(
				'.tab-pane'
			) as HTMLElement;

			expect(
				within(tabPanel).getByText('Category Name')
			).toBeInTheDocument();
			expect(
				within(tabPanel).getByText('Vocabulary')
			).toBeInTheDocument();
		});

		it('should render the vocabulary name of every category returned', () => {
			render(<TopCategoriesAndTags account={ACCOUNT} />);

			expect(screen.getAllByText('Department').length).toBeGreaterThan(0);
			expect(screen.getAllByText('Document Type').length).toBeGreaterThan(
				0
			);
			expect(
				screen.getAllByText('Employment Type').length
			).toBeGreaterThan(0);
		});

		it('should use the Tag Name header and omit the Vocabulary column on the Tag tab', () => {
			const {container} = render(
				<TopCategoriesAndTags account={ACCOUNT} />
			);

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

			const tabPanel = container.querySelector(
				'.tab-pane'
			) as HTMLElement;

			expect(within(tabPanel).getByText('Tag Name')).toBeInTheDocument();
			expect(within(tabPanel).queryByText('Vocabulary')).toBeNull();
		});
	});

	describe('group by picker', () => {
		it('should refetch with viewsMetric when the user picks Views', () => {
			render(<TopCategoriesAndTags account={ACCOUNT} />);

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

		it('should refetch with downloadsMetric when the user picks Downloads', () => {
			render(<TopCategoriesAndTags account={ACCOUNT} />);

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

	describe('tab switching', () => {
		it('should query the categories data source on initial render', () => {
			const API = jest.requireMock('shared/api');

			render(<TopCategoriesAndTags account={ACCOUNT} />);

			const firstCall = mockedUseRequest.mock.calls[0][0];

			expect(firstCall.variables.isCategory).toBe(true);

			firstCall.dataSourceFn(firstCall.variables);

			expect(API.categories.fetchAccountTopCategories).toHaveBeenCalled();
			expect(API.tags.fetchAccountTopTags).not.toHaveBeenCalled();
		});

		it('should query the tags data source when the Tag tab is clicked', () => {
			const API = jest.requireMock('shared/api');

			render(<TopCategoriesAndTags account={ACCOUNT} />);

			fireEvent.click(screen.getByRole('tab', {name: 'Tag'}));

			const lastCall =
				mockedUseRequest.mock.calls[
					mockedUseRequest.mock.calls.length - 1
				][0];

			expect(lastCall.variables.isCategory).toBe(false);

			lastCall.dataSourceFn(lastCall.variables);

			expect(API.tags.fetchAccountTopTags).toHaveBeenCalled();
			expect(
				API.categories.fetchAccountTopCategories
			).not.toHaveBeenCalled();
		});

		it('should change the request variables when switching tabs so the request refetches', () => {
			render(<TopCategoriesAndTags account={ACCOUNT} />);

			const categoryVariables =
				mockedUseRequest.mock.calls[0][0].variables;

			fireEvent.click(screen.getByRole('tab', {name: 'Tag'}));

			const tagVariables =
				mockedUseRequest.mock.calls[
					mockedUseRequest.mock.calls.length - 1
				][0].variables;

			expect(tagVariables).not.toEqual(categoryVariables);
			expect(categoryVariables.isCategory).toBe(true);
			expect(tagVariables.isCategory).toBe(false);
		});

		it('should render tag items after switching to the Tag tab', () => {
			render(<TopCategoriesAndTags account={ACCOUNT} />);

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

	describe('request shape', () => {
		it('should forward accountId, channelId, and groupId to the data source', () => {
			render(<TopCategoriesAndTags account={ACCOUNT} />);

			const firstCall = mockedUseRequest.mock.calls[0][0];

			expect(firstCall.variables.accountId).toBe('acc-1');
			expect(firstCall.variables.channelId).toBe('5');
			expect(firstCall.variables.groupId).toBe('23');
		});
	});

	describe('loading state', () => {
		it('should render the loading indicator while the request is in flight', () => {
			mockUseRequestWith({loading: true});

			const {container} = render(
				<TopCategoriesAndTags account={ACCOUNT} />
			);

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();
		});

		it('should not render rows while loading', () => {
			mockUseRequestWith({loading: true});

			render(<TopCategoriesAndTags account={ACCOUNT} />);

			expect(screen.queryByText('Department Names')).toBeNull();
		});
	});

	describe('empty state', () => {
		it('should render the categories empty message on the Category tab when no items are returned', () => {
			mockUseRequestWith({data: {items: []}});

			render(<TopCategoriesAndTags account={ACCOUNT} />);

			expect(
				screen.getAllByText('No Categories Available').length
			).toBeGreaterThan(0);
			expect(
				screen.getAllByText(
					'Categories will appear here when available.'
				).length
			).toBeGreaterThan(0);
		});

		it('should render the tags empty message on the Tag tab when no items are returned', () => {
			mockUseRequestWith({data: {items: []}});

			render(<TopCategoriesAndTags account={ACCOUNT} />);

			fireEvent.click(screen.getByRole('tab', {name: 'Tag'}));

			expect(
				screen.getAllByText('No Tags Available').length
			).toBeGreaterThan(0);
			expect(
				screen.getAllByText('Tags will appear here when available.')
					.length
			).toBeGreaterThan(0);
		});

		it('should keep tabs visible in the empty state', () => {
			mockUseRequestWith({data: {items: []}});

			render(<TopCategoriesAndTags account={ACCOUNT} />);

			expect(
				screen.getByRole('tab', {name: 'Category'})
			).toBeInTheDocument();
			expect(screen.getByRole('tab', {name: 'Tag'})).toBeInTheDocument();
		});

		it('should not render rows when no items are returned', () => {
			mockUseRequestWith({data: {items: []}});

			render(<TopCategoriesAndTags account={ACCOUNT} />);

			expect(screen.queryByText('Department Names')).toBeNull();
		});
	});

	describe('metric column', () => {
		it('should show the selected metric value in the metric column', () => {
			mockUseRequestWith({
				data: {
					items: [
						buildCategory({
							downloadsMetric: {value: 7},
							id: 'c-solo',
							impressionsMetric: {value: 999},
							name: 'Solo',
							viewsMetric: {value: 333},
						}),
					],
				},
			});

			const {container} = render(
				<TopCategoriesAndTags account={ACCOUNT} />
			);

			const tabPanel = container.querySelector(
				'.tab-pane'
			) as HTMLElement;

			expect(within(tabPanel).getAllByText('999').length).toBe(1);
		});
	});
});
