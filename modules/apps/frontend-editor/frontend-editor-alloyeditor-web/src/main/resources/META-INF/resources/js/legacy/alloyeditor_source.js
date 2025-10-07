/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-alloy-editor-source',
	(A) => {
		const CSS_SHOW_SOURCE = 'show-source';

		const MAP_TOGGLE_STATE = {
			false: {
				iconCssClass: 'code',
			},
			true: {
				iconCssClass: 'text-editor',
			},
		};

		const STR_HOST = 'host';

		const STRINGS = 'strings';

		const STR_VALUE = 'value';

		const LiferayAlloyEditorSource = A.Component.create({
			ATTRS: {
				strings: {
					value: {
						cancel: Liferay.Language.get('cancel'),
						done: Liferay.Language.get('done'),
						editContent: Liferay.Language.get('edit-content'),
					},
				},
			},

			EXTENDS: A.Plugin.Base,

			NAME: 'liferayalloyeditorsource',

			NS: 'liferayalloyeditorsource',

			prototype: {
				_createSourceEditor() {
					const instance = this;

					const host = instance.get(STR_HOST);

					const sourceEditor = new A.LiferaySourceEditor({
						boundingBox: instance._editorSource,
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
						value: host.getHTML(),
					}).render();

					instance._toggleEditorModeUI();

					instance._sourceEditor = sourceEditor;
				},

				_getEditorStateLexiconIcon() {
					const instance = this;

					let icon;

					const currentState = MAP_TOGGLE_STATE[instance._isVisible];

					if (currentState.icon) {
						icon = currentState.icon.cloneNode(true);
					}
					else {
						icon = Liferay.Util.getLexiconIcon(
							currentState.iconCssClass
						);

						currentState.icon = icon;
					}

					return icon;
				},

				_getHTML() {
					const instance = this;

					const sourceEditor = instance._sourceEditor;

					if (sourceEditor && instance._isVisible) {
						const text = sourceEditor.get('value');

						return new A.Do.AlterReturn(
							'Modified source editor text',
							text
						);
					}
				},

				_onEditorUpdate(event) {
					const instance = this;

					instance._toggleSourceSwitchFn(event.data.state);
				},

				_onFullScreenBtnClick() {
					const instance = this;

					const host = instance.get(STR_HOST);
					const strings = instance.get(STRINGS);

					let fullScreenEditor = instance._fullScreenEditor;

					Liferay.Util.openModal({
						bodyHTML: '<div id="fullScreenEditorBody"></div>',
						buttons: [
							{
								label: strings.cancel,
								onClick: ({processClose}) => {
									processClose();
								},
							},
							{
								displayType: 'primary',
								label: strings.done,
								onClick: ({processClose}) => {
									processClose();
									instance._switchMode({
										content: fullScreenEditor.get('value'),
									});
								},
							},
						],
						className:
							'lfr-fullscreen-source-editor-dialog modal-full-screen',
						containerProps: {},
						onOpen: () => {
							Liferay.Util.getTop()
								.AUI()
								.use(
									'liferay-fullscreen-source-editor',
									(A) => {
										fullScreenEditor =
											new A.LiferayFullScreenSourceEditor(
												{
													boundingBox:
														document.getElementById(
															'fullScreenEditorBody'
														),
													dataProcessor:
														host.getNativeEditor()
															.dataProcessor,
													previewCssClass:
														'alloy-editor alloy-editor-placeholder',
													value: host.getHTML(),
												}
											).render();

										instance._fullScreenEditor =
											fullScreenEditor;
									}
								);
						},
						title: strings.editContent,
					});
				},

				_onSwitchBlur() {
					const instance = this;

					instance._isFocused = false;

					instance._toggleSourceSwitchFn({
						hidden: true,
					});
				},

				_onSwitchFocus() {
					const instance = this;

					instance._isFocused = true;

					instance._toggleSourceSwitchFn({
						hidden: false,
					});
				},

				_onSwitchMouseDown() {
					const instance = this;

					instance._isClicked = true;
				},

				_onSwitchMouseOut() {
					const instance = this;

					instance._isClicked = false;
				},

				_setHTML(value) {
					const instance = this;

					const sourceEditor = instance._sourceEditor;

					if (sourceEditor && instance._isVisible) {
						sourceEditor.set(STR_VALUE, value);
					}
				},

				_switchMode(event) {
					const instance = this;

					instance._isClicked = false;

					const host = instance.get(STR_HOST);

					const editor = instance._sourceEditor;

					if (instance._isVisible) {
						const content =
							event.content ||
							(editor ? editor.get(STR_VALUE) : '');

						host.setHTML(content);

						instance._toggleEditorModeUI();
					}
					else if (editor) {
						const currentContent = event.content || host.getHTML();

						if (currentContent !== editor.get(STR_VALUE)) {
							editor.set(STR_VALUE, currentContent);
						}

						instance._toggleEditorModeUI();
					}
					else {
						instance._createSourceEditor();
					}
				},

				_switchTheme() {
					const instance = this;

					instance._sourceEditor.switchTheme();
				},

				_toggleEditorModeUI() {
					const instance = this;

					const editorContent = instance._editorContent;
					const editorFullscreen = instance._editorFullscreen;
					const editorSwitch = instance._editorSwitch;
					const editorSwitchContainer = editorSwitch.ancestor();
					const editorSwitchTheme = instance._editorSwitchTheme;
					const editorWrapper = instance._editorWrapper;

					editorContent.toggleClass('hide');
					editorWrapper.toggleClass(CSS_SHOW_SOURCE);
					editorSwitchContainer.toggleClass(CSS_SHOW_SOURCE);
					editorFullscreen.toggleClass('hide');
					editorSwitchTheme.toggleClass('hide');

					instance._isVisible =
						editorWrapper.hasClass(CSS_SHOW_SOURCE);

					editorSwitch
						.one('.lexicon-icon')
						.replace(instance._getEditorStateLexiconIcon());
					editorSwitch.setAttribute(
						'data-title',
						instance._isVisible
							? Liferay.Language.get('text-view')
							: Liferay.Language.get('code-view')
					);

					instance._toggleSourceSwitchFn({
						hidden: true,
					});
				},

				_toggleSourceSwitch(editorState) {
					const instance = this;

					let showSourceSwitch = true;

					if (!instance._isClicked) {
						showSourceSwitch =
							instance._isVisible ||
							instance._isFocused ||
							!editorState.hidden;
					}

					instance._editorSwitch
						.ancestor()
						.toggleClass('hide', !showSourceSwitch);
				},

				destructor() {
					const instance = this;

					const sourceEditor = instance._sourceEditor;

					if (sourceEditor) {
						sourceEditor.destroy();
					}

					const fullScreenEditor = instance._fullScreenEditor;

					if (fullScreenEditor) {
						fullScreenEditor.destroy();
					}

					new A.EventHandle(instance._eventHandles).detach();
				},

				initializer() {
					const instance = this;

					const host = instance.get(STR_HOST);

					instance._editorContent = host._srcNode;
					instance._editorFullscreen = host.one('#Fullscreen');
					instance._editorSource = host.one('#Source');
					instance._editorSwitch = host.one('#Switch');
					instance._editorSwitchTheme = host.one('#SwitchTheme');
					instance._editorWrapper = host.one('#Wrapper');

					instance._toggleSourceSwitchFn = A.debounce(
						instance._toggleSourceSwitch,
						100,
						instance
					);

					host.getNativeEditor().on(
						'editorUpdate',
						A.bind('_onEditorUpdate', instance)
					);

					instance._eventHandles = [
						instance._editorFullscreen.on(
							'click',
							instance._onFullScreenBtnClick,
							instance
						),
						instance._editorSwitch.on(
							'blur',
							instance._onSwitchBlur,
							instance
						),
						instance._editorSwitch.on(
							'click',
							instance._switchMode,
							instance
						),
						instance._editorSwitch.on(
							'focus',
							instance._onSwitchFocus,
							instance
						),
						instance._editorSwitch.on(
							'mousedown',
							instance._onSwitchMouseDown,
							instance
						),
						instance._editorSwitch.on(
							'mouseout',
							instance._onSwitchMouseOut,
							instance
						),
						instance._editorSwitchTheme.on(
							'click',
							instance._switchTheme,
							instance
						),
						instance.doAfter(
							'getHTML',
							instance._getHTML,
							instance
						),
						instance.doAfter(
							'setHTML',
							instance._setHTML,
							instance
						),
					];
				},
			},
		});

		A.Plugin.LiferayAlloyEditorSource = LiferayAlloyEditorSource;
	},
	'',
	{
		requires: [
			'aui-debounce',
			'liferay-fullscreen-source-editor',
			'liferay-source-editor',
			'plugin',
		],
	}
);
