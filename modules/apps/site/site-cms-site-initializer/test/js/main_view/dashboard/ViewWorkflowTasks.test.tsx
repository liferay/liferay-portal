/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {serializeFDSConfig} from '@liferay/frontend-data-set-web';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import * as WorkflowService from '../../../../src/main/resources/META-INF/resources/js/common/services/WorkflowService';
import ViewWorkflowTasks from '../../../../src/main/resources/META-INF/resources/js/main_view/home/ViewWorkflowTasks';

jest.mock('@liferay/frontend-data-set-web', () => ({
	...(jest.requireActual('@liferay/frontend-data-set-web') as object),
	serializeFDSConfig: jest.fn(() => 'SERIALIZED_CONFIG'),
}));

describe('[CMS Dashboard] Components: ViewWorkflowTasks', () => {
	const defaultProps = {
		assigneeFilter: {id: 'assigneeClassPKs', value: 20164},
		fdsActionDropdownItems: [],
		id: 'myWorkflowTasksSection',
		objectDefinitions: [],
		tasksSectionFDSName: 'tasksSection',
		workflowTaskURL:
			'http://www.test.com/workflowTask?p_p_id=com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet',
	};

	beforeEach(() => {
		Liferay.ThemeDisplay.getPathFriendlyURLPublic = jest.fn(() => '/web');
		Liferay.ThemeDisplay.getUserId = jest.fn(() => '20164');
		Liferay.ThemeDisplay.getUserName = jest.fn(() => 'Test Test');
	});

	it('links View All to the pending tasks assigned to the user', () => {
		render(<ViewWorkflowTasks {...defaultProps} />);

		expect(
			screen.getByRole('heading', {name: 'my-tasks'})
		).toBeInTheDocument();
		expect(screen.getByText('view-all').closest('a')).toHaveAttribute(
			'href',
			'/web/cms/tasks?tasksSection_fdsConfig=SERIALIZED_CONFIG'
		);
		expect(serializeFDSConfig).toHaveBeenCalledWith({
			filters: [
				{
					id: 'assigneeClassPKs',
					selectedData: {
						exclude: false,
						selectedItems: [{label: 'Test Test', value: 20164}],
					},
				},
				{
					id: 'completed',
					selectedData: {
						exclude: false,
						selectedItems: [{label: 'pending', value: 'false'}],
					},
				},
			],
		});
	});

	it('requests the first 8 tasks assigned to the user', async () => {
		const getWorkflowTasksAssignedToMe = jest
			.spyOn(WorkflowService, 'getWorkflowTasksAssignedToMe')
			.mockResolvedValue({items: [], totalCount: 0});

		render(<ViewWorkflowTasks {...defaultProps} />);

		await waitFor(() =>
			expect(getWorkflowTasksAssignedToMe).toHaveBeenCalledWith({
				objectDefinitions: [],
				page: 1,
				pageSize: 8,
			})
		);
	});
});
