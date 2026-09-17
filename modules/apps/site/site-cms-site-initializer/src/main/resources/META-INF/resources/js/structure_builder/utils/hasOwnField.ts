/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../types/Structure';
import isField from './isField';

export default function hasOwnField(children: Group['children']): boolean {
	for (const child of children.values()) {
		if (isField(child)) {
			return true;
		}

		if (
			child.type === 'group' &&
			!child.isRepeatable &&
			hasOwnField(child.children)
		) {
			return true;
		}
	}

	return false;
}
