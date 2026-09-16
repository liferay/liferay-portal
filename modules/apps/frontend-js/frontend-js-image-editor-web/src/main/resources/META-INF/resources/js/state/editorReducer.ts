/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mirrorOverlay} from '../imaging/overlayShapes';
import {
	imageMatrix,
	invert,
	multiply,
	transformOverlay,
} from '../imaging/overlayTransform';
import {patchOverlay} from './overlayPatch';
import {
	AdjustmentKey,
	CropRect,
	DEFAULT_ADJUSTMENTS,
	DEFAULT_FRAME,
	EditState,
	EditorHistory,
	FilterPreset,
	Frame,
	MIN_CROP_SIZE,
	Overlay,
	RATIO_VALUES,
	RatioPreset,
	rotatedSize,
} from './types';

export type EditorAction =
	| {overlay: Overlay; type: 'add-overlay'}
	| {type: 'cancel-gesture'}
	| {id: string; newId: string; type: 'duplicate-overlay'}
	| {type: 'flip-horizontal'}
	| {direction: -1 | 1; id: string; type: 'move-overlay-layer'}
	| {
			dx: number;
			dy: number;
			ids: string[];
			transient?: boolean;
			type: 'move-overlays';
	  }
	| {type: 'redo'}
	| {id: string; type: 'remove-overlay'}
	| {ids: string[]; type: 'remove-overlays'}
	| {type: 'reset-adjustments'}
	| {type: 'rotate-90'}
	| {
			key: AdjustmentKey;
			transient?: boolean;
			type: 'set-adjustment';
			value: number;
	  }
	| {angle: number; transient?: boolean; type: 'set-angle'}
	| {crop: CropRect; transient?: boolean; type: 'set-crop'}
	| {filter: FilterPreset; type: 'set-filter'}
	| {frame: Partial<Frame>; transient?: boolean; type: 'set-frame'}
	| {ratio: RatioPreset; type: 'set-ratio'}
	| {type: 'undo'}
	| {
			id: string;
			patch: Partial<Overlay>;
			transient?: boolean;
			type: 'update-overlay';
	  };

export interface InitialStateOptions {
	ratios?: RatioPreset[];
}

const HISTORY_LIMIT = 100;

export function cloneOffset(
	state: Pick<EditState, 'sourceHeight' | 'sourceWidth'>
): number {
	return Math.round(
		Math.max(16, Math.min(state.sourceWidth, state.sourceHeight) * 0.02)
	);
}

export function clampCrop(
	crop: CropRect,
	bounds: {height: number; width: number}
): CropRect {
	const width = Math.min(
		Math.max(Math.round(crop.width), MIN_CROP_SIZE),
		bounds.width
	);
	const height = Math.min(
		Math.max(Math.round(crop.height), MIN_CROP_SIZE),
		bounds.height
	);
	const x = Math.min(Math.max(Math.round(crop.x), 0), bounds.width - width);
	const y = Math.min(Math.max(Math.round(crop.y), 0), bounds.height - height);

	return {height, width, x, y};
}

