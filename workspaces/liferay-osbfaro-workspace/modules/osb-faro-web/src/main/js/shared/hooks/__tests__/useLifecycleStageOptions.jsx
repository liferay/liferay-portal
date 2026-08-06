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
					{displayOrder: 2, id: '1002', stageType: 'ENGAGED'},
					{displayOrder: 1, id: '1001', stageType: 'AWARE'}
				]
			})
		);

		const {result} = renderHook(() =>
			useLifecycleStageOptions({groupId: '123'})
		);

		await waitFor(() =>
			expect(result.current.options).toEqual([
				{label: 'Aware', value: '1001'},
				{label: 'Engaged', value: '1002'}
			])
		);

		expect(result.current.loading).toBe(false);
	});

	it('should return the stage type of an unknown stage', async () => {
		API.lifecycle.fetchAccountLifecycles.mockReturnValueOnce(
			Promise.resolve([{id: '1'}])
		);

		API.lifecycle.fetchLifecycle.mockReturnValueOnce(
			Promise.resolve({
				stages: [{displayOrder: 1, id: '1001', stageType: 'CONVERTED'}]
			})
		);

		const {result} = renderHook(() =>
			useLifecycleStageOptions({groupId: '123'})
		);

		await waitFor(() =>
			expect(result.current.options).toEqual([
				{label: 'CONVERTED', value: '1001'}
			])
		);
	});

	it('should return no options without a lifecycle', async () => {
		const {result} = renderHook(() =>
			useLifecycleStageOptions({groupId: '123'})
		);

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(result.current.options).toEqual([]);

		expect(API.lifecycle.fetchLifecycle).not.toHaveBeenCalled();
	});
});
