/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {stackedSegmentPath} from './geometry';

import type {BarDatum} from '../types';
import type {StackedSegmentLayout} from './geometry';

const TOOLTIP_CHAR_WIDTH = 7.5;
const TOOLTIP_HEIGHT = 28;
const TOOLTIP_PADDING_X = 12;

interface Props {
	active: boolean;
	datum: BarDatum;
	fill: string | null;
	index: number;
	layout: StackedSegmentLayout;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onLeave: (index: number) => void;
	setBarRef: (index: number, element: SVGGraphicsElement | null) => void;

	/** Coordinate-space width used to keep the tooltip box inside the viewport. */
	width: number;
}

export default function BarChartStackedSegment({
	active,
	datum,
	fill,
	index,
	layout,
	onFocus,
	onHover,
	onLeave,
	setBarRef,
	width,
}: Props) {
	const label = datum.description ?? `${datum.label}: ${datum.value}`;

	const segmentWidth = Math.max(0, layout.width);

	const tooltipWidth =
		label.length * TOOLTIP_CHAR_WIDTH + TOOLTIP_PADDING_X * 2;
	const centerX = layout.x + segmentWidth / 2;

	// Keep the tooltip box inside the viewport but let its pointer track the
	// segment center.

	const tooltipX = Math.max(
		4,
		Math.min(centerX - tooltipWidth / 2, width - tooltipWidth - 4)
	);
	const tooltipY = layout.rowY - 10 - TOOLTIP_HEIGHT;
	const pointerX = Math.max(
		tooltipX + 10,
		Math.min(centerX, tooltipX + tooltipWidth - 10)
	);

	return (
		<g
			className={classNames('charts-bar-chart__bar-group', {
				'is-active': active,
			})}
		>
			<path
				aria-label={label}
				className="charts-bar-chart__bar"
				d={stackedSegmentPath(
					layout.x,
					layout.rowY,
					segmentWidth,
					layout.thickness,
					layout.rx,
					layout.roundLeft,
					layout.roundRight
				)}
				onBlur={() => onLeave(index)}
				onFocus={() => onFocus(index)}
				onMouseEnter={() => onHover(index)}
				onMouseLeave={() => onLeave(index)}
				ref={(element) => setBarRef(index, element)}
				role="img"
				style={
					{
						'--charts-bar-delay': `${index * 60}ms`,
						...(fill ? {'--charts-bar-fill': fill} : null),
					} as React.CSSProperties
				}
				tabIndex={0}
			/>

			{active && (
				<g className="charts-bar-chart__tip" pointerEvents="none">
					<rect
						className="charts-bar-chart__tip-bg"
						height={TOOLTIP_HEIGHT}
						rx={6}
						width={tooltipWidth}
						x={tooltipX}
						y={tooltipY}
					/>

					<path
						className="charts-bar-chart__tip-bg"
						d={`M ${pointerX - 6} ${tooltipY + TOOLTIP_HEIGHT} L ${pointerX + 6} ${tooltipY + TOOLTIP_HEIGHT} L ${pointerX} ${tooltipY + TOOLTIP_HEIGHT + 6} Z`}
					/>

					<text
						className="charts-bar-chart__tip-text"
						textAnchor="middle"
						x={tooltipX + tooltipWidth / 2}
						y={tooltipY + TOOLTIP_HEIGHT / 2 + 4}
					>
						{label}
					</text>
				</g>
			)}
		</g>
	);
}
