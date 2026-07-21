/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {checkTypes} from '../util';

export function notEq(
	value: Set<string> | boolean | number | string,
	expected: boolean | number | string
): boolean {
	checkTypes(
		value,
		['Set', 'boolean', 'number', 'string'],
		`Operator 'not_eq' value`
	);

	if (value instanceof Set) {
		return !value.has(expected as string);
	}

	return value !== expected;
}
