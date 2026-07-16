/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../../types';
import {splitPath} from './splitPath';
import {updateGroup} from './updateGroup';

export function deleteRule(root: Group, path: number[]): Group {
	const [groupPath, index] = splitPath(path);

	return updateGroup(root, groupPath, (group) => ({
		...group,
		items: group.items.filter((_node, itemIndex) => itemIndex !== index),
	}));
}
