/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {Draggable} from '@fullcalendar/interaction';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import UnscheduledTasksPanel from '../../js/components/props_transformer/views/calendar_view/components/UnscheduledTasksPanel';
import getActionURL from '../../js/utils/getActionURL';
import getTaskItemsActions from '../../js/utils/getTaskItemsActions';
import {ITaskObjectEntry} from '../../js/utils/types';

jest.mock('../../js/utils/getActionURL', () => ({
	__esModule: true,
	default: jest.fn(() => '/view/1'),
}));

jest.mock('../../js/utils/getTaskItemsActions', () => ({
	__esModule: true,
	default: jest.fn(() => []),
}));

jest.mock('@clayui/drop-down', () => ({
	ClayDropDownWithItems: ({trigger}: {trigger: React.ReactNode}) => (
		<div>{trigger}</div>
	),
}));

jest.mock('@fullcalendar/interaction', () => ({
	Draggable: jest.fn(() => ({
		destroy: jest.fn(),
		dragging: {emitter: {off: jest.fn(), on: jest.fn()}},
	})),
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

function renderUnscheduledTasksPanel(
	tasks: ITaskObjectEntry[],
	containerElement: HTMLElement | null = null
) {
	return render(
		<UnscheduledTasksPanel
			containerRef={{current: containerElement}}
			onOpenChange={jest.fn()}
			open
			tasks={tasks}
		/>
	);
}

describe('UnscheduledTasksPanel', () => {
	beforeEach(() => {
		(getTaskItemsActions as jest.Mock).mockReturnValue([]);
		(getActionURL as jest.Mock).mockReturnValue('/view/1');
	});

	it('filters the tasks by title as the user types', () => {
		const {getByTestId, queryByText} = renderUnscheduledTasksPanel([
			createTask({id: 1, title: 'Alpha'}),
			createTask({id: 2, title: 'Beta'}),
		]);

		fireEvent.change(getByTestId('calendarUnscheduledTasksSearch'), {
			target: {value: 'alph'},
		});

		expect(queryByText('Alpha')).toBeInTheDocument();
		expect(queryByText('Beta')).not.toBeInTheDocument();
	});

	it('orders tasks by blocked, in progress, not started, then done', () => {
		const {getAllByTestId} = renderUnscheduledTasksPanel([
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
		]);

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

	it('paginates the tasks into pages of 20 by default', () => {
		const {getAllByTestId, getByLabelText} = renderUnscheduledTasksPanel(
			Array.from({length: 25}, (_, index) =>
				createTask({id: index + 1, title: `Task ${index + 1}`})
			)
		);

		expect(getAllByTestId('calendarUnscheduledTaskTitle')).toHaveLength(20);

		fireEvent.click(getByLabelText('Go to page, 2'));

		expect(getAllByTestId('calendarUnscheduledTaskTitle')).toHaveLength(5);
	});

	it('registers the task rows as draggable into the calendar', () => {
		(Draggable as jest.Mock).mockClear();

		const containerElement = document.createElement('div');

		const {unmount} = renderUnscheduledTasksPanel(
			[createTask()],
			containerElement
		);

		expect(Draggable).toHaveBeenCalledWith(containerElement, {
			eventData: {create: false},
			itemSelector: '.lfr__cmp-unscheduled-tasks-panel-item',
		});

		unmount();

		const draggableInstance = (Draggable as jest.Mock).mock.results[0]
			.value;

		expect(draggableInstance.destroy).toHaveBeenCalled();
	});

	it('renders a row for each unscheduled task', () => {
		const {getByText} = renderUnscheduledTasksPanel([
			createTask({id: 1, title: 'Alpha'}),
			createTask({id: 2, title: 'Beta'}),
		]);

		expect(getByText('Alpha')).toBeInTheDocument();
		expect(getByText('Beta')).toBeInTheDocument();
	});

	it('renders an actions menu for each task', () => {
		(getTaskItemsActions as jest.Mock).mockReturnValue([
			{label: 'edit', onClick: jest.fn()},
		]);

		const {getAllByLabelText} = renderUnscheduledTasksPanel([
			createTask({id: 1, title: 'Alpha'}),
			createTask({id: 2, title: 'Beta'}),
		]);

		expect(getAllByLabelText('actions')).toHaveLength(2);
	});

	it('renders the state label for a task', () => {
		const {getByText} = renderUnscheduledTasksPanel([createTask()]);

		expect(getByText('In Progress')).toBeInTheDocument();
	});

	it('renders the task title as a link to the view page when the user can view it', () => {
		const {getByRole} = renderUnscheduledTasksPanel([
			createTask({actions: {get: {href: '/view', method: 'GET'}}}),
		]);

		expect(
			getByRole('link', {name: 'Design the landing page'})
		).toHaveAttribute('href', '/view/1');
	});

	it('renders the task title as plain text when the user cannot view it', () => {
		const {queryByRole} = renderUnscheduledTasksPanel([createTask()]);

		expect(queryByRole('link')).not.toBeInTheDocument();
	});

	it('resets to the first page when the search query changes', () => {
		const {getByLabelText, getByTestId, getByText} =
			renderUnscheduledTasksPanel(
				Array.from({length: 25}, (_, index) =>
					createTask({id: index + 1, title: `Task ${index + 1}`})
				)
			);

		fireEvent.click(getByLabelText('Go to page, 2'));

		expect(getByText('Task 21')).toBeInTheDocument();

		fireEvent.change(getByTestId('calendarUnscheduledTasksSearch'), {
			target: {value: 'task'},
		});

		expect(getByText('Task 1')).toBeInTheDocument();
	});

	it('shows the empty state when the search matches no tasks', () => {
		const {getByTestId, getByText} = renderUnscheduledTasksPanel([
			createTask({title: 'Alpha'}),
		]);

		fireEvent.change(getByTestId('calendarUnscheduledTasksSearch'), {
			target: {value: 'zzz'},
		});

		expect(getByText('no-results-found')).toBeInTheDocument();
	});

	it('shows the empty state when there are no unscheduled tasks', () => {
		const {getByText, queryByText} = renderUnscheduledTasksPanel([]);

		expect(getByText('no-unscheduled-tasks')).toBeInTheDocument();
		expect(queryByText('clear-search')).not.toBeInTheDocument();
	});
});
