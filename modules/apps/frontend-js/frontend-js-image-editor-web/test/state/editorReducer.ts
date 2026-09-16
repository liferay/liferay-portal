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

	it('commits an adjustment and skips a value that does not change', () => {
		let state = editorReducer(history(), {
			key: 'brightness',
			type: 'set-adjustment',
			value: 40,
		});

		expect(state.present.adjustments.brightness).toBe(40);
		expect(undoLabel(state)).toBe('adjustments');

		const unchanged = editorReducer(state, {
			key: 'brightness',
			type: 'set-adjustment',
			value: 40,
		});

		expect(unchanged).toBe(state);

		state = editorReducer(state, {type: 'undo'});

		expect(state.present.adjustments.brightness).toBe(0);
	});

	it('resets every adjustment at once', () => {
		let state = editorReducer(history(), {
			key: 'brightness',
			type: 'set-adjustment',
			value: 40,
		});

		state = editorReducer(state, {
			key: 'shadows',
			type: 'set-adjustment',
			value: -20,
		});

		state = editorReducer(state, {type: 'reset-adjustments'});

		expect(state.present.adjustments.brightness).toBe(0);
		expect(state.present.adjustments.shadows).toBe(0);

		state = editorReducer(state, {type: 'undo'});

		expect(state.present.adjustments.brightness).toBe(40);
		expect(state.present.adjustments.shadows).toBe(-20);
	});

	it('applies a filter preset as one undoable step', () => {
		let state = editorReducer(history(), {
			filter: 'sepia',
			type: 'set-filter',
		});

		expect(state.present.filter).toBe('sepia');
		expect(undoLabel(state)).toBe('filter');

		state = editorReducer(state, {type: 'undo'});

		expect(state.present.filter).toBe('none');
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

describe('set-ratio', () => {
	const start = () => initialHistory(1000, 600);

	it('starts on the original ratio', () => {
		expect(start().present.ratio).toBe('original');
	});

	it('centers the largest crop of the chosen ratio', () => {
		const next = editorReducer(start(), {ratio: '1:1', type: 'set-ratio'});

		expect(next.present.ratio).toBe('1:1');
		expect(next.present.crop).toEqual({
			height: 600,
			width: 600,
			x: 200,
			y: 0,
		});
		expect(undoLabel(next)).toBe('ratio');
	});

	it('restores the full image on original and keeps the crop on custom', () => {
		let state = editorReducer(start(), {ratio: '16:9', type: 'set-ratio'});

		const framed = state.present.crop;

		state = editorReducer(state, {ratio: 'custom', type: 'set-ratio'});

		expect(state.present.crop).toEqual(framed);

		state = editorReducer(state, {ratio: 'original', type: 'set-ratio'});

		expect(state.present.crop).toEqual({
			height: 600,
			width: 1000,
			x: 0,
			y: 0,
		});
	});

	it('falls back to custom when the crop is edited and to original on rotation', () => {
		let state = editorReducer(start(), {ratio: '1:1', type: 'set-ratio'});

		state = editorReducer(state, {
			crop: {...state.present.crop, width: 500},
			type: 'set-crop',
		});

		expect(state.present.ratio).toBe('custom');

		state = editorReducer(state, {type: 'rotate-90'});

		expect(state.present.ratio).toBe('original');
	});
});

describe('set-angle', () => {
	it('collapses a slider gesture into one undoable step', () => {
		let state = initialHistory(WIDTH, HEIGHT);

		for (const angle of [2, 4, 6]) {
			state = editorReducer(state, {
				angle,
				transient: true,
				type: 'set-angle',
			});
		}

		state = editorReducer(state, {angle: 6, type: 'set-angle'});

		expect(state.present.angle).toBe(6);
		expect(state.past).toHaveLength(1);
		expect(undoLabel(state)).toBe('straighten');

		state = editorReducer(state, {type: 'undo'});

		expect(state.present.angle).toBe(0);
	});

	it('ignores an angle that changes nothing', () => {
		const initial = initialHistory(WIDTH, HEIGHT);

		expect(editorReducer(initial, {angle: 0, type: 'set-angle'})).toBe(
			initial
		);
	});
});

describe('set-frame', () => {
	it('merges what changed and leaves the rest of the frame alone', () => {
		const framed = editorReducer(history(), {
			frame: {kind: 'mat'},
			type: 'set-frame',
		});

		const sized = editorReducer(framed, {
			frame: {size: 10},
			type: 'set-frame',
		});

		expect(sized.present.frame).toEqual({
			color: '#ffffff',
			kind: 'mat',
			offset: 0,
			overAnnotations: true,
			size: 10,
		});
	});

	it('ignores a change that changes nothing', () => {
		const framed = editorReducer(history(), {
			frame: {kind: 'mat'},
			type: 'set-frame',
		});

		expect(
			editorReducer(framed, {frame: {kind: 'mat'}, type: 'set-frame'})
		).toBe(framed);
	});

	it('is undoable, and a slider drag is a single step', () => {
		let state = editorReducer(history(), {
			frame: {kind: 'mat'},
			type: 'set-frame',
		});

		for (const size of [5, 6, 7, 8]) {
			state = editorReducer(state, {
				frame: {size},
				transient: true,
				type: 'set-frame',
			});
		}

		state = editorReducer(state, {frame: {size: 8}, type: 'set-frame'});

		expect(state.present.frame.size).toBe(8);
		expect(undoLabel(state)).toBe('frame');

		const undone = editorReducer(state, {type: 'undo'});

		expect(undone.present.frame.size).toBe(4);
		expect(undone.present.frame.kind).toBe('mat');
	});

	it('survives a crop, because it is intent rather than geometry', () => {
		const framed = editorReducer(history(), {
			frame: {kind: 'polaroid'},
			type: 'set-frame',
		});

		const cropped = editorReducer(framed, {
			crop: {height: 400, width: 400, x: 100, y: 100},
			type: 'set-crop',
		});

		expect(cropped.present.frame.kind).toBe('polaroid');
	});
});

describe('initialHistory with allowed ratios', () => {
	it('starts framed on the only allowed preset', () => {
		const {present} = initialHistory(1000, 600, {ratios: ['1:1']});

		expect(present.ratio).toBe('1:1');
		expect(present.crop).toEqual({height: 600, width: 600, x: 200, y: 0});
	});

	it('prefers original, then custom, over a preset', () => {
		expect(
			initialHistory(1000, 600, {ratios: ['1:1', 'original']}).present
				.ratio
		).toBe('original');
		expect(
			initialHistory(1000, 600, {ratios: ['1:1', 'custom']}).present.ratio
		).toBe('custom');
	});
});

describe('overlays', () => {
	const caption = {
		color: '#ffffff',
		fontFamily: 'sans-serif',
		fontSize: 48,
		id: 'text-1',
		kind: 'text' as const,
		text: 'Hello',
		x: 100,
		y: 100,
	};

	it('adds, updates and removes an annotation, one step each', () => {
		let state = editorReducer(history(), {
			overlay: caption,
			type: 'add-overlay',
		});

		expect(state.present.overlays).toEqual([caption]);
		expect(undoLabel(state)).toBe('annotation');

		state = editorReducer(state, {
			id: 'text-1',
			patch: {x: 200},
			type: 'update-overlay',
		});

		expect(state.present.overlays[0]).toMatchObject({id: 'text-1', x: 200});

		state = editorReducer(state, {id: 'text-1', type: 'remove-overlay'});

		expect(state.present.overlays).toHaveLength(0);
		expect(state.past).toHaveLength(3);
	});

	it('ignores an update that changes nothing or names no annotation', () => {
		const state = editorReducer(history(), {
			overlay: caption,
			type: 'add-overlay',
		});

		expect(
			editorReducer(state, {
				id: 'text-1',
				patch: {x: 100},
				type: 'update-overlay',
			})
		).toBe(state);

		expect(
			editorReducer(state, {
				id: 'text-9',
				patch: {x: 300},
				type: 'update-overlay',
			})
		).toBe(state);
	});

	it('collapses a drag into a single undo step', () => {
		let state = editorReducer(history(), {
			overlay: caption,
			type: 'add-overlay',
		});

		state = editorReducer(state, {
			id: 'text-1',
			patch: {x: 120},
			transient: true,
			type: 'update-overlay',
		});
		state = editorReducer(state, {
			id: 'text-1',
			patch: {x: 140},
			transient: true,
			type: 'update-overlay',
		});
		state = editorReducer(state, {
			id: 'text-1',
			patch: {x: 140},
			type: 'update-overlay',
		});

		expect(state.present.overlays[0]).toMatchObject({x: 140});
		expect(state.past).toHaveLength(2);

		state = editorReducer(state, {type: 'undo'});

		expect(state.present.overlays[0]).toMatchObject({x: 100});
	});

	it('mirrors the annotations with the photograph', () => {
		let state = editorReducer(history(), {
			overlay: caption,
			type: 'add-overlay',
		});

		state = editorReducer(state, {type: 'flip-horizontal'});

		expect(state.present.overlays[0]).toMatchObject({
			x: WIDTH - 100 - 5 * 48 * 0.6,
			y: 100,
		});

		state = editorReducer(state, {type: 'flip-horizontal'});

		expect(state.present.overlays[0]).toMatchObject({x: 100, y: 100});
	});

	it('turns the annotations with the photograph', () => {
		let state = editorReducer(history(), {
			overlay: caption,
			type: 'add-overlay',
		});

		state = editorReducer(state, {type: 'rotate-90'});

		const turned = state.present.overlays[0] as typeof caption & {
			rotation?: number;
		};

		expect(turned.rotation).toBe(90);

		// The caption's center follows the pixel it was written over.

		const bounds = rotatedSize(state.present);

		expect(bounds).toEqual({height: WIDTH, width: HEIGHT});
		expect(turned.x + (5 * 48 * 0.6) / 2).toBeCloseTo(HEIGHT - 80.8, 0);
		expect(turned.y - 0.4 * 48).toBeCloseTo(172, 0);

		for (let turn = 0; turn < 3; turn++) {
			state = editorReducer(state, {type: 'rotate-90'});
		}

		expect(state.present.overlays[0]).toMatchObject({
			fontSize: 48,
			rotation: undefined,
			x: 100,
			y: 100,
		});
	});
});

describe('layers', () => {
	const caption = {
		color: '#ffffff',
		fontFamily: 'sans-serif',
		fontSize: 48,
		id: 'text-1',
		kind: 'text' as const,
		text: 'Hello',
		x: 100,
		y: 100,
	};

	const two = () => {
		const state = editorReducer(history(), {
			overlay: caption,
			type: 'add-overlay',
		});

		return editorReducer(state, {
			overlay: {...caption, id: 'text-2', text: 'World'},
			type: 'add-overlay',
		});
	};

	it('duplicates a layer right above the original with an offset', () => {
		const state = editorReducer(two(), {
			id: 'text-1',
			newId: 'text-1-copy',
			type: 'duplicate-overlay',
		});

		expect(state.present.overlays.map((item) => item.id)).toEqual([
			'text-1',
			'text-1-copy',
			'text-2',
		]);

		expect(state.present.overlays[1]).toMatchObject({x: 120, y: 120});
		expect(undoLabel(state)).toBe('annotation');
	});

	it('reorders a layer and stops at either end', () => {
		const state = editorReducer(two(), {
			direction: 1,
			id: 'text-1',
			type: 'move-overlay-layer',
		});

		expect(state.present.overlays.map((item) => item.id)).toEqual([
			'text-2',
			'text-1',
		]);
		expect(undoLabel(state)).toBe('layer-order');

		expect(
			editorReducer(state, {
				direction: 1,
				id: 'text-1',
				type: 'move-overlay-layer',
			})
		).toBe(state);

		expect(
			editorReducer(state, {
				direction: -1,
				id: 'text-9',
				type: 'move-overlay-layer',
			})
		).toBe(state);
	});
});

describe('groups', () => {
	const caption = {
		color: '#ffffff',
		fontFamily: 'sans-serif',
		fontSize: 48,
		id: 'text-1',
		kind: 'text' as const,
		text: 'Hello',
		x: 100,
		y: 100,
	};

	const three = () => {
		let state = history();

		for (const id of ['text-1', 'text-2', 'text-3']) {
			state = editorReducer(state, {
				overlay: {...caption, id},
				type: 'add-overlay',
			});
		}

		return state;
	};

	it('moves the named annotations together and leaves the rest', () => {
		const state = editorReducer(three(), {
			dx: 10,
			dy: -5,
			ids: ['text-1', 'text-3'],
			type: 'move-overlays',
		});

		expect(state.present.overlays.map(({x, y}) => [x, y])).toEqual([
			[110, 95],
			[100, 100],
			[110, 95],
		]);
		expect(undoLabel(state)).toBe('annotation');
	});

	it('collapses a group drag into one step and ignores an empty move', () => {
		let state = editorReducer(three(), {
			dx: 5,
			dy: 0,
			ids: ['text-1', 'text-2'],
			transient: true,
			type: 'move-overlays',
		});

		state = editorReducer(state, {
			dx: 5,
			dy: 0,
			ids: ['text-1', 'text-2'],
			transient: true,
			type: 'move-overlays',
		});

		state = editorReducer(state, {
			dx: 0,
			dy: 0,
			ids: ['text-1', 'text-2'],
			type: 'move-overlays',
		});

		expect(state.present.overlays[0]).toMatchObject({x: 110});
		expect(state.past).toHaveLength(4);

		expect(
			editorReducer(state, {
				dx: 0,
				dy: 0,
				ids: ['text-1'],
				type: 'move-overlays',
			})
		).toBe(state);

		expect(
			editorReducer(state, {dx: 9, dy: 9, ids: [], type: 'move-overlays'})
		).toBe(state);
	});

	it('removes the named annotations as one step', () => {
		const state = editorReducer(three(), {
			ids: ['text-1', 'text-3'],
			type: 'remove-overlays',
		});

		expect(state.present.overlays.map((item) => item.id)).toEqual([
			'text-2',
		]);

		expect(editorReducer(state, {ids: [], type: 'remove-overlays'})).toBe(
			state
		);
	});
});

describe('the frame placement', () => {
	it('moves under the annotations when asked, and no more than that', () => {
		let state = editorReducer(history(), {
			frame: {kind: 'mat'},
			type: 'set-frame',
		});

		expect(state.present.frame.overAnnotations).toBe(true);

		state = editorReducer(state, {
			frame: {overAnnotations: false},
			type: 'set-frame',
		});

		expect(state.present.frame).toMatchObject({
			kind: 'mat',
			overAnnotations: false,
		});

		expect(
			editorReducer(state, {
				frame: {overAnnotations: false},
				type: 'set-frame',
			})
		).toBe(state);
	});
});
