/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Plugin} from '@ckeditor/ckeditor5-core/dist/index.js';
import {EmailConfigurationHelper} from '@ckeditor/ckeditor5-email/dist/index.js';
import {ExportInlineStyles} from '@ckeditor/ckeditor5-export-inline-styles/dist/index.js';
import {EmptyBlock} from '@ckeditor/ckeditor5-html-support/dist/index.js';
import {MergeFields} from '@ckeditor/ckeditor5-merge-fields/dist/index.js';
import {Table} from '@ckeditor/ckeditor5-table/dist/index.js';
import {Template} from '@ckeditor/ckeditor5-template/dist/index.js';
import {ButtonView} from '@ckeditor/ckeditor5-ui/dist/index.js';
import {
	EditorConfigTransformer,
	EditorTransformer,
} from '@liferay/js-api/editor';

const EXPORT_ICON =
	'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path d="M10 13.5 5.5 9l1.4-1.4L9 9.7V2h2v7.7l2.1-2.1L14.5 9z"/><path d="M4 15h12v2H4z"/></svg>';

class ExportInlineStylesPreview extends Plugin {
	init() {
		const editor = this.editor;

		editor.ui.componentFactory.add('exportInlineStylesPreview', () => {
			const button = new ButtonView();

			button.set({
				icon: EXPORT_ICON,
				label: 'Preview with Inline Styles',
				tooltip: true,
			});

			button.on('execute', async () => {
				const html = await editor.commands.execute(
					'exportInlineStyles',
					{}
				);

				window.open(
					URL.createObjectURL(new Blob([html], {type: 'text/html'})),
					'_blank'
				);
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

	const extraPlugins = [
		...(config.extraPlugins ?? []),
		Table,
		EmptyBlock,
		EmailConfigurationHelper,
		MergeFields,
		Template,
		ExportInlineStyles,
		ExportInlineStylesPreview,
	];

	const toolbarItems = [
		...existingItems,
		'|',
		'insertTable',
		'insertMergeField',
		'previewMergeFields',
		'insertTemplate',
		'exportInlineStylesPreview',
	];

	return {
		...config,
		extraPlugins,
		toolbar: {
			items: toolbarItems,
		},
	};
};

const editorTransformer: EditorTransformer<any> = {
	editorConfigTransformer,
};

export default editorTransformer;
