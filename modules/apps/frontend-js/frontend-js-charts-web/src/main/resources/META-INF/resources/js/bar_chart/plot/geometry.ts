/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {BarDatum} from '../types';

/** Precomputed SVG coordinates for a single bar and its satellites. */
export interface BarLayout {
	barRx: number;
	height: number;
	labelAnchor: 'end' | 'middle';
	labelX: number;
	labelY: number;
	trackHeight: number;
	trackWidth: number;
	trackX: number;
	trackY: number;
	valueHeight: number;
	valueWidth: number;
	valueX: number;
	valueY: number;
	width: number;
	x: number;
	y: number;
}

export interface BarChartGeometry {
	axis: {x1: number; x2: number; y1: number; y2: number};
	bars: BarLayout[];
}

interface Options {
	data: BarDatum[];
	height: number;
	orientation: 'horizontal' | 'vertical';
	rounded: boolean;
	size: 'default' | 'inline';
	width: number;
}

/** Precomputed SVG coordinates for a single segment of the stacked meter. */
export interface StackedSegmentLayout {
	roundLeft: boolean;
	roundRight: boolean;
	rowY: number;
	rx: number;
	thickness: number;
	width: number;
	x: number;
}

export interface StackedBarChartGeometry {
	segments: StackedSegmentLayout[];
}

interface StackedOptions {
	data: BarDatum[];
	height: number;
	rounded: boolean;
	size: 'default' | 'inline';
	width: number;
}

const VERTICAL_PADDING = {bottom: 32, left: 40, right: 16, top: 28};
const HORIZONTAL_PADDING = {bottom: 24, left: 96, right: 48, top: 16};

// Stacked meters read edge-to-edge, so almost no side padding — the row spans
// the full measured width. The tooltip is free to overflow the SVG (see the
// `overflow: visible` rule) so it needs no reserved top room.

const STACKED_PADDING = {bottom: 8, left: 2, right: 2, top: 8};

const STACKED_GAP = 2;

const VALUE_CHAR_WIDTH = 7.5;
const VALUE_HEIGHT = 18;
const VALUE_PADDING_X = 6;

/**
 * Turns the chart props into the per-bar coordinates the SVG needs, keeping the
 * layout math out of the render tree. Vertical bars rise from the baseline;
 * horizontal bars grow rightward from the left gutter.
 */
export function getBarChartGeometry({
	data,
	height,
	orientation,
	rounded,
	size,
	width,
}: Options): BarChartGeometry {
	const isVertical = orientation === 'vertical';
	const pad = isVertical ? VERTICAL_PADDING : HORIZONTAL_PADDING;

	const plotWidth = Math.max(0, width - pad.left - pad.right);
	const plotHeight = Math.max(0, height - pad.top - pad.bottom);

	const max = Math.max(0, ...data.map((datum) => datum.value));
	const bandSize =
		(isVertical ? plotWidth : plotHeight) / Math.max(1, data.length);
	const barThickness = size === 'inline' ? 8 : Math.max(4, bandSize * 0.6);
	const barRx = rounded ? barThickness / 2 : 2;

	const bars = data.map((datum, index): BarLayout => {
		const value = Math.max(0, datum.value);
		const ratio = max === 0 ? 0 : value / max;
		const length = ratio * (isVertical ? plotHeight : plotWidth);
		const bandStart =
			(isVertical ? pad.left : pad.top) +
			index * bandSize +
			(bandSize - barThickness) / 2;

		const x = isVertical ? bandStart : pad.left;
		const y = isVertical ? height - pad.bottom - length : bandStart;

		const valueWidth =
			String(datum.value).length * VALUE_CHAR_WIDTH + VALUE_PADDING_X * 2;

		return {
			barRx,
			height: isVertical ? length : barThickness,
			labelAnchor: isVertical ? 'middle' : 'end',
			labelX: isVertical ? x + barThickness / 2 : pad.left - 8,
			labelY: isVertical
				? height - pad.bottom + 16
				: bandStart + barThickness / 2 + 4,
			trackHeight: isVertical ? plotHeight : barThickness,
			trackWidth: isVertical ? barThickness : plotWidth,
			trackX: isVertical ? x : pad.left,
			trackY: isVertical ? pad.top : y,
			valueHeight: VALUE_HEIGHT,
			valueWidth,
			valueX: isVertical
				? x + barThickness / 2
				: x + length + VALUE_PADDING_X + valueWidth / 2,
			valueY: isVertical
				? y - 4 - VALUE_HEIGHT / 2
				: bandStart + barThickness / 2,
			width: isVertical ? barThickness : length,
			x,
			y,
		};
	});

	return {
		axis: {
			x1: pad.left,
			x2: isVertical ? width - pad.right : pad.left,
			y1: isVertical ? height - pad.bottom : pad.top,
			y2: height - pad.bottom,
		},
		bars,
	};
}

/**
 * Path for a stacked segment with rounding on selectable sides. `<rect rx>`
 * rounds all four corners, but a segmented meter reads as a single pill: only
 * the first segment's left corners and the last segment's right corners should
 * be rounded — every inner edge stays square. Left/right toggle independently
 * so a lone segment gets a full pill.
 */
export function stackedSegmentPath(
	x: number,
	y: number,
	w: number,
	h: number,
	r: number,
	roundLeft: boolean,
	roundRight: boolean
): string {
	const cap = Math.min(r, w / 2, h / 2);
	const rl = roundLeft ? cap : 0;
	const rr = roundRight ? cap : 0;

	return [
		`M ${x + rl} ${y}`,
		`H ${x + w - rr}`,
		rr && `A ${rr} ${rr} 0 0 1 ${x + w} ${y + rr}`,
		`V ${y + h - rr}`,
		rr && `A ${rr} ${rr} 0 0 1 ${x + w - rr} ${y + h}`,
		`H ${x + rl}`,
		rl && `A ${rl} ${rl} 0 0 1 ${x} ${y + h - rl}`,
		`V ${y + rl}`,
		rl && `A ${rl} ${rl} 0 0 1 ${x + rl} ${y}`,
		'Z',
	]
		.filter(Boolean)
		.join(' ');
}

/**
 * Turns the chart props into the per-segment coordinates for the stacked meter:
 * every datum becomes a slice of one horizontal row, sized to its share of the
 * total, with a fixed gap between adjacent segments and the row centered
 * vertically in the plot area.
 */
export function getStackedBarChartGeometry({
	data,
	height,
	rounded,
	size,
	width,
}: StackedOptions): StackedBarChartGeometry {
	const pad = STACKED_PADDING;

	const plotWidth = Math.max(0, width - pad.left - pad.right);
	const plotHeight = Math.max(0, height - pad.top - pad.bottom);

	const thickness = size === 'inline' ? 8 : Math.max(4, plotHeight * 0.6);
	const rx = rounded ? thickness / 2 : 2;

	const total = data.reduce(
		(acc, datum) => acc + Math.max(0, datum.value),
		0
	);

	const count = data.length;
	const gapTotal = Math.max(0, (count - 1) * STACKED_GAP);
	const available = Math.max(0, plotWidth - gapTotal);
	const rowY = pad.top + (plotHeight - thickness) / 2;

	let cursor = pad.left;

	const segments = data.map((datum, index): StackedSegmentLayout => {
		const share = total === 0 ? 0 : Math.max(0, datum.value) / total;
		const segmentWidth = share * available;
		const x = cursor;

		cursor += segmentWidth + STACKED_GAP;

		return {
			roundLeft: index === 0,
			roundRight: index === count - 1,
			rowY,
			rx,
			thickness,
			width: segmentWidth,
			x,
		};
	});

	return {segments};
}
