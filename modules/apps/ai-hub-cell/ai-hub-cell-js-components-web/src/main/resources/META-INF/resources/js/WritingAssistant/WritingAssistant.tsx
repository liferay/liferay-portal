/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Command, Editor, Plugin} from '@ckeditor/ckeditor5-core/dist/index.js';
import {Model} from '@ckeditor/ckeditor5-engine/dist/index.js';
import {ContextualBalloon, View} from '@ckeditor/ckeditor5-ui/dist/index.js';
import {ModelText, ModelTextProxy, ModelWriter} from 'ckeditor5';
import {cancelDebounce, debounce} from 'frontend-js-web';
import React from 'react';
import {Root, createRoot} from 'react-dom/client';

import ReportFeedbackModal from '../ReportFeedback/ReportFeedbackModal';
import submitPositiveReportFeedback from '../ReportFeedback/submitPositiveReportFeedback';
import {RequestTooLargeError} from '../utils/throwIfRequestTooLarge';
import {createEventSource, postAgentInstance} from './api';
import WritingAssistantActions from './components/WritingAssistantActions';
import WritingAssistantConfirmationAction from './components/WritingAssistantConfirmationAction';
import WritingAssistantDisclaimer from './components/WritingAssistantDisclaimer';
import {EActionType} from './types';
import {fireContentAcceptedEvent} from './utils/disclaimerUtils';

export default class WritingAssistant extends Plugin {
	public balloonView: View | null = null;
	public contentSelection: string = '';
	public disclaimerContainer: HTMLDivElement | null = null;
	public disclaimerRoot: Root | null = null;
	public eventSourceReference: string = '';
	public lastActionType: EActionType | null = null;
	public reactRoot: Root | null = null;
	public reportModalElement: HTMLDivElement | null = null;
	public reportModalRoot: Root | null = null;
	public confirmationBalloonOpen: boolean = false;

	static get requires() {
		return [ContextualBalloon];
	}

	async init() {
		const editor = this.editor;

		const balloon = editor.plugins.get(ContextualBalloon);

		editor.commands.add('writingAssistant', new Command(editor));

		const model = editor.model;

		this._addEventListeners();
		this._addSelectionListener(balloon, editor, model);
		this._createMarker(editor);
	}

	_addEventListeners() {
		createEventSource().then((eventSource) => {
			if (!eventSource) {
				return;
			}

			eventSource.addEventListener('Subscribe', (event) => {
				this.eventSourceReference = event.data;
			});

			Object.values(EActionType).forEach((type) => {
				eventSource.addEventListener(type, (event) => {
					const dataJSON = JSON.parse(event.data);

					this._changeContent(dataJSON['data']);
				});
			});
		});
	}

	_addSelectionListener(
		balloon: ContextualBalloon,
		editor: Editor,
		model: Model
	) {
		const debouncedSelectionCheck = debounce(() => {
			this._selectedContent(model);

			if (this.confirmationBalloonOpen) {
				return;
			}

			if (
				this.contentSelection &&
				!!this.contentSelection.trim().length
			) {
				this._showBalloon(balloon, editor);
			}
			else {
				this._hideBalloon(balloon);
			}
		}, 800);

		const view = editor.editing.view;

		view.document.selection.on('change', () => {
			debouncedSelectionCheck();
		});

		this.editor.on('destroy', () => {
			cancelDebounce(debouncedSelectionCheck);
			this._hideReportFeedbackModal();
		});
	}

	_changeContent(content: string) {
		const editor = this.editor;

		const balloon = editor.plugins.get(ContextualBalloon);

		const model = editor.model;
		const view = editor.editing.view;

		model.change((writer: ModelWriter) => {
			const selection = model.document.selection;

			const range = selection.getFirstRange();

			if (!range) {
				return;
			}

			writer.remove(range);

			const insertPosition = range.start;

			writer.insertText(content, insertPosition);

			const endPosition = writer.createPositionAt(
				insertPosition.parent,
				insertPosition.offset + content.length
			);

			const newRange = writer.createRange(insertPosition, endPosition);

			writer.setSelection(endPosition);

			writer.addMarker('writingAssistantHighlight', {
				affectsData: false,
				range: newRange,
				usingOperation: false,
			});

			view.focus();

			view.scrollToTheSelection();

			this._hideBalloon(balloon);

			this._showConfimationBalloon(balloon, editor);
		});
	}

	_createMarker(editor: Editor) {
		editor.conversion.for('editingDowncast').markerToHighlight({
			model: 'writingAssistantHighlight',
			view: {
				classes: 'writing-assistant__content--highlight',
				priority: 10,
			},
		});
	}

	_fireContentAcceptedEvent() {
		fireContentAcceptedEvent();
	}

	_getBalloonPosition(editor: Editor) {
		const view = editor.editing.view;

		const domConverter = view.domConverter;

		const firstRange = view.document.selection.getFirstRange();

		if (firstRange) {
			const domRange = domConverter.viewRangeToDom(firstRange);

			return {target: domRange};
		}
	}

	_hideBalloon(balloon: ContextualBalloon) {
		if (this.balloonView && balloon.hasView(this.balloonView)) {
			balloon.remove(this.balloonView);
			this.balloonView = null;
		}
	}

