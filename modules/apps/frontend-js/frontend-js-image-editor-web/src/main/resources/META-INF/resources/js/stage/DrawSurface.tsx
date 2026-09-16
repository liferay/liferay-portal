/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {useEditorId} from '../chrome/instance';
import {DEFAULT_ANNOTATION_COLOR} from '../imaging/overlayShapes';
import {
	pointsBounds,
	pointsToPath,
	simplifyPoints,
} from '../imaging/strokeGeometry';
import {nextId} from '../state/ids';
import {CropRect, StrokeOverlay} from '../state/types';

const CAPTURE_SPACING = 3;

const DRAG_THRESHOLD = 4;

function DrawAnchor({x, y, zoom}: {x: number; y: number; zoom: number}) {
	return (
		<g pointerEvents="none" transform={`translate(${x} ${y})`}>
			<circle
				className="editor-draw-anchor-halo"
				r={6 / zoom}
				strokeWidth={4 / zoom}
			/>

			<circle
				className="editor-draw-anchor"
				r={6 / zoom}
				strokeWidth={2 / zoom}
			/>
		</g>
	);
}

export interface DrawResult {
	points: number[];
	smooth: boolean;
}

export function strokeWidthFor(area: CropRect): number {
	return Math.max(3, Math.round(Math.min(area.width, area.height) * 0.008));
}

export function strokeFromDrawing(
	result: DrawResult,
	area: CropRect
): StrokeOverlay {
	const box = pointsBounds(result.points);

	return {
		color: DEFAULT_ANNOTATION_COLOR,
		id: nextId('stroke'),
		kind: 'stroke',
		points: result.points.map(
			(value, index) =>
				Math.round((value - (index % 2 === 0 ? box.x : box.y)) * 10) /
				10
		),
		smooth: result.smooth,
		width: strokeWidthFor(area),
		x: Math.round(box.x),
		y: Math.round(box.y),
	};
}

interface Props {

	/**
	 * The crop rectangle, which is where the keyboard cursor starts and
	 * what the surface covers.
	 */
	area: CropRect;

	color: string;

	/**
	 * Keyboard-triggered drawing runs as a guided line instead of the
	 * free pen: the start is placed at the centre, the arrows aim the
	 * end, Enter sets it, the arrows then bend the middle, Enter
	 * finishes. A fixed number of announced steps, because "place as
	 * many points as you like" is a fine pointer contract and a vague
	 * ear-only one.
	 */
	guided?: boolean;

	onAnnounce: (message: string) => void;

	/**
	 * Called once per finished stroke, or with null when drawing is
	 * cancelled. Points are absolute image coordinates.
	 */
	onFinish: (result: DrawResult | null) => void;

	width: number;
	zoom: number;
}

/**
 * The drawing mode. One surface, two routes to the same stroke: a drag is
 * freehand and commits on release, while clicks (or the keyboard cursor
 * with Enter) place pen points one at a time, no dragging required
 * anywhere (WCAG 2.5.7). Enter on the spot where the last point already
 * is finishes the stroke, the keyboard analogue of the pen's double
 * click; Backspace removes the last point; Escape abandons the stroke.
 */
