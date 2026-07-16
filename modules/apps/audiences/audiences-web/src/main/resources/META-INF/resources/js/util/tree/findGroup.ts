/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../../types';
import {findNode} from './findNode';
import {isGroup} from './isGroup';

export function findGroup(root: Group, groupId: string): Group | undefined {
	if (root.id === groupId) {
		return root;
	}

	const node = findNode(root, groupId);

	return node && isGroup(node) ? node : undefined;
}
