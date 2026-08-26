/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getTransitions,
	groupWorkflowTasks,
} from '../../js/utils/groupWorkflowTasks';
import {WorkflowTaskItemData} from '../../js/utils/types';

const APPROVE_AND_REJECT = {
	changeTransition: {
		href: '/o/headless-admin-workflow/v1.0/workflow-tasks/1/change-transition',
		method: 'POST',
	},
	workflow_approve: {
		href: '/o/headless-admin-workflow/v1.0/workflow-tasks/1/change-transition',
		label: 'Approve',
		method: 'POST',
		name: 'approve',
	},
	workflow_reject: {
		href: '/o/headless-admin-workflow/v1.0/workflow-tasks/1/change-transition',
		label: 'Reject',
		method: 'POST',
		name: 'reject',
	},
};

function createItem({
	actions = APPROVE_AND_REJECT,
	assetTitle = 'Blog 1',
	id = 1,
	label = 'Review',
	name = 'review',
	workflowDefinitionName = 'Single Approver',
	workflowDefinitionVersion = '1',
}: {
	actions?: Record<string, any>;
	assetTitle?: string;
	id?: number;
	label?: string;
	name?: string;
	workflowDefinitionName?: string;
	workflowDefinitionVersion?: string;
} = {}): WorkflowTaskItemData {
	const person = {
		contentType: 'UserAccount',
		familyName: 'Test',
		givenName: 'Test',
		id: 20132,
		name: 'Test Test',
		profileURL: '/web/test',
	};

	return {
		embedded: {
			actions,
			assignedToMe: true,
			assigneePerson: person,
			assigneeRoles: [],
			completed: false,
			creator: person,
			dateCreated: '2026-08-25T19:11:43Z',
			dateDue: '',
			description: '',
			id,
			label,
			name,
			objectReviewed: {
				assetTitle,
				assetType: 'Blog',
				id: id + 1000,
			},
			workflowDefinitionId: 35827,
			workflowDefinitionName,
			workflowDefinitionVersion,
			workflowInstanceId: id + 2000,
		},
		entryClassName:
			'com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken',
		id,
	};
}

describe('getTransitions', () => {
	it('ignores actions that are not transitions', () => {
		expect(getTransitions(createItem()).map(({name}) => name)).toEqual([
			'approve',
			'reject',
		]);
	});

	it('orders transitions by label rather than by action key', () => {
		const item = createItem({
			actions: {
				workflow_approve: {
					href: '/change-transition',
					label: 'Publish',
					method: 'POST',
					name: 'approve',
				},
				workflow_reject: {
					href: '/change-transition',
					label: 'Deny',
					method: 'POST',
					name: 'reject',
				},
			},
		});

		expect(getTransitions(item).map(({label}) => label)).toEqual([
			'Deny',
			'Publish',
		]);
	});

	it('returns an empty list for a task the user does not own', () => {
		const item = createItem({
			actions: {
				get: {href: '/workflow-tasks/1', method: 'GET'},
			},
		});

		expect(getTransitions(item)).toEqual([]);
	});
});

describe('groupWorkflowTasks', () => {
	it('counts every task on its workflow group', () => {
		const groups = groupWorkflowTasks([
			createItem({id: 1}),
			createItem({id: 2, label: 'Publish', name: 'publish'}),
			createItem({id: 3, label: 'Publish', name: 'publish'}),
		]);

		expect(groups).toHaveLength(1);
		expect(groups[0].taskCount).toBe(3);
	});

	it('groups the tasks of one workflow by their current step', () => {
		const groups = groupWorkflowTasks([
			createItem({id: 1}),
			createItem({id: 2, label: 'Publish', name: 'publish'}),
			createItem({id: 3}),
		]);

		expect(groups).toHaveLength(1);

		const {stepGroups} = groups[0];

		expect(stepGroups.map(({name}) => name)).toEqual(['review', 'publish']);
		expect(stepGroups[0].tasks.map(({id}) => id)).toEqual([1, 3]);
		expect(stepGroups[1].tasks.map(({id}) => id)).toEqual([2]);
	});

	it('keeps the same workflow at two versions in separate groups', () => {
		const groups = groupWorkflowTasks([
			createItem({id: 1, workflowDefinitionVersion: '1'}),
			createItem({id: 2, workflowDefinitionVersion: '3'}),
		]);

		expect(groups).toHaveLength(2);
		expect(
			groups.map(
				({workflowDefinitionName, workflowDefinitionVersion}) => [
					workflowDefinitionName,
					workflowDefinitionVersion,
				]
			)
		).toEqual([
			['Single Approver', '1'],
			['Single Approver', '3'],
		]);
	});

	it('preserves the order the rows arrived in', () => {
		const groups = groupWorkflowTasks([
			createItem({id: 1, workflowDefinitionName: 'Single Approver'}),
			createItem({id: 2, workflowDefinitionName: 'Liferay.com Review'}),
			createItem({id: 3, workflowDefinitionName: 'Single Approver'}),
		]);

		expect(
			groups.map(({workflowDefinitionName}) => workflowDefinitionName)
		).toEqual(['Single Approver', 'Liferay.com Review']);
	});

	it('returns an empty list when nothing is selected', () => {
		expect(groupWorkflowTasks([])).toEqual([]);
	});

	it('takes the transitions of its first task, so callers must pass only tasks the user owns', () => {
		const [group] = groupWorkflowTasks([
			createItem({
				actions: {get: {href: '/workflow-tasks/1', method: 'GET'}},
				id: 1,
			}),
			createItem({id: 2}),
		]);

		expect(group.stepGroups).toHaveLength(1);
		expect(group.stepGroups[0].tasks).toHaveLength(2);
		expect(group.stepGroups[0].transitions).toEqual([]);
	});
});
