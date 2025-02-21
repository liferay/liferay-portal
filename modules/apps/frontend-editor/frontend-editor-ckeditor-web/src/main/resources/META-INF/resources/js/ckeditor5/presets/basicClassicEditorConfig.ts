/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EditorConfig} from 'ckeditor5';

const basicClassicEditorConfig: EditorConfig = {
	toolbar: [
		'undo',
		'redo',
		'|',
		'bold',
		'italic',
		'underline',
		'|',
		'numberedList',
		'bulletedList',
		'|',
		'link',
	],
};

export default basicClassicEditorConfig;
