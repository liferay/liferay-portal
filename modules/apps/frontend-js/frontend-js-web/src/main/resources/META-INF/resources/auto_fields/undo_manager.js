/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from '../main/index';
import {create} from './dom';
import Emitter from './emitter';

const CSS_ACTION_CLEAR = 'float-right lfr-action-clear';

const CSS_ACTION_UNDO = 'float-left lfr-action-undo';

const CSS_HELPER_CLEARFIX = 'helper-clearfix';

const CSS_ITEMS_LEFT = 'lfr-items-left';

const CSS_MESSAGE_INFO = 'alert alert-info';

const CSS_QUEUE = 'lfr-undo-queue mx-auto my-2';

const CSS_QUEUE_EMPTY = 'lfr-queue-empty d-none';

const CSS_QUEUE_ITEMS = 'd-flex justify-content-between';

const TPL_UNDO_TEXT = '<span class="' + CSS_ITEMS_LEFT + '">(0)</span>';

function toggleCSSClasses(element, cssClasses, force) {
	for (const cssClass of cssClasses.split(' ')) {
		element.classList.toggle(cssClass, force);
	}
}

/**
 * Holds the handlers that undo the deletion of a row.
 *
 * Nothing is serialized: <code>add</code> receives a closure that captures
 * whatever state has to be restored, and <code>undo</code> replays the most
 * recent ones. <code>clear</code> discards the queue, which is the signal
 * AutoFields uses to definitively remove the rows that are still hidden.
 *
 * The <code>undomanager</code> and <code>undomanager-content</code> CSS classes
 * used to be derived by <code>A.Component</code> from the widget name. They are
 * written explicitly because functional tests locate the queue through them.
 */
export default class UndoManager extends Emitter {
	constructor(container, {location = 'top'} = {}) {
		super();

		this._undoCache = [];

		this._render(container, location);

		this.on('update', () => this._updateList());
	}

	add(handler, stateData) {
		if (typeof handler !== 'function') {
			return;
		}

		const undo = {handler, stateData};

		this._undoCache.unshift(undo);

		this.fire('update', {undo});
		this.fire('add', {undo});
	}

	clear() {
		this._undoCache.length = 0;

		this.fire('update');
		this.fire('clearList');
	}

	size() {
		return this._undoCache.length;
	}

	undo(limit = 1) {
		for (let i = 0; i < limit && this._undoCache.length; i++) {
			const undo = this._undoCache.shift();

			undo.handler.call(this, undo.stateData);
		}

		this.fire('update');
		this.fire('undo');
	}

	_render(container, location) {
		const boundingBox = create('<div class="undomanager"></div>');

		const contentBox = create('<div class="undomanager-content"></div>');

		boundingBox.appendChild(contentBox);

		const actionUndo = create(
			'<a class="' + CSS_ACTION_UNDO + '" href="javascript:void(0);"></a>'
		);

		actionUndo.innerHTML = sub(Liferay.Language.get('undo-x'), [
			TPL_UNDO_TEXT,
		]);

		const actionClear = create(
			'<a class="' +
				CSS_ACTION_CLEAR +
				'" href="javascript:void(0);"></a>'
		);

		actionClear.textContent = Liferay.Language.get('clear-history');

		contentBox.appendChild(actionUndo);
		contentBox.appendChild(actionClear);

		toggleCSSClasses(contentBox, CSS_HELPER_CLEARFIX, true);
		toggleCSSClasses(contentBox, CSS_MESSAGE_INFO, true);
		toggleCSSClasses(contentBox, CSS_QUEUE, true);
		toggleCSSClasses(contentBox, CSS_QUEUE_EMPTY, true);

		container.appendChild(boundingBox);

		if (location === 'top') {
			container.insertBefore(boundingBox, container.firstChild);
		}

		actionUndo.addEventListener('click', (event) => {
			event.preventDefault();

			this.undo(1);
		});

		actionClear.addEventListener('click', (event) => {
			event.preventDefault();

			this.clear();
		});

		this._contentBox = contentBox;
		this._undoItemsLeft = contentBox.querySelector('.' + CSS_ITEMS_LEFT);
	}

	/**
	 * <code>lfr-queue-empty d-none</code> is never removed. The queue becomes
	 * visible because <code>d-flex</code> is declared after <code>d-none</code>
	 * in the utility stylesheet and therefore wins. This mirrors the original
	 * behavior and is why both class sets coexist on the element.
	 */
	_updateList() {
		const itemsLeft = this._undoCache.length;

		toggleCSSClasses(this._contentBox, CSS_QUEUE_ITEMS, itemsLeft > 0);

		this._undoItemsLeft.textContent = '(' + itemsLeft + ')';
	}
}
