/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import * as WorkflowService from '../../../../src/main/resources/META-INF/resources/js/common/services/WorkflowService';
import ViewWorkflowTasks from '../../../../src/main/resources/META-INF/resources/js/main_view/home/ViewWorkflowTasks';

describe('[CMS Dashboard] Components: ViewWorkflowTasks', () => {
	const originalWindowOpen = window.open;

	beforeAll(() => {
		window.open = jest.fn();
	});

	afterAll(() => {
		window.open = originalWindowOpen;
	});

	const defaultProps = {
		id: 'myWorkflowTasksSection',
		myRolesWorkflowTasksURL: 'http://www.test.com/myRolesWorkflowTasks',
		myWorkflowTasksURL:
			'http://www.test.com/myWorkflowTasks?p_p_id=com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet',
		objectDefinitions: [],
	};

	it('renders correctly', () => {
		render(<ViewWorkflowTasks {...defaultProps} />);

		expect(screen.getByText('my-workflow-tasks')).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'assigned-to-me'})
		).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: 'assigned-to-me'}));

		expect(
			screen.getByRole('menuitem', {name: 'assigned-to-my-roles'})
		).toBeInTheDocument();

		expect(screen.getByLabelText('open-x')).toBeInTheDocument();
	});

	it('opens expected link after picking "Assigned to My Roles"', () => {
		render(<ViewWorkflowTasks {...defaultProps} />);

		fireEvent.click(screen.getByLabelText('open-x'));

		expect(window.open).toHaveBeenCalledWith(
			defaultProps.myWorkflowTasksURL,
			'_blank'
		);

		fireEvent.click(screen.getByRole('button', {name: 'assigned-to-me'}));

		expect(
			screen.getByRole('menuitem', {name: 'assigned-to-my-roles'})
		).toBeInTheDocument();

		fireEvent.click(
			screen.getByRole('menuitem', {name: 'assigned-to-my-roles'})
		);

		fireEvent.click(screen.getByLabelText('open-x'));

		expect(window.open).toHaveBeenCalledWith(
			defaultProps.myRolesWorkflowTasksURL,
			'_blank'
		);
	});

	it('renders the pagination bar with translated labels', async () => {
		jest.spyOn(
			WorkflowService,
			'getWorkflowTasksAssignedToMe'
		).mockResolvedValue({
			items: [
				{
					assignedDate: '2026-09-30T10:00:00Z',
					assigneePerson: {id: 1, name: 'Test User'},
					auditUser: 'Test User',
					auditUserImageURL: '',
					completed: false,
					dateDue: '',
					id: '1',
					myWorkflowTasksURL: defaultProps.myWorkflowTasksURL,
					name: 'review',
					objectReviewed: {
						assetTitle: 'Test Content',
						assetType: 'Basic Web Content',
						id: 2,
					},
					workflowLogs: [],
				},
			],
			totalCount: 1,
		} as any);

		render(<ViewWorkflowTasks {...defaultProps} />);

		expect(
			await screen.findByText('showing-x-to-x-of-x-entries')
		).toBeInTheDocument();
		expect(screen.getByText('x-items')).toBeInTheDocument();
	});
});
