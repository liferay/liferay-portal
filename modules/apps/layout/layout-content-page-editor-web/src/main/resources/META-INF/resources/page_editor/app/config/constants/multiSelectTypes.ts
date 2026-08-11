/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const MULTI_SELECT_TYPES = {
	range: 'range',
	simple: 'simple',
} as const;

export type MultiSelectType =
	(typeof MULTI_SELECT_TYPES)[keyof typeof MULTI_SELECT_TYPES];
