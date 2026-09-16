/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../contexts/StateContext';
import {Structure} from '../types/Structure';

export default function findAvailableFieldName(
	children: Structure['children'],
	deletedChildren: State['history']['deletedChildren'],
	name: string
) {
	const deletedFields =
		deletedChildren?.filter(
			(child) =>
				child.type !== 'referenced-structure' && child.type !== 'group'
		) || [];

	const fields = [...deletedFields, ...children.values()];

	const exists = (name: string) =>
		fields.some(
			(child) =>
				child.type !== 'referenced-structure' &&
				child.type !== 'group' &&
				child.name === name
		);

	if (!exists(name)) {
		return name;
	}

	let i = 1;

	while (exists(`${name}${i}`)) {
		i++;
	}

	return `${name}${i}`;
}
