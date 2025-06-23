/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CKEditor5BalloonEditor} from 'frontend-editor-ckeditor-web';
import React from 'react';

import {config} from '../../../app/config/index';
import getCKEditorConfig, {
	EditorConfig,
} from '../../../app/processors/getCKEditorConfig';

export default function CKEditor({
	initialData,
	onChange,
}: {
	initialData: string;
	onChange: (value: string) => void;
}) {
	const {editorConfig: initialConfig} =
		config.defaultEditorConfigurations['comment'];

	const editorConfig = {
		...initialConfig,
		label: Liferay.Language.get('add-comment'),
		placeholder: Liferay.Language.get('type-your-comment-here'),
	} as EditorConfig;

	return (
		<CKEditor5BalloonEditor
			className="c-mb-3 form-control form-control-sm"
			config={getCKEditorConfig({
				editorConfig,
				initialData,
			})}
			onChange={(event, editor) => onChange(editor.getData())}
		/>
	);
}
