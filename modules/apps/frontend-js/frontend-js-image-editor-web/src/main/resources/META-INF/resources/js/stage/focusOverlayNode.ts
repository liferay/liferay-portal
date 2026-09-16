/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function focusOverlayNode(
	root: () => ParentNode,
	id: string,
	delay = 0
): void {
	window.setTimeout(() => {
		window.requestAnimationFrame(() => {
			const node = root().querySelector<SVGElement>(
				`[data-overlay-id="${id}"]`
			);

			if (!node) {
				return;
			}

			(node as unknown as HTMLElement).focus?.({preventScroll: true});

			revealInWorkspace(node);
		});
	}, delay);
}

function revealInWorkspace(node: SVGElement): void {
	const workspace = node.closest<HTMLElement>('.editor-workspace');

	if (!workspace) {
		return;
	}

	const area = workspace.getBoundingClientRect();
	const box = node.getBoundingClientRect();

	const overflow = (start: number, end: number, low: number, high: number) =>
		start < low ? start - low : end > high ? end - high : 0;

	workspace.scrollLeft += overflow(
		box.left,
		box.right,
		area.left,
		area.right
	);
	workspace.scrollTop += overflow(box.top, box.bottom, area.top, area.bottom);
}
