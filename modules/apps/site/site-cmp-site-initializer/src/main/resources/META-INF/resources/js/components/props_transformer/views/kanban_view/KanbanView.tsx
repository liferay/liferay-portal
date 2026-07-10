/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FrontendDataSetContext,
	IItemsActions,
} from '@liferay/frontend-data-set-web';
import React, {useCallback, useContext, useEffect} from 'react';

import {patchTaskById} from '../../../../utils/api';
import {ITask} from '../../../../utils/types';
import {UPDATE_TASKS_QUICK_FILTER_VISIBILITY} from '../../../task/TasksQuickFilters';
import Board from './components/Board';
import {KanbanViewContext} from './context';
import {useOptimisticBoard} from './hooks/useOptimisticBoard';

interface KanbanViewProps {
	hasAddTaskPermission: boolean;
	items: ITask[];
	itemsActions: IItemsActions[];
	projectId: string;
	projectObjectDefinitionId: number;
}

function KanbanView(props: KanbanViewProps) {
	const {loadData} = useContext(FrontendDataSetContext);

	const handleApiCall = useCallback(
		async (task: ITask, newStatus: {key: string; name: string}) => {
			return await patchTaskById({
				body: {state: newStatus.key},
				taskId: String(task.embedded.id),
			});
		},
		[]
	);

	const {boardData, moveTask} = useOptimisticBoard(
		props.items,
		handleApiCall
	);

	const changeTaskStatus = useCallback(
		(task: ITask, newStatus: {key: string; name: string}) => {
			moveTask(task, newStatus);
		},
		[moveTask]
	);

	useEffect(() => {
		Liferay.fire(UPDATE_TASKS_QUICK_FILTER_VISIBILITY, {visible: false});

		return () => {
			Liferay.fire(UPDATE_TASKS_QUICK_FILTER_VISIBILITY, {visible: true});
		};
	}, []);

	return (
		<KanbanViewContext.Provider
			value={{
				boardData,
				changeTaskStatus,
				hasAddTaskPermission: props.hasAddTaskPermission,
				itemsActions: props.itemsActions,
				loadData,
				projectId: props.projectId,
				projectObjectDefinitionId: props.projectObjectDefinitionId,
			}}
		>
			<Board />
		</KanbanViewContext.Provider>
	);
}

export default KanbanView;
