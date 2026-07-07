/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const MIN_TARGET_RADIUS_PX = 12;

export function computeMarkerHitRadius(
	fallbackRadius: number,
	renderedWidthPx: number | undefined,
	viewBoxWidth: number
): number {
	if (!renderedWidthPx) {
		return fallbackRadius;
	}

	return (MIN_TARGET_RADIUS_PX / renderedWidthPx) * viewBoxWidth;
}
