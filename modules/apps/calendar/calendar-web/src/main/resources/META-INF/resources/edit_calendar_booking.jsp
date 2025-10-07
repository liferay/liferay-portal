<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String activeView = ParamUtil.getString(request, "activeView", defaultView);

boolean allDay = BeanParamUtil.getBoolean(calendarBooking, request, "allDay");

TimeZone calendarBookingTimeZone = allDay ? TimeZone.getTimeZone(StringPool.UTC) : userTimeZone;

java.util.Calendar defaultStartTimeJCalendar = CalendarFactoryUtil.getCalendar(calendarBookingTimeZone);

defaultStartTimeJCalendar.add(java.util.Calendar.HOUR, 1);

defaultStartTimeJCalendar.set(java.util.Calendar.MINUTE, 0);

long calendarBookingId = BeanPropertiesUtil.getLong(calendarBooking, "calendarBookingId");

int instanceIndex = BeanParamUtil.getInteger(calendarBooking, request, "instanceIndex");

long calendarId = BeanParamUtil.getLong(calendarBooking, request, "calendarId", defaultCalendar.getCalendarId());

long startTime = BeanPropertiesUtil.getLong(calendarBooking, "startTime", defaultStartTimeJCalendar.getTimeInMillis());

java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(startTime, calendarBookingTimeZone);

int startTimeYear = ParamUtil.getInteger(request, "startTimeYear", startTimeJCalendar.get(java.util.Calendar.YEAR));
int startTimeMonth = ParamUtil.getInteger(request, "startTimeMonth", startTimeJCalendar.get(java.util.Calendar.MONTH));
int startTimeDay = ParamUtil.getInteger(request, "startTimeDay", startTimeJCalendar.get(java.util.Calendar.DAY_OF_MONTH));
int startTimeHour = ParamUtil.getInteger(request, "startTimeHour", startTimeJCalendar.get(java.util.Calendar.HOUR_OF_DAY));
int startTimeMinute = ParamUtil.getInteger(request, "startTimeMinute", startTimeJCalendar.get(java.util.Calendar.MINUTE));

int startTimeAmPm = ParamUtil.getInteger(request, "startTimeAmPm");

if (startTimeAmPm == java.util.Calendar.PM) {
	startTimeHour += 12;
}

startTimeJCalendar = CalendarFactoryUtil.getCalendar(startTimeYear, startTimeMonth, startTimeDay, startTimeHour, startTimeMinute, 0, 0, calendarBookingTimeZone);

startTimeJCalendar.setFirstDayOfWeek(weekStartsOn + 1);

startTime = startTimeJCalendar.getTimeInMillis();

java.util.Calendar defaultEndTimeJCalendar = (java.util.Calendar)startTimeJCalendar.clone();

defaultEndTimeJCalendar.add(java.util.Calendar.MINUTE, defaultDuration);

long endTime = BeanPropertiesUtil.getLong(calendarBooking, "endTime", defaultEndTimeJCalendar.getTimeInMillis());

java.util.Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(endTime, calendarBookingTimeZone);

int endTimeYear = ParamUtil.getInteger(request, "endTimeYear", endTimeJCalendar.get(java.util.Calendar.YEAR));
int endTimeMonth = ParamUtil.getInteger(request, "endTimeMonth", endTimeJCalendar.get(java.util.Calendar.MONTH));
int endTimeDay = ParamUtil.getInteger(request, "endTimeDay", endTimeJCalendar.get(java.util.Calendar.DAY_OF_MONTH));
int endTimeHour = ParamUtil.getInteger(request, "endTimeHour", endTimeJCalendar.get(java.util.Calendar.HOUR_OF_DAY));
int endTimeMinute = ParamUtil.getInteger(request, "endTimeMinute", endTimeJCalendar.get(java.util.Calendar.MINUTE));

int endTimeAmPm = ParamUtil.getInteger(request, "endTimeAmPm");

if (endTimeAmPm == java.util.Calendar.PM) {
	endTimeHour += 12;
}

endTimeJCalendar = CalendarFactoryUtil.getCalendar(endTimeYear, endTimeMonth, endTimeDay, endTimeHour, endTimeMinute, 0, 0, calendarBookingTimeZone);

endTimeJCalendar.setFirstDayOfWeek(weekStartsOn + 1);

endTime = endTimeJCalendar.getTimeInMillis();

long firstReminder = BeanParamUtil.getLong(calendarBooking, request, "firstReminder");
String firstReminderType = BeanParamUtil.getString(calendarBooking, request, "firstReminderType", CalendarServiceConfigurationValues.CALENDAR_NOTIFICATION_DEFAULT_TYPE.name());
long secondReminder = BeanParamUtil.getLong(calendarBooking, request, "secondReminder");
String secondReminderType = BeanParamUtil.getString(calendarBooking, request, "secondReminderType", CalendarServiceConfigurationValues.CALENDAR_NOTIFICATION_DEFAULT_TYPE.name());

JSONArray acceptedCalendarsJSONArray = JSONFactoryUtil.createJSONArray();
JSONArray declinedCalendarsJSONArray = JSONFactoryUtil.createJSONArray();
JSONArray maybeCalendarsJSONArray = JSONFactoryUtil.createJSONArray();
JSONArray pendingCalendarsJSONArray = JSONFactoryUtil.createJSONArray();

