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
import {FrontendDataSetContext} from '@liferay/frontend-data-set-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import {AssigneeAvatar} from '@liferay/object-dynamic-data-mapping-form-field-type';
import React, {useContext, useMemo, useState} from 'react';

import getTaskItemsActions from '../../../../../utils/getTaskItemsActions';
import {ITaskObjectEntry} from '../../../../../utils/types';
import StateLabel from '../../../../StateLabel';
import sortTasksByPriority from '../utils/sortTasksByPriority';
import {unscheduledTasksAtom} from '../utils/unscheduledTasksAtom';

import './UnscheduledTasksPanel.scss';

export default function UnscheduledTasksPanel() {
	const {itemsActions, loadData} = useContext(FrontendDataSetContext);

	const [tasks] = useLiferayState<ITaskObjectEntry[]>(unscheduledTasksAtom);

	const [query, setQuery] = useState('');

	const filteredTasks = useMemo(() => {
		const normalizedQuery = query.trim().toLowerCase();

		return sortTasksByPriority(
			tasks.filter((task) =>
				(task.title ?? '').toLowerCase().includes(normalizedQuery)
			)
		);
	}, [query, tasks]);

	return (
		<div
			className="lfr__cmp-unscheduled-tasks-panel"
			data-testid="calendarUnscheduledTasksPanel"
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
								<ClayList.Item flex key={task.id}>
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
		</div>
	);
}
