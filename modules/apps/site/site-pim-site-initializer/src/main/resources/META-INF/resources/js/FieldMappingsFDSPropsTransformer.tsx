/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import FieldMappingChannelFieldRenderer from './cell_renderers/FieldMappingChannelFieldRenderer';
import FieldMappingSourceAttributesRenderer from './cell_renderers/FieldMappingSourceAttributesRenderer';

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
					component: FieldMappingSourceAttributesRenderer,
					name: 'sourceAttributesTableCellRenderer',
					type: 'internal',
				},
			],
		},
		hideManagementBarInEmptyState: true,
	};
}