boolean approved = false;
boolean hasChildCalendarBookings = false;
boolean hasWorkflowDefinitionLink = false;
boolean invitable = true;
boolean masterBooking = true;
Recurrence recurrence = null;
boolean recurring = false;

Calendar calendar = CalendarServiceUtil.fetchCalendar(calendarId);

if (calendarBooking != null) {
	acceptedCalendarsJSONArray = CalendarUtil.toCalendarBookingsJSONArray(themeDisplay, CalendarBookingServiceUtil.getChildCalendarBookings(calendarBooking.getParentCalendarBookingId(), WorkflowConstants.STATUS_APPROVED));
	declinedCalendarsJSONArray = CalendarUtil.toCalendarBookingsJSONArray(themeDisplay, CalendarBookingServiceUtil.getChildCalendarBookings(calendarBooking.getParentCalendarBookingId(), WorkflowConstants.STATUS_DENIED));
	maybeCalendarsJSONArray = CalendarUtil.toCalendarBookingsJSONArray(themeDisplay, CalendarBookingServiceUtil.getChildCalendarBookings(calendarBooking.getParentCalendarBookingId(), CalendarBookingWorkflowConstants.STATUS_MAYBE));

	List<CalendarBooking> pendingCalendarBookings = new ArrayList<CalendarBooking>();

	pendingCalendarBookings.addAll(CalendarBookingServiceUtil.getChildCalendarBookings(calendarBooking.getParentCalendarBookingId(), WorkflowConstants.STATUS_PENDING));
	pendingCalendarBookings.addAll(CalendarBookingServiceUtil.getChildCalendarBookings(calendarBooking.getParentCalendarBookingId(), WorkflowConstants.STATUS_DRAFT));
	pendingCalendarBookings.addAll(CalendarBookingServiceUtil.getChildCalendarBookings(calendarBooking.getParentCalendarBookingId(), CalendarBookingWorkflowConstants.STATUS_MASTER_PENDING));

	pendingCalendarsJSONArray = CalendarUtil.toCalendarBookingsJSONArray(themeDisplay, pendingCalendarBookings);

	if (calendarBooking.isMasterBooking()) {
		List<CalendarBooking> childCalendarBookings = CalendarBookingServiceUtil.getChildCalendarBookings(calendarBooking.getParentCalendarBookingId());

		if (childCalendarBookings.size() > 1) {
			hasChildCalendarBookings = true;
		}
	}
	else {
		invitable = false;
		masterBooking = false;
	}

	if (calendarBooking.isRecurring()) {
		recurring = true;
	}

	recurrence = calendarDisplayContext.getLastRecurrence(calendarBooking);

	recurrence = RecurrenceUtil.inTimeZone(recurrence, startTimeJCalendar, calendarBookingTimeZone);

	approved = calendarBooking.isApproved();

	calendar = calendarBooking.getCalendar();

	CalendarResource calendarResource = calendar.getCalendarResource();

	hasWorkflowDefinitionLink = WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(themeDisplay.getCompanyId(), calendarResource.getGroupId(), CalendarBooking.class.getName());
}
else if (calendar != null) {
	JSONObject calendarJSONObject = CalendarUtil.toCalendarJSONObject(themeDisplay, calendar);

	CalendarResource calendarResource = calendar.getCalendarResource();

	if (calendar.getUserId() == themeDisplay.getUserId()) {
		acceptedCalendarsJSONArray.put(calendarJSONObject);
	}
	else {
		pendingCalendarsJSONArray.put(calendarJSONObject);

		if (defaultCalendar.getUserId() == themeDisplay.getUserId()) {
			acceptedCalendarsJSONArray.put(CalendarUtil.toCalendarJSONObject(themeDisplay, defaultCalendar));
		}
	}

	hasWorkflowDefinitionLink = WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(themeDisplay.getCompanyId(), calendarResource.getGroupId(), CalendarBooking.class.getName());
}

long[] groupIds = null;

if (showUserEvents) {
	groupIds = ArrayUtil.append(user.getGroupIds(), new long[] {user.getGroupId(), scopeGroupId});
}
else {
	groupIds = ArrayUtil.append(user.getGroupIds(), new long[] {scopeGroupId});
}

List<Calendar> manageableCalendars = CalendarServiceUtil.search(themeDisplay.getCompanyId(), groupIds, null, null, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS, CalendarNameComparator.getInstance(true), CalendarActionKeys.MANAGE_BOOKINGS);

CalendarResource guestCalendarResource = CalendarResourceUtil.fetchGuestCalendarResource(themeDisplay.getCompanyId());

if (guestCalendarResource != null) {
	manageableCalendars.remove(guestCalendarResource.getDefaultCalendar());
}

for (Calendar otherCalendar : otherCalendars) {
	if (!manageableCalendars.contains(otherCalendar) && CalendarPermission.contains(themeDisplay.getPermissionChecker(), otherCalendar, CalendarActionKeys.MANAGE_BOOKINGS)) {
		manageableCalendars.add(otherCalendar);
	}
}

Iterator<Calendar> manageableCalendarsIterator = manageableCalendars.iterator();

while (manageableCalendarsIterator.hasNext()) {
	Calendar curCalendar = manageableCalendarsIterator.next();

	if (!CalendarServiceUtil.isManageableFromGroup(curCalendar.getCalendarId(), themeDisplay.getScopeGroupId())) {
		manageableCalendarsIterator.remove();
	}
}
%>

