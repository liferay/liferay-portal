/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const DEFAULT_ADJUSTMENTS: Adjustments = {
	brightness: 0,
	contrast: 0,
	highlights: 0,
	saturation: 0,
	shadows: 0,
};

export const MIN_CROP_SIZE = 16;

export const RATIO_VALUES: Record<
	Exclude<RatioPreset, 'custom' | 'original'>,
	number
> = {
	'1:1': 1,
	'3:4': 3 / 4,
	'4:3': 4 / 3,
	'9:16': 9 / 16,
	'16:9': 16 / 9,
};

export type AdjustmentKey = keyof Adjustments;

export interface Adjustments {
	brightness: number;
	contrast: number;
	highlights: number;
	saturation: number;
	shadows: number;
}

export interface CropRect {
	height: number;
	width: number;
	x: number;
	y: number;
}

export interface EditorHistory {
	future: HistoryEntry[];
	past: HistoryEntry[];

	pendingBase?: HistoryEntry;

	present: EditState;
}

export interface EditState {
	adjustments: Adjustments;

	angle: number;
	crop: CropRect;

	flipHorizontal: boolean;

	ratio: RatioPreset;
	rotation: Rotation;
	sourceHeight: number;
	sourceWidth: number;
}

interface HistoryEntry {
	label: string;
	state: EditState;
}

export type RatioPreset =
	| '1:1'
	| '16:9'
	| '3:4'
	| '4:3'
	| '9:16'
	| 'custom'
	| 'original';

type Rotation = 0 | 90 | 180 | 270;

export function rotatedSize(state: EditState): {
	height: number;
	width: number;
} {
	return state.rotation % 180 === 0
		? {height: state.sourceHeight, width: state.sourceWidth}
		: {height: state.sourceWidth, width: state.sourceHeight};
}
