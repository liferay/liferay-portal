/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';

import DeserializeUtil from '../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/deserializeUtil';
import {
	getReviewTaskDefinition,
	getReviewTaskDefinitionWithReference,
	getRolesXML,
	getUsersXML,
} from '../../../../utils/workflowDefinition';

const getElements = (xmlDefinition) => {
	const deserializeUtil = new DeserializeUtil(xmlDefinition);

	return deserializeUtil.getElements();
};

const getExpectedReferences = (reference) => ({
	assignment: reference,
	reassignment: reference,
	recipients: [reference],
	timerRecipients: [reference],
});

const getReferences = (xmlDefinition) => {
	const task = getElements(xmlDefinition).find(
		(element) => element.id === 'Review'
	);

	return {
		assignment: task.data.assignments,
		reassignment: task.data.taskTimers.reassignments[0],
		recipients: task.data.notifications.recipients[0],
		timerRecipients:
			task.data.taskTimers.timerNotifications[0].recipients[0],
	};
};

const readFixture = (fileName) =>
	fs.readFileSync(
		path.join(__dirname, `../../../../dependencies/${fileName}`),
		'utf8'
	);

describe('Deserializing a notification whose recipient roles omit auto-create', () => {
	it('Reads every role and leaves auto-create unset', () => {
		const elements = getElements(
			readFixture(
				'recipients-with-no-auto-create-roles-workflow-definition.xml'
			)
		);

		const task = elements.find((element) => element.id === 'Review');

		expect(task.data.notifications.recipients[0][0]).toEqual({
			assignmentType: ['roleType'],
			roleName: ['Portal Content Reviewer', 'Portal Content Publisher'],
			roleType: ['regular', 'regular'],
		});
	});
});

describe('Deserializing roles that carry an external reference code', () => {
	it('Reads the external reference code, type, and ID of every role', () => {
		expect(
			getReferences(
				getReviewTaskDefinition({
					assignment: getRolesXML([
						{
							externalReferenceCode:
								'ROLE_EXTERNAL_REFERENCE_CODE_1',
							id: 1,
							type: 'regular',
						},
					]),
					reassignment: getRolesXML([
						{
							externalReferenceCode:
								'ROLE_EXTERNAL_REFERENCE_CODE_3',
							id: 3,
							type: 'organization',
						},
					]),
					recipient: getRolesXML([
						{
							externalReferenceCode:
								'ROLE_EXTERNAL_REFERENCE_CODE_2',
							id: 2,
							type: 'site',
						},
					]),
					timerRecipient: getRolesXML([
						{
							externalReferenceCode:
								'ROLE_EXTERNAL_REFERENCE_CODE_4',
							id: 4,
							type: 'depot',
						},
					]),
				})
			)
		).toEqual({
			assignment: {
				assignmentType: ['roleId'],
				roleExternalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE_1',
				roleId: 1,
				roleType: 'regular',
			},
			reassignment: {
				assignmentType: ['roleId'],
				roleExternalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE_3',
				roleId: 3,
				roleType: 'organization',
			},
			recipients: [
				{
					assignmentType: ['roleId'],
					roleExternalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE_2',
					roleId: '2',
					roleType: 'site',
				},
			],
			timerRecipients: [
				{
					assignmentType: ['roleId'],
					roleExternalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE_4',
					roleId: ['4'],
					roleType: 'depot',
				},
			],
		});
	});
});

describe('Deserializing roles that carry an external reference code without an ID', () => {
	it('Reads every role by its external reference code, with or without a name', () => {
		const roleReferences = getExpectedReferences({
			assignmentType: ['roleId'],
			roleExternalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE',
			roleType: 'site',
		});

		const getRolesByExternalReferenceCode = (name) =>
			getReviewTaskDefinitionWithReference(
				getRolesXML([
					{
						externalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE',
						name,
						type: 'site',
					},
				])
			);

		expect(getReferences(getRolesByExternalReferenceCode())).toEqual(
			roleReferences
		);
		expect(
			getReferences(
				getRolesByExternalReferenceCode('Portal Content Reviewer')
			)
		).toEqual(roleReferences);
	});
});

