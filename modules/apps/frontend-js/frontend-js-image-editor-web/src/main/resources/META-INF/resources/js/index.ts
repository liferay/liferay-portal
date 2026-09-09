/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export {ImageEditor} from './ImageEditor';
export type {EditorSaveResult, ImageEditorProps} from './ImageEditor';

export type {EditorConfig} from './editorConfig';

export {
	ImageEditorLoadError,
	disposeLoadedImage,
	loadImage,
} from './imaging/loadImage';
export type {ImageLoadErrorReason, LoadedImage} from './imaging/loadImage';

export type {EditState} from './state/types';