<aui:script use="liferay-calendar-container,liferay-calendar-remote-services,liferay-component">
	Liferay.component('<portlet:namespace />calendarContainer', () => {
		var calendarContainer = new Liferay.CalendarContainer({
			groupCalendarResourceId:
				<%= groupCalendarResource.getCalendarResourceId() %>,

			<c:if test="<%= userCalendarResource != null %>">
				userCalendarResourceId:
					<%= userCalendarResource.getCalendarResourceId() %>,
			</c:if>

			namespace: '<portlet:namespace />',
		});

		var destroyInstance = function (event) {
			if (event.portletId === '<%= portletDisplay.getId() %>') {
				calendarContainer.destroy();

				Liferay.component('<portlet:namespace />calendarContainer', null);
				Liferay.detach('destroyPortlet', destroyInstance);
			}
		};

		Liferay.on('destroyPortlet', destroyInstance);

		return calendarContainer;
	});

	Liferay.component('<portlet:namespace />remoteServices', () => {
		var remoteServices = new Liferay.CalendarRemoteServices({
			baseActionURL:
				'<%= PortletURLFactoryUtil.create(request, portletDisplay.getId(), PortletRequest.ACTION_PHASE) %>',
			baseResourceURL:
				'<%= PortletURLFactoryUtil.create(request, portletDisplay.getId(), PortletRequest.RESOURCE_PHASE) %>',
			invokerURL: themeDisplay.getPathContext() + '/api/jsonws/invoke',
			namespace: '<portlet:namespace />',
		});

		var destroyInstance = function (event) {
			if (event.portletId === '<%= portletDisplay.getId() %>') {
				remoteServices.destroy();

				Liferay.component('<portlet:namespace />remoteServices', null);

				Liferay.detach('destroyPortlet', destroyInstance);
			}
		};

		Liferay.on('destroyPortlet', destroyInstance);

		return remoteServices;
	});
</aui:script>

<liferay-portlet:actionURL name="updateFormCalendarBooking" var="updateFormCalendarBookingURL" />

