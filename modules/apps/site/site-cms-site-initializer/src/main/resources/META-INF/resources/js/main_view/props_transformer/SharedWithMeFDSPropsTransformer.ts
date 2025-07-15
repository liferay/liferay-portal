/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import AuthorRenderer from './cell_renderers/AuthorRenderer';
import SharedItemRenderer from './cell_renderers/SharedItemRenderer';

export default function SharedWithMeFDSPropsTransformer({
	...otherProps
}: {
	otherProps: any;
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: AuthorRenderer,
					name: 'authorTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: SharedItemRenderer,
					name: 'sharedItemTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
	};
}
