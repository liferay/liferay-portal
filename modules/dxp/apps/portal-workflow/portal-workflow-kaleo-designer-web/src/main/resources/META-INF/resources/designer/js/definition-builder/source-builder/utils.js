/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DEFAULT_LANGUAGE} from './constants';

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

export function parseAssignments(node) {
	const assignments = {};
	const autoCreateValues = [];
	const roleNames = [];
	const roleTypes = [];
	const users = [];
	const typeUser = Object.keys(node.assignments[0])[0];

	node.assignments.forEach((item) => {
		const itemKeys = Object.keys(item);

		if (itemKeys.includes('resource-action')) {
			assignments.assignmentType = ['resourceActions'];
			assignments.resourceAction = item['resource-action'];
		}
		else if (itemKeys.includes('role-id')) {
			assignments.assignmentType = ['roleId'];
			assignments.roleId = parseInt(item['role-id'], 10);
		}
		else if (itemKeys.includes('role-type')) {
			assignments.assignmentType = ['roleType'];
			autoCreateValues.push(item['auto-create']);
			roleNames.push(item.name);
			roleTypes.push(item['role-type']);
		}
		else if (itemKeys.includes('script')) {
			assignments.assignmentType = ['scriptedAssignment'];
			assignments.script = [item.script];
			assignments.scriptLanguage = item['script-language'];
		}
		else if (itemKeys.includes('user')) {
			assignments.assignmentType = ['user'];
		}
		else if (itemKeys.includes('email-address')) {
			assignments.assignmentType = ['user'];
			users.push(item['email-address']);
		}
		else if (itemKeys.includes('user-id')) {
			assignments.assignmentType = ['user'];
			users.push(item['user-id']);
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
			if (item['roles'][0]['role']?.['role-type']) {
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
						item['roles'][index]?.['role']?.['role-type']
					);
				}
			}
			else {
				assignments.assignmentType = ['roleId'];
				assignments.roleId = parseInt(
					item['roles'][0]['role']?.['role-id'],
					10
				);
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

			const usersLength = item['user']?.length;

			const emailAddress = [];
			const screenName = [];
			const userId = [];

			for (let i = 0; i < usersLength; i++) {
				if (item['user'][i]?.['email-address']) {
					emailAddress.push(item['user'][i]?.['email-address']);
				}
				else if (item['user'][0]?.['screen-name']) {
					screenName.push(item['user'][i]?.['screen-name']);
				}
				else if (item['user'][0]?.['user-id']) {
					userId.push(item['user'][i]?.['user-id']);
				}
			}

			if (emailAddress.length) {
				assignments.emailAddress = emailAddress;
			}

			if (screenName.length) {
				assignments.screenName = screenName;
			}

			if (userId.length) {
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
			if (item['user'].some((item) => item['email-address'])) {
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

			if (item['user'].some((item) => item['user-id'])) {
				const userId = [];

				item['user'].forEach((item) => {
					userId.push(item['user-id']);
				});

				if (item.receptionType) {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						receptionType: [receptionType],
						userId,
					});
				}
				else {
					notifications.recipients[index].push({
						assignmentType: ['user'],
						userId,
					});
				}
			}

			if (item['user'].some((item) => item['screen-name'])) {
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

			item['recipients'].forEach((item) => {
				if (item['user']?.['email-address']) {
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
		}

		const roles = item['recipients']?.[0]?.['roles'];

		if (item['role-type'] || roles?.['role-type']) {
			const autoCreate = item['auto-create'] || roles?.['auto-create'];
			const roleName = item['role-name'] || roles?.['name'];
			const roleType = item['role-type'] || roles?.['role-type'];

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

		if (
			item['role-id'] ||
			(item['recipients'] &&
				(item['recipients'][0]?.['roles']?.['role'] ||
					item['recipients'][0]?.['roles']?.['role-id']))
		) {
			const roleId = item['role-id']
				? item['role-id'][0]
				: item['recipients'][0]['roles']['role'] ||
					item['recipients'][0]['roles']['role-id'];

			if (receptionType) {
				notifications.recipients[index].push({
					assignmentType: ['roleId'],
					receptionType: [receptionType],
					roleId,
				});
			}
			else {
				notifications.recipients[index].push({
					assignmentType: ['roleId'],
					roleId,
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
