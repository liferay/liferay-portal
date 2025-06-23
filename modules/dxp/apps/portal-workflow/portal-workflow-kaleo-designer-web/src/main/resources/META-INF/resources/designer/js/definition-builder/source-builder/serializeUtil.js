/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isObject, isObjectEmpty} from '../util/utils';
import {
	DEFAULT_LANGUAGE,
	STR_CDATA_CLOSE,
	STR_CDATA_OPEN,
	STR_CHAR_CRLF,
	STR_CHAR_TAB,
} from './constants';
import XMLUtil from './xmlUtil';

function cdata(value) {
	value = value.replace(STR_CDATA_OPEN, '').replace(STR_CDATA_CLOSE, '');

	return (
		STR_CHAR_CRLF + STR_CDATA_OPEN + value + STR_CDATA_CLOSE + STR_CHAR_CRLF
	);
}

function isValidValue(array, index) {
	return array && array[index] !== undefined;
}

function jsonStringify(value) {
	let jsonString = null;

	try {
		jsonString =
			STR_CHAR_CRLF +
			JSON.stringify(value, null, STR_CHAR_TAB) +
			STR_CHAR_CRLF;
	}
	catch (error) {}

	return jsonString;
}

function createTagWithEscapedContent(tag, content) {
	const escapedContent = Liferay.Util.escape(content);

	return XMLUtil.create(tag, escapedContent);
}

function appendXMLActions(
	buffer,
	actions,
	notifications,
	assignments,
	wrapperNodeName,
	actionNodeName,
	notificationNodeName,
	assignmentNodeName
) {
	const hasAction = isObject(actions) && !isObjectEmpty(actions);
	const hasAssignment = isObject(assignments) && !isObjectEmpty(assignments);
	const hasNotification =
		isObject(notifications) && !isObjectEmpty(notifications);
	const xmlActions = XMLUtil.createObj(wrapperNodeName || 'actions');

	if (hasAction || hasNotification || hasAssignment) {
		buffer.push(xmlActions.open);
	}

	if (hasAction) {
		const {
			description,
			executionType,
			priority,
			script,
			scriptLanguage,
			status,
		} = actions;

		const xmlAction = XMLUtil.createObj(actionNodeName || 'action');

		actions.name.forEach((item, index) => {
			buffer.push(
				xmlAction.open,
				createTagWithEscapedContent('name', item)
			);

			if (isValidValue(description, index)) {
				buffer.push(
					createTagWithEscapedContent(
						'description',
						description[index]
					)
				);
			}

			if (isValidValue(status, index) && status[index]) {
				buffer.push(
					createTagWithEscapedContent('status', status[index])
				);
			}
			else {
				if (isValidValue(script, index)) {
					buffer.push(XMLUtil.create('script', cdata(script[index])));
				}

				buffer.push(
					createTagWithEscapedContent(
						'scriptLanguage',
						scriptLanguage[index] || DEFAULT_LANGUAGE
					)
				);
			}

			if (isValidValue(priority, index)) {
				buffer.push(
					createTagWithEscapedContent('priority', priority[index])
				);
			}

			if (isValidValue(executionType, index)) {
				buffer.push(
					createTagWithEscapedContent(
						'executionType',
						executionType[index]
					)
				);
			}

			buffer.push(xmlAction.close);
		});
	}

	if (hasNotification) {
		appendXMLNotifications(buffer, notifications, notificationNodeName);
	}

	if (hasAssignment) {
		appendXMLAssignments(buffer, assignments, assignmentNodeName);
	}

	if (hasAction || hasNotification || hasAssignment) {
		buffer.push(xmlActions.close);
	}
}

