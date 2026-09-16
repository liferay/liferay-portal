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

export const DEFAULT_FRAME: Frame = {
	color: '#ffffff',
	kind: 'none',
	offset: 0,
	overAnnotations: true,
	size: 4,
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

export type ArrowHead = 'filled' | 'open';

export interface ArrowOverlay {
	color: string;

	dx: number;

	dy: number;

	head: ArrowHead;
	id: string;
	kind: 'arrow';
	opacity?: number;

	thickness: number;

	x: number;
	y: number;
}

export interface Adjustments {
	brightness: number;
	contrast: number;
	highlights: number;
	saturation: number;
	shadows: number;
}

interface BoxOverlayBase {
	borderColor?: string;
	borderWidth?: number;
	color: string;

	height: number;
	id: string;
	opacity?: number;
	rotation?: number;

	sketchSeed?: number;

	width: number;
	x: number;
	y: number;
}

export interface CircleOverlay extends BoxOverlayBase {
	kind: 'circle';
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
	filter: FilterPreset;

	flipHorizontal: boolean;

	frame: Frame;

	overlays: Overlay[];
	ratio: RatioPreset;
	rotation: Rotation;
	sourceHeight: number;
	sourceWidth: number;
}

export type FilterPreset =
	| 'bleach'
	| 'cool'
	| 'crossprocess'
	| 'cyanotype'
	| 'fade'
	| 'grayscale'
	| 'invert'
	| 'matte'
	| 'noir'
	| 'none'
	| 'polaroid'
	| 'posterize'
	| 'sepia'
	| 'solarize'
	| 'splittone'
	| 'tealorange'
	| 'technicolor'
	| 'vintage'
	| 'vivid'
	| 'warm';

export interface Frame {
	color: string;
	kind: FrameKind;

	offset: number;

	overAnnotations: boolean;

	size: number;
}

export type FrameKind =
	| 'bevel'
	| 'corners'
	| 'dashed'
	| 'double'
	| 'inset'
	| 'line'
	| 'mat'
	| 'none'
	| 'polaroid'
	| 'ticks';

interface HistoryEntry {
	label: string;
	state: EditState;
}

export type Overlay =
	| ArrowOverlay
	| CircleOverlay
	| ShapeOverlay
	| StrokeOverlay
	| TextOverlay;

export type RatioPreset =
	| '1:1'
	| '16:9'
	| '3:4'
	| '4:3'
	| '9:16'
	| 'custom'
	| 'original';

type Rotation = 0 | 90 | 180 | 270;

export interface ShapeOverlay extends BoxOverlayBase {
	kind: 'shape';
}

export interface StrokeOverlay {
	color: string;
	id: string;
	kind: 'stroke';
	opacity?: number;

	points: number[];

	rotation?: number;

	smooth: boolean;

	width: number;

	x: number;
	y: number;
}

export interface TextOverlay {
	color: string;
	fontFamily: string;
	fontSize: number;
	id: string;
	kind: 'text';
	opacity?: number;
	rotation?: number;
	text: string;
	x: number;
	y: number;
}

export function rotatedSize(state: EditState): {
	height: number;
	width: number;
} {
	return state.rotation % 180 === 0
		? {height: state.sourceHeight, width: state.sourceWidth}
		: {height: state.sourceWidth, width: state.sourceHeight};
}

export function isBoxOverlay(
	overlay: Overlay
): overlay is CircleOverlay | ShapeOverlay {
	return overlay.kind === 'circle' || overlay.kind === 'shape';
}
