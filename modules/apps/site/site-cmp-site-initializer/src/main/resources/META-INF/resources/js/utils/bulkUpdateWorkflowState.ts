/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getTransitions,
	groupWorkflowTasks,
	StepGroup,
	WorkflowGroup,
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

export function getChangeTransitions(
	deselectedTaskIds: number[],
	transitionNames: Record<string, string>,
	workflowGroups: WorkflowGroup[]
) {
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

export function getWorkflowTaskIds(workflowGroup: WorkflowGroup) {
	const workflowTaskIds: number[] = [];

	workflowGroup.stepGroups.forEach((stepGroup) => {
		stepGroup.tasks.forEach(({embedded}) =>
			workflowTaskIds.push(embedded.id)
		);
	});

	return workflowTaskIds;
}
