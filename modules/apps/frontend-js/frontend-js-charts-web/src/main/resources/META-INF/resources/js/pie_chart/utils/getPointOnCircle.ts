/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Point} from '../types/Point';

export function getPointOnCircle(
	centerX: number,
	centerY: number,
	radius: number,
	angle: number
): Point {
	return {
		x: centerX + radius * Math.cos(angle),
		y: centerY + radius * Math.sin(angle),
	};
}
