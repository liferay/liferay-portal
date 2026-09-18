/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import FieldMappingChannelFieldRenderer from './cell_renderers/FieldMappingChannelFieldRenderer';
import FieldMappingRequiredRenderer from './cell_renderers/FieldMappingRequiredRenderer';
import FieldMappingSourceAttributeRenderer from './cell_renderers/FieldMappingSourceAttributeRenderer';
import FieldMappingStatusRenderer from './cell_renderers/FieldMappingStatusRenderer';

export default function propsTransformer({...props}: {[key: string]: any}) {
	return {
		...props,
		customRenderers: {
			tableCell: [
				{
					component: FieldMappingChannelFieldRenderer,
					name: 'channelFieldTableCellRenderer',
					type: 'internal',
				},
				{
					component: FieldMappingRequiredRenderer,
					name: 'requiredTableCellRenderer',
					type: 'internal',
				},
				{
					component: FieldMappingSourceAttributeRenderer,
					name: 'sourceAttributeTableCellRenderer',
					type: 'internal',
				},
				{
					component: FieldMappingStatusRenderer,
					name: 'statusTableCellRenderer',
					type: 'internal',
				},
			],
		},
		hideManagementBarInEmptyState: true,
	};
}
