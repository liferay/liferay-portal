/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function isOverdue({
	dueDate,
	state,
}: {
	dueDate?: string;
	state?: {
		key: string;
		name: string;
	};
}): boolean {
	return (
		Boolean(dueDate) &&
		state?.key !== 'done' &&
		dueDate!.slice(0, 10) < new Date().toISOString().slice(0, 10)
	);
}
