/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Adjustments, FilterPreset} from '../state/types';

const FILTER_RECIPES: Record<FilterPreset, FilterRecipe> = {
	bleach: {
		curve: curve(0, 1.05, 0.72),
		saturate: 0.35,
	},
	cool: {matrix: '0.92 0 0 0 0  0 0.99 0 0 0  0 0 1.08 0 0  0 0 0 1 0'},
	crossprocess: {
		channels: {
			b: curve(0.08, 0.82, 1.15),
			g: curve(0.02, 0.98, 1),
			r: curve(0, 1.05, 0.85),
		},
	},
	cyanotype: {
		matrix: '0.18 0.42 0.08 0 0.05  0.28 0.62 0.12 0 0.09  0.42 0.86 0.18 0 0.16  0 0 0 1 0',
	},
	fade: {curve: curve(0.14, 0.78, 1)},
	grayscale: {saturate: 0},
	invert: {matrix: '-1 0 0 0 1  0 -1 0 0 1  0 0 -1 0 1  0 0 0 1 0'},
	matte: {curve: curve(0.1, 0.8, 1.08), saturate: 0.82},
	noir: {
		curve: curve(0, 1.08, 0.85),
		matrix: '0.2764 0.9298 0.0939 0 -0.15  0.2764 0.9298 0.0939 0 -0.15  0.2764 0.9298 0.0939 0 -0.15  0 0 0 1 0',
	},
	none: {},
	polaroid: {
		matrix: '1.438 -0.062 -0.062 0 -0.02  -0.122 1.378 -0.122 0 -0.02  -0.016 -0.016 1.483 0 -0.02  0 0 0 1 0',
	},
	posterize: {levels: 5},
	sepia: {
		matrix: '0.393 0.769 0.189 0 0  0.349 0.686 0.168 0 0  0.272 0.534 0.131 0 0  0 0 0 1 0',
	},
	solarize: {curve: '0 0.25 0.5 0.75 1 0.75 0.5 0.25 0'},
	splittone: {
		channels: {
			b: curve(0.1, 0.78, 1.2),
			g: curve(0.02, 0.92, 1.02),
			r: curve(0, 1.02, 0.88),
		},
		saturate: 0.9,
	},
	tealorange: {
		matrix: '1.16 -0.06 -0.05 0 -0.01  -0.04 1.02 -0.02 0 0  -0.06 0.08 1.06 0 0.03  0 0 0 1 0',
		saturate: 1.1,
	},
	technicolor: {
		matrix: '1.56 -0.4 -0.12 0 0.02  -0.28 1.6 -0.22 0 -0.02  -0.19 -0.35 1.78 0 -0.04  0 0 0 1 0',
	},
	vintage: {
		curve: curve(0.06, 0.88, 1.05),
		matrix: '0.6965 0.3845 0.0945 0 0.03  0.1745 0.843 0.084 0 0.03  0.136 0.267 0.5655 0 0.03  0 0 0 1 0',
	},
	vivid: {
		matrix: '1.3148 -0.286 -0.0288 0 0  -0.1676 1.1966 -0.0288 0 0  -0.1676 -0.286 1.4538 0 0  0 0 0 1 0',
	},
	warm: {matrix: '1.08 0 0 0 0  0 1.02 0 0 0  0 0 0.92 0 0  0 0 0 1 0'},
};

const TONE_CURVE_STEPS = 16;

const TONE_CURVE_STRENGTH = 0.35;

interface FilterRecipe {
	channels?: {b: string; g: string; r: string};

	curve?: string;

	levels?: number;

	matrix?: string;
	saturate?: number;
}

interface Props {
	adjustments: Adjustments;
	filter: FilterPreset;
	id: string;
}

export function FilterDefs({adjustments, filter, id}: Props) {
	const brightnessSlope = 1 + adjustments.brightness / 100;
	const contrastSlope = 1 + adjustments.contrast / 100;
	const contrastIntercept = 0.5 * (1 - contrastSlope);
	const recipe = FILTER_RECIPES[filter];
	const saturation =
		(1 + adjustments.saturation / 100) * (recipe.saturate ?? 1);
	const toneTable =
		adjustments.shadows !== 0 || adjustments.highlights !== 0
			? toneCurveTable(adjustments.shadows, adjustments.highlights)
			: undefined;

	return (
		<filter colorInterpolationFilters="sRGB" id={id}>
			<feComponentTransfer>
				<feFuncR slope={brightnessSlope} type="linear" />

				<feFuncG slope={brightnessSlope} type="linear" />

				<feFuncB slope={brightnessSlope} type="linear" />
			</feComponentTransfer>

			<feComponentTransfer>
				<feFuncR
					intercept={contrastIntercept}
					slope={contrastSlope}
					type="linear"
				/>

				<feFuncG
					intercept={contrastIntercept}
					slope={contrastSlope}
					type="linear"
				/>

				<feFuncB
					intercept={contrastIntercept}
					slope={contrastSlope}
					type="linear"
				/>
			</feComponentTransfer>

			<feColorMatrix type="saturate" values={String(saturation)} />

			{toneTable && (
				<feComponentTransfer>
					<feFuncR tableValues={toneTable} type="table" />

					<feFuncG tableValues={toneTable} type="table" />

					<feFuncB tableValues={toneTable} type="table" />
				</feComponentTransfer>
			)}

			{recipe.matrix && (
				<feColorMatrix type="matrix" values={recipe.matrix} />
			)}

			{recipe.curve && (
				<feComponentTransfer>
					<feFuncR tableValues={recipe.curve} type="table" />

					<feFuncG tableValues={recipe.curve} type="table" />

					<feFuncB tableValues={recipe.curve} type="table" />
				</feComponentTransfer>
			)}

			{recipe.channels && (
				<feComponentTransfer>
					<feFuncR tableValues={recipe.channels.r} type="table" />

					<feFuncG tableValues={recipe.channels.g} type="table" />

					<feFuncB tableValues={recipe.channels.b} type="table" />
				</feComponentTransfer>
			)}

			{recipe.levels && (
				<feComponentTransfer>
					<feFuncR
						tableValues={quantise(recipe.levels)}
						type="discrete"
					/>

					<feFuncG
						tableValues={quantise(recipe.levels)}
						type="discrete"
					/>

					<feFuncB
						tableValues={quantise(recipe.levels)}
						type="discrete"
					/>
				</feComponentTransfer>
			)}
		</filter>
	);
}

export function isIdentityFilter(
	adjustments: Adjustments,
	filter: FilterPreset
): boolean {
	return (
		filter === 'none' &&
		Object.values(adjustments).every((value) => value === 0)
	);
}

function curve(toe: number, gain: number, gamma: number): string {
	return Array.from({length: 9}, (_, index) => {
		const input = index / 8;

		return Math.min(
			1,
			Math.max(0, toe + gain * Math.pow(input, gamma))
		).toFixed(4);
	}).join(' ');
}

function quantise(levels: number): string {
	return Array.from({length: levels}, (_, index) =>
		(index / (levels - 1)).toFixed(3)
	).join(' ');
}

function toneCurveTable(shadows: number, highlights: number): string {
	const samples: string[] = [];

	for (let i = 0; i <= TONE_CURVE_STEPS; i++) {
		const input = i / TONE_CURVE_STEPS;

		const output =
			input +
			TONE_CURVE_STRENGTH * (shadows / 100) * (1 - input) ** 2 +
			TONE_CURVE_STRENGTH * (highlights / 100) * input ** 2;

		samples.push(Math.min(1, Math.max(0, output)).toFixed(4));
	}

	return samples.join(' ');
}
