/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DEFAULT_LANGUAGE, DEFAULT_ROLE_TYPE} from './constants';

export function parseActions(node) {
	const actions = {};

	node.actions.forEach((item) => {
		actions.name = parseProperty(actions, item, 'name');
		actions.description = parseProperty(actions, item, 'description');
		actions.executionType = parseProperty(actions, item, 'execution-type');
		actions.priority = parseProperty(actions, item, 'priority');
		actions.script = parseProperty(actions, item, 'script');
		actions.scriptLanguage = parseProperty(
			actions,
			item,
			'script-language'
		);
		actions.status = parseProperty(actions, item, 'status');
	});

	return actions;
}

function getReassignmentUsers(entries) {
	const users = [];

	let pendingUser = null;

	for (const entry of entries) {
		if (entry['user-external-reference-code']) {
			pendingUser = {...entry};

			users.push(pendingUser);
		}
		else if (pendingUser) {
			Object.assign(pendingUser, entry);

			pendingUser = null;
		}
		else {
			users.push(entry);
		}
	}

	return users;
}

function isRoleById(keys) {
	return (
		keys.includes('role-external-reference-code') ||
		keys.includes('role-id')
	);
}

function isUserById(user) {
	return user['user-external-reference-code'] || user['user-id'];
}

function parseRoleId(value) {
	const roleId = parseInt(value, 10);

	return Number.isNaN(roleId) ? undefined : roleId;
}

export function parseAssignments(node) {
	const assignments = {};
	const autoCreateValues = [];
	const roleNames = [];
	const roleTypes = [];
	const typeUser = isUserById(node.assignments[0])
		? 'user-id'
		: Object.keys(node.assignments[0])[0];
	const userExternalReferenceCodes = [];
	const users = [];

	node.assignments.forEach((item) => {
		const itemKeys = Object.keys(item);

		if (itemKeys.includes('resource-action')) {
			assignments.assignmentType = ['resourceActions'];
			assignments.resourceAction = item['resource-action'];
		}
		else if (isRoleById(itemKeys)) {
			assignments.assignmentType = ['roleId'];
			assignments.roleExternalReferenceCode =
				item['role-external-reference-code']?.[0];
			assignments.roleId = parseRoleId(item['role-id']);
			assignments.roleType = item['role-type']?.[0];
		}
		else if (
			itemKeys.includes('role-type') ||
			itemKeys.includes('name')
		) {
			assignments.assignmentType = ['roleType'];
			autoCreateValues.push(item['auto-create']);
			roleNames.push(item.name);
			roleTypes.push(item['role-type'] ?? [DEFAULT_ROLE_TYPE]);
		}
		else if (itemKeys.includes('script')) {
			assignments.assignmentType = ['scriptedAssignment'];
			assignments.script = [item.script];
			assignments.scriptLanguage = item['script-language'];
		}
		else if (itemKeys.includes('user')) {
			assignments.assignmentType = ['user'];
		}
		else if (isUserById(item)) {
			assignments.assignmentType = ['user'];
			userExternalReferenceCodes.push(
				item['user-external-reference-code']
			);
			users.push(item['user-id']);
		}
		else if (itemKeys.includes('email-address')) {
			assignments.assignmentType = ['user'];
			users.push(item['email-address']);
		}
		else if (itemKeys.includes('screen-name')) {
			assignments.assignmentType = ['user'];
			users.push(item['screen-name']);
		}
	});

	if (users.length) {
		if (typeUser === 'email-address') {
			assignments.emailAddress = users;
		}
		if (typeUser === 'user-id') {
			assignments.userExternalReferenceCode = userExternalReferenceCodes;
			assignments.userId = users;
		}
		if (typeUser === 'screen-name') {
			assignments.screenName = users;
		}
	}

	if (assignments.assignmentType[0] === 'roleType') {
		assignments.autoCreate = autoCreateValues[0];
		assignments.roleName = roleNames[0];
		assignments.roleType = roleTypes[0];
	}

	return assignments;
}

