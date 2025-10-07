/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function getPaginatedList({
	delta,
	items,
	page,
}: {
	delta: number;
	items: any[];
	page: number;
}) {
	const startingIndex = page * delta - delta;

	let endingIndex = startingIndex + delta;

	if (endingIndex > items.length) {
		endingIndex = items.length;
	}

	return items.slice(startingIndex, endingIndex);
}
