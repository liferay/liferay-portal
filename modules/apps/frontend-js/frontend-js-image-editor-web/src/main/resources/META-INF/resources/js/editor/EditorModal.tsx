/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal, {useModal} from '@clayui/modal';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {useAnnouncer} from '../chrome/Announcer';
import {BottomBar} from '../chrome/BottomBar';
import {
	EditorInstanceProvider,
	nextEditorInstancePrefix,
} from '../chrome/instance';
import {useEditorHistory} from '../hooks/useEditorHistory';
import {useSaveController} from '../hooks/useSaveController';
import {t} from '../i18n';
import {anchoredScroll} from '../imaging/geometry';
import {LoadedImage} from '../imaging/loadImage';
import {Workspace} from '../stage/Workspace';
import {redoLabel, undoLabel} from '../state/editorReducer';
import {EditState, rotatedSize} from '../state/types';

const ZOOM_LEVELS = [0.05, 0.1, 0.15, 0.25, 0.35, 0.5, 0.75, 1, 1.5, 2, 3];

const STAGE_PADDING = 48;

function fitZoom(
	workspace: HTMLElement | null,
	width: number,
	height: number,
	max = 1
): number {
	const availableWidth = workspace
		? workspace.clientWidth - STAGE_PADDING
		: Math.max(window.innerWidth - 360, 240);
	const availableHeight = workspace
		? workspace.clientHeight - STAGE_PADDING
		: Math.max(window.innerHeight - 200, 240);

	const fit = Math.min(availableWidth / width, availableHeight / height, max);

	return Math.max(Math.floor(fit * 100) / 100, 0.01);
}

function stepZoom(zoom: number, direction: -1 | 1): number {
	if (direction === 1) {
		return ZOOM_LEVELS.find((level) => level > zoom + 1e-6) ?? zoom;
	}

	const smaller = ZOOM_LEVELS.filter((level) => level < zoom - 1e-6);

	return smaller.length ? smaller[smaller.length - 1] : zoom;
}

export interface EditorSaveResult {
	blob: Blob;
	fileName: string;
	state: EditState;
}

interface Props {
	image: LoadedImage;
	onClose: () => void;

	onSave: (
		result: EditorSaveResult,
		signal: AbortSignal
	) => Promise<void> | void;
}

