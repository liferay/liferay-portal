jest.mock('shared/util/request');

jest.mock('shared/util/feature-flags', () => ({
	...jest.requireActual('shared/util/feature-flags'),
	ENABLE_LIFECYCLE_STAGES_MOCK_DATA: false,
}));

import sendRequest from 'shared/util/request';
import {fetchLifecycleStageFieldValues} from '../accounts';
import {
	createLifecycle,
	fetchLifecycle,
	fetchLifecycleStages,
	ILifecycleStagePayload,
	updateLifecycle,
} from '../lifecycle';
import {resetMockLifecycle} from 'lifecycle/utils/lifecycleMock';

const featureFlags = jest.requireMock('shared/util/feature-flags');

const buildStage = (
	stage: Partial<ILifecycleStagePayload> = {}
): ILifecycleStagePayload => ({
	accountLifecycleStageRule: {
		filterMetadata: '{}',
		filterString: '(account.annualRevenue gt 1000)',
		name: 'Hub Stage Aware Criteria',
	},
	description: 'Cold accounts',
	maxDuration: 90,
	priority: 1,
	stageName: 'Aware',
	...stage,
});

describe('Lifecycle API', () => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockClear();

		resetMockLifecycle();
	});

	afterEach(() => {
		featureFlags.ENABLE_LIFECYCLE_STAGES_MOCK_DATA = false;
	});

	describe('while the mock data flag is off', () => {
		it('reads the stage metrics from the server', () => {
			fetchLifecycleStages({groupId: '23', lifecycleId: '9'});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					method: 'GET',
					path: 'contacts/23/account-lifecycle/9/stages',
				})
			);
		});

		it('reads a lifecycle from the server', () => {
			fetchLifecycle({groupId: '23', lifecycleId: '9'});

			expect(sendRequest).toHaveBeenCalledWith({
				method: 'GET',
				path: 'contacts/23/account-lifecycle/9',
			});
		});

		it('creates a lifecycle with its description and named stages', () => {
			const stages = [buildStage()];

			createLifecycle({
				channelId: '123',
				description: 'Qualified accounts',
				groupId: '23',
				name: 'Hub',
				stages,
			});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {
					accountLifecycle: {
						description: 'Qualified accounts',
						name: 'Hub',
						stages,
					},
				},
				method: 'POST',
				path: 'contacts/23/account-lifecycle?channelId=123',
			});
		});

		it('updates a lifecycle with its description and named stages', () => {
			const stages = [buildStage({id: '4101'})];

			updateLifecycle({
				description: 'Qualified accounts',
				groupId: '23',
				lifecycleId: '9',
				name: 'Hub',
				stages,
			});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {
					accountLifecycle: {
						description: 'Qualified accounts',
						name: 'Hub',
						stages,
					},
				},
				method: 'PUT',
				path: 'contacts/23/account-lifecycle/9',
			});
		});
	});

	describe('while the mock data flag is on', () => {
		beforeEach(() => {
			featureFlags.ENABLE_LIFECYCLE_STAGES_MOCK_DATA = true;
		});

		it('reads stage metrics for every mock stage in display order', async () => {
			const lifecycle = await fetchLifecycle({
				groupId: '23',
				lifecycleId: '9',
			});

			const metrics = await fetchLifecycleStages({
				groupId: '23',
				lifecycleId: '9',
			});

			expect(sendRequest).not.toHaveBeenCalled();
			expect(
				metrics.map(({stageName}: {stageName: string}) => stageName)
			).toEqual(
				[...lifecycle.stages!]
					.sort((a, b) => a.priority - b.priority)
					.map(({stageName}) => stageName)
			);
			expect(
				metrics.every(
					(metric: object) =>
						'id' in metric &&
						'priority' in metric &&
						!('stageType' in metric)
				)
			).toBe(true);
			expect(metrics[metrics.length - 1].conversionRateToNextStage).toBe(
				null
			);
		});

		it('reads a lifecycle with named custom stages and no stage type', async () => {
			const lifecycle = await fetchLifecycle({
				groupId: '23',
				lifecycleId: '9',
			});

			expect(sendRequest).not.toHaveBeenCalled();
			expect(lifecycle.externalReferenceCode).toEqual(expect.any(String));
			expect(lifecycle.id).toEqual(expect.any(String));
			expect(lifecycle.authorName).toEqual(expect.any(String));
			expect(lifecycle.modifiedDate).toEqual(expect.any(Number));
			expect(lifecycle.stages!.length).toBeGreaterThanOrEqual(4);
			expect(
				lifecycle.stages!.every(
					(stage) => !!stage.stageName && !('stageType' in stage)
				)
			).toBe(true);
		});

		it('creates a lifecycle locally and gives new stages an id', async () => {
			const lifecycle = await createLifecycle({
				channelId: '123',
				description: 'Qualified accounts',
				groupId: '23',
				name: 'Hub',
				stages: [buildStage()],
			});

			expect(sendRequest).not.toHaveBeenCalled();
			expect(lifecycle.description).toBe('Qualified accounts');
			expect(lifecycle.name).toBe('Hub');
			expect(lifecycle.stages![0].id).toEqual(expect.any(String));
		});

		it('reads the stage field values with the id and external reference code of every mock stage', async () => {
			const lifecycle = await fetchLifecycle({
				groupId: '23',
				lifecycleId: '9',
			});

			const {items} = await fetchLifecycleStageFieldValues({
				accountLifecycleId: '9',
				channelId: '123',
				groupId: '23',
			});

			expect(sendRequest).not.toHaveBeenCalled();
			expect(items).toEqual(
				lifecycle.stages!.map(
					({externalReferenceCode, id, stageName}) => ({
						externalReferenceCode,
						id,
						stageName,
					})
				)
			);
		});

		it('keeps an update for the rest of the session as a new version', async () => {
			const previous = await fetchLifecycle({
				groupId: '23',
				lifecycleId: '9',
			});

			const updated = await updateLifecycle({
				description: 'Qualified accounts',
				groupId: '23',
				lifecycleId: '9',
				name: 'Renamed Hub',
				stages: [
					buildStage({id: '4101', stageName: 'Aware'}),
					buildStage({priority: 2, stageName: 'Qualified'}),
				],
			});

			const lifecycle = await fetchLifecycle({
				groupId: '23',
				lifecycleId: '9',
			});

			expect(sendRequest).not.toHaveBeenCalled();
			expect(updated.id).not.toBe(previous.id);
			expect(updated.externalReferenceCode).toBe(
				previous.externalReferenceCode
			);
			expect(lifecycle.id).toBe(updated.id);
			expect(lifecycle.name).toBe('Renamed Hub');
			expect(lifecycle.stages!.map(({stageName}) => stageName)).toEqual([
				'Aware',
				'Qualified',
			]);
			expect(lifecycle.stages![0].id).toBe('4101');
			expect(lifecycle.stages![1].id).toEqual(expect.any(String));
		});
	});
});
