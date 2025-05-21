/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClaySticker from '@clayui/sticker';

export const spaceLogoColors = [
	'outline-0',
	'outline-1',
	'outline-2',
	'outline-3',
	'outline-4',
	'outline-5',
	'outline-6',
	'outline-7',
	'outline-8',
	'outline-9',
] as const;

export function getSpaceLogoColor(char: string) {
	const validDisplayTypes: React.ComponentProps<
		typeof ClaySticker
	>['displayType'][] = [...spaceLogoColors];

	return validDisplayTypes[char.charCodeAt(0) % validDisplayTypes.length];
}
