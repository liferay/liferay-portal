/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useCallback, useEffect, useRef, useState} from 'react';

import '../../../css/components/BulkActionsMonitor.scss';

import Badge from '@clayui/badge';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import DropDown from '@clayui/drop-down';
import ClayLink from '@clayui/link';
import classnames from 'classnames';
import {debounce} from 'frontend-js-web';

import AssetBulkActionTaskService from '../../common/services/AssetBulkActionTaskService';
import {
	IBulkActionTask,
	IBulkActionTaskStarter,
	IBulkActionTaskStarterDTO,
	IBulkActionTaskType,
} from '../../common/types/BulkActionTask';
import {START_TASK} from '../../common/utils/events';
import {displaySystemErrorToast} from '../../common/utils/toastUtil';
import BulkActionsMonitorItemList from './components/BulkActionsMonitorItemList';
import {BulkActionTaskStarter} from './services/BulkActionTaskStarter';
import {INTERVAL_TASK_POLLING_MS, URL_TASKS_REPORT} from './util/constants';

function BulkActionsMonitor({
	bulkActionTaskClassNameId: classNameId,
}: {
	bulkActionTaskClassNameId: number;
}) {
	const [active, setActive] = useState<boolean>(false);
	const [processingTasks, setProcessingTask] = useState(0);
	const [tasks, setTasks] = useState<IBulkActionTask[]>([]);
	const [tasksLoading, setTasksLoading] = useState<boolean>(false);

	const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

	const getTasks = useCallback(
		() =>
			debounce(async () => {
				setTasksLoading(true);

				const {data} = await AssetBulkActionTaskService.getTasks({
					pageSize: 5,
					sort: 'dateCreated:desc',
				});

				setTasks(data?.items || []);
				setTasksLoading(false);
			}, 500)(),
		[setTasks]
	);

	const onActiveChange = useCallback(
		(active: boolean) => {
			setActive(active);

			if (active) {
				getTasks();
			}
		},
		[getTasks, setActive]
	);

	const stopPolling = useCallback(() => {
		if (intervalRef.current) {
			clearInterval(intervalRef.current);

			intervalRef.current = null;
		}
	}, [intervalRef]);

	const pollProcessingTasks = useCallback(() => {
		if (intervalRef.current) {
			return;
		}

		const getProcessingTasks = async () => {
			try {
				const {data} = await AssetBulkActionTaskService.getTasks({
					filter: `executionStatus eq 'initial' or executionStatus eq 'started'`,
				});

				if (!data?.totalCount) {
					stopPolling();
				}

				setProcessingTask(data?.totalCount || 0);
			}
			catch {
				stopPolling();
			}
		};

		getProcessingTasks();

		intervalRef.current = setInterval(
			getProcessingTasks,
			INTERVAL_TASK_POLLING_MS
		);
	}, [setProcessingTask, stopPolling]);

	const postBulkAction = useCallback(
		async (
			bulkActionDTO: IBulkActionTaskStarterDTO<keyof IBulkActionTaskType>
		) => {
			const bulkAction: IBulkActionTaskStarter =
				new BulkActionTaskStarter({
					classNameId,
					...bulkActionDTO,
				});

			try {
				const response = await AssetBulkActionTaskService.createTask(
					bulkAction.payload,
					bulkAction.postURL
				);

				if (response.data) {
					bulkAction.onCreateSuccess(response);

					pollProcessingTasks();
				}

				if (response.error) {
					bulkAction.onCreateError(response);
				}
			}
			catch (error) {
				bulkAction.onCreateError(error as unknown as any);

				if (!bulkAction.overrideDefaultErrorToast) {
					displaySystemErrorToast();
				}
			}
		},
		[classNameId, pollProcessingTasks]
	);

	useEffect(() => {
		Liferay.on(START_TASK, postBulkAction);

		return () => {
			Liferay.detach(START_TASK, postBulkAction);

			stopPolling();
		};
	}, [postBulkAction, stopPolling]);

	useEffect(() => {
		getTasks();

		pollProcessingTasks();
	}, [getTasks, pollProcessingTasks]);

	return processingTasks || tasks.length ? (
		<DropDown
			active={active}
			onActiveChange={onActiveChange}
			trigger={
				<div>
					<ClayButtonWithIcon
						borderless
						className={classnames('task-status-toggle', {
							'task-status-toggle-show': !processingTasks,
						})}
						displayType="secondary"
						symbol="forms"
					/>

					{processingTasks > 0 ? (
						<ClayButton
							className={classnames(
								'task-status-toggle',
								'task-status-toggle-processing',
								'task-status-toggle-show',
								{
									'border-info text-3 text-info': !active,
									'btn-info text-3': active,
								}
							)}
							displayType="secondary"
						>
							<Badge
								className={classnames({
									'mr-2 badge-info': !active,
									'mr-2 badge-light': active,
								})}
								label={processingTasks}
							/>

							{processingTasks === 1
								? Liferay.Language.get('processing-task')
								: Liferay.Language.get('processing-tasks')}
						</ClayButton>
					) : null}
				</div>
			}
		>
			<>
				<BulkActionsMonitorItemList
					classNameId={classNameId}
					items={tasks}
				/>

				{tasksLoading ? (
					<div className="task-status-loading">
						<span className="loading-animation text-secondary" />
					</div>
				) : null}

				<ClayLink
					block
					className="btn btn-block btn-secondary task-status-view-all text-3"
					displayType="secondary"
					href={URL_TASKS_REPORT}
				>
					{Liferay.Language.get('view-all-tasks')}
				</ClayLink>
			</>
		</DropDown>
	) : null;
}

export default BulkActionsMonitor;
