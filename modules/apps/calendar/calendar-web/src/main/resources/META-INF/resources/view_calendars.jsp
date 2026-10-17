<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

CalendarResource calendarResource = (CalendarResource)request.getAttribute(CalendarWebKeys.CALENDAR_RESOURCE);
%>

<clay:container-fluid>
	<liferay-ui:header
		backURL="<%= redirect %>"
		localizeTitle="<%= false %>"
		title='<%= LanguageUtil.format(request, "x-calendars", calendarResource.getName(locale), false) %>'
	/>

	<c:if test="<%= CalendarResourcePermission.contains(permissionChecker, calendarResource, CalendarActionKeys.ADD_CALENDAR) %>">
		<aui:button-row>
			<liferay-portlet:renderURL var="editCalendarURL">
				<liferay-portlet:param name="mvcPath" value="/edit_calendar.jsp" />
				<liferay-portlet:param name="redirect" value="<%= currentURL %>" />
				<liferay-portlet:param name="backURL" value="<%= currentURL %>" />
				<liferay-portlet:param name="calendarResourceId" value="<%= String.valueOf(calendarResource.getCalendarResourceId()) %>" />
			</liferay-portlet:renderURL>

			<aui:button href="<%= editCalendarURL %>" primary="<%= true %>" value="add-calendar" />
		</aui:button-row>
	</c:if>
</clay:container-fluid>

<clay:container-fluid>
	<liferay-ui:search-container
		emptyResultsMessage="there-are-no-calendars-for-the-selected-resource"
		iteratorURL="<%= renderResponse.createRenderURL() %>"
		total="<%= CalendarServiceUtil.searchCount(themeDisplay.getCompanyId(), new long[] {calendarResource.getGroupId()}, new long[] {calendarResource.getCalendarResourceId()}, null, false) %>"
	>
		<liferay-ui:search-container-results
			results="<%= CalendarServiceUtil.search(themeDisplay.getCompanyId(), new long[] {calendarResource.getGroupId()}, new long[] {calendarResource.getCalendarResourceId()}, null, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, CalendarNameComparator.getInstance(true)) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.calendar.model.Calendar"
			keyProperty="calendarId"
			modelVar="calendar"
		>
			<liferay-ui:search-container-column-text
				name="name"
				value="<%= HtmlUtil.escape(calendar.getName(locale)) %>"
			/>

			<liferay-ui:search-container-column-text
				name="description"
				value="<%= HtmlUtil.escape(StringUtil.shorten(calendar.getDescription(locale))) %>"
			/>

			<liferay-ui:search-container-column-text
				align="center"
				name="color"
			>
				<aui:style type="text/css">
					.calendar-portlet-color-box {
						background-color: <%= ColorUtil.toHexString(calendar.getColor()) %>;
					}
				</aui:style>

				<span class="calendar-portlet-color-box">&nbsp;</span>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="default"
			>
				<c:choose>
					<c:when test="<%= calendar.isDefaultCalendar() %>">
						<liferay-ui:message key="yes" />
					</c:when>
					<c:otherwise>
						<liferay-ui:message key="no" />
					</c:otherwise>
				</c:choose>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-jsp
				align="right"
				cssClass="entry-action"
				path="/calendar_action.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
			paginate="<%= false %>"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>

<div class="calendar-portlet-import-container hide" id="<portlet:namespace />importCalendarContainer">
	<div class="hide portlet-msg-error" id="<portlet:namespace />portletErrorMessage"></div>

	<div class="hide portlet-msg-success" id="<portlet:namespace />portletSuccessMessage">
		<liferay-ui:message key="your-request-completed-successfully" />
	</div>

	<aui:form enctype="multipart/form-data" method="post" name="importFm">
		<aui:input id="file" name="file" type="file" />

		<div class="portlet-msg-help">
			<liferay-ui:message key="choose-the-file-that-contains-your-events.this-calendar-can-import-event-information-in-ical-format" />
		</div>
	</aui:form>
</div>

<aui:script use="io-upload-iframe">
	let importDialog;

	Liferay.provide(
		window,
		'<portlet:namespace />importCalendar',
		(url) => {
			function hideMessage(messageElement) {
				messageElement.hidden = true;
				messageElement.classList.add('hide');
			}

			function showMessage(messageElement) {
				messageElement.hidden = false;
				messageElement.classList.remove('hide');
			}

			if (!importDialog) {
				const buttons = [
					{
						label: '<liferay-ui:message key="import" />',
						on: {
							click: function () {
								const form = document.getElementById(
									'<portlet:namespace />importFm'
								);

								Liferay.Util.fetch(url, {
									body: new FormData(form),
									method: 'POST',
								})
									.then((response) => {
										return response.text();
									})
									.then((data) => {
										const responseData = {};

										try {
											responseData = JSON.parse(data);
										}
										catch (e) {}

										const portletErrorMessage =
											document.getElementById(
												'<portlet:namespace />portletErrorMessage'
											);

										const portletSuccessMessage =
											document.getElementById(
												'<portlet:namespace />portletSuccessMessage'
											);

										const error =
											responseData && responseData.error;

										if (error) {
											showMessage(portletErrorMessage);

											hideMessage(portletSuccessMessage);

											portletErrorMessage.innerHTML = error;
										}
										else {
											hideMessage(portletErrorMessage);

											showMessage(portletSuccessMessage);
										}
									});
							},
						},
					},
				];

				const buttonClose = [
					{
						cssClass: 'close',
						labelHTML: '<span aria-label="close">&times;</span>',
						on: {
							click: function () {
								importDialog.hide();
							},
						},
						render: true,
					},
				];

				const importCalendarContainer = document.getElementById(
					'<portlet:namespace />importCalendarContainer'
				);

				importDialog = Liferay.Util.Window.getWindow({
					dialog: {
						bodyContent: importCalendarContainer.innerHTML,
						modal: true,
						on: {
							visibleChange: function (event) {
								const importForm = document.getElementById(
									'<portlet:namespace />importFm'
								);

								if (importForm) {
									importForm.reset();
								}

								const portletErrorMessage = document.getElementById(
									'<portlet:namespace />portletErrorMessage'
								);
								const portletSuccessMessage =
									document.getElementById(
										'<portlet:namespace />portletSuccessMessage'
									);

								hideMessage(portletErrorMessage);

								hideMessage(portletSuccessMessage);
							},
						},
						toolbars: {
							footer: buttons,
							header: buttonClose,
						},
					},
					title: '<liferay-ui:message key="import" />',
				}).render();
			}

			importDialog.show();
		},
		['aui-io', 'liferay-util-window']
	);
</aui:script>