/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import UnscheduledTasksPanel from '../../js/components/props_transformer/views/calendar_view/components/UnscheduledTasksPanel';
import getTaskItemsActions from '../../js/utils/getTaskItemsActions';
import {ITaskObjectEntry} from '../../js/utils/types';

let mockUnscheduledTasks: ITaskObjectEntry[] = [];

jest.mock('../../js/utils/getTaskItemsActions', () => ({
	__esModule: true,
	default: jest.fn(() => []),
}));

jest.mock('@clayui/drop-down', () => ({
	ClayDropDownWithItems: ({trigger}: {trigger: React.ReactNode}) => (
		<div>{trigger}</div>
	),
}));

jest.mock('@clayui/core', () => {
	const SidePanel = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);

	SidePanel.Body = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);
	SidePanel.Header = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);
	SidePanel.Title = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);

	return {...(jest.requireActual('@clayui/core') as {}), SidePanel};
});

jest.mock('@liferay/frontend-js-state-web/react', () => ({
	useLiferayState: () => [mockUnscheduledTasks, jest.fn()],
}));

jest.mock('@liferay/object-dynamic-data-mapping-form-field-type', () => ({
	AssigneeAvatar: ({name}: {name: string}) => <span>{name}</span>,
}));

function createTask(overrides: Partial<ITaskObjectEntry> = {}) {
	return {
		assignTo: {
			name: 'Jane Doe',
			portrait: 'https://example.com/jane.png',
		},
		id: 1,
		state: {
			key: 'inProgress',
			name: 'In Progress',
		},
		title: 'Design the landing page',
		...overrides,
	} as ITaskObjectEntry;
}

describe('UnscheduledTasksPanel', () => {
	afterEach(() => {
		mockUnscheduledTasks = [];
	});

	beforeEach(() => {
		(getTaskItemsActions as jest.Mock).mockReturnValue([]);
	});

	it('filters the tasks by title as the user types', () => {
		mockUnscheduledTasks = [
			createTask({id: 1, title: 'Alpha'}),
			createTask({id: 2, title: 'Beta'}),
		];

		const {getByTestId, queryByText} = render(<UnscheduledTasksPanel />);

		fireEvent.change(getByTestId('calendarUnscheduledTasksSearch'), {
			target: {value: 'alph'},
		});

		expect(queryByText('Alpha')).toBeInTheDocument();
		expect(queryByText('Beta')).not.toBeInTheDocument();
	});

	it('orders tasks by blocked, in progress, not started, then done', () => {
		mockUnscheduledTasks = [
			createTask({
				id: 1,
				state: {key: 'done', name: 'Done'},
				title: 'DoneTask',
			}),
			createTask({
				id: 2,
				state: {key: 'notStarted', name: 'Not Started'},
				title: 'NotStartedTask',
			}),
			createTask({
				id: 3,
				state: {key: 'inProgress', name: 'In Progress'},
				title: 'InProgressTask',
			}),
			createTask({
				id: 4,
				state: {key: 'blocked', name: 'Blocked'},
				title: 'BlockedTask',
			}),
		];

		const {getAllByTestId} = render(<UnscheduledTasksPanel />);

		const titles = getAllByTestId('calendarUnscheduledTaskTitle').map(
			(element) => element.textContent
		);

		expect(titles).toEqual([
			'BlockedTask',
			'InProgressTask',
			'NotStartedTask',
			'DoneTask',
		]);
	});

	it('renders a row for each unscheduled task', () => {
		mockUnscheduledTasks = [
			createTask({id: 1, title: 'Alpha'}),
			createTask({id: 2, title: 'Beta'}),
		];

		const {getByText} = render(<UnscheduledTasksPanel />);

		expect(getByText('Alpha')).toBeInTheDocument();
		expect(getByText('Beta')).toBeInTheDocument();
	});

	it('renders an actions menu for each task', () => {
		(getTaskItemsActions as jest.Mock).mockReturnValue([
			{label: 'edit', onClick: jest.fn()},
		]);

		mockUnscheduledTasks = [
			createTask({id: 1, title: 'Alpha'}),
			createTask({id: 2, title: 'Beta'}),
		];

		const {getAllByLabelText} = render(<UnscheduledTasksPanel />);

		expect(getAllByLabelText('actions')).toHaveLength(2);
	});

	it('renders the state label for a task', () => {
		mockUnscheduledTasks = [createTask()];

		const {getByText} = render(<UnscheduledTasksPanel />);

		expect(getByText('In Progress')).toBeInTheDocument();
	});

	it('shows the empty state when the search matches no tasks', () => {
		mockUnscheduledTasks = [createTask({title: 'Alpha'})];

		const {getByTestId, getByText} = render(<UnscheduledTasksPanel />);

		fireEvent.change(getByTestId('calendarUnscheduledTasksSearch'), {
			target: {value: 'zzz'},
		});

		expect(getByText('no-results-found')).toBeInTheDocument();
	});

	it('shows the empty state when there are no unscheduled tasks', () => {
		mockUnscheduledTasks = [];

		const {getByText} = render(<UnscheduledTasksPanel />);

		expect(getByText('no-results-found')).toBeInTheDocument();
	});
});
