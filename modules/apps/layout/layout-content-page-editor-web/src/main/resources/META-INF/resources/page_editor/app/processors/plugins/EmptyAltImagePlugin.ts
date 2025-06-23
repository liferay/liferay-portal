/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TEditor} from 'frontend-editor-ckeditor-web';

type Change = {
	name: string;
	position: {nodeAfter: any};
	type: 'insert' | 'remove';
};

export default function EmptyAltImagePlugin(editor: TEditor) {
	const model = editor.model;

	model.document.on('change:data', () => {
		const changes = model.document.differ.getChanges();

		(changes as Change[]).forEach(({name, position, type}) => {

			// When an image is inserted, the alt attribute is set to empty
			// by default to indicate that the image is decorative.

			if (type === 'insert' && name === 'imageInline') {
				model.change((writer: any) => {
					writer.setAttribute('alt', '', position.nodeAfter);
				});
			}
		});
	});
}
