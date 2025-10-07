/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getPaginatedList from '../../main_view/home/util/getPaginatedList';
import {AssignableUser} from '../types/AssignableUser';
import {Workflow} from '../types/Workflow';
import {WorkflowTask} from '../types/WorkflowTask';
import ApiHelper from './ApiHelper';

export async function getWorkflowDefinitions(): Promise<Workflow[]> {
	return await ApiHelper.getAll<Workflow>({
		url: '/o/headless-admin-workflow/v1.0/workflow-definitions',
	});
}

export async function getWorkflowTasksAssignedToMe({
	objectDefinitions,
	page,
	pageSize,
}: {
	objectDefinitions: any[];
	page: number;
	pageSize: number;
}): Promise<{items: WorkflowTask[]; totalCount: number}> {
	let fetchUrl =
		'/o/headless-admin-workflow/v1.0/workflow-tasks/assigned-to-me?nestedFields=workflowLogs&';

	fetchUrl =
		fetchUrl +
		new URLSearchParams({
			page: String(-1),
			pageSize: String(-1),
		}).toString();

	const {data, error} = await ApiHelper.get<{
		items: WorkflowTask[];
		totalCount: number;
	}>(fetchUrl);

	if (data) {
		const assetTypes = objectDefinitions.map(
			(item) => item.label[item.defaultLanguageId]
		);

		const filteredWorkflowTasks = data.items.filter(
			(item) =>
				!item.completed &&
				item.name === 'review' &&
				assetTypes.includes(item.objectReviewed.assetType)
		);

		const transformedWorkflowTasks = filteredWorkflowTasks.map(
			(workflowTask) => {
				const workflowLogs = workflowTask.workflowLogs.filter(
					(item) => item.type === 'TaskAssign'
				);

				return {
					...workflowTask,
					assignedDate: workflowLogs[0].dateCreated,
					auditUser: workflowLogs[0].auditPerson.name,
					auditUserImageURL: workflowLogs[0].auditPerson.image,
				};
			}
		);

		transformedWorkflowTasks.sort((a, b) => {
			const dateA = new Date(a.assignedDate);
			const dateB = new Date(b.assignedDate);

			return dateB.getTime() - dateA.getTime();
		});

		return {
			items: getPaginatedList({
				delta: pageSize,
				items: transformedWorkflowTasks,
				page,
			}),
			totalCount: transformedWorkflowTasks.length,
		};
	}

	throw new Error(error);
}

export async function getWorkflowTasksAssignedToMyRoles({
	objectDefinitions,
	page,
	pageSize,
}: {
	objectDefinitions: any[];
	page: number;
	pageSize: number;
}): Promise<{items: WorkflowTask[]; totalCount: number}> {
	let fetchUrl =
		'/o/headless-admin-workflow/v1.0/workflow-tasks/assigned-to-my-roles?nestedFields=workflowLogs&';

	fetchUrl =
		fetchUrl +
		new URLSearchParams({
			page: String(-1),
			pageSize: String(-1),
		}).toString();

	const {data, error} = await ApiHelper.get<{
		items: WorkflowTask[];
		totalCount: number;
	}>(fetchUrl);

	if (data) {
		const assetTypes = objectDefinitions.map(
			(item) => item.label[item.defaultLanguageId]
		);

		const filteredWorkflowTasks = data.items.filter(
			(item) =>
				!item.completed &&
				item.name === 'review' &&
				assetTypes.includes(item.objectReviewed.assetType)
		);

		const transformedWorkflowTasks = filteredWorkflowTasks.map(
			(workflowTask) => {
				const workflowLogs = workflowTask.workflowLogs.filter(
					(item) => item.type === 'TaskAssign'
				);

				return {
					...workflowTask,
					assignedDate: workflowLogs[0].dateCreated,
					auditUser: workflowLogs[0].auditPerson.name,
					auditUserImageURL: workflowLogs[0].auditPerson.image,
				};
			}
		);

		transformedWorkflowTasks.sort((a, b) => {
			const dateA = new Date(a.assignedDate);
			const dateB = new Date(b.assignedDate);

			return dateB.getTime() - dateA.getTime();
		});

		return {
			items: getPaginatedList({
				delta: pageSize,
				items: transformedWorkflowTasks,
				page,
			}),
			totalCount: transformedWorkflowTasks.length,
		};
	}

	throw new Error(error);
}

export async function getAssignableUsers(
	workflowTaskId: number
): Promise<AssignableUser[]> {
	const {data, error} = await ApiHelper.get<{items: AssignableUser[]}>(
		`/o/headless-admin-workflow/v1.0/workflow-tasks/${workflowTaskId}/assignable-users`
	);

	if (data) {
		return data.items;
	}

	throw new Error(error);
}

export async function assignToMe({
	comment,
	workflowTaskId,
}: {
	comment: string;
	workflowTaskId: number;
}) {
	return await ApiHelper.post<WorkflowTask>(
		`/o/headless-admin-workflow/v1.0/workflow-tasks/${workflowTaskId}/assign-to-me`,
		{
			comment,
			workflowTaskId,
		}
	);
}

export async function assignToUser({
	assigneeId,
	comment,
	workflowTaskId,
}: {
	assigneeId: number;
	comment: string;
	workflowTaskId: number;
}) {
	return await ApiHelper.post<WorkflowTask>(
		`/o/headless-admin-workflow/v1.0/workflow-tasks/${workflowTaskId}/assign-to-user`,
		{
			assigneeId,
			comment,
			workflowTaskId,
		}
	);
}

export async function updateDueDate({
	comment,
	dueDate,
	workflowTaskId,
}: {
	comment: string;
	dueDate: string;
	workflowTaskId: number;
}) {
	return await ApiHelper.post<WorkflowTask>(
		`/o/headless-admin-workflow/v1.0/workflow-tasks/${workflowTaskId}/update-due-date`,
		{
			comment,
			dueDate,
			workflowTaskId,
		}
	);
}

export async function transitionWorkflowState({
	comment,
	transitionName,
	workflowTaskId,
}: {
	comment: string;
	transitionName: string;
	workflowTaskId: number;
}) {
	return await ApiHelper.post<WorkflowTask>(
		`/o/headless-admin-workflow/v1.0/workflow-tasks/${workflowTaskId}/change-transition`,
		{
			comment,
			transitionName,
			workflowTaskId,
		}
	);
}