<aui:form action="<%= updateFormCalendarBookingURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "updateCalendarBooking();" %>'>
	<aui:input name="mvcPath" type="hidden" value="/edit_calendar_booking.jsp" />

	<liferay-portlet:renderURL var="redirectURL">
		<liferay-portlet:param name="mvcPath" value="/edit_calendar_booking.jsp" />
		<liferay-portlet:param name="calendarBookingId" value="<%= String.valueOf(calendarBookingId) %>" />
	</liferay-portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="<%= calendarDisplayContext.getEditCalendarBookingRedirectURL(request, redirectURL) %>" />
	<aui:input name="calendarBookingId" type="hidden" value="<%= calendarBookingId %>" />
	<aui:input name="instanceIndex" type="hidden" value="<%= instanceIndex %>" />
	<aui:input name="childCalendarIds" type="hidden" />
	<aui:input name="reinvitableCalendarIds" type="hidden" />
	<aui:input name="allFollowing" type="hidden" />
	<aui:input name="updateCalendarBookingInstance" type="hidden" />
	<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_PUBLISH %>" />

	<div class="lfr-form-content">
		<clay:sheet>
			<liferay-ui:error exception="<%= CalendarBookingDurationException.class %>" message="please-enter-a-start-date-that-comes-before-the-end-date" />
			<liferay-ui:error exception="<%= CalendarBookingRecurrenceException.class %>" message="the-last-repeating-date-should-come-after-the-event-start-date" />

			<liferay-asset:asset-categories-error />

			<liferay-asset:asset-tags-error />

			<aui:model-context bean="<%= calendarBooking %>" model="<%= CalendarBooking.class %>" />

			<clay:sheet-section>
				<aui:input defaultLanguageId="<%= LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale()) %>" name="title" />

				<div class="<%= allDay ? "allday-class-active" : "" %>" id="<portlet:namespace />startDateContainer">
					<aui:input ignoreRequestValue="<%= true %>" label="starts" name="startTime" required="<%= true %>" timeFormat="<%= timeFormat %>" value="<%= startTimeJCalendar %>" />
				</div>

				<div class="<%= allDay ? "allday-class-active" : "" %>" id="<portlet:namespace />endDateContainer">
					<aui:input ignoreRequestValue="<%= true %>" label="ends" name="endTime" required="<%= true %>" timeFormat="<%= timeFormat %>" value="<%= endTimeJCalendar %>" />
				</div>

				<aui:input checked="<%= allDay %>" name="allDay" />

				<aui:field-wrapper cssClass="calendar-portlet-recurrence-container" inlineField="<%= true %>" label="">
					<aui:input checked="<%= recurring %>" name="repeat" type="checkbox" />

					<a class="calendar-portlet-recurrence-summary" href="javascript:void(0);" id="<portlet:namespace />summary"></a>
				</aui:field-wrapper>

				<aui:input defaultLanguageId="<%= LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale()) %>" name="description" />
			</clay:sheet-section>

			<clay:panel-group>
				<clay:panel
					displayTitle='<%= LanguageUtil.get(request, "details") %>'
				>
					<div class="panel-body">
						<aui:select label="calendar" name="calendarId">

							<%
							for (Calendar curCalendar : manageableCalendars) {
								if ((calendarBooking != null) && (curCalendar.getCalendarId() != calendarId) && (CalendarBookingLocalServiceUtil.getCalendarBookingsCount(curCalendar.getCalendarId(), calendarBooking.getParentCalendarBookingId()) > 0)) {
									continue;
								}

								CalendarResource curCalendarResource = curCalendar.getCalendarResource();

								String calendarName = curCalendar.getName(locale);
								String calendarResourceName = curCalendarResource.getName(locale);

								if (!calendarName.equals(calendarResourceName)) {
									calendarName = calendarResourceName + StringPool.SPACE + StringPool.DASH + StringPool.SPACE + calendarName;
								}
							%>

								<aui:option selected="<%= curCalendar.getCalendarId() == calendarId %>" value="<%= curCalendar.getCalendarId() %>"><%= HtmlUtil.escape(calendarName) %></aui:option>

							<%
							}
							%>

						</aui:select>

						<aui:input name="location" />

						<liferay-expando:custom-attributes-available
							className="<%= CalendarBooking.class.getName() %>"
						>
							<liferay-expando:custom-attribute-list
								className="<%= CalendarBooking.class.getName() %>"
								classPK="<%= (calendarBooking != null) ? calendarBooking.getCalendarBookingId() : 0 %>"
								editable="<%= true %>"
								label="<%= true %>"
							/>
						</liferay-expando:custom-attributes-available>

						<c:if test="<%= calendarBooking == null %>">
							<aui:field-wrapper label="permissions">
								<liferay-ui:input-permissions
									modelName="<%= CalendarBooking.class.getName() %>"
								/>
							</aui:field-wrapper>
						</c:if>
					</div>
				</clay:panel>

				<clay:panel
					displayTitle='<%= LanguageUtil.get(request, "invitations") %>'
				>
					<div class="panel-body">
						<c:if test="<%= invitable %>">
							<aui:input inputCssClass="calendar-portlet-invite-resources-input" label="" name="inviteResource" placeholder="add-people-sites-rooms" type="text" />

							<hr class="separator" />
						</c:if>

						<clay:row
							cssClass="calendar-booking-invitations"
						>
							<clay:col
								md="<%= (calendarBooking != null) ? String.valueOf(3) : String.valueOf(4) %>"
							>
								<label class="field-label">
									<liferay-ui:message key="pending[calendar]" /> (<span id="<portlet:namespace />pendingCounter"><%= pendingCalendarsJSONArray.length() %></span>)
								</label>

								<div class="calendar-portlet-calendar-list" id="<portlet:namespace />calendarListPending"></div>
							</clay:col>

							<clay:col
								md="<%= (calendarBooking != null) ? String.valueOf(3) : String.valueOf(4) %>"
							>
								<label class="field-label">
									<liferay-ui:message key="accepted" /> (<span id="<portlet:namespace />acceptedCounter"><%= acceptedCalendarsJSONArray.length() %></span>)
								</label>

								<div class="calendar-portlet-calendar-list" id="<portlet:namespace />calendarListAccepted"></div>
							</clay:col>

							<clay:col
								md="<%= (calendarBooking != null) ? String.valueOf(3) : String.valueOf(4) %>"
							>
								<label class="field-label">
									<liferay-ui:message key="declined" /> (<span id="<portlet:namespace />declinedCounter"><%= declinedCalendarsJSONArray.length() %></span>)
								</label>

								<div class="calendar-portlet-calendar-list" id="<portlet:namespace />calendarListDeclined"></div>
							</clay:col>

							<c:if test="<%= calendarBooking != null %>">
								<clay:col
									md="6"
								>
									<label class="field-label">
										<liferay-ui:message key="maybe" /> (<span id="<portlet:namespace />maybeCounter"><%= maybeCalendarsJSONArray.length() %></span>)
									</label>

									<div class="calendar-portlet-calendar-list" id="<portlet:namespace />calendarListMaybe"></div>
								</clay:col>
							</c:if>

							<clay:col
								size="12"
							>
								<div class="calendar-portlet-list-header toggler-header-collapsed" id="<portlet:namespace />checkAvailability">
									<span class="calendar-portlet-list-arrow"></span>

									<span class="calendar-portlet-list-text"><liferay-ui:message key="resources-availability" /></span>
								</div>

								<div class="calendar-portlet-availability">
									<div class="toggler-content-collapsed" id="<portlet:namespace />schedulerContainer">
										<div id="<portlet:namespace />message"></div>

										<liferay-util:include page="/scheduler.jsp" servletContext="<%= application %>">
											<liferay-util:param name="activeView" value="<%= activeView %>" />
											<liferay-util:param name="date" value="<%= String.valueOf(startTime) %>" />
											<liferay-util:param name="filterCalendarBookings" value='<%= liferayPortletResponse.getNamespace() + "filterCalendarBookings" %>' />
											<liferay-util:param name="hideAgendaView" value="<%= Boolean.TRUE.toString() %>" />
											<liferay-util:param name="hideMonthView" value="<%= Boolean.TRUE.toString() %>" />
											<liferay-util:param name="preventPersistence" value="<%= Boolean.TRUE.toString() %>" />
											<liferay-util:param name="readOnly" value="<%= Boolean.TRUE.toString() %>" />
										</liferay-util:include>
									</div>
								</div>
							</clay:col>
						</clay:row>
					</div>
				</clay:panel>

				<clay:panel
					displayTitle='<%= LanguageUtil.get(request, "reminders") %>'
				>
					<div class="panel-body">
						<div class="calendar-booking-reminders" id="<portlet:namespace />reminders"></div>
					</div>
				</clay:panel>

				<clay:panel
					displayTitle='<%= LanguageUtil.get(request, "categorization") %>'
				>
					<div class="panel-body">
						<liferay-asset:asset-categories-selector
							className="<%= CalendarBooking.class.getName() %>"
							classPK="<%= calendarBookingId %>"
							visibilityTypes="<%= AssetVocabularyConstants.VISIBILITY_TYPES %>"
						/>

						<liferay-asset:asset-tags-selector
							className="<%= CalendarBooking.class.getName() %>"
							classPK="<%= calendarBookingId %>"
						/>
					</div>
				</clay:panel>

				<clay:panel
					displayTitle='<%= LanguageUtil.get(request, "related-assets") %>'
				>
					<div class="panel-body">
						<liferay-asset:input-asset-links
							className="<%= CalendarBooking.class.getName() %>"
							classPK="<%= calendarBookingId %>"
						/>
					</div>
				</clay:panel>
			</clay:panel-group>

			<%@ include file="/calendar_booking_recurrence_container.jspf" %>
		</clay:sheet>
	</div>

	<aui:button-row cssClass="d-block">
		<div class="alert alert-info <%= (hasWorkflowDefinitionLink && approved) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />approvalProcessAlert">
			<liferay-ui:message arguments="<%= ResourceActionsUtil.getModelResource(locale, CalendarBooking.class.getName()) %>" key="this-x-is-approved.-publishing-these-changes-will-cause-it-to-be-unpublished-and-go-through-the-approval-process-again" translateArguments="<%= false %>" />
		</div>

		<%
		String publishButtonLabel = "publish";

		if (hasWorkflowDefinitionLink) {
			publishButtonLabel = "submit-for-workflow";
		}
		%>

		<aui:button cssClass="ml-0" name="publishButton" type="submit" value="<%= publishButtonLabel %>" />

		<aui:button name="saveButton" primary="<%= false %>" type="submit" value="save-as-draft" />

		<c:if test="<%= (calendarBooking != null) && CalendarBookingPermission.contains(themeDisplay.getPermissionChecker(), calendarBooking, ActionKeys.PERMISSIONS) %>">
			<liferay-security:permissionsURL
				modelResource="<%= CalendarBooking.class.getName() %>"
				modelResourceDescription="<%= calendarBooking.getTitle(locale) %>"
				redirect="<%= redirectURL %>"
				resourceGroupId="<%= calendarBooking.getGroupId() %>"
				resourcePrimKey="<%= String.valueOf(calendarBooking.getCalendarBookingId()) %>"
				var="permissionsCalendarBookingURL"
			/>

			<aui:button href="<%= permissionsCalendarBookingURL %>" value="permissions" />
		</c:if>
	</aui:button-row>
