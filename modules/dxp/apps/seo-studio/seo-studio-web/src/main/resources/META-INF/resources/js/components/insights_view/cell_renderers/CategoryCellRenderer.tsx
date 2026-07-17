/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

import './CategoryCellRenderer.scss';

export default function CategoryCellRenderer({
	itemData,
}: {
	itemData: {category?: {name?: string}};
}) {
	const category = itemData?.category;

	if (!category?.name) {
		return null;
	}

	return (
		<ClayLabel
			className="seo-studio-category-label"
			displayType="info"
			withClose={false}
		>
			{category.name}
		</ClayLabel>
	);
}
