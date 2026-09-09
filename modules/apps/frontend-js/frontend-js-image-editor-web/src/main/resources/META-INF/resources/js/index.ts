/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export {ImageEditor} from './ImageEditor';
export type {ImageEditorProps} from './ImageEditor';

export type {EditorMessages} from './i18n';
export {setMessages} from './i18n';
export {liferayMessages} from './i18n/liferayMessages';
export type {ImageLoadErrorReason, LoadedImage} from './imaging/loadImage';
export {
	ImageEditorLoadError,
	disposeLoadedImage,
	loadImage,
} from './imaging/loadImage';
