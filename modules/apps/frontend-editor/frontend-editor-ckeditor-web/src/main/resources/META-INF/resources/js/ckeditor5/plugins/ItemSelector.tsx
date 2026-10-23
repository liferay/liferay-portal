/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Command, Plugin} from '@ckeditor/ckeditor5-core/dist/index.js';
import {ButtonView} from '@ckeditor/ckeditor5-ui/dist/index.js';
import {Config} from '@ckeditor/ckeditor5-utils/dist/index.js';
import {openSelectionModal} from 'frontend-js-components-web';

import getIcon from '../utils/getIcon';
import {LiferayEditorConfig} from '../utils/types';

const ITEM_SELECTOR_FOLDER_ID_PARAM =
	'_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_folderId';

function createFolderMemory() {
	let lastFolderId: number | string | null = null;

	const isFolderIdEmpty = (folderId: number | string | null | undefined) => {
		return folderId === null || folderId === undefined || folderId === '';
	};

	return {
		applyTo(url: string): string {
			if (isFolderIdEmpty(lastFolderId)) {
				return url;
			}

			try {
				const parsed = new URL(url, window.location.origin);

				parsed.searchParams.set(
					ITEM_SELECTOR_FOLDER_ID_PARAM,
					String(lastFolderId)
				);

				return parsed.toString();
			}
			catch (error) {
				return url;
			}
		},

		remember(folderId: number | string | undefined) {
			if (!isFolderIdEmpty(folderId)) {
				lastFolderId = folderId as number | string;
			}
		},
	};
}

class ItemSelector extends Plugin {
	init() {
		const editor = this.editor;

		const commandName = 'itemSelectorCommand';

		editor.commands.add(commandName, new Command(editor));

		const command = editor.commands.get(commandName)!;

		const config: Config<LiferayEditorConfig> = editor.config;

		const folderMemory = createFolderMemory();

		const rememberSelectionFolder = Boolean(
			config.get('itemSelectorRememberSelectionFolder')
		);

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
						onSelect: ({
							folderId,
							value,
						}: {
							folderId?: number | string;
							value: string;
						}) => {
							if (rememberSelectionFolder) {
								folderMemory.remember(folderId);
							}

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
						url: rememberSelectionFolder
							? folderMemory.applyTo(filebrowserImageBrowseUrl)
							: filebrowserImageBrowseUrl,
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
						onSelect: ({
							folderId,
							value,
						}: {
							folderId?: number | string;
							value: any;
						}) => {
							if (rememberSelectionFolder) {
								folderMemory.remember(folderId);
							}

							let html: string | undefined;
							let url: string | undefined;

							try {
								({html, url} = JSON.parse(value));
							}
							catch (error) {
								({html, url} = value);
							}

							if (!html && !url) {
								return;
							}

							const viewFragment = editor.data.processor.toView(
								html || `<oembed url="${url}"></oembed>`
							);

							const modelFragment =
								editor.data.toModel(viewFragment);

							editor.model.insertContent(modelFragment);
						},
						selectEventName: config.get('itemSelectorEventName'),
						title: Liferay.Language.get('select-item'),
						url: rememberSelectionFolder
							? folderMemory.applyTo(filebrowserVideoBrowseUrl)
							: filebrowserVideoBrowseUrl,
						zIndex: Liferay.zIndex.WINDOW + 10,
					});
				});

				return buttonView;
			});
		}
	}
}

export default ItemSelector;
