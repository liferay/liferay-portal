<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long backgroundTaskId = ParamUtil.getLong(request, BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID);

BackgroundTask backgroundTask = BackgroundTaskManagerUtil.fetchBackgroundTask(backgroundTaskId);

JSONObject jsonObject = null;

try {
	jsonObject = JSONFactoryUtil.createJSONObject(backgroundTask.getStatusMessage());
}
catch (Exception e) {
}
%>

<c:choose>
	<c:when test="<%= jsonObject == null %>">
		<div class="alert <%= (backgroundTask.getStatus() == BackgroundTaskConstants.STATUS_FAILED) ? "alert-danger" : StringPool.BLANK %> publish-error">
			<liferay-ui:message arguments="<%= backgroundTask.getStatusMessage() %>" key="unable-to-execute-process-x" translateArguments="<%= false %>" />
		</div>
	</c:when>
	<c:otherwise>
		<div class="alert alert-danger publish-error">

			<%
			boolean exported = MapUtil.getBoolean(backgroundTask.getTaskContextMap(), "exported");
			boolean validated = MapUtil.getBoolean(backgroundTask.getTaskContextMap(), "validated");
			%>

			<c:choose>
				<c:when test="<%= exported && !validated %>">
					<div class="h4 upload-error-message"><liferay-ui:message key="the-publish-process-did-not-start-due-to-validation-errors" /></div>
				</c:when>
				<c:otherwise>
					<div class="h4 upload-error-message"><liferay-ui:message key="an-unexpected-error-occurred-with-the-publish-process.-please-check-your-portal-and-publishing-configuration" /></div>
				</c:otherwise>
			</c:choose>

			<span class="error-message"><%= HtmlUtil.escape(jsonObject.getString("message")) %></span>

			<%
			JSONArray messageListItemsJSONArray = jsonObject.getJSONArray("messageListItems");
			%>

			<c:if test="<%= (messageListItemsJSONArray != null) && (messageListItemsJSONArray.length() > 0) %>">
				<ul class="error-list-items">

					<%
					for (int i = 0; i < messageListItemsJSONArray.length(); i++) {
						JSONObject messageListItemJSONObject = messageListItemsJSONArray.getJSONObject(i);

						String info = messageListItemJSONObject.getString("info");
					%>

						<li>
							<%= messageListItemJSONObject.getString("type") %>

							<%= messageListItemJSONObject.getString("site") %>:

							<strong><%= HtmlUtil.escape(messageListItemJSONObject.getString("name")) %></strong>

							<c:if test="<%= Validator.isNotNull(info) %>">
								<span class="error-info">(<%= HtmlUtil.escape(messageListItemJSONObject.getString("info")) %>)</span>
							</c:if>
						</li>

					<%
					}
					%>

				</ul>
			</c:if>
		</div>

		<%
		JSONArray warningMessagesJSONArray = jsonObject.getJSONArray("warningMessages");
		%>

		<c:if test="<%= (warningMessagesJSONArray != null) && (warningMessagesJSONArray.length() > 0) %>">
			<div class="alert upload-error">
				<span class="error-message"><liferay-ui:message key='<%= ((messageListItemsJSONArray != null) && (messageListItemsJSONArray.length() > 0)) ? "consider-that-the-following-data-would-not-have-been-published-either" : "the-following-data-was-not-published" %>' /></span>

				<ul class="error-list-items">

					<%
					for (int i = 0; i < warningMessagesJSONArray.length(); i++) {
						JSONObject warningMessageJSONObject = warningMessagesJSONArray.getJSONObject(i);

						String info = warningMessageJSONObject.getString("info");
					%>

						<li>
							<%= warningMessageJSONObject.getString("type") %>:

							<strong><%= warningMessageJSONObject.getString("size") %></strong>

							<c:if test="<%= Validator.isNotNull(info) %>">
								<span class="error-info">(<%= HtmlUtil.escape(warningMessageJSONObject.getString("info")) %>)</span>
							</c:if>
						</li>

					<%
					}
					%>

				</ul>
			</div>
		</c:if>
	</c:otherwise>
</c:choose>