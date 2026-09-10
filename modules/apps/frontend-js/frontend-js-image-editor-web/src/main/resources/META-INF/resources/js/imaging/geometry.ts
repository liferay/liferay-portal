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
 * The full transform placing the source image inside the stage: the
 * mirror, then the quarter turns. Shared by the preview and the export so
 * every projection stays aligned.
 */
export function imageTransform(
	state: Pick<
		EditState,
		'flipHorizontal' | 'rotation' | 'sourceHeight' | 'sourceWidth'
	>
): string | undefined {
	const quarter = rotationTransform(state);

	const mirror = state.flipHorizontal
		? `translate(${rotatedSize(state as EditState).width} 0) scale(-1 1)`
		: undefined;

	return [mirror, quarter].filter(Boolean).join(' ') || undefined;
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