function appendXMLAssignments(
	buffer,
	dataAssignments,
	wrapperNodeName,
	wrapperNodeAttrs
) {
	if (dataAssignments) {
		if (
			!dataAssignments.assignmentType &&
			dataAssignments[0].assignmentType
		) {
			dataAssignments = dataAssignments[0];
		}

		const assignmentType = Array.from(dataAssignments.assignmentType)[0];

		const xmlAssignments = XMLUtil.createObj(
			wrapperNodeName || 'assignments',
			wrapperNodeAttrs
		);

		buffer.push(xmlAssignments.open);

		if (dataAssignments.address) {
			dataAssignments.address.forEach((item) => {
				if (item !== '') {
					buffer.push(createTagWithEscapedContent('address', item));
				}
			});
		}

		const xmlRoles = XMLUtil.createObj('roles');

		if (assignmentType === 'resourceActions') {
			const xmlResourceAction = XMLUtil.createObj('resourceActions');

			const resourceAction = dataAssignments.resourceAction;

			buffer.push(
				xmlResourceAction.open,
				createTagWithEscapedContent('resourceAction', resourceAction),
				xmlResourceAction.close
			);
		}
		else if (assignmentType === 'roleId') {
			buffer.push(xmlRoles.open);

			const xmlRole = XMLUtil.createObj('role');

			const roleId = dataAssignments.roleId;

			buffer.push(
				xmlRole.open,
				createTagWithEscapedContent('roleId', roleId),
				xmlRole.close
			);

			buffer.push(xmlRoles.close);
		}
		else if (assignmentType === 'roleType') {
			buffer.push(xmlRoles.open);

			const xmlRole = XMLUtil.createObj('role');

			dataAssignments.roleType.forEach((item, index) => {
				const roleName = dataAssignments.roleName[index];
				const roleType = dataAssignments.roleType[index];

				if (roleName) {
					buffer.push(
						xmlRole.open,
						createTagWithEscapedContent('roleType', roleType),
						createTagWithEscapedContent('name', roleName)
					);

					let autoCreate = dataAssignments.autoCreate?.[index];

					if (autoCreate !== undefined && autoCreate !== null) {
						if (!autoCreate) {
							autoCreate = 'false';
						}

						buffer.push(
							createTagWithEscapedContent(
								'autoCreate',
								autoCreate
							)
						);
					}

					buffer.push(xmlRole.close);
				}
			});

			buffer.push(xmlRoles.close);
		}
		else if (
			assignmentType === 'scriptedAssignment' &&
			dataAssignments.script?.length
		) {
			const xmlScriptedAssignment =
				XMLUtil.createObj('scriptedAssignment');

			dataAssignments.script.forEach((item) => {
				buffer.push(
					xmlScriptedAssignment.open,
					XMLUtil.create('script', cdata(item)),
					createTagWithEscapedContent(
						'scriptLanguage',
						dataAssignments.scriptLanguage || DEFAULT_LANGUAGE
					),
					xmlScriptedAssignment.close
				);
			});
		}
		else if (assignmentType === 'scriptedRecipient') {
			const xmlScriptedRecipient = XMLUtil.createObj('scriptedRecipient');

			dataAssignments.script.forEach((item) => {
				buffer.push(
					xmlScriptedRecipient.open,
					XMLUtil.create('script', cdata(item)),
					createTagWithEscapedContent(
						'scriptLanguage',
						dataAssignments.scriptLanguage || DEFAULT_LANGUAGE
					),
					xmlScriptedRecipient.close
				);
			});
		}
		else if (assignmentType === 'user') {
			if (
				Array.isArray(dataAssignments.emailAddress) &&
				dataAssignments.emailAddress.filter(
					(emailAddress) => emailAddress !== ''
				).length !== 0
			) {
				const xmlUser = XMLUtil.createObj('user');

				dataAssignments.emailAddress.forEach((item) => {
					buffer.push(xmlUser.open);

					if (item !== '') {
						buffer.push(
							createTagWithEscapedContent('emailAddress', item)
						);
					}

					buffer.push(xmlUser.close);
				});
			}
			else if (
				Array.isArray(dataAssignments.screenName) &&
				dataAssignments.screenName.filter(
					(screenName) => screenName !== ''
				).length !== 0
			) {
				const xmlUser = XMLUtil.createObj('user');

				dataAssignments.screenName.forEach((item) => {
					buffer.push(xmlUser.open);

					if (item !== '') {
						buffer.push(
							createTagWithEscapedContent('screenName', item)
						);
					}

					buffer.push(xmlUser.close);
				});
			}
			else if (
				Array.isArray(dataAssignments.userId) &&
				dataAssignments.userId.filter((userId) => userId !== '')
					.length !== 0
			) {
				const xmlUser = XMLUtil.createObj('user');

				dataAssignments.userId.forEach((item) => {
					buffer.push(xmlUser.open);

					if (item !== '') {
						buffer.push(
							createTagWithEscapedContent('userId', item)
						);
					}

					buffer.push(xmlUser.close);
				});
			}
			else {
				buffer.push('<user />');
			}
		}
		else if (assignmentType === 'taskAssignees') {
			buffer.push('<assignees />');
		}
		else if (
			!dataAssignments.address ||
			!dataAssignments.address.filter((address) => address !== '').length
		) {
			buffer.push('<user />');
		}

		buffer.push(xmlAssignments.close);
	}
}

