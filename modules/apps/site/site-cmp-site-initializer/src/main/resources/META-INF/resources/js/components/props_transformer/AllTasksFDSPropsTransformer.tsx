/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	DateRenderer,
	FDS_EVENT,
	IInternalRenderer,
	IView,
} from '@liferay/frontend-data-set-web';
import {
	SimpleActionLinkRenderer,
	addOnClickToCreationMenuItems,
	deleteAssetEntriesBulkAction,
	deleteItemAction,
} from '@liferay/site-cms-site-initializer';
import {sub} from 'frontend-js-web';
import React from 'react';

import {styleActions, styleBulkActions} from '../../utils/actionStyles';
import {
	installCMPTabPersistence,
	registerTabFDS,
} from '../../utils/cmpTabPersistence';
import {WORKFLOW_TASK_ACTION_LINK_ID} from '../../utils/constants';
import getCMPProjectObjectEntryIds from '../../utils/getCMPProjectObjectEntryIds';
import {getFormattedLabel} from '../../utils/getFormattedText';
import {openCMPModal} from '../../utils/openCMPModal';
import {transformFDSBulkActions} from '../../utils/transformFDSBulkActions';
import {
	ProjectTaskItemData,
	TaskAction,
	WorkflowTaskItemData,
} from '../../utils/types';
import WORKFLOW_TASK_MODALS from '../../utils/workflowTaskModals';
import StateLabel from '../StateLabel';
import BulkEditAssigneeModalContent from '../modal/BulkEditAssigneeModalContent';
import BulkEditDueDateModalContent from '../modal/BulkEditDueDateModalContent';
import BulkEditStateModalContent from '../modal/BulkEditStateModalContent';
import BulkEditWorkflowAssigneeModalContent from '../modal/BulkEditWorkflowAssigneeModalContent';
import BulkEditWorkflowDueDateModalContent from '../modal/BulkEditWorkflowDueDateModalContent';
import EditAssigneeModalContent from '../modal/EditAssigneeModalContent';
import ACTIONS from './actions/creationMenuActions';
import AssigneeRenderer from './cell_renderers/AssigneeRenderer';
import WorkflowStateRenderer from './cell_renderers/WorkflowStateRenderer';
import WorkflowTaskActionLinkRenderer from './cell_renderers/WorkflowTaskActionLinkRenderer';

const _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN =
	'com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken';

const isWorkflowTask = (
	itemData: ProjectTaskItemData | WorkflowTaskItemData
): itemData is WorkflowTaskItemData =>
	itemData.entryClassName === _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN;

type BulkModalProps = {
	closeModal: () => void;
	loadData: () => void;
	selectedData: any;
};

const WORKFLOW_BULK_ACTION_MODALS: Record<
	string,
	React.ComponentType<BulkModalProps>
> = {
	'assign-to': BulkEditWorkflowAssigneeModalContent,
	'update-due-date': BulkEditWorkflowDueDateModalContent,
};

const WORKFLOW_BULK_ACTION_PERMISSION_KEYS: Record<string, string> = {
	'assign-to': 'assignToUser',
	'update-due-date': 'updateDueDate',
};

