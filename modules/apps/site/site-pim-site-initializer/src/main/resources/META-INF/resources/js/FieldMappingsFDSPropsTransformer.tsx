/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';

import FieldMappingChannelFieldRenderer from './cell_renderers/FieldMappingChannelFieldRenderer';
import FieldMappingSourceAttributesRenderer from './cell_renderers/FieldMappingSourceAttributesRenderer';

export default function propsTransformer(props: {[key: string]: any}) {
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
		onActionDropdownItemClick({action, itemData, loadData}: any) {
			if (action?.data?.id !== 'clear') {
				return;
			}

			clearFieldMappings(
				action.data.apiURL,
				itemData.fieldMappingIds,
				loadData
			);
		},
	};
}

function clearFieldMappings(
	apiURL: string,
	fieldMappingIds: number[],
	loadData: Function
) {
	if (!fieldMappingIds?.length) {
		return;
	}

	Promise.all(
		fieldMappingIds.map((fieldMappingId) =>
			fetch(`${apiURL}/${fieldMappingId}`, {method: 'DELETE'})
		)
	)
		.then((responses) => {
			if (responses.some(({ok}) => !ok)) {
				throw new Error();
			}

			loadData();

			openToast({
				message: Liferay.Language.get(
					'your-request-completed-successfully'
				),
				type: 'success',
			});
		})
		.catch(() => {
			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		});
}
