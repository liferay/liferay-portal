/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useCallback, useId, useMemo, useRef, useState} from 'react';

import PieChartGraphic from './components/PieChartGraphic';
import PieChartLegend from './components/PieChartLegend';
import PieChartSummary from './components/PieChartSummary';
import {SIZE_PRESETS, STROKE_INSET, THICKNESS_RATIOS} from './constants';

import '../../css/PieChart.scss';
import {usePieKeyboardNav} from './hooks/usePieKeyboardNav';
import {PieDatum} from './types/PieDatum';
import {getPieChartSlicePathFactory} from './utils/getPieChartSlicePathFactory';
import {toPercent} from './utils/percent';
import {getPieSliceColors} from './utils/pieColors';

export interface PieChartProps {
	animationDisabled?: boolean;
	className?: string;
	data: PieDatum[];
	description?: string;
	innerRadius?: number;
	legend?: 'list' | 'none' | 'table';
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
	animationDisabled = false,
	className,
	data,
	description,
	innerRadius: innerRadiusRatio,
	legend = 'list',
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

	const onKeyDown = usePieKeyboardNav(data.length, focusSlice);

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

	return (
		<figure
			aria-describedby={summaryId}
			aria-labelledby={titleId}
			className={classNames(
				'chart-pie',
				{'chart-pie-revealed': !animationDisabled},
				className
			)}
			style={{maxWidth: pixelSize}}
		>
			<figcaption className="chart-pie-caption" id={titleId}>
				{title}
			</figcaption>

			{legend === 'table' ? null : (
				<PieChartSummary
					data={data}
					description={description}
					id={summaryId}
					total={total}
				/>
			)}

			<div className="chart-pie-body">
				<PieChartGraphic
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
				onFocus={focusSlice}
				onHover={setHoverIndex}
				onHoverEnd={() => setHoverIndex(null)}
				titleId={titleId}
				total={total}
			/>
		</figure>
	);
}
