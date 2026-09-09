/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	editorReducer,
	initialHistory,
	redoLabel,
	undoLabel,
} from '../../src/main/resources/META-INF/resources/js/state/editorReducer';
import {
	EditorHistory,
	rotatedSize,
} from '../../src/main/resources/META-INF/resources/js/state/types';

const WIDTH = 1600;
const HEIGHT = 1000;

function history(): EditorHistory {
	return initialHistory(WIDTH, HEIGHT);
}

describe('editorReducer', () => {
	it('starts unrotated and unflipped', () => {
		const {present} = history();

		expect(present.rotation).toBe(0);
		expect(present.flipHorizontal).toBe(false);
		expect(rotatedSize(present)).toEqual({height: HEIGHT, width: WIDTH});
	});

	it('swaps dimensions on rotation', () => {
		const next = editorReducer(history(), {type: 'rotate-90'});

		expect(next.present.rotation).toBe(90);
		expect(rotatedSize(next.present)).toEqual({
			height: WIDTH,
			width: HEIGHT,
		});
	});

	it('round-trips undo and redo with labels', () => {
		let state = editorReducer(history(), {type: 'rotate-90'});

		expect(undoLabel(state)).toBe('rotation');

		state = editorReducer(state, {type: 'undo'});

		expect(state.present.rotation).toBe(0);
		expect(redoLabel(state)).toBe('rotation');

		state = editorReducer(state, {type: 'redo'});

		expect(state.present.rotation).toBe(90);
		expect(undoLabel(state)).toBe('rotation');
	});

	it('clears the redo stack on a new edit', () => {
		let state = editorReducer(history(), {type: 'rotate-90'});

		state = editorReducer(state, {type: 'undo'});
		state = editorReducer(state, {type: 'flip-horizontal'});

		expect(state.future).toHaveLength(0);
		expect(redoLabel(state)).toBeNull();
	});
});

describe('flip-horizontal', () => {
	const start = () => initialHistory(1000, 600);

	it('mirrors the composition and returns on the second flip', () => {
		const once = editorReducer(start(), {type: 'flip-horizontal'});

		expect(once.present.flipHorizontal).toBe(true);

		const twice = editorReducer(once, {type: 'flip-horizontal'});

		expect(twice.present.flipHorizontal).toBe(false);
	});

	it('is one undoable step', () => {
		const flipped = editorReducer(start(), {type: 'flip-horizontal'});
		const undone = editorReducer(flipped, {type: 'undo'});

		expect(undone.present.flipHorizontal).toBe(false);
	});
});
