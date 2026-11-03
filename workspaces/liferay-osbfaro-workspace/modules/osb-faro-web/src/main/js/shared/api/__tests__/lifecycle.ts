jest.mock('shared/util/request');

jest.mock('shared/util/feature-flags', () => ({
	...jest.requireActual('shared/util/feature-flags'),
	ENABLE_LIFECYCLE_DELETE_MOCK_DATA: false,
	ENABLE_LIFECYCLE_STAGES_MOCK_DATA: false,
}));

import sendRequest from 'shared/util/request';
import {fetchLifecycleStageFieldValues} from '../accounts';
import {
	createLifecycle,
	deleteLifecycle,
	fetchLifecycle,
	fetchLifecycles,
	fetchLifecyclesPage,
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
	name: 'Aware',
	position: 1,
	...stage,
});

describe('Lifecycle API', () => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockClear();

		resetMockLifecycle();
	});

	afterEach(() => {
		featureFlags.ENABLE_LIFECYCLE_DELETE_MOCK_DATA = false;
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
						accountLifecycleStages: stages,
						description: 'Qualified accounts',
						name: 'Hub',
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
						accountLifecycleStages: stages,
						description: 'Qualified accounts',
						name: 'Hub',
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
			expect(metrics.map(({name}: {name: string}) => name)).toEqual(
				[...lifecycle.accountLifecycleStages!]
					.sort((a, b) => a.position - b.position)
					.map(({name}) => name)
			);
			expect(
				metrics.every(
					(metric: object) =>
						'id' in metric &&
						'position' in metric &&
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
			expect(
				lifecycle.accountLifecycleStages!.length
			).toBeGreaterThanOrEqual(4);
			expect(
				lifecycle.accountLifecycleStages!.every(
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
			expect(lifecycle.accountLifecycleStages![0].id).toEqual(
				expect.any(String)
			);
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
				lifecycle.accountLifecycleStages!.map(
					({externalReferenceCode, id, name}) => ({
						externalReferenceCode,
						id,
						stageName: name,
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
					buildStage({id: '4101', name: 'Aware'}),
					buildStage({name: 'Qualified', position: 2}),
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
			expect(
				lifecycle.accountLifecycleStages!.map(({name}) => name)
			).toEqual(['Aware', 'Qualified']);
			expect(lifecycle.accountLifecycleStages![0].id).toBe('4101');
			expect(lifecycle.accountLifecycleStages![1].id).toEqual(
				expect.any(String)
			);
		});
	});

	describe('deleteLifecycle', () => {
		it('deletes the lifecycle on the server', () => {
			deleteLifecycle({groupId: '23', lifecycleId: '9'});

			expect(sendRequest).toHaveBeenCalledWith({
				method: 'DELETE',
				path: 'contacts/23/account-lifecycle/9',
			});
		});

		describe('while the delete mock data flag is on', () => {
			const lifecycles = [
				{id: '8', name: 'Growth Hub'},
				{id: '9', name: 'Partner Lifecycle'},
			];

			const mockLifecyclesRequests = (items: {id: string}[]) =>
				(sendRequest as jest.Mock).mockImplementation(
					({path}: {path: string}) =>
						Promise.resolve(
							path.endsWith('/account-lifecycle')
								? {
										items,
										lastPage: 1,
										page: 1,
										pageSize: items.length,
										totalCount: items.length,
									}
								: items.find(({id}) => path.endsWith(`/${id}`))
						)
				);

			beforeEach(() => {
				featureFlags.ENABLE_LIFECYCLE_DELETE_MOCK_DATA = true;

				mockLifecyclesRequests(lifecycles);
			});

			afterEach(() => (sendRequest as jest.Mock).mockReset());

			it('resolves without sending a delete', async () => {
				await expect(
					deleteLifecycle({
						channelId: '123',
						groupId: '23',
						lifecycleId: '9',
					})
				).resolves.toBeDefined();

				expect(sendRequest).not.toHaveBeenCalledWith(
					expect.objectContaining({method: 'DELETE'})
				);
			});

			it('leaves the deleted lifecycle out of the list', async () => {
				await deleteLifecycle({groupId: '23', lifecycleId: '9'});

				const result = await fetchLifecyclesPage({
					channelId: '123',
					groupId: '23',
					page: 1,
					pageSize: 20,
				});

				expect(result.items.map(({id}) => id)).toEqual(['8']);
				expect(result.totalCount).toBe(1);
			});

			it('no longer finds the deleted lifecycle', async () => {
				await deleteLifecycle({groupId: '23', lifecycleId: '9'});

				await expect(
					fetchLifecycle({groupId: '23', lifecycleId: '9'})
				).rejects.toThrow();
			});

			describe('when the default lifecycle is deleted', () => {
				const withDefault = [
					{defaultAccountLifecycle: true, id: '7', name: 'Alpha Hub'},
					{
						defaultAccountLifecycle: false,
						id: '8',
						name: 'Growth Hub',
					},
					{
						defaultAccountLifecycle: false,
						id: '9',
						name: 'Partner Lifecycle',
					},
				];

				const fetchPage = () =>
					fetchLifecyclesPage({
						channelId: '123',
						groupId: '23',
						page: 1,
						pageSize: 20,
					});

				beforeEach(() => mockLifecyclesRequests(withDefault));

				it('makes the next lifecycle by name the default', async () => {
					await deleteLifecycle({
						channelId: '123',
						groupId: '23',
						lifecycleId: '7',
					});

					const {items} = await fetchPage();

					expect(
						items.map(({defaultAccountLifecycle, id}) => [
							id,
							defaultAccountLifecycle,
						])
					).toEqual([
						['8', true],
						['9', false],
					]);

					await expect(
						fetchLifecycles({
							channelId: '123',
							defaultAccountLifecycle: true,
							groupId: '23',
						})
					).resolves.toEqual([
						expect.objectContaining({
							defaultAccountLifecycle: true,
							id: '8',
						}),
					]);

					await expect(
						fetchLifecycle({groupId: '23', lifecycleId: '8'})
					).resolves.toEqual(
						expect.objectContaining({defaultAccountLifecycle: true})
					);
				});

				it('leaves no lifecycle after deleting the last one', async () => {
					for (const lifecycleId of ['7', '8', '9']) {
						await deleteLifecycle({
							channelId: '123',
							groupId: '23',
							lifecycleId,
						});
					}

					const {items, totalCount} = await fetchPage();

					expect(items).toEqual([]);
					expect(totalCount).toBe(0);

					await expect(
						fetchLifecycles({
							channelId: '123',
							defaultAccountLifecycle: true,
							groupId: '23',
						})
					).resolves.toEqual([]);
				});
			});

			it('keeps the list as is when nothing was deleted', async () => {
				const result = await fetchLifecyclesPage({
					channelId: '123',
					groupId: '23',
					page: 1,
					pageSize: 20,
				});

				expect(result.items.map(({id}) => id)).toEqual(['8', '9']);
				expect(result.totalCount).toBe(2);
			});
		});
	});
});
