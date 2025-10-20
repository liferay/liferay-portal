/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-nocheck

import 'ckeditor5/ckeditor5.css';

export * from '@ckeditor/ckeditor5-adapter-ckfinder/dist/index.js';
export * from '@ckeditor/ckeditor5-alignment/dist/index.js';
export * from '@ckeditor/ckeditor5-autoformat/dist/index.js';
export * from '@ckeditor/ckeditor5-autosave/dist/index.js';
export * from '@ckeditor/ckeditor5-basic-styles/dist/index.js';
export * from '@ckeditor/ckeditor5-block-quote/dist/index.js';
export * from '@ckeditor/ckeditor5-bookmark/dist/index.js';
export * from '@ckeditor/ckeditor5-ckbox/dist/index.js';
export * from '@ckeditor/ckeditor5-ckfinder/dist/index.js';
export * from '@ckeditor/ckeditor5-clipboard/dist/index.js';
export * from '@ckeditor/ckeditor5-cloud-services/dist/index.js';
export * from '@ckeditor/ckeditor5-code-block/dist/index.js';
export * from '@ckeditor/ckeditor5-core/dist/index.js';
export * from '@ckeditor/ckeditor5-easy-image/dist/index.js';
export * from '@ckeditor/ckeditor5-editor-decoupled/dist/index.js';
export * from '@ckeditor/ckeditor5-editor-inline/dist/index.js';
export * from '@ckeditor/ckeditor5-editor-multi-root/dist/index.js';
export * from '@ckeditor/ckeditor5-emoji/dist/index.js';
export * from '@ckeditor/ckeditor5-engine/dist/index.js';
export * from '@ckeditor/ckeditor5-enter/dist/index.js';
export * from '@ckeditor/ckeditor5-essentials/dist/index.js';
export * from '@ckeditor/ckeditor5-find-and-replace/dist/index.js';
export * from '@ckeditor/ckeditor5-font/dist/index.js';
export * from '@ckeditor/ckeditor5-fullscreen/dist/index.js';
export * from '@ckeditor/ckeditor5-heading/dist/index.js';
export * from '@ckeditor/ckeditor5-highlight/dist/index.js';
export * from '@ckeditor/ckeditor5-horizontal-line/dist/index.js';
export * from '@ckeditor/ckeditor5-html-embed/dist/index.js';
export * from '@ckeditor/ckeditor5-html-support/dist/index.js';
export * from '@ckeditor/ckeditor5-icons/dist/index.js';
export * from '@ckeditor/ckeditor5-image/dist/index.js';
export * from '@ckeditor/ckeditor5-indent/dist/index.js';
export * from '@ckeditor/ckeditor5-language/dist/index.js';
export * from '@ckeditor/ckeditor5-link/dist/index.js';
export * from '@ckeditor/ckeditor5-list/dist/index.js';
export * from '@ckeditor/ckeditor5-markdown-gfm/dist/index.js';
export * from '@ckeditor/ckeditor5-media-embed/dist/index.js';
export * from '@ckeditor/ckeditor5-mention/dist/index.js';
export * from '@ckeditor/ckeditor5-minimap/dist/index.js';
export * from '@ckeditor/ckeditor5-page-break/dist/index.js';
export * from '@ckeditor/ckeditor5-paragraph/dist/index.js';
export * from '@ckeditor/ckeditor5-paste-from-office/dist/index.js';
export * from '@ckeditor/ckeditor5-remove-format/dist/index.js';
export * from '@ckeditor/ckeditor5-restricted-editing/dist/index.js';
export * from '@ckeditor/ckeditor5-select-all/dist/index.js';
export * from '@ckeditor/ckeditor5-show-blocks/dist/index.js';
export * from '@ckeditor/ckeditor5-source-editing/dist/index.js';
export * from '@ckeditor/ckeditor5-special-characters/dist/index.js';
export * from '@ckeditor/ckeditor5-style/dist/index.js';
export * from '@ckeditor/ckeditor5-table/dist/index.js';
export * from '@ckeditor/ckeditor5-typing/dist/index.js';
export * from '@ckeditor/ckeditor5-ui/dist/index.js';
export * from '@ckeditor/ckeditor5-undo/dist/index.js';
export * from '@ckeditor/ckeditor5-upload/dist/index.js';
export * from '@ckeditor/ckeditor5-utils/dist/index.js';
export * from '@ckeditor/ckeditor5-watchdog/dist/index.js';
export * from '@ckeditor/ckeditor5-widget/dist/index.js';
export * from '@ckeditor/ckeditor5-word-count/dist/index.js';

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
