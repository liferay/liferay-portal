/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TEditor} from 'frontend-editor-ckeditor-web';

export default function RemoveOuterParagraph(editor: TEditor) {
	const processor = editor.data.processor;
	const toData = processor.toData.bind(processor);

	processor.toData = (view: Parameters<(typeof processor)['toData']>[0]) => {
		return toData(view).replace(/<\/?p>/g, '');
	};
}
