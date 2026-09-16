/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React from 'react';

import {
	ArrowOverlay,
	Overlay,
	RedactLevel,
	RedactOverlay,
} from '../state/types';
import {REDACT_SIZES} from './loadImage';
import {
	pointsBounds,
	pointsToPath,
	sketchyEllipsePath,
	sketchyRectPath,
} from './strokeGeometry';

export const DEFAULT_BORDER_COLOR = '#272833';

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
 * The outline colour, or nothing at all when no border was asked for. A
 * width without a colour still draws, in the default border colour, so the
 * two fields do not have to be filled in a particular order.
 */
function borderStroke(overlay: {
	borderColor?: string;
	borderWidth?: number;
}): string | undefined {
	if (!overlay.borderWidth) {
		return undefined;
	}

	return overlay.borderColor ?? DEFAULT_BORDER_COLOR;
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
		case 'arrow': {

			// The box around both ends, grown by the stroke so a focus
			// ring does not cut through a thick arrow's edge.

			const pad = overlay.thickness / 2;

			return {
				height: Math.abs(overlay.dy) + overlay.thickness,
				width: Math.abs(overlay.dx) + overlay.thickness,
				x: overlay.x + Math.min(overlay.dx, 0) - pad,
				y: overlay.y + Math.min(overlay.dy, 0) - pad,
			};
		}

		case 'circle':
		case 'redact':
		case 'shape':
			return {
				height: overlay.height,
				width: overlay.width,
				x: overlay.x,
				y: overlay.y,
			};

		case 'stroke': {

			// The points' own box, grown by the stroke on every side.

			const box = pointsBounds(overlay.points);
			const pad = overlay.width / 2;

			return {
				height: box.height + overlay.width,
				width: box.width + overlay.width,
				x: overlay.x + box.x - pad,
				y: overlay.y + box.y - pad,
			};
		}

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

	// An arrow has none to speak of: where it points is already said by
	// its two ends, so the field is not on the type at all.

	return overlay.kind === 'arrow' ? 0 : overlay.rotation ?? 0;
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
		case 'arrow':
			return Liferay.Language.get('arrow');

		case 'circle':
			return Liferay.Language.get('circle');

		case 'redact':
			return Liferay.Language.get('redacted-area');

		case 'shape':
			return Liferay.Language.get('rectangle');

		case 'stroke':
			return Liferay.Language.get('stroke');

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
export interface RedactSource {

	/**
	 * Reference to the color pipeline in use (`url(#...)`), so the mosaic
	 * carries the same adjustments and filter as the image underneath.
	 * Undefined when the pipeline is the identity.
	 */
	filter?: string;

	/**
	 * The picture itself, which the blur works from: a mosaic wants a
	 * downsampled copy, a blur wants the real thing.
	 */
	imageUrl?: string;

	pixelUrls: Record<RedactLevel, string>;
	sourceHeight: number;
	sourceWidth: number;

	/**
	 * The same transform the base image uses, so the mosaic lines up with
	 * the photo whatever the rotation and straighten angle.
	 */
	transform?: string;
}

export function OverlayShape({
	overlay,
	redactSource,
}: {
	overlay: Overlay;
	redactSource?: RedactSource;
}) {
	const opacity = (overlay.opacity ?? 100) / 100;

	const node = renderOverlayNode(overlay, redactSource);

	return opacity < 1 ? <g opacity={opacity}>{node}</g> : node;
}

/**
 * The blur that matches a mosaic step. A mosaic of block size B destroys
 * detail finer than B, and a Gaussian blur does something comparable at
 * roughly half that as its deviation, so the four steps mean the same
 * amount of hiding whichever style is chosen.
 */
function blurDeviation(level: RedactLevel, sourceLongestSide: number): number {
	return Math.max(sourceLongestSide / REDACT_SIZES[level] / 2, 1);
}

/**
 * A redaction reveals a heavily downsampled copy of the image through a
 * clip, scaled back up with nearest-neighbor: real pixelation, entirely
 * declarative. The inner counter-rotation keeps the mosaic locked to the
 * photo when the block itself is rotated.
 */
function RedactBlock({
	overlay,
	source,
}: {
	overlay: RedactOverlay;
	source?: RedactSource;
}) {
	const blurId = `redact-blur-${overlay.id}`;
	const clipId = `redact-clip-${overlay.id}`;

	if (!source) {
		return (
			<rect
				fill="#14151f"
				height={overlay.height}
				width={overlay.width}
				x={overlay.x}
				y={overlay.y}
			/>
		);
	}

	const centerX = overlay.x + overlay.width / 2;
	const centerY = overlay.y + overlay.height / 2;

	const blurred = overlay.style === 'blur' && Boolean(source.imageUrl);

	// The blur is applied to the whole picture and clipped afterwards, so
	// no transparency is drawn in from outside the block: blurring a
	// cut-out first would fade its own edges.

	const deviation = blurDeviation(
		overlay.level,
		Math.max(source.sourceWidth, source.sourceHeight)
	);

	return (
		<>
			<defs>
				<clipPath id={clipId}>
					<rect
						height={overlay.height}
						width={overlay.width}
						x={overlay.x}
						y={overlay.y}
					/>
				</clipPath>

				{blurred && (
					<filter
						colorInterpolationFilters="sRGB"
						height="130%"
						id={blurId}
						width="130%"
						x="-15%"
						y="-15%"
					>
						<feGaussianBlur stdDeviation={deviation} />
					</filter>
				)}
			</defs>

			<g clipPath={`url(#${clipId})`}>
				<g
					transform={`rotate(${-(
						overlay.rotation ?? 0
					)} ${centerX} ${centerY})`}
				>
					<g filter={blurred ? `url(#${blurId})` : undefined}>
						<g transform={source.transform}>
							<image
								filter={source.filter}
								height={source.sourceHeight}
								href={
									blurred
										? source.imageUrl
										: source.pixelUrls[overlay.level]
								}
								preserveAspectRatio="none"
								style={
									blurred
										? undefined
										: {imageRendering: 'pixelated'}
								}
								width={source.sourceWidth}
							/>
						</g>
					</g>
				</g>
			</g>
		</>
	);
}

/**
 * Where the parts of an arrow sit, in image units. The head is sized from
 * the stroke so the two stay in proportion at any thickness, and the
 * shaft of a filled arrow stops short of the tip: a line running all the
 * way through a solid head pokes out of it whenever the head is drawn
 * with a stroke of its own.
 */
export function arrowGeometry(overlay: ArrowOverlay) {
	const length = Math.hypot(overlay.dx, overlay.dy);

	const tipX = overlay.x + overlay.dx;
	const tipY = overlay.y + overlay.dy;

	// A head no longer than a third of the arrow, so a short arrow stays
	// an arrow rather than becoming a triangle on a stub.

	const headLength = Math.min(overlay.thickness * 3.2, length / 3 || 0);
	const headWidth = headLength * 0.8;

	if (!length) {
		return {
			barbs: '',
			headLength,
			headPoints: '',
			shaftX: tipX,
			shaftY: tipY,
			tipX,
			tipY,
		};
	}

	// Unit vector along the arrow, and the perpendicular that gives the
	// head its width.

	const ux = overlay.dx / length;
	const uy = overlay.dy / length;

	const baseX = tipX - ux * headLength;
	const baseY = tipY - uy * headLength;

	const spreadX = (-uy * headWidth) / 2;
	const spreadY = (ux * headWidth) / 2;

	return {
		barbs: [
			`M${baseX + spreadX} ${baseY + spreadY}`,
			`L${tipX} ${tipY}`,
			`L${baseX - spreadX} ${baseY - spreadY}`,
		].join(' '),
		headLength,
		headPoints: [
			`${tipX},${tipY}`,
			`${baseX + spreadX},${baseY + spreadY}`,
			`${baseX - spreadX},${baseY - spreadY}`,
		].join(' '),
		shaftX: baseX,
		shaftY: baseY,
		tipX,
		tipY,
	};
}

/**
 * The two head styles. Filled is a solid triangle, open is the same two
 * barbs left as strokes, and both take the arrow's colour and weight.
 */
function ArrowLine({overlay}: {overlay: ArrowOverlay}) {
	const {barbs, headPoints, shaftX, shaftY, tipX, tipY} =
		arrowGeometry(overlay);

	const filled = overlay.head === 'filled';

	return (
		<>
			<line
				stroke={overlay.color}
				strokeLinecap="round"
				strokeWidth={overlay.thickness}
				x1={overlay.x}
				x2={filled ? shaftX : tipX}
				y1={overlay.y}
				y2={filled ? shaftY : tipY}
			/>

			{filled ? (
				<polygon fill={overlay.color} points={headPoints} />
			) : (
				<path
					d={barbs}
					fill="none"
					stroke={overlay.color}
					strokeLinecap="round"
					strokeLinejoin="round"
					strokeWidth={overlay.thickness}
				/>
			)}
		</>
	);
}

function renderOverlayNode(overlay: Overlay, redactSource?: RedactSource) {
	switch (overlay.kind) {
		case 'arrow':
			return <ArrowLine overlay={overlay} />;

		case 'circle':
			if (overlay.sketchSeed !== undefined) {
				return (
					<path
						d={sketchyEllipsePath(
							overlay.x + overlay.width / 2,
							overlay.y + overlay.height / 2,
							overlay.width / 2,
							overlay.height / 2,
							overlay.sketchSeed
						)}
						fill={overlay.color}
						stroke={borderStroke(overlay)}
						strokeLinejoin="round"
						strokeWidth={overlay.borderWidth || undefined}
					/>
				);
			}

			return (
				<ellipse
					cx={overlay.x + overlay.width / 2}
					cy={overlay.y + overlay.height / 2}
					fill={overlay.color}
					rx={overlay.width / 2}
					ry={overlay.height / 2}
					stroke={borderStroke(overlay)}
					strokeWidth={overlay.borderWidth || undefined}
				/>
			);

		case 'redact':
			return <RedactBlock overlay={overlay} source={redactSource} />;

		case 'shape':
			if (overlay.sketchSeed !== undefined) {
				return (
					<path
						d={sketchyRectPath(
							overlay.x,
							overlay.y,
							overlay.width,
							overlay.height,
							overlay.sketchSeed
						)}
						fill={overlay.color}
						stroke={borderStroke(overlay)}
						strokeLinejoin="round"
						strokeWidth={overlay.borderWidth || undefined}
					/>
				);
			}

			return (
				<rect
					fill={overlay.color}
					height={overlay.height}
					stroke={borderStroke(overlay)}
					strokeWidth={overlay.borderWidth || undefined}
					width={overlay.width}
					x={overlay.x}
					y={overlay.y}
				/>
			);

		case 'stroke':
			return (
				<path
					d={pointsToPath(overlay.points, overlay.smooth)}
					fill="none"
					stroke={overlay.color}
					strokeLinecap="round"
					strokeLinejoin="round"
					strokeWidth={overlay.width}
					transform={`translate(${overlay.x} ${overlay.y})`}
				/>
			);

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
	if (overlay.kind === 'arrow') {

		// Both ends mirror, which for a tail plus a vector means the tail
		// reflects and the vector flips: an arrow pointing at a face keeps
		// pointing at it once the photograph turns around.

		return {
			...overlay,
			dx: -overlay.dx,
			x: boundsWidth - overlay.x,
		};
	}

	const rotation = overlay.rotation ? -overlay.rotation : overlay.rotation;

	if (overlay.kind === 'stroke') {

		// The origin reflects and every relative x negates, exactly as
		// the arrow's vector does: the stroke keeps hugging whatever it
		// was drawn around.

		const box = pointsBounds(overlay.points);

		return {
			...overlay,
			points: overlay.points.map((value, index) =>
				index % 2 === 0 ? box.width - (value - box.x) + box.x : value
			),
			rotation,
			x: boundsWidth - overlay.x - box.width - box.x * 2,
		};
	}

	if (overlay.kind === 'text') {
		const width = textWidth(
			overlay.text,
			overlay.fontFamily,
			overlay.fontSize
		);

		return {...overlay, rotation, x: boundsWidth - overlay.x - width};
	}

	return {
		...overlay,
		rotation,
		x: boundsWidth - overlay.x - overlay.width,
	};
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
