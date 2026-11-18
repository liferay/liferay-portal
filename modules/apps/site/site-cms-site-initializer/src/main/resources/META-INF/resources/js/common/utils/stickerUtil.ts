/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type StickerColor =
	| 'outline-0'
	| 'outline-1'
	| 'outline-2'
	| 'outline-3'
	| 'outline-4'
	| 'outline-5'
	| 'outline-6'
	| 'outline-7'
	| 'outline-8'
	| 'outline-9';

export function getInitials(name: string) {
	return name
		.trim()
		.split(/\s+/)
		.slice(0, 2)
		.map((word) => word.charAt(0))
		.join('')
		.toUpperCase();
}

export function getStickerColor(key: string) {
	const hash = [...key].reduce(
		(sum, character) => sum + character.charCodeAt(0),
		0
	);

	return `outline-${hash % 10}` as StickerColor;
}
