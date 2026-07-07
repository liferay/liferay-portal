/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

export type LineMarkerShape =
	| 'bar-h'
	| 'bar-v'
	| 'circle'
	| 'd-down'
	| 'd-up'
	| 'diamond'
	| 'square'
	| 'triangle'
	| 'triangle-down';

/**
 * Canonical marker cycle. Series `i` uses shape `i % 9` unless it overrides
 * `marker`. The order matches the reference "Nine Series" matrix.
 */
export const LINE_MARKER_SHAPE_ORDER: readonly LineMarkerShape[] = [
	'circle',
	'square',
	'triangle',
	'diamond',
	'triangle-down',
	'd-up',
	'd-down',
	'bar-h',
	'bar-v',
];

/**
 * Nine `stroke-dasharray` values arranged as a 3x3 grid of dot-thickness x gap,
 * so series 1 stays the finest dotted line and series 9 the chunkiest dashed.
 * Series `i` uses pattern `i % 9`. `blue` mode leans on these to keep series
 * distinguishable in monochrome.
 */
export const LINE_DASH_PATTERNS: readonly string[] = [
	'1 1',
	'2 1',
	'4 1',
	'1 2',
	'2 2',
	'4 2',
	'1 4',
	'2 4',
	'4 4',
];

export function markerShapeFor(index: number): LineMarkerShape {
	return LINE_MARKER_SHAPE_ORDER[index % LINE_MARKER_SHAPE_ORDER.length];
}

export function dashPatternFor(index: number): string {
	return LINE_DASH_PATTERNS[index % LINE_DASH_PATTERNS.length];
}

/**
 * Draws a marker centered at the origin with the given half-extent `size`. The
 * caller positions it with a `translate(...)` transform and sets the fill; the
 * shape carries no color of its own.
 */
export function renderMarker(
	shape: LineMarkerShape,
	size: number
): React.ReactElement {
	switch (shape) {
		case 'square':
			return (
				<rect height={size * 2} width={size * 2} x={-size} y={-size} />
			);
		case 'triangle':
			return (
				<polygon
					points={`0,${-size} ${size},${size} ${-size},${size}`}
				/>
			);
		case 'diamond':
			return (
				<polygon points={`0,${-size} ${size},0 0,${size} ${-size},0`} />
			);
		case 'triangle-down':
			return (
				<polygon
					points={`0,${size} ${size},${-size} ${-size},${-size}`}
				/>
			);
		case 'd-up':
			return (
				<path d={`M ${-size} 0 A ${size} ${size} 0 0 0 ${size} 0 Z`} />
			);
		case 'd-down':
			return (
				<path d={`M ${-size} 0 A ${size} ${size} 0 0 1 ${size} 0 Z`} />
			);
		case 'bar-h':
			return (
				<rect height={size} width={size * 2} x={-size} y={-size / 2} />
			);
		case 'bar-v':
			return (
				<rect height={size * 2} width={size} x={-size / 2} y={-size} />
			);
		case 'circle':
		default:
			return <circle r={size} />;
	}
}
