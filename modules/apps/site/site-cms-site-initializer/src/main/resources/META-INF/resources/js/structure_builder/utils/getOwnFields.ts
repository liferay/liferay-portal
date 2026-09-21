/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../types/Structure';
import {Field} from './field';
import isField from './isField';

export default function getOwnFields(children: Group['children']): Field[] {
	const fields: Field[] = [];

	for (const child of children.values()) {
		if (isField(child)) {
			fields.push(child);
		}
		else if (child.type === 'group' && !child.isRepeatable) {
			fields.push(...getOwnFields(child.children));
		}
	}

	return fields;
}
