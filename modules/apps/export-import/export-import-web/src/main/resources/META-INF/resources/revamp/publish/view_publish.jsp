<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/revamp/init.jsp" %>

<liferay-staging:defineObjects />

<%
String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNotNull(redirect)) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);
}

PublishProcessesDisplayContext publishProcessesDisplayContext = new PublishProcessesDisplayContext(request, liferayPortletResponse, liveGroup);

String title = publishProcessesDisplayContext.getTitle();

if (title != null) {
	renderResponse.setTitle(title);
}
%>

<c:if test="<%= title == null %>">
	<clay:navigation-bar
		navigationItems="<%= publishProcessesDisplayContext.getNavigationItems() %>"
	/>
</c:if>

<c:choose>
	<c:when test='<%= Objects.equals(ParamUtil.getString(request, "tabs1"), "scheduled") %>'>
		<frontend-data-set:headless-display
			apiURL="<%= publishProcessesDisplayContext.getScheduledPublishProcessesAPIURL() %>"
			creationMenu="<%= publishProcessesDisplayContext.getCreationMenu() %>"
			fdsActionDropdownItems="<%= publishProcessesDisplayContext.getScheduledPublishFDSActionDropdownItems() %>"
			id="<%= publishProcessesDisplayContext.getScheduledPublishFDSName() %>"
			propsTransformer="{ScheduledPublishProcessesFDSPropsTransformer} from exportimport-web"
			style="fluid"
			uniformActionsDisplay="<%= true %>"
		/>
	</c:when>
	<c:otherwise>
		<frontend-data-set:headless-display
			apiURL="<%= publishProcessesDisplayContext.getPublishProcessesAPIURL() %>"
			creationMenu="<%= publishProcessesDisplayContext.getCreationMenu() %>"
			fdsActionDropdownItems="<%= publishProcessesDisplayContext.getPublishFDSActionDropdownItems() %>"
			id="<%= publishProcessesDisplayContext.getPublishFDSName() %>"
			propsTransformer="{PublishProcessesFDSPropsTransformer} from exportimport-web"
			style="fluid"
			uniformActionsDisplay="<%= true %>"
		/>
	</c:otherwise>
</c:choose>