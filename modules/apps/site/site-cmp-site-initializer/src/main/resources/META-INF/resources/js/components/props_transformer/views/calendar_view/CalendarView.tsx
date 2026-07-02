/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDatePicker from '@clayui/date-picker';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import dayGridPlugin from '@fullcalendar/daygrid';
import FullCalendar from '@fullcalendar/react';
import {
	FrontendDataSetContext,
	IItemsActions,
} from '@liferay/frontend-data-set-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import classNames from 'classnames';
import {dateUtils, sub} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useRef, useState} from 'react';

import {DEFAULT_TASK_STATE_KEY} from '../../../../utils/constants';
import {openCMPModal} from '../../../../utils/openCMPModal';
import {ITask, ITaskObjectEntry} from '../../../../utils/types';
import CreateTaskModal from '../../../modal/CreateTaskModal';
import {UPDATE_TASKS_QUICK_FILTER_VISIBILITY} from '../../../task/TasksQuickFilters';
import CalendarMoreLinkPopover from './components/CalendarMoreLinkPopover';
import CalendarTaskCard from './components/CalendarTaskCard';
import {unscheduledTasksAtom} from './utils/unscheduledTasksAtom';

import './CalendarView.scss';

import type {FirstDayOfWeekLocale} from 'frontend-js-web';

interface CalendarViewProps {
	items: ITask[];
	itemsActions: IItemsActions[];
	projectId?: string;
	projectObjectDefinitionId: number;
}

interface MoreLinkPopover {
	alignElement: HTMLElement;
	date: Date;
	tasks: ITaskObjectEntry[];
}

