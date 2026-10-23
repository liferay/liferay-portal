/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	EditorConfigTransformer,
	EditorTransformer,
	WordCount,
} from '@liferay/js-api/editor';

const editorConfigTransformer: EditorConfigTransformer<any> = (config) => {
	let displayEl: HTMLElement | null = null;

	return {
		...config,
		extraPlugins: [...(config.extraPlugins ?? []), WordCount],
		wordCount: {
			onUpdate: ({
				characters,
				words,
			}: {
				characters: number;
				words: number;
			}) => {
				if (!displayEl) {
					displayEl = document.createElement('div');
					displayEl.className = 'mt-1 text-secondary';
					displayEl.dataset.testid = 'word-count-container';

					document
						.querySelector('.ck-editor__editable')
						?.closest('.ck-editor')
						?.parentElement?.appendChild(displayEl);
				}

				displayEl.textContent = `Words: ${words} | Characters: ${characters}`;
			},
		},
	};
};

const editorTransformer: EditorTransformer<any> = {
	editorConfigTransformer,
};

export default editorTransformer;
