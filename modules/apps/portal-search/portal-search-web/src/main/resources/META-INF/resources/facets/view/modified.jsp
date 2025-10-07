<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/facets/init.jsp" %>

<%
String fieldParamSelection = ParamUtil.getString(request, facet.getFieldId() + "selection", "0");

String fieldParamFrom = ParamUtil.getString(request, facet.getFieldId() + "from");
String fieldParamTo = ParamUtil.getString(request, facet.getFieldId() + "to");

Date fromDate = null;
Date toDate = null;

if (Validator.isNotNull(fieldParamFrom) || Validator.isNotNull(fieldParamTo)) {
	String delimiter = StringPool.FORWARD_SLASH;

	int dayPosition = 1;
	int monthPosition = 0;
	int yearPosition = 2;

	if (BrowserSnifferUtil.isMobile(request)) {
		delimiter = StringPool.DASH;

		dayPosition = 2;
		monthPosition = 1;
		yearPosition = 0;
	}

	String[] from = StringUtil.split(fieldParamFrom, delimiter);

	if (ArrayUtil.isNotEmpty(from) && (from.length == 3)) {
		int fromDay = GetterUtil.getInteger(from[dayPosition]);
		int fromMonth = GetterUtil.getInteger(from[monthPosition]) - 1;
		int fromYear = GetterUtil.getInteger(from[yearPosition]);

		fromDate = PortalUtil.getDate(fromMonth, fromDay, fromYear);
	}

	String[] to = StringUtil.split(fieldParamTo, delimiter);

	if (ArrayUtil.isNotEmpty(to) && (to.length == 3)) {
		int toDay = GetterUtil.getInteger(to[dayPosition]);
		int toMonth = GetterUtil.getInteger(to[monthPosition]) - 1;
		int toYear = GetterUtil.getInteger(to[yearPosition]);

		toDate = PortalUtil.getDate(toMonth, toDay, toYear);
	}
}

JSONArray rangesJSONArray = dataJSONObject.getJSONArray("ranges");

int index = 0;
%>

