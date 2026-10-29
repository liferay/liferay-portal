<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SchedulerResponseDisplayContext schedulerResponseDisplayContext = (SchedulerResponseDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<clay:navigation-bar
	navigationItems="<%= schedulerResponseDisplayContext.getNavigationItems() %>"
/>

<div id="<portlet:namespace />scheduledTaskContainer">
	<div class="closed container" id="<portlet:namespace />infoPanelId">
		<liferay-ui:search-container
			id="schedulerResponses"
			searchContainer="<%= schedulerResponseDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse"
				modelVar="schedulerResponse"
			>
				<liferay-ui:search-container-column-text
					cssClass="font-weight-bold important table-cell-expand"
					name="name"
				>
					<liferay-ui:message key="<%= schedulerResponseDisplayContext.getSimpleName(schedulerResponse.getJobName()) %>" />

					<liferay-ui:icon-help message="<%= schedulerResponse.getJobName() %>" />
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					name="system"
					value='<%= LanguageUtil.get(request, schedulerResponseDisplayContext.isSystem(schedulerResponse) ? "yes" : "no") %>'
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-ws-nowrap"
					name="next-fire-date"
					value="<%= schedulerResponseDisplayContext.getNextFireDateString(schedulerResponse) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="font-weight-bold important table-cell-ws-nowrap"
					name="status"
				>

					<%
					TriggerState triggerState = schedulerResponseDisplayContext.getTriggerState(schedulerResponse);
					%>

					<span class="<%= (triggerState == TriggerState.NORMAL) ? "label label-success" : "label label-info" %>">
						<liferay-ui:message key="<%= triggerState.toString() %>" />
					</span>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-jsp
					cssClass="table-cell-ws-nowrap"
					path="/scheduler_response_buttons.jsp"
				/>

				<liferay-ui:search-container-column-jsp
					cssClass="entry-action-column"
					path="/scheduler_response_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="list"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</div>
</div>