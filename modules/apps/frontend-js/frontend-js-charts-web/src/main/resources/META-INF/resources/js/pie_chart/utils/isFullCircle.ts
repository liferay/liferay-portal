/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FULL_CIRCLE_EPSILON, FULL_CIRCLE_RADIANS} from '../constants';

export function isFullCircle(sweepAngle: number): boolean {
	return sweepAngle >= FULL_CIRCLE_RADIANS - FULL_CIRCLE_EPSILON;
}
