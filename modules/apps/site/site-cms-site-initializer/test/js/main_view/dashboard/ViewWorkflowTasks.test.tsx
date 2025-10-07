/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

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
		myWorkflowTasksURL: 'http://www.test.com/myWorkflowTasks',
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
});
