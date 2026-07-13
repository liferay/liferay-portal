/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import CalendarTaskCard from '../../js/components/props_transformer/views/calendar_view/components/CalendarTaskCard';
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

function renderCard(task: ITaskObjectEntry, props: {expanded?: boolean} = {}) {
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
});
