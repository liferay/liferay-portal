/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	fetch,
	focusFormField,
	getLexiconIconTpl,
	objectToFormData,
	runScriptsInElement,
	toggleDisabled,
} from '../main/index';
import {
	all,
	create,
	guid,
	hide,
	isHidden,
	isVisible,
	replaceCSSClass,
	show,
	toElement,
} from './dom';
import Emitter from './emitter';
import Sortable from './sortable';
import UndoManager from './undo_manager';

const CSS_ICON_LOADING = 'loading-animation';

const CSS_VALIDATION_HELPER_CLASSES = [
	'error',
	'error-field',
	'has-error',
	'success',
	'success-field',
];

const TPL_ADD_BUTTON =
	'<button class="add-row btn btn-icon-only btn-monospaced btn-primary toolbar-first toolbar-item" title="' +
	Liferay.Language.get('add') +
	'" type="button">' +
	getLexiconIconTpl('plus') +
	'</button>';

const TPL_DELETE_BUTTON =
	'<button class="btn btn-icon-only btn-monospaced btn-primary delete-row toolbar-item toolbar-last" title="' +
	Liferay.Language.get('remove') +
	'" type="button">' +
	getLexiconIconTpl('hr') +
	'</button>';

const TPL_AUTOROW_CONTROLS =
	'<span class="lfr-autorow-controls toolbar toolbar-horizontal">' +
	'<span class="toolbar-content">' +
	TPL_DELETE_BUTTON +
	TPL_ADD_BUTTON +
	'</span>' +
	'</span>';

const TPL_BASE_CONTAINER =
	'<div class="lfr-form-row"><div class="row-fields"></div></div>';

const TPL_LOADING = '<div class="' + CSS_ICON_LOADING + '"></div>';

/**
 * Bridges the global form submission event to the per instance
 * <code>saveAutoFields</code> event. A single listener serves every instance in
 * the page, the way the AlloyUI version did by replacing its own prototype
 * method after the first call.
 */
let submitListenerAttached = false;

function attachSubmitListener() {
	if (submitListenerAttached) {
		return;
	}

	submitListenerAttached = true;

	Liferay.on('submitForm', (event) => Liferay.fire('saveAutoFields', event));
}

/**
 * Turns a container of rows into a list the user can grow and shrink.
 *
 * Rows are never rendered by this component. It adopts the markup the server
 * already produced, clones the row the user acted on, and rewrites the trailing
 * index of every <code>id</code> and <code>name</code> inside the clone. Field
 * values therefore stay in the DOM and the form keeps posting natively, either
 * as repeated parameters or, when <code>fieldIndexes</code> is given, as an
 * index list read back by the server.
 *
 * Deletion is deferred: the row is hidden and its validation rules are moved
 * into a closure held by the undo queue. Hidden rows are removed for good when
 * the queue is cleared or when the form is submitted.
 */
export class AutoFields extends Emitter {
	constructor(config) {
		super();

		this.config = config;

		this._guid = 0;
		this._sortable = null;

		for (const [type, fn] of Object.entries(config.on || {})) {
			this.on(type, fn);
		}
	}

	addRow(node) {
		const clone = this._createClone(node);

		clone.setAttribute('id', guid());

		node.parentNode.insertBefore(clone, node.nextSibling);

		const input = clone.querySelector(
			'input[type=text], input[type=password], textarea'
		);

		if (input) {
			focusFormField(input);
		}

		this._updateContentButtons();

		this.fire('clone', {
			guid: this._guid,
			originalRow: node,
			row: clone,
		});

		if (this._sortable) {
			this._addHandleClass(clone);
		}
	}

	deleteRow(node) {
		const visibleRows = all(this._contentBox, '.lfr-form-row').filter(
			isVisible
		);

		let deleteRow = visibleRows.length > 1;

		if (visibleRows.length === 1) {
			this.addRow(node);

			deleteRow = true;
		}

		if (deleteRow) {
			const form = node.closest('form');

			hide(node);

			for (const cssClass of CSS_VALIDATION_HELPER_CLASSES) {
				replaceCSSClass(node, cssClass, cssClass + '-disabled');
			}

			let rules;

			const deletedRules = {};

			const formValidator = this._getFormValidator(node);

			if (formValidator) {
				const errors = formValidator.errors;

				rules = formValidator.get('rules');

				for (const item of all(node, 'input, select, textarea')) {
					const name =
						item.getAttribute('name') || item.getAttribute('id');

					if (rules && rules[name]) {
						deletedRules[name] = rules[name];

						delete rules[name];
					}

					if (errors && errors[name]) {
						delete errors[name];
					}
				}
			}

			this._undoManager.add(() => {
				if (rules) {
					Object.assign(rules, deletedRules);
				}

				for (const cssClass of CSS_VALIDATION_HELPER_CLASSES) {
					replaceCSSClass(node, cssClass + '-disabled', cssClass);
				}

				show(node);

				this._updateContentButtons();

				this._fireFormUpdate(form);
			});

			this.fire('delete', {
				deletedRow: node,
				guid: this._guid,
			});

			this._fireFormUpdate(form);
		}

		this._updateContentButtons();
	}

