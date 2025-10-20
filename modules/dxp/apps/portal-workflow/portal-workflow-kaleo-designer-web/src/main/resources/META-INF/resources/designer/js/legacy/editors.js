/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-kaleo-designer-editors',
	(A) => {
		const AArray = A.Array;
		const getClassName = A.getClassName;
		const Lang = A.Lang;
		const Template = A.Template;
		const WidgetStdMod = A.WidgetStdMod;

		const emptyFn = Lang.emptyFn;
		const isBoolean = Lang.isBoolean;
		const isValue = Lang.isValue;

		const KaleoDesignerRemoteServices = Liferay.KaleoDesignerRemoteServices;
		const KaleoDesignerStrings = Liferay.KaleoDesignerStrings;

		const serializeForm = Liferay.KaleoDesignerUtils.serializeForm;

		const CSS_CELLEDITOR_ASSIGNMENT_VIEW = getClassName(
			'celleditor',
			'assignment',
			'view'
		);

		const CSS_CELLEDITOR_VIEW_TYPE = getClassName(
			'celleditor',
			'view',
			'type'
		);

		const STR_BLANK = '';

		const STR_DASH = '-';

		const STR_DOT = '.';

		const STR_REMOVE_DYNAMIC_VIEW_BUTTON =
			'<div class="celleditor-view-menu">' +
			'<a class="celleditor-view-menu-remove btn btn-link btn-sm" href="#" title="' +
			KaleoDesignerStrings.remove +
			'">' +
			Liferay.Util.getLexiconIconTpl('times') +
			'</a>' +
			'</div>';

		const SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE =
			STR_DOT + CSS_CELLEDITOR_VIEW_TYPE + STR_DASH;

		const BaseAbstractEditor = A.Component.create({
			ATTRS: {
				builder: {},

				editorForm: {},

				editorFormClass: {},

				strings: {
					value: KaleoDesignerStrings,
				},

				viewTemplate: {
					setter: '_setViewTemplate',
				},
			},

			EXTENDS: A.BaseCellEditor,

			NAME: 'base-abstract-multi-section-editor',

			prototype: {
				_afterEditorVisibleChange(event) {
					const instance = this;

					if (event.newVal) {
						const editorForm = instance.get('editorForm');

						editorForm.syncViewsUI();
					}
				},

				_afterRender() {
					const instance = this;

					BaseAbstractEditor.superclass._afterRender.apply(
						this,
						arguments
					);

					const editorForm = instance.get('editorForm');

					editorForm.addStaticViews();

					instance.customizeToolbar();

					editorForm.syncToolbarUI();

					editorForm.syncViewsUI();
				},

				_getEditorForm(config) {
					const instance = this;

					const editorFormClass = instance.get('editorFormClass');

					const editorForm = new editorFormClass(config);

					const bodyNode = editorForm.get('bodyNode');

					instance.set('bodyContent', bodyNode);

					return editorForm;
				},

				_onClickViewMenu(event) {
					const anchor = event.currentTarget;

					if (anchor.hasClass('celleditor-view-menu-remove')) {
						anchor.ancestor('.celleditor-view').remove();
					}

					event.halt();
				},

				_onDestroyPortlet() {
					const instance = this;

					instance.destroy(true);
				},

				_onValueChange(event) {
					const instance = this;

					const editorForm = instance.get('editorForm');

					editorForm.set('value', event.newVal);
				},

				_setViewTemplate(val) {
					if (!A.instanceOf(val, A.Template)) {
						val = new Template(val);
					}

					return val;
				},

				_syncElementsFocus() {
					const instance = this;

					const editorForm = instance.get('editorForm');

					editorForm.syncElementsFocus();
				},

				customizeToolbar() {
					const instance = this;

					const editorForm = instance.get('editorForm');

					instance.addSectionButton =
						editorForm.get('addSectionButton');

					if (instance.addSectionButton) {
						instance.toolbar.add([instance.addSectionButton]);
					}
				},

				destructor() {
					const instance = this;

					const editorForm = instance.get('editorForm');

					if (editorForm) {
						editorForm.destroy(true);
					}
				},

				getValue() {
					const instance = this;

					const editorForm = instance.get('editorForm');

					return editorForm.getValue();
				},

				initializer(config) {
					const instance = this;

					instance.set('editorForm', instance._getEditorForm(config));

					instance
						.get('boundingBox')
						.delegate(
							'click',
							A.bind(instance._onClickViewMenu, instance),
							'.celleditor-view-menu a'
						);

					instance.after('valueChange', instance._onValueChange);

					instance.after(
						'visibleChange',
						instance._afterEditorVisibleChange
					);

					instance.destroyPortletHandler = Liferay.on(
						'destroyPortlet',
						A.bind(instance._onDestroyPortlet, instance)
					);
				},
			},
		});

		const BaseAbstractEditorForm = A.Component.create({
			ATTRS: {
				addSectionButton: {
					valueFn: '_valueAddSectionButton',
				},

				bodyNode: {
					valueFn() {
						const instance = this;

						if (!instance.bodyNode) {
							const template = instance.get('bodyNodeTemplate');

							const bodyNode = A.Node.create(template);

							bodyNode.addClass('celleditor-full-view');

							instance.bodyNode = bodyNode;
						}

						return instance.bodyNode;
					},
				},

				bodyNodeTemplate: {
					value: [
						'<div class="celleditor-view-full-view">',
						'<div class="celleditor-view-static-view"></div>',
						'<div class="celleditor-view-dynamic-views"></div>',
						'</div>',
					].join(STR_BLANK),
				},

				builder: {},

				dynamicViewSingleton: {
					validator: isBoolean,
					value: false,
					writeOnce: 'initOnly',
				},

				strings: {
					value: KaleoDesignerStrings,
				},

				value: {},

				viewTemplate: {
					setter: '_setViewTemplate',
				},
			},

			EXTENDS: A.Component,

			NAME: 'base-abstract-editor-form',

			UI_ATTRS: ['value'],

			prototype: {
				_addRemoveDynamicViewButton(dynamicViewNode) {
					dynamicViewNode.append(
						A.Node.create(STR_REMOVE_DYNAMIC_VIEW_BUTTON)
					);
				},

				_afterRender() {
					const instance = this;

					instance.addStaticViews();

					instance.syncToolbarUI();

					instance.syncViewsUI();
				},

				_onClickAddSectionButton(event) {
					const instance = this;

					instance.handleAddViewSection(event);

					const viewNodes = instance.getDynamicViews();

					instance._addRemoveDynamicViewButton(viewNodes.last());
				},

				_setViewTemplate(val) {
					if (!A.instanceOf(val, A.Template)) {
						val = new Template(val);
					}

					return val;
				},

				_uiSetValue(val) {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					instance.addDynamicViews(val);

					A.each(val, (item1, index1) => {
						const fields = bodyNode.all('[name="' + index1 + '"]');

						item1 = AArray(item1);

						fields.each((item2, index2) => {
							const value = item1[index2];

							if (
								item2.test(
									'input[type=checkbox],input[type=radio]'
								)
							) {
								item2.set(
									'checked',
									A.DataType.Boolean.parse(
										value || typeof value === 'boolean'
											? value
											: true
									)
								);
							}
							else if (
								item2.test('select[multiple]') &&
								Lang.isArray(value)
							) {
								value.forEach((option) => {
									for (const key in option) {
										item2
											.one(
												'option[value=' +
													option[key] +
													']'
											)
											.set('selected', true);
									}
								});
							}
							else {
								item2.val(value);
							}
						});
					});
				},

				_valueAddSectionButton() {
					const instance = this;

					if (instance.get('dynamicViewSingleton')) {
						instance._addSectionButton = null;
					}
					else if (!instance._addSectionButton) {
						const strings = instance.get('strings');

						const addSectionButton = new A.Button({
							cssClass: 'btn-secondary',
							disabled: true,
							id: 'addSectionButton',
							label: strings.addSection,
							on: {
								click: A.bind(
									instance._onClickAddSectionButton,
									instance
								),
							},
						}).render();

						const bodyNode = instance.get('bodyNode');

						bodyNode.append(addSectionButton.get('boundingBox'));

						instance._addSectionButton = addSectionButton;
					}

					return instance._addSectionButton;
				},

				addDynamicViews: emptyFn,

				addStaticViews: emptyFn,

				appendToDynamicView(view) {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicView = bodyNode.one(
						'.celleditor-view-dynamic-views'
					);

					if (typeof view === 'string') {
						view = A.Node.create(view);
					}

					dynamicView.append(view);

					if (!instance.get('dynamicViewSingleton')) {
						const dynamicViews =
							dynamicView.all('.celleditor-view');

						dynamicViews.each(
							A.bind(
								instance._addRemoveDynamicViewButton,
								instance
							)
						);
					}
				},

				appendToStaticView(view) {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const staticView = bodyNode.one(
						'.celleditor-view-static-view'
					);

					if (typeof view === 'string') {
						view = A.Node.create(view);
					}

					staticView.append(view);
				},

				convertScriptLanguagesToJSONArray(scriptLanguages) {
					const instance = this;

					const scriptLanguagesJSONArray = [];

					const strings = instance.getStrings();

					scriptLanguages.forEach((item) => {
						if (item) {
							scriptLanguagesJSONArray.push({
								label: strings[item],
								value: item,
							});
						}
					});

					return scriptLanguagesJSONArray;
				},

				destructor() {
					const instance = this;

					instance.bodyNode.remove(true);
				},

				getDynamicViews() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicView = bodyNode.one(
						'.celleditor-view-dynamic-views'
					);

					return dynamicView.get('childNodes');
				},

				getScriptLanguages(scriptLanguages) {
					KaleoDesignerRemoteServices.getScriptLanguages((data) => {
						AArray.each(data, (item) => {
							if (item) {
								scriptLanguages.push(item.scriptLanguage);
							}
						});
					});
				},

				getStrings() {
					const instance = this;

					return instance.get('strings');
				},

				getValue() {
					const instance = this;

					return serializeForm(instance.get('bodyNode'));
				},

				handleAddViewSection: emptyFn,

				initializer() {
					const instance = this;

					instance.after('render', instance._afterRender);
				},

				removeAllViews(viewId) {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					if (bodyNode) {
						bodyNode
							.all(SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + viewId)
							.remove();
					}
				},

				syncElementsFocus: emptyFn,

				syncToolbarUI() {
					const instance = this;

					const addSectionButton = instance.get('addSectionButton');

					if (addSectionButton) {
						addSectionButton.set('disabled', !instance.viewId);
					}
				},

				syncViewsUI() {
					const instance = this;

					instance._uiSetValue(instance.get('value'));
				},
			},
		});

		const CompositeEditorFormBase = function () {};

		CompositeEditorFormBase.prototype = {
			getEmbeddedEditorForm(editorFormClass, container, config) {
				const instance = this;

				const editorFormName = editorFormClass.NAME;

				let editorForm = container.getData(
					editorFormName + '-instance'
				);

				if (!editorForm) {

					// eslint-disable-next-line @liferay/aui/no-merge
					config = A.merge(
						{
							builder: instance.get('builder'),
							parentEditor: instance,
							render: false,
						},
						config
					);

					editorForm = new editorFormClass(config);

					container.setData(editorFormName + '-instance', editorForm);
				}

				return editorForm;
			},

			showEditorForm(editorFormClass, container, value, config) {
				const instance = this;

				// eslint-disable-next-line @liferay/aui/no-merge
				config = A.merge(
					{
						render: container,
						value,
					},
					config
				);

				const editor = instance.getEmbeddedEditorForm(
					editorFormClass,
					container,
					config
				);

				const bodyNode = editor.get('bodyNode');

				container.append(bodyNode);

				editor.show();
			},
		};

		const AssignmentsEditorForm = A.Component.create({
			ATTRS: {
				assignmentsType: {
					valueFn: '_valueAssignmentsType',
				},

				roleTypes: {
					valueFn() {
						const instance = this;

						const strings = instance.getStrings();

						return [
							{
								label: strings.account,
								value: 'account',
							},
							{
								label: strings.depot,
								value: 'depot',
							},
							{
								label: strings.organization,
								value: 'organization',
							},
							{
								label: strings.regular,
								value: 'regular',
							},
							{
								label: strings.site,
								value: 'site',
							},
						];
					},
				},

				scriptLanguages: {
					valueFn() {
						const instance = this;

						const scriptLanguages = [];

						instance.getScriptLanguages(scriptLanguages);

						const scriptLanguagesJSONArray =
							instance.convertScriptLanguagesToJSONArray(
								scriptLanguages
							);

						return scriptLanguagesJSONArray;
					},
				},

				strings: {
					valueFn() {

						// eslint-disable-next-line @liferay/aui/no-merge
						return A.merge(KaleoDesignerStrings, {
							assignmentTypeLabel:
								KaleoDesignerStrings.assignmentType,
							defaultAssignmentLabel:
								KaleoDesignerStrings.assetCreator,
						});
					},
				},

				typeSelect: {},

				viewTemplate: {
					value: [
						'<div class="{$ans}celleditor-assignment-view {$ans}celleditor-view {$ans}celleditor-view-type-{viewId} {$ans}hide">',
						'{content}',
						'</div>',
					],
				},
			},

			EXTENDS: BaseAbstractEditorForm,

			NAME: 'assignments-editor-form',

			prototype: {
				_countRoleTypeViews(val) {
					let count = 0;

					if (val) {
						count = val.roleType
							? val.roleType.filter(isValue).length
							: 1;
					}

					return count;
				},

				_countUserViews(val) {
					let count = 0;

					if (val) {
						count = Math.max(
							val.emailAddress
								? val.emailAddress.filter(isValue).length
								: 1,
							val.screenName
								? val.screenName.filter(isValue).length
								: 1,
							val.userId ? val.userId.filter(isValue).length : 1
						);
					}

					return count;
				},

				_onTypeValueChange(event) {
					const instance = this;

					instance.showView(event.currentTarget.val());
				},

				_valueAssignmentsType() {
					const instance = this;

					const strings = instance.getStrings();

					return [
						{
							label: strings.defaultAssignmentLabel,
							value: STR_BLANK,
						},
						{
							label: strings.resourceActions,
							value: 'resourceActions',
						},
						{
							label: strings.role,
							value: 'roleId',
						},
						{
							label: strings.roleType,
							value: 'roleType',
						},
						{
							label: strings.scriptedAssignment,
							value: 'scriptedAssignment',
						},
						{
							label: strings.user,
							value: 'user',
						},
					];
				},

				addDynamicViews(val) {
					const instance = this;

					Liferay.KaleoDesignerAutoCompleteUtil.destroyAll();

					instance.removeAllViews('roleType');

					instance.addViewRoleType(instance._countRoleTypeViews(val));

					instance.removeAllViews('user');

					instance.addViewUser(instance._countUserViews(val));

					if (val) {
						instance.showView(val.assignmentType);
					}
				},

				addStaticViews() {
					const instance = this;

					const strings = instance.getStrings();

					const assignmentsViewTpl = instance.get('viewTemplate');
					const inputTpl = Template.get('input');
					const selectTpl = Template.get('select');
					const textareaTpl = Template.get('textarea');

					const select = selectTpl.render({
						auiCssClass: 'form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						label: strings.assignmentTypeLabel,
						name: 'assignmentType',
						options: instance.get('assignmentsType'),
					});

					const selectWrapper =
						A.Node.create('<div/>').append(select);

					const typeSelect = selectWrapper.one('select');

					instance.set('typeSelect', typeSelect);

					instance.appendToStaticView(selectWrapper);

					typeSelect.on(
						['change', 'keyup'],
						A.bind(instance._onTypeValueChange, instance)
					);

					const buffer = [];

					const resourceActionContent = textareaTpl.parse({
						auiCssClass:
							'celleditor-textarea-small form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.resourceActions,
						name: 'resourceAction',
					});

					buffer.push(
						assignmentsViewTpl.parse({
							content: resourceActionContent,
							viewId: 'resourceActions',
						})
					);

					const roleIdContent = [
						inputTpl.parse({
							auiCssClass:
								'assignments-cell-editor-input form-control input-sm',
							auiLabelCssClass: 'celleditor-label',
							id: A.guid(),
							label: strings.role,
							name: 'roleNameAC',
							placeholder: KaleoDesignerStrings.search,
							size: 35,
							type: 'text',
						}),

						inputTpl.parse({
							auiCssClass:
								'assignments-cell-editor-input form-control input-sm',
							auiLabelCssClass: 'celleditor-label',
							disabled: true,
							id: A.guid(),
							label: strings.roleId,
							name: 'roleId',
							size: 35,
							type: 'text',
						}),
					].join(STR_BLANK);

					buffer.push(
						assignmentsViewTpl.parse({
							content: roleIdContent,
							viewId: 'roleId',
						})
					);

					const scriptedAssignmentContent = [
						textareaTpl.parse({
							auiCssClass:
								'celleditor-textarea-small form-control input-sm',
							auiLabelCssClass: 'celleditor-label',
							id: A.guid(),
							label: strings.script,
							name: 'script',
						}),

						selectTpl.parse({
							auiCssClass: 'form-control input-sm',
							auiLabelCssClass: 'celleditor-label',
							id: A.guid(),
							label: strings.scriptLanguage,
							name: 'scriptLanguage',
							options: instance.get('scriptLanguages'),
						}),
					].join(STR_BLANK);

					buffer.push(
						assignmentsViewTpl.parse({
							content: scriptedAssignmentContent,
							viewId: 'scriptedAssignment',
						})
					);

					instance.appendToStaticView(buffer.join(STR_BLANK));
				},

				addViewRoleType(num) {
					const instance = this;

					num = num || 1;

					const strings = instance.getStrings();

					const assignmentsViewTpl = instance.get('viewTemplate');

					const checkboxTpl = Template.get('checkbox');
					const inputTpl = Template.get('input');
					const selectTpl = Template.get('select');

					const buffer = [];

					for (let i = 0; i < num; i++) {
						const roleTypeContent = [
							selectTpl.parse({
								auiCssClass:
									'assignments-cell-editor-input form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.roleType,
								name: 'roleType',
								options: instance.get('roleTypes'),
							}),

							inputTpl.parse({
								auiCssClass:
									'assignments-cell-editor-input form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.roleName,
								name: 'roleName',
								size: 35,
								type: 'text',
							}),

							'<div class="checkbox">',

							checkboxTpl.parse({
								auiCssClass: 'assignments-cell-editor-input',
								auiLabelCssClass: 'celleditor-label-checkbox',
								checked: false,
								id: A.guid(),
								label: strings.autoCreate,
								name: 'autoCreate',
								type: 'checkbox',
							}),

							'</div>',
						].join(STR_BLANK);

						buffer.push(
							assignmentsViewTpl.parse({
								content: roleTypeContent,
								showMenu: true,
								viewId: 'roleType',
							})
						);
					}

					instance.appendToDynamicView(buffer.join(STR_BLANK));
				},

				addViewUser(num) {
					const instance = this;

					num = num || 1;

					const strings = instance.getStrings();

					const assignmentsViewTpl = instance.get('viewTemplate');

					const inputTpl = Template.get('input');

					const buffer = [];

					for (let i = 0; i < num; i++) {
						const userContent = [
							inputTpl.parse({
								auiCssClass:
									'assignments-cell-editor-input form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.name,
								name: 'fullName',
								placeholder: KaleoDesignerStrings.search,
								size: 35,
								type: 'text',
							}),

							inputTpl.parse({
								auiCssClass:
									'assignments-cell-editor-input form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								disabled: true,
								id: A.guid(),
								label: strings.screenName,
								name: 'screenName',
								size: 35,
								type: 'text',
							}),

							inputTpl.parse({
								auiCssClass:
									'assignments-cell-editor-input form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								disabled: true,
								id: A.guid(),
								label: strings.emailAddress,
								name: 'emailAddress',
								size: 35,
								type: 'text',
							}),

							inputTpl.parse({
								auiCssClass:
									'assignments-cell-editor-input form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								disabled: true,
								id: A.guid(),
								label: strings.userId,
								name: 'userId',
								size: 35,
								type: 'text',
							}),
						].join(STR_BLANK);

						buffer.push(
							assignmentsViewTpl.parse({
								content: userContent,
								showMenu: true,
								viewId: 'user',
							})
						);
					}

					instance.appendToDynamicView(buffer.join(STR_BLANK));
				},

				getViewNodes() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					return bodyNode.all(
						STR_DOT + CSS_CELLEDITOR_ASSIGNMENT_VIEW
					);
				},

				handleAddViewSection(event) {
					const instance = this;

					const button = event.target;

					if (!button.get('disabled')) {
						const viewId = instance.viewId;

						if (viewId === 'user') {
							instance.addViewUser();
						}
						else if (viewId === 'roleType') {
							instance.addViewRoleType();
						}

						instance.showView(viewId);
					}
				},

				showView(viewId) {
					const instance = this;

					instance.viewId = viewId;

					instance.getViewNodes().hide();

					const bodyNode = instance.get('bodyNode');

					bodyNode
						.all(SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + viewId)
						.show();

					instance.syncToolbarUI();
				},

				syncElementsFocus() {
					const instance = this;

					const typeSelect = instance.get('typeSelect');

					typeSelect.focus();
				},

				syncToolbarUI() {
					const instance = this;

					const viewId = instance.viewId;

					const disabled = viewId !== 'roleType' && viewId !== 'user';

					const addSectionButton = instance.get('addSectionButton');

					if (addSectionButton) {
						addSectionButton.set('disabled', disabled);
					}
				},

				syncViewsUI() {
					const instance = this;

					AssignmentsEditorForm.superclass.syncViewsUI.apply(
						this,
						arguments
					);

					const typeSelect = instance.get('typeSelect');

					instance.showView(typeSelect.val());
				},
			},
		});

		const AssignmentsEditor = A.Component.create({
			ATTRS: {
				editorFormClass: {
					value: AssignmentsEditorForm,
				},
			},

			EXTENDS: BaseAbstractEditor,

			NAME: 'assignments-cell-editor',
		});

		const FormsEditorForm = A.Component.create({
			ATTRS: {
				viewTemplate: {
					value: [
						'<div class="{$ans}celleditor-forms-view {$ans}celleditor-view {$ans}celleditor-view-type-{viewId}">{content}</div>',
					],
				},
			},

			EXTENDS: BaseAbstractEditorForm,

			NAME: 'form-editor-form',

			prototype: {
				addStaticViews() {
					const instance = this;

					const strings = instance.getStrings();

					const formsViewTpl = instance.get('viewTemplate');

					const inputTpl = Template.get('input');

					const buffer = [];

					const formsContent = [
						inputTpl.parse({
							auiCssClass:
								'form-control forms-cell-editor-input input-sm',
							auiLabelCssClass: 'celleditor-label',
							id: A.guid(),
							label: strings.formTemplate,
							name: 'templateName',
							placeholder: KaleoDesignerStrings.search,
							size: 35,
							type: 'text',
						}),

						inputTpl.parse({
							auiCssClass:
								'form-control forms-cell-editor-input input-sm',
							auiLabelCssClass: 'celleditor-label',
							id: A.guid(),
							name: 'templateId',
							size: 35,
							type: 'hidden',
						}),
					].join(STR_BLANK);

					buffer.push(
						formsViewTpl.parse({
							content: formsContent,
							viewId: 'formTemplateId',
						})
					);

					instance.appendToStaticView(buffer.join(STR_BLANK));
				},
			},
		});

		const FormsEditor = A.Component.create({
			ATTRS: {
				editorFormClass: {
					value: FormsEditorForm,
				},
			},

			EXTENDS: BaseAbstractEditor,

			NAME: 'forms-cell-editor',
		});

		const ExecutionTypesEditorFormBase = function () {};

		ExecutionTypesEditorFormBase.prototype = {
			_executionTypesSetter(val) {
				const instance = this;

				const strings = instance.getStrings();

				const selectedNode = instance.get('builder.selectedNode');
				const type = selectedNode.get('type');

				if (type === 'task') {
					val.push({
						label: strings.onAssignment,
						value: 'onAssignment',
					});
				}

				val.push(
					{
						label: strings.onEntry,
						value: 'onEntry',
					},
					{
						label: strings.onExit,
						value: 'onExit',
					}
				);

				return val;
			},
		};

		ExecutionTypesEditorFormBase.ATTRS = {
			executionTypes: {
				setter: '_executionTypesSetter',
				value: [],
			},
		};

		const NotificationRecipientsEditorFormConfig = {
			ATTRS: {
				executionTypeSelect: {
					value: null,
				},

				strings: {
					valueFn() {

						// eslint-disable-next-line @liferay/aui/no-merge
						return A.merge(KaleoDesignerStrings, {
							assignmentTypeLabel:
								KaleoDesignerStrings.recipientType,
							defaultAssignmentLabel:
								KaleoDesignerStrings.assetCreator,
						});
					},
				},
			},

			EXTENDS: AssignmentsEditorForm,

			NAME: 'notification-recipients-editor-form',

			prototype: {
				_valueAssignmentsType() {
					const instance = this;

					const strings = instance.getStrings();

					const assignmentsTypes = [
						{
							label: strings.defaultAssignmentLabel,
							value: STR_BLANK,
						},
						{
							label: strings.role,
							value: 'roleId',
						},
						{
							label: strings.roleType,
							value: 'roleType',
						},
						{
							label: strings.scriptedRecipient,
							value: 'scriptedRecipient',
						},
						{
							label: strings.user,
							value: 'user',
						},
					];

					const executionTypeSelect = instance.get(
						'executionTypeSelect'
					);

					const executionType = executionTypeSelect.val();

					if (executionType === 'onAssignment') {
						assignmentsTypes.push({
							label: KaleoDesignerStrings.taskAssignees,
							value: 'taskAssignees',
						});
					}

					return assignmentsTypes;
				},

				addStaticViews() {
					const instance = this;

					const strings = instance.getStrings();

					const assignmentsViewTpl = instance.get('viewTemplate');

					const inputTpl = Template.get('input');
					const selectTpl = Template.get('select');
					const textareaTpl = Template.get('textarea');

					const select = selectTpl.render({
						auiCssClass: 'form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						label: strings.assignmentTypeLabel,
						name: 'assignmentType',
						options: instance.get('assignmentsType'),
					});

					const selectWrapper =
						A.Node.create('<div/>').append(select);

					const typeSelect = selectWrapper.one('select');

					instance.set('typeSelect', typeSelect);

					instance.appendToStaticView(selectWrapper);

					typeSelect.on(
						['change', 'keyup'],
						A.bind(instance._onTypeValueChange, instance)
					);

					const receptionType = inputTpl.parse({
						id: A.guid(),
						name: 'receptionType',
						type: 'hidden',
					});

					instance.appendToStaticView(receptionType);

					const buffer = [];

					const roleIdContent = [
						inputTpl.parse({
							auiCssClass:
								'assignments-cell-editor-input form-control input-sm',
							auiLabelCssClass: 'celleditor-label',
							id: A.guid(),
							label: strings.role,
							name: 'roleNameAC',
							placeholder: KaleoDesignerStrings.search,
							size: 35,
							type: 'text',
						}),

						inputTpl.parse({
							auiCssClass:
								'assignments-cell-editor-input form-control input-sm',
							auiLabelCssClass: 'celleditor-label',
							disabled: true,
							id: A.guid(),
							label: strings.roleId,
							name: 'roleId',
							size: 35,
							type: 'text',
						}),
					].join(STR_BLANK);

					buffer.push(
						assignmentsViewTpl.parse({
							content: roleIdContent,
							viewId: 'roleId',
						})
					);

					const scriptedRecipientContent = [
						textareaTpl.parse({
							auiCssClass:
								'celleditor-textarea-small form-control input-sm',
							auiLabelCssClass: 'celleditor-label',
							id: A.guid(),
							label: strings.script,
							name: 'script',
						}),

						selectTpl.parse({
							auiCssClass: 'form-control input-sm',
							auiLabelCssClass: 'celleditor-label',
							id: A.guid(),
							label: strings.scriptLanguage,
							name: 'scriptLanguage',
							options: instance.get('scriptLanguages'),
						}),
					].join(STR_BLANK);

					buffer.push(
						assignmentsViewTpl.parse({
							content: scriptedRecipientContent,
							viewId: 'scriptedRecipient',
						})
					);

					instance.appendToStaticView(buffer.join(STR_BLANK));
				},
			},
		};

		const NotificationRecipientsEditorForm = A.Component.create(
			NotificationRecipientsEditorFormConfig
		);

		NotificationRecipientsEditorFormConfig.prototype._valueAssignmentsType =
			function () {
				const instance = this;

				const strings = instance.getStrings();

				const assignmentsTypes = [
					{
						label: strings.defaultAssignmentLabel,
						value: STR_BLANK,
					},
					{
						label: strings.role,
						value: 'roleId',
					},
					{
						label: strings.roleType,
						value: 'roleType',
					},
					{
						label: strings.scriptedRecipient,
						value: 'scriptedRecipient',
					},
					{
						label: strings.user,
						value: 'user',
					},
				];

				return assignmentsTypes;
			};

		const TimerNotificationRecipientsEditorForm = A.Component.create(
			NotificationRecipientsEditorFormConfig
		);

		let NotificationsEditorForm; // eslint-disable-line prefer-const

		const NotificationsEditorFormConfig = {
			ATTRS: {
				notificationTypes: {
					valueFn() {
						const instance = this;

						const strings = instance.getStrings();

						return [
							{
								label: strings.email,
								value: 'email',
							},
							{
								label: strings.userNotification,
								value: 'user-notification',
							},
						];
					},
				},

				recipients: {
					getter: '_getRecipients',
					value: [],
				},

				templateLanguages: {
					valueFn() {
						const instance = this;

						const strings = instance.getStrings();

						return [
							{
								label: strings.freemarker,
								value: 'freemarker',
							},
							{
								label: strings.text,
								value: 'text',
							},
						];
					},
				},

				viewTemplate: {
					value: [
						'<div class="{$ans}celleditor-notifications-view {$ans}celleditor-view {$ans}celleditor-view-type-{viewId}">',
						'{content}',
						'<div class="recipients-editor-container"></div>',
						'</div>',
					],
				},
			},

			AUGMENTS: [CompositeEditorFormBase, ExecutionTypesEditorFormBase],

			EXTENDS: BaseAbstractEditorForm,

			NAME: 'notifications-editor-form',

			prototype: {
				_appendRecipientsEditorToLastSection() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicViews = bodyNode.all(
						SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + 'notification'
					);

					const lastDynamicView = dynamicViews.item(
						dynamicViews.size() - 1
					);

					instance._showRecipientsEditor(lastDynamicView);
				},

				_countNotificationViews(val) {
					let count = 0;

					if (val) {
						count = val.notificationTypes
							? val.notificationTypes.filter(isValue).length
							: 1;
					}

					return count;
				},

				_getRecipients(val) {
					const instance = this;

					return instance.get('value.recipients') || val;
				},

				_renderRecipientsEditor() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicViews = bodyNode.all(
						SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + 'notification'
					);

					dynamicViews.each(
						A.bind(instance._showRecipientsEditor, instance)
					);
				},

				_showRecipientsEditor(bodyContentNode, index) {
					const instance = this;

					const executionTypeSelect = bodyContentNode.one(
						'.execution-type-select'
					);

					const editorContainer = bodyContentNode.one(
						'.recipients-editor-container'
					);

					const recipients = instance.get('recipients');

					const value = recipients[index];

					// eslint-disable-next-line @liferay/aui/no-object
					if (value && A.Object.isEmpty(value)) {
						value.assignmentType = 'taskAssignees';
						value.receptionType = ['to'];
						value.taskAssignees = [''];
					}

					instance.showEditorForm(
						NotificationRecipientsEditorForm,
						editorContainer,
						value,
						{
							executionTypeSelect,
						}
					);
				},

				addDynamicViews(val) {
					const instance = this;

					instance.removeAllViews('notification');

					instance.addNotificationView(
						instance._countNotificationViews(val)
					);
				},

				addNotificationView(num) {
					const instance = this;

					num = num || 1;

					const strings = instance.getStrings();

					const notificationsViewTpl = instance.get('viewTemplate');

					const inputTpl = Template.get('input');
					const selectMultipleTpl = Template.get('select-multiple');
					const selectTpl = Template.get('select');
					const textareaTpl = Template.get('textarea');

					const buffer = [];

					for (let i = 0; i < num; i++) {
						const notificationContent = [
							inputTpl.parse({
								auiCssClass:
									'form-control input-sm notifications-cell-editor-input',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.name,
								name: 'name',
								size: 35,
								type: 'text',
							}),

							textareaTpl.parse({
								auiCssClass:
									'celleditor-textarea-small form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.description,
								name: 'description',
							}),

							selectTpl.parse({
								auiCssClass: 'form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.templateLanguage,
								name: 'templateLanguage',
								options: instance.get('templateLanguages'),
							}),

							textareaTpl.parse({
								auiCssClass:
									'celleditor-textarea-small form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.template,
								name: 'template',
							}),

							selectMultipleTpl.parse({
								auiCssClass: 'form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.notificationType,
								multiple: true,
								name: 'notificationTypes',
								options: instance.get('notificationTypes'),
							}),

							selectTpl.parse({
								auiCssClass:
									'form-control input-sm execution-type-select',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.executionType,
								name: 'executionType',
								options: instance.get('executionTypes'),
							}),
						].join(STR_BLANK);

						buffer.push(
							notificationsViewTpl.parse({
								content: notificationContent,
								viewId: 'notification',
							})
						);
					}

					instance.appendToDynamicView(buffer.join(STR_BLANK));
				},

				getValue() {
					const instance = this;

					const localRecipients = instance.get('recipients');

					const recipients = [];

					instance.getDynamicViews().each((item, index) => {
						const editorContainer = item.one(
							'.recipients-editor-container'
						);

						const recipientsEditor = instance.getEmbeddedEditorForm(
							NotificationRecipientsEditorForm,
							editorContainer
						);

						if (recipientsEditor) {
							recipients.push(recipientsEditor.getValue());
						}

						localRecipients[index] = recipientsEditor.getValue();
					});

					instance.set('recipients', localRecipients);

					// eslint-disable-next-line @liferay/aui/no-merge
					return A.merge(
						NotificationsEditorForm.superclass.getValue.apply(
							this,
							arguments
						),
						{
							recipients,
						}
					);
				},

				handleAddViewSection(event) {
					const instance = this;

					const button = event.target;

					if (!button.get('disabled')) {
						instance.addNotificationView();
					}

					instance._appendRecipientsEditorToLastSection();
				},

				syncElementsFocus() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					bodyNode.one(':input').focus();
				},

				syncToolbarUI() {
					const instance = this;

					const addSectionButton = instance.get('addSectionButton');

					if (addSectionButton) {
						addSectionButton.set('disabled', false);
					}
				},

				syncViewsUI() {
					const instance = this;

					NotificationsEditorForm.superclass.syncViewsUI.apply(
						this,
						arguments
					);

					instance._renderRecipientsEditor();
				},
			},
		};

		NotificationsEditorForm = A.Component.create(
			NotificationsEditorFormConfig
		);

		const NotificationsEditor = A.Component.create({
			ATTRS: {
				cssClass: {
					value: 'tall-editor',
				},

				editorFormClass: {
					value: NotificationsEditorForm,
				},
			},

			AUGMENTS: [A.WidgetCssClass],

			EXTENDS: BaseAbstractEditor,

			NAME: 'notifications-cell-editor',
		});

		NotificationsEditorFormConfig.prototype.addNotificationView = function (
			num
		) {
			const instance = this;

			num = num || 1;

			const strings = instance.getStrings();

			const notificationsViewTpl = instance.get('viewTemplate');

			const inputTpl = Template.get('input');
			const selectMultipleTpl = Template.get('select-multiple');
			const selectTpl = Template.get('select');
			const textareaTpl = Template.get('textarea');

			const buffer = [];

			for (let i = 0; i < num; i++) {
				const notificationContent = [
					inputTpl.parse({
						auiCssClass:
							'form-control input-sm notifications-cell-editor-input',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.name,
						name: 'name',
						size: 35,
						type: 'text',
					}),

					textareaTpl.parse({
						auiCssClass:
							'celleditor-textarea-small form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.description,
						name: 'description',
					}),

					selectTpl.parse({
						auiCssClass: 'form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.templateLanguage,
						name: 'templateLanguage',
						options: instance.get('templateLanguages'),
					}),

					textareaTpl.parse({
						auiCssClass:
							'celleditor-textarea-small form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.template,
						name: 'template',
					}),

					selectMultipleTpl.parse({
						auiCssClass: 'form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.notificationType,
						multiple: true,
						name: 'notificationTypes',
						options: instance.get('notificationTypes'),
					}),
				].join(STR_BLANK);

				buffer.push(
					notificationsViewTpl.parse({
						content: notificationContent,
						viewId: 'notification',
					})
				);
			}

			instance.appendToDynamicView(buffer.join(STR_BLANK));
		};

		NotificationsEditorFormConfig.prototype.getValue = function () {
			const instance = this;

			const localRecipients = instance.get('recipients');

			const recipients = [];

			instance.getDynamicViews().each((item, index) => {
				const editorContainer = item.one(
					'.recipients-editor-container'
				);

				const recipientsEditor = instance.getEmbeddedEditorForm(
					TimerNotificationRecipientsEditorForm,
					editorContainer
				);

				if (recipientsEditor) {
					recipients.push(recipientsEditor.getValue());
				}

				localRecipients[index] = recipientsEditor.getValue();
			});

			instance.set('recipients', localRecipients);

			// eslint-disable-next-line @liferay/aui/no-merge
			return A.merge(
				NotificationsEditorForm.superclass.getValue.apply(
					this,
					arguments
				),
				{
					recipients,
				}
			);
		};

		NotificationsEditorFormConfig.prototype._showRecipientsEditor =
			function (bodyContentNode, index) {
				const instance = this;

				const executionTypeSelect = bodyContentNode.one(
					'.execution-type-select'
				);

				const editorContainer = bodyContentNode.one(
					'.recipients-editor-container'
				);

				const recipients = instance.get('recipients');

				const value = recipients[index];

				instance.showEditorForm(
					TimerNotificationRecipientsEditorForm,
					editorContainer,
					value,
					{
						executionTypeSelect,
					}
				);
			};

		const TimerNotificationsEditorForm = A.Component.create(
			NotificationsEditorFormConfig
		);

		const ActionsEditorFormConfig = {
			ATTRS: {
				scriptLanguages: {
					valueFn() {
						const instance = this;

						const scriptLanguages = [];

						instance.getScriptLanguages(scriptLanguages);

						const scriptLanguagesJSONArray =
							instance.convertScriptLanguagesToJSONArray(
								scriptLanguages
							);

						return scriptLanguagesJSONArray;
					},
				},

				viewTemplate: {
					value: [
						'<div class="{$ans}celleditor-actions-view {$ans}celleditor-view {$ans}celleditor-view-type-{viewId}">',
						'{content}',
						'</div>',
					],
				},
			},

			AUGMENTS: [ExecutionTypesEditorFormBase],

			EXTENDS: BaseAbstractEditorForm,

			NAME: 'actions-editor-form',

			prototype: {
				_countActionViews(val) {
					let count = 0;

					if (val) {
						count = val.name ? val.name.filter(isValue).length : 1;
					}

					return count;
				},

				addActionView(num) {
					const instance = this;

					num = num || 1;

					const strings = instance.getStrings();

					const actionsViewTpl = instance.get('viewTemplate');

					const inputTpl = Template.get('input');
					const selectTpl = Template.get('select');
					const textareaTpl = Template.get('textarea');

					const buffer = [];

					for (let i = 0; i < num; i++) {
						const actionContent = [
							inputTpl.parse({
								auiCssClass:
									'actions-cell-editor-input form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.name,
								name: 'name',
								size: 35,
								type: 'text',
							}),

							textareaTpl.parse({
								auiCssClass:
									'celleditor-textarea-small form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.description,
								name: 'description',
							}),

							textareaTpl.parse({
								auiCssClass:
									'celleditor-textarea-small form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.script,
								name: 'script',
							}),

							selectTpl.parse({
								auiCssClass: 'form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.scriptLanguage,
								name: 'scriptLanguage',
								options: instance.get('scriptLanguages'),
							}),

							selectTpl.parse({
								auiCssClass: 'form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.executionType,
								name: 'executionType',
								options: instance.get('executionTypes'),
							}),

							inputTpl.parse({
								auiCssClass:
									'actions-cell-editor-input form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.priority,
								name: 'priority',
								size: 35,
								type: 'text',
							}),
						].join(STR_BLANK);

						buffer.push(
							actionsViewTpl.parse({
								content: actionContent,
								viewId: 'action',
							})
						);
					}

					instance.appendToDynamicView(buffer.join(STR_BLANK));
				},

				addDynamicViews(val) {
					const instance = this;

					instance.removeAllViews('action');

					instance.addActionView(instance._countActionViews(val));
				},

				handleAddViewSection(event) {
					const instance = this;

					const button = event.target;

					if (!button.get('disabled')) {
						instance.addActionView();
					}
				},

				syncElementsFocus() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					bodyNode.one(':input').focus();
				},

				syncToolbarUI() {
					const instance = this;

					const addSectionButton = instance.get('addSectionButton');

					if (addSectionButton) {
						addSectionButton.set('disabled', false);
					}
				},
			},
		};

		const ActionsEditorForm = A.Component.create(ActionsEditorFormConfig);

		const ActionsEditor = A.Component.create({
			ATTRS: {
				cssClass: {
					value: 'tall-editor',
				},

				editorFormClass: {
					value: ActionsEditorForm,
				},
			},

			AUGMENTS: [A.WidgetCssClass],

			EXTENDS: BaseAbstractEditor,

			NAME: 'actions-cell-editor',
		});

		ActionsEditorFormConfig.prototype.addActionView = function (num) {
			const instance = this;

			num = num || 1;

			const strings = instance.getStrings();

			const actionsViewTpl = instance.get('viewTemplate');

			const inputTpl = Template.get('input');
			const selectTpl = Template.get('select');
			const textareaTpl = Template.get('textarea');

			const buffer = [];

			for (let i = 0; i < num; i++) {
				const actionContent = [
					inputTpl.parse({
						auiCssClass:
							'actions-cell-editor-input form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.name,
						name: 'name',
						size: 35,
						type: 'text',
					}),

					textareaTpl.parse({
						auiCssClass:
							'celleditor-textarea-small form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.description,
						name: 'description',
					}),

					textareaTpl.parse({
						auiCssClass:
							'celleditor-textarea-small form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.script,
						name: 'script',
					}),

					selectTpl.parse({
						auiCssClass: 'form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.scriptLanguage,
						name: 'scriptLanguage',
						options: instance.get('scriptLanguages'),
					}),

					inputTpl.parse({
						auiCssClass:
							'actions-cell-editor-input form-control input-sm',
						auiLabelCssClass: 'celleditor-label',
						id: A.guid(),
						label: strings.priority,
						name: 'priority',
						size: 35,
						type: 'text',
					}),
				].join(STR_BLANK);

				buffer.push(
					actionsViewTpl.parse({
						content: actionContent,
						viewId: 'action',
					})
				);
			}

			instance.appendToDynamicView(buffer.join(STR_BLANK));
		};

		const TimerActionsEditorForm = A.Component.create(
			ActionsEditorFormConfig
		);

		const TaskTimerActionsEditorForm = A.Component.create({
			ATTRS: {
				actionTypes: {
					valueFn() {
						const instance = this;

						const strings = instance.getStrings();

						return [
							{
								label: strings.action,
								value: 'action',
							},

							{
								label: strings.notification,
								value: 'notification',
							},

							{
								label: strings.reassignment,
								value: 'reassignment',
							},
						];
					},
				},

				editorFormClasses: {
					value: {
						action: TimerActionsEditorForm,
						notification: TimerNotificationsEditorForm,
						reassignment: AssignmentsEditorForm,
					},
				},

				viewTemplate: {
					value: [
						'<div class="{$ans}celleditor-task-timer-actions-view {$ans}celleditor-view {$ans}celleditor-view-type-{viewId}">',
						'{content}',
						'<div class="editor-container editor-container-action"></div>',
						'<div class="editor-container editor-container-notification"></div>',
						'<div class="editor-container editor-container-reassignment"></div>',
						'</div>',
					],
				},
			},

			AUGMENTS: [CompositeEditorFormBase],

			EXTENDS: BaseAbstractEditorForm,

			NAME: 'task-timer-actions-editor-form',

			prototype: {
				_appendEditorToLastSection() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicViews = bodyNode.all(
						SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + 'timerAction'
					);

					const lastDynamicView = dynamicViews.item(
						dynamicViews.size() - 1
					);

					instance._showEditor(lastDynamicView);
				},

				_countTimerActionViews(val) {
					let count = 0;

					if (val) {
						count = val.actionType
							? val.actionType.filter(isValue).length
							: 1;
					}

					return count;
				},

				_displayEditor(dynamicViewNode) {
					const actionTypeSelect = dynamicViewNode.one(
						'.select-action-type'
					);

					const actionType = actionTypeSelect.val();

					dynamicViewNode.all('.editor-container').hide();

					dynamicViewNode
						.all('.editor-container-' + actionType)
						.show();
				},

				_displayEditors() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicViews = bodyNode.all(
						SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + 'timerAction'
					);

					dynamicViews.each(
						A.bind(instance._displayEditor, instance)
					);
				},

				_onActionTypeValueChange(event) {
					const instance = this;

					const actionTypeSelect = event.currentTarget;

					const dynamicViewNode = actionTypeSelect.ancestor(
						'.celleditor-task-timer-actions-view'
					);

					instance._showEditor(dynamicViewNode);

					instance._displayEditor(dynamicViewNode);
				},

				_renderEditor() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicViews = bodyNode.all(
						SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + 'timerAction'
					);

					dynamicViews.each(A.bind(instance._showEditor, instance));
				},

				_showEditor(bodyContentNode, index) {
					const instance = this;

					let actionType;

					let timerAction;

					const value = instance.get('value');

					if (value && value.actionType && value.actionType[index]) {
						actionType = value.actionType[index];
						timerAction = value.timerAction[index];
					}
					else {
						const actionTypeSelect = bodyContentNode.one(
							'.select-action-type'
						);

						actionType = actionTypeSelect.val();
					}

					const editorFormClass = instance.get(
						'editorFormClasses.' + actionType
					);

					const editorContainer = bodyContentNode.one(
						'.editor-container-' + actionType
					);

					instance.showEditorForm(
						editorFormClass,
						editorContainer,
						timerAction
					);
				},

				addDynamicViews(val) {
					const instance = this;

					instance.removeAllViews('timerAction');

					instance.addTimerActionView(
						instance._countTimerActionViews(val)
					);
				},

				addTimerActionView(num) {
					const instance = this;

					num = num || 1;

					const strings = instance.getStrings();

					const timerActionViewTpl = instance.get('viewTemplate');

					const selectTpl = Template.get('select');

					const buffer = [];

					for (let i = 0; i < num; i++) {
						const timerActionContent = [
							selectTpl.parse({
								auiCssClass:
									'form-control input-sm select-action-type',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.type,
								name: 'actionType',
								options: instance.get('actionTypes'),
							}),
						].join(STR_BLANK);

						buffer.push(
							timerActionViewTpl.parse({
								content: timerActionContent,
								viewId: 'timerAction',
							})
						);
					}

					instance.appendToDynamicView(buffer.join(STR_BLANK));
				},

				getValue() {
					const instance = this;

					const value = {
						actionType: [],
						timerAction: [],
					};

					const dynamicViews = instance.getDynamicViews();

					dynamicViews.each((item) => {
						const actionTypeSelect = item.one(
							'.select-action-type'
						);

						const actionType = actionTypeSelect.val();

						value.actionType.push(actionType);

						const editorContainer = item.one(
							'.editor-container-' + actionType
						);

						const editorFormClass = instance.get(
							'editorFormClasses.' + actionType
						);

						const editor = instance.getEmbeddedEditorForm(
							editorFormClass,
							editorContainer
						);

						value.timerAction.push(editor.getValue());
					});

					return value;
				},

				handleAddViewSection(event) {
					const instance = this;

					const button = event.target;

					if (!button.get('disabled')) {
						instance.addTimerActionView();
					}

					instance._appendEditorToLastSection();

					instance._displayEditors();
				},

				initializer() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					bodyNode.delegate(
						['change', 'keyup'],
						A.bind(instance._onActionTypeValueChange, instance),
						'.select-action-type'
					);
				},

				syncToolbarUI() {
					const instance = this;

					const addSectionButton = instance.get('addSectionButton');

					if (addSectionButton) {
						addSectionButton.set('disabled', false);
					}
				},

				syncViewsUI() {
					const instance = this;

					TaskTimerActionsEditorForm.superclass.syncViewsUI.apply(
						this,
						arguments
					);

					instance._renderEditor();

					instance._displayEditors();
				},
			},
		});

		const TaskTimerDelaysEditorForm = A.Component.create({
			ATTRS: {
				scales: {
					valueFn() {
						const instance = this;

						const strings = instance.getStrings();

						return [
							{
								label: strings.second,
								value: 'second',
							},

							{
								label: strings.minute,
								value: 'minute',
							},

							{
								label: strings.hour,
								value: 'hour',
							},

							{
								label: strings.day,
								value: 'day',
							},

							{
								label: strings.week,
								value: 'week',
							},

							{
								label: strings.month,
								value: 'month',
							},

							{
								label: strings.year,
								value: 'year',
							},
						];
					},
				},

				viewTemplate: {
					value: [
						'<div class="{$ans}celleditor-task-timer-delays-view {$ans}celleditor-view {$ans}celleditor-view-type-{viewId}">',
						'{content}',
						'</div>',
					],
				},
			},

			EXTENDS: BaseAbstractEditorForm,

			NAME: 'task-timer-delays-editor-form',

			prototype: {
				addStaticViews() {
					const instance = this;

					const delayContent = instance.getDelayContent();

					instance.appendToStaticView(delayContent);

					const recurrenceContent = instance.getRecurrenceContent();

					instance.appendToStaticView(recurrenceContent);
				},

				getDelayContent() {
					const instance = this;

					const strings = instance.getStrings();

					const inputTpl = Template.get('input');
					const selectTpl = Template.get('select');

					return [
						inputTpl.parse({
							auiCssClass: 'form-control input-sm',
							auiLabelCssClass: 'celleditor-label',
							id: A.guid(),
							label: strings.duration,
							name: 'duration',
						}),

						selectTpl.parse({
							auiCssClass: 'form-control input-sm',
							auiLabelCssClass: 'celleditor-label',
							id: A.guid(),
							label: strings.scale,
							name: 'scale',
							options: instance.get('scales'),
						}),
					].join(STR_BLANK);
				},

				getRecurrenceContent() {
					const instance = this;

					const timersViewTpl = instance.get('viewTemplate');

					const delayContent = instance.getDelayContent();

					return [
						timersViewTpl.parse({
							content: delayContent,
							viewId: 'recurrence',
						}),
					].join(STR_BLANK);
				},
			},
		});

		const TaskTimersEditorForm = A.Component.create({
			ATTRS: {
				delays: {
					getter: '_getDelays',
					value: [],
				},

				timerActions: {
					getter: '_getTimerActions',
					value: [],
				},

				viewTemplate: {
					value: [
						'<div class="{$ans}celleditor-task-timers-view {$ans}celleditor-view {$ans}celleditor-view-type-{viewId}">',
						'{content}',
						'</div>',
					],
				},
			},

			AUGMENTS: [CompositeEditorFormBase],

			EXTENDS: BaseAbstractEditorForm,

			NAME: 'task-timers-editor-form',

			prototype: {
				_appendDelaysEditorToLastSection() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicViews = bodyNode.all(
						SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + 'timer'
					);

					const lastDynamicView = dynamicViews.item(
						dynamicViews.size() - 1
					);

					instance._showDelaysEditor(lastDynamicView);
				},

				_appendTimerActionsEditorToLastSection() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicViews = bodyNode.all(
						SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + 'timer'
					);

					const lastDynamicView = dynamicViews.item(
						dynamicViews.size() - 1
					);

					instance._showTimerActionsEditor(lastDynamicView);
				},

				_countTimerViews(val) {
					let count = 0;

					if (val) {
						count = val.name ? val.name.filter(isValue).length : 1;
					}

					return count;
				},

				_getDelays(val) {
					const instance = this;

					return instance.get('value.delay') || val;
				},

				_getTimerActions() {
					const instance = this;

					const actions = instance.get('value.timerActions') || [];

					const notifications =
						instance.get('value.timerNotifications') || [];

					const reassignments =
						instance.get('value.reassignments') || [];

					const count = Math.max(
						actions.length,
						notifications.length,
						reassignments.length
					);

					const timerActions = [];

					for (let i = 0; i < count; i++) {
						let actionType = [];

						let splitTimerActions;

						let timerAction = [];

						if (reassignments[i]) {
							splitTimerActions = instance._splitTimerActions(
								reassignments[i]
							);

							actionType = actionType.concat(
								instance._repeat(
									'reassignment',
									splitTimerActions.length
								)
							);

							timerAction = timerAction.concat(splitTimerActions);
						}

						if (
							notifications[i] &&
							notifications[i].notificationTypes
						) {
							splitTimerActions = instance._splitTimerActions(
								notifications[i]
							);

							actionType = actionType.concat(
								instance._repeat(
									'notification',
									splitTimerActions.length
								)
							);

							timerAction = timerAction.concat(splitTimerActions);
						}

						if (actions[i] && actions[i].name) {
							splitTimerActions = instance._splitTimerActions(
								actions[i]
							);

							actionType = actionType.concat(
								instance._repeat(
									'action',
									splitTimerActions.length
								)
							);

							timerAction = timerAction.concat(splitTimerActions);
						}

						timerActions.push({
							actionType,
							timerAction,
						});
					}

					return timerActions;
				},

				_put(object, key, value) {
					object[key] = object[key] || [];

					object[key].push(value);
				},

				_renderDelaysEditor() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicViews = bodyNode.all(
						SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + 'timer'
					);

					dynamicViews.each(
						A.bind(instance._showDelaysEditor, instance)
					);
				},

				_renderTimerActionsEditor() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					const dynamicViews = bodyNode.all(
						SELECTOR_PREFIX_CELLEDITOR_VIEW_TYPE + 'timer'
					);

					dynamicViews.each(
						A.bind(instance._showTimerActionsEditor, instance)
					);
				},

				_repeat(value, times) {
					const array = [];

					for (let i = 0; i < times; i++) {
						array.push(value);
					}

					return array;
				},

				_showDelaysEditor(bodyContentNode, index) {
					const instance = this;

					const editorContainer = bodyContentNode.one(
						'.delays-editor-container'
					);

					const delays = instance.get('delays');

					const value = delays[index];

					instance.showEditorForm(
						TaskTimerDelaysEditorForm,
						editorContainer,
						value
					);
				},

				_showTimerActionsEditor(bodyContentNode, index) {
					const instance = this;

					const editorContainer = bodyContentNode.one(
						'.timer-actions-editor-container'
					);

					const timerActions = instance.get('timerActions');

					const value = timerActions[index];

					instance.showEditorForm(
						TaskTimerActionsEditorForm,
						editorContainer,
						value
					);
				},

				_splitTimerActions(timerActions) {
					const splitTimerActions = [];

					A.each(timerActions, (item1, index1) => {
						item1.forEach((item2, index2) => {
							if (!splitTimerActions[index2]) {
								splitTimerActions[index2] = {};
							}

							const timerAction = splitTimerActions[index2];

							if (!timerAction[index1]) {
								timerAction[index1] = [];
							}

							timerAction[index1][0] = item2;
						});
					});

					return splitTimerActions;
				},

				addDynamicViews(val) {
					const instance = this;

					instance.removeAllViews('timer');

					instance.addTaskTimerView(instance._countTimerViews(val));
				},

				addTaskTimerView(num) {
					const instance = this;

					num = num || 1;

					const strings = instance.getStrings();

					const timersViewTpl = instance.get('viewTemplate');

					const checkboxTpl = Template.get('checkbox');
					const inputTpl = Template.get('input');
					const textareaTpl = Template.get('textarea');

					const buffer = [];

					for (let i = 0; i < num; i++) {
						const taskTimerContent = [
							inputTpl.parse({
								auiCssClass:
									'task-timers-cell-editor-input form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.name,
								name: 'name',
								size: 35,
								type: 'text',
							}),

							textareaTpl.parse({
								auiCssClass:
									'task-timers-cell-editor-input celleditor-textarea-small form-control input-sm',
								auiLabelCssClass: 'celleditor-label',
								id: A.guid(),
								label: strings.description,
								name: 'description',
							}),

							'<div class="delays-editor-container"></div>',

							checkboxTpl.parse({
								auiCssClass: 'task-timers-cell-editor-input',
								auiLabelCssClass: 'celleditor-label-checkbox',
								checked: false,
								id: A.guid(),
								label: strings.blocking,
								name: 'blocking',
								type: 'checkbox',
							}),

							'<div class="timer-actions-editor-container"></div>',
						].join(STR_BLANK);

						buffer.push(
							timersViewTpl.parse({
								content: taskTimerContent,
								viewId: 'timer',
							})
						);
					}

					instance.appendToDynamicView(buffer.join(STR_BLANK));
				},

				getValue() {
					const instance = this;

					const value = {
						blocking: [],
						delay: [],
						description: [],
						name: [],
						reassignments: [],
						timerActions: [],
						timerNotifications: [],
					};

					const bodyNode = instance.get('bodyNode');

					const taskTimerInputs = bodyNode.all(
						'.task-timers-cell-editor-input'
					);

					taskTimerInputs.each((item) => {
						if (
							item.get('type') &&
							item.get('type') === 'checkbox'
						) {
							value[item.get('name')].push(item.get('checked'));
						}
						else {
							value[item.get('name')].push(item.val());
						}
					});

					const dynamicViews = instance.getDynamicViews();

					dynamicViews.each((item1, index1) => {
						const delaysEditorContainer = item1.one(
							'.delays-editor-container'
						);

						const delaysEditorForm = instance.getEmbeddedEditorForm(
							TaskTimerDelaysEditorForm,
							delaysEditorContainer
						);

						value.delay.push(delaysEditorForm.getValue());

						const timerActionsEditorContainer = item1.one(
							'.timer-actions-editor-container'
						);

						const timerActionsEditorForm =
							instance.getEmbeddedEditorForm(
								TaskTimerActionsEditorForm,
								timerActionsEditorContainer
							);

						value.timerActions.push({});

						value.timerNotifications.push({});

						value.reassignments.push({});

						const timerActionValue =
							timerActionsEditorForm.getValue();

						timerActionValue.actionType.forEach(
							(actionType, index2) => {
								const timerAction =
									timerActionValue.timerAction[index2];

								let object;

								if (actionType === 'action') {
									object = value.timerActions[index1];
								}
								else if (actionType === 'notification') {
									object = value.timerNotifications[index1];
								}
								else if (actionType === 'reassignment') {
									object = value.reassignments[index1];
								}

								A.each(timerAction, (value, key) => {
									instance._put(object, key, value[0]);
								});
							}
						);
					});

					return value;
				},

				handleAddViewSection(event) {
					const instance = this;

					const button = event.target;

					if (!button.get('disabled')) {
						instance.addTaskTimerView();
					}

					instance._appendDelaysEditorToLastSection();

					instance._appendTimerActionsEditorToLastSection();
				},

				syncElementsFocus() {
					const instance = this;

					const bodyNode = instance.get('bodyNode');

					bodyNode.one(':input').focus();
				},

				syncToolbarUI() {
					const instance = this;

					const addSectionButton = instance.get('addSectionButton');

					if (addSectionButton) {
						addSectionButton.set('disabled', false);
					}
				},

				syncViewsUI() {
					const instance = this;

					TaskTimersEditorForm.superclass.syncViewsUI.apply(
						this,
						arguments
					);

					instance._renderDelaysEditor();

					instance._renderTimerActionsEditor();
				},
			},
		});

		const TaskTimersEditor = A.Component.create({
			ATTRS: {
				cssClass: {
					value: 'tall-editor',
				},

				editorFormClass: {
					value: TaskTimersEditorForm,
				},
			},

			AUGMENTS: [A.WidgetCssClass],

			EXTENDS: BaseAbstractEditor,

			NAME: 'task-timers-cell-editor',
		});

		const ScriptEditor = A.Component.create({
			ATTRS: {
				inputFormatter: {
					value(val) {
						return val;
					},
				},
			},

			EXTENDS: A.BaseCellEditor,

			NAME: 'script-cell-editor',

			prototype: {
				_afterRender() {
					const instance = this;

					const editor = instance.editor;

					ScriptEditor.superclass._afterRender.apply(this, arguments);

					instance.setStdModContent(
						WidgetStdMod.BODY,
						STR_BLANK,
						WidgetStdMod.AFTER
					);

					setTimeout(() => {
						editor.render(instance.bodyNode);
					}, 0);
				},

				_syncElementsFocus: emptyFn,

				_uiSetValue(val) {
					const instance = this;

					const editor = instance.editor;

					if (editor && isValue(val)) {
						editor.set('value', val);
					}
				},

				getValue() {
					const instance = this;

					return instance.editor.get('value');
				},

				initializer() {
					const instance = this;

					instance.editor = new A.AceEditor({
						height: 300,
						width: 550,
					});
				},
			},
		});

		Liferay.KaleoDesignerEditors = {
			ActionsEditor,
			AssignmentsEditor,
			BaseAbstractEditor,
			CompositeEditorFormBase,
			ExecutionTypesEditorFormBase,
			FormsEditor,
			NotificationsEditor,
			ScriptEditor,
			TaskTimersEditor,
		};
	},
	'',
	{
		requires: [
			'aui-ace-editor',
			'aui-ace-editor-mode-xml',
			'aui-base',
			'aui-datatype',
			'aui-node',
			'liferay-kaleo-designer-autocomplete-util',
			'liferay-kaleo-designer-remote-services',
			'liferay-kaleo-designer-templates',
			'liferay-kaleo-designer-utils',
		],
	}
);
