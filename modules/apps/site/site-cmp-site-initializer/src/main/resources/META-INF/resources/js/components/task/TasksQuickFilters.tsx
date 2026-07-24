/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClaySticker from '@clayui/sticker';
import {
	FDS_EVENT,
	IBaseFilterState,
	IFDSState,
} from '@liferay/frontend-data-set-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import classNames from 'classnames';
import {fetch} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {cmpTasksFDSAtom} from '../props_transformer/atoms';

import './TasksQuickFilters.scss';

export const UPDATE_TASKS_QUICK_FILTER_EVENT = 'cmp-update-tasks-quick-filter';
export const UPDATE_TASKS_QUICK_FILTER_VISIBILITY =
	'cmp-update-tasks-quick-filter-visibility';

export const TASK_QUICK_FILTER_TYPES = {
	BLOCKED: 'blocked',
	IN_PROGRESS: 'inProgress',
	OVERDUE: 'overdue',
	TOTAL: 'total',
};

function QuickFilterButton({
	active,
	count,
	displayType,
	icon,
	label,
	onClick,
}: {
	active: boolean;
	count: number;
	displayType:
		| 'secondary'
		| 'success'
		| 'warning'
		| 'danger'
		| 'info'
		| 'unstyled';
	icon: string;
	label: string;
	onClick?: () => void;
}) {
	return (
		<ClayButton
			className={classNames('quick-filter-button', {
				active,
			})}
			displayType="secondary"
			onClick={onClick}
		>
			<div className="align-items-center d-flex">
				<ClaySticker
					className="rounded"
					displayType={displayType}
					size="lg"
				>
					<ClayIcon symbol={icon} />
				</ClaySticker>

				<div className="ml-2">
					<div className="text-dark">{count || 0}</div>

					<div className="text-3 text-secondary text-weight-normal">
						{label}
					</div>
				</div>
			</div>
		</ClayButton>
	);
}

