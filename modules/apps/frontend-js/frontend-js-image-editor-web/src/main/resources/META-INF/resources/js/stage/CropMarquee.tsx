/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useRef, useState} from 'react';

import {useEditorId} from '../chrome/instance';
import {arrowDelta} from '../imaging/geometry';
import {EditorAction} from '../state/editorReducer';
import {CropRect} from '../state/types';
import {FocusModality, FocusRing, matchesFocusVisible} from './FocusRing';

const HANDLES: Array<{
	direction: HandleDirection;
	edges: Edges;
	label: string;
}> = [
	{
		direction: 'nw',
		edges: {left: true, top: true},
		label: Liferay.Language.get('crop-handle-top-left-corner'),
	},
	{
		direction: 'n',
		edges: {top: true},
		label: Liferay.Language.get('crop-handle-top-edge'),
	},
	{
		direction: 'ne',
		edges: {right: true, top: true},
		label: Liferay.Language.get('crop-handle-top-right-corner'),
	},
	{
		direction: 'e',
		edges: {right: true},
		label: Liferay.Language.get('crop-handle-right-edge'),
	},
	{
		direction: 'se',
		edges: {bottom: true, right: true},
		label: Liferay.Language.get('crop-handle-bottom-right-corner'),
	},
	{
		direction: 's',
		edges: {bottom: true},
		label: Liferay.Language.get('crop-handle-bottom-edge'),
	},
	{
		direction: 'sw',
		edges: {bottom: true, left: true},
		label: Liferay.Language.get('crop-handle-bottom-left-corner'),
	},
	{
		direction: 'w',
		edges: {left: true},
		label: Liferay.Language.get('crop-handle-left-edge'),
	},
];

const CORNER_HANDLES = HANDLES.filter(({direction}) => direction.length === 2);

const MOVE_EDGES: Edges = {};

interface Edges {
	bottom?: boolean;
	left?: boolean;
	right?: boolean;
	top?: boolean;
}

type HandleDirection = 'e' | 'n' | 'ne' | 'nw' | 's' | 'se' | 'sw' | 'w';

interface Props {
	aspectLocked: boolean;

	bounds: {height: number; width: number};

	crop: CropRect;
	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;
	onCenterCrop: () => void;

	showCrop: boolean;

	showRecenter: boolean;

	zoom: number;
}

export function applyResizeModifiers(
	base: CropRect,
	origin: CropRect,
	edges: Edges,
	options: {center: boolean; proportional: boolean}
): CropRect {
	const isResize = !!(edges.bottom || edges.left || edges.right || edges.top);

	if (!isResize || (!options.center && !options.proportional)) {
		return base;
	}

	let {height, width} = base;

	if (options.center) {
		width = origin.width + 2 * (base.width - origin.width);
		height = origin.height + 2 * (base.height - origin.height);
	}

	if (options.proportional) {
		const scaleX = width / origin.width;
		const scaleY = height / origin.height;

		const horizontal = !!(edges.left || edges.right);
		const vertical = !!(edges.bottom || edges.top);

		const scale =
			horizontal && vertical
				? Math.max(Math.abs(scaleX), Math.abs(scaleY))
				: horizontal
					? Math.abs(scaleX)
					: Math.abs(scaleY);

		width = origin.width * scale;
		height = origin.height * scale;
	}

	let x;
	let y;

	if (options.center) {
		x = origin.x + origin.width / 2 - width / 2;
		y = origin.y + origin.height / 2 - height / 2;
	}
	else {
		x = edges.left
			? origin.x + origin.width - width
			: edges.right
				? origin.x
				: origin.x + origin.width / 2 - width / 2;
		y = edges.top
			? origin.y + origin.height - height
			: edges.bottom
				? origin.y
				: origin.y + origin.height / 2 - height / 2;
	}

	return {height, width, x, y};
}

