/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	CHART_FAMILY_CLAY_PALETTE,
	CHART_FAMILY_CLAY_PALETTE_EXTENDED,
	CHART_FAMILY_ORDER,
} from './tokens';

export function getCategoricalColors(count: number): string[] {
	if (count <= 0) {
		return [];
	}

	const baseSize = CHART_FAMILY_ORDER.length;
	const totalSize = baseSize + CHART_FAMILY_CLAY_PALETTE_EXTENDED.length;

	return Array.from({length: count}, (_, index) => {
		const slot = index % totalSize;

		if (slot < baseSize) {
			return CHART_FAMILY_CLAY_PALETTE[CHART_FAMILY_ORDER[slot]];
		}

		return CHART_FAMILY_CLAY_PALETTE_EXTENDED[slot - baseSize];
	});
}
