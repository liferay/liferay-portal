/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EditState, rotatedSize} from '../state/types';

/**
 * Where the workspace has to be scrolled so that the point under `anchor`
 * stays under it after a zoom step.
 *
 * `anchor` is in workspace-viewport coordinates (0,0 at its top left), and
 * the stage begins `padding / 2` into the scrollable content.
 */
export function anchoredScroll({
	anchor,
	next,
	padding,
	scroll,
	zoom,
}: {
	anchor: {x: number; y: number};
	next: number;
	padding: number;
	scroll: {left: number; top: number};
	zoom: number;
}): {left: number; top: number} {
	const offset = padding / 2;

	const point = {
		x: (scroll.left + anchor.x - offset) / zoom,
		y: (scroll.top + anchor.y - offset) / zoom,
	};

	return {
		left: offset + point.x * next - anchor.x,
		top: offset + point.y * next - anchor.y,
	};
}

/**
 * The step an arrow key asks for, as a unit vector, or nothing when the
 * key was not an arrow. Shared by everything on the stage that moves with
 * the keyboard: the crop and its handles.
 */
export function arrowDelta(key: string): [number, number] | null {
	switch (key) {
		case 'ArrowDown':
			return [0, 1];
		case 'ArrowLeft':
			return [-1, 0];
		case 'ArrowRight':
			return [1, 0];
		case 'ArrowUp':
			return [0, -1];
		default:
			return null;
	}
}

/**
 * Factor the image must grow by so that, rotated by `angle`, it still
 * covers a frame of the given size. Exported for unit testing.
 */
export function coverScale(
	width: number,
	height: number,
	angle: number
): number {
	if (!angle) {
		return 1;
	}

	const radians = (angle * Math.PI) / 180;

	const cos = Math.abs(Math.cos(radians));
	const sin = Math.abs(Math.sin(radians));

	return Math.max(
		(width * cos + height * sin) / width,
		(width * sin + height * cos) / height
	);
}

/**
 * The full transform placing the source image inside the stage: the
 * quarter turns, then the straighten angle with its cover scale, both
 * around the center of the frame. Shared by the preview and the export so
 * every projection stays aligned.
 */
export function imageTransform(
	state: Pick<
		EditState,
		'angle' | 'flipHorizontal' | 'rotation' | 'sourceHeight' | 'sourceWidth'
	>
): string | undefined {
	const quarter = rotationTransform(state);

	const mirror = state.flipHorizontal
		? `translate(${rotatedSize(state as EditState).width} 0) scale(-1 1)`
		: undefined;

	if (!state.angle) {
		return [mirror, quarter].filter(Boolean).join(' ') || undefined;
	}

	const bounds = rotatedSize(state as EditState);

	const centerX = bounds.width / 2;
	const centerY = bounds.height / 2;

	const scale = coverScale(bounds.width, bounds.height, state.angle);

	const straighten =
		`rotate(${state.angle} ${centerX} ${centerY}) ` +
		`translate(${centerX} ${centerY}) scale(${scale}) ` +
		`translate(${-centerX} ${-centerY})`;

	return [mirror, straighten, quarter].filter(Boolean).join(' ');
}

function rotationTransform(
	state: Pick<EditState, 'rotation' | 'sourceHeight' | 'sourceWidth'>
): string | undefined {
	switch (state.rotation) {
		case 90:
			return `translate(${state.sourceHeight} 0) rotate(90)`;
		case 180:
			return `translate(${state.sourceWidth} ${state.sourceHeight}) rotate(180)`;
		case 270:
			return `translate(0 ${state.sourceWidth}) rotate(270)`;
		default:
			return undefined;
	}
}
