/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';

import DetachedPinturaEditorModal from './DetachedPinturaEditorModal';

export interface OpenPinturaEditorModalOptions {
	imageName: string;
	imageUrl: string | Blob;
	onSave: (blob: Blob) => void | Promise<void>;
	[key: string]: unknown;
}

/**
 * Opens the Pintura image editor modal from any JS context.
 * The caller provides `onSave(blob)` to handle the edited image.
 */
export default function openPinturaEditorModal(
	options: OpenPinturaEditorModalOptions
) {

	// Mount in detached node; Clay will take care of appending to `document.body`.
	// See: https://github.com/liferay/clay/blob/master/packages/clay-shared/src/Portal.tsx

	return render(
		DetachedPinturaEditorModal,
		options,
		document.createElement('div')
	);
}
