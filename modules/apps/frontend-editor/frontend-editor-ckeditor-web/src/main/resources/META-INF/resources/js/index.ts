/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import 'ckeditor5/ckeditor5.css';

// @ts-ignore

export {default as BalloonEditor} from './ckeditor4/BalloonEditor';

// @ts-ignore

export {default as ClassicEditor} from './ckeditor4/ClassicEditor';

// @ts-ignore

export {Editor} from './ckeditor4/Editor';

// @ts-ignore

export {InlineEditor} from './ckeditor4/InlineEditor';

export {default as CKEditor5BalloonEditor} from './ckeditor5/BalloonEditor';
export {default as CKEditor5ClassicEditor} from './ckeditor5/ClassicEditor';

export {default as getIcon} from './ckeditor5/utils/getIcon';
export {
	EEditorConfigPreset,
	LiferayEditorConfig,
	TEditor,
} from './ckeditor5/utils/types';

export {default as InputLocalized} from './input_localized/InputLocalized';
