/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../contexts/StateContext';

export default function findAvailableFieldName(
	fields: State['fields'],
	name: string
) {
	const exists = (name: string) =>
		[...fields.values()].some(
			(field) =>
				field.type !== 'referenced-structure' && field.name === name
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