export default function EditorModal({image, onClose, onSave}: Props) {
	const [instancePrefix] = useState(nextEditorInstancePrefix);

	const announce = useAnnouncer();

	const {observer, onClose: closeModal} = useModal({onClose});

	const savingRef = useRef(false);

	const {dispatch, editorRef, handleUndoShortcut, history, redo, undo} =
		useEditorHistory(image, announce, () => savingRef.current);

	const state = history.present;

	const {handleSave, saveError, saving} = useSaveController(
		image,
		state,
		onSave,
		announce,
		closeModal
	);

	useEffect(() => {
		savingRef.current = saving;
	});

	useEffect(() => {
		editorRef.current?.toggleAttribute('inert', saving);
	}, [saving, editorRef]);

	const [zoom, setZoom] = useState(() =>
		fitZoom(null, image.width, image.height)
	);

	const workspaceRef = useRef<HTMLDivElement | null>(null);

	const autoFitRef = useRef(true);

	const stageBoundsRef = useRef(rotatedSize(state));

	useEffect(() => {
		stageBoundsRef.current = rotatedSize(state);
	});

	useEffect(() => {
		announce(t('editor-loaded', image.width, image.height));
	}, [announce, image]);

	const resizeObserverRef = useRef<ResizeObserver | null>(null);

	const handleWorkspaceRef = useCallback((element: HTMLDivElement | null) => {
		resizeObserverRef.current?.disconnect();
		resizeObserverRef.current = null;

		workspaceRef.current = element;

		if (!element) {
			return;
		}

		const observer = new ResizeObserver(() => {
			if (autoFitRef.current) {
				setZoom(
					fitZoom(
						element,
						stageBoundsRef.current.width,
						stageBoundsRef.current.height
					)
				);
			}
		});

		observer.observe(element);

		resizeObserverRef.current = observer;
	}, []);

	useEffect(() => {
		if (autoFitRef.current && workspaceRef.current) {
			setZoom(
				fitZoom(
					workspaceRef.current,
					stageBoundsRef.current.width,
					stageBoundsRef.current.height
				)
			);
		}
	}, [state.rotation]);

	const pointerRef = useRef<{x: number; y: number} | null>(null);

	const handleWorkspacePointerMove = (event: React.PointerEvent) => {
		const element = workspaceRef.current;

		if (!element) {
			return;
		}

		const rect = element.getBoundingClientRect();

		pointerRef.current = {
			x: event.clientX - rect.left,
			y: event.clientY - rect.top,
		};
	};

	const handleWorkspacePointerLeave = () => {
		pointerRef.current = null;
	};

	const pendingAnchorRef = useRef<{
		anchor: {x: number; y: number};
		from: number;
		scroll: {left: number; top: number};
		zoom: number;
	} | null>(null);

	const announceZoom = (level: number) =>
		announce(t('zoom-level', Math.round(level * 100)));

	const zoomBy = (direction: -1 | 1) => {
		autoFitRef.current = false;

		const next = stepZoom(zoom, direction);

		if (next === zoom) {
			return;
		}

		const element = workspaceRef.current;

		if (element) {
			const pointer = pointerRef.current;

			const inside =
				pointer &&
				pointer.x >= 0 &&
				pointer.y >= 0 &&
				pointer.x <= element.clientWidth &&
				pointer.y <= element.clientHeight;

			pendingAnchorRef.current = {
				anchor:
					inside && pointer
						? pointer
						: {
								x: element.clientWidth / 2,
								y: element.clientHeight / 2,
							},
				from: zoom,
				scroll: {left: element.scrollLeft, top: element.scrollTop},
				zoom: next,
			};
		}

		setZoom(next);
		announceZoom(next);
	};

	const zoomToActual = () => {
		autoFitRef.current = false;

		setZoom(1);

		announceZoom(1);
	};

	const zoomToFit = () => {
		autoFitRef.current = true;

		const bounds = rotatedSize(state);

		const next = fitZoom(workspaceRef.current, bounds.width, bounds.height);

		setZoom(next);
		announceZoom(next);
	};

	useEffect(() => {
		const anchored = pendingAnchorRef.current;
		const element = workspaceRef.current;

		if (anchored && anchored.zoom === zoom && element) {
			pendingAnchorRef.current = null;

			const scroll = anchoredScroll({
				anchor: anchored.anchor,
				next: zoom,
				padding: STAGE_PADDING,
				scroll: anchored.scroll,
				zoom: anchored.from,
			});

			element.scrollLeft = scroll.left;
			element.scrollTop = scroll.top;
		}
	});

	return (
		<EditorInstanceProvider value={instancePrefix}>
			<ClayModal
				className="image-editor-modal"
				observer={observer}
				size="full-screen"
			>
				<ClayModal.Header closeButtonAriaLabel={t('close')} withTitle>
					{t('editing-image')}
				</ClayModal.Header>

				<div
					className="image-editor"
					onKeyDown={handleUndoShortcut}
					ref={editorRef}
				>
					<div className="editor-main">
						<Workspace
							image={image}
							onWorkspacePointerLeave={
								handleWorkspacePointerLeave
							}
							onWorkspacePointerMove={handleWorkspacePointerMove}
							onZoom={zoomBy}
							onZoomActual={zoomToActual}
							onZoomFit={zoomToFit}
							state={state}
							workspaceRef={handleWorkspaceRef}
							zoom={zoom}
						/>
					</div>

					{saveError && (
						<div
							className="alert alert-danger editor-save-error"
							role="alert"
						>
							{t('save-failed')}
						</div>
					)}

					<BottomBar
						canRedo={!!redoLabel(history)}
						canUndo={!!undoLabel(history)}
						dispatch={dispatch}
						onAnnounce={announce}
						onCancel={closeModal}
						onRedo={redo}
						onSave={handleSave}
						onUndo={undo}
						onZoom={zoomBy}
						onZoomFit={zoomToFit}
						saving={saving}
						zoom={zoom}
					/>
				</div>
			</ClayModal>
		</EditorInstanceProvider>
	);
}
