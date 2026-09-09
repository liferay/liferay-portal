/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {t} from '../i18n';
import {EditState, EditorHistory} from './types';

export type EditorAction =
	| {type: 'redo'}
	| {type: 'flip-horizontal'}
	| {type: 'rotate-90'}
	| {type: 'undo'};

export function initialEditState(
	sourceWidth: number,
	sourceHeight: number
): EditState {
	return {
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

export function undoLabel(history: EditorHistory): string | null {
	return history.past.length
		? history.past[history.past.length - 1].label
		: null;
}

export function redoLabel(history: EditorHistory): string | null {
	return history.future.length ? history.future[0].label : null;
}

export const HISTORY_LIMIT = 100;

function applyEdit(
	history: EditorHistory,
	next: EditState,
	label: string
): EditorHistory {
	const past = [...history.past, {label, state: history.present}];

	if (past.length > HISTORY_LIMIT) {
		past.shift();
	}

	return {
		future: [],
		past,
		present: next,
	};
}

export function editorReducer(
	history: EditorHistory,
	action: EditorAction
): EditorHistory {
	const {present} = history;

	switch (action.type) {
		case 'flip-horizontal': {
			return applyEdit(
				history,
				{...present, flipHorizontal: !present.flipHorizontal},
				t('label-flip')
			);
		}

		case 'rotate-90': {
			return applyEdit(
				history,
				{
					...present,
					rotation: ((present.rotation + 90) %
						360) as EditState['rotation'],
				},
				t('label-rotate')
			);
		}

		case 'undo': {
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
