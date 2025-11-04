/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Command, Editor, Plugin} from '@ckeditor/ckeditor5-core/dist/index.js';
import {Model} from '@ckeditor/ckeditor5-engine/dist/index.js';
import {ContextualBalloon, View} from '@ckeditor/ckeditor5-ui/dist/index.js';
import {ModelText, ModelTextProxy} from 'ckeditor5';
import {EventSource} from 'eventsource';
import React from 'react';
import {Root, createRoot} from 'react-dom/client';

import {getEventSourceConnection, postTasks} from './api';
import WriteAssistentActions from './components/WriteAssistentActions';
import WriteAssistentConfirmatinoAction from './components/WriteAssistentConfimationAction';

export default class WriteAssistent extends Plugin {
	public balloonView: View | null = null;
	public contentSelection: string = '';
	public connection: EventSource | null = null;
	public reactRoot: Root | null = null;

	static get requires() {
		return [ContextualBalloon];
	}

	async init() {
		const editor = this.editor;

		const balloon = editor.plugins.get(ContextualBalloon);

		const commandName = 'writeAssistent';

		editor.commands.add(commandName, new Command(editor));
		const model = editor.model;

		editor.conversion.for('editingDowncast').markerToHighlight({
			model: 'aiHighlight',
			view: {
				classes: 'ai-highlight',
				priority: 10,
			},
		});

		this._onConnect();
		this._selectionChange(balloon, editor, model);
	}

	_changeContent(content: string) {
		const editor = this.editor;

		const balloon = editor.plugins.get(ContextualBalloon);

		const model = editor.model;
		const view = editor.editing.view;

		model.change((writer: any) => {
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

			writer.addMarker('aiHighlight', {
				affectsData: false,
				range: newRange,
				usingOperation: false,
			});

			view.focus();

			view.scrollToTheSelection();

			this._hideBalloon(balloon);

			this._showConfimationBalloon(balloon, editor);
		});

		view.focus();
		view.scrollToTheSelection();
	}

	_getBalloonPosition(editor: any) {
		const view = editor.editing.view;

		const domConverter = view.domConverter;

		const domRange = domConverter.viewRangeToDom(
			view.document.selection.getFirstRange()
		);

		return {target: domRange};
	}

	_hideBalloon(balloon: ContextualBalloon) {
		if (this.balloonView && balloon.hasView(this.balloonView)) {
			balloon.remove(this.balloonView);
			this.balloonView = null;
		}
	}

	_onConnect() {
		const connection = getEventSourceConnection();

		this.connection = connection;

		connection.addEventListener('Improve Writing', (event) => {
			this._changeContent(event.data);
		});
	}

	_removeMarker(model: Model) {
		model.change((writer: any) => {
			const marker = model.markers.get('aiHighlight');

			if (marker) {
				writer.removeMarker('aiHighlight');
			}
		});
	}

	_selectionChange(balloon: ContextualBalloon, editor: Editor, model: Model) {
		let selectionTimeout: NodeJS.Timeout;

		this.listenTo(model.document.selection, 'change:range', () => {
			if (selectionTimeout) {
				clearTimeout(selectionTimeout);
			}

			selectionTimeout = setTimeout(() => {
				this._selectedContent(model);

				if (
					this.contentSelection &&
					!!this.contentSelection.trim().length
				) {
					this._showBalloon(balloon, editor);
				}
				else {
					this._hideBalloon(balloon);
				}
			}, 300);
		});

		this.editor.on('destroy', () => {
			this.stopListening(model.document.selection, 'change:range');

			if (selectionTimeout) {
				clearTimeout(selectionTimeout);
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

	_showBalloon(balloon: ContextualBalloon, editor: any) {
		if (this.balloonView && balloon.hasView(this.balloonView)) {
			return;
		}

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
				<WriteAssistentActions
					connection={this.connection}
					containerRef={reactView.element}
					handleActionClick={async (type: any) => {
						await postTasks(this.contentSelection, type);
					}}
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

	_showConfimationBalloon(balloon: ContextualBalloon, editor: any) {
		if (this.balloonView && balloon.hasView(this.balloonView)) {
			return;
		}

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
				<WriteAssistentConfirmatinoAction
					containerRef={reactView.element}
					handleAccept={() => {
						this._removeMarker(editor.model);
						this._hideBalloon(balloon);
					}}
					handleDiscard={() => {
						editor.execute('undo');
						editor.model.change((writer: any) => {
							writer.setSelection(null);
						});

						this._hideBalloon(balloon);
						this._removeMarker(editor.model);
					}}
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
