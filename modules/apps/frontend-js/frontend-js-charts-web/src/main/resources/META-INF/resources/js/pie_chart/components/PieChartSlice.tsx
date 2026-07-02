/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {PieDatum} from '../types/PieDatum';

interface PieChartSliceProps {
	color: string;
	d: string;
	datum: PieDatum;
	index: number;
	isActive: boolean;
	onBlur: () => void;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	onKeyDown: (event: React.KeyboardEvent, index: number) => void;
	percent: number;
	sliceRef: (element: SVGPathElement | null) => void;
}

export default function PieChartSlice({
	color,
	d,
	datum,
	index,
	isActive,
	onBlur,
	onFocus,
	onHover,
	onHoverEnd,
	onKeyDown,
	percent,
	sliceRef,
}: PieChartSliceProps) {
	return (
		<path
			aria-label={
				datum.description ??
				`${datum.label}: ${datum.value} (${percent}%)`
			}
			className={classNames('chart-pie-slice', {'is-hover': isActive})}
			d={d}
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
