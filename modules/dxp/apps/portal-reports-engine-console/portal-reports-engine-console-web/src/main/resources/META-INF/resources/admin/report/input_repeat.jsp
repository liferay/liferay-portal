<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
int dailyType = ParamUtil.getInteger(request, "dailyType");
int monthlyType = ParamUtil.getInteger(request, "monthlyType");
int recurrenceType = ParamUtil.getInteger(request, "recurrenceType", Recurrence.NO_RECURRENCE);
int yearlyDay1 = ParamUtil.getInteger(request, "yearlyDay1", Calendar.SUNDAY);
int yearlyMonth0 = ParamUtil.getInteger(request, "yearlyMonth0", Calendar.JANUARY);
int yearlyMonth1 = ParamUtil.getInteger(request, "yearlyMonth1", Calendar.JANUARY);
int yearlyPos = ParamUtil.getInteger(request, "yearlyPos", 1);
int yearlyType = ParamUtil.getInteger(request, "yearlyType");
%>

<aui:fieldset cssClass="taglib-input-repeat">
	<div class="col-md-3" id="<portlet:namespace />eventsContainer">
		<aui:field-wrapper label="repeat" name="recurrenceType">
			<aui:input checked="<%= recurrenceType == Recurrence.NO_RECURRENCE %>" id="recurrenceTypeNever" label="never" name="recurrenceType" type="radio" value="<%= Recurrence.NO_RECURRENCE %>" />

			<aui:input checked="<%= recurrenceType == Recurrence.DAILY %>" id="recurrenceTypeDaily" label="daily" name="recurrenceType" type="radio" value="<%= Recurrence.DAILY %>" />

			<aui:input checked="<%= recurrenceType == Recurrence.WEEKLY %>" id="recurrenceTypeWeekly" label="weekly" name="recurrenceType" type="radio" value="<%= Recurrence.WEEKLY %>" />

			<aui:input checked="<%= recurrenceType == Recurrence.MONTHLY %>" id="recurrenceTypeMonthly" label="monthly" name="recurrenceType" type="radio" value="<%= Recurrence.MONTHLY %>" />

			<aui:input checked="<%= recurrenceType == Recurrence.YEARLY %>" id="recurrenceTypeYearly" label="yearly" name="recurrenceType" type="radio" value="<%= Recurrence.YEARLY %>" />
		</aui:field-wrapper>
	</div>

	<div class="col-md-9">
		<div class="<%= (recurrenceType != Recurrence.NO_RECURRENCE) ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />recurrenceTypeNeverTable">
			<liferay-ui:message key="do-not-repeat-this-event" />
		</div>

		<div class="<%= (recurrenceType != Recurrence.DAILY) ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />recurrenceTypeDailyTable">
			<aui:input checked="<%= dailyType == 0 %>" cssClass="input-container" inlineField="<%= true %>" label="recur-every" name="dailyType" type="radio" value="0" />

			<aui:input inlineField="<%= true %>" inlineLabel="right" label="day-s" maxlength="3" name="dailyInterval" size="3" type="text" value='<%= ParamUtil.getInteger(request, "dailyInterval", 1) %>' />

			<aui:input checked="<%= dailyType == 1 %>" label="every-weekday" name="dailyType" type="radio" value="1" />
		</div>

		<div class="<%= (recurrenceType != Recurrence.WEEKLY) ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />recurrenceTypeWeeklyTable">
			<aui:input inlineField="<%= true %>" inlineLabel="left" label="recur-every" maxlength="2" name="weeklyInterval" size="2" suffix="weeks-on" type="text" value='<%= ParamUtil.getInteger(request, "weeklyInterval", 1) %>' />

			<%
			String[] days = CalendarUtil.getDays(locale);
			%>

			<div class="clearfix pt-3 row weekdays">
				<div class="col-md-3">
					<aui:input inlineLabel="right" label="<%= days[0] %>" name='<%= "weeklyDayPos" + Calendar.SUNDAY %>' type="checkbox" value="<%= _getWeeklyDayPos(request, Calendar.SUNDAY) %>" />

					<aui:input inlineLabel="right" label="<%= days[4] %>" name='<%= "weeklyDayPos" + Calendar.THURSDAY %>' type="checkbox" value="<%= _getWeeklyDayPos(request, Calendar.THURSDAY) %>" />
				</div>

				<div class="col-md-3">
					<aui:input inlineLabel="right" label="<%= days[1] %>" name='<%= "weeklyDayPos" + Calendar.MONDAY %>' type="checkbox" value="<%= _getWeeklyDayPos(request, Calendar.MONDAY) %>" />

					<aui:input inlineLabel="right" label="<%= days[5] %>" name='<%= "weeklyDayPos" + Calendar.FRIDAY %>' type="checkbox" value="<%= _getWeeklyDayPos(request, Calendar.FRIDAY) %>" />
				</div>

				<div class="col-md-3">
					<aui:input inlineLabel="right" label="<%= days[2] %>" name='<%= "weeklyDayPos" + Calendar.TUESDAY %>' type="checkbox" value="<%= _getWeeklyDayPos(request, Calendar.TUESDAY) %>" />

					<aui:input inlineLabel="right" label="<%= days[6] %>" name='<%= "weeklyDayPos" + Calendar.SATURDAY %>' type="checkbox" value="<%= _getWeeklyDayPos(request, Calendar.SATURDAY) %>" />
				</div>

				<div class="col-md-3">
					<aui:input inlineLabel="right" label="<%= days[3] %>" name='<%= "weeklyDayPos" + Calendar.WEDNESDAY %>' type="checkbox" value="<%= _getWeeklyDayPos(request, Calendar.WEDNESDAY) %>" />
				</div>
			</div>
		</div>

		<div class="<%= (recurrenceType != Recurrence.MONTHLY) ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />recurrenceTypeMonthlyTable">
			<span class="field-row">
				<aui:input checked="<%= monthlyType == 0 %>" cssClass="input-container" inlineField="<%= true %>" label="day" name="monthlyType" type="radio" value="0" />

				<aui:input inlineField="<%= true %>" inlineLabel="right" label="of-every" maxlength="2" name="monthlyDay0" size="2" type="text" value='<%= ParamUtil.getInteger(request, "monthlyDay0", 15) %>' />

				<aui:input inlineField="<%= true %>" inlineLabel="right" label="month-s" maxlength="2" name="monthlyInterval0" size="2" type="text" value='<%= ParamUtil.getInteger(request, "monthlyInterval0", 1) %>' />
			</span>
			<span class="field-row">
				<aui:input checked="<%= monthlyType == 1 %>" cssClass="input-container" inlineField="<%= true %>" label="the" name="monthlyType" type="radio" value="1" />

				<aui:select cssClass="input-container" inlineField="<%= true %>" inlineLabel="left" label="" name="monthlyPos" title="month-position" value='<%= ParamUtil.getInteger(request, "monthlyPos", 1) %>'>
					<aui:option label="first" value="1" />
					<aui:option label="second" value="2" />
					<aui:option label="third" value="3" />
					<aui:option label="fourth" value="4" />
					<aui:option label="last" value="-1" />
				</aui:select>

				<aui:select cssClass="input-container" inlineField="<%= true %>" label="" name="monthlyDay1" title="first-day-of-week" value='<%= ParamUtil.getInteger(request, "monthlyDay1", Calendar.SUNDAY) %>'>
					<aui:option label="<%= days[0] %>" value="<%= Calendar.SUNDAY %>" />
					<aui:option label="<%= days[1] %>" value="<%= Calendar.MONDAY %>" />
					<aui:option label="<%= days[2] %>" value="<%= Calendar.TUESDAY %>" />
					<aui:option label="<%= days[3] %>" value="<%= Calendar.WEDNESDAY %>" />
					<aui:option label="<%= days[4] %>" value="<%= Calendar.THURSDAY %>" />
					<aui:option label="<%= days[5] %>" value="<%= Calendar.FRIDAY %>" />
					<aui:option label="<%= days[6] %>" value="<%= Calendar.SATURDAY %>" />
				</aui:select>

				<aui:input inlineField="<%= true %>" inlineLabel="left" label="of-every" maxlength="2" name="monthlyInterval1" size="2" suffix="month-s" type="text" value='<%= ParamUtil.getInteger(request, "monthlyInterval1", 1) %>' />
			</span>
		</div>

		<%
		int[] monthIds = CalendarUtil.getMonthIds();
		String[] months = CalendarUtil.getMonths(locale);
		%>

		<div class="<%= (recurrenceType != Recurrence.YEARLY) ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />recurrenceTypeYearlyTable">
			<span class="field-row">
				<aui:input checked="<%= yearlyType == 0 %>" cssClass="input-container" inlineField="<%= true %>" label="every" name="yearlyType" type="radio" value="0" />

				<aui:select cssClass="input-container" inlineField="<%= true %>" inlineLabel="left" label="" name="yearlyMonth0" title="first-month-of-year">

					<%
					for (int i = 0; i < 12; i++) {
					%>

						<aui:option label="<%= months[i] %>" selected="<%= monthIds[i] == yearlyMonth0 %>" value="<%= monthIds[i] %>" />

					<%
					}
					%>

				</aui:select>

				<aui:input inlineField="<%= true %>" inlineLabel="right" label="of-every" maxlength="2" name="yearlyDay0" size="2" type="text" value='<%= ParamUtil.getInteger(request, "yearlyDay0", 15) %>' />

				<aui:input inlineField="<%= true %>" inlineLabel="right" label="year-s" maxlength="2" name="yearlyInterval0" size="2" type="text" value='<%= ParamUtil.getInteger(request, "yearlyInterval0", 1) %>' />
			</span>
			<span class="field-row">
				<aui:input checked="<%= yearlyType == 1 %>" cssClass="input-container" inlineField="<%= true %>" label="the" name="yearlyType" type="radio" value="1" />

				<aui:select cssClass="input-container" inlineField="<%= true %>" label="" name="yearlyPos" title="year-position">
					<aui:option label="first" selected="<%= yearlyPos == 1 %>" value="1" />
					<aui:option label="second" selected="<%= yearlyPos == 2 %>" value="2" />
					<aui:option label="third" selected="<%= yearlyPos == 3 %>" value="3" />
					<aui:option label="fourth" selected="<%= yearlyPos == 4 %>" value="4" />
					<aui:option label="last" selected="<%= yearlyPos == -1 %>" value="-1" />
				</aui:select>

				<aui:select cssClass="input-container" inlineField="<%= true %>" inlineLabel="right" label="of[date-time]" name="yearlyDay1">
					<aui:option label="<%= days[0] %>" selected="<%= yearlyDay1 == Calendar.SUNDAY %>" value="<%= Calendar.SUNDAY %>" />
					<aui:option label="<%= days[1] %>" selected="<%= yearlyDay1 == Calendar.MONDAY %>" value="<%= Calendar.MONDAY %>" />
					<aui:option label="<%= days[2] %>" selected="<%= yearlyDay1 == Calendar.TUESDAY %>" value="<%= Calendar.TUESDAY %>" />
					<aui:option label="<%= days[3] %>" selected="<%= yearlyDay1 == Calendar.WEDNESDAY %>" value="<%= Calendar.WEDNESDAY %>" />
					<aui:option label="<%= days[4] %>" selected="<%= yearlyDay1 == Calendar.THURSDAY %>" value="<%= Calendar.THURSDAY %>" />
					<aui:option label="<%= days[5] %>" selected="<%= yearlyDay1 == Calendar.FRIDAY %>" value="<%= Calendar.FRIDAY %>" />
					<aui:option label="<%= days[6] %>" selected="<%= yearlyDay1 == Calendar.SATURDAY %>" value="<%= Calendar.SATURDAY %>" />
				</aui:select>

				<aui:select cssClass="input-container" inlineField="<%= true %>" inlineLabel="right" label="of-every" name="yearlyMonth1">

					<%
					for (int i = 0; i < 12; i++) {
					%>

						<aui:option label="<%= months[i] %>" selected="<%= monthIds[i] == yearlyMonth1 %>" value="<%= monthIds[i] %>" />

					<%
					}
					%>

				</aui:select>

				<aui:input inlineField="<%= true %>" inlineLabel="right" label="year-s" maxlength="2" name="yearlyInterval1" size="2" type="text" value='<%= ParamUtil.getInteger(request, "yearlyInterval1", 1) %>' />
			</span>
		</div>
	</div>
</aui:fieldset>

<aui:script sandbox="<%= true %>">
	var tables = document.querySelectorAll(
		'#<portlet:namespace />recurrenceTypeDailyTable, #<portlet:namespace />recurrenceTypeMonthlyTable, #<portlet:namespace />recurrenceTypeNeverTable, #<portlet:namespace />recurrenceTypeWeeklyTable, #<portlet:namespace />recurrenceTypeYearlyTable'
	);

	var eventsContainer = document.getElementById(
		'<portlet:namespace />eventsContainer'
	);

	if (eventsContainer) {
		Liferay.Util.delegate(eventsContainer, 'change', '.field', (event) => {
			var tableId = event.delegateTarget.id + 'Table';

			Array.prototype.forEach.call(tables, (table) => {
				if (table.id === tableId) {
					table.classList.remove('hide');
				}
				else {
					table.classList.add('hide');
				}
			});
		});
	}
</aui:script>

<%!
private boolean _getWeeklyDayPos(HttpServletRequest httpServletRequest, int day) {
	return ParamUtil.getBoolean(httpServletRequest, "weeklyDayPos" + day);
}
%>