export function editorReducer(
	history: EditorHistory,
	action: EditorAction
): EditorHistory {
	const {present} = history;

	switch (action.type) {
		case 'add-overlay': {
			return applyEdit(
				history,
				{...present, overlays: [...present.overlays, action.overlay]},
				Liferay.Language.get('annotation')
			);
		}

		case 'duplicate-overlay': {
			const index = present.overlays.findIndex(
				(overlay) => overlay.id === action.id
			);

			if (index < 0) {
				return history;
			}

			const source = present.overlays[index];

			const offset = cloneOffset(present);

			const clone: Overlay = {
				...source,
				id: action.newId,
				x: source.x + offset,
				y: source.y + offset,
			};

			const overlays = [...present.overlays];

			overlays.splice(index + 1, 0, clone);

			return applyEdit(
				history,
				{...present, overlays},
				Liferay.Language.get('annotation')
			);
		}

		case 'move-overlay-layer': {
			const index = present.overlays.findIndex(
				(overlay) => overlay.id === action.id
			);
			const target = index + action.direction;

			if (index < 0 || target < 0 || target >= present.overlays.length) {
				return history;
			}

			const overlays = [...present.overlays];

			[overlays[index], overlays[target]] = [
				overlays[target],
				overlays[index],
			];

			return applyEdit(
				history,
				{...present, overlays},
				Liferay.Language.get('layer-order')
			);
		}

		case 'move-overlays': {
			const moving = new Set(action.ids);

			if (!moving.size) {
				return history;
			}

			if (
				!action.transient &&
				!history.pendingBase &&
				!action.dx &&
				!action.dy
			) {
				return history;
			}

			return applyEdit(
				history,
				{
					...present,
					overlays: present.overlays.map((overlay) =>
						moving.has(overlay.id)
							? {
									...overlay,
									x: overlay.x + action.dx,
									y: overlay.y + action.dy,
								}
							: overlay
					),
				},
				Liferay.Language.get('annotation'),
				action.transient
			);
		}

		case 'remove-overlays': {
			const removing = new Set(action.ids);

			if (!removing.size) {
				return history;
			}

			return applyEdit(
				history,
				{
					...present,
					overlays: present.overlays.filter(
						(overlay) => !removing.has(overlay.id)
					),
				},
				Liferay.Language.get('annotation')
			);
		}

		case 'remove-overlay': {
			return applyEdit(
				history,
				{
					...present,
					overlays: present.overlays.filter(
						(overlay) => overlay.id !== action.id
					),
				},
				Liferay.Language.get('annotation')
			);
		}

		case 'update-overlay': {
			const target = present.overlays.find(
				(overlay) => overlay.id === action.id
			);

			if (!target) {
				return history;
			}

			const patched = patchOverlay(target, action.patch);

			if (
				patched === target &&
				!action.transient &&
				!history.pendingBase
			) {
				return history;
			}

			return applyEdit(
				history,
				{
					...present,
					overlays: present.overlays.map((overlay) =>
						overlay.id === action.id ? patched : overlay
					),
				},
				Liferay.Language.get('annotation'),
				action.transient
			);
		}

		case 'set-adjustment': {
			if (
				!action.transient &&
				!history.pendingBase &&
				present.adjustments[action.key] === action.value
			) {
				return history;
			}

			return applyEdit(
				history,
				{
					...present,
					adjustments: {
						...present.adjustments,
						[action.key]: action.value,
					},
				},
				Liferay.Language.get('adjustments'),
				action.transient
			);
		}

		case 'reset-adjustments': {
			return applyEdit(
				history,
				{...present, adjustments: {...DEFAULT_ADJUSTMENTS}},
				Liferay.Language.get('adjustments')
			);
		}

		case 'set-angle': {
			if (
				!action.transient &&
				!history.pendingBase &&
				present.angle === action.angle
			) {
				return history;
			}

			return applyEdit(
				history,
				{...present, angle: action.angle},
				Liferay.Language.get('straighten'),
				action.transient
			);
		}

		case 'set-crop': {
			const crop = clampCrop(action.crop, rotatedSize(present));

			if (
				!action.transient &&
				!history.pendingBase &&
				cropsEqual(crop, present.crop)
			) {
				return history;
			}

			return applyEdit(
				history,
				{
					...present,
					crop,
					ratio: cropsEqual(crop, present.crop)
						? present.ratio
						: 'custom',
				},
				Liferay.Language.get('crop'),
				action.transient
			);
		}

		case 'set-filter': {
			return applyEdit(
				history,
				{...present, filter: action.filter},
				Liferay.Language.get('filter')
			);
		}

		case 'set-frame': {
			const frame = {...present.frame, ...action.frame};

			if (
				!action.transient &&
				!history.pendingBase &&
				frame.color === present.frame.color &&
				frame.kind === present.frame.kind &&
				frame.offset === present.frame.offset &&
				frame.overAnnotations === present.frame.overAnnotations &&
				frame.size === present.frame.size
			) {
				return history;
			}

			return applyEdit(
				history,
				{...present, frame},
				Liferay.Language.get('frame'),
				action.transient
			);
		}

		case 'set-ratio': {
			let crop = present.crop;

			if (action.ratio === 'original') {
				const bounds = rotatedSize(present);

				crop = {
					height: bounds.height,
					width: bounds.width,
					x: 0,
					y: 0,
				};
			}
			else if (action.ratio !== 'custom') {
				crop = centeredCrop(present, RATIO_VALUES[action.ratio]);
			}

			return applyEdit(
				history,
				{...present, crop, ratio: action.ratio},
				Liferay.Language.get('ratio')
			);
		}

		case 'flip-horizontal': {
			const bounds = rotatedSize(present);

			return applyEdit(
				history,
				{
					...present,
					crop: {
						...present.crop,
						x: bounds.width - present.crop.x - present.crop.width,
					},
					flipHorizontal: !present.flipHorizontal,
					overlays: present.overlays.map((overlay) =>
						mirrorOverlay(overlay, bounds.width)
					),
				},
				Liferay.Language.get('flip')
			);
		}

		case 'rotate-90': {
			const next: EditState = {
				...present,
				rotation: ((present.rotation + 90) %
					360) as EditState['rotation'],
			};

			const bounds = rotatedSize(next);

			const mapping = multiply(
				imageMatrix(next),
				invert(imageMatrix(present))
			);

			return applyEdit(
				history,
				{
					...next,
					crop: {
						height: bounds.height,
						width: bounds.width,
						x: 0,
						y: 0,
					},
					overlays: present.overlays.map((overlay) =>
						transformOverlay(overlay, mapping)
					),
					ratio: 'original',
				},
				Liferay.Language.get('rotation')
			);
		}

		case 'cancel-gesture': {
			if (!history.pendingBase) {
				return history;
			}

			return {
				...history,
				pendingBase: undefined,
				present: history.pendingBase.state,
			};
		}

		case 'undo': {
			if (history.pendingBase) {
				return {
					...history,
					pendingBase: undefined,
					present: history.pendingBase.state,
				};
			}

			if (!history.past.length) {
				return history;
			}

			const past = [...history.past];
			const entry = past.pop()!;

			return {
				future: [
					{label: entry.label, state: history.present},
					...history.future,
				],
				past,
				present: entry.state,
			};
		}

		case 'redo': {
			if (!history.future.length) {
				return history;
			}

			const [entry, ...future] = history.future;

			return {
				future,
				past: [
					...history.past,
					{label: entry.label, state: history.present},
				],
				present: entry.state,
			};
		}

		default: {
			return history;
		}
	}
}

