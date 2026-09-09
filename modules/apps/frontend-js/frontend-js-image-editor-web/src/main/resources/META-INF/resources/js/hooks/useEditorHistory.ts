/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useReducer, useRef} from 'react';

import {t} from '../i18n';
import {LoadedImage} from '../imaging/loadImage';
import {
	editorReducer,
	initialHistory,
	redoLabel,
	undoLabel,
} from '../state/editorReducer';

export function useEditorHistory(
	image: LoadedImage,
	announce: (message: string) => void,

	frozen?: () => boolean
) {
	const [history, dispatch] = useReducer(editorReducer, undefined, () =>
		initialHistory(image.width, image.height)
	);

	const undo = () => {
		const label = undoLabel(history);

		if (!label || frozen?.()) {
			return;
		}

		dispatch({type: 'undo'});

		announce(t('undo-x', label));
	};

	const redo = () => {
		const label = redoLabel(history);

		if (!label || frozen?.()) {
			return;
		}

		dispatch({type: 'redo'});

		announce(t('redo-x', label));
	};

	const handleUndoShortcut = (event: React.KeyboardEvent) => {
		if (
			!(event.metaKey || event.ctrlKey) ||
			event.key.toLowerCase() !== 'z'
		) {
			return;
		}

		event.preventDefault();

		if (event.shiftKey) {
			redo();
		}
		else {
			undo();
		}
	};

	const editorRef = useRef<HTMLDivElement | null>(null);

	return {dispatch, editorRef, handleUndoShortcut, history, redo, undo};
}
