/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDropDown from '@clayui/drop-down';
import {
	FOCUSABLE_ELEMENTS,
	Keys,
	getFocusableList,
	useNavigation,
} from '@clayui/shared';
import {IItemsActions, getItemActionURL} from '@liferay/frontend-data-set-web';
import {Immutable} from '@liferay/frontend-js-state-web';
import {AssigneeAvatar} from '@liferay/object-dynamic-data-mapping-form-field-type';
import {navigate} from 'frontend-js-web';
import React, {useEffect, useMemo, useRef} from 'react';

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
	const menuRef = useRef<HTMLDivElement>(null);

	const sortedTasks = useMemo(() => sortTasksByPriority(tasks), [tasks]);

	const {navigationProps} = useNavigation({
		activation: 'manual',
		containerRef: menuRef,
		loop: true,
		orientation: 'vertical',
		typeahead: true,
		visible: true,
	});

	/**
	 * FullCalendar owns the "more" link, so this menu cannot be a
	 * ClayDropDown with a trigger and misses the focus handling that
	 * component brings. Move the focus into the menu as it opens: the menu
	 * renders in a portal at the end of the body, so pressing Tab from the
	 * link would otherwise land on the next calendar cell, which blurs the
	 * menu and closes it, leaving the task list unreachable by keyboard.
	 */
	useEffect(() => {
		const [firstTask] = getFocusableList(menuRef);

		firstTask?.focus();
	}, []);

	/**
	 * Resume the document order around the "more" link on Tab, the way
	 * ClayDropDown does, instead of leaving the focus at the end of the body
	 * where the portal lives.
	 */
	const handleTab = (shiftKey: boolean) => {
		if (shiftKey) {
			alignElement.focus();
		}
		else {
			const focusableElements = Array.from<HTMLElement>(
				document.querySelectorAll(FOCUSABLE_ELEMENTS.join(','))
			);

			focusableElements[
				focusableElements.indexOf(alignElement) + 1
			]?.focus();
		}

		onClose();
	};

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
			onKeyDown={(event) => {
				if (event.key === Keys.Tab) {
					event.preventDefault();

					handleTab(event.shiftKey);

					return;
				}

				navigationProps.onKeyDown(event);
			}}
			ref={menuRef}
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
