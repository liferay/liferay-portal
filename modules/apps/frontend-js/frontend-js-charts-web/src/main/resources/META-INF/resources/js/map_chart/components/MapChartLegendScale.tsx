/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {getBlueSchemeColor} from '../utils/blueSchemeColors';
import {getCategoricalSchemeColor} from '../utils/categoricalSchemeColors';

interface MapChartLegendScaleProps {
	bucketCount: number;
	scheme: 'blue' | 'categorical';
}

export default function MapChartLegendScale({
	bucketCount,
	scheme,
}: MapChartLegendScaleProps) {
	const getSwatchColor =
		scheme === 'categorical'
			? getCategoricalSchemeColor
			: getBlueSchemeColor;

	const swatchColors = Array.from(
		{length: bucketCount},
		(_datum, bucketIndex) => getSwatchColor(bucketCount, bucketIndex)
	);

	return (
		<div className="chart-map-legend-scale">
			<span className="chart-map-legend-scale-label">
				{Liferay.Language.get('less')}
			</span>

			<ul className="chart-map-legend-scale-swatches">
				{swatchColors.map((color, index) => (
					<li
						className="chart-map-legend-scale-swatch"
						key={index}
						style={{background: color}}
					/>
				))}
			</ul>

			<span className="chart-map-legend-scale-label">
				{Liferay.Language.get('more')}
			</span>
		</div>
	);
}
