/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const MIN_CROP_SIZE = 16;

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
	crop: CropRect;

	flipHorizontal: boolean;
	rotation: Rotation;
	sourceHeight: number;
	sourceWidth: number;
}

interface HistoryEntry {
	label: string;
	state: EditState;
}

type Rotation = 0 | 90 | 180 | 270;

export function rotatedSize(state: EditState): {
	height: number;
	width: number;
} {
	return state.rotation % 180 === 0
		? {height: state.sourceHeight, width: state.sourceWidth}
		: {height: state.sourceWidth, width: state.sourceHeight};
}
