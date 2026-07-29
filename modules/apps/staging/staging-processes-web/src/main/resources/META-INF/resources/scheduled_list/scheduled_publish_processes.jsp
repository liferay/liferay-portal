<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ScheduledPublishProcessesDisplayContext scheduledPublishProcessesDisplayContext = new ScheduledPublishProcessesDisplayContext(group, request, liferayPortletResponse, liveGroupId);
%>

<div id="<portlet:namespace />scheduledPublishProcessesSearchContainer">
	<liferay-ui:search-container
		id="scheduledPublishProcesses"
		searchContainer="<%= scheduledPublishProcessesDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse"
			modelVar="schedulerResponse"
		>
			<liferay-ui:search-container-column-text
				cssClass="background-task-user-column"
				name="user"
			>

				<%
				Message message = schedulerResponse.getMessage();

				ExportImportConfiguration exportImportConfiguration = ExportImportConfigurationLocalServiceUtil.getExportImportConfiguration(GetterUtil.getLong(message.getPayload()));
				%>

				<liferay-user:user-portrait
					userId="<%= exportImportConfiguration.getUserId() %>"
				/>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="title"
			>

				<%
				String description = schedulerResponse.getDescription();

				if (description.equals(StringPool.BLANK)) {
					description = LanguageUtil.get(request, "untitled-scheduled-publish-process");
				}
				%>

				<liferay-ui:message key="<%= HtmlUtil.escape(description) %>" />
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-date
				name="create-date"
				orderable="<%= true %>"
				value="<%= SchedulerEngineHelperUtil.getStartDate(schedulerResponse) %>"
			/>

			<%
			Date endDate = SchedulerEngineHelperUtil.getEndDate(schedulerResponse);
			%>

			<c:choose>
				<c:when test="<%= endDate != null %>">
					<liferay-ui:search-container-column-date
						name="end-date"
						orderable="<%= true %>"
						value="<%= SchedulerEngineHelperUtil.getEndDate(schedulerResponse) %>"
					/>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-text
						name="end-date"
					>
						<liferay-ui:message key='<%= LanguageUtil.get(request, "no-end-date") %>' />
					</liferay-ui:search-container-column-text>
				</c:otherwise>
			</c:choose>

			<liferay-ui:search-container-column-text
				align="right"
			>
				<liferay-ui:icon-menu
					direction="left-side"
					icon="<%= StringPool.BLANK %>"
					markupView="lexicon"
					message="<%= StringPool.BLANK %>"
					showWhenSingleIcon="<%= true %>"
				>
					<portlet:renderURL var="deleteScheduledPublicationRedirectURL">
						<portlet:param name="mvcPath" value="/view.jsp" />
						<portlet:param name="tabs1" value="scheduled" />
					</portlet:renderURL>

					<portlet:actionURL name="/staging_processes/publish_layouts" var="deleteScheduledPublicationURL">
						<portlet:param name="cmd" value="<%= scheduledPublishProcessesDisplayContext.getCmd() %>" />
						<portlet:param name="stagingGroupId" value="<%= String.valueOf(stagingGroupId) %>" />
						<portlet:param name="jobName" value="<%= schedulerResponse.getJobName() %>" />
						<portlet:param name="redirect" value="<%= deleteScheduledPublicationRedirectURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						message="delete"
						url="<%= deleteScheduledPublicationURL %>"
					/>
				</liferay-ui:icon-menu>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</div>