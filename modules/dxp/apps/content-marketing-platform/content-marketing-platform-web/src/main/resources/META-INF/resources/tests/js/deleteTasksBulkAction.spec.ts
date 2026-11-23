/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ApiHelper,
	deleteAssetEntriesBulkAction,
} from '@liferay/site-cms-site-initializer';

import deleteTasksBulkAction from '../../js/utils/deleteTasksBulkAction';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	ApiHelper: {get: jest.fn()},
	deleteAssetEntriesBulkAction: jest.fn(),
}));

const liferayLanguageGet = Liferay.Language.get;

function getTask(id: number, scopeKey = 'scope1') {
	return {embedded: {id, scopeKey}};
}

function getTitle() {
	const {getCustomBulkDeleteMessage, selectedData} = (
		deleteAssetEntriesBulkAction as jest.Mock
	).mock.calls[0][0];

	return getCustomBulkDeleteMessage(selectedData).title;
}

function mockSubtaskIds(...subtaskIdsList: number[][]) {
	subtaskIdsList.forEach((subtaskIds) =>
		(ApiHelper.get as jest.Mock).mockResolvedValueOnce({
			data: {items: subtaskIds.map((id) => ({id}))},
		})
	);
}

function runDeleteTasksBulkAction(
	tasks: Array<ReturnType<typeof getTask>>,
	selectAll = false
) {
	return deleteTasksBulkAction({
		apiURL: '/o/search/v1.0/search',
		dataSetId: 'tasks',
		selectedData: {items: tasks, selectAll} as any,
	});
}

describe('deleteTasksBulkAction', () => {
	beforeAll(() => {
		Liferay.Language.get = (key: string) =>
			key === 'delete-x-tasks'
				? 'Delete {0} Tasks'
				: liferayLanguageGet(key);
	});

	afterAll(() => {
		Liferay.Language.get = liferayLanguageGet;
	});

	beforeEach(() => {
		jest.clearAllMocks();

		Liferay.FeatureFlags['LPD-107678'] = true;
	});

	afterEach(() => {
		Liferay.FeatureFlags['LPD-107678'] = false;
	});

	it('counts a subtask selected with its parent task once', async () => {
		mockSubtaskIds([2]);

		await runDeleteTasksBulkAction([getTask(1), getTask(2)]);

		expect(getTitle()).toBe('Delete 2 Tasks');
	});

	it('counts only the selected tasks when LPD-107678 is disabled', async () => {
		Liferay.FeatureFlags['LPD-107678'] = false;

		await runDeleteTasksBulkAction([getTask(1), getTask(2)]);

		expect(ApiHelper.get).not.toHaveBeenCalled();
		expect(getTitle()).toBe('Delete 2 Tasks');
	});

	it('counts the subtasks of every level', async () => {
		mockSubtaskIds([2], [3], []);

		await runDeleteTasksBulkAction([getTask(1)]);

		expect(getTitle()).toBe('Delete 3 Tasks');
	});

	it('ignores a new delete while the tasks are being counted', async () => {
		mockSubtaskIds([]);

		await Promise.all([
			runDeleteTasksBulkAction([getTask(1)]),
			runDeleteTasksBulkAction([getTask(1)]),
		]);

		expect(ApiHelper.get).toHaveBeenCalledTimes(1);
		expect(deleteAssetEntriesBulkAction).toHaveBeenCalledTimes(1);
	});

	it('looks up the subtasks in the scope of each task', async () => {
		mockSubtaskIds([], []);

		await runDeleteTasksBulkAction([
			getTask(1, 'scope1'),
			getTask(2, 'scope2'),
		]);

		expect(ApiHelper.get).toHaveBeenCalledWith(
			expect.stringContaining('/o/cmp/tasks/scopes/scope1?')
		);
		expect(ApiHelper.get).toHaveBeenCalledWith(
			expect.stringContaining('/o/cmp/tasks/scopes/scope2?')
		);
		expect(getTitle()).toBe('Delete 2 Tasks');
	});

	it('shows the delete all title when every task is selected', async () => {
		await runDeleteTasksBulkAction([getTask(1), getTask(2)], true);

		expect(ApiHelper.get).not.toHaveBeenCalled();
		expect(getTitle()).toBe('delete-all-tasks');
	});

	it('shows the single task title when the task has no subtasks', async () => {
		mockSubtaskIds([]);

		await runDeleteTasksBulkAction([getTask(1)]);

		expect(getTitle()).toBe('delete-task');
	});
});
