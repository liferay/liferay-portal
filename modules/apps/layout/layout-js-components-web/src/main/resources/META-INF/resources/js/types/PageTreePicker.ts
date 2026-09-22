/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface PageTreePickerDataSource<T = unknown> {
	getChildren(
		parentItem: PageTreePickerItem<T> | null,
		page: number
	): Promise<PageTreePickerPage<T>>;

	getSubtreeCount(item: PageTreePickerItem<T>): Promise<number>;

	resolveItems(
		items: Array<PageTreePickerItem<T>>
	): Promise<Array<PageTreePickerItem<T>>>;

	search(query: string, page: number): Promise<PageTreePickerPage<T>>;
}

export interface PageTreePickerItem<T = unknown> {
	alwaysIncludeDescendants?: boolean;
	badge?: PageTreePickerItemBadge;
	disabled?: boolean;
	hasChildren: boolean;
	icon?: string;
	id: string;
	label: string;
	page: T;
	parentId?: string | null;
	path?: string[];
	title?: string;
}

export interface PageTreePickerItemBadge {
	label: string;
	symbol: string;
}

export interface PageTreePickerPage<T = unknown> {
	ancestors?: Array<PageTreePickerItem<T>>;
	items: Array<PageTreePickerItem<T>>;
	totalCount: number;
}

export interface PageTreePickerSelectionEntry<T = unknown> {
	excluded?: boolean;
	includeDescendants: boolean;
	item: PageTreePickerItem<T>;
}

export type PageTreePickerSelectionMode = 'multiple' | 'single';
