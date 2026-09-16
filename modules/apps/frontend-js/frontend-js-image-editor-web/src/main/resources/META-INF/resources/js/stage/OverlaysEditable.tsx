/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {useEditorId, useEditorRoot} from '../chrome/instance';
import {arrowDelta} from '../imaging/geometry';
import {
	OverlayShape,
	overlayBounds,
	overlayHitBox,
	overlayLabel,
	overlayRotation,
	overlayTransform,
	textWidth,
} from '../imaging/overlayShapes';
import {EditorAction} from '../state/editorReducer';
import {ArrowOverlay, Overlay, isBoxOverlay} from '../state/types';
import {FocusModality, FocusRing, matchesFocusVisible} from './FocusRing';
import {OverlayTextEditor} from './OverlayTextEditor';
import {focusOverlayNode} from './focusOverlayNode';

function toLocalDelta(
	dx: number,
	dy: number,
	rotation: number
): [number, number] {
	if (!rotation) {
		return [dx, dy];
	}

	const radians = (rotation * Math.PI) / 180;

	const cos = Math.cos(radians);
	const sin = Math.sin(radians);

	return [dx * cos + dy * sin, -dx * sin + dy * cos];
}

function toStageDelta(
	dx: number,
	dy: number,
	rotation: number
): [number, number] {
	if (!rotation) {
		return [dx, dy];
	}

	const radians = (rotation * Math.PI) / 180;

	const cos = Math.cos(radians);
	const sin = Math.sin(radians);

	return [dx * cos - dy * sin, dx * sin + dy * cos];
}

const MINIMUM_TARGET = 24;

type StretchEdge = 'e' | 'n' | 's' | 'w';

interface ResizeHandle {
	cursor: string;

	edge?: StretchEdge;

	name: string;
	x: number;
	y: number;
}

const RESIZE_CORNERS: ResizeHandle[] = [
	{cursor: 'nwse-resize', name: 'nw', x: 0, y: 0},
	{cursor: 'nesw-resize', name: 'ne', x: 1, y: 0},
	{cursor: 'nwse-resize', name: 'se', x: 1, y: 1},
	{cursor: 'nesw-resize', name: 'sw', x: 0, y: 1},
];

const STRETCH_EDGES: ResizeHandle[] = [
	{cursor: 'ns-resize', edge: 'n', name: 'n', x: 0.5, y: 0},
	{cursor: 'ew-resize', edge: 'e', name: 'e', x: 1, y: 0.5},
	{cursor: 'ns-resize', edge: 's', name: 's', x: 0.5, y: 1},
	{cursor: 'ew-resize', edge: 'w', name: 'w', x: 0, y: 0.5},
];

const ARROW_ENDS = ['tail', 'tip'] as const;

interface ManipGesture {
	centerX: number;
	centerY: number;
	edge?: StretchEdge;

	end?: 'tail' | 'tip';

	id: string;
	kind: 'endpoint' | 'resize' | 'rotate';
	overlay: Overlay;
	startAngle: number;
	startDistance: number;
	startX: number;
	startY: number;
}

interface Props {
	dispatch: (action: EditorAction) => void;

	multiSelectedIds: string[];
	onAnnounce: (message: string) => void;

	onCopy: (id: string) => void;
	onMultiSelectToggle: (id: string) => void;
	onSelect: (id: string | null) => void;
	overlays: Overlay[];

	proportional: boolean;

	selectedId: string | null;
	zoom: number;
}

