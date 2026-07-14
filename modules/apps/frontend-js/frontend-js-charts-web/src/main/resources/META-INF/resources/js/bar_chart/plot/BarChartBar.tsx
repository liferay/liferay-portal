/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {LABEL_LINE_HEIGHT} from './geometry';

import type {BarDatum} from '../types';
import type {BarLayout} from './geometry';

interface Props {
	active: boolean;
	datum: BarDatum;
	fill: string | null;
	index: number;
	layout: BarLayout;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onKeyDown: (event: React.KeyboardEvent, index: number) => void;
	onLeave: (index: number) => void;
	setBarRef: (index: number, element: SVGGraphicsElement | null) => void;
	track: boolean;
}

export default function BarChartBar({
	active,
	datum,
	fill,
	index,
	layout,
	onFocus,
	onHover,
	onKeyDown,
	onLeave,
	setBarRef,
	track,
}: Props) {
	return (
		<g
			className={classNames('charts-bar-chart__bar-group', {
				'is-active': active,
			})}
		>
			{track && (
				<rect
					className="charts-bar-chart__bar-track"
					height={layout.trackHeight}
					rx={layout.barRx}
					width={layout.trackWidth}
					x={layout.trackX}
					y={layout.trackY}
				/>
			)}

			<rect
				aria-label={
					datum.description ?? `${datum.label}: ${datum.value}`
				}
				className="charts-bar-chart__bar"
				height={layout.height}
				onBlur={() => onLeave(index)}
				onFocus={() => onFocus(index)}
				onKeyDown={(event) => onKeyDown(event, index)}
				onMouseEnter={() => onHover(index)}
				onMouseLeave={() => onLeave(index)}
				ref={(element) => setBarRef(index, element)}
				role="img"
				rx={layout.barRx}
				style={
					{
						'--charts-bar-delay': `${index * 60}ms`,
						...(fill ? {'--charts-bar-fill': fill} : null),
					} as React.CSSProperties
				}
				tabIndex={0}
				width={layout.width}
				x={layout.x}
				y={layout.y}
			/>

			<text
				aria-hidden="true"
				className="charts-bar-chart__label"
				textAnchor={layout.labelAnchor}
				x={layout.labelX}
				y={layout.labelY}
			>
				<title>{datum.label}</title>

				{layout.labelLines.map((line, lineIndex) => (
					<tspan
						dy={lineIndex === 0 ? 0 : LABEL_LINE_HEIGHT}
						key={`${line}-${lineIndex}`}
						x={layout.labelX}
					>
						{line}
					</tspan>
				))}
			</text>

			<g
				className={classNames('charts-bar-chart__value-group', {
					'is-active': active,
				})}
				transform={`translate(${layout.valueX} ${layout.valueY})`}
			>
				{active && (
					<rect
						className="charts-bar-chart__value-bg"
						height={layout.valueHeight}
						rx={4}
						width={layout.valueWidth}
						x={-layout.valueWidth / 2}
						y={-layout.valueHeight / 2}
					/>
				)}

				<text
					className="charts-bar-chart__value"
					textAnchor="middle"
					x={0}
					y={4}
				>
					{datum.value}
				</text>
			</g>
		</g>
	);
}
