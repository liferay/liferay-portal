/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../css/Stage.scss';

import React from 'react';

import {useEditorId} from '../chrome/instance';
import {imageTransform} from '../imaging/geometry';
import {LoadedImage} from '../imaging/loadImage';
import {EditorAction} from '../state/editorReducer';
import {EditState, rotatedSize} from '../state/types';
import {CropMarquee} from './CropMarquee';

interface Props {
	aspectLocked: boolean;

	dispatch: (action: EditorAction) => void;
	image: LoadedImage;
	onAnnounce: (message: string) => void;
	onCenterCrop: () => void;
	onWorkspacePointerLeave?: () => void;
	onWorkspacePointerMove?: (event: React.PointerEvent) => void;
	onWorkspaceScroll?: () => void;
	onZoom: (direction: -1 | 1) => void;
	onZoomActual: () => void;
	onZoomFit: () => void;
	showCrop: boolean;
	showRecenter: boolean;
	state: EditState;
	workspaceRef?: React.Ref<HTMLDivElement>;
	zoom: number;
}

export function Workspace({
	aspectLocked,
	dispatch,
	image,
	onAnnounce,
	onCenterCrop,
	onWorkspacePointerLeave,
	onWorkspacePointerMove,
	onWorkspaceScroll,
	onZoom,
	onZoomActual,
	onZoomFit,
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
	};

	return (
		<div
			aria-describedby={eid('workspace-description')}
			aria-label={Liferay.Language.get('image-workspace')}
			className="editor-workspace"
			onKeyDown={handleKeyDown}
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
				/>
			</svg>
		</div>
	);
}
