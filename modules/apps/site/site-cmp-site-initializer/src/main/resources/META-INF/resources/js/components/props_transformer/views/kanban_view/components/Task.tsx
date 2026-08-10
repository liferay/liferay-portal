/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import Card from '@clayui/card/src/Card';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {DateRenderer} from '@liferay/frontend-data-set-web';
import {AssigneeAvatar} from '@liferay/object-dynamic-data-mapping-form-field-type';
import classNames from 'classnames';
import React, {
	forwardRef,
	useCallback,
	useContext,
	useEffect,
	useRef,
} from 'react';
import {useDrag} from 'react-dnd';
import {getEmptyImage} from 'react-dnd-html5-backend';

import getTaskItemsActions from '../../../../../utils/getTaskItemsActions';
import {ITask} from '../../../../../utils/types';
import StateLabel from '../../../../StateLabel';
import {KanbanViewContext} from '../context';
import {ItemTypes} from './Column';

import './Task.scss';

export interface ITaskDragItem {
	cardWidth?: number;
	task: ITask;
	type: ItemTypes;
}

export const TaskCard = React.memo(
	forwardRef<HTMLDivElement, {isDragging?: boolean; task: ITask}>(
		({isDragging, task}, ref) => {
			const {cmpProjectObjectEntryId, itemsActions, loadData} =
				useContext(KanbanViewContext);

			return (
				<div
					className={classNames('lfr__kaban-task-card', {
						'lfr__kaban-task-card-dragging': isDragging,
					})}
					ref={ref}
				>
					<Card>
						<Card.Body>
							<Card.Row>
								<div className="lfr__kaban-task-card-row">
									<strong className="lfr__kaban-task-card-row-text-content">
										{task.embedded.title}
									</strong>

									<ClayDropDownWithItems
										items={getTaskItemsActions(
											itemsActions,
											loadData,
											task
										)}
										trigger={
											<ClayButton
												aria-label={Liferay.Language.get(
													'actions'
												)}
												className="component-action"
												displayType="unstyled"
												monospaced
											>
												<ClayIcon symbol="ellipsis-v" />
											</ClayButton>
										}
									/>
								</div>
							</Card.Row>

							<Card.Row>
								<Card.Description
									className="lfr__kaban-task-card-row-text-content"
									displayType="subtitle"
								>
									{!cmpProjectObjectEntryId
										? task.embedded.cmpProjectToCMPTasks
												.title
										: DateRenderer({
												value: task.embedded.dueDate,
											})}
								</Card.Description>
							</Card.Row>

							<Card.Row>
								<div className="lfr__kaban-task-card-row">
									<StateLabel
										dueDate={task.embedded.dueDate}
										state={{
											key: task.embedded.state.key,
											name: task.embedded.state.name,
										}}
									/>

									<div className="lfr__kaban-task-card-assignee">
										<AssigneeAvatar
											name={task.embedded.assignTo.name}
											portrait={
												task.embedded.assignTo.portrait
											}
										/>
									</div>
								</div>
							</Card.Row>
						</Card.Body>
					</Card>
				</div>
			);
		}
	)
);

TaskCard.displayName = 'TaskCard';

export default function Task(task: ITask) {
	const cardRef = useRef<HTMLDivElement | null>(null);

	const [{isDragging}, drag, preview] = useDrag<
		ITaskDragItem,
		unknown,
		{isDragging: boolean}
	>({
		begin: () => ({
			cardWidth: cardRef.current?.offsetWidth,
			task,
			type: ItemTypes.TASK,
		}),
		collect: (monitor) => ({
			isDragging: !!monitor.isDragging(),
		}),
		item: {task, type: ItemTypes.TASK},
	});

	useEffect(() => {
		preview(getEmptyImage(), {captureDraggingState: true});
	}, [preview]);

	const setCardRef = useCallback(
		(node: HTMLDivElement | null) => {
			cardRef.current = node;

			drag(node);
		},
		[drag]
	);

	return <TaskCard isDragging={isDragging} ref={setCardRef} task={task} />;
}
