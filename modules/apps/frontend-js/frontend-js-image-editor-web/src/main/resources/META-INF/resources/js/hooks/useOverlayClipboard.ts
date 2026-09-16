/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import {useRef} from 'react';

import {overlayLabel} from '../imaging/overlayShapes';
import {focusOverlayNode} from '../stage/focusOverlayNode';
import {EditorAction} from '../state/editorReducer';
import {nextId} from '../state/ids';
import {EditState, Overlay} from '../state/types';

export function useOverlayClipboard(
	state: EditState,
	dispatch: (action: EditorAction) => void,
	onPasteSelect: (id: string) => void,
	announce: (message: string) => void,

	root: () => ParentNode
) {
	const clipboardRef = useRef<Overlay | null>(null);

	const copyOverlay = (id: string) => {
		const overlay = state.overlays.find((candidate) => candidate.id === id);

		if (!overlay) {
			return;
		}

		clipboardRef.current = {...overlay};

		announce(sub(Liferay.Language.get('x-copied'), overlayLabel(overlay)));
	};

	const pasteOverlay = () => {
		const copied = clipboardRef.current;

		if (!copied) {
			return;
		}

		const offset = Math.round(
			Math.max(16, Math.min(state.sourceWidth, state.sourceHeight) * 0.02)
		);

		const overlay: Overlay = {
			...copied,
			id: nextId(copied.kind),
			x: copied.x + offset,
			y: copied.y + offset,
		};

		clipboardRef.current = overlay;

		dispatch({overlay, type: 'add-overlay'});

		onPasteSelect(overlay.id);

		announce(sub(Liferay.Language.get('x-pasted'), overlayLabel(overlay)));

		focusOverlayNode(root, overlay.id);
	};

	return {copyOverlay, pasteOverlay};
}
