/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {MapDatum} from '../types/MapDatum';
import {getCountryLabel} from './getCountryLabel';

interface MapChartFocusableItemHandlers {
	index: number;
	isFocusable: boolean;
	onBlur: () => void;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	onKeyDown: (event: React.KeyboardEvent, index: number) => void;
}

interface MapChartFocusableProps {
	'aria-label': string;
	'onBlur': () => void;
	'onFocus': () => void;
	'onKeyDown': (event: React.KeyboardEvent) => void;
	'onPointerEnter': () => void;
	'onPointerLeave': () => void;
	'role': 'img';
	'tabIndex': number;
}

export function getMapChartFocusableProps(
	datum: MapDatum,
	{
		index,
		isFocusable,
		onBlur,
		onFocus,
		onHover,
		onHoverEnd,
		onKeyDown,
	}: MapChartFocusableItemHandlers
): MapChartFocusableProps {
	return {
		'aria-label': `${getCountryLabel(datum)}: ${datum.value}`,
		onBlur,
		'onFocus': () => onFocus(index),
		'onKeyDown': (event) => onKeyDown(event, index),
		'onPointerEnter': () => onHover(index),
		'onPointerLeave': onHoverEnd,
		'role': 'img',
		'tabIndex': isFocusable ? 0 : -1,
	};
}
