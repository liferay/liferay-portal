/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {
	FrontendDataSet,
	IInternalRenderer,
} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import AssignToModalContent from '../home/modal/AssignToModalContent';
import TransitionWorkflowStateModalContent from '../home/modal/TransitionWorkflowStateModalContent';
import UpdateDueDateModalContent from '../home/modal/UpdateDueDateModalContent';

import '../../../css/home/Home.scss';
import {
	getWorkflowTasksAssignedToMe,
	getWorkflowTasksAssignedToMyRoles,
} from '../../common/services/WorkflowService';
import {WorkflowTask} from '../../common/types/WorkflowTask';
import WorkflowTaskRenderer from '../props_transformer/cell_renderers/WorkflowTaskRenderer';

export default function ViewWorkflowTasks({
	id,
	myRolesWorkflowTasksURL,
	myWorkflowTasksURL,
	objectDefinitions,
}: {
	id: string;
	myRolesWorkflowTasksURL: string;
	myWorkflowTasksURL: string;
	objectDefinitions: any[];
}) {
	const filterItems = [
		{
			label: Liferay.Language.get('assigned-to-me'),
			value: 'assigned-to-me',
		},
		{
			label: Liferay.Language.get('assigned-to-my-roles'),
			value: 'assigned-to-my-roles',
		},
	];

	const [selectedItem, setSelectedItem] = useState({
		label: Liferay.Language.get('assigned-to-me'),
		value: 'assigned-to-me',
	});

	const [workflowTasks, setWorkflowTasks] = useState<{
		items: WorkflowTask[];
		totalCount: number;
	}>({
		items: [],
		totalCount: 0,
	});

	const [pagination, setPagination] = useState({
		currentPage: 1,
		pageSize: 8,
	});

	const handlePaginationDeltaChange = useCallback((value: any) => {
		setPagination((prevState) => ({
			...prevState,
			pageSize: value,
		}));
	}, []);

	const handlePaginationPageChange = useCallback((value: any) => {
		setPagination((prevState) => ({
			...prevState,
			currentPage: value,
		}));
	}, []);

	const getWorkflowTasks = useCallback(async () => {
		try {
			const getWorkflowTasksAPI =
				selectedItem.value === 'assigned-to-me'
					? getWorkflowTasksAssignedToMe
					: getWorkflowTasksAssignedToMyRoles;

			const res = await getWorkflowTasksAPI({
				objectDefinitions,
				page: pagination.currentPage,
				pageSize: pagination.pageSize,
			});

			const items = res.items.map((item) => {
				return {
					...item,
					myWorkflowTasksURL,
				};
			});

			setWorkflowTasks({
				items,
				totalCount: res.totalCount,
			});
		}
		catch (error) {
			setWorkflowTasks({items: [], totalCount: 0});
		}
	}, [pagination, selectedItem.value, myWorkflowTasksURL, objectDefinitions]);

	useEffect(() => {
		getWorkflowTasks();
	}, [getWorkflowTasks]);

	const defaultFDSDataSetProps = {
		customViewsEnabled: false,
		showManagementBar: false,
		showPagination: false,
		showSearch: false,
		showSelectAll: false,
	};

	const frontendDataSetProps = {
		...defaultFDSDataSetProps,
		customRenderers: {
			tableCell: [
				{
					component: WorkflowTaskRenderer,
					name: 'workflowTaskTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		emptyState: {
			description: Liferay.Language.get(
				'there-are-no-tasks-assigned-to-you'
			),
			image: '',
			title: Liferay.Language.get('no-tasks'),
		},
		id,
		items: workflowTasks.items,
		itemsActions:
			selectedItem.value === 'assigned-to-me'
				? [
						{
							data: {
								id: 'approve',
							},
							label: Liferay.Language.get('approve'),
							onClick: ({itemData}: any) => {
								openModal({
									contentComponent: ({
										closeModal,
									}: {
										closeModal: () => void;
									}) =>
										TransitionWorkflowStateModalContent({
											closeModal,
											loadData: getWorkflowTasks,
											transitionName: 'approve',
											workflowTaskId: Number(itemData.id),
										}),
									size: 'md',
								});
							},
						},
						{
							data: {
								id: 'reject',
							},
							label: Liferay.Language.get('reject'),
							onClick: ({itemData}: any) => {
								openModal({
									contentComponent: ({
										closeModal,
									}: {
										closeModal: () => void;
									}) =>
										TransitionWorkflowStateModalContent({
											closeModal,
											loadData: getWorkflowTasks,
											transitionName: 'reject',
											workflowTaskId: Number(itemData.id),
										}),
									size: 'md',
								});
							},
						},
						{
							data: {
								id: 'assignTo',
							},
							label: Liferay.Language.get('assign-to'),
							onClick: ({itemData}: any) => {
								openModal({
									contentComponent: ({
										closeModal,
									}: {
										closeModal: () => void;
									}) =>
										AssignToModalContent({
											assignable: true,
											closeModal,
											loadData: getWorkflowTasks,
											workflowTaskId: Number(itemData.id),
										}),
									size: 'md',
								});
							},
						},
						{
							data: {
								id: 'updateDueDate',
							},
							label: Liferay.Language.get('update-due-date'),
							onClick: ({itemData}: any) => {
								openModal({
									contentComponent: ({
										closeModal,
									}: {
										closeModal: () => void;
									}) =>
										UpdateDueDateModalContent({
											closeModal,
											dueDate: itemData.dateDue,
											loadData: getWorkflowTasks,
											workflowTaskId: Number(itemData.id),
										}),
									size: 'md',
								});
							},
						},
					]
				: [
						{
							data: {
								id: 'assignToMe',
							},
							label: Liferay.Language.get('assign-to-me'),
							onClick: ({itemData}: any) => {
								openModal({
									contentComponent: ({
										closeModal,
									}: {
										closeModal: () => void;
									}) =>
										AssignToModalContent({
											assignable: false,
											closeModal,
											loadData: getWorkflowTasks,
											workflowTaskId: Number(itemData.id),
										}),
									size: 'md',
								});
							},
						},
						{
							data: {
								id: 'assignTo',
							},
							label: Liferay.Language.get('assign-to-...'),
							onClick: ({itemData}: any) => {
								openModal({
									contentComponent: ({
										closeModal,
									}: {
										closeModal: () => void;
									}) =>
										AssignToModalContent({
											assignable: true,
											closeModal,
											loadData: getWorkflowTasks,
											workflowTaskId: Number(itemData.id),
										}),
									size: 'md',
								});
							},
						},
						{
							data: {
								id: 'updateDueDate',
							},
							label: Liferay.Language.get('update-due-date'),
							onClick: ({itemData}: any) => {
								openModal({
									contentComponent: ({
										closeModal,
									}: {
										closeModal: () => void;
									}) =>
										UpdateDueDateModalContent({
											closeModal,
											dueDate: itemData.dateDue,
											loadData: getWorkflowTasks,
											workflowTaskId: Number(itemData.id),
										}),
									size: 'md',
								});
							},
						},
					],
		views: [
			{
				contentRenderer: 'table',
				label: 'Table',
				name: 'table',
				schema: {
					fields: [
						{
							contentRenderer: 'workflowTaskTableCellRenderer',
							fieldName: 'renderer',
							label: Liferay.Language.get('renderer'),
						},
					],
				},
				style: 'fluid',
				thumbnail: 'table',
			},
		],
	};

	return (
		<div className="container-fluid">
			<div className="align-items-center d-flex justify-content-between mb-4">
				<h3 className="font-weight-semi-bold ml-3 mr-auto text-4">
					{Liferay.Language.get('my-workflow-tasks')}
				</h3>

				<ClayDropdown
					className="filter-dropdown"
					closeOnClick
					hasLeftSymbols
					trigger={
						<ClayButton displayType="secondary" size="sm">
							<span>
								{selectedItem.label}

								<ClayIcon
									className="ml-2"
									symbol="caret-bottom"
								/>
							</span>
						</ClayButton>
					}
				>
					{filterItems.map((item) => (
						<ClayDropdown.Item
							active={item.value === selectedItem.value}
							key={item.value}
							onClick={() => {
								setSelectedItem(item);
							}}
							symbolLeft={
								item.value === selectedItem.value ? 'check' : ''
							}
						>
							{item.label}
						</ClayDropdown.Item>
					))}
				</ClayDropdown>

				<ClayTooltipProvider>
					<ClayButton
						aria-label={sub(
							Liferay.Language.get('open-x'),
							`${Liferay.Language.get('my-workflow-tasks')}: ${selectedItem.label}`
						)}
						borderless
						className="btn btn-secondary btn-sm cms-btn-icon-only cms-btn-secondary ml-2"
						displayType="secondary"
						onClick={() =>
							window.open(
								selectedItem.value === 'assigned-to-me'
									? myWorkflowTasksURL
									: myRolesWorkflowTasksURL,
								'_blank'
							)
						}
						title={sub(
							Liferay.Language.get('open-x-in-full-page-view'),
							`"${selectedItem.label}"`
						)}
						type="button"
					>
						<ClayIcon symbol="shortcut" />
					</ClayButton>
				</ClayTooltipProvider>
			</div>

			<div className="cms-fds-fluid cms-section home-custom-empty-state">
				<FrontendDataSet {...frontendDataSetProps} />

				{workflowTasks.totalCount > 0 && (
					<ClayPaginationBarWithBasicItems
						activeDelta={pagination.pageSize}
						deltas={[8, 20, 40, 60].map((size) => ({
							label: size,
						}))}
						ellipsisBuffer={3}
						onActiveChange={handlePaginationPageChange}
						onDeltaChange={handlePaginationDeltaChange}
						totalItems={workflowTasks.totalCount}
					/>
				)}
			</div>
		</div>
	);
}
