<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
UADExportProcessDisplayContext uadExportProcessDisplayContext = new UADExportProcessDisplayContext(request, renderResponse);
%>

<liferay-ui:search-container
	searchContainer="<%= uadExportProcessDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.backgroundtask.BackgroundTask"
		keyPropertyName="backgroundTaskId"
		modelVar="backgroundTask"
	>
		<liferay-ui:search-container-column-text
			cssClass="lfr-title-column"
		>
			<div id="<portlet:namespace />exportStatus">
				<div class="h5">
					<liferay-ui:message key="<%= UADLanguageUtil.getApplicationName(UADExportProcessUtil.getApplicationKey(backgroundTask), locale) %>" />
				</div>

				<clay:label
					displayType="<%= UADExportProcessUtil.getStatusStyle(backgroundTask.getStatus()) %>"
					label="<%= backgroundTask.getStatusLabel() %>"
				/>
			</div>
		</liferay-ui:search-container-column-text>

		<%
		Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat("yyyy.MM.dd - hh:mm a", locale, themeDisplay.getTimeZone());
		%>

		<liferay-ui:search-container-column-text
			cssClass="lfr-create-date-column table-cell-expand text-nowrap"
		>
			<liferay-ui:message key="create-date" />: <%= dateFormat.format(backgroundTask.getCreateDate()) %>

			<c:if test="<%= backgroundTask.isInProgress() %>">

				<%
				request.setAttribute("backgroundTask", backgroundTask);
				%>

				<liferay-util:include page="/export_process_progress_bar.jsp" servletContext="<%= application %>">
					<liferay-util:param name="backgroundTaskId" value="<%= String.valueOf(backgroundTask.getBackgroundTaskId()) %>" />
				</liferay-util:include>
			</c:if>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			cssClass="lfr-completion-date-column table-cell-expand text-nowrap"
		>
			<c:choose>
				<c:when test="<%= backgroundTask.isCompleted() %>">
					<liferay-ui:message key="completion-date" />: <%= dateFormat.format(backgroundTask.getCompletionDate()) %>
				</c:when>
				<c:otherwise>
					<liferay-ui:message key="in-progress" />
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-jsp
			cssClass="entry-action-column"
			path="/export_process_action.jsp"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		resultRowSplitter="<%= new UADExportProcessResultRowSplitter() %>"
	/>
</liferay-ui:search-container>