/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../../types';
import {isGroup} from './isGroup';

export function updateGroup(
	root: Group,
	groupPath: number[],
	updater: (group: Group) => Group
): Group {
	if (!groupPath.length) {
		return updater(root);
	}

	const [head, ...rest] = groupPath;

	return {
		...root,
		items: root.items.map((node, index) => {
			if (index !== head) {
				return node;
			}

			if (!isGroup(node)) {
				throw new Error(
					`The path segment ${head} does not point to a group`
				);
			}

			return updateGroup(node, rest, updater);
		}),
	};
}
