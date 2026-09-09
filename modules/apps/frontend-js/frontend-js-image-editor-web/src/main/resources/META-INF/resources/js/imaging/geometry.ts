/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

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
