/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '../utils/types';
import DefaultRenderer from './DefaultRenderer';
import {INTERNAL_CELL_RENDERERS} from './InternalCellRenderer';

export function getInternalCellRenderer(name: string): IInternalRenderer {
	const renderer = INTERNAL_CELL_RENDERERS.find(
		(renderer) => renderer.name === name
	);

	if (!renderer) {
		return {
			component: DefaultRenderer,
			label: Liferay.Language.get('default'),
			name: 'default',
			type: 'internal',
		};
	}

	return renderer;
}
