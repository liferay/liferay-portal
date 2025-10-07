/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Bookmark, Underline} from 'ckeditor5';
import {
	CKEditor5ClassicEditor as ClassicEditor,
	LiferayEditorConfig,
} from 'frontend-editor-ckeditor-web';
import React from 'react';

import Timestamp from './Timestamp';

const CKEditor5ReactClassicEditor = ({
	editorConfig,
	editorTransformerURLs,
}: {
	editorConfig: LiferayEditorConfig;
	editorTransformerURLs?: Array<string>;
}) => {
	const config: LiferayEditorConfig = {
		...editorConfig,
		extraPlugins: [Bookmark, Timestamp],
		initialData:
			'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Nunc id cursus metus aliquam eleifend mi in nulla. Quam adipiscing vitae proin sagittis nisl rhoncus. Suspendisse faucibus interdum posuere lorem. Nullam ac tortor vitae purus faucibus ornare. Ac felis donec et odio pellentesque diam. Nulla at volutpat diam ut. Posuere urna nec tincidunt praesent semper feugiat nibh. Gravida quis blandit turpis cursus. Proin libero nunc consequat interdum varius. Sollicitudin ac orci phasellus egestas tellus rutrum tellus pellentesque. Neque volutpat ac tincidunt vitae semper quis lectus nulla at. Odio euismod lacinia at quis risus sed vulputate odio ut. Augue lacus viverra vitae congue eu consequat ac. Elementum sagittis vitae et leo duis ut diam. Diam quis enim lobortis scelerisque fermentum dui faucibus.',
		removePlugins: [Underline],
		toolbar: {
			items: [
				'undo',
				'redo',
				'|',
				'bold',
				'italic',
				'underline',
				'|',
				'bookmark',
				'timestamp',
				'|',
				'headlessImageSelector',
				'headlessVideoSelector',
			],
		},
	};

	if (editorTransformerURLs?.length) {
		config.editorTransformerURLs = editorTransformerURLs;
	}

	return <ClassicEditor config={config} />;
};

export default CKEditor5ReactClassicEditor;
