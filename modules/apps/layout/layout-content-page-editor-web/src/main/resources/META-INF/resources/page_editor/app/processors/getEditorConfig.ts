/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LiferayEditorConfig, TEditor} from 'frontend-editor-ckeditor-web';
import {openSelectionModal} from 'frontend-js-components-web';

export type EditorConfig = LiferayEditorConfig & {
	documentBrowseLinkUrl: string;
	editorTransformerURLs: string;
	filebrowserImageBrowseLinkUrl: string;
	filebrowserImageBrowseUrl: string;
};

export default function getEditorConfig({
	editorConfig,
	editorName,
	initialData,
	itemSelectorEventName,
}: {
	editorConfig: EditorConfig;
	editorName: string;
	initialData: string;
	itemSelectorEventName: string;
}) {
	return {
		...editorConfig,
		documentBrowseLinkCallback: (
			editor: TEditor,
			url: string,
			changeLinkCallback: () => void
		) => {
			openSelectionModal({
				onSelect: changeLinkCallback,
				selectEventName: itemSelectorEventName,
				title: Liferay.Language.get('select-item'),
				url,
			});
		},
		documentBrowseLinkUrl: editorConfig.documentBrowseLinkUrl.replaceAll(
			'_EDITOR_NAME_',
			editorName
		),
		filebrowserImageBrowseLinkUrl:
			editorConfig.filebrowserImageBrowseLinkUrl.replaceAll(
				'_EDITOR_NAME_',
				editorName
			),
		filebrowserImageBrowseUrl:
			editorConfig.filebrowserImageBrowseUrl.replaceAll(
				'_EDITOR_NAME_',
				editorName
			),
		initialData,
		itemSelectorEventName,
		name: editorName,
	};
}
