/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RequestResult} from '@liferay/site-cms-site-initializer';
import {useCallback, useEffect, useMemo, useState} from 'react';

import {
	KANBAN_COLUMN_ORDER,
	mapStateKeyToDisplayType,
	mapStateKeyToIcon,
	mapStateKeyToLabel,
} from '../../../../../utils/constants';
import {displayStateSuccessToast} from '../../../../../utils/toastUtil';
import {IColumn, ITask, ITaskObjectEntry} from '../../../../../utils/types';

function mapByStateCode(items: ITask[]): {[key: string]: IColumn} {
	const boardData: {[name: string]: IColumn} = {};

	KANBAN_COLUMN_ORDER.forEach((stateKey) => {
		boardData[stateKey] = {
			displayType: mapStateKeyToDisplayType[stateKey],
			icon: mapStateKeyToIcon[stateKey],
			key: stateKey,
			name: mapStateKeyToLabel[stateKey],
			tasks: [],
		};
	});

	items.forEach((item) => {
		const key = item.embedded?.state?.key;

		if (key && boardData[key]) {
			boardData[key].tasks.push(item);
		}
	});

	return boardData;
}

interface PendingMove {
	cmpTaskObjectEntryId: number | string;
	newStatus: {
		key: string;
		name: string;
	};
}

export function useOptimisticBoard(
	tasks: ITask[],
	onTaskMoveApi: (
		task: ITask,
		newStatus: {key: string; name: string}
	) => Promise<RequestResult<ITaskObjectEntry>>
) {

	// Source of truth

	const [serverTasks, setServerTasks] = useState<ITask[]>(tasks);

	useEffect(() => {
		setServerTasks(tasks);
	}, [tasks]);

	const [pendingMoves, setPendingMoves] = useState<PendingMove[]>([]);

	// View Layer: Merges Server Data + Pending Moves

	const boardData = useMemo(() => {
		const mergedItems = serverTasks.map((serverTask) => {
			const pendingMove = pendingMoves.find(
				(pendingMove) =>
					pendingMove.cmpTaskObjectEntryId === serverTask.embedded.id
			);

			if (pendingMove) {

				// Return a modified clone of the task for display only

				return {
					...serverTask,
					embedded: {
						...serverTask.embedded,
						state: pendingMove.newStatus,
					},
				};
			}

			return serverTask;
		});

		return mapByStateCode(mergedItems);
	}, [serverTasks, pendingMoves]);

	const moveTask = useCallback(
		async (task: ITask, newStatus: {key: string; name: string}) => {
			const pendingMoveOperation: PendingMove = {
				cmpTaskObjectEntryId: task.embedded.id,
				newStatus,
			};

			setPendingMoves((prevPendingMoves) => [
				...prevPendingMoves,
				pendingMoveOperation,
			]);

			try {
				const {error} = await onTaskMoveApi(task, newStatus);

				if (error) {
					throw new Error(error);
				}

				setServerTasks((previousServerTasks) =>
					previousServerTasks.map((serverTask) => {
						if (serverTask.embedded.id === task.embedded.id) {
							return {
								...serverTask,
								embedded: {
									...serverTask.embedded,
									state: newStatus,
								},
							};
						}

						return serverTask;
					})
				);

				displayStateSuccessToast();
			}
			catch (error) {

				// No need to manually revert data; removing the pending move below does it.

				Liferay.Util.openToast({
					message: error,
					type: 'danger',
				});
			}
			finally {
				setPendingMoves((prevPendingMoves) =>
					prevPendingMoves.filter(
						(pendingMove) => pendingMove !== pendingMoveOperation
					)
				);
			}
		},
		[onTaskMoveApi]
	);

	return {boardData, moveTask};
}
