/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {useReducer, useRef} from 'react';

import {LoadedImage} from '../imaging/loadImage';
import {
	editorReducer,
	initialHistory,
	redoLabel,
	undoLabel,
} from '../state/editorReducer';
import {RatioPreset} from '../state/types';

export function useEditorHistory(
	image: LoadedImage,
	ratios: RatioPreset[],
	announce: (message: string) => void,

	frozen?: () => boolean
) {
	const [history, dispatch] = useReducer(editorReducer, undefined, () =>
		initialHistory(image.width, image.height, {ratios})
	);

	const editorRef = useRef<HTMLDivElement | null>(null);

	const undo = () => {
		const label = undoLabel(history);

		if (!label || frozen?.()) {
			return;
		}

		dispatch({type: 'undo'});

		announce(sub(Liferay.Language.get('undo-x'), label));
	};

	const redo = () => {
		const label = redoLabel(history);

		if (!label || frozen?.()) {
			return;
		}

		dispatch({type: 'redo'});

		announce(sub(Liferay.Language.get('redo-x'), label));
	};

	const handleUndoShortcut = (event: React.KeyboardEvent) => {
		if (
			!(event.metaKey || event.ctrlKey) ||
			event.key.toLowerCase() !== 'z' ||
			!editorRef.current?.contains(event.target as Node)
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

	return {dispatch, editorRef, handleUndoShortcut, history, redo, undo};
}