export default function CalendarView({
	items,
	itemsActions,
	projectId,
	projectObjectDefinitionId,
}: CalendarViewProps) {
	const {loadData, onInfoPanelToggleButtonClick} = useContext(
		FrontendDataSetContext
	);

	const calendarRef = useRef<FullCalendar>(null);
	const calendarViewRef = useRef<HTMLDivElement>(null);

	const [datePickerExpanded, setDatePickerExpanded] = useState(false);
	const [datePickerValue, setDatePickerValue] = useState('');
	const [moreLinkPopover, setMoreLinkPopover] =
		useState<MoreLinkPopover | null>(null);
	const [title, setTitle] = useState('');

	const [, setUnscheduledTasks] =
		useLiferayState<ITaskObjectEntry[]>(unscheduledTasksAtom);

	const events = useMemo(
		() =>
			items
				.filter((item) => item.embedded?.dueDate)
				.map((item) => ({
					allDay: true,

					// Attach the full task entry to the event so the custom
					// renderers (eventContent and the "more" popover) can read
					// it back through event.extendedProps.

					extendedProps: {task: item.embedded},
					id: String(item.embedded.id),
					start: item.embedded.dueDate.slice(0, 10),
					title: item.embedded.title,
				})),
		[items]
	);

	const unscheduledTasks = useMemo(
		() =>
			items
				.filter((item) => !item.embedded?.dueDate)
				.map((item) => item.embedded)
				.filter(Boolean),
		[items]
	);

	// Share the unscheduled tasks with the info panel component, since the FDS
	// core provides the info panel with only the currently selected items.

	useEffect(() => {
		setUnscheduledTasks(unscheduledTasks);
	}, [setUnscheduledTasks, unscheduledTasks]);

	useEffect(() => {
		Liferay.fire(UPDATE_TASKS_QUICK_FILTER_VISIBILITY, {visible: false});

		return () => {
			Liferay.fire(UPDATE_TASKS_QUICK_FILTER_VISIBILITY, {visible: true});
		};
	}, []);

	// Properly resize the calendar width when the unscheduled tasks panel is
	// opened or closed. FullCalendar caches its layout and only recomputes on a
	// window resize, so its width can go stale. Watch the container instead and
	// recompute on every width change.

	useEffect(() => {
		const element = calendarViewRef.current;

		if (!element) {
			return;
		}

		const resizeObserver = new ResizeObserver(() => {
			requestAnimationFrame(() => {
				calendarRef.current?.getApi().updateSize();
			});
		});

		resizeObserver.observe(element);

		return () => resizeObserver.disconnect();
	}, []);

	const openCreateTaskModal = (dueDate: string) => {
		openCMPModal({
			center: true,
			contentComponent: ({closeModal}: {closeModal: () => void}) => (
				<CreateTaskModal
					closeModal={closeModal}
					dueDate={dueDate}
					loadData={loadData}
					projectId={projectId}
					projectObjectDefinitionId={projectObjectDefinitionId}
					state={DEFAULT_TASK_STATE_KEY}
				/>
			),
			size: 'md',
		});
	};

	const currentYear = new Date().getFullYear();
	const locale = Liferay.ThemeDisplay.getBCP47LanguageId();

	return (
		<div className="lfr__calendar-view" ref={calendarViewRef}>
			<ClayLayout.Row className="lfr__calendar-view-toolbar">
				<ClayLayout.Col
					className="lfr__calendar-view-toolbar-start"
					md={3}
				>
					{!!unscheduledTasks.length && (
						<ClayButton
							displayType="warning"
							onClick={() => onInfoPanelToggleButtonClick()}
							outline
							size="sm"
						>
							<span className="inline-item inline-item-before">
								<ClayIcon symbol="warning-full" />
							</span>

							{sub(
								unscheduledTasks.length === 1
									? Liferay.Language.get('x-unscheduled-task')
									: Liferay.Language.get(
											'x-unscheduled-tasks'
										),
								[unscheduledTasks.length]
							)}
						</ClayButton>
					)}
				</ClayLayout.Col>

				<ClayLayout.Col
					className="lfr__calendar-view-toolbar-center"
					md={6}
				>
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('previous-month')}
						borderless
						displayType="secondary"
						onClick={() => calendarRef.current?.getApi().prev()}
						size="sm"
						symbol="angle-left"
					/>

					<div className="lfr__calendar-view-toolbar-date-picker">
						<ClayButton
							aria-expanded={datePickerExpanded}
							aria-haspopup="dialog"
							borderless
							className={classNames(
								'lfr__calendar-view-toolbar-title',
								{
									active: datePickerExpanded,
								}
							)}
							data-testid="calendarTitle"
							displayType="secondary"
							onClick={() =>
								setDatePickerExpanded((expanded) => !expanded)
							}
						>
							{title}

							<span className="inline-item inline-item-after">
								<ClayIcon symbol="caret-bottom" />
							</span>
						</ClayButton>

						{/* "inert" is spread because React 18.2 lacks JSX support for it (added in 18.3) and the build's DOM types omit the property. */}

						<div {...{inert: ''}}>
							<ClayDatePicker
								ariaLabels={{
									buttonChooseDate:
										Liferay.Language.get('select-date'),
									buttonDot: Liferay.Language.get(
										'select-current-date'
									),
									buttonNextMonth:
										Liferay.Language.get(
											'select-next-month'
										),
									buttonPreviousMonth: Liferay.Language.get(
										'select-previous-month'
									),
									dialog: Liferay.Language.get('select-date'),
									selectMonth:
										Liferay.Language.get('select-a-month'),
									selectYear:
										Liferay.Language.get('select-a-year'),
								}}
								dateFormat="yyyy-MM-dd"
								expanded={datePickerExpanded}
								firstDayOfWeek={dateUtils.getFirstDayOfWeek(
									locale as FirstDayOfWeekLocale
								)}
								months={dateUtils.getMonthsLong(locale)}
								onChange={(value) => {
									setDatePickerValue(value);

									if (value) {
										calendarRef.current
											?.getApi()
											.gotoDate(value);

										setDatePickerExpanded(false);
									}
								}}
								onExpandedChange={setDatePickerExpanded}
								value={datePickerValue}
								weekdaysShort={dateUtils.getWeekdaysShort(
									locale
								)}
								years={{
									end: currentYear + 10,
									start: currentYear - 10,
								}}
							/>
						</div>
					</div>

					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('next-month')}
						borderless
						displayType="secondary"
						onClick={() => calendarRef.current?.getApi().next()}
						symbol="angle-right"
					/>

					<ClayButton
						displayType="secondary"
						onClick={() => calendarRef.current?.getApi().today()}
						size="sm"
					>
						{Liferay.Language.get('today')}
					</ClayButton>
				</ClayLayout.Col>

				{/* Reserved for future toolbar actions; keeping the column
				    balances the start column so the center stays centered. */}

				<ClayLayout.Col
					className="lfr__calendar-view-toolbar-end"
					md={3}
				/>
			</ClayLayout.Row>

			<FullCalendar
				datesSet={({view}) => setTitle(view.title)}
				dayHeaderFormat={{weekday: 'long'}}
				dayMaxEvents
				eventContent={(arg) => (
					<CalendarTaskCard
						itemsActions={itemsActions}
						loadData={loadData}
						task={arg.event.extendedProps.task}
					/>
				)}
				events={events}
				fixedWeekCount={false}
				headerToolbar={false}
				initialView="dayGridMonth"
				moreLinkClassNames={[
					'btn',
					'btn-outline-secondary',
					'btn-outline-borderless',
				]}
				moreLinkClick={(arg) => {
					setMoreLinkPopover({
						alignElement: arg.jsEvent.currentTarget as HTMLElement,
						date: arg.date,
						tasks: arg.allSegs.map(
							(seg) => seg.event.extendedProps.task
						),
					});

					// Prevent FullCalendar's built-in popover from opening.
					// It stays closed only when the handler returns a truthy
					// value other than "popover". The return type is
					// "string | void", which rejects a boolean, so "true" is
					// force-cast to void for the compiler; at runtime the
					// value is still true.

					return true as unknown as void;
				}}
				moreLinkContent={(arg) => (
					<>
						{`${arg.num} ${Liferay.Language.get('more')}`}

						<span className="inline-item inline-item-after">
							<ClayIcon symbol="caret-bottom" />
						</span>
					</>
				)}
				moreLinkHint={Liferay.Language.get('view-all-tasks')}
				plugins={[dayGridPlugin]}
				ref={calendarRef}
				{...(Liferay.FeatureFlags['LPD-69885'] && {
					dayCellContent: (arg) => (
						<>
							{arg.dayNumberText}

							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('add-task')}
								borderless
								className="lfr__calendar-view-add-task-button"
								displayType="secondary"
								onClick={() =>
									openCreateTaskModal(
										dateUtils.format(arg.date, 'yyyy-MM-dd')
									)
								}
								rounded
								size="xs"
								symbol="plus"
								title={Liferay.Language.get('add-task')}
							/>
						</>
					),
				})}
			/>

			{moreLinkPopover && (
				<CalendarMoreLinkPopover
					alignElement={moreLinkPopover.alignElement}
					itemsActions={itemsActions}
					loadData={loadData}
					onClose={() => setMoreLinkPopover(null)}
					tasks={moreLinkPopover.tasks}
				/>
			)}
		</div>
	);
}
