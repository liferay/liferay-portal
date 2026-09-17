/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';

import DeserializeUtil from '../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/deserializeUtil';

const getElements = (fileName) => {
	const xmlFilePath = path.join(
		__dirname,
		`../../../../dependencies/${fileName}`
	);

	const xmlFileContent = fs.readFileSync(xmlFilePath, 'utf8');

	const deserializeUtil = new DeserializeUtil(xmlFileContent);

	return deserializeUtil.getElements();
};

describe('Deserializing a notification whose recipient roles omit auto-create', () => {
	it('Reads every role and leaves auto-create unset', () => {
		const elements = getElements(
			'recipients-with-no-auto-create-roles-workflow-definition.xml'
		);

		const task = elements.find((element) => element.id === 'Review');

		expect(task.data.notifications.recipients[0][0]).toEqual({
			assignmentType: ['roleType'],
			roleName: ['Portal Content Reviewer', 'Portal Content Publisher'],
			roleType: ['regular', 'regular'],
		});
	});
});

describe('Deserializing transitions that share a name', () => {
	it('Renames a transition only when its source node already uses the name', () => {
		const elements = getElements(
			'same-name-transitions-workflow-definition.xml'
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
