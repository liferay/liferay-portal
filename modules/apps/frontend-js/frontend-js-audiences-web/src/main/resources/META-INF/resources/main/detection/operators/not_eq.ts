/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {checkTypes} from '../util';

export function notEq<T extends boolean | number | string>(
	value: T,
	expected: T
): boolean {
	checkTypes(
		value,
		['boolean', 'number', 'string'],
		`Operator 'not_eq' value`
	);

	return value !== expected;
}