describe('Deserializing roles written by name without a type', () => {
	it('Reads every role as a regular role by name', () => {
		const roleReference = {
			assignmentType: ['roleType'],
			roleName: ['Portal Content Reviewer'],
			roleType: ['regular'],
		};

		expect(
			getReferences(
				getReviewTaskDefinitionWithReference(
					getRolesXML([{name: 'Portal Content Reviewer'}])
				)
			)
		).toEqual({
			...getExpectedReferences(roleReference),
			reassignment: {...roleReference, autoCreate: [undefined]},
		});
	});
});

describe('Deserializing transitions that share a name', () => {
	it('Renames a transition only when its source node already uses the name', () => {
		const elements = getElements(
			readFixture('same-name-transitions-workflow-definition.xml')
		);

		const TransitionFromStartToParentTask = elements.find(
			(element) =>
				element.data.label.en_US ===
				'Transition From Start to Parent Task'
		);

		expect(TransitionFromStartToParentTask.data.name).toBe(
			'transitionName'
		);

		const TransitionFromParentTaskToChildTask1 = elements.find(
			(element) =>
				element.data.label.en_US ===
				'Transition From Parent Task to Child Task 1'
		);

		expect(TransitionFromParentTaskToChildTask1.data.name).toBe(
			'transitionName'
		);

		// The transition from Parent Task to Child Task 2 should be renamed
		// from transitionName to 'Parent Task_transitionName_Child Task 2'

		const TransitionFromParentTaskToChildTask2 = elements.find(
			(element) =>
				element.data.label.en_US ===
				'Transition From Parent Task to Child Task 2'
		);

		expect(TransitionFromParentTaskToChildTask2.data.name).toBe(
			'Parent Task_transitionName_Child Task 2'
		);

		const TransitionFromChildTask1ToEnd = elements.find(
			(element) =>
				element.data.label.en_US ===
				'Transition From Child Task 1 to End'
		);

		expect(TransitionFromChildTask1ToEnd.data.name).toBe('transitionName');

		const TransitionFromChildTask2ToEnd = elements.find(
			(element) =>
				element.data.label.en_US ===
				'Transition From Child Task 2 to End'
		);

		expect(TransitionFromChildTask2ToEnd.data.name).toBe('transitionName');
	});
});

describe('Deserializing users that carry an external reference code and users that do not', () => {
	it('Keeps the external reference codes paired with their user IDs', () => {
		expect(
			getReferences(
				getReviewTaskDefinitionWithReference(
					getUsersXML([
						{
							externalReferenceCode:
								'USER_EXTERNAL_REFERENCE_CODE',
							id: 1,
						},
						{id: 10},
					])
				)
			)
		).toEqual(
			getExpectedReferences({
				assignmentType: ['user'],
				userExternalReferenceCode: [
					'USER_EXTERNAL_REFERENCE_CODE',
					undefined,
				],
				userId: ['1', '10'],
			})
		);
	});
});

describe('Deserializing users that carry an external reference code without an ID', () => {
	it('Reads every user by its external reference code, with or without an email address', () => {
		const userReferences = getExpectedReferences({
			assignmentType: ['user'],
			userExternalReferenceCode: ['USER_EXTERNAL_REFERENCE_CODE'],
			userId: [undefined],
		});

		const getUsersByExternalReferenceCode = (emailAddress) =>
			getReviewTaskDefinitionWithReference(
				getUsersXML([
					{
						emailAddress,
						externalReferenceCode: 'USER_EXTERNAL_REFERENCE_CODE',
					},
				])
			);

		expect(getReferences(getUsersByExternalReferenceCode())).toEqual(
			userReferences
		);
		expect(
			getReferences(getUsersByExternalReferenceCode('user@liferay.com'))
		).toEqual(userReferences);
	});
});
