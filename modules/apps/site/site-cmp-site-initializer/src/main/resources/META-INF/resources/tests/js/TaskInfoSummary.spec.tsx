/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import TaskInfoSummary from '../../js/components/task/TaskInfoSummary';
import createMockFetchAssigneeContext from '../js/__mocks__/createMockFetchAssigneeContext';

const mockStates = [
	{key: 'notStarted', name: 'Not Started', nextStates: ['inProgress']},
	{key: 'inProgress', name: 'In Progress', nextStates: ['notStarted']},
];

const mockAssignTo = {
	externalReferenceCode: '123',
	id: 123,
	name: 'Assignee Name',
	type: 'user',
};

describe('TaskInfoSummary', () => {
	beforeEach(() => {
		global.fetch = createMockFetchAssigneeContext();
	});

	it('renders with props', () => {
		const {container} = render(
			<TaskInfoSummary
				assignTo={mockAssignTo}
				dueDate="2023-12-31"
				hasUpdatePermission
				initialState="notStarted"
				states={mockStates}
				tags={['tag1', 'tag2']}
				taskId="123"
				title="Task"
			/>
		);

		expect(container).toBeInTheDocument();
	});

	it('disables the state selector and makes the assignee read only when the user lacks update permission', () => {
		render(
			<TaskInfoSummary
				assignTo={mockAssignTo}
				dueDate="2023-12-31"
				hasUpdatePermission={false}
				initialState="notStarted"
				states={mockStates}
				tags={['tag1', 'tag2']}
				taskId="123"
				title="Task"
			/>
		);

		expect(screen.getByRole('combobox', {name: 'assignee'})).toBeDisabled();

		const stateSelector = screen
			.getAllByRole('combobox')
			.find((element) =>
				element.classList.contains('lfr-cmp__state-selector')
			);

		expect(stateSelector).toHaveClass('disabled');
	});
});
