<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String orderByType = ParamUtil.getString(request, "orderByType", "asc");

List<UserTracker> userTrackers = null;

if (PropsValues.LIVE_USERS_ENABLED && PropsValues.SESSION_TRACKER_MEMORY_ENABLED) {
	Map<String, UserTracker> sessionUsers = LiveUsers.getSessionUsers(company.getCompanyId());

	userTrackers = new ArrayList<UserTracker>(sessionUsers.values());

	userTrackers = ListUtil.sort(userTrackers, new UserTrackerModifiedDateComparator(orderByType.equals("asc")));
}

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/monitoring/view"
).buildPortletURL();
%>

<clay:management-toolbar
	disabled="<%= ListUtil.isEmpty(userTrackers) %>"
	selectable="<%= false %>"
	showSearch="<%= false %>"
	sortingOrder="<%= orderByType %>"
	sortingURL='<%=
		PortletURLBuilder.create(
			PortletURLUtil.clone(portletURL, renderResponse)
		).setParameter(
			"orderByType", orderByType.equals("asc") ? "desc" : "asc"
		).buildString()
	%>'
/>

<clay:container-fluid>
	<c:choose>
		<c:when test="<%= userTrackers != null %>">
			<liferay-ui:search-container
				emptyResultsMessage="there-are-no-live-sessions"
				headerNames="session-id,user-id,name,screen-name,last-request,num-of-hits"
				total="<%= userTrackers.size() %>"
			>
				<liferay-ui:search-container-results
					calculateStartAndEnd="<%= true %>"
					results="<%= userTrackers %>"
				/>

				<liferay-ui:search-container-row
					className="com.liferay.portal.kernel.model.UserTracker"
					keyProperty="userTrackerId"
					modelVar="userTracker"
				>

					<%
					User user2 = null;

					try {
						user2 = UserLocalServiceUtil.getUserById(userTracker.getUserId());
					}
					catch (NoSuchUserException nsue) {
					}
					%>

					<portlet:renderURL var="rowURL">
						<portlet:param name="mvcRenderCommandName" value="/monitoring/edit_session" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
						<portlet:param name="sessionId" value="<%= userTracker.getSessionId() %>" />
					</portlet:renderURL>

					<liferay-ui:search-container-column-text
						cssClass="text-strong"
						href="<%= rowURL %>"
						name="session-id"
						property="sessionId"
					/>

					<liferay-ui:search-container-column-text
						href="<%= rowURL %>"
						name="user-id"
						property="userId"
					/>

					<liferay-ui:search-container-column-text
						href="<%= rowURL %>"
						name="user-name"
						value='<%= (user2 != null) ? HtmlUtil.escape(user2.getFullName()) : LanguageUtil.get(request, "not-available") %>'
					/>

					<liferay-ui:search-container-column-text
						href="<%= rowURL %>"
						name="screen-name"
						value='<%= (user2 != null) ? HtmlUtil.escape(user2.getScreenName()) : LanguageUtil.get(request, "not-available") %>'
					/>

					<liferay-ui:search-container-column-date
						href="<%= rowURL %>"
						name="last-request"
						value="<%= userTracker.getModifiedDate() %>"
					/>

					<liferay-ui:search-container-column-text
						href="<%= rowURL %>"
						name="num-of-hits"
						property="hits"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="list"
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</c:when>
		<c:when test="<%= !PropsValues.LIVE_USERS_ENABLED %>">
			<liferay-ui:message arguments="<%= PropsKeys.LIVE_USERS_ENABLED %>" key="display-of-live-session-data-is-disabled" translateArguments="<%= false %>" />
		</c:when>
		<c:otherwise>
			<liferay-ui:message arguments="<%= PropsKeys.SESSION_TRACKER_MEMORY_ENABLED %>" key="display-of-live-session-data-is-disabled" translateArguments="<%= false %>" />
		</c:otherwise>
	</c:choose>
</clay:container-fluid>