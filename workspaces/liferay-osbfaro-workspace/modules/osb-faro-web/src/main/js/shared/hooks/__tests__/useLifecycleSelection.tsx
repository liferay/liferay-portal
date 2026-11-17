import {act, renderHook} from '@testing-library/react';
import {useChannelLifecycles} from 'shared/hooks/useChannelLifecycles';
import {useLifecycleSelection} from 'shared/hooks/useLifecycleSelection';

jest.unmock('react-dom');

jest.mock('shared/hooks/useChannelLifecycles', () => ({
	useChannelLifecycles: jest.fn(),
}));

const mockedUseChannelLifecycles = useChannelLifecycles as jest.Mock;

describe('useLifecycleSelection', () => {
	it.each([
		{
			expectedLifecycleId: 'al-2',
			lifecycles: [
				{id: 'al-1', name: 'Customer'},
				{defaultAccountLifecycle: true, id: 'al-2', name: 'Partner'},
			],
		},
		{
			expectedLifecycleId: 'al-1',
			lifecycles: [
				{id: 'al-1', name: 'Customer'},
				{id: 'al-2', name: 'Partner'},
			],
		},
		{
			expectedLifecycleId: undefined,
			lifecycles: [],
		},
	])(
		'should select $expectedLifecycleId before the user picks a lifecycle',
		({expectedLifecycleId, lifecycles}) => {
			mockedUseChannelLifecycles.mockReturnValue({
				lifecycles,
				loading: false,
			});

			const {result} = renderHook(() =>
				useLifecycleSelection({channelId: '123', groupId: '23'})
			);

			expect(result.current.selectedLifecycleId).toBe(
				expectedLifecycleId
			);
		}
	);

	it('should select the lifecycle the user picks', () => {
		mockedUseChannelLifecycles.mockReturnValue({
			lifecycles: [
				{defaultAccountLifecycle: true, id: 'al-1', name: 'Customer'},
				{id: 'al-2', name: 'Partner'},
			],
			loading: false,
		});

		const {result} = renderHook(() =>
			useLifecycleSelection({channelId: '123', groupId: '23'})
		);

		act(() => result.current.setSelectedLifecycleId('al-2'));

		expect(result.current.selectedLifecycleId).toBe('al-2');
	});

	it('should load the lifecycles of the channel', () => {
		mockedUseChannelLifecycles.mockReturnValue({
			lifecycles: [],
			loading: true,
		});

		const {result} = renderHook(() =>
			useLifecycleSelection({channelId: '123', groupId: '23'})
		);

		expect(mockedUseChannelLifecycles).toHaveBeenCalledWith({
			channelId: '123',
			groupId: '23',
		});
		expect(result.current.loading).toBe(true);
	});
});
