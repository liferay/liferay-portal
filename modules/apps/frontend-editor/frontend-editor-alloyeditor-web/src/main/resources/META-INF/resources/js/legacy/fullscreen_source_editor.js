/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-fullscreen-source-editor',
	(A) => {
		const Lang = A.Lang;

		const CONTENT_TEMPLATE =
			'<div class="cadmin lfr-fullscreen-source-editor-header row">' +
			'<div class="col-6">' +
			'<button class="btn btn-secondary btn-sm float-right lfr-portal-tooltip" data-title="{iconMoonTooltip}" id="switchTheme" type="button">' +
			'<svg class="lexicon-icon lexicon-icon-moon" focusable="false" role="img">' +
			'<use href="{spritemap}#moon" />' +
			'</svg>' +
			'</button>' +
			'</div>' +
			'<div class="col-6 layout-selector text-right">' +
			'<div class="btn-group" role="group">' +
			'<button class="btn btn-secondary btn-sm" data-layout="vertical">' +
			'<svg class="lexicon-icon lexicon-icon-columns" focusable="false" role="img">' +
			'<use href="{spritemap}#columns" />' +
			'</svg>' +
			'</button>' +
			'<button class="btn btn-secondary btn-sm" data-layout="horizontal">' +
			'<svg class="lexicon-icon lexicon-icon-cards" focusable="false" role="img">' +
			'<use href="{spritemap}#cards" />' +
			'</svg>' +
			'</button>' +
			'<button class="btn btn-secondary btn-sm" data-layout="simple">' +
			'<svg class="lexicon-icon lexicon-icon-expand" focusable="false" role="img">' +
			'<use href="{spritemap}#expand" />' +
			'</svg>' +
			'</button>' +
			'</div>' +
			'</div>' +
			'</div>' +
			'<div class="lfr-fullscreen-source-editor-content">' +
			'<div class="source-panel">' +
			'<div class="source-html"></div>' +
			'</div>' +
			'<div class="preview-panel"></div>' +
			'<div class="panel-splitter"></div>' +
			'</div>';

		const CSS_PREVIEW_PANEL = '.preview-panel';

		const STR_BOUNDING_BOX = 'boundingBox';

		const STR_CLICK = 'click';

		const STR_DOT = '.';

		const STR_LAYOUT = 'layout';

		const STR_VALUE = 'value';

		const LiferayFullScreenSourceEditor = A.Component.create({
			ATTRS: {
				aceOptions: {
					validator: Lang.isObject,
					value: {
						fontSize: 13,
						showInvisibles: false,
						showPrintMargin: false,
					},
				},

				dataProcessor: {
					validator: Lang.isObject,
				},

				layout: {
					validator: Lang.isString,
					value: 'vertical',
				},

				previewCssClass: {
					validator: Lang.isString,
					value: '',
				},

				previewDelay: {
					validator: Lang.isNumber,
					value: 100,
				},

				value: {
					getter: '_getValue',
					validator: Lang.isString,
					value: '',
				},
			},

			CSS_PREFIX: 'lfr-fullscreen-source-editor',

			EXTENDS: A.Widget,

			NAME: 'liferayfullscreensourceeditor',

			NS: 'liferayfullscreensourceeditor',

			prototype: {
				_getHtml(val) {
					const instance = this;

					const dataProcessor = instance.get('dataProcessor');

					if (dataProcessor && dataProcessor.toHtml) {
						val = dataProcessor.toHtml(val);
					}

					return val;
				},

				_getValue(val) {
					const instance = this;

					return instance._editor
						? instance._editor.get(STR_VALUE)
						: val;
				},

				_onEditorChange(event) {
					const instance = this;

					if (event.newVal || event.newVal === '') {
						instance._previewPanel.html(
							instance._getHtml(event.newVal)
						);
					}
				},

				_onLayoutChange(event) {
					const instance = this;

					instance
						.get(STR_BOUNDING_BOX)
						.one(STR_DOT + instance.getClassName('content'))
						.replaceClass(event.prevVal, event.newVal);

					instance.resizeEditor();
				},

				_onLayoutClick(event) {
					const instance = this;

					instance.set(
						STR_LAYOUT,
						event.currentTarget.attr('data-layout')
					);
				},

				_onPreviewLink(event) {
					event.currentTarget.attr('target', '_blank');
				},

				_onValueChange(event) {
					const instance = this;

					instance._editor.set(STR_VALUE, event.newVal);
				},

				_switchTheme() {
					const instance = this;

					instance._editor.switchTheme();
				},

				CONTENT_TEMPLATE: Lang.sub(CONTENT_TEMPLATE, {
					iconMoonTooltip: Liferay.Language.get('dark-theme'),
					spritemap: Liferay.Icons.spritemap,
				}),

				bindUI() {
					const instance = this;

					const boundingBox = instance.get(STR_BOUNDING_BOX);

					const onChangeTask = A.debounce(
						'_onEditorChange',
						instance.get('previewDelay'),
						instance
					);

					instance._eventHandles = [
						instance._editor.on('change', onChangeTask),
						instance.on('layoutChange', instance._onLayoutChange),
						instance.on('valueChange', instance._onValueChange),
						instance._editorSwitchTheme.on(
							'click',
							instance._switchTheme,
							instance
						),
						boundingBox
							.one(STR_DOT + instance.getClassName('header'))
							.delegate(
								STR_CLICK,
								instance._onLayoutClick,
								'[data-layout]',
								instance
							),
						boundingBox
							.one(CSS_PREVIEW_PANEL)
							.delegate(
								STR_CLICK,
								instance._onPreviewLink,
								'a',
								instance
							),
					];
				},

				destructor() {
					const instance = this;

					const sourceEditor = instance._editor;

					if (sourceEditor) {
						sourceEditor.destroy();
					}

					new A.EventHandle(instance._eventHandles).detach();
				},

				renderUI() {
					const instance = this;

					const boundingBox = instance.get(STR_BOUNDING_BOX);

					boundingBox
						.one(STR_DOT + instance.getClassName('content'))
						.addClass(instance.get(STR_LAYOUT));

					instance._editorSwitchTheme =
						boundingBox.one('#switchTheme');

					instance._editor = new A.LiferaySourceEditor({
						boundingBox: boundingBox.one('.source-html'),
						height: '100%',
						mode: 'html',
						on: {
							themeSwitched(event) {
								const editorSwitchTheme =
									instance._editorSwitchTheme;

								const nextTheme =
									event.themes[event.nextThemeIndex];

								editorSwitchTheme
									.one('.lexicon-icon')
									.replace(nextTheme.icon);

								editorSwitchTheme.setAttribute(
									'data-title',
									nextTheme.tooltip
								);
							},
						},
						value: instance.get(STR_VALUE),
					}).render();

					instance._previewPanel = boundingBox.one(CSS_PREVIEW_PANEL);

					instance._previewPanel.html(
						instance._getHtml(instance.get(STR_VALUE))
					);
					instance._previewPanel.addClass(
						instance.get('previewCssClass')
					);
				},

				resizeEditor() {
					const instance = this;

					instance._editor.getEditor().resize();
				},
			},
		});

		A.LiferayFullScreenSourceEditor = LiferayFullScreenSourceEditor;
	},
	'',
	{
		requires: ['liferay-source-editor'],
	}
);
