import * as API from 'shared/api';
import {renderHook, waitFor} from '@testing-library/react';
import {useChannelLifecycles} from 'shared/hooks/useChannelLifecycles';

jest.unmock('react-dom');

describe('useChannelLifecycles', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('should return every lifecycle of the channel with its stages', async () => {
		const lifecycles = [
			{
				id: '1',
				name: 'Customer',
				accountLifecycleStages: [{externalReferenceCode: 'erc-stage-1001', id: '1001'}],
			},
			{
				id: '2',
				name: 'Partner',
				accountLifecycleStages: [{externalReferenceCode: 'erc-stage-2001', id: '2001'}],
			},
		];

		API.lifecycle.fetchChannelLifecycles.mockReturnValueOnce(
			Promise.resolve(lifecycles)
		);

		const {result} = renderHook(() =>
			useChannelLifecycles({channelId: '456', groupId: '123'})
		);

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(result.current.lifecycles).toEqual(lifecycles);

		expect(API.lifecycle.fetchChannelLifecycles).toHaveBeenCalledWith({
			channelId: '456',
			groupId: '123',
		});
	});

	it('should return no lifecycles while loading', () => {
		const {result} = renderHook(() =>
			useChannelLifecycles({channelId: '456', groupId: '123'})
		);

		expect(result.current.lifecycles).toEqual([]);
		expect(result.current.loading).toBe(true);
	});
});
