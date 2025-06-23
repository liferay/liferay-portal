/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TEditor} from 'frontend-editor-ckeditor-web';

export default function BlockButtonCustomization(editor: TEditor) {
	let button: HTMLElement | null = null;

	const setTitle = () => {
		button!.dataset.ckeTooltipText = Liferay.Language.get('add');
	};

	editor.on('ready', () => {
		button = document.querySelector('.ck-block-toolbar-button');

		if (button) {
			setTitle();

			button.setAttribute('draggable', 'false');

			const label = button.querySelector('.ck-button__label');

			if (label) {
				label.innerHTML = Liferay.Language.get('add');
			}

			// Set this event because opening and closing the toolbar with
			// the button causes a re-render, which resets the title.

			button.addEventListener('click', setTitle);
		}
	});

	// Set this event because changes in the editor cause the button to
	// re-render and lose its title.

	editor.model.document.on('change:data', () => {
		if (button) {
			setTitle();
		}
	});

	editor.on('destroy', () => {
		button?.removeEventListener('click', setTitle);

		editor.model.document.off('change:data', setTitle);
	});
}
