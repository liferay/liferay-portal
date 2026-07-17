/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group, Rule} from '../../types';
import {isGroup} from './isGroup';

export function flattenRules(group: Group): Rule[] {
	return group.items.flatMap((node) =>
		isGroup(node) ? flattenRules(node) : [node]
	);
}
