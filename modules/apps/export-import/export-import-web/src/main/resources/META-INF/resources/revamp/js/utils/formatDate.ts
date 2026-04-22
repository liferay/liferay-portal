/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function formatDate(date: string): string {
	const dateObject = new Date(date);

	const options: Intl.DateTimeFormatOptions = {
		day: 'numeric',
		hour: 'numeric',
		hour12: true,
		minute: 'numeric',
		month: 'short',
		second: 'numeric',
		year: 'numeric',
	};

	const formattedDate = new Intl.DateTimeFormat('en-US', options).format(
		dateObject
	);

	return formattedDate;
}
