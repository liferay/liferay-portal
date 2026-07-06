/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Underline} from '@ckeditor/ckeditor5-basic-styles/dist/index.js';
import {Bookmark} from '@ckeditor/ckeditor5-bookmark/dist/index.js';
import ClayButton from '@clayui/button';
import {
	CKEditor5ClassicEditor as ClassicEditor,
	LiferayEditorConfig,
	TEditor,
} from 'frontend-editor-ckeditor-web';
import React, {useRef, useState} from 'react';

const CKEditor5ReactClassicEditor = ({
	editorConfig,
}: {
	editorConfig: LiferayEditorConfig;
}) => {
	const [disabled, setDisabled] = useState(false);

	const editorRef = useRef<TEditor>();

	const config: LiferayEditorConfig = {
		...editorConfig,
		extraPlugins: [Bookmark],
		initialData:
			'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Nunc id cursus metus aliquam eleifend mi in nulla. Quam adipiscing vitae proin sagittis nisl rhoncus. Suspendisse faucibus interdum posuere lorem. Nullam ac tortor vitae purus faucibus ornare. Ac felis donec et odio pellentesque diam. Nulla at volutpat diam ut. Posuere urna nec tincidunt praesent semper feugiat nibh. Gravida quis blandit turpis cursus. Proin libero nunc consequat interdum varius. Sollicitudin ac orci phasellus egestas tellus rutrum tellus pellentesque. Neque volutpat ac tincidunt vitae semper quis lectus nulla at. Odio euismod lacinia at quis risus sed vulputate odio ut. Augue lacus viverra vitae congue eu consequat ac. Elementum sagittis vitae et leo duis ut diam. Diam quis enim lobortis scelerisque fermentum dui faucibus. <p><a href="/home">Link to home page</a></p>',
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
				'link',
				'|',
				'bookmark',
				'|',
				'headlessImageSelector',
				'headlessVideoSelector',
			],
		},
	};

	return (
		<div className="container-fluid">
			<ClayButton.Group spaced>
				<ClayButton
					onClick={() => {
						setDisabled(!disabled);
					}}
				>
					Toggle disabled prop
				</ClayButton>

				<ClayButton
					onClick={() => {
						const editor = editorRef.current;

						if (!editor) {
							return;
						}

						if (editor.isReadOnly) {
							editor.disableReadOnlyMode('sample');
						}
						else {
							editor.enableReadOnlyMode('sample');
						}
					}}
				>
					Toggle internal read-only mode
				</ClayButton>
			</ClayButton.Group>

			<div className="mt-3 row">
				<div>
					<ClassicEditor
						config={config}
						disabled={disabled}
						onReady={(editor) => {
							editorRef.current = editor;
						}}
					/>
				</div>
			</div>
		</div>
	);
};

export default CKEditor5ReactClassicEditor;
