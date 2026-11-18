jest.mock('shared/util/request');

import sendRequest from 'shared/util/request';
import {fetchLifecycleStageFieldValues} from '../accounts';
import {
	createLifecycle,
	deleteLifecycle,
	fetchLifecycle,
	fetchLifecycleStages,
	ILifecycleStagePayload,
	updateLifecycle,
} from '../lifecycle';

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
	});

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

	it('reads the stage field values of a lifecycle from the server', () => {
		fetchLifecycleStageFieldValues({
			accountLifecycleId: '9',
			channelId: '123',
			groupId: '23',
		});

		expect(sendRequest).toHaveBeenCalledWith({
			data: {
				accountLifecycleId: '9',
				channelId: '123',
				fieldMappingFieldName: 'lifecycleStatus',
			},
			method: 'GET',
			path: 'contacts/23/account/fds_field_values',
		});
	});

	it('deletes the lifecycle on the server', () => {
		deleteLifecycle({groupId: '23', lifecycleId: '9'});

		expect(sendRequest).toHaveBeenCalledWith({
			method: 'DELETE',
			path: 'contacts/23/account-lifecycle/9',
		});
	});
});
