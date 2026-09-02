/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import domAlign from 'dom-align';

import {doAlign} from '../doAlign';

jest.mock('dom-align', () => jest.fn());

const domAlignMock = domAlign as unknown as jest.Mock;

describe('doAlign', () => {
	let getComputedStyleSpy: jest.SpyInstance;
	let source: HTMLElement;
	let target: HTMLElement;

	function mockDirection(direction: 'ltr' | 'rtl') {
		getComputedStyleSpy = jest
			.spyOn(window, 'getComputedStyle')
			.mockReturnValue({direction} as CSSStyleDeclaration);
	}

	function recordStylesAtCall() {
		const styles: Record<string, string> = {};

		domAlignMock.mockImplementation(
			(srcElement: HTMLElement, _target, config) => {
				styles.bottom = srcElement.style.bottom;
				styles.left = srcElement.style.left;
				styles.right = srcElement.style.right;
				styles.top = srcElement.style.top;

				return config;
			}
		);

		return styles;
	}

	beforeEach(() => {
		target = document.createElement('div');
		source = document.createElement('div');

		source.style.bottom = '40px';
		source.style.left = '20px';
		source.style.right = '30px';
		source.style.top = '10px';

		document.body.appendChild(target);
		document.body.appendChild(source);
	});

	afterEach(() => {
		domAlignMock.mockReset();

		getComputedStyleSpy?.mockRestore();

		document.body.removeChild(source);
		document.body.removeChild(target);
	});

	it('clears the horizontal offset dom-align will not write and preserves the one it will', () => {
		mockDirection('ltr');

		const styles = recordStylesAtCall();

		doAlign({
			points: ['tl', 'bl'],
			sourceElement: source,
			targetElement: target,
		});

		expect(styles.right).toBe('');
		expect(styles.left).toBe('20px');
	});

	it('clears the opposite horizontal offset when the source reads right to left', () => {
		mockDirection('rtl');

		const styles = recordStylesAtCall();

		doAlign({
			points: ['tl', 'bl'],
			sourceElement: source,
			targetElement: target,
		});

		expect(styles.left).toBe('');
		expect(styles.right).toBe('30px');
	});

	it('always clears bottom and always preserves top', () => {
		for (const direction of ['ltr', 'rtl'] as const) {
			getComputedStyleSpy?.mockRestore();

			mockDirection(direction);

			source.style.bottom = '40px';
			source.style.top = '10px';

			const styles = recordStylesAtCall();

			doAlign({
				points: ['tl', 'bl'],
				sourceElement: source,
				targetElement: target,
			});

			expect(styles.bottom).toBe('');
			expect(styles.top).toBe('10px');
		}
	});

	it('passes the resolved direction to dom-align', () => {
		mockDirection('rtl');

		domAlignMock.mockImplementation((_source, _target, config) => config);

		doAlign({
			points: ['tl', 'bl'],
			sourceElement: source,
			targetElement: target,
		});

		expect(domAlignMock.mock.calls[0][2]).toEqual(
			expect.objectContaining({points: ['tl', 'bl'], useCssRight: true})
		);
	});

	it('measures the source where the previous pass placed it', () => {

		// A shrink to fit source is only as wide as the space left of the
		// viewport edge, so blanking the offset that positions it makes
		// dom-align measure a width the source will not have once placed.

		Object.defineProperty(source, 'offsetWidth', {
			get: () => (source.style.left === '' ? 200 : 120),
		});

		mockDirection('ltr');

		let widthAtCall;

		domAlignMock.mockImplementation(
			(srcElement: HTMLElement, _target, config) => {
				widthAtCall = srcElement.offsetWidth;

				return config;
			}
		);

		doAlign({
			points: ['bc', 'tc'],
			sourceElement: source,
			targetElement: target,
		});

		expect(widthAtCall).toBe(120);
	});
});
