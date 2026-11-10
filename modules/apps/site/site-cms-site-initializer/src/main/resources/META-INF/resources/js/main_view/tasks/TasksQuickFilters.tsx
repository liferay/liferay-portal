/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {IFDSState} from '@liferay/frontend-data-set-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import {fetch} from 'frontend-js-web';
import React, {useCallback, useState} from 'react';

import useRefreshOnFDSDisplayUpdated from '../../common/hooks/useRefreshOnFDSDisplayUpdated';
import {CMSSiteInitializerFDSNames} from '../../common/utils/constants';
import QuickFilterButton from '../quick_filters/QuickFilterButton';
import {
	applyQuickFilterUpdates,
	getActiveQuickFilter,
} from '../quick_filters/utils';
import {tasksFDSAtom} from './atoms';
import {
	TASK_QUICK_FILTER_UPDATES,
	TaskQuickFilterType,
} from './taskQuickFilterUpdates';

type TaskCounts = Record<TaskQuickFilterType, number>;

function getOverdueODataFilter() {
	const now = new Date();

	now.setSeconds(0, 0);

	return `completed eq false and dueDate le ${now.toISOString()}`;
}

function fetchCount(apiURL: string, odataFilter: string): Promise<number> {
	return fetch(
		`${apiURL}&filter=${encodeURIComponent(odataFilter)}&pageSize=1`
	)
		.then((response) => {
			if (!response.ok) {
				throw new Error(`HTTP ${response.status}`);
			}

			return response.json();
		})
		.then((data) => data.totalCount ?? 0);
}

export default function TasksQuickFilters({apiURL}: {apiURL: string}) {
	const [tasksFDSState, setTasksFDSState] =
		useLiferayState<IFDSState>(tasksFDSAtom);

	const activeQuickFilter = getActiveQuickFilter(
		tasksFDSState.filters,
		TASK_QUICK_FILTER_UPDATES
	);

	const [counts, setCounts] = useState<TaskCounts>({
		completed: 0,
		overdue: 0,
		pending: 0,
	});

	const fetchCounts = useCallback(() => {
		Promise.all([
			fetchCount(apiURL, 'completed eq true'),
			fetchCount(apiURL, getOverdueODataFilter()),
			fetchCount(apiURL, 'completed eq false'),
		])
			.then(([completed, overdue, pending]) => {
				setCounts({completed, overdue, pending});
			})
			.catch((error) => {
				console.error('Failed to fetch task counts', error);
			});
	}, [apiURL]);

	useRefreshOnFDSDisplayUpdated(
		CMSSiteInitializerFDSNames.TASKS_SECTION,
		fetchCounts
	);

	const applyQuickFilter = useCallback(
		(quickFilterType: TaskQuickFilterType) => {
			setTasksFDSState({
				...tasksFDSState,
				filters: applyQuickFilterUpdates(
					tasksFDSState.filters,
					TASK_QUICK_FILTER_UPDATES[quickFilterType]()
				),
			});
		},
		[setTasksFDSState, tasksFDSState]
	);

	if (!counts.completed && !counts.pending) {
		return null;
	}

	const quickFilters = [
		{
			displayType: 'info',
			icon: 'time',
			label: Liferay.Language.get('pending'),
			type: 'pending',
		},
		{
			displayType: 'warning',
			icon: 'info-circle',
			label: Liferay.Language.get('overdue'),
			type: 'overdue',
		},
		{
			displayType: 'success',
			icon: 'check-circle-full',
			label: Liferay.Language.get('completed'),
			type: 'completed',
		},
	] as const;

	return (
		<ClayLayout.ContainerFluid
			className="c-pb-4 c-pt-2 c-px-4"
			size={false}
		>
			<ClayLayout.Row className="c-mx-n2">
				{quickFilters.map(({displayType, icon, label, type}) => (
					<ClayLayout.Col className="c-px-2" key={type} size={4}>
						<QuickFilterButton
							active={activeQuickFilter === type}
							count={counts[type]}
							displayType={displayType}
							icon={icon}
							label={label}
							onClick={() => applyQuickFilter(type)}
						/>
					</ClayLayout.Col>
				))}
			</ClayLayout.Row>
		</ClayLayout.ContainerFluid>
	);
}
