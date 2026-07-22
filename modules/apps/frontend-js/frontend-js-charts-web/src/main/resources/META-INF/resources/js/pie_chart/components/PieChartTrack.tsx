/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {STROKE_INSET} from '../constants';

interface PieChartTrackProps {
	innerRadius: number;
	pixelSize: number;
}

/**
 * Neutral ring shown in place of the slices when the total is 0, so the
 * chart keeps its shape instead of disappearing.
 */
export default function PieChartTrack({
	innerRadius,
	pixelSize,
}: PieChartTrackProps) {
	const outerRadius = pixelSize / 2 - STROKE_INSET;

	return (
		<circle
			className="chart-pie-track"
			cx={pixelSize / 2}
			cy={pixelSize / 2}
			r={(outerRadius + innerRadius) / 2}
			strokeWidth={outerRadius - innerRadius}
		/>
	);
}
