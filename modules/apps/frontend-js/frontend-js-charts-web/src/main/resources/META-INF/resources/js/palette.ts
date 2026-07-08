/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CHART_FAMILY_CLAY_PALETTE, CHART_FAMILY_ORDER} from './tokens';

export function getCategoricalColors(count: number): string[] {
	if (count <= 0) {
		return [];
	}

	return Array.from(
		{length: count},
		(_, index) =>
			CHART_FAMILY_CLAY_PALETTE[
				CHART_FAMILY_ORDER[index % CHART_FAMILY_ORDER.length]
			]
	);
}
