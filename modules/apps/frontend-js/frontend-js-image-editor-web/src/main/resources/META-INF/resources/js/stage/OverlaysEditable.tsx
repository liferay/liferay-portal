/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {useEditorId} from '../chrome/instance';
import {arrowDelta} from '../imaging/geometry';
import {
	OverlayShape,
	overlayBounds,
	overlayHitBox,
	overlayLabel,
	overlayTransform,
} from '../imaging/overlayShapes';
import {EditorAction} from '../state/editorReducer';
import {Overlay} from '../state/types';
import {FocusModality, FocusRing, matchesFocusVisible} from './FocusRing';
import {OverlayTextEditor} from './OverlayTextEditor';

const MINIMUM_TARGET = 24;

interface Props {
	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;
	onSelect: (id: string | null) => void;
	overlays: Overlay[];
	selectedId: string | null;
	zoom: number;
}

export function OverlaysEditable({
	dispatch,
	onAnnounce,
	onSelect,
	overlays,
	selectedId,
	zoom,
}: Props) {
	const eid = useEditorId();

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

	const [editing, setEditing] = useState<{
		draft: string;
		id: string;
	} | null>(null);

	const current = (id: string) =>
		overlaysRef.current.find((overlay) => overlay.id === id);

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
			if (event.key === 'Delete' || event.key === 'Backspace') {
				event.preventDefault();

				// The node under focus is about to unmount, and focus
				// falling to the body would take the undo shortcut down
				// with it: the workspace inherits it instead.

				const workspace = (
					event.currentTarget as SVGElement
				).closest<HTMLElement>('.editor-workspace');

				window.setTimeout(() => workspace?.focus(), 0);

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

	// Unmounting mid-drag (a panel switch, the host closing the editor)
	// must not leave a transient half-applied under the next commit.

	useEffect(() => {
		return () => {
			if (pointerGestureRef.current) {
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

	const commitTextEdit = () => {
		if (!editing) {
			return;
		}

		const overlay = current(editing.id);
		const value = editing.draft.trim();

		setEditing(null);

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

	return (
		<g>
			<desc id={eid('overlay-instructions')}>
				{Liferay.Language.get(
					'use-the-arrow-keys-to-move-by-1-pixel-hold-shift-for-10-pixels-press-delete-to-remove'
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
							selectedId === overlay.id && (
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
									onCancel={() => setEditing(null)}
									onChange={(draft) =>
										setEditing({draft, id: overlay.id})
									}
									onCommit={commitTextEdit}
									overlay={overlay}
								/>
							)}
					</g>
				);
			})}
		</g>
	);
}
