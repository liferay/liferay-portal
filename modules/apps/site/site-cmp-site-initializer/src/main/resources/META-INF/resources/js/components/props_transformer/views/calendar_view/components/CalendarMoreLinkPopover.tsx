/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {ClayDropDownWithItems} from '@clayui/drop-down';
import {IItemsActions, getItemActionURL} from '@liferay/frontend-data-set-web';
import {Immutable} from '@liferay/frontend-js-state-web';
import {AssigneeAvatar} from '@liferay/object-dynamic-data-mapping-form-field-type';
import classNames from 'classnames';
import {navigate} from 'frontend-js-web';
import React, {useMemo} from 'react';

import getTaskItemsActions from '../../../../../utils/getTaskItemsActions';
import isActionsMenuEvent from '../../../../../utils/isActionsMenuEvent';
import isOverdue from '../../../../../utils/isOverdue';
import {ITaskObjectEntry} from '../../../../../utils/types';
import StateLabel from '../../../../StateLabel';
import sortTasksByPriority from '../utils/sortTasksByPriority';

import './CalendarMoreLinkPopover.scss';

function getDisplayState(task: Immutable<ITaskObjectEntry>) {
	if (isOverdue(task)) {
		return {key: 'overdue', name: Liferay.Language.get('overdue')};
	}

	return task.state;
}

interface CalendarMoreLinkPopoverProps {
	alignElement: HTMLElement;
	itemsActions: IItemsActions[];
	loadData: Function;
	onClose: () => void;
	tasks: ITaskObjectEntry[];
}

export default function CalendarMoreLinkPopover({
	alignElement,
	itemsActions,
	loadData,
	onClose,
	tasks,
}: CalendarMoreLinkPopoverProps) {
	const sortedTasks = useMemo(() => sortTasksByPriority(tasks), [tasks]);

	const handleViewTask = (task: Immutable<ITaskObjectEntry>) => {
		const viewURL = getItemActionURL(itemsActions, 'actionLink', {
			embedded: task,
		});

		if (viewURL) {
			navigate(viewURL);
		}
	};

	return (
		<ClayDropDown.Menu
			active
			alignElementRef={{current: alignElement}}
			className="lfr__cmp-calendar-more-link-popover"
			data-testid="calendarMoreLinkPopover"
			onActiveChange={onClose}
		>
			<div className="lfr__cmp-calendar-more-link-popover-tasks">
				{sortedTasks.map((task) => {
					const hasViewPermission = Boolean(task.actions?.get);

					const taskItemsActions = getTaskItemsActions(
						itemsActions,
						loadData,
						{
							actions: task.actions,
							embedded: task,
						}
					);

					return (
						<div
							className={classNames(
								'lfr__cmp-calendar-more-link-popover-task',
								{
									'lfr__cmp-calendar-more-link-popover-task-clickable':
										hasViewPermission,
								}
							)}
							key={task.id}
							onClick={
								hasViewPermission
									? (event) => {
											if (!isActionsMenuEvent(event)) {
												handleViewTask(task);
											}
										}
									: undefined
							}
							onKeyDown={
								hasViewPermission
									? (event) => {
											if (
												!isActionsMenuEvent(event) &&
												(event.key === 'Enter' ||
													event.key === ' ')
											) {
												event.preventDefault();

												handleViewTask(task);
											}
										}
									: undefined
							}
							role={hasViewPermission ? 'button' : undefined}
							tabIndex={hasViewPermission ? 0 : undefined}
						>
							<span
								className="lfr__cmp-calendar-more-link-popover-task-title"
								data-testid="calendarMoreLinkPopoverTaskTitle"
							>
								{task.title}
							</span>

							<span className="lfr__cmp-calendar-more-link-popover-task-state">
								<StateLabel state={getDisplayState(task)} />
							</span>

							<span className="lfr__cmp-calendar-more-link-popover-task-assignee">
								<AssigneeAvatar
									name={task.assignTo?.name}
									portrait={task.assignTo?.portrait}
								/>
							</span>

							{!!taskItemsActions.length && (
								<ClayDropDownWithItems
									items={taskItemsActions}
									trigger={
										<ClayButtonWithIcon
											aria-label={Liferay.Language.get(
												'actions'
											)}
											borderless
											className="component-action lfr__cmp-calendar-more-link-popover-task-actions"
											data-actions-menu
											displayType="secondary"
											size="sm"
											symbol="ellipsis-v"
										/>
									}
								/>
							)}
						</div>
					);
				})}
			</div>
		</ClayDropDown.Menu>
	);
}
