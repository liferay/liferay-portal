/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import CustomAuthorTableCell from './CustomAuthorTableCell';
import SampleInfoPanel from './SampleInfoPanel';

import type {IInternalRenderer} from '@liferay/frontend-data-set-web';

export default function propsTransformer({...otherProps}: any) {
	const customAuthorTableCellRenderer: IInternalRenderer = {
		component: CustomAuthorTableCell,
		name: 'customAuthorTableCellRenderer',
		type: 'internal',
	};

	return {
		...otherProps,
		customRenderers: {
			tableCell: [customAuthorTableCellRenderer],
		},
		infoPanelComponent: SampleInfoPanel,
	};
}
