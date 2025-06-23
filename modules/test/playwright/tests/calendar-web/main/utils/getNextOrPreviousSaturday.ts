/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getNextOrPreviousSaturday(referenceDate: Date): Date {
	const dayOfWeek = referenceDate.getDay();

	const result = new Date(referenceDate);
	const isFirstHalfOfMonth = referenceDate.getDate() <= 15;

	if (isFirstHalfOfMonth) {
		const daysUntilSaturday = (6 - dayOfWeek + 7) % 7 || 7;

		result.setDate(referenceDate.getDate() + daysUntilSaturday);
	}
	else {
		const daysSinceSaturday = (dayOfWeek + 1) % 7 || 7;

		result.setDate(referenceDate.getDate() - daysSinceSaturday);
	}

	return result;
}
