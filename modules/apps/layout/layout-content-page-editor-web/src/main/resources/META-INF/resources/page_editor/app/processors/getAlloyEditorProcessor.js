/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';
import {openSelectionModal} from 'frontend-js-components-web';
import {debounce, loadEditorClientExtensions} from 'frontend-js-web';

import {SPACE_KEY_CODE} from '../config/constants/keyboardCodes';
import {config} from '../config/index';

const ENTER_KEYCODE = 13;
const ESCAPE_KEYCODE = 27;
const SHIFT_ENTER_KEYCODE = (window.CKEDITOR?.SHIFT ?? 0) + ENTER_KEYCODE;

const defaultGetEditorWrapper = (element) => {
	const wrapper = document.createElement('div');

	wrapper.innerHTML = element.innerHTML;
	element.innerHTML = '';
	element.appendChild(wrapper);

	return wrapper;
};

const defaultRender = (element, value) => {
	if (!isNullOrUndefined(value)) {
		element.innerHTML = value;
	}
};

const keyupHandler = (event) => {
	if (event.code === SPACE_KEY_CODE) {
		event.preventDefault();
	}
};

/**
 * @param {'text'|'rich-text'} editorConfigurationName
 * @param {function} [getEditorWrapper=defaultGetEditorWrapper] Optionally
 *  override getEditorWrapper function, where the editor will be instanciated
 * @param {function} [render=defaultRender] Optionally override render function
 */
