/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {IItemsActions} from '@liferay/frontend-data-set-web';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import CalendarMoreLinkPopover from '../../js/components/props_transformer/views/calendar_view/components/CalendarMoreLinkPopover';
import {ITaskObjectEntry} from '../../js/utils/types';
import {mockNavigate} from './__mocks__/frontend-js-web';

jest.mock('@liferay/object-dynamic-data-mapping-form-field-type', () => ({
	AssigneeAvatar: ({name}: {name: string}) => <span>{name}</span>,
}));

const futureDueDate = '2026-02-10T00:00:00Z';
const mockedSystemDate = '2026-02-05T00:00:00Z';
const pastDueDate = '2026-02-04T00:00:00Z';

const taskActions = {
	delete: {href: '/delete', method: 'DELETE'},
	get: {href: '/view', method: 'GET'},
	update: {href: '/edit', method: 'GET'},
};

const viewTaskItemsActions = [
	{data: {id: 'actionLink'}, href: '/o/tasks/1'},
] as IItemsActions[];

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

function renderPopover(
	tasks: ITaskObjectEntry[],
	itemsActions: IItemsActions[] = [],
	{
		alignElement = document.createElement('a'),
		onClose = jest.fn(),
	}: {alignElement?: HTMLElement; onClose?: () => void} = {}
) {
	return render(
		<CalendarMoreLinkPopover
			alignElement={alignElement}
			itemsActions={itemsActions}
			onClose={onClose}
			tasks={tasks}
		/>
	);
}

const appendedElements: HTMLElement[] = [];

/**
 * Appends a focusable element to the body so the document order around the
 * "more" link can be asserted. Everything the popover renders lands in a
 * portal at the end of the body, after whatever is appended here.
 */
function appendFocusableElement(tagName: 'a' | 'button') {
	const element = document.createElement(tagName);

	element.tabIndex = 0;

	document.body.appendChild(element);

	appendedElements.push(element);

	return element;
}

/**
 * jsdom has no layout engine and always reports offsetParent as null, which
 * Clay reads as "hidden" when it collects the focusable items of a menu.
 * Report the parent element instead so the focus moves as it does in a
 * browser.
 */
function mockOffsetParent() {
	Object.defineProperty(HTMLElement.prototype, 'offsetParent', {
		configurable: true,
		get() {
			return this.parentElement;
		},
	});
}

describe('CalendarMoreLinkPopover', () => {
	const offsetParentDescriptor = Object.getOwnPropertyDescriptor(
		HTMLElement.prototype,
		'offsetParent'
	);

	beforeAll(() => {
		jest.useFakeTimers();

		jest.setSystemTime(new Date(mockedSystemDate));

		mockOffsetParent();
	});

	afterAll(() => {
		jest.useRealTimers();

		if (offsetParentDescriptor) {
			Object.defineProperty(
				HTMLElement.prototype,
				'offsetParent',
				offsetParentDescriptor
			);
		}
	});

	beforeEach(() => {
		mockNavigate.mockClear();
	});

	afterEach(() => {
		appendedElements.forEach((element) => element.remove());

		appendedElements.length = 0;
	});

	it('closes and returns the focus to the more link on shift tab', () => {
		const alignElement = appendFocusableElement('a');
		const onClose = jest.fn();

		const {getByRole} = renderPopover(
			[createTask({actions: taskActions, title: 'Alpha'})],
			viewTaskItemsActions,
			{alignElement, onClose}
		);

		fireEvent.keyDown(getByRole('menuitem'), {
			key: 'Tab',
			shiftKey: true,
		});

		expect(alignElement).toHaveFocus();
		expect(onClose).toHaveBeenCalled();
	});

	it('closes and skips past the more link on tab', () => {
		const alignElement = appendFocusableElement('a');
		const nextElement = appendFocusableElement('button');
		const onClose = jest.fn();

		const {getByRole} = renderPopover(
			[createTask({actions: taskActions, title: 'Alpha'})],
			viewTaskItemsActions,
			{alignElement, onClose}
		);

		fireEvent.keyDown(getByRole('menuitem'), {key: 'Tab'});

		expect(nextElement).toHaveFocus();
		expect(onClose).toHaveBeenCalled();
	});

	it('does not nest an actions menu inside a task', () => {
		const {queryByLabelText} = renderPopover([
			createTask({actions: taskActions}),
		]);

		expect(queryByLabelText('actions')).not.toBeInTheDocument();
	});

	it('does not view a task the user has no permission to view', () => {
		const {getByText} = renderPopover(
			[createTask({title: 'Alpha'})],
			viewTaskItemsActions
		);

		fireEvent.click(getByText('Alpha'));

		expect(mockNavigate).not.toHaveBeenCalled();
	});

	it('focuses the first task when it opens', () => {
		const {getAllByRole} = renderPopover(
			[
				createTask({actions: taskActions, id: 1, title: 'Alpha'}),
				createTask({actions: taskActions, id: 2, title: 'Beta'}),
			],
			viewTaskItemsActions
		);

		expect(getAllByRole('menuitem')[0]).toHaveFocus();
	});

	it('moves the focus between tasks with the arrow keys', () => {
		const {getAllByRole} = renderPopover(
			[
				createTask({actions: taskActions, id: 1, title: 'Alpha'}),
				createTask({actions: taskActions, id: 2, title: 'Beta'}),
			],
			viewTaskItemsActions
		);

		const [firstTask, secondTask] = getAllByRole('menuitem');

		fireEvent.keyDown(firstTask!, {key: 'ArrowDown'});

		expect(secondTask).toHaveFocus();

		fireEvent.keyDown(secondTask!, {key: 'ArrowUp'});

		expect(firstTask).toHaveFocus();
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

	it('views the task when it is clicked', () => {
		const {getByText} = renderPopover(
			[createTask({actions: taskActions, title: 'Alpha'})],
			viewTaskItemsActions
		);

		fireEvent.click(getByText('Alpha'));

		expect(mockNavigate).toHaveBeenCalledWith('/o/tasks/1');
	});
});