<div class="panel panel-secondary">
	<div class="panel-heading">
		<div class="panel-title">
			<liferay-ui:message key="time" />
		</div>
	</div>

	<div class="panel-body">
		<div class="<%= cssClass %>" data-facetFieldName="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>" id="<%= randomNamespace %>facet">
			<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>" type="hidden" value="<%= fieldParam %>" />
			<aui:input autocomplete="off" name='<%= HtmlUtil.escapeAttribute(facet.getFieldId()) + "selection" %>' type="hidden" value="<%= fieldParamSelection %>" />

			<aui:field-wrapper cssClass='<%= randomNamespace + "calendar calendar_" %>' label="" name="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>">
				<ul class="list-unstyled modified">
					<li class="default facet-value">

						<%
						Map<String, Object> data = HashMapBuilder.<String, Object>put(
							"selection", 0
						).put(
							"value", StringPool.BLANK
						).build();
						%>

						<aui:a cssClass='<%= (Validator.isNull(fieldParamSelection) || fieldParamSelection.equals("0")) ? "facet-term-selected" : "facet-term-unselected" %>' href="javascript:void(0);">
							<liferay-ui:message key="<%= HtmlUtil.escape(facetConfiguration.getLabel()) %>" />
						</aui:a>
					</li>

					<%
					for (int i = 0; i < rangesJSONArray.length(); i++) {
						JSONObject rangesJSONObject = rangesJSONArray.getJSONObject(i);

						String label = HtmlUtil.escape(rangesJSONObject.getString("label"));
						String range = rangesJSONObject.getString("range");

						index = i + 1;
					%>

						<li class="facet-value">

							<%
							String rangeCssClass = "facet-term-unselected";

							if (fieldParamSelection.equals(String.valueOf(index))) {
								rangeCssClass = "facet-term-selected";
							}

							data = HashMapBuilder.<String, Object>put(
								"selection", index
							).put(
								"value", HtmlUtil.escape(range)
							).build();
							%>

							<aui:a cssClass="<%= rangeCssClass %>" data="<%= data %>" href="javascript:void(0);">
								<liferay-ui:message key="<%= label %>" />

								<%
								TermCollector termCollector = facetCollector.getTermCollector(range);
								%>

								<c:if test="<%= termCollector != null %>">
									<span class="frequency">(<%= termCollector.getFrequency() %>)</span>
								</c:if>
							</aui:a>
						</li>

					<%
					}
					%>

					<li class="facet-value">

						<%
						String customRangeCssClass = randomNamespace + "custom-range-toggle";

						if (fieldParamSelection.equals(String.valueOf(index + 1))) {
							customRangeCssClass += " facet-term-selected";
						}
						else {
							customRangeCssClass += " facet-term-unselected";
						}

						TermCollector termCollector = null;

						if (fieldParamSelection.equals(String.valueOf(index + 1))) {
							termCollector = facetCollector.getTermCollector(fieldParam);
						}
						%>

						<aui:a cssClass="<%= customRangeCssClass %>" href="javascript:void(0);">
							<liferay-ui:message key="custom-range" />&hellip;

							<c:if test="<%= termCollector != null %>">
								<span class="frequency">(<%= termCollector.getFrequency() %>)</span>
							</c:if>
						</aui:a>
					</li>

					<%
					Calendar fromCalendar = CalendarFactoryUtil.getCalendar(timeZone, locale);

					if (fromDate != null) {
						fromCalendar.setTime(fromDate);
					}
					else {
						fromCalendar.add(Calendar.DATE, -1);
					}

					Calendar toCalendar = CalendarFactoryUtil.getCalendar(timeZone, locale);

					if (toDate != null) {
						toCalendar.setTime(toDate);
					}
					%>

					<div class="<%= !fieldParamSelection.equals(String.valueOf(index + 1)) ? "hide" : StringPool.BLANK %> modified-custom-range" id="<%= randomNamespace %>customRange">
						<clay:col
							id='<%= randomNamespace + "customRangeFrom" %>'
							md="6"
						>
							<aui:field-wrapper label="from">
								<liferay-ui:input-date
									dayParam='<%= HtmlUtil.escapeJS(facet.getFieldId()) + "dayFrom" %>'
									dayValue="<%= fromCalendar.get(Calendar.DATE) %>"
									disabled="<%= false %>"
									firstDayOfWeek="<%= fromCalendar.getFirstDayOfWeek() - 1 %>"
									monthParam='<%= HtmlUtil.escapeJS(facet.getFieldId()) + "monthFrom" %>'
									monthValue="<%= fromCalendar.get(Calendar.MONTH) %>"
									name='<%= HtmlUtil.escapeJS(facet.getFieldId()) + "from" %>'
									yearParam='<%= HtmlUtil.escapeJS(facet.getFieldId()) + "yearFrom" %>'
									yearValue="<%= fromCalendar.get(Calendar.YEAR) %>"
								/>
							</aui:field-wrapper>
						</clay:col>

						<clay:col
							id='<%= randomNamespace + "customRangeTo" %>'
							md="6"
						>
							<aui:field-wrapper label="to[date-time]">
								<liferay-ui:input-date
									dayParam='<%= HtmlUtil.escapeJS(facet.getFieldId()) + "dayTo" %>'
									dayValue="<%= toCalendar.get(Calendar.DATE) %>"
									disabled="<%= false %>"
									firstDayOfWeek="<%= toCalendar.getFirstDayOfWeek() - 1 %>"
									monthParam='<%= HtmlUtil.escapeJS(facet.getFieldId()) + "monthTo" %>'
									monthValue="<%= toCalendar.get(Calendar.MONTH) %>"
									name='<%= HtmlUtil.escapeJS(facet.getFieldId()) + "to" %>'
									yearParam='<%= HtmlUtil.escapeJS(facet.getFieldId()) + "yearTo" %>'
									yearValue="<%= toCalendar.get(Calendar.YEAR) %>"
								/>
							</aui:field-wrapper>
						</clay:col>

						<%
						String taglibSearchCustomRange = "window['" + liferayPortletResponse.getNamespace() + HtmlUtil.escapeJS(facet.getFieldId()) + "searchCustomRange'](" + (index + 1) + ");";
						%>

						<aui:button disabled="<%= toCalendar.getTimeInMillis() < fromCalendar.getTimeInMillis() %>" name="searchCustomRangeButton" onClick="<%= taglibSearchCustomRange %>" value="search" />
					</div>
				</ul>
			</aui:field-wrapper>
		</div>
	</div>
</div>

