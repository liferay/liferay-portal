/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {forwardRef} from 'react';

import {MapDatum} from '../types/MapDatum';
import {getMapChartFocusableProps} from '../utils/getMapChartFocusableProps';

interface MapChartCountryFillProps {
	color: string;
	countryCode: string;
	countryPath: string;
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
}

const MapChartCountryFill = forwardRef<
	SVGPathElement,
	MapChartCountryFillProps
>(function MapChartCountryFill(
	{
		color,
		countryCode,
		countryPath,
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
	},
	ref
) {
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
		<path
			{...focusableProps}
			className={classNames('chart-map-land', 'is-data', {
				'is-active': isActive,
			})}
			d={countryPath}
			data-country={countryCode}
			ref={ref}
			style={
				{
					'--country-delay': `${delayMs}ms`,
					'--country-fill': color,
				} as React.CSSProperties
			}
		/>
	);
});

export default MapChartCountryFill;
