/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render as renderElement} from '@liferay/frontend-js-react-web';
import {isNullOrUndefined} from '@liferay/layout-js-components-web';
import {CKEditor5BalloonEditor, TEditor} from 'frontend-editor-ckeditor-web';
import {loadEditorClientExtensions} from 'frontend-js-web';

import {EditableConfig} from '../../types/editables/EditableValue';
import {config} from '../config/index';
import getEditorConfig, {EditorConfig} from './getEditorConfig';

type ChangeCallback = (data: string) => Promise<void>;

type DestroyCallback = () => void;

type EditorType = 'text' | 'rich-text';

type RenderFunction = (
	element: HTMLElement,
	value: string,
	editableConfig?: EditableConfig
) => void;

type State = {
	callbacks: {
		changeCallback?: ChangeCallback;
		destroyCallback?: DestroyCallback;
	};
	editor: TEditor | null;
	element: HTMLElement | null;
	eventHandlers: {
		callback: (...args: any[]) => void;
		emitter: any;
		event: string;
	}[];
};

const INITIAL_STATE = {
	callbacks: {},
	editor: null,
	element: null,
	eventHandlers: [],
};

export default function getEditorProcessor(
	editorType: EditorType,
	getEditorWrapper = defaultGetEditorWrapper,
	render: RenderFunction = defaultRender
) {
	let state: State = INITIAL_STATE;

	const onBlurEditor = (editor: TEditor) => {
		const {changeCallback, destroyCallback} = state.callbacks;

		if (changeCallback) {
			changeCallback(editor.getData()).finally(() => destroyCallback?.());
		}
		else if (destroyCallback) {
			requestAnimationFrame(() => destroyCallback());
		}
	};

	const createEventHandlers = (
		editorName: string,
		itemSelectorEventName: string
	) => {
		if (!state.editor) {
			return [];
		}

		// For the cases where we open the item selector we need to make sure that
		// the editor is destroyed. Since the focus listener does not work for these cases
		// we have to setup an additional listener.

		const onClickOutside = (event: Event) => {
			const target = event.target as Element;

			if (
				state.editor &&
				!target.closest(`[name="${editorName}"]`) &&
				(target.closest('.page-editor__toolbar') ||
					target.closest('.page-editor__wrapper'))
			) {
				onBlurEditor(state.editor);
			}
		};

		const onFocusEditor = (
			event: Event,
			name: string,
			isFocused: boolean
		) => {
			if (!state.editor || isFocused) {
				return;
			}

			// Ignoring the blur event, because we don't want to destroy the editor
			// when opening the item selector.

			if (document.getElementById(itemSelectorEventName)) {
				document.addEventListener('click', onClickOutside);

				return;
			}

			onBlurEditor(state.editor);
		};

		return [
			{
				callback: onClickOutside,
				emitter: {
					off: (event: string, callback: () => void) =>
						document.removeEventListener(event, callback),
				},
				event: 'click',
			},
			{
				callback: onFocusEditor,
				emitter: state.editor.ui.focusTracker,
				event: 'change:isFocused',
			},
		];
	};

	return {
		createEditor: (
			element: HTMLElement,
			changeCallback: ChangeCallback,
			destroyCallback: DestroyCallback
		) => {
			state.callbacks = {changeCallback, destroyCallback};
			state.element = element;

			const {editorConfig} = config.defaultEditorConfigurations[
				editorType
			] as {editorConfig: EditorConfig};

			const editorName = `${config.portletNamespace}FragmentEntryLinkEditable_${state.element?.id}`;
			const itemSelectorEventName = `${editorName}selectItem`;

			const editorWrapper = getEditorWrapper(state.element!);

			editorWrapper.setAttribute('id', editorName);
			editorWrapper.setAttribute('name', editorName);

			const initEditor = (editorConfig: EditorConfig) => {
				renderElement(
					CKEditor5BalloonEditor as any,
					{
						config: getEditorConfig({
							editorConfig,
							editorName,
							initialData: editorWrapper.innerHTML,
							itemSelectorEventName,
						}),
						onReady: (editor: TEditor) => {
							if (!editor) {
								return;
							}

							state.editor = editor;
							editor.focus();

							state.eventHandlers = createEventHandlers(
								editorName,
								itemSelectorEventName
							)!;

							state.eventHandlers.forEach(
								({callback, emitter, event}) => {
									emitter.on?.(event, callback);
								}
							);
						},
					},
					editorWrapper
				);
			};

			const editorTransformerURLs = editorConfig.editorTransformerURLs;

			if (editorTransformerURLs) {
				initEditorWithClientExtensions({
					editorConfig,
					element,
					initEditor,
				});
			}
			else {
				initEditor(editorConfig);
			}
		},

		destroyEditor: (
			element: HTMLElement,
			editableConfig: EditableConfig
		) => {
			if (!state.editor) {
				return;
			}

			const lastValue = state.editor.getData();

			state.callbacks.changeCallback?.(lastValue);

			state.editor.destroy();

			state.eventHandlers.forEach(({callback, emitter, event}) => {
				emitter.off(event, callback);
			});

			if (state.element) {
				render(state.element, lastValue, editableConfig);
			}

			state = INITIAL_STATE;
		},

		render: (
			element: HTMLElement,
			value: string,
			editableConfig: EditableConfig
		) => {
			if (element !== state.element) {
				render(element, value, editableConfig);
			}
		},
	};
}

function defaultGetEditorWrapper(element: HTMLElement) {
	const wrapper = document.createElement('div');

	wrapper.innerHTML = element.innerHTML;

	element.innerHTML = '';
	element.appendChild(wrapper);

	return wrapper;
}

function defaultRender(element: HTMLElement, value: string) {
	if (!isNullOrUndefined(value)) {
		element.innerHTML = value;
	}
}

function initEditorWithClientExtensions({
	editorConfig,
	element,
	initEditor,
}: {
	editorConfig: EditorConfig;
	element: HTMLElement;
	initEditor: (config: EditorConfig) => void;
}) {
	const loadingIndicator = document.createElement('span');

	loadingIndicator.classList.add('loading-animation');
	loadingIndicator.setAttribute('aria-hidden', 'true');

	element?.appendChild(loadingIndicator);

	loadEditorClientExtensions({
		config: editorConfig,
		onLoad: ({transformedConfig}: {transformedConfig: EditorConfig}) => {
			if (loadingIndicator) {
				loadingIndicator.remove();
			}

			initEditor(transformedConfig);
		},
	});
}
