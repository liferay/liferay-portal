/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-auto-fields',
	(A) => {

		// eslint-disable-next-line @liferay/aui/no-object
		const AObject = A.Object;
		const Lang = A.Lang;

		const CSS_ACTION_CLEAR = 'float-right lfr-action-clear';

		const CSS_ACTION_UNDO = 'float-left lfr-action-undo';

		const CSS_HELPER_CLEARFIX = 'helper-clearfix';

		const CSS_ICON_LOADING = 'loading-animation';

		const CSS_ITEMS_LEFT = 'lfr-items-left';

		const CSS_MESSAGE_INFO = 'alert alert-info';

		const CSS_QUEUE = 'lfr-undo-queue mx-auto my-2';

		const CSS_QUEUE_EMPTY = 'lfr-queue-empty d-none';

		const CSS_QUEUE_ITEMS = 'd-flex justify-content-between';

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
			Liferay.Util.getLexiconIconTpl('plus') +
			'</button>';

		const TPL_ACTION_CLEAR =
			'<a class="' +
			CSS_ACTION_CLEAR +
			'" href="javascript:void(0);"></a>';

		const TPL_ACTION_UNDO =
			'<a class="' +
			CSS_ACTION_UNDO +
			'" href="javascript:void(0);"></a>';

		const TPL_DELETE_BUTTON =
			'<button class="btn btn-icon-only btn-monospaced btn-primary delete-row toolbar-item toolbar-last" title="' +
			Liferay.Language.get('remove') +
			'" type="button">' +
			Liferay.Util.getLexiconIconTpl('hr') +
			'</button>';

		const TPL_AUTOROW_CONTROLS =
			'<span class="lfr-autorow-controls toolbar toolbar-horizontal">' +
			'<span class="toolbar-content">' +
			TPL_DELETE_BUTTON +
			TPL_ADD_BUTTON +
			'</span>' +
			'</span>';

		const TPL_LOADING = '<div class="' + CSS_ICON_LOADING + '"></div>';

		const TPL_UNDO_TEXT = '<span class="' + CSS_ITEMS_LEFT + '">(0)</span>';

		const UndoManager = A.Component.create({
			ATTRS: {
				location: {
					value: 'top',
				},
			},

			NAME: 'undomanager',

			prototype: {
				_afterUndoManagerRender() {
					const instance = this;

					const location = instance.get('location');

					if (location !== false) {
						const boundingBox = instance.get('boundingBox');
						const boundingBoxParent = boundingBox.get('parentNode');

						let action = 'append';

						if (location === 'top') {
							action = 'prepend';
						}

						boundingBoxParent[action](boundingBox);
					}
				},

				_onActionClear() {
					const instance = this;

					instance.clear();
				},

				_onActionUndo() {
					const instance = this;

					instance.undo(1);
				},

				_updateList() {
					const instance = this;

					const itemsLeft = instance._undoCache.size();

					const contentBox = instance.get('contentBox');

					let action = 'removeClass';

					if (itemsLeft > 0) {
						action = 'addClass';
					}

					contentBox[action](CSS_QUEUE_ITEMS);

					instance._undoItemsLeft.text('(' + itemsLeft + ')');
				},

				add(handler, stateData) {
					const instance = this;

					if (Lang.isFunction(handler)) {
						const undo = {
							handler,
							stateData,
						};

						instance._undoCache.insert(0, undo);

						const eventData = {
							undo,
						};

						instance.fire('update', eventData);
						instance.fire('add', eventData);
					}
				},

				bindUI() {
					const instance = this;

					instance._actionClear.on(
						'click',
						instance._onActionClear,
						instance
					);
					instance._actionUndo.on(
						'click',
						instance._onActionUndo,
						instance
					);

					instance.after('render', instance._afterUndoManagerRender);
				},

				clear() {
					const instance = this;

					instance._undoCache.clear();

					instance.fire('update');
					instance.fire('clearList');
				},

				initializer() {
					const instance = this;

					instance._undoCache = new A.DataSet();
				},

				renderUI() {
					const instance = this;

					const clearText = Liferay.Language.get('clear-history');
					let undoText = Liferay.Language.get('undo-x');

					undoText = Lang.sub(undoText, [TPL_UNDO_TEXT]);

					const contentBox = instance.get('contentBox');

					const actionClear = A.Node.create(TPL_ACTION_CLEAR);
					const actionUndo = A.Node.create(TPL_ACTION_UNDO);

					actionClear.append(clearText);
					actionUndo.append(undoText);

					contentBox.appendChild(actionUndo);
					contentBox.appendChild(actionClear);

					contentBox.addClass(CSS_HELPER_CLEARFIX);
					contentBox.addClass(CSS_MESSAGE_INFO);
					contentBox.addClass(CSS_QUEUE);
					contentBox.addClass(CSS_QUEUE_EMPTY);

					instance.after('update', instance._updateList);

					instance._undoItemsLeft = contentBox.one(
						'.' + CSS_ITEMS_LEFT
					);

					instance._actionClear = actionClear;
					instance._actionUndo = actionUndo;
				},

				undo(limit) {
					const instance = this;

					limit = limit || 1;

					const undoCache = instance._undoCache;

					undoCache.each((item, index) => {
						if (index < limit) {
							item.handler.call(instance, item.stateData);

							undoCache.removeAt(0);
						}
					});

					instance.fire('update');
					instance.fire('undo');
				},
			},
		});

		/**
		 * OPTIONS
		 *
		 * Required
		 * container {string|object}: A selector that contains the rows you wish to duplicate.
		 * baseRows {string|object}: A selector that defines which fields are duplicated.
		 *
		 * Optional
		 * fieldIndexes {string}: The name of the POST parameter that will contain a list of the order for the fields.
		 * sortable{boolean}: Whether or not the rows should be sortable
		 * sortableHandle{string}: A selector that defines a handle for the sortables
		 *
		 */

		const AutoFields = A.Component.create({
			AUGMENTS: [Liferay.PortletBase],

			EXTENDS: A.Base,

			NAME: 'autofields',

			prototype: {
				_addHandleClass(node) {
					const instance = this;

					const sortableHandle = instance.config.sortableHandle;

					if (sortableHandle) {
						node.all(sortableHandle).addClass(
							'handle-sort-vertical'
						);
					}
				},

				_attachSubmitListener() {
					Liferay.on(
						'submitForm',
						A.bind('fire', Liferay, 'saveAutoFields')
					);

					AutoFields.prototype._attachSubmitListener = Lang.emptyFn;
				},

				_clearForm(node) {
					node.all('input, select, textarea').each((item) => {
						const tag = item.get('nodeName').toLowerCase();

						const type = item.getAttribute('type');

						if (
							type === 'text' ||
							type === 'password' ||
							tag === 'textarea'
						) {
							item.val('');
						}
						else if (type === 'checkbox' || type === 'radio') {
							item.attr('checked', false);
						}
						else if (tag === 'select') {
							let selectedIndex = 0;

							if (item.getAttribute('showEmptyOption')) {
								selectedIndex = -1;
							}

							item.attr('selectedIndex', selectedIndex);
						}
					});

					CSS_VALIDATION_HELPER_CLASSES.forEach((item) => {
						node.all('.' + item).removeClass(item);
					});
				},

				_clearHiddenRows(item) {
					const instance = this;

					if (instance._isHiddenRow(item)) {
						item.remove(true);
					}
				},

				_clearInputsLocalized(node) {
					node.all('.language-value').attr('placeholder', '');
					node.all('.form-text:not(.form-text-repeat)').setHTML('');
				},

				_createClone(node) {
					const instance = this;

					const currentRow = node;

					const clone = currentRow.clone();

					const guid = instance._guid++;

					const formValidator = instance._getFormValidator(node);

					const paletteIsCloned =
						clone.one("[id$='PaletteBoundingBox']") !== null;

					const inputsLocalized = node.all('.language-value');

					let clonedRow;

					if (!!inputsLocalized._nodes.length && !paletteIsCloned) {
						const trigger = clone.one('button');

						const currentButton = currentRow.one('button');

						const currentMenu = currentButton.getData('menu');

						const currentMenuListContainer =
							currentButton.getData('menuListContainer');

						trigger.setData('menu', currentMenu);

						trigger.setData(
							'menuListContainer',
							currentMenuListContainer
						);

						const list = A.Node.create(
							'<ul class="dropdown-menu dropdown-menu-left-side"></ul>'
						);

						trigger.placeAfter(list);
					}

					if (instance.url) {
						clonedRow = instance._createCloneFromURL(clone, guid);
					}
					else {
						clonedRow = instance._createCloneFromMarkup(
							clone,
							guid,
							formValidator,
							inputsLocalized
						);
					}

					return clonedRow;
				},

				_createCloneFromMarkup(
					node,
					guid,
					formValidator,
					inputsLocalized
				) {
					const instance = this;

					let fieldStrings;

					let rules;

					if (formValidator) {
						fieldStrings = formValidator.get('fieldStrings');

						rules = formValidator.get('rules');
					}

					node.all('button, input, select, textarea, span, div').each(
						(item) => {
							const inputNodeName = item.attr('nodeName');
							const inputType = item.attr('type');

							const oldId = item.attr('id');
							let oldName = item.attr('name') || oldId;

							const newId = oldId.replace(
								/([0-9]+)([_A-Za-z]*)$/,
								guid + '$2'
							);

							const newName = oldName.replace(
								/([0-9]+)([_A-Za-z]*)$/,
								guid + '$2'
							);

							if (inputType === 'radio') {
								oldName = item.attr('id');

								item.attr('checked', '');
								item.attr('name', newName);
								item.attr('id', newId);
							}
							else if (
								inputNodeName === 'button' ||
								inputNodeName === 'div' ||
								inputNodeName === 'span'
							) {
								if (oldId) {
									item.attr('id', newId);
								}
							}
							else {
								item.attr('name', newName);
								item.attr('id', newId);
							}

							if (fieldStrings && fieldStrings[oldName]) {
								fieldStrings[newName] = fieldStrings[oldName];
							}

							if (rules && rules[oldName]) {
								rules[newName] = rules[oldName];
							}

							if (item.attr('aria-describedby')) {
								item.attr(
									'aria-describedby',
									newName + '_desc'
								);
							}

							node.all('label[for=' + oldId + ']').attr(
								'for',
								newId
							);
						}
					);

					instance._clearInputsLocalized(node);

					instance.once('clone', () => {
						inputsLocalized.each((item) => {
							const inputId = item.attr('id');

							instance._registerInputLocalized(
								Liferay.InputLocalized._instances[inputId],
								guid
							);
						});
					});

					node.all('.form-validator-stack').remove();
					node.all('.help-inline').remove();

					instance._clearForm(node);

					node.all('input[type=hidden]').val('');

					return node;
				},

				_createCloneFromURL(node, guid) {
					const instance = this;

					const contentBox = node.one('> div');

					contentBox.html(TPL_LOADING);

					contentBox.plug(A.Plugin.ParseContent);

					const data = {
						index: guid,
					};

					const namespace = instance.urlNamespace
						? instance.urlNamespace
						: instance.namespace;

					const namespacedData = Liferay.Util.ns(namespace, data);

					Liferay.Util.fetch(instance.url, {
						body: Liferay.Util.objectToFormData(namespacedData),
						method: 'POST',
					})
						.then((response) => response.text())
						.then((response) => contentBox.setContent(response));

					return node;
				},

				_getFormValidator(node) {
					let formValidator;

					const form = node.ancestor('form');

					if (form) {
						const formId = form.attr('id');

						formValidator = Liferay.Form.get(formId).formValidator;
					}

					return formValidator;
				},

				_guid: 0,

				_isHiddenRow(row) {
					return row.hasClass(row._hideClass || 'hide');
				},

				_makeSortable(sortableHandle) {
					const instance = this;

					const rows = instance._contentBox.all('.lfr-form-row');

					instance._addHandleClass(rows);

					instance._sortable = new A.Sortable({
						container: instance._contentBox,
						handles: [sortableHandle],
						nodes: '.lfr-form-row',
						opacity: 0,
					});

					instance._undoManager.on('clearList', () => {
						rows.all('.lfr-form-row').each((item) => {
							if (instance._isHiddenRow(item)) {
								A.DD.DDM.getDrag(item).destroy();
							}
						});
					});
				},

				_registerInputLocalized(inputLocalized, guid) {
					const inputLocalizedId = inputLocalized
						.get('id')
						.replace(/([0-9]+)$/, guid);

					const inputLocalizedNamespace =
						inputLocalized.get('namespace');

					const inputLocalizedNamespaceId = `${inputLocalizedNamespace}${inputLocalizedId}`;

					Liferay.InputLocalized.register(inputLocalizedNamespaceId, {
						adminMode: inputLocalized.get('adminMode'),
						availableLocales:
							inputLocalized.get('availableLocales'),
						boundingBox: `#${inputLocalizedNamespaceId}PaletteBoundingBox`,
						columns: inputLocalized.get('columns'),
						contentBox: `#${inputLocalizedNamespaceId}PaletteContentBox`,
						defaultLanguageId:
							inputLocalized.get('defaultLanguageId'),
						fieldPrefix: inputLocalized.get('fieldPrefix'),
						fieldPrefixSeparator: inputLocalized.get(
							'fieldPrefixSeparator'
						),
						frontendJsComponentsWebModule: inputLocalized.get(
							'frontendJsComponentsWebModule'
						),
						frontendJsReactWebModule: inputLocalized.get(
							'frontendJsReactWebModule'
						),
						frontendJsStateWebModule: inputLocalized.get(
							'frontendJsStateWebModule'
						),
						helpMessage: inputLocalized.get('helpMessage'),
						id: inputLocalizedId,
						inputBox: `#${inputLocalizedNamespaceId}BoundingBox`,
						inputPlaceholder: '#' + inputLocalizedNamespaceId,
						items: inputLocalized.get('items'),
						itemsError: inputLocalized.get('itemsError'),
						languagesDropdownDirection: inputLocalized.get(
							'languagesDropdownDirection'
						),
						languagesTranslationsAriaLabels: inputLocalized.get(
							'languagesTranslationsAriaLabels'
						),
						lazy: inputLocalized.get('lazy'),
						name: inputLocalizedId,
						namespace: inputLocalized.get('namespace'),
						selected: inputLocalized
							.get('items')
							.indexOf(inputLocalized.getSelectedLanguageId()),
						selectedLanguageId:
							inputLocalized.get('selectedLanguageId'),
						toggleSelection: inputLocalized.get('toggleSelection'),
						translatedLanguages: inputLocalized.get(
							'translatedLanguages'
						),
					});

					const inputLocalizedMenuId = `${inputLocalizedNamespace}${inputLocalizedNamespaceId}Menu`;

					Liferay.Menu.register(inputLocalizedMenuId);
				},

				_updateContentButtons() {
					const instance = this;

					const minimumRows = instance.minimumRows;

					if (minimumRows) {
						const deleteRowButtons = instance._contentBox.all(
							'.lfr-form-row:not(.hide) .delete-row'
						);

						Liferay.Util.toggleDisabled(
							deleteRowButtons,
							deleteRowButtons.size() <= minimumRows
						);
					}
				},

				addRow(node) {
					const instance = this;

					const clone = instance._createClone(node);

					clone.resetId();

					node.placeAfter(clone);

					const input = clone.one(
						'input[type=text], input[type=password], textarea'
					);

					if (input) {
						Liferay.Util.focusFormField(input);
					}

					instance._updateContentButtons();

					instance.fire('clone', {
						guid: instance._guid,
						originalRow: node,
						row: clone,
					});

					if (instance._sortable) {
						instance._addHandleClass(clone);
					}
				},

				deleteRow(node) {
					const instance = this;

					const contentBox = instance._contentBox;

					const visibleRows = contentBox
						.all('.lfr-form-row')
						.getDOMNodes()
						.filter((node) => {
							const computedStyle = window.getComputedStyle(node);

							return (
								computedStyle.display !== 'none' &&
								computedStyle.visibility !== 'collapse' &&
								computedStyle.visibility !== 'hidden'
							);
						});

					const visibleRowsLength = visibleRows.length;

					let deleteRow = visibleRowsLength > 1;

					if (visibleRowsLength === 1) {
						instance.addRow(node);

						deleteRow = true;
					}

					if (deleteRow) {
						const form = node.ancestor('form');

						node.hide();

						CSS_VALIDATION_HELPER_CLASSES.forEach((item) => {
							const disabledClass = item + '-disabled';

							node.all('.' + item).replaceClass(
								item,
								disabledClass
							);
						});

						let rules;

						const deletedRules = {};

						const formValidator = instance._getFormValidator(node);

						if (formValidator) {
							const errors = formValidator.errors;

							rules = formValidator.get('rules');

							node.all('input, select, textarea').each((item) => {
								const name =
									item.attr('name') || item.attr('id');

								if (rules && rules[name]) {
									deletedRules[name] = rules[name];

									delete rules[name];
								}

								if (errors && errors[name]) {
									delete errors[name];
								}
							});
						}

						instance._undoManager.add(() => {
							if (rules) {
								AObject.each(deletedRules, (item, index) => {
									rules[index] = item;
								});
							}

							CSS_VALIDATION_HELPER_CLASSES.forEach((item) => {
								const disabledClass = item + '-disabled';

								node.all('.' + disabledClass).replaceClass(
									disabledClass,
									item
								);
							});

							node.show();

							instance._updateContentButtons();

							if (form) {
								form.fire('autofields:update');
							}
						});

						instance.fire('delete', {
							deletedRow: node,
							guid: instance._guid,
						});

						if (form) {
							form.fire('autofields:update');
						}
					}

					instance._updateContentButtons();
				},

				initializer(config) {
					const instance = this;

					instance.config = config;
				},

				render() {
					const instance = this;

					const baseContainer = A.Node.create(
						'<div class="lfr-form-row"><div class="row-fields"></div></div>'
					);

					const config = instance.config;
					const contentBox = A.one(config.contentBox);

					const baseRows = contentBox.all(
						config.baseRows || '.lfr-form-row'
					);

					instance._contentBox = contentBox;
					instance._guid = baseRows.size();

					instance.minimumRows = config.minimumRows;
					instance.namespace = config.namespace;
					instance.url = config.url;
					instance.urlNamespace = config.urlNamespace;

					instance._undoManager = new UndoManager().render(
						contentBox
					);

					if (config.fieldIndexes) {
						instance._fieldIndexes = A.all(
							'[name=' + config.fieldIndexes + ']'
						);

						if (!instance._fieldIndexes.size()) {
							instance._fieldIndexes = A.Node.create(
								'<input name="' +
									config.fieldIndexes +
									'" type="hidden" />'
							);

							contentBox.append(instance._fieldIndexes);
						}
					}
					else {
						instance._fieldIndexes = A.all([]);
					}

					contentBox.delegate(
						'click',
						(event) => {
							const link = event.currentTarget;

							const currentRow = link.ancestor('.lfr-form-row');

							if (link.hasClass('add-row')) {
								instance.addRow(currentRow);
							}
							else if (link.hasClass('delete-row')) {
								link.fire('change');

								instance.deleteRow(currentRow);
							}
						},
						'.lfr-autorow-controls .btn:not(:disabled)'
					);

					baseRows.each((item, index) => {
						let firstChild;
						let formRow;

						if (item.hasClass('lfr-form-row')) {
							formRow = item;
						}
						else {
							formRow = baseContainer.clone();
							firstChild = formRow.one('> div');
							firstChild.append(item);
						}

						formRow.append(TPL_AUTOROW_CONTROLS);

						if (!contentBox.contains(formRow)) {
							contentBox.append(formRow);
						}

						if (index === 0) {
							instance._rowTemplate = formRow.clone();
							instance._clearForm(instance._rowTemplate);
						}
					});

					instance._updateContentButtons();

					if (config.sortable) {
						instance._makeSortable(config.sortableHandle);
					}

					Liferay.on('saveAutoFields', (event) => {
						instance.save(event.form);
					});

					instance._undoManager.on('clearList', () => {
						contentBox
							.all('.lfr-form-row')
							.each(instance._clearHiddenRows, instance);
					});

					instance._attachSubmitListener();

					return instance;
				},

				reset() {
					const instance = this;

					const contentBox = instance._contentBox;

					contentBox.all('.lfr-form-row').each((item) => {
						instance.deleteRow(item);
					});

					instance._undoManager.clear();
				},

				save(form) {
					const instance = this;

					const contentBox = form || instance._contentBox;

					contentBox
						.all('.lfr-form-row')
						.each(instance._clearHiddenRows, instance);

					const fieldOrder = instance.serialize();

					instance._fieldIndexes.val(fieldOrder);
				},

				serialize(filter) {
					const instance = this;

					const visibleRows = instance._contentBox
						.all('.lfr-form-row')
						.each(instance._clearHiddenRows, instance);

					let serializedData = [];

					if (filter) {
						serializedData =
							filter.call(instance, visibleRows) || [];
					}
					else {
						visibleRows.each((item) => {
							const formField = item.one(
								'input, textarea, select'
							);

							let fieldId = formField.attr('id');

							if (!fieldId) {
								fieldId = formField.attr('name');
							}

							fieldId = (fieldId || '').match(/([0-9]+)$/);

							if (fieldId && fieldId[0]) {
								serializedData.push(fieldId[0]);
							}
						});
					}

					return serializedData.join();
				},
			},
		});

		Liferay.AutoFields = AutoFields;
	},
	'',
	{
		requires: [
			'aui-base',
			'aui-data-set-deprecated',
			'aui-parse-content',
			'base',
			'liferay-form',
			'liferay-menu',
			'liferay-portlet-base',
			'sortable',
		],
	}
);
