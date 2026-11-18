<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/revamp/init.jsp" %>

<liferay-staging:defineObjects />

<%
PublishProcessesDisplayContext publishProcessesDisplayContext = new PublishProcessesDisplayContext(request, liferayPortletResponse, liveGroup);
%>

<clay:navigation-bar
	navigationItems="<%= publishProcessesDisplayContext.getNavigationItems() %>"
/>

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