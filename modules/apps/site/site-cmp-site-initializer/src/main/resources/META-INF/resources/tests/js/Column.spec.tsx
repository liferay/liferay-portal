/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import {ColumnView} from '../../js/components/props_transformer/views/kanban_view/components/Column';
import {KanbanViewContext} from '../../js/components/props_transformer/views/kanban_view/context';

let capturedDropSpec: any;
const mockOpenCMPModal = jest.fn();
const mockStateFlow = {
	objectStates: [
		{
			key: 'inProgress',
			objectStateTransitions: [{key: 'done'}],
		},
		{
			key: 'done',
			objectStateTransitions: [],
		},
	],
};

jest.mock('react-dnd', () => ({
	DndProvider: ({children}: any) => <>{children}</>,
	useDrag: () => [{}, jest.fn()],
	useDrop: (spec: any) => {
		capturedDropSpec = spec;

		return [{}, jest.fn()];
	},
}));

jest.mock('../../js/utils/openCMPModal', () => ({
	openCMPModal: (...args: any[]) => mockOpenCMPModal(...args),
}));

jest.mock(
	'../../js/components/props_transformer/views/kanban_view/components/Task',
	() => () => <div data-testid="task" />
);

const renderColumnView = (props: any, contextProps: any = {}) =>
	render(
		<KanbanViewContext.Provider
			value={{
				boardData: {},
				changeTaskStatus: jest.fn(),
				hasAddTaskPermission: true,
				loadData: () => {},
				...contextProps,
			}}
		>
			<ColumnView {...props} />
		</KanbanViewContext.Provider>
	);

describe('Kanban ColumnView', () => {
	const defaultColumn = {
		displayType: 'info',
		icon: {color: '#fff', name: 'star'},
		key: 'inProgress',
		name: 'In Progress',
		tasks: [{embedded: {id: 1}}],
	};

	it('canDrop returns true for valid transition + update permission', () => {
		const column = {
			...defaultColumn,
			key: 'done',
			name: 'Done',
			tasks: [],
		};

		renderColumnView({
			column,
			stateFlow: mockStateFlow,
		});

		const task = {
			actions: {update: true},
			embedded: {
				id: 1,
				state: {key: 'inProgress'},
			},
		};

		expect(capturedDropSpec.canDrop({task})).toBe(true);
	});

	it('canDrop returns false for invalid transition', () => {
		const column = {
			...defaultColumn,
			key: 'notStarted',
			name: 'Not Started',
			tasks: [],
		};

		renderColumnView({
			column,
			stateFlow: mockStateFlow,
		});

		const task = {
			actions: {update: true},
			embedded: {
				id: 1,
				state: {key: 'inProgress'},
			},
		};

		expect(capturedDropSpec.canDrop({task})).toBe(false);
	});

	it('canDrop returns false when update is not allowed', () => {
		const column = {
			...defaultColumn,
			key: 'done',
			name: 'Done',
			tasks: [],
		};

		renderColumnView({
			column,
			stateFlow: mockStateFlow,
		});

		const task = {
			actions: {update: false},
			embedded: {
				id: 1,
				state: {key: 'inProgress'},
			},
		};

		expect(capturedDropSpec.canDrop({task})).toBe(false);
	});

	it('calls changeTaskStatus on drop', () => {
		const mockChangeTaskStatus = jest.fn();

		const column = {
			...defaultColumn,
			key: 'done',
			name: 'Done',
			tasks: [],
		};

		renderColumnView(
			{
				column,
				stateFlow: mockStateFlow,
			},
			{changeTaskStatus: mockChangeTaskStatus}
		);

		const task = {
			actions: {update: true},
			embedded: {
				id: 1,
				state: {key: 'inProgress'},
			},
		};

		capturedDropSpec.drop({task});

		expect(mockChangeTaskStatus).toHaveBeenCalledWith(task, {
			key: 'done',
			name: 'Done',
		});
	});

	it('hides the add task button without add task permission', () => {
		const {queryByText} = renderColumnView(
			{
				column: defaultColumn,
				stateFlow: mockStateFlow,
			},
			{hasAddTaskPermission: false}
		);

		expect(queryByText('add-task')).not.toBeInTheDocument();
	});

	it('renders header and tasks', () => {
		const {getAllByTestId, getByText} = renderColumnView({
			column: defaultColumn,
			stateFlow: mockStateFlow,
		});

		expect(getByText('In Progress')).toBeInTheDocument();
		expect(getAllByTestId('task').length).toBe(1);
	});

	it('shows the add task button with add task permission', () => {
		const {getByText} = renderColumnView({
			column: defaultColumn,
			stateFlow: mockStateFlow,
		});

		expect(getByText('add-task')).toBeInTheDocument();
	});
});