	render() {
		const config = this.config;

		const contentBox = toElement(config.contentBox);

		const baseRows = all(contentBox, config.baseRows || '.lfr-form-row');

		this._contentBox = contentBox;
		this._guid = baseRows.length;

		this.minimumRows = config.minimumRows;
		this.namespace = config.namespace;
		this.url = config.url;
		this.urlNamespace = config.urlNamespace;

		this._undoManager = new UndoManager(contentBox);

		this._fieldIndexes = this._resolveFieldIndexes(config.fieldIndexes);

		contentBox.addEventListener('click', (event) => {
			const link = event.target.closest(
				'.lfr-autorow-controls .btn:not(:disabled)'
			);

			if (!link || !contentBox.contains(link)) {
				return;
			}

			const currentRow = link.closest('.lfr-form-row');

			if (link.classList.contains('add-row')) {
				this.addRow(currentRow);
			}
			else if (link.classList.contains('delete-row')) {
				link.dispatchEvent(new Event('change', {bubbles: true}));

				this.deleteRow(currentRow);
			}
		});

		for (const item of baseRows) {
			let formRow = item;

			if (!item.classList.contains('lfr-form-row')) {
				formRow = create(TPL_BASE_CONTAINER);

				formRow.querySelector(':scope > div').appendChild(item);
			}

			formRow.appendChild(create(TPL_AUTOROW_CONTROLS));

			if (!contentBox.contains(formRow)) {
				contentBox.appendChild(formRow);
			}
		}

		this._updateContentButtons();

		if (config.sortable) {
			this._makeSortable(config.sortableHandle);
		}

		Liferay.on('saveAutoFields', (event) => this.save(event && event.form));

		this._undoManager.on('clearList', () => {
			for (const item of all(contentBox, '.lfr-form-row')) {
				this._clearHiddenRows(item);
			}
		});

		attachSubmitListener();

		return this;
	}

	reset() {
		for (const item of all(this._contentBox, '.lfr-form-row')) {
			this.deleteRow(item);
		}

		this._undoManager.clear();
	}

	save(form) {
		const container = toElement(form) || this._contentBox;

		for (const item of all(container, '.lfr-form-row')) {
			this._clearHiddenRows(item);
		}

		const fieldOrder = this.serialize();

		for (const fieldIndexes of this._fieldIndexes) {
			fieldIndexes.value = fieldOrder;
		}
	}

	serialize(filter) {
		for (const item of all(this._contentBox, '.lfr-form-row')) {
			this._clearHiddenRows(item);
		}

		const visibleRows = all(this._contentBox, '.lfr-form-row');

		let serializedData = [];

		if (filter) {
			serializedData = filter.call(this, visibleRows) || [];
		}
		else {
			for (const item of visibleRows) {
				const formField = item.querySelector('input, textarea, select');

				if (!formField) {
					continue;
				}

				const fieldId = (
					formField.getAttribute('id') ||
					formField.getAttribute('name') ||
					''
				).match(/([0-9]+)$/);

				if (fieldId && fieldId[0]) {
					serializedData.push(fieldId[0]);
				}
			}
		}

		return serializedData.join();
	}

	_addHandleClass(node) {
		const sortableHandle = this.config.sortableHandle;

		if (!sortableHandle) {
			return;
		}

		for (const item of Array.isArray(node) ? node : [node]) {
			for (const handle of all(item, sortableHandle)) {
				handle.classList.add('handle-sort-vertical');
			}
		}
	}

	_clearForm(node) {
		for (const item of all(node, 'input, select, textarea')) {
			const tag = item.nodeName.toLowerCase();

			const type = item.getAttribute('type');

			if (type === 'text' || type === 'password' || tag === 'textarea') {
				item.value = '';
			}
			else if (type === 'checkbox' || type === 'radio') {
				item.checked = false;
			}
			else if (tag === 'select') {
				item.selectedIndex = item.getAttribute('showEmptyOption')
					? -1
					: 0;
			}
		}

		for (const cssClass of CSS_VALIDATION_HELPER_CLASSES) {
			for (const item of all(node, '.' + cssClass)) {
				item.classList.remove(cssClass);
			}
		}
	}

	_clearHiddenRows(item) {
		if (isHidden(item)) {
			item.remove();
		}
	}

	/**
	 * Help text that is not marked as repeatable belongs to the first row only,
	 * so it is emptied out of every clone.
	 */
	_clearHelpText(node) {
		for (const item of all(node, '.language-value')) {
			item.setAttribute('placeholder', '');
		}

		for (const item of all(node, '.form-text:not(.form-text-repeat)')) {
			item.innerHTML = '';
		}
	}

