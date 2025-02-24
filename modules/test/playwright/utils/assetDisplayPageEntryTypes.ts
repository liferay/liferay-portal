/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const ASSET_DISPLAY_PAGE_ENTRY_TYPES: Record<
	AssetDisplayPageEntryType,
	string
> = {
	default: '1',
	inherited: '3',
	none: '0',
	specific: '2',
} as const;
