/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

const PAGE_TYPE_ICONS: Record<string, string> = {
	'Document': 'document',
	'Web Content': 'web-content',
};

const DEFAULT_PAGE_TYPE_ICON = 'page';

function pageTypeIcon(type?: string): string {
	if (type && PAGE_TYPE_ICONS[type]) {
		return PAGE_TYPE_ICONS[type];
	}

	return DEFAULT_PAGE_TYPE_ICON;
}

export default function TitleCellRenderer({
	itemData,
	value,
}: {
	itemData: {
		r_seoStudioPageToSEOStudioScanInsights_seoStudioPage?: {
			type?: string;
		};
	};
	value: string;
}) {
	const type =
		itemData.r_seoStudioPageToSEOStudioScanInsights_seoStudioPage?.type;

	return (
		<span className="seo-studio-affected-pages-title">
			<ClayIcon className="mr-2" symbol={pageTypeIcon(type)} />

			{value}
		</span>
	);
}
