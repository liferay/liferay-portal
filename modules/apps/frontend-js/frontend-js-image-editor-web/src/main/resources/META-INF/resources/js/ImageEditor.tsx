/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../css/ImageEditor.scss';

import {ClayIconSpriteContext} from '@clayui/icon';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {AnnouncerProvider, useAnnouncer} from './chrome/Announcer';
import {BottomBar} from './chrome/BottomBar';
import {EditorSidebar} from './chrome/EditorSidebar';
import {ShortcutsDialog} from './chrome/ShortcutsDialog';
import {
	EditorInstanceProvider,
	nextEditorInstancePrefix,
} from './chrome/instance';
import {useEditorHistory} from './hooks/useEditorHistory';
import {useSaveController} from './hooks/useSaveController';
import {anchoredScroll} from './imaging/geometry';
import {LoadedImage} from './imaging/loadImage';
import {Workspace} from './stage/Workspace';
import {redoLabel, undoLabel} from './state/editorReducer';
import {CropRect, EditState, rotatedSize} from './state/types';

const STAGE_PADDING = 48;

const ZOOM_LEVELS = [0.05, 0.1, 0.15, 0.25, 0.35, 0.5, 0.75, 1, 1.5, 2, 3];

const MAX_ZOOM = ZOOM_LEVELS[ZOOM_LEVELS.length - 1];

export interface EditorSaveResult {
	blob: Blob;
	fileName: string;
	state: EditState;
}

export interface ImageEditorProps {
	image: LoadedImage;
	onClose: () => void;

	onSave: (
		result: EditorSaveResult,
		signal: AbortSignal
	) => Promise<void> | void;

	spritemap: string;
}

export function ImageEditor({
	image,
	onClose,
	onSave,
	spritemap,
}: ImageEditorProps) {
	return (
		<ClayIconSpriteContext.Provider value={spritemap}>
			<AnnouncerProvider>
				<Editor
					image={image}
					key={image.previewUrl}
					onClose={onClose}
					onSave={onSave}
				/>
			</AnnouncerProvider>
		</ClayIconSpriteContext.Provider>
	);
}

function Editor({image, onClose, onSave}: Omit<ImageEditorProps, 'spritemap'>) {
	const [instancePrefix] = useState(nextEditorInstancePrefix);

	const announce = useAnnouncer();

	const savingRef = useRef(false);

	const {dispatch, editorRef, handleUndoShortcut, history, redo, undo} =
		useEditorHistory(image, announce, () => savingRef.current);

	const state = history.present;

	const {handleSave, saveError, saving} = useSaveController(
		image,
		state,
		onSave,
		announce,
		onClose
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

	const [shortcutsOpen, setShortcutsOpen] = useState(false);

	const [aspectLocked, setAspectLocked] = useState(false);

	const [cropFramed, setCropFramed] = useState(false);

	const programmaticScrollRef = useRef(false);

	const workspaceRef = useRef<HTMLDivElement | null>(null);

	const autoFitRef = useRef(true);

	const stageBoundsRef = useRef(rotatedSize(state));

	useEffect(() => {
		stageBoundsRef.current = rotatedSize(state);
	});

	useEffect(() => {
		announce(
			sub(
				Liferay.Language.get(
					'the-image-editor-was-opened-the-image-is-x-by-x-pixels'
				),
				image.width,
				image.height
			)
		);
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

	useEffect(() => setCropFramed(false), [state.crop]);

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
		announce(
			sub(Liferay.Language.get('zoom-x-percent'), Math.round(level * 100))
		);

	const zoomBy = (direction: -1 | 1) => {
		autoFitRef.current = false;

		setCropFramed(false);

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

		setCropFramed(false);
		setZoom(1);

		announceZoom(1);
	};

	const zoomToFit = () => {
		autoFitRef.current = true;

		setCropFramed(false);

		const bounds = rotatedSize(state);

		const next = fitZoom(workspaceRef.current, bounds.width, bounds.height);

		setZoom(next);
		announceZoom(next);
	};

	const pendingCenterRef = useRef<{crop: CropRect; zoom: number} | null>(
		null
	);

	const scrollCropToCenter = (crop: CropRect, level: number) => {
		const element = workspaceRef.current;

		if (!element) {
			return;
		}

		programmaticScrollRef.current = true;

		element.scrollLeft =
			STAGE_PADDING / 2 +
			(crop.x + crop.width / 2) * level -
			element.clientWidth / 2;
		element.scrollTop =
			STAGE_PADDING / 2 +
			(crop.y + crop.height / 2) * level -
			element.clientHeight / 2;
	};

	useEffect(() => {
		const pending = pendingCenterRef.current;

		if (pending && pending.zoom === zoom) {
			pendingCenterRef.current = null;

			scrollCropToCenter(pending.crop, zoom);
		}

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

			programmaticScrollRef.current = true;

			element.scrollLeft = scroll.left;
			element.scrollTop = scroll.top;
		}
	});

	const centerCrop = () => {
		autoFitRef.current = false;

		const element = workspaceRef.current;

		if (!element) {
			return;
		}

		const {crop} = state;

		const next = fitZoom(element, crop.width, crop.height, MAX_ZOOM);

		if (next === zoom) {
			scrollCropToCenter(crop, next);
		}
		else {
			pendingCenterRef.current = {crop, zoom: next};

			setZoom(next);
		}

		setCropFramed(true);

		announce(
			sub(
				Liferay.Language.get(
					'crop-centered-in-the-view-at-x-percent-zoom'
				),
				Math.round(next * 100)
			)
		);
	};

	return (
		<EditorInstanceProvider value={instancePrefix}>
			<div
				className="image-editor"
				onKeyDown={handleUndoShortcut}
				ref={editorRef}
			>
				<div className="editor-main">
					<Workspace
						aspectLocked={aspectLocked}
						dispatch={dispatch}
						image={image}
						onAnnounce={announce}
						onCenterCrop={centerCrop}
						onWorkspacePointerLeave={handleWorkspacePointerLeave}
						onWorkspacePointerMove={handleWorkspacePointerMove}
						onWorkspaceScroll={() => {
							if (programmaticScrollRef.current) {
								programmaticScrollRef.current = false;
							}
							else {
								setCropFramed(false);
							}
						}}
						onZoom={zoomBy}
						onZoomActual={zoomToActual}
						onZoomFit={zoomToFit}
						showRecenter={!cropFramed}
						state={state}
						workspaceRef={handleWorkspaceRef}
						zoom={zoom}
					/>

					<EditorSidebar
						aspectLocked={aspectLocked}
						dispatch={dispatch}
						onAnnounce={announce}
						onAspectLockedChange={setAspectLocked}
						state={state}
					/>
				</div>

				{saveError && (
					<div
						className="alert alert-danger editor-save-error"
						role="alert"
					>
						{Liferay.Language.get(
							'unable-to-save-the-image-please-try-again'
						)}
					</div>
				)}

				<BottomBar
					canRedo={!!redoLabel(history)}
					canUndo={!!undoLabel(history)}
					dispatch={dispatch}
					onAnnounce={announce}
					onCancel={onClose}
					onRedo={redo}
					onSave={handleSave}
					onShowShortcuts={() => setShortcutsOpen(true)}
					onUndo={undo}
					onZoom={zoomBy}
					onZoomFit={zoomToFit}
					ratio={state.ratio}
					saving={saving}
					zoom={zoom}
				/>
			</div>

			<ShortcutsDialog
				onOpenChange={setShortcutsOpen}
				open={shortcutsOpen}
			/>
		</EditorInstanceProvider>
	);
}

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
