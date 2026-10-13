jest.mock('shared/util/request');

jest.mock('shared/util/feature-flags', () => ({
	...jest.requireActual('shared/util/feature-flags'),
	ENABLE_LIFECYCLE_STAGES_MOCK_DATA: false,
}));

import sendRequest from 'shared/util/request';
import {
	createLifecycle,
	fetchLifecycle,
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
	displayOrder: 1,
	maxDuration: 90,
	name: 'Aware',
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

		it('reads a lifecycle with named custom stages and no stage type', async () => {
			const lifecycle = await fetchLifecycle({
				groupId: '23',
				lifecycleId: '9',
			});

			expect(sendRequest).not.toHaveBeenCalled();
			expect(lifecycle.id).toBe('9');
			expect(lifecycle.authorName).toEqual(expect.any(String));
			expect(lifecycle.modifiedDate).toEqual(expect.any(Number));
			expect(lifecycle.stages!.length).toBeGreaterThanOrEqual(4);
			expect(
				lifecycle.stages!.every(
					(stage) => !!stage.name && !('stageType' in stage)
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

		it('keeps an update for the rest of the session', async () => {
			await updateLifecycle({
				description: 'Qualified accounts',
				groupId: '23',
				lifecycleId: '9',
				name: 'Renamed Hub',
				stages: [
					buildStage({id: '4101', name: 'Aware'}),
					buildStage({displayOrder: 2, name: 'Qualified'}),
				],
			});

			const lifecycle = await fetchLifecycle({
				groupId: '23',
				lifecycleId: '9',
			});

			expect(sendRequest).not.toHaveBeenCalled();
			expect(lifecycle.name).toBe('Renamed Hub');
			expect(lifecycle.stages!.map(({name}) => name)).toEqual([
				'Aware',
				'Qualified',
			]);
			expect(lifecycle.stages![0].id).toBe('4101');
			expect(lifecycle.stages![1].id).toEqual(expect.any(String));
		});
	});
});
