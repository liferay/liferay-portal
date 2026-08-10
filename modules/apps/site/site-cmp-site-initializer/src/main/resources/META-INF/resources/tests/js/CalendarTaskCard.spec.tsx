/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import CalendarTaskCard from '../../js/components/props_transformer/views/calendar_view/components/CalendarTaskCard';
import {
	getTaskById,
	getUserAccount,
	patchTaskById,
	postSubscribeTaskByExternalReferenceCode,
	postUnsubscribeTaskByExternalReferenceCode,
} from '../../js/utils/api';
import {ITaskObjectEntry} from '../../js/utils/types';
import {mockNavigate} from './__mocks__/frontend-js-web';

jest.mock('@liferay/object-dynamic-data-mapping-form-field-type', () => ({
	AssigneeAvatar: ({name, portrait}: {name: string; portrait: string}) => (
		<img alt={name} src={portrait} />
	),
}));

jest.mock('@liferay/site-cms-site-initializer', () => ({
	displayErrorToast: jest.fn(),
	displayRequestSuccessToast: jest.fn(),
}));

jest.mock('../../js/utils/api', () => ({
	deleteTaskById: jest.fn(),
	getTaskById: jest.fn(),
	getUserAccount: jest.fn(),
	patchTaskById: jest.fn(),
	postSubscribeTaskByExternalReferenceCode: jest.fn(),
	postUnsubscribeTaskByExternalReferenceCode: jest.fn(),
}));

jest.mock('../../js/utils/openCMPModal', () => ({
	openCMPModal: jest.fn(),
}));

jest.mock('../../js/utils/toastUtil', () => ({
	displayAssignSuccessToast: jest.fn(),
	displayDeleteSuccessToast: jest.fn(),
}));

const futureDueDate = '2026-02-10T00:00:00Z';
const mockedSystemDate = '2026-02-05T00:00:00Z';
const pastDueDate = '2026-02-04T00:00:00Z';

const taskActions = {
	assignToMe: {href: '/assign-to-me', method: 'GET'},
	delete: {href: '/delete', method: 'DELETE'},
	get: {href: '/view', method: 'GET'},
	subscribe: {href: '/subscribe', method: 'POST'},
	update: {href: '/edit', method: 'GET'},
};

function createTask(overrides: Partial<ITaskObjectEntry> = {}) {
	return {
		assignTo: {
			name: 'Jane Doe',
			portrait: 'https://example.com/jane.png',
		},
		dueDate: futureDueDate,
		state: {
			key: 'inProgress',
			name: 'In Progress',
		},
		title: 'Design the landing page',
		...overrides,
	} as ITaskObjectEntry;
}

function renderCard(
	task: ITaskObjectEntry,
	props: Partial<React.ComponentProps<typeof CalendarTaskCard>> = {}
) {
	return render(
		<CalendarTaskCard loadData={jest.fn()} task={task} {...props} />
	);
}

