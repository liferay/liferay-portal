/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useId, useMemo} from 'react';

import MapChartPlot from './components/MapChartPlot';
import MapChartSummary from './components/MapChartSummary';
import {MapChartProps} from './types/MapChartProps';
import {getBlueSchemeColor} from './utils/blueSchemeColors';
import {getCategoricalSchemeColor} from './utils/categoricalSchemeColors';
import {
	computeQuantileBuckets,
	getEffectiveBucketCount,
} from './utils/computeQuantileBuckets';
import {getClampedSteps} from './utils/getClampedSteps';

import '../../css/MapChart.scss';

function getSchemeColor(
	scheme: MapChartProps['scheme'],
	bucketCount: number,
	bucketIndex: number
): string {
	return scheme === 'categorical'
		? getCategoricalSchemeColor(bucketCount, bucketIndex)
		: getBlueSchemeColor(bucketCount, bucketIndex);
}

export default function MapChart({
	data,
	scheme = 'blue',
	steps = 5,
	title,
	variant = 'markers',
}: MapChartProps) {
	const baseId = useId();
	const titleId = `${baseId}-title`;
	const summaryId = `${baseId}-summary`;

	const total = useMemo(
		() => data.reduce((sum, datum) => sum + Math.max(0, datum.value), 0),
		[data]
	);

	const clampedSteps = getClampedSteps(steps);

	const bucketCount = useMemo(
		() => getEffectiveBucketCount(data, clampedSteps),
		[data, clampedSteps]
	);

	const buckets = useMemo(
		() => computeQuantileBuckets(data, bucketCount),
		[data, bucketCount]
	);

	const colors = useMemo(
		() =>
			data.map((_datum, index) =>
				getSchemeColor(scheme, bucketCount, buckets[index])
			),
		[data, scheme, bucketCount, buckets]
	);

	return (
		<figure
			aria-describedby={summaryId}
			aria-labelledby={titleId}
			className="chart-map"
		>
			<figcaption className="chart-map-caption" id={titleId}>
				{title}
			</figcaption>

			<MapChartSummary data={data} id={summaryId} total={total} />

			<MapChartPlot
				colors={colors}
				data={data}
				titleId={titleId}
				variant={variant}
			/>
		</figure>
	);
}
