/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {CIRCLE_START_ANGLE, FULL_CIRCLE_RADIANS} from '../constants';
import {PieDatum} from '../types/PieDatum';
import {SliceAngles} from '../types/SliceAngles';

interface PieChartSliceProps {
	color: string;
	datum: PieDatum;
	index: number;
	isActive: boolean;
	onBlur: () => void;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	onKeyDown: (event: React.KeyboardEvent, index: number) => void;
	pathFactory: (angles: SliceAngles) => string;
	percent: number;
	precedingTotal: number;
	sliceRef: (element: SVGPathElement | null) => void;
	total: number;
}

export default function PieChartSlice({
	color,
	datum,
	index,
	isActive,
	onBlur,
	onFocus,
	onHover,
	onHoverEnd,
	onKeyDown,
	pathFactory,
	percent,
	precedingTotal,
	sliceRef,
	total,
}: PieChartSliceProps) {
	if (total <= 0) {
		return null;
	}

	const startAngle =
		CIRCLE_START_ANGLE + (precedingTotal / total) * FULL_CIRCLE_RADIANS;
	const sweepAngle = (Math.max(0, datum.value) / total) * FULL_CIRCLE_RADIANS;

	return (
		<path
			aria-label={
				datum.description ??
				`${datum.label}: ${datum.value} (${percent}%)`
			}
			className={classNames('chart-pie-slice', {'is-hover': isActive})}
			d={pathFactory({
				endAngle: startAngle + sweepAngle,
				startAngle,
				sweepAngle,
			})}
			fill={color}
			onBlur={onBlur}
			onFocus={() => onFocus(index)}
			onKeyDown={(event) => onKeyDown(event, index)}
			onMouseEnter={() => onHover(index)}
			onMouseLeave={onHoverEnd}
			ref={sliceRef}
			role="img"
			stroke="var(--white)"
			strokeWidth={2}
			tabIndex={0}
		/>
	);
}
