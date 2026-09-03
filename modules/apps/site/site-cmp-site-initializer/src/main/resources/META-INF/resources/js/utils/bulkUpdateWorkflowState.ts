/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	StepGroup,
	WorkflowGroup,
	getTransitions,
	groupWorkflowTasks,
} from './groupWorkflowTasks';
import {ChangeTransition, WorkflowTaskItemData} from './types';

function compareWorkflowGroups(
	firstWorkflowGroup: WorkflowGroup,
	secondWorkflowGroup: WorkflowGroup
) {
	const nameOrder = firstWorkflowGroup.workflowDefinitionName.localeCompare(
		secondWorkflowGroup.workflowDefinitionName
	);

	if (nameOrder) {
		return nameOrder;
	}

	return firstWorkflowGroup.workflowDefinitionVersion.localeCompare(
		secondWorkflowGroup.workflowDefinitionVersion,
		undefined,
		{numeric: true}
	);
}

/**
 * Pairs every selected task with the transition chosen for its step group.
 * transitionNames is keyed by getStepGroupKey, so step groups without a chosen
 * transition and tasks in deselectedTaskIds are left out. Returns one
 * ChangeTransition per remaining task, without a comment; the comment modal
 * adds that later.
 *
 * With tasks 1, 2 and 3 in the "review" step of "Single Approver" version 1,
 * task 2 deselected and "approve" chosen for that step:
 *
 * deselectedTaskIds = [2]
 * transitionNames = {'Single Approver-1-review': 'approve'}
 *
 * returns
 *
 * [
 *     {transitionName: 'approve', workflowTaskId: 1},
 *     {transitionName: 'approve', workflowTaskId: 3},
 * ]
 */
export function getChangeTransitions(
	deselectedTaskIds: number[],
	transitionNames: Record<string, string>,
	workflowGroups: WorkflowGroup[]
): ChangeTransition[] {
	const changeTransitions: ChangeTransition[] = [];

	workflowGroups.forEach((workflowGroup) => {
		workflowGroup.stepGroups.forEach((stepGroup) => {
			const transitionName =
				transitionNames[getStepGroupKey(workflowGroup, stepGroup)];

			if (!transitionName) {
				return;
			}

			stepGroup.tasks.forEach(({embedded}) => {
				if (deselectedTaskIds.includes(embedded.id)) {
					return;
				}

				changeTransitions.push({
					transitionName,
					workflowTaskId: embedded.id,
				});
			});
		});
	});

	return changeTransitions;
}

export function getStepGroupKey(
	workflowGroup: WorkflowGroup,
	stepGroup: StepGroup
) {
	return `${getWorkflowKey(workflowGroup)}-${stepGroup.name}`;
}

export function getWorkflowGroups(items: WorkflowTaskItemData[]) {
	const transitionableItems = items.filter(
		(item) => getTransitions(item).length
	);

	return groupWorkflowTasks(transitionableItems).sort(compareWorkflowGroups);
}

export function getWorkflowKey({
	workflowDefinitionName,
	workflowDefinitionVersion,
}: WorkflowGroup) {
	return `${workflowDefinitionName}-${workflowDefinitionVersion}`;
}

export function getWorkflowNamesWithMultipleVersions(
	workflowGroups: WorkflowGroup[]
) {
	const versionCountByWorkflowName = new Map<string, number>();

	workflowGroups.forEach(({workflowDefinitionName}) => {
		versionCountByWorkflowName.set(
			workflowDefinitionName,
			(versionCountByWorkflowName.get(workflowDefinitionName) ?? 0) + 1
		);
	});

	const workflowNames = new Set<string>();

	versionCountByWorkflowName.forEach(
		(versionCount, workflowDefinitionName) => {
			if (versionCount > 1) {
				workflowNames.add(workflowDefinitionName);
			}
		}
	);

	return workflowNames;
}

export function getWorkflowTaskIds(workflowGroup: WorkflowGroup) {
	const workflowTaskIds: number[] = [];

	workflowGroup.stepGroups.forEach((stepGroup) => {
		stepGroup.tasks.forEach(({embedded}) =>
			workflowTaskIds.push(embedded.id)
		);
	});

	return workflowTaskIds;
}
