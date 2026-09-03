/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	DateRenderer,
	FDS_EVENT,
	IInternalRenderer,
	IView,
	getItemActionURL,
} from '@liferay/frontend-data-set-web';
import {addOnClickToCreationMenuItems} from '@liferay/site-cms-site-initializer';
import React from 'react';

import {styleActions} from '../../utils/actionStyles';
import {
	installCMPTabPersistence,
	registerTabFDS,
} from '../../utils/cmpTabPersistence';
import {WORKFLOW_TASK_ACTION_LINK_ID} from '../../utils/constants';
import {openCMPModal} from '../../utils/openCMPModal';
import {transformFDSBulkActions} from '../../utils/transformFDSBulkActions';
import {TaskAction, WorkflowTaskItemData} from '../../utils/types';
import WORKFLOW_TASK_MODALS from '../../utils/workflowTaskModals';
import BulkEditWorkflowAssigneeModalContent from '../modal/BulkEditWorkflowAssigneeModalContent';
import BulkEditWorkflowDueDateModalContent from '../modal/BulkEditWorkflowDueDateModalContent';
import BulkUpdateWorkflowStateModalContent from '../modal/BulkUpdateWorkflowStateModalContent';
import ACTIONS from './actions/creationMenuActions';
import {cmpWorkflowTasksFDSAtom} from './atoms';
import WorkflowStateRenderer from './cell_renderers/WorkflowStateRenderer';
import WorkflowTaskActionLinkRenderer from './cell_renderers/WorkflowTaskActionLinkRenderer';

type BulkModalProps = {
	closeModal: () => void;
	getTaskURL: (task: WorkflowTaskItemData) => string;
	loadData: () => void;
	selectedData: any;
};

const BULK_ACTION_MODALS: Record<
	string,
	{
		contentComponent: React.ComponentType<BulkModalProps>;
		size?: 'lg' | 'sm';
	}
> = {
	'assign-to': {contentComponent: BulkEditWorkflowAssigneeModalContent},
	'update-due-date': {contentComponent: BulkEditWorkflowDueDateModalContent},
	'update-state': {
		contentComponent: BulkUpdateWorkflowStateModalContent,
		size: 'lg',
	},
};

export default function WorkflowTasksFDSPropsTransformer({
	bulkActions = [],
	creationMenu,
	id,
	itemsActions = [],
	views,
	...otherProps
}: {
	apiURL: string;
	bulkActions?: any[];
	creationMenu: any;
	id: string;
	itemsActions?: any[];
	otherProps: any;
	views: IView[];
}) {
	const nonDefaultViews = views
		.filter((view) => view.name !== 'kanban')
		.map((view) => ({
			...view,
			default: false,
			initialPaginationDelta: 20,
		}));

	registerTabFDS(id, 2);
	installCMPTabPersistence();

	return {
		...otherProps,
		atom: cmpWorkflowTasksFDSAtom,
		bulkActions: transformFDSBulkActions(bulkActions).map((action) => ({
			...action,
			isDisabled: ({
				allItemsSelectedActive,
				selectedItems = [],
			}: {
				allItemsSelectedActive: boolean;
				selectedItems?: WorkflowTaskItemData[];
			}) => {
				if (allItemsSelectedActive) {
					return true;
				}

				if (action?.data?.id !== 'update-state') {
					return false;
				}

				return !selectedItems.every(
					({embedded}) =>
						embedded?.assignedToMe && !embedded?.completed
				);
			},
		})),
		creationMenu: creationMenu && {
			...creationMenu,
			primaryItems: addOnClickToCreationMenuItems(
				creationMenu.primaryItems,
				ACTIONS
			),
		},
		customRenderers: {
			tableCell: [
				{
					component: ({actions, itemData}) => (
						<WorkflowTaskActionLinkRenderer
							actionId={WORKFLOW_TASK_ACTION_LINK_ID}
							actions={actions}
							itemData={itemData}
						/>
					),
					name: 'assetTitleTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						itemData.embedded?.objectReviewed?.assetType ?? '-',
					name: 'assetTypeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						itemData.embedded?.title ?? itemData.embedded?.name,
					name: 'taskTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						DateRenderer({value: itemData.embedded?.dateDue}),
					name: 'dueDateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						itemData.embedded?.objectReviewed?.projectTitle ?? '-',
					name: 'projectTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) => (
						<WorkflowStateRenderer embedded={itemData.embedded} />
					),
					name: 'workflowStateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						DateRenderer({value: itemData.embedded?.modifiedDate}),
					name: 'lastActivityDateTableCellRenderer',
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
			itemData: WorkflowTaskItemData;
			loadData: () => Promise<void>;
		}) {
			const modal = WORKFLOW_TASK_MODALS[action?.data?.id];

			if (!modal) {
				return;
			}

			await openCMPModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: () => void}) =>
					modal({
						closeModal,
						dueDate: itemData.embedded?.dateDue,
						loadData,
						workflowTaskId: itemData.embedded?.id,
					}),
				size: 'md',
			});
		},
		onBulkActionItemClick: async ({
			action,
			selectedData,
		}: {
			action: any;
			selectedData: any;
		}) => {
			const modal = BULK_ACTION_MODALS[action?.data?.id];

			if (!modal) {
				return;
			}

			const {contentComponent: ContentComponent, size} = modal;

			const loadData = () => Liferay.fire(FDS_EVENT.UPDATE_DISPLAY, {id});

			await openCMPModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: () => void}) => (
					<ContentComponent
						closeModal={closeModal}
						getTaskURL={(task: WorkflowTaskItemData) =>
							getItemActionURL(
								itemsActions,
								WORKFLOW_TASK_ACTION_LINK_ID,
								task
							)
						}
						loadData={loadData}
						selectedData={selectedData}
					/>
				),
				size,
			});
		},
		views: nonDefaultViews,
	};
}