export function initialEditState(
	sourceWidth: number,
	sourceHeight: number,
	options: InitialStateOptions = {}
): EditState {
	const ratio = initialRatio(options.ratios);

	const state: EditState = {
		adjustments: {...DEFAULT_ADJUSTMENTS},
		angle: 0,
		crop: {height: sourceHeight, width: sourceWidth, x: 0, y: 0},
		filter: 'none',
		flipHorizontal: false,
		frame: {...DEFAULT_FRAME},
		overlays: [],
		ratio,
		rotation: 0,
		sourceHeight,
		sourceWidth,
	};

	if (ratio !== 'original' && ratio !== 'custom') {
		state.crop = centeredCrop(state, RATIO_VALUES[ratio]);
	}

	return state;
}

export function initialHistory(
	sourceWidth: number,
	sourceHeight: number,
	options: InitialStateOptions = {}
): EditorHistory {
	return {
		future: [],
		past: [],
		present: initialEditState(sourceWidth, sourceHeight, options),
	};
}

export function redoLabel(history: EditorHistory): string | null {
	return history.future.length ? history.future[0].label : null;
}

export function undoLabel(history: EditorHistory): string | null {
	if (history.pendingBase) {
		return history.pendingBase.label;
	}

	return history.past.length
		? history.past[history.past.length - 1].label
		: null;
}

function applyEdit(
	history: EditorHistory,
	next: EditState,
	label: string,
	transient?: boolean
): EditorHistory {
	if (transient) {
		return {
			...history,
			pendingBase: history.pendingBase ?? {
				label,
				state: history.present,
			},
			present: next,
		};
	}

	const base = history.pendingBase ?? {label, state: history.present};

	const past = [...history.past, {label, state: base.state}];

	if (past.length > HISTORY_LIMIT) {
		past.shift();
	}

	return {
		future: [],
		past,
		pendingBase: undefined,
		present: next,
	};
}

function centeredCrop(state: EditState, ratio: number): CropRect {
	const bounds = rotatedSize(state);

	let width = bounds.width;
	let height = width / ratio;

	if (height > bounds.height) {
		height = bounds.height;
		width = height * ratio;
	}

	return clampCrop(
		{
			height,
			width,
			x: (bounds.width - width) / 2,
			y: (bounds.height - height) / 2,
		},
		bounds
	);
}

function cropsEqual(a: CropRect, b: CropRect): boolean {
	return (
		a.height === b.height &&
		a.width === b.width &&
		a.x === b.x &&
		a.y === b.y
	);
}

function initialRatio(allowed: RatioPreset[] | undefined): RatioPreset {
	if (!allowed || !allowed.length || allowed.includes('original')) {
		return 'original';
	}

	if (allowed.includes('custom')) {
		return 'custom';
	}

	return allowed[0];
}
