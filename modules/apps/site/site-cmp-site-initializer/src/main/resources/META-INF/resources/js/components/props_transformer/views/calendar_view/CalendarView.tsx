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
import UnscheduledTasksPanel from './components/UnscheduledTasksPanel';

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
	const {loadData} = useContext(FrontendDataSetContext);

	const calendarRef = useRef<FullCalendar>(null);
	const calendarViewRef = useRef<HTMLDivElement>(null);

	const calendarViews = [
		{label: Liferay.Language.get('day'), view: 'dayGridDay'},
		{label: Liferay.Language.get('week'), view: 'dayGridWeek'},
		{label: Liferay.Language.get('month'), view: 'dayGridMonth'},
	];

	const [currentView, setCurrentView] = useState('dayGridMonth');
	const [datePickerExpanded, setDatePickerExpanded] = useState(false);
	const [datePickerValue, setDatePickerValue] = useState('');
	const [fdsContainerElement, setFDSContainerElement] =
		useState<HTMLElement | null>(null);
	const [moreLinkPopover, setMoreLinkPopover] =
		useState<MoreLinkPopover | null>(null);
	const [title, setTitle] = useState('');
	const [unscheduledTasksPanelOpen, setUnscheduledTasksPanelOpen] =
		useState(false);

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

	// The panel should push the whole FDS container aside, not just the
	// calendar, so anchor it to the FDS root. FDS does not expose that
	// element, so resolve it from the DOM.
	//
	// Store it in state, not a ref: the panel reads the container in a
	// layout effect that runs before this component's effects, and a ref
	// mutation would never re-trigger it.

	useEffect(() => {
		setFDSContainerElement(
			calendarViewRef.current?.closest<HTMLElement>('.fds') ?? null
		);
	}, []);

	// Anchor to the top of the FDS container, pulling up past the CMS
	// breadcrumb and the project tab navigation above it.

	useEffect(() => {
		if (!fdsContainerElement) {
			return;
		}

		return () => {
			fdsContainerElement.classList.remove(
				'c-slideout-container',
				'c-slideout-push-end',
				'c-slideout-transition',
				'c-slideout-transition-in',
				'c-slideout-transition-out'
			);
		};
	}, [fdsContainerElement]);

	const fdsContainerRef = useMemo(
		() => ({current: fdsContainerElement}),
		[fdsContainerElement]
	);

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

	const nextLabel = {
		dayGridDay: Liferay.Language.get('next-day'),
		dayGridMonth: Liferay.Language.get('next-month'),
		dayGridWeek: Liferay.Language.get('next-week'),
	}[currentView];

	const previousLabel = {
		dayGridDay: Liferay.Language.get('previous-day'),
		dayGridMonth: Liferay.Language.get('previous-month'),
		dayGridWeek: Liferay.Language.get('previous-week'),
	}[currentView];

	return (
		<div className="lfr__calendar-view" ref={calendarViewRef}>
			<ClayLayout.Row className="lfr__calendar-view-toolbar">
				<ClayLayout.Col
					className="lfr__calendar-view-toolbar-start"
					md={3}
				>
					{!!unscheduledTasks.length && (
						<ClayButton
							aria-pressed={unscheduledTasksPanelOpen}
							displayType="warning"
							onClick={() =>
								setUnscheduledTasksPanelOpen((open) => !open)
							}
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
						aria-label={previousLabel}
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
						aria-label={nextLabel}
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

				<ClayLayout.Col
					className="lfr__calendar-view-toolbar-end"
					md={3}
				>
					<ClayButton.Group>
						{calendarViews.map(({label, view}) => (
							<ClayButton
								aria-label={label}
								aria-pressed={currentView === view}
								displayType="secondary"
								key={view}
								onClick={() =>
									calendarRef.current
										?.getApi()
										.changeView(view)
								}
								outline={currentView !== view}
								size="sm"
								title={label}
							>
								{label}
							</ClayButton>
						))}
					</ClayButton.Group>
				</ClayLayout.Col>
			</ClayLayout.Row>

			<FullCalendar
				datesSet={({view}) => {
					setCurrentView(view.type);
					setTitle(view.title);
				}}
				dayHeaderFormat={{weekday: 'long'}}
				dayMaxEvents
				eventContent={(arg) => (
					<CalendarTaskCard
						expanded={currentView !== 'dayGridMonth'}
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

			{fdsContainerElement && (
				<UnscheduledTasksPanel
					containerRef={fdsContainerRef}
					onOpenChange={setUnscheduledTasksPanelOpen}
					open={unscheduledTasksPanelOpen}
					tasks={unscheduledTasks}
				/>
			)}
		</div>
	);
}
