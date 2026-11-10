/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface UnifiedSidebarItem {
	canonicalName?: string;
	filterOnly?: boolean;
	href?: string;
	id: string;
	items?: Array<UnifiedSidebarItem>;
	label: string;
	leadingIcon?: string;
	parentLabel?: string;
}

export type UnifiedSidebarItemsMap = Record<string, Array<UnifiedSidebarItem>>;
