<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-util:include page="/navigation.jsp" servletContext="<%= application %>" />

<%
ViewDataMasksDisplayContext viewDataMasksDisplayContext = new ViewDataMasksDisplayContext(liferayPortletResponse);
%>

<frontend-data-set:headless-display
	additionalProps="<%= viewDataMasksDisplayContext.getFDSAdditionalProps() %>"
	apiURL="<%= viewDataMasksDisplayContext.getAPIURL() %>"
	formName="fm"
	id="<%= viewDataMasksDisplayContext.getFDSName() %>"
	itemsPerPage="<%= 20 %>"
	namespace="<%= liferayPortletResponse.getNamespace() %>"
	pageNumber="<%= 1 %>"
	portletURL="<%= liferayPortletResponse.createRenderURL() %>"
	propsTransformer="{DataMasksFDSPropsTransformer} from mcp-server-web"
	style="fluid"
/>