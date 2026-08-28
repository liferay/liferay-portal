<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditProfileDisplayContext editProfileDisplayContext = new EditProfileDisplayContext(request, liferayPortletResponse, renderRequest);
%>

<frontend-data-set:headless-display
	additionalProps="<%= editProfileDisplayContext.getAdditionalProps() %>"
	apiURL="<%= editProfileDisplayContext.getAPIURL() %>"
	creationMenu="<%= editProfileDisplayContext.getCreationMenu() %>"
	fdsSortItemList="<%= editProfileDisplayContext.getFDSSortItemList() %>"
	id="<%= editProfileDisplayContext.getFDSName() %>"
	propsTransformer="{ProfileToolsFDSPropsTransformer} from mcp-server-web"
	style="fluid"
/>