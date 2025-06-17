/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type Options = {
	limit?: number;
	style?: 'camel' | 'pascal';
};

export default function normalizeName(
	name: string,
	{limit, style}: Options = {}
) {

	// Remove special characters, replace dashes with spaces and split

	let splits = name
		.replace(/[^A-Z0-9 -]/gi, '')
		.replaceAll('-', ' ')
		.split(' ');

	// Convert to correct style if specified

	if (style === 'pascal') {
		splits = splits.map(
			(split) =>
				split.charAt(0).toUpperCase() + split.slice(1).toLowerCase()
		);
	}
	else if (style === 'camel') {
		splits = splits.map((split, index) =>
			index === 0
				? split.toLowerCase()
				: split.charAt(0).toUpperCase() + split.slice(1).toLowerCase()
		);
	}

	const result = splits.join('');

	if (limit) {
		return result.substring(0, limit);
	}

	return splits.join('');
}
