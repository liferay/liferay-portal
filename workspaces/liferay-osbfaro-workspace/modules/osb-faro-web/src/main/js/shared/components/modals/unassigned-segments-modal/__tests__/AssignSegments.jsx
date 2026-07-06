import * as API from 'shared/api';
import AssignSegments from '../AssignSegments';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {mockSegment} from 'test/data';
import {UnassignedSegmentsContext} from 'shared/context/unassignedSegments';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	individualSegment: {
		delete: jest.fn(() => Promise.resolve()),
		updateChannel: jest.fn(() => Promise.resolve())
	}
}));

const mockedContext = {
	unassignedSegments: [
		mockSegment(1, {channelId: null}),
		mockSegment(2, {channelId: null})
	],
	unassignedSegmentsDispatch: jest.fn(),
	unassignedSegmentsTriggered: false
};

const DefaultComponent = props => (
	<MemoryRouter>
		<UnassignedSegmentsContext.Provider value={mockedContext}>
			<ChannelContext.Provider value={mockChannelContext()}>
				<AssignSegments groupId='123' onClose={jest.fn()} {...props} />
			</ChannelContext.Provider>
		</UnassignedSegmentsContext.Provider>
	</MemoryRouter>
);

describe('AssignSegments', () => {
	afterEach(() => {
		cleanup();
		jest.clearAllMocks();
	});

	it('should render', () => {
		const {container, getAllByText, getByTestId, getByText} = render(
			<DefaultComponent />
		);

		fireEvent.click(getByTestId('select-1'));

		expect(getAllByText('Unassigned').length).toBeGreaterThan(0);
		expect(getByText('Delete')).toBeTruthy();
		expect(getByText('Channel 1')).toBeTruthy();
		expect(getByText('Channel 2')).toBeTruthy();

		expect(container).toMatchSnapshot();
	});

	it('should run close from OnClose prop', () => {
		const onClose = jest.fn();
		const {getByText} = render(<DefaultComponent onClose={onClose} />);

		fireEvent.click(getByText('Skip for Now'));

		expect(onClose).toHaveBeenCalled();
	});

	it('it should enable done button when a valid value is selected', () => {
		const {getByTestId, getByText} = render(<DefaultComponent />);

		fireEvent.click(getByTestId('select-1'));

		fireEvent.click(getByText('Channel 1'));

		const button = getByTestId('submit-button');

		expect(button).not.toBeDisabled();
	});

	it('should call api functions with Channel 1 args', async () => {
		const {getByTestId, getByText} = render(<DefaultComponent />);

		fireEvent.click(getByTestId('select-1'));

		fireEvent.click(getByText('Channel 1'));

		fireEvent.click(getByTestId('submit-button'));

		await waitFor(() =>
			expect(API.individualSegment.updateChannel).toHaveBeenCalledWith({
				channelId: '1',
				groupId: '123',
				id: '1'
			})
		);
	});

	it('should call api functions with Delete args', async () => {
		const {getByTestId, getByText} = render(<DefaultComponent />);

		fireEvent.click(getByTestId('select-1'));

		fireEvent.click(getByText('Delete'));

		fireEvent.click(getByTestId('submit-button'));

		await waitFor(() =>
			expect(API.individualSegment.delete).toHaveBeenCalledWith({
				groupId: '123',
				ids: ['1']
			})
		);
	});
});
