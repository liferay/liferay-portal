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

import {cmpWorkflowTasksFDSAtom} from '../props_transformer/atoms';

import './WorkflowTasksOverview.scss';

const CONTAINER_CLASS_NAME = 'lfr-cmp__workflow-tasks-overview-container';

const WORKFLOW_TASK_QUICK_FILTER_TYPES = {
	COMPLETED: 'completed',
	PENDING: 'pending',
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
			<div className="align-items-center d-flex quick-filter-button-content">
				<ClaySticker
					className="rounded"
					displayType={displayType}
					size="lg"
				>
					<ClayIcon symbol={icon} />
				</ClaySticker>

				<div className="ml-2">
					<div className="quick-filter-button-count text-dark">
						{count || 0}
					</div>

					<div className="text-3 text-secondary text-weight-normal">
						{label}
					</div>
				</div>
			</div>
		</ClayButton>
	);
}

export default function WorkflowTasksOverview({
	filterURL,
}: {
	filterURL?: string;
}) {
	const [completedCount, setCompletedCount] = useState(0);
	const [loading, setLoading] = useState(true);
	const [pendingCount, setPendingCount] = useState(0);

	const [activeQuickFilter, setActiveQuickFilter] = useState<string | null>(
		null
	);

	const [workflowTasksFDSState, setWorkflowTasksFDSState] =
		useLiferayState<IFDSState>(cmpWorkflowTasksFDSAtom);

	const isQuickFilterChangeRef = useRef(false);

	const fetchCounts = useCallback(async () => {
		if (!filterURL) {
			setLoading(false);

			return;
		}

		setLoading(true);

		const fetchWorkflowCount = (completed: boolean) =>
			fetch(`${filterURL}&filter=completed eq ${completed}&pageSize=1`)
				.then((response) => {
					if (!response.ok) {
						throw new Error();
					}

					return response.json();
				})
				.then((data) => data.totalCount ?? 0);

		try {
			const [fetchedCompleted, fetchedPending] = await Promise.all([
				fetchWorkflowCount(true),
				fetchWorkflowCount(false),
			]);

			setCompletedCount(fetchedCompleted);
			setPendingCount(fetchedPending);
		}
		catch {
			setCompletedCount(0);
			setPendingCount(0);
		}
		finally {
			setLoading(false);
		}
	}, [filterURL]);

	const updateCompletedFilter = useCallback(
		({
			label,
			type,
			value,
		}: {
			label: string;
			type: string;
			value: string;
		}) => {
			setActiveQuickFilter(type);

			setWorkflowTasksFDSState({
				...workflowTasksFDSState,
				filters: workflowTasksFDSState.filters.map(
					(filter: IBaseFilterState) => {
						if (filter.id === 'completed') {
							return {
								...filter,
								active: true,
								selectedData: {
									exclude: false,
									selectedItems: [
										{
											label,
											value,
										},
									],
								},
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
					}
				),
			});

			isQuickFilterChangeRef.current = true;
		},
		[setWorkflowTasksFDSState, workflowTasksFDSState]
	);

	const handleCompletedClick = useCallback(() => {
		updateCompletedFilter({
			label: Liferay.Language.get('completed'),
			type: WORKFLOW_TASK_QUICK_FILTER_TYPES.COMPLETED,
			value: 'true',
		});
	}, [updateCompletedFilter]);

	const handlePendingClick = useCallback(() => {
		updateCompletedFilter({
			label: Liferay.Language.get('pending'),
			type: WORKFLOW_TASK_QUICK_FILTER_TYPES.PENDING,
			value: 'false',
		});
	}, [updateCompletedFilter]);

	useEffect(() => {
		fetchCounts();
	}, [fetchCounts]);

	useEffect(() => {
		if (isQuickFilterChangeRef.current) {
			isQuickFilterChangeRef.current = false;

			return;
		}

		setActiveQuickFilter(null);
	}, [workflowTasksFDSState.filters]);

	useEffect(() => {
		Liferay.on(FDS_EVENT.DISPLAY_UPDATED, fetchCounts);

		return () => {
			Liferay.detach(FDS_EVENT.DISPLAY_UPDATED, fetchCounts);
		};
	}, [fetchCounts]);

	if (loading) {
		return (
			<div className={CONTAINER_CLASS_NAME}>
				<ClayLoadingIndicator />
			</div>
		);
	}

	const totalCount = completedCount + pendingCount;

	if (totalCount === 0) {
		return null;
	}

	return (
		<div className={CONTAINER_CLASS_NAME}>
			<ClayLayout.ContainerFluid className="c-px-0" size={false}>
				<ClayLayout.Row className="quick-filter-row">
					<ClayLayout.Col className="c-px-2" md={6}>
						<QuickFilterButton
							active={
								activeQuickFilter ===
								WORKFLOW_TASK_QUICK_FILTER_TYPES.PENDING
							}
							count={pendingCount}
							displayType="secondary"
							icon="time"
							label={Liferay.Language.get('pending')}
							onClick={handlePendingClick}
						/>
					</ClayLayout.Col>

					<ClayLayout.Col className="c-px-2" md={6}>
						<QuickFilterButton
							active={
								activeQuickFilter ===
								WORKFLOW_TASK_QUICK_FILTER_TYPES.COMPLETED
							}
							count={completedCount}
							displayType="success"
							icon="check-circle-full"
							label={Liferay.Language.get('completed')}
							onClick={handleCompletedClick}
						/>
					</ClayLayout.Col>
				</ClayLayout.Row>
			</ClayLayout.ContainerFluid>
		</div>
	);
}