export function CropMarquee({
	aspectLocked,
	bounds,
	crop,
	dispatch,
	onAnnounce,
	onCenterCrop,
	showCrop,
	showRecenter,
	zoom,
}: Props) {
	const eid = useEditorId();

	const cropRef = useRef(crop);

	useEffect(() => {
		cropRef.current = crop;
	});

	const [focused, setFocused] = useState<{
		key: string;
		modality: FocusModality;
	} | null>(null);

	const [gesturing, setGesturing] = useState(false);

	const [recenterFocused, setRecenterFocused] = useState(false);

	const spritemap = useContext(ClayIconSpriteContext);

	const moveRef = useRef<SVGRectElement>(null);

	const recenterHasFocusRef = useRef(false);

	useEffect(() => {
		if (!showRecenter && recenterHasFocusRef.current) {
			recenterHasFocusRef.current = false;

			moveRef.current?.focus();
		}
	}, [showRecenter]);

	const keyboardGestureRef = useRef(false);

	const pointerGestureRef = useRef<{
		crop: CropRect;
		startX: number;
		startY: number;
	} | null>(null);

	const announceCrop = () => {
		const current = cropRef.current;

		onAnnounce(
			sub(
				Liferay.Language.get(
					'crop-set-to-x-x-y-x-width-x-height-x-pixels'
				),
				current.x,
				current.y,
				current.width,
				current.height
			)
		);
	};

	const handleKeyDown =
		(edges: Edges, focusKey: string) => (event: React.KeyboardEvent) => {
			const delta = arrowDelta(event.key);

			if (!delta) {
				return;
			}

			event.preventDefault();
			event.stopPropagation();

			const step = event.shiftKey ? 10 : 1;

			keyboardGestureRef.current = true;

			setGesturing(true);

			setFocused({key: focusKey, modality: 'keyboard'});

			dispatch({
				crop: adjustCrop(
					cropRef.current,
					edges,
					delta[0] * step,
					delta[1] * step
				),
				transient: true,
				type: 'set-crop',
			});
		};

	const handleKeyUp = (event: React.KeyboardEvent) => {
		if (!arrowDelta(event.key) || !keyboardGestureRef.current) {
			return;
		}

		keyboardGestureRef.current = false;

		setGesturing(false);

		dispatch({crop: cropRef.current, type: 'set-crop'});

		announceCrop();
	};

	const handlePointerDown = (event: React.PointerEvent<SVGElement>) => {
		event.currentTarget.setPointerCapture?.(event.pointerId);

		setGesturing(true);

		pointerGestureRef.current = {
			crop: cropRef.current,
			startX: event.clientX,
			startY: event.clientY,
		};
	};

	const handlePointerMove =
		(edges: Edges) => (event: React.PointerEvent<SVGElement>) => {
			const gesture = pointerGestureRef.current;

			if (!gesture) {
				return;
			}

			const base = adjustCrop(
				gesture.crop,
				edges,
				(event.clientX - gesture.startX) / zoom,
				(event.clientY - gesture.startY) / zoom
			);

			dispatch({
				crop: applyResizeModifiers(base, gesture.crop, edges, {
					center: event.altKey,
					proportional: event.shiftKey || aspectLocked,
				}),
				transient: true,
				type: 'set-crop',
			});
		};

	const handlePointerUp = () => {
		if (!pointerGestureRef.current) {
			return;
		}

		pointerGestureRef.current = null;

		setGesturing(false);

		dispatch({crop: cropRef.current, type: 'set-crop'});

		announceCrop();
	};

	const handlePointerCancel = () => {
		if (!pointerGestureRef.current) {
			return;
		}

		pointerGestureRef.current = null;

		setGesturing(false);

		dispatch({type: 'cancel-gesture'});
	};

	const gridWidth = 1 / zoom;
	const hitRadius = 12 / zoom;
	const visualRadius = 6 / zoom;
	const strokeWidth = 2 / zoom;

	if (!showCrop) {
		return null;
	}

	const dimPath =
		`M0 0H${bounds.width}V${bounds.height}H0Z` +
		`M${crop.x} ${crop.y}` +
		`H${crop.x + crop.width}V${crop.y + crop.height}H${crop.x}Z`;

	return (
		<g>
			<desc id={eid('crop-area-description')}>
				{Liferay.Language.get(
					'use-the-arrow-keys-to-move-the-crop-area-by-1-pixel-hold-shift-for-10-pixels-tab-to-reach-each-resize-handle'
				)}
			</desc>

			<desc id={eid('crop-handle-description')}>
				{Liferay.Language.get(
					'use-the-arrow-keys-to-move-this-handle-by-1-pixel-hold-shift-for-10-pixels'
				)}
			</desc>

			<g
				className={classNames('crop-grid', {
					'crop-grid-visible': gesturing,
				})}
				pointerEvents="none"
			>
				{[1, 2].map((step) => (
					<line
						key={`v-${step}`}
						strokeWidth={gridWidth}
						x1={crop.x + (crop.width * step) / 3}
						x2={crop.x + (crop.width * step) / 3}
						y1={crop.y}
						y2={crop.y + crop.height}
					/>
				))}

				{[1, 2].map((step) => (
					<line
						key={`h-${step}`}
						strokeWidth={gridWidth}
						x1={crop.x}
						x2={crop.x + crop.width}
						y1={crop.y + (crop.height * step) / 3}
						y2={crop.y + (crop.height * step) / 3}
					/>
				))}
			</g>

			<rect
				aria-describedby={eid('crop-area-description')}
				aria-label={Liferay.Language.get('crop-area')}
				className="crop-move"
				fill="transparent"
				height={crop.height}
				onBlur={() => setFocused(null)}
				onFocus={(event) =>
					setFocused({
						key: 'move',
						modality: matchesFocusVisible(event.currentTarget)
							? 'keyboard'
							: 'pointer',
					})
				}
				onKeyDown={handleKeyDown(MOVE_EDGES, 'move')}
				onKeyUp={handleKeyUp}
				onPointerCancel={handlePointerCancel}
				onPointerDown={handlePointerDown}
				onPointerMove={handlePointerMove(MOVE_EDGES)}
				onPointerUp={handlePointerUp}
				ref={moveRef}
				role="button"
				tabIndex={0}
				width={crop.width}
				x={crop.x}
				y={crop.y}
			/>

			{focused?.key === 'move' && (
				<FocusRing
					bounds={crop}
					emphasis={focused.modality}
					zoom={zoom}
				/>
			)}

			<path
				className="crop-dim"
				d={dimPath}
				fillRule="evenodd"
				pointerEvents="none"
			/>

			<rect
				className="crop-border"
				fill="none"
				height={crop.height}
				pointerEvents="none"
				strokeWidth={strokeWidth}
				width={crop.width}
				x={crop.x}
				y={crop.y}
			/>

			{showRecenter &&
				(crop.width < bounds.width || crop.height < bounds.height) && (
					<g
						aria-label={Liferay.Language.get(
							'center-the-crop-in-the-view'
						)}
						className={classNames('crop-recenter', {
							'crop-recenter-focused': recenterFocused,
						})}
						onBlur={() => {
							recenterHasFocusRef.current = false;

							setRecenterFocused(false);
						}}
						onClick={onCenterCrop}
						onFocus={(event) => {
							recenterHasFocusRef.current = true;

							setRecenterFocused(
								matchesFocusVisible(event.currentTarget)
							);
						}}
						onKeyDown={(event: React.KeyboardEvent) => {
							if (event.key === 'Enter' || event.key === ' ') {
								event.preventDefault();
								onCenterCrop();
							}
						}}
						role="button"
						tabIndex={0}
					>
						<title>
							{Liferay.Language.get(
								'center-the-crop-in-the-view'
							)}
						</title>

						<circle
							className="crop-recenter-disc"
							cx={crop.x + crop.width / 2}
							cy={crop.y + crop.height / 2}
							r={16 / zoom}
						/>

						<use
							className="crop-recenter-icon"
							height={18 / zoom}
							href={`${spritemap}#autosize`}
							width={18 / zoom}
							x={crop.x + crop.width / 2 - 9 / zoom}
							y={crop.y + crop.height / 2 - 9 / zoom}
						/>

						{/*
						 * The same ring as every other control on the stage.
						 * A CSS stroke here would be measured in image units
						 * and thin out with the zoom: at 55% the old 3px ring
						 * drew at 1.65px, which is what made a focused
						 * control look unfocused.
						 */}

						{recenterFocused && (
							<FocusRing
								bounds={{
									height: 32 / zoom,
									width: 32 / zoom,
									x: crop.x + crop.width / 2 - 16 / zoom,
									y: crop.y + crop.height / 2 - 16 / zoom,
								}}
								shape="circle"
								zoom={zoom}
							/>
						)}
					</g>
				)}

			{(aspectLocked ? CORNER_HANDLES : HANDLES).map(
				({direction, edges, label}) => {
					const position = handlePosition(crop, direction);

					return (
						<g key={direction}>
							<circle
								className="crop-handle-visual"
								cx={position.x}
								cy={position.y}
								pointerEvents="none"
								r={visualRadius}
								strokeWidth={strokeWidth}
							/>

							{focused?.key === direction && (
								<FocusRing
									bounds={{
										height: hitRadius * 2,
										width: hitRadius * 2,
										x: position.x - hitRadius,
										y: position.y - hitRadius,
									}}
									emphasis={focused.modality}
									shape="circle"
									zoom={zoom}
								/>
							)}

							<circle
								aria-describedby={eid(
									'crop-handle-description'
								)}
								aria-label={label}
								className="crop-handle"
								cx={position.x}
								cy={position.y}
								fill="transparent"
								onBlur={() => setFocused(null)}
								onFocus={(event) =>
									setFocused({
										key: direction,
										modality: matchesFocusVisible(
											event.currentTarget
										)
											? 'keyboard'
											: 'pointer',
									})
								}
								onKeyDown={handleKeyDown(edges, direction)}
								onKeyUp={handleKeyUp}
								onPointerCancel={handlePointerCancel}
								onPointerDown={handlePointerDown}
								onPointerMove={handlePointerMove(edges)}
								onPointerUp={handlePointerUp}
								r={hitRadius}
								role="button"
								tabIndex={0}
							/>
						</g>
					);
				}
			)}
		</g>
	);
}

function adjustCrop(
	crop: CropRect,
	edges: Edges,
	dx: number,
	dy: number
): CropRect {
	let {height, width, x, y} = crop;

	if (!edges.bottom && !edges.left && !edges.right && !edges.top) {
		return {height, width, x: x + dx, y: y + dy};
	}

	if (edges.left) {
		x += dx;
		width -= dx;
	}

	if (edges.right) {
		width += dx;
	}

	if (edges.top) {
		y += dy;
		height -= dy;
	}

	if (edges.bottom) {
		height += dy;
	}

	return {height, width, x, y};
}

function handlePosition(
	crop: CropRect,
	direction: HandleDirection
): {x: number; y: number} {
	const x = direction.includes('w')
		? crop.x
		: direction.includes('e')
			? crop.x + crop.width
			: crop.x + crop.width / 2;

	const y = direction.includes('n')
		? crop.y
		: direction.includes('s')
			? crop.y + crop.height
			: crop.y + crop.height / 2;

	return {x, y};
}
