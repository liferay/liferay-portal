/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	Alignment,
	BlockQuote,
	BlockToolbar,
	Bold,
	EditorConfig,
	Essentials,
	Font,
	GeneralHtmlSupport,
	Heading,
	HorizontalLine,
	Image,
	ImageBlock,
	ImageCaption,
	ImageInline,
	ImageResize,
	ImageStyle,
	ImageToolbar,
	Indent,
	Italic,
	Link,
	List,
	MediaEmbed,
	Paragraph,
	RemoveFormat,
	SourceEditing,
	Strikethrough,
	Style,
	Table,
	TableCaption,
	TableProperties,
	TableToolbar,
	Underline,
} from 'ckeditor5';
import {sub} from 'frontend-js-web';

import ItemSelector from '../plugins/ItemSelector';
import {EEditorConfigPreset, EEditorVariant} from './types';

const getDefaultEditorConfig = ({
	editorVariant,
	preset,
}: {
	editorVariant: EEditorVariant;
	preset: EEditorConfigPreset;
}): EditorConfig => {
	const basicPlugins = [
		BlockToolbar,
		Bold,
		Essentials,
		GeneralHtmlSupport,
		Italic,
		Image,
		Link,
		List,
		Paragraph,
		Underline,
	];

	const classicUI = {
		viewportOffset: {
			top: 56,
		},
	};

	if (preset === EEditorConfigPreset.BASIC) {
		const basicEditorConfig: EditorConfig = {
			plugins: basicPlugins,
			toolbar: {
				items: [
					'undo',
					'redo',
					'|',
					'bold',
					'italic',
					'underline',
					'|',
					'numberedList',
					'bulletedList',
					'|',
					'link',
				],
				shouldNotGroupWhenFull: false,
			},
			ui:
				editorVariant === EEditorVariant.CLASSIC
					? classicUI
					: undefined,
		};

		return basicEditorConfig;
	}

	const advancedPlugins = [
		...basicPlugins,
		Alignment,
		BlockQuote,
		Font,
		Heading,
		HorizontalLine,
		ItemSelector,
		ImageBlock,
		ImageCaption,
		ImageInline,
		ImageResize,
		ImageStyle,
		ImageToolbar,
		Indent,
		RemoveFormat,
		MediaEmbed,
		Strikethrough,
		Style,
		Table,
		TableCaption,
		TableProperties,
		TableToolbar,
	];

	if (editorVariant === EEditorVariant.CLASSIC) {
		advancedPlugins.push(SourceEditing);
	}

	const toolbarItems = [
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
		'imageSelector',
		'videoSelector',
		'|',
		'horizontalLine',
		'|',
		'alignment',
	];

	if (editorVariant === EEditorVariant.CLASSIC) {
		toolbarItems.push('sourceEditing');
	}

	const advancedEditorConfig: EditorConfig = {
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
					attributes: true,
					classes: true,
					name: /.*/,
					styles: true,
				},
			],
			allowEmpty: ['img'],
		},
		image: {
			toolbar: [
				'imageStyle:inline',
				'imageStyle:alignBlockLeft',
				'imageStyle:block',
				'imageStyle:alignBlockRight',
				'imageStyle:side',
				'toggleImageCaption',
				'imageTextAlternative',
			],
		},
		mediaEmbed: {
			previewsInData: true,
		},
		plugins: advancedPlugins,
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
		toolbar: {
			items: toolbarItems,
			shouldNotGroupWhenFull: true,
		},
		ui: editorVariant === EEditorVariant.CLASSIC ? classicUI : undefined,
	};

	return advancedEditorConfig;
};

export default getDefaultEditorConfig;
