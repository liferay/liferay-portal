/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AudiencesCriteria, Group} from '../../types';
import {createRule} from './createRule';
import {findParent} from './findParent';
import {insertIntoGroup} from './insertIntoGroup';
import {unwrapRedundantGroups} from './unwrapRedundantGroups';
import {wrapNode} from './wrapNode';

export function addGroup(
	root: Group,
	targetId: string,
	audiencesCriteria: AudiencesCriteria,
	conjunction = 'AND'
): Group {
	const rule = createRule(audiencesCriteria);

	const parent = findParent(root, targetId);

	if (parent && parent.items.length === 1) {
		return insertIntoGroup(root, parent.id, rule, 1);
	}

	return unwrapRedundantGroups(wrapNode(root, targetId, rule, conjunction));
}
