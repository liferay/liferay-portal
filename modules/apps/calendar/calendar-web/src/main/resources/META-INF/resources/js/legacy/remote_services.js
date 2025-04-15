/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-calendar-remote-services',
	(A) => {
		const Lang = A.Lang;

		const isString = Lang.isString;
		const toInt = Lang.toInt;

		const CalendarUtil = Liferay.CalendarUtil;
		const MessageUtil = Liferay.CalendarMessageUtil;

		const STR_BLANK = '';

		const CalendarRemoteServices = A.Base.create(
			'calendar-remote-services',
			A.Base,
			[Liferay.PortletBase],
			{
				_invokeActionURL(config) {
					const instance = this;

					const actionParameters = {
						'jakarta.portlet.action': config.actionName,
					};

					A.mix(actionParameters, config.queryParameters);

					const url = Liferay.Util.PortletURL.createActionURL(
						instance.get('baseActionURL'),
						actionParameters
					);

					let payload;

					if (config.payload) {
						payload = Liferay.Util.ns(
							instance.get('namespace'),
							config.payload
						);
					}

					const data = new URLSearchParams();

					Object.keys(payload).forEach((key) => {
						data.append(key, payload[key]);
					});

					Liferay.Util.fetch(url.toString(), {
						body: data,
						method: 'POST',
					})
						.then((response) => {
							return response.json();
						})
						.then((data) => {
							config.callback(data);
						});
				},

				_invokeResourceURL(config) {
					const instance = this;

					const resourceParameters = {
						p_p_resource_id: config.resourceId,
					};

					A.mix(resourceParameters, config.queryParameters);

					const url = Liferay.Util.PortletURL.createResourceURL(
						instance.get('baseResourceURL'),
						resourceParameters
					);

					let payload;

					if (config.payload) {
						payload = Liferay.Util.ns(
							instance.get('namespace'),
							config.payload
						);
					}

					const data = new URLSearchParams();

					if (payload) {
						Object.keys(payload).forEach((key) => {
							data.append(key, payload[key]);
						});
					}

					Liferay.Util.fetch(url.toString(), {
						body: data,
						method: 'POST',
					})
						.then((response) => {
							return response.text();
						})
						.then((data) => {
							if (data.length) {
								config.callback(JSON.parse(data));
							}
						});
				},

				_invokeService(payload, callback) {
					const instance = this;

					callback = callback || {};

					const data = new URLSearchParams();
					data.append('cmd', JSON.stringify(payload));
					data.append('payload', Liferay.authToken);

					Liferay.Util.fetch(instance.get('invokerURL'), {
						body: data,
						method: 'POST',
					})
						.then((response) => {
							return response.json();
						})
						.then((data) => {
							if (typeof callback.success === 'function') {
								callback.success.apply(this, [data]);
							}
						})
						.catch((error) => {
							if (typeof callback.failure === 'function') {
								callback.failure(error);
							}
						});
				},

				deleteCalendar(calendarId, callback) {
					const instance = this;

					instance._invokeService(
						{
							'/calendar.calendar/delete-calendar': {
								calendarId,
							},
						},
						{
							success() {
								callback(this.get('responseData'));
							},
						}
					);
				},

				deleteEvent(schedulerEvent, success) {
					const instance = this;

					instance._invokeService(
						{
							'/calendar.calendarbooking/move-calendar-booking-to-trash':
								{
									calendarBookingId:
										schedulerEvent.get('calendarBookingId'),
								},
						},
						{
							success(data) {
								if (success) {
									success.call(instance, data);
									MessageUtil.showSuccessMessage(
										instance.get('rootNode')._node
									);
								}
							},
						}
					);
				},

				deleteEventInstance(schedulerEvent, allFollowing, success) {
					const instance = this;

					instance._invokeService(
						{
							'/calendar.calendarbooking/delete-calendar-booking-instance':
								{
									allFollowing,
									calendarBookingId:
										schedulerEvent.get('calendarBookingId'),
									deleteRecurringCalendarBookings: true,
									instanceIndex:
										schedulerEvent.get('instanceIndex'),
								},
						},
						{
							success(data) {
								if (success) {
									success.call(instance, data);
									MessageUtil.showSuccessMessage(
										instance.get('rootNode')._node
									);
								}
							},
						}
					);
				},

				getCalendar(calendarId, callback) {
					const instance = this;

					instance._invokeResourceURL({
						callback,
						queryParameters: {
							calendarId,
						},
						resourceId: 'calendar',
					});
				},

				getCalendarBookingInvitees(calendarBookingId, callback) {
					const instance = this;

					instance._invokeResourceURL({
						callback,
						queryParameters: {
							parentCalendarBookingId: calendarBookingId,
						},
						resourceId: 'calendarBookingInvitees',
					});
				},

				getCalendarRenderingRules(
					calendarIds,
					startDate,
					endDate,
					ruleName,
					callback
				) {
					const instance = this;

					instance._invokeResourceURL({
						callback,
						payload: {
							calendarIds: calendarIds.join(),
							endTime: endDate.getTime(),
							ruleName,
							startTime: startDate.getTime(),
						},
						resourceId: 'calendarRenderingRules',
					});
				},

				getCurrentTime(callback) {
					const instance = this;

					const lastCurrentTime = instance.lastCurrentTime;

					if (lastCurrentTime) {
						const lastBrowserTime = instance.lastBrowserTime;

						const browserTime = new Date();

						const timeDiff = Math.abs(
							browserTime.getTime() - lastBrowserTime.getTime()
						);

						const currentTime =
							lastCurrentTime.getTime() + timeDiff;

						lastCurrentTime.setTime(currentTime);

						instance.lastCurrentTime = lastCurrentTime;

						instance.lastBrowserTime = browserTime;

						callback(instance.lastCurrentTime);

						return;
					}

					instance._invokeResourceURL({
						callback(dateObj) {
							instance.lastCurrentTime =
								CalendarUtil.getDateFromObject(dateObj);

							instance.lastBrowserTime = new Date();

							callback(instance.lastCurrentTime);
						},
						resourceId: 'currentTime',
					});
				},

				getEvent(calendarBookingId, success, failure) {
					const instance = this;

					instance._invokeService(
						{
							'/calendar.calendarbooking/get-calendar-booking': {
								calendarBookingId,
							},
						},
						{
							failure,
							success,
						}
					);
				},

				getEvents(
					calendarIds,
					eventsPerPage,
					startDate,
					endDate,
					status,
					callback
				) {
					const instance = this;

					instance._invokeResourceURL({
						callback,
						payload: {
							calendarIds: calendarIds.join(','),
							endTimeDay: endDate.getDate(),
							endTimeHour: endDate.getHours(),
							endTimeMinute: endDate.getMinutes(),
							endTimeMonth: endDate.getMonth(),
							endTimeYear: endDate.getFullYear(),
							eventsPerPage,
							startTimeDay: startDate.getDate(),
							startTimeHour: startDate.getHours(),
							startTimeMinute: startDate.getMinutes(),
							startTimeMonth: startDate.getMonth(),
							startTimeYear: startDate.getFullYear(),
							statuses: status.join(','),
						},
						resourceId: 'calendarBookings',
					});
				},

				getResourceCalendars(calendarResourceId, callback) {
					const instance = this;

					instance._invokeResourceURL({
						callback,
						queryParameters: {
							calendarResourceId,
						},
						resourceId: 'resourceCalendars',
					});
				},

				hasExclusiveCalendarBooking(
					calendarId,
					startDate,
					endDate,
					callback
				) {
					const instance = this;

					instance._invokeResourceURL({
						callback(result) {
							callback(result.hasExclusiveCalendarBooking);
						},
						queryParameters: {
							calendarId,
							endTimeDay: endDate.getDate(),
							endTimeHour: endDate.getHours(),
							endTimeMinute: endDate.getMinutes(),
							endTimeMonth: endDate.getMonth(),
							endTimeYear: endDate.getFullYear(),
							startTimeDay: startDate.getDate(),
							startTimeHour: startDate.getHours(),
							startTimeMinute: startDate.getMinutes(),
							startTimeMonth: startDate.getMonth(),
							startTimeYear: startDate.getFullYear(),
						},
						resourceId: 'hasExclusiveCalendarBooking',
					});
				},

				invokeTransition(
					schedulerEvent,
					instanceIndex,
					status,
					updateInstance,
					allFollowing
				) {
					const instance = this;

					const scheduler = schedulerEvent.get('scheduler');

					instance._invokeService(
						{
							'/calendar.calendarbooking/invoke-transition': {
								allFollowing,
								calendarBookingId:
									schedulerEvent.get('calendarBookingId'),
								instanceIndex,
								status,
								updateInstance,
								userId: instance.get('userId'),
							},
						},
						{
							start() {
								schedulerEvent.set('loading', true, {
									silent: true,
								});
							},

							success(data) {
								schedulerEvent.set('loading', false, {
									silent: true,
								});

								if (data && !data.exception && scheduler) {
									const eventRecorder =
										scheduler.get('eventRecorder');

									eventRecorder.hidePopover();

									scheduler.load();
								}
							},
						}
					);
				},

				updateCalendarColor(calendarId, color) {
					const instance = this;

					instance._invokeService({
						'/calendar.calendar/update-color': {
							calendarId,
							color: parseInt(color.substr(1), 16),
						},
					});
				},

				updateEvent(
					schedulerEvent,
					updateInstance,
					allFollowing,
					success
				) {
					const instance = this;

					const endDate = schedulerEvent.get('endDate');
					const startDate = schedulerEvent.get('startDate');

					instance._invokeActionURL({
						actionName: 'updateSchedulerCalendarBooking',
						callback(data) {
							schedulerEvent.set('loading', false, {
								silent: true,
							});

							if (data) {
								if (data.exception) {
									CalendarUtil.destroyEvent(schedulerEvent);
									MessageUtil.showErrorMessage(
										instance.get('rootNode')._node,
										data.exception
									);
								}
								else {
									CalendarUtil.setEventAttrs(
										schedulerEvent,
										data
									);

									if (success) {
										success.call(instance, data);
										MessageUtil.showSuccessMessage(
											instance.get('rootNode')._node
										);
									}
								}
							}
						},
						payload: {
							allDay: schedulerEvent.get('allDay'),
							allFollowing,
							calendarBookingId:
								schedulerEvent.get('calendarBookingId'),
							calendarId: schedulerEvent.get('calendarId'),
							endTimeDay: endDate.getDate(),
							endTimeHour: endDate.getHours(),
							endTimeMinute: endDate.getMinutes(),
							endTimeMonth: endDate.getMonth(),
							endTimeYear: endDate.getFullYear(),
							instanceIndex: schedulerEvent.get('instanceIndex'),
							recurrence: schedulerEvent.get('recurrence'),
							startTimeDay: startDate.getDate(),
							startTimeHour: startDate.getHours(),
							startTimeMinute: startDate.getMinutes(),
							startTimeMonth: startDate.getMonth(),
							startTimeYear: startDate.getFullYear(),
							title: Liferay.Util.unescapeHTML(
								schedulerEvent.get('content')
							),
							updateInstance,
						},
					});
				},
			},
			{
				ATTRS: {
					baseActionURL: {
						validator: isString,
						value: STR_BLANK,
					},
					baseResourceURL: {
						validator: isString,
						value: STR_BLANK,
					},
					invokerURL: {
						validator: isString,
						value: STR_BLANK,
					},
					userId: {
						setter: toInt,
					},
				},
			}
		);

		Liferay.CalendarRemoteServices = CalendarRemoteServices;
	},
	'',
	{
		requires: [
			'aui-base',
			'aui-component',
			'liferay-calendar-message-util',
			'liferay-calendar-util',
			'liferay-portlet-base',
		],
	}
);
