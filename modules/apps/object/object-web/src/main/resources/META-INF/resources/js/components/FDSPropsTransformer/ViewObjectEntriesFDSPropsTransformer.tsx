/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IBulkActionItem} from '@liferay/frontend-data-set-web';
import React from 'react';

import DecimalDataRenderer from './FDSDataRenderers/DecimalDataRenderer';
import LocalizedMultiselectPicklistDataRenderer from './FDSDataRenderers/LocalizedMultiselectPicklistDataRenderer';
import LocalizedPicklistDataRenderer from './FDSDataRenderers/LocalizedPicklistDataRenderer';
import LocalizedRichTextDataRenderer from './FDSDataRenderers/LocalizedRichTextDataRenderer';
import LocalizedTextDataRenderer from './FDSDataRenderers/LocalizedTextDataRenderer';
import MultiselectPicklistDataRenderer from './FDSDataRenderers/MultiselectPicklistDataRenderer';
import ObjectEntryStatusDataRenderer from './FDSDataRenderers/ObjectEntryStatusDataRenderer';
import transformFDSBulkActions from './utils/transformFDSBulkActions';

type ObjectEntryStatusDataRendererProps = {
	itemData: ObjectEntry;
	restContextPath: string;
};

export default function ViewObjectEntriesFDSPropsTransformer({
	bulkActions,
	...otherProps
}: {
	bulkActions?: Array<IBulkActionItem>;
	[key: string]: any;
}) {
	return {
		...otherProps,
		bulkActions:
			bulkActions && transformFDSBulkActions<ObjectEntry>(bulkActions),
		customDataRenderers: {
			decimalDataRenderer: DecimalDataRenderer,
			localizedMultiselectPicklistDataRenderer:
				LocalizedMultiselectPicklistDataRenderer,
			localizedPicklistDataRenderer: LocalizedPicklistDataRenderer,
			localizedRichTextDataRenderer: LocalizedRichTextDataRenderer,
			localizedTextDataRenderer: LocalizedTextDataRenderer,
			multiselectPicklistDataRenderer: MultiselectPicklistDataRenderer,
			statusDataRenderer: (props: ObjectEntryStatusDataRendererProps) => (
				<ObjectEntryStatusDataRenderer
					{...props}
					restContextPath={otherProps.apiURL}
				/>
			),
		},
		onActionDropdownItemClick({
			action,
			itemData,
		}: {
			action: {data: {id: string}};
			itemData: any;
		}) {
			if (action.data.id === 'deleteObjectEntry') {
				Liferay.fire('openModalDeleteObjectEntry', {
					objectEntry: itemData,
				});
			}
		},
		onBulkActionItemClick: async ({
			action,
			selectedData,
		}: {
			action: {data: {id: string}};
			selectedData: any;
		}) => {
			if (action?.data?.id === 'delete') {
				Liferay.fire('openModalBulkDeleteObjectEntries', {
					selectedData,
				});
			}
		},
	};
}
