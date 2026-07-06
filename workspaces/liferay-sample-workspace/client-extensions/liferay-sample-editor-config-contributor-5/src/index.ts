/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	EditorConfigTransformer,
	EditorTransformer,
} from '@liferay/js-api/editor';
import DocumentLinkSelector from 'frontend-editor-ckeditor-web/plugins/DocumentLinkSelector';

const editorConfigTransformer: EditorConfigTransformer<any> = (config) => {
	const allPlugins = [
		...((config.plugins as any[]) ?? []),
		...((config.extraPlugins as any[]) ?? []),
	];

	const alreadyLoaded = allPlugins.some(
		(plugin: any) => plugin === DocumentLinkSelector
	);

	return {
		...config,
		extraPlugins: alreadyLoaded
			? (config.extraPlugins as any[]) ?? []
			: [...((config.extraPlugins as any[]) ?? []), DocumentLinkSelector],
	};
};

const editorTransformer: EditorTransformer<any> = {
	editorConfigTransformer,
};

export default editorTransformer;
