/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {forwardRef} from 'react';

import {WORLD_MAP_DATA} from '../geography/mapChartData';
import {MapDatum} from '../types/MapDatum';
import {getMapChartFocusableProps} from '../utils/getMapChartFocusableProps';

interface MapChartMarkerProps {
	color: string;
	datum: MapDatum;
	delayMs: number;
	index: number;
	isActive: boolean;
	isFocusable: boolean;
	onBlur: () => void;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	onKeyDown: (event: React.KeyboardEvent, index: number) => void;
	radius: number;
}

const MapChartMarker = forwardRef<SVGCircleElement, MapChartMarkerProps>(
	function MapChartMarker(
		{
			color,
			datum,
			delayMs,
			index,
			isActive,
			isFocusable,
			onBlur,
			onFocus,
			onHover,
			onHoverEnd,
			onKeyDown,
			radius,
		},
		ref
	) {
		const {centroid} = WORLD_MAP_DATA[datum.country];

		const focusableProps = getMapChartFocusableProps(datum, {
			index,
			isFocusable,
			onBlur,
			onFocus,
			onHover,
			onHoverEnd,
			onKeyDown,
		});

		return (
			<circle
				{...focusableProps}
				className={classNames('chart-map-marker', {
					'is-active': isActive,
				})}
				cx={centroid[0]}
				cy={centroid[1]}
				r={radius}
				ref={ref}
				style={
					{
						'--marker-delay': `${delayMs}ms`,
						'--marker-fill': color,
					} as React.CSSProperties
				}
			/>
		);
	}
);

export default MapChartMarker;
