/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {WorkflowTaskItemData} from './types';

const TRANSITION_ACTION_PREFIX = 'workflow_';

interface Transition {
	label: string;
	name: string;
}

interface StepGroup {
	label: string;
	name: string;
	tasks: WorkflowTaskItemData[];
	transitions: Transition[];
}

interface WorkflowGroup {
	stepGroups: StepGroup[];
	taskCount: number;
	workflowDefinitionName: string;
	workflowDefinitionVersion: string;
}

export function getTransitions(item: WorkflowTaskItemData): Transition[] {
	return Object.entries(item.embedded.actions || {})
		.filter(([key]) => key.startsWith(TRANSITION_ACTION_PREFIX))
		.map(([key, action]) => {
			const name =
				action.name || key.slice(TRANSITION_ACTION_PREFIX.length);

			return {
				label: action.label || name,
				name,
			};
		})
		.sort((a, b) => a.label.localeCompare(b.label));
}

export function groupWorkflowTasks(
	items: WorkflowTaskItemData[]
): WorkflowGroup[] {
	const workflowGroups = new Map<string, WorkflowGroup>();

	(items || []).forEach((item) => {
		const {label, name, workflowDefinitionName, workflowDefinitionVersion} =
			item.embedded;

		const key = `${workflowDefinitionName}\0${workflowDefinitionVersion}`;

		let workflowGroup = workflowGroups.get(key);

		if (!workflowGroup) {
			workflowGroup = {
				stepGroups: [],
				taskCount: 0,
				workflowDefinitionName,
				workflowDefinitionVersion,
			};

			workflowGroups.set(key, workflowGroup);
		}

		workflowGroup.taskCount += 1;

		let stepGroup = workflowGroup.stepGroups.find(
			(stepGroup) => stepGroup.name === name
		);

		if (!stepGroup) {
			stepGroup = {
				label,
				name,
				tasks: [],
				transitions: getTransitions(item),
			};

			workflowGroup.stepGroups.push(stepGroup);
		}

		stepGroup.tasks.push(item);
	});

	return Array.from(workflowGroups.values());
}
