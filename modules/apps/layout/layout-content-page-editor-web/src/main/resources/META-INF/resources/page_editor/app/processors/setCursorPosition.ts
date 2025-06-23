/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TEditor} from 'frontend-editor-ckeditor-web';

export type Position = {clientX: number; clientY: number};

export default function setCursorPosition(
	editor: TEditor,
	clickPosition: Position
) {
	let caretPosition = null;

	if (!clickPosition) {
		editor.execute('selectAll');

		return;
	}

	// @ts-ignore

	if (document.caretPositionFromPoint) {

		// @ts-ignore

		caretPosition = document.caretPositionFromPoint(
			clickPosition.clientX,
			clickPosition.clientY
		);
	}
	else if (document.caretRangeFromPoint) {
		const range = document.caretRangeFromPoint(
			clickPosition.clientX,
			clickPosition.clientY
		);

		if (!range) {
			return;
		}

		caretPosition = {
			offset: range.startOffset,
			offsetNode: range.startContainer,
		};
	}

	if (!caretPosition) {
		return;
	}

	// Convert DOM position to editor view position

	const viewPosition = editor.editing.view.domConverter.domPositionToView(
		caretPosition.offsetNode,
		caretPosition.offset
	);

	if (!viewPosition) {
		return;
	}

	// Convert editor view position to editor model position

	editor.model.change((writer) => {
		const modelPosition =
			editor.editing.mapper.toModelPosition(viewPosition);

		writer.setSelection(modelPosition);

		editor.editing.view.focus();
	});
}
