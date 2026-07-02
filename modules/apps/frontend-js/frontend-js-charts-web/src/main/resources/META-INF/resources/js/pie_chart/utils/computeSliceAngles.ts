/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CIRCLE_START_ANGLE, FULL_CIRCLE_RADIANS} from '../constants';
import {SliceAngles} from '../types/SliceAngles';

interface ComputeSliceAnglesParameters {
	precedingTotal: number;
	total: number;
	value: number;
}

export function computeSliceAngles({
	precedingTotal,
	total,
	value,
}: ComputeSliceAnglesParameters): SliceAngles {
	const startAngle =
		CIRCLE_START_ANGLE + (precedingTotal / total) * FULL_CIRCLE_RADIANS;
	const sweepAngle = (Math.max(0, value) / total) * FULL_CIRCLE_RADIANS;

	return {
		endAngle: startAngle + sweepAngle,
		startAngle,
		sweepAngle,
	};
}
