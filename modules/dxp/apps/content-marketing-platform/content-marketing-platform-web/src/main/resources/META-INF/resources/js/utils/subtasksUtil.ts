/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {getTasks} from './api';
import {ISubtaskObjectEntry} from './types';

type Task = {
	id: number;
	scopeKey: string;
};

async function getSubtasks(
	tasks: Task[],
	scopeKey: string
): Promise<ISubtaskObjectEntry[]> {
	const subtasks = await getTasksByParentIds(
		tasks.map(({id}) => id),
		scopeKey
	);

	if (!subtasks.length) {
		return [];
	}

	return [
		...subtasks,
		...(await getTasksByParentIds(
			subtasks.map(({id}) => id),
			scopeKey
		)),
	];
}

async function getTasksByParentIds(parentIds: number[], scopeKey: string) {
	const {data} = await getTasks({
		filter: `${getIdsFilter(
			'r_cmpTaskToCMPTasks_c_cmpTaskId',
			parentIds
		)} and status ne 2`,
		scopeKey,
	});

	return data?.items ?? [];
}

async function getDoneTasks(tasks: Task[], scopeKey: string) {
	const {data} = await getTasks({
		filter: `${getIdsFilter(
			'id',
			tasks.map(({id}) => id)
		)} and state eq 'done'`,
		scopeKey,
	});

	return data?.items ?? [];
}

function getIdsFilter(fieldName: string, ids: number[]) {
	return `${fieldName} in (${ids.map((id) => `'${id}'`).join(',')})`;
}

function isOpen(subtask: ISubtaskObjectEntry) {
	return subtask.state?.key !== 'done';
}

export function getDeleteTaskConfirmationMessage(
	title: string,
	subtasksCount: number
) {
	if (subtasksCount === 1) {
		return sub(
			Liferay.Language.get(
				'delete-task-and-one-subtask-confirmation-body'
			),
			title
		);
	}

	if (subtasksCount > 1) {
		return sub(
			Liferay.Language.get(
				'delete-task-and-x-subtasks-confirmation-body'
			),
			title,
			String(subtasksCount)
		);
	}

	return sub(Liferay.Language.get('delete-task-confirmation-body'), title);
}

export async function getDoneTasksOpenSubtasksCount(tasks: Task[]) {
	if (!Liferay.FeatureFlags['LPD-107678']) {
		return 0;
	}

	const scopeKeys = [...new Set(tasks.map(({scopeKey}) => scopeKey))];

	const subtasks = (
		await Promise.all(
			scopeKeys.map(async (scopeKey) => {
				const doneTasks = await getDoneTasks(
					tasks.filter((task) => task.scopeKey === scopeKey),
					scopeKey
				);

				if (!doneTasks.length) {
					return [];
				}

				return getSubtasks(
					doneTasks.map(({id}) => ({id, scopeKey})),
					scopeKey
				);
			})
		)
	).flat();

	return new Set(subtasks.filter(isOpen).map(({id}) => id)).size;
}

export async function getOpenSubtasksCount(task: Task) {
	if (!Liferay.FeatureFlags['LPD-107678']) {
		return 0;
	}

	const subtasks = await getSubtasks([task], task.scopeKey);

	return subtasks.filter(isOpen).length;
}

export async function getSubtasksCount(task: Task) {
	if (!Liferay.FeatureFlags['LPD-107678']) {
		return 0;
	}

	const subtasks = await getSubtasks([task], task.scopeKey);

	return subtasks.length;
}
