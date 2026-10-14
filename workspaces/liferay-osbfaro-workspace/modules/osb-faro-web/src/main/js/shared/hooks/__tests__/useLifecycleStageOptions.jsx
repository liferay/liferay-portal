import * as API from 'shared/api';
import {renderHook, waitFor} from '@testing-library/react';
import {useLifecycleStageOptions} from 'shared/hooks/useLifecycleStageOptions';

jest.unmock('react-dom');

describe('useLifecycleStageOptions', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('should return the stages of the lifecycle sorted by display order', async () => {
		API.lifecycle.fetchAccountLifecycles.mockReturnValueOnce(
			Promise.resolve([{id: '1'}])
		);

		API.lifecycle.fetchLifecycle.mockReturnValueOnce(
			Promise.resolve({
				stages: [
					{
						priority: 2,
						externalReferenceCode: 'erc-stage-1002',
						id: '1002',
						stageName: 'Engaged'
					},
					{
						priority: 1,
						externalReferenceCode: 'erc-stage-1001',
						id: '1001',
						stageName: 'Aware'
					}
				]
			})
		);

		const {result} = renderHook(() =>
			useLifecycleStageOptions({groupId: '123'})
		);

		await waitFor(() =>
			expect(result.current.options).toEqual([
				{label: 'Aware', value: 'erc-stage-1001'},
				{label: 'Engaged', value: 'erc-stage-1002'}
			])
		);

		expect(result.current.loading).toBe(false);
	});

	it('should return no options without a lifecycle', async () => {
		const {result} = renderHook(() =>
			useLifecycleStageOptions({groupId: '123'})
		);

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(result.current.options).toEqual([]);

		expect(API.lifecycle.fetchLifecycle).not.toHaveBeenCalled();
	});

	it('should read the default lifecycle of the channel', async () => {
		API.lifecycle.fetchAccountLifecycles.mockReturnValueOnce(
			Promise.resolve([])
		);

		renderHook(() =>
			useLifecycleStageOptions({channelId: '456', groupId: '123'})
		);

		await waitFor(() =>
			expect(API.lifecycle.fetchAccountLifecycles).toHaveBeenCalledWith({
				channelId: '456',
				groupId: '123'
			})
		);
	});
});
