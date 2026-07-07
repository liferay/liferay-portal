/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {WORLD_MAP_DATA} from '../geography/mapChartData';
import {MapDatum} from '../types/MapDatum';

export function getMatchedDataIndices(data: MapDatum[]): number[] {
	return data.reduce<number[]>((indices, datum, index) => {
		if (WORLD_MAP_DATA[datum.country]) {
			indices.push(index);
		}

		return indices;
	}, []);
}
