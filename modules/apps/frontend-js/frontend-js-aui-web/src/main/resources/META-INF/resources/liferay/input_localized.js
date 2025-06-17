/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-input-localized',
	(A) => {
		const Lang = A.Lang;

		const STR_BLANK = '';

		const STR_INPUT_PLACEHOLDER = 'inputPlaceholder';

		const STR_ITEMS = 'items';

		const STR_ITEMS_ERROR = 'itemsError';

		const STR_SELECTED = 'selected';

		const STR_SUBMIT = '_onSubmit';

		const availableLanguages = Liferay.Language.available;

		const defaultLanguageId = themeDisplay.getDefaultLanguageId();

		const userLanguageId = themeDisplay.getLanguageId();

		const availableLanguageIds = A.Array.dedupe(
			[defaultLanguageId, userLanguageId].concat(
				Object.keys(availableLanguages)
			)
		);

		const InputLocalized = A.Component.create({
			_instances: {},

			ATTRS: {
				activeLanguageIds: {
					validator: Lang.isArray,
				},

				adminMode: {
					validator: Lang.isBoolean,
					value: false,
				},

				animateClass: {
					validator: Lang.isString,
					value: 'highlight-animation',
				},

				availableLocales: {
					validator: Lang.isObject,
					value: [],
				},

				defaultLanguageId: {
					value: defaultLanguageId,
				},

				edited: {
					validator: Lang.isBoolean,
					value: false,
				},

				editor: {},

				fieldPrefix: {
					validator: Lang.isString,
				},

				fieldPrefixSeparator: {
					validator: Lang.isString,
				},

				frontendJsComponentsWebModule: {
					validator: Lang.isObject,
				},

				frontendJsReactWebModule: {
					validator: Lang.isObject,
				},

				frontendJsStateWebModule: {
					validator: Lang.isObject,
				},

				helpMessage: {
					validator: Lang.isString,
				},

				id: {
					validator: Lang.isString,
				},

				inputBox: {
					setter: A.one,
				},

				inputPlaceholder: {
					setter: A.one,
				},

				instanceId: {
					value: Lang.isString,
				},

				items: {
					value: availableLanguageIds,
				},

				itemsError: {
					validator: Array.isArray,
				},

				languagesTranslationsAriaLabels: {
					validator: Lang.isObject,
				},

				name: {
					validator: Lang.isString,
				},

				namespace: {
					validator: Lang.isString,
				},

				selected: {
					valueFn() {
						const instance = this;

						const itemsError = instance.get(STR_ITEMS_ERROR);

						let itemIndex =
							instance.get('selectedLanguageId') ||
							instance.get('defaultLanguageId');

						if (itemsError.length) {
							itemIndex = itemsError[0];
						}

						return instance.get(STR_ITEMS).indexOf(itemIndex);
					},
				},

				selectedLanguageId: {
					validator: Lang.isString,
				},

				translatedLanguages: {
					setter(val) {
						const set = new A.Set();

						if (Lang.isString(val)) {
							val.split(',').forEach(set.add, set);
						}

						return set;
					},
					value: null,
				},
			},

			EXTENDS: A.Palette,

			NAME: 'input-localized',

			prototype: {
				_State: null,

				_activeLanguageIdsAtom: null,

				_animate(input, shouldFocus) {
					const instance = this;

					const animateClass = instance.get('animateClass');

					if (animateClass) {
						input.removeClass(animateClass);

						clearTimeout(instance._animating);

						setTimeout(() => {
							input.addClass(animateClass);

							if (shouldFocus) {
								input.focus();
							}
						}, 0);

						instance._animating = setTimeout(() => {
							input.removeClass(animateClass);

							clearTimeout(instance._animating);
						}, 700);
					}
				},

				_animating: null,

				_availableLanguagesSubscription: null,

				_bindManageTranslationsButton() {
					const instance = this;

					const boundingBox = instance.get('boundingBox');

					if (instance.get('adminMode')) {
						const manageTranslationsButton = boundingBox.one(
							'#manage-translations'
						);

						instance._eventHandles.push(
							manageTranslationsButton.on(
								'click',
								A.bind('_onManageTranslationsClick', instance)
							)
						);
					}
				},

				_clearFormValidator(input) {
					const form = input.get('form');

					const liferayForm = Liferay.Form.get(form.attr('id'));

					if (liferayForm) {
						const validator = liferayForm.formValidator;

						if (A.instanceOf(validator, A.FormValidator)) {
							validator.resetAllFields();
						}
					}
				},

				_flags: null,

				_flagsInitialContent: null,

				_getInputLanguage(languageId) {
					const instance = this;

					const fieldPrefix = instance.get('fieldPrefix');
					const fieldPrefixSeparator = instance.get(
						'fieldPrefixSeparator'
					);
					const id = instance.get('id');
					const inputBox = instance.get('inputBox');
					const name = instance.get('name');
					const namespace = instance.get('namespace');

					let fieldNamePrefix = STR_BLANK;
					let fieldNameSuffix = STR_BLANK;

					if (fieldPrefix) {
						fieldNamePrefix = fieldPrefix + fieldPrefixSeparator;
						fieldNameSuffix = fieldPrefixSeparator;
					}

					let inputLanguage = inputBox.one(
						instance._getInputLanguageId(languageId)
					);

					if (!inputLanguage) {
						inputLanguage = A.Node.create(
							A.Lang.sub(instance.INPUT_HIDDEN_TEMPLATE, {
								fieldNamePrefix,
								fieldNameSuffix,
								id,
								languageId,
								name: A.Lang.String.escapeHTML(name),
								namespace,
							})
						);

						inputBox.append(inputLanguage);
					}

					return inputLanguage;
				},

				_getInputLanguageId(languageId) {
					const instance = this;

					const id = instance.get('id');
					const namespace = instance.get('namespace');

					return '#' + namespace + id + '_' + languageId;
				},

				_moveDefaultLanguageFlagToFirstPosition(defaultLanguageId) {
					const instance = this;

					const flags = instance._flags.getDOMNode();

					const languageNode = flags.querySelector(
						'[data-languageid="' + defaultLanguageId + '"]'
					)?.parentElement;

					if (languageNode) {
						flags.removeChild(languageNode);

						flags.insertBefore(
							languageNode,
							flags.firstElementChild
						);
					}
				},

				_onActiveLanguageIdsChange(activeLanguageIds) {
					const instance = this;

					instance.set('activeLanguageIds', activeLanguageIds);

					instance._renderActiveLanguageIds();
				},

				_onDefaultLocaleChanged(event) {
					const instance = this;

					const prevDefaultLanguageId =
						instance.get('defaultLanguageId');
					const prevDefaultValue = instance.getValue(
						prevDefaultLanguageId
					);

					if (!prevDefaultValue) {
						instance.removeInputLanguage(prevDefaultLanguageId);
						instance.updateInputLanguage(
							prevDefaultValue,
							prevDefaultLanguageId
						);
					}

					const defaultLanguageId =
						event.item.getAttribute('data-value');

					instance.set('defaultLanguageId', defaultLanguageId);

					instance._updateTranslationStatus(defaultLanguageId);
					instance._updateTranslationStatus(prevDefaultLanguageId);

					instance._moveDefaultLanguageFlagToFirstPosition(
						defaultLanguageId
					);

					Liferay.fire('inputLocalized:localeChanged', {
						item: event.item,
						source: instance,
					});
				},

				_onInputValueChange(event, input) {
					const instance = this;

					const editor = instance.get('editor');

					let value;

					if (editor) {
						value = editor.getHTML();
					}
					else {
						input = input || event.currentTarget;

						value = input.val();
					}

					if (Liferay.FeatureFlags['LPD-11228']) {
						instance.set('edited', true);
					}

					instance.updateInputLanguage(value);
				},

				_onLocaleChanged(event) {
					const instance = this;

					const languageId = event.item.getAttribute('data-value');

					instance.selectFlag(languageId, event.source === instance);
				},

				_onManageTranslationsClick() {
					const instance = this;

					const modalContainerId =
						instance.get('namespace') + 'modal-container';
					let modalContainer;

					if (!document.getElementById(modalContainerId)) {
						modalContainer = document.createElement('div');
						document.body.appendChild(modalContainer);
					}

					const availableLocales = instance.get('availableLocales');

					const locales = instance.get('items').map((languageId) => {
						const displayName = availableLocales[languageId];

						const label = languageId.replaceAll(/_/g, '-');

						return {
							displayName,
							id: languageId,
							label,
							symbol: label.toLowerCase(),
						};
					});

					const translations = instance
						.get('translatedLanguages')
						.values()
						.reduce((accumulator, item) => {
							const language = item.replaceAll(/_/g, '-');

							if (!accumulator[language]) {
								accumulator[language] = language;
							}

							return accumulator;
						}, {});

					const props = {
						activeLanguageIds: instance.get('activeLanguageIds'),
						availableLocales: locales,
						defaultLanguageId: instance.get('defaultLanguageId'),
						onClose(newActiveLanguageIds) {
							instance._State.writeAtom(
								instance._activeLanguageIdsAtom,
								newActiveLanguageIds
							);
						},
						translations,
						visible: true,
					};

					instance
						.get('frontendJsReactWebModule')
						.render(
							instance.get('frontendJsComponentsWebModule')
								.TranslationAdminModal,
							props,
							modalContainer
						);
				},

				_onMarkAsTranslated(event) {
					const instance = this;

					const languageId = event.selectedLanguageId;

					const translatedLanguages = instance.get(
						'translatedLanguages'
					);

					translatedLanguages.add(languageId);

					this.updateInputLanguage(this.getValue(), languageId);

					instance._updateTranslationStatus(languageId);
				},

				_onSelectFlag(event) {
					const instance = this;

					const languageId = event.item.getAttribute('data-value');

					instance._State.writeAtom(
						instance._selectedLanguageIdAtom,
						languageId
					);

					if (!event.domEvent) {
						Liferay.fire('inputLocalized:localeChanged', {
							item: event.item,
							source: instance,
						});

						Liferay.fire('journal:localeChanged', {
							item: event.item,
							source: instance,
						});
					}
				},

				_onSelectedLanguageIdChange(languageId) {
					const instance = this;

					instance.selectFlag(languageId);
				},

				_onSubmit(event, input) {
					const instance = this;

					if (event.form === input.get('form')) {
						instance._onInputValueChange.apply(instance, arguments);

						InputLocalized.unregister(input.attr('id'));
					}
				},

				_renderActiveLanguageIds() {
					const instance = this;

					const activeLanguageIds = instance.get('activeLanguageIds');

					const defaultLanguageId = instance.get('defaultLanguageId');

					const translatedLanguages = instance.get(
						'translatedLanguages'
					);

					const selectedLanguageId = instance.getSelectedLanguageId();

					const currentFlagsNode = instance._flags.getDOMNode();

					const newFlagsNode = instance._flagsInitialContent
						.cloneNode(true)
						.getDOMNode();

					let changeToDefault;

					Object.entries(instance.get('availableLocales')).forEach(
						(entry) => {
							const key = entry[0];

							const localeNode = newFlagsNode.querySelector(
								'[data-languageid="' + key + '"]'
							)?.parentElement;

							if (!activeLanguageIds.includes(key)) {
								if (localeNode) {
									newFlagsNode.removeChild(localeNode);
								}

								instance.removeInputLanguage(key);
								translatedLanguages['remove'](key);

								if (key === selectedLanguageId) {
									changeToDefault = true;
								}
							}
							else {
								const currentlocaleNode =
									currentFlagsNode.querySelector(
										'[data-languageid="' + key + '"]'
									)?.parentElement;

								if (currentlocaleNode) {
									localeNode.innerHTML =
										currentlocaleNode.innerHTML;
								}
							}
						}
					);

					currentFlagsNode.innerHTML = newFlagsNode.innerHTML;

					Object.entries(instance.get('availableLocales')).forEach(
						([key]) => {
							this._updateTranslationStatus(key);
						}
					);

					const boundingBox = instance.get('boundingBox');
					instance._flags = boundingBox.one('.palette-container');

					if (changeToDefault) {
						instance._onSelectFlag({
							item: currentFlagsNode.querySelector(
								'[data-languageid="' + defaultLanguageId + '"]'
							),
						});
					}

					if (instance.get('adminMode')) {
						instance._bindManageTranslationsButton();
					}
				},

				_selectedLanguageIdAtom: null,

				_selectedLanguageIdSubscription: null,

				_storeState() {
					const instance = this;

					if (instance.get('edited')) {
						instance.set('edited', false);

						Liferay.fire('journal:storeState', {
							fieldName:
								Liferay.Language.get('edit') +
								' ' +
								document.querySelector(
									"label[for='" +
										instance.get('namespace') +
										instance.get('id') +
										"']"
								).textContent,
						});
					}
				},

				_updateHelpMessage(languageId) {
					const instance = this;

					let helpMessage = instance.get('helpMessage');

					if (!instance.get('editor')) {
						const defaultLanguageId =
							instance.get('defaultLanguageId');

						if (languageId !== defaultLanguageId) {
							helpMessage = instance.getValue(defaultLanguageId);
						}

						helpMessage = Liferay.Util.escapeHTML(helpMessage);
					}

					instance
						.get('inputBox')
						.next('.form-text')
						.setHTML(helpMessage);
				},

				_updateInputPlaceholderDescription(languageId) {
					const instance = this;

					if (instance._inputPlaceholderDescription) {
						const icon = instance._flags.one(
							'[data-languageId="' + languageId + '"]'
						);

						let title = '';

						if (icon) {
							title = icon.attr('title');
						}

						instance._inputPlaceholderDescription.text(title);
					}
				},

				_updateSelectedItem(languageId) {
					const instance = this;

					instance._flags.all('.active').toggleClass('active');

					const selectedLanguageId =
						languageId || instance.getSelectedLanguageId();

					const flagNode = instance._flags.one(
						'[data-languageid="' + selectedLanguageId + '"]'
					);

					if (flagNode) {
						flagNode.toggleClass('active');
					}
				},

				_updateTranslationStatus(languageId) {
					const instance = this;

					const languagesTranslationsAriaLabels = instance.get(
						'languagesTranslationsAriaLabels'
					);

					const translatedLanguages = instance.get(
						'translatedLanguages'
					);

					let translationAriaLabel =
						languagesTranslationsAriaLabels[languageId][
							'notTranslatedStatus'
						];
					let translationStatus =
						Liferay.Language.get('not-translated');
					let translationStatusCssClass = 'warning';

					if (translatedLanguages.has(languageId)) {
						translationAriaLabel =
							languagesTranslationsAriaLabels[languageId][
								'translatedStatus'
							];
						translationStatus = Liferay.Language.get('translated');
						translationStatusCssClass = 'success';
					}

					if (languageId === instance.get('defaultLanguageId')) {
						translationAriaLabel =
							languagesTranslationsAriaLabels[languageId][
								'defaultStatus'
							];
						translationStatus = Liferay.Language.get('default');
						translationStatusCssClass = 'info';
					}

					const languageStatusNode = instance._flags.one(
						'[data-languageid="' +
							languageId +
							'"] .taglib-text-icon'
					);

					if (languageStatusNode) {
						languageStatusNode.setHTML(
							A.Lang.sub(instance.TRANSLATION_STATUS_TEMPLATE, {
								languageId: languageId.replaceAll(/_/g, '-'),
								translationAriaLabel,
								translationStatus,
								translationStatusCssClass,
							})
						);
					}
				},

				_updateTrigger(languageId) {
					const instance = this;

					const languagesTranslationsAriaLabels = instance.get(
						'languagesTranslationsAriaLabels'
					);

					const updatedTriggerAriaLabel =
						languagesTranslationsAriaLabels[languageId][
							'currentlySelected'
						];

					languageId = languageId.replaceAll('_', '-');

					const triggerContent = A.Lang.sub(
						instance.TRIGGER_TEMPLATE,
						{
							flag: Liferay.Util.getLexiconIconTpl(
								languageId.toLowerCase()
							),
							languageId,
							updatedTriggerAriaLabel,
						}
					);

					instance
						.get('inputBox')
						.one('.input-localized-trigger')
						.setHTML(triggerContent);
				},

				INPUT_HIDDEN_TEMPLATE:
					'<input data-field-name={name} data-languageid={languageId} id="{namespace}{id}_{languageId}" name="{namespace}{fieldNamePrefix}{name}_{languageId}{fieldNameSuffix}" type="hidden" value="" />',

				TRANSLATION_STATUS_TEMPLATE:
					'<span aria-label="{translationAriaLabel}" role="button" tabindex="0"> {languageId} <span class="dropdown-item-indicator-end w-auto"><span class="label label-{translationStatusCssClass}">{translationStatus}</span></span></span>',

				TRIGGER_TEMPLATE:
					'<span class="inline-item">{flag}</span><span aria-label="{updatedTriggerAriaLabel}" class="btn-section">{languageId}</span>',

				destructor() {
					const instance = this;

					InputLocalized.unregister(instance.get('instanceId'));

					new A.EventHandle(instance._eventHandles).detach();

					if (instance._availableLanguagesSubscription) {
						instance._availableLanguagesSubscription.dispose();
					}

					if (instance._selectedLanguageIdSubscription) {
						instance._selectedLanguageIdSubscription.dispose();
					}
				},

				getSelectedLanguageId() {
					const instance = this;

					const items = instance.get(STR_ITEMS);
					const selected = instance.get(STR_SELECTED);

					return (
						items[selected] || instance.get('selectedLanguageId')
					);
				},

				getValue(languageId) {
					const instance = this;

					if (!Lang.isValue(languageId)) {
						languageId = defaultLanguageId;
					}

					return instance._getInputLanguage(languageId).val();
				},

				initializer() {
					const instance = this;

					const inputPlaceholder = instance.get(
						STR_INPUT_PLACEHOLDER
					);

					const eventHandles = [
						inputPlaceholder
							.get('form')
							.on(
								'submit',
								A.rbind(STR_SUBMIT, instance, inputPlaceholder)
							),
						instance.after(
							'select',
							instance._onSelectFlag,
							instance
						),
						Liferay.after(
							'inputLocalized:defaultLocaleChanged',
							A.bind('_onDefaultLocaleChanged', instance)
						),
						Liferay.on(
							'inputLocalized:localeChanged',
							A.bind('_onLocaleChanged', instance)
						),
						Liferay.on(
							'inputLocalized:markAsTranslated',
							A.bind('_onMarkAsTranslated', instance)
						),
						Liferay.on(
							'submitForm',
							A.rbind(STR_SUBMIT, instance, inputPlaceholder)
						),
					];

					if (!instance.get('editor')) {
						if (Liferay.FeatureFlags['LPD-11228']) {
							eventHandles.push(
								inputPlaceholder.on(
									'blur',
									A.bind('_storeState', instance)
								)
							);
						}
						eventHandles.push(
							inputPlaceholder.on(
								'input',
								A.debounce('_onInputValueChange', 100, instance)
							)
						);
					}

					instance._eventHandles = eventHandles;

					const boundingBox = instance.get('boundingBox');

					boundingBox.plug(A.Plugin.NodeFocusManager, {
						descendants: '.palette-item a',
						keys: {
							next: 'down:39,40',
							previous: 'down:37,38',
						},
					});

					instance._inputPlaceholderDescription = boundingBox.one(
						'#' + inputPlaceholder.attr('id') + '_desc'
					);
					instance._flags = boundingBox.one('.palette-container');

					instance._State = instance.get(
						'frontendJsStateWebModule'
					).State;

					instance._selectedLanguageIdAtom = instance.get(
						'frontendJsComponentsWebModule'
					).selectedLanguageIdAtom;

					const selectedLanguageIdAtom =
						instance._selectedLanguageIdAtom;

					instance._selectedLanguageIdSubscription =
						instance._State.subscribe(
							selectedLanguageIdAtom,
							A.bind('_onSelectedLanguageIdChange', instance)
						);

					const activeLanguageIds = instance.get('activeLanguageIds');

					if (activeLanguageIds) {
						instance._activeLanguageIdsAtom = instance.get(
							'frontendJsComponentsWebModule'
						).activeLanguageIdsAtom;

						instance._flagsInitialContent =
							instance._flags.cloneNode(true);

						instance._renderActiveLanguageIds();

						const activeLanguageIdsAtom =
							instance._activeLanguageIdsAtom;

						if (instance.get('adminMode')) {
							instance._State.writeAtom(
								activeLanguageIdsAtom,
								activeLanguageIds
							);
						}

						instance._availableLanguagesSubscription =
							instance._State.subscribe(
								activeLanguageIdsAtom,
								A.bind('_onActiveLanguageIdsChange', instance)
							);
					}
				},

				removeInputLanguage(languageId) {
					const instance = this;

					const inputBox = instance.get('inputBox');

					const inputLanguage = inputBox.one(
						instance._getInputLanguageId(languageId)
					);

					if (inputLanguage) {
						inputLanguage.remove();
					}
				},

				selectFlag(languageId, shouldFocus) {
					const instance = this;

					if (!Lang.isValue(languageId)) {
						languageId = defaultLanguageId;
					}

					const inputPlaceholder = instance.get(
						STR_INPUT_PLACEHOLDER
					);

					const defaultLanguageValue =
						instance.getValue(defaultLanguageId);

					const inputLanguageValue = instance.getValue(languageId);

					instance._animate(inputPlaceholder, shouldFocus);
					instance._clearFormValidator(inputPlaceholder);

					instance._fillDefaultLanguage = !defaultLanguageValue;

					instance.set(
						'selected',
						parseInt(instance.get('items').indexOf(languageId), 10)
					);

					instance.updateInput(inputLanguageValue);

					instance._updateInputPlaceholderDescription(languageId);
					instance._updateHelpMessage(languageId);
					instance._updateTrigger(languageId);
					instance._updateSelectedItem(languageId);
				},

				updateInput(value) {
					const instance = this;

					const inputPlaceholder = instance.get(
						STR_INPUT_PLACEHOLDER
					);

					const editor = instance.get('editor');

					if (editor) {
						editor.setHTML(value);
					}
					else {
						inputPlaceholder.val(value);

						inputPlaceholder.attr(
							'dir',
							Liferay.Language.direction[
								instance.getSelectedLanguageId()
							]
						);
					}
				},

				updateInputLanguage(value, languageId) {
					const instance = this;

					let selectedLanguageId =
						languageId || instance.getSelectedLanguageId();

					if (!Lang.isValue(selectedLanguageId)) {
						selectedLanguageId = defaultLanguageId;
					}

					const defaultInputLanguage =
						instance._getInputLanguage(defaultLanguageId);
					const inputLanguage =
						instance._getInputLanguage(selectedLanguageId);

					inputLanguage.val(value);

					if (selectedLanguageId === defaultLanguageId) {
						if (instance._fillDefaultLanguage) {
							defaultInputLanguage.val(value);
						}
					}

					const translatedLanguages = instance.get(
						'translatedLanguages'
					);

					let action = 'remove';

					if (value) {
						action = 'add';
					}

					translatedLanguages[action](selectedLanguageId);

					instance._updateTranslationStatus(selectedLanguageId);
				},

				updateInputPlaceholder(editor) {
					const instance = this;

					const inputPlaceholder = instance.get(
						STR_INPUT_PLACEHOLDER
					);

					const nativeEditor = editor.getNativeEditor();

					const newPlaceholder = A.one(
						'#' + nativeEditor.element.getId()
					);

					if (inputPlaceholder.compareTo(newPlaceholder)) {
						return;
					}

					instance.set(STR_INPUT_PLACEHOLDER, newPlaceholder);
				},
			},

			register(id, config) {
				const instance = this;

				config.instanceId = id;

				const instances = instance._instances;

				let inputLocalizedInstance = instances[id];

				const createInstance = !(
					inputLocalizedInstance &&
					inputLocalizedInstance
						.get(STR_INPUT_PLACEHOLDER)
						.compareTo(A.one('#' + id))
				);

				if (createInstance) {
					if (inputLocalizedInstance) {
						inputLocalizedInstance.destroy();
					}

					inputLocalizedInstance = new InputLocalized(config);
					inputLocalizedInstance._bindUIPalette();

					instances[id] = inputLocalizedInstance;
				}

				const portletId = inputLocalizedInstance
					.get('namespace')
					.replace(/^_|_$/gm, '');

				Liferay.component(id, inputLocalizedInstance, {
					portletId,
				});
			},

			unregister(id) {
				delete InputLocalized._instances[id];
			},
		});

		Liferay.InputLocalized = InputLocalized;
	},
	'',
	{
		requires: [
			'aui-base',
			'aui-component',
			'aui-event-input',
			'aui-palette',
			'aui-set',
			'liferay-form',
		],
	}
);
