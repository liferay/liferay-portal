/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function toPercent(value: number, total: number): number {
	if (total <= 0) {
		return 0;
	}

	return Math.round((Math.max(0, value) / total) * 100);
}