function appendXMLRecipients(buffer, recipients) {
	const recipientsAttrs = {};

	if (
		recipients?.receptionType &&
		recipients.receptionType.some((receptionType) => receptionType !== '')
	) {
		recipientsAttrs.receptionType = recipients.receptionType;
	}

	if (isObject(recipients) && !isObjectEmpty(recipients)) {
		appendXMLAssignments(buffer, recipients, 'recipients', recipientsAttrs);
	}
}

function appendXMLNotifications(buffer, notifications, nodeName) {
	if (notifications && notifications.name && !!notifications.name.length) {
		const {
			description,
			executionType,
			notificationTypes,
			recipients,
			template,
			templateLanguage,
		} = notifications;

		const xmlNotification = XMLUtil.createObj(nodeName || 'notification');

		notifications.name.forEach((item, index) => {
			buffer.push(
				xmlNotification.open,
				createTagWithEscapedContent('name', item)
			);

			if (isValidValue(description, index)) {
				buffer.push(
					XMLUtil.create('description', cdata(description[index]))
				);
			}

			if (isValidValue(template, index)) {
				buffer.push(XMLUtil.create('template', cdata(template[index])));
			}

			if (isValidValue(templateLanguage, index)) {
				buffer.push(
					createTagWithEscapedContent(
						'templateLanguage',
						templateLanguage[index]
					)
				);
			}

			if (isValidValue(notificationTypes, index)) {
				notificationTypes[index].forEach((item) => {
					buffer.push(
						createTagWithEscapedContent(
							'notificationType',
							item.notificationType
						)
					);
				});
			}

			let currentRecipients = recipients;

			if (Array.isArray(recipients[0]) && recipients.length === 1) {
				currentRecipients = recipients[0];
			}

			if (
				Array.isArray(currentRecipients) &&
				Array.isArray(currentRecipients[index])
			) {
				for (const recipientsIndex in currentRecipients[index]) {
					appendXMLRecipients(
						buffer,
						currentRecipients[index][recipientsIndex]
					);
				}
			}
			else {
				appendXMLRecipients(buffer, currentRecipients[index]);
			}

			if (executionType) {
				buffer.push(
					createTagWithEscapedContent(
						'executionType',
						executionType[index]
					)
				);
			}

			buffer.push(xmlNotification.close);
		});
	}
}

function appendXMLTaskTimers(buffer, taskTimers) {
	if (taskTimers && taskTimers.name && !!taskTimers.name.length) {
		const xmlTaskTimers = XMLUtil.createObj('task-timers');

		buffer.push(xmlTaskTimers.open);

		const blocking = taskTimers.blocking;
		const delay = taskTimers.delay;
		const description = taskTimers.description;
		const reassignments = taskTimers.reassignments;
		const timerActions = taskTimers.timerActions;
		const timerNotifications = taskTimers.timerNotifications;

		const xmlTaskTimer = XMLUtil.createObj('task-timer');

		taskTimers.name.forEach((item, index) => {
			buffer.push(
				xmlTaskTimer.open,
				createTagWithEscapedContent('name', item)
			);

			if (isValidValue(description, index)) {
				buffer.push(
					createTagWithEscapedContent(
						'description',
						description[index]
					)
				);
			}

			const xmlDelay = XMLUtil.createObj('delay');

			buffer.push(xmlDelay.open);

			buffer.push(
				createTagWithEscapedContent(
					'duration',
					delay[index].duration[0]
				)
			);
			buffer.push(
				createTagWithEscapedContent('scale', delay[index].scale[0])
			);

			buffer.push(xmlDelay.close);

			if (delay[index].duration.length > 1 && delay[index].duration[1]) {
				const xmlRecurrence = XMLUtil.createObj('recurrence');

				buffer.push(xmlRecurrence.open);

				buffer.push(
					createTagWithEscapedContent(
						'duration',
						delay[index].duration[1]
					)
				);
				buffer.push(
					createTagWithEscapedContent('scale', delay[index].scale[1])
				);

				buffer.push(xmlRecurrence.close);
			}

			if (blocking && blocking[index] !== '') {
				buffer.push(
					createTagWithEscapedContent('blocking', blocking[index])
				);
			}
			else {
				buffer.push(
					createTagWithEscapedContent('blocking', String(false))
				);
			}

			appendXMLActions(
				buffer,
				timerActions[index],
				timerNotifications[index],
				reassignments[index],
				'timer-actions',
				'timer-action',
				'timer-notification',
				'reassignments'
			);

			buffer.push(xmlTaskTimer.close);
		});

		buffer.push(xmlTaskTimers.close);
	}
}

