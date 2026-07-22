/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useState} from 'react';

export const DEFAULT_HEIGHT = 600;
export const DEFAULT_WIDTH = 400;
export const MARGIN = 24;
export const MAX_WIDTH = 600;
export const MIN_HEIGHT = 450;
export const MIN_WIDTH = 320;
export const NUDGE_STEP = 16;
export const RESIZE_STEP = 16;
export const STORAGE_KEY_PREFIX = 'liferay-ai-chat-floating-panel';

export interface PanelState {
	height: number;
	width: number;
	x: number;
	y: number;
}

export function getMaxHeight(viewportHeight: number): number {
	return Math.floor(0.8 * viewportHeight);
}

export function clamp(min: number, max: number, value: number): number {
	if (max < min) {
		return min;
	}

	if (value < min) {
		return min;
	}

	if (value > max) {
		return max;
	}

	return value;
}

export function getDefaultState(
	viewportWidth: number,
	viewportHeight: number
): PanelState {
	const width = clamp(MIN_WIDTH, MAX_WIDTH, DEFAULT_WIDTH);
	const height = clamp(
		MIN_HEIGHT,
		getMaxHeight(viewportHeight),
		DEFAULT_HEIGHT
	);

	return {
		height,
		width,
		x: clamp(0, viewportWidth - width, viewportWidth - width - MARGIN),
		y: clamp(0, viewportHeight - height, viewportHeight - height - MARGIN),
	};
}

export function clampStateToViewport(
	state: PanelState,
	viewportWidth: number,
	viewportHeight: number
): PanelState {
	const width = clamp(MIN_WIDTH, MAX_WIDTH, state.width);
	const height = clamp(
		MIN_HEIGHT,
		getMaxHeight(viewportHeight),
		state.height
	);

	return {
		height,
		width,
		x: clamp(0, viewportWidth - width, state.x),
		y: clamp(0, viewportHeight - height, state.y),
	};
}

export function applyResize(
	state: PanelState,
	deltaWidth: number,
	deltaHeight: number,
	viewportWidth: number,
	viewportHeight: number
): PanelState {
	const width = clamp(MIN_WIDTH, MAX_WIDTH, state.width + deltaWidth);
	const height = clamp(
		MIN_HEIGHT,
		getMaxHeight(viewportHeight),
		state.height + deltaHeight
	);

	return {
		...state,
		height,
		width,
		x: clamp(0, viewportWidth - width, state.x),
		y: clamp(0, viewportHeight - height, state.y),
	};
}

export function applyDrag(
	state: PanelState,
	deltaX: number,
	deltaY: number,
	viewportWidth: number,
	viewportHeight: number
): PanelState {
	return {
		...state,
		x: clamp(0, viewportWidth - state.width, state.x + deltaX),
		y: clamp(0, viewportHeight - state.height, state.y + deltaY),
	};
}

function isPanelState(value: unknown): value is PanelState {
	if (typeof value !== 'object' || value === null) {
		return false;
	}

	const candidate = value as Record<string, unknown>;

	return (
		typeof candidate.height === 'number' &&
		typeof candidate.width === 'number' &&
		typeof candidate.x === 'number' &&
		typeof candidate.y === 'number'
	);
}

export function readStoredState(key: string): PanelState | null {
	try {
		const storedValue = sessionStorage.getItem(key);

		if (!storedValue) {
			return null;
		}

		const parsedValue = JSON.parse(storedValue);

		if (!isPanelState(parsedValue)) {
			return null;
		}

		return parsedValue;
	}
	catch (_error) {
		return null;
	}
}

export function writeStoredState(key: string, state: PanelState): void {
	try {
		sessionStorage.setItem(key, JSON.stringify(state));
	}
	catch (_error) {}
}

export default function useFloatingPanel(id: string) {
	const storageKey = `${STORAGE_KEY_PREFIX}:${id}`;

	const [state, setState] = useState<PanelState>(() => {
		const storedState = readStoredState(storageKey);

		if (storedState) {
			return clampStateToViewport(
				storedState,
				window.innerWidth,
				window.innerHeight
			);
		}

		return getDefaultState(window.innerWidth, window.innerHeight);
	});

	useEffect(() => {
		writeStoredState(storageKey, state);
	}, [state, storageKey]);

	useEffect(() => {
		const handleWindowResize = () => {
			setState((previousState) =>
				clampStateToViewport(
					previousState,
					window.innerWidth,
					window.innerHeight
				)
			);
		};

		window.addEventListener('resize', handleWindowResize);

		return () => {
			window.removeEventListener('resize', handleWindowResize);
		};
	}, []);

	const drag = useCallback((deltaX: number, deltaY: number) => {
		setState((previousState) =>
			applyDrag(
				previousState,
				deltaX,
				deltaY,
				window.innerWidth,
				window.innerHeight
			)
		);
	}, []);

	const resize = useCallback((deltaWidth: number, deltaHeight: number) => {
		setState((previousState) =>
			applyResize(
				previousState,
				deltaWidth,
				deltaHeight,
				window.innerWidth,
				window.innerHeight
			)
		);
	}, []);

	return {drag, resize, state};
}
