/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {
	MARKER_FOCUS_RING_INNER_RADIUS,
	MARKER_FOCUS_RING_OUTER_RADIUS,
	MARKER_OVERLAY_RADIUS,
} from '../constants';

interface MapChartMarkerOverlayProps {
	centroid: [number, number];
	color: string;
	focused: boolean;
	markerScale: number;
}

export default function MapChartMarkerOverlay({
	centroid,
	color,
	focused,
	markerScale,
}: MapChartMarkerOverlayProps) {
	return (
		<g aria-hidden="true" pointerEvents="none">
			{focused ? (
				<>
					<circle
						className="chart-map-marker-focus-ring-outer"
						cx={centroid[0]}
						cy={centroid[1]}
						r={MARKER_FOCUS_RING_OUTER_RADIUS * markerScale}
					/>

					<circle
						className="chart-map-marker-focus-ring-inner"
						cx={centroid[0]}
						cy={centroid[1]}
						r={MARKER_FOCUS_RING_INNER_RADIUS * markerScale}
					/>
				</>
			) : null}

			<circle
				className={classNames(
					'chart-map-marker',
					'chart-map-marker-overlay',
					{
						'is-focused': focused,
					}
				)}
				cx={centroid[0]}
				cy={centroid[1]}
				r={MARKER_OVERLAY_RADIUS * markerScale}
				style={
					{
						'--marker-fill': color,
					} as React.CSSProperties
				}
			/>
		</g>
	);
}
