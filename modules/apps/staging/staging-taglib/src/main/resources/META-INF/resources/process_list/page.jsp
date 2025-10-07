<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/process_list/init.jsp" %>

<portlet:actionURL name="/staging_processes/delete_background_tasks" var="deleteBackgroundTasksURL">
	<portlet:param name="redirect" value="<%= currentURL.toString() %>" />
</portlet:actionURL>

<%
ProcessListDisplayContext processListDisplayContext = new ProcessListDisplayContext(groupId, request, liferayPortletResponse, liveGroup);
%>

<aui:form action="<%= deleteBackgroundTasksURL %>" cssClass="<%= processListDisplayContext.getProcessListListViewCss() %>" method="get" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL.toString() %>" />
	<aui:input name="deleteBackgroundTaskIds" type="hidden" />

	<liferay-staging:process-error
		authException="<%= true %>"
		remoteExportException="<%= true %>"
		remoteOptionsException="<%= true %>"
	/>

	<liferay-ui:search-container
		id='<%= HtmlUtil.escapeJS(ParamUtil.getString(request, "searchContainerId")) %>'
		searchContainer="<%= processListDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.backgroundtask.BackgroundTask"
			keyProperty="backgroundTaskId"
			modelVar="backgroundTask"
		>
			<c:choose>
				<c:when test='<%= Objects.equals(processListDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<liferay-ui:search-container-column-text
						valign="top"
					>
						<liferay-user:user-portrait
							userId="<%= backgroundTask.getUserId() %>"
						/>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<liferay-staging:process-info
							backgroundTask="<%= backgroundTask %>"
						/>

						<liferay-staging:process-message-task-details
							backgroundTaskId="<%= backgroundTask.getBackgroundTaskId() %>"
							backgroundTaskStatusMessage="<%= backgroundTask.getStatusMessage() %>"
							linkClass="background-task-status-row"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= Objects.equals(processListDisplayContext.getDisplayStyle(), "list") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200 table-title"
						name="title"
					>
						<liferay-user:user-portrait
							userId="<%= backgroundTask.getUserId() %>"
						/>

						<liferay-staging:process-title
							backgroundTask="<%= backgroundTask %>"
							listView="<%= true %>"
						/>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						cssClass="background-task-status-column"
						name="status"
					>
						<liferay-staging:process-status
							backgroundTaskStatus="<%= backgroundTask.getStatus() %>"
							backgroundTaskStatusLabel="<%= backgroundTask.getStatusLabel() %>"
						/>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-date
						cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
						name="create-date"
						orderable="<%= true %>"
						value="<%= backgroundTask.getCreateDate() %>"
					/>

					<liferay-ui:search-container-column-date
						cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
						name="completion-date"
						orderable="<%= true %>"
						value="<%= backgroundTask.getCompletionDate() %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="background-task-status-column table-cell-expand table-cell-minw-200"
					>
						<liferay-staging:process-in-progress
							backgroundTask="<%= backgroundTask %>"
							listView="<%= true %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
			</c:choose>

			<liferay-ui:search-container-column-text>
				<liferay-staging:process-list-menu
					backgroundTask="<%= backgroundTask %>"
					deleteMenu="<%= deleteMenu %>"
					detailsMenu="<%= detailsMenu %>"
					localPublishing="<%= processListDisplayContext.isLocalPublishing() %>"
					relaunchMenu="<%= relaunchMenu %>"
					summaryMenu="<%= summaryMenu %>"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= processListDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
			resultRowSplitter="<%= resultRowSplitter %>"
		/>
	</liferay-ui:search-container>
</aui:form>

<liferay-staging:incomplete-process-message
	localPublishing="<%= processListDisplayContext.isLocalPublishing() %>"
/>