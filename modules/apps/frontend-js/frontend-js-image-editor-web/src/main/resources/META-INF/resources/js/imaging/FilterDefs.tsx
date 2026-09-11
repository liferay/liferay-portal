/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Adjustments} from '../state/types';

interface Props {
	adjustments: Adjustments;
	id: string;
}

export function FilterDefs({adjustments, id}: Props) {
	const brightnessSlope = 1 + adjustments.brightness / 100;
	const contrastSlope = 1 + adjustments.contrast / 100;
	const contrastIntercept = 0.5 * (1 - contrastSlope);
	const saturation = 1 + adjustments.saturation / 100;

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
		</filter>
	);
}

export function isIdentityFilter(adjustments: Adjustments): boolean {
	return Object.values(adjustments).every((value) => value === 0);
}
