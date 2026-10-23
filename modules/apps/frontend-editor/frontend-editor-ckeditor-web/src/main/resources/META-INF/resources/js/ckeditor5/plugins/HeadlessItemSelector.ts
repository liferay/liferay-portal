/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Command, Plugin} from '@ckeditor/ckeditor5-core/dist/index.js';
import {ButtonView} from '@ckeditor/ckeditor5-ui/dist/index.js';
import {openCMSFileSelectorModal} from '@liferay/frontend-js-item-selector-web';

import getIcon from '../utils/getIcon';

const ALLOWED_IMAGE_FILE_EXTENSIONS = [
	'apng',
	'avif',
	'gif',
	'jpg',
	'jpeg',
	'png',
	'svg',
	'tiff',
	'webp',
];

const ALLOWED_VIDEO_FILE_EXTENSIONS = [
	'avi',
	'm4v',
	'mkv',
	'mov',
	'mp4',
	'ogg',
	'ogv',
	'webm',
	'wmv',
];

const toAllowedExtensionsCSV = (extensions: string[]) =>
	extensions.map((extension) => `.${extension}`).join(',');

const ALLOWED_IMAGE_EXTENSIONS_CSV = toAllowedExtensionsCSV(
	ALLOWED_IMAGE_FILE_EXTENSIONS
);

const ALLOWED_VIDEO_EXTENSIONS_CSV = toAllowedExtensionsCSV(
	ALLOWED_VIDEO_FILE_EXTENSIONS
);

const VIDEO_FILTER = `((objectDefinitionExternalReferenceCode eq 'L_CMS_EXTERNAL_VIDEO') or (extension in ('${ALLOWED_VIDEO_FILE_EXTENSIONS.join(
	"','"
)}')))`;

let instanceCount = 0;

class HeadlessItemSelector extends Plugin {
	init() {
		const editor = this.editor;

		const commandName = 'headlessItemSelectorCommand';

		editor.commands.add(commandName, new Command(editor));

		const command = editor.commands.get(commandName)!;

		const folderMemoryKeyPrefix = `headless-item-selector:${instanceCount++}`;

		const getGroupId = () =>
			Number(editor.config.get('groupId')) ||
			Liferay.ThemeDisplay.getScopeGroupId();

		editor.ui.componentFactory.add('headlessImageSelector', () => {
			const buttonView = new ButtonView();

			buttonView.set({
				icon: getIcon({symbol: 'picture'}),
				label: Liferay.Language.get('image'),
				tooltip: true,
			});

			buttonView.bind('isEnabled').to(command, 'isEnabled');

			buttonView.on('execute', () => {
				openCMSFileSelectorModal({
					allowDragAndDrop: true,
					allowedExtensions: ALLOWED_IMAGE_EXTENSIONS_CSV,
					folderMemoryKey: `${folderMemoryKeyPrefix}:image`,
					groupId: getGroupId(),
					itemTypeLabel: Liferay.Language.get('image'),
					onSelect: (items) => {
						const href = items[0]?.embedded?.file?.link?.href;

						if (!href) {
							return;
						}

						const viewFragment = editor.data.processor.toView(
							`<img src="${href}">`
						);

						const modelFragment = editor.data.toModel(viewFragment);

						editor.model.insertContent(modelFragment);
					},
				});
			});

			return buttonView;
		});

		editor.ui.componentFactory.add('headlessVideoSelector', () => {
			const buttonView = new ButtonView();

			buttonView.set({
				icon: getIcon({symbol: 'video'}),
				label: Liferay.Language.get('video'),
				tooltip: true,
			});

			buttonView.bind('isEnabled').to(command, 'isEnabled');

			buttonView.on('execute', () => {
				openCMSFileSelectorModal({
					allowDragAndDrop: true,
					allowedExtensions: ALLOWED_VIDEO_EXTENSIONS_CSV,
					filters: [VIDEO_FILTER],
					folderMemoryKey: `${folderMemoryKeyPrefix}:video`,
					groupId: getGroupId(),
					itemTypeLabel: Liferay.Language.get('video'),
					onSelect: (items) => {
						const url =
							items[0]?.embedded?.videoURL ||
							items[0]?.embedded?.file?.link?.href;

						if (!url) {
							return;
						}

						const viewFragment = editor.data.processor.toView(
							`<oembed url="${url}"></oembed>`
						);

						const modelFragment = editor.data.toModel(viewFragment);

						editor.model.insertContent(modelFragment);
					},
				});
			});

			return buttonView;
		});
	}
}

export default HeadlessItemSelector;
