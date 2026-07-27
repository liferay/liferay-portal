import * as API from 'shared/api';
import React from 'react';
import SegmentDropdown from '../SegmentDropdown';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import {MemoryRouter, Route} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockSegmentPageViewsReq} from 'test/graphql-data';
import {RangeKeyTimeRanges} from 'shared/util/constants';

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

const openPicker = async () => {
	const trigger = screen.getByRole('combobox', {name: 'Filter By Segments'});

	// The trigger is disabled while its request is in flight.

	await waitFor(() => expect(trigger).toBeEnabled());

	fireEvent.click(trigger);
};

describe('SegmentDropdown', () => {
	afterEach(cleanup);

	it('should render with "All Segments" as the default value', async () => {
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
				<SegmentDropdown
					onFilterChange={jest.fn()}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		expect(
			screen.getByRole('combobox', {name: 'Filter By Segments'})
		).toHaveTextContent('All Segments');

		await waitFor(() =>
			expect(API.individualSegment.search).toHaveBeenCalled()
		);

		expect(container).toMatchSnapshot();
	});

	it('should list the fetched segments when opened', async () => {
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

		render(
			<Wrapper mocks={mocks}>
				<SegmentDropdown
					onFilterChange={jest.fn()}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitFor(() =>
			expect(API.individualSegment.search).toHaveBeenCalled()
		);

		await openPicker();

		expect(
			await screen.findByRole('option', {name: 'Viewed Page'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'Viewed Form'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'Viewed Web Content'})
		).toBeInTheDocument();
	});

	it('should call onFilterChange with the selected segment', async () => {
		const onFilterChange = jest.fn();

		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('123', 'Viewed Page')],
				total: 1,
			})
		);

		const segmentPageViews = [{segmentId: '123', views: 100}];
		const mocks = [mockSegmentPageViewsReq({segmentPageViews})];

		render(
			<Wrapper mocks={mocks}>
				<SegmentDropdown
					onFilterChange={onFilterChange}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitFor(() =>
			expect(API.individualSegment.search).toHaveBeenCalled()
		);

		await openPicker();

		const viewedPageOption = await screen.findByRole('option', {
			name: 'Viewed Page',
		});

		await waitFor(() => expect(viewedPageOption).not.toBeDisabled());

		fireEvent.click(viewedPageOption);

		expect(onFilterChange).toHaveBeenCalledWith(
			expect.objectContaining({id: '123', name: 'Viewed Page'})
		);
	});

	it('should call onFilterChange with null when "All Segments" is selected again', async () => {
		const onFilterChange = jest.fn();

		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('123', 'Viewed Page')],
				total: 1,
			})
		);

		const segmentPageViews = [{segmentId: '123', views: 100}];
		const mocks = [mockSegmentPageViewsReq({segmentPageViews})];

		render(
			<Wrapper mocks={mocks}>
				<SegmentDropdown
					onFilterChange={onFilterChange}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitFor(() =>
			expect(API.individualSegment.search).toHaveBeenCalled()
		);

		await openPicker();

		const viewedPageOption = await screen.findByRole('option', {
			name: 'Viewed Page',
		});

		await waitFor(() => expect(viewedPageOption).not.toBeDisabled());

		fireEvent.click(viewedPageOption);

		await openPicker();

		fireEvent.click(
			await screen.findByRole('option', {name: 'All Segments'})
		);

		expect(onFilterChange).toHaveBeenCalledWith(null);
	});

	it('should disable segments with no views for the current page', async () => {
		const onFilterChange = jest.fn();

		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('123', 'Viewed Page')],
				total: 1,
			})
		);

		const segmentPageViews = [{segmentId: '123', views: 0}];
		const mocks = [mockSegmentPageViewsReq({segmentPageViews})];

		render(
			<Wrapper mocks={mocks}>
				<SegmentDropdown
					onFilterChange={onFilterChange}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: '',
					}}
				/>
			</Wrapper>
		);

		await waitFor(() =>
			expect(API.individualSegment.search).toHaveBeenCalled()
		);

		await openPicker();

		const viewedPageOption = await screen.findByRole('option', {
			name: 'Viewed Page',
		});

		await waitFor(() => expect(viewedPageOption).toBeDisabled());

		fireEvent.click(viewedPageOption);

		expect(onFilterChange).not.toHaveBeenCalled();
	});
});
