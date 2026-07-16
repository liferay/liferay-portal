/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../../types';
import {isGroup} from './isGroup';

export function detachNode(group: Group, id: string): Group {
	return {
		...group,
		items: group.items
			.filter((node) => node.id !== id)
			.map((node) => (isGroup(node) ? detachNode(node, id) : node)),
	};
}
