/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {
	FrontendDataSet,
	IInternalRenderer,
	IItemsActions,
} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import {getWorkflowTasksAssignedToMe} from '../../common/services/WorkflowService';
import {WorkflowTask} from '../../common/types/WorkflowTask';
import {FDS_EVENT_ACTION_PERFORMED} from '../../common/utils/constants';
import getSectionHref, {
	toFDSConfigFilters,
} from '../dashboard/governance/getSectionHref';
import WorkflowTaskRenderer from '../props_transformer/cell_renderers/WorkflowTaskRenderer';
import {onWorkflowTaskActionClick} from '../tasks/onWorkflowTaskActionClick';
import {TASK_QUICK_FILTER_UPDATES} from '../tasks/taskQuickFilterUpdates';

const MAX_WORKFLOW_TASKS = 8;

const VIEWS = [
	{
		contentRenderer: 'table',
		label: 'Table',
		name: 'table',
		schema: {
			fields: [
				{
					contentRenderer: 'workflowTaskTableCellRenderer',
					fieldName: 'renderer',
					label: Liferay.Language.get('renderer'),
				},
			],
		},
		style: 'fluid',
		thumbnail: 'table',
	},
];

type AssigneeFilter = {id: string; value: number | string};

type WorkflowTaskItem = WorkflowTask & {embedded: WorkflowTask};

function getTasksHref(
	assigneeFilter: AssigneeFilter,
	tasksSectionFDSName: string
) {
	return getSectionHref('tasks', tasksSectionFDSName, {
		filters: [
			{
				id: assigneeFilter.id,
				selectedData: {
					exclude: false,
					selectedItems: [
						{
							label: Liferay.ThemeDisplay.getUserName(),
							value: assigneeFilter.value,
						},
					],
				},
			},
			...toFDSConfigFilters(TASK_QUICK_FILTER_UPDATES.pending()),
		],
	});
}

export default function ViewWorkflowTasks({
	assigneeFilter,
	fdsActionDropdownItems,
	id,
	objectDefinitions,
	tasksSectionFDSName,
	workflowTaskURL,
}: {
	assigneeFilter: AssigneeFilter;
	fdsActionDropdownItems: IItemsActions[];
	id: string;
	objectDefinitions: any[];
	tasksSectionFDSName: string;
	workflowTaskURL: string;
}) {
	const [workflowTasks, setWorkflowTasks] = useState<WorkflowTaskItem[]>([]);

	const getWorkflowTasks = useCallback(async () => {
		try {
			const {items} = await getWorkflowTasksAssignedToMe({
				objectDefinitions,
				page: 1,
				pageSize: MAX_WORKFLOW_TASKS,
			});

			setWorkflowTasks(
				items.map((item) => {
					const workflowTask = {...item, workflowTaskURL};

					return {...workflowTask, embedded: workflowTask};
				})
			);
		}
		catch (error) {
			setWorkflowTasks([]);
		}
	}, [objectDefinitions, workflowTaskURL]);

	useEffect(() => {
		getWorkflowTasks();
	}, [getWorkflowTasks]);

	useEffect(() => {
		const handleActionPerformed = (event?: {id?: string}) => {
			if (event?.id === id) {
				getWorkflowTasks();
			}
		};

		Liferay.on(FDS_EVENT_ACTION_PERFORMED, handleActionPerformed);

		return () => {
			Liferay.detach(FDS_EVENT_ACTION_PERFORMED, handleActionPerformed);
		};
	}, [getWorkflowTasks, id]);

	return (
		<div className="container-fluid-max p-2 p-sm-3">
			<div className="align-items-center d-flex justify-content-between mb-4">
				<span
					aria-level={2}
					className="font-weight-semi-bold text-4"
					role="heading"
				>
					{Liferay.Language.get('my-tasks')}
				</span>

				<ClayLink
					aria-label={sub(
						Liferay.Language.get('view-all-x'),
						Liferay.Language.get('my-tasks')
					)}
					borderless
					className="font-weight-semi-bold text-3"
					href={getTasksHref(assigneeFilter, tasksSectionFDSName)}
					small
				>
					{Liferay.Language.get('view-all')}
				</ClayLink>
			</div>

			<div className="cms-fds-fluid cms-home-my-tasks cms-section custom-empty-state table-valign-top">
				<FrontendDataSet
					customRenderers={{
						tableCell: [
							{
								component: WorkflowTaskRenderer,
								name: 'workflowTaskTableCellRenderer',
								type: 'internal',
							} as IInternalRenderer,
						],
					}}
					emptyState={{
						description: Liferay.Language.get(
							'there-are-no-tasks-assigned-to-you'
						),
						image: '/states/cms_empty_state_tasks.svg',
						title: Liferay.Language.get('no-tasks-yet'),
					}}
					hideManagementBarInEmptyState
					id={id}
					items={workflowTasks}
					itemsActions={fdsActionDropdownItems}
					onActionDropdownItemClick={(
						props: Parameters<typeof onWorkflowTaskActionClick>[0]
					) =>
						onWorkflowTaskActionClick({
							...props,
							loadData: getWorkflowTasks,
						})
					}
					showManagementBar={false}
					showPagination={false}
					showSearch={false}
					showSelectAll={false}
					views={VIEWS}
				/>
			</div>
		</div>
	);
}