describe('CalendarTaskCard', () => {
	beforeAll(() => {
		jest.useFakeTimers();

		jest.setSystemTime(new Date(mockedSystemDate));
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	beforeEach(() => {
		mockNavigate.mockClear();
	});

	it('does not render the actions menu when the task has no available actions', () => {
		const {queryByLabelText} = renderCard(createTask());

		expect(queryByLabelText('actions')).not.toBeInTheDocument();
	});

	it('does not render the blocked icon when the task is not blocked', () => {
		const {container} = renderCard(createTask());

		expect(
			container.querySelector('.lexicon-icon-block')
		).not.toBeInTheDocument();
	});

	it('does not render the overdue icon when the state is done', () => {
		const {container} = renderCard(
			createTask({
				dueDate: pastDueDate,
				state: {key: 'done', name: 'Done'},
			})
		);

		expect(
			container.querySelector('.lexicon-icon-exclamation-full')
		).not.toBeInTheDocument();
	});

	it('does not view the task when the actions kebab is clicked', () => {
		const {getByLabelText} = renderCard(createTask({actions: taskActions}));

		fireEvent.click(getByLabelText('actions'));

		expect(mockNavigate).not.toHaveBeenCalled();
	});

	it('renders the actions menu when the task has available actions', () => {
		const {getByLabelText} = renderCard(createTask({actions: taskActions}));

		expect(getByLabelText('actions')).toBeInTheDocument();
	});

	it('renders the assignee avatar', () => {
		const {getByAltText} = renderCard(createTask());

		const avatar = getByAltText('Jane Doe');

		expect(avatar).toBeInTheDocument();
		expect(avatar).toHaveAttribute('src', 'https://example.com/jane.png');
	});

	it('renders the blocked icon when the task is blocked', () => {
		const {container} = renderCard(
			createTask({state: {key: 'blocked', name: 'Blocked'}})
		);

		expect(
			container.querySelector('.lexicon-icon-block')
		).toBeInTheDocument();
	});

	it('renders the overdue icon when the due date is past and the state is not done', () => {
		const {container} = renderCard(createTask({dueDate: pastDueDate}));

		expect(
			container.querySelector('.lexicon-icon-exclamation-full')
		).toBeInTheDocument();
	});

	it('renders the overdue label in the expanded card when the due date is past', () => {
		const {getByText} = renderCard(createTask({dueDate: pastDueDate}), {
			expanded: true,
		});

		expect(getByText('overdue')).toBeInTheDocument();
	});

	it('renders the state label in the expanded card', () => {
		const {getByText} = renderCard(createTask(), {expanded: true});

		expect(getByText('In Progress')).toBeInTheDocument();
	});

	it('renders the task title', () => {
		const {getByText} = renderCard(createTask());

		expect(getByText('Design the landing page')).toBeInTheDocument();
	});

	it('shows the overdue icon instead of the blocked icon when a blocked task is also overdue', () => {
		const {container} = renderCard(
			createTask({
				dueDate: pastDueDate,
				state: {key: 'blocked', name: 'Blocked'},
			})
		);

		expect(
			container.querySelector('.lexicon-icon-exclamation-full')
		).toBeInTheDocument();
		expect(
			container.querySelector('.lexicon-icon-block')
		).not.toBeInTheDocument();
	});

	it('shows the task actions when the kebab is opened', () => {
		const {getByLabelText, getByText} = renderCard(
			createTask({actions: taskActions})
		);

		fireEvent.click(getByLabelText('actions'));

		expect(getByText('assign-to-...')).toBeInTheDocument();
		expect(getByText('assign-to-me')).toBeInTheDocument();
		expect(getByText('delete')).toBeInTheDocument();
		expect(getByText('edit')).toBeInTheDocument();
		expect(getByText('view')).toBeInTheDocument();
		expect(getByText('watch-task')).toBeInTheDocument();
	});

	describe('quick actions with in-place task updates', () => {
		const task = createTask({
			actions: taskActions,
			externalReferenceCode: 'TASK-1',
			id: 1,
			scopeKey: 'scope-1',
		} as Partial<ITaskObjectEntry>);

		beforeEach(() => {
			jest.clearAllMocks();
		});

		it('clears the due date when the updated task comes back without one', async () => {
			(getUserAccount as jest.Mock).mockResolvedValue({
				externalReferenceCode: 'USER-1',
				name: 'John Doe',
			});

			const taskWithDueDate = createTask({
				actions: taskActions,
				dueDate: futureDueDate,
				id: 1,
			} as Partial<ITaskObjectEntry>);

			const updatedTaskWithoutDueDate: Partial<ITaskObjectEntry> = {
				...taskWithDueDate,
				assignTo: {
					externalReferenceCode: 'USER-1',
					id: 1,
					name: 'John Doe',
					type: 'User',
				},
			};

			delete updatedTaskWithoutDueDate.dueDate;

			(patchTaskById as jest.Mock).mockResolvedValue({
				data: updatedTaskWithoutDueDate,
				error: null,
			});

			const onTaskChanged = jest.fn();

			const {getByLabelText, getByText} = renderCard(taskWithDueDate, {
				loadData: jest.fn(),
				onTaskChanged,
			});

			fireEvent.click(getByLabelText('actions'));
			fireEvent.click(getByText('assign-to-me'));

			await waitFor(() => {
				expect(onTaskChanged).toHaveBeenCalledWith({
					actions: taskActions,
					embedded: updatedTaskWithoutDueDate,
				});
			});

			expect(onTaskChanged.mock.calls[0][0].embedded).not.toHaveProperty(
				'dueDate'
			);
		});

		it('keeps the task untouched when watching fails', async () => {
			(
				postSubscribeTaskByExternalReferenceCode as jest.Mock
			).mockResolvedValue({error: 'error'});

			const loadData = jest.fn();
			const onTaskChanged = jest.fn();

			const {getByLabelText, getByText} = renderCard(task, {
				loadData,
				onTaskChanged,
			});

			fireEvent.click(getByLabelText('actions'));
			fireEvent.click(getByText('watch-task'));

			await waitFor(() => {
				expect(
					postSubscribeTaskByExternalReferenceCode
				).toHaveBeenCalled();
			});

			expect(loadData).not.toHaveBeenCalled();
			expect(onTaskChanged).not.toHaveBeenCalled();
		});

		it('refreshes the task after stop watching instead of reloading', async () => {
			(
				postUnsubscribeTaskByExternalReferenceCode as jest.Mock
			).mockResolvedValue({error: null});

			const watchedTask = createTask({
				...task,
				actions: {
					assignToMe: taskActions.assignToMe,
					delete: taskActions.delete,
					get: taskActions.get,
					unsubscribe: {href: '/unsubscribe', method: 'POST'},
					update: taskActions.update,
				},
			} as Partial<ITaskObjectEntry>);

			const refreshedTask = {...watchedTask, actions: taskActions};

			(getTaskById as jest.Mock).mockResolvedValue({
				data: refreshedTask,
				error: null,
			});

			const loadData = jest.fn();
			const onTaskChanged = jest.fn();

			const {getByLabelText, getByText} = renderCard(watchedTask, {
				loadData,
				onTaskChanged,
			});

			fireEvent.click(getByLabelText('actions'));
			fireEvent.click(getByText('stop-watching-task'));

			await waitFor(() => {
				expect(onTaskChanged).toHaveBeenCalledWith({
					actions: taskActions,
					embedded: refreshedTask,
				});
			});

			expect(getTaskById).toHaveBeenCalledWith({taskId: '1'});
			expect(loadData).not.toHaveBeenCalled();
		});

		it('refreshes the task after watching instead of reloading', async () => {
			(
				postSubscribeTaskByExternalReferenceCode as jest.Mock
			).mockResolvedValue({error: null});

			const refreshedTaskActions = {
				assignToMe: taskActions.assignToMe,
				delete: taskActions.delete,
				get: taskActions.get,
				unsubscribe: {href: '/unsubscribe', method: 'POST'},
				update: taskActions.update,
			};

			const refreshedTask = {...task, actions: refreshedTaskActions};

			(getTaskById as jest.Mock).mockResolvedValue({
				data: refreshedTask,
				error: null,
			});

			const loadData = jest.fn();
			const onTaskChanged = jest.fn();

			const {getByLabelText, getByText} = renderCard(task, {
				loadData,
				onTaskChanged,
			});

			fireEvent.click(getByLabelText('actions'));
			fireEvent.click(getByText('watch-task'));

			await waitFor(() => {
				expect(onTaskChanged).toHaveBeenCalledWith({
					actions: refreshedTaskActions,
					embedded: refreshedTask,
				});
			});

			expect(getTaskById).toHaveBeenCalledWith({taskId: '1'});
			expect(loadData).not.toHaveBeenCalled();
		});

		it('reloads after watching when in-place updates are unavailable', async () => {
			(
				postSubscribeTaskByExternalReferenceCode as jest.Mock
			).mockResolvedValue({error: null});

			const loadData = jest.fn();

			const {getByLabelText, getByText} = renderCard(task, {loadData});

			fireEvent.click(getByLabelText('actions'));
			fireEvent.click(getByText('watch-task'));

			await waitFor(() => {
				expect(loadData).toHaveBeenCalled();
			});

			expect(getTaskById).not.toHaveBeenCalled();
		});

		it('reloads after watching when the task refetch returns no data', async () => {
			(
				postSubscribeTaskByExternalReferenceCode as jest.Mock
			).mockResolvedValue({error: null});

			(getTaskById as jest.Mock).mockResolvedValue({
				data: null,
				error: 'error',
			});

			const loadData = jest.fn();
			const onTaskChanged = jest.fn();

			const {getByLabelText, getByText} = renderCard(task, {
				loadData,
				onTaskChanged,
			});

			fireEvent.click(getByLabelText('actions'));
			fireEvent.click(getByText('watch-task'));

			await waitFor(() => {
				expect(loadData).toHaveBeenCalled();
			});

			expect(onTaskChanged).not.toHaveBeenCalled();
		});
	});
});
