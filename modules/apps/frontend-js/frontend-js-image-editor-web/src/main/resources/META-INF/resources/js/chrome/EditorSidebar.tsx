/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../css/Panels.scss';

import React from 'react';

import {AnnotatePanel} from '../annotations/AnnotatePanel';
import {LayersPanel} from '../annotations/LayersPanel';
import {AnnotateTool} from '../editorConfig';
import {LoadedImage} from '../imaging/loadImage';
import {AdjustPanel} from '../panels/AdjustPanel';
import {CropPanel} from '../panels/CropPanel';
import {FilterGallery} from '../panels/FilterGallery';
import {FramePanel} from '../panels/FramePanel';
import {EditorAction} from '../state/editorReducer';
import {
	AdjustmentKey,
	EditState,
	FilterPreset,
	FrameKind,
	rotatedSize,
} from '../state/types';

interface Props {
	aspectLocked: boolean;

	dispatch: (action: EditorAction) => void;
	frames: FrameKind[];
	image: LoadedImage;

	multiSelectedIds: string[];
	onAnnounce: (message: string) => void;
	onAspectLockedChange: (locked: boolean) => void;
	onProportionalChange: (proportional: boolean) => void;

	onSelectOverlay: (id: string | null) => void;

	presets: FilterPreset[];

	proportional: boolean;

	selectedOverlayId: string | null;
	showCrop: boolean;
	showStraighten: boolean;
	sidebarRef: React.Ref<HTMLElement>;
	sliders: AdjustmentKey[];
	state: EditState;
	tools: AnnotateTool[];
}

export function EditorSidebar({
	aspectLocked,
	dispatch,
	frames,
	image,
	multiSelectedIds,
	onAnnounce,
	onAspectLockedChange,
	onProportionalChange,
	onSelectOverlay,
	presets,
	proportional,
	selectedOverlayId,
	showCrop,
	showStraighten,
	sidebarRef,
	sliders,
	state,
	tools,
}: Props) {
	return (
		<aside
			aria-label={Liferay.Language.get('edit-controls')}
			className="editor-sidebar"
			ref={sidebarRef}
		>
			{showCrop && (
				<CropPanel
					angle={state.angle}
					aspectLocked={aspectLocked}
					bounds={rotatedSize(state)}
					crop={state.crop}
					dispatch={dispatch}
					onAnnounce={onAnnounce}
					onAspectLockedChange={onAspectLockedChange}
					showStraighten={showStraighten}
				/>
			)}

			{!!sliders.length && (
				<AdjustPanel
					adjustments={state.adjustments}
					dispatch={dispatch}
					onAnnounce={onAnnounce}
					sliders={sliders}
				/>
			)}

			{!!presets.length && (
				<FilterGallery
					dispatch={dispatch}
					filter={state.filter}
					image={image}
					onAnnounce={onAnnounce}
					presets={presets}
				/>
			)}

			{!!frames.length && (
				<FramePanel
					dispatch={dispatch}
					frame={state.frame}
					image={image}
					onAnnounce={onAnnounce}
					presets={frames}
				/>
			)}

			{!!tools.length && (
				<>
					<AnnotatePanel
						area={state.crop}
						dispatch={dispatch}
						onAnnounce={onAnnounce}
						tools={tools}
					/>

					<LayersPanel
						dispatch={dispatch}
						multiSelectedIds={multiSelectedIds}
						onAnnounce={onAnnounce}
						onProportionalChange={onProportionalChange}
						onSelect={onSelectOverlay}
						overlays={state.overlays}
						proportional={proportional}
						selectedId={selectedOverlayId}
					/>
				</>
			)}
		</aside>
	);
}
