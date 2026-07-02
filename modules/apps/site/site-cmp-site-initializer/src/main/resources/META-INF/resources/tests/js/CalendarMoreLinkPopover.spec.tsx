/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import CalendarMoreLinkPopover from '../../js/components/props_transformer/views/calendar_view/components/CalendarMoreLinkPopover';
import getTaskItemsActions from '../../js/utils/getTaskItemsActions';
import {ITaskObjectEntry} from '../../js/utils/types';

jest.mock('../../js/utils/getTaskItemsActions', () => ({
	__esModule: true,
	default: jest.fn(() => []),
}));

jest.mock('@clayui/drop-down', () => ({
	__esModule: true,
	ClayDropDownWithItems: ({trigger}: {trigger: React.ReactNode}) => (
		<div>{trigger}</div>
	),
	default: {
		Menu: ({children}: {children: React.ReactNode}) => (
			<div>{children}</div>
		),
	},
}));

jest.mock('@liferay/object-dynamic-data-mapping-form-field-type', () => ({
	AssigneeAvatar: ({name}: {name: string}) => <span>{name}</span>,
}));

const futureDueDate = '2026-02-10T00:00:00Z';
const mockedSystemDate = '2026-02-05T00:00:00Z';
const pastDueDate = '2026-02-04T00:00:00Z';

function createTask(overrides: Partial<ITaskObjectEntry> = {}) {
	return {
		assignTo: {
			name: 'Jane Doe',
			portrait: 'https://example.com/jane.png',
		},
		dueDate: futureDueDate,
		id: 1,
		state: {
			key: 'inProgress',
			name: 'In Progress',
		},
		title: 'Design the landing page',
		...overrides,
	} as ITaskObjectEntry;
}

function renderPopover(tasks: ITaskObjectEntry[]) {
	return render(
		<CalendarMoreLinkPopover
			alignElement={document.createElement('a')}
			itemsActions={[]}
			loadData={jest.fn()}
			onClose={jest.fn()}
			tasks={tasks}
		/>
	);
}

describe('CalendarMoreLinkPopover', () => {
	beforeAll(() => {
		jest.useFakeTimers();

		jest.setSystemTime(new Date(mockedSystemDate));
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	beforeEach(() => {
		(getTaskItemsActions as jest.Mock).mockReturnValue([]);
	});

	it('orders tasks by overdue, blocked, in progress, not started, then done', () => {
		const {getAllByTestId} = renderPopover([
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
			createTask({
				dueDate: pastDueDate,
				id: 5,
				state: {key: 'inProgress', name: 'In Progress'},
				title: 'OverdueTask',
			}),
		]);

		const titles = getAllByTestId('calendarMoreLinkPopoverTaskTitle').map(
			(element) => element.textContent
		);

		expect(titles).toEqual([
			'OverdueTask',
			'BlockedTask',
			'InProgressTask',
			'NotStartedTask',
			'DoneTask',
		]);
	});

	it('renders an actions menu for each task', () => {
		(getTaskItemsActions as jest.Mock).mockReturnValue([
			{label: 'edit', onClick: jest.fn()},
		]);

		const {getAllByLabelText} = renderPopover([
			createTask({id: 1, title: 'Alpha'}),
			createTask({id: 2, title: 'Beta'}),
		]);

		expect(getAllByLabelText('actions')).toHaveLength(2);
	});

	it('renders every task for the day', () => {
		const {getByText} = renderPopover([
			createTask({id: 1, title: 'Alpha'}),
			createTask({id: 2, title: 'Beta'}),
		]);

		expect(getByText('Alpha')).toBeInTheDocument();
		expect(getByText('Beta')).toBeInTheDocument();
	});

	it('renders the state label for a task', () => {
		const {getByText} = renderPopover([
			createTask({state: {key: 'inProgress', name: 'In Progress'}}),
		]);

		expect(getByText('In Progress')).toBeInTheDocument();
	});

	it('shows only the overdue label for an overdue task', () => {
		const {getByText, queryByText} = renderPopover([
			createTask({
				dueDate: pastDueDate,
				state: {key: 'inProgress', name: 'In Progress'},
			}),
		]);

		expect(getByText('overdue')).toBeInTheDocument();
		expect(queryByText('In Progress')).not.toBeInTheDocument();
	});
});
