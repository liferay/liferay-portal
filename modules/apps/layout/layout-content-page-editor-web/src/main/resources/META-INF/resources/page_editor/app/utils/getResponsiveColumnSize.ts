/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ColumnLayoutDataItem} from '../../types/layout_data/ColumnLayoutDataItem';
import {VIEWPORT_SIZES, ViewportSize} from '../config/constants/viewportSizes';

type ColumnConfig = ColumnLayoutDataItem['config'] &
	Partial<Record<ViewportSize, {size?: number}>>;

const ORDERED_VIEWPORT_SIZES: ViewportSize[] = [
	VIEWPORT_SIZES.desktop,
	VIEWPORT_SIZES.tablet,
	VIEWPORT_SIZES.landscapeMobile,
	VIEWPORT_SIZES.portraitMobile,
];

export function getResponsiveColumnSize(
	config: ColumnConfig,
	viewportSize: ViewportSize
) {
	const getViewportSize = (
		config: ColumnConfig,
		viewportSize: ViewportSize
	): ViewportSize => {
		const viewportSizePosition =
			ORDERED_VIEWPORT_SIZES.indexOf(viewportSize);

		if (
			viewportSize === VIEWPORT_SIZES.desktop ||
			viewportSizePosition === -1
		) {
			return VIEWPORT_SIZES.desktop;
		}

		return config[viewportSize] && config[viewportSize].size
			? viewportSize
			: getViewportSize(
					config,
					ORDERED_VIEWPORT_SIZES[viewportSizePosition - 1]
				);
	};

	const newViewportSize = getViewportSize(config, viewportSize);

	const responsiveConfig = config[newViewportSize];

	return responsiveConfig ? responsiveConfig.size : config.size;
}
