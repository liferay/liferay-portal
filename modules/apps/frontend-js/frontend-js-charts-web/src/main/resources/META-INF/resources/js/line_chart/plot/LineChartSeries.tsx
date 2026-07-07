/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {renderMarker} from './markers';

import type {LineSeriesLayout} from './geometry';
import type {LineMarkerShape} from './markers';

const MARKER_SIZE = 4;
const MARKER_SIZE_ACTIVE = 6;
const HALO_INNER_SIZE = 8;
const HALO_OUTER_SIZE = 10;
const POINT_HIT_RADIUS = 10;

interface Props {
	active: boolean;
	activeCategoryIndex: number | null;
	categories: string[];
	color: string;
	dasharray: string;
	focusedCategoryIndex: number | null;
	format: (value: number) => string;
	layout: LineSeriesLayout;
	marker: LineMarkerShape;
	onBlurPoint: (seriesIndex: number, categoryIndex: number) => void;
	onFocusPoint: (seriesIndex: number, categoryIndex: number) => void;
	onHoverPoint: (seriesIndex: number, categoryIndex: number) => void;
	onKeyDownPoint: (
		seriesIndex: number,
		categoryIndex: number,
		event: React.KeyboardEvent
	) => void;
	onLeavePoint: (seriesIndex: number, categoryIndex: number) => void;
	seriesIndex: number;
	seriesLabel: string;
	setPointRef: (
		seriesIndex: number,
		categoryIndex: number,
		element: SVGCircleElement | null
	) => void;
	tabbable: {categoryIndex: number; seriesIndex: number} | null;
}

export default function LineChartSeries({
	active,
	activeCategoryIndex,
	categories,
	color,
	dasharray,
	focusedCategoryIndex,
	format,
	layout,
	marker,
	onBlurPoint,
	onFocusPoint,
	onHoverPoint,
	onKeyDownPoint,
	onLeavePoint,
	seriesIndex,
	seriesLabel,
	setPointRef,
	tabbable,
}: Props) {
	return (
		<g
			className={classNames('charts-line-chart__series', {
				'is-active': active,
			})}
			style={{'--charts-line-color': color} as React.CSSProperties}
		>
			{layout.paths.map((path, index) => (
				<path
					className="charts-line-chart__line"
					d={path}
					key={index}
					style={{strokeDasharray: dasharray}}
				/>
			))}

			{layout.points.map((point) => {
				if (!point) {
					return null;
				}

				const isActive = activeCategoryIndex === point.categoryIndex;

				const isFocused = focusedCategoryIndex === point.categoryIndex;

				const isTabbable =
					tabbable?.seriesIndex === seriesIndex &&
					tabbable?.categoryIndex === point.categoryIndex;

				return (
					<g
						className={classNames(
							'charts-line-chart__point-group',
							{
								'is-active': isActive,
							}
						)}
						key={point.categoryIndex}
					>
						{isFocused && (
							<g
								aria-hidden="true"
								className="charts-line-chart__halo"
								transform={`translate(${point.x} ${point.y})`}
							>
								<g className="charts-line-chart__halo-outer">
									{renderMarker(marker, HALO_OUTER_SIZE)}
								</g>

								<g className="charts-line-chart__halo-inner">
									{renderMarker(marker, HALO_INNER_SIZE)}
								</g>
							</g>
						)}

						<g
							aria-hidden="true"
							className="charts-line-chart__marker"
							transform={`translate(${point.x} ${point.y})`}
						>
							<g
								className="charts-line-chart__marker-inner"
								style={
									{
										'--charts-marker-delay': `${
											point.categoryIndex * 40
										}ms`,
									} as React.CSSProperties
								}
							>
								{renderMarker(
									marker,
									isActive ? MARKER_SIZE_ACTIVE : MARKER_SIZE
								)}
							</g>
						</g>

						<circle
							aria-label={`${seriesLabel}, ${
								categories[point.categoryIndex]
							}: ${format(point.value)}`}
							className="charts-line-chart__point"
							cx={point.x}
							cy={point.y}
							onBlur={() =>
								onBlurPoint(seriesIndex, point.categoryIndex)
							}
							onFocus={() =>
								onFocusPoint(seriesIndex, point.categoryIndex)
							}
							onKeyDown={(event) =>
								onKeyDownPoint(
									seriesIndex,
									point.categoryIndex,
									event
								)
							}
							onMouseEnter={() =>
								onHoverPoint(seriesIndex, point.categoryIndex)
							}
							onMouseLeave={() =>
								onLeavePoint(seriesIndex, point.categoryIndex)
							}
							r={POINT_HIT_RADIUS}
							ref={(element) =>
								setPointRef(
									seriesIndex,
									point.categoryIndex,
									element
								)
							}
							role="img"
							tabIndex={isTabbable ? 0 : -1}
						/>
					</g>
				);
			})}
		</g>
	);
}
