import * as API from 'shared/api';
import * as data from 'test/data';
import React from 'react';
import {ChannelContext, ChannelProvider} from 'shared/context/channel';
import {renderHook, waitFor} from '@testing-library/react';
import {useChannels} from 'shared/hooks/useChannels';

const CHANNELS = [
	data.mockChannel(1, 0, {id: '1'}),
	data.mockChannel(2, 0, {id: '2'}),
];

const wrapper = ({children}) => (
	<ChannelProvider>{children}</ChannelProvider>
);

const renderRecordingHook = (props, options = {wrapper}) => {
	const renders = [];

	const hook = renderHook(
		({channelId}) => {
			const channels = useChannels({channelId, groupId: '23'});

			const context = React.useContext(ChannelContext);

			renders.push({
				loading: channels.loading,
				selectedChannel: context.selectedChannel,
			});

			return {channels, context};
		},
		{initialProps: props, ...options}
	);

	return {...hook, renders};
};

describe('useChannels', () => {
	beforeEach(() => {
		API.channels.fetchAll
			.mockReset()
			.mockReturnValue(Promise.resolve({items: CHANNELS}));
	});

	it('selects the channel from the URL and fills the channel context', async () => {
		const {result} = renderRecordingHook({channelId: '2'});

		await waitFor(() =>
			expect(result.current.channels.loading).toBe(false)
		);

		expect(result.current.channels.channel).toEqual(CHANNELS[1]);
		expect(result.current.context.channels).toEqual(CHANNELS);
		expect(result.current.context.selectedChannel).toEqual(CHANNELS[1]);
	});

	it('falls back to the first channel when the URL has none', async () => {
		const {result} = renderRecordingHook({});

		await waitFor(() =>
			expect(result.current.channels.loading).toBe(false)
		);

		expect(result.current.context.selectedChannel).toEqual(CHANNELS[0]);
	});

	it('stays loading until it fills the channel context', async () => {
		const {renders, result} = renderRecordingHook({channelId: '2'});

		await waitFor(() =>
			expect(result.current.channels.loading).toBe(false)
		);

		expect(
			renders
				.filter(({loading}) => !loading)
				.every(({selectedChannel}) => selectedChannel === CHANNELS[1])
		).toBe(true);
	});

	it('stays loading until it clears the channel context of a workspace without channels', async () => {
		API.channels.fetchAll.mockReturnValue(Promise.resolve({items: []}));

		const {renders, result} = renderRecordingHook(
			{},
			{
				wrapper: ({children}) => (
					<ChannelProvider selectedChannel={CHANNELS[0]}>
						{children}
					</ChannelProvider>
				)
			}
		);

		await waitFor(() =>
			expect(result.current.channels.loading).toBe(false)
		);

		expect(
			renders
				.filter(({loading}) => !loading)
				.every(({selectedChannel}) => selectedChannel === null)
		).toBe(true);
	});

	it('does not report loading again when the URL switches to another channel', async () => {
		const {renders, rerender, result} = renderRecordingHook({
			channelId: '1'
		});

		await waitFor(() =>
			expect(result.current.channels.loading).toBe(false)
		);

		renders.length = 0;

		rerender({channelId: '2'});

		await waitFor(() =>
			expect(result.current.context.selectedChannel).toEqual(
				CHANNELS[1]
			)
		);

		expect(renders.some(({loading}) => loading)).toBe(false);
	});

	it('reloads the channels once when the URL channel is missing from them', async () => {
		const newChannel = data.mockChannel(3, 0, {id: '3'});

		API.channels.fetchAll
			.mockReturnValueOnce(Promise.resolve({items: CHANNELS}))
			.mockReturnValueOnce(
				Promise.resolve({items: [...CHANNELS, newChannel]})
			);

		const {result} = renderRecordingHook({channelId: '3'});

		await waitFor(() =>
			expect(result.current.context.selectedChannel).toEqual(
				newChannel
			)
		);

		expect(result.current.channels.loading).toBe(false);
		expect(API.channels.fetchAll).toHaveBeenCalledTimes(2);
	});

	it('reloads the channels only once when the URL channel stays missing', async () => {
		const {result} = renderRecordingHook({channelId: '999'});

		await waitFor(() =>
			expect(API.channels.fetchAll).toHaveBeenCalledTimes(2)
		);

		await waitFor(() =>
			expect(result.current.channels.loading).toBe(false)
		);

		expect(API.channels.fetchAll).toHaveBeenCalledTimes(2);
	});

	it('reports an error when the channels request fails', async () => {
		API.channels.fetchAll.mockReturnValue(
			Promise.reject(new Error('failed'))
		);

		const {result} = renderRecordingHook({channelId: '1'});

		await waitFor(() => expect(result.current.channels.error).toBe(true));
	});
});