export function DrawSurface({
	area,
	color,
	guided,
	onAnnounce,
	onFinish,
	width,
	zoom,
}: Props) {
	const eid = useEditorId();

	const [points, setPoints] = useState<number[]>([]);

	const [cursor, setCursor] = useState({
		x: Math.round(area.x + area.width / 2),
		y: Math.round(area.y + area.height / 2),
	});

	/**
	 * The guided line's fixed parts: the start (always the crop centre)
	 * and, once Enter has set it, the end. `stage` is which point the
	 * arrows are moving.
	 */
	const [guide, setGuide] = useState<{
		end: {x: number; y: number} | null;
		stage: 'bend' | 'end';
	}>({end: null, stage: 'end'});

	const start = {
		x: Math.round(area.x + area.width / 2),
		y: Math.round(area.y + area.height / 2),
	};

	const surfaceRef = useRef<SVGRectElement>(null);

	const gestureRef = useRef<{
		capturing: boolean;
		last: [number, number];
		startCount: number;
	} | null>(null);

	// The mode begins on the surface, where every key already works.

	// The announcement belongs to the mode's opening, not to any later
	// change of the announcer's identity: the ref makes the effect
	// run-once under its full dependency list.

	const openedRef = useRef(false);

	useEffect(() => {
		if (openedRef.current) {
			return;
		}

		openedRef.current = true;

		surfaceRef.current?.focus({preventScroll: true});

		onAnnounce(
			Liferay.Language.get(
				guided
					? 'the-line-starts-at-the-center-of-the-crop-area.-move-its-end-with-the-arrow-keys-then-press-enter-to-set-the-line'
					: 'drawing-started.-click-or-press-enter-to-add-points-and-finish-on-the-last-point'
			)
		);
	}, [guided, onAnnounce]);

	const toImage = (event: React.PointerEvent): [number, number] => {
		const box = surfaceRef.current!.getBoundingClientRect();

		return [
			area.x + (event.clientX - box.left) / zoom,
			area.y + (event.clientY - box.top) / zoom,
		];
	};

	const addPoint = (x: number, y: number) => {
		setPoints((current) => [
			...current,
			Math.round(x * 10) / 10,
			Math.round(y * 10) / 10,
		]);
	};

	const announcePoint = (count: number, x: number, y: number) =>
		onAnnounce(
			sub(
				Liferay.Language.get('point-x-added-at-x-x-y-x'),
				count,
				Math.round(x),
				Math.round(y)
			)
		);

	const cancel = () => {
		onFinish(null);

		onAnnounce(Liferay.Language.get('drawing-canceled'));
	};

	const finish = (raw: number[], smooth: boolean) => {
		if (raw.length < 4) {
			cancel();

			return;
		}

		onFinish({points: raw, smooth});
	};

	const handlePointerDown = (event: React.PointerEvent<SVGRectElement>) => {
		if (guided) {
			return;
		}

		event.currentTarget.setPointerCapture?.(event.pointerId);

		const [x, y] = toImage(event);

		gestureRef.current = {
			capturing: false,
			last: [x, y],
			startCount: points.length,
		};
	};

	const handlePointerMove = (event: React.PointerEvent<SVGRectElement>) => {
		const active = gestureRef.current;

		if (!active) {
			return;
		}

		const [x, y] = toImage(event);

		const spacing = CAPTURE_SPACING / zoom;

		if (!active.capturing) {

			// Not freehand until the hand actually moves: a click with a
			// shaky press must stay a click.

			if (
				Math.hypot(x - active.last[0], y - active.last[1]) * zoom <
				DRAG_THRESHOLD
			) {
				return;
			}

			active.capturing = true;

			setPoints((current) => [
				...current,
				active.last[0],
				active.last[1],
			]);
		}

		if (Math.hypot(x - active.last[0], y - active.last[1]) >= spacing) {
			active.last = [x, y];

			addPoint(x, y);
		}
	};

	const handlePointerCancel = () => {
		const active = gestureRef.current;

		if (!active) {
			return;
		}

		gestureRef.current = null;

		if (active.capturing) {

			// The interrupted drag never became a stroke: its points go,
			// the mode and any pen points placed before it stay.

			setPoints((current) => current.slice(0, active.startCount));

			onAnnounce(Liferay.Language.get('drawing-canceled'));
		}
	};

	const handlePointerUp = (event: React.PointerEvent<SVGRectElement>) => {
		const active = gestureRef.current;

		if (!active) {
			return;
		}

		gestureRef.current = null;

		const [x, y] = toImage(event);

		if (active.capturing) {

			// A freehand gesture is one stroke: simplified once, at a
			// tolerance measured on screen, and committed on release.

			finish(simplifyPoints([...points, x, y], 1.5 / zoom), true);

			return;
		}

		// A click places a pen point, and a second click on the same spot
		// finishes, mirroring the double-click convention.

		const last = points.length
			? [points[points.length - 2], points[points.length - 1]]
			: null;

		if (last && Math.hypot(x - last[0], y - last[1]) * zoom < 6) {
			finish(points, true);

			return;
		}

		addPoint(x, y);

		setCursor({x: Math.round(x), y: Math.round(y)});

		announcePoint(points.length / 2 + 1, x, y);
	};

	const handleGuidedKeyDown = (
		event: React.KeyboardEvent<SVGRectElement>
	) => {
		const step = (event.shiftKey ? 10 : 1) / zoom;

		switch (event.key) {
			case 'ArrowDown':
				setCursor((at) => ({...at, y: at.y + step}));
				break;

			case 'ArrowLeft':
				setCursor((at) => ({...at, x: at.x - step}));
				break;

			case 'ArrowRight':
				setCursor((at) => ({...at, x: at.x + step}));
				break;

			case 'ArrowUp':
				setCursor((at) => ({...at, y: at.y - step}));
				break;

			case 'Backspace':
			case 'Delete':

				// One stage back: the end unfixes and the arrows hold it
				// again.

				if (guide.stage === 'bend') {
					setCursor({...guide.end!});

					setGuide({end: null, stage: 'end'});

					onAnnounce(
						Liferay.Language.get(
							'the-line-starts-at-the-center-of-the-crop-area.-move-its-end-with-the-arrow-keys-then-press-enter-to-set-the-line'
						)
					);
				}
				break;

			case 'Enter':
				if (guide.stage === 'end') {

					// A line needs a length before it can be set.

					if (
						Math.hypot(cursor.x - start.x, cursor.y - start.y) < 1
					) {
						onAnnounce(
							Liferay.Language.get(
								'move-the-end-away-from-the-start-first'
							)
						);

						break;
					}

					// The end is fixed; the arrows now hold the middle,
					// starting from the straight line's own midpoint.

					setGuide({end: {...cursor}, stage: 'bend'});

					setCursor({
						x: Math.round((start.x + cursor.x) / 2),
						y: Math.round((start.y + cursor.y) / 2),
					});

					onAnnounce(
						Liferay.Language.get(
							'line-set.-move-the-middle-point-with-the-arrow-keys-to-curve-the-line-then-press-enter-to-finish'
						)
					);

					break;
				}

				{
					const end = guide.end!;

					const straightX = (start.x + end.x) / 2;
					const straightY = (start.y + end.y) / 2;

					// A middle never moved is no middle at all: the line
					// commits as its two ends.

					const bent =
						Math.hypot(
							cursor.x - straightX,
							cursor.y - straightY
						) >= 1;

					finish(
						bent
							? [
									start.x,
									start.y,
									cursor.x,
									cursor.y,
									end.x,
									end.y,
								]
							: [start.x, start.y, end.x, end.y],
						true
					);
				}
				break;

			case 'Escape':
				cancel();
				break;

			default:
				return;
		}

		event.preventDefault();
		event.stopPropagation();
	};

	const handleKeyDown = (event: React.KeyboardEvent<SVGRectElement>) => {
		if (guided) {
			handleGuidedKeyDown(event);

			return;
		}

		const step = (event.shiftKey ? 10 : 1) / zoom;

		switch (event.key) {
			case 'ArrowDown':
				setCursor((at) => ({...at, y: at.y + step}));
				break;

			case 'ArrowLeft':
				setCursor((at) => ({...at, x: at.x - step}));
				break;

			case 'ArrowRight':
				setCursor((at) => ({...at, x: at.x + step}));
				break;

			case 'ArrowUp':
				setCursor((at) => ({...at, y: at.y - step}));
				break;

			case 'Backspace':
			case 'Delete':
				setPoints((current) => current.slice(0, -2));

				onAnnounce(Liferay.Language.get('point-removed'));
				break;

			case 'Enter': {
				const last = points.length
					? [points[points.length - 2], points[points.length - 1]]
					: null;

				// Enter where the last point already sits finishes: the
				// keyboard's double click.

				if (
					last &&
					Math.hypot(cursor.x - last[0], cursor.y - last[1]) <
						2 / zoom
				) {
					finish(points, true);

					break;
				}

				addPoint(cursor.x, cursor.y);

				announcePoint(points.length / 2 + 1, cursor.x, cursor.y);
				break;
			}

			case 'Escape':
				cancel();
				break;

			default:
				return;
		}

		event.preventDefault();
		event.stopPropagation();
	};

	const preview = guided
		? guide.stage === 'end'
			? Math.hypot(cursor.x - start.x, cursor.y - start.y) >= 1
				? pointsToPath([start.x, start.y, cursor.x, cursor.y], false)
				: ''
			: pointsToPath(
					[
						start.x,
						start.y,
						cursor.x,
						cursor.y,
						guide.end!.x,
						guide.end!.y,
					],
					true
				)
		: pointsToPath(points, true);

	return (
		<g className="editor-draw-surface">
			<desc id={eid('draw-instructions')}>
				{Liferay.Language.get(
					'click-or-press-enter-to-add-points.-use-the-arrow-keys-to-move-the-cursor.-press-backspace-to-remove-the-last-point.-press-enter-on-the-last-point-or-double-click-to-finish.-press-escape-to-cancel'
				)}
			</desc>

			<rect
				aria-describedby={eid('draw-instructions')}
				aria-label={Liferay.Language.get('drawing-area')}
				fill="transparent"
				height={area.height}
				onKeyDown={handleKeyDown}
				onPointerCancel={handlePointerCancel}
				onPointerDown={handlePointerDown}
				onPointerMove={handlePointerMove}
				onPointerUp={handlePointerUp}
				ref={surfaceRef}
				role="application"
				style={{cursor: 'crosshair'}}
				tabIndex={0}
				width={area.width}
				x={area.x}
				y={area.y}
			/>

			{Boolean(preview) && (
				<path
					className="editor-draw-preview"
					d={preview}
					fill="none"
					pointerEvents="none"
					stroke={color}
					strokeLinecap="round"
					strokeLinejoin="round"
					strokeWidth={width}
				/>
			)}

			{guided && <DrawAnchor x={start.x} y={start.y} zoom={zoom} />}

			{guided && guide.end && (
				<DrawAnchor x={guide.end.x} y={guide.end.y} zoom={zoom} />
			)}

			{/*
			 * The keyboard cursor: a crosshair that never grows or
			 * shrinks with the zoom, drawn last so it rides above the
			 * preview.
			 */}

			<g
				className="editor-draw-cursor"
				pointerEvents="none"
				transform={`translate(${cursor.x} ${cursor.y})`}
			>

				{/*
				 * Two passes, like every indicator on the stage: a white
				 * halo under the accent, so the cross reads on stone and
				 * on sky alike, at any zoom.
				 */}

				{[
					{name: 'editor-draw-cursor-halo', width: 5},
					{name: 'editor-draw-cursor-line', width: 2},
				].map(({name, width}) => (
					<g className={name} key={name}>
						<line
							strokeWidth={width / zoom}
							x1={-14 / zoom}
							x2={14 / zoom}
							y1={0}
							y2={0}
						/>

						<line
							strokeWidth={width / zoom}
							x1={0}
							x2={0}
							y1={-14 / zoom}
							y2={14 / zoom}
						/>
					</g>
				))}

				<circle
					className="editor-draw-cursor-dot"
					r={2 / zoom}
					strokeWidth={1 / zoom}
				/>
			</g>
		</g>
	);
}
