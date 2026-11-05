/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DateRenderer, IInternalRenderer} from '@liferay/frontend-data-set-web';

import AssetTitleActionLinkRenderer from './cell_renderers/AssetTitleActionLinkRenderer';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
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
					component: AssetTitleActionLinkRenderer,
					name: 'assetTitleTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
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
					component: TaskStatusRenderer,
					name: 'taskStatusTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
	};
}
