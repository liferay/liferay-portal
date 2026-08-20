/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	DateRenderer,
	FDS_PAGINATION_DELTA_ALL,
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
import getCMPProjectObjectEntryIds from '../../utils/getCMPProjectObjectEntryIds';
import {getFormattedLabel} from '../../utils/getFormattedText';
import {openCMPModal} from '../../utils/openCMPModal';
import {transformFDSBulkActions} from '../../utils/transformFDSBulkActions';
import {ProjectTaskItemData, TaskAction} from '../../utils/types';
import StateLabel from '../StateLabel';
import BulkEditAssigneeModalContent from '../modal/BulkEditAssigneeModalContent';
import BulkEditDueDateModalContent from '../modal/BulkEditDueDateModalContent';
import BulkEditStateModalContent from '../modal/BulkEditStateModalContent';
import EditAssigneeModalContent from '../modal/EditAssigneeModalContent';
import UpdateDueDateModalContent from '../modal/UpdateDueDateModalContent';
import ACTIONS from './actions/creationMenuActions';
import {cmpTasksFDSAtom} from './atoms';
import AssigneeRenderer from './cell_renderers/AssigneeRenderer';
import CalendarView from './views/calendar_view/CalendarView';
import KanbanView from './views/kanban_view/KanbanView';

export default function ProjectTasksFDSPropsTransformer({
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

	registerTabFDS(id, 1);
	installCMPTabPersistence();

	const calendarView: IView = {
		component: (props: any) =>
			CalendarView({
				...props,
				cmpProjectObjectDefinitionId:
					additionalProps.cmpProjectObjectDefinitionId,
				cmpProjectObjectEntryId:
					additionalProps.cmpProjectObjectEntryId,
				hasAddTaskPermission: additionalProps.hasAddTaskPermission,
			}),
		default: false,
		initialPaginationDelta: FDS_PAGINATION_DELTA_ALL,
		label: Liferay.Language.get('calendar'),
		name: 'calendar',
		schema: {
			description: 'description',
			image: 'imageURL',
			link: '',
			sticker: '',
			symbol: '',
			title: 'embedded.title',
		},
		selectable: false,
		showPagination: false,
		thumbnail: 'calendar',
	};

	const kanbanView: IView = {
		component: (props: any) =>
			KanbanView({
				...props,
				cmpProjectObjectDefinitionId:
					additionalProps.cmpProjectObjectDefinitionId,
				cmpProjectObjectEntryId:
					additionalProps.cmpProjectObjectEntryId,
				hasAddTaskPermission: additionalProps.hasAddTaskPermission,
			}),
		default: false,
		initialPaginationDelta: FDS_PAGINATION_DELTA_ALL,
		label: Liferay.Language.get('kanban'),
		name: 'kanban',
		schema: {
			description: 'description',
			image: 'imageURL',
			link: '',
			sticker: '',
			symbol: '',
			title: 'embedded.title',
		},
		selectable: false,
		showPagination: false,
		thumbnail: 'columns',
	};

	return {
		...otherProps,
		atom: cmpTasksFDSAtom,
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

					if (!selectedItems?.length) {
						return true;
					}

					const cmpProjectObjectEntryIds =
						getCMPProjectObjectEntryIds(selectedItems);

					return cmpProjectObjectEntryIds.size === 1;
				},
			}))
		),
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
					component: ({itemData}) => (
						<AssigneeRenderer
							image={itemData.embedded?.assignTo?.portrait}
							name={itemData.embedded?.assignTo?.name}
						/>
					),
					name: 'assigneeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						DateRenderer({value: itemData.embedded?.dueDate}),
					name: 'dueDateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						itemData.embedded?.r_cmpProjectToCMPTasks_c_cmpProject
							?.title,
					name: 'projectTitleTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({actions, itemData, options}) =>
						SimpleActionLinkRenderer({
							actions,
							itemData,
							options,
							value: itemData.embedded?.title,
						}),
					name: 'simpleActionLinkTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						StateLabel({
							dueDate: itemData.embedded?.dueDate,
							state: itemData.embedded?.state,
						}),
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
			itemData: ProjectTaskItemData;
			loadData: () => Promise<void>;
		}) {
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
			else if (action?.data?.id === 'update-due-date') {
				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<UpdateDueDateModalContent
							closeModal={closeModal}
							cmpTaskObjectEntryId={String(itemData.embedded.id)}
							cmpTaskObjectEntryTitle={itemData.embedded.title}
							dueDate={itemData.embedded.dueDate}
							loadData={loadData}
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
			if (action?.data?.id === 'assign-to') {
				const [cmpProjectObjectEntryId] = getCMPProjectObjectEntryIds(
					selectedData?.items ?? []
				);

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
		views: [...nonDefaultViews, kanbanView, calendarView],
	};
}
