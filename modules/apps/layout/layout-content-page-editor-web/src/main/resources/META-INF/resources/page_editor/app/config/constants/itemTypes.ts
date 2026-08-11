/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const ITEM_TYPES = {
	editable: 'editable',
	inlineContent: 'inlineContent',
	layoutDataItem: 'layoutDataItem',
	mappedContent: 'mappedContent',
} as const;

export type ItemType = (typeof ITEM_TYPES)[keyof typeof ITEM_TYPES];
