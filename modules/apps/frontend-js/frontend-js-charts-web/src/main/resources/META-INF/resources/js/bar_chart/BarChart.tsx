/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useCallback, useId, useMemo, useRef, useState} from 'react';

import {getCategoricalColors} from '../palette';
import {CHART_FAMILY_CLAY_PALETTE} from '../tokens';
import BarChartLegend from './legend/BarChartLegend';
import BarChartPlot from './plot/BarChartPlot';
import {getBarChartGeometry} from './plot/geometry';

import '../../css/BarChart.scss';

import type {BarChartProps} from './types';

export default function BarChart({
	alignment = 'start',
	animated = true,
	className,
	data,
	description,
	height = 280,
	legend = 'none',
	legendSwatchBorder = true,
	legendValue = 'percent',
	orientation = 'vertical',
	rounded = false,
	scheme = 'blue',
	size = 'default',
	title,
	track = false,
	width = 480,
}: BarChartProps) {
	const reactId = useId();
	const titleId = `${reactId}-title`;
	const descId = `${reactId}-desc`;

	const [focusIndex, setFocusIndex] = useState<number | null>(null);
	const [hoverIndex, setHoverIndex] = useState<number | null>(null);
	const activeIndex = focusIndex ?? hoverIndex;
	const barRefs = useRef<Array<SVGRectElement | null>>([]);

	const total = useMemo(
		() => data.reduce((acc, d) => acc + Math.max(0, d.value), 0),
		[data]
	);

	const palette = useMemo(
		() =>
			scheme === 'categorical' ? getCategoricalColors(data.length) : null,
		[scheme, data.length]
	);

	const colorFor = useCallback(
		(index: number): string =>
			palette
				? palette[index] ?? CHART_FAMILY_CLAY_PALETTE.blue
				: CHART_FAMILY_CLAY_PALETTE.blue,
		[palette]
	);

	const geometry = useMemo(
		() =>
			getBarChartGeometry({
				data,
				height,
				orientation,
				rounded,
				size,
				width,
			}),
		[data, height, orientation, rounded, size, width]
	);

	const summaryText = useMemo(() => {
		if (legend === 'table') {
			return description ?? '';
		}

		return (
			description ??
			data
				.map((d) => d.description ?? `${d.label}: ${d.value}`)
				.join('. ')
		);
	}, [data, description, legend]);

	const focusBar = useCallback((index: number) => {
		barRefs.current[index]?.focus();
	}, []);

	const deactivate = useCallback(
		(index: number) =>
			setHoverIndex((current) => (current === index ? null : current)),
		[]
	);

	const setBarRef = useCallback(
		(index: number, element: SVGRectElement | null) => {
			barRefs.current[index] = element;
		},
		[]
	);

	return (
		<figure
			aria-describedby={descId}
			aria-labelledby={titleId}
			className={classNames(
				'charts-bar-chart',
				`charts-bar-chart--${orientation}`,
				`charts-bar-chart--${scheme}`,
				`charts-bar-chart--legend-${legend}`,
				`charts-bar-chart--size-${size}`,
				`charts-bar-chart--align-${alignment}`,
				{
					'charts-bar-chart--motion': animated,
					'charts-bar-chart--no-swatch-border': !legendSwatchBorder,
					'charts-bar-chart--rounded': rounded,
					'charts-bar-chart--track': track,
				},
				className
			)}
			style={{maxWidth: width}}
		>
			<figcaption className="charts-bar-chart__title" id={titleId}>
				{title}
			</figcaption>

			<p className="sr-only" id={descId}>
				{summaryText}
			</p>

			<BarChartPlot
				data={data}
				focusIndex={focusIndex}
				geometry={geometry}
				height={height}
				hoverIndex={hoverIndex}
				onFocus={setFocusIndex}
				onHover={setHoverIndex}
				onLeave={deactivate}
				palette={palette}
				setBarRef={setBarRef}
				showAxis={size !== 'inline'}
				track={track}
				width={width}
			/>

			<BarChartLegend
				activeIndex={activeIndex}
				colorFor={colorFor}
				data={data}
				layout={legend}
				legendValue={legendValue}
				onActivate={setHoverIndex}
				onDeactivate={deactivate}
				onSelect={focusBar}
				titleId={titleId}
				total={total}
			/>
		</figure>
	);
}
