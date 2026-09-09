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

describe('set-crop', () => {
	const start = () => initialHistory(1000, 600);

	it('covers the whole image at first', () => {
		expect(start().present.crop).toEqual({
			height: 600,
			width: 1000,
			x: 0,
			y: 0,
		});
	});

	it('keeps the crop inside the image', () => {
		const next = editorReducer(start(), {
			crop: {height: 700, width: 400, x: 800, y: -20},
			type: 'set-crop',
		});

		expect(next.present.crop).toEqual({
			height: 600,
			width: 400,
			x: 600,
			y: 0,
		});
	});

	it('ignores a crop that changes nothing', () => {
		const initial = start();

		expect(
			editorReducer(initial, {
				crop: initial.present.crop,
				type: 'set-crop',
			})
		).toBe(initial);
	});

	it('collapses a gesture into one undoable step', () => {
		let state = editorReducer(start(), {
			crop: {height: 600, width: 900, x: 0, y: 0},
			transient: true,
			type: 'set-crop',
		});

		state = editorReducer(state, {
			crop: {height: 600, width: 800, x: 0, y: 0},
			transient: true,
			type: 'set-crop',
		});

		expect(state.past).toHaveLength(0);
		expect(undoLabel(state)).toBe('crop');

		state = editorReducer(state, {
			crop: {height: 600, width: 800, x: 0, y: 0},
			type: 'set-crop',
		});

		expect(state.past).toHaveLength(1);

		state = editorReducer(state, {type: 'undo'});

		expect(state.present.crop.width).toBe(1000);
	});

	it('drops the pending gesture when it is cancelled or undone', () => {
		const initial = start();

		const gesture = editorReducer(initial, {
			crop: {height: 600, width: 500, x: 0, y: 0},
			transient: true,
			type: 'set-crop',
		});

		expect(
			editorReducer(gesture, {type: 'cancel-gesture'}).present.crop
		).toEqual(initial.present.crop);

		const undone = editorReducer(gesture, {type: 'undo'});

		expect(undone.present.crop).toEqual(initial.present.crop);
		expect(undone.past).toHaveLength(0);
	});

	it('resets the crop on rotation and mirrors it on flip', () => {
		let state = editorReducer(start(), {
			crop: {height: 300, width: 400, x: 100, y: 200},
			type: 'set-crop',
		});

		state = editorReducer(state, {type: 'flip-horizontal'});

		expect(state.present.crop).toEqual({
			height: 300,
			width: 400,
			x: 500,
			y: 200,
		});

		state = editorReducer(state, {type: 'rotate-90'});

		expect(state.present.crop).toEqual({
			height: 1000,
			width: 600,
			x: 0,
			y: 0,
		});
	});
});
