/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	CropRect,
	EditState,
	EditorHistory,
	MIN_CROP_SIZE,
	rotatedSize,
} from './types';

export type EditorAction =
	| {type: 'cancel-gesture'}
	| {type: 'flip-horizontal'}
	| {type: 'redo'}
	| {type: 'rotate-90'}
	| {crop: CropRect; transient?: boolean; type: 'set-crop'}
	| {type: 'undo'};

const HISTORY_LIMIT = 100;

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
				{...present, crop},
				Liferay.Language.get('crop'),
				action.transient
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
	sourceHeight: number
): EditState {
	return {
		crop: {height: sourceHeight, width: sourceWidth, x: 0, y: 0},
		flipHorizontal: false,
		rotation: 0,
		sourceHeight,
		sourceWidth,
	};
}

export function initialHistory(
	sourceWidth: number,
	sourceHeight: number
): EditorHistory {
	return {
		future: [],
		past: [],
		present: initialEditState(sourceWidth, sourceHeight),
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

function cropsEqual(a: CropRect, b: CropRect): boolean {
	return (
		a.height === b.height &&
		a.width === b.width &&
		a.x === b.x &&
		a.y === b.y
	);
}
