/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Structure} from '../types/Structure';

export default function findAvailableFieldName(
	children: Structure['children'],
	name: string
) {
	const exists = (name: string) =>
		[...children.values()].some(
			(child) =>
				child.type !== 'referenced-structure' && child.name === name
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
