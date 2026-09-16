/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../css/Stage.scss';

import React from 'react';

import {useEditorId} from '../chrome/instance';
import {FilterDefs, isIdentityFilter} from '../imaging/FilterDefs';
import {FrameShape} from '../imaging/frameShapes';
import {imageTransform} from '../imaging/geometry';
import {LoadedImage} from '../imaging/loadImage';
import {
	DEFAULT_ANNOTATION_COLOR,
	redactSourceFor,
} from '../imaging/overlayShapes';
import {EditorAction} from '../state/editorReducer';
import {EditState, rotatedSize} from '../state/types';
import {CropMarquee} from './CropMarquee';
import {DrawResult, DrawSurface, strokeWidthFor} from './DrawSurface';
import {OverlaysEditable} from './OverlaysEditable';

interface Props {
	aspectLocked: boolean;

	dispatch: (action: EditorAction) => void;

	drawing?: {guided: boolean} | null;

	image: LoadedImage;

	multiSelectedIds: string[];
	onAnnounce: (message: string) => void;
	onCenterCrop: () => void;

	onCopyOverlay: (id: string) => void;

	onFinishDrawing?: (result: DrawResult | null) => void;

	onMultiSelectToggle: (id: string) => void;

	onPasteOverlay: () => void;

	onSelectOverlay: (id: string | null) => void;
	onWorkspacePointerLeave?: () => void;
	onWorkspacePointerMove?: (event: React.PointerEvent) => void;
	onWorkspaceScroll?: () => void;
	onZoom: (direction: -1 | 1) => void;
	onZoomActual: () => void;
	onZoomFit: () => void;

	proportional: boolean;

	selectedOverlayId: string | null;
	showCrop: boolean;
	showRecenter: boolean;
	state: EditState;
	workspaceRef?: React.Ref<HTMLDivElement>;
	zoom: number;
}

export function Workspace({
	aspectLocked,
	dispatch,
	drawing,
	image,
	multiSelectedIds,
	onAnnounce,
	onCenterCrop,
	onCopyOverlay,
	onFinishDrawing,
	onMultiSelectToggle,
	onPasteOverlay,
	onSelectOverlay,
	onWorkspacePointerLeave,
	onWorkspacePointerMove,
	onWorkspaceScroll,
	onZoom,
	onZoomActual,
	onZoomFit,
	proportional,
	selectedOverlayId,
	showCrop,
	showRecenter,
	state,
	workspaceRef,
	zoom,
}: Props) {
	const eid = useEditorId();

	const bounds = rotatedSize(state);
	const {crop} = state;

	const handleKeyDown = (event: React.KeyboardEvent) => {
		if (event.key === '+' || event.key === '=') {
			event.preventDefault();
			onZoom(1);
		}
		else if (event.key === '-' || event.key === '_') {
			event.preventDefault();
			onZoom(-1);
		}
		else if (event.key === '0') {
			event.preventDefault();
			onZoomFit();
		}
		else if (event.key === '1') {
			event.preventDefault();
			onZoomActual();
		}
		else if (event.key === '2') {
			event.preventDefault();
			onCenterCrop();
		}
		else if (
			(event.metaKey || event.ctrlKey) &&
			event.key.toLowerCase() === 'v'
		) {
			event.preventDefault();
			onPasteOverlay();
		}
	};

	return (
		<div
			aria-describedby={eid('workspace-description')}
			aria-label={Liferay.Language.get('image-workspace')}
			className="editor-workspace"
			onKeyDown={handleKeyDown}
			onPointerDown={(event) => {
				if (
					!(event.target as Element).closest(
						'.overlay-hit, .object-handles, .overlay-text-editor'
					)
				) {
					onSelectOverlay(null);
				}
			}}
			onPointerLeave={onWorkspacePointerLeave}
			onPointerMove={onWorkspacePointerMove}
			onScroll={onWorkspaceScroll}
			ref={workspaceRef}
			role="region"
			tabIndex={0}
		>
			<span className="sr-only" id={eid('workspace-description')}>
				{Liferay.Language.get(
					'scrollable-view-of-the-image-use-the-zoom-buttons-or-plus-and-minus-keys-to-zoom-tab-to-reach-the-crop-area-and-its-handles'
				)}
			</span>

			<svg
				className="editor-stage"
				height={bounds.height * zoom}
				viewBox={`0 0 ${bounds.width} ${bounds.height}`}
				width={bounds.width * zoom}
			>
				<defs>

					{/*
					 * A straighten angle scales the image up, so it
					 * spills past the stage: clip it to the image area
					 * to keep the surrounding padding clean.
					 */}

					<clipPath id={eid('stage-clip')}>
						<rect
							height={bounds.height}
							width={bounds.width}
							x={0}
							y={0}
						/>
					</clipPath>

					<FilterDefs
						adjustments={state.adjustments}
						filter={state.filter}
						id={eid('preview-filter')}
					/>
				</defs>

				<g
					clipPath={

						// Only needed while straightening, and clipping a
						// 20MP-derived bitmap is not free.

						state.angle ? `url(#${eid('stage-clip')})` : undefined
					}
				>
					<g transform={imageTransform(state)}>
						<image
							filter={
								isIdentityFilter(
									state.adjustments,
									state.filter
								)
									? undefined
									: `url(#${eid('preview-filter')})`
							}
							height={state.sourceHeight}
							href={image.previewUrl}
							preserveAspectRatio="none"
							width={state.sourceWidth}
						/>
					</g>
				</g>

				<CropMarquee
					aspectLocked={aspectLocked}
					bounds={bounds}
					crop={crop}
					dispatch={dispatch}
					onAnnounce={onAnnounce}
					onCenterCrop={onCenterCrop}
					showCrop={showCrop}
					showRecenter={showRecenter}
					zoom={zoom}
				>

					{/*
					 * Under the annotations when asked: a mat that covers
					 * the caption written along the bottom edge is a real
					 * outcome, and which one is wanted is the user's call.
					 */}

					{!state.frame.overAnnotations && (
						<FrameShape crop={crop} frame={state.frame} />
					)}

					<OverlaysEditable
						dispatch={dispatch}
						multiSelectedIds={multiSelectedIds}
						onAnnounce={onAnnounce}
						onCopy={onCopyOverlay}
						onMultiSelectToggle={onMultiSelectToggle}
						onSelect={onSelectOverlay}
						overlays={state.overlays}
						proportional={proportional}
						redactSource={redactSourceFor(state, {
							filterId: eid('preview-filter'),
							imageUrl: image.previewUrl,
							pixelUrls: image.pixelUrls,
						})}
						selectedId={selectedOverlayId}
						zoom={zoom}
					/>

					{/*
					 * Above the marquee is never right: the marquee is
					 * chrome, the frame is picture.
					 */}

					{state.frame.overAnnotations && (
						<FrameShape crop={crop} frame={state.frame} />
					)}

					{/*
					 * The drawing surface rides above everything while it
					 * lasts, because while drawing, drawing is the mode.
					 */}

					{drawing && onFinishDrawing && (
						<DrawSurface
							area={crop}
							color={DEFAULT_ANNOTATION_COLOR}
							guided={drawing.guided}
							onAnnounce={onAnnounce}
							onFinish={onFinishDrawing}
							width={strokeWidthFor(crop)}
							zoom={zoom}
						/>
					)}
				</CropMarquee>
			</svg>
		</div>
	);
}
