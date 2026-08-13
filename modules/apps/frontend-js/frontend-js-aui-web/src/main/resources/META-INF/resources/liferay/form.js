/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-form',
	(A) => {
		const AArray = A.Array;

		const Lang = A.Lang;

		const DEFAULTS_FORM_VALIDATOR = A.config.FormValidator;

		const defaultAcceptFiles = DEFAULTS_FORM_VALIDATOR.RULES.acceptFiles;

		const TABS_SECTION_STR = 'TabsSection';

		const REGEX_CUSTOM_ELEMENT_NAME =
			/^[a-z]([a-z]|[0-9]|-|\.|_)*-([a-z]|[0-9]|-|\.|_)*/;

		const REGEX_EMAIL =
			/^[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[^\W_](?:[0-9A-Za-z-]*[^\W_])?\.)+([^\W_](?:[0-9A-Za-z-]*[^\W_]))$/;

		const REGEX_FRIENDLY_URL_MAPPING = /[A-Za-z0-9-_]*/;

		const REGEX_NUMBER = /^[+-]?(\d+)([.|,]\d+)*([eE][+-]?\d+)?$/;

		const REGEX_URL =
			/((([A-Za-z]{3,9}:(?:\/\/)?)(?:[-;:&=+$,\w]+@)?[A-Za-z0-9.-]+|(https?:\/\/|www.|[-;:&=+$,\w]+@)[A-Za-z0-9.-]+)((?:\/[+~%/.\w-_]*)?\??(?:[-+=&;%@.\w_]*)#?(?:[\w]*))((.*):(\d*)\/?(.*))?)/;

		const REGEX_URL_ALLOW_RELATIVE =
			/((([A-Za-z]{3,9}:(?:\/\/)?)|\/(?:[-;:&=+$,\w]+@)?[A-Za-z0-9.-]+|(https?:\/\/|www.|[-;:&=+$,\w]+@)[A-Za-z0-9.-]+)((?:\/[+~%/.\w-_]*)?\??(?:[-+=&;%@.\w_]*)#?(?:[\w]*))((.*):(\d*)\/?(.*))?)/;

		const RESERVED_CUSTOM_ELEMENT_NAMES = new Set([
			'annotation-xml',
			'color-profile',
			'font-face',
			'font-face-format',
			'font-face-name',
			'font-face-src',
			'font-face-uri',
			'missing-glyph',
		]);

		const acceptFiles = function (val, node, ruleValue) {
			if (ruleValue && ruleValue.split(',').includes('*')) {
				return true;
			}

			return defaultAcceptFiles(val, node, ruleValue);
		};

		const customElementName = function (val, _node, _ruleValue) {
			return (
				REGEX_CUSTOM_ELEMENT_NAME.test(val) &&
				!RESERVED_CUSTOM_ELEMENT_NAMES.has(val)
			);
		};

		const email = function (val) {
			return REGEX_EMAIL.test(val);
		};

		const friendlyURLMapping = function (val, _node, _ruleValue) {
			return REGEX_FRIENDLY_URL_MAPPING.test(val);
		};

		const maxFileSize = function (_val, node, ruleValue) {
			const nodeType = node.get('type').toLowerCase();

			if (nodeType === 'file') {
				return ruleValue === 0 || node._node.files[0].size <= ruleValue;
			}

			return true;
		};

		const number = function (val, _node, _ruleValue) {
			return REGEX_NUMBER && REGEX_NUMBER.test(val);
		};

		const url = function (val, _node, _ruleValue) {
			return REGEX_URL && REGEX_URL.test(val);
		};

		const urlAllowRelative = function (val) {
			return (
				REGEX_URL_ALLOW_RELATIVE && REGEX_URL_ALLOW_RELATIVE.test(val)
			);
		};

		A.mix(
			DEFAULTS_FORM_VALIDATOR.RULES,
			{
				acceptFiles,
				customElementName,
				email,
				friendlyURLMapping,
				maxFileSize,
				number,
				url,
				urlAllowRelative,
			},
			true
		);

		A.mix(
			DEFAULTS_FORM_VALIDATOR.STRINGS,
			{
				DEFAULT: Liferay.Language.get('please-fix-this-field'),
				acceptFiles: Liferay.Language.get(
					'please-enter-a-file-with-a-valid-extension-x'
				),
				alpha: Liferay.Language.get(
					'please-enter-only-alpha-characters'
				),
				alphanum: Liferay.Language.get(
					'please-enter-only-alphanumeric-characters'
				),
				customElementName: Liferay.Language.get(
					'please-enter-a-valid-html-element-name'
				),
				date: Liferay.Language.get('please-enter-a-valid-date'),
				digits: Liferay.Language.get('please-enter-only-digits'),
				email: Liferay.Language.get(
					'please-enter-a-valid-email-address'
				),
				equalTo: Liferay.Language.get(
					'please-enter-the-same-value-again'
				),
				friendlyURLMapping: Liferay.Language.get(
					'please-enter-a-valid-friendly-url-mapping'
				),
				max: Liferay.Language.get(
					'please-enter-a-value-less-than-or-equal-to-x'
				),
				maxFileSize: Liferay.Language.get(
					'please-enter-a-file-with-a-valid-file-size-no-larger-than-x'
				),
				maxLength: Liferay.Language.get(
					'please-enter-no-more-than-x-characters'
				),
				min: Liferay.Language.get(
					'please-enter-a-value-greater-than-or-equal-to-x'
				),
				minLength: Liferay.Language.get(
					'please-enter-at-list-x-characters'
				),
				number: Liferay.Language.get('please-enter-a-valid-number'),
				range: Liferay.Language.get(
					'please-enter-a-value-between-x-and-x'
				),
				rangeLength: Liferay.Language.get(
					'please-enter-a-value-between-x-and-x-characters-long'
				),
				required: Liferay.Language.get('this-field-is-required'),
				url: Liferay.Language.get('please-enter-a-valid-url'),
				urlAllowRelative: Liferay.Language.get(
					'please-enter-a-valid-url'
				),
			},
			true
		);

		const Form = A.Component.create({
			_INSTANCES: {},

			ATTRS: {
				fieldRules: {
					setter(val) {
						const instance = this;

						instance._processFieldRules(val);

						return val;
					},
				},
				id: {},
				namespace: {},
				onSubmit: {
					valueFn() {
						const instance = this;

						return instance._onSubmit;
					},
				},
				validateOnBlur: {
					validator: Lang.isBoolean,
					value: true,
				},
			},

			EXTENDS: A.Base,

			get(id) {
				const instance = this;

				return instance._INSTANCES[id];
			},

			prototype: {
				_afterGetFieldsByName(fieldName) {
					const instance = this;

					const editorString = 'Editor';

					if (
						fieldName.lastIndexOf(editorString) ===
						fieldName.length - editorString.length
					) {
						const formNode = instance.formNode;

						return new A.Do.AlterReturn(
							'Return editor dom element',
							formNode.one('#' + fieldName)
						);
					}
				},

				_bindForm() {
					const instance = this;

					const formNode = instance.formNode;
					const formValidator = instance.formValidator;

					formValidator.on(
						'submit',
						A.bind('_onValidatorSubmit', instance)
					);
					formValidator.on(
						'submitError',
						A.bind('_onSubmitError', instance)
					);

					formNode.delegate(
						['blur', 'focus'],
						A.bind('_onFieldFocusChange', instance),
						'button,input,select,textarea'
					);
					formNode.delegate(
						['blur', 'input'],
						A.bind('_onEditorBlur', instance),
						'div[contenteditable="true"]'
					);

					A.Do.after(
						'_afterGetFieldsByName',
						formValidator,
						'getFieldsByName',
						instance
					);
				},

				_defaultSubmitFn(event) {
					const instance = this;

					if (!event.stopped) {
						submitForm(instance.form);
					}
				},

				_findRuleIndex(fieldRules, fieldName, validatorName) {
					let ruleIndex = -1;

					AArray.some(fieldRules, (element, index) => {
						if (
							element.fieldName === fieldName &&
							element.validatorName === validatorName
						) {
							ruleIndex = index;

							return true;
						}
					});

					return ruleIndex;
				},

				_focusInvalidFieldTab() {
					const instance = this;

					const formNode = instance.formNode;

					const field = formNode.one(
						'.' + instance.formValidator.get('errorClass')
					);

					if (field) {
						const fieldWrapper = field.ancestor(
							'form > fieldset > div, form > div'
						);

						const formTabs = formNode.one('.lfr-nav');

						if (fieldWrapper && formTabs) {
							const tabs = formTabs.all('.nav-item');
							const tabsNamespace = formTabs.getAttribute(
								'data-tabs-namespace'
							);

							const tabNames = AArray.map(tabs._nodes, (tab) => {
								return tab.getAttribute('data-tab-name');
							});

							const fieldWrapperId = fieldWrapper
								.getAttribute('id')
								.slice(0, -TABS_SECTION_STR.length);

							const fieldTabId = AArray.find(
								tabs._nodes,
								(tab) => {
									return (
										tab
											.getAttribute('id')
											.indexOf(fieldWrapperId) !== -1
									);
								}
							);

							if (tabsNamespace) {
								Liferay.Portal.Tabs.show(
									tabsNamespace,
									tabNames,
									fieldTabId.getAttribute('data-tab-name')
								);
							}
						}
					}
				},

				_onEditorBlur(event) {
					const instance = this;

					const formValidator = instance.formValidator;

					formValidator.validateField(event.target);
				},

				_onFieldFocusChange(event) {
					const row = event.currentTarget.ancestor('.field');

					if (row) {
						row.toggleClass(
							'field-focused',
							event.type === 'focus'
						);
					}
				},

				_onSubmit(event) {
					const instance = this;

					event.preventDefault();

					setTimeout(() => {
						instance._defaultSubmitFn(event);
					}, 0);
				},

				_onSubmitError() {
					const instance = this;

					const collapsiblePanels =
						instance.formNode.all('.panel-collapse');

					collapsiblePanels.each((panel) => {
						const errorFields = panel
							.get('children')
							.all('.has-error');

						if (errorFields.size() > 0 && !panel.hasClass('show')) {
							const panelNode = panel.getDOM();

							Liferay.CollapseProvider.show({panel: panelNode});
						}
					});
				},

				_onValidatorSubmit(event) {
					const instance = this;

					const onSubmit = instance.get('onSubmit');

					onSubmit.call(instance, event.validator.formEvent);
				},

				_processFieldRule(rules, strings, rule) {
					const instance = this;

					let value = true;

					const fieldName = rule.fieldName;
					const validatorName = rule.validatorName;

					const field = this.formValidator.getField(fieldName);

					if (field) {
						const fieldNode = field.getDOMNode();

						A.Do.after(
							'_setFieldAttribute',
							fieldNode,
							'setAttribute',
							instance,
							fieldName
						);

						A.Do.after(
							'_removeFieldAttribute',
							fieldNode,
							'removeAttribute',
							instance,
							fieldName
						);
					}

					if ((rule.body || rule.body === 0) && !rule.custom) {
						value = rule.body;
					}

					let fieldRules = rules[fieldName];

					if (!fieldRules) {
						fieldRules = {};

						rules[fieldName] = fieldRules;
					}

					fieldRules[validatorName] = value;

					if (rule.custom) {
						DEFAULTS_FORM_VALIDATOR.RULES[validatorName] =
							rule.body;
					}

					const errorMessage = rule.errorMessage;

					if (errorMessage) {
						let fieldStrings = strings[fieldName];

						if (!fieldStrings) {
							fieldStrings = {};

							strings[fieldName] = fieldStrings;
						}

						fieldStrings[validatorName] = errorMessage;
					}
				},

				_processFieldRules(fieldRules) {
					const instance = this;

					if (!fieldRules) {
						fieldRules = instance.get('fieldRules');
					}

					const fieldStrings = {};
					const rules = {};

					for (const rule in fieldRules) {
						instance._processFieldRule(
							rules,
							fieldStrings,
							fieldRules[rule]
						);
					}

					const formValidator = instance.formValidator;

					if (formValidator) {
						formValidator.set('fieldStrings', fieldStrings);
						formValidator.set('rules', rules);
					}
				},

				_removeFieldAttribute(name, fieldName) {
					if (name === 'disabled') {
						this.formValidator.validateField(fieldName);
					}
				},

				_setFieldAttribute(name, value, fieldName) {
					if (name === 'disabled') {
						this.formValidator.resetField(fieldName);
					}
				},

				_validatable(field) {
					let result;

					if (field.test(':disabled')) {
						result = new A.Do.Halt();
					}

					return result;
				},

				addRule(fieldName, validatorName, errorMessage, body, custom) {
					const instance = this;

					const fieldRules = instance.get('fieldRules');

					const ruleIndex = instance._findRuleIndex(
						fieldRules,
						fieldName,
						validatorName
					);

					if (ruleIndex === -1) {
						fieldRules.push({
							body: body || '',
							custom: custom || false,
							errorMessage: errorMessage || '',
							fieldName,
							validatorName,
						});

						instance._processFieldRules(fieldRules);
					}
				},

				initializer() {
					const instance = this;

					const id = instance.get('id');

					const form = document[id];
					const formNode = A.one(form);

					instance.form = form;
					instance.formNode = formNode;

					if (formNode) {
						const formValidator = new A.FormValidator({
							boundingBox: formNode,
							messageContainer: '<div></div>',
							stackErrorContainer:
								'<div class="form-feedback-item form-validator-stack help-block" role="alert"></div>',
							validateOnBlur: instance.get('validateOnBlur'),
						});

						A.Do.before(
							'_focusInvalidFieldTab',
							formValidator,
							'focusInvalidField',
							instance
						);

						A.Do.before(
							'_validatable',
							formValidator,
							'validatable',
							instance
						);

						instance.formValidator = formValidator;

						instance._processFieldRules();

						instance._bindForm();
					}
				},

				removeRule(fieldName, validatorName) {
					const instance = this;

					const fieldRules = instance.get('fieldRules');

					const ruleIndex = instance._findRuleIndex(
						fieldRules,
						fieldName,
						validatorName
					);

					if (ruleIndex !== -1) {
						const rule = fieldRules[ruleIndex];

						instance.formValidator.resetField(rule.fieldName);

						fieldRules.splice(ruleIndex, 1);

						instance._processFieldRules(fieldRules);
					}
				},
			},

			/*
			 * @deprecated As of Mueller (7.2.x), with no direct replacement
			 */
			register(config) {
				const instance = this;

				const form = new Liferay.Form(config);

				const formName = config.id || config.namespace;

				instance._INSTANCES[formName] = form;

				Liferay.fire('form:registered', {
					form,
					formName,
				});

				return form;
			},
		});

		Liferay.Form = Form;
	},
	'',
	{
		requires: ['aui-base', 'aui-form-validator'],
	}
);
