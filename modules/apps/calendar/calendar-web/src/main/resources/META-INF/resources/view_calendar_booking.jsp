<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-util:dynamic-include key="com.liferay.calendar.web#/view_calendar_booking.jsp#pre" />

<%
String backURL = ParamUtil.getString(request, "backURL");

int instanceIndex = BeanParamUtil.getInteger(calendarBooking, request, "instanceIndex");

calendarBooking = RecurrenceUtil.getCalendarBookingInstance(calendarBooking, instanceIndex);

Calendar calendar = calendarBooking.getCalendar();

long startTime = calendarBooking.getStartTime();

java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(startTime, userTimeZone);

long endTime = calendarBooking.getEndTime();

java.util.Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(endTime, userTimeZone);

AssetEntry layoutAssetEntry = AssetEntryLocalServiceUtil.getEntry(CalendarBooking.class.getName(), calendarBooking.getCalendarBookingId());

portletDisplay.setShowBackIcon(Validator.isNotNull(backURL));
%>

<c:if test="<%= CalendarPermission.contains(themeDisplay.getPermissionChecker(), calendar, CalendarActionKeys.VIEW_BOOKING_DETAILS) %>">
	<div class="mt-4">
		<clay:container-fluid
			class="mt-4"
		>
			<div class="panel panel-secondary">
				<liferay-ui:header
					backURL="<%= backURL %>"
					cssClass="panel-heading"
					localizeTitle="<%= false %>"
					title="<%= calendarBooking.getTitle(locale) %>"
				/>

				<aui:fieldset markupView="lexicon">
					<dl class="property-list">
						<dt>
							<liferay-ui:message key="status" />:
						</dt>
						<dd>
							<aui:workflow-status markupView="lexicon" model="<%= CalendarBooking.class %>" showHelpMessage="<%= false %>" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= calendarBooking.getStatus() %>" />
						</dd>
						<dt>
							<liferay-ui:message key="starts" />:
						</dt>

						<c:choose>
							<c:when test="<%= calendarBooking.isAllDay() %>">
								<dd>
									<%= utcLongDateJFormat.format(startTimeJCalendar.getTime()) %>
								</dd>
							</c:when>
							<c:otherwise>
								<dd>
									<%= longDateJFormat.format(startTimeJCalendar.getTime()) %>, <%= timeJFormat.format(startTimeJCalendar.getTime()) %>
								</dd>
							</c:otherwise>
						</c:choose>

						<dt>
							<liferay-ui:message key="ends" />:
						</dt>

						<c:choose>
							<c:when test="<%= calendarBooking.isAllDay() %>">
								<dd>
									<%= utcLongDateJFormat.format(endTimeJCalendar.getTime()) %>
								</dd>
							</c:when>
							<c:otherwise>
								<dd>
									<%= longDateJFormat.format(endTimeJCalendar.getTime()) %>, <%= timeJFormat.format(endTimeJCalendar.getTime()) %>
								</dd>
							</c:otherwise>
						</c:choose>

						<%
						List<CalendarBooking> childCalendarBookings = calendarDisplayContext.getChildCalendarBookings(calendarBooking);
						%>

						<c:if test="<%= !childCalendarBookings.isEmpty() %>">
							<dt>
								<liferay-ui:message key="resources" />:
							</dt>
							<dd>

								<%
								List<String> calendarResourcesNames = new ArrayList<String>();

								for (CalendarBooking childCalendarBooking : childCalendarBookings) {
									CalendarResource calendarResource = childCalendarBooking.getCalendarResource();

									calendarResourcesNames.add(calendarResource.getName(locale));
								}
								%>

								<%= HtmlUtil.escape(StringUtil.merge(calendarResourcesNames, ", ")) %>
							</dd>
						</c:if>

						<c:if test="<%= Validator.isNotNull(calendarBooking.getLocation()) %>">
							<dt>
								<liferay-ui:message key="location" />:
							</dt>
							<dd>
								<span class="location"><%= HtmlUtil.escape(calendarBooking.getLocation()) %></span>
							</dd>
						</c:if>

						<c:if test="<%= Validator.isNotNull(calendarBooking.getRecurrence()) %>">
							<dt>
								<liferay-ui:message key="repeat" />:
							</dt>
							<dd>
								<span id="<portlet:namespace />recurrenceSummary"></span>
							</dd>
						</c:if>
					</dl>

					<liferay-expando:custom-attributes-available
						className="<%= CalendarBooking.class.getName() %>"
					>
						<liferay-expando:custom-attribute-list
							className="<%= CalendarBooking.class.getName() %>"
							classPK="<%= calendarBooking.getCalendarBookingId() %>"
							editable="<%= false %>"
							label="<%= true %>"
						/>
					</liferay-expando:custom-attributes-available>

					<p>
						<%= calendarBooking.getDescription(locale) %>
					</p>

					<div class="entry-categories">
						<liferay-asset:asset-categories-summary
							className="<%= CalendarBooking.class.getName() %>"
							classPK="<%= calendarBooking.getCalendarBookingId() %>"
						/>
					</div>

					<div class="entry-tags">
						<liferay-asset:asset-tags-summary
							className="<%= CalendarBooking.class.getName() %>"
							classPK="<%= calendarBooking.getCalendarBookingId() %>"
							message="tags"
						/>
					</div>

					<div class="entry-links">
						<liferay-asset:asset-links
							assetEntryId="<%= layoutAssetEntry.getEntryId() %>"
						/>
					</div>

					<c:if test="<%= calendar.isEnableRatings() %>">
						<div class="entry-ratings">
							<liferay-ratings:ratings
								className="<%= CalendarBooking.class.getName() %>"
								classPK="<%= calendarBooking.getCalendarBookingId() %>"
								inTrash="<%= calendarBooking.isInTrash() %>"
							/>
						</div>
					</c:if>

					<c:if test="<%= calendar.isEnableComments() %>">
						<liferay-comment:discussion
							className="<%= CalendarBooking.class.getName() %>"
							classPK="<%= calendarBooking.getCalendarBookingId() %>"
							formName="fm2"
							ratingsEnabled="<%= true %>"
							redirect="<%= currentURL %>"
							userId="<%= calendarBooking.getUserId() %>"
						/>
					</c:if>
				</aui:fieldset>

				<portlet:actionURL name="invokeTransition" var="invokeTransitionURL" />

				<c:if test="<%= Validator.isNotNull(calendarBooking.getRecurrence()) %>">
					<%@ include file="/calendar_booking_recurrence_language_keys.jspf" %>

					<aui:script use="liferay-calendar-recurrence-util">
						var endValue = 'never';
						var untilDate = null;

						<%
						Recurrence recurrence = calendarBooking.getRecurrenceObj();

						java.util.Calendar untilJCalendar = recurrence.getUntilJCalendar();
						%>

						<c:choose>
							<c:when test="<%= untilJCalendar != null %>">
								endValue = 'on';

								untilDate = new Date(
									<%= untilJCalendar.get(java.util.Calendar.YEAR) %>,
									<%= untilJCalendar.get(java.util.Calendar.MONTH) %>,
									<%= untilJCalendar.get(java.util.Calendar.DATE) %>
								);
							</c:when>
							<c:when test="<%= recurrence.getCount() > 0 %>">
								endValue = 'after';
							</c:when>
						</c:choose>

						<%
						Frequency frequency = recurrence.getFrequency();

						PositionalWeekday positionalWeekday = recurrence.getPositionalWeekday();

						JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

						List<String> weekdayValues = new ArrayList<>();

						for (Weekday weekday : recurrence.getWeekdays()) {
							weekdayValues.add(weekday.getValue());
						}
						%>

						var positionalWeekday = null;

						<c:if test="<%= (frequency.equals(Frequency.MONTHLY) || frequency.equals(Frequency.YEARLY)) && (positionalWeekday != null) %>">
							positionalWeekday = {
								month: <%= startTimeJCalendar.get(java.util.Calendar.MONTH) %>,
								position: <%= positionalWeekday.getPosition() %>,
								weekday: '<%= positionalWeekday.getWeekday() %>',
							};
						</c:if>

						var recurrence = {
							count: <%= recurrence.getCount() %>,
							endValue: endValue,
							frequency: '<%= String.valueOf(frequency) %>',
							interval: <%= recurrence.getInterval() %>,
							positionalWeekday: positionalWeekday,
							untilDate: untilDate,
							weekdays: <%= jsonSerializer.serialize(weekdayValues) %>,
						};

						var summaryNode = document.getElementById(
							'<portlet:namespace />recurrenceSummary'
						);
						var recurrenceSummary = Liferay.RecurrenceUtil.getSummary(recurrence);

						if (summaryNode) {
							summaryNode.innerHTML = recurrenceSummary;
						}
					</aui:script>
				</c:if>

				<liferay-util:dynamic-include key="com.liferay.calendar.web#/view_calendar_booking.jsp#post" />
			</div>

			<aui:form action="<%= invokeTransitionURL %>" method="post" name="fm">
				<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
				<aui:input name="calendarBookingId" type="hidden" value="<%= calendarBooking.getCalendarBookingId() %>" />
				<aui:input name="startTime" type="hidden" value="<%= calendarBooking.getStartTime() %>" />
				<aui:input name="status" type="hidden" />
				<aui:input name="updateInstance" type="hidden" value="false" />
				<aui:input name="allFollowing" type="hidden" value="false" />

				<aui:button-row>

					<%
					boolean hasManageBookingsPermission = CalendarPermission.contains(permissionChecker, calendar, CalendarActionKeys.MANAGE_BOOKINGS) && !calendarBooking.isDraft();
					boolean hasWorkflowInstanceLink = WorkflowInstanceLinkLocalServiceUtil.hasWorkflowInstanceLink(themeDisplay.getCompanyId(), calendarBooking.getGroupId(), CalendarBooking.class.getName(), calendarBooking.getCalendarBookingId());
					%>

					<c:if test="<%= hasManageBookingsPermission && !hasWorkflowInstanceLink %>">
						<c:if test="<%= calendarBooking.getStatus() != WorkflowConstants.STATUS_APPROVED %>">
							<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "invokeTransition(" + WorkflowConstants.STATUS_APPROVED + ");" %>' value="accept" />
						</c:if>

						<c:if test="<%= calendarBooking.getStatus() != CalendarBookingWorkflowConstants.STATUS_MAYBE %>">
							<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "invokeTransition(" + CalendarBookingWorkflowConstants.STATUS_MAYBE + ");" %>' value="maybe" />
						</c:if>

						<c:if test="<%= calendarBooking.getStatus() != WorkflowConstants.STATUS_DENIED %>">
							<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "invokeTransition(" + WorkflowConstants.STATUS_DENIED + ");" %>' value="decline" />
						</c:if>
					</c:if>
				</aui:button-row>
			</aui:form>

			<aui:script>
				function <portlet:namespace />invokeTransition(status) {
					document.<portlet:namespace />fm.<portlet:namespace />status.value = status;

					if (<%= calendarBooking.isRecurring() %>) {
						Liferay.RecurrenceUtil.openConfirmationPanel(
							'invokeTransition',
							() => {
								document.<portlet:namespace />fm.<portlet:namespace />updateInstance.value =
									'true';
								document.<portlet:namespace />fm.<portlet:namespace />allFollowing.value =
									'false';
								submitForm(document.<portlet:namespace />fm);
							},
							() => {
								document.<portlet:namespace />fm.<portlet:namespace />updateInstance.value =
									'true';
								document.<portlet:namespace />fm.<portlet:namespace />allFollowing.value =
									'true';
								submitForm(document.<portlet:namespace />fm);
							},
							() => {
								submitForm(document.<portlet:namespace />fm);
							}
						);
					}
					else {
						submitForm(document.<portlet:namespace />fm);
					}
				}
			</aui:script>
		</clay:container-fluid>
	</div>
</c:if>