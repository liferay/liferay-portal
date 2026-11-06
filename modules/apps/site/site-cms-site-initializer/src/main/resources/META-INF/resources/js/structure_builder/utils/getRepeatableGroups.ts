/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group, RepeatableGroup, Structure} from '../types/Structure';
import isRepeatableGroup from './isRepeatableGroup';

export default function getRepeatableGroups(
	children: Group['children'] | Structure['children']
): RepeatableGroup[] {
	const repeatableGroups: RepeatableGroup[] = [];

	for (const child of children.values()) {
		if (isRepeatableGroup(child)) {
			repeatableGroups.push(child);
		}
		else if (child.type === 'group') {
			repeatableGroups.push(...getRepeatableGroups(child.children));
		}
	}

	return repeatableGroups;
}

export function hasRepeatableGroups(
	children: Group['children'] | Structure['children']
): boolean {
	for (const child of children.values()) {
		if (
			isRepeatableGroup(child) ||
			(child.type === 'group' && hasRepeatableGroups(child.children))
		) {
			return true;
		}
	}

	return false;
}
