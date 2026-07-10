/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {Col} from '@clayui/layout';
import {ObjectField, StateFlowValue} from '@liferay/site-cms-site-initializer';
import React, {useContext, useEffect, useState} from 'react';
import {useDrop} from 'react-dnd';

import {getStateObjectField} from '../../../../../utils/api';
import {openCMPModal} from '../../../../../utils/openCMPModal';
import {IColumn, ITask} from '../../../../../utils/types';
import StateLabel from '../../../../StateLabel';
import CreateTaskModal from '../../../../modal/CreateTaskModal';
import {KanbanViewContext} from '../context';
import Task from './Task';

import './Column.scss';

import classNames from 'classnames';

interface DragItem {
	task: ITask;
	type: ItemTypes;
}

interface IColumnProps {
	column: IColumn;
}

interface IColumnViewProps extends IColumnProps {
	stateFlow?: StateFlowValue;
}

export enum ItemTypes {
	TASK = 'KANBAN_TASK',
}

export function ColumnView({
	column: {icon, key, name, tasks},
	stateFlow,
}: IColumnViewProps) {
	const {
		changeTaskStatus,
		hasAddTaskPermission,
		loadData,
		projectId,
		projectObjectDefinitionId,
	} = useContext(KanbanViewContext);

	const canTransition = (taskStateKey: string) => {
		if (!stateFlow) {
			return false;
		}

		const state = stateFlow.objectStates.find(
			({key}) => key === taskStateKey
		);

		if (!state) {
			return false;
		}

		const {objectStateTransitions} = state;

		return objectStateTransitions.some(
			({key: transitionsKey}) => transitionsKey === key
		);
	};

	const [{canDrop, isOver}, drop] = useDrop({
		accept: ItemTypes.TASK,
		canDrop: ({task: {actions, embedded}}: DragItem) => {
			if (!actions.update) {
				return false;
			}

			const taskStateKey = embedded.state.key;

			return taskStateKey !== key && canTransition(taskStateKey);
		},
		collect: (monitor) => ({
			canDrop: !!monitor.canDrop(),
			isOver: !!monitor.isOver(),
		}),
		drop: (item) => changeTaskStatus(item.task, {key, name}),
	});

	return (
		<div className="lfr__kaban-view-column" ref={drop}>
			<Col>
				<div className="lfr__kaban-view-column-header">
					<StateLabel state={{key, name}} />

					{icon.name && (
						<ClayIcon
							style={{color: icon.color}}
							symbol={icon.name}
						/>
					)}

					<span>{tasks.length}</span>
				</div>

				<div
					className={classNames('lfr__kaban-view-column-state', {
						'lfr__kaban-view-column-state-candidate':
							!isOver && canDrop,
						'lfr__kaban-view-column-state-over': isOver && canDrop,
					})}
				>
					<div className="lfr__kaban-view-column-state-list">
						{tasks.map((task) => {
							return <Task key={task.embedded.id} {...task} />;
						})}
					</div>

					{hasAddTaskPermission && (
						<ClayButton
							borderless
							className="lfr__kaban-view-column-state-add-button"
							displayType="secondary"
							onClick={async () => {
								await openCMPModal({
									center: true,
									contentComponent: ({
										closeModal,
									}: {
										closeModal: () => void;
									}) => (
										<CreateTaskModal
											closeModal={closeModal}
											loadData={loadData}
											projectId={projectId}
											projectObjectDefinitionId={
												projectObjectDefinitionId
											}
											state={key}
										/>
									),
									size: 'md',
								});
							}}
							size="xs"
						>
							<ClayIcon symbol="plus" />

							{Liferay.Language.get('add-task')}
						</ClayButton>
					)}
				</div>
			</Col>
		</div>
	);
}

export default function Column(props: IColumnProps) {
	const [stateFlow, setStateFlow] = useState<StateFlowValue>();

	useEffect(() => {
		const makeFetch = async () => {
			const {data} = (await getStateObjectField()) as {
				data: {items: ObjectField[]};
			};

			const objectFieldSettings =
				data?.items?.[0]?.objectFieldSettings ?? [];

			const setting = objectFieldSettings!.find(
				({name}) => name === 'stateFlow'
			)!;

			const value = setting?.value as StateFlowValue;

			if (value) {
				setStateFlow(value);
			}
		};

		makeFetch();
	}, []);

	return <ColumnView {...props} stateFlow={stateFlow} />;
}
