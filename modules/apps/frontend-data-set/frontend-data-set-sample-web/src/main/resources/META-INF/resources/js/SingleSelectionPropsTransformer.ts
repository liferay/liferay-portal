/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	EConfigInURLBehavior,
	IInternalRenderer,
} from '@liferay/frontend-data-set-web';

import CustomAuthorTableCell from './CustomAuthorTableCell';
import SampleInfoPanel from './SampleInfoPanel';

export default function propsTransformer({...otherProps}: any) {
	const customAuthorTableCellRenderer: IInternalRenderer = {
		component: CustomAuthorTableCell,
		name: 'customAuthorTableCellRenderer',
		type: 'internal',
	};

	return {
		...otherProps,
		configInURLBehavior: EConfigInURLBehavior.OFF,
		creationMenu: {
			primaryItems: [
				{
					href: '#',
					icon: 'plus',
					label: 'Create New Item',
					onClick: () => alert('Create New Item clicked!'),
				},
				{
					data: {id: 'importItems'},
					icon: 'upload',
					items: [
						{
							data: {id: 'importFromFile'},
							label: 'From File',
							onClick: () => alert('From File clicked!'),
						},
						{
							data: {id: 'importFromURL'},
							label: 'From URL',
							onClick: () => alert('From URL clicked!'),
						},
					],
					label: 'Import Items',
					type: 'contextual',
				},
			],
		},
		customRenderers: {
			tableCell: [customAuthorTableCellRenderer],
		},
		infoPanelComponent: SampleInfoPanel,
	};
}
