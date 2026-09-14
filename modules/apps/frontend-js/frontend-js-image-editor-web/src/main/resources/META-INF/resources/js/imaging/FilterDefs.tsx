/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Adjustments} from '../state/types';

const TONE_CURVE_STEPS = 16;

const TONE_CURVE_STRENGTH = 0.35;

interface Props {
	adjustments: Adjustments;
	id: string;
}

export function FilterDefs({adjustments, id}: Props) {
	const brightnessSlope = 1 + adjustments.brightness / 100;
	const contrastSlope = 1 + adjustments.contrast / 100;
	const contrastIntercept = 0.5 * (1 - contrastSlope);
	const saturation = 1 + adjustments.saturation / 100;
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
		</filter>
	);
}

export function isIdentityFilter(adjustments: Adjustments): boolean {
	return Object.values(adjustments).every((value) => value === 0);
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
