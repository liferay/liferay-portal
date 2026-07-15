/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {IItemsActions, getItemActionURL} from '@liferay/frontend-data-set-web';
import {AssigneeAvatar} from '@liferay/object-dynamic-data-mapping-form-field-type';
import classNames from 'classnames';
import {navigate} from 'frontend-js-web';
import React from 'react';

import getTaskItemsActions from '../../../../../utils/getTaskItemsActions';
import isActionsMenuEvent from '../../../../../utils/isActionsMenuEvent';
import isOverdue from '../../../../../utils/isOverdue';
import {ITaskObjectEntry} from '../../../../../utils/types';
import StateLabel from '../../../../StateLabel';

import './CalendarTaskCard.scss';

interface CalendarTaskCardProps {
	expanded?: boolean;
	itemsActions?: IItemsActions[];
	loadData: Function;
	task: ITaskObjectEntry;
}

export default function CalendarTaskCard({
	expanded = false,
	itemsActions = [],
	loadData,
	task,
}: CalendarTaskCardProps) {
	const {assignTo, dueDate, state, title} = task;

	const blocked = state?.key === 'blocked';
	const overdue = isOverdue({dueDate, state});

	const taskItemsActions = getTaskItemsActions(itemsActions, loadData, {
		actions: task.actions,
		embedded: task,
	});

	const hasViewPermission = Boolean(task.actions?.get);

	const handleViewTask = () => {
		const viewURL = getItemActionURL(itemsActions, 'actionLink', {
			embedded: task,
		});

		if (viewURL) {
			navigate(viewURL);
		}
	};

	const actionsMenu = !!taskItemsActions.length && (
		<ClayDropDownWithItems
			items={taskItemsActions}
			trigger={
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('actions')}
					borderless
					className="component-action lfr__cmp-calendar-task-card-actions"
					data-actions-menu
					displayType="secondary"
					size="sm"
					symbol="ellipsis-v"
				/>
			}
		/>
	);

	const assignee = (
		<span className="lfr__cmp-calendar-task-card-assignee">
			<AssigneeAvatar
				name={assignTo?.name}
				portrait={assignTo?.portrait}
			/>
		</span>
	);

	const stateIcon = (
		<>
			{blocked && !overdue && (
				<ClayIcon
					className="lfr__cmp-calendar-task-card-icon lfr__cmp-calendar-task-card-icon-blocked"
					symbol="block"
				/>
			)}

			{overdue && (
				<ClayIcon
					className="lfr__cmp-calendar-task-card-icon lfr__cmp-calendar-task-card-icon-overdue"
					symbol="exclamation-full"
				/>
			)}
		</>
	);

	return (
		<div
			className={classNames('lfr__cmp-calendar-task-card', {
				'lfr__cmp-calendar-task-card-clickable': hasViewPermission,
				'lfr__cmp-calendar-task-card-expanded': expanded,
				'lfr__cmp-calendar-task-card-state-overdue': overdue,
				[`lfr__cmp-calendar-task-card-state-${state?.key}`]:
					!overdue && state?.key,
			})}
			onClick={
				hasViewPermission
					? (event) => {
							if (!isActionsMenuEvent(event)) {
								handleViewTask();
							}
						}
					: undefined
			}
			onKeyDown={
				hasViewPermission
					? (event) => {
							if (
								!isActionsMenuEvent(event) &&
								(event.key === 'Enter' || event.key === ' ')
							) {
								event.preventDefault();

								handleViewTask();
							}
						}
					: undefined
			}
			role={hasViewPermission ? 'button' : undefined}
			tabIndex={hasViewPermission ? 0 : undefined}
		>
			{expanded ? (
				<>
					<div className="lfr__cmp-calendar-task-card-header">
						<span className="lfr__cmp-calendar-task-card-title">
							{title}
						</span>

						{actionsMenu}
					</div>

					<div className="lfr__cmp-calendar-task-card-footer">
						<StateLabel dueDate={dueDate} state={state} />

						<div className="lfr__cmp-calendar-task-card-footer-end">
							{stateIcon}

							{assignee}
						</div>
					</div>
				</>
			) : (
				<>
					<span className="lfr__cmp-calendar-task-card-title">
						{title}
					</span>

					{stateIcon}

					{assignee}

					{actionsMenu}
				</>
			)}
		</div>
	);
}