export default function TasksQuickFilters({
	cmpProjectObjectEntryId,
}: {
	cmpProjectObjectEntryId?: string;
}) {
	const [activeQuickFilter, setActiveQuickFilter] = useState<string | null>(
		null
	);

	const [blockedCount, setBlockedCount] = useState(0);
	const [inProgressCount, setInProgressCount] = useState(0);
	const [overdueCount, setOverdueCount] = useState(0);
	const [totalCount, setTotalCount] = useState(0);

	const [loading, setLoading] = useState(true);

	const [tasksFDSState, setTasksFDSState] =
		useLiferayState<IFDSState>(cmpTasksFDSAtom);

	const [visible, setVisible] = useState(true);

	const isQuickFilterChangeRef = useRef(false);
	const activeQuickFilterSelectionsRef = useRef<Record<
		string,
		IBaseFilterState['selectedData']
	> | null>(null);

	const handleTotalTasksClick = useCallback(() => {
		setActiveQuickFilter(TASK_QUICK_FILTER_TYPES.TOTAL);
		activeQuickFilterSelectionsRef.current = null;

		setTasksFDSState({
			...tasksFDSState,
			filters: tasksFDSState.filters.map((filter: IBaseFilterState) => {
				return {
					...filter,
					active: false,
					selectedData: {
						exclude: false,
						selectedItems: [],
					},
				};
			}),
		});

		isQuickFilterChangeRef.current = true;
	}, [setTasksFDSState, tasksFDSState]);

	const handleOverdueClick = useCallback(() => {
		setActiveQuickFilter(TASK_QUICK_FILTER_TYPES.OVERDUE);

		const cmpStateSelectedData: IBaseFilterState['selectedData'] = {
			exclude: true,
			selectedItems: [
				{
					label: Liferay.Language.get('done'),
					value: 'done',
				},
			],
		};

		const currentDate = new Date();

		currentDate.setDate(currentDate.getDate() - 1);

		const cmpDueDateSelectedData: IBaseFilterState['selectedData'] = {
			exclude: false,
			from: null,
			to: {
				day: currentDate.getDate(),
				month: currentDate.getMonth() + 1,
				year: currentDate.getFullYear(),
			},
		};

		activeQuickFilterSelectionsRef.current = {
			cmpDueDate: cmpDueDateSelectedData,
			cmpState: cmpStateSelectedData,
		};

		setTasksFDSState({
			...tasksFDSState,
			filters: tasksFDSState.filters.map((filter: IBaseFilterState) => {
				if (filter.id === 'cmpState') {
					return {
						...filter,
						active: true,
						selectedData: cmpStateSelectedData,
					};
				}

				if (filter.id === 'cmpDueDate') {
					return {
						...filter,
						active: true,
						selectedData: cmpDueDateSelectedData,
					};
				}

				return {
					...filter,
					active: false,
					selectedData: {
						exclude: false,
						selectedItems: [],
					},
				};
			}),
		});

		isQuickFilterChangeRef.current = true;
	}, [setTasksFDSState, tasksFDSState]);

	const handleBlockedClick = useCallback(() => {
		setActiveQuickFilter(TASK_QUICK_FILTER_TYPES.BLOCKED);

		const cmpStateSelectedData: IBaseFilterState['selectedData'] = {
			exclude: false,
			selectedItems: [
				{
					label: Liferay.Language.get('blocked'),
					value: 'blocked',
				},
			],
		};

		activeQuickFilterSelectionsRef.current = {
			cmpState: cmpStateSelectedData,
		};

		setTasksFDSState({
			...tasksFDSState,
			filters: tasksFDSState.filters.map((filter: IBaseFilterState) => {
				if (filter.id === 'cmpState') {
					return {
						...filter,
						active: true,
						selectedData: cmpStateSelectedData,
					};
				}

				return {
					...filter,
					active: false,
					selectedData: {
						exclude: false,
						selectedItems: [],
					},
				};
			}),
		});

		isQuickFilterChangeRef.current = true;
	}, [setTasksFDSState, tasksFDSState]);

	const handleInProgressClick = useCallback(() => {
		setActiveQuickFilter(TASK_QUICK_FILTER_TYPES.IN_PROGRESS);

		const cmpStateSelectedData: IBaseFilterState['selectedData'] = {
			exclude: false,
			selectedItems: [
				{
					label: Liferay.Language.get('in-progress'),
					value: 'inProgress',
				},
			],
		};

		activeQuickFilterSelectionsRef.current = {
			cmpState: cmpStateSelectedData,
		};

		setTasksFDSState({
			...tasksFDSState,
			filters: tasksFDSState.filters.map((filter: IBaseFilterState) => {
				if (filter.id === 'cmpState') {
					return {
						...filter,
						active: true,
						selectedData: cmpStateSelectedData,
					};
				}

				return {
					...filter,
					active: false,
					selectedData: {
						exclude: false,
						selectedItems: [],
					},
				};
			}),
		});

		isQuickFilterChangeRef.current = true;
	}, [setTasksFDSState, tasksFDSState]);

	const fetchCounts = useCallback(async () => {
		fetch(
			cmpProjectObjectEntryId
				? `/o/headless-cmp/v1.0/projects/${cmpProjectObjectEntryId}/task-statistics/`
				: '/o/headless-cmp/v1.0/task-statistics/',
			{
				method: 'GET',
			}
		).then(async (response: Response) => {
			const data = await response.json();

			setBlockedCount(data.blockedCount);
			setInProgressCount(data.inProgressCount);
			setOverdueCount(data.overdueCount);
			setTotalCount(data.totalCount);
		});
	}, [cmpProjectObjectEntryId]);

	useEffect(() => {
		fetchCounts();

		setLoading(false);
	}, [fetchCounts]);

	/**
	 * Clear the active quick filter if the filters in the FDS changes.
	 * `isQuickFilterChangeRef` is used to prevent the active quick filter from
	 * immediately clearing when one of the quick filters are clicked.
	 * `activeQuickFilterSelectionsRef` tracks the exact `selectedData` the
	 * active quick filter set for each filter ID, so unrelated filter changes
	 * (e.g. a text search) do not reset it, but changing the selection within
	 * a tracked filter (e.g. adding "Done" to the State dropdown) does.
	 */
	useEffect(() => {
		if (isQuickFilterChangeRef.current) {
			isQuickFilterChangeRef.current = false;

			return;
		}

		// null = TOTAL: valid while all quick-filter dimensions remain inactive

		const isFilterActive = (filterId: string) =>
			tasksFDSState.filters.some(
				(filter: IBaseFilterState) =>
					filter.id === filterId && filter.active
			);

		const isFilterMatchingSelection = (filterId: string) => {
			const expectedSelectedData =
				activeQuickFilterSelectionsRef.current?.[filterId];

			return tasksFDSState.filters.some(
				(filter: IBaseFilterState) =>
					filter.id === filterId &&
					filter.active &&
					JSON.stringify(filter.selectedData) ===
						JSON.stringify(expectedSelectedData)
			);
		};

		const quickFilterStillActive =
			activeQuickFilterSelectionsRef.current === null
				? !isFilterActive('cmpState') && !isFilterActive('cmpDueDate')
				: Object.keys(activeQuickFilterSelectionsRef.current).every(
						isFilterMatchingSelection
					);

		if (!quickFilterStillActive) {
			setActiveQuickFilter(null);
		}
	}, [tasksFDSState.filters]);

	/**
	 * Listens for the `UPDATE_TASKS_QUICK_FILTER_EVENT` to allow external
	 * components, such as `TasksOverview`, to programmatically update the
	 * active quick filter.
	 */
	useEffect(() => {
		const handleUpdateTasksQuickFilter = (event: {type: string}) => {
			const {type} = event;

			if (type === TASK_QUICK_FILTER_TYPES.BLOCKED) {
				handleBlockedClick();
			}
			else if (type === TASK_QUICK_FILTER_TYPES.IN_PROGRESS) {
				handleInProgressClick();
			}
			else if (type === TASK_QUICK_FILTER_TYPES.OVERDUE) {
				handleOverdueClick();
			}
			else if (type === TASK_QUICK_FILTER_TYPES.TOTAL) {
				handleTotalTasksClick();
			}
		};

		const handleUpdateTasksQuickVisibility = (event: {
			visible: boolean;
		}) => {
			setVisible(event.visible);
		};

		Liferay.on(
			UPDATE_TASKS_QUICK_FILTER_EVENT,
			handleUpdateTasksQuickFilter
		);

		Liferay.on(
			UPDATE_TASKS_QUICK_FILTER_VISIBILITY,
			handleUpdateTasksQuickVisibility
		);

		Liferay.on(FDS_EVENT.DISPLAY_UPDATED, fetchCounts);

		return () => {
			Liferay.detach(
				UPDATE_TASKS_QUICK_FILTER_EVENT,
				handleUpdateTasksQuickFilter
			);

			Liferay.detach(FDS_EVENT.DISPLAY_UPDATED, fetchCounts);
		};
	}, [
		fetchCounts,
		handleBlockedClick,
		handleInProgressClick,
		handleOverdueClick,
		handleTotalTasksClick,
	]);

	return visible ? (
		<div className="lfr-cmp__tasks-quick-filters-container">
			{loading ? (
				<ClayLoadingIndicator />
			) : totalCount !== 0 ? (
				<ClayLayout.ContainerFluid
					className="c-pb-4 c-pt-2 c-px-4"
					size={false}
				>
					<ClayLayout.Row>
						<ClayLayout.Col className="c-px-2" size={3}>
							<QuickFilterButton
								active={
									activeQuickFilter ===
									TASK_QUICK_FILTER_TYPES.TOTAL
								}
								count={totalCount}
								displayType="unstyled"
								icon="task-status"
								label={Liferay.Language.get('total-tasks')}
								onClick={handleTotalTasksClick}
							/>
						</ClayLayout.Col>

						<ClayLayout.Col className="c-px-2" size={3}>
							<QuickFilterButton
								active={
									activeQuickFilter ===
									TASK_QUICK_FILTER_TYPES.IN_PROGRESS
								}
								count={inProgressCount}
								displayType="info"
								icon="analytics"
								label={Liferay.Language.get('in-progress')}
								onClick={handleInProgressClick}
							/>
						</ClayLayout.Col>

						<ClayLayout.Col className="c-px-2" size={3}>
							<QuickFilterButton
								active={
									activeQuickFilter ===
									TASK_QUICK_FILTER_TYPES.BLOCKED
								}
								count={blockedCount}
								displayType="danger"
								icon="block"
								label={Liferay.Language.get('blocked')}
								onClick={handleBlockedClick}
							/>
						</ClayLayout.Col>

						<ClayLayout.Col className="c-px-2" size={3}>
							<QuickFilterButton
								active={
									activeQuickFilter ===
									TASK_QUICK_FILTER_TYPES.OVERDUE
								}
								count={overdueCount}
								displayType="warning"
								icon="exclamation-full"
								label={Liferay.Language.get('overdue')}
								onClick={handleOverdueClick}
							/>
						</ClayLayout.Col>
					</ClayLayout.Row>
				</ClayLayout.ContainerFluid>
			) : null}
		</div>
	) : null;
}
