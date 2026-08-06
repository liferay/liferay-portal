/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {FrontendDataSetContext} from '@liferay/frontend-data-set-web';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import CalendarView from '../../js/components/props_transformer/views/calendar_view/CalendarView';
import {patchTaskById} from '../../js/utils/api';
import {displayErrorToast} from '../../js/utils/toastUtil';
import {ITask} from '../../js/utils/types';

jest.mock('@fullcalendar/daygrid', () => ({}));

jest.mock('@fullcalendar/interaction', () => ({}));

// FullCalendar cannot run under jsdom, so render only the day cell and
// event content, expose the props CalendarView feeds it so the tests can call
// the drop callbacks FullCalendar would call, and let tests report a view
// change through a button the way the real calendar reports it through
// datesSet.

const mockCalendarProps: {current: any} = {current: null};

jest.mock('@fullcalendar/react', () => {
	const React = require('react');

	return {
		__esModule: true,
		default: React.forwardRef((props: any, _ref: unknown) => {
			React.useEffect(() => {
				mockCalendarProps.current = props;
			});

			return (
				<div
					data-first-day={props.firstDay}
					data-initial-date={props.initialDate?.toISOString() ?? ''}
					data-initial-view={props.initialView}
					data-locale={props.locale}
					data-testid="fullCalendar"
				>
					<button
						onClick={() =>
							props.datesSet?.({
								view: {
									calendar: {
										getDate: () => new Date(2026, 6, 15),
									},
									currentStart: new Date(2026, 6, 13),
									title: 'Jul 13 – 19, 2026',
									type: 'dayGridWeek',
								},
							})
						}
						type="button"
					>
						Switch to week
					</button>

					<button
						onClick={() =>
							props.datesSet?.({
								view: {
									calendar: {
										getDate: () => new Date(2026, 6, 15),
									},
									currentStart: new Date(2026, 6, 1),
									title: 'July 2026',
									type: 'dayGridMonth',
								},
							})
						}
						type="button"
					>
						Switch to month
					</button>

					{props.dayCellContent?.({
						date: new Date(2026, 6, 15),
						dayNumberText: '15',
					})}

					{props.events?.map((event: any) => (
						<div key={event.id}>
							{props.eventContent?.({
								event: {extendedProps: event.extendedProps},
							})}
						</div>
					))}
				</div>
			);
		}),
	};
});

jest.mock(
	'../../js/components/props_transformer/views/calendar_view/components/CalendarTaskCard',
	() => ({
		__esModule: true,
		default: ({onTaskChanged, task}: any) => (
			<button
				onClick={() =>
					onTaskChanged({
						actions: {get: {href: '/view', method: 'GET'}},
						embedded: {
							...task,
							id: task.reportedId ?? task.id,
							title: 'Renamed',
						},
					})
				}
				type="button"
			>
				{task.title}
			</button>
		),
	})
);

jest.mock('../../js/utils/api', () => ({
	patchTaskById: jest.fn(() => Promise.resolve({error: null})),
}));

jest.mock('../../js/utils/toastUtil', () => ({
	displayDueDateSuccessToast: jest.fn(),
	displayErrorToast: jest.fn(),
}));

const NO_UPDATE_PERMISSION_MESSAGE =
	'you-do-not-have-permission-to-update-this-task';

const TASK_UPDATE_ACTION = {update: {href: '/o/cmp/tasks/1', method: 'PATCH'}};

const renderCalendarView = (
	hasAddTaskPermission: boolean,
	{
		id,
		items = [],
		loadData = jest.fn(),
		onItemsChange = jest.fn(),
	}: {
		id?: string;
		items?: ITask[];
		loadData?: Function;
		onItemsChange?: Function;
	} = {}
) =>
	render(
		<FrontendDataSetContext.Provider
			value={{id, loadData, onItemsChange} as any}
		>
			<CalendarView
				cmpProjectObjectDefinitionId={456}
				cmpProjectObjectEntryId="123"
				hasAddTaskPermission={hasAddTaskPermission}
				items={items}
				itemsActions={[]}
			/>
		</FrontendDataSetContext.Provider>
	);

function createItem(overrides: Partial<ITask['embedded']> = {}) {
	return {
		embedded: {
			dueDate: '2026-07-10T00:00:00Z',
			id: 1,
			title: 'Design the landing page',
			...overrides,
		},
		entryClassName: 'com.liferay.object.model.ObjectEntry',
	} as unknown as ITask;
}

