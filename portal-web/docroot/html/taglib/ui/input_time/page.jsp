<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/init.jsp" %>

<%
String randomNamespace = PortalUtil.generateRandomKey(request, "taglib_ui_input_time_page") + StringPool.UNDERLINE;

if (!GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-time:useNamespace"), true)) {
	namespace = StringPool.BLANK;
}

String amPmParam = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-time:amPmParam"));
int amPmValue = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:input-time:amPmValue"));
String autoComplete = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-time:autoComplete"));
String cssClass = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-time:cssClass"));
String dateParam = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-time:dateParam"), "date");
Date dateValue = (Date)GetterUtil.getObject(request.getAttribute("liferay-ui:input-time:dateValue"));
boolean disabled = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-time:disabled"));
String hourParam = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-time:hourParam"));
int hourValue = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:input-time:hourValue"));
String minuteParam = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-time:minuteParam"));
int minuteValue = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:input-time:minuteValue"));
int minuteInterval = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:input-time:minuteInterval"));
String name = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-time:name"));
String timeFormat = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-time:timeFormat"));

boolean amPm = DateUtil.isFormatAmPm(locale);

if (timeFormat.equals("am-pm")) {
	amPm = true;
}
else if (timeFormat.equals("24-hour")) {
	amPm = false;
}

if (minuteInterval < 1) {
	minuteInterval = 30;
}

int hourOfDayValue = hourValue;

if (amPmValue == Calendar.PM) {
	hourOfDayValue += 12;
}

String amPmParamId = namespace + HtmlUtil.getAUICompatibleId(amPmParam);
String dateParamId = namespace + HtmlUtil.getAUICompatibleId(dateParam);
String hourParamId = namespace + HtmlUtil.getAUICompatibleId(hourParam);
String minuteParamId = namespace + HtmlUtil.getAUICompatibleId(minuteParam);
String nameId = namespace + HtmlUtil.getAUICompatibleId(name);

Calendar calendar = CalendarFactoryUtil.getCalendar(1970, 0, 1, hourOfDayValue, minuteValue, 0, 0, timeZone);

String placeholder = _PLACEHOLDER_DEFAULT;

if (!amPm) {
	placeholder = _PLACEHOLDER_ISO;
}

Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(_SIMPLE_DATE_FORMAT_PATTERN_HTML5, locale, timeZone);
%>

<span class="lfr-input-time" id="<%= randomNamespace %>displayTime">
	<input aria-label="<%= HtmlUtil.escapeAttribute(LanguageUtil.get(request, "time")) %>" <%= Validator.isNotNull(autoComplete) ? "autocomplete=\"" + autoComplete + "\"" : StringPool.BLANK %> class="form-control <%= cssClass %>" <%= disabled ? "disabled=\"disabled\"" : "" %> id="<%= nameId %>" name="<%= namespace + HtmlUtil.escapeAttribute(name) %>" type="time" value="<%= format.format(calendar.getTime()) %>" />

	<input <%= disabled ? "disabled=\"disabled\"" : "" %> id="<%= hourParamId %>" name="<%= namespace + HtmlUtil.escapeAttribute(hourParam) %>" type="hidden" value="<%= hourValue %>" />
	<input <%= disabled ? "disabled=\"disabled\"" : "" %> id="<%= minuteParamId %>" name="<%= namespace + HtmlUtil.escapeAttribute(minuteParam) %>" type="hidden" value="<%= minuteValue %>" />
	<input <%= disabled ? "disabled=\"disabled\"" : "" %> id="<%= amPmParamId %>" name="<%= namespace + HtmlUtil.escapeAttribute(amPmParam) %>" type="hidden" value="<%= amPmValue %>" />
	<input <%= disabled ? "disabled=\"disabled\"" : "" %> id="<%= dateParamId %>" name="<%= namespace + HtmlUtil.escapeAttribute(dateParam) %>" type="hidden" value="<%= dateValue %>" />
</span>

<aui:script use="aui-timepicker-native">
	Liferay.component(
		'<%= nameId %>TimePicker',
		function () {
			var timePicker = new A.TimePickerNative(
				{
					container: '#<%= randomNamespace %>displayTime',
					on: {
						enterKey: function(event) {
							var instance = this;

							var inputVal = instance.get('activeInput').val();

							var date = instance.getParsedDatesFromInputValue(inputVal).pop();

							instance.updateTime(date);
						},
						selectionChange: function(event) {
							var instance = this;

							var date = event.newSelection[0];

							instance.updateTime(date);
						}
					},
					trigger: '#<%= nameId %>',
				}
			);

			timePicker.getTime = function () {
				var instance = this;

				var container = instance.get('container');

				var amPm = A.Lang.toInt(container.one('#<%= amPmParamId %>').val());
				var hours = A.Lang.toInt(container.one('#<%= hourParamId %>').val());
				var minutes = container.one('#<%= minuteParamId %>').val();

				if (amPm) {
					hours += 12;
				}

				var time = A.Date.parse(A.Date.aggregates.T, hours + ':' + minutes + ':0');

				return time;
			};

			timePicker.updateTime = function(date) {
				var instance = this;

				var container = instance.get('container');

				var hours = date.getHours();

				var amPm = 0;

				<c:if test="<%= amPm %>">
					if (hours > 11) {
						amPm = 1;
						hours -= 12;
					}
				</c:if>

				if (date) {
					container.one('#<%= hourParamId %>').val(hours);
					container.one('#<%= minuteParamId %>').val(date.getMinutes());
					container.one('#<%= amPmParamId %>').val(amPm);
					container.one('#<%= dateParamId %>').val(date);
				}
			};

			return timePicker;
		}
	);

	Liferay.component('<%= nameId %>TimePicker');
</aui:script>

<%!
private static final String _SIMPLE_DATE_FORMAT_PATTERN_HTML5 = "HH:mm";

private static final String _PLACEHOLDER_DEFAULT = "h:mm am/pm";

private static final String _PLACEHOLDER_ISO = "h:mm";
%>