/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AssetsFDSPropsTransformer from './AssetsFDSPropsTransformer';

const ASSETS_SECTION_SELECTOR = '.cms-all-related-assets';

const TAB_PANEL_SELECTOR = '.tab-panel-item';

/**
 * Scopes the info panel to the tab panel shared by the Content Coverage Matrix
 * and the asset table, so opening it pushes both instead of only the table.
 * Falls back to the data set element when the tab panel is not found.
 */
export default function AllRelatedAssetsFDSPropsTransformer(
	props: Parameters<typeof AssetsFDSPropsTransformer>[0]
) {
	const tabPanelElement = document
		.querySelector(ASSETS_SECTION_SELECTOR)
		?.closest<HTMLElement>(TAB_PANEL_SELECTOR);

	return {
		...AssetsFDSPropsTransformer(props),
		...(tabPanelElement && {
			infoPanelContainerRef: {current: tabPanelElement},
		}),
	};
}
