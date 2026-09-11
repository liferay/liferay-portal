/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../css/Panels.scss';

import React from 'react';

import {AdjustPanel} from '../panels/AdjustPanel';
import {CropPanel} from '../panels/CropPanel';
import {EditorAction} from '../state/editorReducer';
import {EditState, rotatedSize} from '../state/types';

interface Props {
	aspectLocked: boolean;

	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;
	onAspectLockedChange: (locked: boolean) => void;
	showCrop: boolean;
	showStraighten: boolean;
	state: EditState;
}

export function EditorSidebar({
	aspectLocked,
	dispatch,
	onAnnounce,
	onAspectLockedChange,
	showCrop,
	showStraighten,
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

			<AdjustPanel
				adjustments={state.adjustments}
				dispatch={dispatch}
				onAnnounce={onAnnounce}
			/>
		</aside>
	);
}
