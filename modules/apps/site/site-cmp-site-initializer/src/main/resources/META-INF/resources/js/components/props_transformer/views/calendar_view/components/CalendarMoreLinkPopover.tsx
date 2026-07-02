/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {ClayDropDownWithItems} from '@clayui/drop-down';
import {IItemsActions} from '@liferay/frontend-data-set-web';
import {Immutable} from '@liferay/frontend-js-state-web';
import {AssigneeAvatar} from '@liferay/object-dynamic-data-mapping-form-field-type';
import classNames from 'classnames';
import {navigate} from 'frontend-js-web';
import React, {useMemo} from 'react';

import getActionURL from '../../../../../utils/getActionURL';
import getTaskItemsActions from '../../../../../utils/getTaskItemsActions';
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

/**
 * A popover row is clickable to view the task, but it also hosts the actions
 * kebab. Its dropdown menu is rendered in a portal, so its clicks still bubble
 * to the row through the React tree. Skip viewing the task when the event comes
 * from the kebab button or its menu (anything outside the row element).
 *
 * A "stopPropagation" on the kebab trigger does not solve this. ClayDropDown
 * clones the trigger and overrides its "onClick" with its own toggle handler,
 * so the trigger's "stopPropagation" is not guaranteed to run. And even when it
 * does, the menu is portaled: a menu item lives outside the row in the DOM but
 * is still a React descendant, so its click bubbles to the row's "onClick"
 * through the React tree, which "stopPropagation" on the trigger never sees.
 */
function isActionsMenuEvent(event: React.SyntheticEvent) {
	const target = event.target as HTMLElement;

	return (
		!event.currentTarget.contains(target) ||
		Boolean(target.closest('[data-actions-menu]'))
	);
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
		const viewURL = getActionURL({
			actionId: 'actionLink',
			itemsActions,
			task: {embedded: task},
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