function appendXMLTransitions(buffer, transitions) {
	if (transitions.length) {
		const xmlTransitions = XMLUtil.createObj('transitions');

		buffer.push(xmlTransitions.open);

		const xmlTransition = XMLUtil.createObj('transition');

		transitions.forEach((item) => {
			buffer.push(xmlTransition.open);

			const xmlLabels = XMLUtil.createObj('labels');

			buffer.push(xmlLabels.open);

			Object.entries(item.data.label).map(([key, value]) => {
				const xmlLabel = XMLUtil.createObj('label', {
					'language-id': `${key}`,
				});
				buffer.push(xmlLabel.open, value);

				buffer.push(xmlLabel.close);
			});

			buffer.push(xmlLabels.close);

			buffer.push(createTagWithEscapedContent('name', item.data.name));

			buffer.push(
				createTagWithEscapedContent('target', item.target),
				createTagWithEscapedContent(
					'default',
					`${item.data.defaultEdge}`
				),
				xmlTransition.close
			);
		});

		buffer.push(xmlTransitions.close);
	}
}

function serializeDefinition(xmlNamespace, metadata, nodes, transitions) {
	const description = metadata.description;
	const name = metadata.name;
	const version = parseInt(metadata.version, 10);

	const buffer = [];

	const xmlWorkflowDefinition = XMLUtil.createObj(
		'workflow-definition',
		xmlNamespace
	);

	buffer.push(
		'<?xml version="1.0"?>',
		STR_CHAR_CRLF,
		xmlWorkflowDefinition.open
	);

	if (name) {
		const nameWithHTMLEscape = Liferay.Util.escape(name);

		buffer.push(createTagWithEscapedContent('name', nameWithHTMLEscape));
	}

	if (description) {
		const descriptionWithHTMLEscape = Liferay.Util.escape(description);

		buffer.push(
			createTagWithEscapedContent(
				'description',
				descriptionWithHTMLEscape
			)
		);
	}

	if (version) {
		buffer.push(createTagWithEscapedContent('version', version));
	}

	nodes?.forEach((item) => {
		const description = item.data?.description;
		const initial = item.type === 'start';
		const name = item.id;
		const script = item.data?.script;
		const scriptLanguage = item.data?.scriptLanguage;
		let xmlType = item.type;

		if (xmlType === 'start' || xmlType === 'end') {
			xmlType = 'state';
		}

		const xmlNode = XMLUtil.createObj(xmlType);

		buffer.push(xmlNode.open, createTagWithEscapedContent('name', name));

		if (description) {
			const descriptionWithHTMLEscape = Liferay.Util.escape(description);

			buffer.push(
				createTagWithEscapedContent(
					'description',
					descriptionWithHTMLEscape
				)
			);
		}

		const metadata = {xy: [item.position.x, item.position.y]};

		if (item.type === 'end') {
			metadata.terminal = true;
		}

		buffer.push(XMLUtil.create('metadata', cdata(jsonStringify(metadata))));

		appendXMLActions(buffer, item.data.actions, item.data.notifications);

		appendXMLAssignments(buffer, item.data.assignments);

		if (initial) {
			buffer.push(createTagWithEscapedContent('initial', initial));
		}

		const xmlLabels = XMLUtil.createObj('labels');

		buffer.push(xmlLabels.open);

		Object.entries(item.data.label).map(([key, value]) => {
			const xmlLabel = XMLUtil.createObj('label', {
				'language-id': `${key}`,
			});
			buffer.push(xmlLabel.open, value);

			buffer.push(xmlLabel.close);
		});

		buffer.push(xmlLabels.close);

		appendXMLTaskTimers(buffer, item.data.taskTimers);

		if (script) {
			buffer.push(XMLUtil.create('script', cdata(script)));
		}

		if (xmlType === 'condition') {
			buffer.push(
				createTagWithEscapedContent(
					'scriptLanguage',
					scriptLanguage || DEFAULT_LANGUAGE
				)
			);
		}

		const nodeTransitions = transitions.filter(
			(transition) => transition.source === name
		);

		appendXMLTransitions(buffer, nodeTransitions);

		buffer.push(xmlNode.close);
	});

	buffer.push(xmlWorkflowDefinition.close);

	return XMLUtil.format(buffer);
}

export {serializeDefinition};
