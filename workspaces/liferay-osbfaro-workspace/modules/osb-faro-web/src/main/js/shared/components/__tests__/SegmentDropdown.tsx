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

jest.unmock('react-dom');

const MOCK_SEGMENT = (id: string, name: string) => ({
	id,
	name,
});

const Wrapper = ({children}: {children: React.ReactNode}) => (
	<MemoryRouter initialEntries={['/workspace/123/456/sites']}>
		<Route path="/workspace/:groupId/:channelId/sites">{children}</Route>
	</MemoryRouter>
);

// Opening the picker is what triggers the segments request.

const openPicker = () => {
	fireEvent.click(screen.getByRole('combobox', {name: 'Filter By Segments'}));
};

describe('SegmentDropdown', () => {
	afterEach(cleanup);

	// The api mock is shared across the suite, so a test that opens the picker
	// would otherwise leave calls behind for the next one to trip over.

	beforeEach(() => {
		(API.individualSegment.search as jest.Mock).mockClear();
	});

	it('should render with "All Segments" as the default value', async () => {
		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('100', 'Segment 100')],
				total: 1,
			})
		);

		const {container} = render(
			<Wrapper>
				<SegmentDropdown onFilterChange={jest.fn()} />
			</Wrapper>
		);

		expect(
			screen.getByRole('combobox', {name: 'Filter By Segments'})
		).toHaveTextContent('All Segments');

		expect(container).toMatchSnapshot();
	});

	it('should not search segments until the picker is opened', async () => {
		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('100', 'Segment 100')],
				total: 1,
			})
		);

		render(
			<Wrapper>
				<SegmentDropdown onFilterChange={jest.fn()} />
			</Wrapper>
		);

		expect(API.individualSegment.search).not.toHaveBeenCalled();

		openPicker();

		await waitFor(() =>
			expect(API.individualSegment.search).toHaveBeenCalledTimes(1)
		);
	});

	it('should list the fetched segments when opened', async () => {
		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [
					MOCK_SEGMENT('100', 'Segment 100'),
					MOCK_SEGMENT('200', 'Segment 200'),
				],
				total: 2,
			})
		);

		render(
			<Wrapper>
				<SegmentDropdown onFilterChange={jest.fn()} />
			</Wrapper>
		);

		openPicker();

		await waitFor(() =>
			expect(API.individualSegment.search).toHaveBeenCalled()
		);

		expect(
			await screen.findByRole('option', {name: 'Segment 100'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'Segment 200'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'All Segments'})
		).toBeInTheDocument();
	});

	it('should call onFilterChange with the selected segment', async () => {
		const onFilterChange = jest.fn();

		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('100', 'Segment 100')],
				total: 1,
			})
		);

		render(
			<Wrapper>
				<SegmentDropdown onFilterChange={onFilterChange} />
			</Wrapper>
		);

		openPicker();

		await waitFor(() =>
			expect(API.individualSegment.search).toHaveBeenCalled()
		);

		fireEvent.click(
			await screen.findByRole('option', {name: 'Segment 100'})
		);

		expect(onFilterChange).toHaveBeenCalledWith(
			expect.objectContaining({id: '100', name: 'Segment 100'})
		);
	});

	it('should call onFilterChange with null when "All Segments" is selected again', async () => {
		const onFilterChange = jest.fn();

		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('100', 'Segment 100')],
				total: 1,
			})
		);

		render(
			<Wrapper>
				<SegmentDropdown onFilterChange={onFilterChange} />
			</Wrapper>
		);

		openPicker();

		await waitFor(() =>
			expect(API.individualSegment.search).toHaveBeenCalled()
		);

		fireEvent.click(
			await screen.findByRole('option', {name: 'Segment 100'})
		);

		openPicker();

		fireEvent.click(
			await screen.findByRole('option', {name: 'All Segments'})
		);

		expect(onFilterChange).toHaveBeenCalledWith(null);
	});

	it('should preselect the segment passed via initialSegmentId/initialSegmentName', () => {
		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('200', 'Segment 200')],
				total: 1,
			})
		);

		render(
			<Wrapper>
				<SegmentDropdown
					initialSegmentId="100"
					initialSegmentName="Segment 100"
					onFilterChange={jest.fn()}
				/>
			</Wrapper>
		);

		expect(
			screen.getByRole('combobox', {name: 'Filter By Segments'})
		).toHaveTextContent('Segment 100');
	});

	it('should fall back to the raw id when initialSegmentName is missing', () => {
		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({items: [], total: 0})
		);

		render(
			<Wrapper>
				<SegmentDropdown
					initialSegmentId="100"
					onFilterChange={jest.fn()}
				/>
			</Wrapper>
		);

		expect(
			screen.getByRole('combobox', {name: 'Filter By Segments'})
		).toHaveTextContent('100');
	});

	it('should search segments channel-wide, sorted by name', async () => {
		(API.individualSegment.search as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_SEGMENT('100', 'Segment 100')],
				total: 1,
			})
		);

		render(
			<Wrapper>
				<SegmentDropdown onFilterChange={jest.fn()} />
			</Wrapper>
		);

		openPicker();

		await waitFor(() =>
			expect(API.individualSegment.search).toHaveBeenCalledWith(
				expect.objectContaining({
					channelId: '456',
					groupId: '123',
					orderIOMap: expect.anything(),
				})
			)
		);
	});

	it('should render the caller-supplied items instead of fetching, when items is provided', () => {
		render(
			<Wrapper>
				<SegmentDropdown
					items={[MOCK_SEGMENT('100', 'Segment 100')]}
					onFilterChange={jest.fn()}
				/>
			</Wrapper>
		);

		expect(API.individualSegment.search).not.toHaveBeenCalled();

		openPicker();

		expect(
			screen.getByRole('option', {name: 'Segment 100'})
		).toBeInTheDocument();
	});
});
