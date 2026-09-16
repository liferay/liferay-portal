/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	patchFor,
	patchOverlay,
} from '../../src/main/resources/META-INF/resources/js/state/overlayPatch';
import {TextOverlay} from '../../src/main/resources/META-INF/resources/js/state/types';

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

		// @ts-expect-error a caption has no `width`; the compiler is the
		// guard here, the runtime filter is the second line of defence.

		typed({width: 300});
	});

	it('returns the same reference when nothing changes', () => {
		expect(patchOverlay(TEXT, {text: 'Hello', x: 400})).toBe(TEXT);
		expect(patchOverlay(TEXT, {})).toBe(TEXT);
	});
});