<aui:script>
	var form = document.<portlet:namespace />fm;

	var dayFromInput = Liferay.Util.getFormElement(
		form,
		'<%= HtmlUtil.escapeJS(facet.getFieldId()) %>dayFrom'
	);
	var monthFromInput = Liferay.Util.getFormElement(
		form,
		'<%= HtmlUtil.escapeJS(facet.getFieldId()) %>monthFrom'
	);
	var yearFromInput = Liferay.Util.getFormElement(
		form,
		'<%= HtmlUtil.escapeJS(facet.getFieldId()) %>yearFrom'
	);

	var dayToInput = Liferay.Util.getFormElement(
		form,
		'<%= HtmlUtil.escapeJS(facet.getFieldId()) %>dayTo'
	);
	var monthToInput = Liferay.Util.getFormElement(
		form,
		'<%= HtmlUtil.escapeJS(facet.getFieldId()) %>monthTo'
	);
	var yearToInput = Liferay.Util.getFormElement(
		form,
		'<%= HtmlUtil.escapeJS(facet.getFieldId()) %>yearTo'
	);

	if (
		dayFromInput &&
		monthFromInput &&
		yearFromInput &&
		dayToInput &&
		monthToInput &&
		yearToInput
	) {
		Liferay.Util.toggleDisabled(dayFromInput, true);
		Liferay.Util.toggleDisabled(monthFromInput, true);
		Liferay.Util.toggleDisabled(yearFromInput, true);

		Liferay.Util.toggleDisabled(dayToInput, true);
		Liferay.Util.toggleDisabled(monthToInput, true);
		Liferay.Util.toggleDisabled(yearToInput, true);

		function <portlet:namespace /><%= HtmlUtil.escapeJS(facet.getFieldId()) %>searchCustomRange(
			selection
		) {
			var A = AUI();
			var Lang = A.Lang;
			var LString = Lang.String;

			var dayFrom = dayFromInput.value;
			var monthFrom = Lang.toInt(monthFromInput.value) + 1;
			var yearFrom = yearFromInput.value;

			var dayTo = dayToInput.value;
			var monthTo = Lang.toInt(monthToInput.value) + 1;
			var yearTo = yearToInput.value;

			var range =
				'[' +
				yearFrom +
				LString.padNumber(monthFrom, 2) +
				LString.padNumber(dayFrom, 2) +
				'000000 TO ' +
				yearTo +
				LString.padNumber(monthTo, 2) +
				LString.padNumber(dayTo, 2) +
				'235959]';

			var data = {};

			data['<%= HtmlUtil.escapeJS(facet.getFieldId()) %>'] = range;
			data['<%= HtmlUtil.escapeJS(facet.getFieldId()) %>selection'] =
				selection;

			Liferay.Util.postForm(form, {data: data});
		}
	}
</aui:script>

<aui:script use="aui-form-validator">
	var Util = Liferay.Util;

	var customRangeFrom = Liferay.component(
		'<%= liferayPortletResponse.getNamespace() %>modifiedfromDatePicker'
	);
	var customRangeTo = Liferay.component(
		'<%= liferayPortletResponse.getNamespace() %>modifiedtoDatePicker'
	);
	var searchButton = A.one('#<portlet:namespace />searchCustomRangeButton');

	var preventKeyboardDateChange = function (event) {
		if (!event.isKey('TAB')) {
			event.preventDefault();
		}
	};

	A.one(
		'#<portlet:namespace /><%= HtmlUtil.escapeJS(facet.getFieldId()) %>from'
	).on('keydown', preventKeyboardDateChange);
	A.one(
		'#<portlet:namespace /><%= HtmlUtil.escapeJS(facet.getFieldId()) %>to'
	).on('keydown', preventKeyboardDateChange);

	var DEFAULTS_FORM_VALIDATOR = A.config.FormValidator;

	A.mix(
		DEFAULTS_FORM_VALIDATOR.STRINGS,
		{
			<portlet:namespace />dateRange:
				'<%= UnicodeLanguageUtil.get(request, "search-custom-range-invalid-date-range") %>',
		},
		true
	);

	A.mix(
		DEFAULTS_FORM_VALIDATOR.RULES,
		{
			<portlet:namespace />dateRange: function (val, fieldNode, ruleValue) {
				return A.Date.isGreaterOrEqual(
					customRangeTo.getDate(),
					customRangeFrom.getDate()
				);
			},
		},
		true
	);

	var customRangeValidator = new A.FormValidator({
		boundingBox: document.<portlet:namespace />fm,
		fieldContainer: 'div',
		on: {
			errorField: function (event) {
				Util.toggleDisabled(searchButton, true);
			},
			validField: function (event) {
				Util.toggleDisabled(searchButton, false);
			},
		},
		rules: {
			<portlet:namespace /><%= HtmlUtil.escapeJS(facet.getFieldId()) %>from: {
				<portlet:namespace />dateRange: true,
			},
			<portlet:namespace /><%= HtmlUtil.escapeJS(facet.getFieldId()) %>to: {
				<portlet:namespace />dateRange: true,
			},
		},
	});

	var onRangeSelectionChange = function (event) {
		customRangeValidator.validate();
	};

	customRangeFrom.on('selectionChange', onRangeSelectionChange);
	customRangeTo.on('selectionChange', onRangeSelectionChange);

	A.one('.<%= randomNamespace %>custom-range-toggle').on('click', (event) => {
		event.halt();

		A.one('#<%= randomNamespace %>customRange').toggle();
	});
</aui:script>