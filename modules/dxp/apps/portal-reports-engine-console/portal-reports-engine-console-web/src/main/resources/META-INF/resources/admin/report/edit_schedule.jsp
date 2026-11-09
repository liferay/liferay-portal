<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
Definition definition = (Definition)request.getAttribute(ReportsEngineWebKeys.DEFINITION);

String reportName = BeanParamUtil.getString(definition, request, "reportName");

portletDisplay.setShowBackIcon(true);

PortletURL searchDefinitionURL = PortletURLBuilder.create(
	reportsEngineDisplayContext.getPortletURL()
).setMVCPath(
	"/admin/view.jsp"
).setTabs1(
	"definitions"
).buildPortletURL();

portletDisplay.setURLBack(searchDefinitionURL.toString());

renderResponse.setTitle(LanguageUtil.get(request, "new-report-entry"));
%>

<portlet:renderURL var="searchRequestsURL">
	<portlet:param name="mvcPath" value="/admin/view.jsp" />
	<portlet:param name="tabs1" value="reports" />
</portlet:renderURL>

<portlet:actionURL name="/reports_admin/add_scheduler" var="addSchedulerURL">
	<portlet:param name="mvcPath" value="/admin/report/edit_schedule.jsp" />
	<portlet:param name="redirect" value="<%= searchRequestsURL %>" />
</portlet:actionURL>

<aui:form action="<%= addSchedulerURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="definitionId" type="hidden" value="<%= definition.getDefinitionId() %>" />

	<portlet:renderURL var="generatedReportsURL">
		<portlet:param name="mvcPath" value="/admin/report/requested_report_detail.jsp" />
	</portlet:renderURL>

	<aui:input name="generatedReportsURL" type="hidden" value="<%= generatedReportsURL %>" />

	<liferay-ui:error exception="<%= DefinitionNameException.class %>" message="please-enter-a-valid-name" />
	<liferay-ui:error exception="<%= EntryEmailDeliveryException.class %>" message="please-enter-a-valid-email-address" />
	<liferay-ui:error exception="<%= EntryEmailNotificationsException.class %>" message="please-enter-a-valid-email-address" />

	<div class="sheet">
		<div class="panel-group panel-group-flush">
			<aui:fieldset>
				<aui:input name="reportName" value="<%= reportName %>" />

				<aui:select label="report-format" name="format">

					<%
					for (ReportFormat reportFormat : ReportFormat.values()) {
					%>

						<aui:option label="<%= reportFormat.getValue() %>" value="<%= reportFormat.getValue() %>" />

					<%
					}
					%>

				</aui:select>

				<aui:input label="email-notifications" name="emailNotifications" type="text" />

				<aui:input label="email-recipient" name="emailDelivery" type="text" />
			</aui:fieldset>

			<aui:fieldset collapsible="<%= true %>" cssClass="options-group" label="schedule[noun]">
				<liferay-util:include page="/admin/report/input_scheduler.jsp" servletContext="<%= application %>" />
			</aui:fieldset>

			<%
			JSONArray reportParametersJSONArray = JSONFactoryUtil.createJSONArray(definition.getReportParameters());
			%>

			<c:if test="<%= reportParametersJSONArray.length() > 0 %>">
				<aui:fieldset collapsible="<%= true %>" cssClass="options-group" label="report-parameters">

					<%
					for (int i = 0; i < reportParametersJSONArray.length(); i++) {
						JSONObject reportParameterJSONObject = reportParametersJSONArray.getJSONObject(i);

						String key = reportParameterJSONObject.getString("key");
						String type = reportParameterJSONObject.getString("type");
						String value = reportParameterJSONObject.getString("value");

						String keyJSId = StringUtil.randomId();
					%>

						<clay:row>
							<c:choose>
								<c:when test='<%= type.equals("date") %>'>
									<clay:col
										md="4"
									>
										<aui:field-wrapper helpMessage="entry-report-date-parameters-help" label="<%= HtmlUtil.escape(key) %>" />
									</clay:col>

									<clay:col
										md="4"
									>
										<aui:select label="" name='<%= "useVariable" + HtmlUtil.escapeAttribute(key) %>' onChange='<%= "useVariable" + keyJSId + "();" %>' showEmptyOption="<%= true %>">
											<aui:option label="start-date" value="startDate" />
											<aui:option label="end-date" value="endDate" />
										</aui:select>

										<aui:script type="text/javascript">
											function useVariable<%= keyJSId %>() {
												const type = document.getElementById(
													'<%= liferayPortletResponse.getNamespace() %>useVariable<%= HtmlUtil.escapeJS(key) %>'
												).value;

												const day = document.getElementById(
													'<%= liferayPortletResponse.getNamespace() + HtmlUtil.escapeJS(key) %>Day'
												);

												const month = document.getElementById(
													'<%= liferayPortletResponse.getNamespace() + HtmlUtil.escapeJS(key) %>Month'
												);

												const year = document.getElementById(
													'<%= liferayPortletResponse.getNamespace() + HtmlUtil.escapeJS(key) %>Year'
												);

												if (type == 'startDate' || type == 'endDate') {
													day.disabled = true;
													month.disabled = true;
													year.disabled = true;

													if (type == 'endDate') {
														document.<portlet:namespace />fm.<portlet:namespace />endDateType[1].checked =
															'true';
													}
												}
												else {
													day.disabled = false;
													month.disabled = false;
													year.disabled = false;
												}
											}
										</aui:script>
									</clay:col>

									<clay:col
										md="4"
									>

										<%
										Calendar calendar = CalendarFactoryUtil.getCalendar(timeZone, locale);

										String[] date = value.split("-");

										calendar.set(Calendar.YEAR, GetterUtil.getInteger(date[0]));
										calendar.set(Calendar.MONTH, GetterUtil.getInteger(date[1]) - 1);
										calendar.set(Calendar.DATE, GetterUtil.getInteger(date[2]));
										%>

										<liferay-ui:input-date
											dayParam='<%= key + "Day" %>'
											dayValue="<%= calendar.get(Calendar.DATE) %>"
											disabled="<%= false %>"
											firstDayOfWeek="<%= calendar.getFirstDayOfWeek() - 1 %>"
											monthParam='<%= key + "Month" %>'
											monthValue="<%= calendar.get(Calendar.MONTH) %>"
											yearParam='<%= key +"Year" %>'
											yearValue="<%= calendar.get(Calendar.YEAR) %>"
										/>
									</clay:col>
								</c:when>
								<c:otherwise>
									<clay:col
										md="4"
									>
										<%= HtmlUtil.escape(key) %>
									</clay:col>

									<clay:col
										md="8"
									>
										<span class="field field-text" id="aui_3_2_0_1428">
											<input class="form-control" name="<portlet:namespace />parameterValue<%= HtmlUtil.escapeAttribute(key) %>" type="text" value="<%= HtmlUtil.escapeAttribute(value) %>" /><br />
										</span>
									</clay:col>
								</c:otherwise>
							</c:choose>
						</clay:row>

					<%
					}
					%>

				</aui:fieldset>
			</c:if>

			<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="permissions">
				<liferay-ui:input-permissions
					modelName="<%= Entry.class.getName() %>"
				/>
			</aui:fieldset>
		</div>
	</div>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" value="schedule[verb]" />

		<aui:button cssClass="btn-lg" href="<%= searchDefinitionURL.toString() %>" type="cancel" />
	</aui:button-row>
</aui:form>