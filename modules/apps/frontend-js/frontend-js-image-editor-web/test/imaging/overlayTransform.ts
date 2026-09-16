/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	editorReducer,
	initialHistory,
} from '../../src/main/resources/META-INF/resources/js/state/editorReducer';
import {
	ArrowOverlay,
	CircleOverlay,
	Overlay,
	ShapeOverlay,
	TextOverlay,
} from '../../src/main/resources/META-INF/resources/js/state/types';

const RECT: ShapeOverlay = {
	color: '#0b5fff',
	height: 100,
	id: 'shape-1',
	kind: 'shape',
	width: 300,
	x: 100,
	y: 200,
};

const CIRCLE: CircleOverlay = {
	color: '#0b5fff',
	height: 160,
	id: 'circle-1',
	kind: 'circle',
	rotation: 30,
	width: 360,
	x: 260,
	y: 700,
};

const TEXT: TextOverlay = {
	color: '#ffffff',
	fontFamily: 'sans-serif',
	fontSize: 50,
	id: 'text-1',
	kind: 'text',
	text: 'Hello',
	x: 400,
	y: 500,
};

const ARROW: ArrowOverlay = {
	color: '#0b5fff',
	dx: 200,
	dy: -100,
	head: 'filled',
	id: 'arrow-1',
	kind: 'arrow',
	thickness: 6,
	x: 300,
	y: 400,
};

const ALL: Overlay[] = [RECT, CIRCLE, TEXT, ARROW];

function withOverlays(overlays: Overlay[]) {
	let history = initialHistory(1600, 1000);

	for (const overlay of overlays) {
		history = editorReducer(history, {overlay, type: 'add-overlay'});
	}

	return history;
}

function rotate(history: ReturnType<typeof initialHistory>, times: number) {
	let next = history;

	for (let turn = 0; turn < times; turn++) {
		next = editorReducer(next, {type: 'rotate-90'});
	}

	return next;
}

describe('rotate-90 carries the annotations', () => {
	it('maps a rectangle exactly, folding the turn into its box', () => {
		const rotated = rotate(withOverlays([RECT]), 1).present
			.overlays[0] as ShapeOverlay;

		expect(rotated).toMatchObject({
			height: 300,
			width: 100,
			x: 700,
			y: 100,
		});

		expect(rotated.rotation).toBeUndefined();
	});

	it('keeps a rotated circle at its angle plus the turn', () => {
		const rotated = rotate(withOverlays([CIRCLE]), 1).present
			.overlays[0] as CircleOverlay;

		expect(rotated).toMatchObject({
			height: 360,
			rotation: 30,
			width: 160,
		});
	});

	it('rotates an arrow by its vector', () => {
		const rotated = rotate(withOverlays([ARROW]), 1).present
			.overlays[0] as ArrowOverlay;

		expect(rotated).toMatchObject({dx: 100, dy: 200, x: 600, y: 300});
	});

	it('returns every kind to itself after four turns', () => {
		const start = withOverlays(ALL).present.overlays;
		const full = rotate(withOverlays(ALL), 4).present.overlays;

		for (let index = 0; index < start.length; index++) {
			const before = start[index] as unknown as Record<string, unknown>;
			const after = full[index] as unknown as Record<string, unknown>;

			for (const key of Object.keys(before)) {
				const original = before[key];
				const returned = after[key];

				if (typeof original === 'number') {
					expect(returned as number).toBeCloseTo(original, 1);
				}
				else {
					expect(returned).toEqual(original);
				}
			}
		}
	});

	it('stays exact with the picture flipped', () => {
		let history = withOverlays([RECT]);

		history = editorReducer(history, {type: 'flip-horizontal'});

		const flipped = history.present.overlays[0] as ShapeOverlay;

		const rotated = rotate(history, 4).present.overlays[0] as ShapeOverlay;

		expect(rotated.x).toBeCloseTo(flipped.x, 1);
		expect(rotated.y).toBeCloseTo(flipped.y, 1);
		expect(rotated.width).toBeCloseTo(flipped.width, 1);
		expect(rotated.height).toBeCloseTo(flipped.height, 1);
	});
});
