/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useCallback, useId, useMemo, useRef, useState} from 'react';

import ChartSummary from '../chart_summary/ChartSummary';
import PieChartLegend from './components/PieChartLegend';
import PieChartPlot from './components/PieChartPlot';
import {SIZE_PRESETS, STROKE_INSET, THICKNESS_RATIOS} from './constants';

import '../../css/PieChart.scss';
import {useChartKeyboardNav} from '../hooks/useChartKeyboardNav';
import {toPercent} from '../percent';
import {PieDatum} from './types/PieDatum';
import {getPieChartSlicePathFactory} from './utils/getPieChartSlicePathFactory';
import {getPieSliceColors} from './utils/pieColors';

/**
 * What the `legend="list"` rows show next to each label.
 *
 * - `percent` (default): the slice's share of the total, e.g. `42.3%`.
 * - `value`: the raw value, e.g. `68`.
 * - `name`: nothing extra — just the swatch and label.
 *
 * No effect on `legend="table"`, which always breaks value and share into their
 * own columns.
 */
export type PieChartLegendValue = 'name' | 'percent' | 'value';

export interface PieChartProps {
	animated?: boolean;
	className?: string;
	data: PieDatum[];
	description?: string;
	innerRadius?: number;
	legend?: 'list' | 'none' | 'table';

	/**
	 * Draw the 1px border around each legend color swatch (list and table).
	 * Default `true`. Set `false` for borderless swatches.
	 */
	legendSwatchBorder?: boolean;

	/** Draw the divider lines under the `table` legend header and rows. Default `true`. */
	legendTableDividers?: boolean;

	/** What the `legend="list"` rows show next to each label. Default `percent`. */
	legendValue?: PieChartLegendValue;
	size?: 'lg' | 'md' | 'sm' | 'xs' | number;
	thickness?: 'lg' | 'md';
	title: string;
}

function getInnerRatio(
	innerRadiusRatio: number | undefined,
	thickness: 'lg' | 'md'
): number {
	if (innerRadiusRatio !== undefined) {
		return Math.min(0.95, Math.max(0, innerRadiusRatio));
	}

	return THICKNESS_RATIOS[thickness];
}

function getPixelSize(size: PieChartProps['size']): number {
	if (typeof size === 'number') {
		return size;
	}

	return SIZE_PRESETS[size ?? 'md'];
}

export default function PieChart({
	animated = true,
	className,
	data,
	description,
	innerRadius: innerRadiusRatio,
	legend = 'list',
	legendSwatchBorder = true,
	legendTableDividers = true,
	legendValue = 'percent',
	size = 'md',
	thickness = 'md',
	title,
}: PieChartProps) {
	const [hoverIndex, setHoverIndex] = useState<number | null>(null);
	const [focusIndex, setFocusIndex] = useState<number | null>(null);

	const sliceRefs = useRef<(SVGPathElement | null)[]>([]);

	const activeIndex = focusIndex ?? hoverIndex;

	const baseId = useId();
	const titleId = `${baseId}-title`;
	const summaryId = `${baseId}-summary`;

	const pixelSize = getPixelSize(size);
	const innerRatio = getInnerRatio(innerRadiusRatio, thickness);

	const outerRadius = pixelSize / 2 - STROKE_INSET;
	const innerRadius = innerRatio * outerRadius;
	const center = pixelSize / 2;

	const total = data.reduce(
		(sum, datum) => sum + Math.max(0, datum.value),
		0
	);

	const colors = useMemo(() => getPieSliceColors(data), [data]);

	const pathFactory = useMemo(
		() =>
			getPieChartSlicePathFactory({
				centerX: center,
				centerY: center,
				innerRadius,
				outerRadius,
			}),
		[center, innerRadius, outerRadius]
	);

	const focusSlice = useCallback((index: number) => {
		setFocusIndex(index);

		sliceRefs.current[index]?.focus();
	}, []);

	const focusableIndexes = useMemo(
		() => Array.from({length: data.length}, (_, index) => index),
		[data.length]
	);

	const onKeyDown = useChartKeyboardNav(focusableIndexes, focusSlice);

	const sliceRefFactory = useCallback(
		(index: number) => (element: SVGPathElement | null) => {
			sliceRefs.current[index] = element;
		},
		[]
	);

	const activeDatum = activeIndex === null ? undefined : data[activeIndex];
	const activePercent = activeDatum
		? toPercent(activeDatum.value, total)
		: undefined;

	const summaryDescribedBy = legend === 'table' ? undefined : summaryId;

	return (
		<figure
			aria-describedby={summaryDescribedBy}
			aria-labelledby={titleId}
			className={classNames(
				'chart-pie',
				{
					'chart-pie-no-swatch-border': !legendSwatchBorder,
					'chart-pie-revealed': animated,
				},
				className
			)}
		>
			<figcaption className="chart-pie-caption" id={titleId}>
				{title}
			</figcaption>

			{legend === 'table' ? null : (
				<ChartSummary
					description={description}
					id={summaryId}
					items={data}
					showPosition
					total={total}
				/>
			)}

			<div className="chart-pie-row">
				<div
					className="chart-pie-body mx-auto"
					style={{maxWidth: pixelSize}}
				>
					<PieChartPlot
						activeDatum={activeDatum}
						activeIndex={activeIndex}
						activePercent={activePercent}
						baseId={baseId}
						colors={colors}
						data={data}
						focusIndex={focusIndex}
						innerRadius={innerRadius}
						onFocus={focusSlice}
						onHover={setHoverIndex}
						onHoverEnd={() => setHoverIndex(null)}
						onKeyDown={onKeyDown}
						onSliceBlur={() => setFocusIndex(null)}
						pathFactory={pathFactory}
						pixelSize={pixelSize}
						sliceRefFactory={sliceRefFactory}
						total={total}
					/>
				</div>

				<PieChartLegend
					activeIndex={activeIndex}
					colors={colors}
					data={data}
					legend={legend}
					legendTableDividers={legendTableDividers}
					legendValue={legendValue}
					onFocus={focusSlice}
					onHover={setHoverIndex}
					onHoverEnd={() => setHoverIndex(null)}
					titleId={titleId}
					total={total}
				/>
			</div>
		</figure>
	);
}
