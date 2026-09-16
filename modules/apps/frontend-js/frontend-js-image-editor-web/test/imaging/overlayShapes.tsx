/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import React from 'react';
import {renderToStaticMarkup} from 'react-dom/server';

import {
	OverlayShape,
	arrowGeometry,
	mirrorOverlay,
	overlayBounds,
	overlayHitBox,
	overlayLabel,
	overlayRotation,
	overlayTransform,
} from '../../src/main/resources/META-INF/resources/js/imaging/overlayShapes';
import {
	ArrowOverlay,
	EmojiOverlay,
	Overlay,
	RedactOverlay,
	ShapeOverlay,
	StrokeOverlay,
	TextOverlay,
	isBoxOverlay,
} from '../../src/main/resources/META-INF/resources/js/state/types';

const markup = (overlay: Overlay) =>
	renderToStaticMarkup(<OverlayShape overlay={overlay} />);

const CAPTION: TextOverlay = {
	color: '#ffffff',
	fontFamily: 'sans-serif',
	fontSize: 50,
	id: 'text-1',
	kind: 'text',
	text: 'Hello',
	x: 400,
	y: 500,
};

describe('a text annotation', () => {
	it('sits on its baseline, so its box rises above the anchor', () => {
		expect(overlayBounds(CAPTION)).toEqual({
			height: 60,
			width: 150,
			x: 400,
			y: 450,
		});
	});

	it('is named by what it says', () => {
		expect(overlayLabel(CAPTION)).toBe('text-x');
	});

	it('turns about its own center', () => {
		expect(overlayTransform(CAPTION)).toBeUndefined();

		expect(overlayTransform({...CAPTION, rotation: 45})).toBe(
			'rotate(45 475 480)'
		);
	});

	it('keeps its place when the photograph mirrors', () => {
		expect(mirrorOverlay(CAPTION, 1000)).toMatchObject({x: 450});

		expect(mirrorOverlay({...CAPTION, rotation: 30}, 1000)).toMatchObject({
			rotation: -30,
		});
	});

	it('keeps a full size target when the caption is tiny', () => {
		const footnote = {...CAPTION, fontSize: 10, text: 'a'};

		expect(overlayHitBox(footnote, 24)).toEqual({
			height: 24,
			width: 24,
			x: 393,
			y: 484,
		});
	});
});

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

describe('an arrow', () => {
	it('is not a box, so it is placed by its ends rather than stretched', () => {
		expect(isBoxOverlay(ARROW)).toBe(false);

		expect(overlayBounds(ARROW)).toEqual({
			height: 106,
			width: 206,
			x: 297,
			y: 297,
		});
	});

	it('has no rotation of its own, because its ends already aim it', () => {
		expect(overlayRotation(ARROW)).toBe(0);
		expect(overlayTransform(ARROW)).toBeUndefined();
	});

	it('keeps pointing at what it pointed at when the photo mirrors', () => {
		const mirrored = mirrorOverlay(ARROW, 1000) as ArrowOverlay;

		expect(mirrored.x).toBe(700);
		expect(mirrored.dx).toBe(-200);
		expect(mirrored.x + mirrored.dx).toBe(500);

		expect(mirrored.dy).toBe(ARROW.dy);
	});

	it('sizes its head from the stroke, and its shaft stops at the head', () => {
		const geometry = arrowGeometry(ARROW);

		expect(geometry.tipX).toBe(500);
		expect(geometry.tipY).toBe(300);

		expect(geometry.headLength).toBeCloseTo(19.2, 5);

		expect(
			Math.hypot(
				geometry.tipX - geometry.shaftX,
				geometry.tipY - geometry.shaftY
			)
		).toBeCloseTo(geometry.headLength, 5);

		expect(arrowGeometry({...ARROW, thickness: 12}).headLength).toBeCloseTo(
			38.4,
			5
		);
	});

	it('will not let the head eat a short arrow', () => {
		const stub = arrowGeometry({...ARROW, dx: 30, dy: 0});

		expect(stub.headLength).toBeCloseTo(10, 5);
	});

	it('survives being given no length at all', () => {
		const degenerate = arrowGeometry({...ARROW, dx: 0, dy: 0});

		expect(degenerate.tipX).toBe(ARROW.x);
		expect(degenerate.headPoints).toBe('');
	});

	it('is named as an arrow', () => {
		expect(overlayLabel(ARROW)).toBe('arrow');
	});
});

const RECT: ShapeOverlay = {
	color: '#0b5fff',
	height: 100,
	id: 'shape-1',
	kind: 'shape',
	width: 300,
	x: 100,
	y: 200,
};

