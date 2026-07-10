/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import Task from '../../js/components/props_transformer/views/kanban_view/components/Task';
import {KanbanViewContext} from '../../js/components/props_transformer/views/kanban_view/context';
import {mockNavigate} from '../../tests/js/__mocks__/frontend-js-web';

const mockGetUserAccount = jest.fn();
const mockPatchTaskById = jest.fn();
const mockDeleteTaskById = jest.fn();
const mockDisplayAssignSuccessToast = jest.fn();
const mockDisplayDeleteSuccessToast = jest.fn();
const mockDisplayErrorToast = jest.fn();
const mockDisplayRequestSuccessToast = jest.fn();
const mockLoadData = jest.fn();
const mockPostSubscribeTaskByExternalReferenceCode = jest.fn();
const mockPostUnsubscribeTaskByExternalReferenceCode = jest.fn();

jest.mock('react-dnd', () => ({
	useDrag: () => [{isDragging: false}, jest.fn()],
}));

jest.mock('@clayui/drop-down', () => ({
	ClayDropDownWithItems: ({items}: any) => (
		<div>
			{items.map((item: any, index: number) =>
				item.type === 'divider' ? null : (
					<button key={index} onClick={item.onClick}>
						{item.label}
					</button>
				)
			)}
		</div>
	),
}));

jest.mock('@liferay/frontend-data-set-web', () => ({
	...((jest.requireActual('@liferay/frontend-data-set-web') ?? {}) as any),
	DateRenderer: ({value}: {value: string}) => (
		<span>Formatted Date: {value}</span>
	),
}));

jest.mock('../../js/utils/api', () => ({
	deleteTaskById: (...args: any[]) => mockDeleteTaskById(...args),
	getUserAccount: (...args: any[]) => mockGetUserAccount(...args),
	patchTaskById: (...args: any[]) => mockPatchTaskById(...args),
	postSubscribeTaskByExternalReferenceCode: (...args: any[]) =>
		mockPostSubscribeTaskByExternalReferenceCode(...args),
	postUnsubscribeTaskByExternalReferenceCode: (...args: any[]) =>
		mockPostUnsubscribeTaskByExternalReferenceCode(...args),
}));

const mockOpenCMPModal = jest.fn();

jest.mock('../../js/utils/openCMPModal', () => ({
	openCMPModal: (...args: any[]) => mockOpenCMPModal(...args),
}));

jest.mock('../../js/utils/toastUtil', () => ({
	displayAssignSuccessToast: (...args: any[]) =>
		mockDisplayAssignSuccessToast(...args),
	displayDeleteSuccessToast: (...args: any[]) =>
		mockDisplayDeleteSuccessToast(...args),
}));

jest.mock('@liferay/site-cms-site-initializer', () => ({
	displayErrorToast: (...args: any[]) => mockDisplayErrorToast(...args),
	displayRequestSuccessToast: (...args: any[]) =>
		mockDisplayRequestSuccessToast(...args),
}));

afterEach(() => {
	jest.clearAllMocks();
});

