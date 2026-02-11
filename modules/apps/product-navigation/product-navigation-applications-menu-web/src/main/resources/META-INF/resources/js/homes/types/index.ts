/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type CategoryItem = {
	href: string;
	id: string;
	label: string;
	leadingIcon: string;
};

export type CategoryItemGrouped = {
	id: string;
	items: Array<CategoryItem>;
	label: string;
};

export type HomeProps = {
	componentId?: string | null;
	items: Array<CategoryItemGrouped>;
	label: string;
	locale: string;
	logo: string;
	portletId: string;
	portletNamespace: string;
};
