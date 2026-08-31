/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {all, isVisible} from './dom';

const CSS_DRAGGING = 'lfr-form-row-dragging';

/**
 * Elements that keep their own behavior instead of starting a drag. Dragging
 * begins on pointer down and suppresses the default action, so without this a
 * row whose handle covers the whole row would swallow the click that focuses a
 * field. A.Sortable excluded the same set through the "invalid" attribute of
 * A.DD.Drag.
 */
const SELECTOR_INVALID_HANDLES = 'a, button, input, select, textarea';

/**
 * Vertical drag and drop reordering for the rows of a single container,
 * replacing <code>A.Sortable</code>.
 *
 * The pointer listener is delegated on the container, so rows added after
 * construction are draggable without being registered. That also removes the
 * need for the per row teardown <code>A.DD.DDM.getDrag(node).destroy()</code>
 * the AlloyUI version performed, since no state is held per row.
 *
 * As in the original, the row itself is moved while dragging instead of a
 * proxy node being displayed.
 */
export default class Sortable {
	constructor({container, handle, nodes}) {
		this._container = container;
		this._handle = handle;
		this._nodes = nodes;

		this._offsetY = 0;
		this._row = null;

		this._onPointerDown = (event) => this._handlePointerDown(event);
		this._onPointerMove = (event) => this._handlePointerMove(event);
		this._onPointerUp = () => this._handlePointerUp();

		container.addEventListener('pointerdown', this._onPointerDown);
	}

	destroy() {
		this._handlePointerUp();

		this._container.removeEventListener('pointerdown', this._onPointerDown);
	}

	_handlePointerDown(event) {
		if (event.button !== 0) {
			return;
		}

		const target = event.target;

		if (
			target.closest(SELECTOR_INVALID_HANDLES) ||
			target.closest('.lfr-autorow-controls')
		) {
			return;
		}

		if (this._handle && !target.closest(this._handle)) {
			return;
		}

		const row = target.closest(this._nodes);

		if (!row || !this._container.contains(row)) {
			return;
		}

		event.preventDefault();

		this._offsetY = event.clientY - row.getBoundingClientRect().top;
		this._row = row;

		row.classList.add(CSS_DRAGGING);

		this._container.style.userSelect = 'none';

		window.addEventListener('pointermove', this._onPointerMove);
		window.addEventListener('pointerup', this._onPointerUp);
		window.addEventListener('pointercancel', this._onPointerUp);
	}

	_handlePointerMove(event) {
		const row = this._row;

		if (!row) {
			return;
		}

		const rows = this._rows().filter((item) => item !== row);

		// Compare where the row would sit if it followed the pointer, not
		// where the pointer is. Using the pointer makes the distance needed to
		// move a row depend on where it was grabbed, so dragging downwards has
		// to cross a whole extra row before anything happens.

		const midpoint =
			event.clientY -
			this._offsetY +
			row.getBoundingClientRect().height / 2;

		for (const item of rows) {
			const rect = item.getBoundingClientRect();

			if (midpoint < rect.top + rect.height / 2) {
				if (item.previousElementSibling !== row) {
					this._container.insertBefore(row, item);
				}

				return;
			}
		}

		const lastRow = rows[rows.length - 1];

		if (lastRow && lastRow !== row.previousElementSibling) {
			this._container.insertBefore(row, lastRow.nextSibling);
		}
	}

	_handlePointerUp() {
		if (!this._row) {
			return;
		}

		this._row.classList.remove(CSS_DRAGGING);

		this._row = null;

		this._container.style.userSelect = '';

		window.removeEventListener('pointermove', this._onPointerMove);
		window.removeEventListener('pointerup', this._onPointerUp);
		window.removeEventListener('pointercancel', this._onPointerUp);
	}

	_rows() {
		return all(this._container, this._nodes).filter(isVisible);
	}
}
