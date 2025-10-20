/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	EditorConfigTransformer,
	EditorTransformer,
} from '@liferay/js-api/editor';
import {ButtonView, Fullscreen, Plugin} from 'ckeditor5';

const editorConfigTransformer: EditorConfigTransformer<any> = (config) => {

	// CKEditor 5

	if (config?.editorType === 'ckeditor5') {
		class HelloWorld extends Plugin {
			init() {
				const editor = this.editor;

				editor.ui.componentFactory.add('helloworld', () => {
					const button = new ButtonView();

					button.set({
						label: 'Hello',
						withText: true,
					});

					button.on('execute', () => {
						editor.model.change((writer) => {
							editor.model.insertContent(
								writer.createText('Hello World ')
							);
						});
					});

					return button;
				});
			}
		}

		const toolbar = [

			// If plugin is in advanced preset, we can just add a toolabar entry.

			'AccessibilityHelp',
			'undo',
			'redo',
			'alignment',
			{

				// A dropdown with custom icon.

				icon: 'text',
				items: ['bold', 'italic', 'underline'],
				label: 'Text formatting',
			},
			{

				// A dropdown with text label instead of icon.

				icon: false,
				items: ['bulletedList', 'numberedList'],
				label: 'Lists',
			},

			// An official plugin that was added as extra in `frontend-editor-ckeditor-sample-web`.

			'bookmark',

			// A custom plugin that was added in `frontend-editor-ckeditor-sample-web`.

			'timestamp',

			// An official plugin not in advanced preset.

			'fullscreen',

			// A custom plugin.

			'helloworld',
		];

		const updatedConfig = {
			...config,
			extraPlugins: [Fullscreen, HelloWorld],
			toolbar,
		};

		return updatedConfig;
	}

	// Alloy Editor

	const toolbars: any = config.toolbars;

	if (typeof toolbars === 'object') {
		interface ISelection {
			buttons: Array<string>;
			name: string;
		}

		const textSelection: ISelection = toolbars.styles?.selections?.find(
			(selection: ISelection) => selection.name === 'text'
		);

		if (textSelection.buttons) {
			textSelection.buttons.push('video');

			return {
				...config,
				toolbars,
			};
		}
	}

	// CKEditor

	const toolbar: string | [string[]] = config.toolbar;

	const buttonName = 'AICreator';
	let transformedConfig: any;

	if (typeof toolbar === 'string') {
		const activeToolbar = config[`toolbar_${toolbar}`];

		activeToolbar.push([buttonName]);

		transformedConfig = {
			...config,
			[`toolbar_${toolbar}`]: activeToolbar,
		};
	}
	else if (Array.isArray(toolbar)) {
		toolbar.push([buttonName]);

		transformedConfig = {
			...config,
			toolbar,
		};
	}

	const extraPlugins: string = config.extraPlugins;

	return {
		...transformedConfig,
		extraPlugins: extraPlugins ? `${extraPlugins},aicreator` : 'aicreator',
	};
};

const editorTransformer: EditorTransformer<any> = {
	editorConfigTransformer,
};

export default editorTransformer;
