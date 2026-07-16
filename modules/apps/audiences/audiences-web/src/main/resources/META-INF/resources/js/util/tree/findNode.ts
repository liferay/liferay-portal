/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CriteriaNode, Group} from '../../types';
import {isGroup} from './isGroup';

export function findNode(group: Group, id: string): CriteriaNode | undefined {
	for (const node of group.items) {
		if (node.id === id) {
			return node;
		}

		if (isGroup(node)) {
			const found = findNode(node, id);

			if (found) {
				return found;
			}
		}
	}

	return undefined;
}