</aui:form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"namespace", "<portlet:namespace />"
		).build()
	%>'
	module="{schedulerEventValidator} from calendar-web"
/>

<aui:script>
	function <portlet:namespace />filterCalendarBookings(calendarBooking) {
		return calendarBooking.calendarBookingId !== '<%= calendarBookingId %>';
	}

	function <portlet:namespace />resolver(data) {
		const answers = data.answers;

		if (!answers.cancel) {
			const allFollowingNode = document.getElementById(
				'<portlet:namespace />allFollowing'
			);
			const updateCalendarBookingInstanceNode = document.getElementById(
				'<portlet:namespace />updateCalendarBookingInstance'
			);

			if (allFollowingNode) {
				allFollowingNode.value = !!answers.allFollowing;
			}

			if (updateCalendarBookingInstanceNode) {
				updateCalendarBookingInstanceNode.value = !!answers.updateInstance;
			}

			submitForm(document.<portlet:namespace />fm);
		}
	}

	Liferay.provide(
		window,
		'<portlet:namespace />updateCalendarBooking',
		() => {
			<c:if test="<%= invitable %>">
				const calendarContainer = Liferay.component(
					'<portlet:namespace />calendarContainer'
				);

				const childCalendarIds = Object.keys(
					calendarContainer.get('availableCalendars')
				);

				const calendarIdNode = document.getElementById(
					'<portlet:namespace />calendarId'
				);

				if (!calendarIdNode) {
					return;
				}

				const index = childCalendarIds.indexOf(calendarIdNode.value);

				if (index > -1) {
					childCalendarIds.splice(index, 1);
				}

				const childCalendarIdsNode = document.getElementById(
					'<portlet:namespace />childCalendarIds'
				);

				if (childCalendarIdsNode) {
					childCalendarIdsNode.value = childCalendarIds.join(',');
				}
			</c:if>

			Liferay.CalendarMessageUtil.promptSchedulerEventUpdate({
				calendarName: '<%= HtmlUtil.escapeJS(calendar.getName(locale)) %>',
				hasChild: <%= hasChildCalendarBookings %>,
				masterBooking: <%= masterBooking %>,
				recurring: <%= recurring %>,
				resolver: <portlet:namespace />resolver,
			});
		},
		['liferay-calendar-message-util', 'json']
	);

	<%
	String titleCurrentValue = ParamUtil.getString(request, "titleCurrentValue");
	%>

	<c:if test="<%= Validator.isNotNull(titleCurrentValue) && ((calendarBooking == null) || !Objects.equals(titleCurrentValue, calendarBooking.getTitle(locale))) %>">
		document.<portlet:namespace />fm.<portlet:namespace />title.value =
			'<%= HtmlUtil.escapeJS(titleCurrentValue) %>';
		document.<portlet:namespace />fm.<portlet:namespace />title_<%= LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale()) %>.value =
			'<%= HtmlUtil.escapeJS(titleCurrentValue) %>';
	</c:if>
