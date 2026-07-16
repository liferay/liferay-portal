/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../../types';
import {isGroup} from './isGroup';

export function deleteEmptyGroups(group: Group): Group {
	return {
		...group,
		items: group.items
			.map((node) => (isGroup(node) ? deleteEmptyGroups(node) : node))
			.filter((node) => !isGroup(node) || !!node.items.length),
	};
}
