/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React from 'react';

import {Overlay} from '../state/types';

let measureContext: CanvasRenderingContext2D | null | undefined;

export function textWidth(
	content: string,
	fontFamily: string,
	fontSize: number
): number {
	if (measureContext === undefined) {
		try {
			measureContext = document.createElement('canvas').getContext('2d');
		}
		catch {
			measureContext = null;
		}
	}

	if (measureContext) {
		measureContext.font = `${fontSize}px ${fontFamily}`;

		const width = measureContext.measureText(content).width;

		if (Number.isFinite(width) && width > 0) {
			return width;
		}
	}

	return Math.max(content.length * fontSize * 0.6, fontSize);
}

/**
 * Bounding box of an overlay, used for the selection frame, the
 * keyboard/pointer hit target, and the rotation pivot. Text width is
 * measured, not estimated.
 */
export function overlayBounds(overlay: Overlay): {
	height: number;
	width: number;
	x: number;
	y: number;
} {
	switch (overlay.kind) {
		case 'text':
			return {
				height: overlay.fontSize * 1.2,
				width: textWidth(
					overlay.text,
					overlay.fontFamily,
					overlay.fontSize
				),
				x: overlay.x,
				y: overlay.y - overlay.fontSize,
			};

		default:
			throw new Error('Unknown overlay kind');
	}
}

export function overlayCenter(overlay: Overlay): {x: number; y: number} {
	const bounds = overlayBounds(overlay);

	return {x: bounds.x + bounds.width / 2, y: bounds.y + bounds.height / 2};
}

/**
 * The rotation transform of an overlay, around its center. Applied by the
 * stage to the whole interactive group and by the export renderer to the
 * static shape, so both stay identical.
 */
export function overlayRotation(overlay: Overlay): number {
	return overlay.rotation ?? 0;
}

export function overlayTransform(overlay: Overlay): string | undefined {
	const rotation = overlayRotation(overlay);

	if (!rotation) {
		return undefined;
	}

	const center = overlayCenter(overlay);

	return `rotate(${rotation} ${center.x} ${center.y})`;
}

export function overlayLabel(overlay: Overlay): string {
	switch (overlay.kind) {
		case 'text':
			return sub(Liferay.Language.get('text-x'), overlay.text);

		default:
			throw new Error('Unknown overlay kind');
	}
}

/**
 * The visual node of an overlay. Shared verbatim between the interactive
 * preview and the static export renderer. Opacity (the native color input
 * offers no alpha channel) wraps the node as a group attribute, so it
 * rasterizes identically at export.
 */
export function OverlayShape({overlay}: {overlay: Overlay}) {
	const opacity = (overlay.opacity ?? 100) / 100;

	const node = renderOverlayNode(overlay);

	return opacity < 1 ? <g opacity={opacity}>{node}</g> : node;
}

function renderOverlayNode(overlay: Overlay) {
	switch (overlay.kind) {
		case 'text':
			return (
				<text
					fill={overlay.color}
					fontFamily={overlay.fontFamily}
					fontSize={overlay.fontSize}
					x={overlay.x}
					y={overlay.y}
				>
					{overlay.text}
				</text>
			);

		default:
			return null;
	}
}

/**
 * The same overlay, mirrored horizontally inside a frame of `boundsWidth`.
 * A flip has to carry the annotations with it: a caption that stayed put
 * while the photograph mirrored underneath would end up over something
 * else.
 */
export function mirrorOverlay(overlay: Overlay, boundsWidth: number): Overlay {
	const rotation = overlay.rotation ? -overlay.rotation : overlay.rotation;

	const width = textWidth(overlay.text, overlay.fontFamily, overlay.fontSize);

	return {...overlay, rotation, x: boundsWidth - overlay.x - width};
}

/**
 * The box a pointer or a keyboard has to be able to hit, which is the
 * annotation's own box grown to `minimum` on each axis when the annotation
 * is smaller than that. A 6 pixel dot is a legitimate annotation; a 6 pixel
 * target is not (WCAG 2.2, 2.5.8), and the target is the only thing that
 * grows: what is painted stays the size it was asked for.
 */
export function overlayHitBox(
	overlay: Overlay,
	minimum: number
): {height: number; width: number; x: number; y: number} {
	const box = overlayBounds(overlay);

	const width = Math.max(box.width, minimum);
	const height = Math.max(box.height, minimum);

	return {
		height,
		width,
		x: box.x - (width - box.width) / 2,
		y: box.y - (height - box.height) / 2,
	};
}
