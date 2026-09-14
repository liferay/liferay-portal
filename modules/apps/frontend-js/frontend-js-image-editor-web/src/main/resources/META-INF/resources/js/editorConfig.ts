/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FILTER_PRESETS} from './imaging/FilterDefs';
import {AdjustmentKey, FilterPreset, RatioPreset} from './state/types';

export const ADJUSTMENT_KEYS: AdjustmentKey[] = [
	'brightness',
	'contrast',
	'saturation',
	'shadows',
	'highlights',
];

export const RATIO_PRESETS: RatioPreset[] = [
	'custom',
	'original',
	'1:1',
	'4:3',
	'16:9',
	'3:4',
	'9:16',
];

export interface EditorConfig {
	adjustments?: false | {sliders?: AdjustmentKey[]};

	crop?:
		| false
		| {
				ratios?: RatioPreset[];
				rotate?: boolean;
				straighten?: boolean;
		  };

	filters?: false | {presets?: FilterPreset[]};
}

interface ResolvedEditorConfig {
	adjustments: AdjustmentKey[];
	crop: {
		enabled: boolean;
		ratios: RatioPreset[];
		rotate: boolean;
		straighten: boolean;
	};
	filters: FilterPreset[];
}

export function resolveConfig(config: EditorConfig = {}): ResolvedEditorConfig {
	const crop = config.crop;

	return {
		adjustments:
			config.adjustments === false
				? []
				: pick(ADJUSTMENT_KEYS, config.adjustments?.sliders),
		crop:
			crop === false
				? {enabled: false, ratios: [], rotate: false, straighten: false}
				: {
						enabled: true,
						ratios: pick(RATIO_PRESETS, crop?.ratios),
						rotate: crop?.rotate ?? true,
						straighten: crop?.straighten ?? true,
					},
		filters:
			config.filters === false
				? []
				: pick(FILTER_PRESETS, config.filters?.presets),
	};
}

function pick<T>(all: T[], wanted?: T[]): T[] {
	if (!wanted) {
		return all;
	}

	const set = new Set(wanted);

	return all.filter((item) => set.has(item));
}
