/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {SidePanel} from '@clayui/core';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayEmptyState from '@clayui/empty-state';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import {Draggable} from '@fullcalendar/interaction';
import {FrontendDataSetContext} from '@liferay/frontend-data-set-web';
import {AssigneeAvatar} from '@liferay/object-dynamic-data-mapping-form-field-type';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import getTaskItemsActions from '../../../../../utils/getTaskItemsActions';
import {ITaskObjectEntry} from '../../../../../utils/types';
import StateLabel from '../../../../StateLabel';
import sortTasksByPriority from '../utils/sortTasksByPriority';

import './UnscheduledTasksPanel.scss';

// Marks the task rows the calendar Draggable picks up, and doubles as the
// class name each row renders with so the two always match.

const DRAGGABLE_ITEM_CLASS_NAME = 'lfr__cmp-unscheduled-tasks-panel-item';

interface UnscheduledTasksPanelProps {
	containerRef: React.RefObject<HTMLElement>;
	onOpenChange: (open: boolean) => void;
	open: boolean;
	tasks: ITaskObjectEntry[];
}

export default function UnscheduledTasksPanel({
	containerRef,
	onOpenChange,
	open,
	tasks,
}: UnscheduledTasksPanelProps) {
	const {itemsActions, loadData} = useContext(FrontendDataSetContext);

	const [query, setQuery] = useState('');

	const filteredTasks = useMemo(() => {
		const normalizedQuery = query.trim().toLowerCase();

		return sortTasksByPriority(
			tasks.filter((task) =>
				(task.title ?? '').toLowerCase().includes(normalizedQuery)
			)
		);
	}, [query, tasks]);

	// Make the task rows draggable into the calendar's day cells. The
	// Draggable matches rows through the item selector at drag time, so one
	// instance on the FDS container survives list filtering. With create
	// disabled, FullCalendar fires only the calendar's drop callback instead
	// of inserting an event itself.

	useEffect(() => {
		const containerElement = containerRef.current;

		if (!containerElement) {
			return;
		}

		const draggable = new Draggable(containerElement, {
			eventData: {create: false},
			itemSelector: `.${DRAGGABLE_ITEM_CLASS_NAME}`,
		});

		return () => draggable.destroy();
	}, [containerRef]);

	return (
		<SidePanel
			className="lfr__cmp-unscheduled-tasks-panel"
			containerRef={containerRef}
			data-testid="calendarUnscheduledTasksPanel"
			onOpenChange={onOpenChange}
			open={open}
		>
			<SidePanel.Header>
				<SidePanel.Title>
					<span className="inline-item inline-item-before">
						<ClayIcon
							className="text-secondary"
							symbol="date-time"
						/>
					</span>

					{Liferay.Language.get('unscheduled-tasks')}
				</SidePanel.Title>
			</SidePanel.Header>

			<SidePanel.Body className="pt-0">
				<ClayInput.Group className="lfr__cmp-unscheduled-tasks-panel-search">
					<ClayInput.GroupItem>
						<ClayInput
							aria-label={Liferay.Language.get('search')}
							data-testid="calendarUnscheduledTasksSearch"
							insetAfter
							onChange={(event) => setQuery(event.target.value)}
							placeholder={Liferay.Language.get('search')}
							type="text"
							value={query}
						/>

						<ClayInput.GroupInsetItem after tag="span">
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('search')}
								displayType="unstyled"
								symbol="search"
							/>
						</ClayInput.GroupInsetItem>
					</ClayInput.GroupItem>
				</ClayInput.Group>

				{filteredTasks.length ? (
					<ClayList className="lfr__cmp-unscheduled-tasks-panel-list">
						{filteredTasks.map((task) => {
							const taskItemsActions = getTaskItemsActions(
								itemsActions ?? [],
								loadData,
								{actions: task.actions, embedded: task}
							);

							return (
								<ClayList.Item
									className={DRAGGABLE_ITEM_CLASS_NAME}
									data-task-id={task.id}
									flex
									key={task.id}
								>
									<ClayList.ItemField>
										<AssigneeAvatar
											name={task.assignTo?.name}
											portrait={task.assignTo?.portrait}
										/>
									</ClayList.ItemField>

									<ClayList.ItemField expand>
										<ClayList.ItemTitle>
											<span data-testid="calendarUnscheduledTaskTitle">
												{task.title}
											</span>
										</ClayList.ItemTitle>

										<ClayList.ItemText>
											<StateLabel state={task.state} />
										</ClayList.ItemText>
									</ClayList.ItemField>

									{!!taskItemsActions.length && (
										<ClayList.ItemField>
											<ClayDropDownWithItems
												items={taskItemsActions}
												trigger={
													<ClayButtonWithIcon
														aria-label={Liferay.Language.get(
															'actions'
														)}
														borderless
														className="component-action"
														displayType="secondary"
														symbol="ellipsis-v"
													/>
												}
											/>
										</ClayList.ItemField>
									)}
								</ClayList.Item>
							);
						})}
					</ClayList>
				) : (
					<ClayEmptyState
						description={Liferay.Language.get(
							'review-your-search-and-try-again'
						)}
						imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.svg`}
						imgSrcReducedMotion={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state_reduced_motion.svg`}
						small
						title={Liferay.Language.get('no-results-found')}
					>
						<ClayButton
							displayType="secondary"
							onClick={() => setQuery('')}
							size="sm"
						>
							{Liferay.Language.get('clear-search')}
						</ClayButton>
					</ClayEmptyState>
				)}
			</SidePanel.Body>
		</SidePanel>
	);
}
