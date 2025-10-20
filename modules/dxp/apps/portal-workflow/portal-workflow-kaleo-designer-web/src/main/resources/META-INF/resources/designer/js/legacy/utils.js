/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-kaleo-designer-utils',
	(A) => {
		const AArray = A.Array;
		const isArray = Array.isArray;

		const STR_CDATA_CLOSE = ']]>';

		const STR_CDATA_OPEN = '<![CDATA[';

		const STR_CHAR_CRLF = '\r\n';

		const STR_CHAR_TAB = '\t';

		const STR_ELLIPSIS = '...';

		const KaleoDesignerUtils = {};

		const KaleoDesignerStrings = {
			account: Liferay.Language.get('account'),
			action: Liferay.Language.get('action'),
			actions: Liferay.Language.get('actions'),
			actionsType: Liferay.Language.get('actions-type'),
			addAnchorMessage: Liferay.Language.get('add-anchor'),
			addSection: Liferay.Language.get('add-section'),
			approve: Liferay.Language.get('approve'),
			assetCreator: Liferay.Language.get('asset-creator'),
			assignmentType: Liferay.Language.get('assignment-type'),
			assignments: Liferay.Language.get('assignments'),
			autoCreate: Liferay.Language.get('auto-create'),
			beanshell: Liferay.Language.get('beanshell'),
			blocking: Liferay.Language.get('blocking'),
			cancel: Liferay.Language.get('cancel'),
			closeMessage: Liferay.Language.get('close'),
			day: Liferay.Language.get('day'),
			default: Liferay.Language.get('default'),
			definition: Liferay.Language.get('definition'),
			deleteNodesMessage: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-nodes'
			),
			depot: Liferay.Language.get('asset-library'),
			description: Liferay.Language.get('description'),
			duration: Liferay.Language.get('duration'),
			edit: Liferay.Language.get('edit'),
			editMessage: Liferay.Language.get('edit'),
			email: Liferay.Language.get('email'),
			emailAddress: Liferay.Language.get('email-address'),
			executionType: Liferay.Language.get('execution-type'),
			formTemplate: Liferay.Language.get('form-template'),
			forms: Liferay.Language.get('forms'),
			freemarker: Liferay.Language.get('freemarker'),
			groovy: Liferay.Language.get('groovy'),
			hour: Liferay.Language.get('hour'),
			initial: Liferay.Language.get('initial'),
			inspectTaskMessage: Liferay.Language.get(
				'inspect-the-task-nodes-to-assign-a-form-template'
			),
			java: Liferay.Language.get('java'),
			javascript: Liferay.Language.get('javascript'),
			language: Liferay.Language.get('language'),
			minute: Liferay.Language.get('minute'),
			month: Liferay.Language.get('month'),
			name: Liferay.Language.get('name'),
			notification: Liferay.Language.get('notification'),
			notificationRecipients: Liferay.Language.get(
				'notification-recipients'
			),
			notificationType: Liferay.Language.get('notification-type'),
			notifications: Liferay.Language.get('notifications'),
			onAssignment: Liferay.Language.get('on-assignment'),
			onEntry: Liferay.Language.get('on-entry'),
			onExit: Liferay.Language.get('on-exit'),
			organization: Liferay.Language.get('organization'),
			priority: Liferay.Language.get('priority'),
			python: Liferay.Language.get('python'),
			reassignment: Liferay.Language.get('reassignment'),
			recipientType: Liferay.Language.get('recipient-type'),
			regular: Liferay.Language.get('regular'),
			remove: Liferay.Language.get('remove'),
			resourceActions: Liferay.Language.get('resource-actions'),
			role: Liferay.Language.get('role'),
			roleId: Liferay.Language.get('role-id'),
			roleName: Liferay.Language.get('role-name'),
			roleType: Liferay.Language.get('role-type'),
			ruby: Liferay.Language.get('ruby'),
			save: Liferay.Language.get('save'),
			scale: Liferay.Language.get('scale'),
			screenName: Liferay.Language.get('screen-name'),
			script: Liferay.Language.get('script'),
			scriptLanguage: Liferay.Language.get('script-language'),
			scriptedAssignment: Liferay.Language.get('scripted-assignment'),
			scriptedRecipient: Liferay.Language.get('scripted-recipient'),
			search: Liferay.Language.get('search'),
			second: Liferay.Language.get('second'),
			site: Liferay.Language.get('site'),
			taskAssignees: Liferay.Language.get('task-assignees'),
			taskTimers: Liferay.Language.get('task-timers'),
			template: Liferay.Language.get('template'),
			templateLanguage: Liferay.Language.get('template-language'),
			text: Liferay.Language.get('text'),
			timers: Liferay.Language.get('timers'),
			type: Liferay.Language.get('type'),
			user: Liferay.Language.get('user'),
			userId: Liferay.Language.get('user-id'),
			userNotification: Liferay.Language.get('user-notification'),
			week: Liferay.Language.get('week'),
			year: Liferay.Language.get('year'),
		};

		const PropertyListFormatter = {
			assignmentsType(data) {
				const value = data.value;

				let assignmentType;

				if (value && value.assignmentType) {
					assignmentType = value.assignmentType[0];
				}

				return KaleoDesignerStrings[assignmentType || 'assetCreator'];
			},

			forms(data) {
				const value = data.value;

				let templateName;

				if (value) {
					templateName = value.templateName;
				}

				return AArray(templateName).join(', ');
			},

			names(data) {
				const value = data.value;

				let names;

				if (value) {
					names = value.name;
				}

				return AArray(names).join(', ');
			},

			script() {
				return STR_ELLIPSIS;
			},
		};

		KaleoDesignerUtils.PropertyListFormatter = PropertyListFormatter;

		const cdata = function (value) {
			value = value
				.replace(STR_CDATA_OPEN, '')
				.replace(STR_CDATA_CLOSE, '');

			return (
				STR_CHAR_CRLF +
				STR_CDATA_OPEN +
				value +
				STR_CDATA_CLOSE +
				STR_CHAR_CRLF
			);
		};

		KaleoDesignerUtils.cdata = cdata;

		const jsonParse = function (val) {
			let jsonObj = null;

			try {
				jsonObj = JSON.parse(val);
			}
			catch (error) {}

			return jsonObj;
		};

		KaleoDesignerUtils.jsonParse = jsonParse;

		const jsonStringify = function (val) {
			let jsonString = null;

			try {
				jsonString =
					STR_CHAR_CRLF +
					JSON.stringify(val, null, STR_CHAR_TAB) +
					STR_CHAR_CRLF;
			}
			catch (error) {}

			return jsonString;
		};

		KaleoDesignerUtils.jsonStringify = jsonStringify;

		const serializeForm = function (form) {
			const data = {};

			if (form) {
				form.all(':input:not(:button)').each((item) => {
					const checked = item.get('checked');
					const name = item.get('name');
					const type = item.get('type');

					let value = item.val();

					if (name) {
						if (!isArray(data[name])) {
							data[name] = [];
						}

						if (type === 'checkbox') {
							value = checked;
						}

						if (type === 'radio' && !checked) {
							value = null;
						}

						if (type === 'select-multiple') {
							value = [];

							item.all('option:selected').each((option) => {
								value.push({
									notificationType: option.val(),
								});
							});
						}

						data[name].push(value);
					}
				});
			}

			return data;
		};

		KaleoDesignerUtils.serializeForm = serializeForm;

		const uniformRandomInt = function (a, b) {
			return parseInt(a + Math.random() * (b - a), 10) || 0;
		};

		KaleoDesignerUtils.uniformRandomInt = uniformRandomInt;

		const previewBeforeRevert = function (_, renderUrl, actionUrl, title) {
			const dialog = Liferay.Util.Window.getWindow({
				dialog: {
					destroyOnHide: true,
					modal: true,
					toolbars: {
						footer: [
							{
								cssClass: 'btn btn-secondary',
								discardDefaultButtonCssClasses: true,
								label: Liferay.Language.get('cancel'),
								on: {
									click() {
										dialog.destroy();
									},
								},
							},
							{
								cssClass: 'btn btn-primary',
								discardDefaultButtonCssClasses: true,
								label: Liferay.Language.get('restore'),
								on: {
									click() {
										window.location.assign(actionUrl);
									},
								},
							},
						],
						header: [
							{
								cssClass: 'close',
								discardDefaultButtonCssClasses: true,
								labelHTML:
									'<svg class="lexicon-icon" focusable="false"><use href="' +
									Liferay.Icons.spritemap +
									'#times" /><title>' +
									Liferay.Language.get('close') +
									'</title></svg>',
								on: {
									click(event) {
										dialog.destroy();

										event.domEvent.stopPropagation();
									},
								},
							},
						],
					},
				},
				title,
				uri: renderUrl,
			});
		};

		KaleoDesignerUtils.previewBeforeRevert = previewBeforeRevert;

		Liferay.KaleoDesignerStrings = KaleoDesignerStrings;

		Liferay.KaleoDesignerUtils = KaleoDesignerUtils;
	},
	'',
	{}
);
