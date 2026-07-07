/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function toPercent(value: number, total: number): string {
	const safeValue = Number.isFinite(value) ? Math.max(0, value) : 0;

	if (Number.isNaN(total) || total <= 0) {
		return (0).toFixed(1);
	}

	return ((safeValue / total) * 100).toFixed(1);
}
