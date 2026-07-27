/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDropDown from '@clayui/drop-down';
import {IItemsActions, getItemActionURL} from '@liferay/frontend-data-set-web';
import {Immutable} from '@liferay/frontend-js-state-web';
import {AssigneeAvatar} from '@liferay/object-dynamic-data-mapping-form-field-type';
import {navigate} from 'frontend-js-web';
import React, {useMemo} from 'react';

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
	onClose: () => void;
	tasks: ITaskObjectEntry[];
}

export default function CalendarMoreLinkPopover({
	alignElement,
	itemsActions,
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
			<ClayDropDown.ItemList className="lfr__cmp-calendar-more-link-popover-tasks">
				{sortedTasks.map((task) => {
					const hasViewPermission = Boolean(task.actions?.get);

					return (
						<ClayDropDown.Item
							className="lfr__cmp-calendar-more-link-popover-task"
							disabled={!hasViewPermission}
							key={task.id}
							onClick={
								hasViewPermission
									? () => handleViewTask(task)
									: undefined
							}
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
						</ClayDropDown.Item>
					);
				})}
			</ClayDropDown.ItemList>
		</ClayDropDown.Menu>
	);
}
