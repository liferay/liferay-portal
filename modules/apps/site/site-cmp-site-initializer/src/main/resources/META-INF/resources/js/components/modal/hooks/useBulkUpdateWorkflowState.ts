/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMemo, useState} from 'react';

import {
	getChangeTransitions,
	getWorkflowGroups,
	getWorkflowNamesWithMultipleVersions,
} from '../../../utils/bulkUpdateWorkflowState';
import {WorkflowTaskItemData} from '../../../utils/types';

export default function useBulkUpdateWorkflowState(
	items: WorkflowTaskItemData[] = []
) {
	const [collapsedWorkflowKeys, setCollapsedWorkflowKeys] = useState<
		string[]
	>([]);
	const [deselectedTaskIds, setDeselectedTaskIds] = useState<number[]>([]);
	const [transitionNames, setTransitionNames] = useState<
		Record<string, string>
	>({});

	const workflowGroups = useMemo(() => getWorkflowGroups(items), [items]);

	const workflowNamesWithMultipleVersions = useMemo(
		() => getWorkflowNamesWithMultipleVersions(workflowGroups),
		[workflowGroups]
	);

	const changeTransitions = useMemo(
		() =>
			getChangeTransitions(
				deselectedTaskIds,
				transitionNames,
				workflowGroups
			),
		[deselectedTaskIds, transitionNames, workflowGroups]
	);

	const isCollapsed = (workflowKey: string) =>
		collapsedWorkflowKeys.includes(workflowKey);

	const setTasksSelected = (taskIds: number[], selected: boolean) =>
		setDeselectedTaskIds((previousDeselectedTaskIds) => {
			if (selected) {
				return previousDeselectedTaskIds.filter(
					(taskId) => !taskIds.includes(taskId)
				);
			}

			return Array.from(
				new Set([...previousDeselectedTaskIds, ...taskIds])
			);
		});

	const setTransitionName = (stepGroupKey: string, transitionName: string) =>
		setTransitionNames((previousTransitionNames) => ({
			...previousTransitionNames,
			[stepGroupKey]: transitionName,
		}));

	const toggleCollapsed = (workflowKey: string) =>
		setCollapsedWorkflowKeys((previousCollapsedWorkflowKeys) => {
			if (previousCollapsedWorkflowKeys.includes(workflowKey)) {
				return previousCollapsedWorkflowKeys.filter(
					(key) => key !== workflowKey
				);
			}

			return [...previousCollapsedWorkflowKeys, workflowKey];
		});

	return {
		changeTransitions,
		deselectedTaskIds,
		isCollapsed,
		setTasksSelected,
		setTransitionName,
		toggleCollapsed,
		transitionNames,
		workflowGroups,
		workflowNamesWithMultipleVersions,
	};
}
