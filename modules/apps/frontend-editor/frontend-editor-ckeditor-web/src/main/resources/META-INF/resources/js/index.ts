/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-nocheck

import 'ckeditor5/ckeditor5.css';

import 'ckeditor5-premium-features/ckeditor5-premium-features.css';

export {default as BalloonEditor} from './ckeditor4/BalloonEditor';
export {default as ClassicEditor} from './ckeditor4/ClassicEditor';
export {Editor} from './ckeditor4/Editor';

export {default as CKEditor5BalloonEditor} from './ckeditor5/BalloonEditor';
export {default as CKEditor5ClassicEditor} from './ckeditor5/ClassicEditor';
export {default as getIcon} from './ckeditor5/utils/getIcon';
export {
	EEditorConfigPreset,
	LiferayEditorConfig,
	TEditor,
} from './ckeditor5/utils/types';

export {default as InputLocalized} from './input_localized/InputLocalized';

export {
	BalloonEditor as BaseCKEditor5BalloonEditor,
	BalloonEditorUI,
	BalloonEditorUIView,
} from '@ckeditor/ckeditor5-editor-balloon/dist/index.js';
export {
	ClassicEditor as BaseCKEditor5ClassicEditor,
	ClassicEditorUI,
	ClassicEditorUIView,
} from '@ckeditor/ckeditor5-editor-classic/dist/index.js';
