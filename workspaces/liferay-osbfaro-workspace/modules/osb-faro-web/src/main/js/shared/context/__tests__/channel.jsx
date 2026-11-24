import React from 'react';
import {
	ActionType,
	ChannelContext,
	ChannelProvider,
	channelReducer,
	useChannelContext
} from '../channel';
import {cleanup, render} from '@testing-library/react';
import {mockChannel} from 'test/data';
import {mockChannelContext} from 'test/mock-channel-context';

const initialState = {
	channels: [],
	selectedChannel: null
};

describe('channelsReducer', () => {
	it('should return selectedChannel state with added item', () => {
		const channel = mockChannel();

		const {selectedChannel} = channelReducer(initialState, {
			payload: channel,
			type: ActionType.setSelectedChannel
		});

		expect(selectedChannel).toBe(channel);
	});

	it('should return channels state with added item', () => {
		const channel = mockChannel();

		const {channels} = channelReducer(initialState, {
			payload: [channel],
			type: ActionType.setChannels
		});

		expect(channels).toContain(channel);
	});
});

describe('ChannelProvider', () => {
	afterEach(cleanup);

	it('should allow an initial context value to be set through the channel prop', () => {
		const successMsg = 'has channel intialized in context!';

		const ChildComponent = () => {
			const {selectedChannel} = useChannelContext();

			return selectedChannel && successMsg;
		};

		const channel = mockChannel(1);

		const {container} = render(
			<ChannelProvider selectedChannel={channel}>
				<ChildComponent />
			</ChannelProvider>
		);

		expect(container).toHaveTextContent(successMsg);
	});
});

describe('useChannelContext', () => {
	afterEach(cleanup);

	it('should return context', () => {
		const successMsg = 'has channel context!';
		const ChildComponent = () => {
			const {channelDispatch, channels, selectedChannel} =
				useChannelContext();

			return channels && selectedChannel && channelDispatch && successMsg;
		};

		const {container} = render(
			<ChannelContext.Provider value={mockChannelContext()}>
				<ChildComponent />
			</ChannelContext.Provider>
		);

		expect(container).toHaveTextContent(successMsg);
	});
});
