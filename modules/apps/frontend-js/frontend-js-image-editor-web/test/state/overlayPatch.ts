/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	patchFor,
	patchOverlay,
} from '../../src/main/resources/META-INF/resources/js/state/overlayPatch';
import {
	RedactOverlay,
	ShapeOverlay,
	StrokeOverlay,
	TextOverlay,
} from '../../src/main/resources/META-INF/resources/js/state/types';

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

describe('patchOverlay', () => {
	it('applies keys the kind owns', () => {
		expect(patchOverlay(TEXT, {text: 'World', x: 50})).toMatchObject({
			text: 'World',
			x: 50,
			y: 500,
		});
	});

	it('drops keys the kind does not own', () => {
		expect(patchOverlay(TEXT, {width: 300} as never)).toBe(TEXT);
	});

	it('never lets a non-finite number into the state', () => {
		const next = patchOverlay(TEXT, {x: Number.NaN, y: 300});

		expect(next.x).toBe(400);
		expect(next.y).toBe(300);

		expect(patchOverlay(TEXT, {fontSize: Infinity})).toBe(TEXT);
	});

	it('clamps the domains: opacity to its range, sizes to one', () => {
		expect(patchOverlay(TEXT, {opacity: 250})).toMatchObject({
			opacity: 100,
		});
		expect(patchOverlay(TEXT, {opacity: -3})).toMatchObject({opacity: 0});
		expect(patchOverlay(TEXT, {fontSize: 0})).toMatchObject({fontSize: 1});
	});

	it('rejects a value of the wrong type', () => {
		expect(patchOverlay(TEXT, {rotation: '45' as never})).toBe(TEXT);
		expect(patchOverlay(TEXT, {text: 12 as never})).toBe(TEXT);
	});

	it('types the patch against the kind at narrowed call sites', () => {
		const typed = patchFor(TEXT);

		expect(typed({fontSize: 80})).toEqual({fontSize: 80});

		// @ts-expect-error

		typed({width: 300});
	});

	it('returns the same reference when nothing changes', () => {
		expect(patchOverlay(TEXT, {text: 'Hello', x: 400})).toBe(TEXT);
		expect(patchOverlay(TEXT, {})).toBe(TEXT);
	});
});

const SHAPE: ShapeOverlay = {
	color: '#0b5fff',
	height: 100,
	id: 'shape-1',
	kind: 'shape',
	width: 300,
	x: 100,
	y: 200,
};

describe('patchOverlay on shapes and arrows', () => {
	it('drops keys another kind owns', () => {
		expect(patchOverlay(SHAPE, {text: 'smuggled'} as never)).toBe(SHAPE);
	});

	it('rejects values outside an enum', () => {
		const arrow = patchOverlay(
			{
				color: '#000000',
				dx: 10,
				dy: 10,
				head: 'filled',
				id: 'arrow-1',
				kind: 'arrow',
				thickness: 4,
				x: 0,
				y: 0,
			},
			{head: 'banana' as never}
		);

		expect(arrow).toMatchObject({head: 'filled'});
	});

	it('clears an optional key on an explicit undefined', () => {
		const seeded = patchOverlay(SHAPE, {sketchSeed: 42}) as ShapeOverlay;

		expect(seeded.sketchSeed).toBe(42);

		const cleaned = patchOverlay(seeded, {
			sketchSeed: undefined,
		}) as ShapeOverlay;

		expect(cleaned.sketchSeed).toBeUndefined();

		expect(patchOverlay(SHAPE, {x: undefined})).toBe(SHAPE);
	});

	it('keeps the sizes at one and the border unsigned', () => {
		expect(patchOverlay(SHAPE, {height: -20})).toMatchObject({height: 1});
		expect(patchOverlay(SHAPE, {borderWidth: -4})).toMatchObject({
			borderWidth: 0,
		});
	});
});

const STROKE: StrokeOverlay = {
	color: '#0b5fff',
	id: 'stroke-1',
	kind: 'stroke',
	points: [0, 0, 50, 50],
	smooth: true,
	width: 4,
	x: 10,
	y: 20,
};

describe('patchOverlay on strokes', () => {
	it('switches the line style with a boolean and nothing else', () => {
		expect(patchOverlay(STROKE, {smooth: false})).toMatchObject({
			smooth: false,
		});
		expect(patchOverlay(STROKE, {smooth: 'no' as never})).toBe(STROKE);
	});

	it('takes only whole, finite point lists', () => {
		expect(patchOverlay(STROKE, {points: [1, 2, 3]})).toBe(STROKE);
		expect(patchOverlay(STROKE, {points: [1, NaN]})).toBe(STROKE);
		expect(patchOverlay(STROKE, {points: [1, 2, 3, 4]})).toMatchObject({
			points: [1, 2, 3, 4],
		});
	});

	it('keeps the width at one', () => {
		expect(patchOverlay(STROKE, {width: 0})).toMatchObject({width: 1});
	});
});

const REDACT: RedactOverlay = {
	height: 80,
	id: 'redact-1',
	kind: 'redact',
	level: 'fine',
	width: 120,
	x: 0,
	y: 0,
};

describe('patchOverlay on redactions', () => {
	it('owns no color, and keeps its level and style to the known values', () => {
		expect(patchOverlay(REDACT, {color: '#000000'} as never)).toBe(REDACT);
		expect(patchOverlay(REDACT, {level: 'huge' as never})).toBe(REDACT);
		expect(patchOverlay(REDACT, {style: 'smear' as never})).toBe(REDACT);
		expect(
			patchOverlay(REDACT, {level: 'coarse', style: 'blur'})
		).toMatchObject({level: 'coarse', style: 'blur'});
	});
});
