<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/publications/init.jsp" %>

<div class="container-view">
	<div class="sheet">
		<h2 class="sheet-title">
			<liferay-ui:message arguments='<%= HtmlUtil.escape(renderRequest.getParameter("ctCollectionName")) %>' key="x-scheduled-publication-failed-with-an-unexpected-system-error" />
		</h2>

		<div class="sheet-section">

			<%
			long backgroundTaskId = GetterUtil.getLong(renderRequest.getParameter("backgroundTaskId"));

			BackgroundTask backgroundTask = BackgroundTaskLocalServiceUtil.getBackgroundTask(backgroundTaskId);

			String errorStackTrace = backgroundTask.getErrorStackTrace();

			if (Validator.isNull(errorStackTrace)) {
				errorStackTrace = backgroundTask.getStatusMessage();
			}
			%>

			<pre class="bg-light border p-2"><%= HtmlUtil.escape(GetterUtil.getString(errorStackTrace)) %></pre>
		</div>
</div>