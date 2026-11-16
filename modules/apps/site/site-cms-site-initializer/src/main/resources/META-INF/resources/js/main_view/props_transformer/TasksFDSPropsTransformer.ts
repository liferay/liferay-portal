/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import {tasksFDSAtom} from '../tasks/atoms';
import {onWorkflowTaskActionClick} from '../tasks/onWorkflowTaskActionClick';
import AssigneeRenderer from './cell_renderers/AssigneeRenderer';
import DueDateRenderer from './cell_renderers/DueDateRenderer';
import SimpleActionLinkRenderer from './cell_renderers/SimpleActionLinkRenderer';
import WorkflowStateRenderer from './cell_renderers/WorkflowStateRenderer';

export default function TasksFDSPropsTransformer({
	...otherProps
}: {
	apiURL: string;
	otherProps: any;
}) {
	return {
		...otherProps,
		atom: tasksFDSAtom,
		customRenderers: {
			tableCell: [
				{
					component: AssigneeRenderer,
					name: 'assigneeTableCellRenderer',
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
					component: DueDateRenderer,
					name: 'dueDateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: WorkflowStateRenderer,
					name: 'workflowStateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		onActionDropdownItemClick: onWorkflowTaskActionClick,
	};
}
