/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {constrainHeightToViewport} from '../constrainHeightToViewport';

import type {AlignPoints} from '../useOverlayPosition';

const ABOVE: AlignPoints = ['bl', 'tl'];
const BELOW: AlignPoints = ['tl', 'bl'];

describe('constrainHeightToViewport', () => {
	let source: HTMLElement;
	let sourceHeight: number;
	let sourceScrollTop: number;
	let target: HTMLElement;

	function getOffset(points: AlignPoints): [number, number] {
		return points[0][0] === 't' ? [0, 4] : [0, -4];
	}

	function placeTarget(top: number) {
		jest.spyOn(target, 'getBoundingClientRect').mockReturnValue({
			bottom: top + 30,
			top,
		} as DOMRect);
	}

	beforeEach(() => {
		source = document.createElement('div');
		target = document.createElement('div');

		sourceHeight = 0;
		sourceScrollTop = 0;

		jest.spyOn(
			document.documentElement,
			'clientHeight',
			'get'
		).mockReturnValue(600);

		jest.spyOn(source, 'getBoundingClientRect').mockImplementation(() => {
			if (!source.style.maxHeight) {
				sourceScrollTop = 0;
			}

			return {height: sourceHeight} as DOMRect;
		});

		Object.defineProperty(source, 'scrollTop', {
			configurable: true,
			get: () => sourceScrollTop,
			set: (value: number) => {
				sourceScrollTop = value;
			},
		});
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('keeps the alignment and the height when the source fits', () => {
		placeTarget(300);

		sourceHeight = 200;

		const points = constrainHeightToViewport({
			getOffset,
			points: BELOW,
			sourceElement: source,
			targetElement: target,
		});

		expect(points).toEqual(BELOW);
		expect(source.style.maxHeight).toBe('');
	});

	it('constrains the height and keeps the source below when there is more room below', () => {
		placeTarget(200);

		sourceHeight = 500;

		const points = constrainHeightToViewport({
			getOffset,
			points: BELOW,
			sourceElement: source,
			targetElement: target,
		});

		expect(points).toEqual(BELOW);
		expect(source.style.maxHeight).toBe('366px');
	});

	it('constrains the height and moves the source above when there is more room above', () => {
		placeTarget(400);

		sourceHeight = 500;

		const points = constrainHeightToViewport({
			getOffset,
			points: BELOW,
			sourceElement: source,
			targetElement: target,
		});

		expect(points).toEqual(ABOVE);
		expect(source.style.maxHeight).toBe('396px');
	});

	it('ignores alignments that are not vertical', () => {
		placeTarget(300);

		sourceHeight = 500;

		const points: AlignPoints = ['tl', 'tr'];

		expect(
			constrainHeightToViewport({
				getOffset,
				points,
				sourceElement: source,
				targetElement: target,
			})
		).toEqual(points);
		expect(source.style.maxHeight).toBe('');
	});

	it('restores the max height set by the caller when the source fits again', () => {
		placeTarget(200);

		source.style.maxHeight = '550px';
		sourceHeight = 500;

		constrainHeightToViewport({
			getOffset,
			points: BELOW,
			sourceElement: source,
			targetElement: target,
		});

		expect(source.style.maxHeight).toBe('366px');

		sourceHeight = 300;

		constrainHeightToViewport({
			getOffset,
			points: BELOW,
			sourceElement: source,
			targetElement: target,
		});

		expect(source.style.maxHeight).toBe('550px');
	});

	it('keeps the scroll position of a constrained source across alignment passes', () => {
		placeTarget(200);

		sourceHeight = 500;

		constrainHeightToViewport({
			getOffset,
			points: BELOW,
			sourceElement: source,
			targetElement: target,
		});

		source.scrollTop = 120;

		constrainHeightToViewport({
			getOffset,
			points: BELOW,
			sourceElement: source,
			targetElement: target,
		});

		expect(source.scrollTop).toBe(120);
	});
});
