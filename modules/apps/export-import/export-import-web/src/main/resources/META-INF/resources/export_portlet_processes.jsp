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
).setMVCRenderCommandName(
	"/export_import/export_import"
).setTabs2(
	"export"
).setParameter(
	"tabs3", "current-and-previous"
).buildPortletURL();

String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNotNull(redirect)) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);
}

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
		emptyResultsMessage="no-export-processes-were-found"
		iteratorURL="<%= portletURL %>"
		orderByCol="<%= orderByCol %>"
		orderByComparator="<%= BackgroundTaskComparatorFactoryUtil.getBackgroundTaskOrderByComparator(orderByCol, orderByType) %>"
		orderByType="<%= orderByType %>"
		total="<%= BackgroundTaskManagerUtil.getBackgroundTasksCount(groupId, selPortlet.getPortletId(), BackgroundTaskExecutorNames.PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR) %>"
	>
		<liferay-ui:search-container-results
			results="<%= BackgroundTaskManagerUtil.getBackgroundTasks(groupId, selPortlet.getPortletId(), BackgroundTaskExecutorNames.PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR, searchContainer.getStart(), searchContainer.getEnd(), searchContainer.getOrderByComparator()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.backgroundtask.BackgroundTask"
			keyPropertyName="backgroundTaskId"
			modelVar="backgroundTask"
		>
			<liferay-ui:search-container-column-text
				cssClass="background-task-user-column"
				name="user"
			>
				<liferay-user:user-portrait
					userId="<%= backgroundTask.getUserId() %>"
				/>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-jsp
				cssClass="background-task-status-column"
				name="status"
				path="/publish_process_message.jsp"
			/>

			<liferay-ui:search-container-column-date
				name="create-date"
				orderable="<%= true %>"
				value="<%= backgroundTask.getCreateDate() %>"
			/>

			<liferay-ui:search-container-column-date
				name="completion-date"
				orderable="<%= true %>"
				value="<%= backgroundTask.getCompletionDate() %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="download"
			>

				<%
				for (FileEntry fileEntry : backgroundTask.getAttachmentsFileEntries()) {
					Map<String, Object> data = HashMapBuilder.<String, Object>put(
						"senna-off", "true"
					).build();

					StringBundler sb = new StringBundler(4);

					sb.append(fileEntry.getTitle());
					sb.append(StringPool.OPEN_PARENTHESIS);
					sb.append(LanguageUtil.formatStorageSize(fileEntry.getSize(), locale));
					sb.append(StringPool.CLOSE_PARENTHESIS);
				%>

					<liferay-ui:icon
						data="<%= data %>"
						icon="download"
						label="<%= true %>"
						markupView="lexicon"
						message="<%= sb.toString() %>"
						method="get"
						url="<%= PortletFileRepositoryUtil.getDownloadPortletFileEntryURL(themeDisplay, fileEntry, StringPool.BLANK) %>"
					/>

				<%
				}
				%>

			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text>
				<c:if test="<%= !backgroundTask.isInProgress() %>">

					<%
					Date completionDate = backgroundTask.getCompletionDate();
					%>

					<liferay-portlet:renderURL var="redirectURL">
						<portlet:param name="mvcRenderCommandName" value="/export_import/export_import" />
						<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.EXPORT %>" />
						<portlet:param name="tabs2" value="export" />
						<portlet:param name="tabs3" value="current-and-previous" />
						<portlet:param name="redirect" value="<%= redirect %>" />
						<portlet:param name="portletResource" value="<%= portletResource %>" />
					</liferay-portlet:renderURL>

					<liferay-portlet:actionURL name="/export_import/delete_portlet_background_task" portletName="<%= PortletKeys.EXPORT_IMPORT %>" var="deleteBackgroundTaskURL">
						<portlet:param name="redirect" value="<%= redirectURL %>" />
						<portlet:param name="backgroundTaskId" value="<%= String.valueOf(backgroundTask.getBackgroundTaskId()) %>" />
					</liferay-portlet:actionURL>

					<liferay-ui:icon-menu
						icon="<%= StringPool.BLANK %>"
						markupView="lexicon"
						showWhenSingleIcon="<%= true %>"
					>
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
	int incompleteBackgroundTasksCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(groupId, selPortlet.getPortletId(), BackgroundTaskExecutorNames.PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR, false);
	%>

	<div class="hide incomplete-process-message">
		<liferay-util:include page="/incomplete_processes_message.jsp" servletContext="<%= application %>">
			<liferay-util:param name="incompleteBackgroundTasksCount" value="<%= String.valueOf(incompleteBackgroundTasksCount) %>" />
		</liferay-util:include>
	</div>
</clay:container-fluid>