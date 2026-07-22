/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';
import {useId} from 'react';

import useFloatingPanel, {
	MAX_WIDTH,
	MIN_HEIGHT,
	MIN_WIDTH,
	STORAGE_KEY_PREFIX,
	applyDrag,
	applyResize,
	clamp,
	clampStateToViewport,
	getDefaultState,
	readStoredState,
	writeStoredState,
} from '../../../src/main/resources/META-INF/resources/ai-assistant-chat/hooks/useFloatingPanel';

import type {PanelState} from '../../../src/main/resources/META-INF/resources/ai-assistant-chat/hooks/useFloatingPanel';

const VIEWPORT_WIDTH = 1280;
const VIEWPORT_HEIGHT = 800;

describe('clamp', () => {
	it('returns the value when it is within bounds', () => {
		expect(clamp(0, 10, 5)).toBe(5);
	});

	it('returns the minimum when the value is below it', () => {
		expect(clamp(0, 10, -5)).toBe(0);
	});

	it('returns the maximum when the value is above it', () => {
		expect(clamp(0, 10, 15)).toBe(10);
	});

	it('returns the minimum when the maximum is smaller than the minimum', () => {
		expect(clamp(10, 5, 7)).toBe(10);
	});
});

describe('getDefaultState', () => {
	it('returns a state clamped within the configured bounds', () => {
		const state = getDefaultState(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

		expect(state.width).toBeGreaterThanOrEqual(MIN_WIDTH);
		expect(state.width).toBeLessThanOrEqual(MAX_WIDTH);
		expect(state.height).toBeGreaterThanOrEqual(MIN_HEIGHT);
		expect(state.x).toBeGreaterThanOrEqual(0);
		expect(state.y).toBeGreaterThanOrEqual(0);
	});
});

describe('clampStateToViewport', () => {
	it('clamps width and height to their configured bounds', () => {
		const state: PanelState = {height: 10000, width: 10000, x: 0, y: 0};

		const nextState = clampStateToViewport(
			state,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.width).toBe(MAX_WIDTH);
		expect(nextState.height).toBe(Math.floor(0.8 * VIEWPORT_HEIGHT));
	});

	it('pulls a stored state from a wider viewport back within the current viewport', () => {
		const state: PanelState = {height: 500, width: 400, x: 1800, y: 100};

		const nextState = clampStateToViewport(
			state,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.x + nextState.width).toBeLessThanOrEqual(
			VIEWPORT_WIDTH
		);
	});

	it('pulls a stored state from a taller viewport back within the current viewport', () => {
		const state: PanelState = {height: 500, width: 400, x: 100, y: 5000};

		const nextState = clampStateToViewport(
			state,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.y + nextState.height).toBeLessThanOrEqual(
			VIEWPORT_HEIGHT
		);
	});
});

describe('applyResize', () => {
	const state: PanelState = {height: 500, width: 400, x: 100, y: 100};

	it('clamps the width to the maximum on a large positive delta', () => {
		const nextState = applyResize(
			state,
			10000,
			0,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.width).toBe(MAX_WIDTH);
	});

	it('clamps the width to the minimum on a large negative delta', () => {
		const nextState = applyResize(
			state,
			-10000,
			0,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.width).toBe(MIN_WIDTH);
	});

	it('clamps the height to the viewport-derived maximum on a large positive delta', () => {
		const nextState = applyResize(
			state,
			0,
			10000,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.height).toBe(Math.floor(0.8 * VIEWPORT_HEIGHT));
	});

	it('clamps the height to the minimum on a large negative delta', () => {
		const nextState = applyResize(
			state,
			0,
			-10000,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.height).toBe(MIN_HEIGHT);
	});

	it('does not change the position on a small delta that keeps the panel in bounds', () => {
		const nextState = applyResize(
			state,
			20,
			20,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.x).toBe(state.x);
		expect(nextState.y).toBe(state.y);
	});

	it('keeps the panel within the right edge of the viewport on a large width delta', () => {
		const rightEdgeState: PanelState = {
			...state,
			x: VIEWPORT_WIDTH - state.width,
		};

		const nextState = applyResize(
			rightEdgeState,
			10000,
			0,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.x + nextState.width).toBeLessThanOrEqual(
			VIEWPORT_WIDTH
		);
	});

	it('keeps the panel within the bottom edge of the viewport on a large height delta', () => {
		const bottomEdgeState: PanelState = {
			...state,
			y: VIEWPORT_HEIGHT - state.height,
		};

		const nextState = applyResize(
			bottomEdgeState,
			0,
			10000,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.y + nextState.height).toBeLessThanOrEqual(
			VIEWPORT_HEIGHT
		);
	});
});

describe('applyDrag', () => {
	const state: PanelState = {height: 500, width: 400, x: 100, y: 100};

	it('clamps x to the left edge of the viewport', () => {
		const nextState = applyDrag(
			state,
			-10000,
			0,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.x).toBe(0);
	});

	it('clamps x to the right edge of the viewport', () => {
		const nextState = applyDrag(
			state,
			10000,
			0,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.x).toBe(VIEWPORT_WIDTH - state.width);
	});

	it('clamps y to the top edge of the viewport', () => {
		const nextState = applyDrag(
			state,
			0,
			-10000,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.y).toBe(0);
	});

	it('clamps y to the bottom edge of the viewport', () => {
		const nextState = applyDrag(
			state,
			0,
			10000,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.y).toBe(VIEWPORT_HEIGHT - state.height);
	});

	it('does not change the size', () => {
		const nextState = applyDrag(
			state,
			20,
			20,
			VIEWPORT_WIDTH,
			VIEWPORT_HEIGHT
		);

		expect(nextState.width).toBe(state.width);
		expect(nextState.height).toBe(state.height);
	});
});

describe('readStoredState and writeStoredState', () => {
	const key = `${STORAGE_KEY_PREFIX}:test-panel`;

	afterEach(() => {
		sessionStorage.clear();
	});

	it('round-trips a written state', () => {
		const state: PanelState = {height: 500, width: 400, x: 10, y: 20};

		writeStoredState(key, state);

		expect(readStoredState(key)).toEqual(state);
	});

	it('returns null when storage is empty', () => {
		expect(readStoredState(key)).toBeNull();
	});

	it('returns null when storage holds invalid JSON', () => {
		sessionStorage.setItem(key, '{not json');

		expect(readStoredState(key)).toBeNull();
	});

	it('returns null when the stored object is missing required keys', () => {
		sessionStorage.setItem(key, JSON.stringify({height: 500, width: 400}));

		expect(readStoredState(key)).toBeNull();
	});

	it('derives a different key per panel id', () => {
		const state: PanelState = {height: 500, width: 400, x: 10, y: 20};

		writeStoredState(`${STORAGE_KEY_PREFIX}:panel-a`, state);

		expect(readStoredState(`${STORAGE_KEY_PREFIX}:panel-a`)).toEqual(state);
		expect(readStoredState(`${STORAGE_KEY_PREFIX}:panel-b`)).toBeNull();
	});
});

describe('useFloatingPanel', () => {
	afterEach(() => {
		sessionStorage.clear();
	});

	it('persists state under a key derived from the given id', () => {
		const {result} = renderHook(() => useFloatingPanel('panel-a'));

		act(() => {
			result.current.drag(50, 50);
		});

		expect(readStoredState(`${STORAGE_KEY_PREFIX}:panel-a`)).toEqual(
			result.current.state
		);
	});

	it('does not let one panel id read state written under another', () => {
		const {result: resultA} = renderHook(() => useFloatingPanel('panel-a'));

		act(() => {
			resultA.current.drag(50, 50);
		});

		const {result: resultB} = renderHook(() => useFloatingPanel('panel-b'));

		expect(resultB.current.state).not.toEqual(resultA.current.state);
	});

	it('re-clamps the panel into view when the window shrinks', () => {
		const originalWidth = window.innerWidth;
		const originalHeight = window.innerHeight;

		const {result} = renderHook(() => useFloatingPanel('panel-resize'));

		act(() => {
			result.current.resize(1000, 1000);
			result.current.drag(1000, 1000);
		});

		act(() => {
			window.innerWidth = 700;
			window.innerHeight = 600;
			window.dispatchEvent(new Event('resize'));
		});

		expect(
			result.current.state.x + result.current.state.width
		).toBeLessThanOrEqual(700);
		expect(
			result.current.state.y + result.current.state.height
		).toBeLessThanOrEqual(600);

		window.innerWidth = originalWidth;
		window.innerHeight = originalHeight;
	});

	it('isolates two instances that both fall back to a generated id', () => {
		function useFloatingPanelWithFallbackId() {
			const generatedId = useId();

			return useFloatingPanel(generatedId);
		}

		const {result} = renderHook(() => ({
			panelA: useFloatingPanelWithFallbackId(),
			panelB: useFloatingPanelWithFallbackId(),
		}));

		act(() => {
			result.current.panelA.drag(50, 50);
		});

		expect(result.current.panelB.state).not.toEqual(
			result.current.panelA.state
		);
	});
});