export default function getAlloyEditorProcessor(
	editorConfigurationName,
	getEditorWrapper = defaultGetEditorWrapper,
	render = defaultRender
) {
	let _editor;
	let _eventHandlers;
	let _element;
	let _callbacks = {};

	return {
		createEditor: (
			element,
			changeCallback,
			destroyCallback,
			clickPosition
		) => {
			_callbacks.changeCallback = changeCallback;
			_callbacks.destroyCallback = destroyCallback;

			if (_editor) {
				return;
			}

			const {editorConfig} =
				config.defaultEditorConfigurations[editorConfigurationName];

			_element = element;

			const editorName = `${config.portletNamespace}FragmentEntryLinkEditable_${element.id}`;

			const editorWrapper = getEditorWrapper(element);

			editorWrapper.setAttribute('id', editorName);
			editorWrapper.setAttribute('name', editorName);

			element.addEventListener('keyup', keyupHandler);

			const initEditor = (editorConfig) => {
				_editor = AlloyEditor.editable(editorWrapper, {
					...editorConfig,

					documentBrowseLinkCallback: (
						editor,
						url,
						changeLinkCallback
					) => {
						openSelectionModal({
							onSelect: changeLinkCallback,
							selectEventName: editorName + 'selectItem',
							title: Liferay.Language.get('select-item'),
							url,
						});
					},

					documentBrowseLinkUrl:
						editorConfig.documentBrowseLinkUrl.replace(
							'_EDITOR_NAME_',
							editorName
						),

					filebrowserImageBrowseLinkUrl:
						editorConfig.filebrowserImageBrowseLinkUrl.replace(
							'_EDITOR_NAME_',
							editorName
						),

					filebrowserImageBrowseUrl:
						editorConfig.filebrowserImageBrowseUrl.replace(
							'_EDITOR_NAME_',
							editorName
						),

					title: '',
				});

				const nativeEditor = _editor.get('nativeEditor');

				// For the cases where we open the selector we need to make sure that
				// the editor is destroyed. Since we cannot rely on the blur event for these cases
				// (it is ignored) we have to setup an additional listener.

				const onClickOutside = (event) => {
					if (
						!event.target.closest(`[name="${editorName}"]`) &&
						(event.target.closest('.page-editor__toolbar') ||
							event.target.closest('.page-editor__wrapper'))
					) {
						onBlurEditor();
					}
				};

				const onBlurEditor = () => {
					if (_callbacks.changeCallback) {
						_callbacks
							.changeCallback(nativeEditor.getData())
							.then(() => {
								if (_callbacks.destroyCallback) {
									_callbacks.destroyCallback();
								}
							})
							.catch(() => {
								if (_callbacks.destroyCallback) {
									_callbacks.destroyCallback();
								}
							});
					}
					else if (_callbacks.destroyCallback) {
						requestAnimationFrame(() =>
							_callbacks.destroyCallback()
						);
					}
				};

				_eventHandlers = [
					{
						removeListener: () =>
							document.removeEventListener(
								'click',
								onClickOutside
							),
					},
					nativeEditor.on('key', (event) => {
						if (
							(event.data.keyCode === ENTER_KEYCODE ||
								event.data.keyCode === SHIFT_ENTER_KEYCODE) &&
							_element &&
							(_element.getAttribute('type') === 'link' ||
								_element.getAttribute('type') === 'text' ||
								_element.dataset.lfrEditableType === 'link' ||
								_element.dataset.lfrEditableType === 'text')
						) {
							event.cancel();
						}
						else if (event.data.keyCode === ESCAPE_KEYCODE) {
							onBlurEditor();
						}
					}),
					nativeEditor.on('blur', () => {
						if (_editor._mainUI.state.hidden) {
							onBlurEditor();
						}
						else {

							// Ignoring the blur event, because we don't want to destroy the editor
							// when opening a selector (image or link).

							document.addEventListener('click', onClickOutside);
						}
					}),

					nativeEditor.on('instanceReady', (event) => {
						event.editor.dataProcessor.htmlFilter.addRules({
							elements: {
								img(element) {
									element.attributes.alt = '';
								},
							},
						});

						nativeEditor.focus();

						if (clickPosition) {
							_selectRange(clickPosition, nativeEditor);
						}
						else {
							nativeEditor.execCommand('selectAll');
						}
					}),

					nativeEditor.on(
						'saveSnapshot',
						debounce(() => {
							if (_callbacks.changeCallback) {
								_callbacks.changeCallback(
									nativeEditor.getData()
								);
							}
						}, 100)
					),
				];
			};

			const editorTransformerURLs = editorConfig.editorTransformerURLs;

			if (editorTransformerURLs) {
				const loadingIndicator = document.createElement('span');

				loadingIndicator.classList.add('loading-animation');
				loadingIndicator.setAttribute('aria-hidden', true);

				_element.appendChild(loadingIndicator);

				loadEditorClientExtensions({
					config: editorConfig,
					onLoad: ({transformedConfig}) => {
						if (loadingIndicator) {
							loadingIndicator.remove();
						}

						initEditor(transformedConfig);
					},
				});
			}
			else {
				initEditor(editorConfig);
			}
		},

		/**
		 */
		destroyEditor: (element, editableConfig) => {
			if (_editor) {
				const lastValue = _editor.get('nativeEditor').getData();

				_callbacks.changeCallback(lastValue);

				_editor.destroy();

				_eventHandlers.forEach((handler) => {
					handler.removeListener();
				});

				render(_element, lastValue, editableConfig);

				_editor = null;
				_eventHandlers = null;
				_element = null;
				_callbacks = {};
			}

			if (element) {
				element.removeEventListener('keyup', keyupHandler);
			}
		},

		/**
		 * @param {HTMLElement} element HTMLElement that should be mutated with the
		 *  given value.
		 * @param {string} value Element content
		 * @param {Object} editableConfig
		 */
		render: (element, value, editableConfig) => {
			if (element !== _element) {
				render(element, value, editableConfig);
			}
		},
	};
}

/**
 * Place the caret in the click position
 * @param {Event} event
 * @param {CKEditor} nativeEditor
 */
function _selectRange(clickPosition, nativeEditor) {
	const ckRange = nativeEditor.getSelection().getRanges()[0];

	if (document.caretPositionFromPoint) {
		const range = document.caretPositionFromPoint(
			clickPosition.clientX,
			clickPosition.clientY
		);

		const node = range.offsetNode;

		if (isTextNode(node)) {
			ckRange.setStart(CKEDITOR.dom.node(node), range.offset);
			ckRange.setEnd(CKEDITOR.dom.node(node), range.offset);
		}
	}
	else if (document.caretRangeFromPoint) {
		const range = document.caretRangeFromPoint(
			clickPosition.clientX,
			clickPosition.clientY
		);

		const offset = range.startOffset || 0;

		if (
			isTextNode(range.startContainer) &&
			isTextNode(range.endContainer)
		) {
			ckRange.setStart(CKEDITOR.dom.node(range.startContainer), offset);
			ckRange.setEnd(CKEDITOR.dom.node(range.endContainer), offset);
		}
	}

	nativeEditor.getSelection().selectRanges([ckRange]);
}

function isTextNode(node) {
	return node.nodeType === Node.TEXT_NODE;
}
