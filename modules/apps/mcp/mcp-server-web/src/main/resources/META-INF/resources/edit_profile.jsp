<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditProfileDisplayContext editProfileDisplayContext = new EditProfileDisplayContext(request, liferayPortletResponse, renderRequest);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(editProfileDisplayContext.getBackURL());

renderResponse.setTitle(editProfileDisplayContext.getTitle());
%>

<clay:navigation-bar
	inverted="<%= false %>"
	navigationItems="<%= editProfileDisplayContext.getNavigationItems() %>"
/>

<clay:container-fluid
	cssClass="container-fluid-max-xl container-view"
>
	<react:component
		module="{EditProfile} from mcp-server-web"
		props="<%= editProfileDisplayContext.getEditProfileProps() %>"
	/>
</clay:container-fluid>