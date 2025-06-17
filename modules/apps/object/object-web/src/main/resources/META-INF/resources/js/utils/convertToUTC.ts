/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * /**
 * Receives a date and converts it from local to utc format
 */
export function convertToUTC(value: string | undefined) {
	if (!value) {
		return null;
	}

	const date = new Date(value);

	const dateInUTCFormat =
		date.getFullYear() +
		'-' +
		`0${date.getMonth() + 1}`.slice(-2) +
		'-' +
		`0${date.getDate()}`.slice(-2) +
		'T' +
		`0${date.getHours()}`.slice(-2) +
		':' +
		`0${date.getMinutes()}`.slice(-2) +
		'Z';

	return dateInUTCFormat;
}
