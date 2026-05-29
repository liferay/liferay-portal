/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import LevelTableCell from './LevelTableCell';

import type {IInternalRenderer} from '@liferay/frontend-data-set-web';

export default function propsTransformer(props: any) {
	const levelTableCellRenderer: IInternalRenderer = {
		component: LevelTableCell,
		name: 'levelTableCellRenderer',
		type: 'internal',
	};

	return {
		...props,
		customRenderers: {
			...props.customRenderers,
			tableCell: [
				...(props.customRenderers?.tableCell ?? []),
				levelTableCellRenderer,
			],
		},
	};
}
