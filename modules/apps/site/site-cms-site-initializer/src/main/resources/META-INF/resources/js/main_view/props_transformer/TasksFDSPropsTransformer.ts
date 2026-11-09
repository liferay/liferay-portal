/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DateRenderer, IInternalRenderer} from '@liferay/frontend-data-set-web';

import {openCMSModal} from '../../common/utils/openCMSModal';
import AssignToModalContent from '../home/modal/AssignToModalContent';
import UpdateDueDateModalContent from '../home/modal/UpdateDueDateModalContent';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import SimpleActionLinkRenderer from './cell_renderers/SimpleActionLinkRenderer';
import TaskStatusRenderer from './cell_renderers/TaskStatusRenderer';

export default function TasksFDSPropsTransformer({
	...otherProps
}: {
	apiURL: string;
	otherProps: any;
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: AuthorRenderer,
					name: 'authorTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						DateRenderer({value: itemData.embedded?.dateDue}),
					name: 'dueDateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: (props: any) =>
						SimpleActionLinkRenderer({
							...props,
							requiresUpdatePermission: false,
						}),
					name: 'simpleActionLinkTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: TaskStatusRenderer,
					name: 'taskStatusTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		async onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: {data?: {id?: string}};
			itemData: {embedded?: {dateDue?: string; id?: string}};
			loadData: () => Promise<void>;
		}) {
			const workflowTaskId = Number(itemData.embedded?.id);

			if (action?.data?.id === 'assignToMeWorkflowTask') {
				openCMSModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						AssignToModalContent({
							assignable: false,
							closeModal,
							loadData,
							workflowTaskId,
						}),
					size: 'md',
				});
			}
			else if (action?.data?.id === 'assignToWorkflowTask') {
				openCMSModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						AssignToModalContent({
							assignable: true,
							closeModal,
							loadData,
							workflowTaskId,
						}),
					size: 'md',
				});
			}
			else if (action?.data?.id === 'updateDueDateWorkflowTask') {
				openCMSModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						UpdateDueDateModalContent({
							closeModal,
							dueDate: itemData.embedded?.dateDue as string,
							loadData,
							workflowTaskId,
						}),
					size: 'md',
				});
			}
		},
	};
}
