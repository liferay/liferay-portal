/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function nextId(kind: string): string {
	const [value] = crypto.getRandomValues(new Uint32Array(1));

	return `${kind}-${value.toString(16).padStart(8, '0')}`;
}
