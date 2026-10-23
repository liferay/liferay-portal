/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	confirmAndDeleteEntryAction,
	styleDeleteAction,
} from '@liferay/site-cms-site-initializer';
import {sub} from 'frontend-js-web';

import ConnectorNameRenderer from './cell_renderers/ConnectorNameRenderer';
import ConnectorStatusRenderer from './cell_renderers/ConnectorStatusRenderer';
import LastExecutionStatusRenderer from './cell_renderers/LastExecutionStatusRenderer';
import MappingStatusRenderer from './cell_renderers/MappingStatusRenderer';

export default function propsTransformer({
	itemsActions,
	...props
}: {
	itemsActions?: any[];
	[key: string]: any;
}) {
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
				{
					component: MappingStatusRenderer,
					name: 'mappingStatusTableCellRenderer',
					type: 'internal',
				},
				{
					component: LastExecutionStatusRenderer,
					name: 'lastExecutionStatusTableCellRenderer',
					type: 'internal',
				},
			],
		},
		hideManagementBarInEmptyState: true,
		itemsActions: itemsActions?.map(styleDeleteAction),
		onActionDropdownItemClick({
			action,
			event,
			itemData,
			loadData,
		}: {
			action: {data?: {id?: string}};
			event: Event;
			itemData: {
				actions: {delete: {href: string; method: string}};
				name: string;
			};
			loadData: () => void;
		}) {
			if (action?.data?.id !== 'delete') {
				return;
			}

			event.preventDefault();

			confirmAndDeleteEntryAction({
				bodyHTML: Liferay.Language.get(
					'the-connector-and-all-its-field-mappings-will-be-deleted.-this-action-cannot-be-undone'
				),
				deleteAction: itemData.actions.delete,
				loadData,
				successMessage: sub(
					Liferay.Language.get('x-has-been-permanently-deleted'),
					Liferay.Util.escapeHTML(itemData.name)
				),
				title: sub(Liferay.Language.get('delete-x'), itemData.name),
			});
		},
	};
}
