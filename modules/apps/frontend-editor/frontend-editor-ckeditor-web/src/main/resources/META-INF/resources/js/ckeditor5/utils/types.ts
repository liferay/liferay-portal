/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {EditorConfig} from '@ckeditor/ckeditor5-core';
import type {BalloonEditor} from '@ckeditor/ckeditor5-editor-balloon';
import type {ClassicEditor} from '@ckeditor/ckeditor5-editor-classic';

export enum EEditorConfigPreset {
	BASIC = 'basic',
	ADVANCED = 'advanced',
}

export enum EEditorType {
	ALLOYEDITOR = 'alloyeditor',
	CKEDITOR4 = 'ckeditor4',
	CKEDITOR5 = 'ckeditor5',
}

export enum EEditorVariant {
	BALLOON = 'balloon',
	CLASSIC = 'classic',
}

export interface LiferayEditorConfig extends EditorConfig {
	editorTransformerURLs?: Array<string>;
	editorType?: EEditorType;
	editorVariant?: EEditorVariant;
	editorVersion?: string;
	filebrowserImageBrowseUrl?: string;
	filebrowserVideoBrowseUrl?: string;
	itemSelectorEventName?: string;
	preset?: EEditorConfigPreset;
}

export type TEditor = BalloonEditor | ClassicEditor;
