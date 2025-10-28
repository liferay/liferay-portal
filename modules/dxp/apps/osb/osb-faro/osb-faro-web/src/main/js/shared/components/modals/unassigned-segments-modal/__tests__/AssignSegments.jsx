import * as API from 'shared/api';
import AssignSegments from '../AssignSegments';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {
	cleanup,
	fireEvent,
	render,
	waitForDomChange
} from '@testing-library/react';
import {mockChannelContext} from 'test/mock-channel-context';
import {mockSegment} from 'test/data';
import {noop} from 'lodash';
import {StaticRouter} from 'react-router-dom';
import {UnassignedSegmentsContext} from 'shared/context/unassignedSegments';

jest.unmock('react-dom');

const mockedContext = {
	unassignedSegments: [
		mockSegment(1, {channelId: null}),
		mockSegment(2, {channelId: null})
	],
	unassignedSegmentsDispatch: jest.fn(),
	unassignedSegmentsTriggered: false
};

const DefaultComponent = props => (
	<UnassignedSegmentsContext.Provider value={mockedContext}>
		<ChannelContext.Provider value={mockChannelContext()}>
			<StaticRouter>
				<AssignSegments groupId='123' onClose={noop} {...props} />
			</StaticRouter>
		</ChannelContext.Provider>
	</UnassignedSegmentsContext.Provider>
);

describe('AssignSegments', () => {
	afterEach(() => {
		cleanup();
		jest.useRealTimers();
	});

	it('should render', () => {
		jest.useFakeTimers();
		const {container, getByTestId, getByText} = render(
			<DefaultComponent />
		);
		fireEvent.click(getByTestId('select-1'));

		jest.runAllTimers();

		expect(getByText('Unassigned')).toBeTruthy();
		expect(getByText('Delete')).toBeTruthy();
		expect(getByText('Channel 1')).toBeTruthy();
		expect(getByText('Channel 2')).toBeTruthy();

		expect(container).toMatchSnapshot();
	});

	it('should run close from OnClose prop', async () => {
		const spy = jest.fn();

		const {getByTestId, getByText} = render(
			<DefaultComponent onClose={spy} />
		);

		fireEvent.click(getByText('Skip for Now'));

		fireEvent.click(getByTestId('submit-button'));

		expect(spy).toBeCalled();
	});

	it('it should enable done button when a valid value is selected', async () => {
		const {getByTestId, getByText} = render(<DefaultComponent />);

		fireEvent.click(getByTestId('select-1'));

		jest.runAllTimers();

		fireEvent.click(getByText('Channel 1'));

		const button = getByTestId('submit-button');

		expect(button).not.toBeDisabled();
	});

	it('should call api functions with Channel 1 args', async () => {
		const {container, getByTestId, getByText} = render(
			<DefaultComponent />
		);

		fireEvent.click(getByTestId('select-1'));

		jest.runAllTimers();

		fireEvent.click(getByText('Channel 1'));

		fireEvent.click(getByTestId('submit-button'));

		await waitForDomChange(container.querySelector('.assign-segments'));

		expect(API.individualSegment.updateChannel).toBeCalledWith({
			channelId: '1',
			groupId: '123',
			id: '1'
		});
	});

	it('should call api functions with Delete args', async () => {
		const {container, getByTestId, getByText} = render(
			<DefaultComponent />
		);

		fireEvent.click(getByTestId('select-1'));

		jest.runAllTimers();

		fireEvent.click(getByText('Delete'));

		fireEvent.click(getByTestId('submit-button'));

		await waitForDomChange(container.querySelector('.assign-segments'));

		expect(API.individualSegment.delete).toBeCalledWith({
			groupId: '123',
			ids: ['1']
		});
	});
});
