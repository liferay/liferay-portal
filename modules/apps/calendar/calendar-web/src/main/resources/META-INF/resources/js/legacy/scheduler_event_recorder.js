/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-scheduler-event-recorder',
	(A) => {
		const AArray = A.Array;
		const DateMath = A.DataType.DateMath;
		const Lang = A.Lang;

		const CalendarWorkflow = Liferay.CalendarWorkflow;

		const isObject = Lang.isObject;
		const isString = Lang.isString;
		const isValue = Lang.isValue;

		const toInt = function (value) {
			return Lang.toInt(value, 10, 0);
		};

		const STR_BLANK = '';

		const STR_COMMA_SPACE = ', ';

		const CalendarUtil = Liferay.CalendarUtil;

		const SchedulerEventRecorder = A.Component.create({
			ATTRS: {
				calendarContainer: {
					validator: isObject,
					value: null,
				},

				calendarId: {
					setter: toInt,
					value: 0,
				},

				dateFormat: {
					validator: isString,
					value: Liferay.Language.get('a-b-d'),
				},

				editCalendarBookingURL: {
					setter: String,
					validator: isValue,
					value: STR_BLANK,
				},

				headerContent: {
					value:
						'<input aria-label="' +
						Liferay.Language.get('title') +
						'" class="scheduler-event-recorder-content form-control" name="content" value="{content}" />',
				},

				permissionsCalendarBookingURL: {
					setter: String,
					validator: isValue,
					value: STR_BLANK,
				},

				portletNamespace: {
					setter: String,
					validator: isValue,
					value: STR_BLANK,
				},

				remoteServices: {
					validator: isObject,
					value: null,
				},

				status: {
					setter: toInt,
					value: CalendarWorkflow.STATUS_DRAFT,
				},

				toolbar: {
					value: {
						children: [],
					},
				},

				viewCalendarBookingURL: {
					setter: String,
					validator: isValue,
					value: STR_BLANK,
				},
			},

			EXTENDS: A.SchedulerEventRecorder,

			NAME: 'scheduler-event-recorder',

			prototype: {
				_getFooterToolbar() {
					const instance = this;

					let schedulerEvent = instance.get('event');

					let schedulerEventCreated = false;

					if (schedulerEvent) {
						schedulerEventCreated = true;
					}
					else {
						schedulerEvent = instance;
					}

					const children = [];
					const editGroup = [];
					const respondGroup = [];

					const calendarContainer = instance.get('calendarContainer');

					const calendar = calendarContainer.getCalendar(
						schedulerEvent.get('calendarId')
					);
					const status = schedulerEvent.get('status');

					if (calendar) {
						const permissions = calendar.get('permissions');

						if (
							instance._hasSaveButton(
								permissions,
								calendar,
								status
							)
						) {
							editGroup.push({
								cssClass: 'btn-primary btn-sm',
								id: 'saveBtn',
								label: Liferay.Language.get('save'),
								on: {
									click: A.bind(
										instance._handleSaveEvent,
										instance
									),
								},
								primary: true,
							});
						}

						if (
							instance._hasEditButton(
								permissions,
								calendar,
								status
							)
						) {
							editGroup.push({
								cssClass: 'btn-secondary btn-sm',
								id: 'editBtn',
								label: Liferay.Language.get('edit'),
								on: {
									click: A.bind(
										instance._handleEditEvent,
										instance
									),
								},
							});
						}

						if (
							schedulerEventCreated === true &&
							permissions.VIEW_BOOKING_DETAILS
						) {
							editGroup.push({
								cssClass: 'btn-secondary btn-sm',
								id: 'viewBtn',
								label: Liferay.Language.get('view-details'),
								on: {
									click: A.bind(
										instance._handleViewEvent,
										instance
									),
								},
							});
						}

						if (
							schedulerEvent.isMasterBooking() &&
							instance._hasDeleteButton(
								permissions,
								calendar,
								status
							)
						) {
							editGroup.push({
								cssClass: 'btn-secondary btn-sm',
								id: 'deleteBtn',
								label: Liferay.Language.get('delete'),
								on: {
									click: A.bind(
										instance._handleDeleteEvent,
										instance
									),
								},
							});
						}

						if (editGroup.length) {
							children.push(editGroup);
						}

						if (respondGroup.length) {
							children.push(respondGroup);
						}
					}

					return children;
				},

				_handleEditEvent() {
					const instance = this;

					const scheduler = instance.get('scheduler');

					const activeViewName = scheduler
						.get('activeView')
						.get('name');

					const date = scheduler.get('date');

					const schedulerEvent = instance.get('event');

					const editCalendarBookingURL = decodeURIComponent(
						instance.get('editCalendarBookingURL')
					);

					const data = instance.serializeForm();

					data.activeView = activeViewName;

					data.date = date.getTime();

					const endTime = new Date(data.endTime);

					data.endTimeDay = endTime.getDate();
					data.endTimeHour = endTime.getHours();
					data.endTimeMinute = endTime.getMinutes();
					data.endTimeMonth = endTime.getMonth();
					data.endTimeYear = endTime.getFullYear();

					const startTime = new Date(data.startTime);

					data.startTimeDay = startTime.getDate();
					data.startTimeHour = startTime.getHours();
					data.startTimeMinute = startTime.getMinutes();
					data.startTimeMonth = startTime.getMonth();
					data.startTimeYear = startTime.getFullYear();

					data.titleCurrentValue = encodeURIComponent(data.content);

					if (schedulerEvent) {
						data.allDay = schedulerEvent.get('allDay');
						data.calendarBookingId =
							schedulerEvent.get('calendarBookingId');
					}

					Liferay.Util.openModal({
						containerProps: {},
						iframeBodyCssClass: 'dialog-with-footer',
						onClose: function destroy() {
							scheduler.load();
						},
						title: Liferay.Language.get('edit-calendar-booking'),
						url: CalendarUtil.fillURLParameters(
							editCalendarBookingURL,
							data
						),
					});

					instance.hidePopover();
				},

				_handleEventAnswer(event) {
					const instance = this;

					const currentTarget = event.currentTarget;

					const schedulerEvent = instance.get('event');

					const linkEnabled = A.DataType.Boolean.parse(
						currentTarget.hasClass('calendar-event-answer-true')
					);

					const statusData = toInt(currentTarget.getData('status'));

					if (schedulerEvent && linkEnabled) {
						const remoteServices = instance.get('remoteServices');

						if (schedulerEvent.isRecurring()) {
							Liferay.RecurrenceUtil.openConfirmationPanel(
								'invokeTransition',
								() => {
									remoteServices.invokeTransition(
										schedulerEvent,
										schedulerEvent.get('instanceIndex'),
										statusData,
										true,
										false
									);
								},
								() => {
									remoteServices.invokeTransition(
										schedulerEvent,
										schedulerEvent.get('instanceIndex'),
										statusData,
										true,
										true
									);
								},
								() => {
									remoteServices.invokeTransition(
										schedulerEvent,
										schedulerEvent.get('instanceIndex'),
										statusData,
										false,
										false
									);
								}
							);
						}
						else {
							remoteServices.invokeTransition(
								schedulerEvent,
								0,
								statusData,
								false,
								false
							);
						}
					}
				},

				_handleViewEvent(event) {
					const instance = this;

					const viewCalendarBookingURL = decodeURIComponent(
						instance.get('viewCalendarBookingURL')
					);

					const data = instance.serializeForm();

					const schedulerEvent = instance.get('event');

					data.calendarBookingId =
						schedulerEvent.get('calendarBookingId');

					Liferay.Util.openModal({
						containerProps: {},
						iframeBodyCssClass: '',
						onClose: function destroy() {
							schedulerEvent.syncWithServer();
						},
						title: Liferay.Language.get(
							'view-calendar-booking-details'
						),
						url: CalendarUtil.fillURLParameters(
							viewCalendarBookingURL,
							data
						),
					});

					event.domEvent.preventDefault();
				},

				_hasDeleteButton(permissions, calendar, _status) {
					return permissions.MANAGE_BOOKINGS && calendar;
				},

				_hasEditButton(permissions, _calendar, _status) {
					return permissions.MANAGE_BOOKINGS;
				},

				_hasSaveButton(permissions, _calendar, _status) {
					return permissions.MANAGE_BOOKINGS;
				},

				_hasWorkflowStatusPermission(schedulerEvent, newStatus) {
					const instance = this;

					let hasPermission = false;

					if (schedulerEvent) {
						const calendarId = schedulerEvent.get('calendarId');

						const calendarContainer =
							instance.get('calendarContainer');

						const calendar =
							calendarContainer.getCalendar(calendarId);

						const permissions = calendar.get('permissions');

						const status = schedulerEvent.get('status');

						hasPermission =
							permissions.MANAGE_BOOKINGS &&
							status !== newStatus &&
							status !== CalendarWorkflow.STATUS_DRAFT;
					}

					return hasPermission;
				},

				_renderPopOver() {
					const instance = this;

					const popoverBB = instance.popover.get('boundingBox');

					SchedulerEventRecorder.superclass._renderPopOver.apply(
						this,
						arguments
					);

					popoverBB.delegate(
						['change', 'keypress'],
						(event) => {
							const schedulerEvent =
								instance.get('event') || instance;

							const calendarId = toInt(event.currentTarget.val());

							const calendarContainer =
								instance.get('calendarContainer');

							const selectedCalendar =
								calendarContainer.getCalendar(calendarId);

							if (selectedCalendar) {
								schedulerEvent.set(
									'color',
									selectedCalendar.get('color'),
									{
										silent: true,
									}
								);
							}
						},
						'#' +
							instance.get('portletNamespace') +
							'eventRecorderCalendar'
					);
				},

				_showResources() {
					const instance = this;

					const schedulerEvent = instance.get('event');

					const popoverBB = instance.popover.get('boundingBox');

					popoverBB.toggleClass(
						'calendar-portlet-event-recorder-editing',
						!!schedulerEvent
					);

					const idPopoverBB = popoverBB._node.getAttribute('id');

					let focusableElements;
					const keysPressed = {};

					const setFocusableElemeents = () => {
						if (!focusableElements) {
							focusableElements = [
								...document
									.getElementById(idPopoverBB)
									.querySelectorAll(
										'a[href], button, input:not([type="hidden"]), textarea, select, details, [tabindex]:not([tabindex="-1"])'
									),
							].filter(
								(element) =>
									!element.hasAttribute('disabled') &&
									!element.getAttribute('aria-hidden') &&
									!element.classList.contains('hide')
							);
						}
					};

					popoverBB.delegate(
						'keydown',
						(event) => {
							keysPressed[event.keyCode] = true;

							setFocusableElemeents();

							const lastIndexElem = focusableElements.length - 1;
							const isTabPressed =
								event.keyCode === A.Event.KeyMap.TAB ||
								keysPressed[A.Event.KeyMap.TAB];
							const isShiftPressed =
								event.keyCode === A.Event.KeyMap.SHIFT ||
								keysPressed[A.Event.KeyMap.SHIFT];
							const isForwardNavigation =
								isTabPressed && !isShiftPressed;
							const isBackwardNavigation =
								isTabPressed && isShiftPressed;

							if (isForwardNavigation) {
								const isLastFocusableElement =
									focusableElements &&
									focusableElements[lastIndexElem] ===
										event.target._node;
								if (isLastFocusableElement) {
									focusableElements[0].focus();
									event.preventDefault();
								}
							}
							else if (isBackwardNavigation) {
								const isFirstFocusableElement =
									focusableElements &&
									focusableElements[0] === event.target._node;
								if (isFirstFocusableElement) {
									focusableElements[lastIndexElem].focus();
									event.preventDefault();
								}
							}
						},
						'#' + idPopoverBB
					);

					popoverBB.delegate(
						'keyup',
						(event) => {
							delete keysPressed[event.keyCode];
						},
						'#' + idPopoverBB
					);

					const calendarContainer = instance.get('calendarContainer');

					const defaultCalendar =
						calendarContainer.get('defaultCalendar');

					let calendarId = defaultCalendar.get('calendarId');
					let color = defaultCalendar.get('color');

					let eventInstance = instance;

					if (schedulerEvent) {
						calendarId = schedulerEvent.get('calendarId');

						const calendar =
							calendarContainer.getCalendar(calendarId);

						if (calendar) {
							color = calendar.get('color');

							eventInstance = schedulerEvent;
						}
					}

					eventInstance.set('color', color, {
						silent: true,
					});

					const portletNamespace = instance.get('portletNamespace');

					const eventRecorderCalendar = document.querySelector(
						`#${portletNamespace}eventRecorderCalendar`
					);

					if (eventRecorderCalendar) {
						eventRecorderCalendar.value = calendarId.toString();
					}

					instance._syncInvitees();
				},

				_syncInvitees() {
					const instance = this;

					const schedulerEvent = instance.get('event');

					if (schedulerEvent) {
						const calendarContainer =
							instance.get('calendarContainer');

						const calendar = calendarContainer.getCalendar(
							schedulerEvent.get('calendarId')
						);

						if (calendar) {
							const permissions = calendar.get('permissions');

							if (permissions.VIEW_BOOKING_DETAILS) {
								const parentCalendarBookingId =
									schedulerEvent.get(
										'parentCalendarBookingId'
									);

								const portletNamespace =
									instance.get('portletNamespace');

								const remoteServices =
									instance.get('remoteServices');

								remoteServices.getCalendarBookingInvitees(
									parentCalendarBookingId,
									(data) => {
										const results = AArray.partition(
											data,
											(item) => {
												return (
													toInt(item.classNameId) ===
													CalendarUtil.USER_CLASS_NAME_ID
												);
											}
										);

										instance._syncInviteesContent(
											'#' +
												portletNamespace +
												'eventRecorderUsers',
											results.matches
										);
										instance._syncInviteesContent(
											'#' +
												portletNamespace +
												'eventRecorderResources',
											results.rejects
										);
									}
								);
							}
						}
					}
				},

				_syncInviteesContent(contentNode, calendarResources) {
					const values = calendarResources.map((item) => {
						return Liferay.Util.escapeHTML(item.name);
					});

					contentNode = document.querySelector(contentNode);

					const messageNode = contentNode.querySelector(
						'.calendar-portlet-invitees'
					);

					let messageHTML = '&mdash;';

					if (values.length) {
						contentNode.style.display = '';

						messageHTML = values.join(STR_COMMA_SPACE);
					}

					messageNode.innerHTML = messageHTML;
				},

				getFormattedDate() {
					const instance = this;
					const event = instance.get('event') || instance;
					const endDate = event.get('endDate');
					const startDate = event.get('startDate');

					const formattedStartDate = event._formatDate(
						startDate,
						instance.get('dateFormat')
					);

					if (event.get('allDay')) {
						return formattedStartDate;
					}

					let formattedEndDate = event._formatDate(
						endDate,
						instance.get('dateFormat')
					);

					if (formattedEndDate === formattedStartDate) {
						formattedEndDate = '';
					}

					const scheduler = event.get('scheduler');
					const formatHours = scheduler
						.get('activeView')
						.get('isoTime')
						? DateMath.toIsoTimeString
						: DateMath.toUsTimeString;

					return [
						formattedStartDate.concat(','),
						formatHours(startDate),
						'-',
						formattedEndDate ? formattedEndDate.concat(',') : '',
						formatHours(endDate),
					].join(' ');
				},

				getTemplateData() {
					const instance = this;

					let editing = true;

					let schedulerEvent = instance.get('event');

					if (!schedulerEvent) {
						editing = false;

						schedulerEvent = instance;
					}

					const calendarContainer = instance.get('calendarContainer');

					const calendar = calendarContainer.getCalendar(
						schedulerEvent.get('calendarId')
					);

					const permissions = calendar.get('permissions');

					const templateData =
						SchedulerEventRecorder.superclass.getTemplateData.apply(
							this,
							arguments
						);

					return {
						...templateData,
						acceptLinkEnabled:
							instance._hasWorkflowStatusPermission(
								schedulerEvent,
								CalendarWorkflow.STATUS_APPROVED
							),
						allDay: schedulerEvent.get('allDay'),
						availableCalendars:
							calendarContainer.get('availableCalendars'),
						calendar,
						calendarIds: Object.keys(
							calendarContainer.get('availableCalendars')
						),
						declineLinkEnabled:
							instance._hasWorkflowStatusPermission(
								schedulerEvent,
								CalendarWorkflow.STATUS_DENIED
							),
						editing,
						endTime: templateData.endDate,
						hasWorkflowInstanceLink: schedulerEvent.get(
							'hasWorkflowInstanceLink'
						),
						instanceIndex: schedulerEvent.get('instanceIndex'),
						maybeLinkEnabled: instance._hasWorkflowStatusPermission(
							schedulerEvent,
							CalendarWorkflow.STATUS_MAYBE
						),
						permissions,
						startTime: templateData.startDate,
						status: schedulerEvent.get('status'),
						workflowStatus: CalendarWorkflow,
					};
				},

				getUpdatedSchedulerEvent(optAttrMap) {
					const instance = this;

					const attrMap = {
						color: instance.get('color'),
					};

					const event = instance.get('event');

					if (event) {
						const calendarContainer =
							instance.get('calendarContainer');

						const calendar = calendarContainer.getCalendar(
							event.get('calendarId')
						);

						if (calendar) {
							attrMap.color = calendar.get('color');
						}
					}

					return SchedulerEventRecorder.superclass.getUpdatedSchedulerEvent.call(
						instance,
						{...attrMap, ...optAttrMap}
					);
				},

				hidePopover() {
					const instance = this;
					const selectedEvent = instance._selectedEvent;
					const target = document.activeElement;

					instance.popover.hide();

					if (selectedEvent) {
						selectedEvent.setAttribute('aria-expanded', false);
					}

					const isTargetVisible =
						target &&
						document.body.contains(target) &&
						target.offsetWidth > 0;

					if (isTargetVisible && target !== selectedEvent) {
						target.focus();
					}
					else if (selectedEvent) {
						selectedEvent.focus();
					}
				},

				initializer() {
					const instance = this;

					const popoverBB = instance.popover.get('boundingBox');

					popoverBB.delegate(
						'click',
						instance._handleEventAnswer,
						'.calendar-event-answer',
						instance
					);
				},

				isMasterBooking: Lang.emptyFnFalse,

				populateForm() {
					const instance = this;

					const bodyTemplate = instance.get('bodyTemplate');

					const headerTemplate = instance.get('headerTemplate');

					const templateData = instance.getTemplateData();

					if (
						A.instanceOf(bodyTemplate, A.Template) &&
						A.instanceOf(headerTemplate, A.Template)
					) {
						instance.popover.setStdModContent(
							'body',
							bodyTemplate.parse(templateData)
						);
						instance.popover.setStdModContent(
							'header',
							headerTemplate.parse(templateData)
						);

						instance.popover.addToolbar(
							instance._getFooterToolbar(),
							'footer'
						);
					}
					else {
						SchedulerEventRecorder.superclass.populateForm.apply(
							instance,
							arguments
						);
					}

					instance.popover.addToolbar(
						[
							{
								cssClass: 'close',
								labelHTML:
									'<span aria-label="close">&times;</span>',
								on: {
									click: A.bind(
										instance._handleCancelEvent,
										instance
									),
								},
								render: true,
							},
						],
						'body'
					);

					if (instance.popover.headerNode) {
						instance.popover.headerNode.toggleClass(
							'hide',
							!templateData.permissions.VIEW_BOOKING_DETAILS
						);
					}

					instance._showResources();
				},
			},
		});

		Liferay.SchedulerEventRecorder = SchedulerEventRecorder;
	},
	'',
	{
		requires: ['dd-plugin', 'liferay-calendar-util', 'resize-plugin'],
	}
);
