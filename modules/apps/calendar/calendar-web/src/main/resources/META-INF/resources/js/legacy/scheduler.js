/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-scheduler',
	(A) => {
		const AArray = A.Array;
		const DateMath = A.DataType.DateMath;
		const Lang = A.Lang;

		const RecurrenceUtil = Liferay.RecurrenceUtil;

		const isBoolean = Lang.isBoolean;
		const isDate = Lang.isDate;
		const isFunction = Lang.isFunction;
		const isObject = Lang.isObject;
		const isValue = Lang.isValue;

		const timeZone = Liferay.ThemeDisplay.getTimeZone();

		const CONTROLS_NODE = 'controlsNode';

		const DAYS_OF_WEEK = ['SU', 'MO', 'TU', 'WE', 'TH', 'FR', 'SA'];

		const ICON_ADD_EVENT_NODE = 'iconAddEventNode';

		const STR_BLANK = '';

		const TPL_ICON_ADD_EVENT_NODE =
			'<div class="btn-group">' +
			'<button class="btn btn-primary calendar-add-event-btn" type="button">' +
			Liferay.Language.get('add-event') +
			'</button>' +
			'</div>';

		const TPL_SCHEDULER_ICON_NEXT =
			'<button aria-label="{ariaLabel}"" role="button" type="button" class="btn btn-secondary scheduler-base-icon-next">' +
			Liferay.Util.getLexiconIconTpl('angle-right') +
			'</button>';

		const TPL_SCHEDULER_ICON_PREV =
			'<button aria-label="{ariaLabel}"" role="button" type="button" class="btn btn-secondary scheduler-base-icon-prev">' +
			Liferay.Util.getLexiconIconTpl('angle-left') +
			'</button>';

		const TPL_SCHEDULER_TODAY =
			'<button aria-label="{ariaLabel}"" role="button" type="button" class="btn btn-secondary scheduler-base-today">' +
			Liferay.Language.get('today') +
			'</button>';

		const TPL_SCHEDULER_VIEWS =
			'<div aria-label="{ariaLabel}" class="col-xs-5 form-inline scheduler-base-views" role="listbox">' +
			'</div>';

		const WEEKLY = 'WEEKLY';

		const Time = Liferay.Time;

		A.mix(A.DataType.DateMath, {
			getWeeksInMonth(date, firstDayOfWeek) {
				const daysInMonth = DateMath.getDaysInMonth(
					date.getFullYear(),
					date.getMonth()
				);
				let firstWeekDay =
					DateMath.getDate(
						date.getFullYear(),
						date.getMonth(),
						1
					).getDay() - firstDayOfWeek;

				if (firstWeekDay < 0) {
					firstWeekDay = firstWeekDay + 7;
				}

				const daysInFirstWeek = DateMath.WEEK_LENGTH - firstWeekDay;

				return (
					Math.ceil(
						(daysInMonth - daysInFirstWeek) / DateMath.WEEK_LENGTH
					) + 1
				);
			},
		});

		const CalendarUtil = Liferay.CalendarUtil;

		const Scheduler = A.Component.create({
			ATTRS: {
				calendarContainer: {
					validator: isObject,
					value: null,
				},

				currentTime: {
					validator: isDate,
					value: new Date(),
				},

				eventsPerPage: {},

				filterCalendarBookings: {
					validator: isFunction,
				},

				iconAddEventNode: {
					valueFn() {
						return A.Node.create(TPL_ICON_ADD_EVENT_NODE);
					},
				},

				iconNextNode: {
					valueFn() {
						const instance = this;

						return A.Node.create(
							A.Lang.sub(TPL_SCHEDULER_ICON_NEXT, {
								ariaLabel: instance.getAriaLabel('next'),
							})
						);
					},
				},

				iconPrevNode: {
					valueFn() {
						const instance = this;

						return A.Node.create(
							A.Lang.sub(TPL_SCHEDULER_ICON_PREV, {
								ariaLabel: instance.getAriaLabel('previous'),
							})
						);
					},
				},

				maxDaysDisplayed: {},

				portletNamespace: {
					setter: String,
					validator: isValue,
					value: STR_BLANK,
				},

				preventPersistence: {
					validator: isBoolean,
					value: false,
				},

				remoteServices: {
					validator: isObject,
					value: null,
				},

				showAddEventBtn: {
					validator: isBoolean,
					value: true,
				},

				todayNode: {
					valueFn() {
						const instance = this;

						return A.Node.create(
							A.Lang.sub(
								this._processTemplate(TPL_SCHEDULER_TODAY),
								{
									ariaLabel: instance.getAriaLabel('today'),
								}
							)
						);
					},
				},

				viewsNode: {
					valueFn() {
						const instance = this;

						return A.Node.create(
							A.Lang.sub(TPL_SCHEDULER_VIEWS, {
								ariaLabel: instance.getAriaLabel('calendar'),
							})
						);
					},
				},
			},

			AUGMENTS: [Liferay.RecurrenceConverter],

			EXTENDS: A.Scheduler,

			NAME: 'scheduler-base',

			prototype: {
				_afterActiveViewChange(event) {
					const instance = this;

					instance.resetEvents();

					Scheduler.superclass._afterActiveViewChange.apply(
						this,
						arguments
					);

					Liferay.Util.Session.set(
						'com.liferay.calendar.web_defaultView',
						event.newVal.get('name')
					);

					instance.load();
				},

				_afterDateChange() {
					const instance = this;

					instance.load();
				},

				_afterSchedulerEventChange(event) {
					const instance = this;

					if (!instance.get('preventPersistence')) {
						const changedAttributes = event.changed;

						const persistentAttrMap = {
							calendarId: 1,
							color: 1,
							content: 1,
							endDate: 1,
							endTime: 1,
							startDate: 1,
							startTime: 1,
						};

						let persist = true;

						A.each(changedAttributes, (_item, index) => {
							persist = Object.prototype.hasOwnProperty.call(
								persistentAttrMap,
								index
							);
						});

						if (persist) {
							const schedulerEvent = event.target;

							instance._updateSchedulerEvent(
								schedulerEvent,
								changedAttributes
							);
						}
					}
				},

				_bindCurrentTimeInterval() {
					const instance = this;

					instance._currentTimeInterval = setInterval(
						A.bind(instance._updateCurrentTime, instance),
						60000
					);
				},

				_createViewTriggerNode(view) {
					const node =
						Scheduler.superclass._createViewTriggerNode.apply(
							this,
							arguments
						);

					let schedulerViewText = '';

					const viewName = view.get('name');

					if (viewName === 'agenda') {
						schedulerViewText = Liferay.Language.get('agenda-view');
					}
					else if (viewName === 'day') {
						schedulerViewText = Liferay.Language.get('day-view');
					}
					else if (viewName === 'month') {
						schedulerViewText = Liferay.Language.get('month-view');
					}
					else if (viewName === 'week') {
						schedulerViewText = Liferay.Language.get('week-view');
					}

					if (node.get('nodeName') === 'OPTION') {
						node.text(schedulerViewText);
					}

					return node;
				},

				_getCalendarBookingDuration(schedulerEvent) {
					const duration =
						schedulerEvent.getSecondsDuration() * Time.SECOND;

					return duration;
				},

				_getCalendarBookingOffset(schedulerEvent, changedAttributes) {
					let offset = 0;

					if (changedAttributes.startDate) {
						offset =
							changedAttributes.startDate.newVal.getTime() -
							changedAttributes.startDate.prevVal.getTime();
					}

					return offset;
				},

				_getNewRecurrence(schedulerEvent, changedAttributes) {
					const instance = this;

					const recurrence = instance.parseRecurrence(
						schedulerEvent.get('recurrence')
					);

					if (
						recurrence &&
						changedAttributes.startDate &&
						changedAttributes.endDate
					) {
						const rrule = recurrence.rrule;

						const newDate = changedAttributes.startDate.newVal;

						const prevDate = changedAttributes.startDate.prevVal;

						if (DateMath.isDayOverlap(prevDate, newDate)) {
							if (rrule.freq === WEEKLY) {
								const index = rrule.byday.indexOf(
									DAYS_OF_WEEK[prevDate.getDay()]
								);

								AArray.remove(rrule.byday, index);

								rrule.byday.push(
									DAYS_OF_WEEK[newDate.getDay()]
								);
							}
							else if (rrule.byday) {
								let position = Math.ceil(
									newDate.getDate() / DateMath.WEEK_LENGTH
								);

								const futureDate = DateMath.add(
									newDate,
									DateMath.WEEK,
									1
								);

								const lastDayOfWeek =
									futureDate.getMonth() !==
									newDate.getMonth();

								if (
									position > 4 ||
									(lastDayOfWeek &&
										rrule.byday.position === -1)
								) {
									position = -1;
								}

								rrule.byday.position = position;
								rrule.byday.dayOfWeek =
									DAYS_OF_WEEK[newDate.getDay()];

								if (rrule.bymonth) {
									rrule.bymonth = newDate.getMonth() + 1;
								}
							}
						}
					}

					return recurrence;
				},

				_onClickAddEvent() {
					const instance = this;

					const recorder = instance.get('eventRecorder');

					const activeViewName = instance
						.get('activeView')
						.get('name');

					const calendarContainer = instance.get('calendarContainer');

					const defaultCalendar =
						calendarContainer.get('defaultCalendar');

					const calendarId = defaultCalendar.get('calendarId');

					const editCalendarBookingURL = decodeURIComponent(
						recorder.get('editCalendarBookingURL')
					);

					const startTimeDate = instance.get('date');

					const data = {
						activeView: activeViewName,
						calendarId,
						startTimeDay: startTimeDate.getDate(),
						startTimeMonth: startTimeDate.getMonth(),
						startTimeYear: startTimeDate.getFullYear(),
						titleCurrentValue: '',
					};

					Liferay.Util.openModal({
						containerProps: {},
						iframeBodyCssClass: '',
						onClose: function destroy() {
							instance.load();
						},
						title: Liferay.Language.get('new-calendar-booking'),
						url: CalendarUtil.fillURLParameters(
							editCalendarBookingURL,
							data
						),
					});
				},

				_onDeleteEvent(event) {
					const instance = this;

					const schedulerEvent = event.schedulerEvent;

					const remoteServices = instance.get('remoteServices');

					const success = function () {
						instance.load();
						instance.get('eventRecorder').hidePopover();
					};

					if (schedulerEvent.isRecurring()) {
						RecurrenceUtil.openConfirmationPanel(
							'delete',
							() => {
								remoteServices.deleteEventInstance(
									schedulerEvent,
									false,
									success
								);
							},
							() => {
								remoteServices.deleteEventInstance(
									schedulerEvent,
									true,
									success
								);
							},
							() => {
								remoteServices.deleteEvent(
									schedulerEvent,
									success
								);
							}
						);
					}
					else if (schedulerEvent.isMasterBooking()) {
						let confirmationMessage;

						if (schedulerEvent.get('hasChildCalendarBookings')) {
							confirmationMessage = Liferay.Language.get(
								'deleting-this-event-will-cancel-the-meeting-with-your-guests-would-you-like-to-delete'
							);
						}
						else {
							confirmationMessage = Liferay.Language.get(
								'would-you-like-to-delete-this-event'
							);
						}

						Liferay.Util.openConfirmModal({
							message: confirmationMessage,
							onConfirm: (isConfirmed) => {
								if (isConfirmed) {
									remoteServices.deleteEvent(
										schedulerEvent,
										success
									);
								}
							},
						});
					}

					event.preventDefault();
				},

				_onLoadSchedulerEvents(event) {
					const instance = this;

					instance.plotCalendarBookings(event.parsed);
				},

				_onSaveEvent(event) {
					const instance = this;

					const remoteServices = instance.get('remoteServices');

					remoteServices.updateEvent(
						event.newSchedulerEvent,
						false,
						false,
						() => {
							instance.load();
							instance.get('eventRecorder').hidePopover();
						}
					);
				},

				_queueableQuestionResolver(data) {
					const instance = this;

					const answers = data.answers;
					const newRecurrence = data.newRecurrence;
					const schedulerEvent = data.schedulerEvent;

					const showNextQuestion = A.bind(instance.load, instance);

					if (
						newRecurrence &&
						(!answers.updateInstance || answers.allFollowing)
					) {
						schedulerEvent.set(
							'recurrence',
							instance.encodeRecurrence(newRecurrence)
						);
					}

					if (answers.cancel) {
						A.soon(showNextQuestion);
					}
					else {
						const remoteServices = instance.get('remoteServices');

						remoteServices.updateEvent(
							schedulerEvent,
							!!answers.updateInstance,
							!!answers.allFollowing,
							showNextQuestion
						);
					}
				},

				_updateCurrentTime() {
					const instance = this;

					const currentTimeFn = instance.get('currentTimeFn');

					currentTimeFn((time) => {
						instance.set('currentTime', time);
					});
				},

				_updatePastEvents(event) {
					const instance = this;

					const currentTime = event.newVal;

					const pastSchedulerEvents = instance.getEvents(
						(schedulerEvent) => {
							const endDate = schedulerEvent.get('endDate');

							return endDate.getTime() <= currentTime;
						},
						false
					);

					A.each(pastSchedulerEvents, (schedulerEvent) => {
						return schedulerEvent._uiSetPast(true);
					});
				},

				_updateSchedulerEvent(schedulerEvent, changedAttributes) {
					const instance = this;

					const calendarContainer = instance.get('calendarContainer');

					const calendar = calendarContainer.getCalendar(
						schedulerEvent.get('calendarId')
					);

					Liferay.CalendarMessageUtil.promptSchedulerEventUpdate({
						calendarName: calendar.get('name'),
						duration:
							instance._getCalendarBookingDuration(
								schedulerEvent
							),
						hasChild: schedulerEvent.get(
							'hasChildCalendarBookings'
						),
						masterBooking: schedulerEvent.isMasterBooking(),
						newRecurrence: instance._getNewRecurrence(
							schedulerEvent,
							changedAttributes
						),
						offset: instance._getCalendarBookingOffset(
							schedulerEvent,
							changedAttributes
						),
						recurring: schedulerEvent.isRecurring(),
						resolver: A.bind(
							instance._queueableQuestionResolver,
							instance
						),
						schedulerEvent,
					});
				},

				bindUI() {
					const instance = this;

					instance.after({
						'scheduler-base:dateChange': instance._afterDateChange,
						'scheduler-event:change':
							instance._afterSchedulerEventChange,
					});

					instance.on({
						'*:load': instance._onLoadSchedulerEvents,
						'scheduler-event-recorder:delete':
							instance._onDeleteEvent,
						'scheduler-event-recorder:save': instance._onSaveEvent,
					});

					instance._bindCurrentTimeInterval();

					instance.on(
						'currentTimeChange',
						instance._updatePastEvents
					);

					Scheduler.superclass.bindUI.apply(this, arguments);
				},

				calendarModel: Liferay.SchedulerCalendar,

				destructor() {
					const instance = this;

					clearInterval(instance._currentTimeInterval);

					instance.get('views').forEach((item) => {
						item.destroy();
					});

					Scheduler.superclass.destructor.apply(instance, arguments);
				},

				eventModel: Liferay.SchedulerEvent,
				eventsModel: Liferay.SchedulerEvents,

				getEventsByCalendarBookingId(calendarBookingId) {
					const instance = this;

					return instance.getEvents((schedulerEvent) => {
						return (
							schedulerEvent.get('calendarBookingId') ===
							calendarBookingId
						);
					});
				},

				load() {
					const instance = this;

					const events = instance._events;

					return events.load.apply(events, arguments);
				},

				plotCalendarBookings(calendarBookings) {
					const instance = this;

					const calendarEvents = {};
					const events = [];

					calendarBookings.forEach((item) => {
						const calendarId = item.calendarId;

						if (!calendarEvents[calendarId]) {
							calendarEvents[calendarId] = [];
						}

						const schedulerEvent =
							CalendarUtil.createSchedulerEvent(item);

						schedulerEvent.set('scheduler', instance, {
							silent: true,
						});

						events.push(schedulerEvent);
						calendarEvents[calendarId].push(schedulerEvent);
					});

					instance.resetEvents(events);

					const calendarContainer = instance.get('calendarContainer');

					A.each(
						calendarContainer.get('availableCalendars'),
						(item, index) => {
							item.reset(calendarEvents[index], {
								skipSyncUI: true,
							});
						}
					);

					if (instance.get('rendered')) {
						instance.syncEventsUI();
					}
				},

				queue: null,

				renderButtonGroup() {
					const instance = this;

					Scheduler.superclass.renderButtonGroup.apply(
						this,
						arguments
					);

					instance.viewsNode.setAttribute('role', 'tablist');

					instance.viewsNode
						.all('button')
						.setAttribute('role', 'tab');
				},

				renderUI() {
					const instance = this;

					Scheduler.superclass.renderUI.apply(this, arguments);

					instance.navDateNode.replaceClass(
						'hidden-xs',
						'd-none d-sm-block'
					);
					instance.viewDateNode.replaceClass(
						'visible-xs',
						'd-block d-sm-none'
					);
					instance.viewsNode
						.all('button')
						.replaceClass('hidden-xs', 'd-none d-sm-block');
					instance.viewsSelectNode.replaceClass(
						'visible-xs',
						'd-block d-sm-none'
					);

					const showAddEventBtn = instance.get('showAddEventBtn');

					if (showAddEventBtn) {
						instance[ICON_ADD_EVENT_NODE] =
							instance.get(ICON_ADD_EVENT_NODE);

						instance[CONTROLS_NODE].prepend(
							instance[ICON_ADD_EVENT_NODE]
						);

						instance[ICON_ADD_EVENT_NODE].on(
							'click',
							instance._onClickAddEvent,
							instance
						);
					}
				},

				sync() {
					const instance = this;

					const events = instance._events;

					return events.sync.apply(events, arguments);
				},
			},
		});

		Liferay.Scheduler = Scheduler;

		const SchedulerDayView = A.Component.create({
			ATTRS: {
				initialScroll: {
					value: CalendarUtil.getInitialScroll(new Date(), timeZone),
				},
				navigationDateFormatter: {
					validator: isFunction,
					value(date) {
						const instance = this;

						const scheduler = instance.get('scheduler');

						return A.DataType.Date.format(date, {
							format: Liferay.Language.get('a-b-d-y'),
							locale: scheduler.get('locale'),
						});
					},
				},

				syncCurrentTimeUI() {
					const instance = this;

					const scheduler = instance.get('scheduler');

					const currentTime = scheduler.get('currentTime');

					instance._moveCurrentTimeNode(currentTime);
				},
			},

			EXTENDS: A.SchedulerDayView,

			NAME: 'scheduler-day-view',
		});

		Liferay.SchedulerDayView = SchedulerDayView;

		Liferay.SchedulerWeekView = A.Component.create({
			ATTRS: {
				headerDateFormatter: {
					validator: isFunction,
					value(date) {
						const instance = this;

						const scheduler = instance.get('scheduler');

						return A.DataType.Date.format(date, {
							format: Liferay.Language.get('a-d'),
							locale: scheduler.get('locale'),
						});
					},
				},
				initialScroll: {
					value: CalendarUtil.getInitialScroll(new Date(), timeZone),
				},
				navigationDateFormatter: {
					validator: isFunction,
					value(date) {
						const instance = this;

						const scheduler = instance.get('scheduler');

						const locale = scheduler.get('locale');

						const startDate = instance._firstDayOfWeek(date);

						const endDate = DateMath.add(
							startDate,
							DateMath.DAY,
							instance.get('days') - 1
						);

						const startDateFormat = Liferay.Language.get('b-d');

						let endDateFormat;

						if (DateMath.isMonthOverlapWeek(startDate)) {
							endDateFormat = Liferay.Language.get('b-d-y');
						}
						else {
							endDateFormat = Liferay.Language.get('d-y');
						}

						const startDateLabel = A.DataType.Date.format(
							startDate,
							{
								format: startDateFormat,
								locale,
							}
						);

						const endDateLabel = A.DataType.Date.format(endDate, {
							format: endDateFormat,
							locale,
						});

						return [startDateLabel, '&mdash;', endDateLabel].join(
							' '
						);
					},
				},
			},

			EXTENDS: A.SchedulerWeekView,

			NAME: 'scheduler-week-view',
		});

		const SchedulerMonthView = A.Component.create({
			ATTRS: {
				navigationDateFormatter: {
					validator: isFunction,
					value(date) {
						const instance = this;

						const scheduler = instance.get('scheduler');

						return A.DataType.Date.format(date, {
							format: Liferay.Language.get('b-y'),
							locale: scheduler.get('locale'),
						});
					},
				},
			},

			EXTENDS: A.SchedulerMonthView,

			NAME: 'scheduler-month-view',

			prototype: {
				_getEvtSplitInfo(event, celDate, rowStartDate, rowEndDate) {
					const eventEndDate = event.getClearEndDate();
					const eventStartDate = event.getClearStartDate();

					if (
						DateMath.compare(
							event.get('endDate'),
							DateMath.toMidnight(eventEndDate)
						)
					) {
						eventEndDate.setDate(eventEndDate.getDate() - 1);
						eventEndDate.setSeconds(eventEndDate.getSeconds() - 1);
					}

					const maxColspan = DateMath.countDays(rowEndDate, celDate);

					const info = {
						colspan:
							Math.min(
								DateMath.countDays(eventEndDate, celDate),
								maxColspan
							) + 1,
						left: DateMath.before(eventStartDate, rowStartDate),
						right: DateMath.after(eventEndDate, rowEndDate),
					};

					return info;
				},

				_getRenderableEvent(events, rowStartDate, rowEndDate, celDate) {
					const instance = this;
					const key = instance._getEvtRenderedStackKey(celDate);
					let i;

					if (!instance.evtRenderedStack[key]) {
						instance.evtRenderedStack[key] = [];
					}

					for (i = 0; i < events.length; i++) {
						const event = events[i];

						const eventEndDate = event.get('endDate');
						const eventStartDate = event.get('startDate');

						const weekEndDate = DateMath.toLastHour(rowEndDate);
						const weekStartDate = DateMath.toMidnight(rowStartDate);

						if (
							DateMath.before(eventStartDate, weekEndDate) &&
							DateMath.after(eventEndDate, weekStartDate)
						) {
							const isEventDateContinuation =
								DateMath.after(celDate, eventStartDate) &&
								!DateMath.isDayOverlap(celDate, rowStartDate);
							const isEventStartDateDay = !DateMath.isDayOverlap(
								eventStartDate,
								celDate
							);

							const isRendered =
								A.Array.indexOf(
									instance.evtRenderedStack[key],
									event
								) > -1;

							if (
								!isRendered &&
								(isEventStartDateDay || isEventDateContinuation)
							) {
								return event;
							}
						}
					}

					return null;
				},

				_syncCellDimensions() {
					const instance = this;

					const scheduler = instance.get('scheduler');

					const viewDate = scheduler.get('viewDate');

					const firstDayOfWeek = scheduler.get('firstDayOfWeek');

					const weeks = DateMath.getWeeksInMonth(
						viewDate,
						firstDayOfWeek
					);

					SchedulerMonthView.superclass._syncCellDimensions.apply(
						this,
						arguments
					);

					instance.gridCellHeight =
						instance.rowsContainerNode.get('offsetHeight') / weeks;
				},

				_uiSetDate(date) {
					const instance = this;

					const scheduler = instance.get('scheduler');

					const firstDayOfWeek = scheduler.get('firstDayOfWeek');

					const weeks = DateMath.getWeeksInMonth(
						date,
						firstDayOfWeek
					);

					A.each(instance.tableRows, (item, index) => {
						if (index >= weeks) {
							item.remove();
						}
						else if (index < weeks && !item.parentElement) {
							instance.tableRowContainer.appendChild(item);
						}
					});

					SchedulerMonthView.superclass._uiSetDate.apply(
						this,
						arguments
					);
				},
			},
		});

		Liferay.SchedulerMonthView = SchedulerMonthView;

		const SchedulerAgendaView = A.Component.create({
			ATTRS: {
				eventsDateFormatter: {
					validator: isFunction,
					value(startDate, endDate) {
						const instance = this;

						const scheduler = instance.get('scheduler');

						const isoTime = scheduler
							.get('activeView')
							.get('isoTime');

						let startDateMask;

						let endDateMask;

						if (isoTime) {
							startDateMask = '%H:%M';

							endDateMask = '%H:%M';
						}
						else {
							startDateMask = '%l:%M';
							endDateMask = '%l:%M';

							if (startDate.getHours() >= 12) {
								startDateMask += 'pm';
							}
							else {
								startDateMask += 'am';
							}

							if (endDate.getHours() >= 12) {
								endDateMask += 'pm';
							}
							else {
								endDateMask += 'am';
							}
						}

						if (DateMath.isDayOverlap(startDate, endDate)) {
							startDateMask += ', ' + Liferay.Language.get('b-e');
							endDateMask += ', ' + Liferay.Language.get('b-e');
						}

						const startDateFormatter = instance._getFormatter.call(
							instance,
							startDateMask
						);
						const endDateFormatter = instance._getFormatter.call(
							instance,
							endDateMask
						);

						return [
							startDateFormatter.call(instance, startDate),
							'&mdash;',
							endDateFormatter.call(instance, endDate),
						].join(' ');
					},
				},

				headerDayDateFormatter: {
					validator: isFunction,
					value(date) {
						const instance = this;

						const todayDate = instance
							.get('scheduler')
							.get('todayDate');

						let mask;

						if (!DateMath.isDayOverlap(date, todayDate)) {
							mask = Liferay.Language.get('today');
						}
						else {
							mask = Liferay.Language.get('a');
						}

						const formatter = instance._getFormatter.call(
							instance,
							mask
						);

						return formatter.call(instance, date);
					},
				},

				headerExtraDateFormatter: {
					validator: isFunction,
					valueFn() {
						const instance = this;

						return instance._getFormatter(
							Liferay.Language.get('b-e')
						);
					},
				},

				infoDayDateFormatter: {
					validator: isFunction,
					valueFn() {
						const instance = this;

						return instance._getFormatter(
							Liferay.Language.get('e')
						);
					},
				},

				infoLabelBigDateFormatter: {
					validator: isFunction,
					valueFn() {
						const instance = this;

						return instance._getFormatter(
							Liferay.Language.get('a')
						);
					},
				},

				infoLabelSmallDateFormatter: {
					validator: isFunction,
					valueFn() {
						const instance = this;

						return instance._getFormatter(
							Liferay.Language.get('b-d-y')
						);
					},
				},
			},

			EXTENDS: A.SchedulerAgendaView,

			NAME: 'scheduler-view-agenda',

			prototype: {
				_getFormatter(mask) {
					return function (date) {
						const instance = this;

						const scheduler = instance.get('scheduler');

						return A.DataType.Date.format(date, {
							format: mask,
							locale: scheduler.get('locale'),
						});
					};
				},

				plotEvents() {
					const instance = this;

					const scheduler = instance.get('scheduler');

					SchedulerAgendaView.superclass.plotEvents.apply(
						instance,
						arguments
					);

					const headerContent = instance.get('headerContent');

					if (scheduler.get('showHeader')) {
						headerContent.show();
					}
					else {
						headerContent.hide();
					}
				},
			},
		});

		Liferay.SchedulerAgendaView = SchedulerAgendaView;
	},
	'',
	{
		requires: [
			'async-queue',
			'aui-datatype',
			'aui-scheduler',
			'dd-plugin',
			'liferay-calendar-a11y',
			'liferay-calendar-message-util',
			'liferay-calendar-recurrence-converter',
			'liferay-calendar-recurrence-util',
			'liferay-calendar-util',
			'liferay-scheduler-event-recorder',
			'liferay-scheduler-models',
			'promise',
			'resize-plugin',
		],
	}
);