export function parseReassignments(node) {
	const assignments = {};

	node.assignments.forEach((item) => {
		if (item['resource-actions']) {
			assignments.assignmentType = ['resourceActions'];
			assignments.resourceAction =
				item['resource-actions'][0]['resource-action'];
		}
		else if (item['roles']) {
			if (isRoleById(Object.keys(item['roles'][0]['role'] ?? {}))) {
				assignments.assignmentType = ['roleId'];
				assignments.roleExternalReferenceCode =
					item['roles'][0]['role']?.['role-external-reference-code'];
				assignments.roleId = parseRoleId(
					item['roles'][0]['role']?.['role-id']
				);
				assignments.roleType = item['roles'][0]['role']?.['role-type'];
			}
			else {
				assignments.assignmentType = ['roleType'];

				assignments.autoCreate = [];
				assignments.roleName = [];
				assignments.roleType = [];

				for (let index = 0; index < item['roles']?.length; index++) {
					assignments.autoCreate.push(
						item['roles'][index]?.['role']?.['auto-create']
					);
					assignments.roleName.push(
						item['roles'][index]?.['role']?.['name']
					);
					assignments.roleType.push(
						item['roles'][index]?.['role']?.['role-type'] ??
							DEFAULT_ROLE_TYPE
					);
				}
			}
		}
		else if (item['scripted-assignment']) {
			assignments.assignmentType = ['scriptedAssignment'];
			assignments.script = [item['scripted-assignment'][0].script];
			assignments.scriptLanguage =
				item['scripted-assignment'][1]['script-language'];
		}
		else if (item['user'] !== undefined) {
			assignments.assignmentType = ['user'];

			const emailAddress = [];
			const screenName = [];
			const userExternalReferenceCode = [];
			const userId = [];

			for (const user of getReassignmentUsers(item['user'])) {
				if (isUserById(user)) {
					userExternalReferenceCode.push(
						user['user-external-reference-code']
					);
					userId.push(user['user-id']);
				}
				else if (user['email-address']) {
					emailAddress.push(user['email-address']);
				}
				else if (user['screen-name']) {
					screenName.push(user['screen-name']);
				}
			}

			if (emailAddress.length) {
				assignments.emailAddress = emailAddress;
			}

			if (screenName.length) {
				assignments.screenName = screenName;
			}

			if (userId.length) {
				assignments.userExternalReferenceCode =
					userExternalReferenceCode;
				assignments.userId = userId;
			}
		}
	});

	return assignments;
}

export function parseNotifications(node) {
	const notifications = {notificationTypes: [], recipients: []};

	if (node.nodeName !== 'timer-notification') {
		node.notifications.forEach((item) => {
			notifications.executionType = parseProperty(
				notifications,
				item,
				'execution-type'
			);
		});
	}

	node.notifications.forEach((item, index) => {
		notifications.description = parseProperty(
			notifications,
			item,
			'description'
		);

		notifications.name = parseProperty(notifications, item, 'name');

		let notificationTypes = parseProperty(
			notifications,
			item,
			'notification-type'
		);

		if (Array.isArray(notificationTypes[0])) {
			const typeArray = [];
			notificationTypes[0].forEach((type) => {
				typeArray.push({notificationType: type});
			});

			notificationTypes = typeArray;
		}
		else {
			notificationTypes = [{notificationType: notificationTypes[0]}];
		}

		notifications.notificationTypes[index] = notificationTypes;

		notifications.template = parseProperty(notifications, item, 'template');
		notifications.templateLanguage = parseProperty(
			notifications,
			item,
			'template-language'
		);

		if (!notifications.recipients[index]) {
			notifications.recipients[index] = [];
		}

		let receptionType;

		if (item.receptionType) {
			if (Array.isArray(item.receptionType)) {
				receptionType =
					item.receptionType[notifications.recipients[index].length];
			}
			else {
				receptionType = item.receptionType;
			}
		}

		if (
			item.assignees ||
			(item['recipients'] && 'assignees' in item['recipients'][0])
		) {
			if (receptionType) {
				notifications.recipients[index].push({
					assignmentType: ['taskAssignees'],
					receptionType: [receptionType],
				});
			}
			else {
				notifications.recipients[index].push({
					assignmentType: ['taskAssignees'],
				});
			}
		}

		if (item['user']) {
			if (item['user'].some(isUserById)) {
				const userExternalReferenceCode = [];
				const userId = [];

				item['user'].forEach((item) => {
					userExternalReferenceCode.push(
						item['user-external-reference-code']
					);
					userId.push(item['user-id']);
				});

				if (item.receptionType) {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						receptionType: [receptionType],
						userExternalReferenceCode,
						userId,
					});
				}
				else {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						userExternalReferenceCode,
						userId,
					});
				}
			}
			else if (item['user'].some((item) => item['email-address'])) {
				const emailAddress = [];

				item['user'].forEach((item) => {
					emailAddress.push(item['email-address']);
				});

				if (receptionType) {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						emailAddress,
						receptionType: [receptionType],
					});
				}
				else {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						emailAddress,
					});
				}
			}
			else if (item['user'].some((item) => item['screen-name'])) {
				const screenName = [];

				item['user'].forEach((item) => {
					screenName.push(item['screen-name']);
				});

				if (receptionType) {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						receptionType: [receptionType],
						screenName,
					});
				}
				else {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						screenName,
					});
				}
			}
		}
		else if (item['recipients'] && item['recipients'][0]?.['user']) {
			const emailAddress = [];
			const userExternalReferenceCode = [];
			const userId = [];

			item['recipients'].forEach((item) => {
				if (item['user'] && isUserById(item['user'])) {
					userExternalReferenceCode.push(
						item['user']['user-external-reference-code']
					);
					userId.push(item['user']['user-id']);
				}
				else if (item['user']?.['email-address']) {
					emailAddress.push(item['user']['email-address']);
				}
			});

			if (emailAddress.length) {
				if (receptionType) {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						emailAddress,
						receptionType: [receptionType],
					});
				}
				else {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						emailAddress,
					});
				}
			}

			if (userId.length) {
				if (receptionType) {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						receptionType: [receptionType],
						userExternalReferenceCode,
						userId,
					});
				}
				else {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						userExternalReferenceCode,
						userId,
					});
				}
			}
		}

		const roles = item['recipients']?.[0]?.['roles'];

		const roleByIdRecipient = Boolean(
			item['role-external-reference-code'] ||
				item['role-id'] ||
				roles?.['role'] ||
				roles?.['role-external-reference-code'] ||
				roles?.['role-id']
		);

		if (!roleByIdRecipient && (item['role-name'] || roles?.['name'])) {
			const autoCreate = item['auto-create'] || roles?.['auto-create'];
			const roleName = item['role-name'] || roles?.['name'];
			const roleType =
				item['role-type'] ||
				roles?.['role-type'] ||
				(Array.isArray(roleName)
					? roleName.map(() => DEFAULT_ROLE_TYPE)
					: DEFAULT_ROLE_TYPE);

			if (receptionType) {
				notifications.recipients[index].push({
					assignmentType: ['roleType'],
					autoCreate,
					receptionType: [receptionType],
					roleName,
					roleType,
				});
			}
			else {
				notifications.recipients[index].push({
					assignmentType: ['roleType'],
					autoCreate,
					roleName,
					roleType,
				});
			}
		}

		if (roleByIdRecipient) {
			const roleExternalReferenceCode =
				item['role-external-reference-code']?.[0] ||
				roles?.['role-external-reference-code']?.[0];
			const roleId = item['role-id']
				? item['role-id'][0]
				: item['recipients']?.[0]?.['roles']?.['role'] ||
					item['recipients']?.[0]?.['roles']?.['role-id'];
			const roleType =
				item['role-type']?.[0] || roles?.['role-type']?.[0];

			if (receptionType) {
				notifications.recipients[index].push({
					assignmentType: ['roleId'],
					receptionType: [receptionType],
					roleExternalReferenceCode,
					roleId,
					roleType,
				});
			}
			else {
				notifications.recipients[index].push({
					assignmentType: ['roleId'],
					roleExternalReferenceCode,
					roleId,
					roleType,
				});
			}
		}

		if (
			item['scripted-recipient'] ||
			(item['recipients'] &&
				item['recipients'][0]?.['scripted-recipient'])
		) {
			const scriptedRecipient = item['scripted-recipient']
				? item['scripted-recipient'][0]
				: item['recipients'][0]['scripted-recipient'];

			const script = scriptedRecipient.script;
			const scriptLanguage = scriptedRecipient['script-language'];

			if (receptionType) {
				notifications.recipients[index].push({
					assignmentType: ['scriptedRecipient'],
					receptionType: [receptionType],
					script: [script],
					scriptLanguage: scriptLanguage || [DEFAULT_LANGUAGE],
				});
			}
			else {
				notifications.recipients[index].push({
					assignmentType: ['scriptedRecipient'],
					script: [script],
					scriptLanguage: scriptLanguage || [DEFAULT_LANGUAGE],
				});
			}
		}

		if (!notifications.recipients[index].length) {
			if (item.receptionType) {
				notifications.recipients[index].push({
					assignmentType: ['user'],
					receptionType: [item.receptionType],
				});
			}
			else {
				notifications.recipients[index].push({
					assignmentType: ['user'],
				});
			}
		}
	});

	return notifications;
}

