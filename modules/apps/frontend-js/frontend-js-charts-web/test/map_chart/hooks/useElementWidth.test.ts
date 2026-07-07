/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';
import {RefObject} from 'react';

import {useElementWidth} from '../../../src/main/resources/META-INF/resources/js/map_chart/hooks/useElementWidth';

type ResizeObserverCallback = (
	entries: {contentRect: {width: number}}[]
) => void;

function createRef(width: number): RefObject<Element> {
	return {
		current: {
			getBoundingClientRect: () => ({width}),
		} as unknown as Element,
	};
}

describe('useElementWidth', () => {
	const {ResizeObserver: originalResizeObserver} = window;

	let observedCallback: ResizeObserverCallback | undefined;
	let disconnect: jest.Mock;
	let observe: jest.Mock;

	beforeEach(() => {
		disconnect = jest.fn();
		observe = jest.fn();

		window.ResizeObserver = jest
			.fn()
			.mockImplementation((callback: ResizeObserverCallback) => {
				observedCallback = callback;

				return {disconnect, observe};
			}) as unknown as typeof ResizeObserver;
	});

	afterEach(() => {
		window.ResizeObserver = originalResizeObserver;
		observedCallback = undefined;
	});

	it('reports the initial rendered width', () => {
		const ref = createRef(120);

		const {result} = renderHook(() => useElementWidth(ref));

		expect(result.current).toBe(120);
	});

	it('updates the width when the observed element resizes', () => {
		const ref = createRef(120);

		const {result} = renderHook(() => useElementWidth(ref));

		act(() => {
			observedCallback?.([{contentRect: {width: 240}}]);
		});

		expect(result.current).toBe(240);
	});

	it('disconnects the observer on unmount', () => {
		const ref = createRef(120);

		const {unmount} = renderHook(() => useElementWidth(ref));

		unmount();

		expect(disconnect).toHaveBeenCalled();
	});

	it('returns undefined when the ref has no current element', () => {
		const emptyRef: RefObject<Element> = {current: null};

		const {result} = renderHook(() => useElementWidth(emptyRef));

		expect(result.current).toBeUndefined();
		expect(observe).not.toHaveBeenCalled();
	});
});
