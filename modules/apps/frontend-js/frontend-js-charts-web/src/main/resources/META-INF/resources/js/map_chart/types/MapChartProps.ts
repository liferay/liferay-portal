/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MapDatum} from './MapDatum';

export interface MapChartProps {
	className?: string;
	data: MapDatum[];
	fit?: 'data' | 'world';
	legend?: 'list' | 'none' | 'scale' | 'table';

	/**
	 * Draw the 1px border around each legend color swatch (list and table).
	 * Default `true`. Set `false` for borderless swatches.
	 */
	legendSwatchBorder?: boolean;

	/** Draw the divider lines under the `table` legend header and rows. Default `true`. */
	legendTableDividers?: boolean;

	scheme?: 'blue' | 'categorical';
	steps?: number;
	title: string;
	variant?: 'choropleth' | 'markers';
}
