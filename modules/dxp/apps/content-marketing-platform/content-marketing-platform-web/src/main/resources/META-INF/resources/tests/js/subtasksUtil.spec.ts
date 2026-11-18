/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getDeleteTaskConfirmationMessage,
	getDoneTasksOpenSubtasksCount,
	getOpenSubtasksCount,
	getSubtasksCount,
} from '../../js/utils/subtasksUtil';

const mockGetTasks = jest.fn();

jest.mock('../../js/utils/api', () => ({
	getTasks: (...args: any[]) => mockGetTasks(...args),
}));

function mockTasks(
	...tasksPages: Array<Array<{id: number; parentId?: number; state: string}>>
) {
	tasksPages.forEach((subtasks) => {
		mockGetTasks.mockResolvedValueOnce({
			data: {
				items: subtasks.map(({id, parentId, state}) => ({
					id,
					r_cmpTaskToCMPTasks_c_cmpTaskId: parentId,
					state: {key: state, name: state},
				})),
			},
			error: null,
		});
	});
}

describe('subtasksUtil', () => {
	beforeEach(() => {
		Liferay.FeatureFlags['LPD-107678'] = true;
	});

	afterEach(() => {
		jest.clearAllMocks();

		Liferay.FeatureFlags['LPD-107678'] = false;
	});

	it('counts the open subtasks', async () => {
		mockTasks(
			[
				{id: 2, parentId: 1, state: 'done'},
				{id: 3, parentId: 1, state: 'done'},
			],
			[{id: 4, parentId: 2, state: 'notStarted'}]
		);

		expect(await getOpenSubtasksCount({id: 1, scopeKey: 'project'})).toBe(
			1
		);
	});

	it('counts the open subtasks of the tasks set to done', async () => {
		mockTasks(
			[
				{id: 1, state: 'done'},
				{id: 2, state: 'done'},
			],
			[
				{id: 4, parentId: 1, state: 'done'},
				{id: 5, parentId: 2, state: 'done'},
			],
			[{id: 6, parentId: 4, state: 'inProgress'}]
		);

		expect(
			await getDoneTasksOpenSubtasksCount([
				{id: 1, scopeKey: 'project'},
				{id: 2, scopeKey: 'project'},
				{id: 3, scopeKey: 'project'},
			])
		).toBe(1);

		expect(mockGetTasks).toHaveBeenNthCalledWith(1, {
			filter: "id in ('1','2','3') and state eq 'done'",
			scopeKey: 'project',
		});
	});

	it('counts the subtasks and their subtasks', async () => {
		mockTasks(
			[
				{id: 2, parentId: 1, state: 'done'},
				{id: 3, parentId: 1, state: 'inProgress'},
			],
			[{id: 4, parentId: 2, state: 'notStarted'}]
		);

		expect(await getSubtasksCount({id: 1, scopeKey: 'project'})).toBe(3);

		expect(mockGetTasks).toHaveBeenNthCalledWith(1, {
			filter: "r_cmpTaskToCMPTasks_c_cmpTaskId in ('1') and status ne 2",
			scopeKey: 'project',
		});
		expect(mockGetTasks).toHaveBeenNthCalledWith(2, {
			filter: "r_cmpTaskToCMPTasks_c_cmpTaskId in ('2','3') and status ne 2",
			scopeKey: 'project',
		});
	});

	it('does not request the subtasks when the feature flag is disabled', async () => {
		Liferay.FeatureFlags['LPD-107678'] = false;

		expect(await getSubtasksCount({id: 1, scopeKey: 'project'})).toBe(0);

		expect(mockGetTasks).not.toHaveBeenCalled();
	});

	it('returns the delete confirmation message for the subtask count', () => {
		expect(getDeleteTaskConfirmationMessage('Task', 0)).toBe(
			'delete-task-confirmation-body'
		);
		expect(getDeleteTaskConfirmationMessage('Task', 1)).toBe(
			'delete-task-and-one-subtask-confirmation-body'
		);
		expect(getDeleteTaskConfirmationMessage('Task', 2)).toBe(
			'delete-task-and-x-subtasks-confirmation-body'
		);
	});
});