function parseProperty(data, item, property) {
	let newProperty = property;

	if (property === 'execution-type') {
		newProperty = 'executionType';
	}
	else if (property === 'script-language') {
		newProperty = 'scriptLanguage';
	}
	else if (property === 'template-language') {
		newProperty = 'templateLanguage';
	}
	if (Array.isArray(data[newProperty])) {
		data[newProperty].push(item[property]);

		return data[newProperty];
	}

	return new Array(item[property]);
}

export function parseTimers(node) {
	const taskTimers = {};
	taskTimers.delay = [];
	taskTimers.reassignments = [];
	taskTimers.timerActions = [];
	taskTimers.timerNotifications = [];

	node.taskTimers.forEach((item, index) => {
		taskTimers.delay.push({
			duration: node.taskTimers[index].duration,
			scale: node.taskTimers[index].scale,
		});
		taskTimers.reassignments.push(
			node.taskTimers[index]['reassignments']
				? parseReassignments({
						assignments: node.taskTimers[index]['reassignments'],
					})
				: {}
		);
		taskTimers.timerActions.push(
			node.taskTimers[index]['timer-action']
				? parseActions({
						actions: node.taskTimers[index]['timer-action'],
					})
				: {}
		);
		taskTimers.timerNotifications.push(
			node.taskTimers[index]['timer-notification']
				? parseNotifications({
						nodeName: 'timer-notification',
						notifications:
							node.taskTimers[index]['timer-notification'],
					})
				: {}
		);
		taskTimers.name = parseProperty(taskTimers, item, 'name');
		taskTimers.description = parseProperty(taskTimers, item, 'description');
		taskTimers.blocking = parseProperty(taskTimers, item, 'blocking');
	});

	return taskTimers;
}
