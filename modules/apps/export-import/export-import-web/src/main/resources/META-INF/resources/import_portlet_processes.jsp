<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long groupId = ParamUtil.getLong(request, "groupId", themeDisplay.getScopeGroupId());

PortletURL portletURL = PortletURLBuilder.create(
	currentURLObj
).setParameter(
	"tabs3", "current-and-previous"
).buildPortletURL();

String orderByCol = ParamUtil.getString(request, "orderByCol");
String orderByType = ParamUtil.getString(request, "orderByType");

if (Validator.isNotNull(orderByCol) && Validator.isNotNull(orderByType)) {
	portalPreferences.setValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-col", orderByCol);
	portalPreferences.setValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-type", orderByType);
}
else {
	orderByCol = portalPreferences.getValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-col", "create-date");
	orderByType = portalPreferences.getValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-type", "desc");
}
%>

<clay:container-fluid>
	<liferay-ui:search-container
		emptyResultsMessage="no-import-processes-were-found"
		iteratorURL="<%= portletURL %>"
		orderByCol="<%= orderByCol %>"
		orderByComparator="<%= BackgroundTaskComparatorFactoryUtil.getBackgroundTaskOrderByComparator(orderByCol, orderByType) %>"
		orderByType="<%= orderByType %>"
		total="<%= BackgroundTaskManagerUtil.getBackgroundTasksCount(groupId, selPortlet.getPortletId(), BackgroundTaskExecutorNames.PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR) %>"
	>
		<liferay-ui:search-container-results
			results="<%= BackgroundTaskManagerUtil.getBackgroundTasks(groupId, selPortlet.getPortletId(), BackgroundTaskExecutorNames.PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR, searchContainer.getStart(), searchContainer.getEnd(), searchContainer.getOrderByComparator()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.backgroundtask.BackgroundTask"
			keyPropertyName="backgroundTaskId"
			modelVar="backgroundTask"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="user"
			>
				<liferay-user:user-portrait
					userId="<%= backgroundTask.getUserId() %>"
				/>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-jsp
				cssClass="table-cell-expand"
				name="status"
				path="/publish_process_message.jsp"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand"
				name="create-date"
				orderable="<%= true %>"
				value="<%= backgroundTask.getCreateDate() %>"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand"
				name="completion-date"
				orderable="<%= true %>"
				value="<%= backgroundTask.getCompletionDate() %>"
			/>

			<liferay-ui:search-container-column-text>
				<c:if test="<%= !backgroundTask.isInProgress() %>">

					<%
					Date completionDate = backgroundTask.getCompletionDate();
					%>

					<liferay-portlet:renderURL var="redirectURL">
						<portlet:param name="mvcRenderCommandName" value="/export_import/export_import" />
						<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.IMPORT %>" />
						<portlet:param name="tabs2" value="import" />
						<portlet:param name="tabs3" value="current-and-previous" />
						<portlet:param name="portletResource" value="<%= portletResource %>" />
					</liferay-portlet:renderURL>

					<liferay-portlet:actionURL name="/export_import/delete_portlet_background_task" portletName="<%= PortletKeys.EXPORT_IMPORT %>" var="deleteBackgroundTaskURL">
						<portlet:param name="redirect" value="<%= redirectURL %>" />
						<portlet:param name="backgroundTaskId" value="<%= String.valueOf(backgroundTask.getBackgroundTaskId()) %>" />
					</liferay-portlet:actionURL>

					<liferay-portlet:renderURL portletName="<%= PortletKeys.EXPORT_IMPORT %>" var="viewReportEntriesURL">
						<portlet:param name="mvcRenderCommandName" value="/export_import/view_import_report_entries" />
						<portlet:param name="backgroundTaskId" value="<%= String.valueOf(backgroundTask.getBackgroundTaskId()) %>" />
					</liferay-portlet:renderURL>

					<liferay-ui:icon-menu
						direction="left-side"
						icon="<%= StringPool.BLANK %>"
						markupView="lexicon"
						showWhenSingleIcon="<%= true %>"
					>
						<c:if test="<%= (backgroundTask.getStatus() == BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS) || (backgroundTask.getStatus() == BackgroundTaskConstants.STATUS_FAILED) %>">
							<li>
								<a class="dropdown-item" href="<%= viewReportEntriesURL %>">
									<liferay-ui:message key="view-report-entries" />
								</a>
							</li>
						</c:if>

						<liferay-ui:icon-delete
							label="<%= true %>"
							message='<%= ((completionDate != null) && completionDate.before(new Date())) ? "clear" : "cancel" %>'
							url="<%= deleteBackgroundTaskURL %>"
						/>
					</liferay-ui:icon-menu>
				</c:if>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>

	<%
	int incompleteBackgroundTasksCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(groupId, selPortlet.getPortletId(), BackgroundTaskExecutorNames.PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR, false);
	%>

	<div class="hide incomplete-process-message">
		<liferay-util:include page="/incomplete_processes_message.jsp" servletContext="<%= application %>">
			<liferay-util:param name="incompleteBackgroundTasksCount" value="<%= String.valueOf(incompleteBackgroundTasksCount) %>" />
		</liferay-util:include>
	</div>
</clay:container-fluid>