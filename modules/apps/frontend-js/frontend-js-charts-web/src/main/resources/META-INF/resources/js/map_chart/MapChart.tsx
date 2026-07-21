/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useCallback, useId, useMemo, useRef, useState} from 'react';

import ChartSummary from '../chart_summary/ChartSummary';
import ChartTooltip from '../chart_tooltip/ChartTooltip';
import {useChartKeyboardNav} from '../hooks/useChartKeyboardNav';
import MapChartLegend from './components/MapChartLegend';
import MapChartPlot from './components/MapChartPlot';
import {MapChartProps} from './types/MapChartProps';
import {getBlueSchemeColor} from './utils/blueSchemeColors';
import {getCategoricalSchemeColor} from './utils/categoricalSchemeColors';
import {
	computeQuantileBuckets,
	getEffectiveBucketCount,
} from './utils/computeQuantileBuckets';
import {getClampedSteps} from './utils/getClampedSteps';
import {getCountryLabel} from './utils/getCountryLabel';
import {getMatchedDataIndexes} from './utils/getMatchedDataIndexes';

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
	className,
	data,
	fit = 'world',
	legend = 'none',
	legendSwatchBorder = true,
	scheme = 'blue',
	steps = 5,
	title,
	variant = 'markers',
}: MapChartProps) {
	const baseId = useId();
	const titleId = `${baseId}-title`;
	const summaryId = `${baseId}-summary`;

	const [hoverIndex, setHoverIndex] = useState<number | null>(null);
	const [focusIndex, setFocusIndex] = useState<number | null>(null);

	const activeIndex = focusIndex ?? hoverIndex;

	const itemRefs = useRef<(SVGGraphicsElement | null)[]>([]);
	const itemRefCallbacks = useRef<
		((element: SVGGraphicsElement | null) => void)[]
	>([]);

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

	const validIndexes = useMemo(() => getMatchedDataIndexes(data), [data]);

	const focusableIndex =
		focusIndex !== null && validIndexes.includes(focusIndex)
			? focusIndex
			: validIndexes[0] ?? null;

	const focusItem = useCallback((index: number) => {
		setFocusIndex(index);

		itemRefs.current[index]?.focus();
	}, []);

	const onKeyDown = useChartKeyboardNav(validIndexes, focusItem);

	const summaryItems = useMemo(
		() =>
			data.map((datum) => ({
				label: getCountryLabel(datum),
				value: datum.value,
			})),
		[data]
	);

	const itemRefFactory = useCallback((index: number) => {
		if (!itemRefCallbacks.current[index]) {
			itemRefCallbacks.current[index] = (element) => {
				itemRefs.current[index] = element;
			};
		}

		return itemRefCallbacks.current[index];
	}, []);

	const clearFocus = useCallback(() => setFocusIndex(null), []);
	const clearHover = useCallback(() => setHoverIndex(null), []);

	const legendElement = (
		<MapChartLegend
			activeIndex={activeIndex}
			bucketCount={bucketCount}
			colors={colors}
			data={data}
			legend={legend}
			onFocus={focusItem}
			onHover={setHoverIndex}
			onHoverEnd={clearHover}
			scheme={scheme}
			titleId={titleId}
			total={total}
		/>
	);

	return (
		<figure
			aria-describedby={summaryId}
			aria-labelledby={titleId}
			className={classNames(
				'chart-map',
				{
					'chart-map-no-swatch-border': !legendSwatchBorder,
				},
				className
			)}
		>
			<figcaption className="chart-map-caption" id={titleId}>
				{title}
			</figcaption>

			<ChartSummary id={summaryId} items={summaryItems} total={total} />

			<div className="chart-map-body">
				<MapChartPlot
					activeIndex={activeIndex}
					baseId={baseId}
					colors={colors}
					data={data}
					fit={fit}
					focusIndex={focusIndex}
					focusableIndex={focusableIndex}
					itemRefFactory={itemRefFactory}
					onBlur={clearFocus}
					onFocus={focusItem}
					onHover={setHoverIndex}
					onHoverEnd={clearHover}
					onKeyDown={onKeyDown}
					titleId={titleId}
					validIndexes={validIndexes}
					variant={variant}
				/>

				{activeIndex !== null && data[activeIndex] ? (
					<ChartTooltip
						label={getCountryLabel(data[activeIndex])}
						value={data[activeIndex].value}
					/>
				) : null}

				{legend === 'list' && legendElement}
			</div>

			{legend !== 'list' && legendElement}
		</figure>
	);
}