describe('CalendarView', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(globalThis as any).Liferay.fire = jest.fn();
		(globalThis as any).Liferay.ThemeDisplay.getBCP47LanguageId = jest.fn(
			() => 'en-US'
		);
	});

	it('falls back to reloading when the changed task is not among the items', () => {
		const loadData = jest.fn();
		const onItemsChange = jest.fn();

		renderCalendarView(false, {
			items: [createItem({reportedId: 999} as any)],
			loadData,
			onItemsChange,
		});

		fireEvent.click(screen.getByText('Design the landing page'));

		expect(loadData).toHaveBeenCalled();
		expect(onItemsChange).not.toHaveBeenCalled();
	});

	it('hides the add task button without add task permission', () => {
		renderCalendarView(false);

		expect(screen.queryByLabelText('add-task')).not.toBeInTheDocument();
	});

	it('replaces a changed task in the data set instead of reloading', () => {
		const item = createItem();

		const loadData = jest.fn();
		const onItemsChange = jest.fn();

		renderCalendarView(false, {items: [item], loadData, onItemsChange});

		fireEvent.click(screen.getByText('Design the landing page'));

		expect(onItemsChange).toHaveBeenCalledWith({
			itemKey: 'embedded.id',
			items: [
				{
					...item,
					embedded: {
						...item.embedded,
						actions: {get: {href: '/view', method: 'GET'}},
						title: 'Renamed',
					},
				},
			],
		});
		expect(loadData).not.toHaveBeenCalled();
	});

	it('reschedules a task dragged in the calendar with update permission', async () => {
		const item = createItem({actions: TASK_UPDATE_ACTION});

		renderCalendarView(false, {items: [item]});

		const {eventAllow, eventDrop} = mockCalendarProps.current;

		expect(eventAllow({}, {extendedProps: {task: item.embedded}})).toBe(
			true
		);

		await eventDrop({
			event: {
				extendedProps: {task: item.embedded},
				startStr: '2026-07-18',
			},
			revert: jest.fn(),
		});

		expect(patchTaskById).toHaveBeenCalledWith({
			body: {dueDate: '2026-07-18'},
			taskId: '1',
		});

		expect(displayErrorToast).not.toHaveBeenCalled();
	});

	it('restores the calendar view and date when it remounts', () => {
		const {unmount} = renderCalendarView(false, {id: 'remount-fds'});

		fireEvent.click(screen.getByText('Switch to week'));

		unmount();

		renderCalendarView(false, {id: 'remount-fds'});

		const fullCalendar = screen.getByTestId('fullCalendar');

		expect(fullCalendar).toHaveAttribute(
			'data-initial-date',
			new Date(2026, 6, 15).toISOString()
		);
		expect(fullCalendar).toHaveAttribute(
			'data-initial-view',
			'dayGridWeek'
		);
	});

	it('restores today instead of the first of the month after a month view remount', () => {
		const {unmount} = renderCalendarView(false, {id: 'month-fds'});

		fireEvent.click(screen.getByText('Switch to month'));

		unmount();

		renderCalendarView(false, {id: 'month-fds'});

		const fullCalendar = screen.getByTestId('fullCalendar');

		expect(fullCalendar).toHaveAttribute(
			'data-initial-date',
			new Date(2026, 6, 15).toISOString()
		);
		expect(fullCalendar).toHaveAttribute(
			'data-initial-view',
			'dayGridMonth'
		);
	});

	it('sets the week grid first day and locale from a Monday-first locale', () => {
		(globalThis as any).Liferay.ThemeDisplay.getBCP47LanguageId = jest.fn(
			() => 'de-DE'
		);

		renderCalendarView(false);

		const calendar = screen.getByTestId('fullCalendar');

		expect(calendar).toHaveAttribute('data-first-day', '1');
		expect(calendar).toHaveAttribute('data-locale', 'de-DE');
	});

	it('sets the week grid first day from a Sunday-first locale', () => {
		renderCalendarView(false);

		const calendar = screen.getByTestId('fullCalendar');

		expect(calendar).toHaveAttribute('data-first-day', '0');
		expect(calendar).toHaveAttribute('data-locale', 'en-US');
	});

	it('shows the add task button with add task permission', () => {
		renderCalendarView(true);

		expect(screen.getByLabelText('add-task')).toBeInTheDocument();
	});

	it('shows the permission message when a task is dragged from the unscheduled panel', async () => {
		renderCalendarView(false, {items: [createItem({dueDate: undefined})]});

		await mockCalendarProps.current.drop({
			dateStr: '2026-07-20',
			draggedEl: {dataset: {taskId: '1'}},
		});

		expect(displayErrorToast).toHaveBeenCalledWith(
			NO_UPDATE_PERMISSION_MESSAGE
		);

		expect(patchTaskById).not.toHaveBeenCalled();
	});

	it('shows the permission message when a task is dragged in the calendar', () => {
		const item = createItem();

		renderCalendarView(false, {items: [item]});

		const {eventAllow, eventDragStop} = mockCalendarProps.current;

		eventDragStop({event: {extendedProps: {task: item.embedded}}});

		expect(displayErrorToast).toHaveBeenCalledWith(
			NO_UPDATE_PERMISSION_MESSAGE
		);

		expect(eventAllow({}, {extendedProps: {task: item.embedded}})).toBe(
			false
		);

		expect(patchTaskById).not.toHaveBeenCalled();
	});

	it('shows the permission message when the server rejects the reschedule', async () => {
		(patchTaskById as jest.Mock).mockResolvedValueOnce({
			error: 'Forbidden',
			status: 'FORBIDDEN',
		});

		const item = createItem({actions: TASK_UPDATE_ACTION});

		const onItemsChange = jest.fn();

		renderCalendarView(false, {items: [item], onItemsChange});

		await mockCalendarProps.current.eventDrop({
			event: {
				extendedProps: {task: item.embedded},
				startStr: '2026-07-18',
			},
			revert: jest.fn(),
		});

		expect(displayErrorToast).toHaveBeenCalledWith(
			NO_UPDATE_PERMISSION_MESSAGE
		);

		expect(onItemsChange).toHaveBeenLastCalledWith({
			itemKey: 'embedded.id',
			items: [item],
		});
	});

	it('starts on the month view when the data set has no stored state', () => {
		renderCalendarView(false, {id: 'fresh-fds'});

		const fullCalendar = screen.getByTestId('fullCalendar');

		expect(fullCalendar).toHaveAttribute('data-initial-date', '');
		expect(fullCalendar).toHaveAttribute(
			'data-initial-view',
			'dayGridMonth'
		);
	});
});
