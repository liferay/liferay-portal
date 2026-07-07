/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {WORLD_MAP_DATA} from '../geography/mapChartData';
import {MapDatum} from '../types/MapDatum';

export function getCountryLabel(datum: MapDatum): string {
	if (datum.label) {
		return datum.label;
	}

	return WORLD_MAP_DATA[datum.country]?.name ?? datum.country;
}
