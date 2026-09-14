/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../css/Panels.scss';

import React from 'react';

import {LoadedImage} from '../imaging/loadImage';
import {AdjustPanel} from '../panels/AdjustPanel';
import {CropPanel} from '../panels/CropPanel';
import {FilterGallery} from '../panels/FilterGallery';
import {EditorAction} from '../state/editorReducer';
import {
	AdjustmentKey,
	EditState,
	FilterPreset,
	rotatedSize,
} from '../state/types';

interface Props {
	aspectLocked: boolean;

	dispatch: (action: EditorAction) => void;
	image: LoadedImage;
	onAnnounce: (message: string) => void;
	onAspectLockedChange: (locked: boolean) => void;
	presets: FilterPreset[];
	showCrop: boolean;
	showStraighten: boolean;
	sliders: AdjustmentKey[];
	state: EditState;
}

export function EditorSidebar({
	aspectLocked,
	dispatch,
	image,
	onAnnounce,
	onAspectLockedChange,
	presets,
	showCrop,
	showStraighten,
	sliders,
	state,
}: Props) {
	return (
		<aside
			aria-label={Liferay.Language.get('edit-controls')}
			className="editor-sidebar"
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
		</aside>
	);
}
