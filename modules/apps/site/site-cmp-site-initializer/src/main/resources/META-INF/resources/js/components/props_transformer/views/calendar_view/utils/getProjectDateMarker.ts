/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface ProjectDates {
	dueDate?: string;
	startDate?: string;
}

/**
 * Returns the project-date marker for a calendar day cell, or null when the
 * cell matches neither the project's start nor due date. All dates are compared
 * as "yyyy-MM-dd" strings, so the caller must normalize them first.
 */
export default function getProjectDateMarker(
	cellDate: string,
	projectDates: ProjectDates | null
): 'dueDate' | 'startDate' | null {
	if (!projectDates) {
		return null;
	}

	if (projectDates.startDate === cellDate) {
		return 'startDate';
	}

	if (projectDates.dueDate === cellDate) {
		return 'dueDate';
	}

	return null;
}
