/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EditorConfig} from 'ckeditor5';
import {sub} from 'frontend-js-web';

const advancedClassicEditorConfig: EditorConfig = {
	alignment: {
		options: ['left', 'center', 'right'],
	},
	heading: {
		options: [
			{
				class: 'lfr-editor-heading_paragraph',
				model: 'paragraph',
				title: Liferay.Language.get('normal'),
			},
			{
				class: 'lfr-editor-heading_heading1',
				model: 'heading1',
				title: sub(Liferay.Language.get('heading-x'), 1),
				view: 'h1',
			},
			{
				class: 'lfr-editor-heading_heading2',
				model: 'heading2',
				title: sub(Liferay.Language.get('heading-x'), 2),
				view: 'h2',
			},
			{
				class: 'lfr-editor-heading_heading3',
				model: 'heading3',
				title: sub(Liferay.Language.get('heading-x'), 3),
				view: 'h3',
			},
		],
	},
	htmlSupport: {
		allow: [
			{
				classes: true,
				name: /.*/,
			},
		],
	},
	style: {
		definitions: [
			{
				classes: ['alert', 'alert-info'],
				element: 'p',
				name: Liferay.Language.get('info-message'),
			},
			{
				classes: ['alert', 'alert-warning'],
				element: 'p',
				name: Liferay.Language.get('alert-message'),
			},
			{
				classes: ['alert', 'alert-danger'],
				element: 'p',
				name: Liferay.Language.get('error-message'),
			},
			{
				classes: ['cite'],
				element: 'cite',
				name: Liferay.Language.get('cited-work'),
			},
			{
				classes: ['code'],
				element: 'code',
				name: Liferay.Language.get('computer-code'),
			},
		],
	},
	table: {
		contentToolbar: [
			'tableColumn',
			'tableRow',
			'tableProperties',
			'toggleTableCaption',
		],
	},
	toolbar: [
		'undo',
		'redo',
		'|',
		'style',
		'|',
		'heading',
		'|',
		'bold',
		'italic',
		'underline',
		'strikethrough',
		'|',
		'fontColor',
		'fontBackgroundColor',
		'|',
		'removeFormat',
		'|',
		'numberedList',
		'bulletedList',
		'|',
		'indent',
		'outdent',
		'|',
		'blockQuote',
		'|',
		'link',
		'insertTable',
		'mediaEmbed',
		'|',
		'horizontalLine',
		'|',
		'alignment',
		'sourceEditing',
	],
};

export default advancedClassicEditorConfig;