export default function AllTasksFDSPropsTransformer({
	additionalProps,
	bulkActions = [],
	creationMenu,
	id,
	itemsActions = [],
	views,
	...otherProps
}: {
	additionalProps: any;
	apiURL: string;
	bulkActions?: any[];
	creationMenu: any;
	id: string;
	itemsActions?: any[];
	otherProps: any;
	views: IView[];
}) {
	const nonDefaultViews = views.map((view) => ({
		...view,
		default: false,
		initialPaginationDelta: 20,
	}));

	registerTabFDS(id, 0);
	installCMPTabPersistence();

	return {
		...otherProps,
		bulkActions: transformFDSBulkActions(
			styleBulkActions(bulkActions).map((action) => ({
				...action,
				isVisible: ({
					allItemsSelectedActive,
					selectedItems,
				}: {
					allItemsSelectedActive: boolean;
					selectedItems: any[];
				}) => {
					if (action?.data?.id !== 'assign-to') {
						return true;
					}

					if (allItemsSelectedActive) {
						return false;
					}

					if (
						!selectedItems?.length ||
						selectedItems.every(isWorkflowTask)
					) {
						return true;
					}

					const cmpProjectObjectEntryIds =
						getCMPProjectObjectEntryIds(selectedItems);

					return cmpProjectObjectEntryIds.size === 1;
				},
			})),
			(action, item) => {
				if (isWorkflowTask(item)) {
					return WORKFLOW_BULK_ACTION_PERMISSION_KEYS[
						action?.data?.id
					];
				}

				return action?.data?.permissionKey;
			}
		).map((action) => ({
			...action,
			isDisabled: ({
				allItemsSelectedActive,
				selectedItems,
			}: {
				allItemsSelectedActive: boolean;
				selectedItems: any[];
			}) => {
				const actionId = action?.data?.id;

				if (actionId === 'update-state' || actionId === 'delete') {
					if (allItemsSelectedActive) {
						return true;
					}

					if (selectedItems?.some(isWorkflowTask)) {
						return true;
					}
				}

				if (allItemsSelectedActive || !selectedItems?.length) {
					return false;
				}

				const firstType = selectedItems[0]?.entryClassName;

				return selectedItems.some(
					(item) => item?.entryClassName !== firstType
				);
			},
		})),
		creationMenu: {
			...creationMenu,
			primaryItems: addOnClickToCreationMenuItems(
				creationMenu.primaryItems,
				ACTIONS
			),
		},
		customRenderers: {
			tableCell: [
				{
					component: ({itemData}) => {
						if (
							itemData.entryClassName ===
							_CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
						) {
							if (itemData.embedded?.assigneePerson) {
								return (
									<AssigneeRenderer
										image={
											itemData.embedded.assigneePerson
												.image
										}
										name={
											itemData.embedded.assigneePerson
												.name
										}
									/>
								);
							}

							return itemData.embedded?.assigneeRoles
								?.map(({name}: {name: string}) => name)
								.join(', ');
						}

						return (
							<AssigneeRenderer
								image={itemData.embedded?.assignTo?.portrait}
								name={itemData.embedded?.assignTo?.name}
							/>
						);
					},
					name: 'assigneeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						DateRenderer({
							value: isWorkflowTask(itemData)
								? itemData.embedded?.dateDue
								: itemData.embedded?.dueDate,
						}),
					name: 'dueDateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						isWorkflowTask(itemData)
							? '-'
							: itemData.embedded
									?.r_cmpProjectToCMPTasks_c_cmpProject
									?.title,
					name: 'projectTitleTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({actions, itemData, options}) =>
						isWorkflowTask(itemData) ? (
							<WorkflowTaskActionLinkRenderer
								actionId={WORKFLOW_TASK_ACTION_LINK_ID}
								actions={actions}
								itemData={itemData}
							/>
						) : (
							SimpleActionLinkRenderer({
								actions,
								itemData,
								options,
								value: itemData.embedded?.title,
							})
						),
					name: 'simpleActionLinkTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						isWorkflowTask(itemData) ? (
							<WorkflowStateRenderer
								embedded={itemData.embedded}
							/>
						) : (
							StateLabel({
								dueDate: itemData.embedded?.dueDate,
								state: itemData.embedded?.state,
							})
						),
					name: 'stateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		id,
		itemsActions: styleActions(itemsActions),
		async onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: TaskAction;
			itemData: ProjectTaskItemData | WorkflowTaskItemData;
			loadData: () => Promise<void>;
		}) {
			if (isWorkflowTask(itemData)) {
				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						WORKFLOW_TASK_MODALS[action?.data?.id]({
							closeModal,
							dueDate: itemData.embedded?.dateDue,
							loadData,
							workflowTaskId: itemData.embedded?.id,
						}),
					size: 'md',
				});

				return;
			}

			if (action?.data?.id === 'delete') {
				await deleteItemAction(
					sub(
						Liferay.Language.get('delete-task-confirmation-body'),
						getFormattedLabel(itemData.embedded.title)
					),
					itemData,
					loadData
				);
			}
			else if (action?.data?.id === 'assign-to') {
				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<EditAssigneeModalContent
							closeModal={closeModal}
							cmpProjectObjectEntryId={
								itemData.embedded
									.r_cmpProjectToCMPTasks_c_cmpProjectId
							}
							cmpTaskObjectEntryId={String(itemData.embedded.id)}
							cmpTaskObjectEntryTitle={itemData.embedded.title}
							loadData={loadData}
							value={itemData.embedded.assignTo}
						/>
					),
					size: 'md',
				});
			}
		},
		onBulkActionItemClick: async ({
			action,
			selectedData,
		}: {
			action: any;
			selectedData: any;
		}) => {
			const selectedItems = selectedData?.items ?? [];

			if (
				!selectedData?.selectAll &&
				selectedItems.length &&
				selectedItems.every(isWorkflowTask)
			) {
				const ContentComponent =
					WORKFLOW_BULK_ACTION_MODALS[action?.data?.id];

				if (!ContentComponent) {
					return;
				}

				const loadData = () =>
					Liferay.fire(FDS_EVENT.UPDATE_DISPLAY, {id});

				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<ContentComponent
							closeModal={closeModal}
							loadData={loadData}
							selectedData={selectedData}
						/>
					),
					size: 'md',
				});

				return;
			}

			if (action?.data?.id === 'assign-to') {
				const [cmpProjectObjectEntryId] =
					getCMPProjectObjectEntryIds(selectedItems);

				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<BulkEditAssigneeModalContent
							apiURL={otherProps.apiURL}
							closeModal={closeModal}
							cmpProjectObjectEntryId={cmpProjectObjectEntryId}
							dataSetId={id}
							selectedData={selectedData}
							value={{name: null}}
						/>
					),
					size: 'md',
				});
			}
			else if (action?.data?.id === 'delete') {
				deleteAssetEntriesBulkAction({
					apiURL: otherProps.apiURL,
					dataSetId: id,
					getCustomBulkDeleteMessage: (selectedData) => {
						if (selectedData.selectAll) {
							return {
								messages: [
									Liferay.Language.get(
										'delete-tasks-confirmation'
									),
								],
								title: Liferay.Language.get('delete-all-tasks'),
							};
						}
						else if (selectedData.items.length > 1) {
							return {
								messages: [
									Liferay.Language.get(
										'delete-tasks-confirmation'
									),
								],
								title: sub(
									Liferay.Language.get('delete-x-tasks'),
									[selectedData.items.length]
								),
							};
						}

						return {
							messages: [
								Liferay.Language.get(
									'delete-tasks-confirmation'
								),
							],
							title: Liferay.Language.get('delete-task'),
						};
					},
					selectedData,
					showConfirmationModal: true,
				});
			}
			else if (action?.data?.id === 'update-due-date') {
				openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						BulkEditDueDateModalContent({
							apiURL: otherProps?.apiURL,
							closeModal,
							dataSetId: id,
							selectedData,
						}),
					size: 'md',
				});
			}
			else if (action?.data?.id === 'update-state') {
				openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						BulkEditStateModalContent({
							apiURL: otherProps?.apiURL,
							closeModal,
							dataSetId: id,
							selectedData,
							states: additionalProps.states,
						}),
					size: 'md',
				});
			}
		},
		views: nonDefaultViews,
	};
}
