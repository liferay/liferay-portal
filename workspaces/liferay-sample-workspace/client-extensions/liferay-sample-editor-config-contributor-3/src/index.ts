/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Plugin} from '@ckeditor/ckeditor5-core/dist/index.js';
import {ButtonView} from '@ckeditor/ckeditor5-ui/dist/index.js';
import {
	EditorConfigTransformer,
	EditorTransformer,
} from '@liferay/js-api/editor';

const TIMESTAMP_ICON =
	'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path d="M10 1a9 9 0 1 0 9 9 9 9 0 0 0-9-9zm0 16a7 7 0 1 1 7-7 7 7 0 0 1-7 7z"/><path d="M10.5 5h-1v5.5l3.7 3.7.7-.7-3.4-3.4z"/></svg>';

class Timestamp extends Plugin {
	init() {
		const editor = this.editor;

		editor.ui.componentFactory.add('timestamp', () => {
			const button = new ButtonView();

			button.set({
				icon: TIMESTAMP_ICON,
				label: 'Timestamp',
				tooltip: true,
			});

			button.on('execute', () => {
				const now = new Date();

				editor.model.change((writer) => {
					editor.model.insertContent(
						writer.createText(`Current time: ${now.toString()} `)
					);
				});
			});

			return button;
		});
	}
}

const editorConfigTransformer: EditorConfigTransformer<any> = (config) => {
	const toolbar = config.toolbar as any;
	const existingItems = Array.isArray(toolbar)
		? toolbar
		: toolbar?.items ?? [];

	return {
		...config,
		extraPlugins: [...(config.extraPlugins ?? []), Timestamp],
		toolbar: {
			items: [...existingItems, 'timestamp'],
		},
	};
};

const editorTransformer: EditorTransformer<any> = {
	editorConfigTransformer,
};

export default editorTransformer;