describe('Kanban Task', () => {
	const task = {
		actions: {
			assignToMe: true,
			delete: true,
			get: true,
			subscribe: true,
			update: true,
		},
		embedded: {
			assignTo: {name: 'Alice', portrait: 'p.jpg'},
			cmpProjectToCMPTasks: {title: 'Project A'},
			externalReferenceCode: 'erc-1',
			id: 42,
			scopeKey: 1,
			state: {key: 'in-progress', name: 'In Progress'},
			title: 'Task title',
		},
	} as any;

	const renderTask = (itemsActions: any[] = [], projectId = '') =>
		render(
			<KanbanViewContext.Provider
				value={{
					boardData: {},
					changeTaskStatus: jest.fn(),
					hasAddTaskPermission: true,
					itemsActions,
					loadData: mockLoadData,
					projectId,
					projectObjectDefinitionId: 123,
				}}
			>
				<Task {...task} />
			</KanbanViewContext.Provider>
		);

	it('assigns task to current user successfully', async () => {
		mockGetUserAccount.mockResolvedValue({
			externalReferenceCode: 'u1',
			name: 'Current User',
		});
		mockPatchTaskById.mockResolvedValue({error: null});

		const {getByText} = renderTask();

		fireEvent.click(getByText('assign-to-me'));

		await waitFor(() => {
			expect(mockPatchTaskById).toHaveBeenCalled();
			expect(mockLoadData).toHaveBeenCalled();
			expect(mockDisplayAssignSuccessToast).toHaveBeenCalledWith(
				'Task title',
				'Current User'
			);
		});
	});

	it('hides other items actions when task only has view permissions', () => {
		const taskWithOnlyViewPermission = {
			...task,
			actions: {
				assignToMe: false,
				delete: false,
				get: true,
				subscribe: false,
				update: false,
			},
		};

		const {queryByText} = render(
			<KanbanViewContext.Provider
				value={{itemsActions: [], loadData: mockLoadData} as any}
			>
				<Task {...taskWithOnlyViewPermission} />
			</KanbanViewContext.Provider>
		);

		expect(queryByText('view')).toBeInTheDocument();

		expect(queryByText('assign-to-...')).not.toBeInTheDocument();
		expect(queryByText('delete')).not.toBeInTheDocument();
		expect(queryByText('edit')).not.toBeInTheDocument();
		expect(queryByText('watch-task')).not.toBeInTheDocument();
	});

	it('navigates when edit and view actions are clicked', async () => {
		const itemsActions = [
			{items: [], type: 'group'},
			{
				items: [
					{data: {id: 'edit'}, href: '/edit/{embedded.id}'},
					{data: {id: 'actionLink'}, href: '/view/{embedded.id}'},
				],
				type: 'group',
			},
		];

		const {getByText} = renderTask(itemsActions);

		fireEvent.click(getByText('edit'));
		expect(mockNavigate).toHaveBeenCalledWith('/edit/42');

		fireEvent.click(getByText('view'));
		expect(mockNavigate).toHaveBeenCalledWith('/view/42');
	});

	it('opens assign-to modal', () => {
		const {getByText} = renderTask();

		fireEvent.click(getByText('assign-to-...'));

		expect(mockOpenCMPModal).toHaveBeenCalledTimes(1);
	});

	it('opens delete modal', () => {
		const {getByText} = renderTask();

		fireEvent.click(getByText('delete'));

		expect(mockOpenCMPModal).toHaveBeenCalledTimes(1);
	});

	it('renders due date when projectId is provided', () => {
		const taskWithDueDate = {
			...task,
			embedded: {
				...task.embedded,
				dueDate: '2023-12-25T14:00:00Z',
			},
		};

		const {getByText, queryByText} = render(
			<KanbanViewContext.Provider
				value={{
					boardData: {},
					changeTaskStatus: jest.fn(),
					hasAddTaskPermission: true,
					itemsActions: [],
					loadData: mockLoadData,
					projectId: '123',
					projectObjectDefinitionId: 123,
				}}
			>
				<Task {...taskWithDueDate} />
			</KanbanViewContext.Provider>
		);

		expect(
			getByText('Formatted Date: 2023-12-25T14:00:00Z')
		).toBeInTheDocument();
		expect(queryByText('Project A')).not.toBeInTheDocument();
	});

	it('renders project title when projectId is not provided', () => {
		const {getByText} = renderTask();

		expect(getByText('Task title')).toBeInTheDocument();
		expect(getByText('Project A')).toBeInTheDocument();
		expect(getByText('In Progress')).toBeInTheDocument();
	});

	it('shows all items actions when task has full permissions', () => {
		const {queryByText} = renderTask();

		expect(queryByText('assign-to-...')).toBeInTheDocument();
		expect(queryByText('delete')).toBeInTheDocument();
		expect(queryByText('edit')).toBeInTheDocument();
		expect(queryByText('view')).toBeInTheDocument();
		expect(queryByText('watch-task')).toBeInTheDocument();
	});

	it('shows error toast when assign-to-me fails', async () => {
		mockGetUserAccount.mockResolvedValue({
			externalReferenceCode: 'u1',
			name: 'Current User',
		});
		mockPatchTaskById.mockResolvedValue({error: 'error'});

		const {getByText} = renderTask();

		fireEvent.click(getByText('assign-to-me'));

		await waitFor(() => {
			expect(mockDisplayErrorToast).toHaveBeenCalledWith('error');
		});
	});

	describe('stop-watching-task', () => {
		const taskWithSubscription = {
			...task,
			actions: {
				subscribe: false,
				unsubscribe: true,
			},
		};

		it('stops watching a task successfully', async () => {
			mockPostUnsubscribeTaskByExternalReferenceCode.mockResolvedValue({
				error: null,
			});

			const {getByText} = render(
				<KanbanViewContext.Provider
					value={{
						boardData: {},
						changeTaskStatus: jest.fn(),
						hasAddTaskPermission: true,
						itemsActions: [],
						loadData: mockLoadData,
						projectId: '',
						projectObjectDefinitionId: 123,
					}}
				>
					<Task {...taskWithSubscription} />
				</KanbanViewContext.Provider>
			);

			fireEvent.click(getByText('stop-watching-task'));

			await waitFor(() => {
				expect(
					mockPostUnsubscribeTaskByExternalReferenceCode
				).toHaveBeenCalledWith({
					externalReferenceCode: 'erc-1',
					scopeKey: 1,
				});
				expect(mockLoadData).toHaveBeenCalled();
				expect(mockDisplayRequestSuccessToast).toHaveBeenCalled();
			});
		});

		it('shows an error toast when stop watching task fails', async () => {
			mockPostUnsubscribeTaskByExternalReferenceCode.mockResolvedValue({
				error: 'error',
			});

			const {getByText} = render(
				<KanbanViewContext.Provider
					value={{
						boardData: {},
						changeTaskStatus: jest.fn(),
						hasAddTaskPermission: true,
						itemsActions: [],
						loadData: mockLoadData,
						projectId: '',
						projectObjectDefinitionId: 123,
					}}
				>
					<Task {...taskWithSubscription} />
				</KanbanViewContext.Provider>
			);

			fireEvent.click(getByText('stop-watching-task'));

			await waitFor(() => {
				expect(mockDisplayErrorToast).toHaveBeenCalledWith('error');
			});
		});
	});

	describe('watch-task', () => {
		it('watches a task successfully', async () => {
			mockPostSubscribeTaskByExternalReferenceCode.mockResolvedValue({
				error: null,
			});

			const {getByText} = renderTask();

			fireEvent.click(getByText('watch-task'));

			await waitFor(() => {
				expect(
					mockPostSubscribeTaskByExternalReferenceCode
				).toHaveBeenCalledWith({
					externalReferenceCode: 'erc-1',
					scopeKey: 1,
				});
				expect(mockLoadData).toHaveBeenCalled();
				expect(mockDisplayRequestSuccessToast).toHaveBeenCalled();
			});
		});

		it('shows an error toast when watch task fails', async () => {
			mockPostSubscribeTaskByExternalReferenceCode.mockResolvedValue({
				error: 'error',
			});

			const {getByText} = renderTask();

			fireEvent.click(getByText('watch-task'));

			await waitFor(() => {
				expect(mockDisplayErrorToast).toHaveBeenCalledWith('error');
			});
		});
	});
});