</aui:script>

<aui:script use="json,liferay-calendar-date-picker-sanitizer,liferay-calendar-interval-selector,liferay-calendar-interval-selector-scheduler-event-link,liferay-calendar-list,liferay-calendar-recurrence-util,liferay-calendar-reminders,liferay-calendar-simple-menu,liferay-calendar-util">
	var calendarContainer = Liferay.component(
		'<portlet:namespace />calendarContainer'
	);

	var defaultCalendarId = <%= calendarId %>;

	var scheduler = window.<portlet:namespace />scheduler;

	var workflowActionNode = document.getElementById(
		'<portlet:namespace />workflowAction'
	);

	var saveButton = document.getElementById('<portlet:namespace />saveButton');

	if (saveButton) {
		saveButton.addEventListener('click', () => {
			workflowActionNode.value = '<%= WorkflowConstants.ACTION_SAVE_DRAFT %>';
		});
	}

	var publishButton = document.getElementById(
		'<portlet:namespace />publishButton'
	);

	if (publishButton) {
		publishButton.addEventListener('click', () => {
			workflowActionNode.value = '<%= WorkflowConstants.ACTION_PUBLISH %>';
		});
	}

	var syncCalendarsMap = function () {
		calendarContainer.syncCalendarsMap([
			window.<portlet:namespace />calendarListAccepted,
			window.<portlet:namespace />calendarListDeclined,

			<c:if test="<%= calendarBooking != null %>">
				window.<portlet:namespace />calendarListMaybe,
			</c:if>

			window.<portlet:namespace />calendarListPending,
		]);

		A.each(calendarContainer.get('availableCalendars'), (item, index) => {
			item.set('disabled', true);
		});
	};

	var calendarsMenu = calendarContainer.getCalendarsMenu({
		content: '#<portlet:namespace />schedulerContainer',
		defaultCalendarId: defaultCalendarId,
		header: '#<portlet:namespace />checkAvailability',
		invitable: <%= invitable %>,
		scheduler: scheduler,
	});

	window.<portlet:namespace />calendarListPending = new Liferay.CalendarList({
		after: {
			'calendarsChange': function (event) {
				var pendingCounterLabel = document.getElementById(
					'<portlet:namespace />pendingCounter'
				);

				if (pendingCounterLabel) {
					pendingCounterLabel.innerHTML = event.newVal.length;
				}

				syncCalendarsMap();

				scheduler.load();
			},
			'scheduler-calendar:visibleChange': syncCalendarsMap,
		},
		boundingBox: '#<portlet:namespace />calendarListPending',
		calendars: <%= pendingCalendarsJSONArray %>,
		scheduler: <portlet:namespace />scheduler,
		simpleMenu: calendarsMenu,
		strings: {
			emptyMessage: '<liferay-ui:message key="no-pending-invites" />',
		},
	}).render();

	window.<portlet:namespace />calendarListAccepted = new Liferay.CalendarList({
		after: {
			'calendarsChange': function (event) {
				var acceptedCounterLabel = document.getElementById(
					'<portlet:namespace />acceptedCounter'
				);

				if (acceptedCounterLabel) {
					acceptedCounterLabel.innerHTML = event.newVal.length;
				}

				syncCalendarsMap();

				scheduler.load();
			},
			'scheduler-calendar:visibleChange': syncCalendarsMap,
		},
		boundingBox: '#<portlet:namespace />calendarListAccepted',
		calendars: <%= acceptedCalendarsJSONArray %>,
		scheduler: <portlet:namespace />scheduler,
		simpleMenu: calendarsMenu,
		strings: {
			emptyMessage: '<liferay-ui:message key="no-accepted-invites" />',
		},
	}).render();

	window.<portlet:namespace />reinvitableCalendarIds = [];

	window.<portlet:namespace />calendarListDeclined = new Liferay.CalendarList({
		after: {
			'calendarRemoved': function (event) {
				var calendar = event.calendar;

				if (calendar) {
					var calendarId = calendar.get('calendarId');

					window.<portlet:namespace />reinvitableCalendarIds.push(
						calendarId
					);
				}
			},
			'calendarsChange': function (event) {
				var declinedCounterLabel = document.getElementById(
					'<portlet:namespace />declinedCounter'
				);

				if (declinedCounterLabel) {
					declinedCounterLabel.innerHTML = event.newVal.length;
				}

				syncCalendarsMap();

				scheduler.load();
			},
			'scheduler-calendar:visibleChange': syncCalendarsMap,
		},
		boundingBox: '#<portlet:namespace />calendarListDeclined',
		calendars: <%= declinedCalendarsJSONArray %>,
		scheduler: <portlet:namespace />scheduler,
		simpleMenu: calendarsMenu,
		strings: {
			emptyMessage: '<liferay-ui:message key="no-declined-invites" />',
		},
	}).render();

	<c:if test="<%= calendarBooking != null %>">
		window.<portlet:namespace />calendarListMaybe = new Liferay.CalendarList({
			after: {
				'calendarsChange': function (event) {
					var maybeCounterLabel = document.getElementById(
						'<portlet:namespace />maybeCounter'
					);

					if (maybeCounterLabel) {
						maybeCounterLabel.innerHTML = event.newVal.length;
					}

					syncCalendarsMap();

					scheduler.load();
				},
				'scheduler-calendar:visibleChange': syncCalendarsMap,
			},
			boundingBox: '#<portlet:namespace />calendarListMaybe',
			calendars: <%= maybeCalendarsJSONArray %>,
			scheduler: <portlet:namespace />scheduler,
			simpleMenu: calendarsMenu,
			strings: {
				emptyMessage: '<liferay-ui:message key="no-outstanding-invites" />',
			},
		}).render();
	</c:if>

	syncCalendarsMap();

	new Liferay.DatePickerSanitizer({
		datePickers: [
			Liferay.component('<portlet:namespace />endTimeDatePicker'),
			Liferay.component('<portlet:namespace />endTimeDatePicker'),
		],
		defaultDate: new Date(
			<%= endTimeYear %>,
			<%= endTimeMonth %>,
			<%= endTimeDay %>,
			<%= endTimeHour %>,
			<%= endTimeMinute %>
		),
		maximumDate: new Date(2099, 11, 31, 23, 59, 59, 999),
		minimumDate: new Date(0),
	});

	var intervalSelector = new Liferay.IntervalSelector({
		endDatePicker: Liferay.component('<portlet:namespace />endTimeDatePicker'),
		endTimePicker: Liferay.component(
			'<portlet:namespace />endTimeTimeTimePicker'
		),
		startDatePicker: Liferay.component(
			'<portlet:namespace />startTimeDatePicker'
		),
		startTimePicker: Liferay.component(
			'<portlet:namespace />startTimeTimeTimePicker'
		),
	});

	var placeholderSchedulerEvent = new Liferay.SchedulerEvent({
		borderColor: '#000',
		borderStyle: 'dashed',
		borderWidth: '2px',
		color: '#F8F8F8',
		content: '',
		editingEvent: true,
		endDate: new Date(
			<%= endTimeYear %>,
			<%= endTimeMonth %>,
			<%= endTimeDay %>,
			<%= endTimeHour %>,
			<%= endTimeMinute %>
		),
		preventDateChange: true,
		scheduler: scheduler,
		startDate: new Date(
			<%= startTimeYear %>,
			<%= startTimeMonth %>,
			<%= startTimeDay %>,
			<%= startTimeHour %>,
			<%= startTimeMinute %>
		),
	});

	new Liferay.IntervalSelectorSchedulerEventLink({
		intervalSelector: intervalSelector,
		schedulerEvent: placeholderSchedulerEvent,
	});

	scheduler.after('*:load', (event) => {
		scheduler.addEvents(placeholderSchedulerEvent);

		scheduler.syncEventsUI();
	});

	<c:if test="<%= invitable %>">
		var manageableCalendars = {};

		<%= CalendarUtil.toCalendarsJSONArray(themeDisplay, manageableCalendars) %>.forEach(
			(item, index) => {
				manageableCalendars[item.calendarId] = item;
			}
		);

		var calendarIdNode = document.getElementById('<portlet:namespace />calendarId');

		if (calendarIdNode) {
			calendarIdNode.addEventListener('change', (event) => {
				var calendarId = parseInt(event.target.value, 10);

				var calendar = manageableCalendars[calendarId];

				var calendarListPendingComponent =
					<portlet:namespace />calendarListPending;

				var calendarListCollection = [
					<portlet:namespace />calendarListAccepted,
					<portlet:namespace />calendarListDeclined,

					<c:if test="<%= calendarBooking != null %>">
						<portlet:namespace />calendarListMaybe,
					</c:if>

					calendarListPendingComponent,
				];

				calendarListCollection.forEach((calendarList, index) => {
					calendarList.remove(calendarList.getCalendar(calendarId));
					calendarList.remove(calendarList.getCalendar(defaultCalendarId));
				});

				calendarListPendingComponent.add(calendar);

				defaultCalendarId = calendarId;

				var approvalProcessAlert = document.getElementById(
					'<portlet:namespace />approvalProcessAlert'
				);

				if (calendar.hasWorkflowDefinitionLink) {
					approvalProcessAlert.classList.toggle('hide', <%= !approved %>);

					publishButton.innerHTML =
						'<%= HtmlUtil.escapeJS(LanguageUtil.get(request, "submit-for-workflow")) %>';
				}
				else {
					approvalProcessAlert.classList.toggle('hide');

					publishButton.innerHTML =
						'<%= HtmlUtil.escapeJS(LanguageUtil.get(request, "publish")) %>';
				}
			});
		}

		var inviteResourcesInput = A.one('#<portlet:namespace />inviteResource');

		<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="calendarResources" var="calendarResourcesURL" />

		calendarContainer.createCalendarsAutoComplete(
			'<%= calendarResourcesURL %>',
			inviteResourcesInput,
			(event) => {
				var calendar = event.result.raw;

				calendar.disabled = true;

				addToList(calendar);

				inviteResourcesInput.val('');
			}
		);

		var addToList = function (calendar) {
			if (
				calendar.classNameId ==
				<%= ClassNameLocalServiceUtil.getClassNameId(CalendarResource.class) %>
			) {
				var remoteServices = Liferay.component(
					'<portlet:namespace />remoteServices'
				);

				remoteServices.hasExclusiveCalendarBooking(
					calendar.calendarId,
					placeholderSchedulerEvent.get('startDate'),
					placeholderSchedulerEvent.get('endDate'),
					(result) => {
						if (result) {
							<portlet:namespace />calendarListDeclined.add(calendar);
						}
						else {
							<portlet:namespace />calendarListPending.add(calendar);
						}
					}
				);
			}
			else {
				<portlet:namespace />calendarListPending.add(calendar);
			}
		};
	</c:if>

	window.<portlet:namespace />reminders = new Liferay.Reminders({
		portletNamespace: '<portlet:namespace />',
		render: '#<portlet:namespace />reminders',
		values: [
			{
				interval: <%= firstReminder %>,
				type: '<%= HtmlUtil.escapeJS(firstReminderType) %>',
			},
			{
				interval: <%= secondReminder %>,
				type: '<%= HtmlUtil.escapeJS(secondReminderType) %>',
			},
		],
	});

	<%
	defaultEndTimeJCalendar = (java.util.Calendar)defaultStartTimeJCalendar.clone();

	defaultEndTimeJCalendar.add(java.util.Calendar.MINUTE, defaultDuration);
	%>

	var maxLength = Liferay.AUI.getDateFormat().replace(/%[mdY]/gm, '').length + 8;

	var endTimeInput = document.getElementById('<portlet:namespace />endTime');

	if (endTimeInput) {
		endTimeInput.maxLength = maxLength;
	}

	var startTimeInput = document.getElementById('<portlet:namespace />startTime');

	if (startTimeInput) {
		startTimeInput.maxLength = maxLength;
	}

	var endDateContainer = document.getElementById(
		'<portlet:namespace />endDateContainer'
	);

	endDateContainer
		.querySelector('label')
		.setAttribute('id', '<portlet:namespace />endDateContainerLabel');

	endDateContainer.querySelectorAll('input').forEach((element) => {
		element.setAttribute(
			'aria-labeledby',
			'<portlet:namespace />endDateContainerLabel'
		);
	});

	var startDateContainer = document.getElementById(
		'<portlet:namespace />startDateContainer'
	);

	startDateContainer
		.querySelector('label')
		.setAttribute('id', '<portlet:namespace />startDateContainerLabel');

	startDateContainer.querySelectorAll('input').forEach((element) => {
		element.setAttribute(
			'aria-labeledby',
			'<portlet:namespace />startDateContainerLabel'
		);
	});

	var allDayCheckbox = document.getElementById('<portlet:namespace />allDay');

	if (allDayCheckbox) {
		allDayCheckbox.addEventListener('click', (event) => {
			var endTimeHours;
			var endTimeMinutes;
			var startTimeHours;
			var startTimeMinutes;

			var checked = allDayCheckbox.checked;

			if (checked) {
				placeholderSchedulerEvent.set('allDay', true);

				startTimeHours = 0;
				startTimeMinutes = 0;
				endTimeHours = 23;
				endTimeMinutes = 59;
			}
			else {
				placeholderSchedulerEvent.set('allDay', false);

				endDateContainer.style.display = 'block';

				startTimeHours =
					<%= defaultStartTimeJCalendar.get(java.util.Calendar.HOUR_OF_DAY) %>;
				startTimeMinutes =
					<%= defaultStartTimeJCalendar.get(java.util.Calendar.MINUTE) %>;
				endTimeHours =
					<%= defaultEndTimeJCalendar.get(java.util.Calendar.HOUR_OF_DAY) %>;
				endTimeMinutes =
					<%= defaultEndTimeJCalendar.get(java.util.Calendar.MINUTE) %>;
			}

			updateTimePickersValues(
				startTimeHours,
				startTimeMinutes,
				endTimeHours,
				endTimeMinutes
			);

			endDateContainer.classList.toggle('allday-class-active', checked);
			startDateContainer.classList.toggle('allday-class-active', checked);

			scheduler.syncEventsUI();
		});
	}

	var updateTimePickersValues = function (
		startTimeHours,
		startTimeMinutes,
		endTimeHours,
		endTimeMinutes
	) {
		var endDatePicker = intervalSelector.get('endDatePicker');
		var startDatePicker = intervalSelector.get('startDatePicker');

		var endDate = endDatePicker.getDate();
		var startDate = startDatePicker.getDate();

		startDate.setHours(startTimeHours);
		startDate.setMinutes(startTimeMinutes);

		endDate.setHours(endTimeHours);
		endDate.setMinutes(endTimeMinutes);

		intervalSelector.setDuration(endDate.valueOf() - startDate.valueOf());

		var endTimePicker = intervalSelector.get('endTimePicker');
		var startTimePicker = intervalSelector.get('startTimePicker');

		startTimePicker.selectDates([startDate]);
		startTimePicker.updateTime(startDate);

		endTimePicker.selectDates([endDate]);
		endTimePicker.updateTime(endDate);
	};

	scheduler.load();

	var descriptionBoundingBox = document.getElementById(
		'<portlet:namespace />descriptionBoundingBox'
	);

	if (descriptionBoundingBox) {
		const observer = new MutationObserver((mutations, observer) => {
			outer: for (const mutation of mutations) {
				for (const node of mutation.addedNodes) {
					if (node.tagName === 'IFRAME') {
						node.addEventListener('load', () => {
							const iframeBody = node.contentDocument?.body;

							if (iframeBody) {
								iframeBody.setAttribute(
									'aria-label',
									'<%= LanguageUtil.get(request, "description") %>'
								);
							}

							observer.disconnect();
						});

						break outer;
					}
				}
			}
		});

		observer.observe(descriptionBoundingBox, {childList: true, subtree: true});
	}
</aui:script>