/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../css/Panels.scss';

import React from 'react';

import {CropPanel} from '../panels/CropPanel';
import {EditorAction} from '../state/editorReducer';
import {EditState, rotatedSize} from '../state/types';

interface Props {
	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;
	state: EditState;
}

export function EditorSidebar({dispatch, onAnnounce, state}: Props) {
	return (
		<aside
			aria-label={Liferay.Language.get('edit-controls')}
			className="editor-sidebar"
		>
			<CropPanel
				bounds={rotatedSize(state)}
				crop={state.crop}
				dispatch={dispatch}
				onAnnounce={onAnnounce}
			/>
		</aside>
	);
}
