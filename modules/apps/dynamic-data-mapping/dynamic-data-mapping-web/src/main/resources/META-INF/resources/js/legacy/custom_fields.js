/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-portlet-dynamic-data-mapping-custom-fields',
	(A) => {
		const AArray = A.Array;

		const AEscape = A.Escape;

		const FormBuilderTextField = A.FormBuilderTextField;
		const FormBuilderTypes = A.FormBuilderField.types;

		const LiferayFormBuilderUtil = Liferay.FormBuilder.Util;

		const Lang = A.Lang;

		const booleanOptions = {
			false: Liferay.Language.get('no'),
			true: Liferay.Language.get('yes'),
		};

		const booleanParse = A.DataType.Boolean.parse;
		const camelize = Lang.String.camelize;

		const editorLocalizedStrings = {
			cancel: Liferay.Language.get('cancel'),
			edit: Liferay.Language.get('edit'),
			save: Liferay.Language.get('save'),
		};

		const instanceOf = A.instanceOf;
		const isNull = Lang.isNull;
		const isObject = Lang.isObject;
		const isUndefined = Lang.isUndefined;
		const isValue = Lang.isValue;

		const structureFieldIndexEnable = function () {
			for (let i = 0; i < Liferay.Portlet.list.length; i++) {
				const indexableNode = A.one(
					'#_' + Liferay.Portlet.list[i] + '_indexable'
				);

				if (indexableNode) {
					const indexable = indexableNode.getAttribute('value');

					if (indexable === 'false') {
						return false;
					}
				}
			}

			return true;
		};

		const CSS_FIELD = A.getClassName('field');

		const CSS_FIELD_CHOICE = A.getClassName('field', 'choice');

		const CSS_FIELD_RADIO = A.getClassName('field', 'radio');

		const CSS_FORM_BUILDER_FIELD_NODE = A.getClassName(
			'form-builder-field',
			'node'
		);

		const CSS_RADIO = A.getClassName('radio');

		const DEFAULTS_FORM_VALIDATOR = A.config.FormValidator;

		const LOCALIZABLE_FIELD_ATTRS =
			Liferay.FormBuilder.LOCALIZABLE_FIELD_ATTRS;

		const RESTRICTED_NAME = 'submit';

		const STR_BLANK = '';

		const TPL_COLOR =
			'<input class="field form-control" type="text" value="' +
			A.Escape.html(Liferay.Language.get('color')) +
			'" readonly="readonly">';

		const TPL_GEOLOCATION =
			'<div class="field-labels-inline">' +
			'<img src="' +
			themeDisplay.getPathThemeImages() +
			'/common/geolocation.png" title="' +
			A.Escape.html(Liferay.Language.get('geolocate')) +
			'" />' +
			'<div>';

		const TPL_INPUT_BUTTON =
			'<div class="form-group">' +
			'<input class="field form-control" type="text" value="" readonly="readonly">' +
			'<div class="button-holder">' +
			'<button class="btn btn-secondary select-button" type="button">' +
			'<span class="lfr-btn-label">' +
			A.Escape.html(Liferay.Language.get('select')) +
			'</span>' +
			'</button>' +
			'</div>' +
			'</div>';

		const TPL_PARAGRAPH = '<p></p>';

		const TPL_RADIO =
			'<div class="' +
			CSS_RADIO +
			'">' +
			'<label class="radio-inline" for="{id}">' +
			'<input id="{id}" class="' +
			[
				CSS_FIELD,
				CSS_FIELD_CHOICE,
				CSS_FIELD_RADIO,
				CSS_FORM_BUILDER_FIELD_NODE,
			].join(' ') +
			'" name="{name}" type="radio" value="{value}" {checked} {disabled} />' +
			'{label}' +
			'</label>' +
			'</div>';

		const TPL_SEPARATOR = '<hr class="separator" />';

		const TPL_TEXT_HTML =
			'<textarea class="form-builder-field-node lfr-ddm-text-html"></textarea>';

		const TPL_WCM_IMAGE =
			'<div class="form-group">' +
			'<input class="field form-control" type="text" value="" readonly="readonly">' +
			'<div class="button-holder">' +
			'<button class="btn btn-secondary select-button" type="button">' +
			'<span class="lfr-btn-label">' +
			A.Escape.html(Liferay.Language.get('select')) +
			'</span>' +
			'</button>' +
			'</div>' +
			'<label class="control-label">' +
			A.Escape.html(Liferay.Language.get('image-description')) +
			'</label>' +
			Liferay.Util.getLexiconIconTpl('asterisk') +
			'<input class="field form-control" type="text" value="" disabled>' +
			'</div>';

		const UNIQUE_FIELD_NAMES_MAP =
			Liferay.FormBuilder.UNIQUE_FIELD_NAMES_MAP;

		const UNLOCALIZABLE_FIELD_ATTRS =
			Liferay.FormBuilder.UNLOCALIZABLE_FIELD_ATTRS;

		DEFAULTS_FORM_VALIDATOR.STRINGS.structureDuplicateFieldName =
			Liferay.Language.get('please-enter-a-unique-field-name');

		DEFAULTS_FORM_VALIDATOR.RULES.structureDuplicateFieldName = function (
			value,
			editorNode
		) {
			const instance = this;

			const editingField = UNIQUE_FIELD_NAMES_MAP.getValue(value);

			const duplicate = editingField && !editingField.get('selected');

			if (duplicate) {
				editorNode.selectText(0, value.length);

				instance.resetField(editorNode);
			}

			return !duplicate;
		};

		DEFAULTS_FORM_VALIDATOR.STRINGS.structureFieldName =
			Liferay.Language.get(
				'please-enter-only-alphanumeric-characters-or-underscore'
			);

		DEFAULTS_FORM_VALIDATOR.RULES.structureFieldName = function (value) {
			return LiferayFormBuilderUtil.validateFieldName(value);
		};

		DEFAULTS_FORM_VALIDATOR.STRINGS.structureRestrictedFieldName = Lang.sub(
			Liferay.Language.get('x-is-a-reserved-word'),
			[RESTRICTED_NAME]
		);

		DEFAULTS_FORM_VALIDATOR.RULES.structureRestrictedFieldName = function (
			value
		) {
			return RESTRICTED_NAME !== value;
		};

		const applyStyles = function (node, styleContent) {
			const styles = styleContent.replace(/\n/g, STR_BLANK).split(';');

			node.setStyle(STR_BLANK);

			styles.forEach((item) => {
				const rule = item.split(':');

				if (rule.length === 2) {
					const key = camelize(rule[0]);
					const value = rule[1].trim();

					node.setStyle(key, value);
				}
			});
		};

		const ColorCellEditor = A.Component.create({
			EXTENDS: A.BaseCellEditor,

			NAME: 'color-cell-editor',

			prototype: {
				_defSaveFn() {
					const instance = this;

					const colorPicker = instance.get('colorPicker');

					const input = instance.get('boundingBox').one('input');

					if (/#[A-F\d]{6}/.test(input.val().toUpperCase())) {
						ColorCellEditor.superclass._defSaveFn.apply(
							instance,
							arguments
						);
					}
					else {
						colorPicker.show();
					}
				},

				_uiSetValue(val) {
					const instance = this;

					const input = instance.get('boundingBox').one('input');

					input.setStyle('color', val);
					input.val(val);

					instance.elements.val(val);
				},

				ELEMENT_TEMPLATE: '<input type="text" />',

				getElementsValue() {
					const instance = this;

					let retVal;

					const input = instance.get('boundingBox').one('input');

					if (input) {
						const val = input.val().toUpperCase();

						if (/#[A-F\d]{6}/.test(val) || val === '') {
							retVal = val;
						}
					}

					return retVal;
				},

				renderUI() {
					const instance = this;

					ColorCellEditor.superclass.renderUI.apply(
						instance,
						arguments
					);

					const input = instance.get('boundingBox').one('input');

					const colorPicker = new A.ColorPickerPopover({
						trigger: input,
						zIndex: 65535,
					}).render();

					colorPicker.on('select', (event) => {
						input.setStyle('color', event.color);
						input.val(event.color);

						instance.fire('save', {
							newVal: instance.getValue(),
							prevVal: event.color,
						});
					});

					instance.set('colorPicker', colorPicker);
				},
			},
		});

		const DLFileEntryCellEditor = A.Component.create({
			EXTENDS: A.BaseCellEditor,

			NAME: 'document-library-file-entry-cell-editor',

			prototype: {
				_defInitToolbarFn() {
					const instance = this;

					DLFileEntryCellEditor.superclass._defInitToolbarFn.apply(
						instance,
						arguments
					);

					instance.toolbar.add(
						{
							label: Liferay.Language.get('select'),
							on: {
								click: A.bind('_onClickChoose', instance),
							},
						},
						1
					);

					instance.toolbar.add(
						{
							label: Liferay.Language.get('clear'),
							on: {
								click: A.bind('_onClickClear', instance),
							},
						},
						2
					);
				},

				_getDocumentLibrarySelectorURL() {
					const instance = this;

					const portletNamespace = instance.get('portletNamespace');

					const criterionJSON = {
						desiredItemSelectorReturnTypes:
							'com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType',
					};

					const uploadCriterionJSON = {
						URL: instance._getUploadURL(),
						desiredItemSelectorReturnTypes:
							'com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType',
					};

					const documentLibrarySelectorParameters = {
						'0_json': JSON.stringify(criterionJSON),
						'1_json': JSON.stringify(criterionJSON),
						'2_json': JSON.stringify(uploadCriterionJSON),
						'criteria': 'file',
						'itemSelectedEventName':
							portletNamespace + 'selectDocumentLibrary',
						'p_p_id': Liferay.PortletKeys.ITEM_SELECTOR,
						'p_p_mode': 'view',
						'p_p_state': 'pop_up',
					};

					const documentLibrarySelectorURL =
						Liferay.Util.PortletURL.createPortletURL(
							themeDisplay.getLayoutRelativeControlPanelURL(),
							documentLibrarySelectorParameters
						);

					return documentLibrarySelectorURL.toString();
				},

				_getUploadURL() {
					const uploadParameters = {
						'cmd': 'add_temp',
						'jakarta.portlet.action':
							'/document_library/upload_file_entry',
						'p_auth': Liferay.authToken,
						'p_p_id': Liferay.PortletKeys.DOCUMENT_LIBRARY,
					};

					const uploadURL = Liferay.Util.PortletURL.createActionURL(
						themeDisplay.getLayoutRelativeControlPanelURL(),
						uploadParameters
					);

					return uploadURL.toString();
				},

				_isDocumentLibraryDialogOpen() {
					const instance = this;

					const portletNamespace = instance.get('portletNamespace');

					return !!Liferay.Util.getWindow(
						portletNamespace + 'selectDocumentLibrary'
					);
				},

				_onClickChoose() {
					const instance = this;

					const portletNamespace = instance.get('portletNamespace');

					Liferay.Util.openSelectionModal({
						onSelect: (selectedItem) => {
							if (selectedItem) {
								const itemValue = JSON.parse(
									selectedItem.value
								);

								instance._selectFileEntry(
									itemValue.groupId,
									itemValue.title,
									itemValue.uuid
								);
							}
						},
						selectEventName:
							portletNamespace + 'selectDocumentLibrary',
						title: Liferay.Language.get('select-file'),
						url: instance._getDocumentLibrarySelectorURL(),
					});
				},

				_onClickClear() {
					const instance = this;

					instance.set('value', STR_BLANK);
				},

				_onDocMouseDownExt(event) {
					const instance = this;

					const boundingBox = instance.get('boundingBox');

					const documentLibraryDialogOpen =
						instance._isDocumentLibraryDialogOpen();

					if (
						!documentLibraryDialogOpen &&
						!boundingBox.contains(event.target)
					) {
						instance.set('visible', false);
					}
				},

				_selectFileEntry(groupId, title, uuid) {
					const instance = this;

					instance.set(
						'value',
						JSON.stringify({
							groupId,
							title,
							uuid,
						})
					);
				},

				_syncElementsFocus() {
					const instance = this;

					const boundingBox = instance.toolbar.get('boundingBox');

					const button = boundingBox.one('button');

					if (button) {
						button.focus();
					}
					else {
						DLFileEntryCellEditor.superclass._syncElementsFocus.apply(
							instance,
							arguments
						);
					}
				},

				_syncFileLabel(title, url) {
					const instance = this;

					const contentBox = instance.get('contentBox');

					let linkNode = contentBox.one('a');

					if (!linkNode) {
						linkNode = A.Node.create('<a></a>');

						contentBox.prepend(linkNode);
					}

					linkNode.setAttribute('href', url);
					linkNode.setContent(Liferay.Util.escapeHTML(title));
				},

				_uiSetValue(val) {
					const instance = this;

					if (val) {
						LiferayFormBuilderUtil.getFileEntry(
							val,
							(fileEntry) => {
								const url =
									LiferayFormBuilderUtil.getFileEntryURL(
										fileEntry
									);

								instance._syncFileLabel(fileEntry.title, url);
							}
						);
					}
					else {
						instance._syncFileLabel(STR_BLANK, STR_BLANK);

						val = STR_BLANK;
					}

					instance.elements.val(val);
				},

				ELEMENT_TEMPLATE: '<input type="hidden" />',

				getElementsValue() {
					const instance = this;

					return instance.get('value');
				},
			},
		});

		const IntegerCellEditor = A.Component.create({
			EXTENDS: A.TextCellEditor,

			NAME: 'text-cell-editor',

			prototype: {
				ELEMENT_TEMPLATE: '<input type="text" />',

				getElementsValue() {
					const instance = this;

					let retVal;

					const input = instance.get('boundingBox').one('input');

					if (input) {
						const val = input.val();

						if (/^[+-]?(\d+)*$/.test(val) || val === '') {
							retVal = val;
						}
					}

					if (retVal) {
						return retVal;
					}
					else {
						instance.fire('save', {
							newVal: '',
							prevVal: retVal,
						});
					}
				},
			},
		});

		const JournalArticleCellEditor = A.Component.create({
			EXTENDS: A.BaseCellEditor,

			NAME: 'journal-article-cell-editor',

			prototype: {
				_defInitToolbarFn() {
					const instance = this;

					JournalArticleCellEditor.superclass._defInitToolbarFn.apply(
						instance,
						arguments
					);

					instance.toolbar.add(
						{
							label: Liferay.Language.get('select'),
							on: {
								click: A.bind('_onClickChoose', instance),
							},
						},
						1
					);

					instance.toolbar.add(
						{
							label: Liferay.Language.get('clear'),
							on: {
								click: A.bind('_onClickClear', instance),
							},
						},
						2
					);
				},

				_getWebContentSelectorURL() {
					const instance = this;

					const portletNamespace = instance.get('portletNamespace');

					const criterionJSON = {
						desiredItemSelectorReturnTypes:
							'com.liferay.item.selector.criteria.JournalArticleItemSelectorReturnType',
					};

					const webContentSelectorParameters = {
						'0_json': JSON.stringify(criterionJSON),
						'criteria':
							'com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion',
						'itemSelectedEventName':
							portletNamespace + 'selectDocumentLibrary',
						'p_auth': Liferay.authToken,
						'p_p_id': Liferay.PortletKeys.ITEM_SELECTOR,
						'p_p_mode': 'view',
						'p_p_state': 'pop_up',
					};

					const webContentSelectorURL =
						Liferay.Util.PortletURL.createRenderURL(
							themeDisplay.getLayoutRelativeControlPanelURL(),
							webContentSelectorParameters
						);

					return webContentSelectorURL.toString();
				},

				_handleCancelEvent() {
					const instance = this;

					instance.get('boundingBox').hide();
				},

				_handleSaveEvent() {
					const instance = this;

					JournalArticleCellEditor.superclass._handleSaveEvent.apply(
						instance,
						arguments
					);

					instance.get('boundingBox').hide();
				},

				_onClickChoose() {
					const instance = this;

					const portletNamespace = instance.get('portletNamespace');

					Liferay.Util.openSelectionModal({
						onSelect: (selectedItem) => {
							if (selectedItem) {
								const itemValue = JSON.parse(
									selectedItem.value
								);

								instance.setValue({
									className: itemValue.className,
									classPK: itemValue.classPK,
									title: itemValue.title,
								});
							}
						},
						selectEventName:
							portletNamespace + 'selectDocumentLibrary',
						title: Liferay.Language.get('journal-article'),
						url: instance._getWebContentSelectorURL(),
					});
				},

				_onClickClear() {
					const instance = this;

					instance.set('value', STR_BLANK);
				},

				_onDocMouseDownExt(event) {
					const instance = this;

					const boundingBox = instance.get('boundingBox');

					if (!boundingBox.contains(event.target)) {
						instance._handleCancelEvent(event);
					}
				},

				_syncJournalArticleLabel(title) {
					const instance = this;

					const contentBox = instance.get('contentBox');

					let linkNode = contentBox.one('span');

					if (!linkNode) {
						linkNode = A.Node.create('<span></span>');

						contentBox.prepend(linkNode);
					}

					linkNode.setContent(Liferay.Util.escapeHTML(title));
				},

				_uiSetValue(val) {
					const instance = this;

					if (val) {
						val = JSON.parse(val);
						const title =
							Liferay.Language.get('journal-article') +
							': ' +
							val.classPK;

						instance._syncJournalArticleLabel(title);
					}
					else {
						instance._syncJournalArticleLabel(STR_BLANK);
					}
				},

				ELEMENT_TEMPLATE: '<input type="hidden" />',

				getElementsValue() {
					const instance = this;

					return instance.get('value');
				},

				getParsedValue(value) {
					if (Lang.isString(value)) {
						if (value !== '') {
							value = JSON.parse(value);
						}
						else {
							value = {};
						}
					}

					return value;
				},

				setValue(value) {
					const instance = this;

					const parsedValue = instance.getParsedValue(value);

					if (!parsedValue.className && !parsedValue.classPK) {
						value = '';
					}
					else {
						value = JSON.stringify(parsedValue);
					}

					instance.set('value', value);
				},
			},
		});

		const NumberCellEditor = A.Component.create({
			EXTENDS: A.TextCellEditor,

			NAME: 'text-cell-editor',

			prototype: {
				ELEMENT_TEMPLATE: '<input type="text" />',

				getElementsValue() {
					const instance = this;

					let retVal;

					const input = instance.get('boundingBox').one('input');

					if (input) {
						const val = input.val();

						if (/^[+-]?(\d+)([.,]\d+)*$/.test(val) || val === '') {
							retVal = val;
						}
					}

					if (retVal) {
						return retVal;
					}
					else {
						instance.fire('save', {
							newVal: '',
							prevVal: retVal,
						});
					}
				},
			},
		});

		Liferay.FormBuilder.CUSTOM_CELL_EDITORS = {};

		const customCellEditors = [
			ColorCellEditor,
			DLFileEntryCellEditor,
			IntegerCellEditor,
			JournalArticleCellEditor,
			NumberCellEditor,
		];

		customCellEditors.forEach((item) => {
			Liferay.FormBuilder.CUSTOM_CELL_EDITORS[item.NAME] = item;
		});

		const LiferayFieldSupport = function () {};

		LiferayFieldSupport.ATTRS = {
			autoGeneratedName: {
				setter: booleanParse,
				value: true,
			},

			indexType: {
				valueFn() {
					return structureFieldIndexEnable() ? 'keyword' : '';
				},
			},

			localizable: {
				setter: booleanParse,
				value: true,
			},

			name: {
				setter: LiferayFormBuilderUtil.normalizeKey,
				validator(val) {
					return !UNIQUE_FIELD_NAMES_MAP.has(val);
				},
				valueFn() {
					const instance = this;

					let label = LiferayFormBuilderUtil.normalizeKey(
						instance.get('label')
					);

					label = label.replace(/[^a-z0-9]/gi, '');

					let name = label + instance._randomString(4);

					while (UNIQUE_FIELD_NAMES_MAP.has(name)) {
						name = A.FormBuilderField.buildFieldName(name);
					}

					return name;
				},
			},

			repeatable: {
				setter: booleanParse,
				value: false,
			},
		};

		LiferayFieldSupport.prototype.initializer = function () {
			const instance = this;

			instance.after('nameChange', instance._afterNameChange);
		};

		LiferayFieldSupport.prototype._afterNameChange = function (event) {
			const instance = this;

			UNIQUE_FIELD_NAMES_MAP.remove(event.prevVal);
			UNIQUE_FIELD_NAMES_MAP.put(event.newVal, instance);
		};

		LiferayFieldSupport.prototype._handleDeleteEvent = function (event) {
			const instance = this;

			const strings = instance.getStrings();

			const deleteModal = Liferay.Util.Window.getWindow({
				dialog: {
					bodyContent: strings.deleteFieldsMessage,
					destroyOnHide: true,
					height: 200,
					resizable: false,
					toolbars: {
						footer: [
							{
								cssClass: 'btn-primary',
								label: Liferay.Language.get('ok'),
								on: {
									click() {
										instance.destroy();

										deleteModal.hide();
									},
								},
							},
							{
								label: Liferay.Language.get('cancel'),
								on: {
									click() {
										deleteModal.hide();
									},
								},
							},
						],
					},
					width: 700,
				},
				title: instance.get('label'),
			})
				.render()
				.show();

			event.stopPropagation();
		};

		LiferayFieldSupport.prototype._randomString = function (length) {
			const randomString = Math.ceil(
				Math.random() * Number.MAX_SAFE_INTEGER
			).toString(36);

			return randomString.substring(0, length);
		};

		const LocalizableFieldSupport = function () {};

		LocalizableFieldSupport.ATTRS = {
			localizationMap: {
				setter: A.clone,
				value: {},
			},

			readOnlyAttributes: {
				getter: '_getReadOnlyAttributes',
			},
		};

		LocalizableFieldSupport.prototype.initializer = function () {
			const instance = this;

			const builder = instance.get('builder');

			instance.after('render', instance._afterLocalizableFieldRender);

			LOCALIZABLE_FIELD_ATTRS.forEach((localizableField) => {
				instance.after(
					localizableField + 'Change',
					instance._afterLocalizableFieldChange
				);
			});

			builder.translationManager.after(
				'editingLocaleChange',
				instance._afterEditingLocaleChange,
				instance
			);
		};

		LocalizableFieldSupport.prototype._afterEditingLocaleChange = function (
			event
		) {
			const instance = this;

			instance._syncLocaleUI(event.newVal);
		};

		LocalizableFieldSupport.prototype._afterLocalizableFieldChange =
			function (event) {
				const instance = this;

				const builder = instance.get('builder');

				const translationManager = builder.translationManager;

				const editingLocale = translationManager.get('editingLocale');

				instance._updateLocalizationMapAttribute(
					editingLocale,
					event.attrName
				);
			};

		LocalizableFieldSupport.prototype._afterLocalizableFieldRender =
			function () {
				const instance = this;

				const builder = instance.get('builder');

				const translationManager = builder.translationManager;

				const editingLocale = translationManager.get('editingLocale');

				instance._updateLocalizationMap(editingLocale);
			};

		LocalizableFieldSupport.prototype._getReadOnlyAttributes = function (
			val
		) {
			const instance = this;

			const builder = instance.get('builder');

			const translationManager = builder.translationManager;

			const defaultLocale = translationManager.get('defaultLocale');
			const editingLocale = translationManager.get('editingLocale');

			if (defaultLocale !== editingLocale) {
				val = UNLOCALIZABLE_FIELD_ATTRS.concat(val);
			}

			return AArray.dedupe(val);
		};

		LocalizableFieldSupport.prototype._syncLocaleUI = function (locale) {
			const instance = this;

			const builder = instance.get('builder');

			const localizationMap = instance.get('localizationMap');

			const translationManager = builder.translationManager;

			let defaultLocale = themeDisplay.getDefaultLanguageId();

			if (translationManager) {
				defaultLocale = translationManager.get('defaultLocale');
			}

			const localeMap =
				localizationMap[locale] || localizationMap[defaultLocale];

			if (isObject(localeMap)) {
				LOCALIZABLE_FIELD_ATTRS.forEach((item) => {
					if (item !== 'options') {
						const localizedItem = localeMap[item];

						if (
							!isUndefined(localizedItem) &&
							!isNull(localizedItem)
						) {
							instance.set(item, localizedItem);
						}
					}
				});

				builder._syncUniqueField(instance);
			}

			if (instanceOf(instance, A.FormBuilderMultipleChoiceField)) {
				instance._syncOptionsLocaleUI(locale);
			}

			if (builder.editingField === instance) {
				builder.propertyList.set('data', instance.getProperties());
			}
		};

		LocalizableFieldSupport.prototype._syncOptionsLocaleUI = function (
			locale
		) {
			const instance = this;

			const options = instance.get('options');

			options.forEach((item) => {
				const localizationMap = item.localizationMap;

				if (isObject(localizationMap)) {
					const localeMap = localizationMap[locale];

					if (isObject(localeMap)) {
						item.label = localeMap.label;
					}
				}
			});

			instance.set('options', options);
		};

		LocalizableFieldSupport.prototype._updateLocalizationMap = function (
			locale
		) {
			const instance = this;

			LOCALIZABLE_FIELD_ATTRS.forEach((item) => {
				instance._updateLocalizationMapAttribute(locale, item);
			});
		};

		LocalizableFieldSupport.prototype._updateLocalizationMapAttribute =
			function (locale, attributeName) {
				const instance = this;

				if (attributeName === 'options') {
					instance._updateLocalizationMapOptions(locale);
				}
				else {
					const localizationMap = instance.get('localizationMap');

					const localeMap = localizationMap[locale] || {};

					localeMap[attributeName] = instance.get(attributeName);

					localizationMap[locale] = localeMap;

					instance.set('localizationMap', localizationMap);
				}
			};

		LocalizableFieldSupport.prototype._updateLocalizationMapOptions =
			function (locale) {
				const instance = this;

				const options = instance.get('options');

				if (options) {
					options.forEach((item) => {
						let localizationMap = item.localizationMap;

						if (!isObject(localizationMap)) {
							localizationMap = {};
						}

						localizationMap[locale] = {
							label: item.label,
						};

						item.localizationMap = localizationMap;
					});
				}
			};

		const SerializableFieldSupport = function () {};

		SerializableFieldSupport.prototype._addDefinitionFieldLocalizedAttributes =
			function (fieldJSON) {
				const instance = this;

				LOCALIZABLE_FIELD_ATTRS.forEach((attr) => {
					if (attr === 'options') {
						if (
							instanceOf(
								instance,
								A.FormBuilderMultipleChoiceField
							)
						) {
							instance._addDefinitionFieldOptions(fieldJSON);
						}
					}
					else {
						fieldJSON[attr] = instance._getLocalizedValue(attr);
					}
				});
			};

		SerializableFieldSupport.prototype._addDefinitionFieldUnlocalizedAttributes =
			function (fieldJSON) {
				const instance = this;

				UNLOCALIZABLE_FIELD_ATTRS.forEach((attr) => {
					fieldJSON[attr] = instance.get(attr);
				});
			};

		SerializableFieldSupport.prototype._addDefinitionFieldOptions =
			function (fieldJSON) {
				const instance = this;

				const options = instance.get('options');

				const fieldOptions = [];

				if (options) {
					const builder = instance.get('builder');

					const translationManager = builder.translationManager;

					const availableLocales =
						translationManager.get('availableLocales');

					options.forEach((option) => {
						const fieldOption = {};

						const localizationMap = option.localizationMap;

						fieldOption.value = option.value;
						fieldOption.label = {};

						availableLocales.forEach((locale) => {
							const label = instance._getValue(
								'label',
								locale,
								localizationMap
							);

							fieldOption.label[locale] =
								LiferayFormBuilderUtil.normalizeValue(label);
						});

						fieldOptions.push(fieldOption);
					});

					fieldJSON.options = fieldOptions;
				}
			};

		SerializableFieldSupport.prototype._addDefinitionFieldNestedFields =
			function (fieldJSON) {
				const instance = this;

				const nestedFields = [];

				instance.get('fields').each((childField) => {
					nestedFields.push(childField.serialize());
				});

				if (nestedFields.length) {
					fieldJSON.nestedFields = nestedFields;
				}
			};

		SerializableFieldSupport.prototype._getLocalizedValue = function (
			attribute
		) {
			const instance = this;

			const builder = instance.get('builder');

			const localizationMap = instance.get('localizationMap');

			const localizedValue = {};

			const translationManager = builder.translationManager;

			translationManager.get('availableLocales').forEach((locale) => {
				localizedValue[locale] = LiferayFormBuilderUtil.normalizeValue(
					instance._getValue(attribute, locale, localizationMap)
				);
			});

			return localizedValue;
		};

		SerializableFieldSupport.prototype._getValue = function (
			attribute,
			locale,
			localizationMap
		) {
			const instance = this;

			const builder = instance.get('builder');

			const translationManager = builder.translationManager;

			const defaultLocale = translationManager.get('defaultLocale');

			// eslint-disable-next-line @liferay/aui/no-object
			let value = A.Object.getValue(localizationMap, [locale, attribute]);

			if (isValue(value)) {
				return value;
			}

			// eslint-disable-next-line @liferay/aui/no-object
			value = A.Object.getValue(localizationMap, [
				defaultLocale,
				attribute,
			]);

			if (isValue(value)) {
				return value;
			}

			for (const localizationMapLocale in localizationMap) {

				// eslint-disable-next-line @liferay/aui/no-object
				value = A.Object.getValue(localizationMap, [
					localizationMapLocale,
					attribute,
				]);

				if (isValue(value)) {
					return value;
				}
			}

			return STR_BLANK;
		};

		SerializableFieldSupport.prototype.serialize = function () {
			const instance = this;

			const fieldJSON = {};

			instance._addDefinitionFieldLocalizedAttributes(fieldJSON);
			instance._addDefinitionFieldUnlocalizedAttributes(fieldJSON);
			instance._addDefinitionFieldNestedFields(fieldJSON);

			return fieldJSON;
		};

		A.Base.mix(A.FormBuilderField, [
			LiferayFieldSupport,
			LocalizableFieldSupport,
			SerializableFieldSupport,
		]);

		const FormBuilderProto = A.FormBuilderField.prototype;

		const originalGetPropertyModel = FormBuilderProto.getPropertyModel;

		FormBuilderProto.getPropertyModel = function () {
			const instance = this;

			const model = originalGetPropertyModel.call(instance);

			const type = instance.get('type');

			let indexTypeOptions = {
				'': Liferay.Language.get('no'),
				'keyword': Liferay.Language.get('yes'),
			};

			if (type === 'ddm-image' || type === 'text') {
				indexTypeOptions = {
					'': Liferay.Language.get('not-indexable'),
					'keyword': Liferay.Language.get('indexable-keyword'),
					'text': Liferay.Language.get('indexable-text'),
				};
			}

			if (type === 'ddm-text-html' || type === 'textarea') {
				indexTypeOptions = {
					'': Liferay.Language.get('not-indexable'),
					'text': Liferay.Language.get('indexable-text'),
				};
			}

			const newModel = [];

			model.forEach((item) => {
				if (item.attributeName === 'name') {
					item.editor = new A.TextCellEditor({
						validator: {
							rules: {
								value: {
									required: true,
									structureDuplicateFieldName: true,
									structureFieldName: true,
									structureRestrictedFieldName: true,
								},
							},
						},
					});
				}

				if (item.editor) {
					item.editor.set('strings', editorLocalizedStrings);
				}

				newModel.push(item);

				if (item.attributeName === 'required') {
					item.id = 'required';

					if (type === 'ddm-image') {
						newModel.push(
							instance.getRequiredDescriptionPropertyModel()
						);
					}
				}
			});

			return newModel.concat([
				{
					attributeName: 'indexType',
					editor: new A.RadioCellEditor({
						options: indexTypeOptions,
						strings: editorLocalizedStrings,
					}),
					formatter(val) {
						return indexTypeOptions[val.data.value];
					},
					name: Liferay.Language.get('indexable'),
				},
				{
					attributeName: 'localizable',
					editor: new A.RadioCellEditor({
						options: booleanOptions,
						strings: editorLocalizedStrings,
					}),
					formatter(val) {
						return booleanOptions[val.data.value];
					},
					name: Liferay.Language.get('localizable'),
				},
				{
					attributeName: 'repeatable',
					editor: new A.RadioCellEditor({
						options: booleanOptions,
						strings: editorLocalizedStrings,
					}),
					formatter(val) {
						return booleanOptions[val.data.value];
					},
					name: Liferay.Language.get('repeatable'),
				},
			]);
		};

		const DDMColorField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'color',
				},

				fieldNamespace: {
					value: 'ddm',
				},

				showLabel: {
					value: false,
				},
			},

			EXTENDS: A.FormBuilderField,

			NAME: 'ddm-color',

			prototype: {
				getHTML() {
					return TPL_COLOR;
				},

				getPropertyModel() {
					const instance = this;

					const model =
						DDMColorField.superclass.getPropertyModel.apply(
							instance,
							arguments
						);

					model.forEach((item, index, collection) => {
						const attributeName = item.attributeName;

						if (attributeName === 'predefinedValue') {
							collection[index] = {
								attributeName,
								editor: new ColorCellEditor({
									strings: editorLocalizedStrings,
								}),
								name: Liferay.Language.get('predefined-value'),
							};
						}
					});

					return model;
				},
			},
		});

		const DDMDateField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'date',
				},

				fieldNamespace: {
					value: 'ddm',
				},
			},

			EXTENDS: A.FormBuilderTextField,

			NAME: 'ddm-date',

			prototype: {
				getPropertyModel() {
					const instance = this;

					const model =
						DDMDateField.superclass.getPropertyModel.apply(
							instance,
							arguments
						);

					model.forEach((item, index, collection) => {
						const attributeName = item.attributeName;

						if (attributeName === 'predefinedValue') {
							collection[index] = {
								attributeName,
								editor: new A.DateCellEditor({
									dateFormat: '%m/%d/%Y',
									inputFormatter(val) {
										const instance = this;

										let value = val;

										if (Array.isArray(val)) {
											value = instance.formatDate(val[0]);
										}

										return value;
									},

									outputFormatter(val) {
										const instance = this;

										let retVal = val;

										if (Array.isArray(val)) {
											const formattedValue =
												A.DataType.Date.parse(
													instance.get('dateFormat'),
													val[0]
												);

											retVal = [formattedValue];
										}

										return retVal;
									},
								}),
								name: Liferay.Language.get('predefined-value'),
								strings: editorLocalizedStrings,
							};
						}
					});

					return model;
				},

				renderUI() {
					const instance = this;

					DDMDateField.superclass.renderUI.apply(instance, arguments);

					let keysPressed = {};

					const onKeyDown = function (domEvent) {
						if (domEvent.keyCode === 16) {
							keysPressed[domEvent.keyCode] = true;
						}
					};

					const onKeyUp = function (domEvent) {
						if (domEvent.keyCode === 16) {
							delete keysPressed[domEvent.keyCode];
						}
					};

					const trigger = instance.get('templateNode').one('input');

					const closePopoverOnKeyboardNavigation = function (
						instance
					) {
						instance.hide();

						keysPressed = {};

						if (trigger) {
							Liferay.Util.focusFormField(trigger);
						}
					};

					if (trigger) {
						instance.datePicker = new A.DatePickerDeprecated({
							calendar: {
								locale: Liferay.ThemeDisplay.getLanguageId(),
							},
							on: {
								destroy() {
									document.removeEventListener(
										'keydown',
										onKeyDown
									);
									document.removeEventListener(
										'keyup',
										onKeyUp
									);
								},
								enterKey() {
									let countInterval = 0;

									const intervalId = setInterval(() => {
										const trigger = A.one(
											'.datepicker-popover:not(.popover-hidden) .yui3-calendarnav-prevmonth'
										);

										if (trigger) {
											Liferay.Util.focusFormField(
												trigger
											);
											clearInterval(intervalId);
										}
										else if (countInterval > 10) {
											clearInterval(intervalId);
										}
										countInterval++;
									}, 100);
								},
								init() {
									document.addEventListener(
										'keydown',
										onKeyDown
									);
									document.addEventListener('keyup', onKeyUp);
								},
								selectionChange(event) {
									const date = event.newSelection;

									instance.setValue(A.Date.format(date));
								},
							},
							popover: {
								on: {
									keydown(event) {
										const instance = this;

										const domEvent = event.domEvent;

										keysPressed[domEvent.keyCode] = true;

										const isTabPressed =
											domEvent.keyCode === 9 ||
											keysPressed[9];

										const isShiftPressed =
											domEvent.keyCode === 16 ||
											keysPressed[16];

										const isForwardNavigation =
											isTabPressed && !isShiftPressed;

										const isEscapePressed =
											domEvent.keyCode === 27 ||
											keysPressed[27];

										const hasClassName =
											domEvent.target.hasClass(
												'yui3-calendar-grid'
											) ||
											domEvent.target.hasClass(
												'yui3-calendar-day'
											);

										if (
											(isForwardNavigation &&
												hasClassName) ||
											isEscapePressed
										) {
											closePopoverOnKeyboardNavigation(
												instance
											);
										}
									},
									keyup(event) {
										const instance = this;

										const domEvent = event.domEvent;

										const isTabPressed =
											domEvent.keyCode === 9 ||
											keysPressed[9];

										const isShiftPressed =
											domEvent.keyCode === 16 ||
											keysPressed[16];

										const isBackwardNavigation =
											isTabPressed && isShiftPressed;

										const hasClassName =
											domEvent.target.hasClass(
												'yui3-calendar-focused'
											);

										if (
											isBackwardNavigation &&
											hasClassName
										) {
											closePopoverOnKeyboardNavigation(
												instance
											);
										}

										delete keysPressed[domEvent.keyCode];
									},
								},
							},
							trigger,
						}).render();
					}

					instance.datePicker.calendar.set('strings', {
						next: Liferay.Language.get('next'),
						none: Liferay.Language.get('none'),
						previous: Liferay.Language.get('previous'),
						today: Liferay.Language.get('today'),
					});
				},
			},
		});

		const DDMDecimalField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'decimal',
				},

				fieldNamespace: {
					value: 'ddm',
				},
			},

			EXTENDS: A.FormBuilderTextField,

			NAME: 'ddm-decimal',

			prototype: {
				getPropertyModel() {
					const instance = this;

					const model =
						DDMDecimalField.superclass.getPropertyModel.apply(
							instance,
							arguments
						);

					model.forEach((item, index, collection) => {
						const attributeName = item.attributeName;

						if (attributeName === 'predefinedValue') {
							collection[index] = {
								attributeName,
								editor: new NumberCellEditor({
									strings: editorLocalizedStrings,
								}),
								name: Liferay.Language.get('predefined-value'),
							};
						}
					});

					return model;
				},
			},
		});

		const DDMDocumentLibraryField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'document-library',
				},

				fieldNamespace: {
					value: 'ddm',
				},
			},

			EXTENDS: A.FormBuilderField,

			NAME: 'ddm-documentlibrary',

			prototype: {
				_defaultFormatter() {
					return 'documents-and-media';
				},

				_uiSetValue() {
					return Liferay.Language.get('select');
				},

				getHTML() {
					return TPL_INPUT_BUTTON;
				},

				getPropertyModel() {
					const instance = this;

					const model =
						DDMDocumentLibraryField.superclass.getPropertyModel.apply(
							instance,
							arguments
						);

					model.forEach((item) => {
						const attributeName = item.attributeName;

						if (attributeName === 'predefinedValue') {
							item.editor = new DLFileEntryCellEditor({
								strings: editorLocalizedStrings,
							});

							item.formatter = function (object) {
								const data = object.data;

								let label = STR_BLANK;

								const value = data.value;

								if (value !== STR_BLANK) {
									label =
										'(' +
										Liferay.Language.get('file') +
										')';
								}

								return label;
							};
						}
						else if (attributeName === 'type') {
							item.formatter = instance._defaultFormatter;
						}
					});

					return model;
				},
			},
		});

		const DDMGeolocationField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'geolocation',
				},

				fieldNamespace: {
					value: 'ddm',
				},

				localizable: {
					setter: booleanParse,
					value: false,
				},
			},

			EXTENDS: A.FormBuilderField,

			NAME: 'ddm-geolocation',

			prototype: {
				getHTML() {
					return TPL_GEOLOCATION;
				},

				getPropertyModel() {
					const instance = this;

					return DDMGeolocationField.superclass.getPropertyModel
						.apply(instance, arguments)
						.filter((item) => {
							return item.attributeName !== 'predefinedValue';
						});
				},
			},
		});

		const DDMImageField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'image',
				},

				fieldNamespace: {
					value: 'ddm',
				},

				indexType: {
					valueFn() {
						return structureFieldIndexEnable() ? 'text' : '';
					},
				},

				requiredDescription: {
					setter: booleanParse,
					value: true,
				},
			},

			EXTENDS: A.FormBuilderField,

			NAME: 'ddm-image',

			prototype: {
				getHTML() {
					return TPL_WCM_IMAGE;
				},

				getRequiredDescriptionPropertyModel() {
					return {
						attributeName: 'requiredDescription',
						editor: new A.RadioCellEditor({
							options: booleanOptions,
							strings: editorLocalizedStrings,
						}),
						formatter(val) {
							return booleanOptions[val.data.value];
						},
						id: 'requiredDescription',
						name: Liferay.Language.get('required-description'),
					};
				},
			},
		});

		const DDMIntegerField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'integer',
				},

				fieldNamespace: {
					value: 'ddm',
				},
			},

			EXTENDS: A.FormBuilderTextField,

			NAME: 'ddm-integer',

			prototype: {
				getPropertyModel() {
					const instance = this;

					const model =
						DDMIntegerField.superclass.getPropertyModel.apply(
							instance,
							arguments
						);

					model.forEach((item, index, collection) => {
						const attributeName = item.attributeName;

						if (attributeName === 'predefinedValue') {
							collection[index] = {
								attributeName,
								editor: new IntegerCellEditor({
									strings: editorLocalizedStrings,
								}),
								name: Liferay.Language.get('predefined-value'),
							};
						}
					});

					return model;
				},
			},
		});

		const DDMNumberField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'number',
				},

				fieldNamespace: {
					value: 'ddm',
				},
			},

			EXTENDS: A.FormBuilderTextField,

			NAME: 'ddm-number',

			prototype: {
				getPropertyModel() {
					const instance = this;

					const model =
						DDMIntegerField.superclass.getPropertyModel.apply(
							instance,
							arguments
						);

					model.forEach((item, index, collection) => {
						const attributeName = item.attributeName;

						if (attributeName === 'predefinedValue') {
							collection[index] = {
								attributeName,
								editor: new NumberCellEditor({
									strings: editorLocalizedStrings,
								}),
								name: Liferay.Language.get('predefined-value'),
							};
						}
					});

					return model;
				},
			},
		});

		const DDMParagraphField = A.Component.create({
			ATTRS: {
				dataType: {
					value: undefined,
				},

				fieldNamespace: {
					value: 'ddm',
				},

				showLabel: {
					readOnly: true,
					value: true,
				},

				style: {
					value: STR_BLANK,
				},
			},

			EXTENDS: A.FormBuilderField,

			NAME: 'ddm-paragraph',

			UI_ATTRS: ['label', 'style'],

			prototype: {
				_uiSetLabel(val) {
					const instance = this;

					instance.get('templateNode').setContent(val);
				},

				_uiSetStyle(val) {
					const instance = this;

					const templateNode = instance.get('templateNode');

					applyStyles(templateNode, val);
				},

				getHTML() {
					return TPL_PARAGRAPH;
				},

				getPropertyModel() {
					return [
						{
							attributeName: 'type',
							editor: false,
							name: Liferay.Language.get('type'),
						},
						{
							attributeName: 'label',
							editor: new A.TextAreaCellEditor({
								strings: editorLocalizedStrings,
							}),
							name: Liferay.Language.get('text'),
						},
						{
							attributeName: 'style',
							editor: new A.TextAreaCellEditor({
								strings: editorLocalizedStrings,
							}),
							name: Liferay.Language.get('style'),
						},
					];
				},
			},
		});

		const DDMRadioField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'radio',
				},

				predefinedValue: {
					setter(val) {
						return val;
					},
				},
			},

			EXTENDS: A.FormBuilderRadioField,

			NAME: 'ddm-radio',

			OVERRIDE_TYPE: 'radio',

			prototype: {
				_uiSetOptions(val) {
					const instance = this;

					const buffer = [];
					let counter = 0;

					const predefinedValue = instance.get('predefinedValue');
					const templateNode = instance.get('templateNode');

					A.each(val, (item) => {
						const checked = predefinedValue === item.value;

						buffer.push(
							Lang.sub(TPL_RADIO, {
								checked: checked ? 'checked="checked"' : '',
								disabled: instance.get('disabled')
									? 'disabled="disabled"'
									: '',
								id: AEscape.html(
									instance.get('id') + counter++
								),
								label: AEscape.html(item.label),
								name: AEscape.html(instance.get('name')),
								value: AEscape.html(item.value),
							})
						);
					});

					instance.optionNodes = A.NodeList.create(buffer.join(''));

					templateNode.setContent(instance.optionNodes);
				},

				_uiSetPredefinedValue(val) {
					const instance = this;

					const optionNodes = instance.optionNodes;

					if (!optionNodes) {
						return;
					}

					optionNodes.set('checked', false);

					optionNodes
						.all('input[value="' + AEscape.html(val) + '"]')
						.set('checked', true);
				},
			},
		});

		const DDMSeparatorField = A.Component.create({
			ATTRS: {
				dataType: {
					value: undefined,
				},

				fieldNamespace: {
					value: 'ddm',
				},

				showLabel: {
					value: false,
				},

				style: {
					value: STR_BLANK,
				},
			},

			EXTENDS: A.FormBuilderField,

			NAME: 'ddm-separator',

			UI_ATTRS: ['style'],

			prototype: {
				_uiSetStyle(val) {
					const instance = this;

					const templateNode = instance.get('templateNode');

					applyStyles(templateNode, val);
				},

				getHTML() {
					return TPL_SEPARATOR;
				},

				getPropertyModel() {
					const instance = this;

					const model =
						DDMSeparatorField.superclass.getPropertyModel.apply(
							instance,
							arguments
						);

					model.push({
						attributeName: 'style',
						editor: new A.TextAreaCellEditor({
							strings: editorLocalizedStrings,
						}),
						name: Liferay.Language.get('style'),
					});

					return model;
				},
			},
		});

		const DDMHTMLTextField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'html',
				},

				fieldNamespace: {
					value: 'ddm',
				},

				indexType: {
					valueFn() {
						return structureFieldIndexEnable() ? 'text' : '';
					},
				},
			},

			EXTENDS: FormBuilderTextField,

			NAME: 'ddm-text-html',

			prototype: {
				getHTML() {
					return TPL_TEXT_HTML;
				},
			},
		});

		const DDMJournalArticleField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'journal-article',
				},

				fieldNamespace: {
					value: 'ddm',
				},
			},

			EXTENDS: A.FormBuilderField,

			NAME: 'ddm-journal-article',

			prototype: {
				getHTML() {
					return TPL_INPUT_BUTTON;
				},

				getPropertyModel() {
					const instance = this;

					const model =
						DDMJournalArticleField.superclass.getPropertyModel.apply(
							instance,
							arguments
						);

					model.push({
						attributeName: 'style',
						editor: new A.TextAreaCellEditor({
							strings: editorLocalizedStrings,
						}),
						name: Liferay.Language.get('style'),
					});

					model.forEach((item) => {
						const attributeName = item.attributeName;

						if (attributeName === 'predefinedValue') {
							item.editor = new JournalArticleCellEditor({
								strings: editorLocalizedStrings,
							});

							item.formatter = function (object) {
								const data = object.data;

								let label = STR_BLANK;

								const value = data.value;

								if (value !== STR_BLANK) {
									label =
										'(' +
										Liferay.Language.get(
											'journal-article'
										) +
										')';
								}

								return label;
							};
						}
					});

					return model;
				},
			},
		});

		const DDMLinkToPageField = A.Component.create({
			ATTRS: {
				dataType: {
					value: 'link-to-page',
				},

				fieldNamespace: {
					value: 'ddm',
				},
			},

			EXTENDS: A.FormBuilderField,

			NAME: 'ddm-link-to-page',

			prototype: {
				getHTML() {
					return TPL_INPUT_BUTTON;
				},
			},
		});

		const DDMTextAreaField = A.Component.create({
			ATTRS: {
				indexType: {
					valueFn() {
						return structureFieldIndexEnable() ? 'text' : '';
					},
				},
			},

			EXTENDS: A.FormBuilderTextAreaField,

			NAME: 'textarea',
		});

		const plugins = [
			DDMColorField,
			DDMDateField,
			DDMDecimalField,
			DDMDocumentLibraryField,
			DDMGeolocationField,
			DDMImageField,
			DDMIntegerField,
			DDMJournalArticleField,
			DDMLinkToPageField,
			DDMNumberField,
			DDMParagraphField,
			DDMRadioField,
			DDMSeparatorField,
			DDMHTMLTextField,
			DDMTextAreaField,
		];

		plugins.forEach((item) => {
			FormBuilderTypes[item.OVERRIDE_TYPE || item.NAME] = item;
		});
	},
	'',
	{
		requires: [
			'aui-base',
			'aui-color-picker-popover',
			'aui-url',
			'liferay-item-selector-dialog',
			'liferay-portlet-dynamic-data-mapping',
		],
	}
);
