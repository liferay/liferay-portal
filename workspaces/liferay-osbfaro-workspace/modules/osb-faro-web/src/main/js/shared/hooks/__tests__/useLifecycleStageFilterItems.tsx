import * as API from 'shared/api';
import {renderHook, waitFor} from '@testing-library/react';
import {useLifecycleStageFilterItems} from 'shared/hooks/useLifecycleStageFilterItems';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	accounts: {
		fetchLifecycleStageFieldValues: jest.fn(),
	},
}));

const mockedFetchLifecycleStageFieldValues = API.accounts
	.fetchLifecycleStageFieldValues as jest.Mock;

describe('useLifecycleStageFilterItems', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it.each([
		{
			expectedLabel: 'Prospect',
			stage: {
				externalReferenceCode: 'erc-stage-1001',
				id: '1001',
				stageName: 'Prospect',
				stageType: 'AWARE',
			},
		},
		{
			expectedLabel: 'Aware',
			stage: {
				externalReferenceCode: 'erc-stage-1001',
				id: '1001',
				stageType: 'AWARE',
			},
		},
		{
			expectedLabel: 'erc-stage-1001',
			stage: {externalReferenceCode: 'erc-stage-1001', id: '1001'},
		},
	])(
		'should label a stage of one lifecycle as $expectedLabel',
		async ({expectedLabel, stage}) => {
			mockedFetchLifecycleStageFieldValues.mockResolvedValueOnce({
				items: [stage],
			});

			const {result} = renderHook(() =>
				useLifecycleStageFilterItems({
					accountLifecycleId: '1',
					channelId: '456',
					groupId: '123',
				})
			);

			await waitFor(() => expect(result.current.loading).toBe(false));

			expect(result.current.items).toEqual([
				{label: expectedLabel, value: 'erc-stage-1001'},
			]);
			expect(result.current.lifecycleStages).toEqual([stage]);

			expect(mockedFetchLifecycleStageFieldValues).toHaveBeenCalledWith(
				expect.objectContaining({
					accountLifecycleId: '1',
					channelId: '456',
					groupId: '123',
				})
			);
		}
	);

	it('should label the stages of every lifecycle with their lifecycle name', async () => {
		mockedFetchLifecycleStageFieldValues.mockResolvedValueOnce({
			items: [
				{
					externalReferenceCode: 'erc-stage-1001',
					id: '1001',
					lifecycleName: 'Customer',
					name: 'Aware',
				},
				{
					externalReferenceCode: 'erc-stage-2001',
					id: '2001',
					lifecycleName: 'Partner',
					name: 'Engaged',
				},
			],
		});

		const {result} = renderHook(() =>
			useLifecycleStageFilterItems({
				allLifecycles: true,
				channelId: '456',
				groupId: '123',
			})
		);

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(result.current.items).toEqual([
			{label: 'Customer: Aware', value: 'erc-stage-1001'},
			{label: 'Partner: Engaged', value: 'erc-stage-2001'},
		]);

		expect(mockedFetchLifecycleStageFieldValues).toHaveBeenCalledWith(
			expect.objectContaining({
				accountLifecycleId: undefined,
				channelId: '456',
				groupId: '123',
			})
		);
	});

	it('should not fetch the stages without a lifecycle', () => {
		const {result} = renderHook(() =>
			useLifecycleStageFilterItems({channelId: '456', groupId: '123'})
		);

		expect(result.current.items).toEqual([]);
		expect(result.current.lifecycleStages).toEqual([]);

		expect(mockedFetchLifecycleStageFieldValues).not.toHaveBeenCalled();
	});
});
