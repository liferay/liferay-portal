/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AudiencesCriteria, Group} from '../../types';
import {createRule} from './createRule';
import {insertAt} from './insertAt';
import {updateGroup} from './updateGroup';

export function addRule(
	root: Group,
	groupPath: number[],
	audiencesCriteria: AudiencesCriteria,
	index?: number
): Group {
	return updateGroup(root, groupPath, (group) => ({
		...group,
		items: insertAt(
			group.items,
			index ?? group.items.length,
			createRule(audiencesCriteria)
		),
	}));
}
