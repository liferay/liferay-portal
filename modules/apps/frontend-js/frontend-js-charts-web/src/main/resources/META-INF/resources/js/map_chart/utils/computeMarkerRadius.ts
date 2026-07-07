/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const MIN_SCALE = 0.45;
const MAX_SCALE = 1;

export function computeMarkerRadius(
	baseRadius: number,
	boundingBoxWidth: number,
	worldViewBoxWidth: number
): number {
	if (!worldViewBoxWidth) {
		return baseRadius;
	}

	const scale = Math.max(
		MIN_SCALE,
		Math.min(MAX_SCALE, boundingBoxWidth / worldViewBoxWidth)
	);

	return baseRadius * scale;
}
