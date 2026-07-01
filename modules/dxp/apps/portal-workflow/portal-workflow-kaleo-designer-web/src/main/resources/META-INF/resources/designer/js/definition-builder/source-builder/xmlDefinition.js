/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {COL_TYPES_FIELD} from './constants';
import XMLSchemaUtil from './xmlSchemaUtil';
import XMLUtil from './xmlUtil';

let ATTRS;

export default function XMLDefinition(config) {
	const instance = this;
	ATTRS = {};

	const value = instance.sanitizeDefinitionXML(config.value);

	if (!value || XMLUtil.validateDefinition(value)) {
		const parser = new DOMParser();

		instance.definitionDoc = parser.parseFromString(value, 'text/xml');
	}
}

XMLDefinition.prototype = {
	forEachField(fn) {
		const instance = this;

		COL_TYPES_FIELD.forEach((item) => {
			const fieldData = instance.translate(item);

			if (fn && !fieldData.error) {
				fn.call(instance, item, fieldData);
			}
		});
	},

	getDefinitionMetadata() {
		const instance = this;

		const output = XMLSchemaUtil.applySchema(
			{
				metaFields: {
					description: '//workflow-definition/description',
					name: '//workflow-definition/name',
					version: '//workflow-definition/version',
				},
			},
			instance.definitionDoc
		);

		return output.meta;
	},

	getSchemaActions(key, tagName) {
		return {
			key: key || 'actions',
			schema: {
				resultFields: [
					{
						key: 'description',
						locator: 'description',
					},
					{
						key: 'executionType',
						locator: 'execution-type',
					},
					{
						key: 'name',
						locator: 'name',
					},
					{
						key: 'priority',
						locator: 'priority',
					},
					{
						key: 'script',
						locator: 'script',
					},
					{
						key: 'scriptLanguage',
						locator: 'script-language',
					},
					{
						key: 'status',
						locator: 'status',
					},
				],
				resultListLocator: tagName || 'action',
			},
		};
	},

	getSchemaAssignments(key, tagName) {
		return {
			key: key || 'assignments',
			schema: {
				resultFields: [
					{
						key: 'address',
						locator: 'address',
					},
					{
						key: 'resourceActions',
						schema: {
							resultFields: [
								{
									key: 'resourceAction',
									locator: 'resource-action',
								},
							],
							resultListLocator: 'resource-actions',
						},
					},
					{
						key: 'roleId',
						schema: {
							resultFields: [
								{
									key: 'roleId',
									locator: 'role-id',
								},
								{
									key: 'roleNameAC',
									locator: 'role-name-ac',
								},
							],
							resultListLocator: 'role',
						},
					},
					{
						key: 'roleType',
						schema: {
							resultFields: [
								{
									key: 'autoCreate',
									locator: 'auto-create',
								},
								{
									key: 'roleName',
									locator: 'name',
								},
								{
									key: 'roleType',
									locator: 'role-type',
								},
							],
							resultListLocator: 'role',
						},
					},
					{
						key: 'scriptedAssignment',
						schema: {
							resultFields: [
								{
									key: 'script',
									locator: 'script',
								},
								{
									key: 'scriptLanguage',
									locator: 'script-language',
								},
							],
							resultListLocator: 'scripted-assignment',
						},
					},
					{
						key: 'scriptedRecipient',
						schema: {
							resultFields: [
								{
									key: 'script',
									locator: 'script',
								},
								{
									key: 'scriptLanguage',
									locator: 'script-language',
								},
							],
							resultListLocator: 'scripted-recipient',
						},
					},
					{
						key: 'taskAssignees',
						locator: 'assignees',
					},
					{
						key: 'user',
						schema: {
							resultFields: [
								{
									key: 'emailAddress',
									locator: 'email-address',
								},
								{
									key: 'fullName',
									locator: 'full-name',
								},
								{
									key: 'screenName',
									locator: 'screen-name',
								},
								{
									key: 'userId',
									locator: 'user-id',
								},
							],
							resultListLocator: 'user',
						},
					},
					{
						key: 'receptionType',
						locator: '@receptionType',
					},
				],
				resultListLocator: tagName || 'assignments',
			},
		};
	},

	getSchemaNotifications(key, tagName, assignmentKey, assignmentTagName) {
		const instance = this;

		assignmentKey = assignmentKey || 'recipients';
		assignmentTagName = assignmentTagName || 'recipients';

		return {
			key: key || 'notifications',
			schema: {
				resultFields: [
					{
						key: 'description',
						locator: 'description',
					},
					{
						key: 'executionType',
						locator: 'execution-type',
					},
					{
						key: 'name',
						locator: 'name',
					},
					{
						key: 'notificationTypes',
						schema: {
							resultFields: [
								{
									key: 'notificationType',
									locator: '.',
								},
							],
							resultListLocator: 'notification-type',
						},
					},
					{
						key: 'template',
						locator: 'template',
					},
					{
						key: 'templateLanguage',
						locator: 'template-language',
					},
					instance.getSchemaAssignments(
						assignmentKey,
						assignmentTagName
					),
				],
				resultListLocator: tagName || 'notification',
			},
		};
	},

	getSchemaTaskTimers(key, tagNode) {
		const instance = this;

		return {
			key: key || 'taskTimers',
			schema: {
				resultFields: [
					{
						key: 'blocking',
						locator: 'blocking',
					},
					{
						key: 'delay',
						schema: {
							resultFields: [
								{
									key: 'duration',
									locator: 'duration',
								},
								{
									key: 'scale',
									locator: 'scale',
								},
							],
							resultListLocator: 'delay',
						},
					},
					{
						key: 'description',
						locator: 'description',
					},
					{
						key: 'name',
						locator: 'name',
					},
					{
						key: 'recurrence',
						schema: {
							resultFields: [
								{
									key: 'duration',
									locator: 'duration',
								},
								{
									key: 'scale',
									locator: 'scale',
								},
							],
							resultListLocator: 'recurrence',
						},
					},
					instance.getSchemaActions('timerActions', 'timer-action'),
					instance.getSchemaAssignments(
						'reassignments',
						'reassignments'
					),
					instance.getSchemaNotifications(
						'timerNotifications',
						'timer-notification'
					),
				],
				resultListLocator: tagNode || 'task-timer',
			},
		};
	},

	sanitizeDefinitionXML(value) {
		const instance = this;

		value = decodeURIComponent(value);

		value = value.replace(/\s*(<!\[CDATA\[)/g, '$1');
		value = value.replace(/(\]\]>)\s*/g, '$1');

		instance.updateXMLNamespace(value);

		return value.replace(/(<workflow-definition)[^>]*(>)/, '$1$2');
	},

	set(key, value) {
		ATTRS[key] = value;
	},

	translate(tagName) {
		const instance = this;

		const schema = {
			resultFields: [
				'agent-definition-external-reference-code',
				'description',
				'http-method',
				'id',
				'input-variables',
				'initial',
				'java-delegate',
				{
					key: 'labels',
					locator: 'labels',
				},
				'metadata',
				'name',
				{
					key: 'prompt',
					locator: 'prompt',
				},
				'output-variables',
				'rag',
				'request-body',
				'script',
				{
					key: 'scriptLanguage',
					locator: 'script-language',
				},
				{
					key: 'transitions',
					locator: 'transitions',
				},
				'timeout',
				'tools',
				'url',
				'user-message',
				instance.getSchemaActions(),
				instance.getSchemaAssignments(),
				instance.getSchemaNotifications(),
				instance.getSchemaTaskTimers(),
			],
			resultListLocator: tagName,
		};

		return XMLSchemaUtil.applySchema(schema, instance.definitionDoc);
	},

	updateXMLNamespace(definition) {
		const instance = this;

		const workflowDefinition = /(<workflow-definition)[^>]*(>)/.exec(
			definition
		);

		if (workflowDefinition) {
			const xmlns = /xmlns="([^"]*)"/.exec(workflowDefinition);
			const xmlnsXsi = /xmlns:xsi="([^"]*)"/.exec(workflowDefinition);
			const xsiSchemaLocation = /xsi:schemaLocation="([^"]*)"/.exec(
				workflowDefinition
			);

			if (xmlns && xmlnsXsi && xsiSchemaLocation) {
				instance.set('xmlNamespace', {
					'xmlns': xmlns[1],
					'xmlns:xsi': xmlnsXsi[1],
					'xsi:schemaLocation': xsiSchemaLocation[1],
				});
			}
		}
	},
};
