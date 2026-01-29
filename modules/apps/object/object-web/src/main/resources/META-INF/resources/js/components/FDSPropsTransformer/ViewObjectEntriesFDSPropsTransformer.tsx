/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import DecimalDataRenderer from './FDSDataRenderers/DecimalDataRenderer';
import MultiselectPicklistDataRenderer from './FDSDataRenderers/MultiselectPicklistDataRenderer';
import ObjectEntryStatusDataRenderer from './FDSDataRenderers/ObjectEntryStatusDataRenderer';

type ObjectEntryStatusDataRendererProps = {
	itemData: ObjectEntry;
	restContextPath: string;
};

export default function ViewObjectEntriesFDSPropsTransformer({...otherProps}) {
	return {
		...otherProps,
		customDataRenderers: {
			decimalDataRenderer: DecimalDataRenderer,
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