describe('a shape', () => {
	it('is a box, named by its outline', () => {
		expect(isBoxOverlay(RECT)).toBe(true);
		expect(overlayLabel(RECT)).toBe('rectangle');
		expect(overlayLabel({...RECT, id: 'circle-1', kind: 'circle'})).toBe(
			'circle'
		);

		expect(overlayBounds(RECT)).toEqual({
			height: 100,
			width: 300,
			x: 100,
			y: 200,
		});
	});

	it('mirrors by its far edge and turns the other way', () => {
		expect(mirrorOverlay({...RECT, rotation: 20}, 1000)).toMatchObject({
			rotation: -20,
			x: 600,
		});
	});

	it('draws no border until one is asked for', () => {
		expect(markup(RECT)).not.toContain('stroke=');

		expect(markup({...RECT, borderWidth: 4})).toContain(
			'stroke="#272833" stroke-width="4"'
		);

		expect(
			markup({...RECT, borderColor: '#ff0000', borderWidth: 4})
		).toContain('stroke="#ff0000"');
	});

	it('wobbles into a closed path in the hand-drawn style', () => {
		expect(markup(RECT)).toContain('<rect');

		const sketchy = markup({...RECT, sketchSeed: 42});

		expect(sketchy).not.toContain('<rect');
		expect(sketchy).toMatch(/<path d="M[^"]* Z"/);
		expect(markup({...RECT, sketchSeed: 42})).toBe(sketchy);

		expect(markup({...RECT, id: 'circle-1', kind: 'circle'})).toContain(
			'<ellipse'
		);
		expect(
			markup({...RECT, id: 'circle-1', kind: 'circle', sketchSeed: 1})
		).toMatch(/<path d="M[^"]* Z"/);
	});

	it('fades as a group, so the border fades with the fill', () => {
		expect(markup({...RECT, opacity: 50})).toMatch(/^<g opacity="0.5">/);
	});
});

const STROKE: StrokeOverlay = {
	color: '#0b5fff',
	id: 'stroke-1',
	kind: 'stroke',
	points: [10, 0, 210, -100],
	smooth: true,
	width: 6,
	x: 290,
	y: 400,
};

describe('a stroke', () => {
	it('is boxed by its points, grown by its own width', () => {
		expect(isBoxOverlay(STROKE)).toBe(false);

		expect(overlayBounds(STROKE)).toEqual({
			height: 106,
			width: 206,
			x: 297,
			y: 297,
		});
	});

	it('draws its points as one path from its origin', () => {
		const svg = markup(STROKE);

		expect(svg).toContain('d="M10 0 C43.33 -16.67 176.67 -83.33 210 -100"');
		expect(svg).toContain('transform="translate(290 400)"');
		expect(svg).toContain('stroke-width="6"');
	});

	it('is named as a stroke', () => {
		expect(overlayLabel(STROKE)).toBe('stroke');
	});

	it('keeps hugging what it was drawn around when the photo mirrors', () => {
		const mirrored = mirrorOverlay(STROKE, 1000) as StrokeOverlay;

		expect(mirrored.points).toEqual([210, 0, 10, -100]);
		expect(mirrored.x + mirrored.points[0]).toBe(700);
		expect(mirrored.x + mirrored.points[2]).toBe(500);
		expect(mirrored.y).toBe(STROKE.y);
	});
});

const REDACT: RedactOverlay = {
	height: 80,
	id: 'redact-1',
	kind: 'redact',
	level: 'fine',
	width: 120,
	x: 100,
	y: 200,
};

describe('a redaction', () => {
	it('is a box named by what it does', () => {
		expect(isBoxOverlay(REDACT)).toBe(true);
		expect(overlayBounds(REDACT)).toEqual({
			height: 80,
			width: 120,
			x: 100,
			y: 200,
		});
		expect(overlayLabel(REDACT)).toBe('redacted-area');
	});

	it('falls back to a solid block without a source to reveal', () => {
		expect(markup(REDACT)).toContain('<rect fill="#14151f"');
	});

	it('mirrors by its far edge, so it keeps hiding the same pixels', () => {
		expect(mirrorOverlay(REDACT, 1000)).toMatchObject({x: 780, y: 200});
	});
});

const EMOJI_OVERLAY: EmojiOverlay = {
	character: '🎉',
	id: 'emoji-1',
	kind: 'emoji',
	name: 'party popper',
	size: 120,
	x: 400,
	y: 300,
};

describe('an emoji annotation', () => {
	it('is a square centred on its point', () => {
		expect(isBoxOverlay(EMOJI_OVERLAY)).toBe(false);

		expect(overlayBounds(EMOJI_OVERLAY)).toEqual({
			height: 120,
			width: 120,
			x: 340,
			y: 240,
		});
	});

	it('is named by Unicode, not by us', () => {
		expect(overlayLabel(EMOJI_OVERLAY)).toBe('party popper');
	});

	it('mirrors by its point when the photograph flips', () => {
		expect(mirrorOverlay(EMOJI_OVERLAY, 1000)).toMatchObject({x: 600});
	});
});
