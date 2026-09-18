/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ConnectorNameRenderer from './cell_renderers/ConnectorNameRenderer';
import ConnectorStatusRenderer from './cell_renderers/ConnectorStatusRenderer';

export default function propsTransformer({...props}: {[key: string]: any}) {
	return {
		...props,
		customRenderers: {
			tableCell: [
				{
					component: ConnectorNameRenderer,
					name: 'nameTableCellRenderer',
					type: 'internal',
				},
				{
					component: ConnectorStatusRenderer,
					name: 'statusTableCellRenderer',
					type: 'internal',
				},
			],
		},
		hideManagementBarInEmptyState: true,
	};
}
