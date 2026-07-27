import * as API from 'shared/api';
import FilterBySegment from '../FilterBySegment';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter, Route} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockSegmentPageViewsReq} from 'test/graphql-data';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const MOCK_SEGMENT = (id: string, name: string) => ({
	id,
	name,
	segmentType: 'BATCH',
	state: 'READY',
	status: 'ACTIVE',
});

const Wrapper = ({
	children,
	mocks = [],
}: {
	children: React.ReactNode;
	mocks?: any[];
}) => (
	<MemoryRouter
		initialEntries={[
			'/workspace/123/456/sites/touchpoints/http%3A%2F%2Fliferay.com/Liferay%20DXP%20-%20Home',
		]}
	>
		<Route path="/workspace/:groupId/:channelId/sites/touchpoints/:touchpoint/:title">
			<MockedProvider addTypename={false} mocks={mocks}>
				{children}
			</MockedProvider>
		</Route>
	</MemoryRouter>
);

describe('FilterBySegment', () => {
	afterEach(cleanup);

	it('should render', async () => {
		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('123', 'Viewed Page')],
				total: 1,
			})
		);

		const segmentPageViews = [{segmentId: '123', views: 100}];
		const mocks = [mockSegmentPageViewsReq({segmentPageViews})];

		const {container} = render(
			<Wrapper mocks={mocks}>
				<FilterBySegment
					onFilterChange={jest.fn()}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('Create Segment')).toHaveClass('btn-secondary');
		expect(container).toMatchSnapshot();
	});

	it('should open dropdown w/ no segments empty state', async () => {
		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [],
				total: 0,
			})
		);

		const {container} = render(
			<Wrapper>
				<FilterBySegment
					onFilterChange={jest.fn()}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('Create Segment')).toHaveClass('btn-primary');

		fireEvent.click(screen.getByText('Filter'));

		expect(screen.getByText('Filter By Segment')).toBeInTheDocument();
		expect(screen.getByText('There are no segments.')).toBeInTheDocument();
	});

	it('should open dropdown w/ a list of segments', async () => {
		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [
					MOCK_SEGMENT('123', 'Viewed Page'),
					MOCK_SEGMENT('456', 'Viewed Form'),
					MOCK_SEGMENT('789', 'Viewed Web Content'),
				],
				total: 3,
			})
		);

		const segmentPageViews = [
			{segmentId: '123', views: 100},
			{segmentId: '456', views: 100},
			{segmentId: '789', views: 100},
		];
		const mocks = [mockSegmentPageViewsReq({segmentPageViews})];

		const {container} = render(
			<Wrapper mocks={mocks}>
				<FilterBySegment
					onFilterChange={jest.fn()}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(screen.getByText('Filter'));

		expect(screen.getByText('Viewed Page')).toBeInTheDocument();
		expect(screen.getByText('Viewed Form')).toBeInTheDocument();
		expect(screen.getByText('Viewed Web Content')).toBeInTheDocument();
	});

	it('should open dropdown w/ no segments found empty state', async () => {
		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('123', 'Viewed Page')],
				total: 1,
			})
		);

		const segmentPageViews = [{segmentId: '123', views: 100}];
		const mocks = [mockSegmentPageViewsReq({segmentPageViews})];

		const {container} = render(
			<Wrapper mocks={mocks}>
				<FilterBySegment
					onFilterChange={jest.fn()}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(screen.getByText('Filter'));

		expect(screen.getByText('Viewed Page')).toBeInTheDocument();

		fireEvent.change(screen.getByRole('textbox'), {
			target: {value: 'Viewed Form'},
		});

		expect(screen.getByText('No results were found.')).toBeInTheDocument();
	});

	it('should open dropdown w/ segments and select one of them', async () => {
		const onFilterChange = jest.fn();

		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('123', 'Viewed Page')],
				total: 1,
			})
		);

		const segmentPageViews = [{segmentId: '123', views: 100}];
		const mocks = [mockSegmentPageViewsReq({segmentPageViews})];

		const {container} = render(
			<Wrapper mocks={mocks}>
				<FilterBySegment
					onFilterChange={onFilterChange}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(screen.getByText('Filter'));

		fireEvent.click(screen.getByText('Viewed Page'));

		expect(onFilterChange).toHaveBeenCalledWith(
			expect.objectContaining({id: '123', name: 'Viewed Page'})
		);

		expect(container.querySelector('.label')).toBeInTheDocument();
	});

	it('should open dropdown w/ segments, select one of them, and then, remove filter', async () => {
		const onFilterChange = jest.fn();

		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('123', 'Viewed Page')],
				total: 1,
			})
		);

		const segmentPageViews = [{segmentId: '123', views: 100}];
		const mocks = [mockSegmentPageViewsReq({segmentPageViews})];

		const {container} = render(
			<Wrapper mocks={mocks}>
				<FilterBySegment
					onFilterChange={onFilterChange}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(screen.getByText('Filter'));

		fireEvent.click(screen.getByText('Viewed Page'));

		expect(onFilterChange).toHaveBeenCalledWith(
			expect.objectContaining({id: '123', name: 'Viewed Page'})
		);

		fireEvent.click(screen.getByTitle('Remove Filter'));

		expect(container.querySelector('.label')).not.toBeInTheDocument();

		expect(onFilterChange).toHaveBeenCalledWith(null);
	});

	it('should open dropdown w/ segment disabled if there are no views', async () => {
		const onFilterChange = jest.fn();

		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('123', 'Viewed Page')],
				total: 1,
			})
		);

		const segmentPageViews = [{segmentId: '123', views: 0}];
		const mocks = [mockSegmentPageViewsReq({segmentPageViews})];

		const {container} = render(
			<Wrapper mocks={mocks}>
				<FilterBySegment
					onFilterChange={onFilterChange}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(screen.getByText('Filter'));

		const viewedPageSegment = screen.getByText('Viewed Page');

		expect(viewedPageSegment.closest('button')).toBeDisabled();

		fireEvent.click(viewedPageSegment);

		expect(onFilterChange).not.toHaveBeenCalled();
	});
});
