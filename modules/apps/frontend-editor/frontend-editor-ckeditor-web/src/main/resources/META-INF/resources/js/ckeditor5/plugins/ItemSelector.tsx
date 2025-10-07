/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ButtonView, Command, Config, Plugin} from 'ckeditor5';
import {openSelectionModal} from 'frontend-js-components-web';

import getIcon from '../utils/getIcon';
import {LiferayEditorConfig} from '../utils/types';

class ItemSelector extends Plugin {
	init() {
		const editor = this.editor;

		const commandName = 'itemSelectorCommand';

		editor.commands.add(commandName, new Command(editor));

		const command = editor.commands.get(commandName)!;

		const config: Config<LiferayEditorConfig> = editor.config;

		const filebrowserImageBrowseUrl = config.get(
			'filebrowserImageBrowseUrl'
		);

		if (filebrowserImageBrowseUrl) {
			editor.ui.componentFactory.add('imageSelector', () => {
				const buttonView = new ButtonView();

				buttonView.set({
					icon: getIcon({symbol: 'picture'}),
					label: Liferay.Language.get('image'),
					tooltip: true,
				});

				buttonView.bind('isEnabled').to(command, 'isEnabled');

				buttonView.on('execute', () => {
					openSelectionModal({
						onSelect: ({value}: {value: string}) => {
							let url;

							try {
								url = JSON.parse(value).url;
							}
							catch (error) {
								url = value;
							}

							if (!url) {
								return;
							}

							const viewFragment = editor.data.processor.toView(
								`<img src="${url}">`
							);

							const modelFragment =
								editor.data.toModel(viewFragment);

							editor.model.insertContent(modelFragment);
						},
						selectEventName: config.get('itemSelectorEventName'),
						title: Liferay.Language.get('select-item'),
						url: filebrowserImageBrowseUrl,
						zIndex: Liferay.zIndex.WINDOW + 10,
					});
				});

				return buttonView;
			});
		}

		const filebrowserVideoBrowseUrl = config.get(
			'filebrowserVideoBrowseUrl'
		);

		if (filebrowserVideoBrowseUrl) {
			editor.ui.componentFactory.add('videoSelector', () => {
				const buttonView = new ButtonView();

				buttonView.set({
					icon: getIcon({symbol: 'video'}),
					label: Liferay.Language.get('video'),
					tooltip: true,
				});

				buttonView.bind('isEnabled').to(command, 'isEnabled');

				buttonView.on('execute', () => {
					openSelectionModal({
						onSelect: ({value}: {value: any}) => {
							let url: string;

							try {
								url = JSON.parse(value).url;
							}
							catch (error) {
								url = value.url;
							}

							if (!url) {
								return;
							}

							const viewFragment = editor.data.processor.toView(
								`<oembed url="${url}"></oembed>`
							);

							const modelFragment =
								editor.data.toModel(viewFragment);

							editor.model.insertContent(modelFragment);
						},
						selectEventName: config.get('itemSelectorEventName'),
						title: Liferay.Language.get('select-item'),
						url: filebrowserVideoBrowseUrl,
						zIndex: Liferay.zIndex.WINDOW + 10,
					});
				});

				return buttonView;
			});
		}
	}
}

export default ItemSelector;