	_removeDisclaimerFromEditor() {
		if (this.disclaimerRoot) {
			this.disclaimerRoot.unmount();
			this.disclaimerRoot = null;
		}

		if (this.disclaimerContainer) {
			this.disclaimerContainer.remove();
			this.disclaimerContainer = null;
		}
	}

	_removeMarker(model: Model) {
		model.change((writer: ModelWriter) => {
			const marker = model.markers.get('writingAssistantHighlight');

			if (marker) {
				writer.removeMarker('writingAssistantHighlight');
			}
		});
	}

	_selectedContent(model: Model) {
		const selection = model.document.selection;
		const range = selection.getFirstRange();

		if (!range) {
			this.contentSelection = '';

			return;
		}

		const rangeItems = Array.from(range.getItems());

		const textItems = rangeItems.filter(
			(item): item is ModelText | ModelTextProxy =>
				item.is('$text') || item.is('$textProxy')
		);

		const textData = textItems.map((item) => item.data);

		this.contentSelection = textData.join('');
	}

	_showBalloon(balloon: ContextualBalloon, editor: Editor) {
		if (this.balloonView && balloon.hasView(this.balloonView)) {
			return;
		}

		this._removeDisclaimerFromEditor();

		const reactView = new View();

		reactView.setTemplate({
			tag: 'span',
		});

		reactView.once('render', () => {
			if (!reactView.element) {
				return;
			}

			const root = createRoot(reactView.element);

			root.render(
				<WritingAssistantActions
					containerRef={reactView.element}
					handleActionClick={async (type: EActionType) => {
						this.lastActionType = type;

						try {
							await postAgentInstance(
								this.contentSelection,
								this.eventSourceReference,
								type
							);
						}
						catch (error) {
							Liferay.Util.openToast({
								message:
									error instanceof RequestTooLargeError
										? error.message
										: Liferay.Language.get(
												'an-unexpected-error-occurred'
											),
								type: 'danger',
							});
						}
					}}
					hideBalloon={() => this._hideBalloon(balloon)}
				/>
			);

			this.reactRoot = root;
		});

		this.balloonView = reactView;

		balloon.add({
			position: this._getBalloonPosition(editor),
			view: this.balloonView,
			withArrow: false,
		});
	}

	_hideReportFeedbackModal() {
		if (this.reportModalRoot) {
			this.reportModalRoot.unmount();
			this.reportModalRoot = null;
		}

		if (this.reportModalElement) {
			this.reportModalElement.remove();
			this.reportModalElement = null;
		}
	}

	_showDisclaimerAfterAccept() {
		this._removeDisclaimerFromEditor();

		const editorElement = this.editor.ui.element;

		if (!editorElement?.parentElement) {
			return;
		}

		const container = document.createElement('div');

		this.disclaimerContainer = container;

		editorElement.parentElement.insertBefore(
			container,
			editorElement.nextSibling
		);

		this.disclaimerRoot = createRoot(container);

		this.disclaimerRoot.render(<WritingAssistantDisclaimer />);
	}

	_showReportFeedbackModal() {
		this._hideReportFeedbackModal();

		const element = document.createElement('div');

		document.body.appendChild(element);

		const root = createRoot(element);

		root.render(
			<ReportFeedbackModal
				agentDefinitionExternalReferenceCodes={
					this.lastActionType ? [this.lastActionType] : []
				}
				onClose={() => this._hideReportFeedbackModal()}
				surface="writingAssistant"
			/>
		);

		this.reportModalElement = element;
		this.reportModalRoot = root;
	}

	_showConfimationBalloon(balloon: ContextualBalloon, editor: Editor) {
		if (this.balloonView && balloon.hasView(this.balloonView)) {
			return;
		}

		this.confirmationBalloonOpen = true;

		const reactView = new View();

		reactView.setTemplate({
			attributes: {
				class: 'custom-react-balloon',
			},
			tag: 'div',
		});

		reactView.once('render', () => {
			if (!reactView.element) {
				return;
			}

			const root = createRoot(reactView.element);

			root.render(
				<WritingAssistantConfirmationAction
					containerRef={reactView.element}
					handleAccept={() => {
						this._removeMarker(editor.model);
						this._fireContentAcceptedEvent();
						this._showDisclaimerAfterAccept();
					}}
					handleDiscard={() => {
						editor.execute('undo');
						editor.model.change((writer: ModelWriter) => {
							writer.setSelection(null);
						});
						this._removeMarker(editor.model);
					}}
					hideBalloon={() => {
						this.confirmationBalloonOpen = false;
						this._hideBalloon(balloon);
					}}
					onReport={() => this._showReportFeedbackModal()}
					onThumbsUp={() =>
						submitPositiveReportFeedback({
							agentDefinitionExternalReferenceCodes: this
								.lastActionType
								? [this.lastActionType]
								: [],
							surface: 'writingAssistant',
						})
					}
				/>
			);

			this.reactRoot = root;
		});

		this.balloonView = reactView;

		balloon.add({
			position: this._getBalloonPosition(editor),
			view: this.balloonView,
			withArrow: false,
		});
	}
}