	_createClone(node) {
		const clone = node.cloneNode(true);

		const rowGuid = this._guid++;

		if (this.url) {
			return this._createCloneFromURL(clone, rowGuid);
		}

		return this._createCloneFromMarkup(
			clone,
			rowGuid,
			this._getFormValidator(node)
		);
	}

	_createCloneFromMarkup(node, rowGuid, formValidator) {
		let fieldStrings;

		let rules;

		if (formValidator) {
			fieldStrings = formValidator.get('fieldStrings');

			rules = formValidator.get('rules');
		}

		for (const item of all(
			node,
			'button, input, select, textarea, span, div'
		)) {
			const inputNodeName = item.nodeName.toLowerCase();
			const inputType = item.getAttribute('type');

			const oldId = item.getAttribute('id') || '';

			let oldName = item.getAttribute('name') || oldId;

			const newId = oldId.replace(
				/([0-9]+)([_A-Za-z]*)$/,
				rowGuid + '$2'
			);

			const newName = oldName.replace(
				/([0-9]+)([_A-Za-z]*)$/,
				rowGuid + '$2'
			);

			if (inputType === 'radio') {
				oldName = oldId;

				item.setAttribute('checked', '');
				item.setAttribute('name', newName);
				item.setAttribute('id', newId);
			}
			else if (
				inputNodeName === 'button' ||
				inputNodeName === 'div' ||
				inputNodeName === 'span'
			) {
				if (oldId) {
					item.setAttribute('id', newId);
				}
			}
			else {
				item.setAttribute('name', newName);
				item.setAttribute('id', newId);
			}

			if (fieldStrings && fieldStrings[oldName]) {
				fieldStrings[newName] = fieldStrings[oldName];
			}

			if (rules && rules[oldName]) {
				rules[newName] = rules[oldName];
			}

			if (item.getAttribute('aria-describedby')) {
				item.setAttribute('aria-describedby', newName + '_desc');
			}

			if (oldId) {
				for (const label of all(node, 'label[for="' + oldId + '"]')) {
					label.setAttribute('for', newId);
				}
			}
		}

		this._clearHelpText(node);

		for (const item of all(node, '.form-validator-stack, .help-inline')) {
			item.remove();
		}

		this._clearForm(node);

		for (const item of all(node, 'input[type=hidden]')) {
			item.value = '';
		}

		return node;
	}

	_createCloneFromURL(node, rowGuid) {
		const rowContentBox = node.querySelector(':scope > div');

		rowContentBox.innerHTML = TPL_LOADING;

		const namespace = this.urlNamespace || this.namespace;

		const namespacedData = Liferay.Util.ns(namespace, {index: rowGuid});

		fetch(this.url, {
			body: objectToFormData(namespacedData),
			method: 'POST',
		})
			.then((response) => response.text())
			.then((response) => {
				rowContentBox.innerHTML = response;

				runScriptsInElement(rowContentBox);
			});

		return node;
	}

	/**
	 * <code>Liferay.Form</code> is still contributed by AlloyUI. It is looked up
	 * lazily because rows are only cloned or deleted through user interaction,
	 * long after the AlloyUI module has been resolved.
	 */
	_getFormValidator(node) {
		const form = node.closest('form');

		if (!form || !Liferay.Form) {
			return undefined;
		}

		const liferayForm = Liferay.Form.get(form.getAttribute('id'));

		return liferayForm && liferayForm.formValidator;
	}

	_fireFormUpdate(form) {
		if (form) {
			form.dispatchEvent(
				new CustomEvent('autofields:update', {bubbles: true})
			);
		}
	}

	_makeSortable(sortableHandle) {
		this._addHandleClass(all(this._contentBox, '.lfr-form-row'));

		this._sortable = new Sortable({
			container: this._contentBox,
			handle: sortableHandle,
			nodes: '.lfr-form-row',
		});
	}

	_resolveFieldIndexes(name) {
		if (!name) {
			return [];
		}

		const fieldIndexes = all(document, '[name="' + name + '"]');

		if (fieldIndexes.length) {
			return fieldIndexes;
		}

		const hiddenInput = create(
			'<input name="' + name + '" type="hidden" />'
		);

		this._contentBox.appendChild(hiddenInput);

		return [hiddenInput];
	}

	_updateContentButtons() {
		const minimumRows = this.minimumRows;

		if (!minimumRows) {
			return;
		}

		const deleteRowButtons = all(
			this._contentBox,
			'.lfr-form-row:not(.hide) .delete-row'
		);

		toggleDisabled(
			deleteRowButtons,
			deleteRowButtons.length <= minimumRows
		);
	}
}

/**
 * Creates an AutoFields instance and renders it, replacing the
 * <code>new Liferay.AutoFields(config).render()</code> call the AlloyUI
 * component required.
 */
export default function autoFields(config) {
	return new AutoFields(config).render();
}