export function OverlaysEditable({
	dispatch,
	multiSelectedIds,
	onAnnounce,
	onCopy,
	onMultiSelectToggle,
	onSelect,
	overlays,
	proportional,
	selectedId,
	zoom,
}: Props) {
	const eid = useEditorId();

	const editorRoot = useEditorRoot();

	const overlaysRef = useRef(overlays);

	useEffect(() => {
		overlaysRef.current = overlays;
	});

	const [focus, setFocus] = useState<{
		id: string;
		modality: FocusModality;
	} | null>(null);

	const keyboardGestureRef = useRef<string | null>(null);

	const pointerGestureRef = useRef<{
		id: string;
		startX: number;
		startY: number;
		x: number;
		y: number;
	} | null>(null);

	const manipGestureRef = useRef<ManipGesture | null>(null);

	const [editing, setEditing] = useState<{
		draft: string;
		id: string;
	} | null>(null);

	const current = (id: string) =>
		overlaysRef.current.find((overlay) => overlay.id === id);

	const multiSet = new Set(multiSelectedIds);

	const grouped = (id: string) => multiSet.has(id) && multiSet.size > 1;

	const announceMoved = (id: string) => {
		const overlay = current(id);

		if (overlay) {
			onAnnounce(
				sub(
					Liferay.Language.get('x-moved-to-x-y'),
					overlayLabel(overlay),
					Math.round(overlay.x),
					Math.round(overlay.y)
				)
			);
		}
	};

	const handleKeyDown =
		(id: string) => (event: React.KeyboardEvent<SVGElement>) => {
			if (event.key === 'Enter') {
				event.preventDefault();

				onSelect(id);

				window.setTimeout(() => {
					const header = editorRoot()
						.querySelector(`#${eid('layers-panel-title')}`)
						?.closest('button');

					if (header?.getAttribute('aria-expanded') === 'false') {
						(header as HTMLButtonElement).click();
					}

					window.requestAnimationFrame(() => {
						editorRoot()
							.querySelector<HTMLElement>(
								'.editor-layer-properties input, .editor-layer-properties select'
							)
							?.focus();
					});
				}, 0);

				return;
			}

			if (
				(event.metaKey || event.ctrlKey) &&
				event.key.toLowerCase() === 'c'
			) {

				// Copy, not cut: the original stays. Paste is handled by
				// the workspace, which this event bubbles up to.

				onCopy(id);

				return;
			}

			if (event.key === 'Delete' || event.key === 'Backspace') {
				event.preventDefault();

				// The node under focus is about to unmount, and focus
				// falling to the body would take the undo shortcut down
				// with it: the workspace inherits it instead.

				const workspace = (
					event.currentTarget as SVGElement
				).closest<HTMLElement>('.editor-workspace');

				window.setTimeout(() => workspace?.focus(), 0);

				// Delete on a group member takes the whole group: one
				// entry, one undo, every ring accounted for.

				if (grouped(id)) {
					dispatch({ids: [...multiSet], type: 'remove-overlays'});

					onAnnounce(
						sub(
							Liferay.Language.get('x-annotations-removed'),
							multiSet.size
						)
					);

					onSelect(null);

					return;
				}

				const overlay = current(id);

				dispatch({id, type: 'remove-overlay'});

				if (overlay) {
					onAnnounce(
						sub(
							Liferay.Language.get('x-removed-from-the-image'),
							overlayLabel(overlay)
						)
					);
				}

				return;
			}

			const delta = arrowDelta(event.key);

			if (!delta) {
				return;
			}

			event.preventDefault();
			event.stopPropagation();

			const overlay = current(id);

			if (!overlay) {
				return;
			}

			const step = event.shiftKey ? 10 : 1;

			keyboardGestureRef.current = id;

			// Arrow keys are keyboard interaction even after a mouse
			// focus: surface the full ring.

			setFocus({id, modality: 'keyboard'});

			// A member of the move-together set never travels alone.

			if (grouped(id)) {
				dispatch({
					dx: delta[0] * step,
					dy: delta[1] * step,
					ids: [...multiSet],
					transient: true,
					type: 'move-overlays',
				});

				return;
			}

			// The rotation pivot is the overlay's own center, so it travels
			// with the element: a plain positional delta already moves the
			// element exactly along the screen axes, rotated or not.

			dispatch({
				id,
				patch: {
					x: overlay.x + delta[0] * step,
					y: overlay.y + delta[1] * step,
				},
				transient: true,
				type: 'update-overlay',
			});
		};

	const handleKeyUp =
		(id: string) => (event: React.KeyboardEvent<SVGElement>) => {
			if (!arrowDelta(event.key) || keyboardGestureRef.current !== id) {
				return;
			}

			keyboardGestureRef.current = null;

			if (grouped(id)) {
				dispatch({
					dx: 0,
					dy: 0,
					ids: [...multiSet],
					type: 'move-overlays',
				});

				onAnnounce(
					sub(
						Liferay.Language.get('x-annotations-moved-together'),
						multiSet.size
					)
				);

				return;
			}

			const overlay = current(id);

			if (overlay) {
				dispatch({
					id,
					patch: {x: overlay.x, y: overlay.y},
					type: 'update-overlay',
				});

				announceMoved(id);
			}
		};

	const handlePointerDown =
		(id: string) => (event: React.PointerEvent<SVGElement>) => {
			const overlay = current(id);

			if (!overlay) {
				return;
			}

			// Shift+click curates the move-together set instead of
			// starting a drag: membership is a decision, not a gesture.

			if (event.shiftKey) {
				onMultiSelectToggle(id);

				return;
			}

			event.currentTarget.setPointerCapture?.(event.pointerId);

			onSelect(id);

			pointerGestureRef.current = {
				id,
				startX: event.clientX,
				startY: event.clientY,
				x: overlay.x,
				y: overlay.y,
			};
		};

	const handlePointerMove = (event: React.PointerEvent<SVGElement>) => {
		const gesture = pointerGestureRef.current;

		if (!gesture) {
			return;
		}

		if (grouped(gesture.id)) {

			// The set moves by the pointer's delta since the last event:
			// relative steps, so every member keeps its own place in the
			// formation.

			const dx = (event.clientX - gesture.startX) / zoom;
			const dy = (event.clientY - gesture.startY) / zoom;

			gesture.startX = event.clientX;
			gesture.startY = event.clientY;

			dispatch({
				dx,
				dy,
				ids: [...multiSet],
				transient: true,
				type: 'move-overlays',
			});

			return;
		}

		dispatch({
			id: gesture.id,
			patch: {
				x: gesture.x + (event.clientX - gesture.startX) / zoom,
				y: gesture.y + (event.clientY - gesture.startY) / zoom,
			},
			transient: true,
			type: 'update-overlay',
		});
	};

	// pointercancel (and a capture lost to the browser) reverts the drag
	// wholesale: transients never became history, so the base state is
	// one dispatch away. lostpointercapture also follows every normal
	// release, after pointerup has already cleared the ref, which is what
	// makes it safe to listen to.

	const cancelPointerGesture = () => {
		if (pointerGestureRef.current) {
			pointerGestureRef.current = null;

			dispatch({type: 'cancel-gesture'});
		}
	};

	const cancelManipGesture = () => {
		if (manipGestureRef.current) {
			manipGestureRef.current = null;

			dispatch({type: 'cancel-gesture'});
		}
	};

	// Unmounting mid-drag (a panel switch, the host closing the editor)
	// must not leave a transient half-applied under the next commit.

	useEffect(() => {
		return () => {
			if (pointerGestureRef.current || manipGestureRef.current) {
				dispatch({type: 'cancel-gesture'});
			}
		};
	}, [dispatch]);

	const handlePointerUp = () => {
		const gesture = pointerGestureRef.current;

		if (!gesture) {
			return;
		}

		pointerGestureRef.current = null;

		if (grouped(gesture.id)) {
			dispatch({
				dx: 0,
				dy: 0,
				ids: [...multiSet],
				type: 'move-overlays',
			});

			onAnnounce(
				sub(
					Liferay.Language.get('x-annotations-moved-together'),
					multiSet.size
				)
			);

			return;
		}

		const overlay = current(gesture.id);

		if (overlay) {
			dispatch({
				id: gesture.id,
				patch: {x: overlay.x, y: overlay.y},
				type: 'update-overlay',
			});

			announceMoved(gesture.id);
		}
	};

	const finishTextEdit = () => {
		if (editing) {
			setEditing(null);

			focusOverlayNode(editorRoot, editing.id);
		}
	};

	const commitTextEdit = () => {
		if (!editing) {
			return;
		}

		const overlay = current(editing.id);
		const value = editing.draft.trim();

		finishTextEdit();

		if (
			overlay &&
			overlay.kind === 'text' &&
			value &&
			value !== overlay.text
		) {
			dispatch({
				id: editing.id,
				patch: {text: value},
				type: 'update-overlay',
			});

			onAnnounce(
				sub(Liferay.Language.get('x-updated'), overlayLabel(overlay))
			);
		}
	};

	const startManipulation =
		(
			overlay: Overlay,
			kind: ManipGesture['kind'],
			handleX: number,
			handleY: number,
			edge?: StretchEdge,
			end?: ManipGesture['end']
		) =>
		(event: React.PointerEvent<SVGElement>) => {
			event.stopPropagation();

			event.currentTarget.setPointerCapture?.(event.pointerId);

			onSelect(overlay.id);

			const bounds = overlayBounds(overlay);

			const centerX = bounds.x + bounds.width / 2;
			const centerY = bounds.y + bounds.height / 2;

			manipGestureRef.current = {
				centerX,
				centerY,
				edge,
				end,
				id: overlay.id,
				kind,
				overlay,
				startAngle: Math.atan2(handleY - centerY, handleX - centerX),
				startDistance: Math.hypot(handleX - centerX, handleY - centerY),
				startX: event.clientX,
				startY: event.clientY,
			};
		};

	const handleManipulationMove = (event: React.PointerEvent<SVGElement>) => {
		const gesture = manipGestureRef.current;

		if (!gesture) {
			return;
		}

		const {centerX, centerY, overlay} = gesture;

		// Pointer position in the overlay's local frame: the handle's
		// start position plus the counter-rotated client delta.

		const [dx, dy] = toLocalDelta(
			(event.clientX - gesture.startX) / zoom,
			(event.clientY - gesture.startY) / zoom,
			overlayRotation(overlay)
		);

		const pointX =
			centerX + gesture.startDistance * Math.cos(gesture.startAngle) + dx;
		const pointY =
			centerY + gesture.startDistance * Math.sin(gesture.startAngle) + dy;

		if (gesture.kind === 'rotate') {
			const degrees =
				overlayRotation(overlay) +
				((Math.atan2(pointY - centerY, pointX - centerX) -
					gesture.startAngle) *
					180) /
					Math.PI;

			let rotation = Math.round(((degrees % 360) + 360) % 360);

			if (event.shiftKey) {
				rotation = (Math.round(rotation / 15) * 15) % 360;
			}

			dispatch({
				id: gesture.id,
				patch: {rotation},
				transient: true,
				type: 'update-overlay',
			});

			return;
		}

		// An arrow is aimed by its ends: dragging one leaves the other
		// where it is. Since the tip is held as a vector, moving the tail
		// has to counter-adjust it, or the whole arrow would travel along
		// with the hand instead of pivoting on its tip.

		if (gesture.kind === 'endpoint' && overlay.kind === 'arrow') {
			const patch =
				gesture.end === 'tip'
					? {
							dx: Math.round(pointX - overlay.x),
							dy: Math.round(pointY - overlay.y),
						}
					: {
							dx: Math.round(overlay.x + overlay.dx - pointX),
							dy: Math.round(overlay.y + overlay.dy - pointY),
							x: Math.round(pointX),
							y: Math.round(pointY),
						};

			dispatch({
				id: gesture.id,
				patch,
				transient: true,
				type: 'update-overlay',
			});

			return;
		}

		// Edge handles stretch one dimension of a rectangle freely,
		// anchoring the opposite side: the center shifts by half the size
		// change along the dragged axis, rotated into stage space.

		if (gesture.edge && isBoxOverlay(overlay)) {
			const horizontal = gesture.edge === 'e' || gesture.edge === 'w';
			const sign = gesture.edge === 'e' || gesture.edge === 's' ? 1 : -1;

			const newSize = Math.max(
				horizontal
					? sign * (pointX - centerX) + overlay.width / 2
					: sign * (pointY - centerY) + overlay.height / 2,
				8
			);

			const oldSize = horizontal ? overlay.width : overlay.height;

			const [shiftX, shiftY] = toStageDelta(
				horizontal ? (sign * (newSize - oldSize)) / 2 : 0,
				horizontal ? 0 : (sign * (newSize - oldSize)) / 2,
				overlay.rotation ?? 0
			);

			const width = horizontal ? newSize : overlay.width;
			const height = horizontal ? overlay.height : newSize;

			dispatch({
				id: gesture.id,
				patch: {
					height: Math.round(height),
					width: Math.round(width),
					x: Math.round(centerX + shiftX - width / 2),
					y: Math.round(centerY + shiftY - height / 2),
				},
				transient: true,
				type: 'update-overlay',
			});

			return;
		}

		// Corner resize, anchored at the center so the geometry stays
		// stable under rotation. Boxes resize freely by default and keep
		// their proportions with Shift, matching the crop; text scales
		// proportionally because its size is a single value.

		const scale = Math.max(
			Math.hypot(pointX - centerX, pointY - centerY) /
				gesture.startDistance,
			0.05
		);

		if (overlay.kind === 'text') {
			const fontSize = Math.max(Math.round(overlay.fontSize * scale), 8);

			// Keep the estimated text box centered while it scales.

			dispatch({
				id: gesture.id,
				patch: {
					fontSize,
					x:
						centerX -
						textWidth(overlay.text, overlay.fontFamily, fontSize) /
							2,
					y: centerY + 0.4 * fontSize,
				},
				transient: true,
				type: 'update-overlay',
			});
		}
		else if (isBoxOverlay(overlay)) {
			let width;
			let height;

			if (event.shiftKey || proportional) {
				width = Math.max(overlay.width * scale, 8);
				height = Math.max(overlay.height * scale, 8);
			}
			else {
				width = Math.max(Math.abs(pointX - centerX) * 2, 8);
				height = Math.max(Math.abs(pointY - centerY) * 2, 8);
			}

			dispatch({
				id: gesture.id,
				patch: {
					height: Math.round(height),
					width: Math.round(width),
					x: Math.round(centerX - width / 2),
					y: Math.round(centerY - height / 2),
				},
				transient: true,
				type: 'update-overlay',
			});
		}
	};

	const handleManipulationUp = () => {
		const gesture = manipGestureRef.current;

		if (!gesture) {
			return;
		}

		manipGestureRef.current = null;

		const overlay = current(gesture.id);

		if (overlay) {

			// Commit the whole gesture as one undo step.

			dispatch({
				id: gesture.id,
				patch:
					overlay.kind === 'arrow'
						? {
								dx: overlay.dx,
								dy: overlay.dy,
								x: overlay.x,
								y: overlay.y,
							}
						: {
								rotation: overlay.rotation,
								x: overlay.x,
								y: overlay.y,
							},
				type: 'update-overlay',
			});

			onAnnounce(
				sub(Liferay.Language.get('x-updated'), overlayLabel(overlay))
			);
		}
	};

	const handleProps = {
		onPointerCancel: cancelManipGesture,
		onPointerMove: handleManipulationMove,
		onPointerUp: handleManipulationUp,
		strokeWidth: 1.5 / zoom,
	};

	const handleSize = 10 / zoom;

	return (
		<g>
			<desc id={eid('overlay-instructions')}>
				{Liferay.Language.get(
					'use-the-arrow-keys-to-move-by-1-pixel-hold-shift-for-10-pixels-press-enter-to-edit-the-properties-delete-to-remove'
				)}
			</desc>

			{overlays.map((overlay) => {
				const bounds = overlayBounds(overlay);

				// 24 screen pixels, expressed in stage units so the target
				// keeps its size at any zoom.

				const hit = overlayHitBox(overlay, MINIMUM_TARGET / zoom);

				return (
					<g key={overlay.id} transform={overlayTransform(overlay)}>
						{editing?.id !== overlay.id && (
							<OverlayShape overlay={overlay} />
						)}

						{focus?.id === overlay.id ? (
							<FocusRing
								bounds={bounds}
								emphasis={focus.modality}
								zoom={zoom}
							/>
						) : (
							(selectedId === overlay.id ||
								multiSet.has(overlay.id)) && (
								<FocusRing
									bounds={bounds}
									emphasis="pointer"
									zoom={zoom}
								/>
							)
						)}

						<rect
							aria-describedby={eid('overlay-instructions')}
							aria-label={overlayLabel(overlay)}
							className="overlay-hit"
							data-overlay-id={overlay.id}
							fill="transparent"
							height={hit.height}
							onBlur={() => setFocus(null)}
							onDoubleClick={
								overlay.kind === 'text'
									? () =>
											setEditing({
												draft: overlay.text,
												id: overlay.id,
											})
									: undefined
							}
							onFocus={(event) => {
								onSelect(overlay.id);

								setFocus({
									id: overlay.id,
									modality: matchesFocusVisible(
										event.currentTarget
									)
										? 'keyboard'
										: 'pointer',
								});
							}}
							onKeyDown={handleKeyDown(overlay.id)}
							onKeyUp={handleKeyUp(overlay.id)}
							onLostPointerCapture={cancelPointerGesture}
							onPointerCancel={cancelPointerGesture}
							onPointerDown={handlePointerDown(overlay.id)}
							onPointerMove={handlePointerMove}
							onPointerUp={handlePointerUp}
							role="button"
							tabIndex={0}
							width={hit.width}
							x={hit.x}
							y={hit.y}
						/>

						{editing?.id === overlay.id &&
							overlay.kind === 'text' && (
								<OverlayTextEditor
									bounds={bounds}
									draft={editing.draft}
									onCancel={finishTextEdit}
									onChange={(draft) =>
										setEditing({draft, id: overlay.id})
									}
									onCommit={commitTextEdit}
									overlay={overlay}
								/>
							)}

						{editing?.id !== overlay.id &&
							multiSet.size <= 1 &&
							(selectedId === overlay.id ||
								focus?.id === overlay.id) && (
								<g
									aria-hidden="true"
									className="object-handles"
								>
									{overlay.kind === 'arrow' ? (
										ARROW_ENDS.map((end) => {
											const arrow =
												overlay as ArrowOverlay;

											const handleX =
												arrow.x +
												(end === 'tip' ? arrow.dx : 0);
											const handleY =
												arrow.y +
												(end === 'tip' ? arrow.dy : 0);

											return (
												<circle
													{...handleProps}
													className="object-handle"
													cx={handleX}
													cy={handleY}
													key={end}
													onPointerDown={startManipulation(
														overlay,
														'endpoint',
														handleX,
														handleY,
														undefined,
														end
													)}
													r={6 / zoom}
													style={{cursor: 'move'}}
												/>
											);
										})
									) : (
										<>
											{[
												...RESIZE_CORNERS,
												...(isBoxOverlay(overlay) &&
												!proportional
													? STRETCH_EDGES
													: []),
											].map(
												({
													cursor,
													edge,
													name,
													x,
													y,
												}) => {
													const handleX =
														bounds.x +
														x * bounds.width;
													const handleY =
														bounds.y +
														y * bounds.height;

													return (
														<rect
															{...handleProps}
															className="object-handle"
															height={handleSize}
															key={name}
															onPointerDown={startManipulation(
																overlay,
																'resize',
																handleX,
																handleY,
																edge
															)}
															style={{cursor}}
															width={handleSize}
															x={
																handleX -
																handleSize / 2
															}
															y={
																handleY -
																handleSize / 2
															}
														/>
													);
												}
											)}

											<line
												className="object-rotate-stick"
												strokeWidth={1.5 / zoom}
												x1={bounds.x + bounds.width / 2}
												x2={bounds.x + bounds.width / 2}
												y1={bounds.y}
												y2={bounds.y - 24 / zoom}
											/>

											<circle
												{...handleProps}
												className="object-handle object-handle-rotate"
												cx={bounds.x + bounds.width / 2}
												cy={bounds.y - 24 / zoom}
												onPointerDown={startManipulation(
													overlay,
													'rotate',
													bounds.x + bounds.width / 2,
													bounds.y - 24 / zoom
												)}
												r={6 / zoom}
											/>
										</>
									)}
								</g>
							)}
					</g>
				);
			})}
		</g>
	);
}
