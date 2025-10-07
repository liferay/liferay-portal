/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	EEditorConfigPreset,
	LiferayEditorConfig,
	TEditor,
} from 'frontend-editor-ckeditor-web';
import {openSelectionModal} from 'frontend-js-components-web';

import BlockButtonCustomization from './plugins/BlockButtonCustomization';
import EmptyAltImagePlugin from './plugins/EmptyAltImagePlugin';
import RemoveOuterParagraph from './plugins/RemoveOuterParagraph';

export type EditorConfig = LiferayEditorConfig & {
	documentBrowseLinkCallback?: (
		editor: TEditor,
		url: string,
		changeLinkCallback: () => void
	) => void;
	documentBrowseLinkUrl?: string;
	filebrowserImageBrowseLinkUrl?: string;
};

export default function getCKEditorConfig({
	editorConfig: initialConfig,
	editorName,
	initialData,
	itemSelectorEventName,
	removeOuterParagraph,
}: {
	editorConfig: EditorConfig;
	editorName?: string;
	initialData: string;
	itemSelectorEventName?: string;
	removeOuterParagraph?: boolean;
}) {
	let config = initialConfig;

	const blockToolbarItems = Array.isArray(config.blockToolbar)
		? config.blockToolbar
		: config.blockToolbar?.items;

	const extraPlugins = [];

	if (blockToolbarItems) {
		extraPlugins.push(BlockButtonCustomization);
	}

	if (blockToolbarItems?.includes('imageSelector')) {
		extraPlugins.push(EmptyAltImagePlugin);
	}

	if (removeOuterParagraph) {
		extraPlugins.push(RemoveOuterParagraph);
	}

	if (editorName && config.preset === EEditorConfigPreset.ADVANCED) {
		config = {
			...config,
			documentBrowseLinkCallback: (editor, url, changeLinkCallback) => {
				openSelectionModal({
					onSelect: changeLinkCallback,
					selectEventName: itemSelectorEventName,
					title: Liferay.Language.get('select-item'),
					url,
				});
			},
			documentBrowseLinkUrl: config.documentBrowseLinkUrl?.replaceAll(
				'_EDITOR_NAME_',
				editorName
			),
			filebrowserImageBrowseLinkUrl:
				config.filebrowserImageBrowseLinkUrl?.replaceAll(
					'_EDITOR_NAME_',
					editorName
				),
			filebrowserImageBrowseUrl:
				config.filebrowserImageBrowseUrl?.replaceAll(
					'_EDITOR_NAME_',
					editorName
				),
			itemSelectorEventName,
		};
	}

	return {
		...config,
		...(editorName && {name: editorName}),
		extraPlugins,
		initialData,
	} as LiferayEditorConfig;
}
